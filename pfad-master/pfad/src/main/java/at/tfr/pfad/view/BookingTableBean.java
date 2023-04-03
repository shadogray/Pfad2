/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Stateful;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.tfr.pfad.BookingStatus;
import at.tfr.pfad.util.ColumnModel;
import at.tfr.pfad.view.convert.BookingStatusConverter;
import at.tfr.pfad.view.convert.TrueFalseTristateConverter;

@Named
@ViewScoped
@Stateful
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class BookingTableBean extends BaseBean {

	private static int cnt = 0;
	private String selectionMode = "multiple";
	private List<ColumnModel> columns = new ArrayList<>();
	protected final Map<String,String> jaNeinAnz = new LinkedHashMap<>();
	protected final Map<String,BookingStatus> createdStorno = new LinkedHashMap<>();

	@Inject
	private transient BookingDataModel bookingDataModel;
	private TrueFalseTristateConverter trueFalseConverter = new TrueFalseTristateConverter();
	private BookingStatusConverter bookStatConverter = new BookingStatusConverter();

	public BookingTableBean() {
		jaNeinAnz.put("Ja", "true");
		jaNeinAnz.put("Nein", "false");
		jaNeinAnz.put("Anz", "anz");
		jaNeinAnz.put("Frei", "free");
		jaNeinAnz.put("keine", "none");
		createdStorno.put("Erst.", BookingStatus.created);
		createdStorno.put("Storno", BookingStatus.storno);
	}

	@PostConstruct
	public void postConstruct() {
		columns.add(new ColumnModel("id", "ID", 0).search(false));
		columns.add(new ColumnModel("member", "Mitglied", 1, true).minLength(2)
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true));
		columns.add(new ColumnModel("strasse", "Strasse", 2).minLength(2)
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true)); //.items(memberRepo.findDistinctStrasse()));
		columns.add(new ColumnModel("ort", "Ort", 3).items(memberRepo.findDistinctOrt())
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true));
		columns.add(new ColumnModel("aktiv", "Aktiv", 4, true).items(trueFalse).colConverter(trueFalseConverter)
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true));
		columns.add(new ColumnModel("activity", "Aktivität", 5)
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true)); //.items(activityRepo.findDistinctName()));
		columns.add(new ColumnModel("activityFinished", "Beendet", 6).items(trueFalse).filter("false").colConverter(trueFalseConverter)
				.headerStyle("border: solid 3px red;").headerStyleValue("false").headerStyleNotEmpty(true)); //.items(activityRepo.findDistinctName()));
		columns.add(new ColumnModel("squadName", "Trupp", 7).items(squadRepo.findDistinctName())
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true));
		columns.add(new ColumnModel("status", "Status", 8).items(createdStorno).colConverter(bookStatConverter)
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true));
		columns.add(new ColumnModel("payed", "Bezahlt", 9).items(jaNeinAnz)
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true));
		columns.add(new ColumnModel("comment", "Bemerkung", 10)
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true));
		bookingDataModel.setColumns(columns);
		bookingDataModel.reloadRowData();
	}

	public List<ColumnModel> getColumns() {
		return columns;
	}

	public BookingDataModel getDataModel() {
		return bookingDataModel;
	}

	/**
	 * @return the selectionMode
	 */
	public String getSelectionMode() {
		return selectionMode;
	}

	/**
	 * @param selectionMode the selectionMode to set
	 */
	public void setSelectionMode(final String selectionMode) {
		this.selectionMode = selectionMode;
	}

	public int getCnt() {
		return ++cnt;
	}
	
	@Override
	public boolean isUpdateAllowed() {
		return sessionBean.isTruppsAllowed();
	}
	
	@Override
	public void retrieve() {
	}
}
