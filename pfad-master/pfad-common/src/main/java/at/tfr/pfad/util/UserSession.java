package at.tfr.pfad.util;

import java.io.Serializable;
import java.security.Principal;

import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

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
