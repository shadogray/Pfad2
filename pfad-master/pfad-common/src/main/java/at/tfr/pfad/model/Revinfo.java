package at.tfr.pfad.model;
// Generated Feb 2, 2021, 9:27:45 PM by Hibernate Tools 5.2.11.Final


import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Revinfo generated by hbm2java
 */
@Entity
@Table(name="REVINFO")
public class Revinfo  implements java.io.Serializable {


     private Integer rev;
     private Long revtstmp;
     private Set<MemberMemberAud> memberMemberAuds = new HashSet<MemberMemberAud>(0);
     private Set<ActivityAud> activityAuds = new HashSet<ActivityAud>(0);
     private Set<BookingAud> bookingAuds = new HashSet<BookingAud>(0);
     private Set<MemberAud> memberAuds = new HashSet<MemberAud>(0);
     private Set<SquadMemberAud> squadMemberAuds = new HashSet<SquadMemberAud>(0);
     private Set<MemberFunctionAud> memberFunctionAuds = new HashSet<MemberFunctionAud>(0);
     private Set<PaymentBookingAud> paymentBookingAuds = new HashSet<PaymentBookingAud>(0);
     private Set<BookingAud> bookingAuds_1 = new HashSet<BookingAud>(0);
     private Set<ActivityAud> activityAuds_1 = new HashSet<ActivityAud>(0);
     private Set<MailtemplateAud> mailtemplateAuds = new HashSet<MailtemplateAud>(0);
     private Set<PaymentAud> paymentAuds = new HashSet<PaymentAud>(0);
     private Set<MemberAud> memberAuds_1 = new HashSet<MemberAud>(0);
     private Set<SquadAud> squadAuds = new HashSet<SquadAud>(0);
     private Set<SquadMemberAud> squadMemberAuds_1 = new HashSet<SquadMemberAud>(0);
     private Set<PaymentAud> paymentAuds_1 = new HashSet<PaymentAud>(0);
     private Set<RegistrationAud> registrationAuds = new HashSet<RegistrationAud>(0);
     private Set<TrainingAud> trainingAuds = new HashSet<TrainingAud>(0);
     private Set<FunctionAud> functionAuds = new HashSet<FunctionAud>(0);
     private Set<MemberFunctionAud> memberFunctionAuds_1 = new HashSet<MemberFunctionAud>(0);
     private Set<MemberMemberAud> memberMemberAuds_1 = new HashSet<MemberMemberAud>(0);
     private Set<SquadAud> squadAuds_1 = new HashSet<SquadAud>(0);
     private Set<FunctionAud> functionAuds_1 = new HashSet<FunctionAud>(0);
     private Set<ParticipationAud> participationAuds = new HashSet<ParticipationAud>(0);
     private Set<PaymentBookingAud> paymentBookingAuds_1 = new HashSet<PaymentBookingAud>(0);

    public Revinfo() {
    }

    public Revinfo(Long revtstmp, Set<MemberMemberAud> memberMemberAuds, Set<ActivityAud> activityAuds, Set<BookingAud> bookingAuds, Set<MemberAud> memberAuds, Set<SquadMemberAud> squadMemberAuds, Set<MemberFunctionAud> memberFunctionAuds, Set<PaymentBookingAud> paymentBookingAuds, Set<BookingAud> bookingAuds_1, Set<ActivityAud> activityAuds_1, Set<MailtemplateAud> mailtemplateAuds, Set<PaymentAud> paymentAuds, Set<MemberAud> memberAuds_1, Set<SquadAud> squadAuds, Set<SquadMemberAud> squadMemberAuds_1, Set<PaymentAud> paymentAuds_1, Set<RegistrationAud> registrationAuds, Set<TrainingAud> trainingAuds, Set<FunctionAud> functionAuds, Set<MemberFunctionAud> memberFunctionAuds_1, Set<MemberMemberAud> memberMemberAuds_1, Set<SquadAud> squadAuds_1, Set<FunctionAud> functionAuds_1, Set<ParticipationAud> participationAuds, Set<PaymentBookingAud> paymentBookingAuds_1) {
       this.revtstmp = revtstmp;
       this.memberMemberAuds = memberMemberAuds;
       this.activityAuds = activityAuds;
       this.bookingAuds = bookingAuds;
       this.memberAuds = memberAuds;
       this.squadMemberAuds = squadMemberAuds;
       this.memberFunctionAuds = memberFunctionAuds;
       this.paymentBookingAuds = paymentBookingAuds;
       this.bookingAuds_1 = bookingAuds_1;
       this.activityAuds_1 = activityAuds_1;
       this.mailtemplateAuds = mailtemplateAuds;
       this.paymentAuds = paymentAuds;
       this.memberAuds_1 = memberAuds_1;
       this.squadAuds = squadAuds;
       this.squadMemberAuds_1 = squadMemberAuds_1;
       this.paymentAuds_1 = paymentAuds_1;
       this.registrationAuds = registrationAuds;
       this.trainingAuds = trainingAuds;
       this.functionAuds = functionAuds;
       this.memberFunctionAuds_1 = memberFunctionAuds_1;
       this.memberMemberAuds_1 = memberMemberAuds_1;
       this.squadAuds_1 = squadAuds_1;
       this.functionAuds_1 = functionAuds_1;
       this.participationAuds = participationAuds;
       this.paymentBookingAuds_1 = paymentBookingAuds_1;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="REV", unique=true, nullable=false)
    public Integer getRev() {
        return this.rev;
    }
    
    public void setRev(Integer rev) {
        this.rev = rev;
    }

    
    @Column(name="REVTSTMP")
    public Long getRevtstmp() {
        return this.revtstmp;
    }
    
    public void setRevtstmp(Long revtstmp) {
        this.revtstmp = revtstmp;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<MemberMemberAud> getMemberMemberAuds() {
        return this.memberMemberAuds;
    }
    
    public void setMemberMemberAuds(Set<MemberMemberAud> memberMemberAuds) {
        this.memberMemberAuds = memberMemberAuds;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<ActivityAud> getActivityAuds() {
        return this.activityAuds;
    }
    
    public void setActivityAuds(Set<ActivityAud> activityAuds) {
        this.activityAuds = activityAuds;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<BookingAud> getBookingAuds() {
        return this.bookingAuds;
    }
    
    public void setBookingAuds(Set<BookingAud> bookingAuds) {
        this.bookingAuds = bookingAuds;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<MemberAud> getMemberAuds() {
        return this.memberAuds;
    }
    
    public void setMemberAuds(Set<MemberAud> memberAuds) {
        this.memberAuds = memberAuds;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<SquadMemberAud> getSquadMemberAuds() {
        return this.squadMemberAuds;
    }
    
    public void setSquadMemberAuds(Set<SquadMemberAud> squadMemberAuds) {
        this.squadMemberAuds = squadMemberAuds;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<MemberFunctionAud> getMemberFunctionAuds() {
        return this.memberFunctionAuds;
    }
    
    public void setMemberFunctionAuds(Set<MemberFunctionAud> memberFunctionAuds) {
        this.memberFunctionAuds = memberFunctionAuds;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<PaymentBookingAud> getPaymentBookingAuds() {
        return this.paymentBookingAuds;
    }
    
    public void setPaymentBookingAuds(Set<PaymentBookingAud> paymentBookingAuds) {
        this.paymentBookingAuds = paymentBookingAuds;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<BookingAud> getBookingAuds_1() {
        return this.bookingAuds_1;
    }
    
    public void setBookingAuds_1(Set<BookingAud> bookingAuds_1) {
        this.bookingAuds_1 = bookingAuds_1;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<ActivityAud> getActivityAuds_1() {
        return this.activityAuds_1;
    }
    
    public void setActivityAuds_1(Set<ActivityAud> activityAuds_1) {
        this.activityAuds_1 = activityAuds_1;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<MailtemplateAud> getMailtemplateAuds() {
        return this.mailtemplateAuds;
    }
    
    public void setMailtemplateAuds(Set<MailtemplateAud> mailtemplateAuds) {
        this.mailtemplateAuds = mailtemplateAuds;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<PaymentAud> getPaymentAuds() {
        return this.paymentAuds;
    }
    
    public void setPaymentAuds(Set<PaymentAud> paymentAuds) {
        this.paymentAuds = paymentAuds;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<MemberAud> getMemberAuds_1() {
        return this.memberAuds_1;
    }
    
    public void setMemberAuds_1(Set<MemberAud> memberAuds_1) {
        this.memberAuds_1 = memberAuds_1;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<SquadAud> getSquadAuds() {
        return this.squadAuds;
    }
    
    public void setSquadAuds(Set<SquadAud> squadAuds) {
        this.squadAuds = squadAuds;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<SquadMemberAud> getSquadMemberAuds_1() {
        return this.squadMemberAuds_1;
    }
    
    public void setSquadMemberAuds_1(Set<SquadMemberAud> squadMemberAuds_1) {
        this.squadMemberAuds_1 = squadMemberAuds_1;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<PaymentAud> getPaymentAuds_1() {
        return this.paymentAuds_1;
    }
    
    public void setPaymentAuds_1(Set<PaymentAud> paymentAuds_1) {
        this.paymentAuds_1 = paymentAuds_1;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<RegistrationAud> getRegistrationAuds() {
        return this.registrationAuds;
    }
    
    public void setRegistrationAuds(Set<RegistrationAud> registrationAuds) {
        this.registrationAuds = registrationAuds;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<TrainingAud> getTrainingAuds() {
        return this.trainingAuds;
    }
    
    public void setTrainingAuds(Set<TrainingAud> trainingAuds) {
        this.trainingAuds = trainingAuds;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<FunctionAud> getFunctionAuds() {
        return this.functionAuds;
    }
    
    public void setFunctionAuds(Set<FunctionAud> functionAuds) {
        this.functionAuds = functionAuds;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<MemberFunctionAud> getMemberFunctionAuds_1() {
        return this.memberFunctionAuds_1;
    }
    
    public void setMemberFunctionAuds_1(Set<MemberFunctionAud> memberFunctionAuds_1) {
        this.memberFunctionAuds_1 = memberFunctionAuds_1;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<MemberMemberAud> getMemberMemberAuds_1() {
        return this.memberMemberAuds_1;
    }
    
    public void setMemberMemberAuds_1(Set<MemberMemberAud> memberMemberAuds_1) {
        this.memberMemberAuds_1 = memberMemberAuds_1;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<SquadAud> getSquadAuds_1() {
        return this.squadAuds_1;
    }
    
    public void setSquadAuds_1(Set<SquadAud> squadAuds_1) {
        this.squadAuds_1 = squadAuds_1;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<FunctionAud> getFunctionAuds_1() {
        return this.functionAuds_1;
    }
    
    public void setFunctionAuds_1(Set<FunctionAud> functionAuds_1) {
        this.functionAuds_1 = functionAuds_1;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<ParticipationAud> getParticipationAuds() {
        return this.participationAuds;
    }
    
    public void setParticipationAuds(Set<ParticipationAud> participationAuds) {
        this.participationAuds = participationAuds;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="revinfo")
    public Set<PaymentBookingAud> getPaymentBookingAuds_1() {
        return this.paymentBookingAuds_1;
    }
    
    public void setPaymentBookingAuds_1(Set<PaymentBookingAud> paymentBookingAuds_1) {
        this.paymentBookingAuds_1 = paymentBookingAuds_1;
    }




}


