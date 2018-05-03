package gov.va.vetservices.lib.filemanager.impl.dto;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;

public class ImplDtoTest {

	ImplDto idto;

	@Before
	public void setUp() throws Exception {
		idto = new ImplDto();
		assertNotNull(idto);
	}

	@Test
	public final void testImplDto() {
		assertTrue((idto.getMessages() != null) && idto.getMessages().isEmpty());

		idto.setFileDto(new FileDto());
		assertNotNull(idto.getFileDto());
		idto.setFileDto(null);
		assertNull(idto.getFileDto());

		idto.setFileParts(new FilePartsDto());
		assertNotNull(idto.getFileParts());
		idto.setFileParts(null);
		assertNull(idto.getFileParts());

		assertTrue((idto.getMessages() != null) && idto.getMessages().isEmpty());
	}

}
