/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import at.tfr.pfad.Role;
import at.tfr.pfad.dao.MailTemplateRepository;
import at.tfr.pfad.model.MailTemplate;
import at.tfr.pfad.model.MailTemplate_;

/**
 * Backing bean for MailTemplate entities.
 * This class provides CRUD functionality for all MailTemplate entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ViewScoped
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class MailTemplateBean extends BaseBean<MailTemplate> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private MailTemplateRepository configRepo;

	/*
	 * Support creating and retrieving MailTemplate entities
	 */

	private Long id;
	private List<MailTemplate> allConfigs;

	@PostConstruct
	public void init() {
		allConfigs = configRepo.findAll();
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private MailTemplate MailTemplate;

	public MailTemplate getMailTemplate() {
		return this.MailTemplate;
	}

	public void setMailTemplate(MailTemplate MailTemplate) {
		this.MailTemplate = MailTemplate;
	}

	public String create() {
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.id == null) {
			this.MailTemplate = this.example;
		} else {
			this.MailTemplate = findById(getId());
		}
	}

	public MailTemplate findById(Long id) {

		return this.entityManager.find(MailTemplate.class, id);
	}

	/*
	 * Support updating and deleting MailTemplate entities
	 */

	@Override
	public boolean isUpdateAllowed() {
		return isAdmin() || isGruppe() || isVorstand();
	}

	public String update() {

		try {
			if (!isUpdateAllowed()) {
				throw new SecurityException("Update denied for: "+sessionBean.getUser());
			}
			validator.validate(MailTemplate);

			if (this.id == null) {
				this.entityManager.persist(this.MailTemplate);
				this.entityManager.flush();
				return "search?faces-redirect=true";
			} else {
				MailTemplate = entityManager.merge(MailTemplate);
				this.entityManager.flush();
				return "view?faces-redirect=true&id=" + this.MailTemplate.getId();
			}
		} catch (Exception e) {
			log.info("update: "+e, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {

		if (!isDeleteAllowed())
			throw new SecurityException("only admins may delete entry");

		try {
			MailTemplate deletableEntity = findById(getId());

			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			log.info("update: "+e, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching MailTemplate entities with pagination
	 */

	private List<MailTemplate> pageItems;

	private MailTemplate example = new MailTemplate();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public MailTemplate getExample() {
		return this.example;
	}

	public void setExample(MailTemplate example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<MailTemplate> root = countCriteria.from(MailTemplate.class);
		countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<MailTemplate> criteria = builder.createQuery(MailTemplate.class);
		root = criteria.from(MailTemplate.class);
		TypedQuery<MailTemplate> query = this.entityManager
				.createQuery(criteria.select(root).where(getSearchPredicates(root))
						.orderBy(builder.asc(root.get(MailTemplate_.name))));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<MailTemplate> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String name = this.example.getName();
		if (StringUtils.isNotBlank(name)) {
			predicatesList.add(builder.like(builder.lower(root.get(MailTemplate_.name)), '%' + name.toLowerCase() + '%'));
		}
		String query = this.example.getQuery();
		if (StringUtils.isNotBlank(query)) {
			predicatesList.add(builder.like(builder.lower(root.get(MailTemplate_.query)), '%' + query.toLowerCase() + '%'));
		}
		String subject = this.example.getSubject();
		if (StringUtils.isNotBlank(subject)) {
			predicatesList.add(builder.like(builder.lower(root.get(MailTemplate_.subject)), '%' + subject.toLowerCase() + '%'));
		}
		String text = this.example.getText();
		if (StringUtils.isNotBlank(text)) {
			predicatesList
					.add(builder.like(builder.lower(root.get(MailTemplate_.text)), '%' + text.toLowerCase() + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<MailTemplate> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back MailTemplate entities (e.g. from inside
	 * an HtmlSelectOneMenu)
	 */

	public List<MailTemplate> getAll() {

		CriteriaQuery<MailTemplate> criteria = this.entityManager.getCriteriaBuilder()
				.createQuery(MailTemplate.class);
		return this.entityManager.createQuery(criteria.select(criteria.from(MailTemplate.class))).getResultList();
	}

	public Converter getConverter() {

		final MailTemplateBean ejbProxy = this.sessionContext.getBusinessObject(MailTemplateBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (StringUtils.isBlank(value))
					return null;
				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof MailTemplate) 
					return ""+((MailTemplate)value).getId();
				return ""+(value != null ? value : "");
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private MailTemplate add = new MailTemplate();

	public MailTemplate getAdd() {
		return this.add;
	}

	public MailTemplate getAdded() {
		MailTemplate added = this.add;
		this.add = new MailTemplate();
		return added;
	}
	
	public List<Role> getRoles() {
		return Arrays.asList(Role.values());
	}
	
	public List<MailTemplate> getAllConfigs() {
		return allConfigs;
	}
}
