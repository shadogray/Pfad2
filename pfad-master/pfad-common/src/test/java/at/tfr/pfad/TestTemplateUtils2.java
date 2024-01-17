package at.tfr.pfad;

import java.util.HashMap;
import java.util.Map;

import org.apache.deltaspike.jpa.spi.transaction.TransactionStrategy;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Test;

import at.tfr.pfad.model.Squad;
import at.tfr.pfad.util.TemplateUtils;
import jakarta.inject.Inject;
import junit.framework.Assert;

public class TestTemplateUtils2 {

	Squad trupp = new Squad();
	{
		trupp.setName("TestTrupp");
	}
	
	private TemplateUtils templateUtils;
	private TransactionStrategy txStrategy;
	
	@Test
	public void testReplaceMapCaseInsensitive() {
		
		WeldContainer weld = new Weld().initialize();
		templateUtils = weld.select(TemplateUtils.class).get();
		
		txStrategy = weld.select(TransactionStrategy.class).get();
		
		Map<String,Object> beans = new HashMap<>();
		beans.put("TruPp", trupp);
		
		String res = templateUtils.replace("${trupp.name}", beans);
		Assert.assertEquals("replace failed: "+res, trupp.getName(), res);
	}

}
