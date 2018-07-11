package gov.va.vetservices.lib.filemanager.pdf;

import javax.activation.MimeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.exception.PdfConverterException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.mime.MimeTypeDetector;
import gov.va.vetservices.lib.filemanager.pdf.convert.ImageConverter;
import gov.va.vetservices.lib.filemanager.pdf.convert.TextConverter;

/**
 * Convert the byte array of a supported file type into a PDF.
 * It is assumed that the submitted file has already been validated to be convertible.
 * If the file cannot be converted, an exception is thrown.
 *
 * @author aburkholder
 */
public class PdfConverter {
	private static final Logger LOGGER = LoggerFactory.getLogger(PdfConverter.class);

	private static final String IMAGE = "image";
	private static final String TEXT = "text";

	private final MimeTypeDetector mimeDetector = new MimeTypeDetector();
	private final ImageConverter imageConverter = new ImageConverter();
	private final TextConverter textConverter = new TextConverter();

	/**
	 * Convert the byte array of a supported file type into a PDF.
	 * This method assumes that the {@code bytes} parameter has already been validated and confirmed to be convertible.
	 * If the byte array cannot be converted, an exception is thrown.
	 *
	 * @param bytes the byte array of the supported file to be converted
	 * @return byte[] the PDF file byte array
	 * @throws FileManagerException
	 */
	public final byte[] convert(final byte[] bytes, final FilePartsDto parts) throws FileManagerException {
		final MimeType mimetype = mimeDetector.detectMimeType(bytes, parts);

		if (ConvertibleTypesEnum.hasMimeType(mimetype)) {
			if (mimetype.match(ConvertibleTypesEnum.PDF.getMimeType())) {
				return bytes;

			} else if (IMAGE.equals(mimetype.getPrimaryType())) {
				return imageConverter.getPdf(bytes, parts);

			} else if (TEXT.equals(mimetype.getPrimaryType())) {
				return textConverter.getPdf(bytes, parts);

			}
		}
		// catch-all ... should never actually get here
		final MessageKeysEnum messagekey = MessageKeysEnum.FILE_EXTENSION_NOT_CONVERTIBLE;
		final String filename = parts.getName() + "." + parts.getExtension();
		LOGGER.error("Attempting to convert unsupported file type. " + messagekey.getKey() + ": " + messagekey.getMessage());
		throw new PdfConverterException(MessageSeverity.ERROR, messagekey.getKey(), messagekey.getMessage(), filename);
	}

}
