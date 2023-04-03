package at.tfr.pfad.svc;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import at.tfr.pfad.BookingStatus;
import at.tfr.pfad.model.Activity;

@XmlRootElement
public class BookingDao extends BaseDao {

	private Long id;
	private int version;
	private Set<BaseDao> payments = new HashSet<BaseDao>();
	private BaseDao member;
	private Activity activity;
	private SquadDao squad;
	private BookingStatus status;
	private String comment;
	protected Date changed;
	protected Date created;
	protected String changedBy;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BookingDao)) {
			return false;
		}
		BookingDao other = (BookingDao) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public Set<BaseDao> getPayments() {
		return this.payments;
	}

	public void setPayments(final Set<BaseDao> payments) {
		this.payments = payments;
	}

	public BaseDao getMember() {
		return this.member;
	}

	public void setMember(final BaseDao member) {
		this.member = member;
	}

	public Activity getActivity() {
		return this.activity;
	}

	public void setActivity(final Activity activity) {
		this.activity = activity;
	}

	public SquadDao getSquad() {
		return squad;
	}

	public void setSquad(SquadDao squad) {
		this.squad = squad;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
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
		String result = (activity != null
				? activity.getType() + ":" + activity.getName() + "/"
						+ activity.getStartString()
				: getClass().getSimpleName());
		if (member != null) {
			result += " " + member.getLongName();
		}
		if (comment != null && !comment.trim().isEmpty())
			result += " " + comment;
		return result;
	}
}