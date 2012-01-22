package org.dhs.chrislee.imapgui.groups;

import java.util.HashMap;
import java.util.Map;

import org.dhs.chrislee.IMAPCrypt;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class GPGGroup {

	/** this variable is the super object, if it is needed */
	private final Composite composite;
	/** this is the shell variable, if it is needed */
	private final Shell shell;
	/** this is the display variable, if it is needed */
	private final Display display;

	private Text gpgLocation;
	private Button open;

	/**
	 * This constructor holds all of the needed variables for the group. Composite is
	 * the super object that holds this group. Display and Shell are for actions
	 * that this group takes.
	 * @param comp
	 * @param options
	 * @param display
	 * @param shell
	 */
	public GPGGroup( Composite comp, int options, Display display, Shell shell ) {
		composite = comp;
		this.shell = shell;
		this.display = display;
	}
	
	public void addToGUI() {
		composite.setLayout( new GridLayout( 2, false ) );

		/* add items to this group */
		createItems();
		
		/* search for GPG */
		findGPG();

		composite.pack();
	}

	private void createItems() {

		/* first object in the column */
		gpgLocation = new Text( composite, SWT.BORDER | SWT.SINGLE );
		gpgLocation.setText( "Searching for GPG on hard disk..." );
		gpgLocation.setEditable( false );
		GridData gpgLocGD = new GridData();
		/* this object expands to fill the space */
		gpgLocGD.grabExcessHorizontalSpace = true;
		gpgLocGD.horizontalAlignment = GridData.FILL;
		gpgLocGD.verticalAlignment = GridData.CENTER;
		gpgLocation.setLayoutData( gpgLocGD );
		gpgLocation.setToolTipText( "Path to GPG, click Open to set" );

		/* second object in the column */
		open = new Button( composite, SWT.PUSH );
		open.setText( "Open..." );
		open.setEnabled(false);
		GridData openGD = new GridData();
		openGD.horizontalAlignment = GridData.END;
		openGD.verticalAlignment = GridData.CENTER;
		open.setLayoutData( openGD );
		OpenListener openListener = new OpenListener( shell, gpgLocation );
		openListener.setTitle( "Select GnuPG Executable" );
		String os = System.getProperty("os.name").toLowerCase();
		if(os.indexOf("win") >= 0) {
			openListener.setExtensions( new String[] { "*.exe" } );
		} else {
			openListener.setExtensions( new String[] { "*" } );
		}
		
		open.addSelectionListener( openListener );
	}

	private void findGPG() {
		String gpgLoc = new IMAPCrypt(null, null, null).getGPGPath();
		if( gpgLoc == null ) {
			gpgLocation.setText("Unable to find GPG, please set manually");
			gpgLocation.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		} else {
			gpgLocation.setText(gpgLoc);
		}
		open.setEnabled(true);
	}
	
	public Map<String, String> getGroupValues() {
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put( "gpgLocation", gpgLocation.getText() );
		return valueMap;
	}
}