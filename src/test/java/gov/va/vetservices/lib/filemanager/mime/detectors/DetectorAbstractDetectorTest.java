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

import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class DetectorAbstractDetectorTest {

	private final FilePartsDto testParts = FileManagerUtils.getFileParts("filename.txt");
	private final byte[] testBytes = "Test bytes".getBytes();
	private final String testTextType = "text/plain";
	private final String testConvertibleType = "application/pdf"; // must match testMimetype

	private TestDetector testDetector = null;
	private MimeType testMimetype;

	class TestDetector extends AbstractDetector {
		@Override
		public MimeType detect(byte[] bytes, FilePartsDto parts) throws FileManagerException {
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
			if (MessageKeysEnum.FILEMANAGER_ISSUE.getKey().equals(e.getKey())) {
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
			// magic, hint match with each other
			MimeType winner = TestDetector.selfCheck(withMagic, withHint);
			assertNotNull(winner);
			assertTrue(testConvertibleType.equals(winner.getBaseType()));

			// both args null
			winner = TestDetector.selfCheck(null, null);
			assertNull(winner);
			System.out.println("TestDetector.selfCheck(null, null)");

			// magic, null
			winner = TestDetector.selfCheck(withMagic, null);
			assertNotNull(winner);
			System.out.println("TestDetector.selfCheck(withMagic, null)");
			assertTrue(testConvertibleType.equals(winner.getBaseType()));

			// null, hint
			winner = TestDetector.selfCheck(null, withHint);
			assertNotNull(winner);
			System.out.println("TestDetector.selfCheck(null, withHint)");
			assertTrue(testConvertibleType.equals(winner.getBaseType()));

			// magic, hint do not match with each other
			withHint = new MimeType(testTextType);
			winner = TestDetector.selfCheck(withMagic, withHint);
			assertNotNull(winner);
			System.out.println("TestDetector.selfCheck(withMagic, withHint)");
			assertTrue(testConvertibleType.equals(winner.getBaseType()));

		} catch (MimeTypeParseException e) {
			e.printStackTrace();
			fail("Could not parse MIME type. " + e.getMessage());
		}
	}

}
