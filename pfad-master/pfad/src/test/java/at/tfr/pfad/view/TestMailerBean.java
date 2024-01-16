package at.tfr.pfad.view;

import java.util.Properties;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

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
