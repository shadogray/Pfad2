package at.tfr.pfad.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import at.tfr.pfad.Pfad;

@NamedQueries({ 
	@NamedQuery(name = "Member.distName", query = "select distinct m.name from Member m where m.name is not null order by m.name"),
	@NamedQuery(name = "Member.distNameLike", query = "select distinct m.name from Member m where lower(m.name) like ?1 order by m.name"),
	@NamedQuery(name = "Member.distVorname", query = "select distinct m.vorname from Member m where m.vorname is not null order by m.vorname"),
	@NamedQuery(name = "Member.distVornameLike", query = "select distinct m.vorname from Member m where lower(m.vorname) like ?1 order by m.vorname"),
	@NamedQuery(name = "Member.distPLZ", query = "select distinct m.plz from Member m where m.plz is not null order by m.plz"),
	@NamedQuery(name = "Member.distPLZLike", query = "select distinct m.plz from Member m where lower(m.plz) like ?1 order by m.plz"),
	@NamedQuery(name = "Member.distOrt", query = "select distinct m.ort from Member m where m.ort is not null order by m.ort"),
	@NamedQuery(name = "Member.distOrtLike", query = "select distinct m.ort from Member m where lower(m.ort) like ?1 order by m.ort"),
	@NamedQuery(name = "Member.distStrasse", query = "select distinct m.strasse from Member m where m.strasse is not null order by m.strasse"),
	@NamedQuery(name = "Member.distStrasseLike", query = "select distinct m.strasse from Member m where lower(m.strasse) like ?1 order by m.strasse"),
	@NamedQuery(name = "Member.distTitel", query = "select distinct m.titel from Member m where m.titel is not null order by m.titel"),
	@NamedQuery(name = "Member.distTitelLike", query = "select distinct m.titel from Member m where lower(m.titel) like ?1 order by m.titel"),
	@NamedQuery(name = "Member.distAnrede", query = "select distinct m.anrede from Member m where m.anrede is not null order by m.anrede"),
	@NamedQuery(name = "Member.distAnredeLike", query = "select distinct m.anrede from Member m where lower(m.anrede) like ?1 order by m.anrede"),
	@NamedQuery(name = "Member.distReligion", query = "select distinct m.religion from Member m where m.religion is not null order by m.religion"),
	@NamedQuery(name = "Member.distReligionLike", query = "select distinct m.religion from Member m where lower(m.religion) like ?1 order by m.religion"), 
	@NamedQuery(name = "Member.withFunction", query = "select m from Member m where ?1 member of m.funktionen"),
	})
@NamedEntityGraphs({
		@NamedEntityGraph(name = "fetchAll", 
				attributeNodes = { @NamedAttributeNode("funktionen"),
				@NamedAttributeNode("Vollzahler"), 
				@NamedAttributeNode("reduced"), 
				@NamedAttributeNode("parents"),
				@NamedAttributeNode("siblings"), 
				@NamedAttributeNode("trupp") }),
		@NamedEntityGraph(name = "withTrupp", attributeNodes = @NamedAttributeNode("trupp")) })

@Entity
@DiscriminatorValue("A")
public class Member extends Membera {

	@ManyToOne
	@OrderBy("name")
	protected Squad trupp;

	@NotAudited // inverse side!
	@ManyToMany(mappedBy = "assistants")
	protected Set<Squad> squads;

	@NotAudited // inverse side!
	@OneToMany(mappedBy = "leaderFemale")
	protected Set<Squad> femaleGuided;

	@NotAudited // inverse side!
	@OneToMany(mappedBy = "leaderMale")
	protected Set<Squad> maleGuided;

	@ManyToOne
	protected Member Vollzahler;

	@NotAudited // inverse side!
	@OneToMany(mappedBy = "Vollzahler")
	@OrderBy("Name, Vorname")
	protected Set<Member> reduced;

	@Audited
	@ManyToMany(fetch = FetchType.EAGER)
	protected Set<Function> funktionen = new HashSet<>();

	@Audited
	@ManyToMany
	@JoinTable(name = "member_member", joinColumns = @JoinColumn(name = "member_id"), inverseJoinColumns = @JoinColumn(name = "siblings_id"))
	@OrderBy("Name, Vorname")
	protected Set<Member> siblings = new HashSet<>();

	@NotAudited // inverse side!
	@ManyToMany(mappedBy = "siblings")
	@OrderBy("Name, Vorname")
	protected Set<Member> parents = new TreeSet<>();

	@NotAudited // inverse side!
	@OneToMany(mappedBy = "payer")
	@OrderBy(value = "id DESC")
	private Set<Payment> payments;

	@NotAudited // inverse side!
	@OneToMany(mappedBy = "member")
	@OrderBy(value = "id DESC")
	private Set<Booking> bookings = new TreeSet<>();

	@NotAudited // inverse side!
	@OneToMany(mappedBy = "member")
	@OrderBy(value = "id DESC")
	private Set<Participation> participations = new TreeSet<>();
	
	@NotAudited
	@OneToMany(mappedBy = "member")
	private Set<MailMessage> mailMessages = new TreeSet<>();
	
	public Squad getTrupp() {
		return this.trupp;
	}

	public void setTrupp(final Squad trupp) {
		this.trupp = trupp;
		if (trupp != null) {
			aktiv = true;
		}
	}

	@Pfad
	public Member getVollzahler() {
		return this.Vollzahler;
	}

	public void setVollzahler(final Member Vollzahler) {
		this.Vollzahler = Vollzahler;
	}

	@Pfad
	public Set<Function> getFunktionen() {
		Iterator<Function> fi = this.funktionen.iterator();
		while (fi.hasNext()) {
			Function f = fi.next();
			if (f != null && f.getId() == null)
				fi.remove();
		}
		return this.funktionen;
	}

	public List<Long> getFunktionenIds() {
		return funktionen.stream().map(Function::getId).collect(Collectors.toList());
	}

	public void setFunktionen(final Set<Function> Funktionen) {
		this.funktionen = Funktionen;
		if (this.funktionen != null && this.funktionen.stream().anyMatch(f -> Boolean.TRUE.equals(f.isExportReg()))) {
			aktiv = true;
		}
	}

	@Pfad
	public Set<Member> getSiblings() {
		return siblings;
	}

	public List<Long> getSiblingIds() {
		return siblings.stream().map(Member::getId).collect(Collectors.toList());
	}

	public void setSiblings(Set<Member> siblings) {
		this.siblings = siblings;
	}

	/**
	 * Inverse relationship, add will not be persisted.
	 * 
	 * @return
	 */
	public Set<Member> getParents() {
		return parents;
	}

	protected void setParents(Set<Member> parents) {
		this.parents = parents;
	}
	
	public Long getTruppId() {
		return truppId;
	}

	public Long getVollzahlerId() {
		return VollzahlerId;
	}

	/**
	 * Inverse relationship, add will not be persisted.
	 * 
	 * @return
	 */
	@XmlTransient
	public Set<Payment> getPayments() {
		return payments;
	}

	@XmlTransient
	public List<Long> getPaymentsIds() {
		return payments.stream().map(Payment::getId).collect(Collectors.toList());
	}

	/**
	 * Inverse relationship, add will not be persisted.
	 * 
	 * @return
	 */
	@XmlTransient
	public Set<Booking> getBookings() {
		return this.bookings;
	}

	@XmlTransient
	public List<Long> getBookingsIds() {
		return bookings.stream().map(Booking::getId).collect(Collectors.toList());
	}

	public void setBookings(final Set<Booking> bookings) {
		this.bookings = bookings;
	}
	
	@XmlTransient
	public Set<Participation> getParticipations() {
		return participations;
	}
	
	@XmlTransient
	public List<Long> getParticipationIds() {
		return participations.stream().map(Participation::getId).collect(Collectors.toList());
	}

	public void setParticipations(Set<Participation> participations) {
		this.participations = participations;
	}

	public Set<MailMessage> getMailMessages() {
		return mailMessages;
	}

	public Member addParents(Collection<Member> parents) {
		parents.iterator().forEachRemaining(p -> addParent(p));
		return this;
	}
	
	public Member addParent(Member parent) {
		parent.getSiblings().add(this);
		getParents().add(parent);
		return this;
	}

	public boolean isAnyFree() {
		return free || (funktionen != null && funktionen.stream().anyMatch(f->Boolean.TRUE.equals(f.isFree())));
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
		if (truppId != null)
			result += ", truppId: " + truppId;
		if (VollzahlerId != null)
			result += ", VollzahlerId: " + VollzahlerId;
		return result;
	}

	public String toUiString() {
		StringBuilder result = new StringBuilder("Person:\n");
		if (bvKey != null && !bvKey.trim().isEmpty())
			result.append("\tBV-Key: \t").append(bvKey).append("\n");
		result.append("\nName:   \n");
		result.append("\tAnrede:  \t").append(anrede).append("\n");
		result.append("\tTitel:   \t").append(titel).append("\n");
		result.append("\tName:    \t").append(name).append("\n");
		result.append("\tVorname: \t").append(vorname).append("\n");
		
		// Address
		result.append("\nAdresse:\n");
		result.append("\tStrasse: \t").append(strasse).append("\n");
		result.append("\tPLZ:     \t").append(plz).append("\n");
		result.append("\tOrt:     \t").append(ort).append("\n");

		// Communications
		result.append("\nKontakt:\n");
		result.append("\tEmail:   \t").append(email).append("\n");
		result.append("\tTelefon: \t").append(telefon).append("\n");
		
		// GebDatum
		result.append("\nPersönliches:\n");
		result.append("\tGebTag:  \t").append(geburtstag().toString("dd.MM.yyyy")).append("\n");
		result.append("\tGeschlecht:\t").append(geschlecht).append("\n");
		result.append("\tAktiv:   \t").append(aktiv).append("\n");
		result.append("\tAktivExtern:\t").append(aktivExtern).append("\n");
		result.append("\tTrupp:   \t").append(trupp).append("\n");
		result.append("\tVollzahler:\t").append(Vollzahler).append("\n");
		
		return result.toString();
	}

}
