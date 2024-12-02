package at.tfr.pfad.view;

import at.tfr.pfad.model.MailMessage;
import at.tfr.pfad.model.MailTemplate;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.ejb.AccessTimeout;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateful;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Stateful
public class MailerMailHandlerBean extends BaseBean<MailMessage> {

    @Inject
    private Instance<MailSender> mailSender;
    @Inject
    private Instance<SmsSender> smsSender;
    private boolean sending;

    @AccessTimeout(value = 60, unit = TimeUnit.MINUTES)
    @Asynchronous
    public void sendMessages(List<MailMessage> mailMessages, MailTemplate mailTemplate, MailerBean.MailConfig mailConfig,
                                Map<String, MailerBean.UpFile> files, boolean testOnly, String testTo, String currentUser) throws Exception {
        sending = true;
        if (mailConfig == null) {
            error("Cannot execute Template for empty MailConfiguration!");
            return;
        }
        if (mailConfig.getFrom() == null) {
            error("Cannot execute Template for empty FROM address in MailConfiguration!");
            return;
        }

        try {

            final boolean saveTemplate = mailTemplate.getId() != null;

            Session session = Session.getInstance(mailConfig.getProperties(), new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailConfig.getUsername(), mailConfig.getPassword());
                }
            });
            InternetAddress sender = new InternetAddress(mailConfig.getFrom());
            if (mailConfig.getAlias() != null) {
                sender.setPersonal(mailConfig.getAlias());
            }

            for (MailMessage msg : mailMessages) {
                if (!msg.isSend()) {
                    log.info("not sending: " + msg);
                    continue;
                }

                try {
                    if (StringUtils.isBlank(msg.getReceiver()) || "null".equals(msg.getReceiver())) {
                        warn("invalid Receiver: " + msg);
                        continue;
                    }
                    if (!mailTemplate.isSms()) {
                        try {
                            InternetAddress[] ia = InternetAddress.parse(msg.getReceiver());
                        } catch (Exception e) {
                            warn("invalid Receiver: "+e.getMessage()+ " : " + msg);
                            continue;
                        }
                        if (StringUtils.isBlank(msg.getSubject())) {
                            warn("empty Subject: " + msg);
                            continue;
                        }
                    }
                    if (StringUtils.isBlank(msg.getText())) {
                        warn("empty MessageText: " + msg);
                        continue;
                    }

                    MimeMessage mail = new MimeMessage(session);
                    mail.setFrom(sender);
                    mail.setSubject(msg.getSubject());

                    MimeMultipart mixed = new MimeMultipart("mixed");

                    MimeMultipart alternative = new MimeMultipart("alternative");

                    if (Boolean.TRUE.equals(mailTemplate.getAlternativeText())) {
                        MimeBodyPart body = new MimeBodyPart();
                        body.setContent(msg.getPlainText(), "text/plain; charset=UTF-8");
                        alternative.addBodyPart(body);
                    }

                    MimeBodyPart htmlBody = new MimeBodyPart();
                    htmlBody.setContent(msg.getText(), "text/html; charset=UTF-8");
                    alternative.addBodyPart(htmlBody);
                    MimeBodyPart alternativeBody = new MimeBodyPart();
                    alternativeBody.setContent(alternative);

                    if (false) { /// if embedded images are supported
                        MimeMultipart related = new MimeMultipart();
                        MimeBodyPart relatedBody = new MimeBodyPart();
                        relatedBody.setContent(alternative);
                        related.addBodyPart(relatedBody);
                        //for each image:
                        //  MimeBodyPart imgBody = new MimeBodyPart();
                        //  imgBody.setContent(image);
                        //	related.addBodyPart(imgBody);
                        relatedBody.setContent(related);
                        mixed.addBodyPart(relatedBody);
                    } else {
                        mixed.addBodyPart(alternativeBody);
                    }

                    for (Map.Entry<String, MailerBean.UpFile> fup : files.entrySet()) {
                        MimeBodyPart filePart = new MimeBodyPart();
                        filePart.setDisposition(MimePart.ATTACHMENT);
                        filePart.setFileName(fup.getKey());
                        filePart.setDataHandler(new DataHandler(new FileDataSource(fup.getValue().content.toFile())));
                        mixed.addBodyPart(filePart);
                    }

                    mail.setContent(mixed);

                    Message.RecipientType to = Message.RecipientType.TO;
                    if (testOnly) {
                        msg = msg.getClone();
                        if (!mailTemplate.isSms()) {
                            addAddresses(mail, testTo, to);
                            msg.setReceiver(testTo);
                            if (mailTemplate.isCc())
                                msg.setCc(testTo);
                        } else {
                            msg.setReceiver(testTo);
                            if (mailTemplate.isCc())
                                msg.setCc(testTo);
                        }
                    } else {
                        addAddresses(mail, msg.getReceiver(), to);
                        if (mailTemplate.isCc() && StringUtils.isNotBlank(msg.getCc())) {
                            addAddresses(mail, msg.getCc(), Message.RecipientType.CC);
                        }
                        if (mailTemplate.isBcc() && StringUtils.isNotBlank(msg.getBcc())) {
                            addAddresses(mail, msg.getBcc(), Message.RecipientType.BCC);
                        }
                    }


                    if (saveTemplate) {
                        msg.setTemplate(mailTemplate);
                    } else {
                        msg.setTemplate(null);
                    }
                    msg.setSender(sender.getAddress()
                            + (StringUtils.isNotBlank(sender.getPersonal()) ? ":" + sender.getPersonal() : ""));
                    msg.setTest(testOnly);
                    msg.setCreatedBy(currentUser);

                    MailMessage sentMsg;
                    if (!mailTemplate.isSms()) {
                        sentMsg = mailSender.get().sendMail(mail, msg, mailTemplate.isSaveText());
                    } else {
                        sentMsg = smsSender.get().sendMail(msg, mailConfig, mailTemplate.isSaveText());
                    }
                    msg.setSend(false);
                    msg.setCreated(sentMsg.getCreated());

                    if (testOnly) {
                        break;
                    }

                } catch (MessagingException me) {
                    log.info("send failed: " + me + " msg: " + msg, me);
                    warn("send failed: " + me + " to: " + msg.getReceiver());
                }
            }

        } catch (Exception e) {
            log.warn("Cannot send messages: " + mailTemplate + " : " + e, e);
            throw e;
        } finally {
            sending = false;
        }
    }

    public void addAddresses(MimeMessage mail, String receivers, Message.RecipientType type)
            throws MessagingException, AddressException {
        Arrays.stream(receivers.split("[,; ]+")).forEach(r -> {
            try {
                if (StringUtils.isNotBlank(r)) {
                    mail.addRecipients(type, InternetAddress.parse(r));
                }
            } catch (Exception e) {
                log.info("cannot convert to Address: " + r + " : " + e);
            }
        });
    }

    public boolean isSending() {
        return sending;
    }

    @Override
    public String update() {
        return "";
    }

    @Override
    public boolean isUpdateAllowed() {
        return false;
    }

    @Override
    public void retrieve() {
    }
}
