package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)AccountList.java 1.00 20121123
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* AccountList: Maintains a list of Account objects that can be
* displayed in a table.
*
* @author Rick Salamone
* @version 1.00
* 20121123 rts created
*******************************************************/
import com.shanebow.util.SBArray;
import com.shanebow.util.SBDate;
import com.shanebow.util.SBProperties;
import java.util.Iterator;
import javax.swing.table.AbstractTableModel;

public final class AccountList
	extends AbstractTableModel
	implements Iterable<Account>
	{
	/**
	* All of the accounts
	*/
	private final SBArray<Account> fList = new SBArray<Account>(30);
	private boolean fIsThai;

	public void add(Account aAccount) { fList.insert(aAccount); }
	public Account get(int r) { return fList.get(r); }
	public Account getRow(int r) { return fList.get(r); }
	public int size() { return fList.size(); }
	public Iterator<Account> iterator() { return fList.iterator(); }

	public Account find(int aAccountID) { return fList.bsearch(aAccountID); }

	@Override public int getColumnCount() { return Account.NUM_FIELDS; }
	public Class  getColumnClass(int c) { return Account.FIELD_TYPES[c]; }
	public String getColumnName(int c)
		{
		return fIsThai? Account.LBL_THAI[c] : Account.FIELD_NAMES[c];
		}
	public int    getRowCount() { return (fList == null)? 0 : fList.size(); }
	public Object getValueAt(int r, int c) { return fList.get(r).get((byte)c); }

	public void   setThai(boolean aIsThai)
		{
		fIsThai = aIsThai;
		fireTableStructureChanged(); // actually just want to repaint col heads
		}
	}
