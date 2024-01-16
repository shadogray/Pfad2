package at.tfr.pfad.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;
import org.hibernate.query.TupleTransformer;

import at.tfr.pfad.ConfigurationType;
import at.tfr.pfad.dao.ConfigurationRepository;
import at.tfr.pfad.model.Configuration;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@Stateless
public class QueryExecutor {

	@Inject
	private SessionBean sessionBean;
	@Inject
	private EntityManager em;
	@Inject
	private ConfigurationRepository configRepo;
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
	
	public List<List<Entry<String, Object>>> execute(Configuration config) {
		return execute(config.getCvalue(), ConfigurationType.nativeQuery.equals(config.getType()));
	}
	
	@SuppressWarnings("unchecked")
	public List<List<Entry<String, Object>>> execute(String query, boolean nativeQuery) {
		
		Integer maxResults = null;
		Integer firstResult = null;
		
		if (query != null && query.matches("(?).*password.*")) {
			throw new SecurityException("security check failed");
		}
		Matcher limitMatch = limitPattern.matcher(query);
		if (limitMatch.matches()) {
			query = limitMatch.group(1);
			maxResults = Integer.valueOf(limitMatch.group(2));
			if (limitMatch.groupCount() == 4 && StringUtils.isNotBlank(limitMatch.group(4))) {
				firstResult = Integer.valueOf(limitMatch.group(4));
			}
		}
		Query q;
		if (sessionBean.isAdmin() && (query.startsWith("update ") || query.startsWith("delete "))) {
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
		
		return list;
	}

	public List<List<Entry<String, Object>>> toResult(int updated) {
		List<List<Entry<String,Object>>> result = new ArrayList<>();
		List<Entry<String,Object>> e = new ArrayList<>();
		Map<String,Object> entries = new HashMap<>();
		entries.put("updated", updated);
		e.addAll(entries.entrySet());
		result.add(e);
		return result;
	}
	
	class AliasTransformer implements TupleTransformer<List<Entry<String, Object>>> {
		@Override
		public List<Entry<String, Object>> transformTuple(Object[] tuple, String[] aliases) {
			Map<String,Object> result = new LinkedHashMap<>(tuple.length);
			for ( int i=0; i<tuple.length; i++ ) {
				String alias = aliases[i];
				result.put( alias != null ? alias : Integer.toString(i), tuple[i] );
			}
			return result.entrySet().stream().collect(Collectors.toList());
		}
	}
}
