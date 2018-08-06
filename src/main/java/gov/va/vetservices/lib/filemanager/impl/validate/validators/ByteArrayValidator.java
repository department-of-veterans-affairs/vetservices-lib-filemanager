package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.ascent.framework.util.Defense;
import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.validate.Validator;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;

/**
 * Determines if the file byte array contains bytes, and does not contain more than {@link FileManagerProperties#KEY_FILE_MAX_BYTES}.
 * This class does NOT attempt to determine if the file is supported for PDF conversion.
 *
 * @author aburkholder
 */
@Component(ByteArrayValidator.BEAN_NAME)
public class ByteArrayValidator implements Validator<byte[]> {

	public static final String BEAN_NAME = "byteArrayValidator";

	@Autowired
	@Qualifier(FileManagerProperties.BEAN_NAME)
	private FileManagerProperties fileManagerProperties;
	
	/** Auto wire message utilities */
	@Autowired
	@Qualifier("messageUtils")
	private gov.va.vetservices.lib.filemanager.util.MessageUtils messageUtils;

	@PostConstruct
	public void postConstruct() {
		Defense.notNull(fileManagerProperties);
	}

	/**
	 * <p>
	 * Validates that the file has bytes, and is no larger than {@link FileManagerProperties#KEY_FILE_MAX_BYTES}.
	 * This method does NOT attempt to determine if the file is supported for PDF conversion.
	 * <p>
	 * If validation succeeds, {@code null} is returned, otherwise a list of messages is returned.
	 * <p>
	 * JavaDoc from {@link Validator}:<br/>
	 * {@inheritDoc Validator#validate(ImplArgDto)}
	 * <p>
	 */
	@Override
	public List<Message> validate(ImplArgDto<byte[]> toValidate) {
		Message message = null;

		if (toValidate == null) {
			return Arrays.asList(new Message[] { 
					messageUtils.createMessage(MessageSeverity.ERROR, LibFileManagerMessageKeys.UNEXPECTED_ERROR) });
		}

		byte[] bytes = toValidate.getValue();

		if ((bytes == null) || (bytes.length < 1)) {
			message = messageUtils.createMessage(MessageSeverity.ERROR, LibFileManagerMessageKeys.FILE_BYTES_NULL_OR_EMPTY);

		} else if (bytes.length > maxFileBytes()) {
			message = messageUtils.createMessage(MessageSeverity.ERROR, LibFileManagerMessageKeys.FILE_BYTES_SIZE);
		}

		return message == null ? null : Arrays.asList(new Message[] { message });
	}

	/**
	 * Get the maximum number of bytes allowed for file content.
	 *
	 * @return int the number of bytes
	 */
	protected int maxFileBytes() {
		return fileManagerProperties.getMaxFileBytes();
	}
}
