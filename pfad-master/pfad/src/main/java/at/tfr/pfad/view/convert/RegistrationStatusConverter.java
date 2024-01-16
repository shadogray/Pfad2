package at.tfr.pfad.view.convert;

import jakarta.faces.convert.EnumConverter;
import jakarta.inject.Named;

import at.tfr.pfad.RegistrationStatus;

@Named
public class RegistrationStatusConverter extends EnumConverter {

	public RegistrationStatusConverter() {
		super(RegistrationStatus.class);
	}
}
