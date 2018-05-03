package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ValidatorDto;
import gov.va.vetservices.lib.filemanager.impl.validate.ValidatorArg;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class ConversionValidatorTest extends AbstractFileHandler {

	private static final String FILE_PATH_GOOD = "files/application/pdf/IS_text-doc.pdf";

	private ConversionValidator conversionValidator = new ConversionValidator();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testValidate() throws IOException {
		FileDto fileDto = new FileDto();
		fileDto.setFilename(FILE_PATH_GOOD);
		fileDto.setFilebytes(super.readFile(Paths.get(FILE_PATH_GOOD)));
		ValidatorDto vdto = FileManagerUtils.makeValidatorDto(fileDto);
		List<Message> messages = conversionValidator.validate(new ValidatorArg<ValidatorDto>(vdto));
		// NOSONAR TODO when code is implemented, fix this test and add bad tests
		assertNotNull(messages);
	}

}
