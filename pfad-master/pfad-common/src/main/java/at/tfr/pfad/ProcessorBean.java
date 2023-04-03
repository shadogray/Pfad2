package at.tfr.pfad;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.logging.Logger;
import org.joda.time.DateTime;

import at.tfr.pfad.dao.ConfigurationRepository;
import at.tfr.pfad.model.Configuration;

@Named
@Singleton
@Startup
@LocalBean
public class ProcessorBean implements Serializable {

	private Logger log = Logger.getLogger(getClass());
	@Inject
	private EntityManager entityManager;
	@Inject
	private ConfigurationRepository configRepo;

	@PostConstruct
	public void init() {
		doBackup();
	}

	@Schedule(persistent = false, hour = "0", minute = "0", second = "5")
	public void doBackup() {
		try {
			Configuration test = configRepo.findOptionalByCkey("test");
			if (test == null || !Boolean.valueOf(test.getCvalue())) {
				String backupName = "pfad_" + new DateTime().toString("yyyy.MM.dd_HH") + ".zip";
				int result = entityManager.createNativeQuery("backup to '" + backupName + "';").executeUpdate();
				log.info("executed Backup to: " + backupName + ", result=" + result);
			} else {
				log.info("Test-Mode, not dumping DB: " + test);
			}
		} catch (Throwable e) {
			log.warn("cannot execute backup: " + e, e);
		}
	}
}
