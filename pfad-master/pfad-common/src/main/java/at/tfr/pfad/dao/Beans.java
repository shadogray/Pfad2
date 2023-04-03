package at.tfr.pfad.dao;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;

import at.tfr.pfad.model.Booking;
import at.tfr.pfad.model.Function;
import at.tfr.pfad.model.Payment;
import at.tfr.pfad.svc.MemberDao;

public class Beans {
	
	static {
		ConvertUtilsBean cu = BeanUtilsBean.getInstance().getConvertUtils();
		cu.register(new PfadConverter(), MemberDao.class);
		cu.register(new PfadConverter(), Payment.class);
		cu.register(new PfadConverter(), Booking.class);
		cu.register(new PfadConverter(), Function.class);
	}

	public static <T> T copyProperties(Object source, T dest) {
		try {
			BeanUtils.copyProperties(dest, source);
			return dest;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
