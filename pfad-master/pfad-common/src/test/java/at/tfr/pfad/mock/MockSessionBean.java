package at.tfr.pfad.mock;

import at.tfr.pfad.Role;
import at.tfr.pfad.util.SessionBean;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.interceptor.Interceptor;

@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@ApplicationScoped
public class MockSessionBean extends SessionBean {

	boolean admin = true;
	
	@Override
	public void init() {
		roles.add(Role.admin);
	}
	
	@Override
	public boolean isAdmin() {
		return admin;
	}
	
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
}
