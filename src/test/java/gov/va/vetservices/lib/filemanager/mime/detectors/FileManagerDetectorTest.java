package gov.va.vetservices.lib.filemanager.mime.detectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;

public class FileManagerDetectorTest {

	private final byte[] bytes = "This is a test".getBytes();
	private final String mimetype = ConvertibleTypesEnum.TXT.getMimeString();
	private final String filename = "test.txt";

	FileManagerDetector fileManagerDetector;

	@Before
	public void setUp() throws Exception {
		fileManagerDetector = new FileManagerDetector();
		assertNotNull(fileManagerDetector);
	}

	@Test
	public final void testDetectByMagic() throws IOException {
		// FileManagerDetector ALWAYS returns null for this method
		assertNull(fileManagerDetector.detectByMagic(bytes));
	}

	@Test
	public final void testDetectWithFilename() throws IOException {
		// more exhaustive test is performed in MimeTypeDetectorTest
		String type = fileManagerDetector.detectWithFilename(bytes, filename);
		assertNotNull(type);
		assertTrue(mimetype.equals(type));
	}

}
