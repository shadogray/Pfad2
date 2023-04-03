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
import java.util.Date;
import java.util.List;
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

import at.tfr.pfad.ParticipationCostType;
import at.tfr.pfad.ParticipationStatus;
import at.tfr.pfad.TrainingForm;
import at.tfr.pfad.TrainingPhase;
import at.tfr.pfad.dao.ParticipationRepository;
import at.tfr.pfad.model.Member_;
import at.tfr.pfad.model.Participation;
import at.tfr.pfad.model.Participation_;
import at.tfr.pfad.model.Training_;

/**
 * Backing bean for Participation entities.
 * This class provides CRUD functionality for all Participation entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ViewScoped
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ParticipationBean extends BaseBean<Participation> implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Participation entities
	 */

	@Inject
	private ParticipationRepository participationRepo;
	
	private Long id;

	public void init() {
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Participation participation;
	private TrainingForm trainingForm;
	private TrainingPhase trainingPhase;

	public Participation getParticipation() {
		return this.participation;
	}

	public void setParticipation(Participation Participation) {
		this.participation = Participation;
	}

	public TrainingForm getTrainingForm() {
		return trainingForm;
	}

	public void setTrainingForm(TrainingForm trainingForm) {
		this.trainingForm = trainingForm;
	}

	public TrainingPhase getTrainingPhase() {
		return trainingPhase;
	}

	public void setTrainingPhase(TrainingPhase trainingPhase) {
		this.trainingPhase = trainingPhase;
	}

	public String create() {
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.id == null) {
			this.participation = this.example;
		} else {
			this.participation = findParticipationById(getId());
		}
	}

	/*
	 * Support updating and deleting Participation entities
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
				this.entityManager.persist(this.participation);
				return "search?faces-redirect=true";
			} else {
				participation = entityManager.merge(participation);
				return "view?faces-redirect=true&id=" + this.participation.getId();
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
			Participation deletableEntity = findParticipationById(getId());

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
	 * Support searching Participation entities with pagination
	 */

	private List<Participation> pageItems;

	private Participation example = new Participation();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Participation getExample() {
		return this.example;
	}

	public void setExample(Participation example) {
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
		Root<Participation> root = countCriteria.from(Participation.class);
		countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Participation> criteria = builder.createQuery(Participation.class);
		root = criteria.from(Participation.class);
		TypedQuery<Participation> query = this.entityManager
				.createQuery(criteria.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Participation> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		Date start = this.example.getStart();
		if (start != null) {
			predicatesList.add(builder.greaterThanOrEqualTo(root.get(Participation_.start), start));
		}
		
		Date end = this.example.getEnd();
		if (end != null) {
			predicatesList.add(builder.greaterThanOrEqualTo(root.get(Participation_.end), end));
		}
		
		if (example.getStatus() != null) {
			predicatesList.add(builder.equal(root.get(Participation_.status), example.getStatus()));
		}
		
		if (trainingSearch != null && trainingSearch.getId() != null) {
			predicatesList.add(builder.equal(root.get(Participation_.training), trainingSearch));
		}
	
		if (StringUtils.isNotBlank(memberExample.getName())) {
			predicatesList.add(builder.or(
					builder.like(builder.lower(root.get(Participation_.member).get(Member_.name)), 
							"%"+memberExample.getName().toLowerCase()+"%"),
					builder.like(builder.lower(root.get(Participation_.member).get(Member_.vorname)), 
							"%"+memberExample.getName().toLowerCase()+"%")
					));
		}
		
		if (memberSearch != null && memberSearch.getId() != null) {
			predicatesList.add(builder.equal(root.get(Participation_.member), memberSearch));
		}
		
		if (trainingForm != null) {
			predicatesList.add(builder.equal(root.get(Participation_.training).get(Training_.form), trainingForm));
		}
	
		if (trainingPhase != null) {
			predicatesList.add(builder.equal(root.get(Participation_.training).get(Training_.phase), trainingPhase));
		}
	
		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Participation> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Participation entities (e.g. from inside
	 * an HtmlSelectOneMenu)
	 */

	public List<Participation> getActive() {
		return participationRepo.findActive();
	}

	public Converter getConverter() {

		return new Converter() {

			final ParticipationBean ejbProxy = sessionContext.getBusinessObject(ParticipationBean.class);

			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (StringUtils.isBlank(value))
					return null;
				return ejbProxy.findParticipationById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof Participation) 
					return ""+((Participation)value).getId();
				return ""+(value != null ? value : "");
			}
		};
	}

	public Converter getListConverter() {
		return new Converter() {
			
			final ParticipationBean ejbProxy = sessionContext.getBusinessObject(ParticipationBean.class);

			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof Collection) {
					return ((Collection<Participation>)value).stream().filter(o->o != null)
							.filter(f->f.getId() != null).map(f->f.getId().toString()).collect(Collectors.joining(","));
				}
				return "";
			}
			
			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (StringUtils.isNotBlank(value)) {
					return Stream.of(value.split(","))
							.map(id->ejbProxy.findParticipationById(Long.valueOf(id)))
							.filter(o->o != null).collect(Collectors.toList());
				}
				return new ArrayList<>();
			}
		};
	}
	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Participation add = new Participation();

	public Participation getAdd() {
		return this.add;
	}

	public Participation getAdded() {
		Participation added = this.add;
		this.add = new Participation();
		return added;
	}
	
	public List<ParticipationStatus> getStati() {
		return Arrays.asList(ParticipationStatus.values());
	}

	public List<ParticipationCostType> getCostTypes() {
		return Arrays.asList(ParticipationCostType.values());
	}
}
