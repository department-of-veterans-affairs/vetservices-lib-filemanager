package gov.va.vetservices.lib.filemanager.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;

import org.junit.Test;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;

public class PdfStamperExceptionTest {

	private static final String FILENAME = "test.txt";

	PdfStamperException pse;

	@Test
	public final void testPdfStamperExceptionMessageSeverityStringStringStringArray() {
		pse = new PdfStamperException(MessageSeverity.ERROR, MessageKeysEnum.FILE_CONTENT_NOT_CONVERTIBLE.getKey(),
				MessageKeysEnum.FILE_CONTENT_NOT_CONVERTIBLE.getMessage(), FILENAME);
		assertNotNull(pse);
		assertEquals(MessageSeverity.ERROR, pse.getMessageSeverity());
		assertEquals(MessageKeysEnum.FILE_CONTENT_NOT_CONVERTIBLE.getKey(), pse.getKey());
		assertEquals(MessageFormat.format(MessageKeysEnum.FILE_CONTENT_NOT_CONVERTIBLE.getMessage(), FILENAME), pse.getMessage());
		assertNull(pse.getCause());
	}

	@Test
	public final void testPdfStamperExceptionThrowableMessageSeverityStringStringStringArray() {
		IllegalArgumentException iae = new IllegalArgumentException();
		pse = new PdfStamperException(iae, MessageSeverity.ERROR, MessageKeysEnum.FILE_CONTENT_NOT_CONVERTIBLE.getKey(),
				MessageKeysEnum.FILE_CONTENT_NOT_CONVERTIBLE.getMessage(), FILENAME);
		assertNotNull(pse);
		assertEquals(MessageSeverity.ERROR, pse.getMessageSeverity());
		assertEquals(MessageKeysEnum.FILE_CONTENT_NOT_CONVERTIBLE.getKey(), pse.getKey());
		assertEquals(MessageFormat.format(MessageKeysEnum.FILE_CONTENT_NOT_CONVERTIBLE.getMessage(), FILENAME), pse.getMessage());
		assertTrue(iae.getClass().equals(pse.getCause().getClass()));
	}
}
