/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.view;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import at.tfr.pfad.PaymentType;
import at.tfr.pfad.model.Booking;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Payment;

public class PaymentUI extends Payment {

	private Payment payment;
	private Member payer;
	private Set<Booking> bookings;
	private List<String> truppNames;

	public PaymentUI(Payment payment, Member payer, Set<Booking> bookings) {
		this.payment = payment;
		this.payer = payer;
		this.bookings = bookings;
		truppNames = bookings.stream().filter(b-> b.getMember() != null && b.getMember().getTrupp() != null).map(b->b.getMember().getTrupp().getName()).collect(Collectors.toList());
	}

	public Long getId() {
		return payment.getId();
	}

	public void setId(Long id) {
		payment.setId(id);
	}

	public int getVersion() {
		return payment.getVersion();
	}

	public void setVersion(int version) {
		payment.setVersion(version);
	}

	public boolean equals(Object obj) {
		return payment.equals(obj);
	}

	public int hashCode() {
		return payment.hashCode();
	}

	public Member getPayer() {
		return payer;
	}

	public void setPayer(Member payer) {
		this.payer = payer;
	}
	
	public String getPayerName() {
		return payer != null ? payer.getName()+","+payer.getVorname() : ""; 
	}
	
	@Override
	public Float getAmount() {
		return payment.getAmount();
	}
	
	@Override
	public void setAmount(Float amount) {
		payment.setAmount(amount);
	}

	public Boolean getFinished() {
		return payment.getFinished();
	}

	public void setFinished(Boolean finished) {
		payment.setFinished(finished);
	}
	
	public Boolean getAconto() {
		return payment.getAconto();
	}
	
	public void setAconto(Boolean aconto) {
		payment.setAconto(aconto);
	}

	public String getComment() {
		return payment.getComment();
	}

	public void setComment(String comment) {
		payment.setComment(comment);
	}
	
	public String getPayerIBAN() {
		return payment.getPayerIBAN();
	}

	public void setPayerIBAN(String payerIBAN) {
		payment.setPayerIBAN(payerIBAN);
	}
	
	public Set<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(Set<Booking> bookings) {
		this.bookings = bookings;
	}

	public PaymentType getType() {
		return payment.getType();
	}

	public void setType(PaymentType type) {
		payment.setType(type);
	}

	public Date getPaymentDate() {
		return payment.getPaymentDate();
	}

	public void setPaymentDate(Date paymentDate) {
		payment.setPaymentDate(paymentDate);
	}

	public Date getChanged() {
		return payment.getChanged();
	}

	public void setChanged(Date changed) {
		payment.setChanged(changed);
	}

	public Date getCreated() {
		return payment.getCreated();
	}

	public void setCreated(Date created) {
		payment.setCreated(created);
	}

	public String getChangedBy() {
		return payment.getChangedBy();
	}

	public void setChangedBy(String changedBy) {
		payment.setChangedBy(changedBy);
	}

	public String getCreatedBy() {
		return payment.getCreatedBy();
	}

	public void setCreatedBy(String createdBy) {
		payment.setCreatedBy(createdBy);
	}

	public String toString() {
		return payment.toString();
	}
	
	public String getBooking() {
		return bookings.stream().map(Booking::getShortString).collect(Collectors.joining());
	}
	
	public String getMember() {
		return bookings.stream().filter(b->b.getMember() != null).map(Booking::getMember).map(m->m.toString()).collect(Collectors.joining(","));
	}

	public String getSquad() {
		return bookings.stream().filter(b->b.getMember() != null && b.getMember().getTrupp() != null).map(b->b.getMember().getTrupp().getName()).collect(Collectors.joining(","));
	}

	public String getActivity() {
		return bookings.stream().filter(b->b.getActivity() != null).map(Booking::getActivity).map(a->a.toString()).collect(Collectors.joining(","));
	}
	
	public String getActivitiesLines() {
		return bookings.stream().filter(b->b.getActivity() != null).map(b -> b.getActivity().toString()).collect(Collectors.joining("<br>"));
	}
}
