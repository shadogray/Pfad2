/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import at.tfr.pfad.SquadType;
import at.tfr.pfad.dao.AuditListener;

@NamedQueries({
	@NamedQuery(name = Squad.SQUAD_DIST_NAME, query = "select s.name from Squad s"),
	@NamedQuery(name = Squad.SQUAD_LEADERS_FEMALE, query = "select s.leaderFemale from Squad s"),
	@NamedQuery(name = Squad.SQUAD_LEADERS_MALE, query = "select s.leaderMale from Squad s"),
	@NamedQuery(name = Squad.SQUAD_ASSISTANTS, query = "select a from Squad s inner join s.assistants a"),
	@NamedQuery(name = Squad.SQUAD_LEADERS_FEMALE_ID, query = "select s.leaderFemale.id from Squad s"),
	@NamedQuery(name = Squad.SQUAD_LEADERS_MALE_ID, query = "select s.leaderMale.id from Squad s"),
	@NamedQuery(name = Squad.SQUAD_ASSISTANTS_ID, query = "select a.id from Squad s inner join s.assistants a"),
	})
@Audited(withModifiedFlag = true)
@Entity
@EntityListeners({AuditListener.class})
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@JsonIgnoreProperties(ignoreUnknown=true, value = {"handler", "hibernateLazyInitializer"})
public class Squad extends BaseEntity implements Comparable<Squad>, Auditable, Presentable {

	public static final String SQUAD_ASSISTANTS = "Squad.assistants";
	public static final String SQUAD_LEADERS_MALE = "Squad.leadersMale";
	public static final String SQUAD_LEADERS_FEMALE = "Squad.leadersFemale";
	public static final String SQUAD_DIST_NAME = "Squad.distName";
	public static final String SQUAD_ASSISTANTS_ID = "Squad.assistants.id";
	public static final String SQUAD_LEADERS_MALE_ID = "Squad.leadersMale.id";
	public static final String SQUAD_LEADERS_FEMALE_ID = "Squad.leadersFemale.id";
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "squad_seq")
	@SequenceGenerator(name = "squad_seq", sequenceName = "squad_seq", allocationSize = 1, initialValue = 1)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@Column(nullable = false, columnDefinition = "varchar2(8) default 'WIWO' not null")
	@Enumerated(EnumType.STRING)
	private SquadType type;

	@Column
	private String name;

	@Column
	private String login;

	@Column
	private Date changed;

	@Column
	private Date created;

	@Column
	private String changedBy;

	@Column
	private String createdBy;

	@ManyToOne
	private Member leaderMale;

	@ManyToOne
	private Member leaderFemale;

	@ManyToMany
	@OrderBy("Name, Vorname")
	@JoinTable(name = "squad_member", joinColumns = @JoinColumn(name = "squad_id"), inverseJoinColumns = @JoinColumn(name = "assistants_id"))
	private Set<Member> assistants = new HashSet<>();

	@NotAudited // inverse side!
	@OneToMany(mappedBy = "trupp")
	@OrderBy("Name, Vorname")
	private Set<Member> scouts = new HashSet<>();

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

	public SquadType getType() {
		return type;
	}

	public void setType(SquadType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
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
			result += "" + name;
		result += ", " + type;
		return result;
	}
	
	@Override
	public String getShortString() {
		return name;
	}

	@Override
	public String getLongString() {
		return toString();
	}
	
	public Member getLeaderMale() {
		return this.leaderMale;
	}

	public void setLeaderMale(final Member leaderMale) {
		this.leaderMale = leaderMale;
	}

	public Member getLeaderFemale() {
		return leaderFemale;
	}

	public void setLeaderFemale(Member leaderFemale) {
		this.leaderFemale = leaderFemale;
	}

	@XmlTransient
	public Set<Member> getAssistants() {
		return this.assistants;
	}

	public void setAssistants(final Set<Member> assistants) {
		this.assistants = assistants;
	}

	@XmlTransient
	public Set<Member> getScouts() {
		return scouts;
	}

	@Override
	public int compareTo(Squad o) {
		if (type != null && o.type != null)
			return type.compareTo(o.type);
		if (id != null && o.id != null)
			return id.compareTo(o.id);
		return this.compareTo(o);
	}

}