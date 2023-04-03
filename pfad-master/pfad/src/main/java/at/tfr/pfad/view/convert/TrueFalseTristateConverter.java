package at.tfr.pfad.view.convert;

import javax.enterprise.inject.Model;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang3.StringUtils;
import org.omnifaces.util.Faces;

@Model
public class TrueFalseTristateConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, final String value) {
		if (StringUtils.isBlank(value))
			return null;
		final String val = value.toLowerCase();
		switch(val) {
		case "true":
		case "ja":
			return Boolean.TRUE;
		default:
			return Boolean.FALSE;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (!(value instanceof Boolean))
			return "";
		Boolean val = (Boolean)value;
		if (Faces.getLocale().getLanguage().equalsIgnoreCase("de"))
			return val ? "Ja" : "Nein";
		return val ? "True" : "False";
	}
}
