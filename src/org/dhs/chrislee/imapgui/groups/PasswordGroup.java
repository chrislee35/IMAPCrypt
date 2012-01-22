package org.dhs.chrislee.imapgui.groups;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This class holds the graphical interfacing to allow
 * the user to specify a password for the server directly or
 * have the password be contained within a password file.
 *
 */
public class PasswordGroup {

	/** this variable is the super object, if it is needed */
	private final Composite composite;
	/** this is the shell variable, if it is needed */
	private final Shell shell;

	private boolean usePassFile = true;
	private Text password;
	private Text passwordFile;

	/**
	 * Only constructor for this object
	 * @param comp the parent composite to hold this object
	 * @param shell used to create a FileDialog for the user to specify a password file
	 */
	public PasswordGroup( Composite comp, Shell shell ) {
		composite = comp;
		this.shell = shell;
	}
	
	/**
	 * Function that instructs the PasswordGroup to actually add its
	 * contents to the GUI. This allows the construction of the object
	 * to occur at a different time from the addition to the GUI.
	 */
	public void addToGUI() {
		composite.setLayout( new GridLayout( 3, false ) );

		/* add items to this group */
		createItems();

		composite.pack();		
	}

	/**
	 * Private function that is responsible for adding the components
	 * to the composite from the constructor.
	 */
	private void createItems() {

		final Button passwordButton = new Button( composite, SWT.RADIO );
		passwordButton.setText( "Password: " );

		password = new Text( composite, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD );
		password.setEnabled( false );
		password.setToolTipText( "Password to login to IMAP server" );
		GridData passGD = new GridData();
		passGD.horizontalSpan = 2;
		passGD.grabExcessHorizontalSpace = true;
		passGD.horizontalAlignment = GridData.FILL;
		password.setLayoutData( passGD );

		final Button passFileButton = new Button( composite, SWT.RADIO );
		passFileButton.setText( "Password File: " );
		passFileButton.setSelection( true );

		passwordFile = new Text( composite, SWT.SINGLE | SWT.BORDER );
		passwordFile.setText("No password file set");
		passwordFile.setEditable( false );
		passwordFile.setToolTipText( "Filename containing the password "
				+ "to login to the IMAP server, use Open to set" );
		GridData passFileGD = new GridData();
		passFileGD.grabExcessHorizontalSpace = true;
		passFileGD.horizontalAlignment = GridData.FILL;
		passwordFile.setLayoutData( passFileGD );

		final Button passFileOpen = new Button( composite, SWT.PUSH );
		passFileOpen.setText( "Open..." );
		OpenListener openListener = new OpenListener( shell, passwordFile );
		openListener.setTitle( "Select Password File" );
		openListener.setExtensions( new String[] { "*.*" } );
		passFileOpen.addSelectionListener( openListener );

		passwordButton.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event arg0 ) {
				password.setEnabled( true );
				passwordFile.setEnabled( false );
				passFileOpen.setEnabled( false );
				usePassFile = false;
			}
		} );
		passFileButton.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event arg0 ) {
				password.setEnabled( false );
				passwordFile.setEnabled( true );
				passFileOpen.setEnabled( true );
				usePassFile = true;
			}
		} );
	}

	/**
	 * Retrieve the values that the user has entered into the
	 * controls within the PasswordGroup.
	 * @return
	 */
	public Map<String, String> getGroupValues() {
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put(KeyConstants.USEPASSFILE, String.valueOf(usePassFile));
		valueMap.put(KeyConstants.PASSWORD, password.getText() );
		valueMap.put(KeyConstants.PASSFILE, passwordFile.getText() );
		return valueMap;
	}
}