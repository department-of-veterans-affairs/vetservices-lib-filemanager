package gov.va.vetservices.lib.filemanager;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class FileManagerConfigTest {

	@Autowired
	FileManagerProperties fileManagerProperties;

	@Test
	public final void testFileManagerConfig() {
		assertNotNull(fileManagerProperties);
	}

	@Test
	public final void testPropertySourcesPlaceholderConfigurer() {
		assertNotNull(FileManagerConfig.propertySourcesPlaceholderConfigurer());
	}

}
