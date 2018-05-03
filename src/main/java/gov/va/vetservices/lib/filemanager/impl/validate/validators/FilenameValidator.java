/**
 *
 */
package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ValidatorDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.impl.validate.Validator;
import gov.va.vetservices.lib.filemanager.impl.validate.ValidatorArg;

/**
 * Validate file names for consistency with common operating system constraints.
 * This class does NOT attempt to determine if the file is supported for PDF conversion.
 * <p>
 * The returned messages list is the same as the messages returned on the ValidatorDto parameter.
 *
 * @author aburkholder
 */
public class FilenameValidator implements Validator<ValidatorDto> {

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
	public List<Message> validate(ValidatorArg<ValidatorDto> toValidate) {  // NOSONAR - shut up complaint about cyclomatic complexity
		if (toValidate == null) {
			throw new IllegalArgumentException("Validator Dto cannot be null.");
		}

		ValidatorDto vdto = toValidate.getValue();

		if (vdto == null) {

			throw new IllegalArgumentException("Validator Dto cannot be null.");

		} else if (vdto.getFileDto() == null) {

			addError(vdto, MessageKeys.FILE_DTO_NULL);

		} else if (StringUtils.isBlank(vdto.getFileDto().getFilename()) || StringUtils.isBlank(vdto.getFileParts().getName())
				|| StringUtils.isBlank(vdto.getFileParts().getExtension())) {

			addError(vdto, MessageKeys.FILE_NAME_NULL_OR_EMPTY);

		} else if (vdto.getFileDto().getFilename().length() > FileManagerProperties.MAX_OS_FILENAME_LENGTH) {

			addError(vdto, MessageKeys.FILE_NAME_TOO_LONG);

		} else if (StringUtils.startsWithAny(vdto.getFileParts().getName(), FileManagerProperties.FILE_NAME_ILLEGAL_CHARS)
				|| StringUtils.endsWithAny(vdto.getFileParts().getName(), FileManagerProperties.FILE_NAME_ILLEGAL_CHARS)) {

			addError(vdto, MessageKeys.FILE_NAME_MALFORMED);

		}

		return vdto.getMessages().isEmpty() ? null : vdto.getMessages();
	}

	/**
	 * Adds a {@link MessageSeverity#ERROR} message to the {@link ValidatorDto}.
	 *
	 * @param vdto the ValidatorDto
	 * @param messageKey the {@link MessageKeys} enumeration for key and message
	 */
	private void addError(ValidatorDto vdto, MessageKeys messageKey) {
		vdto.addMessage(MessageSeverity.ERROR, messageKey.getKey(), messageKey.getMessage());
	}

}
