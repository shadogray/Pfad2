package at.tfr.pfad.util;

import java.io.Serializable;
import java.security.Principal;

import jakarta.annotation.Resource;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;

import org.jboss.logging.Logger;

@Stateful
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class UserSession implements Serializable {

	private Logger log = Logger.getLogger(getClass());
	@Resource
	protected SessionContext sessionContext;

	public SessionContext getSessionContext() {
		return sessionContext;
	}

	public Principal getCallerPrincipal() {
		return sessionContext.getCallerPrincipal();
	}

	public boolean isCallerInRole(String roleName) {
		return sessionContext.isCallerInRole(roleName);
	}
}
