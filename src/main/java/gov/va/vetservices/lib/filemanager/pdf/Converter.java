package gov.va.vetservices.lib.filemanager.pdf;

import javax.activation.MimeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileParts;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.exception.PdfConverterException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
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
public class Converter {
	private static final Logger LOGGER = LoggerFactory.getLogger(Converter.class);

	private static final String IMAGE = "image";
	private static final String TEXT = "text";

	private MimeTypeDetector detector = new MimeTypeDetector();
	private ImageConverter imageConverter = new ImageConverter();
	private TextConverter textConverter = new TextConverter();

	/**
	 * Convert the byte array of a supported file type into a PDF.
	 * This method assumes that the {@code bytes} parameter has already been validated and confirmed to be convertible.
	 * If the byte array cannot be converted, an exception is thrown.
	 *
	 * @param bytes the byte array of the supported file to be converted
	 * @return byte[] the PDF file byte array
	 * @throws FileManagerException
	 */
	public final byte[] convert(byte[] bytes, FileParts parts) throws FileManagerException {
		MimeType mimetype = detector.detectMimeType(bytes, parts);

		if (ConvertibleTypesEnum.hasMimeType(mimetype) && mimetype.match(ConvertibleTypesEnum.PDF.getMimeType())) {
			return bytes;

		} else if (IMAGE.equals(mimetype.getPrimaryType())) {
			return imageConverter.getPdf(bytes, parts);

		} else if (TEXT.equals(mimetype.getPrimaryType())) {
			return textConverter.getPdf(bytes, parts);

		}
		// catch-all ... should never actually get here
		MessageKeys messagekey = MessageKeys.FILE_EXTENSION_NOT_CONVERTIBLE;
		String filename = parts.getName() + "." + parts.getExtension();
		LOGGER.error("Attempting to convert unsupported file type. " + messagekey.getKey() + ": " + messagekey.getMessage());
		throw new PdfConverterException(MessageSeverity.ERROR, messagekey.getKey(), messagekey.getMessage(), filename);
	}

}
