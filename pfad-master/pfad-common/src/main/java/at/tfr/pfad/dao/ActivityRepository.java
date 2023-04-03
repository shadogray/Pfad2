package at.tfr.pfad.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.Criteria;
import org.apache.deltaspike.data.api.criteria.CriteriaSupport;
import org.joda.time.DateTime;

import at.tfr.pfad.ActivityStatus;
import at.tfr.pfad.model.Activity;
import at.tfr.pfad.model.Activity_;

@Repository
public abstract class ActivityRepository implements EntityRepository<Activity, Long>, CriteriaSupport<Activity> {

	public static final List<ActivityStatus> ACTIVE = Arrays.asList(new ActivityStatus[] {ActivityStatus.planned, ActivityStatus.started});
	
	public abstract List<Activity> findByStatusAndEndGreaterThanOrderByNameAsc(ActivityStatus status, Date end);
	
	public abstract List<Activity> findByStatusNotEqualAndEndGreaterThanOrderByNameAsc(ActivityStatus status, Date end);
	
	public List<Activity> findActive() {
		return findActive(ACTIVE, null, new Date());
	}
	
	public List<Activity> findActive(List<ActivityStatus> stati, Date start, Date end) {
		Criteria<Activity, Activity> active = criteria().in(Activity_.status, stati.toArray(new ActivityStatus[]{}));
		if (start != null) {
			active = active.lt(Activity_.start, new DateTime(start).minusDays(1).toDate());
		}
		if (end != null) {
			active = active.gt(Activity_.end, new DateTime(end).minusDays(1).toDate());
		}
		return active
				.orderDesc(Activity_.start)
				.orderAsc(Activity_.name)
				.getResultList();
	}
	
	@Query(named="Activity.distName")
	public abstract List<String> findDistinctName();

	@Query(named="Activity.distNameActive")
	public abstract List<String> findDistinctNameActive();

}
