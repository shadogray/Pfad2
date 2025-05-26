package at.tfr.pfad.view;

import at.tfr.pfad.dao.MailTemplateRepository;
import at.tfr.pfad.model.*;
import at.tfr.pfad.util.ColumnModel;
import at.tfr.pfad.util.ExecutorContext;
import at.tfr.pfad.util.QueryExecutor;
import at.tfr.pfad.util.TemplateUtils;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.AccessTimeout;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.Stateful;
import jakarta.enterprise.inject.Instance;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.model.ListDataModel;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.mail.internet.InternetAddress;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named
@ViewScoped
@Stateful
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class MailerBean extends BaseBean<MailMessage,MailMessage> {

	@Inject
	private Instance<QueryExecutor> queryExec;
	@Inject
	private MailTemplateRepository templateRepo;
	@Inject
	private MailTemplateBean mailTemplateBean;
	@Inject
	private Instance<MailerMailHandlerBean> mailerMailHandlerBeans;
	@Inject
	private Instance<MailerServiceBean> mailerServiceBeans;
	private MailerMailHandlerBean mailerMailHandlerBean;
	private MailConfig mailConfig;
	private String mailConfigKey;
	private String testTo;
	private MailTemplate mailTemplate = new MailTemplate();
	private Activity activity;
	private final List<ColumnModel> columns = new ArrayList<>();
	private final List<String> columnHeaders = new ArrayList<>();
	private final Pattern phoneNumber = Pattern.compile("[+\\d]{7,}");
	private final Pattern phoneNumberFilter = Pattern.compile("[ /()-]");
	private transient Map<String, MailConfig> mailConfigs;
	private transient Future<List<List<Entry<String, Object>>>> valuesFuture;
	private transient List<List<Entry<String, Object>>> values = Collections.emptyList();
	private transient List<MailMessage> mailMessages = Collections.emptyList();
	private transient ListDataModel<List<Entry<String, Object>>> valuesModel = new ListDataModel<>();
	private transient ListDataModel<MailMessage> mailMessagesModel = new ListDataModel<>();
	private transient Future<Integer> mailSendingFuture;
	private boolean mailMessagesModelLoaded;
	private transient final Map<String,UpFile> files = new LinkedHashMap<>();

	public enum MailProps {
		mail_transport_protocol, mail_smtp_starttls_enable, mail_smtp_auth, mail_smtp_host, mail_smtp_port, mail_smtps_auth, mail_smtps_host, mail_smtps_port, mail_smtp_ssl_enable, mail_smtp_socketFactory_class, mail_smtp_socketFactory_port
	}

	@PostConstruct
	public void init() {
		mailConfigs = MailConfig.generateConfigs(sessionBean.getConfig(), log.isDebugEnabled()).entrySet().stream()
				.filter(mc -> StringUtils.isNotBlank(mc.getValue().getPassword()))
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		mailerServiceBeans.get().setMailQueues(mailConfigs);
		Entry<String,MailConfig> myMc = mailConfigs.entrySet().stream()
				.filter(mc -> mc.getKey().toLowerCase().startsWith(sessionBean.getUser().getName())).findFirst().orElse(null);
		if (myMc == null) {
			myMc = mailConfigs.entrySet().stream().findFirst().orElse(null);
		}
		if (myMc != null)
			mailConfigKey = myMc.getKey();
		setMailConfigKey(mailConfigKey);
		if (mailConfigs.isEmpty()) {
			warn("No MailConfiguration found! Cannot execute any Mails.");
		}
	}

	@Override
	public String update() {
		return null;
	}

	public void reinit() {
	}

	@AccessTimeout(unit = TimeUnit.SECONDS, value = 30)
	public void executeQuery() {
		mailMessagesModelLoaded = false;
		values = Collections.emptyList();
		mailMessages = Collections.emptyList();
		valuesModel = new ListDataModel<>();
		mailMessagesModel = new ListDataModel<>();

		if (mailConfig == null) {
			error("Cannot execute Template for empty MailConfiguration!");
			return;
		}
		try {
			String query = mailTemplate.getQuery();
			boolean isNative = Boolean.TRUE.equals(mailTemplate.getSql());
			if (query != null) {
				if (query.startsWith("Configuration:")) {
					Optional<Configuration> optConf = sessionBean.getConfig(query.replace("Configuration:", ""));
					if (optConf.isPresent()) {
						query = optConf.get().getCvalue();
						isNative = optConf.get().isNative();
					}
				}
				valuesFuture = queryExec.get().execute(query
								.replaceAll("\\$\\{templateId\\}", "" + mailTemplate.getId())
								.replaceAll("\\$\\{templateName\\}", mailTemplate.getName())
								.replaceAll("\\$\\{templateOwner\\}", mailTemplate.getOwner())
								.replaceAll("\\$\\{user\\}", sessionBean.getUser().getName())
								.replaceAll("\\$\\{role\\}", sessionBean.getRole().name())
								.replaceAll("\\$\\{activityId\\}", activity != null ? activity.getIdStr() : "null")
								.replaceAll("\\$\\{subject\\}", mailTemplate.getSubject()),
						isNative, new ExecutorContext(sessionBean));
			} else {
				warn("No Query defined!");
			}
		} catch (Exception e) {
			log.warn("Cannot execute: " + mailTemplate + e, e);
			error("Cannot execute: " + mailTemplate + e);
		}
		
	}

	public void loadModels() {
		if (!mailMessagesModelLoaded && valuesFuture != null && valuesFuture.isDone()) {
			try {
				try {
					values = valuesFuture.get();
				} catch (ExecutionException e) {
					valuesFuture = null;
					throw e;
				}
				List<String> realColHeaders = Collections.emptyList();
				columnHeaders.clear();
				columns.clear();
				mailMessages = new ArrayList<>();
				if (values.size() > 0) {
					realColHeaders = values.get(0).stream().map(Entry::getKey).collect(Collectors.toList());
					columnHeaders.addAll(realColHeaders);
					for (int i=0; i<columnHeaders.size(); i++)
						columns.add(new ColumnModel(columnHeaders.get(i), columnHeaders.get(i), i));
				}

				if (values.size() > 0) {
					List<String> lcHeadrs = realColHeaders.stream().map(String::toLowerCase).collect(Collectors.toList());
					final int toIdx = lcHeadrs.indexOf("to");
					final int ccIdx = lcHeadrs.indexOf("cc");
					if  (toIdx >= 0 && ccIdx >= 0) {
						Map<String, List<List<Entry<String, Object>>>> groups = values.stream()
								.filter(v -> StringUtils.isNotBlank((String)v.get(toIdx).getValue()))
								.collect(Collectors.groupingBy(v -> (String)v.get(toIdx).getValue(),
										LinkedHashMap::new, Collectors.toList()));
						groups.entrySet().forEach(e -> {
							String join = e.getValue().stream()
									.map(line -> (String)line.get(ccIdx).getValue())
									.filter(StringUtils::isNotBlank)
									.filter(v -> !v.equalsIgnoreCase(e.getKey()) && !e.getKey().contains(v))
									.distinct()
									.collect(Collectors.joining(","));
							e.getValue().get(0).get(ccIdx).setValue(join);
						});
						values = groups.entrySet().stream().map(e -> e.getValue().get(0)).collect(Collectors.toList());
					}
				}

				valuesModel = new ListDataModel<>(values);

				Map<String,Object> beans = new HashMap<>();
				beans.put("sb", sessionBean);
				beans.put("mt", mailTemplate);
				beans.put("activity", activity);

				for (List<Entry<String, Object>> vals : values) {

					vals.addAll(beans.entrySet());

					MailMessage msg = new MailMessage();
					msg.setValues(vals);
					msg.setTemplate(mailTemplate);
					String text = mailTemplate.getText();
					if (text != null) {
						text = templateUtils.replace(mailTemplate.getText(), vals);
						text = text.replaceAll("<p style=\"", "<p style=\"margin:0; ");
						text = text.replaceAll("<p>", "<p style='margin:0;'>");
					}
					msg.setText(text);
					msg.setReceiver(templateUtils.replace("${to}", vals));
					msg.setPlainText(TemplateUtils.htmlToText(msg.getText()));

					if (mailTemplate.isSms()) {
						msg.setReceiver(msg.getReceiver().replaceAll(phoneNumberFilter.pattern(),""));
					}
					if (mailTemplate.isCc()) {
						String ccVal = templateUtils.replace("${cc}", vals, mailConfig.getCc());
						if (StringUtils.isNotBlank(ccVal)) {
							msg.setCc(ccVal);
						}
					}
					if (mailTemplate.isBcc()) {
						String bccVal = templateUtils.replace("${bcc}", vals, mailConfig.getBcc());
						if (StringUtils.isNotBlank(bccVal)) {
							msg.setBcc(bccVal);
						}
					}

					msg.setSubject(templateUtils.replace(mailTemplate.getSubject(), msg.getValues()));
					msg.setMember(vals.stream().filter(e -> e.getValue() instanceof Member)
							.map(e -> (Member) e.getValue()).findFirst().orElse(null));
					msg.setRegistration(vals.stream().filter(e -> e.getValue() instanceof Registration)
							.map(e -> (Registration) e.getValue()).findFirst().orElse(null));
					mailMessages.add(msg);
				}

				// Validate message addresses:
				validate(mailMessages, mailTemplate.isSms());

				mailMessagesModel = new ListDataModel<>(mailMessages);
				mailMessagesModelLoaded = true;
			} catch (Exception e) {
				error("Cannot load QueryData: " + e);
			}
		}
	}

	public void validate(List<MailMessage> mailMessgs, boolean isSms) {
		List<String> invalidAddrs = new ArrayList<>();
		for (MailMessage msg : mailMessgs) {
			if (!isSms) {
				validate(invalidAddrs, msg.getReceiver());
				validate(invalidAddrs, msg.getCc());
				validate(invalidAddrs, msg.getBcc());
			} else {
				Stream.of(msg.getReceiver().split(" *, *"))
				.forEach(r -> {
						if (!phoneNumber.matcher(r).matches()) {
							invalidAddrs.add(msg.getReceiver());
						}
				});
			}
		}
		if (invalidAddrs.size() > 0) {
			warn("Ungültige Addressen: "+invalidAddrs);
		}
	}

	public void validate(List<String> invalidAddrs, String mailAddr) {
		try {
			if (StringUtils.isNotBlank(mailAddr))
				InternetAddress.parse(mailAddr.replaceAll(";", ","));
		} catch (Exception e) {
			invalidAddrs.add(mailAddr);
		}
	}

	public void saveTemplate() {
		try {
			mailTemplate = templateRepo.saveAndFlush(mailTemplate);
		} catch (Exception e) {
			log.warn("Cannot save: " + mailTemplate + " : " + e, e);
			error("Cannot save: " + e);
		}
	}

	public void copyTemplate() {
		try {
			if (StringUtils.isBlank(mailTemplate.getName())) {
				throw new InvalidParameterException("TemplateName darf nicht leer sein!");
			}
			MailTemplate expl = new MailTemplate();
			expl.setName(mailTemplate.getName());
			List<MailTemplate> existing = templateRepo.findBy(expl, MailTemplate_.name);
			if (!existing.isEmpty()) {
				throw new InvalidParameterException("TemplateName existiert bereits: " + existing);
			}
			if (!isChangeOwnerAllowed()) {
				mailTemplate.setOwner(getCurrentOwner());
			}
			mailTemplate.setId(null);
			mailTemplate = templateRepo.saveAndFlush(mailTemplate);
		} catch (Exception e) {
			log.warn("Cannot save: " + mailTemplate + " : " + e, e);
			error("Cannot save: " + e);
		}
	}

	public boolean willSend(final int index) {
		if (index < 0 || index > mailMessages.size()-1)
			return false;
		return mailMessages.get(index).isSend();
	}
	
	public void toggleAllMessageSend() {
		mailMessages.forEach(m -> m.setSend(!m.isSend()));
	}

	public void toggleForIndex(final int index) {
		if (mailMessages.size() > index) {
			MailMessage m = mailMessages.get(index);
			m.setSend(!m.isSend());
		}
	}

	public void sendTestMessage() {
		sendMessages(true);
	}
	
	@AccessTimeout(unit = TimeUnit.MINUTES, value = 10)
	public void sendRealMessages() {
		sendMessages(false);
	}

	protected void sendMessages(boolean test) {
		mailSendingFuture = null;
		try {
			if (test && StringUtils.isBlank(testTo)) {
				throw new IllegalArgumentException("If Test testTo may not be empty!");
			}
			mailSendingFuture = getMailerMailHandlerBean().sendMessages(mailMessages, mailTemplate, mailConfig, files, test, testTo, sessionBean.getUser().getName());
		} catch (Exception e) {
			error("Cannot send Messages: " + e);
		}
	}

	public void stopSending() {
		if (mailerMailHandlerBean != null) {
			mailerMailHandlerBean.setStopRequest(true);
		}
	}

	public List<String> getSendingMessages() {
		if (mailerMailHandlerBean == null) {
			return Collections.emptyList();
		}
		return mailerMailHandlerBean.getMessages();
	}

	public boolean isInProgress() {
		return isQueryActive() || isSending();
	}

	public boolean isQueryActive() {
		return valuesFuture != null && !valuesFuture.isDone();
	}

	public boolean isSending() {
		return mailSendingFuture != null && !mailSendingFuture.isDone() && getMailerMailHandlerBean().isSending();
	}

	public MailerMailHandlerBean getMailerMailHandlerBean() {
		if (mailerMailHandlerBean != null){
			try {
				mailerMailHandlerBean.isSending();
				return mailerMailHandlerBean;
			} catch (Exception e) {
				log.info("cannot access mailHandler: " + e);
			}
		}
		mailerMailHandlerBean = mailerMailHandlerBeans.get();
		return mailerMailHandlerBean;
	}

	public boolean isChangeTemplateAllowed() {
		return isVorstand() || isAdmin();
	}

	public boolean isUpdateQueryAllowed() {
		return isUpdateAllowed();
	}

	public boolean isChangeOwnerAllowed() {
		return isVorstand() || isAdmin();
	}

	@Override
	public boolean isUpdateAllowed() {
		return isVorstand() || isAdmin() || ownerMatches(mailTemplate.getOwner(), getCurrentOwner());
	}

	private String getCurrentOwner() {
		return ""+getSessionContext().getCallerPrincipal();
	}

	boolean ownerMatches(String owner, String principal) {
		if (StringUtils.isEmpty(owner)) {
			return true;
		}
		if (principal == null)
			return false;
		try {
			return principal.matches(owner);
		} catch (Exception e) {
			log.info("cannot check: " + e);
		}
		return false;
	}
	
	public String getTestTo() {
		if (testTo == null && mailConfig != null) {
			if (StringUtils.isNotBlank(mailConfig.getTestTo())) 
				testTo = mailConfig.getTestTo();
			if (mailTemplate != null && mailTemplate.isSms()) {
				testTo =  mailConfig.getSmsTestTo();
			}
		}
		return testTo;
	}
	
	public void setTestTo(String testTo) {
		this.testTo = testTo;
	}
	
	public ListDataModel<List<Entry<String, Object>>> getValues() {
		return valuesModel;
	}

	public List<String> getColumnHeaders() {
		return columnHeaders;
	}
	
	public List<ColumnModel> getColumns() {
		return columns;
	}
	
	public List<String> getValueKeys() {
		List<String> keys = new ArrayList<>();
		if (values != null && !values.isEmpty()) {
			keys.addAll(values.get(0).stream().map(Entry::getKey).collect(Collectors.toList()));
		}
		return keys;
	}

	public Collection<String> getMailConfigKeys() {
		return mailConfigs.keySet();
	}
	
	public String getMailConfigKey() {
		return mailConfigKey;
	}
	
	public void setMailConfigKey(String mailConfigKey) {
		this.mailConfigKey = mailConfigKey;
		if (mailConfigKey != null && mailConfigs.containsKey(mailConfigKey)) {
			mailConfig = mailConfigs.get(mailConfigKey);
			testTo = mailConfig.getTestTo();
		}
	}
	
	public MailConfig getMailConfig() {
		return mailConfig;
	}

	public int getMailJiffies() {
		if (mailConfig != null) {
			return mailerServiceBeans.get().jiffies(mailConfig.key);
		}
		return -1;
	}

	public ListDataModel<MailMessage> getMailMessages() {
		return mailMessagesModel;
	}
	
	public long getMailMessagesToSend() {
		return mailMessages.stream().filter(m -> m.isSend()).count();
	}

	public MailTemplate getMailTemplate() {
		return mailTemplate;
	}

	public void setMailTemplate(MailTemplate mailTemplate) {
		if (mailTemplate != null && mailTemplate.getId() != null
				&& !mailTemplate.getId().equals(this.mailTemplate.getId())) {
			this.mailTemplate = mailTemplate;
			values.clear();
			mailMessages.clear();
			valuesModel = new ListDataModel<>(values);
			mailMessagesModel = new ListDataModel<>(mailMessages);
		}
	}

	public Converter getConverter() {
		return new Converter() {
			Converter converter = mailTemplateBean.getConverter();

			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				return converter.getAsString(context, component, value);
			}

			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				try {
					MailTemplate tmp = (MailTemplate) converter.getAsObject(context, component, value);
					return (mailTemplate != null && tmp.getId() == mailTemplate.getId()) ? mailTemplate : tmp;
				} catch (Exception e) {
				}
				if (mailTemplate == null)
					mailTemplate = new MailTemplate();
				return mailTemplate;
			}
		};
	}

	public void handleFileUpload(FileUploadEvent event) {
		try {
			UploadedFile file = event.getFile();
			Path tmp = Files.createTempFile(Paths.get(System.getProperty("jboss.server.temp.dir")), file.getFileName(), ".upload");
			Files.copy(file.getInputStream(), tmp, StandardCopyOption.REPLACE_EXISTING);
			files.put(file.getFileName(), new UpFile(file,tmp));
		} catch (Exception e) {
			error("Datei konnte nicht geladen werden: "+e.getLocalizedMessage());
		}
	}
	
	public Map<String,UpFile> getFiles() {
		return files;
	}
	
	public List<String> getFileNames() {
		return new ArrayList<String>(files.keySet());
	}
	
	public void setFileNames(List<String> fileNames) {
		if (fileNames == null || fileNames.isEmpty()) {
			files.clear();
		} else {
			files.entrySet().removeIf(e -> !fileNames.contains(e.getValue().uploadedFile.getFileName()));
		}
	}

	public Activity getActivity() {
		return activity;
	}
	
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	public void retrieve() {
	}
	
	public static class UpFile {
		final UploadedFile uploadedFile;
		final Path content;
		public UpFile(UploadedFile uploaded, Path content) {
			this.uploadedFile = uploaded;
			this.content = content;
		}
		@Override
		protected void finalize() throws Throwable {
			try { Files.delete(content); } catch (Exception e) {}
			super.finalize();
		}
	}
	
	public static class MailConfig {
		private final String key;
		private final String prefix;
		private String alias;
		private String cc;
		private String bcc;
		private String from;
		private String username;
		private String password;
		private String testTo = "noreply@nomail.org";
		private String smsUsername;
		private String smsPassword;
		private String smsService;
		private String smsTestTo;
		private final Properties properties;

		public MailConfig(String key, Collection<Configuration> configs, boolean debug) {
			this.key = key;
			prefix = key + "_";
			username = getValue(configs, "mail_username");
			from = getValue(configs, "mail_from", null);
			password = getValueIntern(configs, "mail_password", null);
			alias = getValue(configs, "mail_alias");
			cc = getValue(configs, "mail_cc");
			bcc = getValue(configs, "mail_bcc", from);
			setTestTo(getValue(configs, "mail_testTo", from != null ? from : testTo));
			smsUsername = getValue(configs, "mail_smsUsername", null);
			smsPassword = getValueIntern(configs, "mail_smsPassword", null);
			smsService = getValue(configs, "mail_smsService", null);
			smsTestTo = getValue(configs, "mail_smsTestTo", null);


			properties = new Properties();
			if (debug)
				properties.put("mail.debug", "true");
			for (MailProps mp : MailProps.values()) {
				Configuration conf = getConfig(configs, mp.name());
				if (conf != null) {
					properties.put(mp.name().replaceAll("_", "."), conf.getCvalue());
				}
			}
		}

		public static Map<String, MailConfig> generateConfigs(Collection<Configuration> configs, boolean debug) {
			Map<String, MailConfig> mailConfigs = new HashMap<>();
			String MAIL_FX = "_mail_";
			List<String> keys = configs.stream().filter(c -> c.getCkey() != null).map(c -> c.getCkey())
					.filter(k -> k.contains(MAIL_FX)).map(k -> k.substring(0, k.indexOf(MAIL_FX))).distinct().sorted()
					.collect(Collectors.toList());
			keys.forEach(k -> {
				mailConfigs.put(k, new MailConfig(k, configs.stream().filter(c -> c.getCkey().startsWith(k + MAIL_FX))
						.collect(Collectors.toList()), debug));
			});
			return mailConfigs;
		}

		private String getValue(Collection<Configuration> configs, String valueKey) {
			return getCValues(configs, valueKey)
					.filter(StringUtils::isNotEmpty).map(String::trim)
					.findFirst().orElse(null);
		}

		public Stream<String> getCValues(Collection<Configuration> configs, String valueKey) {
			return configs.stream().filter(c -> c.getCkey().startsWith(prefix + valueKey)).map(c -> c.getCvalue());
		}

		private String getValue(Collection<Configuration> configs, String valueKey, String defVal) {
			return getCValues(configs, valueKey)
					.filter(StringUtils::isNotEmpty).map(String::trim)
					.findFirst().orElse(defVal);
		}

		private String getValueIntern(Collection<Configuration> configs, String valueKey, String defVal) {
			return configs.stream().filter(c -> c.getCkey().startsWith(prefix + valueKey)).map(c -> c.getCvalueIntern())
					.findFirst().orElse(defVal);
		}

		private Configuration getConfig(Collection<Configuration> configs, String valueKey) {
			return configs.stream().filter(c -> c.getCkey().startsWith(prefix + valueKey)).findFirst().orElse(null);
		}

		public String getKey() {
			return key;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getCc() {
			return cc;
		}

		public void setCc(String cc) {
			this.cc = cc;
		}

		public String getBcc() {
			return bcc;
		}

		public void setBcc(String bcc) {
			this.bcc = bcc;
		}

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
		
		public String getTestTo() {
			return testTo;
		}
		
		public void setTestTo(String testTo) {
			this.testTo = testTo;
		}
		
		public Properties getProperties() {
			return properties;
		}
		
		public String getSmsUsername() {
			return smsUsername;
		}
		
		public String getSmsPassword() {
			return smsPassword;
		}
		
		public String getSmsService() {
			return smsService;
		}
		
		public String getSmsTestTo() {
			return smsTestTo;
		}
	}

	@Override
	public List<MailMessage> getPageItems() {
		return mailMessages;
	}

	@Override
	public void paginate() {
	}
}
