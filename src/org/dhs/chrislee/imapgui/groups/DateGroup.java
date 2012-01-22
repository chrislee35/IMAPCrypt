package org.dhs.chrislee.imapgui.groups;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DateGroup {

	/** this variable is the super object, if it is needed */
	private final Composite composite;
	
	private Button daysAgo;
	private Button daysUntil;
	private Text daysAgoText;
	private Text daysUntilText;
	
	public DateGroup( Composite comp, int options ) {
		composite = comp;
	}

	public void addToGUI() {
		composite.setLayout( new GridLayout( 3, false ) );

		/* add items */
		createItems();

		composite.pack();		
	}
	
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
