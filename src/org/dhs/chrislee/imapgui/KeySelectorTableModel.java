package org.dhs.chrislee.imapgui;

import javax.swing.table.DefaultTableModel;

public class KeySelectorTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1722303027142377916L;
	public KeySelectorTableModel(Object[][] data, String[] columnNames) {
		super(data, columnNames);
	}
	public Class<?> getColumnClass(int columnIndex) {
		if(getValueAt(0, columnIndex) == null)
			return String.class;
        return getValueAt(0, columnIndex).getClass();
    }
	@Override
    public boolean isCellEditable(int row, int column) {
		return(column == 0);
    }
}
