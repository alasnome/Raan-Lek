package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)Raan.java 1.00 20120530
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* Raan: Data for a _RanLek user, with
* routines for loading, accessing, and saving the data.
*
* @author Rick Salamone
* @version 1.00
* 20120530 rts created
*******************************************************/
import com.shanebow.util.SBArray;
import com.shanebow.util.SBLog;
import com.shanebow.util.TextFile;
import java.io.BufferedReader;
import java.io.FileReader; 
import java.io.PrintWriter;
import java.io.IOException; 
import java.lang.reflect.Constructor;
import java.util.*;
import javax.swing.event.EventListenerList;

public final class Raan
	{
	static String EXT_STRUCT = ".rls";
	static String EXT_DATA = ".rld";
	private static final String SEPARATOR="§";

	// static public interface
	private static Raan _current;
	public static Raan current() { return _current; }
	public static void open(String aName)
		throws Exception
		{
		if (_current != null && _current.fRaanName.equals(aName))
			return;
		_current = new Raan(aName);
		fireRaanAction(new RaanEvent(_current, RaanEvent.SET_CURRENT));
		}

	public static void save()
		{
		_current.freeze();
		}

/**
	public static Account findAccount(int aAccountID)
		{
		return _current.getAccountList().find(aAccountID);
		}
**/

	// instance data & methods
	private final String fRaanName;
	private final SBArray<Transaction> fTransactions = new SBArray<Transaction>(100);
	private int fLastAccountID;
	private int fNextTransactionID;

	/**
	* The questions displayed in column zero of the table, in order
	*/
	private final AccountList fAccounts = new AccountList();

	/**
	* the column data - each column contains responses for a particular date
	*/
	private final SBArray<DailyResponses> fDates = new SBArray<DailyResponses>(0);

	/**
	* RaanListener support is implemented as a static list and add/remove methods,
	* because listeners will remain interested regardless of the current Raan
	*/
	static final EventListenerList _listeners = new EventListenerList();

	public static void addRaanListener(RaanListener l)
		{
		_listeners.add(RaanListener.class, l);
		}

	public void removeRaanListener(RaanListener l)
		{
		_listeners.remove(RaanListener.class, l);
		}

	// Notify all listeners that have registered interest for
	// notification on this event type.  The event instance is
	// lazily created using the parameters passed into this method.
	// 

	private static void fireRaanAction(RaanEvent userEvent)
		{
		// Guaranteed to return a non-null array
		Object[] listeners = _listeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2)
			if (listeners[i]==RaanListener.class)
				((RaanListener)listeners[i+1]).userAction(userEvent);
		}

	public static Transaction findTransaction(
		long[] aWhen, Account aAccount, String aWho)
		{
		int accountID = aAccount.id();
		for (Transaction t : _current.fTransactions)
			if (t.accountID() == accountID
			&& t.inRange(aWhen)
			&& ((aWho == null) || aWho.equals(t.entity())))
				return t;
		return null;
		}

	public static List<Transaction> fetchTransactions(long[] aWhen)
		{
		Vector<Transaction> it = new Vector<Transaction>();
		for (Transaction t : _current.fTransactions)
			if (t.inRange(aWhen))
				it.add(t);
		it.trimToSize();
		return it;
		}

	public static AccountList getAccounts() { return _current.fAccounts; }
	public static Category[] getCategories() { return Category._all; }
	public static Account addAccount(int aNum, int aCatID, char aType,
		String aEnglish, String aThai)
		{
		return _current.addAcnt(aNum, aCatID, aType, aEnglish, aThai);
		}

	public static void deleteAccount(Account aAccount)
		{
		_current.delAcnt(aAccount);
		}

	public static void accountModified(Account aAccount)
		{
		_current.acntModified(aAccount);
		}

	public static List<Transaction> getTransactions(int aAccountID)
		{
		Vector<Transaction> it = new Vector<Transaction>();
		if ( aAccountID == 0 ) it.addAll(_current.fTransactions);
		else for (Transaction t : _current.fTransactions)
			if (aAccountID == t.accountID())
				it.add(t);
		it.trimToSize();
		return it;
		}

	public static Transaction addTransaction(long when, int amt, int accountID,
		String aEntity, String aMemo)
		{
		return _current.addTrans(when, amt, accountID, aEntity, aMemo);
		}

	public static void deleteTransaction(Transaction aTransaction)
		{
		_current.delTrans(aTransaction);
		}

	public static void transactionModified(Transaction aTransaction)
		{
		_current.transModified(aTransaction);
		}

	private Raan(String aRaanName)
		throws Exception
		{
		fRaanName = aRaanName;
		thawStructure();
		thawTransactions();
		}

	public AccountList getAccountList() { return fAccounts; }

	public Account addAcnt(int aNum, int aCatID, char aType,
		String aEnglish, String aThai)
		{
		int id = ++fLastAccountID;
		String csv = "" + aCatID
		      + SEPARATOR + id
		      + SEPARATOR + aType
		      + SEPARATOR + aEnglish
		      + SEPARATOR + HtmlParser.javaToHtml(aThai);
		Account acnt = new Account(csv);
		fAccounts.add(acnt);
		freezeStructure();
		fireRaanAction(new RaanEvent(this, RaanEvent.ACNT_ADDED, acnt));
		return acnt;
		}

	private void acntModified(Account aAccount)
		{
		freezeStructure();
		fireRaanAction(new RaanEvent(this, RaanEvent.ACNT_MODIFIED, aAccount));
		}

	private boolean delAcnt(Account aAccount)
		{
// @TODO : implement!!
System.out.println("deleteAccount Not implemented");
/*********
First Check for transations using this account....
		synchronized(fAccounts)
			{
			int i = fAccounts.binarySearch(aTransaction.hashCode());
			if ( i < 0 )
				return false;
			fAccounts.removePack(i);
			}
		freezeStructure();
		fireRaanAction(new RaanEvent(this, RaanEvent.ACNT_REMOVED, aAccount));
**********/
		return true;
		}

	private Transaction addTrans(long when, int aAmount, int aAccountID,
		String aEntity, String aMemo)
		{
		String csv = "" + fNextTransactionID + SEPARATOR + when
		     + SEPARATOR + aAccountID + SEPARATOR + aAmount
		     + SEPARATOR + "e" // aType
		     + SEPARATOR + aEntity
		     + SEPARATOR + aMemo;
		++fNextTransactionID;
		Transaction trans = new Transaction(csv);
		fTransactions.add(trans);
		TextFile.freeze(fTransactions, fRaanName + EXT_DATA, null );
		fireRaanAction(new RaanEvent(this, RaanEvent.TRANS_ADDED, trans));
		return trans;
		}

	private boolean delTrans(Transaction aTransaction)
		{
		synchronized(fTransactions)
			{
			int i = fTransactions.binarySearch(aTransaction.hashCode());
			if ( i < 0 )
				return false;
			fTransactions.removePack(i);
			}
		TextFile.freeze(fTransactions, fRaanName + EXT_DATA, null );
		fireRaanAction(new RaanEvent(this, RaanEvent.TRANS_REMOVED, aTransaction));
		return true;
		}

	private void transModified(Transaction aTransaction)
		{
		TextFile.freeze(fTransactions, fRaanName + EXT_DATA, null );
		fireRaanAction(new RaanEvent(this, RaanEvent.TRANS_MODIFIED, aTransaction));
		}

	private void freeze()
		{
		TextFile.freeze(fTransactions, fRaanName + EXT_DATA, null );
		freezeStructure();
		}

	public List<DailyResponses> getDailyResponses()
		{
		List<DailyResponses> it = new Vector<DailyResponses>();
		for (DailyResponses oneDay : fDates)
			it.add(oneDay);
		return it;
		}

	public DailyResponses getDailyResponses(int yyyymmdd)
		{
		int index = fDates.binarySearch(yyyymmdd);
		return (index < 0)? null : fDates.get(index);
		}

	private String filespec() { return fRaanName + ".rls"; }

	public void freezeStructure()
		{
		try
			{
			PrintWriter file = new PrintWriter(filespec());
			for ( Account q : fAccounts )
				file.println ( q.toCSV());
			file.close();
			}
		catch (IOException e)
			{
			System.err.println(filespec() + " Error: " + e.toString());
			}
		}

	private boolean thawStructure()
		throws Exception
		{
		String filespec = filespec();
		System.out.println ( "Raan.thawStructure(" + filespec + ")" );
		BufferedReader stream = null;
		try
			{
			log ( "Loading accounts from " + filespec );
			stream = new BufferedReader(new FileReader(filespec));
			Account.thaw(stream, fAccounts);
			int size = fAccounts.size();
			fLastAccountID = fAccounts.get(size-1).id();
			log ( "%d accounts, last id: %d", size, fLastAccountID );
			return true;
			}
		catch (IOException e) { System.out.println("ThawStruct error: " + e); e.printStackTrace(); throw(e); }
		finally { try { stream.close(); } catch (Exception ignore) {}}
		}

	private void thawTransactions()
		{
		String filespec = fRaanName + EXT_DATA;
		fTransactions.clear();
		try
			{
			log ( "Loading transactions from " + filespec );
			TextFile.thaw( Transaction.class, filespec, fTransactions, false );
			int size = fTransactions.size();
			fNextTransactionID = 1 + fTransactions.get(size-1).id();
			log ( "%d transactions, next id: %d", size, fNextTransactionID );
			}
		catch (Exception e)
			{
			log(filespec + " Error: " + e.toString());
			}
		}

	private void log(String fmt, Object... args)
		{
		SBLog.write(fRaanName, String.format(fmt, args));
		}
	}
