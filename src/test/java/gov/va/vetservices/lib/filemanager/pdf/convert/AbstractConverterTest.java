package gov.va.vetservices.lib.filemanager.pdf.convert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.exception.PdfConverterException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;

@RunWith(MockitoJUnitRunner.class)
public class AbstractConverterTest {

	private static final byte[] TEXT_BYTES = "This is a test.".getBytes();

	class TestConverter extends AbstractConverter {

		@Override
		public byte[] getPdf(byte[] bytes, FilePartsDto parts) throws FileManagerException {
			// don't care about this, just the methods in the abstract class
			return null;
		}

	}

	private TestConverter testConverter;

	@Before
	public void setUp() throws Exception {
		testConverter = new TestConverter();
		assertNotNull(testConverter);
	}

	@Test
	public final void testGetPdf() {
		try {
			assertNull(testConverter.getPdf(null, null));
		} catch (FileManagerException e) {
			e.printStackTrace();
			fail("Sould not throw exception");
		}
	}

	@Test
	public final void testInitializePdfDocument() {
		Document pdfDocument = null;
		try {
			pdfDocument = new Document();

			testConverter.initializePdfDocument(pdfDocument);
			assertTrue(pdfDocument.isOpen());

		} catch (DocumentException e) {
			e.printStackTrace();
			fail("Something went unexpectedly wrong while initializing iText Document.");
		} finally {
			if ((pdfDocument != null) && pdfDocument.isOpen()) {
				pdfDocument.close();
			}
		}
	}

	@Test
	public final void testGetPlainTextReader() {
		byte[] bytes = TEXT_BYTES;
		BufferedReader reader = null;
		try {
			reader = testConverter.getPlainTextReader(bytes);
			assertNotNull(reader);

			String line = reader.readLine();
			assertTrue(!StringUtils.isBlank(line));

		} catch (Throwable e) {
			e.printStackTrace();
			fail("Could not get buffered reader from byte array.");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	@Test
	public final void testDoThrowException() {
		IllegalArgumentException exception = new IllegalArgumentException("test exception");
		MessageKeysEnum messageKey = MessageKeysEnum.PDF_CONVERSION_PROCESSING;

		try {
			testConverter.doThrowException(exception, "filename.txt");
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
		testConverter.doClose(mockPdfDocument, mockPdfWriter);

		doThrow(new IllegalArgumentException("test exception")).when(mockPdfDocument).close();
		doThrow(new IllegalArgumentException("test exception")).when(mockPdfWriter).close();
		testConverter.doClose(mockPdfDocument, mockPdfWriter);

		testConverter.doClose(null, null);
	}

}
