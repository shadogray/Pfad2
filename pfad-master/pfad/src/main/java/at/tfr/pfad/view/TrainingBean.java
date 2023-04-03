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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
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

import at.tfr.pfad.TrainingForm;
import at.tfr.pfad.TrainingPhase;
import at.tfr.pfad.dao.TrainingRepository;
import at.tfr.pfad.model.Training;
import at.tfr.pfad.model.Training_;

/**
 * Backing bean for Training entities.
 * This class provides CRUD functionality for all Training entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ViewScoped
public class TrainingBean extends BaseBean<Training> implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Training entities
	 */

	@Inject
	private TrainingRepository TrainingRepo;
	
	private List<Training> allTrainings;
	
	private Long id;

	@PostConstruct
	public void init() {
		allTrainings = getAll();
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Training training;

	public Training getTraining() {
		return this.training;
	}

	public void setTraining(Training Training) {
		this.training = Training;
	}

	public String create() {
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.id == null) {
			this.training = this.example;
		} else {
			this.training = findById(getId());
		}
	}

	public Training findById(Long id) {

		return TrainingRepo.findBy(id);
	}

	/*
	 * Support updating and deleting Training entities
	 */

	@Override
	public boolean isUpdateAllowed() {
		return isAdmin() || isGruppe() || isVorstand() || isKassier() || isLeiter() || isTrainer();
	}

	public String update() {

		if (!isUpdateAllowed())
			throw new SecurityException("Update denied for: "+sessionBean.getUser());

		try {
			if (this.id == null) {
				this.entityManager.persist(this.training);
				return "search?faces-redirect=true";
			} else {
				training = entityManager.merge(training);
				return "view?faces-redirect=true&id=" + this.training.getId();
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
			Training deletableEntity = findById(getId());

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
	 * Support searching Training entities with pagination
	 */

	private List<Training> pageItems;

	private Training example = new Training();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Training getExample() {
		return this.example;
	}

	public void setExample(Training example) {
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
		Root<Training> root = countCriteria.from(Training.class);
		countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Training> criteria = builder.createQuery(Training.class);
		root = criteria.from(Training.class);
		TypedQuery<Training> query = this.entityManager
				.createQuery(criteria.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Training> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String name = this.example.getName();
		if (StringUtils.isNoneBlank(name)) {
			predicatesList.add(builder.like(builder.lower(root.get(Training_.name)), '%' + name.toLowerCase() + '%'));
		}
		
		if (example.getForm() != null) {
			predicatesList.add(builder.equal(root.get(Training_.form), example.getForm()));
		}

		if (example.getPhase() != null) {
			predicatesList.add(builder.equal(root.get(Training_.phase), example.getPhase()));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Training> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Training entities (e.g. from inside
	 * an HtmlSelectOneMenu)
	 */

	public List<Training> getAllTrainings() {
		return allTrainings;
	}
	
	public List<Training> getAll() {

		CriteriaQuery<Training> criteria = this.entityManager.getCriteriaBuilder()
				.createQuery(Training.class);
		return this.entityManager.createQuery(criteria.select(criteria.from(Training.class)))
				.getResultList().stream().sorted((x,y)->x.getName().compareTo(y.getName())).collect(Collectors.toList());
	}
	
	public Converter getConverter() {

		return new Converter() {

			final TrainingBean ejbProxy = sessionContext.getBusinessObject(TrainingBean.class);

			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (StringUtils.isBlank(value))
					return null;
				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof Training) 
					return ""+((Training)value).getId();
				return ""+(value != null ? value : "");
			}
		};
	}

	public Converter getListConverter() {
		return new Converter() {
			
			final TrainingBean ejbProxy = sessionContext.getBusinessObject(TrainingBean.class);

			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof Collection) {
					return ((Collection<Training>)value).stream().filter(o->o != null)
							.filter(f->f.getId() != null).map(f->f.getId().toString()).collect(Collectors.joining(","));
				}
				return "";
			}
			
			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (StringUtils.isNotBlank(value)) {
					return Stream.of(value.split(","))
							.map(id->ejbProxy.findById(Long.valueOf(id)))
							.filter(o->o != null).collect(Collectors.toList());
				}
				return new ArrayList<>();
			}
		};
	}
	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Training add = new Training();

	public Training getAdd() {
		return this.add;
	}

	public Training getAdded() {
		Training added = this.add;
		this.add = new Training();
		return added;
	}
	
	public List<TrainingForm> getForms() {
		return Arrays.asList(TrainingForm.values());
	}

	public List<TrainingPhase> getPhases() {
		return Arrays.asList(TrainingPhase.values());
	}
}
