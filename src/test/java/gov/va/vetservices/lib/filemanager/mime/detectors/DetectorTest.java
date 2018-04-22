package gov.va.vetservices.lib.filemanager.mime.detectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class DetectorTest {

	private final String testFilename = "filename.txt";
	private final byte[] testBytes = "Test bytes".getBytes();
	private final String testMagicMimetype = "magic/detected";
	private final String testFilenameMimetype = "filename/detected";

	@Test
	public final void testDetector() throws IOException {
		class TestDetector implements Detector {
			@Override
			public String detectByMagic(byte[] bytes) throws IOException {
				return testMagicMimetype;
			}

			@Override
			public String detectWithFilename(byte[] bytes, String filename) throws IOException {
				return testFilenameMimetype;
			}
		}
		;
		TestDetector testDetector = new TestDetector();
		assertNotNull(testDetector);
		assertTrue(testMagicMimetype.equals(testDetector.detectByMagic(testBytes)));
		assertTrue(testFilenameMimetype.equals(testDetector.detectWithFilename(testBytes, testFilename)));
	}

}
