package gov.va.vetservices.lib.filemanager.testutil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.activation.MimeType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;

public class AbstractFileHandlerTest {

	protected static final String PATH_ABSOLUTE = AbstractFileHandler.DEFAULT_OUTPUT_DIR;
	protected static final String PATH_RELATIVE = AbstractFileHandler.DEFAULT_INPUT_DIR;
	protected static final String FILE_EXTERNAL = "test.txt";
	protected static final String FILE_EXTERNAL_BAD = "doesnotexit.txt";
	protected static final String FILE_CLASSPATH = "README.txt";
	protected static final String FILE_CLASSPATH_BAD = "DOESNOTEXIST.txt";

	protected static final byte[] BYTE_ARRAY = "This is a test file.".getBytes();
	protected static final String BLANK_STRING = "  ";
	/** Raw mime type for unsupported files */
	protected static MimeType MIMETYPE_UNSUPPORTED;

	class FileHandlerTester extends AbstractFileHandler {
		// just use the methods from the abstract class
	}

	protected FileHandlerTester fileHandlerTester = new FileHandlerTester();

	@Before
	public void setUp() throws Exception {
		/*
		 * Change this to false to leave saved files in place after test run.
		 * !!!!! REMEMBER TO SET THIS BACK TO true BEFORE COMMITTING THE CODE !!!!!
		 */
		fileHandlerTester.TEST_DELETE_OUTPUTS_WHEN_DONE = true;

		MIMETYPE_UNSUPPORTED = new MimeType("application/x-chat");
	}

	@After
	public void tearDown() throws Exception {
		if (fileHandlerTester.TEST_DELETE_OUTPUTS_WHEN_DONE) {
			fileHandlerTester.deleteOutputFiles(Paths.get(PATH_ABSOLUTE));
		}
	}

	@Test
	public final void testIsAbsolutePath() {
		// happy
		assertTrue(fileHandlerTester.isAbsolutePath(Paths.get(PATH_ABSOLUTE)));
		assertTrue(fileHandlerTester.isAbsolutePath(Paths.get(PATH_ABSOLUTE + FILE_EXTERNAL)));

		// sad
		assertFalse(fileHandlerTester.isAbsolutePath(Paths.get(PATH_RELATIVE + FILE_CLASSPATH)));
		assertFalse(fileHandlerTester.isAbsolutePath(Paths.get(BLANK_STRING)));
		assertFalse(fileHandlerTester.isAbsolutePath(null));
	}

	@Test
	public final void testIsRelativePath() {
		// happy
		assertTrue(fileHandlerTester.isRelativePath(Paths.get(PATH_RELATIVE)));
		assertTrue(fileHandlerTester.isRelativePath(Paths.get(PATH_RELATIVE + FILE_CLASSPATH)));

		// sad
		assertFalse(fileHandlerTester.isRelativePath(Paths.get(PATH_ABSOLUTE + FILE_EXTERNAL)));
		assertFalse(fileHandlerTester.isRelativePath(Paths.get(BLANK_STRING)));
		assertFalse(fileHandlerTester.isRelativePath(null));
	}

	@Test
	public final void testCreateOutputPath() throws IOException {
		// happy
		fileHandlerTester.deleteOutputFiles(Paths.get(PATH_ABSOLUTE));
		assertTrue(fileHandlerTester.createOutputPath(Paths.get(PATH_ABSOLUTE)));
		fileHandlerTester.deleteOutputFiles(Paths.get(PATH_ABSOLUTE));
		assertTrue(fileHandlerTester.createOutputPath(Paths.get(PATH_ABSOLUTE + FILE_EXTERNAL)));

		// sad
		fileHandlerTester.deleteOutputFiles(Paths.get(PATH_ABSOLUTE));
		assertFalse(fileHandlerTester.createOutputPath(Paths.get(PATH_RELATIVE)));
		fileHandlerTester.deleteOutputFiles(Paths.get(PATH_ABSOLUTE));
		assertFalse(fileHandlerTester.createOutputPath(Paths.get(PATH_RELATIVE + FILE_CLASSPATH)));
		fileHandlerTester.deleteOutputFiles(Paths.get(PATH_ABSOLUTE));
		assertFalse(fileHandlerTester.createOutputPath(Paths.get(BLANK_STRING)));
		fileHandlerTester.deleteOutputFiles(Paths.get(PATH_ABSOLUTE));
		assertFalse(fileHandlerTester.createOutputPath(null));
	}

	@Test
	public final void testDeleteOutputFiles() throws IOException {
		// happy
		assertTrue(fileHandlerTester.deleteOutputFiles(Paths.get(PATH_ABSOLUTE)));
		assertTrue(fileHandlerTester.deleteOutputFiles(Paths.get(PATH_ABSOLUTE + FILE_EXTERNAL)));

		// sad
		assertFalse(fileHandlerTester.deleteOutputFiles(Paths.get(PATH_RELATIVE)));
		assertFalse(fileHandlerTester.deleteOutputFiles(Paths.get(PATH_RELATIVE + FILE_CLASSPATH)));
		assertFalse(fileHandlerTester.deleteOutputFiles(Paths.get(BLANK_STRING)));
		assertFalse(fileHandlerTester.deleteOutputFiles(null));
	}

	@Test
	public final void testListFilesByMimePath() {
		for (ConvertibleTypesEnum enumeration : ConvertibleTypesEnum.values()) {
			List<File> list = fileHandlerTester.listFilesByMimePath(enumeration.getMimeType());
			assertNotNull(list);
			assertTrue(!list.isEmpty());
		}
	}

	@Test
	public final void testGetFilesByMimePath_invalidPath() {
		List<File> nulled = fileHandlerTester.listFilesByMimePath(null);
		assertNull(nulled);

		List<File> unsupported = fileHandlerTester.listFilesByMimePath(MIMETYPE_UNSUPPORTED);
		assertNull(unsupported);
	}

	@Test
	public final void testReadPath() throws FileManagerException {
		FileHandlerTester fileHandlerTester = new FileHandlerTester();
		assertSuccessfulRead(fileHandlerTester, Paths.get(PATH_RELATIVE + FILE_CLASSPATH));
		// make sure file is on the disc
		fileHandlerTester.saveFile(BYTE_ARRAY, PATH_ABSOLUTE + FILE_EXTERNAL);
		assertSuccessfulRead(fileHandlerTester, Paths.get(PATH_ABSOLUTE + FILE_EXTERNAL));
	}

	@Test
	public final void testReadPath_BadFilename() throws IOException {
		FileHandlerTester fileHandlerTester = new FileHandlerTester();
		assertFailedRead(fileHandlerTester, null);
		assertFailedRead(fileHandlerTester, Paths.get(BLANK_STRING));
		assertFailedRead(fileHandlerTester, Paths.get(FILE_CLASSPATH_BAD));
		// make sure bad file is NOT on the disc
		fileHandlerTester.deleteOutputFiles(Paths.get(PATH_ABSOLUTE));
		assertFailedRead(fileHandlerTester, Paths.get(FILE_EXTERNAL_BAD));
	}

	/**
	 * Read the file and perform assertions that expect successful FileLoader operations.
	 *
	 * @param path
	 */
	private void assertSuccessfulRead(AbstractFileHandler fileHandler, Path path) {
		byte[] bytes = read(fileHandler, path);
		assertNotNull("Returned byte array from " + path + " was null", bytes);
		assertTrue("Returned byte array from " + path + " had no content", bytes.length > 0);
	}

	/**
	 * Read the file and perform assertions that expect failed FileLoader operations.
	 *
	 * @param path
	 */
	private void assertFailedRead(AbstractFileHandler fileHandler, Path path) {
		byte[] bytes = read(fileHandler, path);
		assertNull("Returned byte array from " + path + " was not null", bytes);
	}

	/**
	 * Attempt to read the file and return the bytes.
	 * No assertions (other than fail on exception).
	 *
	 * @param path
	 * @return byte[]
	 */
	private byte[] read(AbstractFileHandler fileHandler, Path path) {
		byte[] bytes = null;
		try {
			bytes = fileHandler.readFile(path);
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
		return bytes;
	}

	@Test
	public final void testSaveFile() throws FileManagerException, IOException {
		assertNotNull(fileHandlerTester.saveFile(BYTE_ARRAY, FILE_EXTERNAL));
		assertTrue(Paths.get(PATH_ABSOLUTE + FILE_EXTERNAL).toFile().exists());
		fileHandlerTester.deleteOutputFiles(Paths.get(PATH_ABSOLUTE));
	}

	@Test
	public final void testSaveFile_BadFile() throws FileManagerException, IOException {
		assertNull(fileHandlerTester.saveFile(null, FILE_EXTERNAL));
		assertNull(fileHandlerTester.saveFile(BYTE_ARRAY, null));
	}
}
