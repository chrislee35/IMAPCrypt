package org.dhs.chrislee.imapgui.groups;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class OpenListener implements SelectionListener {

	/** this is the shell object used to create the open dialog */
	private final Shell shell;
	/** this is the text object where the result is displayed */
	private final Text text;

	/** default title */
	private String title = "Open Dialog";
	/** default extensions */
	private String[] extensions = new String[]{ "*.*" };

	/**
	 * Constructor to set the Shell object and Text object.
	 * @param shell the Shell needed to open the dialog
	 * @param text the Text where the output is stored
	 */
	public OpenListener( Shell shell, Text text ) {
		this.shell = shell;
		this.text = text;
	}

	/**
	 * Sets the title of the FileDialog
	 * @param title
	 */
	public void setTitle( String title ) {
		this.title = title;
	}

	/**
	 * Sets the extensions used in the FileDialog
	 * @param extensions
	 */
	public void setExtensions( String[] extensions ) {
		this.extensions = extensions;
	}

	/**
	 * Creates a FileDialog, sets with the given title and extensions.
	 * Opens the FileDialog using the Shell object and stores
	 * the selected file into the Text object.
	 */
	@Override
	public void widgetSelected( SelectionEvent se ) {
		/* create the FileDialog as an Open Dialog */
		FileDialog fd = new FileDialog( shell, SWT.OPEN );
		/* set the title and extensions to use */
		fd.setText( title );
		fd.setFilterExtensions( extensions );
		/* store the result into a temporary string */
		String result = fd.open();
		/* result is null if the user cancels the FileDialog */
		if( result != null )
			text.setText( result );
	}

	/**
	 * This function is not used
	 */
	@Override
	public void widgetDefaultSelected( SelectionEvent arg0 ) {

	}
}
