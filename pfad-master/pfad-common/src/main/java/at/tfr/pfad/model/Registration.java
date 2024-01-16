/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

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
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import at.tfr.pfad.Pfad;
import at.tfr.pfad.RegistrationStatus;
import at.tfr.pfad.Sex;
import at.tfr.pfad.dao.AuditListener;

@NamedQueries({ 
		@NamedQuery(name = "Registration.distName", query = "select distinct r.name from Registration r where r.name is not null order by r.name"),
		@NamedQuery(name = "Registration.distVorname", query = "select distinct r.vorname from Registration r where r.vorname is not null order by r.vorname"),
		@NamedQuery(name = "Registration.distPLZ", query = "select distinct r.plz from Registration r where r.plz is not null order by r.plz"),
		@NamedQuery(name = "Registration.distOrt", query = "select distinct r.ort from Registration r where r.ort is not null order by r.ort"),
		@NamedQuery(name = "Registration.distStrasse", query = "select distinct r.strasse from Registration r where r.strasse is not null order by r.strasse"),
		@NamedQuery(name = "Registration.distGebJahr", query = "select distinct r.gebJahr from Registration r where r.gebJahr is not null order by r.gebJahr desc"),
		@NamedQuery(name = "Registration.distSchoolEntry", query = "select distinct r.schoolEntry from Registration r where r.schoolEntry is not null order by r.schoolEntry"),
		@NamedQuery(name = "Registration.duplicateCheck", query = "select r from Registration r where r.name = ?1 and r.vorname = ?2 "
				+ " and r.gebJahr = ?3 and r.gebMonat = ?4 and r.gebTag = ?5"),
		@NamedQuery(name = "Registration.memberOrParent", query = "select r from Registration r where r.member = ?1 or r.parent = ?1")
	})
@Audited(withModifiedFlag = true)
@Entity
@EntityListeners({ AuditListener.class })
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@JsonIgnoreProperties(ignoreUnknown = true, value = { "handler", "hibernateLazyInitializer" })
public class Registration extends BaseEntity implements Comparable<Registration>, Auditable, Presentable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "registration_seq")
	@SequenceGenerator(name = "registration_seq", sequenceName = "registration_seq", allocationSize = 1, initialValue = 1)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	protected int version;

	@Column(length=16)
	protected String titel;

	@Column(length=128)
	protected String name;

	@Column(length=128)
	protected String vorname;

	@Column(length=128)
	protected String parentName;

	@Column(length=128)
	protected String parentVorname;

	@Column(length=16)
	protected String anrede;

	@Column
	protected int gebTag;

	@Column
	protected int gebMonat;

	@Column
	protected int gebJahr;

	@Column
	protected int schoolEntry;
	
	@Column(length=128)
	protected String strasse;

	@Column(length=16)
	protected String plz;

	@Column(length=128)
	protected String ort;

	@Enumerated(EnumType.STRING)
	protected Sex geschlecht;

	@Column(columnDefinition = "boolean default 'true' not null")
	protected boolean aktiv;

	@Column(length=128)
	protected String email;

	@Column(length=32)
	protected String religion;

	@Column
	protected String telefon;

	@Column(columnDefinition = "boolean default 'false' not null")
	protected boolean storno;
	
	@Column(length = 32)
	@Enumerated(EnumType.STRING)
	protected RegistrationStatus status;
	
	@ManyToOne
	protected Member parent;
	
	@OneToOne
	protected Member member;
	
	@Column
	protected String comment;

	@Column
	protected Date changed;

	@Column
	protected Date created;

	@Column(length=32)
	protected String changedBy;

	@Column(length=32)
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

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getParentVorname() {
		return parentVorname;
	}

	public void setParentVorname(String parentVorname) {
		this.parentVorname = parentVorname;
	}

	public boolean isStorno() {
		return storno;
	}

	public void setStorno(boolean storno) {
		this.storno = storno;
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
	public int getSchoolEntry() {
		return schoolEntry;
	}
	
	public void setSchoolEntry(int schoolEntry) {
		this.schoolEntry = schoolEntry;
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
	public String getEmail() {
		return email;
	}

	public void setEmail(String Email) {
		this.email = Email;
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

	public RegistrationStatus getStatus() {
		return status;
	}
	
	public void setStatus(RegistrationStatus status) {
		this.status = status;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public Member getParent() {
		return parent;
	}

	public void setParent(Member parent) {
		this.parent = parent;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
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

	@Override
	public int compareTo(Registration o) {
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
		return result;
	}

	/**
	 * property for REST API
	 * 
	 * @return
	 */
	public String getFullString() {
		return toFullString();
	}

	public String toFullString() {
		String result = getClass().getSimpleName() + " ";
		if (titel != null && !titel.trim().isEmpty())
			result += ", titel: " + titel;
		if (name != null && !name.trim().isEmpty())
			result += ", name: " + name;
		if (vorname != null && !vorname.trim().isEmpty())
			result += ", vorname: " + vorname;
		if (anrede != null && !anrede.trim().isEmpty())
			result += ", anrede: " + anrede;
		result += ", gebTag: " + gebTag;
		result += ", gebMonat: " + gebMonat;
		result += ", gebJahr: " + gebJahr;
		if (strasse != null && !strasse.trim().isEmpty())
			result += ", strasse: " + strasse;
		if (plz != null && !plz.trim().isEmpty())
			result += ", plz: " + plz;
		if (ort != null && !ort.trim().isEmpty())
			result += ", ort: " + ort;
		result += ", aktiv: " + aktiv;
		if (email != null && !email.trim().isEmpty())
			result += ", email: " + email;
		if (religion != null && !religion.trim().isEmpty())
			result += ", religion: " + religion;
		if (telefon != null && !telefon.trim().isEmpty())
			result += ", telefon: " + telefon;
		return result;
	}
	
	public String toPrettyLines() {
		String result = "Anmeldung: \n";
		if (member != null) {
			result += "\n";
			result += "Umwandlung durchgeführt: " + member.toShortString();
			result += "\n";
			result += "Keine Bearbeitung mehr möglich!!\n";
			result += "\n";
		}
		if (anrede != null && !anrede.trim().isEmpty())
			result += titel + " ";
		if (name != null && !name.trim().isEmpty())
			result += name;
		if (vorname != null && !vorname.trim().isEmpty())
			result += " " + vorname;
		result += "\n";
		result += "Geboren: " + gebTag + "." + gebMonat + "." + gebJahr;
		result += "\n";
		if (parent != null) {
			result += "\n";
			result += "Eltern: " + parent.toShortString();
			result += "\n";
			result += "\n";
		}
		
		if (strasse != null && !strasse.trim().isEmpty())
			result += "Anschrift: " + strasse;
		if (plz != null && !plz.trim().isEmpty())
			result += ", " + plz;
		if (ort != null && !ort.trim().isEmpty())
			result += ", " + ort;
		result += "\n";
		result += "aktiv: " + aktiv + ", storno: " + storno;
		result += "\n";
		if (email != null && !email.trim().isEmpty())
			result += "email: " + email;
		if (telefon != null && !telefon.trim().isEmpty())
			result += ", telefon: " + telefon;
		result += "\n";
		result += "Kommentar: " + (comment!=null ? comment : "");
		return result;
	}
	
}