package at.tfr.pfad.model;
// Generated Feb 2, 2021, 9:27:45 PM by Hibernate Tools 5.2.11.Final


import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * RegistrationAud generated by hbm2java
 */
@Entity
@Table(name="REGISTRATION_AUD")
public class RegistrationAud  implements java.io.Serializable {


     private RegistrationAudId id;
     private Revinfo revinfo;
     private Byte revtype;
     private boolean aktiv;
     private Boolean aktivMod;
     private String anrede;
     private Boolean anredeMod;
     private Date changed;
     private Boolean changedMod;
     private String changedby;
     private Boolean changedbyMod;
     private Date created;
     private Boolean createdMod;
     private String createdby;
     private Boolean createdbyMod;
     private String email;
     private Boolean emailMod;
     private Integer gebjahr;
     private Boolean gebjahrMod;
     private Integer gebmonat;
     private Boolean gebmonatMod;
     private Integer gebtag;
     private Boolean gebtagMod;
     private String geschlecht;
     private Boolean geschlechtMod;
     private String name;
     private Boolean nameMod;
     private String ort;
     private Boolean ortMod;
     private String parentname;
     private Boolean parentnameMod;
     private String parentvorname;
     private Boolean parentvornameMod;
     private String plz;
     private Boolean plzMod;
     private String religion;
     private Boolean religionMod;
     private Integer schoolentry;
     private Boolean schoolentryMod;
     private boolean storno;
     private Boolean stornoMod;
     private String strasse;
     private Boolean strasseMod;
     private String telefon;
     private Boolean telefonMod;
     private String titel;
     private Boolean titelMod;
     private String vorname;
     private Boolean vornameMod;
     private String comment;
     private Boolean commentMod;
     private String status;
     private Boolean statusMod;
     private Long memberId;
     private Boolean memberMod;
     private Long parentId;
     private Boolean parentMod;

    public RegistrationAud() {
    }

	
    public RegistrationAud(RegistrationAudId id, Revinfo revinfo, boolean aktiv, boolean storno) {
        this.id = id;
        this.revinfo = revinfo;
        this.aktiv = aktiv;
        this.storno = storno;
    }
    public RegistrationAud(RegistrationAudId id, Revinfo revinfo, Byte revtype, boolean aktiv, Boolean aktivMod, String anrede, Boolean anredeMod, Date changed, Boolean changedMod, String changedby, Boolean changedbyMod, Date created, Boolean createdMod, String createdby, Boolean createdbyMod, String email, Boolean emailMod, Integer gebjahr, Boolean gebjahrMod, Integer gebmonat, Boolean gebmonatMod, Integer gebtag, Boolean gebtagMod, String geschlecht, Boolean geschlechtMod, String name, Boolean nameMod, String ort, Boolean ortMod, String parentname, Boolean parentnameMod, String parentvorname, Boolean parentvornameMod, String plz, Boolean plzMod, String religion, Boolean religionMod, Integer schoolentry, Boolean schoolentryMod, boolean storno, Boolean stornoMod, String strasse, Boolean strasseMod, String telefon, Boolean telefonMod, String titel, Boolean titelMod, String vorname, Boolean vornameMod, String comment, Boolean commentMod, String status, Boolean statusMod, Long memberId, Boolean memberMod, Long parentId, Boolean parentMod) {
       this.id = id;
       this.revinfo = revinfo;
       this.revtype = revtype;
       this.aktiv = aktiv;
       this.aktivMod = aktivMod;
       this.anrede = anrede;
       this.anredeMod = anredeMod;
       this.changed = changed;
       this.changedMod = changedMod;
       this.changedby = changedby;
       this.changedbyMod = changedbyMod;
       this.created = created;
       this.createdMod = createdMod;
       this.createdby = createdby;
       this.createdbyMod = createdbyMod;
       this.email = email;
       this.emailMod = emailMod;
       this.gebjahr = gebjahr;
       this.gebjahrMod = gebjahrMod;
       this.gebmonat = gebmonat;
       this.gebmonatMod = gebmonatMod;
       this.gebtag = gebtag;
       this.gebtagMod = gebtagMod;
       this.geschlecht = geschlecht;
       this.geschlechtMod = geschlechtMod;
       this.name = name;
       this.nameMod = nameMod;
       this.ort = ort;
       this.ortMod = ortMod;
       this.parentname = parentname;
       this.parentnameMod = parentnameMod;
       this.parentvorname = parentvorname;
       this.parentvornameMod = parentvornameMod;
       this.plz = plz;
       this.plzMod = plzMod;
       this.religion = religion;
       this.religionMod = religionMod;
       this.schoolentry = schoolentry;
       this.schoolentryMod = schoolentryMod;
       this.storno = storno;
       this.stornoMod = stornoMod;
       this.strasse = strasse;
       this.strasseMod = strasseMod;
       this.telefon = telefon;
       this.telefonMod = telefonMod;
       this.titel = titel;
       this.titelMod = titelMod;
       this.vorname = vorname;
       this.vornameMod = vornameMod;
       this.comment = comment;
       this.commentMod = commentMod;
       this.status = status;
       this.statusMod = statusMod;
       this.memberId = memberId;
       this.memberMod = memberMod;
       this.parentId = parentId;
       this.parentMod = parentMod;
    }
   
     @EmbeddedId

    
    @AttributeOverrides( {
        @AttributeOverride(name="id", column=@Column(name="ID", nullable=false) ), 
        @AttributeOverride(name="rev", column=@Column(name="REV", nullable=false) ) } )
    public RegistrationAudId getId() {
        return this.id;
    }
    
    public void setId(RegistrationAudId id) {
        this.id = id;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="REV", nullable=false, insertable=false, updatable=false)
    public Revinfo getRevinfo() {
        return this.revinfo;
    }
    
    public void setRevinfo(Revinfo revinfo) {
        this.revinfo = revinfo;
    }

    
    @Column(name="REVTYPE")
    public Byte getRevtype() {
        return this.revtype;
    }
    
    public void setRevtype(Byte revtype) {
        this.revtype = revtype;
    }

    
    @Column(name="AKTIV", nullable=false)
    public boolean isAktiv() {
        return this.aktiv;
    }
    
    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }

    
    @Column(name="AKTIV_MOD")
    public Boolean getAktivMod() {
        return this.aktivMod;
    }
    
    public void setAktivMod(Boolean aktivMod) {
        this.aktivMod = aktivMod;
    }

    
    @Column(name="ANREDE", length=16)
    public String getAnrede() {
        return this.anrede;
    }
    
    public void setAnrede(String anrede) {
        this.anrede = anrede;
    }

    
    @Column(name="ANREDE_MOD")
    public Boolean getAnredeMod() {
        return this.anredeMod;
    }
    
    public void setAnredeMod(Boolean anredeMod) {
        this.anredeMod = anredeMod;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CHANGED", length=23)
    public Date getChanged() {
        return this.changed;
    }
    
    public void setChanged(Date changed) {
        this.changed = changed;
    }

    
    @Column(name="CHANGED_MOD")
    public Boolean getChangedMod() {
        return this.changedMod;
    }
    
    public void setChangedMod(Boolean changedMod) {
        this.changedMod = changedMod;
    }

    
    @Column(name="CHANGEDBY", length=32)
    public String getChangedby() {
        return this.changedby;
    }
    
    public void setChangedby(String changedby) {
        this.changedby = changedby;
    }

    
    @Column(name="CHANGEDBY_MOD")
    public Boolean getChangedbyMod() {
        return this.changedbyMod;
    }
    
    public void setChangedbyMod(Boolean changedbyMod) {
        this.changedbyMod = changedbyMod;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CREATED", length=23)
    public Date getCreated() {
        return this.created;
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }

    
    @Column(name="CREATED_MOD")
    public Boolean getCreatedMod() {
        return this.createdMod;
    }
    
    public void setCreatedMod(Boolean createdMod) {
        this.createdMod = createdMod;
    }

    
    @Column(name="CREATEDBY", length=32)
    public String getCreatedby() {
        return this.createdby;
    }
    
    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    
    @Column(name="CREATEDBY_MOD")
    public Boolean getCreatedbyMod() {
        return this.createdbyMod;
    }
    
    public void setCreatedbyMod(Boolean createdbyMod) {
        this.createdbyMod = createdbyMod;
    }

    
    @Column(name="EMAIL", length=128)
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    
    @Column(name="EMAIL_MOD")
    public Boolean getEmailMod() {
        return this.emailMod;
    }
    
    public void setEmailMod(Boolean emailMod) {
        this.emailMod = emailMod;
    }

    
    @Column(name="GEBJAHR")
    public Integer getGebjahr() {
        return this.gebjahr;
    }
    
    public void setGebjahr(Integer gebjahr) {
        this.gebjahr = gebjahr;
    }

    
    @Column(name="GEBJAHR_MOD")
    public Boolean getGebjahrMod() {
        return this.gebjahrMod;
    }
    
    public void setGebjahrMod(Boolean gebjahrMod) {
        this.gebjahrMod = gebjahrMod;
    }

    
    @Column(name="GEBMONAT")
    public Integer getGebmonat() {
        return this.gebmonat;
    }
    
    public void setGebmonat(Integer gebmonat) {
        this.gebmonat = gebmonat;
    }

    
    @Column(name="GEBMONAT_MOD")
    public Boolean getGebmonatMod() {
        return this.gebmonatMod;
    }
    
    public void setGebmonatMod(Boolean gebmonatMod) {
        this.gebmonatMod = gebmonatMod;
    }

    
    @Column(name="GEBTAG")
    public Integer getGebtag() {
        return this.gebtag;
    }
    
    public void setGebtag(Integer gebtag) {
        this.gebtag = gebtag;
    }

    
    @Column(name="GEBTAG_MOD")
    public Boolean getGebtagMod() {
        return this.gebtagMod;
    }
    
    public void setGebtagMod(Boolean gebtagMod) {
        this.gebtagMod = gebtagMod;
    }

    
    @Column(name="GESCHLECHT")
    public String getGeschlecht() {
        return this.geschlecht;
    }
    
    public void setGeschlecht(String geschlecht) {
        this.geschlecht = geschlecht;
    }

    
    @Column(name="GESCHLECHT_MOD")
    public Boolean getGeschlechtMod() {
        return this.geschlechtMod;
    }
    
    public void setGeschlechtMod(Boolean geschlechtMod) {
        this.geschlechtMod = geschlechtMod;
    }

    
    @Column(name="NAME", length=128)
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="NAME_MOD")
    public Boolean getNameMod() {
        return this.nameMod;
    }
    
    public void setNameMod(Boolean nameMod) {
        this.nameMod = nameMod;
    }

    
    @Column(name="ORT", length=128)
    public String getOrt() {
        return this.ort;
    }
    
    public void setOrt(String ort) {
        this.ort = ort;
    }

    
    @Column(name="ORT_MOD")
    public Boolean getOrtMod() {
        return this.ortMod;
    }
    
    public void setOrtMod(Boolean ortMod) {
        this.ortMod = ortMod;
    }

    
    @Column(name="PARENTNAME", length=128)
    public String getParentname() {
        return this.parentname;
    }
    
    public void setParentname(String parentname) {
        this.parentname = parentname;
    }

    
    @Column(name="PARENTNAME_MOD")
    public Boolean getParentnameMod() {
        return this.parentnameMod;
    }
    
    public void setParentnameMod(Boolean parentnameMod) {
        this.parentnameMod = parentnameMod;
    }

    
    @Column(name="PARENTVORNAME", length=128)
    public String getParentvorname() {
        return this.parentvorname;
    }
    
    public void setParentvorname(String parentvorname) {
        this.parentvorname = parentvorname;
    }

    
    @Column(name="PARENTVORNAME_MOD")
    public Boolean getParentvornameMod() {
        return this.parentvornameMod;
    }
    
    public void setParentvornameMod(Boolean parentvornameMod) {
        this.parentvornameMod = parentvornameMod;
    }

    
    @Column(name="PLZ", length=16)
    public String getPlz() {
        return this.plz;
    }
    
    public void setPlz(String plz) {
        this.plz = plz;
    }

    
    @Column(name="PLZ_MOD")
    public Boolean getPlzMod() {
        return this.plzMod;
    }
    
    public void setPlzMod(Boolean plzMod) {
        this.plzMod = plzMod;
    }

    
    @Column(name="RELIGION", length=32)
    public String getReligion() {
        return this.religion;
    }
    
    public void setReligion(String religion) {
        this.religion = religion;
    }

    
    @Column(name="RELIGION_MOD")
    public Boolean getReligionMod() {
        return this.religionMod;
    }
    
    public void setReligionMod(Boolean religionMod) {
        this.religionMod = religionMod;
    }

    
    @Column(name="SCHOOLENTRY")
    public Integer getSchoolentry() {
        return this.schoolentry;
    }
    
    public void setSchoolentry(Integer schoolentry) {
        this.schoolentry = schoolentry;
    }

    
    @Column(name="SCHOOLENTRY_MOD")
    public Boolean getSchoolentryMod() {
        return this.schoolentryMod;
    }
    
    public void setSchoolentryMod(Boolean schoolentryMod) {
        this.schoolentryMod = schoolentryMod;
    }

    
    @Column(name="STORNO", nullable=false)
    public boolean isStorno() {
        return this.storno;
    }
    
    public void setStorno(boolean storno) {
        this.storno = storno;
    }

    
    @Column(name="STORNO_MOD")
    public Boolean getStornoMod() {
        return this.stornoMod;
    }
    
    public void setStornoMod(Boolean stornoMod) {
        this.stornoMod = stornoMod;
    }

    
    @Column(name="STRASSE", length=128)
    public String getStrasse() {
        return this.strasse;
    }
    
    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    
    @Column(name="STRASSE_MOD")
    public Boolean getStrasseMod() {
        return this.strasseMod;
    }
    
    public void setStrasseMod(Boolean strasseMod) {
        this.strasseMod = strasseMod;
    }

    
    @Column(name="TELEFON")
    public String getTelefon() {
        return this.telefon;
    }
    
    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    
    @Column(name="TELEFON_MOD")
    public Boolean getTelefonMod() {
        return this.telefonMod;
    }
    
    public void setTelefonMod(Boolean telefonMod) {
        this.telefonMod = telefonMod;
    }

    
    @Column(name="TITEL", length=16)
    public String getTitel() {
        return this.titel;
    }
    
    public void setTitel(String titel) {
        this.titel = titel;
    }

    
    @Column(name="TITEL_MOD")
    public Boolean getTitelMod() {
        return this.titelMod;
    }
    
    public void setTitelMod(Boolean titelMod) {
        this.titelMod = titelMod;
    }

    
    @Column(name="VORNAME", length=128)
    public String getVorname() {
        return this.vorname;
    }
    
    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    
    @Column(name="VORNAME_MOD")
    public Boolean getVornameMod() {
        return this.vornameMod;
    }
    
    public void setVornameMod(Boolean vornameMod) {
        this.vornameMod = vornameMod;
    }

    
    @Column(name="COMMENT")
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }

    
    @Column(name="COMMENT_MOD")
    public Boolean getCommentMod() {
        return this.commentMod;
    }
    
    public void setCommentMod(Boolean commentMod) {
        this.commentMod = commentMod;
    }

    
    @Column(name="STATUS", length=32)
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    
    @Column(name="STATUS_MOD")
    public Boolean getStatusMod() {
        return this.statusMod;
    }
    
    public void setStatusMod(Boolean statusMod) {
        this.statusMod = statusMod;
    }

    
    @Column(name="MEMBER_ID")
    public Long getMemberId() {
        return this.memberId;
    }
    
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    
    @Column(name="MEMBER_MOD")
    public Boolean getMemberMod() {
        return this.memberMod;
    }
    
    public void setMemberMod(Boolean memberMod) {
        this.memberMod = memberMod;
    }

    
    @Column(name="PARENT_ID")
    public Long getParentId() {
        return this.parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    
    @Column(name="PARENT_MOD")
    public Boolean getParentMod() {
        return this.parentMod;
    }
    
    public void setParentMod(Boolean parentMod) {
        this.parentMod = parentMod;
    }




}

