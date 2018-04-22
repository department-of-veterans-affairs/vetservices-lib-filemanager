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
public class FileConvertibleValidator implements Validator<ValidatorDto> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileConvertibleValidator.class);

	private MimeTypeDetector mimeTypeDetector = new MimeTypeDetector();

	private MimeType detectedMimetype;

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

		/*
		 * The order these method calls must not be changed.
		 * Error messages are returned on the vdto object.
		 */
		isExtensionSupported(vdto);
		isMimetypeValid(vdto);
		isConvertible(vdto);

		if ((detectedMimetype != null) && ConvertibleTypesEnum.PDF.getMimeType().match(detectedMimetype)) {
			try {
				if (PdfIntegrityChecker.isReadable(vdto.getFileDto().getFilebytes(), vdto.getFileDto().getFilename())) {
					vdto.addMessage(MessageSeverity.ERROR, MessageKeys.PDF_LOCKED.getKey(),
							MessageFormat.format(MessageKeys.PDF_LOCKED.getMessage(), vdto.getFileDto().getFilename()));
				}
			} catch (FileManagerException e) {
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
	private boolean isExtensionSupported(ValidatorDto vdto) {
		boolean isValid = true;

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
	 * @param vdto
	 * @return boolean
	 * @throws FileManagerException
	 */
	private boolean isMimetypeValid(ValidatorDto vdto) {
		boolean isValid = false;

		// get the mime type for the extension
		try {
			// performs all necessary checks, including extension match and supported types
			detectedMimetype = mimeTypeDetector.detectMimeType(vdto.getFileDto().getFilebytes(), vdto.getFileDto().getFilename());
			isValid = detectedMimetype != null;
		} catch (FileManagerException e) {
			vdto.addMessage(e.getMessageSeverity(), e.getKey(), e.getMessage());
		}

		return isValid;
	}

	/**
	 * Check if iText is ok with embedding the provided image.<br/>
	 * Any MimeType other than {@code image/*} will simply return {@code true}.
	 *
	 * @param vdto
	 * @return
	 */
	private boolean isConvertible(ValidatorDto vdto) {
		boolean isValid = true;

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
			} catch (final Throwable e) {
				isValid = false;
				vdto.addMessage(MessageSeverity.ERROR, MessageKeys.IMAGE_ITEXT_NOT_CONVERTIBLE.getKey(), MessageFormat.format(
						MessageKeys.IMAGE_ITEXT_NOT_CONVERTIBLE.getMessage(), vdto.getFileDto().getFilename(), e.getMessage()));
			}
		}

		return isValid;
	}
}
