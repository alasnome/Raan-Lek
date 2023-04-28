package com.alasnome.apps.RaanLek.register;
/********************************************************************
* @(#)TransactionEntryPanel.java 1.00 20121126
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* TransactionEntryPanel: Allows user to edit the kamsap fields.
*
* @author Rick Salamone
* @version 1.00
* 20121126 rts created
*******************************************************/
import com.alasnome.apps.RaanLek.*;
import com.shanebow.ui.layout.LabeledPairPanel;
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBAction;
import com.shanebow.ui.SBDialog;
import com.shanebow.ui.calendar.MonthCalendar;
import com.shanebow.util.SBDate;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public final class TransactionEntryPanel
	extends JPanel
	{
	private static final Font FONT = new Font("SansSerif", Font.BOLD, 16);

	private Transaction fUnedited;
	private boolean fDirty;

	private final MonthCalendar  fCalendar = new MonthCalendar();
	private final JTextField     ftfAmount = new JTextField(30);
	private final JTextField     ftfEntity = new JTextField();
	private final JTextField     ftfMemo = new JTextField();
//	private final ThaiTextField  ftfMemo = new ThaiTextField();
	private final AccountChooser fcbAccount = new AccountChooser(false);

	private final JLabel         flblAmount = new JLabel();
	private final JLabel         flblEntity = new JLabel();
	private final JLabel         flblMemo = new JLabel();
	private final JLabel         flblAccount = new JLabel();

	private final KeyAdapter    fKeyListener = new KeyAdapter()
		{
		@Override public final void keyPressed(KeyEvent e)
			{
//			if ( e.getKeyCode() == KeyEvent.VK_TAB ) // only ftfMemo gets this
//				processThai();
			}
		@Override public final void keyTyped(KeyEvent e)
			{
			setDirty(true);
			}
		};

	private final SBAction fActSave
		= new SBAction("OK", 'O', "Save changes & close", null)
		{
		@Override public void actionPerformed(ActionEvent e) { onSave(); }
		};

	public TransactionEntryPanel()
		{
		super(new GridLayout(1,0,10,0));

		LabeledPairPanel fields = new LabeledPairPanel();
		fields.addRow(flblAccount, fcbAccount);
		fields.addRow(flblAmount,  ftfAmount);
		fields.addRow(flblEntity,  ftfEntity);
		fields.addRow(flblMemo,    ftfMemo);

		ftfAmount.setFont(FONT);
		ftfEntity.setFont(FONT);
		fcbAccount.addActionListener(new ActionListener()
			{
			@Override public void actionPerformed(ActionEvent e) { setDirty(true); }
			});
		ftfMemo.addKeyListener(fKeyListener);
		ftfMemo.setFocusTraversalKeysEnabled(false);
		ftfAmount.addKeyListener(fKeyListener);
		ftfEntity.addKeyListener(fKeyListener);
		add(fCalendar);
		add(fields);
		}

	public void edit(Transaction aTransaction)
		{
		if ( fDirty 
		&&   SBDialog.confirm("Save changes to current transaction?"))
			{
			if ( !onSave()) return; // save failed - keep working on existing
			}
		fUnedited = aTransaction;
		populateFields(aTransaction);
		}

	public SBAction saveAction() { return fActSave; }

	/** implement Multilingual interface */
	public void updateLanguage(String aLanguage)
		{
fcbAccount.updateLanguage(aLanguage);
		boolean isThai = aLanguage.equals(Multilingual.THAI);

		String thai = Transaction.LBL_THAI[Transaction.AMOUNT];
		String english = Transaction.FIELD_NAMES[Transaction.AMOUNT];
		flblAmount.setText(isThai? thai : english);
		ftfAmount.setToolTipText(isThai? english : thai);

		thai = Transaction.LBL_THAI[Transaction.WHO];
		english = Transaction.FIELD_NAMES[Transaction.WHO];
		flblEntity.setText(isThai? thai : english);
		ftfEntity.setToolTipText(isThai? english : thai);

		thai = Transaction.LBL_THAI[Transaction.MEMO];
		english = Transaction.FIELD_NAMES[Transaction.MEMO];
		flblMemo.setText(isThai? thai : english);
		ftfMemo.setToolTipText(isThai? english : thai);

		thai = Transaction.LBL_THAI[Transaction.ACCOUNT];
		english = Transaction.FIELD_NAMES[Transaction.ACCOUNT];
		flblAccount.setText(isThai? thai : english);
		fcbAccount.setToolTipText(isThai? english : thai);
		}

	private void setDirty(boolean on)
		{
		fDirty = on;
		fActSave.setEnabled(on);
		}

	private void clear()
		{
		ftfMemo.setText("");
		ftfAmount.setText("");
		ftfEntity.setText("");
		fcbAccount.setSelectedIndex(0);
		ftfAmount.requestFocus();
		ftfAmount.setCaretPosition(0);
		setDirty(false);
		}

	private void populateFields(Transaction aTransaction)
		{
		if ( aTransaction == null )
			{
			clear();
			return;
			}
		fCalendar.setTime(aTransaction.time());
		ftfAmount.setText(""+aTransaction.amount());
		ftfMemo.setText(aTransaction.memo());
		ftfEntity.setText(aTransaction.entity());
		fcbAccount.setSelectedID(aTransaction.accountID());
		setDirty(false);
		}

	private boolean onSave()
		{
		if (!validInput())
			return false;
		int amt = 0;
		long when = fCalendar.getDate();
		try { amt = Integer.parseInt(ftfAmount.getText().trim()); }
		catch (Exception ex) { return SBDialog.inputError("Invalid amount"); }
		String who = ftfEntity.getText().trim();
		String memo = ftfMemo.getText().trim();
		int accountID = ((Account)fcbAccount.getSelectedItem()).id();
		if ( fUnedited == null) // adding...
			{
			if ( null == Raan.addTransaction(when, amt, accountID, who, memo))
				return SBDialog.error("Add Failed", "Unknown error", null);
			}
		else // modifing...
			{
			fUnedited.setEntity(who);
			fUnedited.setAccountID(accountID);
			fUnedited.setMemo(memo);
			fUnedited.setAmount(amt);
			fUnedited.setDate(when);
			Raan.transactionModified(fUnedited);
			}
		setDirty(false);
		return true;
		}

	private final boolean validInput()
		{
		if ( ftfAmount.getText().isEmpty()	
		||   ftfEntity.getText().isEmpty())
			{
			SBDialog.error("Input Error", "Cannot be blank", this);
			return false;
			}
		return true;
		}
	}
