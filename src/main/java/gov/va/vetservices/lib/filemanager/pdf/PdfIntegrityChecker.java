package gov.va.vetservices.lib.filemanager.pdf;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.mime.MimeTypeDetector;

/**
 * Checks the integrity of the PDF and confirms it can be read.
 * Use {@link MimeTypeDetector} to ensure the files used with this class are type {@code application/pdf}.
 *
 * @author aburkholder
 */
public class PdfIntegrityChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger(PdfIntegrityChecker.class);

	public static boolean isReadable(final byte[] bytes, String filename) throws FileManagerException {
		boolean isreadable = false;

		PdfReader pdfReader = null;
		try {
			// The document is a valid PDF type if no exception is thrown.
			pdfReader = new PdfReader(bytes);

			// throws exception if locked (e.g. encrypted)
			isLocked(pdfReader, filename);
			// throws exception if signed PDF has been tampered with
			isTampered(pdfReader, filename);

		} catch (final IOException e) {
			if (e.getMessage().contains("PDF header signature not found")) {
				isreadable = true;
			} else {
				LOGGER.info("Invalid PDF content in file " + filename + ": " + e.getMessage(), e);
				throw new FileManagerException(MessageSeverity.ERROR, MessageKeys.PDF_CONTENT_INVALID.getKey(),
						MessageKeys.PDF_CONTENT_INVALID.getMessage(), filename);
			}
		} finally {
			if (pdfReader != null) {
				try {
					pdfReader.close();
					// CHECKSTYLE:OFF
				} catch (Throwable e) {
					// ignore, nothing to do here
					e.printStackTrace();
				}
				// CHECKSTYLE:ON
			}
		}

		return isreadable;
	}

	private static final void isLocked(PdfReader pdfReader, String filename) throws FileManagerException {
		if (pdfReader.isEncrypted() || pdfReader.is128Key()) {
			LOGGER.info("PDF file " + filename + " is encrypted.");
			throw new FileManagerException(MessageSeverity.ERROR, MessageKeys.PDF_LOCKED.getKey(), MessageKeys.PDF_LOCKED.getMessage(),
					filename);
		}
	}

	private static final void isTampered(PdfReader pdfReader, String filename) throws FileManagerException {
		if (pdfReader.isTampered()) {
			LOGGER.info("Signed PDF file " + filename + " has been tampered with.");
			throw new FileManagerException(MessageSeverity.ERROR, MessageKeys.PDF_TAMPERED.getKey(),
					MessageKeys.PDF_TAMPERED.getMessage(), filename);
		}
	}
}
