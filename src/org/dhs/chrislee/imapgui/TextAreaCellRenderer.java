package org.dhs.chrislee.imapgui;

import java.awt.Color;
import java.awt.Component;
//import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

class TextAreaCellRenderer extends JTextArea implements TableCellRenderer {

    private static final long serialVersionUID = 1L;
    protected final static Color evenColor = new Color(230, 240, 255);

    public TextAreaCellRenderer() {
        super();
        setLineWrap(true);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
            //setBackground((row % 2 == 0) ? evenColor : getBackground());
        }
        setFont(table.getFont());
        setText((value == null) ? "" : value.toString());
        
        return this;
    }
}