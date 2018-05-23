package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.ascent.framework.util.Defense;
import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.impl.validate.Validator;

/**
 * Validate file names for consistency with common operating system constraints.
 * This class does NOT attempt to determine if the file is supported for PDF conversion.
 * <p>
 * The returned messages list is the same as the messages returned on the ImplDto parameter.
 *
 * @author aburkholder
 */
@Component(FilenameValidator.BEAN_NAME)
public class FilenameValidator implements Validator<ImplDto> {

	public static final String BEAN_NAME = "filenameValidator";

	@Autowired
	@Qualifier("fileManagerProperties")
	FileManagerProperties fileManagerProperties;

	/**
	 * Make sure instantiation prerequisites are fulfilled.
	 */
	@PostConstruct
	public void postConstruct() {
		Defense.notNull(fileManagerProperties, "FileManagerProperties cannot be null");
	}

	/**
	 * <p>
	 * Validate file names for consistency with common operating system constraints.
	 * This method does NOT attempt to determine if the file is supported for PDF conversion.
	 * <p>
	 * If validations succeeds, {@code null} is returned, otherwise the returned list of messages is also returned on the ValidataorDto
	 * parameter.
	 * <p>
	 * JavaDoc from {@link Validator}:<br/>
	 * {@inheritDoc Validator#validate(ImplArgDto)}
	 * <p>
	 */
	@Override
	public List<Message> validate(ImplArgDto<ImplDto> toValidate) {  // NOSONAR - shut up complaint about cyclomatic complexity
		if ((toValidate == null) || (toValidate.getValue() == null)) {
			MessageKeysEnum msg = MessageKeysEnum.UNEXPECTED_ERROR;
			Message message = new Message(MessageSeverity.ERROR, msg.getKey(), msg.getMessage());
			return Arrays.asList(new Message[] { message });
		}

		ImplDto implDto = toValidate.getValue();

		if (implDto.getOriginalFileDto() == null) {

			addError(implDto, MessageKeysEnum.FILE_DTO_NULL);

		} else if (StringUtils.isBlank(implDto.getOriginalFileDto().getFilename()) || StringUtils.isBlank(implDto.getFileParts().getName())
				|| StringUtils.isBlank(implDto.getFileParts().getExtension())) {

			addError(implDto, MessageKeysEnum.FILE_NAME_NULL_OR_EMPTY);

		} else if (StringUtils.isBlank(implDto.getOriginalFileDto().getFilename())
				|| (implDto.getOriginalFileDto().getFilename().length() > fileManagerProperties.getMaxFilenameLen())) {

			addError(implDto, MessageKeysEnum.FILE_NAME_TOO_LONG);

		} else if (StringUtils.containsAny(implDto.getFileParts().getName(), FileManagerProperties.FILE_NAME_ILLEGAL_CHARS)
				|| StringUtils.containsAny(implDto.getFileParts().getExtension(), FileManagerProperties.FILE_NAME_ILLEGAL_CHARS)) {

			// NOSONAR TODO need to add character filter validations from wss web and service regex validations
			addError(implDto, MessageKeysEnum.FILE_NAME_MALFORMED);

		}

		return implDto.getMessages().isEmpty() ? null : implDto.getMessages();
	}

	/**
	 * Adds a {@link MessageSeverity#ERROR} message to the {@link ImplDto}.
	 *
	 * @param implDto the ImplDto
	 * @param messageKey the {@link MessageKeysEnum} enumeration for key and message
	 */
	private void addError(ImplDto implDto, MessageKeysEnum messageKey) {
		implDto.addMessage(new Message(MessageSeverity.ERROR, messageKey.getKey(), messageKey.getMessage()));
	}

}
