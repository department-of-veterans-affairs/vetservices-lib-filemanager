package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;
import gov.va.vetservices.lib.filemanager.testutil.TestingConstants;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS,
		AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@TestExecutionListeners(inheritListeners = false, listeners = { DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class FileTypeValidatorTest extends AbstractFileHandler {

	private static final byte[] bytesTxt = "This is a text/plain byte array".getBytes();
	private static final String filenameTxt = "plaintext.txt";
	private static final Path imageGoodPng = Paths.get("files/image/png/IS_16bit.png");
	private static final Path imageUnsupportedDwg = Paths.get("files/image/vnddwg/visualization-aerial.dwg");
	private static final String imageDwgMimeRaw = "image/vnddwg";
//	private static final Path imageUnsupportedDoc = Paths.get("files/application/msword/IS_Test.docx");
	private static final String imageDocMimeRaw = "application/msword";
	private static final String claimId = "11111";
	private static final String docTypeId = "123";

	@Autowired
	FileTypeValidator fileTypeValidator = new FileTypeValidator();

	private ImplArgDto<ImplDto> arg;
	private List<Message> messages;

	@Test
	public final void testValidate() {
		FileManagerRequest request = new FileManagerRequest();
		request.setClaimId(claimId);
		request.setDocTypeId(docTypeId);
		request.setProcessType(ProcessType.CLAIMS_526);
		FileDto dto = new FileDto();
		dto.setFilebytes(bytesTxt);
		dto.setFilename(filenameTxt);
		request.setFileDto(dto);

		arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(request));
		messages = fileTypeValidator.validate(arg);
		assertNull(messages);

		for (ConvertibleTypesEnum enumeration : ConvertibleTypesEnum.values()) {
			List<File> files = super.listFilesByMimePath(enumeration.getMimeType());
			assertTrue("Files for " + enumeration.getMimeString() + " is null or empty.", files != null && !files.isEmpty());

			// we have to detect every file in the directory
			// to confirm that we can detect the variations of that file type
			for (File file : files) {
				if (!file.exists()) {
					fail("File enumerated by " + super.getClass().getSimpleName() + ".getFilesByMimePath() returned non-existent file "
							+ file.getPath());
				}

				try {
					byte[] readBytes = Files.readAllBytes(file.toPath());
					dto = new FileDto();
					dto.setFilebytes(readBytes);
					dto.setFilename(file.getName());
					request.setFileDto(dto);
					arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(request));
					messages = fileTypeValidator.validate(arg);

					if (messages != null && (file.getName().contains(TestingConstants.TEST_FILE_PREFIX_CORRUPT)
							|| file.getName().contains(TestingConstants.TEST_FILE_PREFIX_ILLEGITIMATE))) {
						assertNotNull(file.getName() + " messages are null.", messages);
					} else {
						if (file.getName().startsWith(TestingConstants.TEST_FILE_PREFIX_LEGITIMATE)) {
							assertNull(file.getName() + " messages not null.", messages);
						} else if (file.getName().startsWith(TestingConstants.TEST_FILE_PREFIX_CORRUPT)) {
							assertNotNull(file.getName() + " messages are null.", messages);
						} else if (file.getName().startsWith(TestingConstants.TEST_FILE_PREFIX_ILLEGITIMATE)) {
							assertNotNull(file.getName() + " messages are null.", messages);
						}
					}

				} catch (IOException e) {
					fail("Could not read file: " + file.toPath());
				}
			}
		}
	}

	@Test
	public final void testIsImageConvertible() {
		// image - returns true
		byte[] bytes = null;
		try {
			bytes = super.readFile(imageGoodPng);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Could not read file: " + imageGoodPng.toString());
		}
		FileManagerRequest request = new FileManagerRequest();
		request.setClaimId(claimId);
		request.setDocTypeId(docTypeId);
		request.setProcessType(ProcessType.CLAIMS_526);
		FileDto fileDto = new FileDto();
		fileDto.setFilebytes(bytes);
		fileDto.setFilename(imageGoodPng.getFileName().toString());
		request.setFileDto(fileDto);
		ImplDto implDto = FileManagerUtils.makeImplDto(request);
		MimeType detectedMimetype = ConvertibleTypesEnum.PNG.getMimeType();

		boolean convertible = fileTypeValidator.isImageConvertible(implDto, detectedMimetype);
		assertTrue(convertible);

		// legitimate image, but unsupported - returns false
		try {
			bytes = super.readFile(imageUnsupportedDwg);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Could not read file: " + imageUnsupportedDwg.toString());
		}
		fileDto.setFilebytes(bytes);
		fileDto.setFilename(imageUnsupportedDwg.getFileName().toString());
		implDto = FileManagerUtils.makeImplDto(request);
		detectedMimetype = getMimeType(imageDwgMimeRaw);

		convertible = fileTypeValidator.isImageConvertible(implDto, detectedMimetype);
		assertTrue(!convertible);

		// not an image - always returns true
		fileDto.setFilebytes(bytesTxt);
		fileDto.setFilename(filenameTxt);
		implDto = FileManagerUtils.makeImplDto(request);
		detectedMimetype = ConvertibleTypesEnum.TXT.getMimeType();

		convertible = fileTypeValidator.isImageConvertible(implDto, detectedMimetype);
		assertTrue(convertible);
	}

	@Test
	public final void testIsImageConvertible_Bad() {
		// null ImplDto
		MimeType detectedMimetype = ConvertibleTypesEnum.PNG.getMimeType();
		boolean convertible = fileTypeValidator.isImageConvertible(null, detectedMimetype);
		assertTrue(!convertible);

		// null FileDto
		ImplDto implDto = new ImplDto();
		convertible = fileTypeValidator.isImageConvertible(implDto, detectedMimetype);
		assertTrue(!convertible);

		// null bytes
		FileManagerRequest request = new FileManagerRequest();
		request.setClaimId(claimId);
		request.setDocTypeId(docTypeId);
		request.setProcessType(ProcessType.CLAIMS_526);
		FileDto fileDto = new FileDto();
		fileDto.setFilename("null");
		request.setFileDto(fileDto);
		implDto = FileManagerUtils.makeImplDto(request);
		convertible = fileTypeValidator.isImageConvertible(implDto, detectedMimetype);
		assertTrue(!convertible);

		// empty bytes
		fileDto.setFilebytes(new byte[] {});
		fileDto.setFilename("empty");
		implDto = FileManagerUtils.makeImplDto(request);
		convertible = fileTypeValidator.isImageConvertible(implDto, detectedMimetype);
		assertTrue(!convertible);

		// invalid bytes
		fileDto.setFilebytes(new byte[] { 0 });
		fileDto.setFilename("byte-char-zero");
		implDto = FileManagerUtils.makeImplDto(request);
		convertible = fileTypeValidator.isImageConvertible(implDto, detectedMimetype);
		assertTrue(!convertible);

		// bytes that are not an image
		fileDto.setFilebytes(bytesTxt);
		fileDto.setFilename(filenameTxt);
		implDto = FileManagerUtils.makeImplDto(request);
		convertible = fileTypeValidator.isImageConvertible(implDto, detectedMimetype);
		assertTrue(!convertible);

		// detectedMimeType null
		byte[] bytes = null;
		try {
			bytes = super.readFile(imageGoodPng);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Could not read file: " + imageGoodPng.toString());
		}
		fileDto.setFilebytes(bytes);
		fileDto.setFilename(imageGoodPng.getFileName().toString());
		implDto = FileManagerUtils.makeImplDto(request);
		detectedMimetype = null;
		convertible = fileTypeValidator.isImageConvertible(implDto, detectedMimetype);
		assertTrue(convertible);

		// detectedMimeType not image/*, text/*, or application/pdf
		try {
			detectedMimetype = new MimeType(imageDocMimeRaw);
		} catch (MimeTypeParseException e) {
			e.printStackTrace();
			fail(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		convertible = fileTypeValidator.isImageConvertible(implDto, detectedMimetype);
		assertTrue(convertible);
	}

	@Test
	public final void testIsExtensionSupported() {
		FileManagerRequest request = new FileManagerRequest();
		request.setClaimId(claimId);
		request.setDocTypeId(docTypeId);
		request.setProcessType(ProcessType.CLAIMS_526);
		FileDto dto = new FileDto();
		dto.setFilebytes(bytesTxt);
		dto.setFilename("test.txt");
		request.setFileDto(dto);
		ImplDto implDto = FileManagerUtils.makeImplDto(request);
		boolean issupported = fileTypeValidator.isExtensionSupported(implDto);
		assertTrue(issupported);
	}

	@Test
	public final void testIsExtensionSupported_Bad() {
		// empty file extension
		FileManagerRequest request = new FileManagerRequest();
		request.setClaimId(claimId);
		request.setDocTypeId(docTypeId);
		request.setProcessType(ProcessType.CLAIMS_526);
		FileDto dto = new FileDto();
		dto.setFilebytes(new byte[] {});
		dto.setFilename("test.");
		request.setFileDto(dto);
		ImplDto implDto = FileManagerUtils.makeImplDto(request);
		boolean issupported = fileTypeValidator.isExtensionSupported(implDto);
		assertTrue(!issupported);

		// unsupported file extension
		dto.setFilebytes(bytesTxt);
		dto.setFilename("test.chat");
		implDto = FileManagerUtils.makeImplDto(request);
		issupported = fileTypeValidator.isExtensionSupported(implDto);
		assertTrue(!issupported);
	}

	@Test
	public final void testIsMimetypeValid() {
		FileManagerRequest request = new FileManagerRequest();
		request.setClaimId(claimId);
		request.setDocTypeId(docTypeId);
		request.setProcessType(ProcessType.CLAIMS_526);
		FileDto dto = new FileDto();
		dto.setFilebytes(bytesTxt);
		dto.setFilename(filenameTxt);
		request.setFileDto(dto);
		ImplDto implDto = FileManagerUtils.makeImplDto(request);
		MimeType mimetype = fileTypeValidator.isMimetypeValid(implDto);
		assertNotNull(mimetype);
		assertTrue(ConvertibleTypesEnum.TXT.getMimeType().match(mimetype));
	}

	@Test
	public final void testIsMimetypeValid_Bad() {
		// null ImplDto
		try {
			MimeType mimetype = fileTypeValidator.isMimetypeValid(null);
			fail("isMimetypeValid should have thrown exception, not returned " + mimetype);
		} catch (Exception e) {
			assertTrue(IllegalArgumentException.class.isAssignableFrom(e.getClass()));
		}

		// null FileDto
		ImplDto implDto = new ImplDto();
		MimeType mimetype = fileTypeValidator.isMimetypeValid(implDto);
		assertTrue(implDto.getMessages().size() > 0);

		// null bytes
		FileManagerRequest request = new FileManagerRequest();
		request.setClaimId(claimId);
		request.setDocTypeId(docTypeId);
		request.setProcessType(ProcessType.CLAIMS_526);
		FileDto dto = new FileDto();
		request.setFileDto(dto);
		implDto = FileManagerUtils.makeImplDto(request);
		mimetype = fileTypeValidator.isMimetypeValid(implDto);
		assertNull(mimetype);
		assertTrue(implDto.getMessages().size() > 0);

		// empty bytes
		dto.setFilebytes(new byte[] {});
		mimetype = fileTypeValidator.isMimetypeValid(implDto);
		assertNull(mimetype);
		assertTrue(implDto.getMessages().size() > 0);

		// good bytes, null file
		dto.setFilebytes(bytesTxt);
		mimetype = fileTypeValidator.isMimetypeValid(implDto);
		assertNull(mimetype);
		assertTrue(implDto.getMessages().size() > 0);
	}

	private MimeType getMimeType(String mimetypeRaw) {
		MimeType mimetype = null;
		try {
			mimetype = new MimeType(mimetypeRaw);
		} catch (MimeTypeParseException e) {
			e.printStackTrace();
			fail(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		return mimetype;
	}
}
