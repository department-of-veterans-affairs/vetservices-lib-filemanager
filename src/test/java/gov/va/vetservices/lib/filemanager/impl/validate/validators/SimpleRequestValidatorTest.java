package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

public class SimpleRequestValidatorTest extends AbstractFileHandler {

	SimpleRequestValidator simpleRequestValidator;

	@Before
	public void setUp() throws Exception {
		simpleRequestValidator = new SimpleRequestValidator();
		assertNotNull(simpleRequestValidator);
	}

	@Test
	public final void testValidate() {
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
		simpleRequestValidator.validate(toValidate);

		// no docTypeId, no processtype, no FileDto
		fmr.setDocTypeId("");
		fmr.setProcessType(null);
		fmr.setFileDto(null);
		simpleRequestValidator.validate(toValidate);

	}

}
