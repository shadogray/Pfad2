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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import at.tfr.pfad.ActivityStatus;
import at.tfr.pfad.ActivityType;
import at.tfr.pfad.Role;
import at.tfr.pfad.dao.ActivityRepository;
import at.tfr.pfad.model.Activity;
import at.tfr.pfad.model.Activity_;

/**
 * Backing bean for Activity entities.
 * This class provides CRUD functionality for all Activity entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ViewScoped
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ActivityBean extends BaseBean<Activity> implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Activity entities
	 */

	@Inject
	private ActivityRepository activityRepo;
	
	private List<Activity> allActivities;
	
	private Long id;

	public void init() {
		allActivities = getAll();
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Activity activity;

	public Activity getActivity() {
		return this.activity;
	}

	public void setActivity(Activity Activity) {
		this.activity = Activity;
	}

	public String create() {
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.id == null) {
			this.activity = this.example;
		} else {
			this.activity = findActivityById(getId());
		}
	}

	public Activity findActivityById(Long id) {

		return activityRepo.findBy(id);
	}

	/*
	 * Support updating and deleting Activity entities
	 */

	@Override
	public boolean isUpdateAllowed() {
		return isAdmin() || isGruppe() || isVorstand() || isKassier() || isLeiter();
	}

	public String update() {

		if (!isUpdateAllowed())
			throw new SecurityException("Update denied for: "+sessionBean.getUser());

		try {
			if (this.id == null) {
				this.entityManager.persist(this.activity);
				return "search?faces-redirect=true";
			} else {
				activity = entityManager.merge(activity);
				return "view?faces-redirect=true&id=" + this.activity.getId();
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
			Activity deletableEntity = findActivityById(getId());

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
	 * Support searching Activity entities with pagination
	 */

	private List<ActivityUI> pageItems;

	private Activity example = new Activity();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Activity getExample() {
		return this.example;
	}

	public void setExample(Activity example) {
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
		Root<Activity> root = countCriteria.from(Activity.class);
		countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Activity> criteria = builder.createQuery(Activity.class);
		root = criteria.from(Activity.class);
		TypedQuery<Activity> query = this.entityManager
				.createQuery(criteria.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
		List<Activity> activities = query.getResultList();
		Map<Activity,Number> group = bookingRepo.summarize(activities);
		activities.forEach(a -> { if (!group.containsKey(a)) group.put(a, new Integer(0)); });
		this.pageItems = group.entrySet().stream().map(e -> new ActivityUI(e.getKey(),e.getValue())).collect(Collectors.toList());
	}

	private Predicate[] getSearchPredicates(Root<Activity> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String name = this.example.getName();
		if (StringUtils.isNoneBlank(name)) {
			predicatesList.add(builder.like(builder.lower(root.get(Activity_.name)), '%' + name.toLowerCase() + '%'));
		}
		
		if (example.getType() != null) {
			predicatesList.add(builder.equal(root.get(Activity_.type), example.getType()));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<ActivityUI> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Activity entities (e.g. from inside
	 * an HtmlSelectOneMenu)
	 */

	public List<Activity> getAll() {
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Activity> criteria = cb.createQuery(Activity.class);
		Root<Activity> root = criteria.from(Activity.class);
		criteria = criteria.select(root).orderBy(cb.desc(root.get(Activity_.start)), cb.asc(root.get(Activity_.name)));
		return this.entityManager.createQuery(criteria)
				.getResultList().stream().sorted((x,y)->x.getName().compareTo(y.getName())).collect(Collectors.toList());
	}
	
	public List<Activity> getActive() {
		return activityRepo.findActive();
	}

	public Converter getConverter() {

		return new Converter() {

			final ActivityBean ejbProxy = sessionContext.getBusinessObject(ActivityBean.class);

			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (StringUtils.isBlank(value))
					return null;
				return ejbProxy.findActivityById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof Activity) 
					return ((Activity)value).getId() != null ? ""+((Activity)value).getId() : null;
				return ""+(value != null ? value : "");
			}
		};
	}

	public Converter getListConverter() {
		return new Converter() {
			
			final ActivityBean ejbProxy = sessionContext.getBusinessObject(ActivityBean.class);

			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof Collection) {
					return ((Collection<Activity>)value).stream().filter(o->o != null)
							.filter(f->f.getId() != null).map(f->f.getId().toString()).collect(Collectors.joining(","));
				}
				return "";
			}
			
			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (StringUtils.isNotBlank(value)) {
					return Stream.of(value.split(","))
							.map(id->ejbProxy.findActivityById(Long.valueOf(id)))
							.filter(o->o != null).collect(Collectors.toList());
				}
				return new ArrayList<>();
			}
		};
	}
	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Activity add = new Activity();

	public Activity getAdd() {
		return this.add;
	}

	public Activity getAdded() {
		Activity added = this.add;
		this.add = new Activity();
		return added;
	}
	
	public List<ActivityType> getTypes() {
		return Arrays.asList(ActivityType.values());
	}

	public List<ActivityStatus> getStati() {
		return Arrays.asList(ActivityStatus.values());
	}

	public List<Role> getRoles() {
		return Arrays.asList(Role.values());
	}
}
