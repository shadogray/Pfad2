package at.tfr.pfad.action;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import at.tfr.pfad.DuplicateException;
import at.tfr.pfad.InvalidValueException;
import at.tfr.pfad.PfadException;
import at.tfr.pfad.RegistrationStatus;
import at.tfr.pfad.Sex;
import at.tfr.pfad.dao.RegistrationRepository;
import at.tfr.pfad.model.Registration;

@Stateless
public class RegistrationHandlerBean {

	private Logger log = Logger.getLogger(getClass());

	@Inject
	private RegistrationRepository regRepo;

	public void service(ServletRequest req) throws ServletException, IOException, PfadException {
		log.info("req: remote=" + req.getRemoteAddr() + ", parms=" + req.getParameterMap().entrySet().stream()
				.map(e -> "" + e.getKey() + ":" + StringUtils.join(e.getValue(), ",")).collect(Collectors.joining()));
		Registration reg = new Registration();
		reg.setAktiv(true);
		reg.setCreated(new Date());
		reg.setCreatedBy(req.getRemoteAddr());
		reg.setStatus(RegistrationStatus.Erstellt);
		req.getParameterMap().entrySet().stream()
				.forEach(e -> apply(reg, e.getKey(), StringUtils.join(e.getValue(), ",")));

		// Validate
		if (StringUtils.isBlank(reg.getVorname()) || StringUtils.isBlank(reg.getName())) {
			throw new InvalidValueException("Invalid Registration: empty Name/Vorname - " + reg);
		}

		// check for duplicates:
		List<Registration> duplicates = regRepo.getDuplicates(reg);
		if (!duplicates.isEmpty()) {
			log.info("found duplicates: reg=" + reg + " duplicates: " + duplicates);
			throw new DuplicateException("Duplicate Registration for Name=" + reg.getName() + ", Vorname=" + reg.getVorname() 
				+ ", Geburtstag=" + reg.getGebTag() + "." + reg.getGebMonat() + "." + reg.getGebJahr());
		}

		// save
		regRepo.save(reg);
		log.info("saved: " + reg);
	}

	public Registration apply(Registration reg, String param, String value) {
		if (value != null) {
			value = value.trim();
		}

		try {
			switch (param) {
			case "vornameKind":
				reg.setVorname(value);
				break;
			case "nachnameKind":
				reg.setName(value);
				break;
			case "geburtstag":
				reg.setGebTag(asInteger(param, value, reg));
				break;
			case "geburtsmonat":
				reg.setGebMonat(asInteger(param, value, reg));
				break;
			case "geburtsjahr":
				reg.setGebJahr(asInteger(param, value, reg));
				break;
			case "geschlecht":
				reg.setGeschlecht("M".equals(value) ? Sex.M : Sex.W);
				break;
			case "schuleintritt":
				reg.setSchoolEntry(asInteger(param, value, reg));
				break;
			case "vornameErziehungsberechtigter":
				reg.setParentVorname(value);
				break;
			case "nachnameErziehungsberechtigter":
				reg.setParentName(value);
				break;
			case "adresse":
				reg.setStrasse(value);
				break;
			case "postleitzahl":
				reg.setPLZ(value);
				break;
			case "ortschaft":
				reg.setOrt(value);
				break;
			case "telefon":
				reg.setTelefon(value);
				break;
			case "email":
				reg.setEmail(value);
				break;
			default:
				log.info("unknown param: " + param + " : " + value);
			}
		} catch (Exception e) {
			log.info("cannot convert: " + param + " : " + value + " err: " + e);
		}
		return reg;
	}

	public Integer asInteger(String key, String value, Registration reg) {
		if (value == null)
			return null;
		value = value.trim();
		if (Pattern.compile("[^\\d]+").matcher(value).find()) {
			if (reg.getComment() == null)
				reg.setComment("");

			log.info("found invalid: key=" + key + ", value=" + value);
			reg.setComment(reg.getComment() + "Fehler: " + key + "=" + value + "\n");
			value = Stream.of(value.split(" ")).filter(s -> s.trim().length() > 0).map(String::trim)
					.filter(s -> s.matches("\\d+")).findFirst().orElse(value);
		}
		return Integer.valueOf(value);
	}

}
