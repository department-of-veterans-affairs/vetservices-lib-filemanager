package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.impl.validate.Validator;
import gov.va.vetservices.lib.filemanager.impl.validate.ValidatorArg;

/**
 * Determines if the file byte array contains bytes, and does not contain more than {@link FileManagerProperties#KEY_FILE_MAX_BYTES}.
 * This class does NOT attempt to determine if the file is supported for PDF conversion.
 *
 * @author aburkholder
 */
public class ByteArrayValidator implements Validator<byte[]> {

	private int maxBytes = Integer.parseInt(FileManagerProperties.DEFAULT_FILE_MAX_BYTES);

	/**
	 * <p>
	 * Validates that the file has bytes, and is no larger than {@link FileManagerProperties#KEY_FILE_MAX_BYTES}.
	 * This method does NOT attempt to determine if the file is supported for PDF conversion.
	 * <p>
	 * If validation succeeds, {@code null} is returned, otherwise a list of messages is returned.
	 * <p>
	 * JavaDoc from {@link Validator}:<br/>
	 * {@inheritDoc AbstractValidator#validate(java.lang.Object)}
	 * <p>
	 */
	@Override
	public List<Message> validate(ValidatorArg<byte[]> toValidate) {
		byte[] bytes = toValidate.getValue();

		Message message = null;

		if ((bytes == null) || (bytes.length < 1)) {
			message = new Message(MessageSeverity.ERROR, MessageKeys.FILE_BYTES_NULL_OR_EMPTY.getKey(),
					MessageKeys.FILE_BYTES_NULL_OR_EMPTY.getMessage());

		} else if (bytes.length > maxBytes) {
			message = new Message(MessageSeverity.ERROR, MessageKeys.FILE_BYTES_SIZE.getKey(),
					MessageFormat.format(MessageKeys.FILE_BYTES_SIZE.getMessage(), maxBytes));
		}

		return message == null ? null : Arrays.asList(new Message[] { message });
	}

}
