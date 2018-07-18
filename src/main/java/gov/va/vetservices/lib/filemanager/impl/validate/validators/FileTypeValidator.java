package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import javax.activation.MimeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.ascent.framework.util.SanitizationUtil;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.impl.validate.Validator;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.mime.MimeTypeDetector;
import gov.va.vetservices.lib.filemanager.pdf.PdfIntegrityChecker;

/**
 * Validate the file MIME type to ensure the file meets the criteria for conversion to PDF.
 * <p>
 * This class determines if the file meets all criteria for conversion to PDF.
 * It DOES NOT perform any tests to confirm that the conversion can be successfully performed.
 * <p>
 * Validation messages are returned on the implDto parameter.
 *
 * @author aburkholder
 */
@java.lang.SuppressWarnings("squid:S1166")
@Component(FileTypeValidator.BEAN_NAME)
public class FileTypeValidator implements Validator<ImplDto> {

	public static final String BEAN_NAME = "fileTypeValidator";
	private static final Logger LOGGER = LoggerFactory.getLogger(FileTypeValidator.class);

	private static final String IMAGE = "image";

	private final MimeTypeDetector mimeTypeDetector = new MimeTypeDetector();

	private final PdfIntegrityChecker pdfIntegrityChecker = new PdfIntegrityChecker();

	/**
	 * <p>
	 * Validate the file MIME type to ensure the file meets the criteria for conversion to PDF.
	 * <p>
	 * This method determines if the file meets all criteria for conversion to PDF.
	 * It DOES NOT perform any tests to confirm that the conversion can be successfully performed.
	 * <p>
	 * If validations succeeds, {@code null} is returned, otherwise the returned list of messages is also returned on the ValidataorDto
	 * parameter.
	 * <p>
	 * JavaDoc from {@link Validator}:<br/>
	 * {@inheritDoc Validator#validate(ImplArgDto)}
	 * <p>
	 *
	 * @throws FileManagerException
	 */
	@Override
	public List<Message> validate(final ImplArgDto<ImplDto> toValidate) {
		if (toValidate == null || toValidate.getValue() == null) {
			final MessageKeysEnum msg = MessageKeysEnum.UNEXPECTED_ERROR;
			final Message message = new Message(MessageSeverity.ERROR, msg.getKey(), msg.getMessage());
			return Arrays.asList(new Message[] { message });
		}

		final ImplDto implDto = toValidate.getValue();

		MimeType detectedMimetype = null;
		boolean convertible = false;
		if (isExtensionSupported(implDto)) {
			detectedMimetype = isMimetypeValid(implDto);
			convertible = isImageConvertible(implDto, detectedMimetype);
		}

		if (convertible && detectedMimetype != null && ConvertibleTypesEnum.PDF.getMimeType().match(detectedMimetype)) {
			// file is a PDF
			try {
				if (!pdfIntegrityChecker.isReadable(implDto.getOriginalFileDto().getFilebytes(),
						implDto.getOriginalFileDto().getFilename())) {
					implDto.addMessage(new Message(MessageSeverity.ERROR, MessageKeysEnum.PDF_UNREADABLE.getKey(), MessageFormat
							.format(MessageKeysEnum.PDF_UNREADABLE.getMessage(), implDto.getOriginalFileDto().getFilename())));
				}
			} catch (final FileManagerException e) { // squid:S1166
				LOGGER.debug(e.getMessageSeverity().toString() + " " + e.getKey() + ": " + e.getMessage());
				implDto.addMessage(new Message(e.getMessageSeverity(), e.getKey(), e.getMessage()));
			}
		}

		return implDto.getMessages().isEmpty() ? null : implDto.getMessages();
	}

	/**
	 * <p>
	 * Checks if the provided file extension is supported.
	 * <p>
	 * If the file extension in ImplDto is null/empty, this method will return {@code true},
	 * as the file extension is not technically required if the file type can otherwise be detected.
	 * <p>
	 * If the extension is not supported, adds an ERROR to the ImplDto parameter and returns {@code false}.
	 *
	 * @param implDto
	 * @return boolean
	 */
	protected boolean isExtensionSupported(final ImplDto implDto) {
		boolean isValid = false;

		if (implDto != null) {
			if (implDto.getFileParts() != null) {
				final MimeType mimetype = ConvertibleTypesEnum.getMimeTypeForExtension(implDto.getFileParts().getExtension());
				isValid = mimetype != null;
			}
			if (!isValid) {
				implDto.addMessage(new Message(MessageSeverity.ERROR, MessageKeysEnum.FILE_EXTENSION_NOT_CONVERTIBLE.getKey(),
						MessageFormat.format(MessageKeysEnum.FILE_EXTENSION_NOT_CONVERTIBLE.getMessage(),
								implDto.getOriginalFileDto().getFilename())));
			}
		}

		return isValid;
	}

	/**
	 * <p>
	 * Checks if the provided file extension and the detected MimeType match.
	 * Messages are returned on the ImplDto parameter messages.
	 * If the type is undetectable (e.g. due to corruption or other issue), {@code null} is returned.
	 * <p>
	 * The terms of a positive match are loose:
	 * <li>if both the derived and detected MimeTypes are defined, they must match</li>
	 * <li>if only one of the derived or detected MimeTypes are defined, it is considered a match</li>
	 * <li>if neither of the derived or detected MimeTypes are defined, it is considered a mis-match</li>
	 * <p>
	 *
	 * @param implDto the ImplDto with the bytes and filename
	 * @return MimeType the detected MIME type, or {@code null} if undetectable
	 */
	protected MimeType isMimetypeValid(final ImplDto implDto) {
		MimeType detectedMimetype = null;

		// get the mime type for the extension
		if (implDto == null) {

			throw new IllegalArgumentException("Impl Dto cannot be null.");

		} else if (implDto.getOriginalFileDto() == null || implDto.getOriginalFileDto().getFilebytes() == null
				|| implDto.getOriginalFileDto().getFilebytes().length < 1) {

			implDto.addMessage(new Message(MessageSeverity.ERROR, MessageKeysEnum.FILE_DTO_NULL.getKey(),
					MessageKeysEnum.FILE_DTO_NULL.getMessage()));

		} else {
			try {
				// performs all necessary checks, including extension match and supported types
				detectedMimetype =
						mimeTypeDetector.detectMimeType(implDto.getOriginalFileDto().getFilebytes(), implDto.getFileParts());
			} catch (final FileManagerException e) { // squid:S1166
				LOGGER.debug(e.getMessageSeverity().toString() + " " + e.getKey() + ": " + e.getMessage());
				implDto.addMessage(new Message(e.getMessageSeverity(), e.getKey(), e.getMessage()));
			}
		}

		return detectedMimetype;
	}

	/**
	 * Check if iText is ok with embedding the provided image.
	 * <p>
	 * If the detectedMimetype is <b>not</b> {@code image/*}, this method will <b>always</b> return {@code true} - it is intended for
	 * images only, anything else is not part of this validation.
	 * If ImplDto or its FileDto are {@code null}, {@code false} is always returned.
	 *
	 * @param implDto the bytes and filename
	 * @return boolean {@code true} if the image can be embedded (or the bytes are not an image)
	 */
	protected boolean isImageConvertible(final ImplDto implDto, final MimeType detectedMimetype) {
		boolean isValid = true;

		if (implDto == null || implDto.getOriginalFileDto() == null) {
			return false;
		}

		if (detectedMimetype != null && IMAGE.equals(detectedMimetype.getPrimaryType())) {
			// see if itext has any issue
			try {
				// the document is a valid image type if no exception is thrown.
				new Image(ImageDataFactory.create(implDto.getOriginalFileDto().getFilebytes()));
			} catch (final com.itextpdf.io.IOException e) {
				isValid = false;
				LOGGER.error("iText error while performing Image.getInstance(..) on bytes for file "
						+ SanitizationUtil.safeFilename(implDto.getOriginalFileDto().getFilename()), e);
				implDto.addMessage(new Message(MessageSeverity.ERROR, MessageKeysEnum.FILE_CONTENT_NOT_CONVERTIBLE.getKey(),
						MessageFormat.format(MessageKeysEnum.FILE_CONTENT_NOT_CONVERTIBLE.getMessage(),
								implDto.getOriginalFileDto().getFilename())));
			} catch (final Throwable e) { // NOSONAR - intent is to catch everything // squid:S1166
				isValid = false;
				LOGGER.debug(
						MessageSeverity.ERROR.toString() + " " + MessageKeysEnum.IMAGE_ITEXT_NOT_CONVERTIBLE.getKey() + ": "
								+ MessageKeysEnum.IMAGE_ITEXT_NOT_CONVERTIBLE.getMessage(),
						implDto.getOriginalFileDto().getFilename(), e.getMessage());
				implDto.addMessage(new Message(MessageSeverity.ERROR, MessageKeysEnum.IMAGE_ITEXT_NOT_CONVERTIBLE.getKey(),
						MessageFormat.format(MessageKeysEnum.IMAGE_ITEXT_NOT_CONVERTIBLE.getMessage(),
								implDto.getOriginalFileDto().getFilename(), e.getMessage())));
			}
		}

		return isValid;
	}

}
