package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.google.common.base.Strings;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

@RunWith(SpringRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS,
		AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class FilenameValidatorTest {

	private static final String FILENAME = "test.txt";
	private static final String FILENAME_EMPTY = "";
	private String FILENAME_TOO_LONG;
	private String FILENAME_LONG;

	private ImplArgDto<ImplDto> arg;
	private List<Message> messages;

	@Autowired
	@Qualifier(FilenameValidator.BEAN_NAME)
	private FilenameValidator filenameValidator;

//	@Autowired
//	FileManagerProperties fileManagerProperties;

	@Before
	public void setUp() throws Exception {
		FILENAME_TOO_LONG = Strings.repeat("x", 256 - 4) + ".txt"; // fileManagerProperties.getMaxFilenameLen()) + ".pdf";
		FILENAME_LONG = Strings.repeat("g", 255 - 4) + ".txt"; // fileManagerProperties.getMaxFilenameLen() - 4) + ".pdf";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testValidate() {
		FileManagerRequest request = new FileManagerRequest();
		FileDto dto = new FileDto();
		request.setFileDto(dto);

		// happy

		dto.setFilename(FILENAME);
		arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(request));
		messages = filenameValidator.validate(arg);
		assertNull(messages);

		dto.setFilename(FILENAME_LONG);
		arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(request));
		messages = filenameValidator.validate(arg);
		assertNull(messages);

		// sad

		dto.setFilename(null);
		arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(request));
		messages = filenameValidator.validate(arg);
		assertNotNull(messages);

		dto.setFilename(FILENAME_EMPTY);
		arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(request));
		messages = filenameValidator.validate(arg);
		assertNotNull(messages);

		dto.setFilename(FILENAME_TOO_LONG);
		arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(request));
		messages = filenameValidator.validate(arg);
		assertNotNull(messages);

		messages = filenameValidator.validate(null);
		assertNotNull(messages);
		assertTrue(messages.size() == 1);
		assertTrue(MessageKeysEnum.UNEXPECTED_ERROR.getKey().equals(messages.get(0).getKey()));

		arg = new ImplArgDto<>(null);
		messages = filenameValidator.validate(null);
		assertNotNull(messages);
		assertTrue(messages.size() > 0);
		assertTrue(MessageKeysEnum.UNEXPECTED_ERROR.getKey().equals(messages.get(0).getKey()));

		arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(null));
		messages = filenameValidator.validate(arg);
		assertNotNull(messages);
		assertTrue(messages.size() > 0);
		assertTrue(messages.get(0).getKey().equals(MessageKeysEnum.FILE_DTO_NULL.getKey()));

		dto.setFilename("./bad.file");
		arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(request));
		messages = filenameValidator.validate(arg);
		assertNotNull(messages);
		assertTrue(messages.size() > 0);
		assertTrue(messages.get(0).getKey().equals(MessageKeysEnum.FILE_NAME_MALFORMED.getKey()));
	}

}
