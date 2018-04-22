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

	private InterrogateFile interrogateFile = new InterrogateFile();
	FileDto filedto;
	ValidatorDto vdto;

	@Before
	public void setUp() {
		assertNotNull(interrogateFile);
	}

	@Test
	public final void testCanConvertToPdf() {
		// heavy testing of the features used by InterrogateFile is done in other tests

		filedto = new FileDto();
		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME);
		vdto = FileManagerUtils.makeValidatorDto(filedto);
		assertNotNull(vdto);

		FileManagerResponse response = interrogateFile.canConvertToPdf(vdto);
		assertNotNull(response);
		assertTrue(response.getMessages().size() == 0);
		assertNull(response.getFileDto());
	}

}
