package at.tfr.pfad.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import at.tfr.pfad.TrainingForm;
import at.tfr.pfad.TrainingPhase;
import at.tfr.pfad.dao.AuditListener;

@Audited(withModifiedFlag = true)
@Entity
@EntityListeners({ AuditListener.class })
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@JsonIgnoreProperties(ignoreUnknown = true, value = { "handler", "hibernateLazyInitializer" })
public class Training extends BaseEntity implements Comparable<Training>, Auditable, Presentable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "training_seq")
	@SequenceGenerator(name = "training_seq", sequenceName = "training_seq", allocationSize = 1, initialValue = 1)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@Version
	@Column(name = "version")
	private int version;
	
	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private TrainingForm form;
	
	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private TrainingPhase phase;

	@Column(nullable=false)
	private String name;

	@Column
	private String description;

	@NotAudited // inverse side!
	@Column
	@OneToMany(mappedBy="training")
	private Set<Participation> participations;
	
	@Column
	private Date changed;

	@Column
	private Date created;
	
	@Column
	private String changedBy;
	
	@Column
	private String createdBy;

	public Long getId() {
		return id;
	}

	public String getIdStr() {
		return id != null ? id.toString() : "";
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TrainingForm getForm() {
		return form;
	}

	public void setForm(TrainingForm form) {
		this.form = form;
	}

	public TrainingPhase getPhase() {
		return phase;
	}

	public void setPhase(TrainingPhase phase) {
		this.phase = phase;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Participation> getParticipations() {
		return participations;
	}
	
	public void setParticipations(Set<Participation> participations) {
		this.participations = participations;
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
		String result = "";
		if (name != null && !name.trim().isEmpty())
			result += name;
		if (phase != null)
			result += " " + phase.toString();
		if (form != null)
			result += ", " + form.toString();
		return result;
	}

	@Override
	public String getShortString() {
		return name;
	}

	@Override
	public String getLongString() {
		String result = "";
		if (name != null && !name.trim().isEmpty())
			result += name;
		if (phase != null)
			result += " " + phase.toString();
		if (form != null)
			result += ", " + form.toString();
		return result;
	}

	@Override
	public int compareTo(Training o) {
		if (id != null && o.id != null)
			return id.compareTo(o.id);
		return this.compareTo(o);
	}

	
}
