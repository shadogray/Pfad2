package at.tfr.pfad.view;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.logging.Logger;

import at.tfr.pfad.BookingStatus;
import at.tfr.pfad.dao.BookingRepository;
import at.tfr.pfad.dao.MemberRepository;
import at.tfr.pfad.dao.SquadRepository;
import at.tfr.pfad.model.Activity;
import at.tfr.pfad.model.Booking;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Squad;

@Named
@Stateless
public class BookingActionBean {

	private Logger log = Logger.getLogger(getClass());
	
	@Inject
	private EntityManager entityManager;
	@Inject
	private MemberRepository memberRepo;
	@Inject
	private BookingRepository bookingRepo;
	@Inject
	private SquadRepository squadRepo;
	
	
	public String createBookings(Collection<Squad> squads, Activity activity, boolean withAssistants) {
		
		int created = 0;
		
		squads = squads.stream().map(s -> squadRepo.findBy(s.getId())).collect(Collectors.toList());
		
		try {
			for (Squad squad : squads) {
				for (Member scout : squad.getScouts()) {
					if (!scout.getBookings().stream().filter(b -> activity.equals(b.getActivity())).findAny().isPresent()) {
						createBooking(activity, scout, BookingStatus.created);
						created++;
					}
				}
				if (withAssistants) {
					for (Member ass : squad.getAssistants()) {
						if (!ass.getBookings().stream().filter(b -> activity.equals(b.getActivity())).findAny().isPresent()) {
							createBooking(activity, ass, BookingStatus.created);
							created++;
						}
					}
				}
			}
		} catch (Exception e) {
			log.info("createBookings: "+e, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "createBookings: "+e, e.toString()));
			return "";
		}
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Buchungen hergestellt: "+created, ""));

		return FacesContext.getCurrentInstance().getViewRoot().getViewId()+"?faces-redirect=true";
	}

	public String createBookingsForAllActive(Activity activity) {
		
		int created = 0;
		
		try {
			if (activity == null) {
				throw new NullPointerException("activity cannot be null");
			}
			for (Member scout : memberRepo.findActive()) {
				if (!scout.getBookings().stream().filter(b -> activity.equals(b.getActivity())).findAny().isPresent()) {
					createBooking(activity, scout, BookingStatus.created);
					created++;
				}
			}
		} catch (Exception e) {
			log.info("createBookings: "+e, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "createBookings: "+e, e.toString()));
			return "";
		}
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Buchungen hergestellt: "+created, ""));

		return FacesContext.getCurrentInstance().getViewRoot().getViewId()+"?faces-redirect=true";
	}

	public String createBookingsFromSource(Activity sourceActivity, Activity targetActivity) {
		int created = 0;
		
		try {
			for (Booking old : bookingRepo.findByActivity(sourceActivity)) {
				Member member = old.getMember();
				if (!member.getBookings().stream().filter(b -> targetActivity.equals(b.getActivity())).findAny().isPresent()) {
					createBooking(targetActivity, member, BookingStatus.created);
					created++;
				}
			}
		} catch (Exception e) {
			log.info("createBookingsFromSource: "+e, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "createBookingsFromSource: "+e, e.toString()));
			return "";
		}
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Buchungen hergestellt: "+created, ""));

		return FacesContext.getCurrentInstance().getViewRoot().getViewId()+"?faces-redirect=true";
	}
	
	public int createBookingsForMembers(List<Member> members, Activity activity) {
		int created = 0;
		try {
			for (Member scout : members) {
				scout = memberRepo.fetchBy(scout.getId());
				if (!scout.getBookings().stream().filter(b -> activity.equals(b.getActivity())).findAny().isPresent()) {
					createBooking(activity, scout, BookingStatus.created);
					created++;
				}
			}
		} catch (Exception e) {
			log.info("createBookings: "+e, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "createBookings: "+e, e.toString()));
			return created;
		}
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Buchungen hergestellt: "+created, ""));
		return created;
	}

	private void createBooking(Activity activity, Member scout, BookingStatus status) {
		Booking booking = new Booking();
		booking.setActivity(activity);
		booking.setMember(scout);
		booking.setStatus(status);
		booking.setSquad(scout.getTrupp());
		entityManager.persist(booking);
		entityManager.flush();
	}

}
