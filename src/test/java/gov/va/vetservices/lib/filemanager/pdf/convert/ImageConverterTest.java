package gov.va.vetservices.lib.filemanager.pdf.convert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;

import javax.activation.MimeType;

import org.junit.Before;
import org.junit.Test;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.exception.PdfConverterException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.mime.MimeTypeDetector;
import gov.va.vetservices.lib.filemanager.pdf.itext.LayoutAwarePdfDocument;
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
		} catch (final IOException e1) {
			e1.printStackTrace();
			fail("Unexpected exception.");
		}

		final FilePartsDto parts = FileManagerUtils.getFileParts(Paths.get(FILE_PATH).toFile().getName());

		try {
			final byte[] pdf = imageConverter.getPdf(bytes, parts);
			assertNotNull(pdf);
			assertTrue(pdf.length > 0);

			final FilePartsDto pdfparts = FileManagerUtils.getFileParts("IS_Transparent.pdf");
			final MimeType pdftype = mimetypeDetector.detectMimeType(pdf, pdfparts);
			assertTrue(ConvertibleTypesEnum.PDF.getMimeType().match(pdftype));

		} catch (final FileManagerException e) {
			e.printStackTrace();
			fail("Unexpected exception.");
		}
	}

	@Test
	public final void testDoThrowException() {
		final IllegalArgumentException exception = new IllegalArgumentException("test exception");
		final MessageKeysEnum messageKey = MessageKeysEnum.PDF_CONVERSION_PROCESSING;

		try {
			imageConverter.doThrowException(exception, "filename.txt");
			fail("Should have thrown an exception.");

		} catch (final PdfConverterException e) {
			assertNotNull(e);
			assertTrue(e.getClass().equals(PdfConverterException.class));
			assertTrue(messageKey.getKey().equals(e.getKey()));
			assertTrue(MessageSeverity.ERROR.equals(e.getMessageSeverity()));
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getClass().equals(exception.getClass()));
		}
	}

	// manual close - as opposed to normal closing via LayoutAwarePdfDocument.closeAndGetOutput()
	@Test
	public final void testSuperClose() {
		final FilePartsDto parts = FileManagerUtils.getFileParts(Paths.get(FILE_PATH).toFile().getName());
		final LayoutAwarePdfDocument pdfDocument = new LayoutAwarePdfDocument();

		// unclosable document
		try {
			imageConverter.closeDocument(pdfDocument, parts);
			fail("Should have thrown 'Error: Document has no pages'");
		} catch (final PdfConverterException e) {
			assertTrue(e.getMessage().contains("internal processing issue"));
		}

		// closable document
		byte[] bytes = null;
		try {
			bytes = super.readFile(Paths.get(FILE_PATH));
		} catch (final IOException e1) {
			e1.printStackTrace();
			fail("Unexpected exception.");
		}
		final Image image = new Image(ImageDataFactory.create(bytes));
		pdfDocument.getLayoutDocument().add(image);
		try {
			imageConverter.closeDocument(pdfDocument, parts);
			assertTrue(pdfDocument.isClosed());
		} catch (final PdfConverterException e) {
			fail("Should not have thrown exception");
		}

	}
}
