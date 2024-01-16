package at.tfr.pfad.util;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.PartialViewContext;
import jakarta.faces.context.PartialViewContextFactory;

public class PfadPartialViewContextFactory extends PartialViewContextFactory {

	private PartialViewContextFactory wrapped;
	
	public PfadPartialViewContextFactory(PartialViewContextFactory wrapped) {
		this.wrapped = wrapped;
	}
	
	@Override
	public PartialViewContext getPartialViewContext(FacesContext context) {
		return new PfadPartialViewContext(wrapped.getPartialViewContext(context));
	}
	
}
