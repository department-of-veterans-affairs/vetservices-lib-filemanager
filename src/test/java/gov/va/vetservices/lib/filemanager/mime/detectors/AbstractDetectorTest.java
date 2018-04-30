package gov.va.vetservices.lib.filemanager.mime.detectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileParts;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class AbstractDetectorTest {

	private final FileParts testParts = FileManagerUtils.getFileParts("filename.txt");
	private final byte[] testBytes = "Test bytes".getBytes();
	private final String testTextType = "text/plain";
	private final String testConvertibleType = "application/pdf"; // must match testMimetype

	private TestDetector testDetector = null;
	private MimeType testMimetype;

	class TestDetector extends AbstractDetector {
		@Override
		public MimeType detect(byte[] bytes, FileParts parts) throws FileManagerException {
			return ConvertibleTypesEnum.PDF.getMimeType();
		}
	}

	@Before
	public void setup() {
		testDetector = new TestDetector();
		assertNotNull(testDetector);
	}

	@Test
	public final void testDetector() {// throws IOException {

		try {
			testMimetype = new MimeType(testConvertibleType);
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

	@Test
	public final void testSelfCheck() {
		try {
			MimeType withMagic = new MimeType(testConvertibleType);
			MimeType withHint = new MimeType(testConvertibleType);
			MimeType winner = TestDetector.selfCheck(withMagic, withHint);
			assertNotNull(winner);
			assertTrue(testConvertibleType.equals(winner.getBaseType()));

			winner = TestDetector.selfCheck(null, null);
			assertNull(winner);

			winner = TestDetector.selfCheck(withMagic, null);
			assertNotNull(winner);
			assertTrue(testConvertibleType.equals(winner.getBaseType()));

			winner = TestDetector.selfCheck(null, withHint);
			assertNotNull(winner);
			assertTrue(testConvertibleType.equals(winner.getBaseType()));

			withHint = new MimeType(testTextType);
			winner = TestDetector.selfCheck(withMagic, withHint);
			assertNotNull(winner);
			assertTrue(testConvertibleType.equals(winner.getBaseType()));
		} catch (MimeTypeParseException e) {
			e.printStackTrace();
			fail("Could not parse MIME type. " + e.getMessage());
		}
	}

}
