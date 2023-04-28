package com.alasnome.apps.RaanLek.account;
/********************************************************************
* @(#)AccountEntryPanel.java 1.00 20121129
* Copyright © 2011-2012 by Richard T. Salamone, Jr. All rights reserved.
*
* AccountEntryPanel: Allows user to edit the kamsap fields.
*
* @author Rick Salamone
* @version 1.00
* 20121129 rts created
*******************************************************/
import com.alasnome.apps.RaanLek.*;
import com.thaidrills.admin.ThaiTextField;
import com.shanebow.ui.layout.LabeledPairPanel;
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBAction;
import com.shanebow.ui.SBDialog;
import com.shanebow.ui.SBRadioPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public final class AccountEntryPanel
	extends LabeledPairPanel
	{
	private static final Font FONT = new Font("SansSerif", Font.BOLD, 16);

	private Account fUnedited;
	private boolean fDirty;
	private String fPrevious = "";
	private String fMemory = "";

	private static final String[] TYPES = {"Income", "Expense"};
	private final SBRadioPanel<String>  frpType = new SBRadioPanel<String>( 1, 0, TYPES);
	private final ThaiTextField ftfThai = new ThaiTextField();
	private final JTextField    ftfEnglish = new JTextField(30);
	private final JTextField    ftfNumber = new JTextField();
	private final JComboBox     fcbCategory = new JComboBox(Category._all);

	private final KeyAdapter    fKeyListener = new KeyAdapter()
		{
		@Override public final void keyPressed(KeyEvent e)
			{
			if ( e.getKeyCode() == KeyEvent.VK_TAB ) // only ftfThai gets this
				processNumber();
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

	private boolean hasThaiConsonant(String thai)
		{
		for (int i = 0; i < thai.length(); i++ )
			{
			char c = thai.charAt(i);
			if (c >= 3585 && c <= 3630)
				return true;
			}
		return false;
		}

	private final SBAction fActPrevious
		= new SBAction("Previous", 'P', "Insert previous thai", null)
		{
		@Override public void actionPerformed(ActionEvent e) { ftfThai.insert(fPrevious); }
		};

	private final SBAction fActMemorySet
		= new SBAction("M+", '+', "Set memory", null)
		{
		@Override public void actionPerformed(ActionEvent e) { fMemory = ftfThai.getText(); }
		};

	private final SBAction fActMemoryRecall
		= new SBAction("MR", 'R', "Insert from memory", null)
		{
		@Override public void actionPerformed(ActionEvent e) { ftfThai.insert(fMemory); }
		};

	public SBAction saveAction() { return fActSave; }
	public SBAction prevAction(){ return fActPrevious; }
	public SBAction memSetAction(){ return fActMemorySet; }
	public SBAction memRecallAction(){ return fActMemoryRecall; }

	public AccountEntryPanel()
		{
		super();
		addRow(Account.FIELD_NAMES[Account.TYPE],     frpType);
		addRow(Account.FIELD_NAMES[Account.ID],       ftfNumber);
		addRow(Account.FIELD_NAMES[Account.THAI],     ftfThai);
		addRow(Account.FIELD_NAMES[Account.ENGLISH],  ftfEnglish);
		addRow(Account.FIELD_NAMES[Account.CATEGORY], fcbCategory);

		ftfEnglish.setFont(FONT);
		ftfNumber.setFont(FONT);
		ActionListener actionListener = new ActionListener()
			{
			@Override public void actionPerformed(ActionEvent e) { setDirty(true); }
			};
		frpType.addActionListener(actionListener);
		fcbCategory.addActionListener(actionListener);
		ftfThai.addKeyListener(fKeyListener);
		ftfThai.setFocusTraversalKeysEnabled(false);
		ftfEnglish.addKeyListener(fKeyListener);
		ftfNumber.addKeyListener(fKeyListener);
		ftfThai.addActionListener(new ActionListener()
			{
			@Override public void actionPerformed(ActionEvent e) { processNumber(); }
			});
		}

	public void edit(Account aAccount)
		{
		if ( fDirty 
		&&   SBDialog.confirm("Save changes to current account?"))
			{
			if ( !onSave()) return; // save failed - keep working on existing
			}
		populateFields(fUnedited = aAccount);
		}

	private boolean processNumber()
		{
		try
			{
			int num = Integer.parseInt(ftfNumber.getText().trim());
			if (null != Raan.getAccounts().find(num))
				return falseAndSayDuplicate(num);
			fActSave.setEnabled(true);
			ftfEnglish.requestFocus();
			return true;
			}
		catch (Exception e) { return falseInvalidNumber(); }
		}

	private boolean falseInvalidNumber()
		{
		return SBDialog.error("Bad Number", "<html><big><b>" + ftfNumber.getText()
			                        + "</b></big><br> is not a valid number", this);
		}

	private boolean falseAndSayDuplicate(int num)
		{
		return SBDialog.error("Duplicate", "<html><big><b>#" + num
			                            + "</b></big><br> already exists", this);
		}

	/** implement Multilingual interface */
	public void updateLanguage(String aLanguage)
		{
		boolean isThai = aLanguage.equals(Multilingual.THAI);
// @TODO: updateLanguage not implemented
/******
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
******/
		}

	private void setDirty(boolean on)
		{
		fDirty = on;
		fActSave.setEnabled(on);
		}

	public void clear()
		{
		fActSave.setEnabled(false);
		if ( !ftfThai.getText().isEmpty())
			fPrevious = ftfThai.getText();
		ftfThai.setText("");
		ftfEnglish.setText("");
		ftfNumber.setText("");
		fcbCategory.setSelectedIndex(0);
		frpType.setSelectedIndex(1);
		setDirty(false);
		}

	private void populateFields(Account aAccount)
		{
		if ( aAccount == null )
			{
			clear();
			return;
			}
		frpType.setSelectedIndex(
			(aAccount.type() == AccountType.INCOME)? 0 : 1);
		ftfEnglish.setText(aAccount.getEnglish());
		ftfThai.setText(aAccount.getThai());
		ftfNumber.setText("" + aAccount.id());
		fcbCategory.setSelectedItem(aAccount.category());
		setDirty(false);
		}

	private boolean onSave()
		{
		String thai = ftfThai.getText();
		String english = ftfEnglish.getText();
		if ( english.isEmpty() || thai.isEmpty())
			{
			return SBDialog.error("Input Error", "Please fill in all fields", this);
			}
		if ( !hasThaiConsonant(thai))
			{
			return SBDialog.error("Input Error", "No Thai consonant found", this);
			}
		int num = 0;
		try { num = Integer.parseInt(ftfNumber.getText().trim()); }
		catch (Exception ex) { return SBDialog.inputError("Invalid number"); }
		Category cat = (Category)fcbCategory.getSelectedItem();
		AccountType type = (frpType.getSelectedIndex() == 0)?
			AccountType.INCOME : AccountType.EXPENSE;
		if ( fUnedited == null) // adding...
			{
			if ( null == Raan.addAccount(num, cat.id(), type.symbol(), english, thai))
				return SBDialog.error("Add Failed", "Unknown error", null);
			}
		else // modifing...
			{
			fUnedited.setType(type);
			fUnedited.setCategory(cat);
			fUnedited.setThai(thai);
			fUnedited.setEnglish(english);
			Raan.accountModified(fUnedited);
			}
		setDirty(false);
		return true;
		}
	}
