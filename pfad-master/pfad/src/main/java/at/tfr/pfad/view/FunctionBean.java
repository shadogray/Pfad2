/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import at.tfr.pfad.model.Function;
import at.tfr.pfad.model.Function_;

/**
 * Backing bean for Function entities.
 * This class provides CRUD functionality for all Function entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ViewScoped
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class FunctionBean extends BaseBean<Function> implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Function entities
	 */

	private List<Function> allFunctions;
	
	private Long id;

	@PostConstruct
	public void init() {
		allFunctions = getAll();
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Function function;

	public Function getFunction() {
		return this.function;
	}

	public void setFunction(Function function) {
		this.function = function;
	}

	public String create() {
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.id == null) {
			this.function = this.example;
		} else {
			this.function = findById(getId());
		}
	}

	public Function findById(Long id) {

		return this.entityManager.find(Function.class, id);
	}

	/*
	 * Support updating and deleting Function entities
	 */

	@Override
	public boolean isUpdateAllowed() {
		return isAdmin() || isGruppe() || isVorstand();
	}

	public String update() {

		if (!isUpdateAllowed())
			throw new SecurityException("Update denied for: "+sessionBean.getUser());

		log.info("updated " + function + " by " + sessionContext.getCallerPrincipal());

		try {
			if (this.id == null) {
				this.entityManager.persist(this.function);
				return "search?faces-redirect=true";
			} else {
				function = entityManager.merge(function);
				return "view?faces-redirect=true&id=" + this.function.getId();
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

		log.info("deleted " + function + " by " + sessionContext.getCallerPrincipal());

		try {
			Function deletableEntity = findById(getId());

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
	 * Support searching Function entities with pagination
	 */

	private List<Function> pageItems;

	private Function example = new Function();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Function getExample() {
		return this.example;
	}

	public void setExample(Function example) {
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
		Root<Function> root = countCriteria.from(Function.class);
		countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Function> criteria = builder.createQuery(Function.class);
		root = criteria.from(Function.class);
		TypedQuery<Function> query = this.entityManager
				.createQuery(criteria.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Function> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String function = this.example.getFunction();
		if (function != null && !"".equals(function)) {
			predicatesList
					.add(builder.like(builder.lower(root.get(Function_.function)), '%' + function.toLowerCase() + '%'));
		}
		String key = this.example.getKey();
		if (key != null && !"".equals(key)) {
			predicatesList.add(builder.like(builder.lower(root.get(Function_.key)), '%' + key.toLowerCase() + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Function> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Function entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Function> getAll() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Function> criteria = cb.createQuery(Function.class);
		Root<Function> root = criteria.from(Function.class);
		return entityManager.createQuery(criteria.select(root).orderBy(cb.asc(root.get(Function_.function))))
				.getResultList();
	}

	public Converter getConverter() {

		return new Converter() {

			final FunctionBean ejbProxy = sessionContext.getBusinessObject(FunctionBean.class);

			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (StringUtils.isBlank(value) || "[]".equals(value))
					return null;
				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof Function) 
					return ""+((Function)value).getId();
				return ""+(value != null ? value : "");
			}
		};
	}
	
	public Converter getListConverter() {
		return new Converter() {
			
			final FunctionBean ejbProxy = sessionContext.getBusinessObject(FunctionBean.class);
			
			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof Collection) {
					return ((Collection<Function>)value).stream().filter(o->o != null)
							.filter(f->f.getId() != null).map(f->f.getId().toString()).collect(Collectors.joining(","));
				}
				return "";
			}
			
			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (StringUtils.isNotBlank(value)) {
					return Stream.of(value.split(","))
							.map(id->ejbProxy.findById(Long.valueOf(id)))
							.filter(o->o != null).filter(o -> o!=null).collect(Collectors.toList());
				}
				return new ArrayList<>();
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Function add = new Function();

	public Function getAdd() {
		return this.add;
	}

	public Function getAdded() {
		Function added = this.add;
		this.add = new Function();
		return added;
	}
	
	public List<Function> getAllFunctions() {
		return allFunctions;
	}
}
