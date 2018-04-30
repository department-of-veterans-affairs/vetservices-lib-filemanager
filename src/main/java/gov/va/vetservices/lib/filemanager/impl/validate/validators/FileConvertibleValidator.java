package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.activation.MimeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ValidatorDto;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.impl.validate.Validator;
import gov.va.vetservices.lib.filemanager.impl.validate.ValidatorArg;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.mime.MimeTypeDetector;
import gov.va.vetservices.lib.filemanager.pdf.PdfIntegrityChecker;

/**
 * Validate the file MIME type to ensure the file is a type that can be converted to PDF.
 *
 * Validation messages are returned on the validatorDto parameter.
 *
 * @author aburkholder
 */
@java.lang.SuppressWarnings("squid:S1166")
public class FileConvertibleValidator implements Validator<ValidatorDto> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileConvertibleValidator.class);

	private MimeTypeDetector mimeTypeDetector = new MimeTypeDetector();

	private PdfIntegrityChecker pdfIntegrityChecker = new PdfIntegrityChecker();

	/**
	 * <p>
	 * Validate the file MIME type to ensure the file is a type that can be converted to PDF.
	 * <p>
	 * If validations succeeds, {@code null} is returned, otherwise the returned list of messages is also returned on the ValidataorDto
	 * parameter.
	 * <p>
	 * JavaDoc from {@link Validator}:<br/>
	 * {@inheritDoc AbstractValidator#validate(java.lang.Object)}
	 * <p>
	 *
	 * @throws FileManagerException
	 */
	@Override
	public List<Message> validate(ValidatorArg<ValidatorDto> toValidate) {
		ValidatorDto vdto = toValidate.getValue();

		MimeType detectedMimetype = null;
		boolean convertible = false;
		if (isExtensionSupported(vdto)) {
			detectedMimetype = isMimetypeValid(vdto);
			convertible = isImageConvertible(vdto, detectedMimetype);
		}

		if (convertible && (detectedMimetype != null) && ConvertibleTypesEnum.PDF.getMimeType().match(detectedMimetype)) {
			try {
				if (!pdfIntegrityChecker.isReadable(vdto.getFileDto().getFilebytes(), vdto.getFileDto().getFilename())) {
					vdto.addMessage(MessageSeverity.ERROR, MessageKeys.PDF_UNREADABLE.getKey(),
							MessageFormat.format(MessageKeys.PDF_UNREADABLE.getMessage(), vdto.getFileDto().getFilename()));
				}
			} catch (FileManagerException e) { // squid:S1166
				LOGGER.debug(e.getMessageSeverity().toString() + " " + e.getKey() + ": " + e.getMessage());
				vdto.addMessage(e.getMessageSeverity(), e.getKey(), e.getMessage());
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
	protected boolean isExtensionSupported(ValidatorDto vdto) {
		boolean isValid = false;

		MimeType mimetype = ConvertibleTypesEnum.getMimeTypeForExtension(vdto.getFileParts().getExtension());
		isValid = mimetype != null;
		if (!isValid) {
			vdto.addMessage(MessageSeverity.ERROR, MessageKeys.FILE_EXTENSION_NOT_CONVERTIBLE.getKey(),
					MessageFormat.format(MessageKeys.FILE_EXTENSION_NOT_CONVERTIBLE.getMessage(), vdto.getFileDto().getFilename()));
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
	 * @param vdto the ValidatorDto with the bytes and filename
	 * @return MimeType the detected MIME type
	 * @throws FileManagerException
	 */
	protected MimeType isMimetypeValid(ValidatorDto vdto) {
		MimeType detectedMimetype = null;

		// get the mime type for the extension
		if (vdto == null) {

			throw new IllegalArgumentException("Validator Dto cannot be null.");

		} else if ((vdto.getFileDto() == null) || (vdto.getFileDto().getFilebytes() == null)
				|| (vdto.getFileDto().getFilebytes().length < 1)) {

			vdto.addMessage(MessageSeverity.ERROR, MessageKeys.FILE_DTO_NULL.getKey(), MessageKeys.FILE_DTO_NULL.getMessage());

		} else {
			try {
				// performs all necessary checks, including extension match and supported types
				detectedMimetype = mimeTypeDetector.detectMimeType(vdto.getFileDto().getFilebytes(), vdto.getFileParts());
			} catch (FileManagerException e) { // squid:S1166
				LOGGER.debug(e.getMessageSeverity().toString() + " " + e.getKey() + ": " + e.getMessage());
				vdto.addMessage(e.getMessageSeverity(), e.getKey(), e.getMessage());
			}
		}

		return detectedMimetype;
	}

	/**
	 * Check if iText is ok with embedding the provided image.
	 * <p>
	 * If the detectedMimetype is <b>not</b> {@code image/*}, this method will <b>always</b> return {@code true} - it is intended for
	 * images only, anything else is not part of this validation.
	 * If ValidatorDto or its FileDto are {@code null}, {@code false} is always returned.
	 *
	 * @param vdto the bytes and filename
	 * @return boolean {@code true} if the image can be embedded (or the bytes are not an image)
	 */
	protected boolean isImageConvertible(ValidatorDto vdto, MimeType detectedMimetype) {
		boolean isValid = true;

		if ((vdto == null) || (vdto.getFileDto() == null)) {
			return false;
		}

		if ((detectedMimetype != null) && "image".equals(detectedMimetype.getPrimaryType())) {
			// see if itext has any issue
			try {
				// the document is a valid image type if no exception is thrown.
				Image.getInstance(vdto.getFileDto().getFilebytes());
			} catch (final IOException | BadElementException e) {
				isValid = false;
				LOGGER.error("iText error while performing Image.getInstance(..) on bytes for file " + vdto.getFileDto().getFilename(),
						e);
				vdto.addMessage(MessageSeverity.ERROR, MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getKey(),
						MessageFormat.format(MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getMessage(), vdto.getFileDto().getFilename()));
			} catch (final Throwable e) { // NOSONAR - intent is to catch everything // squid:S1166
				isValid = false;
				LOGGER.debug(
						MessageSeverity.ERROR.toString() + " " + MessageKeys.IMAGE_ITEXT_NOT_CONVERTIBLE.getKey() + ": "
								+ MessageKeys.IMAGE_ITEXT_NOT_CONVERTIBLE.getMessage(),
						vdto.getFileDto().getFilename(), e.getMessage());
				vdto.addMessage(MessageSeverity.ERROR, MessageKeys.IMAGE_ITEXT_NOT_CONVERTIBLE.getKey(), MessageFormat.format(
						MessageKeys.IMAGE_ITEXT_NOT_CONVERTIBLE.getMessage(), vdto.getFileDto().getFilename(), e.getMessage()));
			}
		}

		return isValid;
	}
}
