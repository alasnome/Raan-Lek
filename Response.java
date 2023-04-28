package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)Response.java 1.00 20120526
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* Response: A Response object is actually a question id matched with an
* arbitrary value.
*
* @author Rick Salamone
* @version 1.00
* 20120526 rts created
*******************************************************/

class Response
	{
	private final int fAccountID;
	private Object fValue;

	public int questionID() { return fAccountID; }
	public Object value() { return fValue; }

	public Response(int aAccountID, Object aValue)
		{
		fAccountID = aAccountID;
		fValue = aValue;
		}

	public String toString() { return fValue.toString(); }

	public String toCSV() { return "" + fAccountID + "," + fValue; }

	@Override public int hashCode() { return fAccountID; }
	}
