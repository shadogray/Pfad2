package at.tfr.pfad.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Member_;

@Named
@Stateless
public class Members {

	private Logger log = Logger.getLogger(getClass());

	@Inject
	private MemberRepository memberRepo;
	@Inject
	private EntityManager entityManager;

	public List<Member> filtered(FacesContext facesContext, UIComponent component, final String filter) {
		log.debug("filter: " + filter + " for: " + component.getId());
		return filtered(filter);
	}

	public List<Member> filtered(final String filter) {
		return filtered(filter, null);
	}
	
	public List<Member> filtered(final String filter, final Long truppId) {
		log.debug("filter: " + filter);
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Member> cq = cb.createQuery(Member.class);
		Root<Member> root = cq.from(Member.class);
		CriteriaQuery<Member> query = cq.select(root);
		List<Predicate> preds = new ArrayList<>();
		if (truppId != null) {
			preds.add(cb.equal(root.get(Member_.truppId), truppId));
		}
		if (StringUtils.isNotBlank(filter) && filter.length() > 2) {
			Stream.of(filter.toLowerCase().split(" "))
			.forEach(v -> preds.add(cb.or(predicatesFor(v, cb, root))));
		}
		
		if (preds.isEmpty()) {
			return memberRepo.fetchAll(30);
		}
		
		cq.where(cb.and(preds.toArray(new Predicate[preds.size()])));
		return this.entityManager.createQuery(query.distinct(true))
				.setHint(Graphs.FETCHGRAPH, Graphs.createHint(entityManager, "fetchAll"))
				.setMaxResults(30).getResultList();
	}

	Predicate[] predicatesFor(String value, CriteriaBuilder cb, Root<Member> root) {
		
		List<Predicate> list = new ArrayList<>();
		
		for (Field field : Member.class.getDeclaredFields()) {
			if (field.getType() == boolean.class && value.endsWith(field.getName())) {
				list.add(cb.equal(root.get(field.getName()), !value.startsWith("no")));
				return list.toArray(new Predicate[list.size()]);
			}
		}
		
		list.add(cb.like(cb.lower(root.get(Member_.name)), "%" + value + "%"));
		list.add(cb.like(cb.lower(root.get(Member_.vorname)), "%" + value + "%"));
		list.add(cb.like(cb.lower(root.get(Member_.strasse)), "%" + value + "%"));
		list.add(cb.equal(cb.lower(root.get(Member_.geschlecht).as(String.class)), value));
		list.add(cb.like(cb.lower(root.get(Member_.ort)), "%" + value + "%"));
		list.add(cb.like(cb.lower(root.get(Member_.plz)), "%" + value + "%"));
		list.add(cb.equal((root.get(Member_.gebJahr).as(String.class)), value));
		return list.toArray(new Predicate[list.size()]);
	}

}
