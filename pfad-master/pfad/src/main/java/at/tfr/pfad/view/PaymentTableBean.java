/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.view;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.Stateful;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import at.tfr.pfad.model.Payment;
import at.tfr.pfad.util.ColumnModel;

@Named
@ViewScoped
@Stateful
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class PaymentTableBean extends BaseBean<Payment> {

	private static int cnt = 0;
	private String selectionMode = "multiple";
	private List<ColumnModel> columns = new ArrayList<>();

	@Inject
	private transient PaymentDataModel paymentDataModel;

	public PaymentTableBean() {
	}

	@PostConstruct
	public void postConstruct() {
		columns.add(new ColumnModel("id", "ID", 0).search(false));
		columns.add(new ColumnModel("payer", "Zahler", 1).minLength(2)
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true));
		columns.add(new ColumnModel("amount", "Betrag", 2)
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true));
		columns.add(new ColumnModel("member", "Mitglied", 3).minLength(2)
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true));
		columns.add(new ColumnModel("squad", "Trupp", 4).items(squadRepo.findDistinctName())
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true));
		columns.add(new ColumnModel("activity", "Aktivität", 5).minLength(2)
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true));
		columns.add(new ColumnModel("aconto", "Akto", 6).items(trueFalse)
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true));
		columns.add(new ColumnModel("finished", "Erledigt", 7).items(trueFalse)
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true));
		columns.add(new ColumnModel("comment", "Bemerkung", 8)
				.headerStyle("border: solid 3px red;").headerStyleNotEmpty(true));
		paymentDataModel.setColumns(columns);
		paymentDataModel.reloadRowData();
	}

	public List<ColumnModel> getColumns() {
		return columns;
	}
	
	public PaymentDataModel getDataModel() {
		return paymentDataModel;
	}

	/**
	 * @return the selectionMode
	 */
	public String getSelectionMode() {
		return selectionMode;
	}

	/**
	 * @param selectionMode
	 *            the selectionMode to set
	 */
	public void setSelectionMode(final String selectionMode) {
		this.selectionMode = selectionMode;
	}

	public int getCnt() {
		return ++cnt;
	}

	@Override
	public boolean isUpdateAllowed() {
		return isAdmin() || isGruppe() || isKassier();
	}
	
	@Override
	public void retrieve() {
	}
}
