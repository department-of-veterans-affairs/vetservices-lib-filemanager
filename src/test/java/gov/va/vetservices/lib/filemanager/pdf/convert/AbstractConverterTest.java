package gov.va.vetservices.lib.filemanager.pdf.convert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileParts;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.exception.PdfConverterException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;

@RunWith(MockitoJUnitRunner.class)
public class AbstractConverterTest {

	private static final byte[] TEXT_BYTES = "This is a test.".getBytes();

	class TestConverter extends AbstractConverter {

		@Override
		public byte[] getPdf(byte[] bytes, FileParts parts) throws FileManagerException {
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
	public final void testThrowException() {
		Exception exception = new Exception();
		MessageKeys messageKey = MessageKeys.CONVERSION_PROCESSING;

		try {
			testConverter.throwException(exception, "filename.txt");
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
	public final void testClose() {
		ByteArrayOutputStream bytesOutputStream = null;
		Document pdfDocument = null;
		PdfWriter pdfWriter = null;

		try {
			pdfDocument = new Document();
			bytesOutputStream = new ByteArrayOutputStream();
			pdfWriter = PdfWriter.getInstance(pdfDocument, bytesOutputStream);

			testConverter.initializePdfDocument(pdfDocument);

			testConverter.close(pdfDocument, pdfWriter);
			assertNotNull(pdfDocument);
			assertTrue(!pdfDocument.isOpen());
			assertNotNull(pdfWriter);

			byte[] pdfBytes = bytesOutputStream.toByteArray();
			assertNotNull(pdfBytes);
			assertTrue(pdfBytes.length > 0);

		} catch (Throwable e) {
			e.printStackTrace();
			fail("Unexcpected exception.");
		}
	}

	@Test
	public final void testClose_Bad() throws DocumentException {
		ByteArrayOutputStream bytesOutputStream = null;
		Document pdfDocument = null;
		PdfWriter pdfWriter = null;

//		try {
		// set up
		pdfDocument = new Document();
		bytesOutputStream = new ByteArrayOutputStream();
		pdfWriter = PdfWriter.getInstance(pdfDocument, bytesOutputStream);

		testConverter.initializePdfDocument(pdfDocument);

		// throw on document
		Document mockDocument = mock(Document.class);
		Mockito.doThrow(new RuntimeException("Testing.")).when(mockDocument).close();
		testConverter.close(mockDocument, pdfWriter);
		// clean up
		if (pdfDocument != null) {
			pdfDocument.close();
		}

		// set up again
		pdfDocument = new Document();
		bytesOutputStream = new ByteArrayOutputStream();
		pdfWriter = PdfWriter.getInstance(pdfDocument, bytesOutputStream);

		testConverter.initializePdfDocument(pdfDocument);

		// throw on pdf writer
		PdfWriter mockWriter = mock(PdfWriter.class);
		Mockito.doThrow(new RuntimeException("Testing.")).when(mockWriter).close();
		testConverter.close(pdfDocument, mockWriter);
		// clean up
		if (pdfWriter != null) {
			pdfWriter.close();
		}

//		} catch (Throwable e) {
//			e.printStackTrace();
//			fail("Unexcpected exception.");
//		}

	}

}
