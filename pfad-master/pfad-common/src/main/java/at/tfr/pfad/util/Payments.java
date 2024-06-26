package at.tfr.pfad.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.inject.Model;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import at.tfr.pfad.model.Activity_;
import at.tfr.pfad.model.Booking;
import at.tfr.pfad.model.Booking_;
import at.tfr.pfad.model.Member_;
import at.tfr.pfad.model.Payment;
import at.tfr.pfad.model.Payment_;

@Model
public class Payments {

	private Logger log = Logger.getLogger(getClass());

	@Inject
	private EntityManager entityManager;
	
	public List<Payment> filtered(FacesContext facesContext, UIComponent component, final String filter) {
		log.debug("filter: " + filter + " for: " + component.getId());
		return filtered(filter);
	}

	public List<Payment> filtered(final String filter) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Payment> cq = cb.createQuery(Payment.class);
		Root<Payment> root = cq.from(Payment.class);
		CriteriaQuery<Payment> query = cq.select(root);
		List<Predicate> preds = new ArrayList<>();
		if (StringUtils.isNotBlank(filter) && filter.length() > 2) {
			Stream.of(filter.toLowerCase().split(" ")).forEach(v->preds.add(cb.or(predicatesFor(v, cb, root))));
			cq.where(cb.and(preds.toArray(new Predicate[preds.size()])));
		}
		return entityManager.createQuery(query.distinct(true)).setMaxResults(10).getResultList();
	}
	
	Predicate[] predicatesFor(String value, CriteriaBuilder cb, Root<Payment> root) {
		List<Predicate> list = new ArrayList<>();
		list.add(cb.like(cb.lower(root.get(Payment_.payer).get(Member_.name)), "%"+value+"%"));
		list.add(cb.like(cb.lower(root.get(Payment_.payer).get(Member_.vorname)), "%"+value+"%"));
		Join<Payment,Booking> bookings = root.join(Payment_.bookings);
		list.add(cb.like(cb.lower(bookings.get(Booking_.activity).get(Activity_.name)), "%"+value+"%"));
		list.add(cb.like(cb.lower(bookings.get(Booking_.member).get(Member_.name)), "%"+value+"%"));
		list.add(cb.like(cb.lower(bookings.get(Booking_.member).get(Member_.vorname)), "%"+value+"%"));
		return list.toArray(new Predicate[list.size()]);
	}

}
