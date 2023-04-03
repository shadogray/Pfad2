package at.tfr.pfad.processing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;

import at.tfr.pfad.ActivityType;
import at.tfr.pfad.SquadType;
import at.tfr.pfad.dao.ActivityRepository;
import at.tfr.pfad.dao.BookingRepository;
import at.tfr.pfad.dao.ConfigurationRepository;
import at.tfr.pfad.dao.MemberRepository;
import at.tfr.pfad.dao.PaymentRepository;
import at.tfr.pfad.dao.SquadRepository;
import at.tfr.pfad.model.Activity;
import at.tfr.pfad.model.Booking;
import at.tfr.pfad.model.Function;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Squad;
import at.tfr.pfad.util.SessionBean;
import at.tfr.pfad.util.ValidationResult;

@Stateless
public class RegistrationDataGenerator {

	private Logger log = Logger.getLogger(getClass());

	public enum HeaderRegistrierung {
		BVKey, GruppenSchlussel, PersonenKey, Titel, Name, Vorname, Anrede, GebTag, GebMonat, GebJahr, Straße, PLZ, Ort, Geschlecht, Aktiv, Vollzahler, Email, Religion, Telefon, Funktionen,
	}

	public enum HeaderGruppe {
		Key, Name, Heim1, Straße1, Plz1, Ort1, Heim2, Straße2, Plz2, Ort2, BIC, IBAN, Bezirk, Web, Mail, Gruendungsjahr, Verein, Letzte_Wahl_GFm, Letzte_Wahl_GFw, Letzte_Wahl_ER, ZVR, BLZ, KontoNr;
	}

	public enum HeaderLocal {
		OK, Reg, Trupp, Religion, FunktionenBaden, Trail, Gilde, AltER, InfoMail, Mitarbeit, Eltern, Kinder, KinderTrupps
	}

	public enum DataStructure {
		XLS, CSV, XLSX
	}
	
	@Inject
	private MemberRepository membRepo;
	@Inject
	private SquadRepository squadRepo;
	@Inject
	private ConfigurationRepository configRepo;
	@Inject
	private ActivityRepository activityRepo;
	@Inject
	private BookingRepository bookingRepo;
	@Inject
	private PaymentRepository paymentRepo;
	@Inject
	private SessionBean sessionBean;
	@Inject
	private MemberValidator memberValidator;

	public XSSFWorkbook generateData(RegConfig config, Predicate<Member> filter, Squad... squads) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("Personen");
		CellStyle red = wb.createCellStyle();
		red.setFillForegroundColor(HSSFColor.RED.index);
		red.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		squads = Stream.of(squads).filter(s->s != null).collect(Collectors.toList()).toArray(new Squad[]{});

		// non-filtered members
		if (filter == null) {
			filter = m -> true;
		}
		
		int rCount = 0;
		XSSFRow row = sheet.createRow(rCount++);
		
		List<Activity> activities = activityRepo.findAll().stream().filter(a->!a.isFinished())
				.sorted((a,b) -> a.getName().compareTo(b.getName())).collect(Collectors.toList());

		List<String> headers = Arrays.asList(HeaderRegistrierung.values()).stream().map(h -> h.name())
				.collect(Collectors.toList());
		if (config.withLocal) {
			headers.addAll(
					Arrays.asList(HeaderLocal.values()).stream().map(h -> h.name()).collect(Collectors.toList()));
		}
		if (config.withBookings) {
			headers.add("--");
			headers.addAll(activities.stream().map(a->a.getName()).collect(Collectors.toList()));
		}

		for (int i = 0; i < headers.size(); i++) {
			String h = transformHeaders(headers, i);
			XSSFCell c = row.createCell(i);
			c.setCellValue(h);
		}

		final Activity membership = activityRepo.findActive().stream()
				.filter(a -> ActivityType.Membership.equals(a.getType())).findFirst().orElse(null);
		 
		final Collection<Member> leaders = squadRepo.findLeaders();
		final List<Member> members = getMembers(config.isActiveOnly()).stream().filter(filter).collect(Collectors.toList());

		for (final Member m : members) {
			
			if (config.getPayedActivity() != null 
					&& !m.isFree() // Mitglied is frei 
					&& !leaders.contains(m) // Leiter/Funktionen sind frei..
					&& !(m.getFunktionen() != null && m.getFunktionen().stream().anyMatch(f->Boolean.TRUE.equals(f.isFree())))) {
				List<Long> payments = paymentRepo.findIdByMemberAndActivityAndFinished(m, config.getPayedActivity(), true);
				if (payments.isEmpty()) {
					continue;
				}
			}
			
			if (!config.withLocal && !memberValidator.isGrinsExportable(m, leaders))
				continue;

			if (squads != null && squads.length > 0) {
				if (!Stream.of(squads).anyMatch(s -> s.equals(m.getTrupp()))) {
					continue;
				}
			}

			Booking memberBooking = null;
			if (membership != null) {
				memberBooking = bookingRepo.findByMemberAndActivityOrderByIdDesc(m, membership).stream().findFirst().orElse(null);
			}
			if (config.notRegistered) {
				if (memberBooking != null && memberBooking.isRegistered()) {
					continue;
				}
			}

			List<ValidationResult> vr = memberValidator.validate(m, getFunktionen(m), leaders);

			row = sheet.createRow(rCount++);

			int cCount = 0;
			// BVKeyBVKey
			row.createCell(cCount++).setCellValue(m.getBVKey());
			// GruppenSchlussel
			row.createCell(cCount++).setCellValue(m.getGruppenSchluessel());
			// PersonenKey
			row.createCell(cCount++).setCellValue(m.getPersonenKey());
			// Titel
			row.createCell(cCount++).setCellValue(m.getTitel());
			// Name
			row.createCell(cCount++).setCellValue(m.getName());
			// Vorname
			row.createCell(cCount++).setCellValue(m.getVorname());
			// Anrede
			row.createCell(cCount++).setCellValue(m.getAnrede());
			// GebTag
			row.createCell(cCount++).setCellValue(m.getGebTag());
			// GebMonat
			row.createCell(cCount++).setCellValue(m.getGebMonat());
			// GebJahr
			row.createCell(cCount++).setCellValue(m.getGebJahr());
			// Straße
			row.createCell(cCount++).setCellValue(m.getStrasse());
			// PLZ
			row.createCell(cCount++).setCellValue(m.getPLZ());
			// Ort
			row.createCell(cCount++).setCellValue(m.getOrt());
			// Geschlecht
			row.createCell(cCount++).setCellValue(m.getGeschlecht() != null ? m.getGeschlecht().name() : "");
			// Aktiv
			row.createCell(cCount++).setCellValue(m.isAktiv() ? "J" : "N");
			// Vollzahler
			row.createCell(cCount++).setCellValue(m.getFunktionen().stream().anyMatch(f->Function.PTA.equals(f.getKey())) ? "P" : (m.getVollzahler() != null ? m.getVollzahler().getBVKey() : "N"));
			// Email
			row.createCell(cCount++).setCellValue(m.getEmail());
			// Religion
			row.createCell(cCount++).setCellValue(config.withLocal ? m.getReligion() : ""); // do not return Religion
			// Telefon
			row.createCell(cCount++).setCellValue(m.getTelefon());
			// Funktionen
			row.createCell(cCount++).setCellValue(getFunktionen(m));

			// and Local Data
			if (config.withLocal) { // Religion, FunktionenBaden, Trail, Gilde, AltER

				XSSFCell ok = row.createCell(cCount++);
				if (!vr.isEmpty()) {
					ok.setCellValue(vr.stream().map(v -> v.getMessage()).collect(Collectors.joining(",")));
					ok.setCellStyle(red);
				}

				row.createCell(cCount++).setCellValue(memberBooking != null ? ""+memberBooking.isRegistered() : "");
				row.createCell(cCount++).setCellValue(m.getTrupp() != null ? m.getTrupp().getName() : "");
				row.createCell(cCount++).setCellValue(m.getReligion());
				row.createCell(cCount++).setCellValue("");
				row.createCell(cCount++).setCellValue(m.isTrail() ? "X" : "");
				row.createCell(cCount++).setCellValue(m.isGilde() ? "X" : "");
				row.createCell(cCount++).setCellValue(m.isAltER() ? "X" : "");
				row.createCell(cCount++).setCellValue(m.isInfoMail() ? "X" : "");
				row.createCell(cCount++).setCellValue(m.isSupport() ? "X" : "");
				// Eltern, Kinder, KinderTrupps
				row.createCell(cCount++).setCellValue(!m.getSiblings().isEmpty() ? "X" : "");
				row.createCell(cCount++)
						.setCellValue(m.getSiblings().stream().map(s -> s.toString()).collect(Collectors.joining(",")));
				row.createCell(cCount++).setCellValue(m.getSiblings().stream()
						.map(s -> s.getTrupp() != null ? s.getTrupp().getName() : "").collect(Collectors.joining(",")));
			}
			
			if (config.withBookings) {
				row.createCell(cCount++).setCellValue("");
				for (Activity a : activities) {
					Optional<Booking> bOpt = m.getBookings().stream()
							.filter(b-> b.isValid() && a.equals(b.getActivity())).findAny();
					row.createCell(cCount++).setCellValue(bOpt.isPresent() ? ""+a.getName() : "");
				}
			}
			
			if (config.isWithUpdateRegistered() && memberBooking != null) {
				memberBooking.setRegistered(true);
				bookingRepo.flush();
			}

		}

		formatGruppeSheet(wb.createSheet("Gruppe"), config);
		formatStatusSheet(wb.createSheet("Status"), config);

		return wb;
	}

	private XSSFSheet formatGruppeSheet(XSSFSheet sheet, RegConfig config) {
		// Key Name Heim1 Straße1 Plz1 Ort1 Heim2 Straße2 Plz2 Ort2 BIC IBAN
		// Bezirk Web Mail
		// Gründungsjahr Verein Letzte Wahl GFm Letzte Wahl GFw Letzte Wahl ER
		// ZVR BLZ KontoNr
		// BAD Baden Marchetstraße 7 2500 Baden 0 0 www.ontrail.at
		// vorstand@ontrail.at
		// 22.06.14 22.06.14 2015-11-18 0 0
		List<String> headers = transformGruppeHeaders();
		XSSFRow row = sheet.createRow(0);
		for (int i = 0; i < headers.size(); i++) {
			row.createCell(i).setCellValue(headers.get(i));
		}
		row = sheet.createRow(1);
		int cCount = 0;
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Key.name(), "BAD"));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Name.name(), "Baden"));
		row.createCell(cCount++)
				.setCellValue(configRepo.getValue(HeaderGruppe.Heim1.name(), "Fritz Fangl Pfadfinderheim"));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Straße1.name(), "Marchetstraße 7"));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Plz1.name(), "2500"));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Ort1.name(), "Baden"));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Heim2.name(), ""));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Straße2.name(), ""));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Plz2.name(), ""));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Ort2.name(), ""));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.BIC.name(), ""));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.IBAN.name(), ""));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Bezirk.name(), "Baden"));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Web.name(), "www.ontrail.at"));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Mail.name(), "vorstand@ontrail.at"));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Gruendungsjahr.name(), "1930"));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Verein.name(), ""));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Letzte_Wahl_GFw.name(), "22.06.2014"));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Letzte_Wahl_GFm.name(), "22.06.2014"));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.Letzte_Wahl_ER.name(), "18.11.2015"));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.ZVR.name(), "545163933"));
		row.createCell(cCount++).setCellValue(configRepo.getValue(HeaderGruppe.BLZ.name(), "20205"));
		return sheet;
	}

	private XSSFSheet formatStatusSheet(XSSFSheet sheet, RegConfig config) {
		int rCount = 0;
		// "Export_" + DateTime.now().toString("yyyy.mm.dd"));
		// Die Auswertung wurde mit folgenden Optionen erstellt:
		XSSFRow row = sheet.createRow(rCount++);
		row.createCell(0).setCellValue("Die Auswertung wurde mit folgenden Optionen erstellt:");
		// Gruppenkürzel BAD
		row = sheet.createRow(rCount++);
		row.createCell(0).setCellValue("Gruppenkürzel");
		row.createCell(1).setCellValue(configRepo.getValue(HeaderGruppe.Key.name(), "BAD"));
		// Auswertung vom 23.12.2015 10:22
		row = sheet.createRow(rCount++);
		row.createCell(0).setCellValue("Auswertung vom");
		row.createCell(1).setCellValue(DateTime.now().toString("dd.MM.yyyy HH:mm"));
		// GRINS Art HR
		row = sheet.createRow(rCount++);
		row.createCell(0).setCellValue("GRINS Art");
		if (config.vorRegistrierung) {
			row.createCell(1).setCellValue(configRepo.getValue("GRINS_Art_VorReg", "VR"));
		} else {
			row.createCell(1).setCellValue(configRepo.getValue("GRINS_Art", "HR"));
		}
		// Benutzer reg
		row = sheet.createRow(rCount++);
		row.createCell(0).setCellValue("Benutzer");
		row.createCell(1).setCellValue(sessionBean.getUser().getName());
		// Akzeptierte Fehler
		row = sheet.createRow(rCount++);
		row.createCell(0).setCellValue("Akzeptierte Fehler");
		row.createCell(1).setCellValue("");

		return sheet;
	}

	private List<String> transformGruppeHeaders() {
		return Stream.of(HeaderGruppe.values())
				.map(h -> h.name().replaceAll("_", " ").replaceAll("ue", "ü").replaceAll("oe", "ö"))
				.collect(Collectors.toList());
	}

	private String transformHeaders(List<String> headers, int i) {
		String h = headers.get(i);
		switch (h) {
		case "Vollzahler":
			h = "Ermäßigt";
			break;
		case "BVKey":
			h = "BV-Key";
			break;
		case "GruppenSchlussel":
			h = "GruppenSchlüssel";
			break;
		case "PLZ":
			h = "Postleitzahl";
			break;
		}
		return h;
	}

	private List<Member> getMembers(Boolean active) {
		List<Member> members;
		if (active != null) {
			members = membRepo.findActive();
		} else {
			members = membRepo.findAll().stream().sorted().collect(Collectors.toList());
		}
		if (sessionBean.isAdmin() || sessionBean.isGruppe() || sessionBean.isVorstand())
			return members;
		if (sessionBean.isLeiter()) {
			Squad squad = sessionBean.getSquad();
			members = members.stream().filter(m -> m.getTrupp() != null && m.getTrupp().equals(squad))
					.collect(Collectors.toList());
		}
		return members;
	}
	
	private String getFunktionen(Member m) {
		List<String> functions = new ArrayList<>();
		if (m.getTrupp() != null) {
			functions.add(m.getTrupp().getType().getKey(m.getGeschlecht()));
		}

		Collection<Function> funktionen = m.getFunktionen().stream()
				.filter(f -> f.getKey() != null).collect(Collectors.toList());
		
		List<String> toLead = squadRepo.findByLeaderFemaleEqualOrLeaderMaleEqual(m).stream()
				.map(s -> "AS" + s.getType().getKey(m.getGeschlecht())).collect(Collectors.toList());
		
		List<String> toAss = squadRepo.findByAssistant(m).stream()
				.map(s -> "AS" + s.getType().getKey(m.getGeschlecht())).collect(Collectors.toList());
		
		if (!funktionen.isEmpty()) {
			functions.addAll(funktionen.stream()
					.filter(f -> !Function.PTA.equals(f.getKey()))
					.filter(f -> !f.isNoFunction())
					.map(f -> f.getKey()).collect(Collectors.toList()));
		}

		// Sobald aber jemand die Bezeichnung SF oder AS vor der Stufenbezeichnung hat, dann ist er definitiv kein ZBV mehr
		if (!toLead.isEmpty() || !toAss.isEmpty()) {
			funktionen = funktionen.stream()
					.filter(f -> !f.getKey().equals(Function.ZBV)).collect(Collectors.toList());
		}
		
		if (!funktionen.stream().anyMatch(f -> f.isLeader())) {
			if (!functions.stream().anyMatch(f->(f.startsWith("SF") || f.startsWith("GF")))) {
				functions.addAll(toLead);
				functions.addAll(toAss);
			}
		}

		// Wenn bisher keine Funktion gefunden:
		// Kinder, die in keinem Trupp sind, dürfen nicht als MIT gemeldet werden:
		if ((functions.isEmpty() || (functions.size()==1 && (StringUtils.isEmpty(functions.get(0)) || Function.MIT.equals(functions.get(0))))) 
				&& m.isAktiv() && m.getTrupp() == null) {
			functions.clear(); // remove evtl. MIT for later replace
			final int ageYears = DateTime.now().getYear()-m.getGebJahr();
			if (ageYears <= SquadType.RARO.getMax()) {
				functions.clear(); // remove evtl. MIT
				Optional<SquadType> stOpt = Stream.of(SquadType.values())
						.filter(st -> ageYears >= st.getMin() && ageYears <= st.getMax()).findFirst();
				functions.add(stOpt.isPresent() ? stOpt.get().getKey(m.getGeschlecht()) : SquadType.BIBE.getKey(m.getGeschlecht()));
			} else {
				functions.add(Function.MIT);
			}
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(functions.stream().distinct().collect(Collectors.joining(",")));
		return sb.toString().trim();
	}
	
	public static class RegConfig {
		private boolean activeOnly;
		private boolean withLocal;
		private boolean notRegistered;
		private boolean withBookings;
		private boolean vorRegistrierung;
		private boolean withUpdateRegistered;
		private Activity payedActivity;
		
		public RegConfig() {
			// TODO Auto-generated constructor stub
		}

		public RegConfig(Activity payedActivity) {
			this.payedActivity = payedActivity;
		}

		public RegConfig(Activity payedActivity, boolean activeOnly) {
			this.payedActivity = payedActivity;
			this.activeOnly = activeOnly;
		}

		public RegConfig(boolean withLocal, boolean notRegistered, boolean withBookings) {
			super();
			this.withLocal = withLocal;
			this.notRegistered = notRegistered;
			this.withBookings = withBookings;
		}

		public RegConfig(boolean withLocal, boolean notRegistered, boolean withBookings, Activity payedActivity) {
			super();
			this.withLocal = withLocal;
			this.notRegistered = notRegistered;
			this.withBookings = withBookings;
			this.payedActivity = payedActivity;
		}

		public RegConfig notRegistered(boolean notRegistered) {
			this.notRegistered = notRegistered;
			return this;
		}
		
		public RegConfig withLocal() {
			withLocal = true;
			return this;
		}
		
		public RegConfig withBookings() {
			withBookings = true;
			return this;
		}
		
		public RegConfig asVorRegistrierung() {
			vorRegistrierung = true;
			return this;
		}
		
		public RegConfig withUpdateRegistered(boolean update) {
			withUpdateRegistered = update;
			return this;
		}
		
		public RegConfig withPayedActivity(Activity payedActivity) {
			this.payedActivity = payedActivity;
			return this;
		}

		public RegConfig withActiveOnly(boolean activeOnly) {
			this.activeOnly = activeOnly;
			return this;
		}
		
		public RegConfig withActiveOnly() {
			this.activeOnly = true;
			return this;
		}
		
		public boolean isActiveOnly() {
			return activeOnly;
		}
		
		public boolean isWithLocal() {
			return withLocal;
		}

		public boolean isNotRegistered() {
			return notRegistered;
		}

		public boolean isWithBookings() {
			return withBookings;
		}

		public boolean isVorRegistrierung() {
			return vorRegistrierung;
		}

		public boolean isWithUpdateRegistered() {
			return withUpdateRegistered;
		}
		
		public Activity getPayedActivity() {
			return payedActivity;
		}
	}

}
