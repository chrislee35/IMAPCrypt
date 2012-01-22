package org.dhs.chrislee.imapgui.groups;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class RecipientGroup {

	/** this variable is the super object, if it is needed */
	private final Composite composite;
	
	private Text recipientList;
	
	public RecipientGroup( Composite comp, int options ) {
		composite = comp;
	}
	
	public void addToGUI() {
		composite.setLayout( new FillLayout() );

		/* add items */
		createItems();

		composite.pack();		
	}
	
	private void createItems() {
		
		recipientList = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		recipientList.setToolTipText("Specify the email addresses or KeyIDs for encryption,\n" +
				"separated by commas, semicolons, newlines, or spaces");
		
	}
	
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
