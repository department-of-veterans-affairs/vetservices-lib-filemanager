package gov.va.vetservices.lib.filemanager.mime.detectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import javax.activation.MimeType;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class FilenameDetectorTest extends AbstractFileHandler {

//	private final String mimetype = ConvertibleTypesEnum.TXT.getMimeString();
//	private final FilePartsDto parts = FileManagerUtils.getFileParts("test.txt");

	FilenameDetector fileManagerDetector;

	@Before
	public void setUp() throws Exception {
		fileManagerDetector = new FilenameDetector();
		assertNotNull(fileManagerDetector);
	}

	@Test
	public final void testDetect() {
		// test all convertible types
		for (ConvertibleTypesEnum enumeration : ConvertibleTypesEnum.values()) {
			List<File> files = super.listFilesByMimePath(enumeration.getMimeType());
			assertTrue("Files for " + enumeration.getMimeString() + " is null or empty.", (files != null) && !files.isEmpty());

			// we have to detect every file in the directory
			// to confirm that we can detect the variations of that file type
			for (File file : files) {
				if (!file.exists()) {
					fail("File enumerated by " + super.getClass().getSimpleName() + ".getFilesByMimePath() returned non-existent file "
							+ file.getPath());
				}

				FilePartsDto parts = FileManagerUtils.getFileParts(file.getName());
				try {
					MimeType mimetype = fileManagerDetector.detect(null, parts);
					assertNotNull(mimetype);
					if (!parts.getName().startsWith("NOT_")) {
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
		FilePartsDto parts = new FilePartsDto();
		parts.setExtension("art");

		try {
			MimeType mimetype = fileManagerDetector.detect(null, parts);
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

	@Test
	public final void testDetect_Bad() {
		// null extension
		FilePartsDto parts = new FilePartsDto();
		parts.setExtension(null);

		try {
			MimeType mimetype = fileManagerDetector.detect(null, parts);
			assertNull(mimetype);

		} catch (FileManagerException e) {
			assertNotNull(e);
			if (MessageKeys.FILEMANAGER_ISSUE.getKey().equals(e.getKey())) {
				e.printStackTrace();
				fail("Something went wrong: " + e.getKey() + ": " + e.getMessage());
			}
			System.out.println("Threw " + e.getClass().getSimpleName() + " - " + e.getKey() + ": " + e.getMessage());
			assertTrue(!StringUtils.isBlank(e.getKey()));
			assertTrue(MessageKeys.FILE_TYPE_UNVERIFIABLE.getKey().equals(e.getKey()));
		}

		// empty extension
		parts = new FilePartsDto();
		parts.setExtension("");

		try {
			MimeType mimetype = fileManagerDetector.detect(null, parts);
			assertNull(mimetype);

		} catch (FileManagerException e) {
			assertNotNull(e);
			if (MessageKeys.FILEMANAGER_ISSUE.getKey().equals(e.getKey())) {
				e.printStackTrace();
				fail("Something went wrong: " + e.getKey() + ": " + e.getMessage());
			}
			System.out.println("Threw " + e.getClass().getSimpleName() + " - " + e.getKey() + ": " + e.getMessage());
			assertTrue(!StringUtils.isBlank(e.getKey()));
			assertTrue(MessageKeys.FILE_TYPE_UNVERIFIABLE.getKey().equals(e.getKey()));
		}

		// non-existent extension
		parts = new FilePartsDto();
		parts.setExtension("abc123");

		try {
			MimeType mimetype = fileManagerDetector.detect(null, parts);
			assertNull(mimetype);

		} catch (FileManagerException e) {
			assertNotNull(e);
			if (MessageKeys.FILEMANAGER_ISSUE.getKey().equals(e.getKey())) {
				e.printStackTrace();
				fail("Something went wrong: " + e.getKey() + ": " + e.getMessage());
			}
			System.out.println("Threw " + e.getClass().getSimpleName() + " - " + e.getKey() + ": " + e.getMessage());
			assertTrue(!StringUtils.isBlank(e.getKey()));
			assertTrue(MessageKeys.FILE_TYPE_UNVERIFIABLE.getKey().equals(e.getKey()));
		}

	}
}
