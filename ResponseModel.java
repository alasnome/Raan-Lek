package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)ResponseModel.java 1.00 20120529
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* ResponseModel:
*
* @author Rick Salamone
* @version 1.00
* 20120529 rts created
*******************************************************/
import com.shanebow.util.SBArray;
import com.shanebow.util.SBDate;
import com.shanebow.util.SBProperties;
import java.util.List;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

public final class ResponseModel
	extends AbstractTableModel
	implements RaanListener
	{
	// ctor code
		{
		Raan.addRaanListener(this);
		}

	/**
	* The account displayed in column zero of the table, in order
	*/
	private AccountList fAccounts;

	/**
	* the column data - each column contains responses for a particular date
	*/
	private List<DailyResponses> fDates;

	/**
	* the current date in yyyymm format
	*/
	private String fCurrentMonth;
	private int fDaysInMonth;

	/** implement RaanListener interface */
	@Override public void userAction(RaanEvent aRaanEvent)
		{
		if ( aRaanEvent.getID() != RaanEvent.SET_CURRENT )
			return;
		Raan user = aRaanEvent.getRaan();
		fAccounts = user.getAccountList();
		fDates = user.getDailyResponses();
		fCurrentMonth = null;
		setDate(SBDate.yyyymmdd()); // calls fireTableStructureChanged();
		}

	public void setDate(String yyyymmdd)
		{
System.out.println("ResponseModel.setDate: " + yyyymmdd);
		String yyyymm = yyyymmdd.substring(0,6);
		if (fCurrentMonth == null
		|| !yyyymm.equals(fCurrentMonth))
			{
			fDaysInMonth = SBDate.daysInMonth(yyyymmdd);
			fCurrentMonth = yyyymm;
			fireTableStructureChanged();
			}
		// @TODO: code to select column here...
		}

	@Override public int getColumnCount()
		{
		return (fAccounts==null)? 0 : fDaysInMonth + 1;
		}

	@Override public String getColumnName(int c)
		{
		return (c == 0)? fCurrentMonth : "" + c;
		}

	@Override public int getRowCount()
		{
		return (fAccounts==null)? 0 : fAccounts.size();
		}

	@Override public Object getValueAt(int r, int c)
		{
		try {
		Account account = fAccounts.getRow(r);
		return (c==0)? account.name()
		             : fDates.get(c-1).getResponseFor(account).value();
			}
		catch (Exception e) { return "" + r + ", " + c; }
		}
	}
