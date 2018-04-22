package gov.va.vetservices.lib.filemanager.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import gov.va.vetservices.lib.filemanager.FileManagerConfig;

//@RunWith(MockitoJUnitRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(inheritListeners = false, listeners = { DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class FileManagerPropertiesTest {

	private static final String TEST_KEY_FILE_MAX_BYTES = "filemanager.config.file.max.bytes";
	private static final String TEST_KEY_FILE_MAX_TEXT_MB = "filemanager.config.file.max.text.megabytes";
	private static final String TEST_DEFAULT_FILE_MAX_BYTES = "26214400";
	private static final String TEST_DEFAULT_FILE_MAX_TEXT_MB = "25 MB";
	private static final String TEST_CONVERTIBLE_FILE_EXTENSIONS = "[BMP, GIF, JPEG, JPG, PDF, PNG, TIF, TIFF, TXT]";

	int maxFileBytes = new Integer(TEST_DEFAULT_FILE_MAX_BYTES);
	String maxFileMegaBytes = TEST_DEFAULT_FILE_MAX_TEXT_MB;

	@Autowired
	@Qualifier(FileManagerProperties.BEAN_NAME)
	FileManagerProperties fileManagerProperties;

	@Before
	public void setUp() throws Exception {
		assertNotNull("fileManagerProperties cannot be null.", fileManagerProperties);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testFileManagerProperties() {
//		FileManagerProperties fmp = null;
//		try {
//			fmp = new FileManagerProperties();
//		} catch (Throwable e) {
//			assertTrue(IllegalAccessError.class.equals(e.getClass()));
//		}
//		assertNull(fmp);

		assertEquals(FileManagerProperties.KEY_FILE_MAX_BYTES, TEST_KEY_FILE_MAX_BYTES);
		assertEquals(FileManagerProperties.KEY_FILE_MAX_TEXT_MB, TEST_KEY_FILE_MAX_TEXT_MB);
		assertEquals(FileManagerProperties.DEFAULT_FILE_MAX_BYTES, TEST_DEFAULT_FILE_MAX_BYTES);
		assertEquals(FileManagerProperties.DEFAULT_FILE_MAX_TEXT_MB, TEST_DEFAULT_FILE_MAX_TEXT_MB);
		assertEquals(FileManagerProperties.CONVERTIBLE_FILE_EXTENSIONS_STRING, TEST_CONVERTIBLE_FILE_EXTENSIONS);
	}

	@Test
	public final void testGetMaxFileBytes() {
		System.out.println("FileManagerProperties.getMaxFileBytes() = " + fileManagerProperties.getMaxFileBytes());
		System.out.println("maxFileBytes = " + maxFileBytes);
		assertEquals(fileManagerProperties.getMaxFileBytes(), maxFileBytes);
	}

	@Test
	public final void testGetMaxFileMegaBytes() {
		System.out.println("FileManagerProperties.getMaxFileMegaBytes() = " + fileManagerProperties.getMaxFileMegaBytes());
		System.out.println("maxFileMegaBytes = " + maxFileMegaBytes);
		assertEquals(fileManagerProperties.getMaxFileMegaBytes(), maxFileMegaBytes);
	}

}
