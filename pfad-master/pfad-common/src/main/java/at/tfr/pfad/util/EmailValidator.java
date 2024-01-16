package at.tfr.pfad.util;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Named;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;

@Named
@ApplicationScoped
public class EmailValidator implements Validator {

	public static String cleanEmail(String email) {
		if (email == null) 
			return email;
		return Stream.of(email.split("[;, ]+")).filter(s -> StringUtils.isNotBlank(s)).map(s -> s.trim()) 
		.collect(Collectors.joining(","));
	}
	
	@Override
	public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {
		if (value instanceof String) {
			try {
				String email = cleanEmail((String)value);
				String[] addrs = email.split("[;, ]+");
				for (String addr : addrs) {
					addr = addr.trim();
					if (!addr.contains("@")) 
						throw new AddressException("MailAdresse muss ein \"@\" enthalten: "+addr);
					InternetAddress.parse(value.toString(), true);
				}
			} catch (Exception e) {
				throw new ValidationException("Ungültige MailAddresse: "+value + " : " + e.getMessage());
			}
		}
	}

}
