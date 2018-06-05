package gov.va.vetservices.lib.filemanager.pdf.itext;

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

public class ItextUtilsTest extends AbstractFileHandler {

	private static final Path FILE_PATH = Paths.get("files/application/pdf/IS_text-doc.pdf");

	@Test
	public final void testItextUtils() {
		try {
			new ItextUtils();
			fail("Should have thrown exception");
		} catch (final Throwable e) {
			assertNotNull(e);
			assertTrue(IllegalAccessError.class.isAssignableFrom(e.getClass()));
		}
	}

	@Test
	public final void testGetPdfReader() {
		try {
			ItextUtils.getPdfReader(super.readFile(FILE_PATH));
		} catch (FileManagerException | IOException e) {
			e.printStackTrace();
			fail("Should not have caused eception");
		}

		try {
			ItextUtils.getPdfReader(new byte[] { 0 });
			fail("Should have thrown eception");
		} catch (final Exception e) {
			assertNotNull(e);
			assertTrue(FileManagerException.class.isAssignableFrom(e.getClass()));
			assertTrue(MessageKeysEnum.UNEXPECTED_ERROR.getKey().equals(((FileManagerException) e).getKey()));
		}
	}

	@Test
	public final void testGetPdfWriter() {
		assertNotNull(ItextUtils.getPdfWriter());
	}

	@Test
	public final void testGetStampingProperties() {
		final PdfDocumentOptions options = new PdfDocumentOptions();
		assertNotNull(ItextUtils.getStampingProperties(options));
		assertNotNull(ItextUtils.getStampingProperties(null));
	}

}
