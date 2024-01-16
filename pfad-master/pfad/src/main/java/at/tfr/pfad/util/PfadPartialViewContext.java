package at.tfr.pfad.util;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.PartialViewContext;
import jakarta.faces.context.PartialViewContextWrapper;
import jakarta.faces.event.PhaseId;

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
