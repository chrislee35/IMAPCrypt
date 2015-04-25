package org.dhs.chrislee.imapgui.groups;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.dhs.chrislee.imapgui.FilterPopup;

/**
 * This class holds the graphical interfacing for the other
 * filters that the user can apply to the message that
 * will be encrypted.
 *
 */
public class FilterPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1112956529739439555L;
	/**
	 * Right now, a button should be placed to apply a different
	 * filter. When we run out of room for buttons, we'll change
	 * this
	 */
	private JButton senderFilter;
	private JButton subjectFilter;
	
	/*
	 * A set should be added for each type of filter
	 */
	private final Set<String> senderSet;
	private final Set<String> subjectSet;
	
	/*
	 * This set simply holds the name of the filter that should be inverted
	 */
	private final Set<String> invertFilter;
	
	/**
	 * Only constructor for this object
	 * @param comp the parent composite to hold this object
	 * @param display the display used for the windows this object can create
	 */
	public FilterPanel() {
		senderSet = new HashSet<String>();
		subjectSet = new HashSet<String>();
		invertFilter = new HashSet<String>();
		senderFilter = new JButton();
		senderFilter.setText("Filter by Senders...");
		senderFilter.setToolTipText("Encrypt messages from the these senders");
		senderFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openPopup(KeyConstants.SENDERS, senderSet);
				
			}
			
		});

		subjectFilter = new JButton();
		subjectFilter.setText("Filter by Subjects...");
		subjectFilter.setToolTipText("Encrypt messages with these subjects");
		subjectFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openPopup(KeyConstants.SUBJECTS, subjectSet);
			}
		});
		
		add(senderFilter);
		add(subjectFilter);
	}
	
	/**
	 * Given a String type and a set to store information,
	 * open the FilterGroup popup and populate the set when the
	 * popup is closed
	 * @param type
	 * @param set
	 */
	private void openPopup(String type, Set<String> set) {
		/* create the popup */
		FilterPopup fp = new FilterPopup(set);
		/* push the current filters into the popup */
		fp.loadFilters(set);
		/* run the popup */
		fp.setVisible(true);
	}
	
	/**
	 * Retrieve the values that the user has entered into the
	 * controls within the FilterGroup.
	 * @return
	 */
	public Map<String, Set<String>> getFilters() {
		
		Map<String, Set<String>> values = new HashMap<String, Set<String>>();
		
		values.put(KeyConstants.SENDERS, senderSet);
		values.put(KeyConstants.SUBJECTS, subjectSet);
		
		for( String s : invertFilter ) {
			String key = s + KeyConstants.INVERT;
			values.put(key, new HashSet<String>());
		}
		
		return values;
	}
}
