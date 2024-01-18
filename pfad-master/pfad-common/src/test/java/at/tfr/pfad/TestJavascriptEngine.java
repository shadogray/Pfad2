package at.tfr.pfad;

import static org.junit.Assert.assertEquals;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.Test;

import at.tfr.pfad.util.EngineUtil;

public class TestJavascriptEngine {

	@Test
	public void testPolyglot() {
		Context context = EngineUtil.getContext();
		Value val = context.eval("js", "42");
		assertEquals("failed to convert: " + val, Integer.valueOf(42), val.as(Integer.class));
	}

	@Test
	public void testEngineUtil() {
		Object val = EngineUtil.evalStr("42+1");
		assertEquals("failed to convert: " + val, "43", val);
	}
}
