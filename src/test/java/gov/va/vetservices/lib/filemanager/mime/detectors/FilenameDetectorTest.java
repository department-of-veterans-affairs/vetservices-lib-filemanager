package gov.va.vetservices.lib.filemanager.mime.detectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

import javax.activation.MimeType;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;
import gov.va.vetservices.lib.filemanager.testutil.TestingConstants;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS,
		AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class FilenameDetectorTest extends AbstractFileHandler {

//	private final String mimetype = ConvertibleTypesEnum.TXT.getMimeString();
//	private final FilePartsDto parts = FileManagerUtils.getFileParts("test.txt");

	@Autowired
	FilenameDetector fileManagerDetector;

	@Before
	public void setUp() throws Exception {
		assertNotNull(fileManagerDetector);
	}

	@Test
	public final void testDetect() {
		// test all convertible types
		for (final ConvertibleTypesEnum enumeration : ConvertibleTypesEnum.values()) {
			final List<File> files = super.listFilesByMimePath(enumeration.getMimeType());
			assertTrue("Files for " + enumeration.getMimeString() + " is null or empty.", files != null && !files.isEmpty());

			// we have to detect every file in the directory
			// to confirm that we can detect the variations of that file type
			for (final File file : files) {
				if (!file.exists()) {
					fail("File enumerated by " + super.getClass().getSimpleName() + ".getFilesByMimePath() returned non-existent file "
							+ file.getPath());
				}

				final FilePartsDto parts = FileManagerUtils.getFileParts(file.getName());
				try {
					final MimeType mimetype = fileManagerDetector.detect(null, parts);
					assertNotNull(mimetype);
					if (!parts.getName().startsWith("NOT_")) {
						assertTrue(enumeration.getMimeType().match(mimetype));
					}

				} catch (final FileManagerException e) {
					assertNotNull(e);
					if (LibFileManagerMessageKeys.FILEMANAGER_ISSUE.equals(e.getKey())) {
						e.printStackTrace();
						fail("Something went wrong: " + e.getKey() + ": " + e.getMessage());
					}
					if (TestingConstants.PRINT) {
						System.out.println("Threw " + e.getClass().getSimpleName() + " - " + e.getKey() + ": " + e.getMessage());
					}
					assertTrue(!StringUtils.isBlank(e.getKey()));
				}
			}
		}

		// test a non-convertible but valid type
		final FilePartsDto parts = new FilePartsDto();
		parts.setExtension("art");

		try {
			final MimeType mimetype = fileManagerDetector.detect(null, parts);
			assertNotNull(mimetype);

		} catch (final FileManagerException e) {
			assertNotNull(e);
			if (LibFileManagerMessageKeys.FILEMANAGER_ISSUE.equals(e.getKey())) {
				e.printStackTrace();
				fail("Something went wrong: " + e.getKey() + ": " + e.getMessage());
			}
			if (TestingConstants.PRINT) {
				System.out.println("Threw " + e.getClass().getSimpleName() + " - " + e.getKey() + ": " + e.getMessage());
			}
			assertTrue(!StringUtils.isBlank(e.getKey()));
		}
	}

	@Test
	public final void testDetect_Bad() {
		// null extension
		FilePartsDto parts = new FilePartsDto();
		parts.setExtension(null);

		try {
			final MimeType mimetype = fileManagerDetector.detect(null, parts);
			assertNull(mimetype);

		} catch (final FileManagerException e) {
			assertNotNull(e);
			if (LibFileManagerMessageKeys.FILEMANAGER_ISSUE.equals(e.getKey())) {
				e.printStackTrace();
				fail("Something went wrong: " + e.getKey() + ": " + e.getMessage());
			}
			if (TestingConstants.PRINT) {
				System.out.println("Threw " + e.getClass().getSimpleName() + " - " + e.getKey() + ": " + e.getMessage());
			}
			assertTrue(!StringUtils.isBlank(e.getKey()));
			assertTrue(LibFileManagerMessageKeys.FILE_TYPE_UNVERIFIABLE.equals(e.getKey()));
		}

		// empty extension
		parts = new FilePartsDto();
		parts.setExtension("");

		try {
			final MimeType mimetype = fileManagerDetector.detect(null, parts);
			assertNull(mimetype);

		} catch (final FileManagerException e) {
			assertNotNull(e);
			if (LibFileManagerMessageKeys.FILEMANAGER_ISSUE.equals(e.getKey())) {
				e.printStackTrace();
				fail("Something went wrong: " + e.getKey() + ": " + e.getMessage());
			}
			if (TestingConstants.PRINT) {
				System.out.println("Threw " + e.getClass().getSimpleName() + " - " + e.getKey() + ": " + e.getMessage());
			}
			assertTrue(!StringUtils.isBlank(e.getKey()));
			assertTrue(LibFileManagerMessageKeys.FILE_TYPE_UNVERIFIABLE.equals(e.getKey()));
		}

		// non-existent extension
		parts = new FilePartsDto();
		parts.setExtension("abc123");

		try {
			final MimeType mimetype = fileManagerDetector.detect(null, parts);
			assertNull(mimetype);

		} catch (final FileManagerException e) {
			assertNotNull(e);
			if (LibFileManagerMessageKeys.FILEMANAGER_ISSUE.equals(e.getKey())) {
				e.printStackTrace();
				fail("Something went wrong: " + e.getKey() + ": " + e.getMessage());
			}
			if (TestingConstants.PRINT) {
				System.out.println("Threw " + e.getClass().getSimpleName() + " - " + e.getKey() + ": " + e.getMessage());
			}
			assertTrue(!StringUtils.isBlank(e.getKey()));
			assertTrue(LibFileManagerMessageKeys.FILE_TYPE_UNVERIFIABLE.equals(e.getKey()));
		}

	}

	@Test
	public final void testGetProps() {
		super.setFieldFromGivenObject(fileManagerDetector, "props", null);

		// re-populate props
		try {
			super.invokeMethodForGivenObject(fileManagerDetector, "getProps", null);
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
			fail("Unexpected exception.");
		}
		try {
			final Properties props = (Properties) super.getFieldFromGivenObject(fileManagerDetector, "props").get(fileManagerDetector);
			assertNotNull(props);
			assertTrue(!props.isEmpty());
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			fail("Unexpected exception.");
		}

		// get existing props
		try {
			super.invokeMethodForGivenObject(fileManagerDetector, "getProps", null);
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
			fail("Unexpected exception.");
		}
		try {
			final Properties props = (Properties) super.getFieldFromGivenObject(fileManagerDetector, "props").get(fileManagerDetector);
			assertNotNull(props);
			assertTrue(!props.isEmpty());
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			fail("Unexpected exception.");
		}

		// throw exeption
		try {
			final Properties props = (Properties) super.getFieldFromGivenObject(fileManagerDetector, "props").get(fileManagerDetector);
			assertNotNull(props);
			assertTrue(!props.isEmpty());

			final Properties spiedProps = Mockito.spy(props);
			try {
				doThrow(new IOException("Testing")).when(spiedProps).load(any(InputStream.class));
			} catch (final IOException e) {
				fail("Should not throw exception here.");
			}

			try {
				super.invokeMethodForGivenObject(fileManagerDetector, "getProps", null);
			} catch (final IllegalArgumentException e) {
				assertTrue(e.getMessage().contains("could not load classpath file"));
			}

		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			fail("Unexpected exception " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}

	}
}
