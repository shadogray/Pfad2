package at.tfr.pfad.dao;

import at.tfr.pfad.model.MailTemplate;
import at.tfr.pfad.model.MailTemplate_;
import jakarta.ejb.Stateless;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Named
@Stateless
public class MailTemplates {

	private Logger log = Logger.getLogger(getClass());

	@Inject
	private MailTemplateRepository templateRepo;
	@Inject
	private EntityManager entityManager;

	public List<MailTemplate> filtered(FacesContext facesContext, UIComponent component, final String filter) {
		log.debug("filter: " + filter + " for: " + component.getId());
		return filtered(filter);
	}

	public List<MailTemplate> filteredSimple(final String filter) {
		return filtered(filter, null, true);
	}
	public List<MailTemplate> filtered(final String filter) {
		return filtered(filter, null, false);
	}
	public List<MailTemplate> filtered(final String filter, boolean simple) {
		return filtered(filter, null, simple);
	}

	public List<MailTemplate> filteredSimple(final String filter, final Long truppId) {
		return filtered(filter, truppId, true);
	}
	public List<MailTemplate> filtered(final String filter, final Long truppId) {
		return filtered(filter, truppId, false);
	}

	public List<MailTemplate> filtered(final String filter, final Long truppId, boolean simple) {
		log.debug("filter: " + filter);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MailTemplate> cq = cb.createQuery(MailTemplate.class);
		Root<MailTemplate> root = cq.from(MailTemplate.class);
		CriteriaQuery<MailTemplate> query = cq.select(root);
		List<Predicate> preds = new ArrayList<>();
		if (StringUtils.isNotBlank(filter) && filter.length() > 2) {
			Stream.of(filter.toLowerCase().split(" "))
			.forEach(v -> preds.add(cb.or(predicatesFor(v, cb, root, simple))));
		}
		
		if (preds.isEmpty()) {
			return templateRepo.findAll();
		}
		
		cq.where(cb.and(preds.toArray(new Predicate[preds.size()])));
		return entityManager.createQuery(query.distinct(true))
				.setMaxResults(30).getResultList();
	}

	Predicate[] predicatesFor(String value, CriteriaBuilder cb, Root<MailTemplate> root, boolean simple) {
		
		List<Predicate> list = new ArrayList<>();
		
		for (Field field : MailTemplate.class.getDeclaredFields()) {
			if (field.getType() == boolean.class && value.endsWith(field.getName())) {
				list.add(cb.equal(root.get(field.getName()), !value.startsWith("no")));
				return list.toArray(new Predicate[list.size()]);
			}
		}
		
		list.add(cb.like(cb.lower(root.get(MailTemplate_.name)), "%" + value + "%"));
		list.add(cb.like(cb.lower(root.get(MailTemplate_.subject)), "%" + value + "%"));
		if (!simple) {
			list.add(cb.like(cb.lower(root.get(MailTemplate_.text)), "%" + value + "%"));
			list.add(cb.equal(cb.lower(root.get(MailTemplate_.query).as(String.class)), value));
		}
		return list.toArray(new Predicate[list.size()]);
	}

}
