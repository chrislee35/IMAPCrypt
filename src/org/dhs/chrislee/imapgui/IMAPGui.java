package org.dhs.chrislee.imapgui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dhs.chrislee.DateBasedMessageEvaluationCallback;
import org.dhs.chrislee.IMAPCrypt;
import org.dhs.chrislee.SenderBasedMessageEvaluationCallback;
import org.dhs.chrislee.SubjectBasedMessageEvaluationCallback;
import org.dhs.chrislee.imapgui.groups.DateGroup;
import org.dhs.chrislee.imapgui.groups.FilterGroup;
import org.dhs.chrislee.imapgui.groups.FolderGroup;
import org.dhs.chrislee.imapgui.groups.GPGGroup;
import org.dhs.chrislee.imapgui.groups.KeyConstants;
import org.dhs.chrislee.imapgui.groups.RecipientGroup;
import org.dhs.chrislee.imapgui.groups.ServerGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class IMAPGui {

	private Display display;
	private Shell shell;

	private GPGGroup gpgComposite;
	private ServerGroup serverComposite;
	private FolderGroup folderComposite;
	private DateGroup dateComposite;
	private RecipientGroup recipComposite;
	private FilterGroup filterComposite;

	private boolean debug = false;
	
	/**
	 * Default constructor for this object
	 */
	public IMAPGui() {
		Display.setAppName( "IMAPCryptoGUI" );
	}

	/**
	 * A dynamic function that actually creates the windows and sets the items
	 * to display. This object will halt in this function while the program
	 * executes.
	 */
	public void run() {
		display = new Display();
		shell = new Shell( display, SWT.CLOSE );
		shell.setText( "IMAP Crypto GUI" );
		createContents( display, shell );
		if( createWarningBox() ) {
			shell.open();
			while( !shell.isDisposed() ) {
				if( !display.readAndDispatch() ) {
					display.sleep();
				}
			}
		}
		display.dispose();
	}

	/**
	 * Add the contents of the GUI to the display window so they appear
	 * 
	 * @param display
	 * @param shell
	 */
	private void createContents( final Display display, final Shell shell ) {

		RowLayout rl = new RowLayout();
		rl.type = SWT.VERTICAL;
		rl.fill = true;
		shell.setLayout(rl);

		/* create the groups, add them to the GUI,
		 * create composites
		 */
		Group gpgGroup = new Group( shell, SWT.NONE );
		gpgGroup.setText( "GnuPG Location" );
		gpgComposite = new GPGGroup( gpgGroup, SWT.NONE, display, shell );

		Group serverGroup = new Group( shell, SWT.NONE );
		serverGroup.setText( "Server Information" );
		serverComposite = new ServerGroup( serverGroup, SWT.NONE, shell );

		Group folderGroup = new Group( shell, SWT.NONE );
		folderGroup.setText("Folder To Encrypt");
		folderComposite = new FolderGroup( folderGroup, SWT.NONE);
		/* set the folder composites */
		serverComposite.setFolderComposite(folderComposite);

		Composite dateRecipGroup = new Group( shell, SWT.NONE );
		dateRecipGroup.setLayout(new FillLayout());
		
		TabFolder callbackTabs = new TabFolder(dateRecipGroup, SWT.NONE);
		TabItem dateTab = new TabItem(callbackTabs, SWT.NONE);
		dateTab.setText("Date Filter");	
		Composite dateComp = new Composite(callbackTabs, SWT.NONE);
		dateComp.setLayout(new FillLayout());
		dateComposite = new DateGroup( dateComp, SWT.NONE );
		dateTab.setControl(dateComp);
		TabItem filterTab = new TabItem(callbackTabs, SWT.NONE);
		filterTab.setText("Other Filters");
		Composite filterComp = new Composite(callbackTabs, SWT.NONE);
		filterComp.setLayout(new RowLayout());
		filterComposite = new FilterGroup(filterComp, display);
		filterTab.setControl(filterComp);
		
		Group recipGroup = new Group( dateRecipGroup, SWT.NONE );
		recipGroup.setText( "Encrypt the following KeyIDs" );
		recipComposite = new RecipientGroup( recipGroup, SWT.NONE );
		
		/* add the composites to the GUI */
		gpgComposite.addToGUI();
		serverComposite.addToGUI();
		folderComposite.addToGUI();
		dateComposite.addToGUI();
		recipComposite.addToGUI();
		filterComposite.addToGUI();
		
		Composite buttonGroup = new Composite(shell, SWT.NONE);
		buttonGroup.setLayout(new GridLayout(8, true));
		
		Label blank = new Label(buttonGroup, SWT.NONE);
		GridData blankGD = new GridData();
		blankGD.horizontalSpan = 6;
		blankGD.horizontalAlignment = GridData.FILL;
		blankGD.grabExcessHorizontalSpace = true;
		blank.setLayoutData(blankGD);
		
		Button runButton = new Button(buttonGroup, SWT.PUSH);
		runButton.setText("  Run  ");
		GridData runGD = new GridData();
		runGD.horizontalAlignment = GridData.FILL;
		runButton.setLayoutData(runGD);
		runButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected( SelectionEvent arg0 ) {
	
			}

			@Override
			public void widgetSelected( SelectionEvent arg0 ) {
				grabData();
			}
		});
		
		Button closeButton = new Button(buttonGroup, SWT.PUSH);
		closeButton.setText(" Close ");
		GridData closeGD = new GridData();
		closeGD.horizontalAlignment = GridData.FILL;
		closeButton.setLayoutData(closeGD);
		closeButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected( SelectionEvent arg0 ) {
				
			}

			@Override
			public void widgetSelected( SelectionEvent arg0 ) {
				shell.dispose();
			}
		});
		
		shell.pack();
	}
	
	private boolean createWarningBox() {
		
		final String message = "We are furnishing this item \"as is\"." +
				" We do not provide any warranty of the item whatsoever, " +
				"whether express, implied, or statutory, including, but not " +
				"limited to, any warranty of merchantability or fitness for a " +
				"particular purpose or any warranty that the contents of the " +
				"item will be error-free." +
				"\n\n" +
				"In no respect shall we incur any liability for any damages, " +
				"including, but limited to, direct, indirect, special, or " +
				"consequential damages arising out of, resulting from, or any way " +
				"connected to the use of the item, whether or not based upon warranty, " +
				"contract, tort, or otherwise; whether or not injury was sustained by " +
				"persons or property or otherwise; and whether or not loss was sustained " +
				"from, or arose out of, the results of, the item, or any services that " +
				"may be provided by us." + 
				"\n\n" +
				"To use IMAPCrypt and agree to abide by these terms, click OK. Click Cancel " +
				"to exit the program if you do not agree to abide by these terms or do not " +
				"wish to use IMAPCrypt at this time.";
		
		boolean returnValue = true;
		MessageBox mb = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK | SWT.CANCEL );
		mb.setMessage(message);
		int returnCode = mb.open();
		
		switch(returnCode) {
		case SWT.OK:
			break;
		case SWT.CANCEL:
		default:
			returnValue = false;
		}
		
		return returnValue;
	}

	/**
	 * Get the data from the Maps that hold the values from
	 * the composites.
	 */
	private void grabData() {
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.putAll(gpgComposite.getGroupValues());
		valueMap.putAll(serverComposite.getGroupValues());
		valueMap.putAll(folderComposite.getGroupValues());
		valueMap.putAll(dateComposite.getGroupValues());
		valueMap.putAll(recipComposite.getGroupValues());

		Map<String, Set<String>> filterMap = filterComposite.getFilters();
		
		if( debug ) {
			for( String key : valueMap.keySet() ) {
				System.out.println(key + " : " + valueMap.get(key));
			}
			
			for( String key : filterMap.keySet() ) {
				System.out.println("Filter Key: " + key);
				Set<String> set = filterMap.get(key);
				for( String s : set )
					System.out.println("Value: " + s);
			}
			
			System.exit(0);
		}
		
		/* get the password */
		boolean passFile = Boolean.parseBoolean(valueMap.get(KeyConstants.USEPASSFILE));
		String password;
		if( passFile )
			password = valueMap.get(KeyConstants.PASSFILE);
		else
			password = valueMap.get(KeyConstants.PASSWORD);
		
		/* create the crypt object */
		IMAPCrypt crypt = new IMAPCrypt(valueMap.get(KeyConstants.SERVER),
				valueMap.get(KeyConstants.USER), password);
		
		String message;
		int style;
		try {
			/* set the gpg location and folder */
			crypt.setGPGPath(valueMap.get(KeyConstants.GPG));
			crypt.setFolder(valueMap.get(KeyConstants.FOLDER));
			
			/* set the recipients */
			String[] recipientArray = valueMap.get(KeyConstants.RECIPIENTS).split(",");
			crypt.addRecipients(recipientArray);
			
			/* set the date parameters */
			int daysago = Integer.parseInt(valueMap.get(KeyConstants.DAYSAGO));
			int daysuntil = Integer.parseInt(valueMap.get(KeyConstants.DAYSUNTIL));
			DateBasedMessageEvaluationCallback dbmec = new DateBasedMessageEvaluationCallback(daysago, daysuntil);
			crypt.addMessageEvaluationCallback(dbmec);
			
			/* only add the sender callback if there are senders to store */
			Set<String> senderSet = filterMap.get(KeyConstants.SENDERS);
			if( senderSet.size() > 0 ) {
				SenderBasedMessageEvaluationCallback sbmec = new SenderBasedMessageEvaluationCallback();
				for( String s : senderSet )
					sbmec.addSender(s);
						
				/* check for inversion */
				String key = KeyConstants.SENDERS + KeyConstants.INVERT;
				Set<String> invert = filterMap.get(key);
				if( invert != null )
					sbmec.setInvert(true);
				else
					sbmec.setInvert(false);
						
				crypt.addMessageEvaluationCallback(sbmec);
			}
			
			/* only add the subject callback if there is a subject */
			Set<String> subjectSet = filterMap.get(KeyConstants.SUBJECTS);
			if( subjectSet.size() > 0 ) {
				SubjectBasedMessageEvaluationCallback submec = new SubjectBasedMessageEvaluationCallback();
				for( String s : subjectSet )
					submec.addSubject(s);
				
				/* check for inversion */
				String key = KeyConstants.SUBJECTS + KeyConstants.INVERT;
				Set<String> invert = filterMap.get(key);
				if( invert != null )
					submec.setInvert(true);
				else
					submec.setInvert(false);
				
				crypt.addMessageEvaluationCallback(submec);
			}
			
			crypt.setVerbose(true);
			message = String.valueOf(crypt.encrypt())+ " messages encrypted";
			style = SWT.ICON_INFORMATION | SWT.OK;
		} catch( Exception e ) {
			message = e.getMessage();
			style = SWT.ICON_ERROR | SWT.OK;
		}
		
		MessageBox mb = new MessageBox(shell, style);
		mb.setMessage(message);
		mb.open();
	}
	
	public void setDebugOn() {
		this.debug = true;
	}
	
	/**
	 * The main executable function of the program. This just executes the run
	 * function after creating a new IMAPGui object.
	 * 
	 * @param args
	 */
	public static void main( String[] args ) {
		IMAPGui imapgui = new IMAPGui();
		//imapgui.setDebugOn();
		imapgui.run();
	}

}
