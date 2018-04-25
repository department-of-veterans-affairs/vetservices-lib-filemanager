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

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileParts;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class TikaDetectorTest extends AbstractFileHandler {

	private static final String UNSUPPORTED_MIMETYPE = "application/stl";

	private TikaDetector tikaDetector;

	@Before
	public void setUp() throws Exception {
		tikaDetector = new TikaDetector();
		assertNotNull(tikaDetector);
	}

	@Test
	public final void testDetect() throws IOException {
		// test all convertible types
		for (ConvertibleTypesEnum enumeration : ConvertibleTypesEnum.values()) {
			List<File> files = super.listFilesByMimePath(enumeration.getMimeType());
			assertTrue("Files for " + enumeration.getMimeString() + " is null or empty.", (files != null) && !files.isEmpty());

			// we have to detect every file in the directory
			// to confirm that we can detect the variations of that file type
			for (File file : files) {
				if (!file.exists()) {
					fail("File enumerated by AbstractFileManager.getFilesByMimePath() returned non-existent file " + file.getPath());
				}

				byte[] bytes = Files.readAllBytes(file.toPath());
				FileParts parts = FileManagerUtils.getFileParts(file.getName());
				try {
					MimeType mimetype = tikaDetector.detect(bytes, parts);
					System.out.println(file.getName() + "\t " + mimetype.getBaseType());
					assertNotNull(mimetype);
					if (!parts.getName().startsWith("NOT_") && !parts.getName().startsWith("BAD_")) {
						assertTrue(enumeration.getMimeType().match(mimetype));
					}

				} catch (FileManagerException e) {
					assertNotNull(e);
					if (MessageKeys.FILEMANAGER_ISSUE.getKey().equals(e.getKey())) {
						e.printStackTrace();
						fail("Something went wrong: " + e.getKey() + ": " + e.getMessage());
					}
					System.out.println("Threw " + e.getClass().getSimpleName() + " - " + e.getKey() + ": " + e.getMessage());
					assertTrue(!StringUtils.isBlank(e.getKey()));
				}
			}
		}

		// test a non-convertible but valid type
		FileParts parts = new FileParts();
		parts.setName("test");
		parts.setExtension("stl");
		MimeType testtype = null;
		try {
			testtype = new MimeType(UNSUPPORTED_MIMETYPE);
		} catch (MimeTypeParseException e1) {
			e1.printStackTrace();
			fail("Why couldn't MimeType parser handle " + UNSUPPORTED_MIMETYPE);
		}
		List<File> files = super.listFilesByMimePath(testtype);

		for (File file : files) {
			try {
				byte[] moreBytes = Files.readAllBytes(file.toPath());
				MimeType mimetype = tikaDetector.detect(moreBytes, parts);
				assertNotNull(mimetype);

			} catch (FileManagerException e) {
				assertNotNull(e);
				if (MessageKeys.FILEMANAGER_ISSUE.getKey().equals(e.getKey())) {
					e.printStackTrace();
					fail("Something went wrong: " + e.getKey() + ": " + e.getMessage());
				}
				System.out.println("Threw " + e.getClass().getSimpleName() + " - " + e.getKey() + ": " + e.getMessage());
				assertTrue(!StringUtils.isBlank(e.getKey()));
			}
		}
	}

	@Test
	public final void testDetect_Invalid() {
		// null bytes and parts
		FileParts parts = new FileParts();
		parts.setName(null);
		parts.setExtension(null);

		testNullBytesAndParts(null, null);
		testNullBytesAndParts(null, parts);
		testNullBytesAndParts(new byte[] {}, parts);

	}

	private void testNullBytesAndParts(byte[] bytes, FileParts parts) {
		try {
			MimeType mimetype = tikaDetector.detect(bytes, parts);
			assertNull(mimetype);

		} catch (FileManagerException e) {
			assertNotNull(e);
			if (MessageKeys.FILEMANAGER_ISSUE.getKey().equals(e.getKey())) {
				e.printStackTrace();
				fail("Something went wrong: " + e.getKey() + ": " + e.getMessage());
			}
			System.out.println("Threw " + e.getClass().getSimpleName() + " - " + e.getKey() + ": " + e.getMessage());
			assertTrue(!StringUtils.isBlank(e.getKey()));
			assertTrue(MessageKeys.FILE_BYTES_NULL_OR_EMPTY.getKey().equals(e.getKey())
					|| MessageKeys.FILE_BYTES_UNREADABLE.getKey().equals(e.getKey()));
		}
	}
}
