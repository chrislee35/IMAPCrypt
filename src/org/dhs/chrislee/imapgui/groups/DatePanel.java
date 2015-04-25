package org.dhs.chrislee.imapgui.groups;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This class holds the graphical interfacing for the Date
 * filter that allows users to specify which email messages
 * should be encrypted based upon their age.
 */
public class DatePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8912532673967549875L;
	private JCheckBox daysAgo;
	private JCheckBox daysUntil;
	private JTextField daysAgoText;
	private JTextField daysUntilText;
	
	/**
	 * Only constructor for this object
	 * @param comp this is the composite the DateGroup will be added to
	 */
	public DatePanel() {
		super();
		daysAgo = new JCheckBox();
		daysAgo.setText("Encrypt mail older than ");
		daysAgoText = new JTextField();
		daysAgoText.setText("     ");
		daysAgoText.setToolTipText("Encrypt emails older than X days old.\n" +
				"If 0=everything, 1=yesterday and beyond.\nDefault is 0");
		JLabel daLabel = new JLabel();
		daLabel.setText(" days old ");
		
		daysUntil = new JCheckBox();
		daysUntil.setText("Encrypt mail younger than ");
		
		daysUntilText = new JTextField();
		daysUntilText.setText("     ");
		daysUntilText.setToolTipText("Encrypt emails younger than X days old.\n" + 
				"If 0=nothing, 1=today's only.\nDefault is MAXINT");
		
		JLabel duLabel = new JLabel();
		duLabel.setText(" days old");
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		add(daysAgo, c);
		c.gridx = 1;
		add(daysAgoText, c);
		c.gridx = 2;
		add(daLabel, c);
		c.gridy = 1;
		c.gridx = 0;
		add(daysUntil, c);
		c.gridx = 1;
		add(daysUntilText, c);
		c.gridx = 2;
		add(duLabel, c);
	}

	/**
	 * Retrieve the values that the user has entered into the
	 * controls within the DateGroup.
	 * @return
	 */
	public Map<String, String> getGroupValues() {
		Map<String, String> valueMap = new HashMap<String, String>();
		
		if( daysAgo.getText() != null ) {
			String daysAgo = daysAgoText.getText();
			if( daysAgo.equals("") )
				daysAgo = "0";
			valueMap.put( KeyConstants.DAYSAGO, daysAgo );
		}

		if( daysUntil.getText() != null ) {
			String daysUntil = daysUntilText.getText();
			if( daysUntil.equals("") )
				daysUntil = String.valueOf(Integer.MAX_VALUE);
			valueMap.put( KeyConstants.DAYSUNTIL, daysUntil );
		}

		return valueMap;
	}
}
