package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)RaanListener.java 1.00 20120601
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* RaanListener: Interface that is implemented by objects that wish
* to be notified about RaanEvents.
*
* @author Rick Salamone
* @version 1.00
* 20120601 rts created
*******************************************************/
public interface RaanListener
	extends java.util.EventListener
	{
	public void userAction(RaanEvent aRaanEvent);
	}
