package gov.va.vetservices.lib.filemanager.mime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.activation.MimeType;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;
import gov.va.vetservices.lib.filemanager.testutil.TestingConstants;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class MimeTypeDetectorTest extends AbstractFileHandler {

	MimeTypeDetector mimeTypeDetector = new MimeTypeDetector();

	class FileHandlerTester extends AbstractFileHandler {
		// just use the methods from the abstract class
	}

	protected FileHandlerTester fileHandlerTester = new FileHandlerTester();

	@Before
	public void setUp() throws Exception {
		TEST_DELETE_OUTPUTS_WHEN_DONE = false;
	}

	@After
	public void tearDown() throws Exception {
		if (TEST_DELETE_OUTPUTS_WHEN_DONE) {
			fileHandlerTester.deleteOutputFiles(Paths.get(DEFAULT_OUTPUT_DIR));
		}
	}

	@Test
	public final void testDetectMimeType() throws IOException {

		final int singlejMimeMagicEnabled = 6;
		int counter = 0;

		for (final ConvertibleTypesEnum enumeration : ConvertibleTypesEnum.values()) {
			super.setFieldFromGivenObject(mimeTypeDetector, "enableJMimeMagic", false);
			if (++counter == singlejMimeMagicEnabled) {
				super.setFieldFromGivenObject(mimeTypeDetector, "enableJMimeMagic", true);
			}

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
				final String filename = file.getName();
				final String msgPrefix = file.getPath() + ": ";
				final FilePartsDto parts = FileManagerUtils.getFileParts(filename);

				if (TestingConstants.PRINT) {
					System.out.println("File: " + filename);
				}

				if (filename.startsWith(TestingConstants.TEST_FILE_PREFIX_CORRUPT)) {
					// so far, corrupt files have typically still been detected correctly
					MimeType mimetype = null;
					try {
						mimetype = mimeTypeDetector.detectMimeType(bytes, parts);
						assertNotNull(msgPrefix + " detected as null", mimetype);

						final String mimestring = mimetype.toString();
						assertTrue(msgPrefix + " blank mimestring", !StringUtils.isBlank(mimestring));
						assertTrue(msgPrefix + mimestring + " != " + enumeration.getMimeString(),
								StringUtils.equals(mimestring, enumeration.getMimeString()));

					} catch (final FileManagerException e) {
						if (MessageKeysEnum.FILEMANAGER_ISSUE.getKey().equals(e.getKey())) {
							e.printStackTrace();
							fail(msgPrefix + " something went wrong: " + e.getMessage());
						}
					}
				} else {
					MimeType mimetype = null;
					try {
						mimetype = mimeTypeDetector.detectMimeType(bytes, parts);
						assertNotNull(msgPrefix + " detected as null", mimetype);

						final String mimestring = mimetype.toString();
						assertTrue(msgPrefix + " blank mimestring", !StringUtils.isBlank(mimestring));
						assertTrue(msgPrefix + mimestring + " != " + enumeration.getMimeString(),
								StringUtils.equals(mimestring, enumeration.getMimeString()));

					} catch (final FileManagerException e) {
						if (MessageKeysEnum.FILEMANAGER_ISSUE.getKey().equals(e.getKey())) {
							e.printStackTrace();
							fail(msgPrefix + " something went wrong: " + e.getMessage());
						}
					}
				}
			}
		}

		// one test with both tika and jMimeMagic shut off
		super.setFieldFromGivenObject(mimeTypeDetector, "enableJMimeMagic", false);
		super.setFieldFromGivenObject(mimeTypeDetector, "enableTika", false);
		try {
			mimeTypeDetector.detectMimeType(super.readFile(Paths.get("files/image/gif/IS_Animated.gif")),
					FileManagerUtils.getFileParts("IS_Animated.gif"));
			fail("Should have thrown exception");
		} catch (final FileManagerException e) {
			assertTrue(FileManagerException.class.isAssignableFrom(e.getClass()));
			assertTrue(e.getMessage().contains("cannot be verified"));
		}

	}

	@Test
	public final void testIsFileExtensionSupported() {
		assertTrue(MimeTypeDetector.isFileExtensionSupported(TestingConstants.FILE_EXT_BMP));
		assertTrue(MimeTypeDetector.isFileExtensionSupported(TestingConstants.FILE_EXT_GIF));
		assertTrue(MimeTypeDetector.isFileExtensionSupported(TestingConstants.FILE_EXT_JPEG));
		assertTrue(MimeTypeDetector.isFileExtensionSupported(TestingConstants.FILE_EXT_JPG));
		assertTrue(MimeTypeDetector.isFileExtensionSupported(TestingConstants.FILE_EXT_PDF));
		assertTrue(MimeTypeDetector.isFileExtensionSupported(TestingConstants.FILE_EXT_PNG));
		assertTrue(MimeTypeDetector.isFileExtensionSupported(TestingConstants.FILE_EXT_TIF));
		assertTrue(MimeTypeDetector.isFileExtensionSupported(TestingConstants.FILE_EXT_TIFF));
		assertTrue(MimeTypeDetector.isFileExtensionSupported(TestingConstants.FILE_EXT_TXT));
	}

	@Test
	public final void testIsFileExtensionSupported_BadExtension() {
		assertFalse(MimeTypeDetector.isFileExtensionSupported(null));
		assertFalse(MimeTypeDetector.isFileExtensionSupported(TestingConstants.FILE_EXT_UNSUPPORTED));
	}
}
