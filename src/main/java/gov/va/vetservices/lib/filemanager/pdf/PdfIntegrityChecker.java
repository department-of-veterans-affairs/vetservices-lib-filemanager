package gov.va.vetservices.lib.filemanager.pdf;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
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

	private static final String THROWABLE_CORRUPT_1 = "Rebuild failed";
	private static final String THROWABLE_CORRUPT_2 = "cannot be read";
	private static final String REASON_CORRUPT = "corrupt";
	private static final String THROWABLE_PW_EDIT = "signer information does not match";
	private static final String REASON_PW_EDIT = "password protected or restricted for edits";
	private static final String THROWABLE_UNREADABLE_ENCRYPTOR = "Bad type on operand stack";
	private static final String REASON_UNREADABLE_ENCRYPTOR = "encrypted with a certificate";

	private static final String MSG_PDF_FILE = "PDF file ";
	private static final String MSG_IS_UNREADABLE = " is unreadable.";

	public boolean isReadable(final byte[] bytes, final String filename) throws FileManagerException {
		boolean isreadable = false;

		PdfReader pdfReader = null;
		try {
			// The PDF can be read, if no exception is thrown.
			pdfReader = newPdfReader(bytes, filename);

			if (pdfReader != null) {
				// throws exception if locked (e.g. encrypted)
				isLocked(pdfReader, filename);
				// throws exception if signed PDF has been tampered with and a reader app updated the setting
				isTampered(pdfReader, filename);

				isreadable = true;

			} else {
				final MessageKeysEnum msg = MessageKeysEnum.PDF_UNREADABLE;
				throw new FileManagerException(MessageSeverity.ERROR, msg.getKey(), msg.getMessage(), filename);
			}
		} finally {
			if (pdfReader != null) {
				try {
					pdfReader.close();
				} catch (final Throwable e) { // NOSONAR squid:S1166
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
	protected PdfReader newPdfReader(final byte[] bytes, final String filename) throws FileManagerException {
		PdfReader reader = null;
		if (bytes != null && bytes.length > 0 && !StringUtils.isBlank(filename)) {
			try {
				reader = new PdfReader(bytes);
			} catch (final Throwable e) { // NOSONAR squid:S1166
				isUnreadableEncryptor(e, filename);
				isCorrupt(e, filename);
				isPasswordEditProtected(e, filename);

				// default message
				LOGGER.info(MSG_PDF_FILE + filename + MSG_IS_UNREADABLE, e);
				throw new FileManagerException(MessageSeverity.ERROR, MessageKeysEnum.PDF_UNREADABLE.getKey(),
						MessageKeysEnum.PDF_UNREADABLE.getMessage(), filename,
						StringUtils.substringBefore(e.getMessage() == null ? "null" : e.getMessage(), "\n"));
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
// NOSONAR TODO Need to find a better way to do this - will have to wait for the rest of the capabilities to be coded
	protected final void isLocked(final PdfReader pdfReader, final String filename) throws FileManagerException {
		boolean islocked = false;
		MessageKeysEnum msg = MessageKeysEnum.PDF_LOCKED;

		try {
			islocked = pdfReader.isEncrypted() || pdfReader.is128Key() || pdfReader.isMetadataEncrypted();
		} catch (final Throwable e) { // NOSONAR - intentional
			msg = MessageKeysEnum.PDF_CONTENT_INVALID;
			islocked = true;
		}
		if (islocked) {
			LOGGER.debug(MSG_PDF_FILE + filename + " is encrypted.");
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
	protected final void isTampered(final PdfReader pdfReader, final String filename) throws FileManagerException {
		boolean istampered = false;
		MessageKeysEnum msg = MessageKeysEnum.PDF_TAMPERED;

		try {
			istampered = pdfReader.isTampered();
		} catch (final Throwable e) { // NOSONAR - intentional
			msg = MessageKeysEnum.PDF_CONTENT_INVALID;
			istampered = true;
		}
		if (istampered) {
			LOGGER.info("Signed PDF file " + filename + " has been tampered with.");
			throw new FileManagerException(MessageSeverity.ERROR, msg.getKey(), msg.getMessage(), filename);
		}
	}

	/**
	 * If exception message indicates a corrupt file, throws FileManagerException with PDF_UNREADABLE message.
	 *
	 * @param e the cause
	 * @param filename the filename associated with file
	 * @throws FileManagerException exception with PDF_UNREADABLE message
	 */
	protected void isCorrupt(final Throwable e, final String filename) throws FileManagerException {
		if (e.getMessage() != null && StringUtils.containsAny(e.getMessage(), THROWABLE_CORRUPT_1, THROWABLE_CORRUPT_2)) {
			LOGGER.info(MSG_PDF_FILE + filename + MSG_IS_UNREADABLE, e);
			throw new FileManagerException(MessageSeverity.ERROR, MessageKeysEnum.PDF_UNREADABLE.getKey(),
					MessageKeysEnum.PDF_UNREADABLE.getMessage(), filename, REASON_CORRUPT);
		}
	}

	/**
	 * If exception message indicates the file is password protected, throws FileManagerException with PDF_UNREADABLE message.
	 *
	 * @param e the cause
	 * @param filename the filename associated with file
	 * @throws FileManagerException exception with PDF_UNREADABLE message
	 */
	protected void isPasswordEditProtected(final Throwable e, final String filename) throws FileManagerException {
		if (e.getMessage() != null && e.getMessage().contains(THROWABLE_PW_EDIT)) {
			LOGGER.info(MSG_PDF_FILE + filename + MSG_IS_UNREADABLE, e);
			throw new FileManagerException(MessageSeverity.ERROR, MessageKeysEnum.PDF_UNREADABLE.getKey(),
					MessageKeysEnum.PDF_UNREADABLE.getMessage(), filename, REASON_PW_EDIT);
		}
	}

	/**
	 * If exception message indicates the file is encrypted, throws FileManagerException with PDF_UNREADABLE message.
	 *
	 * @param e the cause
	 * @param filename the filename associated with file
	 * @throws FileManagerException exception with PDF_UNREADABLE message
	 */
	protected void isUnreadableEncryptor(final Throwable e, final String filename) throws FileManagerException {
		if (e.getMessage() != null && e.getMessage().contains(THROWABLE_UNREADABLE_ENCRYPTOR)) {
			LOGGER.info(MSG_PDF_FILE + filename + MSG_IS_UNREADABLE, e);
			throw new FileManagerException(MessageSeverity.ERROR, MessageKeysEnum.PDF_UNREADABLE.getKey(),
					MessageKeysEnum.PDF_UNREADABLE.getMessage(), filename, REASON_UNREADABLE_ENCRYPTOR);
		}
	}

}
