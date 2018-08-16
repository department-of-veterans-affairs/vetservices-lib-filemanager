package gov.va.vetservices.lib.filemanager.pdf.convert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.nio.file.Paths;

import javax.activation.MimeType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.exception.PdfConverterException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.mime.MimeTypeDetector;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.pdf.itext.LayoutAwarePdfDocument;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS,
		AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class TextConverterTest extends AbstractFileHandler {

	private static final String FILE_PATH = "files/text/plain/IS_ipsumlorem-diacriticals.txt";

	private TextConverter textConverter;

	@Autowired
	MimeTypeDetector mimetypeDetector;

	@Before
	public void setUp() throws Exception {
		textConverter = new TextConverter();
		assertNotNull(textConverter);
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
			final byte[] pdf = textConverter.getPdf(bytes, parts);
			assertNotNull(pdf);
			assertTrue(pdf.length > 0);

			final FilePartsDto pdfparts = FileManagerUtils.getFileParts("ipsumlorem-diacriticals.pdf");
			final MimeType pdftype = mimetypeDetector.detectMimeType(pdf, pdfparts);
			assertTrue(ConvertibleTypesEnum.PDF.getMimeType().match(pdftype));

		} catch (final FileManagerException e) {
			e.printStackTrace();
			fail("Unexpected exception.");
		}
	}

	@Test
	public final void testGetPdf_Sad() {
		final byte[] bytes = { 64, 65, 66, 67 };
		final FilePartsDto parts = FileManagerUtils.getFileParts(Paths.get(FILE_PATH).toFile().getName());

		try {
			final byte[] pdf = textConverter.getPdf(bytes, parts);
			assertNotNull(pdf);
			assertTrue(pdf.length > 0);

			final FilePartsDto pdfparts = FileManagerUtils.getFileParts("ipsumlorem-diacriticals.pdf");
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
		final String messageKey = LibFileManagerMessageKeys.PDF_CONVERSION_PROCESSING;

		try {
			textConverter.doThrowException(exception, "filename.txt");
			fail("Should have thrown an exception.");

		} catch (final PdfConverterException e) {
			assertNotNull(e);
			assertTrue(e.getClass().equals(PdfConverterException.class));
			assertTrue(messageKey.equals(e.getKey()));
			assertTrue(MessageSeverity.ERROR.equals(e.getMessageSeverity()));
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getClass().equals(exception.getClass()));
		}
	}

	@Test
	public final void testCloseDocument() {
		// happy
		final FilePartsDto parts = new FilePartsDto();
		parts.setName("TransientTest");
		parts.setExtension("txt");

		final LayoutAwarePdfDocument pdfDocument = new LayoutAwarePdfDocument();
		pdfDocument.addNewPage();

		pdfDocument.getCatalog().getPdfObject().flush();

		try {
			textConverter.closeDocument(pdfDocument, parts);
			fail("Should have thrown exception");
		} catch (final Exception e) {
			assertNotNull(e);
			assertTrue(PdfConverterException.class.isAssignableFrom(e.getClass()));
		}

		// fail to close non-null doc
		final LayoutAwarePdfDocument spiedDoc = spy(pdfDocument);
		doThrow(Exception.class).when(spiedDoc).close();
		try {
			textConverter.closeDocument(spiedDoc, parts);
			fail("Should have thrown exception.");
		} catch (final Exception e) {
			assertTrue(PdfConverterException.class.isAssignableFrom(e.getClass()));
		}

	}
}
