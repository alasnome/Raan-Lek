package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)ThaiLabel.java 1.00 20121124
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* Multilingual:
*
* @author Rick Salamone
* @version 1.00
* 20121124 rts created
*******************************************************/

public interface Multilingual
	{
	public static final String LANGUAGE_PROP="usr.lang";
	public static final String ENGLISH="English";
	public static final String THAI="Thai";
	public static final String[] CHOICES=
		{
		ENGLISH,
		HtmlParser.htmlToJava("&#3652;&#3607;&#3618;"),
		};

	public void updateLanguage(String aLanguage);
	}
