package at.tfr.pfad.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import at.tfr.pfad.ActivityStatus;
import at.tfr.pfad.ActivityType;
import at.tfr.pfad.dao.AuditListener;

@Audited(withModifiedFlag = true)
@Entity
@EntityListeners({AuditListener.class})
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@JsonIgnoreProperties(ignoreUnknown=true, value = {"handler", "hibernateLazyInitializer"})
@NamedQueries({
	@NamedQuery(name = "Activity.distName", query = "select a.name from Activity a"),
	@NamedQuery(name = "Activity.distNameActive", query = "select a.name from Activity a where a.end > CURRENT_DATE")
})
public class Activity extends BaseEntity implements Auditable, Presentable {

	private static DateTimeFormatter format = DateTimeFormat
			.forPattern("dd.MM.yyyy");

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activity_seq")
	@SequenceGenerator(name = "activity_seq", sequenceName = "activity_seq", allocationSize = 1, initialValue = 1)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@NotAudited // inverse side!
	@OneToMany(mappedBy = "activity")
	private Set<Booking> bookings = new HashSet<Booking>();

	@Column
	@Temporal(TemporalType.DATE)
	private Date start;

	@Column
	@Temporal(TemporalType.DATE)
	private Date end;

	@Column
	private String name;

	@Column(nullable = false, columnDefinition = "varchar2(16) default 'Membership' not null")
	@Enumerated(EnumType.STRING)
	private ActivityType type;

	@Column(nullable = false, columnDefinition = "varchar2(16) default 'planned' not null")
	@Enumerated(EnumType.STRING)
	private ActivityStatus status;

	@Column
	private Float amount;

	@Column
	private Float aconto;

	@Column
	private String comment;

	@Column
	protected Date changed;

	@Column
	protected Date created;

	@Column
	protected String changedBy;

	@Column
	protected String createdBy;

	@XmlID
	public Long getId() {
		return this.id;
	}

	public String getIdStr() {
		return id != null ? id.toString() : "";
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	@XmlTransient
	public Set<Booking> getBookings() {
		return this.bookings;
	}

	public void setBookings(final Set<Booking> bookings) {
		this.bookings = bookings;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public String getStartString() {
		return start != null ? new DateTime(start).toString(format) : "";
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getEndString() {
		return end != null ? new DateTime(end).toString(format) : "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ActivityType getType() {
		return type;
	}

	public void setType(ActivityType type) {
		this.type = type;
	}

	public ActivityStatus getStatus() {
		return status;
	}

	public void setStatus(ActivityStatus status) {
		this.status = status;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public Float getAconto() {
		return aconto;
	}

	public void setAconto(Float aconto) {
		this.aconto = aconto;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getChanged() {
		return changed;
	}

	public void setChanged(Date changed) {
		this.changed = changed;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public String toString() {
		String result = ((StringUtils.isNotBlank(name) ? name : getClass()
				.getSimpleName()) + ":" + type + ":" + status);
		if (start != null) {
			result += ", " + new DateTime(start).toString(format);
		}
		return result;
	}

	@Override
	public String getShortString() {
		return ((StringUtils.isNotBlank(name) ? name : getClass()
				.getSimpleName()) + ":" + type + ":" + status);
	}
	
	@Override
	public String getLongString() {
		return toString();
	}
	
	@Transient
	public boolean isFinished() {
		return ActivityStatus.cancelled.equals(status) || ActivityStatus.finished.equals(status)
				|| (end != null && new Date().after(end));
	}
}