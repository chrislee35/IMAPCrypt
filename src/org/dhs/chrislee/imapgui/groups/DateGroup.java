package org.dhs.chrislee.imapgui.groups;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * This class holds the graphical interfacing for the Date
 * filter that allows users to specify which email messages
 * should be encrypted based upon their age.
 */
public class DateGroup {

	/** this variable is the super object, if it is needed */
	private final Composite composite;
	
	private Button daysAgo;
	private Button daysUntil;
	private Text daysAgoText;
	private Text daysUntilText;
	
	/**
	 * Only constructor for this object
	 * @param comp this is the composite the DateGroup will be added to
	 */
	public DateGroup( Composite comp) {
		composite = comp;
	}

	/**
	 * Function that instructs the DateGroup to actually add its
	 * contents to the GUI. This allows the construction of the object
	 * to occur at a different time from the addition to the GUI.
	 */
	public void addToGUI() {
		composite.setLayout( new GridLayout( 3, false ) );

		/* add items */
		createItems();

		composite.pack();		
	}
	
	/**
	 * Private function that is responsible for adding the components
	 * to the composite from the constructor.
	 */
	private void createItems() {
		
		daysAgo = new Button(composite, SWT.CHECK);
		daysAgo.setText("Encrypt mail older than ");
		
		daysAgoText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		daysAgoText.setToolTipText("Encrypt emails older than X days old.\n" +
				"If 0=everything, 1=yesterday and beyond.\nDefault is 0");

		Label daLabel = new Label(composite, SWT.NONE);
		daLabel.setText(" days old");
		
		daysUntil = new Button(composite, SWT.CHECK);
		daysUntil.setText("Encrypt mail younger than ");
		
		daysUntilText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		daysUntilText.setToolTipText("Encrypt emails younger than X days old.\n" + 
				"If 0=nothing, 1=today's only.\nDefault is MAXINT");
		
		Label duLabel = new Label(composite, SWT.NONE);
		duLabel.setText(" days old");
		
	}

	/**
	 * Retrieve the values that the user has entered into the
	 * controls within the DateGroup.
	 * @return
	 */
	public Map<String, String> getGroupValues() {
		Map<String, String> valueMap = new HashMap<String, String>();
		
		if( daysAgo.getSelection() ) {
			String daysAgo = daysAgoText.getText();
			if( daysAgo.equals("") )
				daysAgo = "0";
			valueMap.put( KeyConstants.DAYSAGO, daysAgo );
		}

		if( daysUntil.getSelection() ) {
			String daysUntil = daysUntilText.getText();
			if( daysUntil.equals("") )
				daysUntil = String.valueOf(Integer.MAX_VALUE);
			valueMap.put( KeyConstants.DAYSUNTIL, daysUntil );
		}

		return valueMap;
	}
}
