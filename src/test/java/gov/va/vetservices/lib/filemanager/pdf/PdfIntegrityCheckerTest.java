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

}
