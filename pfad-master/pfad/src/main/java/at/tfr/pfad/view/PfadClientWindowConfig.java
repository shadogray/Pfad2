package at.tfr.pfad.view;

import org.apache.deltaspike.jsf.spi.scope.window.DefaultClientWindowConfig;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.faces.context.FacesContext;
import jakarta.interceptor.Interceptor;

@Alternative
@Priority(Interceptor.Priority.APPLICATION)
public class PfadClientWindowConfig extends DefaultClientWindowConfig {

	@Override
	public ClientWindowRenderMode getClientWindowRenderMode(FacesContext facesContext) {
		return super.getClientWindowRenderMode(facesContext);
	}
}
