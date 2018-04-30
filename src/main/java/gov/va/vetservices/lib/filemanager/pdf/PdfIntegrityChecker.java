package gov.va.vetservices.lib.filemanager.pdf;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.exceptions.InvalidPdfException;
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
// Sonar raises a major violation for exceptions that are not logged - even though they are clearly logged.
@java.lang.SuppressWarnings("squid:S1166")
public class PdfIntegrityChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger(PdfIntegrityChecker.class);

	public boolean isReadable(final byte[] bytes, String filename) throws FileManagerException {
		boolean isreadable = false;

		PdfReader pdfReader = null;
		try {
			// The document is a valid PDF type if no exception is thrown.
			pdfReader = newPdfReader(bytes, filename);

			if (pdfReader != null) {
				// throws exception if locked (e.g. encrypted)
				isLocked(pdfReader, filename);
				// throws exception if signed PDF has been tampered with
				isTampered(pdfReader, filename);

				isreadable = true;

			} else {
				MessageKeys msg = MessageKeys.PDF_UNREADABLE;
				throw new FileManagerException(MessageSeverity.ERROR, msg.getKey(), msg.getMessage(), filename);
			}
		} finally {
			if (pdfReader != null) {
				try {
					pdfReader.close();
				} catch (Throwable e) { // NOSONAR squid:S1166
					LOGGER.debug("Couldn't close PdfReader.");
				}
			}
		}

		return isreadable;
	}

	/**
	 * PdfReader instantiation extracted to this method for testing purposes.
	 *
	 * @param bytes the PDF bytes
	 * @return PdfReader the PdfReader
	 * @throws IOException if bytes cannot be read
	 */
	protected PdfReader newPdfReader(byte[] bytes, String filename) throws FileManagerException {
		PdfReader reader = null;
		try {
			reader = new PdfReader(bytes);
		} catch (Exception e) { // NOSONAR squid:S1166
			if (e.getClass().isAssignableFrom(InvalidPdfException.class)) {
				LOGGER.info("PDF file " + filename + " is unreadable.");
				throw new FileManagerException(MessageSeverity.ERROR, MessageKeys.PDF_UNREADABLE.getKey(),
						MessageKeys.PDF_UNREADABLE.getMessage(), filename);
			}
		}
		return reader;
	}

	/**
	 * Throws exception it PDF is locked
	 *
	 * @param pdfReader the PdfReader
	 * @param filename the name of the file
	 * @throws FileManagerException thrown if file is encrypted
	 */
	protected final void isLocked(PdfReader pdfReader, String filename) throws FileManagerException {
		boolean islocked = false;
		MessageKeys msg = MessageKeys.PDF_LOCKED;

		try {
			islocked = pdfReader.isEncrypted() || pdfReader.is128Key();
		} catch (Throwable e) { // NOSONAR - intentional
			msg = MessageKeys.PDF_CONTENT_INVALID;
			islocked = true;
		}
		if (islocked) {
			LOGGER.debug("PDF file " + filename + " is encrypted.");
			throw new FileManagerException(MessageSeverity.ERROR, msg.getKey(), msg.getMessage(), filename);
		}
	}

	/**
	 * Throws exception has been tampered with (must be signed for tamper detection to work).
	 *
	 * @param pdfReader the PdfReader
	 * @param filename the name of the file
	 * @throws FileManagerException thrown if file is tampered
	 */
	protected final void isTampered(PdfReader pdfReader, String filename) throws FileManagerException {
		boolean istampered = false;
		MessageKeys msg = MessageKeys.PDF_TAMPERED;

		try {
			istampered = pdfReader.isTampered();
		} catch (Throwable e) { // NOSONAR - intentional
			msg = MessageKeys.PDF_CONTENT_INVALID;
			istampered = true;
		}
		if (istampered) {
			LOGGER.info("Signed PDF file " + filename + " has been tampered with.");
			throw new FileManagerException(MessageSeverity.ERROR, msg.getKey(), msg.getMessage(), filename);
		}
	}
}
