/**
 *
 */
package gov.va.vetservices.lib.filemanager.exception;

import java.text.MessageFormat;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;

/**
 * Allow bubbling of exceptions withing FileManager back to the interface implementation,
 * with provisions for passing specific message keys, text, and severity.
 *
 * @author aburkholder
 */
public class PdfStamperException extends FileManagerException {
	private static final long serialVersionUID = -904976977203394837L;

	/**
	 * Create an exception for problems with the PDF stamping process.
	 *
	 * @param severity the severity
	 * @param key the {@link MessageKeysEnum} key
	 * @param message the MessageKeysEnum {@link java.text.MessageFormat} explanatory message
	 * @param replaceableArgs any arguments to processed into the message string
	 */
	public PdfStamperException(final MessageSeverity severity, final String key, final String message,
			final String... replaceableArgs) {
		this((Throwable) null, severity, key, message, replaceableArgs);
	}

	/**
	 * Create an exception for problems with the PDF stamping process.
	 *
	 * @param cause the root cause behind this exception
	 * @param severity the severity
	 * @param key the {@link MessageKeysEnum} key
	 * @param message the MessageKeysEnum {@link java.text.MessageFormat} explanatory message
	 * @param replaceableArgs any arguments to processed into the message string
	 */
	public PdfStamperException(final Throwable cause, final MessageSeverity severity, final String key, final String message,
			final String... replaceableArgs) {
		super(MessageFormat.format(message, (Object[]) replaceableArgs), cause);
		super.key = key;
		super.messageSeverity = severity;
	}

}
