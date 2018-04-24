package gov.va.vetservices.lib.filemanager.mime.detectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

public class JMimeMagicDetectorTest extends AbstractFileHandler {

	private static final String JMIME_DEFAULT = "text/plain";

	private static final Path unknownBytesPath = Paths.get("files/application/stl/CAD_Model.stl");

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

		// happy

		String type = jMimeMagicDetector.detectWithFilename(bytes, filename);
		assertNotNull(type);
		assertTrue(mimetype.equals(type));

		// sad

		byte[] unknownBytes = readFile(unknownBytesPath);
		String mimetype = jMimeMagicDetector.detectByMagic(unknownBytes);
		assertTrue(JMIME_DEFAULT.equals(mimetype));

		mimetype = jMimeMagicDetector.detectWithFilename(unknownBytes, unknownBytesPath.getFileName().toString());
		assertTrue(JMIME_DEFAULT.equals(mimetype));

		byte[] nogood = null;
		mimetype = jMimeMagicDetector.detectWithFilename(nogood, null);
		assertNull(mimetype);
	}

}
