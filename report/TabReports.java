package com.alasnome.apps.RaanLek.report;
/********************************************************************
* @(#)TabReports.java 1.00 20100821
* Copyright © 2010-2012 by Richard T. Salamone, Jr. All rights reserved.
*
* TabReports: Displays transaction statistics formatted as html.
*
* @author Rick Salamone
* @version 1.00
* 20100821 rts created for APO call stats app
* 20121204 rts search & replace to adapt to RaanLek app
* 20121205 rts decoupled report building code to worker object
*******************************************************/
import com.alasnome.apps.RaanLek.Account;
import com.shanebow.ui.SBDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class TabReports extends JPanel
	implements ActionListener
	{
	public  static final String CMD_FETCH = "Fetch";
	private final static Color COLOR = new Color( 204, 204, 255 );

	private ReportControls controls;
	private HTMLViewer     viewer = new HTMLViewer();

	public TabReports()
		{
		super(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5,5,0,5));

		JButton fetch = new JButton(CMD_FETCH);
		fetch.addActionListener(this);
		controls = new ReportControls( fetch );

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(controls);
		splitPane.setRightComponent(resultsPanel());
		splitPane.setDividerLocation(260); //XXX ignored in some releases bug 4101306

		add(splitPane, BorderLayout.CENTER);
		}

	public void actionPerformed( ActionEvent e )
		{
		String cmd = e.getActionCommand();
		if ( cmd == CMD_FETCH )
			{
			try
				{
				long[] dateRange = controls.getDateRange();
				ReportWorker worker = new ReportWorker();
				HTMLBuilder report = 	worker.eventsFor(dateRange, controls.getAccounts(),
				                           viewer.getWidth(), controls.isMonthly());
				viewer.setText( report.toString());
				}
			catch (Exception x) {
x.printStackTrace(); SBDialog.inputError( x.getMessage()); }
			}
		}

	private JComponent resultsPanel()
		{
		JPanel p = new JPanel(new BorderLayout());
		viewer.setBackground(Color.BLACK);
		viewer.setForeground(COLOR);
		p.add(new JScrollPane(viewer), BorderLayout.CENTER);
		return p;
		}

	private void log( String fmt, Object... args )
		{
		com.shanebow.util.SBLog.write ( String.format(fmt, args ));
		}
	}

class HTMLViewer extends JLabel
	{
	public  static final Font FONT = new Font("SansSerif", Font.PLAIN, 12);
	// constructor code
		{
		setOpaque(true);
		setFont(FONT);
		setVerticalAlignment(JLabel.TOP);
		setBackground(Color.BLACK);
		setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
		}
	}
