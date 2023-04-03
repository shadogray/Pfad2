/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.view;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import at.tfr.pfad.ActivityType;
import at.tfr.pfad.BookingStatus;
import at.tfr.pfad.model.Activity;
import at.tfr.pfad.model.Booking;
import at.tfr.pfad.model.Function;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Payment;
import at.tfr.pfad.model.Squad;

public class BookingUI extends Booking {

	private Booking booking;
	private boolean isPayed;
	private Set<Member> payers;
	private boolean free;
	private String freeSuffix = "";
	private Activity activity;
	private Member member;
	private Squad squad;
	private String squadName;
	private Set<Payment> payments;

	public BookingUI(Booking booking, Activity activity, Member member, Squad squad, Set<Payment> payments, boolean isLeader, boolean isAssistant) {
		this.booking = booking;
		this.activity = activity;
		this.member = member;
		this.squad = squad;
		this.payments = payments;
		if (member != null) {
			Optional<Function> funcOpt = booking.getMember().getFunktionen().stream().filter(f->Boolean.TRUE.equals(f.isFree())).findFirst();
			if (activity != null && ActivityType.Membership.equals(activity.getType())) {
				free = booking.getMember().isFree() || isLeader || isAssistant ||
						booking.getMember().getFunktionen().stream().anyMatch(f->Boolean.TRUE.equals(f.isFree()));
				if (booking.getMember().isFree()) freeSuffix = "(MF)";
				else if (isLeader) freeSuffix = "(L)";
				else if (isAssistant) freeSuffix = "(AS)";
				else if (funcOpt.isPresent()) {
					freeSuffix = "("+funcOpt.get().getKey()+")";
				}
			}
		}
		squadName = squad != null ? squad.getName() : null;
		if (squadName == null) {
			if (booking.getSquad() != null) {
				squadName = booking.getSquad().getName();
			}
		}
	}

	public Long getId() {
		return booking.getId();
	}

	public void setId(Long id) {
		booking.setId(id);
	}

	public int getVersion() {
		return booking.getVersion();
	}

	public void setVersion(int version) {
		booking.setVersion(version);
	}

	public boolean equals(Object obj) {
		return booking.equals(obj);
	}

	public int hashCode() {
		return booking.hashCode();
	}

	public Set<Payment> getPayments() {
		return payments;
	}

	public void setPayments(Set<Payment> payments) {
		this.payments = payments;
	}

	public Member getMember() {
		return booking.getMember();
	}

	public void setMember(Member member) {
		booking.setMember(member);
	}
	
	public Boolean getAktiv() {
		return booking.getMember() != null ? booking.getMember().isAktiv() : null;
	}

	public Activity getActivity() {
		return booking.getActivity();
	}

	public void setActivity(Activity Activity) {
		booking.setActivity(Activity);
	}
	
	public BookingStatus getStatus() {
		return booking.getStatus();
	}

	public void setStatus(BookingStatus status) {
		booking.setStatus(status);
	}
	
	public String getComment() {
		return booking.getComment();
	}

	public void setComment(String comment) {
		booking.setComment(comment);
	}

	public Date getChanged() {
		return booking.getChanged();
	}

	public void setChanged(Date changed) {
		booking.setChanged(changed);
	}

	public Date getCreated() {
		return booking.getCreated();
	}

	public void setCreated(Date created) {
		booking.setCreated(created);
	}

	public String getChangedBy() {
		return booking.getChangedBy();
	}

	public void setChangedBy(String changedBy) {
		booking.setChangedBy(changedBy);
	}

	public String toString() {
		return booking.toString();
	}

	public String getPayed() {
		boolean finished = payments.stream().anyMatch(p->Boolean.TRUE.equals(p.getFinished()));
		if (finished) {
			return "JA";
		}
		String payed = "NEIN";
		boolean aconto = payments.stream().anyMatch(p->Boolean.TRUE.equals(p.getAconto()));
		if (aconto) {
			payed = "ANZ";
		}
		if (free) {
			payed = "FREI " + freeSuffix;
		}
		return payed;
	}
	
	public String getPayer() {
		return payments.stream().filter(b->b.getPayer() != null).map(Payment::getPayer).map(p->p.toString()).collect(Collectors.joining(","));
	}

	public String getSquadName() {
		return squadName;
	}
	
	public String getStrasse() {
		return booking.getMember().getStrasse();
	}
	
	public String getOrt() {
		return booking.getMember().getOrt();
	}
	
	public boolean isFree() {
		return free;
	}
	
	public void setFree(boolean free) {
		this.free = free;
	}

	public String getFreeSuffix() {
		return freeSuffix;
	}
	
	public boolean isActivityFinished() {
		return activity.isFinished();
	}
}
