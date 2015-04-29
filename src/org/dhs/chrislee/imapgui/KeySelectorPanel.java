package org.dhs.chrislee.imapgui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

import org.dhs.chrislee.gnupg.GPGKeyId;
import org.dhs.chrislee.gnupg.GPGKeyList;
import org.dhs.chrislee.gnupg.GPGUtil;

public class KeySelectorPanel extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5671795795057380451L;
	GPGKeyList secretKeyList;
	GPGKeyList publicKeyList;
	GPGKeyList selectedKeyIds;
	private KeySelectorTableModel dtm;
	
	public KeySelectorPanel() {
		this(
				GPGUtil.getSecretKeyIds(GPGUtil.findGPG()), 
				GPGUtil.getPublicKeyIds(GPGUtil.findGPG())
		);
	}
	public KeySelectorPanel(String gpgLoc) {
		this(
				GPGUtil.getSecretKeyIds(gpgLoc), 
				GPGUtil.getPublicKeyIds(gpgLoc)
		);
	}
	public KeySelectorPanel(GPGKeyList sec, GPGKeyList pub) {
		super();
		secretKeyList = sec;
		publicKeyList = pub;
		selectedKeyIds = new GPGKeyList();
		for(GPGKeyId kid : secretKeyList.getKeyIDArray())
			selectedKeyIds.addKeyId(kid);
		int uidColumnWidth = 500;
		
		String[] columnNames = {"Use?", "KeyId", "UID", "KID"};
		ArrayList<Object[]> data = new ArrayList<Object[]>();
		for(GPGKeyId kid : sec.getKeyIDArray()) {
			addGPGKeyIDToModel(kid, data);
			
		}
		for(GPGKeyId kid : pub.getKeyIDArray())
			addGPGKeyIDToModel(kid, data);
		Object[][] d = new Object[1][];
		d = data.toArray(d);
		dtm = new KeySelectorTableModel(d, columnNames);
		
		JTable table = new JTable(dtm){

            private static final long serialVersionUID = 1L;

            @Override
            public void doLayout() {
                TableColumn col = getColumnModel().getColumn(2);
                for (int row = 0; row < getRowCount(); row++) {
                    Component c = prepareRenderer(col.getCellRenderer(), row, 2);
                    if (c instanceof JTextArea) {
                        JTextArea a = (JTextArea) c;
                        int h = getPreferredHeight(a) + getIntercellSpacing().height;
                        if (getRowHeight(row) != h) {
                            setRowHeight(row, h);
                        }
                    }
                }
                super.doLayout();
            }

            private int getPreferredHeight(JTextComponent c) {
                Insets insets = c.getInsets();
                JTextArea a = (JTextArea) c;
                String text = a.getText();
                int lines = text.split("\\n").length;
                int preferredHeight = lines * 15;
                return preferredHeight + insets.top + insets.bottom;
            }
        };
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(25);
		table.getColumnModel().getColumn(1).setPreferredWidth(75);
		table.getColumnModel().getColumn(2).setPreferredWidth(uidColumnWidth);
		table.getColumnModel().getColumn(3).setPreferredWidth(0);
		table.getColumnModel().getColumn(2).setCellRenderer(new TextAreaCellRenderer());
		table.revalidate();

		table.setSize(uidColumnWidth+100, 600);
		JButton cancelButton = new JButton("Cancel");
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < dtm.getRowCount(); i++) {
					Boolean checked = (Boolean)dtm.getValueAt(i, 0);
					if(checked != null && checked) {
						GPGKeyId kid = (GPGKeyId)dtm.getValueAt(i, 3);
						if(kid != null)
							selectedKeyIds.addKeyId(kid);
					}
				}
				dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 5;
		JScrollPane sp = new JScrollPane(table);
		sp.setSize(new Dimension(uidColumnWidth+100, 600));
		add(sp, c);
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridx = 1;
		add(okButton, c);
		c.gridx = 1;
		c.gridy = 1;
		add(cancelButton, c);
		pack();
		setSize(uidColumnWidth+220, 600);
	}
		
	protected void addGPGKeyIDToModel(GPGKeyId kid, ArrayList<Object[]> data) {
		Object[] row = new Object[4];
		row[0] = new Boolean(kid.isSecret());
		row[1] = kid.getKeyId();
		ArrayList<String> uids = kid.getUids();
		StringBuilder sb = new StringBuilder();
		String sep = "";
		for(String uid : uids) {
			sb.append(sep+uid);
			sep = "\n";
		}
		row[2] = sb.toString();
		row[3] = kid;
		data.add(row);
	}
	
	public GPGKeyList getSelectedGPGKeyList() {
		return selectedKeyIds;
	}
	

	public static void main(String[] args) {
		KeySelectorPanel ksp = new KeySelectorPanel();
		ksp.setVisible(true);
	}
}
