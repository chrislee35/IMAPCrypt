/**
 * 
 */
package org.dhs.chrislee;

import java.util.HashSet;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * The Class SenderBasedMessageEvaluationCallback.
 *
 * @author chris
 */
public class SenderBasedMessageEvaluationCallback implements
		MessageEvaluationCallback {

	private HashSet<String> senders;
	private boolean invert = false;
	
	/**
	 * Instantiates a new sender based message evaluation callback.
	 */
	public SenderBasedMessageEvaluationCallback() {
		senders = new HashSet<String>();
	}
	
	/**
	 * Adds a sender.
	 *
	 * @param sender the sender
	 */
	public void addSender(String sender) {
		senders.add(sender);
	}
	
	/**
	 * Sets the invert.
	 *
	 * @param invert flag to invert the match or not
	 */
	public void setInvert(boolean invert) {
		this.invert = invert;
	}
	
	/**
	 * test the Message to see if the message matches one of the senders
	 * 
	 * @param m Message to evaluate
	 */
	public boolean isEncryptableMessage(Message m) throws MessagingException {
		Address addresses[] = m.getFrom();
		for(Address address:addresses) {
			for(String sender:this.senders) {
				if(sender.toString().equals(address)) {
					return !this.invert;
				}
			}
		}
		return this.invert;
	}

}
