package gov.va.vetservices.lib.filemanager.mime.detectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;

public class JMimeMagicDetectorTest {

	private final byte[] bytes = "This is a test".getBytes();
	private final String mimetype = ConvertibleTypesEnum.TXT.getMimeString();
	private final String filename = "test.txt";

	JMimeMagicDetector jMimeMagicDetector;

	@Before
	public void setUp() throws Exception {
		jMimeMagicDetector = new JMimeMagicDetector();
		assertNotNull(jMimeMagicDetector);
	}

	@Test
	public final void testDetectByMagic() throws IOException {
		String type = jMimeMagicDetector.detectByMagic(bytes);
		assertNotNull(type);
		assertTrue(mimetype.equals(type));
	}

	@Test
	public final void testDetectWithFilename() throws IOException {
		// more exhaustive test is performed in MimeTypeDetectorTest
		String type = jMimeMagicDetector.detectWithFilename(bytes, filename);
		assertNotNull(type);
		assertTrue(mimetype.equals(type));
	}

}
