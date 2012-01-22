package org.dhs.chrislee.imapgui.groups;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * This class holds the graphical interfacing to allow
 * the user to specify the recipients that should be
 * encrypted.
 *
 */
public class RecipientGroup {

	/** this variable is the super object, if it is needed */
	private final Composite composite;
	
	private Text recipientList;
	
	/**
	 * Only constructor for this object
	 * @param comp the parent composite to hold this object
	 */
	public RecipientGroup( Composite comp ) {
		composite = comp;
	}
	
	/**
	 * Function that instructs the RecipientGroup to actually add its
	 * contents to the GUI. This allows the construction of the object
	 * to occur at a different time from the addition to the GUI.
	 */
	public void addToGUI() {
		composite.setLayout( new FillLayout() );

		/* add items */
		createItems();

		composite.pack();		
	}
	
	/**
	 * Private function that is responsible for adding the components
	 * to the composite from the constructor.
	 */
	private void createItems() {
		
		recipientList = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		recipientList.setToolTipText("Specify the email addresses or KeyIDs for encryption,\n" +
				"separated by commas, semicolons, newlines, or spaces");
		
	}
	
	/**
	 * Retrieve the values that the user has entered into the
	 * controls within the RecipientGroup.
	 * @return
	 */
	public Map<String, String> getGroupValues() {
		Map<String, String> valueMap = new HashMap<String, String>();
		
		String recipients = recipientList.getText();
		/* remove the newlines, spaces, and semicolons */
		recipients = recipients.replaceAll("[\\r\\n\\s;]+", ",");
		/* remove any double commas that are artifacts of the replaces */
		recipients = recipients.replaceAll(",,", ",");
		/* remove any trailing commas that have no names beyond */
		recipients = recipients.replaceAll(",[\\r\\n\\s]", "");
		
		valueMap.put(KeyConstants.RECIPIENTS, recipients );

		return valueMap;
	}
}
