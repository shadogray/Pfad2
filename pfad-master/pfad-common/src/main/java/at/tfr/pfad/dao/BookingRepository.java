package at.tfr.pfad.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.deltaspike.data.api.EntityManagerDelegate;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.CriteriaSupport;

import at.tfr.pfad.model.Activity;
import at.tfr.pfad.model.Booking;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Payment;

@Repository
public abstract class BookingRepository implements EntityRepository<Booking, Long>, CriteriaSupport<Booking>, EntityManagerDelegate<Booking> {

	@Inject
	private EntityManager em;
	
	@Query
	public abstract List<Booking> findByActivity(Activity activity);
	
	public Map<Activity,Number> summarize(Collection<Activity> activities) {
		@SuppressWarnings("unchecked")
		List<Object[]> list = em.createNamedQuery("BookingAcvitySummary")
			.setParameter("activities", activities)
			.getResultList(); 
		return list.stream().collect(Collectors.toMap(arr -> (Activity)arr[0], arr -> (Number)arr[1]));
	}
	
	@Query(named = "BookingsForPayment")
	public abstract List<Booking> findByPayment(Payment payment);
	
	@SuppressWarnings("unchecked")
	public List<Booking> findByIds(Collection<Long> ids) {
		return em.createQuery("select b from Booking b where b.id in :ids")
				.setParameter("ids", ids).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Booking> findByMemberNames(String text, Activity activity) {
		List<Booking> list = em.createQuery("select b from Booking b "
				+ "where b.member.trupp != null and b.activity = :activity "
				+ "and locate(b.member.name, :text) > 0 and locate(b.member.vorname, :text) > 0")
		.setParameter("text", text)
		.setParameter("activity", activity)
		.getResultList();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Booking> findByMemberNameOnly(String text, Activity activity) {
		List<Booking> list = em.createQuery("select b from Booking b "
				+ "where b.member.trupp != null and b.activity = :activity "
				+ "and locate(b.member.name, :text) > 0")
					.setParameter("text", text)
					.setParameter("activity", activity)
					.getResultList();
		return list;
	}
	
	public abstract List<Booking> findByMemberAndActivityOrderByIdDesc(Member member, Activity activity);

}
