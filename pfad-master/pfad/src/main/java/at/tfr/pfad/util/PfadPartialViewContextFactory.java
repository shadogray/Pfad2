package at.tfr.pfad.util;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextFactory;

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
