package at.tfr.pfad.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;
import javax.faces.event.PhaseId;

public class PfadPartialViewContext extends PartialViewContextWrapper {

	PartialViewContext wrapped;
	
	public PfadPartialViewContext(PartialViewContext context) {
		this.wrapped = context;
	}
	
	@Override
	public PartialViewContext getWrapped() {
		return wrapped;
	}
	
	@Override
	public void processPartial(PhaseId phaseId) {
		try {
			wrapped.processPartial(phaseId);
		} catch (Throwable e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
			throw e;
		}
	}
}
