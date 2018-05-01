/**
 *
 */
package gov.va.vetservices.lib.filemanager.exception;

import java.text.MessageFormat;

import gov.va.ascent.framework.messages.MessageSeverity;

/**
 * Allow bubbling of exceptions withing FileManager back to the interface implementation,
 * with provisions for passing specific message keys, text, and severity.
 *
 * @author aburkholder
 */
public class PdfStamperException extends FileManagerException {
	private static final long serialVersionUID = -7522183183501249719L;

	public PdfStamperException(final MessageSeverity severity, final String key, final String message,
			final String... replaceableArgs) {
		this((Throwable) null, severity, key, message, replaceableArgs);
	}

	public PdfStamperException(final Throwable cause, final MessageSeverity severity, final String key, final String message,
			final String... replaceableArgs) {
		super(MessageFormat.format(message, (Object[]) replaceableArgs), cause);
		super.key = key;
		super.messageSeverity = severity;
	}

}
