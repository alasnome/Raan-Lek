package com.alasnome.apps.RaanLek.account;
/********************************************************************
* @(#)AccountType.java	1.00 20121202
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* AccountType: A glorified enumeration consisting of the two values
* INCOME and EXPENSE. Glorified in that is supports thai and english.
*
* @author Rick Salamone
* @version 20121202 rts created
*******************************************************/
import com.shanebow.dao.DataField;
import com.shanebow.dao.DataFieldException;
import com.shanebow.util.SBArray;
import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class AccountType
	implements Comparable<AccountType>
	{
	public static final AccountType INCOME  = new AccountType('+', "Income",  "T +", Color.BLUE);
	public static final AccountType EXPENSE = new AccountType('-', "Expense", "T -", Color.RED);

	public static final AccountType[] ALL = { INCOME, EXPENSE };

	public static AccountType find(char aSymbol)
		{
		return (aSymbol == INCOME.fSymbol) ? INCOME : EXPENSE;
		}

	private final char    fSymbol;
	private final String  fEnglish;
	private final String  fThai;
	private final Color   fColor;

	private AccountType( char aSymbol, String aEnglish, String aThai, Color aColor)
		{
		fSymbol = aSymbol;
		fEnglish = aEnglish;
		fThai = aThai;
		fColor = aColor;
		}

	@Override public int compareTo(AccountType other)
		{
		return fSymbol - other.fSymbol;
		}

	@Override public boolean equals(Object other)
		{
		if ( other == null ) return false;
		if ( !(other instanceof AccountType)) return false;
		return ((AccountType)other).fSymbol == this.fSymbol;
		}

	public Color color() { return fColor; }
	public char   symbol() { return fSymbol; }
	public String toString()   { return "" + fSymbol; }
	public String english() { return fEnglish; }
	public String thai() { return fThai; }
	public String csvRepresentation() { return "" + fSymbol; }
	public String dbRepresentation()  { return "" + fSymbol; }
	}
