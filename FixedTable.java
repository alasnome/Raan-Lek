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
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;

/**  Thows an NPE
<pre>
Exception in thread "main" java.lang.NullPointerException
        at tame.examples.FixedTable.access$000(FixedTable.java:21)
</pre>
@version 1.0 12/05/98
@author Nobuo Tamemasa
*/
public class FixedTable
	extends JPanel
	{
	Object[][] data;
  Object[] column;
  JTable fixedTable,table;

	public FixedTable()
		{
		super(new BorderLayout());
//		setSize( 400, 150 );

		data =  new Object[][]{
        {"1","11","A","","","","",""},
        {"2","22","","B","","","",""},
        {"3","33","","","C","","",""},
        {"4","44","","","","D","",""},
        {"5","55","","","","","E",""},
        {"6","66","","","","","","F"}};
		column = new Object[]{"fixed 1","fixed 2","a","b","c","d","e","f"};

		AbstractTableModel fixedModel = new AbstractTableModel()
			{
			public int getColumnCount() { return 2; }
			public int getRowCount() { return data.length; }
			public String getColumnName(int col) { return (String)column[col]; }
			public Object getValueAt(int row, int col) { return data[row][col]; }
			};
		AbstractTableModel    model = new AbstractTableModel()
			{
			public int getColumnCount() { return column.length -2; }
			public int getRowCount() { return data.length; }
			public String getColumnName(int col) { return (String)column[col +2]; }
			public Object getValueAt(int row, int col) { return data[row][col+2]; }
			public void setValueAt(Object obj, int r, int c) { data[r][c +2] = obj; }
			public boolean CellEditable(int row, int col) { return true; }
			};

		fixedTable = new JTable( fixedModel )
			{
			public void valueChanged(ListSelectionEvent e)
				{
				super.valueChanged(e);
				checkSelection(true);
				}
			};
		table = new JTable( model )
			{
			public void valueChanged(ListSelectionEvent e)
				{
				super.valueChanged(e);
				checkSelection(false);
				}
			};
		fixedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		fixedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scroll = new JScrollPane( table );
		JViewport viewport = new JViewport();
		viewport.setView(fixedTable);
		viewport.setPreferredSize(fixedTable.getPreferredSize());
		scroll.setRowHeaderView(viewport);
		scroll.setCorner(JScrollPane.UPPER_LEFT_CORNER,fixedTable.getTableHeader());

		add(scroll, BorderLayout.CENTER);
		}

	private void checkSelection(boolean isFixedTable)
		{
		int fixedSelectedIndex = fixedTable.getSelectedRow();
		int      selectedIndex = table.getSelectedRow();
		if (fixedSelectedIndex != selectedIndex)
			{
			if (isFixedTable)
				table.setRowSelectionInterval(fixedSelectedIndex,fixedSelectedIndex);
			else
				fixedTable.setRowSelectionInterval(selectedIndex,selectedIndex);
			}
		}
	}
