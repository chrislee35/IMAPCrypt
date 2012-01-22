package org.dhs.chrislee.imapgui.groups;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dhs.chrislee.imapgui.FilterPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class FilterGroup {


	/** this variable is the super object, if it is needed */
	private final Composite composite;
	private final Display display;
	
	private Button senderFilter;
	private Button subjectFilter;
	
	private final Set<String> senderSet;
	private final Set<String> subjectSet;
	
	private final Set<String> invertFilter;
	
	public FilterGroup( Composite comp, Display display ) {
		composite = comp;
		this.display = display;
		senderSet = new HashSet<String>();
		subjectSet = new HashSet<String>();
		invertFilter = new HashSet<String>();
	}

	/**
	 * Actually push the items out to the composite for display
	 */
	public void addToGUI() {
		composite.setLayout( new RowLayout() );

		/* add items */
		createItems();

		composite.pack();		
	}
	
	/**
	 * Create the items within the GUI
	 */
	private void createItems() {
		
		senderFilter = new Button(composite, SWT.PUSH);
		senderFilter.setText("Specify Senders...");
		senderFilter.setToolTipText("Encrypt messages from the these senders");
		senderFilter.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event event ) {
				openPopup(KeyConstants.SENDERS, senderSet);
			}
		});
		
		subjectFilter = new Button(composite, SWT.PUSH);
		subjectFilter.setText("Specify Subjects...");
		subjectFilter.setToolTipText("Encrypt messages with these subjects");
		subjectFilter.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event event ) {
				openPopup(KeyConstants.SUBJECTS, subjectSet);
			}
		});
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
		FilterPopup fp = new FilterPopup(type, display);
		/* push the current filters into the popup */
		fp.loadFilters(set);
		/* run the popup */
		fp.open();
		/* reset our stored filters */
		set.clear();
		/* pull the new filters out of the popup */
		set.addAll(fp.getFilters());
		/* get the inversion */
		if( fp.getInvert() )
			invertFilter.add(type);
	}
	
	/**
	 * Get the sets that are stored within this object
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
