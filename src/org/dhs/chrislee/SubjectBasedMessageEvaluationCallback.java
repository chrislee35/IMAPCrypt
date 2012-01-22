/**
 * 
 */
package org.dhs.chrislee;

import java.util.HashSet;

import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * The Class SubjectBasedMessageEvaluationCallback.
 *
 * @author chris
 */
public class SubjectBasedMessageEvaluationCallback implements
		MessageEvaluationCallback {
	
	private HashSet<String> subjects;
	private boolean invert = false;
	
	/**
	 * Instantiates a new subject based message evaluation callback.
	 */
	public SubjectBasedMessageEvaluationCallback() {
		subjects = new HashSet<String>();
	}
	
	/**
	 * Adds a subject.
	 *
	 * @param subject the subject
	 */
	public void addSubject(String subject) {
		subjects.add(subject);
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
	 * test the Message to see if it matches one of the configured subjects
	 * 
	 * @param m Message to evaluate
	 */
	public boolean isEncryptableMessage(Message m) throws MessagingException {
		String subject = m.getSubject();
		for(String sub:subjects) {
			if(subject.contains(sub)) {
				return !this.invert;
			}
		}
		return this.invert;
	}

}
