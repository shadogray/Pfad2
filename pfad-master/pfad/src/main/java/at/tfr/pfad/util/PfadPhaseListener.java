package at.tfr.pfad.util;

import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;

public class PfadPhaseListener implements PhaseListener {

	@Override
	public void afterPhase(PhaseEvent event) {
		//PhaseListener.super.afterPhase(event);
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		//PhaseListener.super.beforePhase(event);
		FacesContext.getCurrentInstance().getAttributes().put("facelets.Encoding", "UTF8"); //RIConstants.FACELETS_ENCODING_KEY;
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}
}
