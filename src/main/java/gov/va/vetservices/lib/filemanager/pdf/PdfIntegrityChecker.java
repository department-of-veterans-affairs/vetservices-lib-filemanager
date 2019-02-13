package gov.va.vetservices.lib.filemanager.pdf;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itextpdf.signatures.SignatureUtil;

import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.ascent.framework.util.SanitizationUtil;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.mime.MimeTypeDetector;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.pdf.itext.LayoutAwarePdfDocument;
import gov.va.vetservices.lib.filemanager.util.MessageUtils;

/**
 * Checks the integrity of the PDF and confirms it can be read.
 * Use {@link MimeTypeDetector} to ensure the files used with this class are type {@code application/pdf}.
 *
 * @author aburkholder
 */
// Sonar raises a major violation for exceptions that are not logged - even though they are clearly logged.
@java.lang.SuppressWarnings("squid:S1166")
@Component(PdfIntegrityChecker.BEAN_NAME)
public class PdfIntegrityChecker {

	private static final AscentLogger LOGGER = AscentLoggerFactory.getLogger(PdfIntegrityChecker.class);

	public static final String BEAN_NAME = "pdfIntegrityChecker";

	/** Exception message fragments that indicate the file is corrupt */
	private static final String[] THROWABLE_CORRUPT = { "Rebuild failed", "cannot be read", "Trailer not found", "Header not found" };
	/** Replacement var for Messages that indicate the file is corrupt */
	private static final String REASON_CORRUPT = "corrupt";

	/** Exception message fragments that indicate the file is password protected */
	private static final String[] THROWABLE_PW_PROTECTED =
			{ "BadPasswordException", "signer information does not match", "not opened with owner password" };
	/** Replacement var for Messages that indicate the file is password protected */
	private static final String REASON_PW_PROTECTED = "password protected or restricted for edits";

	/** Exception message fragments that indicate the file is signed with a digital certificate */
	private static final String[] THROWABLE_SIGNED = { "signed with a digital certificate" };
	/** Replacement var for Messages that indicate the file is signed with a digital certificate */
	private static final String REASON_SIGNED = "has been signed with a digital certificate";

	/** Exception message fragments that indicate the file is encrypted with a certificate */
	private static final String[] THROWABLE_ENCRYPTED =
			{ "Certificate is not provided", "is encrypted with", "Bad type on operand stack" };
	/** Replacement var for Messages that indicate the file is encrypted with a certificate */
	private static final String REASON_ENCRYPTED = "encrypted with a certificate";

	private static final String MSG_PDF_FILE = "PDF file ";
	private static final String MSG_IS_UNREADABLE = " is unreadable.";

	/** Auto wire message utilities */
	@Autowired
	@Qualifier(MessageUtils.BEAN_NAME)
	private MessageUtils messageUtils;

	/*
	 * NOSONAR TODO find out how to reliably identify PDFs that are corrupt or otherwise cannot be opened in desktop software
	 * Evidence: Test PDFs that can still be processed: IS_signed-tampered-unopenable.pdf, IS_signed-tampered.pdf
	 */
	public void isReadable(final byte[] bytes, final String filename)  {
		LayoutAwarePdfDocument pdfDoc = null;
		try {
			pdfDoc = new LayoutAwarePdfDocument(bytes);

		} catch (final Exception e) {
			// errors that occur due just to opening and initializing the PDF
			throwIfCorrupt(e, filename);
			throwIfPasswordProtected(e, filename);
			throwIfEncrypted(e, filename);

			final String key = LibFileManagerMessageKeys.PDF_UNREADABLE;
			throw new FileManagerException(MessageSeverity.ERROR, key, messageUtils.returnMessage(key), filename, "corrupt");
		}

		// manually throw exception if the document is signed
		if (isSigned(pdfDoc)) {
			throwIfSigned(new IllegalArgumentException("Document is signed with a digital certificate."), filename);
		}
	}

	/**
	 * Determines if a PDF document has been signed with a digital certificate.
	 *
	 * @param pdfDoc the LayoutAwarePdfDocument
	 * @return boolean {@code true} if a signature is found
	 */
	protected boolean isSigned(final LayoutAwarePdfDocument pdfDoc) {
		boolean isSigned = false;

		final SignatureUtil sigutil = new SignatureUtil(pdfDoc);
		final List<String> signames = sigutil.getSignatureNames();
		isSigned = signames == null ? false : !signames.isEmpty();

		return isSigned;
	}

	/**
	 * If exception message indicates a corrupt file, throws FileManagerException with PDF_UNREADABLE message.
	 *
	 * @param e the cause
	 * @param filename the filename associated with file
	 * @throws FileManagerException exception with PDF_UNREADABLE message
	 */
	protected void throwIfCorrupt(final Throwable e, final String filename)  {
		if (e.getMessage() != null && StringUtils.containsAny(e.getMessage(), THROWABLE_CORRUPT)) {
			LOGGER.info(MSG_PDF_FILE + filename + MSG_IS_UNREADABLE, e);
			throw new FileManagerException(MessageSeverity.ERROR, LibFileManagerMessageKeys.PDF_UNREADABLE,
					messageUtils.returnMessage(LibFileManagerMessageKeys.PDF_UNREADABLE), filename, REASON_CORRUPT);
		}
	}

	/**
	 * If exception message indicates the file is password protected, throws FileManagerException with PDF_UNREADABLE message.
	 *
	 * @param e the cause
	 * @param filename the filename associated with file
	 * @throws FileManagerException exception with PDF_UNREADABLE message
	 */
	protected void throwIfPasswordProtected(final Throwable e, final String filename)  {
		if (e.getMessage() != null && StringUtils.containsAny(e.getMessage(), THROWABLE_PW_PROTECTED)) {
			final String safename = SanitizationUtil.safeFilename(filename);
			LOGGER.info(MSG_PDF_FILE + safename + THROWABLE_PW_PROTECTED, e);
			throw new FileManagerException(MessageSeverity.ERROR, LibFileManagerMessageKeys.PDF_UNREADABLE,
					messageUtils.returnMessage(LibFileManagerMessageKeys.PDF_UNREADABLE), safename, REASON_PW_PROTECTED);
		}
	}

	/**
	 * If exception message indicates the file is signed with a digital certificate, throws FileManagerException with PDF_UNREADABLE
	 * message.
	 *
	 * @param e the cause
	 * @param filename the filename associated with file
	 * @throws FileManagerException exception with PDF_UNREADABLE message
	 */
	protected void throwIfSigned(final Throwable e, final String filename)  {
		if (e.getMessage() != null && StringUtils.containsAny(e.getMessage(), THROWABLE_SIGNED)) {
			final String safename = SanitizationUtil.safeFilename(filename);
			LOGGER.info(MSG_PDF_FILE + safename + THROWABLE_SIGNED, e);
			throw new FileManagerException(MessageSeverity.ERROR, LibFileManagerMessageKeys.PDF_UNREADABLE,
					messageUtils.returnMessage(LibFileManagerMessageKeys.PDF_UNREADABLE), safename, REASON_SIGNED);
		}
	}

	/**
	 * If exception message indicates the file is encrypted, throws FileManagerException with PDF_UNREADABLE message.
	 *
	 * @param e the cause
	 * @param filename the filename associated with file
	 * @throws FileManagerException exception with PDF_UNREADABLE message
	 */
	protected void throwIfEncrypted(final Throwable e, final String filename)  {
		if (e.getMessage() != null && StringUtils.containsAny(e.getMessage(), THROWABLE_ENCRYPTED)) {
			final String safename = SanitizationUtil.safeFilename(filename);
			LOGGER.info(MSG_PDF_FILE + safename + MSG_IS_UNREADABLE, e);
			throw new FileManagerException(MessageSeverity.ERROR, LibFileManagerMessageKeys.PDF_UNREADABLE,
					messageUtils.returnMessage(LibFileManagerMessageKeys.PDF_UNREADABLE), safename, REASON_ENCRYPTED);
		}
	}

}
