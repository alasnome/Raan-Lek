package com.alasnome.apps.RaanLek.register;
/********************************************************************
* @(#)TransactionTableModel.java 1.00 20121128
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* TransactionTableModel: Table model for Transactions.
*
* @version 1.00 20110814 rts
* @author Rick Salamone
* 20121128 rts created
*******************************************************/
import com.alasnome.apps.RaanLek.*;
import com.shanebow.util.SBDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class TransactionTableModel
	extends AbstractTableModel
	implements RaanListener
	{
	protected List<Transaction> fList = null;
	public  int fColumnCount = Transaction.FIELD_NAMES.length;

	// language
	private boolean fIsThai;

	// filtering
	private Account fAccount; // current account filter - null for no filtering

	// sorting
	private byte fSortColumn = -1;
	private int  fAscending; // 1 for ascending sort, -1 for descending

	TransactionTableModel()
		{
		super();
		Raan.addRaanListener(this);
		}

	public void reset( Account aAccount )
		{
		fAccount = aAccount;
		fList = Raan.getTransactions((aAccount==null)? 0 : aAccount.id());
		fireTableDataChanged();
		}

	public int    getColumnCount() { return fColumnCount; }
	public Class  getColumnClass(int c)
		{
		return (c==Transaction.ACCOUNT)? Account.class
		     : (c==Transaction.AMOUNT) ? Transaction.class
		                             : Transaction.FIELD_TYPES[c];
		}
	public String getColumnName(int c) { return Transaction.FIELD_NAMES[c]; }
	public int    getRowCount() { return (fList == null)? 0 : fList.size(); }
	public Object getValueAt(int r, int c)
		{
		Transaction t = fList.get(r);
		if (c==Transaction.ACCOUNT)
			return t.account();
		else if (c==Transaction.WHEN)
			return SBDate.mmddyy(t.time());
		else if (c==Transaction.AMOUNT)
			return t;
		else return t.get((byte)c);
		}

	public Transaction getRow(int r) { return fList.get(r); }

	public void sort(int aSortColumn, final boolean aIsAscending )
		{
		fSortColumn = (byte)aSortColumn;
		fAscending = aIsAscending? 1 : -1;
		Collections.sort(fList, new Comparator<Transaction>()
			{
			public int compare(Transaction r1, Transaction r2)
				{
				return fAscending * r1.compareTo(r2, fSortColumn);
				}
			});
		fireTableDataChanged();
		}

	private void appendLine(Transaction aTransaction)
		{
		int row = fList.size();
		fList.add(aTransaction);
		fireTableRowsInserted(row,row);
		}

	private void removeLine( int row ) // just remove from this list
		{
		Transaction line = fList.remove(row);
		fireTableRowsDeleted(row,row);
		}

	@Override public void userAction(RaanEvent aEvent)
		{
		int type = aEvent.type();
		if (type == RaanEvent.SET_CURRENT)
			{
			reset(null);
			return;
			}
		Object data = aEvent.getData();
		if ( !(data instanceof Transaction))
			return;
		Transaction transaction = (Transaction)data;
		boolean satisfiesFilter = satisfiesFilter(transaction);
		if ( type == RaanEvent.TRANS_MODIFIED )
			{
			int row = fList.indexOf(transaction);
			if ((row < 0) && satisfiesFilter)
				appendLine(transaction);
			if ((row >= 0) && satisfiesFilter)
				fireTableRowsUpdated(row, row);
			else if ((row >= 0) && !satisfiesFilter)
				removeLine(row);
			}
		if (!satisfiesFilter) // we won't add or remove
			return;
		if ( type == RaanEvent.TRANS_ADDED )
			{
			appendLine(transaction);
			}
		else if ( type == RaanEvent.TRANS_REMOVED )
			{
			int row = fList.indexOf(transaction);
			if ( row >= 0 ) removeLine(row);
			}
		}

	private boolean satisfiesFilter(Transaction aTransaction)
		{
		return (fAccount == null) // no filter!
		    || fAccount.id() == aTransaction.accountID();
		}

	void setThai(boolean aIsThai)
		{
		fIsThai = aIsThai;
		fireTableDataChanged();
		}
	}
