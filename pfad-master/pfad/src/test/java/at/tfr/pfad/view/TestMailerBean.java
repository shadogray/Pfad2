package at.tfr.pfad.view;

import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

public class TestMailerBean {

	@Test(expected = AddressException.class)
	public void testMailAddresses() throws Exception {
		String addr = "test@test@test.at;";
		MimeMessage mail = new MimeMessage(Session.getDefaultInstance(new Properties()));
		mail.setRecipients(RecipientType.TO, InternetAddress.parse(addr));
	}
	
	@Test
	public void testMailerBeanMailAddresses() throws Exception {
		String addr = "test@test@test.at;";
		MimeMessage mail = new MimeMessage(Session.getDefaultInstance(new Properties()));
		MailerBean mailerBean = new MailerBean();
		mailerBean.addAddresses(mail, addr, RecipientType.TO);
	}
	
	
}
