package at.tfr.pfad;

import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.tfr.pfad.model.Squad;
import at.tfr.pfad.util.TemplateUtils;
import junit.framework.Assert;

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
		Assert.assertEquals("replace failed: "+res, trupp.getName(), res);
		
		res = templateUtils.get().replace("${truppX.name}", beans);
		Assert.assertNull("replace failed: "+res, res);

		res = templateUtils.get().replace("${truppX.name}", beans, "DEFAULT");
		Assert.assertEquals("replace failed: "+res, "DEFAULT", res);

		res = templateUtils.get().replace("${NULL}", beans, "DEFAULT");
		Assert.assertEquals("replace failed: "+res, "null", res);
		
		res = templateUtils.get().replace("${NULL.name}", beans, "DEFAULT");
		Assert.assertEquals("replace failed: "+res, "DEFAULT", res);
	}

	@Test
	public void testReplaceMapCaseInsensitive() {
		Map<String,Object> beans = new HashMap<>();
		beans.put("TruPp", trupp);
		
		String res = templateUtils.get().replace("${trupp.name}", beans);
		Assert.assertEquals("replace failed: "+res, trupp.getName(), res);
	}

	@Ignore
	@Test
	public void testHtmlToText() throws Exception {
		MimeMessage msg = new MimeMessage(null, getClass().getResourceAsStream("/testmail.msg"));
		MimeMultipart mm = (MimeMultipart)msg.getContent();
		String text = TemplateUtils.htmlToText(""+mm.getBodyPart(1).getContent());
		Assert.assertFalse("filter html failed: "+text, text.contains("<"));
	}
	
}
