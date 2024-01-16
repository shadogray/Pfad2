package at.tfr.pfad.view;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Named;

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
