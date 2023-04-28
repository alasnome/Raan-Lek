package com.alasnome.apps.RaanLek.report;
/********************************************************************
* @(#)AccountCheckList.java 1.00 20121204
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* AccountCheckList: A component for editing the contact When field.
* Extends JTextField to implement FieldEditor.
*
* @author Rick Salamone
* @version 1.00
* 20121204 rts created
*******************************************************/
import com.alasnome.apps.RaanLek.*;
import java.awt.Component;
import java.awt.event.*;
import java.util.HashSet;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class AccountCheckList
	extends JList
	implements Multilingual
	{
	private HashSet<Integer> selectionCache = new HashSet<Integer>();
	private boolean fIsThai;

	/** implement Multilingual interface */
	public void updateLanguage(String aLanguage)
		{
		fIsThai = aLanguage.equals(Multilingual.THAI);
		repaint();
		}

	private void reload(AccountList aAccounts)
		{
		Account[] accounts = new Account[aAccounts.size()];
		int i = 0;
		for (Account account : aAccounts)
			accounts[i++] = account;
		setListData(accounts);
		}

	public AccountCheckList()
		{
		super();
		Raan.addRaanListener(new RaanListener()
			{
			@Override public void userAction(RaanEvent e)
				{
				if ( e.type() == RaanEvent.SET_CURRENT )
					reload(e.getRaan().getAccountList());
				}
			});

		setCellRenderer(new CheckListRenderer());
		addListSelectionListener( fListSelectionListener);

		// The whole purpose of this mouse listener is to allow the user
		// to deselect when there is only one selected item in the list.
		// When mouse is pressed see if there is only one selection, and
		// if so, when the mouse click is completed see if it was on the
		// solitary selection. Couple hours of effort here!
		addMouseListener(new MouseAdapter()
			{
			private boolean toggling = false;
			public void mousePressed(MouseEvent event)
				{
				toggling = (selectionCache.size() == 1);
				}

			public void mouseClicked(MouseEvent event)
				{
				if (!toggling) return;
				toggling = false;
				if (selectionCache.size() != 1)
					return;
				int index = locationToIndex(event.getPoint());
				if (selectionCache.remove(new Integer(index)))
					clearSelection();
				}
			}); 
		}

	private final ListSelectionListener fListSelectionListener = new ListSelectionListener()
		{
		public void valueChanged (ListSelectionEvent lse)
			{
			if (lse.getValueIsAdjusting())
				return;

			removeListSelectionListener (this);

			// determine if this selection has added or removed items
			HashSet<Integer> newSelections = new HashSet<Integer>();
			int size = getModel().getSize();
			for (int i=0; i<size; i++)
				if (getSelectionModel().isSelectedIndex(i))
					newSelections.add (new Integer(i));

			// turn on everything that was selected previously
			for (Integer it : selectionCache)
				{
				int index = it.intValue();
				getSelectionModel().addSelectionInterval(index, index);
				}

			// add or remove the delta
			for (Integer it : newSelections)
				{
				int index = it.intValue();
				if (selectionCache.contains(it))
					getSelectionModel().removeSelectionInterval (index, index);
				else
					getSelectionModel().addSelectionInterval (index, index);
				}

			// save selections for next time
			selectionCache.clear();
			for (int i=0; i<size; i++)
				if (getSelectionModel().isSelectedIndex(i))
					selectionCache.add (new Integer(i));
			addListSelectionListener (this);
			}
		};

	private void dump(HashSet<Integer> aSet, String aTitle)
		{
		System.out.print(aTitle);
		for (Integer i : aSet)
			System.out.print(" " + i);
		System.out.println();
		}
	class CheckListRenderer extends JCheckBox
		implements ListCellRenderer
		{
		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean hasFocus)
			{
if (value == null) return this;
			Account account = (Account)value;
			setEnabled(list.isEnabled());
	//		setSelected(isSelected); // works but has flicker
			setSelected(selectionCache.contains(new Integer(index)));
			setFont(list.getFont());
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			setText(fIsThai?account.getThai() : account.getEnglish());
			return this;
			}
		}
	}
