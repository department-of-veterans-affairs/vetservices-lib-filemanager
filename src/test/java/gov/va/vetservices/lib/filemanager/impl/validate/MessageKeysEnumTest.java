package gov.va.vetservices.lib.filemanager.impl.validate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class MessageKeysEnumTest {

	@Test
	public final void test() {
		assertNotNull(MessageKeysEnum.values());
		assertTrue(MessageKeysEnum.values().length > 0);

		for (MessageKeysEnum mk : MessageKeysEnum.values()) {
			assertNotNull(mk.getKey());
			assertNotNull(mk.getMessage());
		}
	}

}
