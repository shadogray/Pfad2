package at.tfr.pfad.view.convert;

import javax.faces.convert.EnumConverter;
import javax.inject.Named;

import at.tfr.pfad.RegistrationStatus;

@Named
public class RegistrationStatusConverter extends EnumConverter {

	public RegistrationStatusConverter() {
		super(RegistrationStatus.class);
	}
}
