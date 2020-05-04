package org.dhs.chrislee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.Flags;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import de.buelowssiege.mail.pgp_mime.BodyPartEncrypter;
import de.buelowssiege.mail.pgp_mime.gpg.GnuPGBodyPartEncrypter;
import de.buelowssiege.mail.pgp_mime.MimeMultipartEncrypted;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * The Class IMAPCrypt.
 *
 * @author chrislee and jschwier
 */
public class IMAPCrypt {
	//private static int VERBOSE_SUBJECT_LENGTH = 50;
	private String gpgpath; // the full path to the GPG binary executable
	private String server;  // the hostname or IP of the IMAP server
	private String username; // the username for the IMAP server
	private String password; // the password for the IMAP server
	private String folder;  // the folder to evaluate for encryption
	private HashSet<String> recipients; // the list of keys/email addresses to encrypt the messages to
	// a list of message evaluation callbacks to check each message to see if it should be encrypted
	private ArrayList<MessageEvaluationCallback> messageEvaluationCallbacks; 
	private boolean verbose; // print debug/connection messages?
	private boolean insecureConnection = false;
	private static SSLSocketFactory sslSocketFactory = null;
	final public static String VERSION = "2.0.3";
	final static Logger logger = Logger.getLogger(IMAPCrypt.class);
	
	/**
	 * Instantiates the IMAPCrypt object.
	 *
	 * @param server the server
	 * @param username the username
	 * @param password the password
	 */
	public IMAPCrypt(String server, String username, String password) {
		this.gpgpath = findGPG(); // try to find the GPG binary first
		this.server = server; // set the server
		this.password = password; // set the password
		this.username = username; // set the username
		this.folder = null; // initialize the folder to be null
		this.recipients = new HashSet<String>(); // create an empty list for the recipients
		this.messageEvaluationCallbacks = new ArrayList<MessageEvaluationCallback>(); // create an empty list for the callbacks
		this.verbose = false; // set the verbose flag to false to hide connection details
	}

	/**
	 * detect the OS, and based on the OS, try to find a copy of gpg
	 * 
	 * @return String with the full path to the GPG binary.  null if not found.
	 */
	public String findGPG() {
		String os = System.getProperty("os.name").toLowerCase();
		String locations[];
		if(os.indexOf("win") >= 0) {
			locations = new String[] { "c:/Program Files/GNU/GnuPG/gpg2.exe" };
		} else {
			locations = new String[] { "/usr/bin/gpg", "/usr/local/bin/gpg", "/opt/local/bin/gpg" };
		}
		// iterate through the lists and, on the first hit, return the location of gpg
		for(int i=0; i<locations.length; i++) {
			File f = new File(locations[i]);
			if(f.exists()) {
				return locations[i];
			}
		}
		return null;
	}
	
	/**
	 * sets the gpgpath.
	 *
	 * @param gpgpath the new GPG path
	 */
	public void setGPGPath(String gpgpath) {
		this.gpgpath = gpgpath; 
	}
	
	/**
	 * return the gpgpath
	 * 
	 * @return String of the full path to gpg
	 */
	public String getGPGPath() {
		return this.gpgpath; 
	}
	
	/**
	 * return the server
	 * 
	 * @return String with the IMAP server's hostname or IP address
	 */
	public String getServer() {
		return this.server; 
	}
	
	/**
	 * Gets the username.
	 *
	 * @return String with the username used to connect to the IMAP server
	 */
	public String getUsername() {
		return this.username; 
	}
	
	
	/**
	 * sets the folder to be encrypted
	 * 
	 * @param folder name of the IMAP folder (dotted notation) to process for encryption
	 */
	public void setFolder(String folder) {
		this.folder = folder;
	}
	
	/**
	 * returns the list of recipients
	 * 
	 * @return HashSet<String>
	 */
	public HashSet<String> getRecipients() {
		return this.recipients; 
	}

	/**
	 * adds a recipient for encrypting messages
	 * 
	 * @param recipient an email or KeyID to encrypt to
	 */
	public void addRecipient(String recipient) {
		this.recipients.add(recipient);
	}
	
	/**
	 * adds multiple recipients for encrypting messages.
	 *
	 * @param recipients the list of recipients / PGP KeyIDs
	 */
	public void addRecipients(String[] recipients) {
		for(String recipient:recipients) {
			this.recipients.add(recipient);
		}
	}
	
	/**
	 * adds multiple recipients for encrypting messages.
	 *
	 * @param recipients the list of recipients / GPG KeyIDs
	 */
	public void addRecipients(Collection<String> recipients) {
		this.recipients.addAll(recipients);
	}
	
	/**
	 * adds a callback for evaluating each message to see if it matches given criteria
	 * 
	 * @param mec an instantiation of a subclass of MessageEvaluationCallback
	 */
	public void addMessageEvaluationCallback(MessageEvaluationCallback mec) {
		this.messageEvaluationCallbacks.add(mec);
	}
	
	/**
	 * returns the list of callbacks
	 * 
	 * @return TreeSet<MessageEvaluationCallback>
	 */
	public ArrayList<MessageEvaluationCallback> getMessageEvaluationCallbacks() {
		return this.messageEvaluationCallbacks;
	}
	
	/**
	 * get the verbose setting
	 * 
	 * @return boolean
	 */
	public boolean getVerbose() {
		return this.verbose;
	}

	/**
	 * set the verbose setting
	 * 
	 * @param verbose flag to turn on verbose messages
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	
	public boolean isInsecureConnection() {
		return insecureConnection;
	}

	public void setInsecureConnection(boolean insecureConnection) {
		this.insecureConnection = insecureConnection;
	}

	public static void setSSLSocketFactory(SSLSocketFactory factory) {
		sslSocketFactory = factory;
	}
	/**
	 * Recursive function to walk down the IMAP folders and build a list (with order) of the IMAP folders
	 * 
	 * @param parent
	 * @return TreeSet<String>
	 * @throws MessagingException
	 */
	private TreeSet<String> getChildFolders(Folder parent) throws MessagingException {
		TreeSet<String> folders = new TreeSet<String>();
		folders.add(parent.getFullName());
		for(Folder folder:parent.list()) {
			folders.addAll(getChildFolders(folder));
		}
		return folders;
	}
	
	/**
	 * Enumerate and return the IMAP folders for the configured account
	 * 
	 * @return TreeSet<String>
	 * @throws MessagingException 
	 */
	public TreeSet<String> getFolders() throws MessagingException {
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		if(sslSocketFactory != null)
			props.put("mail.imaps.socketFactory", sslSocketFactory);
		try {
			logger.debug("Creating IMAP session");
			// Create an IMAP session
		    Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");
			// Connect to the IMAP server using a username and password
			logger.info("Connecting to IMAP server: "+username+"@"+server);
			store.connect(this.server, this.username, this.password);
			TreeSet<String> folders = new TreeSet<String>();
			Folder root = store.getDefaultFolder();
			folders.add(root.getFullName());
			for(Folder folder:root.list()) {
				logger.debug("Retrieved folder name "+folder);
				folders.addAll(getChildFolders(folder));
			}
			return folders;
		} catch (NoSuchProviderException e) {
			logger.error("NoSuchProviderException raised when retrieving folders\n"+e);
			e.printStackTrace();
		}
		return null;
	}
		
	/**
	 * encrypts any unencrypted messages matching any callbacks of the configured IMAP folder to the given set of recipients.
	 *
	 * @return int the number of messages that were successfully encrypted
	 * @throws Exception the exception
	 */
	public int encrypt() throws Exception {
		
		if(this.folder == null)
			throw new Exception("folder must be set");
		
		if(this.recipients.size() == 0)
			throw new Exception("must have at least one recipient set");
		
		if(this.gpgpath == null)
			throw new Exception("must specify the gpg binary location");
		
		// create a properties store to pass to the IMAP session
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		if(sslSocketFactory != null)
			props.put("mail.imaps.socketFactory", sslSocketFactory);
		
		int encryptedCount = 0;
		logger.debug("Setting up encryptor for the following recipients: "+recipients);
		// use the jpgpmime encrypter class to set up an encrypter to the list of recipients
		BodyPartEncrypter encrypter = new GnuPGBodyPartEncrypter(this.gpgpath, this.recipients.toArray(new String[0]));

		logger.info("Connecting to "+this.username+"@"+this.server);
		// Create an IMAP session
	    Session session = Session.getDefaultInstance(props, null);
		Store store = session.getStore("imaps");
		// Connect to the IMAP server using a username and password
		try {
			store.connect(this.server, this.username, this.password);
		} catch(MessagingException me) {
			if(me.getMessage().contains("PKIX path building failed")) {
        		System.out.println("Unable to validate the certificate path.");
        		X509Certificate cert = SSLCertificateHelper.getCertificate(this.server, 993);
        		if(cert == null) {
        			System.out.println("Cannot fetch certificate from server");
        		} else {
        			System.out.println(SSLCertificateHelper.certificateSummary(cert));
    				SSLCertificateHelper.addCertToKeyStore(cert);
    				SSLSocketFactory sslsf = SSLCertificateHelper.getSSLSocketFactory();
    				IMAPCrypt.setSSLSocketFactory(sslsf);
    				store.connect(this.server, this.username, this.password);
    				getFolders();
        		}
        	}
		}

		logger.info("Selecting folder "+this.folder);
		// Select the target folder
		Folder inbox = store.getFolder(this.folder);
		// Open the target folder for reading and writing
		inbox.open(Folder.READ_WRITE);
		logger.info("Fetching messages");
		// Get all the messages
		Message messages[] = inbox.getMessages();
		logger.info("Fetched "+messages.length+" messages");
		// Iterate through each message
		for(Message message:messages) {
			// Grab the content type
			String contentType = message.getContentType();
			logger.info("Message #"+message.getMessageNumber()+" on "+message.getSentDate()+": "+message.getSubject());
			// Check if the email is already encrypted
			if(contentType != null && (contentType.contains("application/pgp-encrypted") || contentType.contains("application/pkcs7-mime"))) {
				logger.info("  Skipping already encrypted message");
				continue;
			}
			boolean encryptable = true;
			// Call the message evaluation callback (if set), and if it says false, then continue to the next message
			if(this.messageEvaluationCallbacks.size() > 0) {
				for(MessageEvaluationCallback mec:this.messageEvaluationCallbacks) {
					if(!mec.isEncryptableMessage(message)) {
						encryptable = false;
						logger.info("  Did not match all the filters");
						break;
					}
				}
			}
			if(!encryptable)
				continue;
			
			InputStream is = message.getInputStream();
			System.out.println("** Printing message");
			byte[] b = new byte[1024];
			while(is.read(b) > 0)
				System.out.print(b);
			is.close();
			System.out.println("");
			System.out.println("** End of Message");
			
			// Create a MimeBodyPart from the original email (attachments and all)
			MimeBodyPart mbp = new MimeBodyPart();
			logger.debug(contentType);
			mbp.setContent(message.getContent(), contentType);
			
			try {
				logger.debug("Encrypting the MultiPartMime message");
			    // encrypt the message
				MimeMultipartEncrypted mme = MimeMultipartEncrypted.createInstance(mbp, encrypter);
				// Create a brand new MimeMessage with the contents of the original message
				// IMAP doesn't allow you to update an existing email, so you have to create a new one and delete the old one
				MimeMessage mimeMessage = new MimeMessage((MimeMessage)message);
				// Update the MimeMessage with the encrypted email
			    mimeMessage.setContent(mme);
			    // Save the changes
			    mimeMessage.saveChanges();
			    // create an array (of one) of messages to insert
			    Message newMessages[] = { mimeMessage };
			    logger.info("Appending the new, encrypted version of the message into the folder, "+folder);
			    // insert the message into the folder
			    inbox.appendMessages(newMessages);
			    logger.info("Setting the delete flag on the old, unencrypted version of the message");
			    // set the DELETE flag on the original email
			    message.setFlag(Flags.Flag.DELETED, true);
			    // delete all emails with the DELETE flag set (including ones that this tool hasn't touched)
			    //inbox.expunge();
			    encryptedCount += 1;
			} catch (javax.mail.IllegalWriteException e) {
				logger.error("The message failed to encrypt because of a IllegalWriteException: "+e);
			}
		}
		logger.info("Finished encrypting folder, closing down (and expunging deleted emails)");
		inbox.close(true);
		store.close();
		logger.info("Encrypted a total of "+encryptedCount+" messages");
		return encryptedCount;
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public static Logger getLoggerInstance() {
		return logger;
	}
	
	/**
	 * prints out the usage of the tool
	 */
	public static void usage() {
		System.out.println("Warning - Use at your own risk");
		System.out.println("We are furnishing this item \"as is\". We do not provide any warranty of the item whatsoever,");
		System.out.println("whether express, implied, or statutory, including, but not limited to, any warranty of");
		System.out.println("merchantability or fitness for a particular purpose or any warranty that the contents of"); 
		System.out.println("the item will be error-free.");
		System.out.println("");
		
		System.out.println("In no respect shall we incur any liability for any damages, including, but limited to, direct,"); 
		System.out.println("indirect, special, or consequential damages arising out of, resulting from, or any way connected"); 
		System.out.println("to the use of the item, whether or not based upon warranty, contract, tort, or otherwise; whether"); 
		System.out.println("or not injury was sustained by persons or property or otherwise; and whether or not loss was"); 
		System.out.println("sustained from, or arose out of, the results of, the item, or any services that may be provided"); 
		System.out.println("by us.");
		System.out.println("");
		
		System.out.println("In short, this tool has a high likelihood of destroying your email.  You should back up your email,");
		System.out.println("perform limited tests, and then verify that everything worked.");
		System.out.println("");
		
		System.out.println("IMAP PGP Encryption Program");
		System.out.println("Usage:");
		System.out.println("-h                 a wonderfully helpful help message that hopefully helps.");
		System.out.println("-g <gpg path>      full path to gpg, e.g., /usr/local/bin/gpg");
		System.out.println("-s <server>        hostname or IP of the IMAP server");
		System.out.println("-u <username>      username to login to IMAP server");
		System.out.println("-p <password>      password to login to IMAP server");
		System.out.println("-P <passwordfile>  filename containing the password to login to the IMAP server");
		System.out.println("-k                 ignore server certificate validation errors (insecure)");
		System.out.println("-f <folder>        folder to encrypt, e.g. Inbox.hatemail.2009");
		System.out.println("-r <recipient>     who to encrypt to");
		System.out.println("-d <daysago>       encrypt only emails that are older than X days old, 0=everything, 1=yesterday and beyond, default is 0");
		System.out.println("-D <daysutil>      encrypt only emails that are younger than X days old, 0=nothing, 1=today's only, default is MAXINT");
		System.out.println("-F <from>          encrypt only emails that are from a given list of senders");
		System.out.println("-S <subject>       encrypt only emails that contain <subject> in the subject field (case sensitive)");
		System.exit(0);
	}

	/**
	 * the main parses the arguments, creates a IMAPCrypt object, and runs it
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]) {
		// Set up the arguments parser and then parse the args[] structure
		OptionParser parser = new OptionParser("hkvg:s:u:p:P:f:r:d:D:F:S:");
		OptionSet options = parser.parse(args);
		
		try {
			// if -h, then print the usage
			if(options.has("h"))
				usage();
			
			// if -g, then take that value for the location of gpg, but otherwise, try to find it
			String gpgloc;
			if(options.has("g")) {
				List<?> vals = options.valuesOf("g");
				gpgloc = (String)vals.get(0);
			} else {
				gpgloc = new IMAPCrypt(null,null,null).getGPGPath();
			}
			// if we were until to find it, prompt for it
			if(gpgloc == null) {
				System.out.println("Please specify the full path to your GPG binary:");
				BufferedReader bir = new BufferedReader(new InputStreamReader(System.in));
				gpgloc = bir.readLine().trim();
				if(gpgloc == null || gpgloc.equals("")) {
					System.out.println("The gpg location is null, please specify a location using the -g option.");
					System.exit(1);
				}
			}
			// last, test if the resulting value, either specified by -g or via the prompt, really exists
			File gpgfile = new File(gpgloc);
			if(! gpgfile.exists()) {
				System.out.println("The gpg binary specified, "+gpgloc+" was not found.");
				System.exit(1);
			}
			
			// get a list of recipients (people to encrypt to, usually just yourself).
			// I wanted to try to guess this based on secret keys and whatnot, but it got really complex with
			// all the different ways that people could manage their keys, so, the tool has to have it specified
			String recipients[] = null;
			if(options.has("r")) {
				recipients = ((String)options.valueOf("r")).split(",");
			}
			if(recipients == null || recipients.length == 0) {
				System.out.println("Please specify the email addresses or KeyIDs for encryption, separated by commas:");
				BufferedReader bir = new BufferedReader(new InputStreamReader(System.in));
				recipients = bir.readLine().trim().split("[,\\s]+");
				if(recipients == null || recipients.length == 0) {
					System.out.println("No recipients were specified, aborting.");
					System.exit(1);
				}
			}
	
			// if -s, take in the server address, otherwise, prompt for it
			String server;
			if(options.has("s")) {
				server = options.valuesOf("s").get(0).toString();
			} else {
				System.out.println("Please specify the IMAP server hostname or IP address:");
				BufferedReader bir = new BufferedReader(new InputStreamReader(System.in));
				server = bir.readLine().trim();
				if(server == null || server.equals("")) {
					System.out.println("No server was specified, aborting.");
					System.exit(1);
				}
			}
			
			// if -u, take in the username, otherwise, prompt for it
			String username;
			if(options.has("u")) {
				username = options.valuesOf("u").get(0).toString();
			} else {
				System.out.println("Please specify the username for the IMAP server:");
				BufferedReader bir = new BufferedReader(new InputStreamReader(System.in));
				username = bir.readLine().trim();
				if(username == null || username.equals("")) {
					System.out.println("No username was specified, aborting.");
					System.exit(1);
				}
			}
			
			// if -p, take in the password, otherwise, prompt for it
			// this is a horrid idea to pass on the command line where everyone can see
			// perhaps, I'll allow a -P to specify a file to read in the password from
			String password;
			if(options.has("p")) {
				password = options.valuesOf("p").get(0).toString();
			} else if(options.has("P")) {
				// read in the first line of the provided file
				password = new BufferedReader(new FileReader(new File(options.valuesOf("p").get(0).toString()))).readLine().trim();
			} else {
				System.out.println("Please specify the password for the IMAP server user, "+username+":");
				BufferedReader bir = new BufferedReader(new InputStreamReader(System.in));
				password = bir.readLine().trim();
				if(password == null || password.equals("")) {
					System.out.println("No password was specified, aborting.");
					System.exit(1);
				}
			}
			
			// if -f, set the folder to be encrypted, otherwise, prompt for it
			String folder;
			if(options.has("f")) {
				folder = options.valuesOf("f").get(0).toString();
			} else {
				System.out.println("Please specify the IMAP folder that you wish to encrypt:");
				BufferedReader bir = new BufferedReader(new InputStreamReader(System.in));
				folder = bir.readLine().trim();
				if(folder == null || folder.equals("")) {
					System.out.println("No folder was specified, aborting.");
					System.exit(1);
				}
			}
			
			// if -d, set the number of days old an email must be to be considered for encryption
			int daysago = 0;
			if(options.has("d")) {
				daysago = Integer.parseInt(options.valuesOf("d").get(0).toString());
			} else {
				System.out.println("Please specify the number of days old an email has to be before it's encrypted:");
				BufferedReader bir = new BufferedReader(new InputStreamReader(System.in));
				try {
					daysago = Integer.parseInt(bir.readLine().trim());
				} catch (NumberFormatException e) {
					
				}
			}

			// if -D, set the number of days old an email must be to be considered for encryption
			int daysuntil = Integer.MAX_VALUE;
			if(options.has("D")) {
				daysuntil = Integer.parseInt(options.valuesOf("D").get(0).toString());
			} else {
				System.out.println("Please specify the number of days old an email has to be before it's not considered for encryption:");
				BufferedReader bir = new BufferedReader(new InputStreamReader(System.in));
				try {
					daysuntil = Integer.parseInt(bir.readLine().trim());
				} catch (NumberFormatException e) {
					
				}
			}
			
			IMAPCrypt ic = new IMAPCrypt(server, username, password);
			ic.setGPGPath(gpgloc);
			ic.setFolder(folder);
			ic.addRecipients(recipients);
			if(options.has("F")) {
				String[] senders = options.valuesOf("F").toArray(new String[0]);
				SenderBasedMessageEvaluationCallback sbmec = new SenderBasedMessageEvaluationCallback();
				for(String sender:senders) {
					sbmec.addSender(sender);
				}
				ic.addMessageEvaluationCallback(sbmec);
			}
			if(options.has("S")) {
				String[] subjects = options.valuesOf("S").toArray(new String[0]);
				SubjectBasedMessageEvaluationCallback sbmec = new SubjectBasedMessageEvaluationCallback();
				for(String subject:subjects) {
					sbmec.addSubject(subject);
				}
				ic.addMessageEvaluationCallback(sbmec);
			}
			DateBasedMessageEvaluationCallback dbmec = new DateBasedMessageEvaluationCallback(daysago, daysuntil);
			ic.addMessageEvaluationCallback(dbmec);
			if(options.has("v"))
				ic.setVerbose(true);
			if(options.has("k"))
				ic.setInsecureConnection(true);
			ic.encrypt();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);			
		}
	}
	// TODO: build a keystore and prompt to accept any non-trusted certificate.
	// TODO: fix the 0day email issue
}