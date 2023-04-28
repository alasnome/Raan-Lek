package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)Transaction.java 1.00 20120526
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* Transaction:
*
* @author Rick Salamone
* @version 1.00
* 20120526 rts created
*******************************************************/

public final class Transaction
	implements Comparable<Transaction>
	{
	public static final byte ID = 0;
	public static final byte WHEN = 1;
	public static final byte AMOUNT = 2;
	public static final byte ACCOUNT = 3;
	public static final byte WHO = 4;
	public static final byte MEMO = 5;

	/**
	* Gets a field from this record
	* @param byte field number: ID, WHEN, ACCOUNT, etc
	* @return Object field value
	*/
	public Object get(byte aField)
		{
		return (aField == WHEN) ? fTime
		     : (aField == AMOUNT) ? fAmount
		     : (aField == ACCOUNT) ? fAccountID // Raan.findAccount(fAccountID)
		     : (aField == WHO) ? fEntity
		     : (aField == MEMO) ? fMemo
		     : fID;
		}

	public static final String[] FIELD_NAMES =
		{
		"#",
		"When",
		"Amount",
		"Account",
		"Who",
		"Memo",
		};

	public static final String[] LBL_THAI =
		{
		"#",
		"T When",
		"T Amount",
		"T Account",
		"T Who",
		"T Memo",
		};

	public static final Class[] FIELD_TYPES =
		{
		Integer.class, // ID
		Long.class,    // When
		Integer.class, // Amount
		Integer.class, // Account.class, // Account
		String.class,  // Who
		String.class,  // Memo
		};

	public static final int[] FIELD_WIDTHS =
		{
		5, // ID
		10,    // When
		10, // Amount
		20, // Account.class, // Account
		20,  // Who
		25,  // Memo
		};

	private static final String SEPARATOR="§";
	private static final char INCOME='+';
	private static final char EXPENSE='-';

	private final int  fID;
	private int  fAmount;
	private long fTime;
	private int  fAccountID;
	private Account fAccount;
	private String fEntity;
	private String fMemo;
	private char fType;

	public final int id()        { return fID; }
	public final int amount()    { return fAmount; }
	public final int accountID() { return fAccountID; }
	public final String entity() { return fEntity; }
	public final String memo()   { return fMemo; }
	public final long time()     { return fTime; }
	public final Account account()
		{
		if (fAccount == null)
			fAccount = Raan.getAccounts().find(fAccountID);
		return fAccount;
		}

	public final boolean inRange(long[] aWhen)
		{
		return fTime >= aWhen[0] && fTime <= aWhen[1];
		}

	public final void setAccount(Account aAccount) { fAccountID = aAccount.id(); }
	public final void setAccountID(int aAccountID) { fAccountID = aAccountID; }
	public final void setAmount(int aAmount) { fAmount = aAmount; }
	public final void setEntity(String aWho) { fEntity = aWho; }
	public final void setMemo(String aMemo) { fMemo = aMemo; }
	public final void setDate(long aWhen) { fTime = aWhen; }

	public Transaction(String csv)
		{
		String[] split = csv.split(SEPARATOR,7);
		fID = Integer.parseInt(split[0]);
		fTime = Long.parseLong(split[1]);
		fAccountID = Integer.parseInt(split[2]);
		fAmount = Integer.parseInt(split[3]);
		fType = (split[4].charAt(0) == INCOME)? INCOME : EXPENSE;
		fEntity = split[5].trim();
		fMemo = split[6].trim();
		}

	/**
	* toString() must be the exact inverse of the ctor that takes a String
	* argument - this method creates a csv that the ctor parses.
	*/
	public String toString()
		{
		return "" + fID + SEPARATOR + fTime
		     + SEPARATOR + fAccountID
		     + SEPARATOR + fAmount
		     + SEPARATOR + fType
		     + SEPARATOR + fEntity
		     + SEPARATOR + fMemo;
		}

	public boolean contains(String lowerCaseSearchString)
		{
		return fEntity.toLowerCase().contains(lowerCaseSearchString)
		    || fMemo.toLowerCase().contains(lowerCaseSearchString);
		}

	@Override public int hashCode() { return fID; }
	@Override public int compareTo(Transaction other) { return hashCode() - other.hashCode(); }
	public int compareTo(Transaction aOther, byte aField)
		{
		int compare
		    = (aField == WHEN)   ? compare(fTime, aOther.fTime)
		    : (aField == AMOUNT) ? fAmount - aOther.fAmount
		    : (aField == ACCOUNT) ? fAccountID - aOther.fAccountID
		    : (aField == WHO) ? fEntity.toLowerCase().compareTo(aOther.fEntity.toLowerCase())
		    : (aField == MEMO) ? fMemo.toLowerCase().compareTo(aOther.fMemo.toLowerCase())
		    : fID - aOther.fID;
		return (compare==0)? fID - aOther.fID : compare;
		}
	private int compare(long x1, long x2)
		{
		return (x1 > x2)? 1 : (x1 < x2)? -1 : 0;
		}
	}
