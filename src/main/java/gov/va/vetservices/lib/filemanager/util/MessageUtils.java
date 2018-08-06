package gov.va.vetservices.lib.filemanager.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;

/**
 * Manage a list of messages to be returned to the consumer.
 */
@Component(MessageUtils.BEAN_NAME)
@Scope("prototype")
public class MessageUtils {

	/** The Constant BEAN_NAME. */
	public static final String BEAN_NAME = "messageUtils";

	/** Auto wire the message source for error messages. */
	@Autowired
	private MessageSource messageSource;

	/** Internal list of messages */
	private final List<Message> messages = new ArrayList<>();

	/**
	 * Get the full list of {@link Message} objects
	 *
	 * @return list of {@link Message} objects, or empty list
	 */
	public List<Message> getMessages() {
		return this.messages;
	}

	/**
	 * Invokes an add to the list of {@link Message} objects
	 *
	 * @see List#add(Object)
	 * @param severity
	 *            the {@link MessageSeverity}
	 * @param key
	 *            the message key as a String
	 * @param description
	 *            the message description as a String
	 */
	public void add(final MessageSeverity severity, final String key, final String description) {
		this.messages.add(new Message(severity, key, description));
	}

	/**
	 * Returns message based on key and severity passed
	 *
	 * @param severity
	 * @param key
	 * @return
	 */
	public Message createMessage(final MessageSeverity severity, final String key) {

		final String userMessage = messageSource.getMessage(key, null, Locale.getDefault());
		final Message msg = new Message(severity, key, userMessage);

		return msg;
	}

	/**
	 * Returns message based on key from property file
	 *
	 * @param key
	 * @return string
	 */
	public String returnMessage(final String key) {

		return messageSource.getMessage(key, null, Locale.getDefault());

	}

	/**
	 * Returns message based on key from property file
	 *
	 * @param key
	 * @return string
	 */
	public String returnMsg(final String key) {

		return messageSource.getMessage(key, null, Locale.getDefault());

	}

	/**
	 * Gets the size of the {@link Message} objects list
	 *
	 * @see List#size()
	 * @return int - the size of the list
	 */
	public int size() {
		return this.messages.size();
	}

}
