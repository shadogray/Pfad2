package at.tfr.pfad.svc;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;

import at.tfr.pfad.PaymentType;

@XmlRootElement
public class PaymentDao extends BaseDao {

	private Long id;
	private int version;
	private PaymentType type;
	private BaseDao payer;
	private Float amount;
	private Date paymentDate;
	private Boolean finished;
	private Boolean aconto;
	private String comment;
	protected Date changed;
	protected Date created;
	protected String changedBy;
	protected String createdBy;
	private Set<BaseDao> bookings = new HashSet<BaseDao>();

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
		if (!(obj instanceof PaymentDao)) {
			return false;
		}
		PaymentDao other = (PaymentDao) obj;
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

	public BaseDao getPayer() {
		return payer;
	}

	public void setPayer(BaseDao Payer) {
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Set<BaseDao> getBookings() {
		return this.bookings;
	}

	public List<Long> getBookingsIds() {
		return bookings.stream().map(BaseDao::getId)
				.collect(Collectors.toList());
	}

	public void setBookings(final Set<BaseDao> bookings) {
		this.bookings = bookings;
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

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (payer != null) {
			result += payer.getShortName();
		}
		if (paymentDate != null) {
			result += ", " + new DateTime(paymentDate).toString("dd.MM.yyyy");
		}
		if (aconto != null && aconto) {
			result += ", ANZ";
		}
		result += ", " + (finished != null && finished ? "finished" : "offen");
		if (comment != null && !comment.trim().isEmpty())
			result += ", " + comment;
		return result;
	}
}