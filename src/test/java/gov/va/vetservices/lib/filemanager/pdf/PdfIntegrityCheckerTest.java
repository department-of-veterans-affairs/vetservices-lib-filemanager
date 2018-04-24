package gov.va.vetservices.lib.filemanager.pdf;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

	Path goodPdfPath = Paths.get("files/application/pdf/IS_Regular.pdf");
	Path lockedPdfPath = Paths.get("files/application/pdf/BAD_Locked.pdf");

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testNewPdfReader() {
		PdfReader pdfReader = null;
		try {
			byte[] lockedBytes = readFile(goodPdfPath);

			pdfReader = PdfIntegrityChecker.newPdfReader(lockedBytes, goodPdfPath.getFileName().toString());
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
	public final void testIsLocked() {

		PdfReader pdfReader = null;
		try {
			byte[] lockedBytes = readFile(lockedPdfPath);

			pdfReader = PdfIntegrityChecker.newPdfReader(lockedBytes, lockedPdfPath.getFileName().toString());
			assertNotNull(pdfReader);

			PdfIntegrityChecker.isLocked(pdfReader, lockedPdfPath.getFileName().toString());
			fail("isLocked() should have thrown exception.");

		} catch (IOException e) {
			assertTrue(e.getClass().getSimpleName().equals("InvalidPdfException"));
		} catch (FileManagerException e) {
			e.printStackTrace();
			assertTrue(MessageKeys.PDF_LOCKED.getKey().equals(e.getKey()) || MessageKeys.PDF_UNREADABLE.getKey().equals(e.getKey()));
		} finally {
			if (pdfReader != null) {
				pdfReader.close();
			}
		}
	}

}
