package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)LanguageSelector.java 1.00 20121124
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* LanguageSelector:
*
* @author Rick Salamone
* @version 1.00
* 20121124 rts created
*******************************************************/
import com.shanebow.util.SBProperties;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

public final class LanguageSelector
	extends JComboBox
	{
	private final Component fOwner;
// This is ugly but necessary for components created after language selected
public static String _language = Multilingual.ENGLISH;

	public LanguageSelector(Component aOwner)
		{
		super(Multilingual.CHOICES);
		fOwner = aOwner;
		Dimension d = new Dimension(130,22); // fcbFamilies.getPreferredSize();
		setMaximumSize(d);
		setPreferredSize(d);
		addActionListener(new ActionListener()
			{
			@Override public void actionPerformed(ActionEvent e)
				{
				String language = getSelectedItem().equals(Multilingual.ENGLISH)?
				                Multilingual.ENGLISH : Multilingual.THAI;
				SBProperties.set(Multilingual.LANGUAGE_PROP, language);
_language = language;
				walk(SwingUtilities.getRoot(fOwner));
				}
			});
		String language = SBProperties.getInstance().getProperty(
		      Multilingual.LANGUAGE_PROP, Multilingual.ENGLISH);
_language = language;
		setSelectedIndex(language.equals(Multilingual.ENGLISH)? 0 : 1);
		}

	/**
	* Recursively walks the component tree starting at the specified root,
	* and calling updateLanguage() on each Multilingual component encountered.
	*/
	public static void walk(Component aRoot)
		{
		if (aRoot instanceof Multilingual)
			((Multilingual)aRoot).updateLanguage(_language);
		if (aRoot instanceof Container)
			for ( Component child : ((Container)aRoot).getComponents())
				walk(child);
		}
	}
