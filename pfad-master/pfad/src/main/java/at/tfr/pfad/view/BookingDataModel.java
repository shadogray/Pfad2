/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.view;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import at.tfr.pfad.ActivityStatus;
import at.tfr.pfad.ActivityType;
import at.tfr.pfad.BookingStatus;
import at.tfr.pfad.dao.SquadRepository;
import at.tfr.pfad.model.Activity;
import at.tfr.pfad.model.Activity_;
import at.tfr.pfad.model.Booking;
import at.tfr.pfad.model.Booking_;
import at.tfr.pfad.model.Function_;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Member_;
import at.tfr.pfad.model.Payment;
import at.tfr.pfad.model.Payment_;
import at.tfr.pfad.model.Squad;
import at.tfr.pfad.model.Squad_;

@Stateful
public class BookingDataModel extends BaseDataModel<Booking, BookingUI> {

	@Inject
	private SquadRepository squadRepo;
	
	public BookingDataModel() {
		uiClass = BookingUI.class;
		entityClass = Booking.class;
	}
	
	@Override
	protected Root<Booking> getRoot() {
		Root<Booking> root = super.getRoot();
		return root;
	}

	@Override
	protected Root<Booking> getCountRoot() {
		Root<Booking> root = super.getCountRoot();
		return root;
	}
	
	@Override
	protected Path<Booking>[] getGroupByRoots() {
		return new Path[] { root };
	}
	
	public BookingDataModel(Class<BookingUI> uiClass, Class<Booking> entityClass) {
		super(uiClass, entityClass);
	}

	@Override
	protected CriteriaQuery<Long> createCriteria(boolean addOrder) {
		CriteriaQuery<Long> crit = super.createCriteria(addOrder);
		//root.fetch(Booking_.member); //.fetch(Member_.funktionen, JoinType.LEFT);
		//root.fetch(Booking_.activity);
		//root.fetch(Booking_.payments, JoinType.LEFT);
		return crit;
	}
	
	@Override
	public List<BookingUI> convertToUiBean(List<Long> list) {
		if (!list.isEmpty()) {
			CriteriaQuery<Tuple> tcq = cb.createTupleQuery();
			Root<Booking> tr = tcq.from(Booking.class);
			Subquery<Squad> squadSubLead = tcq.subquery(Squad.class);
			Subquery<Squad> squadSubAss = tcq.subquery(Squad.class);
			Root<Squad> squadLead = squadSubLead.from(Squad.class);
			Root<Squad> squadAss = squadSubAss.from(Squad.class);
			Path<Member> memb = tr.get(Booking_.member);
			Join<Member,Squad> trupp = tr.join(Booking_.member).join(Member_.trupp, JoinType.LEFT);

			tcq.multiselect(tr, 
					tr.get(Booking_.activity), 
					memb, 
					trupp, 
					cb.exists(squadSubLead.select(squadLead).where(cb.or(cb.equal(memb, squadLead.get(Squad_.leaderFemale)), cb.equal(memb,  squadLead.get(Squad_.leaderMale))))), 
					cb.exists(squadSubAss.select(squadAss).where(cb.isMember(memb, squadAss.get(Squad_.assistants)))))
			.where(tr.get(Booking_.id).in(list))
			.orderBy(createOrders(tr))
			.distinct(true);
			
			List<Tuple> res = entityManager.createQuery(tcq).getResultList();
			res.forEach(t -> t.get(0, Booking.class).getPayments().size());
			return res.stream().map(t -> new BookingUI(
					t.get(0, Booking.class), 
					t.get(1, Activity.class), 
					t.get(2, Member.class), 
					t.get(3, Squad.class), 
					t.get(0, Booking.class).getPayments(), 
					t.get(4, Boolean.class), 
					t.get(5, Boolean.class)))
					.collect(Collectors.toList());
		} 
		return Collections.emptyList();
	}
	
	@Override
	protected Predicate createFilterCriteriaForField(final String propertyName, final Object filterValue, CriteriaQuery<?> criteriaQuery) {
		if (!(filterValue instanceof String) || StringUtils.isBlank((String)filterValue)) {
			return null;
		}
		String val = filterValue.toString().toLowerCase();
		switch(propertyName) {
		case "member":
			return getSplittedPredicateName(root.join(Booking_.member), val);
		case "aktiv":
			return cb.equal(root.join(Booking_.member).get(Member_.aktiv), Boolean.parseBoolean(val));
		case "strasse":
			return cb.like(cb.lower(root.join(Booking_.member).get(Member_.strasse)), "%"+val+"%");
		case "ort":
			return cb.like(cb.lower(root.join(Booking_.member).get(Member_.ort)), "%"+val+"%");
		case "activity":
			return getSplittedPredicateName(root.join(Booking_.activity).get(Activity_.name), val);
		case "activityFinished":
			Predicate actFin = cb.or(cb.lessThan(root.join(Booking_.activity).get(Activity_.end), DateTime.now().minusMonths(1).toDate()),
					root.join(Booking_.activity).get(Activity_.status).in(ActivityStatus.cancelled, ActivityStatus.finished));
			switch(val) {
			case "false": 
				return cb.not(actFin);
			case "true":
				return actFin;
			default:
				return null;
			}
		case "squadName":
			return cb.like(cb.lower(root.join(Booking_.member).join(Member_.trupp).get(Squad_.name)), "%"+val+"%");
		case "status":
			BookingStatus bookingStatus = getBookingStatus(val);
			if (bookingStatus != null) {
				return cb.equal(root.get(Booking_.status), bookingStatus);
			}
			return null;
		case "payed":
			Subquery<Booking> sq = criteriaQuery.subquery(entityClass);
			Root<Booking> corr = sq.correlate(root);
			Join<Booking,Payment> jpb = corr.join(Booking_.payments);
			Subquery<Booking> sqm = criteriaQuery.subquery(entityClass);
			Join<Booking,Member> memb = sqm.correlate(root).join(Booking_.member);
			sqm.where(
				cb.and( // Membership: members that do not have to pay fee
					cb.equal(root.get(Booking_.activity).get(Activity_.type), ActivityType.Membership),
					cb.or(
							cb.equal(memb.get(Member_.free), true),
							cb.equal(memb.join(Member_.funktionen, JoinType.LEFT).get(Function_.free), true),
							memb.get(Member_.id).in(squadRepo.findLeaderIds())
					)
				)
			);
			
			if ("free".equalsIgnoreCase(val)) {
				return cb.exists(sqm);
			} else if ("true".equalsIgnoreCase(val)) {
				sq.where(cb.equal(jpb.get(Payment_.finished), true)); 
				return cb.exists(sq);
			} else if ("false".equalsIgnoreCase(val)) {
				sq.where(cb.equal(jpb.get(Payment_.finished), true));
				return cb.and(
						cb.not(cb.exists(sqm)), // Member.free or exist Payment.finished
						cb.not(cb.exists(sq))
					);
			} else if ("none".equalsIgnoreCase(val)) {
				return cb.not(cb.exists(sq));
			}
			
			// "anz"
			sq.where(cb.equal(jpb.get(Payment_.finished), true)); 
			Subquery<Booking> sqAnz = criteriaQuery.subquery(entityClass);
			sqAnz.where(cb.equal(sqAnz.correlate(root).join(Booking_.payments).get(Payment_.aconto), true));
			return cb.and(cb.not(cb.exists(sq)), cb.exists(sqAnz));
		}
		
		return super.createFilterCriteriaForField(propertyName, filterValue, criteriaQuery);
	}

	private BookingStatus getBookingStatus(final String val) {
		for (BookingStatus bs : BookingStatus.values()) {
			if (bs.name().toLowerCase().contains(val.toLowerCase())) {
				return bs;
			}
		}
		return null;
	}
	
	@Override
	protected Path getPathForOrder(Root<Booking> path, String propertyName) {
		switch(propertyName) {
		case "member":
			return path.get(Booking_.member).get(Member_.name);
		case "aktiv":
			return path.get(Booking_.member).get(Member_.aktiv);
		case "strasse":
			return path.get(Booking_.member).get(Member_.strasse);
		case "ort":
			return path.get(Booking_.member).get(Member_.ort);
		case "activity":
			return path.get(Booking_.activity).get(Activity_.name);
		case "squadName":
			return path.get(Booking_.member).get(Member_.trupp).get(Squad_.name);
//		case "payed":
//			return path.join(Booking_.payments).get(Payment_.finished);
		}
		return super.getPathForOrder(path, propertyName);
	}
}
