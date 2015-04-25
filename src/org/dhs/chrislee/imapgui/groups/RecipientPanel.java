package org.dhs.chrislee.imapgui.groups;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.dhs.chrislee.gnupg.GPGKeyId;
import org.dhs.chrislee.gnupg.GPGKeyList;
import org.dhs.chrislee.imapgui.KeySelectorPanel;

/**
 * This class holds the graphical interfacing to allow
 * the user to specify the recipients that should be
 * encrypted.
 *
 */
public class RecipientPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7366241903245193956L;
	KeySelectorPanel ksp;
	
	/**
	 * Only constructor for this object
	 * @param comp the parent composite to hold this object
	 */
	public RecipientPanel() {
		super();
		JButton recipientButton = new JButton("Encrypt to...");
		ksp = new KeySelectorPanel();
		recipientButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ksp.setVisible(true);
			}
		});
		add(recipientButton);
	}
	
	/**
	 * Retrieve the values that the user has entered into the
	 * controls within the RecipientGroup.
	 * @return
	 */
	public Map<String, String> getGroupValues() {
		Map<String, String> valueMap = new HashMap<String, String>();
		GPGKeyList recipients = ksp.getSelectedGPGKeyList();
		if(recipients != null) {
			StringBuilder sb = new StringBuilder();
			String sep = "";
			for(GPGKeyId kid : recipients.getKeyIDArray()) {
				sb.append(sep);
				sb.append(kid.getKeyId());
				sep = ",";
			}
			valueMap.put(KeyConstants.RECIPIENTS, sb.toString() );
		}

		return valueMap;
	}
}
