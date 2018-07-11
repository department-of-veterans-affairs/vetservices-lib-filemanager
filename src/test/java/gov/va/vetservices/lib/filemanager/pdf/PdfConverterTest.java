package gov.va.vetservices.lib.filemanager.pdf;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.mime.MimeTypeDetector;
import gov.va.vetservices.lib.filemanager.pdf.convert.ImageConverter;
import gov.va.vetservices.lib.filemanager.pdf.convert.TextConverter;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;
import gov.va.vetservices.lib.filemanager.testutil.TestingConstants;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

@RunWith(MockitoJUnitRunner.class)
public class PdfConverterTest extends AbstractFileHandler {

	private static final String BYTES_RESPONSE = "Success!";
	private static final String[] FILENAME_PREFIXES = { "IS_", "NOT_", "BAD_" };
	private static final String FILE_UNSUPPORTED = "files/application/stl/CAD_Model.stl";

	@Mock
	private final ImageConverter imageConverter = new ImageConverter();

	@Mock
	private final TextConverter textConverter = new TextConverter();

	@Mock
	MimeTypeDetector detector;

	@InjectMocks
	@Spy
	PdfConverter converter;

	@Before
	public void setUp() throws Exception {
		converter = new PdfConverter();
		assertNotNull(converter);
	}

	@Test
	public final void testConvert() {
		try {
			when(imageConverter.getPdf(any(byte[].class), any(FilePartsDto.class))).thenReturn(BYTES_RESPONSE.getBytes());
			when(textConverter.getPdf(any(byte[].class), any(FilePartsDto.class))).thenReturn(BYTES_RESPONSE.getBytes());
		} catch (final FileManagerException e) {
			e.printStackTrace();
			fail("Mock failed.");
		}

		for (final ConvertibleTypesEnum enumeration : ConvertibleTypesEnum.values()) {
			final List<File> files = super.listFilesByMimePath(enumeration.getMimeType());
			assertTrue("Files for " + enumeration.getMimeString() + " is null or empty.", files != null && !files.isEmpty());

			// we have to detect every file in the directory
			// to confirm that we can detect the variations of that file type
			for (final File file : files) {
				if (!file.exists()) {
					fail("File enumerated by " + super.getClass().getSimpleName() + ".getFilesByMimePath() returned non-existent file "
							+ file.getPath());
				}

				if (TestingConstants.PRINT) {
					System.out.println(file.getName());
				}

				try {
					final byte[] readBytes = Files.readAllBytes(file.toPath());
					final FilePartsDto parts = FileManagerUtils.getFileParts(file.getName());

					converter.convert(readBytes, parts);

				} catch (final Throwable e) {
					assertExpectedException(e, file.getName());
				}
			}
		}
	}

	@Test
	public final void testConvert_Bad() {
		// unsupported mimetype
		byte[] bytes = null;
		try {
			bytes = readFile(Paths.get(FILE_UNSUPPORTED));
		} catch (final IOException e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}

		FilePartsDto parts = FileManagerUtils.getFileParts(Paths.get(FILE_UNSUPPORTED).toFile().getName());
		try {
			converter.convert(bytes, parts);
			fail("converter.convert() should have thrown exception");
		} catch (final FileManagerException e) {
			assertNotNull(e);
		}

		// mock to force throwing default PdfConverterException from convert()
		parts = new FilePartsDto();
		parts.setName("techdraw");
		parts.setExtension("cgm");
		try {
			converter.convert(super.readFile(Paths.get("files/image/cgm/techdraw.cgm")), parts);
			fail("Should have thrown exception.");
		} catch (final Exception e) {
			assertTrue(FileManagerException.class.isAssignableFrom(e.getClass()));
			assertTrue(e.getMessage().contains("connot be converted"));
		}

	}

	private void assertExpectedException(Throwable e, final String filename) throws AssertionError {

		int position = -1;
		for (int i = 0; i < FILENAME_PREFIXES.length; i++) {
			if (filename.contains(FILENAME_PREFIXES[i])) {
				position = i;
			}
		}

		if (FileManagerException.class.isAssignableFrom(e.getClass()) && e.getCause() != null) {
			e = e.getCause();
		}
		if (IllegalArgumentException.class.isAssignableFrom(e.getClass())) {
			return;
		}

		switch (position) {
		case 0: // IS
			throw new AssertionError(filename + " should not have thrown an exception.");
		case 1: // NOT
			e.printStackTrace();
			// would expect an exception, it's all good
			break;
		case 2: // BAD
			e.printStackTrace();
			// would expect an exception, it's all good
			break;
		default:
			fail(filename + " does not have a valid testing prefix. Should be one of " + Arrays.toString(FILENAME_PREFIXES));
			break;
		}
	}
}
