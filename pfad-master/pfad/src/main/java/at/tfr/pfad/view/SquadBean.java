/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.view;

import at.tfr.pfad.SquadType;
import at.tfr.pfad.model.Configuration;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Squad;
import at.tfr.pfad.model.Squad_;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.Stateful;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.model.CollectionDataModel;
import jakarta.faces.model.DataModel;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Backing bean for Squad entities.
 * This class provides CRUD functionality for all Squad entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ViewScoped
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class SquadBean extends BaseBean<Squad,Squad> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/*
	 * Support creating and retrieving Squad entities
	 */

	private Long id;
	private final Map<String,Object> beans = new HashMap<>();
	private List<Configuration> queries = Collections.emptyList();
	
	@PostConstruct
	public void init() {
		Squad responsibleFor = sessionBean.isResponsibleFor();
		beans.put("truppName", responsibleFor != null ? responsibleFor.getName() : "");
		beans.put("truppId", responsibleFor != null ? responsibleFor.getId() : "0");
		beans.put("trupp", sessionBean.getSquad());
		beans.put("user", sessionBean.getUser());
		if (responsibleFor != null && (isGruppe() || responsibleFor.getId().equals(id))) {
			queries = sessionBean.getConfig().stream()
					.filter(q -> q.getCkey().startsWith(responsibleFor.getName()+".download."))
					.collect(Collectors.toList());
		}
		queries.forEach(q -> q.setUiName(q.isDownload() ? q.getDownloadName() : q.getCkey()));
		queries.forEach(q -> q.setUiName(templateUtils.replace(q.getUiName(), beans, q.getUiName())));
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Squad squad;
	private DataModel<Member> scouts;
	private DataModel<Member> assistants;

	public Squad getSquad() {
		return this.squad;
	}

	public void setSquad(Squad squad) {
		this.squad = squad;
	}
	
	public DataModel<Member> getAssistants() {
		return assistants;
	}
	
	public DataModel<Member> getScouts() {
		return scouts;
	}

	public String create() {
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.id == null) {
			this.squad = this.example;
		} else {
			this.squad = findById(getId());
			this.squad.getScouts().size();
			this.squad.getAssistants().size();
		}
		entity = squad;
		init();
		scouts = new CollectionDataModel<>(squad.getScouts());
		assistants = new CollectionDataModel<>(squad.getAssistants());
	}

	public Squad findById(Long id) {

		return entityManager.find(Squad.class, id);
	}

	/*
	 * Support updating and deleting Squad entities
	 */

	@Override
	public boolean isUpdateAllowed() {
		return isUpdateAllowed(squad);
	}

	public boolean isUpdateAllowed(Squad squad) {
		return isAdmin() || isGruppe() || isVorstand()
				|| (sessionContext.getCallerPrincipal().getName().equals(squad.getLogin()) && !isRegistrationEnd());
	}

	public boolean isDownloadAllowed() {
		return isDownloadAllowed(squad);
	}
	
	public boolean isDownloadAllowed(Squad squad) {
		return isAdmin() || isGruppe() || isVorstand()
				|| sessionContext.getCallerPrincipal().getName().equalsIgnoreCase(squad.getLogin());
	}

	public String update() {

		if (!isUpdateAllowed())
			throw new SecurityException("only admins, gruppe, " + squad.getLogin() + " may update entry");

		log.info("updated " + squad + " by " + sessionContext.getCallerPrincipal());
		this.squad.setChanged(new Date());
		this.squad.setChangedBy(sessionContext.getCallerPrincipal().getName());

		try {
			if (this.id == null) {
				this.squad.setCreated(new Date());
				this.squad.setCreatedBy(sessionContext.getCallerPrincipal().getName());
				entityManager.persist(this.squad);
				if (this.squad.getId() != null) {
					return "view?faces-redirect=true&id=" + this.squad.getId();
				}
				return "search?faces-redirect=true";
			} else {
				squad = entityManager.merge(squad);
				return "view?faces-redirect=true&id=" + this.squad.getId();
			}
		} catch (Exception e) {
			log.info("update: "+e, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {

		if (!isAdmin())
			throw new SecurityException("only admins may delete entry");

		log.info("deleted " + squad + " by " + sessionContext.getCallerPrincipal());

		try {
			Squad deletableEntity = findById(getId());

			entityManager.remove(deletableEntity);
			entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			log.info("update: "+e, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching Squad entities with pagination
	 */

	private List<Squad> pageItems;

	private Squad example = new Squad();

	public Squad getExample() {
		return this.example;
	}

	public void setExample(Squad example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Squad> root = countCriteria.from(Squad.class);
		countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
		this.count = entityManager.createQuery(countCriteria).getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Squad> criteria = builder.createQuery(Squad.class);
		root = criteria.from(Squad.class);
		root.fetch(Squad_.assistants);
		root.fetch(Squad_.scouts);
		TypedQuery<Squad> query = entityManager
				.createQuery(criteria.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
		this.pageItems = query.getResultList().stream().sorted().collect(Collectors.toList());
	}

	private Predicate[] getSearchPredicates(Root<Squad> root) {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		SquadType type = this.example.getType();
		if (type != null) {
			predicatesList.add(builder.equal(root.get(Squad_.type), type));
		}
		String name = this.example.getName();
		if (name != null && !"".equals(name)) {
			predicatesList.add(builder.like(builder.lower(root.get(Squad_.name)), '%' + name.toLowerCase() + '%'));
		}
		Member leaderMale = this.example.getLeaderMale();
		if (leaderMale != null) {
			predicatesList.add(builder.equal(root.get(Squad_.leaderMale), leaderMale));
		}
		Member leaderFemale = this.example.getLeaderFemale();
		if (leaderFemale != null) {
			predicatesList.add(builder.equal(root.get(Squad_.leaderFemale), leaderFemale));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Squad> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Squad entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Squad> getAll() {

		CriteriaQuery<Squad> criteria = entityManager.getCriteriaBuilder().createQuery(Squad.class);
		return entityManager.createQuery(criteria.select(criteria.from(Squad.class))).getResultList().stream()
				.sorted().collect(Collectors.toList());
	}

	public Converter getConverter() {

		final SquadBean ejbProxy = this.sessionContext.getBusinessObject(SquadBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (StringUtils.isBlank(value))
					return null;
				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof Squad) 
					return ""+((Squad)value).getId();
				return ""+(value != null ? value : "");
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Squad add = new Squad();

	public Squad getAdd() {
		return this.add;
	}

	public Squad getAdded() {
		Squad added = this.add;
		this.add = new Squad();
		return added;
	}

	public List<SquadType> getTypes() {
		return Arrays.asList(SquadType.values());
	}
	
	public List<Configuration> getQueries() {
		return queries;
	}
}
