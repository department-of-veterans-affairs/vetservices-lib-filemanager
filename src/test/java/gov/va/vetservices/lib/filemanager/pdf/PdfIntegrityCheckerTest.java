package gov.va.vetservices.lib.filemanager.pdf;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS,
		AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class PdfIntegrityCheckerTest extends AbstractFileHandler {

	@Autowired
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
		final String messageKey = LibFileManagerMessageKeys.PDF_UNREADABLE;

		try {
			final byte[] bytes = super.readFile(encryptedPdfPath);

			pdfIntegrityChecker.isReadable(bytes, encryptedPdfPath.getFileName().toString());
			fail("Should have thrown FileManagerException with key '" + messageKey + "'.");

		} catch (final IOException e) {
			e.printStackTrace();
			fail("Unexpected error " + e.getMessage());
		} catch (final FileManagerException e) {
			assertTrue(messageKey.equals(e.getKey()));
			assertTrue(e.getMessage().contains(""));
		}
	}

	@Test
	public final void testIsReadable_corrupt() {
		final String messageKey = LibFileManagerMessageKeys.PDF_UNREADABLE;
	
		try {
			final byte[] bytes = super.readFile(corruptPdfPath);

			pdfIntegrityChecker.isReadable(bytes, corruptPdfPath.getFileName().toString());
			fail("Should have thrown FileManagerException with key '" + messageKey + "'.");

		} catch (final IOException e) {
			e.printStackTrace();
			fail("Unexpected error " + e.getMessage());
		} catch (final FileManagerException e) {
			assertTrue(messageKey.equals(e.getKey()));
			assertTrue(e.getMessage().contains(""));
		}
	}

	@Test
	public final void testIsReadable_protected() {
		final String messageKey = LibFileManagerMessageKeys.PDF_UNREADABLE;
		try {
			final byte[] bytes = super.readFile(protectedPdfPath);

			pdfIntegrityChecker.isReadable(bytes, protectedPdfPath.getFileName().toString());
			fail("Should have thrown FileManagerException with key '" + messageKey + "'.");

		} catch (final IOException e) {
			e.printStackTrace();
			fail("Unexpected error " + e.getMessage());
		} catch (final FileManagerException e) {
			assertTrue(messageKey.equals(e.getKey()));
			assertTrue(e.getMessage().contains(""));
		}
	}

	@Test
	public final void testIsReadable_signed() {
		final String messageKey = LibFileManagerMessageKeys.PDF_UNREADABLE;
		try {
			final byte[] bytes = super.readFile(signedPdfPath);

			pdfIntegrityChecker.isReadable(bytes, signedPdfPath.getFileName().toString());
			fail("Should have thrown FileManagerException with key '" + messageKey + "'.");

		} catch (final IOException e) {
			e.printStackTrace();
			fail("Unexpected error " + e.getMessage());
		} catch (final FileManagerException e) {
			assertTrue(messageKey.equals(e.getKey()));
			assertTrue(e.getMessage().contains(""));
		}
	}

}
