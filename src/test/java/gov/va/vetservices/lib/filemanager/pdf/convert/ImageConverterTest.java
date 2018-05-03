package gov.va.vetservices.lib.filemanager.pdf.convert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;

import javax.activation.MimeType;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.mime.MimeTypeDetector;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class ImageConverterTest extends AbstractFileHandler {

	private static final String FILE_PATH = "files/image/png/IS_Transparent.png";

	private ImageConverter imageConverter;
	private MimeTypeDetector mimetypeDetector;

	@Before
	public void setUp() throws Exception {
		imageConverter = new ImageConverter();
		mimetypeDetector = new MimeTypeDetector();
		assertNotNull(imageConverter);
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

		FilePartsDto parts = FileManagerUtils.getFileParts(Paths.get(FILE_PATH).toFile().getName());

		try {
			byte[] pdf = imageConverter.getPdf(bytes, parts);
			assertNotNull(pdf);
			assertTrue(pdf.length > 0);

			FilePartsDto pdfparts = FileManagerUtils.getFileParts("IS_Transparent.pdf");
			MimeType pdftype = mimetypeDetector.detectMimeType(pdf, pdfparts);
			assertTrue(ConvertibleTypesEnum.PDF.getMimeType().match(pdftype));

		} catch (FileManagerException e) {
			e.printStackTrace();
			fail("Unexpected exception.");
		}
	}

}
