package at.tfr.pfad.util;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.NonexistentConversationException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.ViewExpiredException;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;

//@ExceptionHandler
public class PfadExceptionHandler
{
	private Logger log = Logger.getLogger(getClass());
	
//    void printExceptions(@Handles ExceptionEvent<Throwable> evt)
//    {
//        log.info("Exception:" + evt.getException().getMessage(), evt.getException());
//        FacesContext.getCurrentInstance().addMessage(null, 
//        		new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exception: "+evt.getException().getMessage(), null));
//        if (evt.getException() instanceof NonexistentConversationException
//        		|| evt.getException() instanceof ViewExpiredException) {
//        	try {
//        		ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
//				ectx.redirect(ectx.getRequestContextPath()+"/login.xhtml");
//        	} catch (Exception e) {
//        		log.info("cannot redirect: "+e, e);
//        	}
//        }
//        evt.handledAndContinue();
//    }
}