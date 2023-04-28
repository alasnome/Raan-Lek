package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)DailyResponses.java 1.00 20120529
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* DailyResponses: A list of responses for a given date. The responses are
* stored in an SBArray sorted by the question id for each response which
* facilitates quick retrieval (via binary search).
*
* Note that objects of this class use their date (as an integer in yyyymmdd)
* format as for their hashCode. We assume that there will be only one of these
* per date in any given SBArray or hash table/map.
*
* @author Rick Salamone
* @version 1.00
* 20120529 rts created
*******************************************************/
import com.shanebow.util.SBArray;

final class DailyResponses
	implements Comparable<DailyResponses>
	{
	/**
	* Adds this Response to the list for today, if it's not already
	* present. Keeps Responses sorted by questionID for quick retrieval.
	*/
	public void add(Response aResponse) { fResponses.insert(aResponse); }

	/**
	* @return the Response having the specified questionID or null if
	* not found.
	*/
	public Response getResponseFor(Account aAccount)
		{
		int index = fResponses.binarySearch(aAccount.id());
		return (index < 0)? null : fResponses.get(index);
		}

	public int yyyymmdd() { return fyyyymmdd; }

	@Override public String toString()
		{
		return "DailyResponses for " + fyyyymmdd;
		}

	@Override public int hashCode() { return fyyyymmdd; }

	@Override public int compareTo(DailyResponses other)
		{
		return fyyyymmdd - other.fyyyymmdd;
		}

	@Override public boolean equals(Object other)
		{
		return (other != null)
		    && (other instanceof DailyResponses)
		    && equals((DailyResponses)other);
		}

	public final boolean equals(DailyResponses other)
		{
		return fyyyymmdd == other.fyyyymmdd;
		}

	final SBArray<Response> responses() { return fResponses; }

	// PRIVATE
	private final int fyyyymmdd; // the date of these responses
	private final SBArray<Response> fResponses;

	/** package-private ctor */
	DailyResponses(String yyyymmdd, int initialCapacity)
		{
		fyyyymmdd = Integer.parseInt(yyyymmdd);
		fResponses = new SBArray<Response>(initialCapacity);
		}
	}
