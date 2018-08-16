package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS,
		AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class SimpleRequestValidatorTest extends AbstractFileHandler {

	@Autowired
	SimpleRequestValidator simpleRequestValidator;

	@Before
	public void setUp() throws Exception {
		assertNotNull(simpleRequestValidator);
	}

	@Test
	public final void testValidate() {
		/* dev note: modified values in the fmr object must be reset for each new scenario */

		// happy
		final FileManagerRequest fmr = new FileManagerRequest();
		fmr.setClaimId("11111");
		fmr.setDocTypeId("532");
		final FileDto file = new FileDto();
		try {
			file.setFilebytes(super.readFile(Paths.get("files/application/pdf/IS_text-doc.pdf")));
		} catch (final IOException e) {
			e.printStackTrace();
			fail("Should not throw exception here.");
		}
		file.setFilename("IS_text-doc.pdf");
		fmr.setFileDto(file);
		fmr.setProcessType(ProcessType.CLAIMS_526_SUPPORTING);
		final ImplArgDto<FileManagerRequest> toValidate = new ImplArgDto<>(fmr);
		List<Message> messages = simpleRequestValidator.validate(toValidate);
		assertTrue(messages == null || messages.isEmpty());

		// null request
		messages = simpleRequestValidator.validate(null);
		assertTrue(messages != null && !messages.isEmpty() && messages.size() == 1);
		assertTrue(LibFileManagerMessageKeys.REQUEST_NULL.equals(messages.get(0).getKey()));

		// no docTypeId, no processtype
		fmr.setDocTypeId("");
		fmr.setProcessType(null);
		messages = simpleRequestValidator.validate(toValidate);
		assertTrue(messages != null && !messages.isEmpty());
		int errors = 0;
		for (final Message m : messages) {
			if (LibFileManagerMessageKeys.DOCTYPEID_NULL_OR_EMPTY.equals(m.getKey())
					|| LibFileManagerMessageKeys.PROCESSTYPE_NOT_SPECIFIED.equals(m.getKey())) {
				errors++;
			}
		}
		assertTrue("errors = " + errors, errors == 2);

		// no claim id
		fmr.setDocTypeId("532");
		fmr.setProcessType(ProcessType.CLAIMS_526_SUPPORTING);
		fmr.setClaimId(null);
		messages = simpleRequestValidator.validate(toValidate);
		assertTrue(messages != null && !messages.isEmpty());
		errors = 0;
		for (final Message m : messages) {
			if (LibFileManagerMessageKeys.CLAIMID_NULL_OR_EMPTY.equals(m.getKey())) {
				errors++;
			}
		}
		assertTrue("errors = " + errors, errors == 1);

		// no fileDto
		fmr.setClaimId("11111");
		fmr.setFileDto(null);
		messages = simpleRequestValidator.validate(toValidate);
		assertTrue(messages != null && !messages.isEmpty() && messages.size() == 1);
		assertTrue(LibFileManagerMessageKeys.FILE_DTO_NULL.equals(messages.get(0).getKey()));

		// no filename
		final FileDto fileEmpty = new FileDto();
		fileEmpty.setFilebytes(new byte[] {});
		fmr.setFileDto(fileEmpty);
		messages = simpleRequestValidator.validate(toValidate);
		assertTrue(messages != null && !messages.isEmpty());
		errors = 0;
		for (final Message m : messages) {
			if (LibFileManagerMessageKeys.FILE_NAME_NULL_OR_EMPTY.equals(m.getKey())
					|| LibFileManagerMessageKeys.FILE_BYTES_NULL_OR_EMPTY.equals(m.getKey())) {
				errors++;
			}
		}
		assertTrue("errors = " + errors, errors == 2);

	}

}
