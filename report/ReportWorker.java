package com.alasnome.apps.RaanLek.report;
/********************************************************************
* @(#)ReportWorker.java 1.00 20100821
* Copyright © 2010-2012 by Richard T. Salamone, Jr. All rights reserved.
*
* ReportWorker: Creates an html formatted report based on criteria
* passed in (from the UI).
*
* @author Rick Salamone
* @version 1.00
* 20100821 rts created for APO call stats app
* 20121204 rts search & replace to adapt to RaanLek app
* 20121205 rts decoupled from the ui, prep for background processing
*******************************************************/
import com.alasnome.apps.RaanLek.Account;
import com.alasnome.apps.RaanLek.Raan;
import com.alasnome.apps.RaanLek.Transaction;
import com.shanebow.dao.Duration;
import com.shanebow.util.SBArray;
import com.shanebow.util.SBDate;
import java.util.*;

class ReportWorker
	{
	boolean fCreateBucketsOnTheFly;

	/**
	* A bucket for each account that may be binary searched using the account id
	* Each bucket counts the total number and amount of transactions for the given
	* account. In addition, each bucket contains buckets for tallying the transactions
	* by time period.
	*/
	SBArray<AccountBucket> accountBuckets;
	SBArray<TimePeriod> fTimePeriods;

	private void buildDateList( long[] dateRange, boolean aIsMonthly )
		{
System.out.println("buildDateList: "
+ SBDate.yyyymmdd__hhmmss(dateRange[0])
+ "-" + SBDate.yyyymmdd__hhmmss(dateRange[1]));
		fTimePeriods = new SBArray<TimePeriod>(10);
		if (aIsMonthly)
			{
			int year = SBDate.getYear(dateRange[0]);
			int month = SBDate.getMonth(dateRange[0]);
			int finalYYYYMM = SBDate.getYear(dateRange[1]) * 100
			                + SBDate.getMonth(dateRange[1]);
			do
				{
				fTimePeriods.add( new TimePeriod(year, month++));
				if ( month > 12 ) { ++year; month = 1; }
				}
			while ((year * 100 + month) <= finalYYYYMM);
			}
		else // daily
			{
			long[] range = new long[2];
			for ( long time = dateRange[0]; time <= dateRange[1]; )
				{
				range[0] = time;
				range[1] = SBDate.addDays(time, 1) - 1; // one sec before next day
				fTimePeriods.add( new TimePeriod(range, SBDate.mmdd(time)));
				time = 1 + range[1];
				}
			}
/********/
		System.out.println("Time Periods");
		for (TimePeriod tp : fTimePeriods)
			System.out.println("  " + tp);
		}

	private void log( String fmt, Object... args )
		{
		com.shanebow.util.SBLog.write ( String.format(fmt, args ));
		}

	private void createAccountBuckets(Account[] aAccounts, int numDays)
		{
		if (aAccounts == null || aAccounts.length == 0)
			{
			accountBuckets = new SBArray<AccountBucket>(10);
			fCreateBucketsOnTheFly = true;
			}
		else
			{
			accountBuckets = new SBArray<AccountBucket>(aAccounts.length);
			fCreateBucketsOnTheFly = false;
			for (Account acnt : aAccounts)
				accountBuckets.add(new AccountBucket(acnt, numDays));
			}
		}

	HTMLBuilder eventsFor( long[] dates, Account[] aAccounts,
		int viewerWidth, boolean aIsMonthly )
		{
		HTMLBuilder html = new HTMLBuilder();
		html.clear();
		String when;
		if (dates == null) {
			dates = new long[]{0, SBDate.MAX_TIME};
			when = "All Dates";
			}
		else {
			String mmddyy1 = SBDate.mmddyy(dates[1]);
			when = (dates[0] == 0)? "before"
			     : (dates[0] == dates[1])? "on"
			     : (SBDate.mmddyy(dates[0]) + " -");
			when += " " + mmddyy1;
			}
		html.header(2, "Report for: " + when );
			
		try
			{
			long startTime = SBDate.timeNow();
//			List<Transaction> transactions = Raan.fetchTransactions( dates, where );
			List<Transaction> transactions = Raan.fetchTransactions(dates);
			if ( transactions.size() <= 0 )
				{
				html.append("No transactions found that meet the specified criteria");
				return html;
				}
			int numTrans = transactions.size();
			java.util.Collections.sort(transactions,new Comparator<Transaction>()
				{
				public int compare(Transaction r1, Transaction r2)
					{
					long x1 = r1.time();
					long x2 = r2.time();
					return (x1 > x2)? 1 : (x1 < x2)? -1 : 0;
					}
				});

			dates[0] = transactions.get(0).time();
			dates[1] = transactions.get(numTrans-1).time();

			buildDateList(dates, aIsMonthly);
			int numPeriods = fTimePeriods.size();
			createAccountBuckets(aAccounts, numPeriods);
			TransactionTally[] transactionsForDay = new TransactionTally[numPeriods];
			for ( int i = 0; i < numPeriods; i++ )
				transactionsForDay[i] = new TransactionTally();
			TransactionTally transTotal = new TransactionTally();

			for ( Transaction transaction : transactions )
				{
				String yyyymmdd = SBDate.yyyymmdd(transaction.time());
	//			int dayNum = Collections.binarySearch(dateList, yyyymmdd);
				int iyyyymmdd = Integer.parseInt(yyyymmdd);
				int period = fTimePeriods.binarySearch(iyyyymmdd);
				if (period < 0) // not found
					period = -period-1;

				if (!bucketize(transaction, period, numPeriods))
					continue; // we are not interested in this transaction's account
				transactionsForDay[period].logTransaction(transaction);
				transTotal.logTransaction(transaction);
				}

		// Summary table
		//	int[] widths = { 20, 16, 27, 34, 7 };
			String[] titles = new String[numPeriods + 2];
			titles[0] = "Account";
			for ( int i = 0; i < numPeriods; i++ )
				titles[i + 1] = fTimePeriods.get(i).name();
			titles[numPeriods+1] = "Total";
			html.tableBegin( viewerWidth -30, 1, 1 );
			html.tableHeader( titles ); // , widths );
			for ( AccountBucket account : accountBuckets )
				{
				html.append( "<TR><TD>" + account.getAccount() + "</TD>" );
				for ( int i = 0; i < numPeriods; i++ )
					html.append( "<td align=right>" + account.getTally(i).htmlTotal() + "</td>" );
				html.append( "<td align=right><b>" + account.getTotals().htmlTotal() + "</b></td></tr>" );
				}
			html.append( "<TR><TD><B>Total</B></TD>" );
			for ( int i = 0; i < numPeriods; i++ )
				html.append( "<td align=right><b>" + transactionsForDay[i].htmlTotal() + "</b></td>" );
			html.append( "<td align=right><b>" + transTotal.htmlTotal() + "</b></td></tr>" );
			html.tableEnd();

html.tableBegin( viewerWidth -30, 0, 1 );
html.append( "<TR><TD valign=\"top\">");
			html.header( 3, "Account Totals " );
			for ( AccountBucket accountTally : accountBuckets )
				html.listItem( accountTally.getAccount() + " " + accountTally.htmlTotal());
html.append( "</TD><TD valign=\"top\">" );

			html.header( 3, "Daily Totals" );
			for ( int i = 0; i < numPeriods; i++ )
				html.listItem( fTimePeriods.get(i).name() + " <b>" + transactionsForDay[i].count()
//				html.listItem( dateList.get(i) + " <b>" + transactionsForDay[i].count()
				                + "</b> transactions totaling "
				                + transactionsForDay[i].htmlTotal());
			html.listItem( transTotal.toString());
html.append( "</TD></TR>");
html.tableEnd();

			html.append( "<BR><HR><BR>Fetch time: "
			        + new Duration( SBDate.timeNow() - startTime ));
			}
		catch (Exception e) { html.header(3, "Error: " + e ); e.printStackTrace(); }
		return html;
		}

	boolean bucketize(Transaction transaction, int dayNum, int numDays )
		{
		AccountBucket it;
		Account account = transaction.account();
		int index = accountBuckets.binarySearch(account.hashCode());
		if ( index < 0 ) // not found
			{
			if (!fCreateBucketsOnTheFly) return false;
			index = -index-1;
			it = new AccountBucket(account, numDays );
			accountBuckets.add(index, it);
			}
		else it = accountBuckets.get(index);
		it.logTransaction( dayNum, transaction );
		return true;
		}
	}

class AccountBucket
	implements Comparable<Account>
	{
	private final Account m_account;
	private final TransactionTally[] transactionsForDay;
	private final TransactionTally   totals = new TransactionTally();

	public AccountBucket(Account account, int numDays)
		{
		m_account = account;
		transactionsForDay = new TransactionTally[numDays];
		for ( int i = 0; i < numDays; i++ )
			transactionsForDay[i] = new TransactionTally();
		}

	public final Account getAccount() { return m_account; }
	public final TransactionTally   getTally( int dayNum ) { return transactionsForDay[dayNum]; }
	public final TransactionTally   getTotals()            { return totals; }

	public String htmlTotal()
		{
		return totals.htmlTotal();
		}

	@Override public int hashCode() { return m_account.hashCode(); }

	public void logTransaction( int dayNum, Transaction transaction )
		{
		transactionsForDay[dayNum].logTransaction(transaction);
		totals.logTransaction(transaction);
		}

	public final int compareTo(Account account)
		{
		return this.m_account.compareTo(account);
		}

	public final boolean equals(Account account)
		{
		return this.m_account.equals(account);
		}
	}

final class TimePeriod
	{
	private final long[] fTimeRange = new long[2];
	private final String fName; // returned by toString?
	private final int fHashCode;

	public TimePeriod(int year, int month)
		{
		long[] range = new long[2];
		fTimeRange[0] = timeOfFirst(year, month);
		fTimeRange[1] = timeOfFirst(year, month + 1) -1; // one sec before 1st next month
		fName = SBDate.MONTH_NAMES[month] + " " + (year % 100);
		fHashCode = calcHash();
		}

	private long timeOfFirst(int year, int month)
		{
		return SBDate.toTime("" + year + ((month < 10)? "0" : "") + month + "01  00:00:00");
		}

	public TimePeriod(long[] aDateRange, String aName)
		{
		fTimeRange[0] = aDateRange[0];
		fTimeRange[1] = aDateRange[1];
		fName = aName;
		fHashCode = calcHash();
		}
	public String name() { return fName; }

	private int calcHash()
		{
		return Integer.parseInt(SBDate.yyyymmdd(fTimeRange[1]));
		}

	@Override public int hashCode() { return fHashCode; }
	@Override public String toString()
		{
		return fName + ": " + fHashCode
		 + ", " + SBDate.yyyymmdd__hhmmss(fTimeRange[0])
		 + "-" + SBDate.yyyymmdd__hhmmss(fTimeRange[1]);
		}
	}
