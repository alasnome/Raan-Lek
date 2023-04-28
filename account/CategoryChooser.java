package com.alasnome.apps.RaanLek.account;
/********************************************************************
* @(#)CategoryChooser.java 1.00 201201129
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* CategoryChooser: Extends JComboBox to allow user to choose an account
* category. In order to display both Thai and English, and to show the
* number along with the text, we implement a custom data model and cel
*l renderer and implement the Multilingual interface.
*
* @author Rick Salamone
* @version 1.00
* 201201129 rts created
*******************************************************/
import java.io.*;
import java.awt.Component;
import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import com.alasnome.apps.RaanLek.Category;
import com.alasnome.apps.RaanLek.Multilingual;
import com.alasnome.apps.RaanLek.Raan;
import com.alasnome.apps.RaanLek.RaanListener;
import com.alasnome.apps.RaanLek.RaanEvent;

public final class CategoryChooser
	extends JComboBox
	implements Multilingual
	{
	private final CategoryListCellRenderer _renderer = new CategoryListCellRenderer();

	public CategoryChooser(boolean aAllowBlank)
		{
		super(new CategoryListModel(aAllowBlank));
		setRenderer(_renderer);
		}

	/** implement Multilingual interface */
	public void updateLanguage(String aLanguage)
		{
		_renderer.setThai(aLanguage.equals(Multilingual.THAI));
		}
	}

final class CategoryListModel
		extends AbstractListModel
		implements ComboBoxModel, RaanListener
		{
		private Category[] fCategories;
		private final boolean fAllowBlank;
		Object fSelected;

		CategoryListModel(boolean aAllowBlank)
			{
			fAllowBlank = aAllowBlank;
			Raan.addRaanListener(this);
			}

		@Override public void setSelectedItem(Object anItem) { fSelected = anItem; }
		@Override public Object getSelectedItem() { return fSelected; }
		@Override public void userAction(RaanEvent e)
			{
			if ( e.type() == RaanEvent.SET_CURRENT )
				{
				fCategories = Raan.getCategories();
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
			return fCategories[index];
			}
		@Override public int getSize()
			{
			int size = (fCategories==null)? 0 : fCategories.length;
			if (fAllowBlank) ++size;
			return size;
			}
		}

final class CategoryListCellRenderer
	extends JLabel
	implements ListCellRenderer // , Multilingual
	{
	private boolean fIsThai;

	public CategoryListCellRenderer()
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
		else if (value instanceof Category)
			{
			Category category = (Category)value;
			text = category.id() + " - "
		            + ((fIsThai)? category.getThai() : category.getEnglish());
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
