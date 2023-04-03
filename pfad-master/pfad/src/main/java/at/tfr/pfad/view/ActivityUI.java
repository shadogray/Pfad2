package at.tfr.pfad.view;

import java.util.Date;

import at.tfr.pfad.ActivityStatus;
import at.tfr.pfad.ActivityType;
import at.tfr.pfad.model.Activity;

public class ActivityUI {

	private final Activity activity;
	private Number bookings;
	
	public ActivityUI(Activity activity) {
		this.activity = activity;
	}

	public ActivityUI(Activity activity, Number bookings) {
		this(activity);
		this.bookings = bookings;
	}

	public Activity getActivity() {
		return activity;
	}

	public Number getBookings() {
		return bookings;
	}
	
	public void setBookings(int bookings) {
		this.bookings = bookings;
	}
	
	public boolean equals(Object obj) {
		return activity.equals(obj);
	}

	public int hashCode() {
		return activity.hashCode();
	}

	public Long getId() {
		return activity.getId();
	}

	public String getIdStr() {
		return activity.getIdStr();
	}

	public int getVersion() {
		return activity.getVersion();
	}

	public Date getStart() {
		return activity.getStart();
	}

	public String getStartString() {
		return activity.getStartString();
	}

	public Date getEnd() {
		return activity.getEnd();
	}

	public String getEndString() {
		return activity.getEndString();
	}

	public String getName() {
		return activity.getName();
	}

	public ActivityType getType() {
		return activity.getType();
	}

	public ActivityStatus getStatus() {
		return activity.getStatus();
	}

	public Float getAmount() {
		return activity.getAmount();
	}

	public Float getAconto() {
		return activity.getAconto();
	}

	public String getComment() {
		return activity.getComment();
	}

	public Date getChanged() {
		return activity.getChanged();
	}

	public Date getCreated() {
		return activity.getCreated();
	}

	public String getChangedBy() {
		return activity.getChangedBy();
	}

	public String getCreatedBy() {
		return activity.getCreatedBy();
	}

	public String toString() {
		return activity.toString();
	}

	public String getShortString() {
		return activity.getShortString();
	}

	public String getLongString() {
		return activity.getLongString();
	}

	public boolean isFinished() {
		return activity.isFinished();
	}
	
	
}
