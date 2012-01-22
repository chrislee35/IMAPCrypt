package org.dhs.chrislee.imapgui.groups;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * This class holds the graphical interfacing to allow
 * the user to specify the folder containing the messages
 * that should be encrypted.
 *
 */
public class FolderGroup {

	/** this variable is the super object, if it is needed */
	private final Composite composite;

	private Combo folderCombo;
	
	/**
	 * Only constructor for this object
	 * @param comp the parent composite to hold this object
	 */
	public FolderGroup( Composite comp ) {
		composite = comp;
	}
	
	/**
	 * Function that instructs the FolderGroup to actually add its
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
	
		folderCombo = new Combo( composite, SWT.SINGLE | SWT.READ_ONLY);
		folderCombo.setToolTipText("Select the folder to be encrypted");
		folderCombo.setEnabled(false);
	}

	/**
	 * This object does not know the folders to populate the combo with
	 * until they are added with this function. This function must be
	 * called after the addToGUI() function is called
	 * @param folders a Set containing the folders to add
	 */
	public void setFolders(Set<String> folders) {
		/* set the combo items and enable the combo */
		folderCombo.setItems(folders.toArray(new String[0]));
		folderCombo.setEnabled(true);
	}
	
	/**
	 * Retrieve the values that the user has entered into the
	 * controls within the FolderGroup.
	 * @return
	 */
	public Map<String, String> getGroupValues() {
		Map<String, String> valueMap = new HashMap<String, String>();

		int selectedIndex = folderCombo.getSelectionIndex();
		if( selectedIndex != -1 )
			valueMap.put( KeyConstants.FOLDER, folderCombo.getItem(selectedIndex) );

		return valueMap;
	}
}