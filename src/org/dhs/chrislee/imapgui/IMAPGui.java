package org.dhs.chrislee.imapgui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.lf5.LF5Appender;
import org.apache.log4j.lf5.LogLevel;
import org.apache.log4j.lf5.viewer.LogBrokerMonitor;
import org.dhs.chrislee.DateBasedMessageEvaluationCallback;
import org.dhs.chrislee.IMAPCrypt;
import org.dhs.chrislee.SenderBasedMessageEvaluationCallback;
import org.dhs.chrislee.SubjectBasedMessageEvaluationCallback;
import org.dhs.chrislee.imapgui.groups.DatePanel;
import org.dhs.chrislee.imapgui.groups.FilterPanel;
import org.dhs.chrislee.imapgui.groups.FolderPanel;
import org.dhs.chrislee.imapgui.groups.GPGPanel;
import org.dhs.chrislee.imapgui.groups.KeyConstants;
import org.dhs.chrislee.imapgui.groups.RecipientPanel;
import org.dhs.chrislee.imapgui.groups.ServerPanel;


/**
 * This is the main GUI class for IMAPCrypt. This GUI directly interfaces with the
 * IMAPCrypt backend. All information that can be placed into the command line
 * version of IMAPCrypt can be entered into this GUI.
 *
 */
public class IMAPGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4801829018226763720L;

	private JPanel panel;

	private GPGPanel gpgPanel;
	private ServerPanel serverPanel;
	private FolderPanel folderPanel;
	private DatePanel datePanel;
	private RecipientPanel recipPanel;
	private FilterPanel filterPanel;
	public LF5Appender app;
	public static LF5Appender appInstance;

	private boolean debug = false;

	private LogBrokerMonitor logBrokerMonitor;
	
	/**
	 * Default constructor for this object
	 */
	public IMAPGui() {
		super();

		/* create the groups, add them to the GUI,
		 * create Panels
		 */
		panel = new JPanel();
		gpgPanel = new GPGPanel();
		serverPanel = new ServerPanel();
		folderPanel = new FolderPanel();
		datePanel = new DatePanel();
		filterPanel = new FilterPanel();
		recipPanel = new RecipientPanel();
		
		serverPanel.setFolderComposite(folderPanel);
		
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		// add the Panels to the GUI
		panel.add(gpgPanel, c);
		c.gridy = 1;
		panel.add(serverPanel, c);
		c.gridy = 2;
		panel.add(folderPanel, c);
		c.gridy = 3;
		panel.add(datePanel, c);
		c.gridy = 4;
		c.gridwidth = 1;
		panel.add(recipPanel, c);
		c.gridx = 1;
		panel.add(filterPanel, c);
		
		JPanel buttonGroup = new JPanel();
		
		buttonGroup.setLayout(new BoxLayout(buttonGroup, BoxLayout.X_AXIS));
		
		JButton runButton = new JButton();
		runButton.setText("  Run  ");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Map<String, String>  valueMap = grabData();
				Map<String, Set<String>> filterMap = filterPanel.getFilters();
				
				if( debug ) {
					debugPrint(valueMap, filterMap);
				} else {
					encryptMail(valueMap, filterMap);
				}
			}
		});
		
		JButton closeButton = new JButton();
		closeButton.setText("  Close  ");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		buttonGroup.add(runButton);
		buttonGroup.add(closeButton);
		c.gridx = 0;
		c.gridy = 5;
		panel.add(buttonGroup, c);
		add(panel);
		pack();
		logBrokerMonitor = new LogBrokerMonitor(LogLevel.getLog4JLevels()) {
			@Override
			protected void closeAfterConfirm() {
				hide();
			}
		};
		app = new LF5Appender(logBrokerMonitor);
		appInstance = app;
		app.getLogBrokerMonitor().hide();
	}
	
	/**
	 * Private function to display a warning for the program before the main
	 * graphical interface appears. The user must take an action on this box in order
	 * to use the program.
	 * @return
	 */
	private boolean createWarningBox() {
		
		final String message = "We are furnishing this item \"as is\".\n" +
				" We do not provide any warranty of the item whatsoever, \n" +
				"whether express, implied, or statutory, including, but not \n" +
				"limited to, any warranty of merchantability or fitness for a \n" +
				"particular purpose or any warranty that the contents of the \n" +
				"item will be error-free." +
				"\n\n" +
				"In no respect shall we incur any liability for any damages, \n" +
				"including, but limited to, direct, indirect, special, or \n" +
				"consequential damages arising out of, resulting from, or any way \n" +
				"connected to the use of the item, whether or not based upon warranty, \n" +
				"contract, tort, or otherwise; whether or not injury was sustained by \n" +
				"persons or property or otherwise; and whether or not loss was sustained \n" +
				"from, or arose out of, the results of, the item, or any services that \n" +
				"may be provided by us." + 
				"\n\n" +
				"To use IMAPCrypt and agree to abide by these terms, click OK. Click Cancel \n" +
				"to exit the program if you do not agree to abide by these terms or do not \n" +
				"wish to use IMAPCrypt at this time.";
		Object[] options = { "OK", "Cancel" };
		JFrame warningFrame = new JFrame();
		warningFrame.setSize(500, 400);
		int returnValue = JOptionPane.showOptionDialog(
				warningFrame, message, "WARNING", 
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
				null, 
				options, options[1]
		);
		if(returnValue == JOptionPane.YES_OPTION)
			return true;
		return false;
	}

	/**
	 * Get the data from the Maps that hold the values from
	 * the Panels. If debug mode is on, the key values are dropped to
	 * the command line and the program exits.
	 */
	private Map<String, String> grabData() {
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.putAll(gpgPanel.getGroupValues());
		valueMap.putAll(serverPanel.getGroupValues());
		valueMap.putAll(folderPanel.getGroupValues());
		valueMap.putAll(datePanel.getGroupValues());
		valueMap.putAll(recipPanel.getGroupValues());

		return valueMap;
	}
	
	void debugPrint(Map<String, String> valueMap, Map<String, Set<String>> filterMap) {
		for( String key : valueMap.keySet() ) {
			System.out.println(key + " : " + valueMap.get(key));
		}
		
		for( String key : filterMap.keySet() ) {
			System.out.println("Filter Key: " + key);
			Set<String> set = filterMap.get(key);
			for( String s : set )
				System.out.println("Value: " + s);
		}
	}
	
	protected void encryptMail(Map<String, String> valueMap, Map<String, Set<String>> filterMap) {
		
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
		
		//String message;
		try {
			/* set the gpg location and folder */
			crypt.setGPGPath(valueMap.get(KeyConstants.GPG));
			crypt.setFolder(valueMap.get(KeyConstants.FOLDER));
			
			/* set the recipients */
			String[] recipientArray = valueMap.get(KeyConstants.RECIPIENTS).split(",");
			crypt.addRecipients(recipientArray);
			
			/* set the date parameters */
			int daysago = 0;
			int daysuntil = Integer.MAX_VALUE;
			try {
				daysago = Integer.parseInt(valueMap.get(KeyConstants.DAYSAGO));
				daysuntil = Integer.parseInt(valueMap.get(KeyConstants.DAYSUNTIL));
				DateBasedMessageEvaluationCallback dbmec = new DateBasedMessageEvaluationCallback(daysago, daysuntil);
				crypt.addMessageEvaluationCallback(dbmec);
			} catch (NumberFormatException e) {
				
			}
			
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
			//crypt.getLogger().addAppender(app);
			Runnable r = new Runnable() {
				public void run() {
					try {
						crypt.encrypt();
					} catch (Exception e) {
						crypt.getLogger().error("Exception thrown: "+e);
					}
				}
			};
			new Thread(r).start();
		} catch( Exception e ) {
			crypt.getLogger().error("Exception thrown: "+e);
		}
	}
	
	/**
	 * This function can be used to activate debug mode for the GUI. Debug mode will
	 * simply dump everything to the command line but not execute any IMAPCrypt actions.
	 */
	public void setDebugOn() {
		this.debug = true;
	}
	
	public static LF5Appender getAppInstance() {
		return appInstance;
	}
	
	/**
	 * The main executable function of the program. This just executes the run
	 * function after creating a new IMAPGui object.
	 * 
	 * @param args
	 */
	public static void main( String[] args ) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				IMAPGui imapgui = new IMAPGui();
				//imapgui.setDebugOn();
				if(imapgui.createWarningBox()) {
					imapgui.setVisible(true);
				} else {
					System.exit(0);
				}				
			}
		});
	}

}
