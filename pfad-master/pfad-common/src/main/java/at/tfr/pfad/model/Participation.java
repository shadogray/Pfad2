package at.tfr.pfad.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import at.tfr.pfad.ParticipationCostType;
import at.tfr.pfad.ParticipationStatus;
import at.tfr.pfad.dao.AuditListener;

/**
 * @author u0x27vo
 *
 */
@Audited(withModifiedFlag = true)
@Entity
@EntityListeners({ AuditListener.class })
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@JsonIgnoreProperties(ignoreUnknown = true, value = { "handler", "hibernateLazyInitializer" })
public class Participation extends BaseEntity implements Comparable<Participation>, Auditable, Presentable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "participation_seq")
	@SequenceGenerator(name = "participation_seq", sequenceName = "participation_seq", allocationSize = 1, initialValue = 1)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@Version
	@Column(name = "version")
	private int version;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ParticipationCostType costType;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ParticipationStatus status;

	@Column(name = "startDate")
	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Column(name = "endDate")
	@Temporal(TemporalType.DATE)
	private Date endDate;

	@Column
	private Float amount;

	@Column
	private String comment;

	@Column
	private Date changed;

	@Column
	private Date created;

	@Column
	private String changedBy;

	@Column
	private String createdBy;

	@ManyToOne(optional=false)
	private Member member;

	@ManyToOne(optional=false)
	private Training training;

	public Long getId() {
		return id;
	}

	public String getIdStr() {
		return id != null ? id.toString() : "";
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Training getTraining() {
		return training;
	}

	public void setTraining(Training training) {
		this.training = training;
	}

	public ParticipationCostType getCostType() {
		return costType;
	}

	public void setCostType(ParticipationCostType costType) {
		this.costType = costType;
	}

	public ParticipationStatus getStatus() {
		return status;
	}

	public void setStatus(ParticipationStatus status) {
		this.status = status;
	}

	public Date getStart() {
		return startDate;
	}

	public void setStart(Date start) {
		this.startDate = start;
	}

	public Date getEnd() {
		return endDate;
	}

	public void setEnd(Date end) {
		this.endDate = end;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
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
	public String getName() {
		return toString();
	}
	
	@Override
	public String toString() {
		String result = "";
		if (training != null)
			result += " " + training.getShortString();
		if (member != null)
			result += member.toShortString();
		return result;
	}

	@Override
	public String getShortString() {
		return toString();
	}

	@Override
	public String getLongString() {
		return toString();
	}

	@Override
	public int compareTo(Participation o) {
		if (id != null && o.id != null)
			return id.compareTo(o.id);
		return this.compareTo(o);
	}

}
