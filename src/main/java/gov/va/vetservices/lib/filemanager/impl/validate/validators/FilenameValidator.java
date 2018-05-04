/**
 *
 */
package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
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
public class FilenameValidator implements Validator<ImplDto> {

	/**
	 * <p>
	 * Validate file names for consistency with common operating system constraints.
	 * This method does NOT attempt to determine if the file is supported for PDF conversion.
	 * <p>
	 * If validations succeeds, {@code null} is returned, otherwise the returned list of messages is also returned on the ValidataorDto
	 * parameter.
	 * <p>
	 * JavaDoc from {@link Validator}:<br/>
	 * {@inheritDoc AbstractValidator#validate(java.lang.Object)}
	 * <p>
	 */
	@Override
	public List<Message> validate(ImplArgDto<ImplDto> toValidate) {  // NOSONAR - shut up complaint about cyclomatic complexity
		if (toValidate == null) {
			throw new IllegalArgumentException("Impl Dto cannot be null.");
		}

		ImplDto implDto = toValidate.getValue();

		if (implDto == null) {

			throw new IllegalArgumentException("Impl Dto cannot be null.");

		} else if (implDto.getFileDto() == null) {

			addError(implDto, MessageKeysEnum.FILE_DTO_NULL);

		} else if (StringUtils.isBlank(implDto.getFileDto().getFilename()) || StringUtils.isBlank(implDto.getFileParts().getName())
				|| StringUtils.isBlank(implDto.getFileParts().getExtension())) {

			addError(implDto, MessageKeysEnum.FILE_NAME_NULL_OR_EMPTY);

		} else if (implDto.getFileDto().getFilename().length() > FileManagerProperties.MAX_OS_FILENAME_LENGTH) {

			addError(implDto, MessageKeysEnum.FILE_NAME_TOO_LONG);

		} else if (StringUtils.startsWithAny(implDto.getFileParts().getName(), FileManagerProperties.FILE_NAME_ILLEGAL_CHARS)
				|| StringUtils.endsWithAny(implDto.getFileParts().getName(), FileManagerProperties.FILE_NAME_ILLEGAL_CHARS)) {

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
		implDto.addMessage(MessageSeverity.ERROR, messageKey.getKey(), messageKey.getMessage());
	}

}
