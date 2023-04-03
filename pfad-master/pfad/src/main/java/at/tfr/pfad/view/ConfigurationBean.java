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

import at.tfr.pfad.ConfigurationType;
import at.tfr.pfad.Role;
import at.tfr.pfad.model.Configuration;
import at.tfr.pfad.model.Configuration_;
import at.tfr.pfad.util.SessionBean;

/**
 * Backing bean for Configuration entities.
 * This class provides CRUD functionality for all Configuration entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ViewScoped
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ConfigurationBean extends BaseBean<Configuration> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private SessionBean sessionBean;

	/*
	 * Support creating and retrieving Configuration entities
	 */

	private Long id;
	private List<Configuration> allConfigs;

	@PostConstruct
	public void init() {
		allConfigs = sessionBean.getConfig();
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Configuration configuration;

	public Configuration getConfiguration() {
		return this.configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public String create() {
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.id == null) {
			this.configuration = this.example;
		} else {
			this.configuration = findById(getId());
		}
	}

	public Configuration findById(Long id) {

		return this.entityManager.find(Configuration.class, id);
	}

	/*
	 * Support updating and deleting Configuration entities
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
			validator.validate(configuration);

			if (this.id == null) {
				this.entityManager.persist(this.configuration);
				this.entityManager.flush();
				return "search?faces-redirect=true";
			} else {
				configuration = entityManager.merge(configuration);
				this.entityManager.flush();
				return "view?faces-redirect=true&id=" + this.configuration.getId();
			}
		} catch (Exception e) {
			log.info("update: "+e, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
			return null;
		} finally {
			sessionBean.init();
		}
	}

	public String delete() {

		if (!isDeleteAllowed())
			throw new SecurityException("only admins may delete entry");

		try {
			Configuration deletableEntity = findById(getId());

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
	 * Support searching Configuration entities with pagination
	 */

	private List<Configuration> pageItems;

	private Configuration example = new Configuration();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Configuration getExample() {
		return this.example;
	}

	public void setExample(Configuration example) {
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
		Root<Configuration> root = countCriteria.from(Configuration.class);
		countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Configuration> criteria = builder.createQuery(Configuration.class);
		root = criteria.from(Configuration.class);
		TypedQuery<Configuration> query = this.entityManager
				.createQuery(criteria.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Configuration> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		if (!(sessionBean.isAdmin() || sessionBean.isGruppe())) {
			predicatesList.add(builder.or(
					root.get(Configuration_.role).in(sessionBean.getRoles()),
					builder.isNull(root.get(Configuration_.owners)),
					builder.like(builder.lower(root.get(Configuration_.owners)), '%'+sessionBean.getUser().getName().toLowerCase()+"%")));
		}
		
		String ckey = this.example.getCkey();
		if (ckey != null && !"".equals(ckey)) {
			predicatesList.add(builder.like(builder.lower(root.get(Configuration_.ckey)), '%' + ckey.toLowerCase() + '%'));
		}
		String cvalue = this.example.getCvalue();
		if (cvalue != null && !"".equals(cvalue)) {
			predicatesList
					.add(builder.like(builder.lower(root.get(Configuration_.cvalue)), '%' + cvalue.toLowerCase() + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Configuration> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Configuration entities (e.g. from inside
	 * an HtmlSelectOneMenu)
	 */

	public List<Configuration> getAll() {
		return allConfigs;
	}

	public Converter getConverter() {

		final ConfigurationBean ejbProxy = this.sessionContext.getBusinessObject(ConfigurationBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (StringUtils.isBlank(value))
					return null;
				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof Configuration) 
					return ""+((Configuration)value).getId();
				return ""+(value != null ? value : "");
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Configuration add = new Configuration();

	public Configuration getAdd() {
		return this.add;
	}

	public Configuration getAdded() {
		Configuration added = this.add;
		this.add = new Configuration();
		return added;
	}
	
	public List<ConfigurationType> getTypes() {
		return Arrays.asList(ConfigurationType.values());
	}

	public List<Role> getRoles() {
		return Arrays.asList(Role.values());
	}
	
	public List<Configuration> getAllConfigs() {
		return allConfigs;
	}
	
	public String getValue(String key) {
		return getValue(key, "");
	}
	
	public String getValue(String key, String defVal) {
		return allConfigs.stream().filter(c -> ConfigurationType.simple.equals(c.getType()) && c.getCkey().equalsIgnoreCase(key))
				.map(Configuration::getCvalue).findAny().orElse(defVal);
	}

	public String getDescription(String key) {
		return allConfigs.stream().filter(c -> ConfigurationType.simple.equals(c.getType()) && c.getCkey().equalsIgnoreCase(key))
				.filter(c -> c.getDescription() != null)
				
				.map(Configuration::getDescription).findAny().orElse("");
	}
}
