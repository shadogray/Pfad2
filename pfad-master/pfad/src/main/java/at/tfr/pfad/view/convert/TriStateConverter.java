package at.tfr.pfad.view.convert;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.inject.Named;

@Named
public class TriStateConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null)
			return null;
		switch(value) {
		case "0":
			return null;
		case "1":
			return true;
		case  "2":
			return false;
		default: 
				return null;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null)
			return "0";
		return Boolean.TRUE.equals(value) ? "1" : "2";
	}
}
