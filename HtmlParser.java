package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)HtmlParser.java 1.00 20100422
* Copyright © 2010-2012 by Richard T. Salamone, Jr. All rights reserved.
*
* HtmlParser: A single vocabulary word consisting of Thai, English, and
* phonetic representations along with bookkeeping info.
*
* @author Rick Salamone
* @version 1.00
* 20100422 rts initial version
* 20110713 rts reading from jar file
* 20110724 rts support for Unicoded to allow wordbreak/initial color coding
* 20110730 rts decoupled list from individual words
* 20111005 rts thaw stream now (can come from jar, file, etc)
* 20111023 rts handle nulls in getString(field) & javaToHtml
*******************************************************/

public final class HtmlParser
	{
	/**
	* Converts a java string containing unicode chars
	* into HTML style unicode (e.g. "...&#1234;...")
	*/
	public static String javaToHtml(String aString)
		{
		String it = "";
		if (aString != null) for ( int i = 0; i < aString.length(); i++ )
			{
			char c = aString.charAt(i);
			if ( c > 255 )
				it += "&#" + (int)c + ";";
			else
				it += c;
			}
		return it;
		}

	/**
	* Converts a string containing HTML style unicode
	* (e.g. "...&#1234;...") into a Java string
	*/
	public static String htmlToJava(String aHtmlString)
		{
		String it = "";
		for ( int i = 0; i < aHtmlString.length(); i++ )
			{
			char c = aHtmlString.charAt(i);
			if ( c == '&' )
				{
				int value = 0;
				while ((c = aHtmlString.charAt(++i)) != ';' )
					if ( c >= '0' && c <= '9')
						value = (value * 10) + (int)c - (int)'0';
				c = (value > 0)? (char)value : ' ';
				}
			it += c;
			}
		return it;
		}
	}
