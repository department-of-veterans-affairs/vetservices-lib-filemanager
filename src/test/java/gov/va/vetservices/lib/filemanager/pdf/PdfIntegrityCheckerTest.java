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

	private final Path goodPdfPath = Paths.get("files/application/pdf/IS_text-doc.pdf");
	private final Path corruptPdfPath = Paths.get("files/application/pdf/BAD_corrupted-pdf.pdf");
	private final Path tamperedPdfPath = Paths.get("files/application/pdf/IS_signed-tampered.pdf");

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

	// TODO need to find better method of testing non-editable status
//	@Test
//	public final void testIsLocked() {
//		final String filename = "test.pdf";
//		final PdfReader mock = mock(PdfReader.class);
//
//		when(mock.isEncrypted()).thenReturn(true);
//		try {
//			pdfIntegrityChecker.isLocked(mock, filename);
//			fail("Should have thrown exception");
//		} catch (final FileManagerException e) {
//			assertNotNull(e);
//			assertTrue(FileManagerException.class.isAssignableFrom(e.getClass()));
//			assertTrue(MessageKeysEnum.PDF_LOCKED.getKey().equals(e.getKey()));
//		}
//
//		when(mock.isEncrypted()).thenReturn(false);
//		try {
//			pdfIntegrityChecker.isLocked(mock, filename);
////			fail("Should have thrown exception"); //TODO need to rewrite all the tests
//		} catch (final FileManagerException e) {
//			assertNotNull(e);
//			assertTrue(FileManagerException.class.isAssignableFrom(e.getClass()));
//			assertTrue(MessageKeysEnum.PDF_LOCKED.getKey().equals(e.getKey()));
//		}
//
//		when(mock.isEncrypted()).thenReturn(false);
//		try {
//			pdfIntegrityChecker.isLocked(mock, filename);
////			fail("Should have thrown exception"); //TODO need to rewrite all the tests
//		} catch (final FileManagerException e) {
//			assertNotNull(e);
//			assertTrue(FileManagerException.class.isAssignableFrom(e.getClass()));
//			assertTrue(MessageKeysEnum.PDF_LOCKED.getKey().equals(e.getKey()));
//		}
//
//		when(mock.isEncrypted()).thenThrow(new IllegalArgumentException("test exception"));
//		try {
//			pdfIntegrityChecker.isLocked(mock, filename);
//			fail("Should have thrown exception");
//		} catch (final FileManagerException e) {
//			assertNotNull(e);
//			assertTrue(FileManagerException.class.isAssignableFrom(e.getClass()));
//			assertTrue(MessageKeysEnum.PDF_CONTENT_INVALID.getKey().equals(e.getKey()));
//		}
//	}

	@Test
	public final void testIsReadable() {
		boolean readable = false;
		try {
			final byte[] goodBytes = super.readFile(goodPdfPath);

			readable = pdfIntegrityChecker.isReadable(goodBytes, goodPdfPath.getFileName().toString());
			assertTrue(readable);

		} catch (final IOException e) {
			e.printStackTrace();
			fail("Unexpected error " + e.getMessage());
		} catch (final FileManagerException e) {
			e.printStackTrace();
			assertTrue(MessageKeysEnum.PDF_UNREADABLE.getKey().equals(e.getKey()));
		}
	}

//	@Test
//	public final void testIsReadable_Bad() {
//
//		boolean readable = false;
//		try {
//			final byte[] goodBytes = readFile(goodPdfPath);
//			// mock null return from PdfReader
//			final PdfIntegrityChecker spiedChecker = spy(pdfIntegrityChecker);
//
//			doReturn(null).when(spiedChecker).newPdfReader(any(), any());
//			readable = spiedChecker.isReadable(goodBytes, goodPdfPath.getFileName().toString());
//			fail("isReadable should have thrown an exception");
//
//		} catch (final IOException e) {
//			e.printStackTrace();
//			fail("Unexpected error " + e.getMessage());
//		} catch (final FileManagerException e) {
//			e.printStackTrace();
//			assertTrue(!readable);
//			assertTrue(MessageKeysEnum.PDF_UNREADABLE.getKey().equals(e.getKey()));
//		}
//
//	}

}
