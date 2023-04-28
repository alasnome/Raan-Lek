package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)_RaanLek.java 1.00 20121118
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* _RaanLek: Bilingual tool for managing the finances of a small shop.
*
* @author Rick Salamone
* @version 1.00
* 20121118 rts created
*******************************************************/
import com.alasnome.apps.RaanLek.account.AccountsView;
import com.alasnome.apps.RaanLek.register.TransactionsView;
import com.alasnome.apps.RaanLek.muu.MuuButton;
import com.shanebow.ui.menu.*;
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBAction;
import com.shanebow.ui.SBDialog;
import com.shanebow.ui.SplitPane;
import com.shanebow.util.SBDate;
import com.shanebow.util.SBProperties;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public final class _RaanLek
	extends JFrame
	{
	private static long blowUp = 0; // SBDate.toTime("20130601  23:59");
//	private final AccountsByDateTable fAccountsByDateTable = 
//	                                    new AccountsByDateTable();

	public static void main(String[] args)
		{
		SBProperties.load(_RaanLek.class);
		LAF.initLAF(blowUp, true);
		new _RaanLek();
		}

	private final SBAction fActRaan = new SBAction("Raan", 'U', "Load user data", null)
		{
		@Override public void actionPerformed(ActionEvent e) { onLoadRaan(); }
		};

	_RaanLek()
		{
		SBProperties props = SBProperties.getInstance();
		setTitle(props.getProperty("app.name")
		        + " " + props.getProperty("app.version"));
		setBounds(props.getRectangle("usr.app.bounds", 50,50,940,700)); // x,y,w,h
//		LAF.addPreferencesEditor(new GeneralPreferencesEditor());
		buildContent();
		buildMenus();
onLoadRaan();
		setVisible(true);
		}

	private void buildContent()
		{
		JTabbedPane tabbedPane = new com.shanebow.ui.SBTabbedPane();
		tabbedPane.setBorder(LAF.getStandardBorder());
		tabbedPane.addTab("Reports", null, report(), "T Reports" );
		tabbedPane.addTab("Register", null, new TransactionsView(), "Transaction register" );
//		tabbedPane.addTab("By Date", null, fAccountsByDateTable, "Account totals by date" );
//		tabbedPane.addTab("Table", null, responseTable(), "Daily Summary" );
		tabbedPane.addTab("Accounts", null, accountsTable(), "Account List" );
		setContentPane(tabbedPane);
		}

	private JComponent report()
		{
		return new com.alasnome.apps.RaanLek.report.TabReports();
		}

	private JComponent responseTable()
		{
		ResponseModel fResponses = new ResponseModel();
		JTable table = new JTable(fResponses);
		return new JScrollPane(table);
		}

	private JComponent accountsTable() { return new AccountsView(); }

	private void buildMenus()
		{
		SBMenuBar menuBar = new SBMenuBar();
		menuBar.addMenu("File", fActRaan, null,
			new SBViewLogAction(this), null,
			LAF.setExitAction(new com.shanebow.ui.SBExitAction(this)
				{
				public void doApplicationCleanup() {}
				}));

		menuBar.addMenu("Settings",
			LAF.getPreferencesAction(), null,
			menuBar.getThemeMenu());
		menuBar.addMenu("Help",
			new SBActHelp(), null,
			new SBAboutAction(this));
		menuBar.add(Box.createHorizontalGlue());
//		menuBar.add(new com.thaidrills.common.ThaiFontChooser(this));
		menuBar.add(new MuuButton());
		menuBar.add(new LanguageSelector(this));
		setJMenuBar(menuBar);
		}

	private void onLoadRaan()
		{
		String raanName = "thaidrills";
		try
			{
			Raan.open(raanName);
			}
		catch (Exception e)
			{
			SBDialog.error("Load Raan Failed",
				"<html>Unable to load user <b>" + raanName + "</b><br>" + e.getMessage());
			}
		}
	}

