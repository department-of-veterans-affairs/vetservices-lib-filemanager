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
 *
 * The returned messages list is the same as the messages returned on the ValidatorDto parameter.
 *
 * @author aburkholder
 */
public class FilenameValidator implements Validator<ValidatorDto> {

	/**
	 * <p>
	 * Validate file names for consistency with common operating system constraints.
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
		ValidatorDto vdto = toValidate.getValue();

		if (vdto == null) {

			throw new IllegalArgumentException("Validator Dto cannot be null.");

		} else if (vdto.getFileDto() == null) {

			vdto.addMessage(MessageSeverity.ERROR, MessageKeys.FILE_DTO_NULL.getKey(), MessageKeys.FILE_DTO_NULL.getMessage());

		} else if (StringUtils.isBlank(vdto.getFileDto().getFilename()) || StringUtils.isBlank(vdto.getFileParts().getName())
				|| StringUtils.isBlank(vdto.getFileParts().getExtension())) {

			vdto.addMessage(MessageSeverity.ERROR, MessageKeys.FILE_NAME_NULL_OR_EMPTY.getKey(),
					MessageKeys.FILE_NAME_NULL_OR_EMPTY.getMessage());

		} else if (vdto.getFileDto().getFilename().length() > FileManagerProperties.MAX_OS_FILENAME_LENGTH) {

			vdto.addMessage(MessageSeverity.ERROR, MessageKeys.FILE_NAME_TOO_LONG.getKey(),
					MessageKeys.FILE_NAME_TOO_LONG.getMessage());

		} else if (StringUtils.startsWithAny(FileManagerProperties.FILE_NAME_ILLEGAL_STRING, vdto.getFileParts().getName())
				|| StringUtils.endsWithAny(FileManagerProperties.FILE_NAME_ILLEGAL_STRING, vdto.getFileParts().getName())) {

			vdto.addMessage(MessageSeverity.ERROR, MessageKeys.FILE_NAME_MALFORMED.getKey(),
					MessageKeys.FILE_NAME_MALFORMED.getMessage());

		}

		return vdto.getMessages().isEmpty() ? null : vdto.getMessages();
	}

}
