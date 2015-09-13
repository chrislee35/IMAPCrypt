/**
 * 
 */
package org.dhs.chrislee;

import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * The Class DateBasedMessageEvaluationCallback.
 */
public class DateBasedMessageEvaluationCallback implements
		MessageEvaluationCallback {
	private int minage = 0;
	private int maxage = Integer.MAX_VALUE;
	private boolean invert = false;
	
	/**
	 * Instantiates a new date based message evaluation callback.
	 */
	public DateBasedMessageEvaluationCallback() {
	}
	
	/**
	 * Instantiates a new date based message evaluation callback.
	 *
	 * @param minage the minage
	 */
	public DateBasedMessageEvaluationCallback(int minage) {
		this.minage = minage;
	}

	/**
	 * Instantiates a new date based message evaluation callback.
	 *
	 * @param minage the minage
	 * @param maxage the maxage
	 */
	public DateBasedMessageEvaluationCallback(int minage, int maxage) {
		this.minage = minage;
		this.maxage = maxage;
	}
	
	/**
	 * Sets the invert.
	 *
	 * @param invert flag to invert the match
	 */
	public void setInvert(boolean invert) {
		this.invert = invert;
	}

	/**
	 * test the Message to see if it falls within the configured time range
	 * 
	 * @param m Message to evaluate
	 */
	public boolean isEncryptableMessage(Message m) throws MessagingException {
		java.util.Date today = new java.util.Date();
		// What should I do if m.getSentDate() is null?
		// (a) set it to the epoch
		// (b) set it to current time
		// (c) set it to some offset of current time
		// (d) set it to OCT 21 2015 04:29
		// (e) return false
		// (f) return true ** I'm going with this
		if(m.getSentDate() == null)
			return (! this.invert);

		int daysold = (int)((today.getTime() - m.getSentDate().getTime())/(1000*24*60*60));
		if(this.invert) {
			return ! (daysold >= this.minage && daysold <= this.maxage);
		} else {
			return (daysold >= this.minage && daysold <= this.maxage);
		}
	}

}
