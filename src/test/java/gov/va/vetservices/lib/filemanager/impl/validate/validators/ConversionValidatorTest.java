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
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
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
		FileManagerRequest request = new FileManagerRequest();
		FileDto fileDto = new FileDto();
		fileDto.setFilename(FILE_PATH_GOOD);
		fileDto.setFilebytes(super.readFile(Paths.get(FILE_PATH_GOOD)));
		request.setClaimId("11111");
		request.setDocTypeId("123");
		request.setProcessType(ProcessType.CLAIMS_526);
		request.setFileDto(fileDto);
		ImplDto implDto = FileManagerUtils.makeImplDto(request);
		List<Message> messages = conversionValidator.validate(new ImplArgDto<ImplDto>(implDto));
		// NOSONAR TODO when code is implemented, fix this test and add bad tests
		assertNotNull(messages);
	}

}
