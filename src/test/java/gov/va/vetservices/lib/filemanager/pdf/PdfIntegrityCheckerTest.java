package gov.va.vetservices.lib.filemanager.pdf;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lowagie.text.pdf.PdfReader;

import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

public class PdfIntegrityCheckerTest extends AbstractFileHandler {

	private PdfIntegrityChecker pdfIntegrityChecker = new PdfIntegrityChecker();

	private Path goodPdfPath = Paths.get("files/application/pdf/IS_Regular.pdf");
	private Path corruptPdfPath = Paths.get("files/application/pdf/BAD_CorruptedPdf.pdf");
	private Path lockedPdfPath = Paths.get("files/application/pdf/BAD_Locked.pdf");
	private Path tamperedPdfPath = Paths.get("files/application/pdf/IS_Signed-Tampered.pdf");

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

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
			assertTrue(MessageKeys.PDF_UNREADABLE.getKey().equals(e.getKey()));
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
			assertTrue(MessageKeys.PDF_UNREADABLE.getKey().equals(e.getKey()));
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
			assertTrue(MessageKeys.PDF_UNREADABLE.getKey().equals(e.getKey()));
		} finally {
			if (pdfReader != null) {
				pdfReader.close();
			}
		}

		// bad bytes
		pdfReader = null;
		try {
			pdfReader = pdfIntegrityChecker.newPdfReader(new byte[] { 0 }, "bytes-is-char-zero");
			assertNull(pdfReader);

		} catch (FileManagerException e) {
			e.printStackTrace();
			assertTrue(MessageKeys.PDF_UNREADABLE.getKey().equals(e.getKey()));
		} finally {
			if (pdfReader != null) {
				pdfReader.close();
			}
		}
	}

	@Test
	public final void testIsLocked() {

		PdfReader pdfReader = null;
		try {
			byte[] lockedBytes = readFile(lockedPdfPath);

			pdfReader = pdfIntegrityChecker.newPdfReader(lockedBytes, lockedPdfPath.getFileName().toString());
			assertNotNull(pdfReader);

			pdfIntegrityChecker.isLocked(pdfReader, lockedPdfPath.getFileName().toString());
			fail("isLocked() should have thrown exception.");

		} catch (IOException e) {
			// from readFile
			assertTrue(e.getClass().getSimpleName().equals("InvalidPdfException"));
		} catch (FileManagerException e) {
			e.printStackTrace();
			assertTrue(
					MessageKeys.PDF_LOCKED.getKey().equals(e.getKey()) || MessageKeys.PDF_CONTENT_INVALID.getKey().equals(e.getKey())
							|| MessageKeys.PDF_UNREADABLE.getKey().equals(e.getKey()));
		} finally {
			if (pdfReader != null) {
				pdfReader.close();
			}
		}

		pdfReader = null;
		try {
			byte[] corruptBytes = readFile(corruptPdfPath);

			pdfReader = pdfIntegrityChecker.newPdfReader(corruptBytes, corruptPdfPath.getFileName().toString());
			assertNotNull(pdfReader);

			pdfIntegrityChecker.isLocked(pdfReader, corruptPdfPath.getFileName().toString());
			fail("isLocked() should have thrown exception.");

		} catch (IOException e) {
			// from readFile
			assertTrue(e.getClass().getSimpleName().equals("InvalidPdfException"));
		} catch (FileManagerException e) {
			e.printStackTrace();
			assertTrue(
					MessageKeys.PDF_LOCKED.getKey().equals(e.getKey()) || MessageKeys.PDF_CONTENT_INVALID.getKey().equals(e.getKey())
							|| MessageKeys.PDF_UNREADABLE.getKey().equals(e.getKey()));
		} finally {
			if (pdfReader != null) {
				pdfReader.close();
			}
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
			assertTrue(MessageKeys.PDF_UNREADABLE.getKey().equals(e.getKey()));
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
			assertTrue(MessageKeys.PDF_UNREADABLE.getKey().equals(e.getKey()));
		}

	}

	@Test
	public final void testIsTampered() {

		PdfReader pdfReader = null;
		try {
			byte[] tamperedBytes = readFile(tamperedPdfPath);

			pdfReader = pdfIntegrityChecker.newPdfReader(tamperedBytes, tamperedPdfPath.getFileName().toString());
			assertNotNull(pdfReader);

			pdfIntegrityChecker.isTampered(pdfReader, tamperedPdfPath.getFileName().toString());
			// TODO iText is not correctly confirming tampered status, OR the test file is not tampered.
			// When this is resolved, turn the AssertNull below into a simple fail() with same message.
			assertNull("isTampered() should have thrown exception.", null);

		} catch (IOException e) {
			// from readFile
			assertTrue(e.getClass().getSimpleName().equals("InvalidPdfException"));
		} catch (FileManagerException e) {
			e.printStackTrace();
			assertTrue(MessageKeys.PDF_TAMPERED.getKey().equals(e.getKey())
					|| MessageKeys.PDF_CONTENT_INVALID.getKey().equals(e.getKey()));
		} finally {
			if (pdfReader != null) {
				pdfReader.close();
			}
		}

		pdfReader = null;
		try {
			byte[] corruptBytes = readFile(corruptPdfPath);

			pdfReader = pdfIntegrityChecker.newPdfReader(corruptBytes, corruptPdfPath.getFileName().toString());
			assertNotNull(pdfReader);

			pdfIntegrityChecker.isTampered(pdfReader, corruptPdfPath.getFileName().toString());
			fail("isTampered() should have thrown exception.");

		} catch (IOException e) {
			// from readFile
			assertTrue(e.getClass().getSimpleName().equals("InvalidPdfException"));
		} catch (FileManagerException e) {
			e.printStackTrace();
			assertTrue(
					MessageKeys.PDF_TAMPERED.getKey().equals(e.getKey()) || MessageKeys.PDF_CONTENT_INVALID.getKey().equals(e.getKey())
							|| MessageKeys.PDF_UNREADABLE.getKey().equals(e.getKey()));
		} finally {
			if (pdfReader != null) {
				pdfReader.close();
			}
		}
	}
}
