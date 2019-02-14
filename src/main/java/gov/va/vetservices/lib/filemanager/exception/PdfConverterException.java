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
// NOSONAR
public class PdfConverterException extends FileManagerException {  // NOSONAR
	private static final long serialVersionUID = -7747475590693993558L;

	/**
	 * Create an exception for problems with the PDF conversion process.
	 *
	 * @param severity the severity
	 * @param key the key
	 * @param message the MessageKeysEnum {@link java.text.MessageFormat} explanatory message
	 * @param replaceableArgs any arguments to processed into the message string
	 */
	public PdfConverterException(final MessageSeverity severity, final String key, final String message,
			final String... replaceableArgs) {
		this((Throwable) null, severity, key, message, replaceableArgs);
	}

	/**
	 * Create an exception for problems with the PDF stamping process.
	 *
	 * @param cause the root cause behind this exception
	 * @param severity the severity
	 * @param key the  key
	 * @param message the MessageKeysEnum {@link java.text.MessageFormat} explanatory message
	 * @param replaceableArgs any arguments to processed into the message string
	 */
	public PdfConverterException(final Throwable cause, final MessageSeverity severity, final String key, final String message,
			final String... replaceableArgs) {
		super(MessageFormat.format(message, (Object[]) replaceableArgs), cause);
		super.key = key;
		super.messageSeverity = severity;
		this.setParamValues((String[])replaceableArgs);
		this.setParamCount(replaceableArgs.length);
		String[] params = new String[replaceableArgs.length];
		for(int i=0; i < replaceableArgs.length; i++) {
			params[i] = String.valueOf(i);
		}
		this.setParamNames(params);
	}

}
