package gov.va.vetservices.lib.filemanager.mime.detectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.commons.lang3.StringUtils;
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
public class JMimeMagicDetectorTest extends AbstractFileHandler {

//	private static final String JMIME_DEFAULT = "text/plain";

	private static final String UNSUPPORTED_MIMETYPE = "application/stl";

	@Autowired
	@Qualifier(JMimeMagicDetector.BEAN_NAME)
	JMimeMagicDetector jMimeMagicDetector;

	@Before
	public void setUp() throws Exception {
		assertNotNull(jMimeMagicDetector);
	}

	@Test
	public final void testDetect() throws IOException {
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

				final byte[] bytes = Files.readAllBytes(file.toPath());
				final FilePartsDto parts = FileManagerUtils.getFileParts(file.getName());
				try {
					final MimeType mimetype = jMimeMagicDetector.detect(bytes, parts);
					if (TestingConstants.PRINT) {
						System.out.println(file.getName() + "\t " + mimetype.getBaseType());
					}
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
		parts.setName("test");
		parts.setExtension("stl");
		MimeType testtype = null;
		try {
			testtype = new MimeType(UNSUPPORTED_MIMETYPE);
		} catch (final MimeTypeParseException e1) {
			e1.printStackTrace();
			fail("Why couldn't MimeType parser handle " + UNSUPPORTED_MIMETYPE);
		}
		final List<File> files = super.listFilesByMimePath(testtype);

		for (final File file : files) {
			try {
				final byte[] moreBytes = Files.readAllBytes(file.toPath());
				final MimeType mimetype = jMimeMagicDetector.detect(moreBytes, parts);
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
	}

	@Test
	public final void testDetect_Bad() {
		// null bytes and parts
		final FilePartsDto parts = new FilePartsDto();
		parts.setName(null);
		parts.setExtension(null);

		testNullBytesAndParts(null, null);
		testNullBytesAndParts(null, parts);
		testNullBytesAndParts(new byte[] {}, null);
		testNullBytesAndParts(new byte[] { 0 }, parts);

	}

	private void testNullBytesAndParts(final byte[] bytes, final FilePartsDto parts) {
		try {
			final MimeType mimetype = jMimeMagicDetector.detect(bytes, parts);
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
			assertTrue(LibFileManagerMessageKeys.FILE_BYTES_NULL_OR_EMPTY.equals(e.getKey())
					|| LibFileManagerMessageKeys.FILE_BYTES_UNREADABLE.equals(e.getKey())
					|| LibFileManagerMessageKeys.FILE_NAME_NULL_OR_EMPTY.equals(e.getKey()));
		}
	}
}
