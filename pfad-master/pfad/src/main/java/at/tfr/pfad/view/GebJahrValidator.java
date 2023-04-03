package at.tfr.pfad.view;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

import org.joda.time.DateTime;

@Named
@FacesValidator
public class GebJahrValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value instanceof Integer) {
			Integer gebJahr = (Integer) value;
			if (gebJahr != 0 && (gebJahr < 1900 || gebJahr > new DateTime().getYear())) {
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Geburtsjahr " + value + " ungültig! (0 oder gültiges Jahr)", null));
			}
		} else {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Geburtsjahr " + value + " ungültig! (0 oder gültiges Jahr)", null));
		}
	}
}
