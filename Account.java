package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)Account.java 1.00 20120526
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* Account: This is an accounting account which groups related transations.
* For instance rent would be an account containing a Transaction for each
* rent payment.
*
* @author Rick Salamone
* @version 1.00
* 20120526 rts created
*******************************************************/
import com.alasnome.apps.RaanLek.account.AccountType;
import java.io.*;
import java.util.List;

public final class Account
	implements Comparable<Account>
	{
	public static final byte ID = 0;
	public static final byte TYPE = 1;
	public static final byte ENGLISH = 2;
	public static final byte THAI = 3;
	public static final byte CATEGORY = 4;
	public static final byte NUM_FIELDS = 5;

	public static final String[] FIELD_NAMES =
		{
		"#",
		"Type",
		"English",
		"Thai",
		"Category",
		};

	public static final String[] LBL_THAI =
		{
		"#",
		"T Type",
		"T Anglit",
		"T Thai",
		"T Category",
		};

	public static final Class[] FIELD_TYPES =
		{
		Integer.class, // ID
		AccountType.class,
		String.class,  // ENGLISH
		String.class,  // THAI
		Category.class,
		};

	public static final int[] FIELD_WIDTHS =
		{
		10, // ID
		10, // TYPE
		25, // ENGLISH
		25, // THAI
		25, // CATEGORY
		};

	private static final String SEPARATOR="§";
	private static final String INCOME="+";
	private static final String EXPENSE="-";

	private final int fID;
	private Category fCategory;
	private AccountType fType;
	private String fEnglish;
	private String fThai;

	public final int id()            { return fID; }
	public final Category category() { return fCategory; }
	public final AccountType type()  { return fType; }
	public final String name()       { return fEnglish; }
	public final String prompt()     { return fThai; }
	public final String getEnglish() { return fEnglish; }
	public final String getThai()    { return fThai; }
	public final boolean isExpense() { return AccountType.EXPENSE == fType; }

	public final void setCategory(Category aCat) { fCategory = aCat; }
	public final void setEnglish(String aEnglish) { fEnglish = aEnglish; }
	public final void setThai(String aThai) { fThai = aThai; }
	public final void setType(AccountType aType) { fType = aType; }

	@Override public boolean equals(Object aOther)
		{
		return (aOther instanceof Account)? equals((Account)aOther) : false;
		}

	public final boolean equals(Account aOther)
		{
		return (aOther != null) && fID == aOther.fID;
		}

	@Override public int hashCode() { return fID; }
	@Override public int compareTo(Account other) { return hashCode() - other.hashCode(); }

	/**
	* Gets a field from this record
	* @param byte field number: ENGLISH, THAI, CHAPTER, etc
	* @return Object field value
	*/
	public Object get(byte aField)
		{
		return (aField == ENGLISH) ? getEnglish()
		     : (aField == THAI) ? getThai()
		     : (aField == CATEGORY) ? fCategory
		     : (aField == TYPE) ? fType
		     : fID;
		}

	public static void thaw(BufferedReader stream, AccountList accounts)
		throws Exception
		{
		String csv;
		int line = 0;
		while ((csv = stream.readLine()) != null )
			{
			++line;
			csv = csv.trim();
			if ( csv.isEmpty() || csv.startsWith("#"))
				continue;
			try { accounts.add(new Account(csv)); }
			catch (Exception e) { System.out.println("Account file corrupt line " + line + ": " + csv); }
			}
		}

	public Account(String csv)
		{
// System.out.println("Account(" + csv + ")");
		String[] split = csv.split(SEPARATOR,5);
		fCategory = Category.find(Integer.parseInt(split[0]));
		fID = new Integer(split[1]);
		fType = AccountType.find(split[2].charAt(0));
		fEnglish = split[3].trim();
		fThai = HtmlParser.htmlToJava(split[4].trim());
		}

	private String paddedID()
		{
		String it = "" + fID;
		while (it.length() < 4)
			it = "0" + it;
		return it;
		}

	public String toString(boolean thai)
		{
		return paddedID() + ": " + (thai? fThai : fEnglish);
		}

	public String toString()
		{
		return paddedID() + ": " + fEnglish;
//		return "Account " + fID + ": " + fEnglish;
		}

	public String toCSV()
		{
		return "" + fCategory.id()
		      + SEPARATOR + fID
		      + SEPARATOR + fType
		      + SEPARATOR + fEnglish
		      + SEPARATOR + HtmlParser.javaToHtml(fThai);
		}

	public boolean contains(String lowerCaseSearchString)
		{
		return fEnglish.toLowerCase().contains(lowerCaseSearchString)
		    || fThai.contains(lowerCaseSearchString);
		}
	}
