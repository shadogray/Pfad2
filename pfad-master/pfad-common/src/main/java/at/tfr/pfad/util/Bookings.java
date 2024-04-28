package at.tfr.pfad.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Model;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import at.tfr.pfad.model.Activity;
import at.tfr.pfad.model.Activity_;
import at.tfr.pfad.model.Booking;
import at.tfr.pfad.model.Booking_;
import at.tfr.pfad.model.Member_;

@SessionScoped
@Model
public class Bookings implements Serializable {

	private Logger log = Logger.getLogger(getClass());

	@Inject
	private transient EntityManager entityManager;

	public List<Booking> filtered(FacesContext facesContext, UIComponent component, final String filter) {
		return filtered(facesContext, component, filter, null, null);
	}

	public List<Booking> filtered(FacesContext facesContext, UIComponent component, final String filter, Activity activity, String strasse) {
		log.debug("filter: " + filter + " for: " + component.getId());
		return filtered(filter, activity, strasse);
	}

	public List<Booking> filtered(final String filter) {
		return filtered(filter, null, null);
	}

	public List<Booking> filtered(final String filter, Activity activity) {
		return filtered(filter, activity, null);
	}

	public List<Booking> filtered(final String filter, Activity activity, String strasse) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Booking> cq = cb.createQuery(Booking.class);
		Root<Booking> root = cq.from(Booking.class);
		CriteriaQuery<Booking> query = cq.select(root);
		List<Predicate> preds = filterPreds(root, cb, filter, activity, strasse);
		cq.where(cb.and(preds.toArray(new Predicate[preds.size()])));
		return entityManager.createQuery(query.distinct(true)).setMaxResults(10).getResultList();
	}

	public List<Predicate> filterPreds(Path<Booking> root, CriteriaBuilder cb, final String filter, Activity activity) {
		return filterPreds(root, cb, filter, activity, null);
	}
	
	public List<Predicate> filterPreds(Path<Booking> root, CriteriaBuilder cb, final String filter, Activity activity,
			String strasse) {
		List<Predicate> preds = new ArrayList<>();
		
		if (activity != null) {
			preds.add(cb.equal(root.get(Booking_.activity), activity));
		}
		if (StringUtils.isNotBlank(strasse)) {
			preds.add(cb.equal(root.get(Booking_.member).get(Member_.strasse), strasse));
		}
		
		if (StringUtils.isNotBlank(filter) && filter.length() > 2) {
			Stream.of(filter.toLowerCase().split(" ")).forEach(v->preds.add(cb.or(predicatesFor(v, cb, root))));
		}
		return preds;
	}
	
	Predicate[] predicatesFor(String value, CriteriaBuilder cb, Path<Booking> root) {
		List<Predicate> list = new ArrayList<>();
		list.add(cb.like(cb.lower(root.get(Booking_.member).get(Member_.name)), "%"+value+"%"));
		list.add(cb.like(cb.lower(root.get(Booking_.member).get(Member_.vorname)), "%"+value+"%"));
		list.add(cb.like(cb.lower(root.get(Booking_.activity).get(Activity_.name)), "%"+value+"%"));
		return list.toArray(new Predicate[list.size()]);
	}

}
