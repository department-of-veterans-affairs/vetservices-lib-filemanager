package gov.va.vetservices.lib.filemanager.pdf;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.lowagie.text.pdf.PdfReader;

import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

public class PdfIntegrityCheckerTest extends AbstractFileHandler {

	private PdfIntegrityChecker pdfIntegrityChecker = new PdfIntegrityChecker();

	private Path goodPdfPath = Paths.get("files/application/pdf/IS_text-doc.pdf");
	private Path corruptPdfPath = Paths.get("files/application/pdf/BAD_corrupted-pdf.pdf");
	private Path tamperedPdfPath = Paths.get("files/application/pdf/IS_signed-tampered.pdf");

	@Test
	public final void testPdfIntegrityChecker() {
		try {
			PdfIntegrityChecker test = new PdfIntegrityChecker();
			assertNotNull(test);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exceptin from Constructor. " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}

	@Test
	public final void testNewPdfReader() {
		PdfReader pdfReader = null;
		try {
			byte[] goodBytes = readFile(goodPdfPath);

			pdfReader = pdfIntegrityChecker.newPdfReader(goodBytes, goodPdfPath.getFileName().toString());
			assertNotNull(pdfReader);

		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected error " + e.getMessage());
		} catch (FileManagerException e) {
			e.printStackTrace();
			assertTrue(MessageKeysEnum.PDF_UNREADABLE.getKey().equals(e.getKey()));
		} finally {
			if (pdfReader != null) {
				pdfReader.close();
			}
		}
	}

	@Test
	public final void testNewPdfReader_Bad() {
		// null bytes
		PdfReader pdfReader = null;
		try {
			pdfReader = pdfIntegrityChecker.newPdfReader(null, "empty-bytes");
			assertNull(pdfReader);

		} catch (FileManagerException e) {
			e.printStackTrace();
			assertTrue(MessageKeysEnum.PDF_UNREADABLE.getKey().equals(e.getKey()));
		} finally {
			if (pdfReader != null) {
				pdfReader.close();
			}
		}

		// empty bytes
		pdfReader = null;
		try {
			pdfReader = pdfIntegrityChecker.newPdfReader(new byte[] {}, "empty-bytes");
			assertNull(pdfReader);

		} catch (FileManagerException e) {
			e.printStackTrace();
			assertTrue(MessageKeysEnum.PDF_UNREADABLE.getKey().equals(e.getKey()));
		} finally {
			if (pdfReader != null) {
				pdfReader.close();
			}
		}

		// bad bytes
		pdfReader = null;
		try {
			pdfReader = pdfIntegrityChecker.newPdfReader(new byte[] { 0 }, "bytes-is-char-zero");
			fail("pdfIntegirtyChecker should have thrown and exception.");

		} catch (FileManagerException e) {
			e.printStackTrace();
			assertTrue(MessageKeysEnum.PDF_UNREADABLE.getKey().equals(e.getKey()));
		} finally {
			if (pdfReader != null) {
				pdfReader.close();
			}
		}
	}

	// TODO need to find better method of testing non-editable status
	@Test
	public final void testIsLocked() {
		String filename = "test.pdf";
		PdfReader mock = mock(PdfReader.class);

		when(mock.isEncrypted()).thenReturn(true);
		try {
			pdfIntegrityChecker.isLocked(mock, filename);
			fail("Should have thrown exception");
		} catch (FileManagerException e) {
			assertNotNull(e);
			assertTrue(FileManagerException.class.isAssignableFrom(e.getClass()));
			assertTrue(MessageKeysEnum.PDF_LOCKED.getKey().equals(e.getKey()));
		}

		when(mock.isEncrypted()).thenReturn(false);
		when(mock.is128Key()).thenReturn(true);
		try {
			pdfIntegrityChecker.isLocked(mock, filename);
			fail("Should have thrown exception");
		} catch (FileManagerException e) {
			assertNotNull(e);
			assertTrue(FileManagerException.class.isAssignableFrom(e.getClass()));
			assertTrue(MessageKeysEnum.PDF_LOCKED.getKey().equals(e.getKey()));
		}

		when(mock.isEncrypted()).thenReturn(false);
		when(mock.is128Key()).thenReturn(false);
		when(mock.isMetadataEncrypted()).thenReturn(true);
		try {
			pdfIntegrityChecker.isLocked(mock, filename);
			fail("Should have thrown exception");
		} catch (FileManagerException e) {
			assertNotNull(e);
			assertTrue(FileManagerException.class.isAssignableFrom(e.getClass()));
			assertTrue(MessageKeysEnum.PDF_LOCKED.getKey().equals(e.getKey()));
		}

		when(mock.isEncrypted()).thenThrow(new IllegalArgumentException("test exception"));
		try {
			pdfIntegrityChecker.isLocked(mock, filename);
			fail("Should have thrown exception");
		} catch (FileManagerException e) {
			assertNotNull(e);
			assertTrue(FileManagerException.class.isAssignableFrom(e.getClass()));
			assertTrue(MessageKeysEnum.PDF_CONTENT_INVALID.getKey().equals(e.getKey()));
		}
	}

	@Test
	public final void testIsReadable() {
		boolean readable = false;
		try {
			byte[] goodBytes = readFile(goodPdfPath);

			readable = pdfIntegrityChecker.isReadable(goodBytes, goodPdfPath.getFileName().toString());
			assertTrue(readable);

		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected error " + e.getMessage());
		} catch (FileManagerException e) {
			e.printStackTrace();
			assertTrue(MessageKeysEnum.PDF_UNREADABLE.getKey().equals(e.getKey()));
		}
	}

	@Test
	public final void testIsReadable_Bad() {

		boolean readable = false;
		try {
			byte[] goodBytes = readFile(goodPdfPath);
			// mock null return from PdfReader
			PdfIntegrityChecker spiedChecker = spy(pdfIntegrityChecker);

			doReturn(null).when(spiedChecker).newPdfReader(any(), any());
			readable = spiedChecker.isReadable(goodBytes, goodPdfPath.getFileName().toString());
			fail("isReadable should have thrown an exception");

		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected error " + e.getMessage());
		} catch (FileManagerException e) {
			e.printStackTrace();
			assertTrue(!readable);
			assertTrue(MessageKeysEnum.PDF_UNREADABLE.getKey().equals(e.getKey()));
		}

	}

	@Test
	public final void testIsTampered() {
		String filename = "test.pdf";
		PdfReader mock = mock(PdfReader.class);

		when(mock.isTampered()).thenReturn(true);
		try {
			pdfIntegrityChecker.isTampered(mock, filename);
			fail("Should have thrown exception");
		} catch (FileManagerException e) {
			assertNotNull(e);
			assertTrue(FileManagerException.class.isAssignableFrom(e.getClass()));
			assertTrue(MessageKeysEnum.PDF_TAMPERED.getKey().equals(e.getKey()));
		}

		when(mock.isTampered()).thenThrow(new IllegalArgumentException("test exception"));
		try {
			pdfIntegrityChecker.isTampered(mock, filename);
			fail("Should have thrown exception");
		} catch (FileManagerException e) {
			assertNotNull(e);
			assertTrue(FileManagerException.class.isAssignableFrom(e.getClass()));
			assertTrue(MessageKeysEnum.PDF_CONTENT_INVALID.getKey().equals(e.getKey()));
		}
	}
}
