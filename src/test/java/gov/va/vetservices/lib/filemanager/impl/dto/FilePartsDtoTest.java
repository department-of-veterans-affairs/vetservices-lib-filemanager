package gov.va.vetservices.lib.filemanager.impl.dto;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FilePartsDtoTest {

	private static final String NAME = "test";
	private static final String EXT = "pdf";

	FilePartsDto filePartsDto = new FilePartsDto();

	@Test
	public final void testSetGetName() {
		filePartsDto.setName(NAME);
		assertTrue(NAME.equals(filePartsDto.getName()));
	}

	@Test
	public final void testSetGetExtension() {
		filePartsDto.setExtension(EXT);
		assertTrue(EXT.equals(filePartsDto.getExtension()));
	}
}
