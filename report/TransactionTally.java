package com.alasnome.apps.RaanLek.report;
/********************************************************************
* @(#)TransactionTally.java 1.00 20100922
* Copyright © 2010-2012 by Richard T. Salamone, Jr. All rights reserved.
*
* TransactionTally: Summarizes a given set of transactions. Generally,
* there will be one transaction tally per account per day. However,
* there may also be tallies of transactions representing subtotals
* and totals.
*
* @author Rick Salamone
* @version 2.00
* 20100821 rts created for APO call stats app
* 20121204 rts search & replace to adapt to RaanLek app
*******************************************************/
import com.alasnome.apps.RaanLek.Transaction;

public final class TransactionTally
	{
	private int numTransactions = 0;
	private int totalAmount = 0;

	public void logTransaction( Transaction transaction )
		{
		++numTransactions;
		int amt = transaction.amount();
		if (transaction.account().isExpense())
			amt = -amt;
		totalAmount += amt;
		}

	public int count() { return numTransactions; }
	public int total() { return totalAmount; }

	public String htmlTotal()
		{
		if (totalAmount == 0) return "&nbsp;";
		return "<font color=" + ((totalAmount > 0) ? "BLUE>" : "RED>") + totalAmount + "</font>";
		}

	public String toString()
		{
		return "" + numTransactions + " transactions totaling " + totalAmount + " baht";
		}
	}
