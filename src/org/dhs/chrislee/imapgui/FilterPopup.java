package org.dhs.chrislee.imapgui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This is a popup window that allows a user to add filters to a list. It can
 * be used in standalone mode, but it is designed to be used entirely within the IMAPGui.
 *
 */
public class FilterPopup extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4485441347057989882L;
	
	private JDialog fp;
	private JList<String> filterList;
	private JTextField newFilter;
	private JCheckBox invertToggleButton;
	private Set<String> filters;
	private DefaultListModel<String> model;
	private boolean invertSave = false;
	
	/**
	 * Create a FilterPopup in standalone mode.
	 * @param filterType this String defines what type of filters can be entered
	 */
	public FilterPopup(Set<String> filters) {
		super();
		fp = this;
		this.filters = filters;
		model = new DefaultListModel<String>();
		for(String filter: filters)
			model.addElement(filter);
		
		
		/* create the list */
		
		filterList = new JList<String>(model);
		filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		filterList.setSize(200, 300);
		filterList.setMinimumSize(new Dimension(200, 300));
		filterList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int index = filterList.getSelectedIndex();
				if( index > -1 )
					newFilter.setText((String)filterList.getSelectedValue());
				
			}
		});
				
		JButton removeButton = new JButton();
		removeButton.setText("Remove");
		removeButton.setToolTipText("Remove the selected filter from the list." +
				"\nMultiple filters can be selected");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] indexes = filterList.getSelectedIndices();
				for(int index : indexes)
					model.remove(index);
			}
		});
		JButton selectAllButton = new JButton();
		selectAllButton.setText("Select All");
		selectAllButton.setToolTipText("Select all filters in the list");
		selectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterList.setSelectionInterval(0, model.getSize());
			}
		});

		/* middle row */
		newFilter = new JTextField();
		JButton addButton = new JButton();
		addButton.setText(" Add ");
		addButton.setToolTipText("Add the preceding text as a filter");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				String addFilter = newFilter.getText();
				if( !addFilter.equals("") ) {
					model.addElement(addFilter);
					newFilter.setText("");
				}
			}
		});
		
		/* bottom row */
		invertToggleButton = new JCheckBox();
		invertToggleButton.setText("Encrypt all but these");
		
		JButton okButton = new JButton();
		okButton.setText(" Save ");
		okButton.setToolTipText("Save changes to the filters");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				filters.clear();
				/* save the filters to the set */
				Enumeration<String> en = model.elements();
				while(en.hasMoreElements())
					filters.add(en.nextElement());
				
				invertSave = invertToggleButton.isSelected();
				fp.dispose();
			}
		});
		JButton cancelButton = new JButton();
		cancelButton.setText("Cancel");
		cancelButton.setToolTipText("Close and do not save changes to the filters");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fp.dispose();
			}
		});
		JScrollPane sp = new JScrollPane(filterList);
		sp.setSize(200, 300);
		sp.setMinimumSize(filterList.getMinimumSize());
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 7;
		add(sp, c);
		c.gridheight = 1;
		c.gridx++;
		add(addButton, c);
		c.gridy++;
		add(removeButton, c);
		c.gridy++;
		add(selectAllButton, c);
		c.gridy++;
		add(invertToggleButton, c);
		c.gridy++;
		add(okButton, c);
		c.gridy++;
		add(cancelButton, c);
		c.gridy = 7;
		c.gridx = 0;
		add(newFilter, c);
		pack();
	}
	
	/**
	 * If FilterPopup is used in slave mode, this function allows the IMAPGui to
	 * prepopulate the filter list with already entered filters. It should be called
	 * before the open() function.
	 * @param filters
	 */
	public void loadFilters(Set<String> filters) {
		/* add the filters */
		model.clear();
		for( String filter : filters )
			model.addElement(filter);
	}
	
	/**
	 * This allows the IMAPGui to retrieve the filters from the popup after it has closed
	 * @return
	 */
	public Set<String> getFilters() {
		return filters;
	}
	
	/**
	 * This allows the IMAPGui to retrieve the status of the inversion flag after the popup
	 * has closed.
	 * @return if true, then the filtering should be inverted. This means to encrypt everything except the provided filters
	 */
	public boolean getInvert() {
		return invertSave;
	}
	
	/**
	 * This is a testing main function that was used in development. It is not expected to
	 * be used at any time during IMAPGui operation.
	 * @param args
	 */
	public static void main( String[] args ) {
		HashSet<String> testSet = new HashSet<String>();
		testSet.add("test");
		testSet.add("test2");
		FilterPopup fp = new FilterPopup(testSet);
		fp.setVisible(true);
	}
}
