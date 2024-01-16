package at.tfr.pfad.mock;

import java.security.Principal;
import java.util.Map;

import jakarta.ejb.EJBHome;
import jakarta.ejb.EJBLocalHome;
import jakarta.ejb.EJBLocalObject;
import jakarta.ejb.EJBObject;
import jakarta.ejb.SessionContext;
import jakarta.ejb.TimerService;
import jakarta.enterprise.context.Dependent;
import jakarta.transaction.UserTransaction;

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

}
