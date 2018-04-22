package gov.va.vetservices.lib.filemanager.mime.detectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;

public class TikaDetectorTest {

	private final byte[] bytes = "This is a test".getBytes();
	private final String mimetype = ConvertibleTypesEnum.TXT.getMimeString();
	private final String filename = "test.txt";

	TikaDetector tikaDetector;

	@Before
	public void setUp() throws Exception {
		tikaDetector = new TikaDetector();
		assertNotNull(tikaDetector);
	}

	@Test
	public final void testDetectByMagic() throws IOException {
		String type = tikaDetector.detectByMagic(bytes);
		assertNotNull(type);
		assertTrue(mimetype.equals(type));
	}

	@Test
	public final void testDetectWithFilename() throws IOException {
		// more exhaustive test is performed in MimeTypeDetectorTest
		String type = tikaDetector.detectWithFilename(bytes, filename);
		assertNotNull(type);
		assertTrue(mimetype.equals(type));
	}

}
