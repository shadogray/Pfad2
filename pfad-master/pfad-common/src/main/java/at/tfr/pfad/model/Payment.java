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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import at.tfr.pfad.PaymentType;
import at.tfr.pfad.dao.AuditListener;

@Audited(withModifiedFlag = true)
@NamedQueries({@NamedQuery(name = "PaymentsForBooking", query = "select p from Payment p where ?1 member of p.bookings order by p.id")})
@NamedEntityGraphs({
	@NamedEntityGraph(attributeNodes={@NamedAttributeNode("bookings"), @NamedAttributeNode("payer")})
})
@Entity
@EntityListeners({AuditListener.class})
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@JsonIgnoreProperties(ignoreUnknown=true, value = {"handler", "hibernateLazyInitializer"})
public class Payment extends BaseEntity implements Auditable, Presentable, Comparable<Payment> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_seq")
	@SequenceGenerator(name = "payment_seq", sequenceName = "payment_seq", allocationSize = 1, initialValue = 1)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@Column(nullable = false, columnDefinition = "varchar2(16) default 'Membership' not null")
	@Enumerated(EnumType.STRING)
	private PaymentType type;

	@ManyToOne(fetch = FetchType.EAGER)
	private Member payer;

	@Column
	private Float amount;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;

	@Column(columnDefinition = "boolean default false not null")
	private Boolean finished;

	@Column(columnDefinition = "boolean default false not null")
	private Boolean aconto;
	
	@Column
	private String payerIBAN;

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

	@Audited
	@ManyToMany
	private Set<Booking> bookings = new HashSet<Booking>();

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

	public Member getPayer() {
		return payer;
	}

	public void setPayer(Member Payer) {
		this.payer = Payer;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public Boolean getFinished() {
		return finished;
	}

	public void setFinished(Boolean finished) {
		this.finished = finished;
	}

	public Boolean getAconto() {
		return aconto;
	}

	public void setAconto(Boolean aconto) {
		this.aconto = aconto;
	}

	public String getPayerIBAN() {
		return payerIBAN;
	}
	
	public void setPayerIBAN(String payerIBAN) {
		this.payerIBAN = payerIBAN;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Set<Booking> getBookings() {
		return this.bookings;
	}

	public List<Long> getBookingsIds() {
		return bookings.stream().map(Booking::getId)
				.collect(Collectors.toList());
	}

	public void setBookings(final Set<Booking> bookings) {
		this.bookings = bookings;
	}

	public List<Booking> getSortedBookings() {
		return bookings.stream().sorted(new PkComparator<Booking>())
				.collect(Collectors.toList());
	}

	public PaymentType getType() {
		return type;
	}

	public void setType(PaymentType type) {
		this.type = type;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
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

	public Payment updateType(Booking booking) {
		if (booking.getActivity() != null) {
			updateType(booking.getActivity());
		}
		return this;
	}

	public Payment updateType(Activity activity) {
		if (type == null && activity != null && activity.getType() != null) {
			switch (activity.getType()) {
				case Membership :
					type = PaymentType.Membership;
					break;
				case Camp :
					type = PaymentType.Camp;
					break;
				default :
			}
		}
		return this;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (payer != null) {
			result += payer.toShortString();
		}
		if (paymentDate != null) {
			result += ", " + new DateTime(paymentDate).toString("dd.MM.yyyy");
		}
		if (Boolean.TRUE.equals(finished)) {
			result += ", BEZ";
		} else if (Boolean.TRUE.equals(aconto)) {
			result += ", ANZ";
		}
		if (StringUtils.isNotBlank(payerIBAN)) {
			result += ", " + payerIBAN;
		}
		result += ", " + (Boolean.TRUE.equals(finished) ? "bezahlt" : "offen");
		if (comment != null && !comment.trim().isEmpty())
			result += ", " + comment;
		return result;
	}
	
	@Override
	public String getName() {
		return getShortString();
	}
	
	@Override
	public int compareTo(Payment o) {
		if (this.id != null && o.id != null) 
			return this.id.compareTo(o.id);
		return this.getShortString().compareTo(o.getShortString());
	}
	
	@Override
	public String getShortString() {
		String result = getClass().getSimpleName() + " ";
		if (payer != null) {
			result += payer.toShortString();
		}
		if (Boolean.TRUE.equals(finished)) {
			result += ", BEZ";
		} else if (Boolean.TRUE.equals(aconto)) {
			result += ", ANZ";
		}
		return result;
	}
	
	@Override
	public String getLongString() {
		return toString();
	}
}