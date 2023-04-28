package com.alasnome.apps.RaanLek.account;
/********************************************************************
* @(#)AccountsView.java	1.00 20121125
* Copyright � 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* AccountsView: A panel that prompts the user for WHERE and ORDER BY
* clauses in order to display the requested records in the table.
*
* @author Rick Salamone
* @version 1.00
* 20121125 rts created based on 
*******************************************************/
import com.shanebow.util.SBLog;
import com.shanebow.util.SBProperties;
import com.shanebow.ui.FontSizeSlider;
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBAction;
import com.shanebow.ui.SBDialog;
import com.shanebow.ui.SBTextPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.event.TableModelEvent;
import com.alasnome.apps.RaanLek.*;

public class AccountsView
	extends JPanel
	implements Multilingual
	{
	private static final String KEY_COLUMN_FIT="usr.view.acnt.col.fit";
	private static final String KEY_SHOW_ADDS="usr.view.acnt.showadds";
	private static final String KEY_FONT_SIZE="usr.view.acnt.font.size";

	private AccountTable   m_table = null;
	private JPopupMenu fPopup; // table's popup menu
	private JTextField ftfFind = new JTextField(20); // find what?
	private Account fClickedAccount;

	private final SBAction fActSave
		= new SBAction( "Save", 'S', "Save the store's data", null)
		{
		@Override public void actionPerformed(ActionEvent e) { Raan.save(); }
		};

	private final SBAction fActNew
		= new SBAction( "New", 'N', "Create a new account", null)
		{
		@Override public void actionPerformed(ActionEvent e) { DlgAccount.edit(null); }
		};

	public AccountsView()
		{
		super ( new BorderLayout());
		setBorder(LAF.bevel(5,5));
		add(createTable(new AccountList()), BorderLayout.CENTER);
		add(controlPanel(), BorderLayout.SOUTH);
//		setList(null);
		Raan.addRaanListener(new RaanListener()
			{
			/** implement RaanListener interface */
			@Override public void userAction(RaanEvent aRaanEvent)
				{
				int act = aRaanEvent.getID();
				if ( act == RaanEvent.SET_CURRENT )
						m_table.setModel(Raan.getAccounts());
// @TODO: this is a quick kludge
				else if (act == RaanEvent.ACNT_ADDED
				     ||  act == RaanEvent.ACNT_MODIFIED
				     ||  act == RaanEvent.ACNT_REMOVED)
						getModel().fireTableDataChanged();
				}
			});
		}

	/** implement Multilingual interface */
	public void updateLanguage(String aLanguage)
		{
		boolean isThai = aLanguage.equals(Multilingual.THAI);
		DlgAccount.updateLanguage(aLanguage);
		}

	public final void setList( Account aAccount )
		{
// @TODO: filtering not implemented in AccountList
//		getModel().reset(aAccount);
		}

	private void log( String fmt, Object... args )
		{
		SBLog.write( "AccountsView", String.format( fmt, args ));
		}

	private void setClickedAccount(MouseEvent e)
		{
		fClickedAccount = getModel().getRow(m_table.rowAtPoint(e.getPoint()));
		}

	private JComponent createTable(AccountList aModel)
		{
		m_table = new AccountTable(aModel);

		TableColumn column;
		column = m_table.getColumnModel().getColumn(Account.CATEGORY);
		column.setCellEditor(new DefaultCellEditor(new CategoryChooser(false)));

		column = m_table.getColumnModel().getColumn(Account.ID);
		column.setCellRenderer(new DefaultTableCellRenderer()
			{
			@Override public Component getTableCellRendererComponent(JTable t, Object value,
				 boolean selected, boolean focused, int row, int col)
				{
				Component cell = super.getTableCellRendererComponent(t, value,
				 selected, focused, row, col);
				Account account = getModel().getRow(row);
				cell.setBackground(Color.YELLOW);
				return cell;
				}
			});
		// replace action for SPACE key
		m_table.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "handleSpace");
		m_table.getActionMap().put("handleSpace", new AbstractAction()
			{
			@Override public void actionPerformed(ActionEvent e)
				{
				DlgAccount.edit(getModel().getRow(m_table.getSelectedRow()));
				}
			});

		//Create the popup menu
		fPopup = new JPopupMenu();
		fPopup.add(fActNew);
		fPopup.add(new SBAction("Edit", 'E', "Modify this account", null)
			{
			@Override public void actionPerformed(ActionEvent e)
				{ DlgAccount.edit(fClickedAccount); }
			});
		fPopup.add(new SBAction("Delete", 'D', "Delete this account", null)
			{
			@Override public void actionPerformed(ActionEvent e)
				{ delete(fClickedAccount); }
			});

		m_table.addMouseListener(new MouseAdapter() // to handle double clicks
			{
			public void mousePressed (MouseEvent e)
				{
				setClickedAccount(e);
				if (SwingUtilities.isRightMouseButton( e ) ) showPopup(e);
				else if (e.getClickCount() > 1) DlgAccount.edit(fClickedAccount);
				}
	//		public void mouseReleased (MouseEvent e) { showPopup(e); }
			private void showPopup (MouseEvent e)
				{
				fPopup.show (e.getComponent(), e.getX(), e.getY());
				}
			});
		// Use a scrollbar, in case there are many columns.
		JScrollPane scroller = new JScrollPane(m_table);
		scroller.setBorder(new BevelBorder(BevelBorder.LOWERED));
		return scroller;
		}

	private void delete(Account aAccount)
		{
		if (SBDialog.confirm("Delete Account",
		                     "This will permanently delete the selected line", this))
			Raan.deleteAccount(aAccount);
		}

	public AccountList getModel() { return (AccountList)m_table.getModel(); }

	private void selectAndShow(int row)
		{
		m_table.scrollToVisible(row, 0);
		m_table.setRowSelectionInterval(row,row);
		m_table.requestFocus();
		}

	private final SBAction fActFind = new SBAction("\u2192", 'N', "Find Next", null)
		{
		@Override public void actionPerformed(ActionEvent e)
			{
			String findWhat = ftfFind.getText().toLowerCase();
			setEnabled(!findWhat.isEmpty());
			if ( findWhat.isEmpty())
				return;
			AccountList model = getModel();
			int startRow = m_table.getSelectedRow();
			int numRows = model.getRowCount();
			for ( int row = startRow + 1; row < numRows; row++ )
				if ( model.getRow(row).contains(findWhat))
					{
					selectAndShow(row);
					return;
					}
			for ( int row = 0; row < startRow; row++ )
				if ( model.getRow(row).contains(findWhat))
					{
					selectAndShow(row);
					return;
					}
			java.awt.Toolkit.getDefaultToolkit().beep();
			}
		};

	private JPanel controlPanel()
		{
		JPanel it = new JPanel();
		it.setLayout(new BoxLayout(it, BoxLayout.LINE_AXIS));
		it.setBorder(new EmptyBorder(5,0,0,0));
		Dimension SPACER = new Dimension(5, 0);

		it.add(fActNew.makeButton());
		it.add(Box.createRigidArea(SPACER));

		// get the find button's height for sizing text field & combo box
		JButton btnFind = fActFind.makeButton();
		int compHeight = btnFind.getPreferredSize().height;

		it.add(new JLabel("Filter: "));
		JComboBox filter = new CategoryChooser(true);

		Dimension size = filter.getPreferredSize();
		size.height = compHeight;
		filter.setMaximumSize(size);
		filter.addActionListener(new ActionListener()
			{
			@Override public void actionPerformed(ActionEvent e)
				{
				try // intentially throw if item 0 selected
					{
					Account selected = (Account)((JComboBox)e.getSource()).getSelectedItem();
					setList(selected);
					}
				catch (Exception x) { setList(null); }
				}
			});
		it.add(filter);

		it.add(Box.createRigidArea(SPACER));
		it.add(new JLabel("Find: "));
		size = ftfFind.getPreferredSize();
		size.height = compHeight;
		ftfFind.setMaximumSize(size);
		ftfFind.addActionListener(fActFind);
		it.add(ftfFind);

		it.add(Box.createRigidArea(SPACER));
		fActFind.setEnabled(false); // ftfFind is empty at startup
		it.add(btnFind);

		it.add(Box.createHorizontalGlue());
		it.add(Box.createRigidArea(SPACER));
		//... Create a slider for setting the size value
		SBProperties props = SBProperties.getInstance();
		boolean autoResize = props.getBoolean(KEY_COLUMN_FIT, true);
		m_table.setAutoResizeMode(autoResize? JTable.AUTO_RESIZE_ALL_COLUMNS
		                                    : JTable.AUTO_RESIZE_OFF);
/*************
		JCheckBox chkAuto = new JCheckBox("Fit Columns", autoResize);
		chkAuto.addItemListener(new ItemListener()
			{
			public void itemStateChanged(ItemEvent e)
				{
				boolean auto = (e.getStateChange() == ItemEvent.SELECTED);
				SBProperties.set(KEY_COLUMN_FIT, auto );
				m_table.setAutoResizeMode(auto? JTable.AUTO_RESIZE_ALL_COLUMNS
		                                   : JTable.AUTO_RESIZE_OFF);
				}
			});
		it.add(chkAuto);
		it.add(Box.createRigidArea(SPACER));
*************/
		boolean showAdds = props.getBoolean(KEY_SHOW_ADDS, true);
		JCheckBox chkShowAdds = new JCheckBox("Scroll to Added", showAdds);
		m_table.setScrollToNewData(showAdds);
		chkShowAdds.addItemListener(new ItemListener()
			{
			public void itemStateChanged(ItemEvent e)
				{
				boolean showAdds = (e.getStateChange() == ItemEvent.SELECTED);
				SBProperties.set(KEY_SHOW_ADDS, showAdds );
				m_table.setScrollToNewData(showAdds);
				}
			});
		it.add(chkShowAdds);
		it.add(Box.createRigidArea(SPACER));

		FontSizeSlider sizer = new FontSizeSlider(m_table, KEY_FONT_SIZE, true);
		sizer.setMaximumSize(size); // same as find text field
		it.add( sizer );
		it.add(Box.createRigidArea(SPACER));
		it.add(fActSave.makeButton());
		return it;
		}
	}

class AccountTable
	extends JTable
	implements Multilingual
	{
	private boolean fShowAdds; // scroll To New Data?

	public AccountTable(AccountList model)
		{
		super(model);
		setSelectionMode ( ListSelectionModel.SINGLE_SELECTION );
		JTableHeader header = getTableHeader();
		header.setUpdateTableInRealTime(true);
		header.setReorderingAllowed(true);
		header.addMouseListener(new MouseAdapter() // to handle sorts
			{
			public void mouseClicked(MouseEvent e)
				{
				TableColumnModel colModel = getColumnModel();
				int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
				int sortColumn = colModel.getColumn(columnModelIndex).getModelIndex();
				int shiftPressed = e.getModifiers()&InputEvent.SHIFT_MASK;
				boolean ascending = (shiftPressed == 0);
// @TODO: sorting not implemented in AccountList
//				((AccountList)getModel()).sort(sortColumn, ascending);
				}
			});
		setColumnWidths();
		}

	public final void setColumnWidths()
		{
		for ( int c = getModel().getColumnCount(); c-- > 0; )
			getColumnModel().getColumn(c).setPreferredWidth(Account.FIELD_WIDTHS[c]);
// System.out.println("set widths");
		}

	@Override public void tableChanged(TableModelEvent e)
		{
		super.tableChanged(e);
		if (!fShowAdds
		|| e.getType() != TableModelEvent.INSERT)
			return;
		int lastRow = e.getLastRow();
		if ( lastRow > 5)
			scrollToVisible(lastRow, 0);
		}

	public final void scrollToVisible(int row, int col)
		{
		Rectangle cell = getCellRect(row, col, true);
		int viewHeight = ((JViewport)getParent()).getExtentSize().height; 
		cell.height += viewHeight - cell.height;
		scrollRectToVisible(cell);
		}

	final void setScrollToNewData(boolean showAdds) { fShowAdds = showAdds; }

	/** implement Multilingual interface */
	public void updateLanguage(String aLanguage)
		{
System.out.println("REGISTER LANGUAGE: " + aLanguage);
		((AccountList)getModel())
		    .setThai(aLanguage.equals(Multilingual.THAI));
		}
	}
