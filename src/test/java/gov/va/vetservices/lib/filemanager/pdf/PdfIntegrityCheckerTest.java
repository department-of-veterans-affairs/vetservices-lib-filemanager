package gov.va.vetservices.lib.filemanager.pdf;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

public class PdfIntegrityCheckerTest extends AbstractFileHandler {

	private final PdfIntegrityChecker pdfIntegrityChecker = new PdfIntegrityChecker();

	private static final String BASE_PATH = "files/application/pdf/";
	private final Path goodPdfPath = Paths.get(BASE_PATH + "IS_text-doc.pdf");
	private final Path corruptPdfPath = Paths.get(BASE_PATH + "BAD_corrupted-pdf.pdf");
	private final Path encryptedPdfPath = Paths.get(BASE_PATH + "NOT_encrypted_cert_toopen.pdf");
	private final Path protectedPdfPath = Paths.get(BASE_PATH + "NOT_restrict-editing.pdf");
	private final Path signedPdfPath = Paths.get(BASE_PATH + "IS_NOT_signed.pdf");

	@Test
	public final void testPdfIntegrityChecker() {
		try {
			final PdfIntegrityChecker test = new PdfIntegrityChecker();
			assertNotNull(test);
		} catch (final Exception e) {
			e.printStackTrace();
			fail("Unexpected exceptin from Constructor. " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}

	@Test
	public final void testIsReadable() {
		// happy
		try {
			final byte[] bytes = super.readFile(goodPdfPath);

			pdfIntegrityChecker.isReadable(bytes, goodPdfPath.getFileName().toString());

		} catch (final IOException e) {
			e.printStackTrace();
			fail("Unexpected error " + e.getMessage());
		} catch (final FileManagerException e) {
			e.printStackTrace();
			fail("Unexpected error using good path and good bytes " + e.getMessage());
		}
	}

	@Test
	public final void testIsReadable_encrypted() {
		final String messageKey = MessageKeysEnum.PDF_UNREADABLE.getKey();
		final String messageWord = "encrypted";
		try {
			final byte[] bytes = super.readFile(encryptedPdfPath);

			pdfIntegrityChecker.isReadable(bytes, encryptedPdfPath.getFileName().toString());
			fail("Should have thrown FileManagerException with key '" + messageKey + "'.");

		} catch (final IOException e) {
			e.printStackTrace();
			fail("Unexpected error " + e.getMessage());
		} catch (final FileManagerException e) {
			assertTrue(messageKey.equals(e.getKey()));
			assertTrue(e.getMessage().contains(messageWord));
		}
	}

	@Test
	public final void testIsReadable_corrupt() {
		final String messageKey = MessageKeysEnum.PDF_UNREADABLE.getKey();
		final String messageWord = "corrupt";
		try {
			final byte[] bytes = super.readFile(corruptPdfPath);

			pdfIntegrityChecker.isReadable(bytes, corruptPdfPath.getFileName().toString());
			fail("Should have thrown FileManagerException with key '" + messageKey + "'.");

		} catch (final IOException e) {
			e.printStackTrace();
			fail("Unexpected error " + e.getMessage());
		} catch (final FileManagerException e) {
			assertTrue(messageKey.equals(e.getKey()));
			assertTrue(e.getMessage().contains(messageWord));
		}
	}

	@Test
	public final void testIsReadable_protected() {
		final String messageKey = MessageKeysEnum.PDF_UNREADABLE.getKey();
		final String messageWord = "password";
		try {
			final byte[] bytes = super.readFile(protectedPdfPath);

			pdfIntegrityChecker.isReadable(bytes, protectedPdfPath.getFileName().toString());
			fail("Should have thrown FileManagerException with key '" + messageKey + "'.");

		} catch (final IOException e) {
			e.printStackTrace();
			fail("Unexpected error " + e.getMessage());
		} catch (final FileManagerException e) {
			assertTrue(messageKey.equals(e.getKey()));
			assertTrue(e.getMessage().contains(messageWord));
		}
	}

	@Test
	public final void testIsReadable_signed() {
		final String messageKey = MessageKeysEnum.PDF_UNREADABLE.getKey();
		final String messageWord = "signed";
		try {
			final byte[] bytes = super.readFile(signedPdfPath);

			pdfIntegrityChecker.isReadable(bytes, signedPdfPath.getFileName().toString());
			fail("Should have thrown FileManagerException with key '" + messageKey + "'.");

		} catch (final IOException e) {
			e.printStackTrace();
			fail("Unexpected error " + e.getMessage());
		} catch (final FileManagerException e) {
			assertTrue(messageKey.equals(e.getKey()));
			assertTrue(e.getMessage().contains(messageWord));
		}
	}

}
