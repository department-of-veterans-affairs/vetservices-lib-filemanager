package gov.va.vetservices.lib.filemanager.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Arrays;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

public class FileManagerUtilsTest {

	private static final String PATH_RELATIVE = AbstractFileHandler.DEFAULT_INPUT_DIR;
	private static final String FILE_RELATIVE = "README.txt";

	private static final String BLANK_STRING = "   ";
	private static final String CLAIM_ID = "11111";
	private static final String DOCTYPE_ID = "123";

	@Test
	public final void testFileManagerUtils() {
		FileManagerUtils fmu = null;
		try {
			fmu = new FileManagerUtils();
		} catch (Throwable e) {
			assertTrue("Attempt to instantiate FileManagerUtils did not fail as expected.",
					IllegalAccessError.class.equals(e.getClass()));
		}
		assertNull("Should not have been able to instantiate FileManagerUtils.", fmu);
	}

	@Test
	public final void testHasFilename() {
		// happy
		assertTrue(FileManagerUtils.hasFilename(FILE_RELATIVE));
		assertTrue(FileManagerUtils.hasFilename(Paths.get(PATH_RELATIVE + FILE_RELATIVE).toFile().getName()));

		// sad
		assertFalse(FileManagerUtils.hasFilename("/" + FILE_RELATIVE));
		assertFalse(FileManagerUtils.hasFilename(BLANK_STRING));
		assertFalse(FileManagerUtils.hasFilename(null));
	}

	@Test
	public final void testHasBytes() {
		// happy
		assertTrue(FileManagerUtils.hasBytes(new byte[] { 64, 65, 66 }));

		// sad
		assertFalse(FileManagerUtils.hasBytes(null));
		assertFalse(FileManagerUtils.hasBytes(new byte[] {}));
	}

	@Test
	public final void testGetFileParts() {
		String filename = FILE_RELATIVE.split("\\.")[0];
		String extension = FILE_RELATIVE.split("\\.")[1];

		// happy
		FilePartsDto parts = FileManagerUtils.getFileParts(FILE_RELATIVE);
		assertNotNull(parts);
		assertTrue(filename.equals(parts.getName()));
		assertTrue(extension.equals(parts.getExtension()));

		parts = FileManagerUtils.getFileParts(filename);
		assertTrue(filename.equals(parts.getName()));
		assertNull(parts.getExtension());

		parts = FileManagerUtils.getFileParts("." + extension);
		assertNull(parts.getName());
		assertTrue(extension.equals(parts.getExtension()));

		// sad
		parts = FileManagerUtils.getFileParts(null);
		assertNotNull(parts);
		assertNull(parts.getName());
		assertNull(parts.getExtension());

		parts = FileManagerUtils.getFileParts(BLANK_STRING);
		assertNotNull(parts);
		assertNull(parts.getName());
		assertNull(parts.getExtension());
	}

	@Test
	public final void testMakeImplDto() {
		FileManagerRequest request = new FileManagerRequest();
		request.setClaimId(CLAIM_ID);
		request.setDocTypeId(DOCTYPE_ID);
		request.setProcessType(ProcessType.CLAIMS_526);
		FileDto fdto = new FileDto();
		fdto.setFilename("test filename.txt");
		fdto.setFilebytes(new byte[] { 33, 34, 35, 36 });
		request.setFileDto(fdto);

		ImplDto implDto = FileManagerUtils.makeImplDto(request);

		assertNotNull("ImplDto is unexpectedly null.", implDto);

		assertNotNull("implDto.docMetadataDto is null.", implDto.getDocMetadataDto());
		assertTrue("ClaimId does not match.", CLAIM_ID.equals(implDto.getDocMetadataDto().getClaimId()));
		assertTrue("ProcessType does not match.", ProcessType.CLAIMS_526.equals(implDto.getDocMetadataDto().getProcessType()));
		assertTrue("DocTypeId does not match.", DOCTYPE_ID.equals(implDto.getDocMetadataDto().getDocTypeId()));

		assertNotNull("ImplDto.fileDto is null.", implDto.getOriginalFileDto());
		assertEquals("Filenames do not match.", implDto.getOriginalFileDto().getFilename(), fdto.getFilename());
		assertTrue("Filebytes do not match.", Arrays.areEqual(implDto.getOriginalFileDto().getFilebytes(), fdto.getFilebytes()));
		assertNotNull("FilePartsDto is null.", implDto.getFileParts());
		assertFalse("FilePartsDto.name is null or empty.", StringUtils.isBlank(implDto.getFileParts().getName()));
		assertFalse("FilePartsDto.extension is null or empty.", StringUtils.isBlank(implDto.getFileParts().getExtension()));
	}

	@Test
	public final void testMakeImplDto_NullFileDto() {
		ImplDto implDto = FileManagerUtils.makeImplDto(null);

		assertNotNull("ImplDto is unexpectedly null.", implDto);
		assertNull("ImplDto.fileDto should be null.", implDto.getOriginalFileDto());
		assertNull("ImplDto.fileParts should be null.", implDto.getFileParts());
	}

	@Test
	public final void testSplitOnLastOf() {
		final char c = '/';
		final String empty = "";
		final String blank = "   ";
		final String bstringb = " string ";
		final String string = "string";
		final String cstring = "/string";
		final String stringc = "string/";
		final String strcing = "str/ing";
		final String strcingcses = "str/ing/ses";

		String[] parts = FileManagerUtils.splitOnLastOf(null, c);
		assertNotNull(parts);
		assertTrue(parts.length == 2);
		System.out.println("[\"" + parts[0] + "\", \"" + parts[1] + "\"]");
		assertTrue(parts[0] == null);
		assertTrue(parts[1] == null);

		parts = FileManagerUtils.splitOnLastOf(empty, c);
		assertNotNull(parts);
		assertTrue(parts.length == 2);
		System.out.println("[\"" + parts[0] + "\", \"" + parts[1] + "\"]");
		assertTrue(parts[0] == null);
		assertTrue(parts[1] == null);

		parts = FileManagerUtils.splitOnLastOf(blank, c);
		assertNotNull(parts);
		assertTrue(parts.length == 2);
		System.out.println("[\"" + parts[0] + "\", \"" + parts[1] + "\"]");
		assertTrue(parts[0] == null);
		assertTrue(parts[1] == null);

		parts = FileManagerUtils.splitOnLastOf(bstringb, c);
		assertNotNull(parts);
		assertTrue(parts.length == 2);
		System.out.println("[\"" + parts[0] + "\", \"" + parts[1] + "\"]");
		assertTrue(bstringb.equals(parts[0]));
		assertTrue(parts[1] == null);

		parts = FileManagerUtils.splitOnLastOf(string, c);
		assertNotNull(parts);
		assertTrue(parts.length == 2);
		System.out.println("[\"" + parts[0] + "\", \"" + parts[1] + "\"]");
		assertTrue(string.equals(parts[0]));
		assertTrue(parts[1] == null);

		parts = FileManagerUtils.splitOnLastOf(cstring, c);
		assertNotNull(parts);
		assertTrue(parts.length == 2);
		System.out.println("[\"" + parts[0] + "\", \"" + parts[1] + "\"]");
		assertTrue(empty.equals(parts[0]));
		assertTrue(cstring.endsWith(parts[1]));

		parts = FileManagerUtils.splitOnLastOf(stringc, c);
		assertNotNull(parts);
		assertTrue(parts.length == 2);
		System.out.println("[\"" + parts[0] + "\", \"" + parts[1] + "\"]");
		assertTrue(stringc.startsWith(parts[0]));
		assertTrue(empty.equals(parts[1]));

		parts = FileManagerUtils.splitOnLastOf(strcing, c);
		assertNotNull(parts);
		assertTrue(parts.length == 2);
		System.out.println("[\"" + parts[0] + "\", \"" + parts[1] + "\"]");
		assertTrue("str".equals(parts[0]));
		assertTrue("ing".equals(parts[1]));

		parts = FileManagerUtils.splitOnLastOf(strcingcses, c);
		assertNotNull(parts);
		assertTrue(parts.length == 2);
		System.out.println("[\"" + parts[0] + "\", \"" + parts[1] + "\"]");
		assertTrue("str/ing".equals(parts[0]));
		assertTrue("ses".equals(parts[1]));

	}

}
