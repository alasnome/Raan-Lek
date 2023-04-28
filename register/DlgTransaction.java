package com.alasnome.apps.RaanLek.register;
/********************************************************************
* @(#)DlgTransaction.java 1.00 20121126
* Copyright © 2011-2012 by Richard T. Salamone, Jr. All rights reserved.
*
* DlgTransaction: Modless dialog that allows adding a new Transaction
* or editing an existing Transaction.
*
* @version 1.00
* @author Rick Salamone
* 20121126 rts created
************************************/
import com.alasnome.apps.RaanLek.Transaction;
import com.shanebow.ui.LAF;
import com.shanebow.util.SBProperties;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.border.*;

public final class DlgTransaction
	extends JDialog
	{
	private static final DlgTransaction _instance = new DlgTransaction();
	private static final String KEY_BOUNDS="usr.trans.dlg.bounds";
	private static final String[] _titles =
		{
		LAF.getDialogTitle("Add Transaction"),
		LAF.getDialogTitle("Edit Transaction")
		};

	public static void edit(Transaction aTransaction)
		{
		_instance.setTitle(_titles[(aTransaction==null)?0:1]);
		_instance.setVisible(true);
		_instance.editor.edit(aTransaction);
		}

	public static void updateLanguage(String aLanguage)
		{
		_instance.editor.updateLanguage(aLanguage);
		}

	// PRIVATE //
	private final TransactionEntryPanel editor;

	private DlgTransaction()
		{
//		super((java.awt.Frame)null, LAF.getDialogTitle("Add Transaction"), false);
		super((java.awt.Frame)null);
		editor = new TransactionEntryPanel();
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));
		Dimension SPACER = new Dimension(5, 0);

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
