package at.tfr.pfad.view.convert;

import jakarta.faces.convert.EnumConverter;
import jakarta.inject.Named;

import at.tfr.pfad.Sex;

@Named
public class SexConverter extends EnumConverter {

	public SexConverter() {
		super(Sex.class);
	}
}
