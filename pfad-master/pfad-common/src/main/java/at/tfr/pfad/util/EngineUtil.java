package at.tfr.pfad.util;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.io.IOAccess;
import org.jboss.logging.Logger;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Collections;
import java.util.Map;

public class EngineUtil {
	
	private static Logger log = Logger.getLogger(EngineUtil.class);
	
    private static final String ID = "js";
    private static final String POLYGLOT_CONTEXT = "polyglot.context";
    private static final String OUT_SYMBOL = "$$internal.out$$";
    private static final String IN_SYMBOL = "$$internal.in$$";
    private static final String ERR_SYMBOL = "$$internal.err$$";
    private static final String JS_SYNTAX_EXTENSIONS_OPTION = "js.syntax-extensions";
    private static final String JS_SCRIPT_ENGINE_GLOBAL_SCOPE_IMPORT_OPTION = "js.script-engine-global-scope-import";
    private static final String SCRIPT_CONTEXT_GLOBAL_BINDINGS_IMPORT_FUNCTION_NAME = "importScriptEngineGlobalBindings";
    private static final String NASHORN_COMPATIBILITY_MODE_SYSTEM_PROPERTY = "polyglot.js.nashorn-compat";
    static final String MAGIC_OPTION_PREFIX = "polyglot.js.";
    public static final String NASHORN_COMPATIBILITY_OPTION = "js.nashorn-compat";
	
    private static Context.Builder builder;
    private static ScriptEngine sem = new ScriptEngineManager().getEngineByExtension("nashorn");
    
	public static void init() {
		if (builder == null) {
			System.setProperty("polyglotimpl.DisableClassPathIsolation", ""+true);
			System.setProperty("polyglotimpl.TraceClassPathIsolation", ""+log.isDebugEnabled());
			System.setProperty("polyglot.engine.WarnInterpreterOnly", ""+log.isDebugEnabled());
			builder = Context.newBuilder(ID)
					.allowExperimentalOptions(true)
					.option(NASHORN_COMPATIBILITY_OPTION, "true")
					.option(JS_SYNTAX_EXTENSIONS_OPTION, "true") // see: GraalJSScriptEngine: NASHORN_COMPATIBILITY_MODE
					.allowAllAccess(false)
			        .allowPolyglotAccess(PolyglotAccess.ALL)
			        .allowNativeAccess(false)
			        .allowHostAccess(HostAccess.ALL)
			        .allowHostClassLookup(s -> true)
			        .allowCreateProcess(false)
			        .allowCreateThread(false)
			        .allowIO(IOAccess.NONE);
		}
	}
	
	public static Context getContext() {
		init();
		return builder.build();
	}

	public static Object evalObj(String expression) {
		return evalObj(expression, Collections.emptyMap());
	}
	
	public static Object evalObj(String expression, Map<String, ?> values) {
		if (sem != null) {
			try {
				Bindings b = sem.createBindings();
				b.putAll(values);
				return sem.eval(expression, b);
			} catch (Throwable e) {
				log.info("jscript: " + expression + ": " + e.toString());
				log.debug("jscript: " + expression + ": " + e.toString(), e);
				return e.toString();
			}
		}
		try (Context ctx = getContext()) {
			Value val = eval(ctx, expression, values);
			if (val == null) {
				return null;
			}
			return val.asHostObject();
		}
	}
	
	public static String evalStr(String expression) {
		return evalStr(expression, Collections.emptyMap());
	}

	public static String evalStr(String expression, Map<String, ?> values) {
		try {
			Object val = evalObject(expression, values);
			if (val == null) {
				return null;
			}
			return val.toString();
		} catch (Throwable e) {
			log.info("script: " + expression + ": " + e.toString());
			log.debug("script: " + expression + ": " + e.toString(), e);
			return e.toString();
		}
	}

	public static Object evalObject(String expression, Map<String, ?> values) throws Exception {
		if (sem != null) {
			try {
				Bindings b = sem.createBindings();
				b.putAll(values);
				Object result = sem.eval(expression, b);
				if (result == null)
					return "null";
				return result.toString();
			} catch (Throwable e) {
				log.info("jscript: " + expression + ": " + e.toString());
				log.debug("jscript: " + expression + ": " + e.toString(), e);
				throw e;
			}
		}
		try (Context ctx = getContext()) {
			Value val = eval(ctx, expression, values);
			if (val == null) {
				return null;
			}
			if (val.isHostObject()) {
				return val.asHostObject();
			} else {
				return val.toString();
			}
		} catch (Throwable e) {
			log.info("script: " + expression + ": " + e.toString());
			log.debug("script: " + expression + ": " + e.toString(), e);
			throw e;
		}
	}
	
	protected static Value eval(Context ctx, String expression) {
		return eval(ctx, expression, Collections.emptyMap());
	}
	
	protected static Value eval(Context ctx, String expression, Map<String, ?> values) {
		Value b = ctx.getBindings(ID);
		values.entrySet().forEach(e -> b.putMember(e.getKey(), e.getValue()));
		return ctx.eval(ID, expression);
	}
	
}
