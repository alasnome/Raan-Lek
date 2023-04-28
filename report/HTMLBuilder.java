package com.alasnome.apps.RaanLek.report;
/********************************************************************
* @(#)HTMLBuilder.java 1.00 20100821
* Copyright 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* HTMLBuilder: Creates an HTML document.
*
* The W3C HTML and CSS standards list only 16 valid color names:
* aqua, black, blue, fuchsia, gray, green,
* lime, maroon, navy, olive, purple, red,
* silver, teal, white, and yellow
*
* @author Rick Salamone
* @version 1.00 RTS 08/21/10 initial iteration
*******************************************************/
import com.shanebow.util.SBDate;
import com.shanebow.util.SBFormat;
import java.awt.Color;

public final class HTMLBuilder
	{
	private static final String _prefix = "<html><body>";
	private static final String _postfix = "</body></html>";

	private StringBuffer m_content = new StringBuffer(4096);

	public String toString()
		{
		return _prefix + m_content + _postfix;
		}

	private String colored( String color, Object x )
		{ return "<font color=" + color + ">" + x + "</font>"; }

	public void clear() { m_content.setLength(0); }
	public void append( Object x ) { m_content.append(x); }
	public void append( String color, Object x )
		{ append( colored(color, x)); }

	public void nbsp( int count )
		{ for ( int i = 0; i < count; i++ ) append( "&nbsp;" ); }

	public void header( int level, Object x )
		{ append( "<h" + level + ">" + x + "</h" + level + ">"); }

	public void boldItalic( int level, Object x )
		{ append( "<b><i>" + x + "</i></b><br>" ); }

	public void listItem( Object x )
		{ append( "<li>" + x + "</li>" ); }

	public void listItem( String color, Object x )
		{ listItem( colored( color, x )); }

	public void tableBegin(int width, int border, int cellspacing )
		{
//		append( "<TABLE width=\"800\" border=\"" + border + "\"`"
		append( "<TABLE width=\"" + width + "\" border=\"" + border + "\"`"
		        + "cellspacing=\"" + cellspacing + "\" cellpadding=\"1\" "
		        + "align=\"center\">" );
		}

	public void tableHeader( String[] titles )
		{
		append( "<THEAD ALIGN=\"right\" VALIGN=\"bottom\"><TR>" );
		append( "<td align=\"left\">" + titles[0] + "</td>" );
		for ( int i = 1; i < titles.length; i++ )
			append( "<td>" + titles[i] + "</td>" );
		append( "</TR></THEAD>" );
		}

	public void tableHeader( String[] titles, int[] percents )
		{
		append( "<THEAD ALIGN=\"left\" VALIGN=\"bottom\"><TR>" );
		for ( int i = 0; i < titles.length; i++ )
			append( "<TD WIDTH=\"" + percents[i] + "%\">" + titles[i] + "</TD>" );
		append( "</TR></THEAD>" );
		}

	public void tableEnd()
		{
		append("</TABLE>");
		}

	public void tableRow( Object... fields )
		{
		append("<TR>");
		for ( Object field : fields )
			append("<TD>" + field + "</TD>");
		append("</TR>");
		}

	public void profit( int cents )
		{
		append(colored((cents < 0)? "RED" : "LIME", SBFormat.toDollarString(cents)));
		}
	}
