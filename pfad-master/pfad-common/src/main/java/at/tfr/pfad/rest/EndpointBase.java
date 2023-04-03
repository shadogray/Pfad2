package at.tfr.pfad.rest;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.logging.Logger;

import at.tfr.pfad.dao.ActivityRepository;
import at.tfr.pfad.dao.BookingRepository;
import at.tfr.pfad.dao.MemberRepository;
import at.tfr.pfad.dao.PaymentRepository;
import at.tfr.pfad.dao.SquadRepository;

public class EndpointBase<T> {

	protected Logger log = Logger.getLogger(getClass());
	
	@Inject
	protected EntityManager em;
	@Inject
	protected MemberRepository memberRepo;
	@Inject
	protected SquadRepository squadRepo;
	@Inject
	protected PaymentRepository paymentRepo;
	@Inject
	protected BookingRepository bookingRepo;
	@Inject
	protected ActivityRepository activityRepo;

}
