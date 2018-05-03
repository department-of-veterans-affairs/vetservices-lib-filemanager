package gov.va.vetservices.lib.filemanager.pdf.convert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;

import javax.activation.MimeType;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileParts;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.mime.MimeTypeDetector;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class TextConverterTest extends AbstractFileHandler {

	private static final String FILE_PATH = "files/text/plain/IS_ipsumlorem-diacriticals.txt";

	private TextConverter textConverter;
	private MimeTypeDetector mimetypeDetector;

	@Before
	public void setUp() throws Exception {
		textConverter = new TextConverter();
		mimetypeDetector = new MimeTypeDetector();
		assertNotNull(textConverter);
		assertNotNull(mimetypeDetector);
	}

	@Test
	public final void testGetPdf() {
		byte[] bytes = null;
		try {
			bytes = super.readFile(Paths.get(FILE_PATH));
		} catch (IOException e1) {
			e1.printStackTrace();
			fail("Unexpected exception.");
		}

		FileParts parts = FileManagerUtils.getFileParts(Paths.get(FILE_PATH).toFile().getName());

		try {
			byte[] pdf = textConverter.getPdf(bytes, parts);
			assertNotNull(pdf);
			assertTrue(pdf.length > 0);

			FileParts pdfparts = FileManagerUtils.getFileParts("ipsumlorem-diacriticals.pdf");
			MimeType pdftype = mimetypeDetector.detectMimeType(pdf, pdfparts);
			assertTrue(ConvertibleTypesEnum.PDF.getMimeType().match(pdftype));

		} catch (FileManagerException e) {
			e.printStackTrace();
			fail("Unexpected exception.");
		}
	}

}
