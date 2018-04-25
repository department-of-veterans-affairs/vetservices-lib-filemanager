package gov.va.vetservices.lib.filemanager.mime.detectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileParts;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class AbstractDetectorTest {

	private final FileParts testParts = FileManagerUtils.getFileParts("filename.txt");
	private final byte[] testBytes = "Test bytes".getBytes();
	private final String testMimetypeRaw = "application/pdf"; // must match testMimetype
	private MimeType testMimetype;

	@Test
	public final void testDetector() {// throws IOException {
		class TestDetector extends AbstractDetector {
			@Override
			public MimeType detect(byte[] bytes, FileParts parts) throws FileManagerException {
				return ConvertibleTypesEnum.PDF.getMimeType();
			}
		}
		TestDetector testDetector = new TestDetector();
		assertNotNull(testDetector);

		try {
			testMimetype = new MimeType(testMimetypeRaw);
			assertNotNull(testMimetype);
			assertTrue(testMimetype.match(testDetector.detect(testBytes, testParts)));
		} catch (FileManagerException e) {
			// will never get here, but in case the test changes, here's the logic
			assertNotNull(e);
			if (MessageKeys.FILEMANAGER_ISSUE.getKey().equals(e.getKey())) {
				e.printStackTrace();
				fail("Something went wrong: " + e.getKey() + ": " + e.getMessage());
			}
			e.printStackTrace();
			assertTrue(!StringUtils.isBlank(e.getKey()));
		} catch (MimeTypeParseException e) {
			// should never get here, but in case
			e.printStackTrace();
			fail("Developers should use valid MIME types.");
		}
	}

}
