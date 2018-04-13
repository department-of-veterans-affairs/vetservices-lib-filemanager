package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.impl.validate.Validator;
import gov.va.vetservices.lib.filemanager.impl.validate.ValidatorArg;

/**
 * Determines if the file byte array contains bytes, and does not contain more than {@link FileManagerProperties#KEY_FILE_MAX_BYTES}.
 *
 * @author aburkholder
 */
public class ByteArrayValidator implements Validator<byte[]> {

	@Value("${" + MessageKeys.FILE_BYTES_NULL_OR_EMPTY + "}")
	private String nulloremptyValue;

	@Value("${" + MessageKeys.FILE_BYTES_SIZE + "}")
	private String sizeValue;

	@Value("${" + FileManagerProperties.DEFAULT_FILE_MAX_BYTES + "}")
	private int maxBytes;

	/**
	 * <p>
	 * Validates that the file has bytes, and is no larger than {@link FileManagerProperties#KEY_FILE_MAX_BYTES}.
	 * </p>
	 * <p>
	 * If validation succeeds, {@code null} is returned, otherwise a list of messages is returned.
	 * </p>
	 * <p>
	 * JavaDoc from {@link Validator}:<br/>
	 * {@inheritDoc AbstractValidator#validate(java.lang.Object)}
	 * <p/>
	 */
	@Override
	public List<Message> validate(ValidatorArg<byte[]> toValidate) {
		byte[] bytes = toValidate.getValue();

		Message message = null;

		if ((bytes == null) || (bytes.length < 1)) {
			message = new Message(MessageSeverity.ERROR, MessageKeys.FILE_BYTES_NULL_OR_EMPTY, nulloremptyValue);

		} else if (bytes.length > maxBytes) {
			message = new Message(MessageSeverity.ERROR, MessageKeys.FILE_BYTES_SIZE, sizeValue);
		}

		return message == null ? null : Arrays.asList(new Message[] { message });
	}

}
