package gov.va.vetservices.lib.filemanager.mime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.testutil.TestingConstants;

public class ConvertibleTypesEnumTest {

	@Before
	public final void setup() {
	}

	@Test
	public final void testConvertibleTypesEnum() {
		// manually test each mime type in the enum
		assertNotNull(ConvertibleTypesEnum.BMP);
		assertNotNull(ConvertibleTypesEnum.GIF);
		assertNotNull(ConvertibleTypesEnum.JPEG);
		assertNotNull(ConvertibleTypesEnum.JPG);
		assertNotNull(ConvertibleTypesEnum.PDF);
		assertNotNull(ConvertibleTypesEnum.PNG);
		assertNotNull(ConvertibleTypesEnum.TIF);
		assertNotNull(ConvertibleTypesEnum.TIFF);
		assertNotNull(ConvertibleTypesEnum.TXT);

		for (ConvertibleTypesEnum cte : ConvertibleTypesEnum.values()) {
			if (!(ConvertibleTypesEnum.BMP.equals(cte) || ConvertibleTypesEnum.GIF.equals(cte) || ConvertibleTypesEnum.JPEG.equals(cte)
					|| ConvertibleTypesEnum.JPG.equals(cte) || ConvertibleTypesEnum.PDF.equals(cte)
					|| ConvertibleTypesEnum.PNG.equals(cte) || ConvertibleTypesEnum.TIF.equals(cte)
					|| ConvertibleTypesEnum.TIFF.equals(cte) || ConvertibleTypesEnum.TXT.equals(cte))) {
				fail("ConvertibleTypesEnum contains enumeration '" + cte.name()
						+ "' that is not tested. Uupdate ConvertibleTypesEnumTest.");
			}

		}
	}

	@Test
	public final void testGetConvertiblePrimaryTypes() {
		Set<String> set = ConvertibleTypesEnum.getConvertiblePrimaryTypes();
		assertNotNull(set);
		assertTrue(set.size() > 0);
		String primaryType = set.iterator().next();
		assertTrue(!StringUtils.isBlank(primaryType));
	}

	@Test
	public final void testGetExtension() {
		assertEquals(TestingConstants.FILE_EXT_BMP, ConvertibleTypesEnum.BMP.getExtension());
		assertEquals(TestingConstants.FILE_EXT_GIF, ConvertibleTypesEnum.GIF.getExtension());
		assertEquals(TestingConstants.FILE_EXT_JPEG, ConvertibleTypesEnum.JPEG.getExtension());
		assertEquals(TestingConstants.FILE_EXT_JPG, ConvertibleTypesEnum.JPG.getExtension());
		assertEquals(TestingConstants.FILE_EXT_PDF, ConvertibleTypesEnum.PDF.getExtension());
		assertEquals(TestingConstants.FILE_EXT_PNG, ConvertibleTypesEnum.PNG.getExtension());
		assertEquals(TestingConstants.FILE_EXT_TIF, ConvertibleTypesEnum.TIF.getExtension());
		assertEquals(TestingConstants.FILE_EXT_TIFF, ConvertibleTypesEnum.TIFF.getExtension());
		assertEquals(TestingConstants.FILE_EXT_TXT, ConvertibleTypesEnum.TXT.getExtension());
	}

	@Test
	public final void testGetMimeString() {
		assertEquals(TestingConstants.MIME_RAW_BMP, ConvertibleTypesEnum.BMP.getMimeString());
		assertEquals(TestingConstants.MIME_RAW_GIF, ConvertibleTypesEnum.GIF.getMimeString());
		assertEquals(TestingConstants.MIME_RAW_JPEG, ConvertibleTypesEnum.JPEG.getMimeString());
		assertEquals(TestingConstants.MIME_RAW_JPEG, ConvertibleTypesEnum.JPG.getMimeString());
		assertEquals(TestingConstants.MIME_RAW_PDF, ConvertibleTypesEnum.PDF.getMimeString());
		assertEquals(TestingConstants.MIME_RAW_PNG, ConvertibleTypesEnum.PNG.getMimeString());
		assertEquals(TestingConstants.MIME_RAW_TIF, ConvertibleTypesEnum.TIF.getMimeString());
		assertEquals(TestingConstants.MIME_RAW_TIF, ConvertibleTypesEnum.TIFF.getMimeString());
		assertEquals(TestingConstants.MIME_RAW_TXT, ConvertibleTypesEnum.TXT.getMimeString());
	}

	@Test
	public final void testGetMimeType() throws MimeTypeParseException {
		assertTrue((new MimeType(TestingConstants.MIME_RAW_BMP)).match(ConvertibleTypesEnum.BMP.getMimeType()));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_GIF)).match(ConvertibleTypesEnum.GIF.getMimeType()));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_JPEG)).match(ConvertibleTypesEnum.JPEG.getMimeType()));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_JPG)).match(ConvertibleTypesEnum.JPG.getMimeType()));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_PDF)).match(ConvertibleTypesEnum.PDF.getMimeType()));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_PNG)).match(ConvertibleTypesEnum.PNG.getMimeType()));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_TIF)).match(ConvertibleTypesEnum.TIF.getMimeType()));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_TIFF)).match(ConvertibleTypesEnum.TIFF.getMimeType()));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_TXT)).match(ConvertibleTypesEnum.TXT.getMimeType()));
	}

	@Test
	public final void testGetMimeTypeForExtension() throws MimeTypeParseException {
		assertTrue((new MimeType(TestingConstants.MIME_RAW_BMP))
				.match(ConvertibleTypesEnum.getMimeTypeForExtension(TestingConstants.FILE_EXT_BMP)));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_GIF))
				.match(ConvertibleTypesEnum.getMimeTypeForExtension(TestingConstants.FILE_EXT_GIF)));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_JPEG))
				.match(ConvertibleTypesEnum.getMimeTypeForExtension(TestingConstants.FILE_EXT_JPEG)));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_JPG))
				.match(ConvertibleTypesEnum.getMimeTypeForExtension(TestingConstants.FILE_EXT_JPG)));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_PDF))
				.match(ConvertibleTypesEnum.getMimeTypeForExtension(TestingConstants.FILE_EXT_PDF)));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_PNG))
				.match(ConvertibleTypesEnum.getMimeTypeForExtension(TestingConstants.FILE_EXT_PNG)));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_TIF))
				.match(ConvertibleTypesEnum.getMimeTypeForExtension(TestingConstants.FILE_EXT_TIF)));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_TIFF))
				.match(ConvertibleTypesEnum.getMimeTypeForExtension(TestingConstants.FILE_EXT_TIFF)));
		assertTrue((new MimeType(TestingConstants.MIME_RAW_TXT))
				.match(ConvertibleTypesEnum.getMimeTypeForExtension(TestingConstants.FILE_EXT_TXT)));
	}

	@Test
	public final void testGetMimeTypeForExtension_BadExt() {
		assertNull(ConvertibleTypesEnum.getMimeTypeForExtension(null));
		assertNull(ConvertibleTypesEnum.getMimeTypeForExtension(TestingConstants.FILE_EXT_UNSUPPORTED));
	}

	@Test
	public final void testHasMimeTypeString() throws MimeTypeParseException {
		assertTrue(ConvertibleTypesEnum.hasMimeType(TestingConstants.MIME_RAW_BMP));
		assertTrue(ConvertibleTypesEnum.hasMimeType(TestingConstants.MIME_RAW_GIF));
		assertTrue(ConvertibleTypesEnum.hasMimeType(TestingConstants.MIME_RAW_JPEG));
		assertTrue(ConvertibleTypesEnum.hasMimeType(TestingConstants.MIME_RAW_JPG));
		assertTrue(ConvertibleTypesEnum.hasMimeType(TestingConstants.MIME_RAW_PDF));
		assertTrue(ConvertibleTypesEnum.hasMimeType(TestingConstants.MIME_RAW_PNG));
		assertTrue(ConvertibleTypesEnum.hasMimeType(TestingConstants.MIME_RAW_TIF));
		assertTrue(ConvertibleTypesEnum.hasMimeType(TestingConstants.MIME_RAW_TIFF));
		assertTrue(ConvertibleTypesEnum.hasMimeType(TestingConstants.MIME_RAW_TXT));
	}

	@Test
	public final void testHasMimeTypeString_Unsupported() throws MimeTypeParseException {
		assertFalse(ConvertibleTypesEnum.hasMimeType(TestingConstants.MIME_RAW_UNSUPPORTED));
		assertFalse(ConvertibleTypesEnum.hasMimeType(TestingConstants.MIME_RAW_INVALID));
	}

	@Test
	public final void testHasMimeTypeMimeType() throws MimeTypeParseException {
		assertTrue(ConvertibleTypesEnum.hasMimeType((new MimeType(TestingConstants.MIME_RAW_BMP))));
		assertTrue(ConvertibleTypesEnum.hasMimeType((new MimeType(TestingConstants.MIME_RAW_GIF))));
		assertTrue(ConvertibleTypesEnum.hasMimeType((new MimeType(TestingConstants.MIME_RAW_JPEG))));
		assertTrue(ConvertibleTypesEnum.hasMimeType((new MimeType(TestingConstants.MIME_RAW_JPG))));
		assertTrue(ConvertibleTypesEnum.hasMimeType((new MimeType(TestingConstants.MIME_RAW_PDF))));
		assertTrue(ConvertibleTypesEnum.hasMimeType((new MimeType(TestingConstants.MIME_RAW_PNG))));
		assertTrue(ConvertibleTypesEnum.hasMimeType((new MimeType(TestingConstants.MIME_RAW_TIF))));
		assertTrue(ConvertibleTypesEnum.hasMimeType((new MimeType(TestingConstants.MIME_RAW_TIFF))));
		assertTrue(ConvertibleTypesEnum.hasMimeType((new MimeType(TestingConstants.MIME_RAW_TXT))));
	}

	@Test
	public final void testHasMimeTypeMimeType_Unsupported() throws MimeTypeParseException {
		assertFalse(ConvertibleTypesEnum.hasMimeType((new MimeType(TestingConstants.MIME_RAW_UNSUPPORTED))));
	}
}
