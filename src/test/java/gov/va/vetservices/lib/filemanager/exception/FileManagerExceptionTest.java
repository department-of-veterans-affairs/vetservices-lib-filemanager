package gov.va.vetservices.lib.filemanager.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;

import org.junit.Test;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;

public class FileManagerExceptionTest {

	private static final String FILENAME = "test.txt";

	FileManagerException fme;

	@Test
	public final void testFileManagerExceptionMessageSeverityStringStringStringArray() {
		fme = new FileManagerException(MessageSeverity.ERROR, MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getKey(),
				MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getMessage(), FILENAME);
		assertNotNull(fme);
		assertEquals(MessageSeverity.ERROR, fme.getMessageSeverity());
		assertEquals(MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getKey(), fme.getKey());
		assertEquals(MessageFormat.format(MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getMessage(), FILENAME), fme.getMessage());
		assertNull(fme.getCause());
	}

	@Test
	public final void testFileManagerExceptionThrowableMessageSeverityStringStringStringArray() {
		IllegalArgumentException iae = new IllegalArgumentException();
		fme = new FileManagerException(iae, MessageSeverity.ERROR, MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getKey(),
				MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getMessage(), FILENAME);
		assertNotNull(fme);
		assertEquals(MessageSeverity.ERROR, fme.getMessageSeverity());
		assertEquals(MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getKey(), fme.getKey());
		assertEquals(MessageFormat.format(MessageKeys.FILE_CONTENT_NOT_CONVERTIBLE.getMessage(), FILENAME), fme.getMessage());
		assertTrue(iae.getClass().equals(fme.getCause().getClass()));
	}
}
