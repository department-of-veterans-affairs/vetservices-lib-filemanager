package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import org.springframework.beans.factory.annotation.Value;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.FileManagerProperties;
import gov.va.vetservices.lib.filemanager.impl.validate.AbstractValidator;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;

/**
 * Determines if a byte array contains bytes, and does not contain more than {@link FileManagerProperties#KEY_FILE_MAX_BYTES}.
 *
 * @author aburkholder
 */
public class ByteArrayValidator extends AbstractValidator {

	@Value("${" + MessageKeys.FILE_BYTES_NULL_OR_EMPTY + "}")
	private String nulloremptyValue;

	@Value("${" + MessageKeys.FILE_BYTES_SIZE + "}")
	private String sizeValue;

	@Value("${" + FileManagerProperties.DEFAULT_FILE_MAX_BYTES + "}")
	private int maxBytes;

	/**
	 * <p>
	 * Pass only {@code Byte[]} to this validator.<br/>
	 * Validates that the file has bytes, and is no larger than {@link FileManagerProperties#KEY_FILE_MAX_BYTES}.
	 * </p>
	 * <p>
	 * JavaDoc from {@link AbstractValidator}:<br/>
	 * {@inheritDoc AbstractValidator#validate(java.lang.Object)}
	 * <p/>
	 */
	@Override
	public Message validate(Object toValidate) {
		Byte[] bytes = (Byte[]) toValidate;

		Message message = null;

		if ((bytes == null) || (bytes.length < 1)) {
			message = new Message(MessageSeverity.ERROR, MessageKeys.FILE_BYTES_NULL_OR_EMPTY, nulloremptyValue);

		} else if (bytes.length > maxBytes) {
			message = new Message(MessageSeverity.ERROR, MessageKeys.FILE_BYTES_SIZE, sizeValue);
		}

		return message;
	}

}
