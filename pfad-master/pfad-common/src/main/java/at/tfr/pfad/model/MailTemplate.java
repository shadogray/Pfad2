package at.tfr.pfad.model;

import at.tfr.pfad.dao.AuditListener;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.Date;
import java.util.List;

@Audited(withModifiedFlag = true)
@Entity
@EntityListeners({ AuditListener.class })
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class MailTemplate extends BaseEntity implements Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mailTemplate_seq")
	@SequenceGenerator(name = "mailTemplate_seq", sequenceName = "mailTemplate_seq", allocationSize = 1, initialValue = 1)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@Column(length=64)
	private String name;
	@Column(length=256)
	private String subject;
	@Column(length=40960)
	private String text;
	@Column(length=4096)
	private String query;
	@Column(length=64)
	private String owner;
	@Column
	private Boolean saveText;
	@Column
	private Boolean cc;
	@Column
	private Boolean bcc;
	@Column
	private Boolean sms;
	@Column
	private Boolean alternativeText;
	@Column
	private Boolean sql;
	
	protected Date changed;
	protected Date created;
	protected String changedBy;
	protected String createdBy;

	@NotAudited
	@OneToMany(mappedBy="template")
	private List<MailMessage> messages;

	@Transient
	private String uiName;
	
	@Override
	public Long getId() {
		return id;
	}

	public String getIdStr() {
		return id != null ? id.toString() : null;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name != null ? name : "";
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getText() {
		return text != null ? text : "";
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getOwner() {
		return owner;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public List<MailMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<MailMessage> messages) {
		this.messages = messages;
	}
	
	public boolean isSaveText() {
		return Boolean.TRUE.equals(saveText);
	}
	
	public void setSaveText(boolean saveText) {
		this.saveText = saveText;
	}

	public boolean isCc() {
		return Boolean.TRUE.equals(cc);
	}

	public void setCc(boolean cc) {
		this.cc = cc;
	}

	public boolean isBcc() {
		return Boolean.TRUE.equals(bcc);
	}

	public void setBcc(boolean bcc) {
		this.bcc = bcc;
	}

	public boolean isSms() {
		return Boolean.TRUE.equals(sms);
	}
	
	public void setSms(boolean sms) {
		this.sms = sms;
	}
	
	@Override
	public Date getChanged() {
		return changed;
	}

	@Override
	public void setChanged(Date changed) {
		this.changed = changed;
	}

	@Override
	public Date getCreated() {
		return created;
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public String getChangedBy() {
		return changedBy;
	}

	@Override
	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

	@Override
	public String getCreatedBy() {
		return createdBy;
	}

	@Override
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUiName() {
		return uiName;
	}
	
	public void setUiName(String uiName) {
		this.uiName = uiName;
	}
	
	public Boolean getAlternativeText() {
		return alternativeText;
	}

	public void setAlternativeText(Boolean alternativeText) {
		this.alternativeText = alternativeText;
	}
	
	public Boolean getSql() {
		return sql;
	}
	
	public void setSql(Boolean sql) {
		this.sql = sql;
	}

	@Override
	public String toString() {
		return "MailTemplate [id=" + id + ", name=" + name + ", text=" + StringUtils.abbreviate(text, 50) + ", query=" + StringUtils.abbreviate(query, 50) + "]";
	}

}
