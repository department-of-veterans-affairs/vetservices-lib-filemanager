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

import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ValidatorDto;
import gov.va.vetservices.lib.filemanager.impl.validate.ValidatorArg;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;
import gov.va.vetservices.lib.filemanager.testutil.TestingConstants;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class FileTypeValidatorTest extends AbstractFileHandler {

	private static final byte[] bytesTxt = "This is a text/plain byte array".getBytes();
	private static final String filenameTxt = "plaintext.txt";
	private static final Path imageGoodPng = Paths.get("files/image/png/IS_16bit.png");
	private static final Path imageUnsupportedDwg = Paths.get("files/image/vnddwg/visualization-aerial.dwg");
	private static final String imageDwgMimeRaw = "image/vnddwg";
	private static final Path imageUnsupportedDoc = Paths.get("files/application/msword/IS_Test.docx");
	private static final String imageDocMimeRaw = "application/msword";

	private FileTypeValidator fileConvertibleValidator = new FileTypeValidator();
	private ValidatorArg<ValidatorDto> arg;
	private List<Message> messages;

	@Test
	public final void testValidate() {
		FileDto dto = new FileDto();
		dto.setFilebytes(bytesTxt);
		dto.setFilename(filenameTxt);

		arg = new ValidatorArg<>(FileManagerUtils.makeValidatorDto(dto));
		messages = fileConvertibleValidator.validate(arg);
		assertNull(messages);

		for (ConvertibleTypesEnum enumeration : ConvertibleTypesEnum.values()) {
			List<File> files = super.listFilesByMimePath(enumeration.getMimeType());
			assertTrue("Files for " + enumeration.getMimeString() + " is null or empty.", (files != null) && !files.isEmpty());

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
					arg = new ValidatorArg<>(FileManagerUtils.makeValidatorDto(dto));
					messages = fileConvertibleValidator.validate(arg);

					if ((messages != null) && (file.getName().contains(TestingConstants.TEST_FILE_PREFIX_CORRUPT)
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
		FileDto fileDto = new FileDto();
		fileDto.setFilebytes(bytes);
		fileDto.setFilename(imageGoodPng.getFileName().toString());
		ValidatorDto vdto = FileManagerUtils.makeValidatorDto(fileDto);
		MimeType detectedMimetype = ConvertibleTypesEnum.PNG.getMimeType();

		boolean convertible = fileConvertibleValidator.isImageConvertible(vdto, detectedMimetype);
		assertTrue(convertible);

		// legitimate image, but unsupported - returns false
		try {
			bytes = super.readFile(imageUnsupportedDwg);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Could not read file: " + imageUnsupportedDwg.toString());
		}
		fileDto = new FileDto();
		fileDto.setFilebytes(bytes);
		fileDto.setFilename(imageUnsupportedDwg.getFileName().toString());
		vdto = FileManagerUtils.makeValidatorDto(fileDto);
		detectedMimetype = getMimeType(imageDwgMimeRaw);

		convertible = fileConvertibleValidator.isImageConvertible(vdto, detectedMimetype);
		assertTrue(!convertible);

		// not an image - always returns true
		fileDto = new FileDto();
		fileDto.setFilebytes(bytesTxt);
		fileDto.setFilename(filenameTxt);
		vdto = FileManagerUtils.makeValidatorDto(fileDto);
		detectedMimetype = ConvertibleTypesEnum.TXT.getMimeType();

		convertible = fileConvertibleValidator.isImageConvertible(vdto, detectedMimetype);
		assertTrue(convertible);
	}

	@Test
	public final void testIsImageConvertible_Bad() {
		// null ValidatorDto
		MimeType detectedMimetype = ConvertibleTypesEnum.PNG.getMimeType();
		boolean convertible = fileConvertibleValidator.isImageConvertible(null, detectedMimetype);
		assertTrue(!convertible);

		// null FileDto
		ValidatorDto vdto = new ValidatorDto();
		convertible = fileConvertibleValidator.isImageConvertible(vdto, detectedMimetype);
		assertTrue(!convertible);

		// null bytes
		FileDto fileDto = new FileDto();
		fileDto.setFilename("null");
		vdto = FileManagerUtils.makeValidatorDto(fileDto);
		convertible = fileConvertibleValidator.isImageConvertible(vdto, detectedMimetype);
		assertTrue(!convertible);

		// empty bytes
		fileDto = new FileDto();
		fileDto.setFilebytes(new byte[] {});
		fileDto.setFilename("empty");
		vdto = FileManagerUtils.makeValidatorDto(fileDto);
		convertible = fileConvertibleValidator.isImageConvertible(vdto, detectedMimetype);
		assertTrue(!convertible);

		// invalid bytes
		fileDto = new FileDto();
		fileDto.setFilebytes(new byte[] { 0 });
		fileDto.setFilename("byte-char-zero");
		vdto = FileManagerUtils.makeValidatorDto(fileDto);
		convertible = fileConvertibleValidator.isImageConvertible(vdto, detectedMimetype);
		assertTrue(!convertible);

		// bytes that are not an image
		fileDto = new FileDto();
		fileDto.setFilebytes(bytesTxt);
		fileDto.setFilename(filenameTxt);
		vdto = FileManagerUtils.makeValidatorDto(fileDto);
		convertible = fileConvertibleValidator.isImageConvertible(vdto, detectedMimetype);
		assertTrue(!convertible);

		// detectedMimeType null
		byte[] bytes = null;
		try {
			bytes = super.readFile(imageGoodPng);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Could not read file: " + imageGoodPng.toString());
		}
		fileDto = new FileDto();
		fileDto.setFilebytes(bytes);
		fileDto.setFilename(imageGoodPng.getFileName().toString());
		vdto = FileManagerUtils.makeValidatorDto(fileDto);
		detectedMimetype = null;
		convertible = fileConvertibleValidator.isImageConvertible(vdto, detectedMimetype);
		assertTrue(convertible);

		// detectedMimeType not image/*, text/*, or application/pdf
		try {
			detectedMimetype = new MimeType(imageDocMimeRaw);
		} catch (MimeTypeParseException e) {
			e.printStackTrace();
			fail(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		convertible = fileConvertibleValidator.isImageConvertible(vdto, detectedMimetype);
		assertTrue(convertible);
	}

	@Test
	public final void testIsExtensionSupported() {
		FileDto dto = new FileDto();
		dto.setFilebytes(bytesTxt);
		dto.setFilename("test.txt");
		ValidatorDto vdto = FileManagerUtils.makeValidatorDto(dto);
		boolean issupported = fileConvertibleValidator.isExtensionSupported(vdto);
		assertTrue(issupported);
	}

	@Test
	public final void testIsExtensionSupported_Bad() {
		// empty file extension
		FileDto dto = new FileDto();
		dto.setFilebytes(new byte[] {});
		dto.setFilename("test.");
		ValidatorDto vdto = FileManagerUtils.makeValidatorDto(dto);
		boolean issupported = fileConvertibleValidator.isExtensionSupported(vdto);
		assertTrue(!issupported);

		// unsupported file extension
		dto = new FileDto();
		dto.setFilebytes(bytesTxt);
		dto.setFilename("test.chat");
		vdto = FileManagerUtils.makeValidatorDto(dto);
		issupported = fileConvertibleValidator.isExtensionSupported(vdto);
		assertTrue(!issupported);
	}

	@Test
	public final void testIsMimetypeValid() {
		FileDto dto = new FileDto();
		dto.setFilebytes(bytesTxt);
		dto.setFilename(filenameTxt);
		ValidatorDto vdto = FileManagerUtils.makeValidatorDto(dto);
		MimeType mimetype = fileConvertibleValidator.isMimetypeValid(vdto);
		assertNotNull(mimetype);
		assertTrue(ConvertibleTypesEnum.TXT.getMimeType().match(mimetype));
	}

	@Test
	public final void testIsMimetypeValid_Bad() {
		// null ValidatorDto
		try {
			MimeType mimetype = fileConvertibleValidator.isMimetypeValid(null);
			fail("isMimetypeValid should have thrown exception, not returned " + mimetype);
		} catch (Exception e) {
			assertTrue(IllegalArgumentException.class.isAssignableFrom(e.getClass()));
		}

		// null FileDto
		ValidatorDto vdto = new ValidatorDto();
		MimeType mimetype = fileConvertibleValidator.isMimetypeValid(vdto);
		assertTrue(vdto.getMessages().size() > 0);

		// null bytes
		FileDto dto = new FileDto();
		vdto = FileManagerUtils.makeValidatorDto(dto);
		mimetype = fileConvertibleValidator.isMimetypeValid(vdto);
		assertNull(mimetype);
		assertTrue(vdto.getMessages().size() > 0);

		// empty bytes
		dto.setFilebytes(new byte[] {});
		mimetype = fileConvertibleValidator.isMimetypeValid(vdto);
		assertNull(mimetype);
		assertTrue(vdto.getMessages().size() > 0);

		// good bytes, null file
		dto.setFilebytes(bytesTxt);
		mimetype = fileConvertibleValidator.isMimetypeValid(vdto);
		assertNull(mimetype);
		assertTrue(vdto.getMessages().size() > 0);
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
