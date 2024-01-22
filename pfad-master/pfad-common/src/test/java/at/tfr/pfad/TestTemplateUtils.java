package at.tfr.pfad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.tfr.pfad.model.Squad;
import at.tfr.pfad.util.TemplateUtils;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@RunWith(CdiTestRunner.class)
public class TestTemplateUtils {

	Squad trupp = new Squad();
	{
		trupp.setName("TestTrupp");
	}
	
	@Inject
	private Instance<TemplateUtils> templateUtils;
	
	@Test
	public void testReplaceMap() {
		Map<String,Object> beans = new HashMap<>();
		beans.put("trupp", trupp);
		beans.put("NULL", null);
		
		String res = templateUtils.get().replace("${trupp.name}", beans);
		assertEquals("replace failed: "+res, trupp.getName(), res);
		
		res = templateUtils.get().replace("${truppX.name}", beans);
		assertNull("replace failed: "+res, res);

		res = templateUtils.get().replace("${truppX.name}", beans, "DEFAULT");
		assertEquals("replace failed: "+res, "DEFAULT", res);

		res = templateUtils.get().replace("${NULL}", beans, "DEFAULT");
		assertEquals("replace failed: "+res, "null", res);
		
		res = templateUtils.get().replace("${NULL.name}", beans, "DEFAULT");
		assertEquals("replace failed: "+res, "DEFAULT", res);
	}

	@Test
	public void testReplaceMapCaseInsensitive() {
		Map<String,Object> beans = new HashMap<>();
		beans.put("TruPp", trupp);
		
		String res = templateUtils.get().replace("${trupp.name}", beans);
		assertEquals("replace failed: "+res, trupp.getName(), res);
	}

	@Ignore
	@Test
	public void testHtmlToText() throws Exception {
		MimeMessage msg = new MimeMessage(null, getClass().getResourceAsStream("/testmail.msg"));
		MimeMultipart mm = (MimeMultipart)msg.getContent();
		String text = TemplateUtils.htmlToText(""+mm.getBodyPart(1).getContent());
		assertFalse("filter html failed: "+text, text.contains("<"));
	}
	
}
