package at.tfr.pfad.view.convert;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.inject.Named;

import at.tfr.pfad.BookingStatus;

@Named
public class BookingStatusConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null)
			return null;
		switch(value) {
		case "Erstellt":
			return BookingStatus.created;
		case "Storno":
			return BookingStatus.storno;
		default: 
				return BookingStatus.valueOf(value);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null)
			return "";
		return BookingStatus.created.equals(value) ? "Erstellt" : (BookingStatus.storno.equals(value) ? "Storno" : "undef");
	}
}
