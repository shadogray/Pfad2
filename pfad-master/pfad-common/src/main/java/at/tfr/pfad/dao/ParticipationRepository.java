package at.tfr.pfad.dao;

import at.tfr.pfad.ParticipationStatus;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Participation;
import at.tfr.pfad.model.Participation_;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.Criteria;
import org.apache.deltaspike.data.api.criteria.CriteriaSupport;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
public abstract class ParticipationRepository implements EntityRepository<Participation, Long>, CriteriaSupport<Participation> {

	public static final List<ParticipationStatus> ACTIVE = Arrays.asList(new ParticipationStatus[] {ParticipationStatus.created, ParticipationStatus.started});
	
	public abstract List<Participation> findByStatusAndEndGreaterThanOrderByNameAsc(ParticipationStatus status, Date end);
	
	public abstract List<Participation> findByStatusNotEqualAndEndGreaterThanOrderByNameAsc(ParticipationStatus status, Date end);

	public abstract List<Participation> findByMember(Member member);

	public List<Participation> findActive() {
		return findActive(ACTIVE, null, new Date());
	}
	
	public List<Participation> findActive(List<ParticipationStatus> stati, Date start, Date end) {
		Criteria<Participation, Participation> active = criteria().in(Participation_.status, stati.toArray(new ParticipationStatus[]{}));
		if (start != null) {
			active = active.lt(Participation_.startDate, new DateTime(start).minusDays(1).toDate());
		}
		if (end != null) {
			active = active.gt(Participation_.endDate, new DateTime(end).minusDays(1).toDate());
		}
		return active
				.orderDesc(Participation_.startDate)
				.getResultList();
	}
}
