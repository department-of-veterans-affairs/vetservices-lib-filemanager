package gov.va.vetservices.lib.filemanager.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS,
		AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@TestExecutionListeners(inheritListeners = false, listeners = { DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class MessageUtilsTest {

	@Autowired
	MessageUtils messageUtils;

	@Before
	public void setup() {
	}

	@Test
	public void testInternalMessageList() {
		messageUtils.add(MessageSeverity.ERROR,
				"test.key",
				"test message");
		assertEquals(1, messageUtils.getMessages().size());
		assertEquals(1, messageUtils.size());
	}

	@Test
	public final void testCreateMessage() {
		Message msg = messageUtils.createMessage(MessageSeverity.ERROR, LibFileManagerMessageKeys.FILEMANAGER_ISSUE);
		assertNotNull(msg);
		assertTrue(msg.getKey().equals(LibFileManagerMessageKeys.FILEMANAGER_ISSUE));
		assertTrue(msg.getText().startsWith(""));
	}

	@Test
	public final void testAddMessage() {
		messageUtils.getMessages().clear();
		messageUtils.add(MessageSeverity.TRACE, LibFileManagerMessageKeys.FILE_BYTES_UNREADABLE,
				"Content of {0} cannot be read due to corrupted file or an unexpected issue.", "one");
		assertTrue(messageUtils.getMessages().size() == 1);
		assertTrue(messageUtils.getMessages().get(0).getText().startsWith(""));

		messageUtils.add(MessageSeverity.TRACE, LibFileManagerMessageKeys.FILEMANAGER_ISSUE,
				"Internal issue occurred. Please check the application logs.");
		assertTrue(messageUtils.getMessages().size() == 2);
		assertTrue(messageUtils.getMessages().get(1).getText().startsWith(""));
	}

	@Test
	public final void testReturnMessage() {
		String msg = messageUtils.returnMessage(LibFileManagerMessageKeys.FILEMANAGER_ISSUE);
		assertNotNull(msg);
		assertTrue(msg.startsWith(""));

		msg = messageUtils.returnMessage(LibFileManagerMessageKeys.FILE_BYTES_UNREADABLE, "one");
		assertTrue(msg.startsWith(""));
	}

}
