package com.alasnome.apps.RaanLek.report;
/********************************************************************
* @(#)ReportControls.java 1.00 20100821
* Copyright © 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ReportControls: Interface for Mail Manager to select and filter
* contacts for processing.
*
* @author Rick Salamone
* @version 1.00
* 20110303 rts created
* 20121207 rts added monthy/daily selector
*******************************************************/
import com.alasnome.apps.RaanLek.Account;
import com.shanebow.dao.*;
import com.shanebow.dao.edit.*;
import com.shanebow.ui.calendar.MonthCalendar;
import com.shanebow.ui.SBRadioPanel;
import com.shanebow.ui.layout.LabeledPairPanel;
import com.shanebow.ui.LAF;
import com.shanebow.util.SBDate;
import com.shanebow.util.SBProperties;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

public final class ReportControls
	extends JPanel
	{
	private static final String[] SHOW = { "1000", "2000", "10000" };

	private static final String[] TYPES = {"Monthy", "Daily"};
	private final SBRadioPanel<String>  frpType = new SBRadioPanel<String>( 1, 0, TYPES);
	protected final MonthCalendar calendar = new MonthCalendar();
	private final AccountCheckList lstAccounts = new AccountCheckList();
	private final JComboBox     cbShow = new JComboBox(SHOW);
	private final EditDateRange editDateRange = new EditDateRange(EditDateRange.VERTICAL);
	private final JTextField    tfMisc = new JTextField();
	private final JTextField    tfName = new JTextField();

	public ReportControls(JComponent... controls)
		{
		super( new BorderLayout());

		calendar.addPropertyChangeListener(
			MonthCalendar.TIMECHANGED_PROPERTY_NAME, editDateRange);
		calendar.setOpaque(false);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(calendar);
		splitPane.setBottomComponent(new JScrollPane(filtersPanel()));
		splitPane.setDividerLocation(190); //XXX ignored in some releases bug 4101306
		add(splitPane, BorderLayout.CENTER);

		if ( controls.length > 0 )
			{
			JPanel bottom = new JPanel();
			for ( JComponent c : controls )
				bottom.add(c);
			add(bottom, BorderLayout.SOUTH);
			}
		}

	private JComponent filtersPanel()
		{
//		SBProperties props = SBProperties.getInstance();
		cbShow.setSelectedIndex(2);

		LabeledPairPanel p = new LabeledPairPanel();  // "Filters" );
		p.setBorder(LAF.getStandardBorder());

/***
		p.addRow(new Section("Dates"), frpType );
		frpType.setSelectedIndex(0);
		p.addRow( "", editDateRange );
		p.addRow(new Section("Accounts"), new JScrollPane(lstAccounts));
***/
		p.addRowSpan(new Section("Dates"));
		p.addRowSpan(frpType);
		frpType.setSelectedIndex(0);
		p.addRowSpan(editDateRange);
		p.addRowSpan(new Section("Accounts"));
		p.addRowSpan(new JScrollPane(lstAccounts));

		p.addRow(new Section(), new Section());
		p.addRow(new Section("Filters"), new JLabel());
		p.addRow( "Name", tfName );
		p.addRow( "misc", tfMisc );

		p.addRow(new Section(), new Section());
		p.addRow(new Section("Limit"), cbShow );

		return p;
		}

	public int getMaxShowCount()
		{
		try { return Integer.parseInt((String)cbShow.getSelectedItem()); }
		catch (Exception e) { return -1; }
		}

	public final Account[] getAccounts()
		{
		Object[] acntObjects = lstAccounts.getSelectedValues();
		Account[] accounts = new Account[acntObjects.length];
		for (int i = 0; i < accounts.length; i++)
			accounts[i] = (Account)acntObjects[i];
		return accounts;
		}

	public final boolean isMonthly() { return frpType.getSelectedIndex() == 0; }

	public long[] getDateRange() { return editDateRange.getDateRange(); }
	}

class Section extends JLabel
	{
	public Section()
		{
		super( "<html><HR width=5000>" );
		}
	public Section(String title)
		{
		super( "<html><B>" + title );
		}
	}
