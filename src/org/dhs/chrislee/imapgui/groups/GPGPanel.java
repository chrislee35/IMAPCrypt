package org.dhs.chrislee.imapgui.groups;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dhs.chrislee.IMAPCrypt;

/**
 * This class holds the graphical interfacing to allow
 * the user to specify the location of GPG on their system.
 *
 */
public class GPGPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1559337598642431338L;
	private JTextField gpgLocation;
	private JButton open;

	/**
	 * This constructor holds all of the needed variables for the group. Composite is
	 * the super object that holds this group. Display and Shell are for actions
	 * that this group takes.
	 * @param comp the parent that will host this object
	 * @param display the display needed for configuration options
	 * @param shell the shell needed to open windows
	 */
	public GPGPanel() {
		setName("GPG Location");
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		gpgLocation = new JTextField();
		gpgLocation.setText("Searching for GPG...");
		gpgLocation.setEditable(false);
		gpgLocation.setToolTipText("Path to GPG executable, click Open to set");
		open = new JButton();
		open.setText( "Open..." );
		open.setEnabled(true);
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				File gpgFile = fileChooser.getSelectedFile();
				if(gpgFile != null)
					gpgLocation.setText(gpgFile.getAbsolutePath());
			}
		});
		String gpgLoc = new IMAPCrypt(null, null, null).getGPGPath();
		if( gpgLoc == null ) {
			gpgLocation.setText("Unable to find GPG, please set manually");
		} else {
			gpgLocation.setText(gpgLoc);
		}
		gpgLocation.setEnabled(true);
		add(gpgLocation);
		add(open);
	}

	/**
	 * Retrieve the values that the user has entered into the
	 * controls within the GPGGroup.
	 * @return
	 */
	public Map<String, String> getGroupValues() {
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put( "gpgLocation", gpgLocation.getText() );
		return valueMap;
	}
}