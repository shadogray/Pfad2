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
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import at.tfr.pfad.model.Activity_;
import at.tfr.pfad.model.Booking;
import at.tfr.pfad.model.Booking_;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Member_;
import at.tfr.pfad.model.Payment;
import at.tfr.pfad.model.Payment_;
import at.tfr.pfad.model.Squad_;

@Stateful
public class PaymentDataModel extends BaseDataModel<Payment, PaymentUI> {

	public PaymentDataModel() {
		uiClass = PaymentUI.class;
		entityClass = Payment.class;
	}

	public PaymentDataModel(Class<PaymentUI> uiClass, Class<Payment> entityClass) {
		super(uiClass, entityClass);
	}

	@Override
	protected Path[] getGroupByRoots() {
		return new Path[] { root };
	}
	
//	Fetch<Payment,Booking> bookJoin;
	Join<Payment,Member> payerJoin;

	@Override
	public List<PaymentUI> convertToUiBean(List<Long> ids) {
		if (!ids.isEmpty()) {
			CriteriaQuery<Payment> cq = cb.createQuery(Payment.class);
			Root<Payment> pr = cq.from(Payment.class);
			pr.alias("payment");
			payerJoin = pr.join(Payment_.payer, JoinType.LEFT);
			payerJoin.alias("payer");
			payerJoin.join(Member_.funktionen, JoinType.LEFT);
//			bookJoin = pr.fetch(Payment_.bookings, JoinType.LEFT);
//			pr.fetch(Payment_.bookings, JoinType.LEFT);

			//cq.multiselect(pr, payerJoin);
			
			cq.where(pr.in(ids));
			cq.orderBy(createOrders(pr));
			cq.distinct(true);
			
			List<Payment> res = entityManager.createQuery(cq).getResultList();
			
			CriteriaQuery<Tuple> bc = cb.createTupleQuery();
			Root<Booking> bRoot = bc.from(Booking.class);
			Join<Booking, Member> mj = bRoot.join(Booking_.member);
			mj.fetch(Member_.funktionen, JoinType.LEFT);
			mj.fetch(Member_.trupp, JoinType.LEFT);
			entityManager.createQuery(
					bc.where(bRoot.join(Booking_.payments).get(Payment_.id).in(ids))
					.multiselect(bRoot, mj)
					).getResultList();
			
			return res.stream().map(p->new PaymentUI(p, p.getPayer(), p.getBookings())).collect(Collectors.toList());
			//return res.stream().map(t->new PaymentUI(t.get(0, Payment.class), t.get(1,Member.class), t.get(0, Payment.class).getBookings())).collect(Collectors.toList());
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
		case "payer":
			return getSplittedPredicateName(root.join(Payment_.payer), val);
		
		case "member":
			return getSplittedPredicateName(root.join(Payment_.bookings).join(Booking_.member), val);
		
		case "squad":
			return cb.like(cb.lower(root.join(Payment_.bookings).join(Booking_.member).join(Member_.trupp).get(Squad_.name)), "%"+val+"%");

		case "activity":
			return getSplittedPredicateName(root.join(Payment_.bookings).join(Booking_.activity).get(Activity_.name), val);
		}
		return super.createFilterCriteriaForField(propertyName, val, criteriaQuery);
	}

	@Override
	protected Path getPathForOrder(Root<Payment> path, String propertyName) {
		switch(propertyName) {
		case "payer":
			return payerJoin.get(Member_.name);
		
//		case "member":
//			return memberJoin.get(Member_.name);
//		
//		case "squad":
//			return squadJoin.get(Squad_.name);
//
//		case "activity":
//			return bookJoin.join(Booking_.activity).get(Activity_.name);
		}
		return super.getPathForOrder(path, propertyName);
	}
}
