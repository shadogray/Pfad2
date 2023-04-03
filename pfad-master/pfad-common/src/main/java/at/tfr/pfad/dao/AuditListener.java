package at.tfr.pfad.dao;

import java.util.Date;

import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import at.tfr.pfad.model.Auditable;
import at.tfr.pfad.model.Registration;

public class AuditListener {

	@PrePersist
	private void createUserAndTimestamp(Auditable auditable) {
		String name = getUser();
		if (auditable instanceof Registration && auditable.getCreated() != null) {
			// no overwrite createdate from UI
		} else {
			auditable.setCreated(new Date());
		}
		auditable.setCreatedBy(name);
	}

	@PreUpdate
	private void updateUserAndTimestamp(Auditable auditable) {
		String name = getUser();
		auditable.setChanged(new Date());
		auditable.setChangedBy(name);
	}

	private String getUser() {
		String name = "undef";
		try {
			SessionContext sessionContext = (SessionContext) new InitialContext().lookup("java:comp/EJBContext");
			if (sessionContext != null && sessionContext.getCallerPrincipal() != null) {
				name = sessionContext.getCallerPrincipal().getName();
			}
		} catch (Exception e) {
		}
		return name;
	}

}
