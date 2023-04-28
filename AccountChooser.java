package com.alasnome.apps.RaanLek;
/********************************************************************
* @(#)AccountChooser.java 1.00 201201128
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* AccountChooser: Extends JComboBox to allow user to choose an
* account. In order to display both Thai and English, and to show
* the number along with the text, we implement a custom data model
* and cell renderer and implement the Multilingual interface.
*
* @author Rick Salamone
* @version 1.00
* 201201128 rts created
*******************************************************/
import java.io.*;
import java.awt.Component;
import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public final class AccountChooser
	extends JComboBox
	implements Multilingual
	{
	private final AccountListCellRenderer _renderer = new AccountListCellRenderer();

	public AccountChooser(boolean aAllowBlank)
		{
		super(new AccountListModel(aAllowBlank));
		setRenderer(_renderer);
		}

	public void setSelectedID(int aAccountID)
		{
		setSelectedItem(((AccountListModel)getModel()).find(aAccountID));
		}

	/** implement Multilingual interface */
	public void updateLanguage(String aLanguage)
		{
		_renderer.setThai(aLanguage.equals(Multilingual.THAI));
		}
	}

final class AccountListModel
		extends AbstractListModel
		implements ComboBoxModel, RaanListener
		{
		private AccountList fAccounts;
		private final boolean fAllowBlank;
		Object fSelected;

		AccountListModel(boolean aAllowBlank)
			{
			fAllowBlank = aAllowBlank;
			Raan.addRaanListener(this);
			}

		public Account find(int aAccountID)
			{
			return fAccounts.find(aAccountID);
			}

		@Override public void setSelectedItem(Object anItem) { fSelected = anItem; }
		@Override public Object getSelectedItem() { return fSelected; }
		@Override public void userAction(RaanEvent e)
			{
			if ( e.type() == RaanEvent.SET_CURRENT )
				{
				fAccounts = e.getRaan().getAccountList();
				fSelected = (getSize() > 0)? getElementAt(0) : null;
				}
			}

		@Override public Object getElementAt(int index)
			{
			if (fAllowBlank)
				{
				if (index == 0) return "--";
				else --index;
				}
			return fAccounts.getRow(index);
			}
		@Override public int getSize()
			{
			int size = (fAccounts==null)? 0 : fAccounts.size();
			if (fAllowBlank) ++size;
			return size;
			}
		}

final class AccountListCellRenderer
	extends JLabel
	implements ListCellRenderer // , Multilingual
	{
	private boolean fIsThai;

	public AccountListCellRenderer()
		{
		setOpaque(true);
		}

	void setThai(boolean aIsThai) { fIsThai = aIsThai; }

	/** implement ListCellRenderer interface */
	public Component getListCellRendererComponent( JList list,
		Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
		String text;
		if (value == null)
			text = "null";
		else if (value instanceof Account)
			{
			Account account = (Account)value;
			text = account.id() + " - "
		            + ((fIsThai)? account.getThai() : account.getEnglish());
			}
		else text = value.toString();
		setText(text);

		if (isSelected)
			{
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
			}
		else
			{
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			}
		return this;
		}
	}
