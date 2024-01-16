package at.tfr.pfad.view;

import jakarta.enterprise.inject.Specializes;
import jakarta.faces.context.FacesContext;

import org.apache.deltaspike.jsf.spi.scope.window.DefaultClientWindowConfig;

@Specializes
public class PfadClientWindowConfig extends DefaultClientWindowConfig {

	@Override
	public ClientWindowRenderMode getClientWindowRenderMode(FacesContext facesContext) {
		return super.getClientWindowRenderMode(facesContext);
	}
}
