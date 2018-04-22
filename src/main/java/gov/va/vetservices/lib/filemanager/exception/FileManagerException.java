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

	private final String key; // NOSONAR
	private final MessageSeverity messageSeverity; // NOSONAR

	public FileManagerException(final MessageSeverity severity, final String key, final String message,
			final String... replaceableArgs) {
		this((Throwable) null, severity, key, message, replaceableArgs);
	}

	public FileManagerException(final Throwable cause, final MessageSeverity severity, final String key, final String message,
			final String... replaceableArgs) {
		super(MessageFormat.format(message, replaceableArgs), cause);
		this.key = key;
		this.messageSeverity = severity;
	}

	/**
	 * The Ascent Framework {@link Message} text.
	 *
	 * @return the message (text)
	 */
	@Override
	public String getMessage() {
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
