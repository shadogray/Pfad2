package at.tfr.pfad.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.mail.MessagingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.logging.Logger;

import at.tfr.pfad.dao.MailMessageRepository;
import at.tfr.pfad.model.MailMessage;
import at.tfr.pfad.util.SessionBean;
import at.tfr.pfad.view.MailerBean.MailConfig;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class SmsSender {

	private Logger log = Logger.getLogger(getClass());
	public static final String SMS_PARAM = "sms_param_";

	@Inject
	private MailMessageRepository messageRepo;
	@Inject
	private SessionBean sessionBean;

	public MailMessage sendMail(MailMessage msg, MailConfig config, boolean saveText)
			throws MessagingException, ClientProtocolException, IOException, CloneNotSupportedException {

		List<BasicNameValuePair> configParams = new ArrayList<>();
		sessionBean.getConfig().stream()
			.filter(c -> c.getCkey().startsWith(SMS_PARAM))
			.forEach(c -> configParams.add(
					new BasicNameValuePair(c.getCkey().replace(SMS_PARAM,""), c.getCvalue())));
		
		msg.setSms(true);
		MailMessage msgOrig = msg;
		msg = msg.getClone();
		if (!saveText) {
			msg.setText(null);
		} else {
			msg.setText(msg.getPlainText());
		}

		msg = messageRepo.saveAndFlush(msg);
		try {

			CredentialsProvider creds = new BasicCredentialsProvider();
			creds.setCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials(config.getSmsUsername(), config.getSmsPassword()));
			CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(creds).build();

			try {
				HttpPost httpPost = new HttpPost(config.getSmsService());
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				
				String receivers = msgOrig.getReceiver();
				if (StringUtils.isNotBlank(msgOrig.getCc())) receivers += ","+msgOrig.getCc();
				
				String msgtext = msgOrig.getPlainText().replaceAll("\t","  ");
				
				nvps.addAll(configParams);
				nvps.add(new BasicNameValuePair("recipients", receivers));
				nvps.add(new BasicNameValuePair("msgtext", msgtext));
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
				CloseableHttpResponse response = httpclient.execute(httpPost);

				try {
					log.info(response.getStatusLine());
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						log.info("sent SMS: " + IOUtils.toString(response.getEntity().getContent()));
					} else {
						log.info("failed to send SMS: " + IOUtils.toString(response.getEntity().getContent()));
						throw new MessagingException(
								"failed to send SMS for: " + msg.getReceiver() + " : " + response.getStatusLine());
					}
				} finally {
					response.close();
				}
			} finally {
				httpclient.close();
			}

			msg.setCreated(new Date());
			return messageRepo.saveAndFlushAndRefresh(msg);

		} catch (Exception e) {
			log.warn("cannot send: " + msg + " : " + e);
			messageRepo.removeAndFlush(msg);
			throw e;
		}
	}
}
