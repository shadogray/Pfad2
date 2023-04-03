/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.svc;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

import at.tfr.pfad.Pfad;
import at.tfr.pfad.ScoutRole;
import at.tfr.pfad.Sex;
import at.tfr.pfad.model.Configuration;

@XmlRootElement
public class MemberDao extends BaseDao {

	private Long id;
	protected int version;
	protected String bvKey;
	protected String gruppenSchluessel;
	protected long personenKey;
	protected String titel;
	protected String name;
	protected String vorname;
	protected String anrede;
	protected int gebTag;
	protected int gebMonat;
	protected int gebJahr;
	protected Date geburtstag;
	protected String strasse;
	protected String plz;
	protected String ort;
	protected Sex geschlecht;
	protected boolean aktiv;
	protected boolean aktivExtern;
	protected String email;
	protected String religion;
	protected String telefon;
	protected boolean trail;
	protected boolean gilde;
	protected boolean altER;
	protected boolean support;
	protected boolean infoMail;
	protected boolean free;
	protected ScoutRole rolle;
	protected Long truppId;
	protected Long vollzahlerId;
	protected BaseDao trupp;
	protected BaseDao Vollzahler;
	protected Set<BaseDao> reduced;
	protected Long VollzahlerId;
	protected Set<BaseDao> funktionen = new TreeSet<>();
	protected Set<BaseDao> siblings = new TreeSet<>();
	protected Set<BaseDao> parents = new TreeSet<>();
	protected Set<BaseDao> assisting = new TreeSet<>();
	protected Set<BaseDao> responsible = new TreeSet<>();
	protected Date changed;
	protected Date created;
	protected String changedBy;
	protected String createdBy;
	protected String uiString;

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
		if (!(obj instanceof MemberDao)) {
			return false;
		}
		MemberDao other = (MemberDao) obj;
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
	
	public Date getGeburtstag() {
		return geburtstag;
	}

	public void setGeburtstag(Date date) {
		this.geburtstag = date;
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
	public int compareTo(BaseDao o) {
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
			result += ", " + name;
		if (vorname != null && !vorname.trim().isEmpty())
			result += ", " + vorname;
		result += ", " + gebTag + "." + gebMonat + "." + gebJahr;
		if (ort != null && !ort.trim().isEmpty())
			result += " " + ort;
		return result;
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
		if (bvKey != null && !bvKey.trim().isEmpty())
			result += "bvKey: " + bvKey;
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
		result += ", aktivExtern: " + aktivExtern;
		if (email != null && !email.trim().isEmpty())
			result += ", email: " + email;
		if (religion != null && !religion.trim().isEmpty())
			result += ", religion: " + religion;
		if (telefon != null && !telefon.trim().isEmpty())
			result += ", telefon: " + telefon;
		return result;
	}

	@Pfad
	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	public Long getTruppId() {
		return truppId;
	}
	
	public void setTruppId(Long truppId) {
		this.truppId = truppId;
	}
	
	public Long getVollzahlerId() {
		return vollzahlerId;
	}
	
	public void setVollzahlerId(Long vollzahlerId) {
		this.vollzahlerId = vollzahlerId;
	}

	public BaseDao getTrupp() {
		return this.trupp;
	}

	public void setTrupp(final BaseDao Trupp) {
		this.trupp = Trupp;
	}

	@Pfad
	public BaseDao getVollzahler() {
		return this.Vollzahler;
	}

	public void setVollzahler(final BaseDao Vollzahler) {
		this.Vollzahler = Vollzahler;
	}

	@Pfad
	public Set<BaseDao> getFunktionen() {
		return this.funktionen;
	}

	public List<Long> getFunktionenIds() {
		return funktionen.stream().map(BaseDao::getId).collect(Collectors.toList());
	}

	public void setFunktionen(final Set<BaseDao> Funktionen) {
		this.funktionen = Funktionen;
	}

	@Pfad
	public Set<BaseDao> getSiblings() {
		return siblings;
	}

	public List<Long> getSiblingIds() {
		return siblings.stream().map(BaseDao::getId).collect(Collectors.toList());
	}

	public void setSiblings(Set<BaseDao> siblings) {
		this.siblings = siblings;
	}

	public Set<BaseDao> getParents() {
		return parents;
	}

	public void setParents(Set<BaseDao> parents) {
		this.parents = parents;
	}

	public Set<BaseDao> getAssisting() {
		return assisting;
	}
	
	public void setAssisting(Set<BaseDao> assisting) {
		this.assisting = assisting;
	}
	
	public Set<BaseDao> getResponsible() {
		return responsible;
	}
	
	public void setResponsible(Set<BaseDao> responsible) {
		this.responsible = responsible;
	}
	
	public String getUiString() {
		return uiString;
	}
	
	public void setUiString(String uiString) {
		this.uiString = uiString;
	}
}