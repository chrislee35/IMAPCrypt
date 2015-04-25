package org.dhs.chrislee.imapgui.groups;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * This class holds the graphical interfacing to allow
 * the user to specify the folder containing the messages
 * that should be encrypted.
 *
 */
public class FolderPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 288510273620032446L;
	private JComboBox<String> folderCombo;
	
	/**
	 * Only constructor for this object
	 * @param comp the parent composite to hold this object
	 */
	public FolderPanel() {
		super();	
		folderCombo = new JComboBox<String>();
		folderCombo.setToolTipText("Select the folder to be encrypted");
		folderCombo.setEnabled(false);
		setLayout(new BorderLayout());
		add(folderCombo, BorderLayout.NORTH);
	}

	/**
	 * This object does not know the folders to populate the combo with
	 * until they are added with this function. This function must be
	 * called after the addToGUI() function is called
	 * @param folders a Set containing the folders to add
	 */
	public void setFolders(Set<String> folders) {
		/* set the combo items and enable the combo */
		folderCombo.removeAllItems();
		for(String folder : folders)
			folderCombo.addItem(folder);
		folderCombo.setEnabled(true);
	}
	
	/**
	 * Retrieve the values that the user has entered into the
	 * controls within the FolderGroup.
	 * @return
	 */
	public Map<String, String> getGroupValues() {
		Map<String, String> valueMap = new HashMap<String, String>();
		

		String folder = (String) folderCombo.getSelectedItem();
		if( folder != null )
			valueMap.put( KeyConstants.FOLDER, folder );

		return valueMap;
	}
}