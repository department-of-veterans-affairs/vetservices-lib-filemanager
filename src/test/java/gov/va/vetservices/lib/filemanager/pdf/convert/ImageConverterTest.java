package gov.va.vetservices.lib.filemanager.pdf.convert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.file.Paths;

import javax.activation.MimeType;

import org.junit.Before;
import org.junit.Test;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.exception.PdfConverterException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
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

	@Test
	public final void testDoThrowException() {
		IllegalArgumentException exception = new IllegalArgumentException("test exception");
		MessageKeysEnum messageKey = MessageKeysEnum.PDF_CONVERSION_PROCESSING;

		try {
			imageConverter.doThrowException(exception, "filename.txt");
			fail("Should have thrown an exception.");

		} catch (PdfConverterException e) {
			assertNotNull(e);
			assertTrue(e.getClass().equals(PdfConverterException.class));
			assertTrue(messageKey.getKey().equals(e.getKey()));
			assertTrue(MessageSeverity.ERROR.equals(e.getMessageSeverity()));
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getClass().equals(exception.getClass()));
		}
	}

	@Test
	public final void testDoClose() {
		Document mockPdfDocument = mock(Document.class);
		PdfWriter mockPdfWriter = mock(PdfWriter.class);

		doNothing().when(mockPdfDocument).close();
		doNothing().when(mockPdfWriter).close();
		imageConverter.doClose(mockPdfDocument, mockPdfWriter);

		doThrow(new IllegalArgumentException("test exception")).when(mockPdfDocument).close();
		doThrow(new IllegalArgumentException("test exception")).when(mockPdfWriter).close();
		imageConverter.doClose(mockPdfDocument, mockPdfWriter);

		imageConverter.doClose(null, null);
	}
}
