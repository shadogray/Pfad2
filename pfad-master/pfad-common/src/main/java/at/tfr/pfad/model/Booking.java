package at.tfr.pfad.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import at.tfr.pfad.BookingStatus;
import at.tfr.pfad.dao.AuditListener;

@Audited(withModifiedFlag = true)
@NamedQueries({
	@NamedQuery(name = "BookingsForPayment", query = "select b from Booking b where ?1 member of b.payments order by b.id"),
	@NamedQuery(name = "BookingAcvitySummary", query = "select b.activity, count(b) from Booking b where b.activity in :activities group by b.activity")
	})
@NamedEntityGraphs({
	@NamedEntityGraph(attributeNodes={@NamedAttributeNode("payments"),@NamedAttributeNode("member")})
})
@Entity
@EntityListeners({AuditListener.class})
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@JsonIgnoreProperties(ignoreUnknown=true, value = {"handler", "hibernateLazyInitializer"})
public class Booking extends BaseEntity implements Auditable, Presentable, Comparable<Booking> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_seq")
	@SequenceGenerator(name = "booking_seq", sequenceName = "booking_seq", allocationSize = 1, initialValue = 1)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@ManyToMany(mappedBy = "bookings")
	private Set<Payment> payments = new HashSet<Payment>();

	@ManyToOne(optional = false)
	private Member member;

	@ManyToOne(optional = false)
	private Activity activity;

	@ManyToOne(fetch = FetchType.LAZY)
	private Squad squad;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private BookingStatus status;
	
	@Column(nullable = false, columnDefinition="boolean default 'false'")
	private Boolean registered = false;

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

	public Booking() {
	}
	
	public Booking(Boolean registered) {
		this.registered = registered;
	}
	
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
	public Set<Payment> getPayments() {
		return this.payments;
	}

	@XmlTransient
	public List<Long> getPaymentsIds() {
		return payments.stream().map(Payment::getId)
				.collect(Collectors.toList());
	}

	public void setPayments(final Set<Payment> payments) {
		this.payments = payments;
	}

	@XmlTransient
	public List<Payment> getSortedPayments() {
		return payments.stream().sorted(new PkComparator<Payment>())
				.collect(Collectors.toList());
	}

	public Member getMember() {
		return this.member;
	}

	public void setMember(final Member member) {
		this.member = member;
	}

	public Activity getActivity() {
		return this.activity;
	}

	public void setActivity(final Activity activity) {
		this.activity = activity;
	}

	public Squad getSquad() {
		return squad;
	}

	public void setSquad(Squad squad) {
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
	
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}
	
	public boolean isRegistered() {
		return Boolean.TRUE.equals(registered);
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

	@Transient
	public boolean isValid() {
		return !BookingStatus.storno.equals(status);
	}
	
	@Override
	public String toString() {
		String result = (activity != null
				? activity.getType() + ":" + activity.getName() + "/"
						+ activity.getStartString()
				: getClass().getSimpleName());
		if (member != null) {
			result += " " + member.getName() + " " + member.getVorname() + ", "
					+ member.getGebJahr() + ", " + member.getPLZ() + ", "
					+ member.getStrasse();
		}
		if (comment != null && !comment.trim().isEmpty())
			result += " " + comment;
		return result;
	}
	
	@Override
	public String getName() {
		return getShortString();
	}
	
	@Override
	public int compareTo(Booking o) {
		if (this.id != null && o.id != null) 
			return this.id.compareTo(o.id);
		return this.getShortString().compareTo(o.getShortString());
	}
	
	@Override
	public String getShortString() {
		String result = (activity != null ? activity.getName() : "undef");
		if (member != null) {
			result += " " + member.getName() + " " + member.getVorname() + ", "
					+ member.getGebJahr();
		}
		return result;
	}

	@Override
	public String getLongString() {
		return toString();
	}
}