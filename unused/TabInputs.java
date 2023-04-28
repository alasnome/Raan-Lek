package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)TabInputs.java 1.00 20120529
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* TabInputs: Main frame and controlling logic for the ChartTrainer.
*
* @author Rick Salamone
* 20120529 rts created
*******************************************************/
import com.shanebow.ui.calendar.MonthCalendar;
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBDialog;
import com.shanebow.ui.SplitPane;
import com.shanebow.ui.layout.LabeledPairLayout;
import com.shanebow.util.SBDate;
import com.shanebow.util.SBFormat;
import com.shanebow.util.SBProperties;
import java.awt.*;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import java.util.Vector;

public final class TabInputs
	extends JPanel
	implements PropertyChangeListener
	{
	private final MonthCalendar fCalendar = new MonthCalendar();
	private final DailyPromptsPanel fPrompts = new DailyPromptsPanel();

	public void addCalendarListener(PropertyChangeListener aListener)
		{
		fCalendar.addPropertyChangeListener(
		           MonthCalendar.TIMECHANGED_PROPERTY_NAME, aListener);
		}

	public TabInputs()
		{
		super(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5,5,0,5));

		fCalendar.addPropertyChangeListener(MonthCalendar.TIMECHANGED_PROPERTY_NAME, this);
		fCalendar.setDate(SBDate.yyyymmdd());

		fPrompts.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));

		SplitPane sp = new SplitPane ( SplitPane.VSPLIT, fCalendar, fPrompts);
		sp.setDividerLocation("usr.vsplit", 200);
		Dimension minimumSize = new Dimension(200, 50); // w, h
		fCalendar.setMinimumSize(minimumSize);
		fCalendar.setPreferredSize(minimumSize);

		add(sp, BorderLayout.CENTER);
		}

	public void propertyChange(java.beans.PropertyChangeEvent e)
		{
		if ( !e.getPropertyName().equals(MonthCalendar.TIMECHANGED_PROPERTY_NAME))
			return;
		Object source = e.getSource();
		long newTime = ((Long)e.getNewValue()).longValue();
		String yyyymmdd = SBDate.yyyymmdd(newTime);
		fPrompts.setDate(yyyymmdd);
		}
	}

final class DailyPromptsPanel
	extends JTabbedPane
	implements RaanListener, Multilingual
	{
	private int fyyyymmdd;

	DailyPromptsPanel()
		{
		super();
		Raan.addRaanListener(this);
		changeRaan();
		}

	/** implement RaanListener interface */
	@Override public void userAction(RaanEvent aRaanEvent)
		{
		if ( aRaanEvent.getID() == RaanEvent.SET_CURRENT )
			changeRaan();
		}

	/** implement Multilingual interface */
	public void updateLanguage(String aLanguage)
		{
		boolean isThai = aLanguage.equals(Multilingual.THAI);
		int nTabs = getTabCount();
		for (int tabIndex = 0; tabIndex < nTabs; tabIndex++)
			{
			CatPanel panel = getPanelAt(tabIndex);
			Category cat = panel.fCategory;
			String english = cat.english();
			String thai = cat.thai();
			setTitleAt(tabIndex, isThai? thai : english);
			setToolTipTextAt(tabIndex, isThai? english : thai);
			for (Component c : panel.fields())
				((AccountField)c).setPromptLanguage(isThai);
			}
		}

	private class CatPanel extends JPanel
		{
		Category fCategory;
		CatPanel(Category aCategory)
			{
			super(new LabeledPairLayout());
			setBorder(LAF.bevel(5,5));
			fCategory = aCategory;
			}

		Vector<Component> fields()
			{
			return ((LabeledPairLayout)getLayout()).getFields();
			}

		void populate(DailyResponses aAcntTotalsForDate)
			{
			if (aAcntTotalsForDate == null)
				clear();
			else for (Component c : fields())
				{
				AccountField afield = (AccountField)c;
				Response response = aAcntTotalsForDate.getResponseFor(afield.account());
				afield.setText((response==null)? "" : response.value().toString());
				}
			}

		void clear() { for (Component c : fields()) ((JTextField)c).setText(""); }
		}

	private void changeRaan()
		{
		Raan raan = Raan.current();
		removeAll(); // delete all labels & fields from this container
		if ( raan == null )
			return;
		AccountList accounts = raan.getAccountList();

		Category cat = null;
		JPanel panel = null;
		for (Account account : accounts)
			{
			if (account.category() != cat)
				{
				panel = getPanelFor(account.category());
				cat = account.category();
				}
			AccountField field = new AccountField(account);
			panel.add(field.getLabel(), LabeledPairLayout.LABEL);
			panel.add(field, LabeledPairLayout.FIELD);
			}
		populate();
		validate();
		}

	private CatPanel getPanelFor(Category aCategory)
		{
		CatPanel panel;
		int nTabs = getTabCount();
		for (int tabIndex=0; tabIndex < nTabs; tabIndex++)
			{
			panel = getPanelAt(tabIndex);
			if (panel.fCategory.equals(aCategory))
				return panel;
			}
		// no panel for this aCategory, so create one...
		panel = new CatPanel(aCategory);
		addTab(aCategory.thai(), null, new JScrollPane(panel), aCategory.english());
		return panel;
		}

	private CatPanel getPanelAt(int aTabIndex)
		{
		return (CatPanel)(((JScrollPane)getComponentAt(aTabIndex)).getViewport().getView());
		}

	private void populate()
		{
		DailyResponses accountTotals = Raan.current().getDailyResponses(fyyyymmdd);
		int nTabs = getTabCount();
		for (int tabIndex=0; tabIndex < nTabs; tabIndex++)
			getPanelAt(tabIndex).populate(accountTotals);
		}

	public void setDate(String yyyymmdd)
		{
		fyyyymmdd = Integer.parseInt(yyyymmdd);
		populate();
		}
	}

final class AccountField
	extends JTextField
	{
	private final Account fAccount;
	private final JLabel fLabel;

	AccountField(Account aAccount)
		{
		super();
		fAccount = aAccount;
		fLabel = new JLabel(aAccount.prompt());
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
