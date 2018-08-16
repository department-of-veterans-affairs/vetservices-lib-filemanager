package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;
import gov.va.vetservices.lib.filemanager.util.MessageUtils;

@RunWith(SpringRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS,
		AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class ConversionValidatorTest extends AbstractFileHandler {

	private static final String FILE_PATH_GOOD = "files/application/pdf/IS_text-doc.pdf";

	@Autowired
	@Qualifier(ConversionValidator.BEAN_NAME)
	private ConversionValidator conversionValidator = new ConversionValidator();

	@Autowired
	@Qualifier(MessageUtils.BEAN_NAME)
	private MessageUtils messageUtils;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

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

		// when(conversionValidator.validate(any(ImplArgDto<ImplDto>(implDto)).getClass())).thenReturn(null);
		List<Message> messages = conversionValidator.validate(new ImplArgDto<ImplDto>(implDto));
		// NOSONAR TODO when code is implemented, fix this test and add bad tests
		assertTrue(messages == null || messages.isEmpty());
	}

	@Test
	public final void testValidate_Sad() throws IOException {

		List<Message> messages = conversionValidator.validate(null);
		assertNotNull(messages);

		messages = conversionValidator.validate(new ImplArgDto<ImplDto>(null));
		assertNotNull(messages);

	}
}
