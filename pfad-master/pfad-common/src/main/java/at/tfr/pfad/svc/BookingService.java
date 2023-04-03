package at.tfr.pfad.svc;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import at.tfr.pfad.dao.BookingRepository;
import at.tfr.pfad.model.Booking;

@Stateless
public class BookingService {

	@Inject
	private BookingMapper bm;
	@Inject
	private BookingRepository bookingRepo;
	
	public BookingDao map(Booking booking) {
		return bm.bookingToDao(bookingRepo.findBy(booking.getId()));
	}

	public List<BookingDao> map(Collection<Booking> bookings) { 
		return bookings.stream().map(b -> map(b)).collect(Collectors.toList());
	}
}
