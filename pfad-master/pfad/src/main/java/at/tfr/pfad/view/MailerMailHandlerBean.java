package at.tfr.pfad.view;

import at.tfr.pfad.model.MailMessage;
import at.tfr.pfad.model.MailTemplate;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.enterprise.concurrent.Asynchronous;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Default
public class MailerMailHandlerBean {

    protected Logger log = Logger.getLogger(getClass());

    @Inject
    private MailerServiceBean mailerServiceBean;
    @Inject
    private Instance<MailSender> mailSender;
    @Inject
    private Instance<SmsSender> smsSender;
    private boolean sending;
    private final List<String> errorMessages = new ArrayList<>();
    private final List<String> warnMessages = new ArrayList<>();
    private boolean stopRequest;

    @Asynchronous
    public CompletableFuture<Integer> sendMessages(List<MailMessage> mailMessages, MailTemplate mailTemplate, MailerBean.MailConfig mailConfig,
                                                          Map<String, MailerBean.UpFile> files, boolean testOnly, String testTo, String currentUser) throws Exception {
        if (sending) {
            return Asynchronous.Result.complete(0);
        }
        stopRequest = false;
        sending = true;
        errorMessages.clear();
        warnMessages.clear();

        int messageCount = 0;
        if (mailConfig == null) {
            error("Cannot execute Template for empty MailConfiguration!");
            return Asynchronous.Result.complete(0);
        }
        if (mailConfig.getFrom() == null) {
            error("Cannot execute Template for empty FROM address in MailConfiguration!");
            return Asynchronous.Result.complete(0);
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
                if (stopRequest) {
                    break;
                }

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
                        int addrCount = mail.getAllRecipients().length;
                        for (int i=0; i<10; i++) {
                            int jiffies = mailerServiceBean.jiffies(mailConfig.getKey());
                            if (jiffies < addrCount) {
                                Thread.sleep(1000); // wait for 1 second
                            } else {
                                break;
                            }
                        }
                        mailerServiceBean.take(mailConfig.getKey(), addrCount);
                        sentMsg = mailSender.get().sendMail(mail, msg, mailTemplate.isSaveText());
                        messageCount++;
                    } else {
                        sentMsg = smsSender.get().sendMail(msg, mailConfig, mailTemplate.isSaveText());
                        messageCount++;
                    }
                    msg.setSend(false);
                    msg.setCreated(sentMsg.getCreated());

                } catch (Throwable me) {
                    log.info("send failed: " + me + " msg: " + msg, me);
                    warn("send failed: " + me + " to: " + msg.getReceiver());
                }

                if (testOnly) {
                    break;
                }

            }

        } catch (Exception e) {
            log.warn("Cannot send messages: " + mailTemplate + " : " + e, e);
            throw e;
        } finally {
            sending = false;
        }
        return Asynchronous.Result.complete(messageCount);
    }

    public void addAddresses(MimeMessage mail, String receivers, Message.RecipientType type)
            throws MessagingException, AddressException {
        if (receivers != null) {
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
    }

    private void error(String message) {
        log.error(message);
        errorMessages.add(message);
    }

    private void warn(String message) {
        log.warn(message);
        warnMessages.add(message);
    }

    public boolean isSending() {
        return sending;
    }

    public void setStopRequest(boolean stopRequest) {
        this.stopRequest = stopRequest;
    }

    public boolean isStopRequest() {
        return stopRequest;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public List<String> getWarnMessages() {
        return warnMessages;
    }

    public List<String> getMessages() {
        var all = new ArrayList<>(errorMessages);
        all.addAll(warnMessages);
        return all;
    }
}
