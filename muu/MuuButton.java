package com.alasnome.apps.RaanLek.muu;
/********************************************************************
* @(#)DlgMuu.java 1.00 20121205
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* DlgMuu: Modless dialog that displays custom input screens.
*
* @version 1.00
* @author Rick Salamone
* 20121205 rts created
************************************/
import com.alasnome.apps.RaanLek.Transaction;
import com.alasnome.apps.RaanLek.*;
import com.shanebow.ui.LAF;
import com.shanebow.util.SBProperties;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class MuuButton
	extends JButton
	implements Multilingual
	{
	public MuuButton()
		{
		super("Muu");
		setMargin(new Insets(0,0,0,0)); // T, L, B, R
		addActionListener(new ActionListener()
			{
			public void actionPerformed(ActionEvent e) { DlgMuu.display(); }
			});
		Raan.addRaanListener(new RaanListener()
			{
			@Override public void userAction(RaanEvent aEvent)
				{
				if (aEvent.type() == RaanEvent.SET_CURRENT) DlgMuu.init();
				}
			});
		}

	public void updateLanguage(String aLanguage)
		{
		LanguageSelector.walk(DlgMuu._instance);
		}
	}

final class DlgMuu
	extends JDialog
	{
	static final DlgMuu _instance = new DlgMuu();
	private static final String KEY_BOUNDS="usr.muu.dlg.bounds";

	public static void display() { 	_instance.setVisible(true); }
	public static void init() { _instance.reset(); }

//	public static void updateLanguage(String aLanguage)
//		{
//		_instance.editor.updateLanguage(aLanguage);
//		}

	// PRIVATE //

	private DlgMuu()
		{
		super((java.awt.Frame)null, LAF.getDialogTitle("Inputs"), false);
		setBounds( SBProperties.getInstance()
		                       .getRectangle( KEY_BOUNDS, 50,50,335,660));
		addComponentListener( new ComponentAdapter()
			{
			public void componentMoved(ComponentEvent e) { saveBounds(); }
			public void componentResized(ComponentEvent e) { saveBounds(); }
			});
		LAF.addUISwitchListener(this);
		}

	private static final String SEPARATOR="§";
	public void reset()
		{
		JTabbedPane tabbedPane = new com.shanebow.ui.SBTabbedPane();
		tabbedPane.setBorder(LAF.getStandardBorder());

		String daily = "Daily§Muu Daily Inputs§1,2,3,4,5,6,7,8,9,47,48,51,52";
		String monthy = "Monthly§Muu Monthly Inputs§10,11,12,13,14,16,49";

		String dailyPieces[] = daily.split(SEPARATOR);
		int[] dailyAccountIDs = SBProperties.csvToIntArray(dailyPieces[2]);
		MuuInputs muuDaily = new MuuInputs(dailyPieces[1], dailyAccountIDs);
		tabbedPane.addTab(dailyPieces[0], null, muuDaily, dailyPieces[1]);

		String monthlyTitle = "Monthly";
		String monthlyDesc = "Monthly Expenses";
		int[] monthlyAccountIDs = {10,11,12,13,14,16,49};
		MuuInputs muuMonthly = new MuuInputs(monthlyDesc, monthlyAccountIDs);
		tabbedPane.addTab(monthlyTitle, null, muuMonthly, monthlyDesc);

		setContentPane(tabbedPane);
System.out.println("Muu tabs loaded");
		}

	private void saveBounds()
		{
		SBProperties.getInstance().setProperty( KEY_BOUNDS, getBounds());
		}
	}
