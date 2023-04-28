package com.alasnome.apps.RaanLek.register;
/********************************************************************
* @(#)TransactionAmountCellRenderer.java 1.00 20121203
* Copyright (c) 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* TransactionAmountCellRenderer: Renders a PNL, which is an Integer holding the number
* of cents, using a blue background for a profit and red for a loss.
*
* @version 1.00
* @author Rick Salamone
* 20121203 rts created
*******************************************************/
import com.alasnome.apps.RaanLek.Transaction;
import com.alasnome.apps.RaanLek.Account;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.table.*;

public final class TransactionAmountCellRenderer
	extends DefaultTableCellRenderer 
 // JLabel
//	implements TableCellRenderer
	{
		{
		setOpaque(true);
		setHorizontalAlignment(RIGHT);
		setFont(new JTextField().getFont());
		setForeground( Color.WHITE );
		}

	public Component getTableCellRendererComponent(
                            JTable table, Object aTransaction,
                            boolean isSelected, boolean hasFocus,
                            int row, int column)
		{
		String text;
		Color bg;
		Transaction t = (Transaction)aTransaction;
		int amt = t.amount();
		if ( amt == 0 )
			{
			text = "";
			bg = isSelected?	table.getSelectionBackground()
			               : table.getBackground();
			}
		else
			{
			text = "" + amt;
			bg = t.account().type().color();
			if ( isSelected ) bg = bg.darker();
			}
		setText( text );
		setForeground( Color.WHITE );
		setBackground( bg );
		return this;
		}
	}
