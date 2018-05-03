package gov.va.vetservices.lib.filemanager.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;

import org.junit.Test;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;

public class PdfConverterExceptionTest {

	private static final String FILENAME = "test.txt";

	PdfConverterException pse;

	@Test
	public final void testPdfConverterExceptionMessageSeverityStringStringStringArray() {
		pse = new PdfConverterException(MessageSeverity.ERROR, MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getKey(),
				MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getMessage(), FILENAME);
		assertNotNull(pse);
		assertEquals(MessageSeverity.ERROR, pse.getMessageSeverity());
		assertEquals(MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getKey(), pse.getKey());
		assertEquals(MessageFormat.format(MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getMessage(), FILENAME), pse.getMessage());
		assertNull(pse.getCause());
	}

	@Test
	public final void testPdfConverterExceptionThrowableMessageSeverityStringStringStringArray() {
		IllegalArgumentException iae = new IllegalArgumentException();
		pse = new PdfConverterException(iae, MessageSeverity.ERROR, MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getKey(),
				MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getMessage(), FILENAME);
		assertNotNull(pse);
		assertEquals(MessageSeverity.ERROR, pse.getMessageSeverity());
		assertEquals(MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getKey(), pse.getKey());
		assertEquals(MessageFormat.format(MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getMessage(), FILENAME), pse.getMessage());
		assertTrue(iae.getClass().equals(pse.getCause().getClass()));
	}

}
