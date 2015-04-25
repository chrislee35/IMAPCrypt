package org.dhs.chrislee.imapgui.groups;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.mail.MessagingException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.dhs.chrislee.IMAPCrypt;
import org.dhs.chrislee.imapgui.IMAPGui;

public class ServerPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8307971264870721252L;

	private JPanel parent;
	private JTextField serverText;
	private JTextField userText;
	private boolean usePassFile = true;
	private JTextField password;
	private JTextField passwordFile;
	private FolderPanel linkToFolderComposite;

	/**
	 * Only constructor for this object
	 * @param comp the parent composite to hold this object
	 * @param shell used for the PasswordComposite to be able to generate FileDialogs
	 */
	public ServerPanel() {
		super();
		parent = this;
		
		JLabel serverLabel = new JLabel();
		serverLabel.setText( "Server Hostname or IP Address" );
		JLabel usernameLabel = new JLabel();
		usernameLabel.setText( "Username" );
		serverText = new JTextField();
		serverText.setToolTipText( "Hostname or IP address of the IMAP server" );

		userText = new JTextField();
		userText.setToolTipText( "Username to login to the IMAP server" );

		final ButtonGroup bg = new ButtonGroup();

		final JRadioButton passwordButton = new JRadioButton();
		passwordButton.setText( "Password: " );

		final JRadioButton passFileButton = new JRadioButton();
		passFileButton.setText( "Password File: " );
		passFileButton.setSelected( true );
		
		bg.add(passwordButton);
		bg.add(passFileButton);

		password = new JPasswordField();
		password.setEnabled( false );
		password.setToolTipText( "Password to login to IMAP server" );

		passwordFile = new JTextField();
		passwordFile.setText("No password file set");
		passwordFile.setEditable( false );
		passwordFile.setToolTipText( "Filename containing the password "
				+ "to login to the IMAP server, use Open to set" );

		final JButton passFileOpen = new JButton();
		passFileOpen.setText( "Open..." );
		passFileOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.showOpenDialog(parent);
				File passFile = jfc.getSelectedFile();
				passwordFile.setText(passFile.getAbsolutePath());
			}
		});
		passwordButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				usePassFile = false;
				password.setEnabled(!usePassFile);
				passwordFile.setEnabled(usePassFile);
				passFileOpen.setEnabled(usePassFile);
			}
		});
		passFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				usePassFile = true;
				password.setEnabled(!usePassFile);
				passwordFile.setEnabled(usePassFile);
				passFileOpen.setEnabled(usePassFile);
			}
		});
		
		
		JButton connect = new JButton();
		connect.setText(" Connect to Server ");
		connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Map<String, String> values = getGroupValues();
				boolean passFile = Boolean.parseBoolean(values.get("usePassFile"));
				String password;
				if( passFile )
					password = values.get("passwordFile");
				else
					password = values.get("password");
				
				IMAPCrypt crypt = new IMAPCrypt(values.get("serverIP"),
						values.get("username"), password);
				crypt.getLogger().addAppender(IMAPGui.getAppInstance());
				IMAPGui.getAppInstance().getLogBrokerMonitor().show();
				TreeSet<String> folders;
				try {
					folders = crypt.getFolders();
					if( folders != null ) {
						linkToFolderComposite.setFolders(folders);
					}
				} catch (javax.mail.AuthenticationFailedException e) {
					JFrame jf = new JFrame();
					JOptionPane.showMessageDialog(jf, "Authentication failed.  Please check your username and password.\n(Then make sure that you are really you.)");
				} catch (MessagingException e) {
					JFrame jf = new JFrame();
					int returnValue = JOptionPane.showConfirmDialog(jf, "Cannot confirm the validity of the server's certificate. \nWould you like to connect anyway?");
					if(returnValue == JOptionPane.OK_OPTION) {
						
					} else {
						
					}
					e.printStackTrace();
				}
			}
			
		});


		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		add(serverLabel, c);
		c.gridx = 1;
		add(usernameLabel, c);
		c.gridx = 0;
		c.gridy = 1;
		add(serverText, c);
		c.gridx = 1;
		add(userText, c);
		c.gridx = 0;
		c.gridy = 2;
		add(passwordButton, c);
		c.gridx = 1;
		add(password, c);
		c.gridx = 0;
		c.gridy = 3;
		add(passFileButton, c);
		c.gridx = 1;
		add(passwordFile, c);
		c.gridy = 4;
		add(connect, c);
	}
	
	/**
	 * This sets the value of the FolderComposite in order for the ServerGroup
	 * to populate the list of folders when the connect button is hit. This
	 * function must be called before addToGUI() function is called.
	 * @param folderComposite
	 */
	public void setFolderComposite(FolderPanel folderComposite) {
		this.linkToFolderComposite = folderComposite;
	}

	/**
	 * Retrieve the values that the user has entered into the
	 * controls within the ServerGroup.
	 * @return
	 */
	public Map<String, String> getGroupValues() {
		Map<String, String> valueMap = new HashMap<String, String>();

		valueMap.put(KeyConstants.SERVER, serverText.getText() );
		valueMap.put(KeyConstants.USER, userText.getText() );
		valueMap.put(KeyConstants.USEPASSFILE, String.valueOf(usePassFile));
		valueMap.put(KeyConstants.PASSWORD, password.getText() );
		valueMap.put(KeyConstants.PASSFILE, passwordFile.getText() );

		return valueMap;
	}
}
