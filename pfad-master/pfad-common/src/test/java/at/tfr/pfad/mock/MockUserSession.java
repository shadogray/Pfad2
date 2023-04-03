package at.tfr.pfad.mock;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import at.tfr.pfad.util.UserSession;

@Alternative
@Priority(Interceptor.Priority.APPLICATION + 100)
@ApplicationScoped
public class MockUserSession extends UserSession {

	@Inject 
	private DummySessionContext dummySessionContext;
	
	@PostConstruct
	public void init() {
		sessionContext = dummySessionContext;
	}

	@Override
	public boolean isCallerInRole(String roleName) {
		return super.isCallerInRole(roleName);
	}
	
}
