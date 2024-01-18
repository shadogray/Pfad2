package at.tfr.pfad.action;

import org.omnifaces.util.Messages;

import at.tfr.pfad.util.EngineUtil;
import jakarta.enterprise.inject.Model;

@Model
public class EngineBean {
	
	private String script = "(111 + 333) * 2";
	private String result;

	public void eval() {
		result = eval(script);
	}
	
	public String eval(String script) {
		try {
			return EngineUtil.evalStr(script);
		} catch (Throwable e) {
			Messages.addWarn(null, "failed to eval: " + e.toString());
		}
		return null;
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getScript() {
		return script;
	}
	
	public void setScript(String script) {
		this.script = script;
	}
}
