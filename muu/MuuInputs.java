package com.alasnome.apps.RaanLek.muu;
/********************************************************************
* @(#)MuuInputs.java 1.00 20120529
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* MuuInputs: A custom input panel that can be created for common
* data entry scenarios. For instance, Muu will enter the daily
* amounts for sales and receipts and expenditures.
*
* @author Rick Salamone
* 20120529 rts created
*******************************************************/
import com.alasnome.apps.RaanLek.*;
import com.shanebow.ui.calendar.MonthCalendar;
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBAction;
import com.shanebow.ui.SBDialog;
import com.shanebow.ui.SplitPane;
import com.shanebow.ui.layout.LabeledPairLayout;
import com.shanebow.util.SBDate;
import com.shanebow.util.SBFormat;
import com.shanebow.util.SBProperties;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Vector;

public final class MuuInputs
	extends JPanel
	{
	private final MonthCalendar fCalendar = new MonthCalendar();
	private final PromptsPanel fPrompts;

	public MuuInputs(String title, int[] accountIDs)
		{
		super(new BorderLayout());
		setBorder(LAF.bevel(5,5));
	
		fPrompts = new PromptsPanel(title, accountIDs);

		fCalendar.addPropertyChangeListener(MonthCalendar.TIMECHANGED_PROPERTY_NAME,
			new java.beans.PropertyChangeListener()
				{
				public void propertyChange(java.beans.PropertyChangeEvent e)
					{
					if ( !e.getPropertyName().equals(MonthCalendar.TIMECHANGED_PROPERTY_NAME))
						return;
					Object source = e.getSource();
					long newTime = ((Long)e.getNewValue()).longValue();
					String yyyymmdd = SBDate.yyyymmdd(newTime);
					fPrompts.setDate(yyyymmdd);
					}
				});

		String yyyymmdd = SBDate.yyyymmdd();
		fCalendar.setDate(yyyymmdd);
		fPrompts.setDate(yyyymmdd);

		SplitPane sp = new SplitPane ( SplitPane.VSPLIT, fCalendar, new JScrollPane(fPrompts));
		sp.setDividerLocation("usr.vsplit", 200);
		Dimension minimumSize = new Dimension(200, 50); // w, h
		fCalendar.setMinimumSize(minimumSize);
		fCalendar.setPreferredSize(minimumSize);

//		JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel controls = new JPanel(new BorderLayout());
		controls.add(fPrompts.lblStatusOrTotal, BorderLayout.CENTER);
		controls.add(fPrompts.fActSave.makeButton(), BorderLayout.EAST);

		add(sp, BorderLayout.CENTER);
		add(controls, BorderLayout.SOUTH);
		}
	}

final class PromptsPanel
	extends JPanel
	implements RaanListener, Multilingual
	{
	private String fTitle;
	private final long[] fRange = { 0, 0 };
	private boolean fIsThai;
	private boolean fDirty;
	final JLabel lblStatusOrTotal = new JLabel();
	final SBAction fActSave = new SBAction("Save", 'S', "Save changes", null)
			{
			@Override public void actionPerformed(ActionEvent e) { save(); }
			};

	PromptsPanel(String aTitle, int[] aAccountIDs)
		{
		super(new LabeledPairLayout());
		fTitle = aTitle;
		setBorder(LAF.bevel(5,5));
		KeyAdapter fKeyListener = new KeyAdapter()
			{
			@Override public final void keyPressed(KeyEvent e)
				{
//				if ( e.getKeyCode() == KeyEvent.VK_TAB ) // only ftfThai gets this
//					processNumber();
				}
			@Override public final void keyTyped(KeyEvent e)
				{
				setDirty(true);
				tally();
				}
			};

		AccountList accounts = Raan.getAccounts();
		for (int accountID : aAccountIDs)
			{
			Account account = accounts.find(accountID);
			AccountField field = new AccountField(account);
			add(field.getLabel(), LabeledPairLayout.LABEL);
			add(field, LabeledPairLayout.FIELD);
			field.addKeyListener(fKeyListener);
			}
		Raan.addRaanListener(this);
		updateLanguage(LanguageSelector._language);
		}

	private void tally()
		{
		int total = 0;
		for (Component c : fields())
			{
			AccountField field = (AccountField)c;
			try { total += field.getSignedAmount(); }
			catch (Exception e) {}
			}
		lblStatusOrTotal.setText((total == 0)? ""
		                                     : "" + total);
		if (total == 0)
			lblStatusOrTotal.setText("");
		else
			{
			String color = (total < 0) ? "RED" : "BLUE";
			lblStatusOrTotal.setText("<html><font color="+color+">"+total);
			}
		}

	public void setDate(String yyyymmdd)
		{
		fRange[0] = SBDate.toTime("" + yyyymmdd + "  00:00:00");
		fRange[1] = SBDate.toTime("" + yyyymmdd + "  23:59:59");
		populate();
		}

	/** implement RaanListener interface */
	@Override public void userAction(RaanEvent aRaanEvent)
		{
		if ( aRaanEvent.getID() == RaanEvent.ACNT_MODIFIED )
			{
			Account account = (Account)aRaanEvent.getData();
			for (Component c : fields())
				if (account.equals(((AccountField)c).account()))
					{ ((AccountField)c).setPromptLanguage(fIsThai); break; }
			}
		}

	/** implement Multilingual interface */
	public void updateLanguage(String aLanguage)
		{
		fIsThai = aLanguage.equals(Multilingual.THAI);
		for (Component c : fields())
			((AccountField)c).setPromptLanguage(fIsThai);
		}

	private void setDirty(boolean on)
		{
		fDirty = on;
		fActSave.setEnabled(on);
		}

	Vector<Component> fields()
		{
		return ((LabeledPairLayout)getLayout()).getFields();
		}

	void clear()
		{
		for (Component c : fields())
			{
			AccountField field = (AccountField)c;
			field.setText("");
			field.setUserData(null);
			}
		setDirty(false);
		lblStatusOrTotal.setText("");
		}

	private void populate()
		{
		for (Component c : fields())
			{
			AccountField field = (AccountField)c;
			Transaction trans = Raan.findTransaction(fRange, field.account(), fTitle);
			field.setText((trans==null)? "" : "" + trans.amount());
			field.setUserData(trans);
			}
		setDirty(false);
		tally();
		}

	private boolean validInputs()
		{
		for (Component c : fields())
			{
			AccountField field = (AccountField)c;
			try { field.getAmount(); }
			catch (Exception e)
				{
				return SBDialog.error(field.getLabel().getText(), "Bad Data", this);
				}
			}
		return true;
		}

	public void save()
		{
		if ( !validInputs())
			return;
		for (Component c : fields())
			{
			AccountField field = (AccountField)c;
			Transaction trans = (Transaction)field.getUserData();
			int amount = field.getAmount();
			if (trans==null) // adding a new Transaction
				{
				if (amount == 0) continue;
				trans = Raan.addTransaction(fRange[1], amount, field.account().id(), fTitle, "");
				field.setUserData(trans);
				}
			else if (amount == 0)
				{
				field.setUserData(null);
				Raan.deleteTransaction(trans);
				}
			else
				{
				trans.setAmount(amount);
				Raan.transactionModified(trans);
				}
			}
		setDirty(false);
		}
	}

final class AccountField
	extends JTextField
	{
	private final Account fAccount;
	private final JLabel fLabel;
	private Object fData;

	AccountField(Account aAccount)
		{
		super();
		fAccount = aAccount;
		fLabel = new JLabel(aAccount.prompt());
		setForeground(fAccount.type().color());
		}

	public Object getUserData() { return fData; }
	public void setUserData(Object aData) { fData = aData; }

	public int getAmount()
		{
		String text = getText().trim();
		return text.isEmpty()? 0 : Integer.parseInt(text);
		}

	public int getSignedAmount()
		{
		int amount = getAmount();
		if (fAccount.isExpense()) amount = -amount;
		return amount;
		}

	public JLabel getLabel() { return fLabel; }
	public Account account() { return fAccount; }
	public String toString() { return "AccountField: " + fAccount; }
	void setPromptLanguage(boolean isThai)
		{
		String english = fAccount.getEnglish();
		String thai = fAccount.getThai();
		fLabel.setText(isThai? thai : english);
		setToolTipText(isThai? english : thai);
		}
	}
