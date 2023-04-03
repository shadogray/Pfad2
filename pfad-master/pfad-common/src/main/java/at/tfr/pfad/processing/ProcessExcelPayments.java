package at.tfr.pfad.processing;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.jboss.logging.Logger;

import at.tfr.pfad.dao.BookingRepository;
import at.tfr.pfad.dao.ConfigurationRepository;
import at.tfr.pfad.dao.PaymentRepository;
import at.tfr.pfad.model.Booking;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Payment;

public class ProcessExcelPayments implements Serializable {

	private Logger log = Logger.getLogger(getClass());

	private BookingRepository bookingRepo;
	private PaymentRepository paymentRepo;
	private ConfigurationRepository configRepo;
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd.mm.yyyy");
	public static Pattern ibanPattern = Pattern.compile("[A-Z]{2}\\d{18,}"); // AT112020500000007450

	public ProcessExcelPayments() {
	}

	@Inject
	public ProcessExcelPayments(BookingRepository bookingRepo, PaymentRepository paymentRepo) {
		this.bookingRepo = bookingRepo;
		this.paymentRepo = paymentRepo;
	}
	
	@PostConstruct
	public void init() {
	}

	public XSSFRow processRow(XSSFRow row, ProcessData data) {

		data.setPayment(null);
		AmountData ad = findAmount(row, data);
		Date date = findDate(row, data);
		String iban = findIBAN(row, data);
		BookingData bd = findBooking(row, data);

		if (bd == null || ad == null || date == null) {
			return addResultCell(row, IndexedColors.RED, (ad == null ? "kein Betrag gefunden"
					: bd == null ? "keine Buchung gefunden" : "kein Datum gefunden"), bd);
		}

		if (ad.index == 0 && bd.bookings.size() == 1) {
			log.info("Row: " + row.getRowNum() + ": found single: " + bd);

			Booking booking = bd.bookings.get(0);
			List<Payment> payments = paymentRepo.findByBooking(booking);
			Payment pay;

			Optional<Payment> pOpt = payments.stream()
					.filter(p -> sdf.format(p.getPaymentDate()).equals(sdf.format(date))).findFirst();
			if (pOpt.isPresent()) {
				pay = pOpt.get();
				if (pay.getPayerIBAN() == null && StringUtils.isNotBlank(iban)) {
					pay.setPayerIBAN(iban);
				}

				data.setPayment(pay);
				return addResultCell(row, IndexedColors.YELLOW, "existiert: " + pay, bd);

			} else {
				pay = createPayment(data, ad, bd, iban, booking);
			}

			data.setPayment(pay);
			return addResultCell(row, pay.getId() != null ? IndexedColors.GREEN : IndexedColors.YELLOW,
					(pay.getId() != null ? "Erstellt: " : "Möglich: ") + pay, bd);
		}

		if (ad.index > 0 && bd.bookings.size() > 1) {
			log.info("Row: " + row.getRowNum() + ": found multiple: " + bd);

			if (!validateBookings(bd)) {
				return addResultCell(row, IndexedColors.RED, "keine Geschwister in "+bd, bd);
			}
			
			Payment pay;

			for (Booking booking : bd.bookings) {
				List<Payment> payments = paymentRepo.findByBooking(booking);
	
				Optional<Payment> pOpt = payments.stream()
						.filter(p -> sdf.format(p.getPaymentDate()).equals(sdf.format(date))).findFirst();
				if (pOpt.isPresent()) {
					pay = pOpt.get();
					if (pay.getPayerIBAN() == null && StringUtils.isNotBlank(iban)) {
						pay.setPayerIBAN(iban);
					}
				
					data.setPayment(pay);
					return addResultCell(row, IndexedColors.YELLOW, "existiert: " + pay, bd);
				}
			}

			pay = createPayment(data, ad, bd, iban, bd.bookings.toArray(new Booking[bd.bookings.size()]));

			data.setPayment(pay);
			return addResultCell(row, pay.getId() != null ? IndexedColors.GREEN : IndexedColors.YELLOW,
					(pay.getId() != null ? "Erstellt: " : "Möglich: ") + pay, bd);
		}

		return row;
	}

	public boolean validateBookings(BookingData bd) {
		for (Booking book : bd.bookings) {
			// if !Vollzahler, the Vollzahler must be in List:
			if (book.getMember().getVollzahler() != null && hasVollzahler(bd, book.getMember())
					// or Vollzahler has Sibling in bookings
				|| isVollzahlerForAllSiblings(bd, book.getMember())) {
				return true;
			}
			log.info("no sibling for "+book+" in "+bd);
		}
		return false;
	}

	private boolean isVollzahlerForAllSiblings(BookingData bd, Member vollzahler) {
		return bd.bookings.stream().allMatch(bs->bs.getMember().equals(vollzahler) || vollzahler.equals(bs.getMember().getVollzahler()));
	}

	private boolean hasVollzahler(BookingData bd, Member sibling) {
		return bd.bookings.stream().anyMatch(bv->bv.getMember().equals(sibling.getVollzahler()));
	}

	public Payment createPayment(ProcessData data, AmountData ad, BookingData bd, String iban, Booking... booking) {
		Payment pay = new Payment();
		pay.setAconto(false);
		pay.setFinished(false);
		pay.getBookings().addAll(Arrays.asList(booking));
		pay.updateType(data.getActivity());
		pay.setAmount(ad.amount.floatValue());
		if (ad.acconto) {
			pay.setAconto(true);
		} else {
			pay.setFinished(true);
		}
		pay.setPayerIBAN(iban);
		pay.setComment(StringUtils.abbreviate(bd.text, 250));
		if (data.isCreatePayment()) {
			pay = paymentRepo.saveAndFlush(pay);
			log.info("created: " + bd + " : " + pay);
		} else {
			log.info("NOT created: " + bd + " : " + pay);
		}
		return pay;
	}

	public XSSFRow addResultCell(XSSFRow row, IndexedColors color, String text, BookingData bd) {
		log.info("Row: " + row.getRowNum() + ": " + text);
		XSSFCell res = row.createCell(row.getLastCellNum() + 1);
		res.setCellValue(text);
		CellStyle style = res.getCellStyle();
		style.setFillForegroundColor(color.getIndex());
		res.setCellStyle(style);
		if (bd != null) {
			for (Booking b : bd.bookings) {
				int bCellIdx = row.getLastCellNum();
				XSSFCell bRes = row.createCell(++bCellIdx);
				bRes.setCellValue(b.toString());
			}
		}
		return row;
	}

	public AmountData findAmount(XSSFRow row, ProcessData data) {
		for (Cell cell : row) {
			try {
				double value = cell.getNumericCellValue();
				for (int i = 0; i < data.getAmountGrades().length; i++) {
					if (data.getAmountGrades()[i].equals(value)) {
						return new AmountData(i, value);
					}
				}
				for (int i = 0; i < data.getAccontoGrades().length; i++) {
					if (data.getAccontoGrades()[i].equals(value)) {
						return new AmountData(i, value, true);
					}
				}
			} catch (Exception e) {
			}
			try {
				String value = cell.getStringCellValue();
				for (int i = 0; i < data.getAmountGrades().length; i++) {
					if (data.getAmountGrades()[i].toString().equals(value)) {
						return new AmountData(i, Double.valueOf(value));
					}
				}
				for (int i = 0; i < data.getAccontoGrades().length; i++) {
					if (data.getAccontoGrades()[i].toString().equals(value)) {
						return new AmountData(i, Double.valueOf(value), true);
					}
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	public Date findDate(XSSFRow row, ProcessData data) {
		for (Cell cell : row) {
			try {
				Date value = cell.getDateCellValue();
				return value;
			} catch (Exception e) {
			}
			try {
				String value = cell.getStringCellValue();
				return sdf.parse(value);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public String findIBAN(XSSFRow row, ProcessData data) {
		for (Cell cell : row) {
			try {
				String value = cell.getStringCellValue();
				if (data.getBadenIBANs().matcher(value).matches()) {
					continue;
				}
				if (ibanPattern.matcher(value.trim()).matches()) {
					return value;
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	public BookingData findBooking(XSSFRow row, ProcessData data) {
		String value = getText(row);
		if (StringUtils.isNotBlank(value) && value.length() > 10) {
			List<Booking> bookings = bookingRepo.findByMemberNames(value, data.getActivity());
			if (!bookings.isEmpty()) {
				return new BookingData(value, bookings);
			}
		}
		return null;
	}

	public String getText(XSSFRow row) {
		StringBuilder sb = new StringBuilder();
		for (Cell cell : row) {
			try {
				sb.append(cell.getStringCellValue()).append(" : ");
			} catch (Exception e) {}
		}
		return sb.toString();
	}

	public static class AmountData implements Serializable {
		public final int index;
		public final Double amount;
		public final boolean acconto;

		public AmountData(int index, Double amount) {
			this.index = index;
			this.amount = amount;
			this.acconto = false;
		}

		public AmountData(int index, Double amount, boolean acconto) {
			this.index = index;
			this.amount = amount;
			this.acconto = acconto;
		}

		@Override
		public String toString() {
			return "AmountData [index=" + index + ", amount=" + amount + ", acconto=" + acconto + "]";
		}
	}

	public static class BookingData implements Serializable {
		public final String text;
		public final List<Booking> bookings;

		public BookingData(String text, List<Booking> bookings) {
			this.text = text;
			this.bookings = bookings;
		}

		@Override
		public String toString() {
			return "BookingData [text=" + text + ", bookings=" + bookings + "]";
		}
	}
}
