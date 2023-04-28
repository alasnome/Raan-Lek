package com.alasnome.apps.RaanLek.account;
/********************************************************************
* @(#)DlgAccount.java 1.00 20110725
* Copyright © 2011-2012 by Richard T. Salamone, Jr. All rights reserved.
*
* DlgAccount: Modless dialog that allows adding new Account to the
* master list.
*
* @version 1.00
* @author Rick Salamone
* 20110916 rts created
* 20111218 rts added syllable action
* 20120101 rts added previous action
* 20120123 rts added insert(Account) method
* 20120205 rts added memory set and recall feature
************************************/
import com.alasnome.apps.RaanLek.Account;
import com.shanebow.ui.LAF;
import com.shanebow.util.SBProperties;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.border.*;

public class DlgAccount
	extends JDialog
	{
	private static final DlgAccount _instance = new DlgAccount();
	private static final String KEY_BOUNDS="usr.acnt.dlg.bounds";
	private static final String[] _titles =
		{
		LAF.getDialogTitle("Add Account"),
		LAF.getDialogTitle("Edit Account")
		};

	public static void edit(Account aAccount)
		{
		_instance.setVisible(true);
		_instance.setTitle(_titles[(aAccount==null)?0:1]);
		_instance.editor.edit(aAccount);
		}

	public static void updateLanguage(String aLanguage)
		{
		_instance.editor.updateLanguage(aLanguage);
		}

	// PRIVATE //
	private final AccountEntryPanel editor;

	private DlgAccount()
		{
//		super((java.awt.Frame)null, _titles[0], false);
		super((java.awt.Frame)null);
		editor = new AccountEntryPanel();
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));
		Dimension SPACER = new Dimension(5, 0);

		buttons.add(editor.memSetAction().makeButton());
		buttons.add(editor.memRecallAction().makeButton());
		buttons.add(Box.createRigidArea(SPACER));

		buttons.add(editor.prevAction().makeButton());
		buttons.add(Box.createRigidArea(SPACER));

		buttons.add(Box.createHorizontalGlue());
		buttons.add(editor.saveAction().makeButton());

		setBounds( SBProperties.getInstance()
		                       .getRectangle( KEY_BOUNDS, 50,50,350,200));
		addComponentListener( new ComponentAdapter()
			{
			public void componentMoved(ComponentEvent e) { saveBounds(); }
			public void componentResized(ComponentEvent e) { saveBounds(); }
			});

		JPanel top = new JPanel(new BorderLayout());
		top.add(editor, BorderLayout.CENTER);
		top.add(buttons, BorderLayout.SOUTH);
		top.setBorder(LAF.getStandardBorder());
		setContentPane(top);
		LAF.addUISwitchListener(this);
		}

	private void saveBounds()
		{
		SBProperties.getInstance().setProperty( KEY_BOUNDS, getBounds());
		}
	}
