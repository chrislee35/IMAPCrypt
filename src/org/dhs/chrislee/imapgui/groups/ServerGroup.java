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
	private Text folderText;
	private PasswordGroup passwordComposite;
	private FolderGroup linkToFolderComposite;

	public ServerGroup( Composite comp, int options, Shell shell ) {
		composite = comp;
		this.shell = shell;
	}
	
	public void addToGUI() {
		composite.setLayout( new GridLayout( 2, true ) );

		/* add items */
		createItems();

		composite.pack();		
	}

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
		passwordComposite = new PasswordGroup( passwordGroup, SWT.NONE, shell );
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
	
	public void setFolderComposite(FolderGroup folderComposite) {
		this.linkToFolderComposite = folderComposite;
	}

	public Map<String, String> getGroupValues() {
		Map<String, String> valueMap = new HashMap<String, String>();

		valueMap.put(KeyConstants.SERVER, serverText.getText() );
		valueMap.put(KeyConstants.USER, userText.getText() );
		valueMap.putAll(passwordComposite.getGroupValues());

		return valueMap;
	}
}
