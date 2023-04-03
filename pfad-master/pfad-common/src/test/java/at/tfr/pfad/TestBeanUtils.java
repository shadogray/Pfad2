package at.tfr.pfad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import at.tfr.pfad.action.RegistrationHandlerBean;
import at.tfr.pfad.dao.Beans;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Registration;
import at.tfr.pfad.svc.MemberDao;

public class TestBeanUtils {

	@Test
	public void testBeanCopy() throws Exception {
		
		Member orig = new Member();
		orig.setName("Name");
		orig.addParent(new Member());

		MemberDao clone = Beans.copyProperties(orig, new MemberDao());
		assertEquals("copy failed", orig.getName(), clone.getName());
	}
	
	@Test
	public void testRegistrationBean() throws Exception {
		
		Registration reg = new Registration();
		RegistrationHandlerBean rhb = new RegistrationHandlerBean();
		final Integer check = 1234;
		String test = ""+check;
		assertEquals(check, rhb.asInteger("test", test, reg));
		
		test = ""+check+" oder 4321";
		assertEquals(check, rhb.asInteger("test", test, reg));
		assertNotNull("no comment on failure", reg.getComment());
		
		//reg.setComment(null);
		test = "eventuell "+check+" oder 4321";
		assertEquals(check, rhb.asInteger("test", test, reg));
		assertNotNull("no comment on failure", reg.getComment());
	}
}
