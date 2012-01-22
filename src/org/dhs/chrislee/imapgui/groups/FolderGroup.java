package org.dhs.chrislee.imapgui.groups;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class FolderGroup {

	/** this variable is the super object, if it is needed */
	private final Composite composite;

	private Combo folderCombo;
	
	public FolderGroup( Composite comp, int options ) {
		composite = comp;
	}
	
	public void addToGUI() {
		composite.setLayout( new FillLayout() );

		/* add items */
		createItems();

		composite.pack();		
	}

	private void createItems() {	
	
		folderCombo = new Combo( composite, SWT.SINGLE | SWT.READ_ONLY);
		folderCombo.setToolTipText("Select the folder to be encrypted");
		folderCombo.setEnabled(false);
	}

	public void setFolders(TreeSet<String> folders) {
		/* set the combo items and enable the combo */
		folderCombo.setItems(folders.toArray(new String[0]));
		folderCombo.setEnabled(true);
	}
	
	/**
	 * Get the values of the objects within this group 
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