/**
 *
 */
package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import java.util.List;

import javax.activation.MimeType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ValidatorDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.impl.validate.Validator;
import gov.va.vetservices.lib.filemanager.impl.validate.ValidatorArg;
import gov.va.vetservices.lib.filemanager.mime.MimeTypeDetector;

/**
 * Validate the file MIME type to ensure the file is a type that can be converted to PDF.
 *
 * Validation messages are returned on the validatorDto parameter.
 *
 * @author aburkholder
 */
public class FileConvertibleMimeTypeValidator implements Validator<ValidatorDto> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileConvertibleMimeTypeValidator.class);

	/**
	 * <p>
	 * Validate the file MIME type to ensure the file is a type that can be converted to PDF.
	 * </p>
	 * <p>
	 * If validations succeeds, {@code null} is returned, otherwise the returned list of messages is also returned on the ValidataorDto
	 * parameter.
	 * </p>
	 * <p>
	 * JavaDoc from {@link Validator}:<br/>
	 * {@inheritDoc AbstractValidator#validate(java.lang.Object)}
	 * <p/>
	 */
	@Override
	public List<Message> validate(ValidatorArg<ValidatorDto> toValidate) {
		ValidatorDto vdto = toValidate.getValue();

		// check file extension if it is provided
		if (isExtensionSupported(vdto)) {
			if (isMimetypeMatch(vdto)) {
				// TODO there is likely more to do in here, still perusing the WSS code
			}
		}

		return vdto.getMessages().isEmpty() ? null : vdto.getMessages();
	}

	/**
	 * <p>
	 * Checks if the provided file extension is supported.
	 * <p>
	 * If the file extension in ValidatorDto is null/empty, this method will return {@code true},
	 * as the file extension is not technically required if the file type can otherwise be detected.
	 * <p>
	 * If the extension is not supported, adds an ERROR to the ValidatorDto parameter and returns {@code false}.
	 *
	 * @param vdto
	 * @return boolean
	 */
	private boolean isExtensionSupported(ValidatorDto vdto) {
		boolean isValid = true;

		if (!StringUtils.isBlank(vdto.getFileParts().getExtension())
				&& !MimeTypeDetector.isFileExtensionSupported(vdto.getFileParts().getExtension())) {

			isValid = false;
			vdto.addMessage(MessageSeverity.ERROR, MessageKeys.FILE_EXTENSION_NOT_CONVERTIBLE,
					MessageKeys.getMessage(MessageKeys.FILE_EXTENSION_NOT_CONVERTIBLE));
		}

		return isValid;
	}

	/**
	 * <p>
	 * Checks if the provided file extension and the detected MimeType match.
	 * <p>
	 * The terms of a positive match are loose:
	 * <li>if both the derived and detected MimeTypes are defined, they must match</li>
	 * <li>if only one of the derived or detected MimeTypes are defined, it is considered a match</li>
	 * <li>if neither of the derived or detected MimeTypes are defined, it is considered a mis-match</li>
	 * <p>
	 *
	 * @param vdto
	 * @return boolean
	 */
	private boolean isMimetypeMatch(ValidatorDto vdto) {
		boolean hasMatch = false;

		// get the mime type for the extension
		MimeType extensionMimetype = MimeTypeDetector.deriveMimeType(vdto.getFileParts().getExtension());
		// get the file bytes
		MimeType fileMimetype = MimeTypeDetector.detectMimeType(vdto.getFileDto().getFilebytes());

		// check for difference
		boolean hasExtensionMimeType = (extensionMimetype != null) && !extensionMimetype.toString().contains("*");
		boolean hasFileMimeType = (fileMimetype != null) && !fileMimetype.toString().contains("*");

		if (hasExtensionMimeType && hasFileMimeType) {
			hasMatch = extensionMimetype.toString().equals(fileMimetype.toString());

		} else {
			hasMatch = hasExtensionMimeType || hasFileMimeType;
		}

		if (!hasMatch) {
			vdto.addMessage(MessageSeverity.ERROR, MessageKeys.FILE_EXTENSION_CONTENT_MISMATCH,
					MessageKeys.getMessage(MessageKeys.FILE_EXTENSION_CONTENT_MISMATCH));
		}

		return hasMatch;
	}
}
