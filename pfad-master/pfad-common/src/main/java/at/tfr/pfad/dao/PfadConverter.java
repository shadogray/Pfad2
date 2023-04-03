package at.tfr.pfad.dao;

import org.apache.commons.beanutils.Converter;

import at.tfr.pfad.model.Booking;
import at.tfr.pfad.model.Function;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Payment;
import at.tfr.pfad.svc.MemberDao;

public class PfadConverter implements Converter {

	@Override
	public <T> T convert(Class<T> type, Object value) {
		if (value instanceof Member)
			return type.cast(Beans.copyProperties(value, new MemberDao()));
		if (value instanceof Payment) {
			Payment p = new Payment();
			p.setId(((Payment)value).getId());
			return type.cast(p);
		}
		if (value instanceof Booking) {
			Booking b = new Booking();
			b.setId(((Booking)value).getId());
			return type.cast(b);
		}
		if (type.equals(Function.class)) {
			Function f = new Function();
			f.setId(((Function)value).getId());
			return type.cast(f);
		}
		return type.cast(value);
	}

}
