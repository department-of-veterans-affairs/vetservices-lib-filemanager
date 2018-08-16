package gov.va.vetservices.lib.filemanager.mime.detectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

//@RunWith(MockitoJUnitRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS,
		AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class TikaDetectorTest extends AbstractFileHandler {

	private static final String UNSUPPORTED_MIMETYPE = "application/stl";

	@Autowired
	@Qualifier(TikaDetector.BEAN_NAME)
	private TikaDetector tikaDetector;

	@Before
	public void setUp() throws Exception {
		assertNotNull(tikaDetector);
	}

	@Test
	public final void testGetTikaConfig() {
		// sad invalid file
		String path = "/some-nonexistent-file.xml";
		try {
			invokeMethodForGivenObject(tikaDetector, "getTikaConfig", new Class<?>[] { String.class }, path);
			fail("Should have thrown exception");
		} catch (final Throwable e) {
			assertNotNull(e);
			assertTrue(IllegalArgumentException.class.equals(((InvocationTargetException) e).getTargetException().getClass()));
		}

		// sad null path
		path = null;
		try {
			invokeMethodForGivenObject(tikaDetector, "getTikaConfig", new Class<?>[] { String.class }, path);
			fail("Should have thrown exception");
		} catch (final Throwable e) {
			assertNotNull(e);
			assertTrue(IllegalArgumentException.class.equals(((InvocationTargetException) e).getTargetException().getClass()));
		}

		// sad blank path
		path = "   ";
		try {
			invokeMethodForGivenObject(tikaDetector, "getTikaConfig", new Class<?>[] { String.class }, path);
			fail("Should have thrown exception");
		} catch (final Throwable e) {
			assertNotNull(e);
			assertTrue(IllegalArgumentException.class.equals(((InvocationTargetException) e).getTargetException().getClass()));
		}

		// sad TikaConfig instantiation failure
		path = "/nonexistent.xml";
		try {
			invokeMethodForGivenObject(tikaDetector, "getTikaConfig", new Class<?>[] { String.class }, path);
			fail("Should have thrown exception");
		} catch (final InvocationTargetException e) {
			assertNotNull(e);
			assertTrue(IllegalArgumentException.class.equals(e.getTargetException().getClass()));
		}
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
					final MimeType mimetype = tikaDetector.detect(bytes, parts);
					if (TestingConstants.PRINT) {
						System.out.println(file.getName() + "\t " + mimetype.getBaseType());
					}
					assertNotNull(mimetype);
					if (!parts.getName().startsWith("NOT_") && !parts.getName().startsWith("BAD_")) {
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
				final MimeType mimetype = tikaDetector.detect(moreBytes, parts);
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
		FilePartsDto parts = new FilePartsDto();
		parts.setName(null);
		parts.setExtension(null);

		testNullBytesAndParts(null, parts);
		testNullBytesAndParts(new byte[] { 64, 65, 66 }, null);
		testNullBytesAndParts(null, null);

		final TikaDetector tikad = spy(tikaDetector);
		// cause exceptions //
		parts = new FilePartsDto();
		parts.setName("IS_Text-doc");
		parts.setExtension("pdf");
		byte[] bytes = null;
		try {
			bytes = super.readFile(Paths.get("files/application/pdf/IS_text-doc.pdf"));
		} catch (final IOException e) {
			e.printStackTrace();
			fail("Should not have thrown exception yet.");
		}
		// IOException
		try {
			doThrow(IOException.class).when(tikad).detectByMagic(any());
		} catch (IOException | MimeTypeParseException e) {
			e.printStackTrace();
			fail("Should not have thrown exception yet.");
		}

		try {
			tikad.detect(bytes, parts);
		} catch (final FileManagerException e) {
			if (TestingConstants.PRINT) {
				e.printStackTrace();
			}
			assertTrue("Exception should have been: 'Content of {0} cannot be read due to corrupted file or an unexpected issue.'",
					e.getMessage().contains("cannot be read"));
		}

		// MimeTypeParseException
		try {
			doThrow(MimeTypeParseException.class).when(tikad).detectByMagic(any());
		} catch (IOException | MimeTypeParseException e) {
			e.printStackTrace();
			fail("Should not have thrown exception yet.");
		}

		try {
			tikad.detect(bytes, parts);
		} catch (final FileManagerException e) {
			assertTrue(e.getMessage().contains("connot be converted"));
		}

	}

	private void testNullBytesAndParts(final byte[] bytes, final FilePartsDto parts) {
		try {
			final MimeType mimetype = tikaDetector.detect(bytes, parts);
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
