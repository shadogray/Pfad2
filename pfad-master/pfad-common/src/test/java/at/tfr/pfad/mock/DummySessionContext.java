package at.tfr.pfad.mock;

import java.security.Identity;
import java.security.Principal;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.SessionContext;
import javax.ejb.TimerService;
import javax.enterprise.context.Dependent;
import javax.transaction.UserTransaction;
import javax.xml.rpc.handler.MessageContext;

@Dependent
public class DummySessionContext implements SessionContext {

	String role = "admin";
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	@Override
	public void setRollbackOnly() throws IllegalStateException {
	}

	@Override
	public Object lookup(String name) throws IllegalArgumentException {
		return null;
	}

	@Override
	public boolean isCallerInRole(String roleName) {
		return role.equals(roleName);
	}

	@Override
	public boolean isCallerInRole(Identity role) {
		return false;
	}

	@Override
	public UserTransaction getUserTransaction() throws IllegalStateException {
		return null;
	}

	@Override
	public TimerService getTimerService() throws IllegalStateException {
		return null;
	}

	@Override
	public boolean getRollbackOnly() throws IllegalStateException {
		return false;
	}

	@Override
	public Properties getEnvironment() {
		return null;
	}

	@Override
	public EJBLocalHome getEJBLocalHome() {
		return null;
	}

	@Override
	public EJBHome getEJBHome() {
		return null;
	}

	@Override
	public Map<String, Object> getContextData() {
		return null;
	}

	@Override
	public Principal getCallerPrincipal() {
		return null;
	}

	@Override
	public Identity getCallerIdentity() {
		return null;
	}

	@Override
	public boolean wasCancelCalled() throws IllegalStateException {
		return false;
	}

	@Override
	public Class getInvokedBusinessInterface() throws IllegalStateException {
		return null;
	}

	@Override
	public EJBObject getEJBObject() throws IllegalStateException {
		return null;
	}

	@Override
	public EJBLocalObject getEJBLocalObject() throws IllegalStateException {
		return null;
	}

	@Override
	public <T> T getBusinessObject(Class<T> businessInterface) throws IllegalStateException {
		return null;
	}

	@Override
	public MessageContext getMessageContext() throws IllegalStateException {
		return null;
	}
}
