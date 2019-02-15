package gov.va.vetservices.lib.filemanager.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.util.MessageUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS,
		AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@TestExecutionListeners(inheritListeners = false, listeners = { DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class PdfStamperExceptionTest {

	private static final String FILENAME = "test.txt";

	@Autowired
	MessageUtils messageUtils;

	PdfStamperException pse;

	@Test
	public final void testPdfStamperExceptionMessageSeverityStringStringStringArray() {
		pse = new PdfStamperException(MessageSeverity.ERROR, LibFileManagerMessageKeys.FILE_CONTENT_NOT_CONVERTIBLE,
				messageUtils.returnMessage(LibFileManagerMessageKeys.FILE_CONTENT_NOT_CONVERTIBLE), FILENAME);
		assertNotNull(pse);
		assertEquals(MessageSeverity.ERROR, pse.getMessageSeverity());
		assertEquals(LibFileManagerMessageKeys.FILE_CONTENT_NOT_CONVERTIBLE, pse.getKey());
		assertNull(pse.getCause());
	}

	@Test
	public final void testPdfStamperExceptionThrowableMessageSeverityStringStringStringArray() {
		IllegalArgumentException iae = new IllegalArgumentException();
		pse = new PdfStamperException(iae, MessageSeverity.ERROR, LibFileManagerMessageKeys.FILE_CONTENT_NOT_CONVERTIBLE,
				messageUtils.returnMessage(LibFileManagerMessageKeys.FILE_CONTENT_NOT_CONVERTIBLE), FILENAME);
		assertNotNull(pse);
		assertEquals(MessageSeverity.ERROR, pse.getMessageSeverity());
		assertEquals(LibFileManagerMessageKeys.FILE_CONTENT_NOT_CONVERTIBLE, pse.getKey());
		assertTrue(iae.getClass().equals(pse.getCause().getClass()));
	}
}
