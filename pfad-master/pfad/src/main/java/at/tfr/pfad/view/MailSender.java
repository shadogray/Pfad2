package at.tfr.pfad.view;

import java.util.Date;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;

import org.jboss.logging.Logger;

import at.tfr.pfad.dao.MailMessageRepository;
import at.tfr.pfad.model.MailMessage;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class MailSender {

	private Logger log = Logger.getLogger(getClass());
	
	@Inject
	private MailMessageRepository messageRepo;

	public MailMessage sendMail(Message mail, MailMessage msg, boolean saveText) throws MessagingException, CloneNotSupportedException {

		msg = msg.getClone();
		if (!saveText) {
			msg.setText(null);
		} else {
			msg.setText(msg.getPlainText());
		}
		msg = messageRepo.saveAndFlush(msg);
		try {

			Transport.send(mail);
			log.info("sent " + msg);
			
			msg.setCreated(new Date());
			return messageRepo.saveAndFlushAndRefresh(msg);
			
		} catch (MessagingException e) {
			log.warn("cannot send: " + msg + " : " + e);
			messageRepo.removeAndFlush(msg);
			throw e;
		}
	}
	
}
