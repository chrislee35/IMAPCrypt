/**
 * 
 */
package org.dhs.chrislee;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * The Interface MessageEvaluationCallback.
 *
 * @author chris
 */
public interface MessageEvaluationCallback {
	boolean isEncryptableMessage(Message m)  throws MessagingException;
}
