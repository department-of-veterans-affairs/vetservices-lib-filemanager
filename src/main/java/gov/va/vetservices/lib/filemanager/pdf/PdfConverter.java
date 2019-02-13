package gov.va.vetservices.lib.filemanager.pdf;

import javax.activation.MimeType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
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
@Component(PdfConverter.BEAN_NAME)
public class PdfConverter {
	public static final String BEAN_NAME = "pdfConverter";

	private static final String IMAGE = "image";
	private static final String TEXT = "text";

	@Autowired
	@Qualifier(MimeTypeDetector.BEAN_NAME)
	private MimeTypeDetector mimeTypeDetector;

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
	public final byte[] convert(final byte[] bytes, final FilePartsDto parts)  {
		final MimeType mimetype = mimeTypeDetector.detectMimeType(bytes, parts);
		byte[] returnBytes = null;

		if (mimetype.match(ConvertibleTypesEnum.PDF.getMimeType())) {
			returnBytes = bytes;

		} else if (IMAGE.equals(mimetype.getPrimaryType())) {
			returnBytes = imageConverter.getPdf(bytes, parts);

		} else if (TEXT.equals(mimetype.getPrimaryType())) {
			returnBytes = textConverter.getPdf(bytes, parts);

		}

		return returnBytes;
	}

}
