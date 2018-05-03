package gov.va.vetservices.lib.filemanager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ValidatorDto;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class InterrogateFileTest {

	private static final byte[] STRING_BYTES = "This is a test.".getBytes();
	private static final String STRING_FILENAME = "test.txt";
	private static final String STRING_FILENAME_NO_NAME = ".txt";
	private static final String STRING_FILENAME_NO_EXT = "test.";
	private static final String STRING_FILENAME_MALFORMED = "test..txt";
	private static final String STRING_FILENAME_UNSUPPORTED = "test.chat";

	private InterrogateFile interrogateFile = new InterrogateFile();
	FileDto filedto;
	ValidatorDto vdto;
	FileManagerResponse response;

	@Before
	public void setUp() {
		assertNotNull(interrogateFile);
	}

	@Test
	public final void testCanConvertToPdf() {

		// happy

		filedto = new FileDto();
		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME);
		vdto = FileManagerUtils.makeValidatorDto(filedto);
		assertNotNull(vdto);

		response = interrogateFile.canConvertToPdf(vdto);
		assertNotNull(response);
		// NOSONAR TODO line below will have to change when ConversionValidator is completed
		assertTrue(!response.getMessages().isEmpty() && response.getMessages().get(0).getKey().equals("UNFINISHED.WORK"));
		assertNull(response.getFileDto());

		// sad

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(null);
		response = interrogateFile.canConvertToPdf(vdto);
		assertNotNull(response);
		assertTrue(!response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(null);
		filedto.setFilename(STRING_FILENAME);
		response = interrogateFile.canConvertToPdf(vdto);
		assertNotNull(response);
		assertTrue(!response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_NO_NAME);
		response = interrogateFile.canConvertToPdf(vdto);
		assertNotNull(response);
		assertTrue(!response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_NO_EXT);
		response = interrogateFile.canConvertToPdf(vdto);
		assertNotNull(response);
		assertTrue(!response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_MALFORMED);
		response = interrogateFile.canConvertToPdf(vdto);
		assertNotNull(response);
		assertTrue(!response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_UNSUPPORTED);
		response = interrogateFile.canConvertToPdf(vdto);
		assertNotNull(response);
		assertTrue(!response.getMessages().isEmpty());
		assertNull(response.getFileDto());
	}

}
