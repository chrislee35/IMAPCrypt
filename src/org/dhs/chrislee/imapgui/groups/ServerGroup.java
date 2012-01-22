package org.dhs.chrislee.imapgui.groups;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.dhs.chrislee.IMAPCrypt;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ServerGroup {

	/** this variable is the super object, if it is needed */
	private final Composite composite;
	/** this is the Shell variable, if it is needed */
	private final Shell shell;

	private Text serverText;
	private Text userText;
	private PasswordGroup passwordComposite;
	private FolderGroup linkToFolderComposite;

	/**
	 * Only constructor for this object
	 * @param comp the parent composite to hold this object
	 * @param shell used for the PasswordComposite to be able to generate FileDialogs
	 */
	public ServerGroup( Composite comp, Shell shell ) {
		composite = comp;
		this.shell = shell;
	}
	
	/**
	 * Function that instructs the ServerGroup to actually add its
	 * contents to the GUI. This allows the construction of the object
	 * to occur at a different time from the addition to the GUI.
	 */
	public void addToGUI() {
		composite.setLayout( new GridLayout( 2, true ) );

		/* add items */
		createItems();

		composite.pack();		
	}

	/**
	 * Private function that is responsible for adding the components
	 * to the composite from the constructor.
	 */
	private void createItems() {

		Label serverLabel = new Label( composite, SWT.NONE );
		serverLabel.setText( "Server Hostname or IP Address" );
		GridData serverLabelGD = new GridData();
		serverLabelGD.horizontalAlignment = GridData.CENTER;
		serverLabel.setLayoutData( serverLabelGD );

		Label usernameLabel = new Label( composite, SWT.NONE );
		usernameLabel.setText( "Username" );
		GridData userLabelGD = new GridData();
		userLabelGD.horizontalAlignment = GridData.CENTER;
		usernameLabel.setLayoutData( userLabelGD );

		serverText = new Text( composite, SWT.SINGLE | SWT.BORDER );
		serverText.setToolTipText( "Hostname or IP address of the IMAP server" );
		GridData serverTextGD = new GridData();
		serverTextGD.horizontalAlignment = GridData.FILL;
		serverTextGD.grabExcessHorizontalSpace = true;
		serverTextGD.widthHint = 250;
		serverText.setLayoutData( serverTextGD );

		userText = new Text( composite, SWT.SINGLE | SWT.BORDER );
		userText.setToolTipText( "Username to login to the IMAP server" );
		GridData userTextGD = new GridData();
		userTextGD.horizontalAlignment = GridData.FILL;
		userTextGD.grabExcessHorizontalSpace = true;
		userText.setLayoutData( userTextGD );

		Group passwordGroup = new Group( composite, SWT.NONE );
		passwordGroup.setText( "Password Information" );
		GridData passGroupGD = new GridData();
		passGroupGD.horizontalSpan = 2;
		passGroupGD.horizontalAlignment = GridData.FILL;
		passGroupGD.grabExcessHorizontalSpace = true;
		passwordGroup.setLayoutData(passGroupGD);
		passwordComposite = new PasswordGroup( passwordGroup, shell );
		passwordComposite.addToGUI();
		
		Label blank = new Label(composite, SWT.NONE);
		
		Button connect = new Button(composite, SWT.PUSH);
		connect.setText(" Connect to Server ");
		GridData connectGD = new GridData();
		connectGD.horizontalAlignment = GridData.END;
		connect.setLayoutData(connectGD);
		connect.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event event ) {
				
				Map<String, String> values = getGroupValues();
				boolean passFile = Boolean.parseBoolean(values.get("usePassFile"));
				String password;
				if( passFile )
					password = values.get("passwordFile");
				else
					password = values.get("password");
				
				IMAPCrypt crypt = new IMAPCrypt(values.get("serverIP"),
						values.get("username"), password);
				TreeSet<String> folders = crypt.getFolders();
				if( folders != null ) {
					linkToFolderComposite.setFolders(folders);
				}
			}
		});
	}
	
	/**
	 * This sets the value of the FolderComposite in order for the ServerGroup
	 * to populate the list of folders when the connect button is hit. This
	 * function must be called before addToGUI() function is called.
	 * @param folderComposite
	 */
	public void setFolderComposite(FolderGroup folderComposite) {
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
		valueMap.putAll(passwordComposite.getGroupValues());

		return valueMap;
	}
}
