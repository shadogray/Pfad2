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

import at.tfr.pfad.model.Member_;
import at.tfr.pfad.model.Participation;
import at.tfr.pfad.model.Participation_;
import at.tfr.pfad.model.Training_;

@Named
@Stateless
public class Participations {

	private Logger log = Logger.getLogger(getClass());

	@Inject
	private ParticipationRepository participationRepo;
	@Inject
	private EntityManager entityManager;

	public List<Participation> filtered(FacesContext facesContext, UIComponent component, final String filter) {
		log.debug("filter: " + filter + " for: " + component.getId());
		return filtered(filter);
	}

	public List<Participation> filtered(final String filter) {
		return filtered(filter, null, null);
	}
	
	public List<Participation> filtered(final String filter, final Long trainingId, final Long memberId) {
		log.debug("filter: " + filter);
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Participation> cq = cb.createQuery(Participation.class);
		Root<Participation> root = cq.from(Participation.class);
		CriteriaQuery<Participation> query = cq.select(root);
		List<Predicate> preds = new ArrayList<>();
		if (trainingId != null) {
			preds.add(cb.equal(root.get(Participation_.training).get(Training_.id), trainingId));
		}
		if (memberId != null) {
			preds.add(cb.equal(root.get(Participation_.member).get(Member_.id), memberId));
		}
		if (StringUtils.isNotBlank(filter) && filter.length() > 2) {
			Stream.of(filter.toLowerCase().split(" "))
			.forEach(v -> preds.add(cb.or(predicatesFor(v, cb, root))));
		}
		
		if (preds.isEmpty()) {
			return participationRepo.findAll(0, 30);
		}
		
		cq.where(cb.and(preds.toArray(new Predicate[preds.size()])));
		return this.entityManager.createQuery(query.distinct(true))
				.setHint(Graphs.FETCHGRAPH, Graphs.createHint(entityManager, "fetchAll"))
				.setMaxResults(30).getResultList();
	}

	Predicate[] predicatesFor(String value, CriteriaBuilder cb, Root<Participation> root) {
		
		List<Predicate> list = new ArrayList<>();
		
		for (Field field : Participation.class.getDeclaredFields()) {
			if (field.getType() == boolean.class && value.endsWith(field.getName())) {
				list.add(cb.equal(root.get(field.getName()), !value.startsWith("no")));
				return list.toArray(new Predicate[list.size()]);
			}
		}
		
		list.add(cb.like(cb.lower(root.get(Participation_.training).get(Training_.name)), "%" + value + "%"));
		list.add(cb.like(cb.lower(root.get(Participation_.member).get(Member_.name)), "%" + value + "%"));
		list.add(cb.like(cb.lower(root.get(Participation_.member).get(Member_.vorname)), "%" + value + "%"));
		return list.toArray(new Predicate[list.size()]);
	}

}
