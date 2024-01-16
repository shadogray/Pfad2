package at.tfr.pfad.util;

import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.FacesContextFactory;
import jakarta.faces.lifecycle.Lifecycle;

public class PfadFacesContextFactory extends FacesContextFactory {

	
	public PfadFacesContextFactory(FacesContextFactory wrapped) {
		super(wrapped);
	}

	@Override
	public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
			throws FacesException {
		FacesContext ctx = getWrapped().getFacesContext(context, request, response, lifecycle);
		ctx.getAttributes().put("facelets.Encoding", "UTF8"); //RIConstants.FACELETS_ENCODING_KEY;
		return ctx;
	}

	
	
}
