package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)RaanEvent.java 1.00 20120601
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* RaanEvent: Describes a change in the current Raan state or data that
* is sent to registered RaanListener objects.
*
* @author Rick Salamone
* @version 1.00
* 20120601 rts created
*******************************************************/
public final class RaanEvent
	extends java.util.EventObject
	{
	public static final int SET_CURRENT=1;
	public static final int ACNT_MODIFIED=2;
	public static final int ACNT_ADDED=3;
	public static final int ACNT_REMOVED=4;
	public static final int TRANS_MODIFIED=5;
	public static final int TRANS_ADDED=6;
	public static final int TRANS_REMOVED=7;

	private final int fID;
	private final Object fData;

	RaanEvent(Raan aRaan, int aID )
		{
		this(aRaan, aID, null);
		}

	RaanEvent(Raan aRaan, int aID, Object aData )
		{
		super(aRaan);
		fID = aID;
		fData = aData;
		}

	public Object getData() { return fData; }
	public Raan getRaan() { return (Raan)source; }
	public int getID() { return fID; }
	public int type() { return fID; }
	}
