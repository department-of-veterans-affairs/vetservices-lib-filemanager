package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.impl.FileManagerImpl;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;
import gov.va.vetservices.lib.filemanager.util.MessageUtils;

public class ConversionValidatorTest extends AbstractFileHandler {

	private static final String FILE_PATH_GOOD = "files/application/pdf/IS_text-doc.pdf";

	@Mock
	private ConversionValidator conversionValidator = new ConversionValidator();
	
	@Mock
	private MessageUtils messageUtils;
	
	@Mock
	private ImplDto implDtoMock;
	
	@Mock
	private ImplArgDto<ImplDto> implArgDtoMock = new ImplArgDto<ImplDto>(implDtoMock);

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Ignore
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
	
		//when(conversionValidator.validate(any(ImplArgDto<ImplDto>(implDto)).getClass())).thenReturn(null);
		List<Message> messages = conversionValidator.validate(new ImplArgDto<ImplDto>(implDto));
		// NOSONAR TODO when code is implemented, fix this test and add bad tests
		assertNull(messages);
	}

	@Test
	public final void testValidate_Sad() throws IOException {

		List<Message> msgList = new ArrayList<>();
		msgList.add(new Message());
		
		List<Message> messages = conversionValidator.validate(null);
		assertNotNull(messages);
		
		messages = conversionValidator.validate(new ImplArgDto<ImplDto>(null));
		assertNotNull(messages);

	}
}
