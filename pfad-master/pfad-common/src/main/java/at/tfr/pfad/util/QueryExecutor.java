package at.tfr.pfad.util;

import at.tfr.pfad.ConfigurationType;
import at.tfr.pfad.dao.ConfigurationRepository;
import at.tfr.pfad.model.Configuration;
import at.tfr.pfad.processing.ExecutionResult;
import at.tfr.pfad.processing.PfadCommandExecutor;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;
import org.hibernate.query.TupleTransformer;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Stateless
public class QueryExecutor {

	@Inject
	private EntityManager em;
	@Inject
	private ConfigurationRepository configRepo;
	@Inject
	private PfadCommandExecutor cmdExecutor;
	private String securityFilter = "(?)password";
	private final Pattern limitPattern = Pattern.compile("(?ims)^(.*)\\s+LIMIT\\s+(\\d+)(\\s*,\\s*(\\d+))?$");
	
	@PostConstruct
	private void init() {
		securityFilter = configRepo.getValue("querySecurityFilter", securityFilter);
	}
	
	public List<Object> list(Configuration config) {
		if (ConfigurationType.nativeSource.equals(config.getType())) {
			return em.createNativeQuery(config.getCvalue()).unwrap(Query.class).list();
		} else {
			return em.createQuery(config.getCvalue()).unwrap(Query.class).list();
		}
	}
	
	public Future<List<List<Entry<String, Object>>>> execute(Configuration config, ExecutorContext ctx) {
		return execute(config.getCvalue(), ConfigurationType.nativeQuery.equals(config.getType()), ctx);
	}
	
	@SuppressWarnings("unchecked")
	@Asynchronous
	public Future<List<List<Entry<String, Object>>>> execute(String query, boolean nativeQuery, ExecutorContext ctx) {
		
		Integer maxResults = null;
		Integer firstResult = null;
		
		if (query == null) return new AsyncResult<>(Collections.emptyList());
		
		if (query != null && query.matches("(?).*password.*")) {
			throw new SecurityException("security check failed");
		}
		
		if (query.startsWith(PfadCommandExecutor.PFX)) {
			try {
				ExecutionResult result = cmdExecutor.executeCommand(query);
				return toResult("result:", result);
			} catch (Exception e) {
				return toResult("Exception:", e);
			}
		}
		
		// JPA reserved words:
		query = query.replaceAll("(?i) as +(?=to|cc|bcc)", " as _");

		Matcher limitMatch = limitPattern.matcher(query);
		if (limitMatch.matches()) {
			query = limitMatch.group(1);
			maxResults = Integer.valueOf(limitMatch.group(2));
			if (limitMatch.groupCount() == 4 && StringUtils.isNotBlank(limitMatch.group(4))) {
				firstResult = Integer.valueOf(limitMatch.group(4));
			}
		}
		Query q;
		if (ctx.isAdmin() && (query.startsWith("update ") || query.startsWith("delete "))) {
			int updated;
			if (nativeQuery) {
				updated = em.createNativeQuery(query).executeUpdate();
			} else {
				updated = em.createQuery(query).executeUpdate();
			}
			return toResult(updated);
		}
		if (nativeQuery) {
			q = em.createNativeQuery(query).unwrap(Query.class);
		} else {
			q = em.createQuery(query).unwrap(Query.class);
		}

		if (maxResults != null && maxResults > 0) {
			q.setMaxResults(maxResults);
		}
		if (firstResult != null) {
			q.setFirstResult(firstResult);
		}
		
		q.setTupleTransformer(new AliasTransformer());
		List<List<Entry<String,Object>>> list = q.list();
		
		return new AsyncResult<>(list);
	}

	public Future<List<List<Entry<String, Object>>>> toResult(int updated) {
		return toResult("updated:", updated);
	}

	public Future<List<List<Entry<String, Object>>>> toResult(String key, Object value) {
		List<List<Entry<String,Object>>> result = new ArrayList<>();
		List<Entry<String,Object>> e = new ArrayList<>();
		Map<String,Object> entries = new HashMap<>();
		entries.put(key, value);
		e.addAll(entries.entrySet());
		result.add(e);
		return new AsyncResult<>(result);
	}
	
	class AliasTransformer implements TupleTransformer<List<Entry<String, Object>>> {
		@Override
		public List<Entry<String, Object>> transformTuple(Object[] tuple, String[] aliases) {
			Map<String,Object> result = new LinkedHashMap<>(tuple.length);
			for ( int i=0; i<tuple.length; i++ ) {
				String alias = aliases[i];
				if (alias != null) alias = alias.replaceAll("^_?", "");
				Object value = tuple[i];
				if (value != null && value.getClass().isArray()) {
					value = Arrays.toString((Object[]) value);
				}
				result.put( alias != null ? alias : Integer.toString(i), value);
			}
			return result.entrySet().stream().collect(Collectors.toList());
		}
	}
}
