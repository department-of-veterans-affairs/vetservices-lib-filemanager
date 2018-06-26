package gov.va.vetservices.lib.filemanager.pdf.itext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;

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
		// happy path
		try {
			final PdfReader reader = ItextUtils.getPdfReader(super.readFile(FILE_PATH));
			assertNotNull(reader);
			reader.close();
		} catch (FileManagerException | IOException e) {
			e.printStackTrace();
			fail("Should not have caused exception");
		}

		// sad - null byte array
		try {
			final PdfReader reader = ItextUtils.getPdfReader(null);
			assertNull(reader);
		} catch (final Exception e) {
			e.printStackTrace();
			fail("Should not have caused exception");
		}

		// sad - empty byte array
		try {
			final PdfReader reader = ItextUtils.getPdfReader(new byte[] {});
			assertNull(reader);
		} catch (final Exception e) {
			e.printStackTrace();
			fail("Should not have caused exception");
		}

		// sad - bad byte array
		try {
			ItextUtils.getPdfReader(new byte[] { 0 });
			fail("Should have thrown exception");
		} catch (final Exception e) {
			assertNotNull(e);
			assertTrue(FileManagerException.class.isAssignableFrom(e.getClass()));
			assertTrue(MessageKeysEnum.UNEXPECTED_ERROR.getKey().equals(((FileManagerException) e).getKey()));
		}
	}

	@Test
	public final void testGetPdfWriter() {
		assertNotNull(ItextUtils.getPdfWriter(new ByteArrayOutputStream()));
	}

	@Test
	public final void testGetStampingProperties() {
		// populated options
		final PdfDocumentOptions options = new PdfDocumentOptions();
		options.setStampingProperties(new StampingProperties().preserveEncryption());
		assertNotNull(ItextUtils.getStampingProperties(options));

		// null options
		assertNotNull(ItextUtils.getStampingProperties(null));

		// null options.getStamptingProperties()
		options.setStampingProperties(null);
		assertNotNull(ItextUtils.getStampingProperties(options));
	}

}
