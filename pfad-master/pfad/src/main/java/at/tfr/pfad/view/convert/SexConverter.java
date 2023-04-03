package at.tfr.pfad.view.convert;

import javax.faces.convert.EnumConverter;
import javax.inject.Named;

import at.tfr.pfad.Sex;

@Named
public class SexConverter extends EnumConverter {

	public SexConverter() {
		super(Sex.class);
	}
}
