/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import at.tfr.pfad.Pfad;
import at.tfr.pfad.ScoutRole;
import at.tfr.pfad.Sex;
import at.tfr.pfad.dao.AuditListener;

@Audited(withModifiedFlag = true)
@Entity
@Table(name = "MEMBER")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "status", discriminatorType = DiscriminatorType.STRING, length = 1, columnDefinition = "char(1) default 'A'")
@EntityListeners({ AuditListener.class })
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@JsonIgnoreProperties(ignoreUnknown = true, value = { "handler", "hibernateLazyInitializer" })
public class Membera extends BaseEntity implements Comparable<Membera>, Auditable, Presentable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq")
	@SequenceGenerator(name = "member_seq", sequenceName = "member_seq", allocationSize = 1, initialValue = 1)
	@Column(name = "id", updatable = false, nullable = false)
	protected Long id;
	
	@Version
	@Column(name = "version")
	protected int version;

	@Column
	protected String bvKey;

	@Column
	protected String gruppenSchluessel;

	@Column
	protected long personenKey;

	@Column
	protected String titel;

	@Column
	protected String name;

	@Column
	protected String vorname;

	@Column
	protected String anrede;

	@Column
	protected int gebTag;

	@Column
	protected int gebMonat;

	@Column
	protected int gebJahr;

	@Column
	protected String strasse;

	@Column
	protected String plz;

	@Column
	protected String ort;

	@Enumerated(EnumType.STRING)
	protected Sex geschlecht;

	@Column
	protected boolean aktiv;

	@Column
	protected boolean aktivExtern;

	@Column
	protected String email;

	@Column
	protected String religion;

	@Column
	protected String telefon;

	@Column(columnDefinition = "boolean default 'false' not null")
	protected boolean trail;

	@Column(columnDefinition = "boolean default 'false' not null")
	protected boolean gilde;

	@Column(columnDefinition = "boolean default 'false' not null")
	protected boolean altER;

	@Column(columnDefinition = "boolean default 'false' not null")
	protected boolean support;

	@Column(columnDefinition = "boolean default 'false' not null")
	protected boolean infoMail;

	@Column(columnDefinition = "boolean default 'false' not null")
	protected boolean free;

	@Column(columnDefinition = "boolean default 'false' not null")
	protected boolean dead;

	@Enumerated(EnumType.STRING)
	protected ScoutRole rolle;

	@Column
	protected Date changed;

	@Column
	protected Date created;

	@Column
	protected String changedBy;

	@Column
	protected String createdBy;

	@Column(name = "status", insertable = false, updatable = false)
	protected String status;
	
	@NotAudited
	@Column(insertable = false, updatable = false, name = "Trupp_id")
	protected Long truppId;

	@NotAudited
	@Column(insertable = false, updatable = false, name = "Vollzahler_id")
	protected Long VollzahlerId;

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

	@Pfad
	public String getBVKey() {
		if (id == null) {
			return bvKey;
		}
		return StringUtils.isEmpty(bvKey) ? Configuration.BADEN_KEYPFX + id : bvKey;
	}

	public void setBVKey(String BVKey) {
		this.bvKey = BVKey;
	}

	public String getGruppenSchluessel() {
		return StringUtils.isEmpty(gruppenSchluessel) ? Configuration.BADEN_KEY : gruppenSchluessel;
	}

	public void setGruppenSchluessel(String GruppenSchluessel) {
		if (Configuration.BADEN_KEY.equals(bvKey))
			return;
		this.gruppenSchluessel = GruppenSchluessel;
	}

	public long getPersonenKey() {
		return (personenKey == 0 && id != null) ? id : personenKey;
	}

	public void setPersonenKey(long PersonenKey) {
		this.personenKey = PersonenKey;
	}

	@Pfad
	public String getTitel() {
		return titel;
	}

	public void setTitel(String Titel) {
		this.titel = Titel;
	}

	@Pfad
	public String getName() {
		return name;
	}

	public void setName(String Name) {
		this.name = Name;
	}

	@Pfad
	public String getVorname() {
		return vorname;
	}

	public void setVorname(String Vorname) {
		this.vorname = Vorname;
	}

	@Pfad
	public String getAnrede() {
		return anrede;
	}

	public void setAnrede(String Anrede) {
		this.anrede = Anrede;
	}

	@Pfad
	public int getGebTag() {
		return gebTag;
	}

	public void setGebTag(int GebTag) {
		this.gebTag = GebTag;
	}

	@Pfad
	public int getGebMonat() {
		return gebMonat;
	}

	public void setGebMonat(int GebMonat) {
		this.gebMonat = GebMonat;
	}

	@Pfad
	public int getGebJahr() {
		return gebJahr;
	}

	public void setGebJahr(int GebJahr) {
		this.gebJahr = GebJahr;
	}

	@Pfad
	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String Strasse) {
		this.strasse = Strasse;
	}

	@Pfad
	public String getPLZ() {
		return plz;
	}

	public void setPLZ(String PLZ) {
		this.plz = PLZ;
	}

	@Pfad
	public String getOrt() {
		return ort;
	}

	public void setOrt(String Ort) {
		this.ort = Ort;
	}

	@Pfad
	public Sex getGeschlecht() {
		return geschlecht;
	}

	public void setGeschlecht(Sex Geschlecht) {
		this.geschlecht = Geschlecht;
	}

	@Pfad
	public boolean isAktiv() {
		return aktiv;
	}

	public void setAktiv(boolean Aktiv) {
		this.aktiv = Aktiv;
	}

	@Pfad
	public boolean isAktivExtern() {
		return aktivExtern;
	}

	public void setAktivExtern(boolean aktivExtern) {
		this.aktivExtern = aktivExtern;
	}

	@Pfad
	public String getEmail() {
		return email;
	}

	public void setEmail(String Email) {
		this.email = Email != null ? Email.replaceAll(";", ",") : Email;
	}

	@Pfad
	public String getReligion() {
		return religion;
	}

	public void setReligion(String Religion) {
		this.religion = Religion;
	}

	@Pfad
	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String Telefon) {
		this.telefon = Telefon;
	}

	@Pfad
	public boolean isTrail() {
		return trail;
	}

	public void setTrail(boolean Trail) {
		this.trail = Trail;
	}

	@Pfad
	public boolean isGilde() {
		return gilde;
	}

	public void setGilde(boolean Gilde) {
		this.gilde = Gilde;
	}

	@Pfad
	public boolean isAltER() {
		return altER;
	}

	public void setAltER(boolean AltER) {
		this.altER = AltER;
	}

	@Pfad
	public ScoutRole getRolle() {
		return rolle;
	}

	public void setRolle(ScoutRole Rolle) {
		this.rolle = Rolle;
	}

	@Pfad
	public boolean isInfoMail() {
		return infoMail;
	}

	public void setInfoMail(boolean infoMail) {
		this.infoMail = infoMail;
	}

	@Pfad
	public boolean isSupport() {
		return support;
	}

	public void setSupport(boolean support) {
		this.support = support;
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

	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int compareTo(Membera o) {
		return getCompareString().compareTo(o.getCompareString());
	}

	public String getCompareString() {
		return (("" + name) + vorname) + id;
	}

	/**
	 * property for REST API
	 * 
	 * @return
	 */
	public String getShortString() {
		return toShortString();
	}

	public String toShortString() {
		String result = "";
		if (name != null && !name.trim().isEmpty())
			result += name;
		if (vorname != null && !vorname.trim().isEmpty())
			result += " " + vorname;
		result += ", " + gebJahr;
		if (ort != null && !ort.trim().isEmpty())
			result += ", " + ort;
		return result;
	}

	/**
	 * property for REST API
	 * 
	 * @return
	 */
	public String getLongString() {
		return toString();
	}

	@Override
	public String toString() {
		String result = "";
		// if (bvKey != null && !bvKey.trim().isEmpty())
		// result += bvKey;
		if (titel != null && !titel.trim().isEmpty())
			result += (result.length() > 0 ? ", " : "") + titel;
		if (name != null && !name.trim().isEmpty())
			result += (result.length() > 0 ? ", " : "") + name;
		if (vorname != null && !vorname.trim().isEmpty())
			result += (result.length() > 0 ? ", " : "") + vorname;
		result += (result.length() > 0 ? ", " : "") + gebTag + "." + gebMonat + "." + gebJahr;
		if (plz != null && !plz.trim().isEmpty())
			result += (result.length() > 0 ? ", " : "") + plz;
		if (ort != null && !ort.trim().isEmpty())
			result += " " + ort;
		result += (result.length() > 0 ? ", " : "") + (aktiv ? "aktiv" : "inaktiv");
		result += free ? ":FREI" : "";
		return result;
	}

	@Pfad
	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	@Pfad
	public boolean isDead() {
		return dead;
	}
	
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public DateTime geburtstag() {
		return new DateTime(gebJahr, gebMonat > 0 ? gebMonat : 1, gebTag > 0 ? gebTag : 1, 0, 0);
	}
	
}