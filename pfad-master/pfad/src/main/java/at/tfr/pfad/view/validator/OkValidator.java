package at.tfr.pfad.view.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

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
