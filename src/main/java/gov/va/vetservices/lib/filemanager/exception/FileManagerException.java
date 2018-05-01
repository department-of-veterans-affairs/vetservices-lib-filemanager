/**
 *
 */
package gov.va.vetservices.lib.filemanager.exception;

import java.text.MessageFormat;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;

/**
 * Allow bubbling of exceptions withing FileManager back to the interface implementation,
 * with provisions for passing specific message keys, text, and severity.
 *
 * @author aburkholder
 */
public class FileManagerException extends Exception {
	private static final long serialVersionUID = -7522183183501249719L;

	protected String key = null; // NOSONAR
	protected MessageSeverity messageSeverity = null; // NOSONAR

	/**
	 * Create an exception that can be thrown to callers outside of this library.
	 *
	 * @param severity the {@link MessageSeverity}
	 * @param key the MessageKeys key
	 * @param message the MessageKeys message
	 * @param replaceableArgs any replaceable atguments for use in the message (or nothing)
	 */
	public FileManagerException(final MessageSeverity severity, final String key, final String message,
			final String... replaceableArgs) {
		this((Throwable) null, severity, key, message, replaceableArgs);
	}

	/**
	 * Create an exception that can be thrown to callers outside of this library.
	 *
	 * @param cause the root cause of this exception
	 * @param severity the {@link MessageSeverity}
	 * @param key the MessageKeys key
	 * @param message the MessageKeys message
	 * @param replaceableArgs any replaceable arguments for use in the message (or nothing)
	 */
	public FileManagerException(final Throwable cause, final MessageSeverity severity, final String key, final String message,
			final String... replaceableArgs) {
		this(MessageFormat.format(message, (Object[]) replaceableArgs), cause);
		this.key = key;
		this.messageSeverity = severity;
	}

	/**
	 * This constructor provides a means for subclasses to pass message and cause to the {@link Exception} superclass
	 *
	 * @param message the MessageKeys message
	 * @param cause the root cause of this exception
	 */
	protected FileManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * The Ascent Framework {@link Message} text.
	 *
	 * @return the message (text)
	 */
	@Override
	public String getMessage() { // NOSONAR - explicit override for the javadoc
		return super.getMessage();
	}

	/**
	 * The Ascent Framework {@link Message} key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * The Ascent Framework {@link Message} {@link MessageSeverity}.
	 *
	 * @return the messageSeverity
	 */
	public MessageSeverity getMessageSeverity() {
		return messageSeverity;
	}

}
