package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)Category.java 1.00 20120526
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* Category:
*
* @author Rick Salamone
* @version 1.00
* 20121120 rts created
*******************************************************/
import java.io.*;
import java.util.List;

public class Category
	{
	public static final Category SALES     = new Category(1,"Sales");
	public static final Category COGS      = new Category(2,"Inventory"
,"&#3585;&#3634;&#3619; &#3586;&#3634;&#3618; &#3626;&#3656;&#3591;");
	public static final Category UTILITIES = new Category(3,"Utilities");
	public static final Category FIXTURES  = new Category(4,"Fixtures");
	public static final Category FINANCING = new Category(5,"Financing");
	public static final Category LEGAL     = new Category(6,"Legal");
	public static final Category MARKETING = new Category(7,"Marketing");
	public static final Category SALARY    = new Category(8,"Salary");

	public static final Category[] _all =
		{
		SALES,
		COGS,
		UTILITIES,
		FIXTURES,
		FINANCING,
		LEGAL,
		MARKETING,
		SALARY
		};

	static Category find(int id)
		{
		for (Category c : _all)
			if (c.id() == id)
				return c;
		return null;
		}

	private static final String SEPARATOR="§";

	private final int fID;
	private String fEnglish;
	private String fThai;

	public final int id()        { return fID; }
	public final String english(){ return fEnglish; }
	public final String thai()   { return fThai; }
	public final String getEnglish(){ return fEnglish; }
	public final String getThai()   { return fThai; }
	@Override public String toString() { return fEnglish + "(" + fID + ")"; }

	private Category(int aID, String aEnglish) { this(aID,aEnglish,"t "+aEnglish); }
	private Category(int aID, String aEnglish, String aThaiHtml)
		{
		fID = aID;
		fEnglish = aEnglish;
		fThai = HtmlParser.htmlToJava(aThaiHtml);
		}
	
	private Category(String csv)
		{
		String[] split = csv.split(SEPARATOR,4);
		fID = new Integer(split[0]);
		fEnglish = split[1].trim();
		fThai = HtmlParser.htmlToJava(split[2].trim());
		}

	public String toCSV()
		{
		return "" + fID + SEPARATOR + fEnglish
		     + SEPARATOR + HtmlParser.javaToHtml(fThai);
		}
	}
