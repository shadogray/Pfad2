package at.tfr.pfad.mock;

import at.tfr.pfad.util.UserSession;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

@Alternative
//@Priority(Interceptor.Priority.APPLICATION + 100)
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
