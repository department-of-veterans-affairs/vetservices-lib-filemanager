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
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.pdf.convert.ImageConverter;
import gov.va.vetservices.lib.filemanager.pdf.convert.TextConverter;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

@RunWith(MockitoJUnitRunner.class)
public class PdfConverterTest extends AbstractFileHandler {

	private static final String BYTES_RESPONSE = "Success!";
	private static final String[] FILENAME_PREFIXES = { "IS_", "NOT_", "BAD_" };
	private static final String FILE_UNSUPPORTED = "files/application/stl/CAD_Model.stl";

	@Mock
	private ImageConverter imageConverter = new ImageConverter();

	@Mock
	private TextConverter textConverter = new TextConverter();

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
		} catch (FileManagerException e) {
			e.printStackTrace();
			fail("Mock failed.");
		}

		for (ConvertibleTypesEnum enumeration : ConvertibleTypesEnum.values()) {
			List<File> files = super.listFilesByMimePath(enumeration.getMimeType());
			assertTrue("Files for " + enumeration.getMimeString() + " is null or empty.", (files != null) && !files.isEmpty());

			// we have to detect every file in the directory
			// to confirm that we can detect the variations of that file type
			for (File file : files) {
				if (!file.exists()) {
					fail("File enumerated by " + super.getClass().getSimpleName() + ".getFilesByMimePath() returned non-existent file "
							+ file.getPath());
				}

				System.out.println(file.getName());

				try {
					byte[] readBytes = Files.readAllBytes(file.toPath());
					FilePartsDto parts = FileManagerUtils.getFileParts(file.getName());

					converter.convert(readBytes, parts);

				} catch (Throwable e) {
					assertExpectedException(e, file.getName());
				}
			}
		}
	}

	@Test
	public final void testConvert_Bad() {
		byte[] bytes = null;
		try {
			bytes = readFile(Paths.get(FILE_UNSUPPORTED));
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}

		FilePartsDto parts = FileManagerUtils.getFileParts(Paths.get(FILE_UNSUPPORTED).toFile().getName());
		try {
			converter.convert(bytes, parts);
			fail("converter.convert() should have thrown exception");
		} catch (FileManagerException e) {
			assertNotNull(e);
		}
	}

	private void assertExpectedException(Throwable e, String filename) throws AssertionError {

		int position = -1;
		for (int i = 0; i < FILENAME_PREFIXES.length; i++) {
			if (filename.contains(FILENAME_PREFIXES[i])) {
				position = i;
			}
		}

		if (FileManagerException.class.isAssignableFrom(e.getClass()) && (e.getCause() != null)) {
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
