/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.view;

import at.tfr.pfad.ActivityStatus;
import at.tfr.pfad.BookingStatus;
import at.tfr.pfad.dao.ActivityRepository;
import at.tfr.pfad.model.*;
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
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Backing bean for Booking entities. This class provides CRUD functionality for
 * all Booking entities. It focuses purely on Java EE 6 standards (e.g.
 * <tt>&#64;ConversationScoped</tt> for state management,
 * <tt>PersistenceContext</tt> for persistence, <tt>CriteriaBuilder</tt> for
 * searches) rather than introducing a CRUD framework or custom base class.
 */

@Named
@Stateful
@ViewScoped
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class BookingBean extends BaseBean<Booking,BookingUI> implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Booking entities
	 */

	@Inject
	private ActivityRepository activityRepo;
	@Inject
	private PaymentBean paymentBean;

	private boolean showFinished;
	private boolean showRegistered;
	private boolean showUnregistered;
	private boolean squadBookingVisible;
	private boolean allBookingVisible;
	private boolean paymentPopupVisible;	

	@Override
	@PostConstruct
	public void init() {
		super.init();
		squads = squadRepo.withAssistants();
	}
	
	public boolean isSquadBookingVisible() {
		return squadBookingVisible;
	}

	public void setSquadBookingVisible(boolean squadBookingVisible) {
		this.squadBookingVisible = squadBookingVisible;
	}

	public boolean isAllBookingVisible() {
		return allBookingVisible;
	}

	public void setAllBookingVisible(boolean allBookingVisible) {
		this.allBookingVisible = allBookingVisible;
	}

	public boolean isShowFinished() {
		return showFinished;
	}

	public void setShowFinished(boolean showFinished) {
		this.showFinished = showFinished;
	}

	public boolean isShowRegistered() {
		return showRegistered;
	}
	
	public void setShowRegistered(boolean showRegistered) {
		this.showRegistered = showRegistered;
	}
	
	public boolean isShowUnregistered() {
		return showUnregistered;
	}
	
	public void setShowUnregistered(boolean showUnregistered) {
		this.showUnregistered = showUnregistered;
	}
	
	public List<Activity> getActivities() {
		if (showFinished) {
			return activityRepo.findAll();
		} else {
			List<Activity> active = activityRepo.findActive();
			if (booking != null && booking.getActivity() != null) {
				active.add(booking.getActivity());
			}
			return active;
		}
	}
	
	public String create() {
		return "create?faces-redirect=true";
	}

	public void retrieve(Long id) {
		this.id = id;
		retrieve();
	}
	
	public void retrieve() {

		FacesContext ctx = FacesContext.getCurrentInstance();
		if (ctx.isPostback() && !ctx.getPartialViewContext().isAjaxRequest()) {
			return;
		}

		try {
			if (id == null) {
				booking = getBookingExample();
			} else {
				booking = findById(getId());
				booking.getPayments().size();
				if (booking.getSquad() != null) {
					booking.getSquad().getName();
				}
				filteredMembers.add(booking.getMember());
			}
			entity = booking;
		} catch (Exception e) {
			log.info("retrieve: "+e, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
		}
	}

	public Booking findById(Long id) {

		return entityManager.find(Booking.class, id);
	}

	/*
	 * Support updating and deleting Booking entities
	 */

	@Override
	public boolean isUpdateAllowed() {
		return isAdmin() || isGruppe() || isVorstand() || isKassier() || isLeiter();
	}

	public String update() {

		if (!isUpdateAllowed())
			throw new SecurityException("Update denied for: "+sessionBean.getUser());

		try {

			if (booking.getId() == null && booking.getMember() != null && 
					entityManager.find(Member.class, booking.getMember().getId())
					.getBookings().stream().anyMatch(b -> booking.getActivity().equals(b.getActivity()))) {
				throw new Exception("Duplicate Booking: " + booking);
			}

			if (booking.getMember().getTrupp() != null) {
				booking.setSquad(booking.getMember().getTrupp());
			}

			if (id == null) {
				entityManager.persist(booking);
				id = booking.getId();
				return "search?faces-redirect=true";
			} else {
				booking = entityManager.merge(booking);
				return "view?faces-redirect=true&id=" + booking.getId();
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
			Booking deletableEntity = findById(getId());

			if (!deletableEntity.getPayments().isEmpty()) {
				throw new Exception("Payments exists for Booking: " + deletableEntity.getPayments());
			}

			entityManager.remove(deletableEntity);
			entityManager.flush();

			close();
			return "search?faces-redirect=true";

		} catch (Exception e) {
			log.info("update: "+e, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching Booking entities with pagination
	 */

	private List<BookingUI> pageItems;
	private DataModel<BookingUI> dataModel;

	public Booking getExample() {
		return this.getBookingExample();
	}

	public void setExample(Booking example) {
		setBookingExample(example);
	}

	public String search() {
		this.page = 0;
		return FacesContext.getCurrentInstance().getViewRoot().getViewId()+"?faces-redirect=true&includeViewParams=true";
	}

	public void paginate() {

//		if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
//			return;
//		}

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Booking> root = countCriteria.from(Booking.class);
		countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
		this.count = entityManager.createQuery(countCriteria).getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Booking> criteria = builder.createQuery(Booking.class);
		root = criteria.from(Booking.class);
		TypedQuery<Booking> query = entityManager
				.createQuery(criteria.select(root).distinct(true).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
		this.pageItems = query.getResultList().stream().map(b -> 
			new BookingUI(b, b.getActivity(), b.getMember(), b.getMember().getTrupp(), b.getPayments(), 
					squads.stream().anyMatch(s -> b.getMember().equals(s.getLeaderFemale()) || b.getMember().equals(s.getLeaderMale())),
					squads.stream().anyMatch(s -> s.getAssistants().contains(b.getMember()))
					)).collect(Collectors.toList());
		dataModel = new CollectionDataModel<BookingUI>(pageItems);
	}

	private Predicate[] getSearchPredicates(Root<Booking> root) {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		if (getBookingExample().getActivity() != null) {
			predicatesList.add(builder.equal(root.get(Booking_.activity), getBookingExample().getActivity()));
		}

		if (memberExample.getId() != null) {
			predicatesList.add(builder.equal(root.get(Booking_.member), memberExample));
		}

		if (memberExample != null && StringUtils.isNotBlank(memberExample.getName())) {
			for (String val : memberExample.getName().toLowerCase().split(" ")) {
				predicatesList.add(builder.or(
						builder.like(builder.lower(root.get(Booking_.member).get(Member_.name)), "%"+val+"%"),
						builder.like(builder.lower(root.get(Booking_.member).get(Member_.vorname)), "%"+val+"%")
						));
			}
		}

		if (getBookingExample().getSquad() != null) {
			predicatesList.add(builder.equal(root.get(Booking_.squad), getBookingExample().getSquad()));
		}

		if (getBookingExample().getStatus() != null) {
			predicatesList.add(builder.equal(root.get(Booking_.status), getBookingExample().getStatus()));
		}
		String comment = this.getBookingExample().getComment();
		if (StringUtils.isNoneBlank(comment)) {
			predicatesList.add(builder.like(builder.lower(root.get(Booking_.comment)), '%' + comment.toLowerCase() + '%'));
		}
		
		if (!showFinished && !(getBookingExample().getActivity() != null && getBookingExample().getActivity().isFinished())) {
			Join<Booking,Activity> act = root.join(Booking_.activity);
			predicatesList.add(builder.or(
					builder.isNull(act.get(Activity_.endDate)),
					builder.greaterThan(act.get(Activity_.endDate), new Date())));
			predicatesList.add(builder.notEqual(act.get(Activity_.status), ActivityStatus.cancelled));
		}
		
		if (showRegistered) {
			predicatesList.add(builder.equal(root.get(Booking_.registered), true));
		}

		if (showUnregistered) {
			predicatesList.add(builder.equal(root.get(Booking_.registered), false));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<BookingUI> getPageItems() {
		return this.pageItems;
	}

	public DataModel<BookingUI> getDataModel() {
		return dataModel;
	}

	/*
	 * Support listing and POSTing back Booking entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Booking> getAll() {

		CriteriaQuery<Booking> criteria = entityManager.getCriteriaBuilder().createQuery(Booking.class);
		return entityManager.createQuery(criteria.select(criteria.from(Booking.class))).getResultList();
	}

	public Converter getConverter() {

		return new Converter() {

			final BookingBean ejbProxy = sessionContext.getBusinessObject(BookingBean.class);

			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (StringUtils.isBlank(value))
					return null;
				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof Booking) 
					return ((Booking)value).getId() != null ? ""+((Booking)value).getId() : null;
				return ""+(value != null ? value : "");
			}
		};
	}

	public Converter getListConverter() {
		return new Converter() {
			
			final BookingBean ejbProxy = sessionContext.getBusinessObject(BookingBean.class);
			
			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof Collection) {
					return ((Collection<Booking>)value).stream().filter(o->o != null)
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

	private Booking add = new Booking();

	public Booking getAdd() {
		return this.add;
	}

	public Booking getAdded() {
		Booking added = this.add;
		this.add = new Booking();
		return added;
	}

	public List<BookingStatus> getStati() {
		return Arrays.asList(BookingStatus.values());
	}

	private Activity activity;
	private List<Squad> squads = new ArrayList<>();
	private boolean withAssistants;

	public List<Squad> getSquads() {
		return squads;
	}

	public void setSquads(List<Squad> squads) {
		this.squads = squads;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public boolean isWithAssistants() {
		return withAssistants;
	}

	public void setWithAssistants(boolean withAssistants) {
		this.withAssistants = withAssistants;
	}

	public boolean isCreateAllAllowed() {
		return isAdmin() || isGruppe();
	}

	public boolean isPaymentPopupVisible() {
		return paymentPopupVisible;
	}
	
	public void setPaymentPopupVisible(boolean paymentPopupVisible) {
		this.paymentPopupVisible = paymentPopupVisible;
	}
	
	public void retrieveAndGetPayment(Long id) {
		booking = null; // so much caching around :-/
		retrieve(id);
		paymentBean.setMemberToAdd(null);
		if (!booking.getPayments().isEmpty()) {
			paymentBean.setId(booking.getPayments().iterator().next().getId());
		} else {
			paymentBean.setExample(new Payment());
			paymentBean.setId(null);
		}
		paymentBean.retrieve();
		Payment pay = paymentBean.getPayment();
		if (pay.getId() == null) {
			pay.getBookings().add(booking);
			booking.getPayments().add(pay);
		}
		pay.updateType(booking);
	}

	public void initNewPayment() {
		paymentBean.setMemberToAdd(null);
		paymentBean.setExample(new Payment());
		paymentBean.setId(paymentBean.getPayment() != null ? paymentBean.getPayment().getId() : null);
		paymentBean.retrieve();
		Payment pay = paymentBean.getPayment();
		pay.getBookings().add(booking);
		booking.getPayments().add(pay);
		pay.updateType(booking);
	}
}
