package gov.va.vetservices.lib.filemanager.pdf.itext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

public class LayoutAwarePdfDocumentTest extends AbstractFileHandler {

	private static final Path GOOD_PDF_PATH = Paths.get("files/application/pdf/IS_text-doc.pdf");

	@Test
	public final void testLayoutAwarePdfDocument() {
		LayoutAwarePdfDocument doc = null;

		try {
			doc = new LayoutAwarePdfDocument();
			doc.addNewPage();
			assertNotNull(doc);
		} catch (final Exception e) {
			e.printStackTrace();
			fail("Should not have thrown exception");
		} finally {
			if (doc != null) {
				doc.close();
			}
		}

		try {
			doc = new LayoutAwarePdfDocument(super.readFile(GOOD_PDF_PATH));
			assertNotNull(doc);
		} catch (final IOException e) {
			e.printStackTrace();
			fail("Should not have thrown exception");
		} finally {
			if (doc != null) {
				doc.close();
			}
		}

		try {
			doc = new LayoutAwarePdfDocument(super.readFile(GOOD_PDF_PATH), new PdfDocumentOptions());
			assertNotNull(doc);
		} catch (final IOException e) {
			e.printStackTrace();
			fail("Should not have thrown exception");
		} finally {
			if (doc != null) {
				doc.close();
			}
		}
	}

	@Test
	public final void testGetters() {
		LayoutAwarePdfDocument doc = null;

		try {
			doc = new LayoutAwarePdfDocument(super.readFile(GOOD_PDF_PATH));

			final byte[] out = doc.closeAndGetOutput();
			assertNotNull(out);
			assertTrue(out.length > 15); // empty PDF is 15 bytes long

			assertNotNull(doc.getLayoutDocument());

			assertNotNull(doc.getDocumentOptions());

		} catch (final IOException e) {
			e.printStackTrace();
			fail("Should not have thrown exception");
		} finally {
			if (doc != null && !doc.isClosed()) {
				doc.close();
			}
		}
	}

	@Test
	public final void testCloseAndGetOutput() {
		LayoutAwarePdfDocument doc = null;

		// close with document still open
		try {
			doc = new LayoutAwarePdfDocument(super.readFile(GOOD_PDF_PATH));

			final byte[] out = doc.closeAndGetOutput();
			assertNotNull(out);
			assertTrue(out.length > 15); // empty PDF is 15 bytes long
		} catch (final IOException e) {
			e.printStackTrace();
			fail("Should not have thrown exception");
		} finally {
			if (doc != null && !doc.isClosed()) {
				doc.close();
			}
		}

		// close with document already closed
		try {
			doc = new LayoutAwarePdfDocument(super.readFile(GOOD_PDF_PATH));

			doc.close();
			final byte[] out = doc.closeAndGetOutput();
			assertNotNull(out);
			assertTrue(out.length > 15); // empty PDF is 15 bytes long
		} catch (final IOException e) {
			e.printStackTrace();
			fail("Should not have thrown exception");
		} finally {
			if (doc != null && !doc.isClosed()) {
				doc.close();
			}
		}
	}
}
