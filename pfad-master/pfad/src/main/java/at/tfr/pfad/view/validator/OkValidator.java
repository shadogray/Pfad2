package at.tfr.pfad.view.validator;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Named;

import org.jboss.logging.Logger;

@Named
@FacesValidator
public class OkValidator implements Validator {

	private Logger log = Logger.getLogger(getClass());
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		// noop
		log.debug("validate: "+value+" for comp: "+component.getClientId()+" / "+component.getId());
	}
}
