package at.tfr.pfad.svc;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import at.tfr.pfad.dao.PaymentRepository;
import at.tfr.pfad.model.Payment;

@Stateless
public class PaymentService {

	@Inject
	private PaymentMapper pm;
	@Inject
	private PaymentRepository paymentRepo;
	
	public PaymentDao map(Payment Payment) {
		return pm.paymentToDao(paymentRepo.findBy(Payment.getId()));
	}

	public List<PaymentDao> map(Collection<Payment> Payments) { 
		return Payments.stream().map(b -> map(b)).collect(Collectors.toList());
	}
}
