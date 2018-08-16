package gov.va.vetservices.lib.filemanager.util;

import java.text.MessageFormat;
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
	public static final String BEAN_NAME = "libfilemanagerMessageUtils";

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
	public void add(final MessageSeverity severity, final String key, final String description, Object... args) {
		if (args != null && args.length > 0) {
			MessageFormat.format(description, args);
		}
		this.messages.add(new Message(severity, key, description));
	}

	/**
	 * Returns message based on key and severity passed.
	 * <p>
	 * The created message is NOT added to the messages list used by {@link #add(MessageSeverity, String, String)},
	 * {@link #getMessages()}, or {@link #size()}.
	 *
	 * @param severity
	 * @param key
	 * @return
	 */
	public Message createMessage(final MessageSeverity severity, final String key, Object... args) {

		final String userMessage = messageSource.getMessage(key, args, Locale.getDefault());
		return new Message(severity, key, userMessage);
	}

	/**
	 * Returns message based on key from property file.
	 * <p>
	 * The returned message is NOT referenced from the internal messages list used by {@link #add(MessageSeverity, String, String)},
	 * {@link #getMessages()}, or {@link #size()}.
	 *
	 * @param key
	 * @return string
	 */
	public String returnMessage(final String key, Object... args) {

		if (args == null || args.length == 0) {
			return messageSource.getMessage(key, null, Locale.getDefault());
		}

		return MessageFormat.format(messageSource.getMessage(key, null, Locale.getDefault()), args);

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
