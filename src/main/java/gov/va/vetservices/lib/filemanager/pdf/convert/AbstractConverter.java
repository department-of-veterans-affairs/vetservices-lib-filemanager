package gov.va.vetservices.lib.filemanager.pdf.convert;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.ascent.framework.util.SanitizationUtil;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.exception.PdfConverterException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.pdf.itext.LayoutAwarePdfDocument;

/**
 * Method signatures and base implementation for file type converters.
 * Currently defines one conversion to get a PDF file.
 *
 * @author aburkholder
 */
public abstract class AbstractConverter {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConverter.class);

	/** NEW_LINE Constant */
	protected static final String NEW_LINE = "\n";
	/** generated PDF paragraph fit */
	protected static final float FIT_500F = 500;

	/**
	 * Convert a file into a PDF.
	 * The {@code bytes} parameter must be suitable for the converter being invoked.
	 *
	 * @param bytes the byte array to be converted into a PDF
	 * @param parts the name of the file being converted
	 * @return byte[] the PDF
	 * @throws FileManagerException some problem processing the bytes
	 */
	public abstract byte[] getPdf(byte[] bytes, FilePartsDto parts) throws FileManagerException;

	/**
	 * Get an input stream reader that can be used to read a text file byte stream one line at a time.
	 *
	 * @param bytes the text file bytes
	 * @return BufferedReader text file stream reader
	 */
	protected BufferedReader getPlainTextReader(final byte[] bytes) {
		return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes), Charset.forName("UTF-8")));
	}

	/**
	 * Closes the PDF Document, and handles any exception that could arise from attempting to close it.
	 *
	 * @param pdfDocument the PDF Document to close
	 * @param parts the file name parts associated with the document
	 * @throws PdfConverterException any exception caused by the attempt to close the document
	 */
	protected void closeDocument(final LayoutAwarePdfDocument pdfDocument, final FilePartsDto parts) throws PdfConverterException {
		if (pdfDocument != null && !pdfDocument.isClosed()) {
			try {
				pdfDocument.close();
			} catch (final Exception e) { // NOSONAR - intentionally catching everything
				doThrowException(e, parts.getName() + "." + parts.getExtension());
			}
		}
	}

	/**
	 * Logs the error and throws a PDF Conversion exception.
	 *
	 * @param e the Throwable that caused this exception to be thrown
	 * @param severity the severity of the error
	 * @param messageKey the enumeration to use for key and message
	 * @param filename the filename argument for {@link java.text.MessageFormat} processing on the message
	 * @throws PdfConverterException the PDF Conversion Processing exception
	 */
	protected void doThrowException(final Throwable e, final String filename) throws PdfConverterException {
		final MessageKeysEnum messageKeys = MessageKeysEnum.PDF_CONVERSION_PROCESSING;

		LOGGER.error(messageKeys.getKey() + ": " + messageKeys.getMessage(), e);
		throw new PdfConverterException(e, MessageSeverity.ERROR, messageKeys.getKey(), messageKeys.getMessage(),
				SanitizationUtil.safeFilename(filename), e.getMessage());
	}

}
