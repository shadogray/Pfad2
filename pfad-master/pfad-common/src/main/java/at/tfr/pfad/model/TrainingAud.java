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
 * TrainingAud generated by hbm2java
 */
@Entity
@Table(name="TRAINING_AUD")
public class TrainingAud  implements java.io.Serializable {


     private TrainingAudId id;
     private Revinfo revinfo;
     private Byte revtype;
     private Date changed;
     private Boolean changedMod;
     private String changedby;
     private Boolean changedbyMod;
     private Date created;
     private Boolean createdMod;
     private String createdby;
     private Boolean createdbyMod;
     private String description;
     private Boolean descriptionMod;
     private String form;
     private Boolean formMod;
     private String name;
     private Boolean nameMod;
     private String phase;
     private Boolean phaseMod;
     private Boolean participationsMod;

    public TrainingAud() {
    }

	
    public TrainingAud(TrainingAudId id, Revinfo revinfo) {
        this.id = id;
        this.revinfo = revinfo;
    }
    public TrainingAud(TrainingAudId id, Revinfo revinfo, Byte revtype, Date changed, Boolean changedMod, String changedby, Boolean changedbyMod, Date created, Boolean createdMod, String createdby, Boolean createdbyMod, String description, Boolean descriptionMod, String form, Boolean formMod, String name, Boolean nameMod, String phase, Boolean phaseMod, Boolean participationsMod) {
       this.id = id;
       this.revinfo = revinfo;
       this.revtype = revtype;
       this.changed = changed;
       this.changedMod = changedMod;
       this.changedby = changedby;
       this.changedbyMod = changedbyMod;
       this.created = created;
       this.createdMod = createdMod;
       this.createdby = createdby;
       this.createdbyMod = createdbyMod;
       this.description = description;
       this.descriptionMod = descriptionMod;
       this.form = form;
       this.formMod = formMod;
       this.name = name;
       this.nameMod = nameMod;
       this.phase = phase;
       this.phaseMod = phaseMod;
       this.participationsMod = participationsMod;
    }
   
     @EmbeddedId

    
    @AttributeOverrides( {
        @AttributeOverride(name="id", column=@Column(name="ID", nullable=false) ), 
        @AttributeOverride(name="rev", column=@Column(name="REV", nullable=false) ) } )
    public TrainingAudId getId() {
        return this.id;
    }
    
    public void setId(TrainingAudId id) {
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

    
    @Column(name="CHANGEDBY")
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

    
    @Column(name="CREATEDBY")
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

    
    @Column(name="DESCRIPTION")
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    
    @Column(name="DESCRIPTION_MOD")
    public Boolean getDescriptionMod() {
        return this.descriptionMod;
    }
    
    public void setDescriptionMod(Boolean descriptionMod) {
        this.descriptionMod = descriptionMod;
    }

    
    @Column(name="FORM")
    public String getForm() {
        return this.form;
    }
    
    public void setForm(String form) {
        this.form = form;
    }

    
    @Column(name="FORM_MOD")
    public Boolean getFormMod() {
        return this.formMod;
    }
    
    public void setFormMod(Boolean formMod) {
        this.formMod = formMod;
    }

    
    @Column(name="NAME")
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

    
    @Column(name="PHASE")
    public String getPhase() {
        return this.phase;
    }
    
    public void setPhase(String phase) {
        this.phase = phase;
    }

    
    @Column(name="PHASE_MOD")
    public Boolean getPhaseMod() {
        return this.phaseMod;
    }
    
    public void setPhaseMod(Boolean phaseMod) {
        this.phaseMod = phaseMod;
    }

    
    @Column(name="PARTICIPATIONS_MOD")
    public Boolean getParticipationsMod() {
        return this.participationsMod;
    }
    
    public void setParticipationsMod(Boolean participationsMod) {
        this.participationsMod = participationsMod;
    }




}

