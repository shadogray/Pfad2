package at.tfr.pfad;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.junit.MockBean;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;

import at.tfr.pfad.util.SessionBean;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;

public class CdiTestBase {

	private static Weld myWeld;// = WeldInitiator.createWeld();
	private static WeldContainer myWeldContainer;// = myWeld.initialize();
	
	@ClassRule
    public static WeldInitiator weld = WeldInitiator
    	.from(WeldInitiator.createWeld().enableDiscovery())
    	.activate(RequestScoped.class, SessionScoped.class)
    	.addBeans(MockBean.builder()
    			.types(SessionBean.class)
    			.scope(SessionScoped.class)
    			.creating(new SessionBean()).build())
    	.build();
	
	@Before
	public void init() throws Exception {
		weld.injectNonContextual(this);
	}
	
	@AfterClass
	public static void shutdown() {
		if (myWeldContainer != null && myWeldContainer.isRunning()) {
			myWeldContainer.shutdown();
		}
	}
}
