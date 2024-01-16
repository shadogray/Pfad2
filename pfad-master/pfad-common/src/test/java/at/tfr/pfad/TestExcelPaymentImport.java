package at.tfr.pfad;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import jakarta.inject.Inject;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.tfr.pfad.dao.ActivityRepository;
import at.tfr.pfad.dao.BookingRepository;
import at.tfr.pfad.dao.MemberRepository;
import at.tfr.pfad.dao.SquadRepository;
import at.tfr.pfad.model.Activity;
import at.tfr.pfad.model.Booking;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Squad;
import at.tfr.pfad.processing.ProcessData;
import at.tfr.pfad.processing.ProcessExcelPayments;

@RunWith(CdiTestRunner.class)
public class TestExcelPaymentImport {

	final String excelFile = "./src/test/data/ImportPayments.xlsx";
	final String excelBuchungen = "./src/test/data/Mitgliedsbeitrag_ZahlungenGruppe.xlsx";
	final String selfIban = "AT112211331144115511";
	final String IBANEins = "AT112211331144115511";
	final String IBANDrei = "AT998877665544332211";
	@Inject
	private ProcessExcelPayments procPayments;
	@Inject
	private ActivityRepository activityRepo;
	@Inject
	private MemberRepository memberRepo;
	@Inject
	private BookingRepository bookingRepo;
	@Inject
	private SquadRepository squadRepo;

	private Activity activity;

	@Before
	public void init() throws Exception {
		activity = createActivity();
		Squad s = createSquad();

		Member m = createMember("NameEins", "VornameEins", s);
		createBooking(activity, m);

		String vornameZwei = "VorZweiName";
		Member m2 = createMember("NameZwei", vornameZwei, s);
		createBooking(activity, m2);

		Member vollzahler3 = createMember("NameDrei", "VornameDrei1", s);
		createBooking(activity, vollzahler3);
		createBooking(activity, createMember("NameDrei", "VornameDrei2", s, vollzahler3));
		createBooking(activity, createMember("NameDrei", "VornameDrei3", s, vollzahler3));

		createBooking(activity, createMember("NameVier", "VornameVier1", s));
		createBooking(activity, createMember("NameVier", "VornameVier2", s));
		createBooking(activity, createMember("NameVier", "VornameVier3", s));
		createBooking(activity, createMember("NameVier", "VornameVier4", s));
		bookingRepo.flush();
	}

	@Test
	public void testFindBooking() throws Exception {

		List<Booking> bookings = bookingRepo.findByMemberNames("Gruppenbeitrag 16/17 NameEins VornameEins", activity);
		Assert.assertFalse(bookings.isEmpty());
		Assert.assertEquals(1, bookings.size());

		bookings = bookingRepo.findByMemberNames("Gruppenbeitrag 16/17 NameDrei VornameDrei1 VornameDrei2 VornameDrei3",
				activity);
		Assert.assertFalse(bookings.isEmpty());
		Assert.assertEquals(3, bookings.size());
	}

	@Test
	public void testFindBookingsPerRowSingle() throws Exception {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFRow row = wb.createSheet("TEST").createRow(1);
		row.createCell(1).setCellValue("01.01.2017");
		row.createCell(2).setCellValue("123");
		row.createCell(3).setCellValue("Gruppenbeitrag 16/17 NameEins VornameEins");
		row.createCell(4).setCellValue(120.0D);

		ProcessData data = new ProcessData(activity);

		ProcessExcelPayments.BookingData bd = procPayments.findBooking(row, data);
		Assert.assertEquals(1, bd.bookings.size());
	}

	@Test
	public void testFindIBAN() throws Exception {
		final String badenIBAN = "AT112020500000007450";
		final String targetIBAN = "AT252026702001284989";
		String[] line = new String[] { "01.09.2016", "EinTrupp/Gruppenleitung Irgend Wer", "-199,9", "EUR",
				"Pfadfindergruppe Baden", badenIBAN, "SPBDAT21XXX", "Irgend Wer", targetIBAN,
				"EinTrupp/Gruppenleitung" };
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFRow row = wb.createSheet("TEST").createRow(1);
		int idx = 1;
		for (String val : Arrays.asList(line)) {
			row.createCell(idx++).setCellValue(val);
		}

		ProcessData data = new ProcessData(activity);

		String iban = procPayments.findIBAN(row, data);
		Assert.assertEquals(targetIBAN, iban);
	}

	@Test
	public void testFindBookingsPerRowMultiple() throws Exception {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFRow row = wb.createSheet("TEST").createRow(1);
		row.createCell(1).setCellValue("01.01.2017");
		row.createCell(2).setCellValue("123");
		row.createCell(3).setCellValue("Gruppenbeitrag 16/17 NameDrei VornameDrei1 VornameDrei2 VornameDrei3");
		row.createCell(4).setCellValue(120.0D);

		ProcessData data = new ProcessData(activity);

		ProcessExcelPayments.BookingData bd = procPayments.findBooking(row, data);
		Assert.assertEquals(3, bd.bookings.size());
		// find related members - see Vollzahler
		Assert.assertTrue(procPayments.validateBookings(bd));
	}

	@Test
	public void testFindBookingsPerRowMultipleDoNotFindUnrelated() throws Exception {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFRow row = wb.createSheet("TEST").createRow(1);
		row.createCell(1).setCellValue("01.01.2017");
		row.createCell(2).setCellValue("123");
		row.createCell(3)
				.setCellValue("Gruppenbeitrag 16/17 NameVier VornameVier1, VornameVier2, VornameVier3, VornameVier4");
		row.createCell(4).setCellValue(120.0D);

		ProcessData data = new ProcessData(activity);

		ProcessExcelPayments.BookingData bd = procPayments.findBooking(row, data);
		// do not find unrelated members - see Vollzahler
		Assert.assertFalse(procPayments.validateBookings(bd));
	}

	@Test
	public void testExcelImport() throws Exception {

		ProcessData data = new ProcessData(activity);
		data.setCreatePayment(true);
		data.setAccontoGrades(new Double[] { 50D, 100D });
		Path xmlFile = Paths.get(excelFile);
		System.out.println("Reading: " + xmlFile);

		try (InputStream is = Files.newInputStream(xmlFile);
				XSSFWorkbook wb = (XSSFWorkbook) WorkbookFactory.create(is)) {

			for (Sheet sheet : wb) {
				int lastrow = sheet.getLastRowNum();
				for (Row row : sheet) {
					row = procPayments.processRow((XSSFRow) row, data);
					if (row.getRowNum() > lastrow) {
						break;
					}
				}
			}

			Path outFile = xmlFile.resolveSibling(xmlFile.getFileName() + ".out.xlsx");
			System.out.println("Writing: " + outFile);
			wb.write(Files.newOutputStream(outFile));
		}
	}

	@Test
	public void testExcelImportBuchungenFull() throws Exception {

		final String firstLine = " : NameEins VornameEins : EUR : Pfadfindergruppe Baden : AT112233445555667788 : SPBDAT21XXX : "
				+ "Irgend Wer : AT112233445566778899 : EinTrupp/Gruppenleitung : EinTrupp/Gruppenleitung : Irgend Wer : "
				+ "EinTrupp/Gruppenleitung : 202030304040AIG-090807060504 : ";
		List<Booking> bookings = bookingRepo.findByMemberNames(firstLine, activity);
		Assert.assertFalse(bookings.isEmpty());
		Assert.assertEquals(1, bookings.size());

		ProcessData data = new ProcessData(activity);
		data.setCreatePayment(true);
		data.setAccontoGrades(new Double[] { 50D, 100D });
		Path xmlFile = Paths.get(excelBuchungen);
		System.out.println("Reading: " + xmlFile);

		try (InputStream is = Files.newInputStream(xmlFile);
				XSSFWorkbook wb = (XSSFWorkbook) WorkbookFactory.create(is)) {

			for (Sheet sheet : wb) {
				int lastrow = sheet.getLastRowNum();
				for (Row row : sheet) {
					row = procPayments.processRow((XSSFRow) row, data);
					XSSFRow xssfRow = (XSSFRow) row;
					ProcessExcelPayments.BookingData bd = null;

					switch (row.getRowNum()) {
					case 0:
						bd = procPayments.findBooking(xssfRow, data);
						Assert.assertNotNull(bd);
						Assert.assertEquals(1, bd.bookings.size());
						Assert.assertEquals(IBANEins, data.getPayment().getPayerIBAN());
						break;
					case 1:
						bd = procPayments.findBooking(xssfRow, data);
						Assert.assertNotNull(bd);
						Assert.assertEquals(3, bd.bookings.size());
						Assert.assertEquals(IBANDrei, data.getPayment().getPayerIBAN());
						break;
					}

					if (row.getRowNum() > lastrow) {
						break;
					}
				}
			}

			Path outFile = xmlFile.resolveSibling(xmlFile.getFileName() + ".out.xlsx");
			System.out.println("Writing: " + outFile);
			wb.write(Files.newOutputStream(outFile));
		}
	}

	private Booking createBooking(Activity a, Member m) {
		Booking b = new Booking();
		b.setMember(m);
		b.setActivity(a);
		b.setStatus(BookingStatus.created);
		return bookingRepo.saveAndFlush(b);
	}

	private Squad createSquad() {
		Squad s = new Squad();
		s.setName("TEST");
		s.setType(SquadType.CAEX);
		s = squadRepo.saveAndFlush(s);
		return s;
	}

	private Activity createActivity() {
		Activity a = new Activity();
		a.setAmount(70.0F);
		a.setName("TEST");
		a.setType(ActivityType.Membership);
		a.setStatus(ActivityStatus.planned);
		a = activityRepo.saveAndFlush(a);
		return a;
	}

	private Member createMember(String name, String vorname, Squad s) {
		return createMember(name, vorname, s, null);
	}

	private Member createMember(String name, String vorname, Squad s, Member vollzahler) {
		Member m = new Member();
		m.setName(name);
		m.setVorname(vorname);
		m.setGebJahr(1990);
		m.setGebMonat(1);
		m.setGebTag(1);
		m.setTrupp(s);
		m.setVollzahler(vollzahler);
		m = memberRepo.saveAndFlush(m);
		return m;
	}

}
