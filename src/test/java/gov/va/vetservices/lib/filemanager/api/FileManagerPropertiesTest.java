package gov.va.vetservices.lib.filemanager.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;
import gov.va.vetservices.lib.filemanager.testutil.TestingConstants;

//@RunWith(MockitoJUnitRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS,
		AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@TestExecutionListeners(inheritListeners = false, listeners = { DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class FileManagerPropertiesTest {

	private static final String TEST_KEY_CONVERTIBLE_FILE_EXTENSIONS = "filemanager.config.file.convertible.extensions";
	private static final String TEST_KEY_FILE_MAX_BYTES = "filemanager.config.file.max.bytes";
	private static final String TEST_KEY_FILE_MAX_TEXT_MB = "filemanager.config.file.max.text.megabytes";
	private static final String TEST_KEY_FILENAME_MAX_LENGTH = "filemanager.config.filname.max.length";
	private static final String TEST_DEFAULT_FILE_MAX_BYTES = "26214400";
	private static final String TEST_DEFAULT_FILE_MAX_TEXT_MB = "25 MB";
	private static final String TEST_CONVERTIBLE_FILE_EXTENSIONS = "[BMP, GIF, JPEG, JPG, PDF, PNG, TIF, TIFF, TXT]";
	private static final String TEST_FILE_NAME_ILLEGAL_CHARS = Arrays.toString(new String[] { "/", "\\", ":" });

	int maxFileBytes = new Integer(TEST_DEFAULT_FILE_MAX_BYTES);
	String maxFileMegaBytes = TEST_DEFAULT_FILE_MAX_TEXT_MB;

	@Autowired
	@Qualifier(FileManagerProperties.BEAN_NAME)
	FileManagerProperties fileManagerProperties;

	@Before
	public void setUp() throws Exception {
		assertNotNull("fileManagerProperties cannot be null.", fileManagerProperties);
	}

	@Test
	public final void testFileManagerProperties() {
		assertEquals(FileManagerProperties.CONVERTIBLE_FILE_EXTENSIONS_STRING, TEST_CONVERTIBLE_FILE_EXTENSIONS);
		assertEquals(Arrays.toString(FileManagerProperties.FILE_NAME_ILLEGAL_CHARS.stream().toArray(String[]::new)), TEST_FILE_NAME_ILLEGAL_CHARS);
		assertEquals(FileManagerProperties.KEY_FILE_MAX_BYTES, TEST_KEY_FILE_MAX_BYTES);
		assertEquals(FileManagerProperties.KEY_FILE_MAX_TEXT_MB, TEST_KEY_FILE_MAX_TEXT_MB);
		assertEquals(FileManagerProperties.KEY_CONVERTIBLE_FILE_EXTENSIONS, TEST_KEY_CONVERTIBLE_FILE_EXTENSIONS);
		assertEquals(FileManagerProperties.KEY_FILENAME_MAX_LENGTH, TEST_KEY_FILENAME_MAX_LENGTH);
	}

	@Test
	public final void testGetMaxFileBytes() {
		if (TestingConstants.PRINT) {
			System.out.println("FileManagerProperties.getMaxFileBytes() = " + fileManagerProperties.getMaxFileBytes());
			System.out.println("maxFileBytes = " + maxFileBytes);
		}
		assertEquals(fileManagerProperties.getMaxFileBytes(), maxFileBytes);
	}

	@Test
	public final void testGetMaxFileMegaBytes() {
		if (TestingConstants.PRINT) {
			System.out.println("FileManagerProperties.getMaxFileMegaBytes() = " + fileManagerProperties.getMaxFileMegaBytes());
			System.out.println("maxFileMegaBytes = " + maxFileMegaBytes);
		}
		assertEquals(fileManagerProperties.getMaxFileMegaBytes(), maxFileMegaBytes);
	}

}
