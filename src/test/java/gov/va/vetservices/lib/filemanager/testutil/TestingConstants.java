package gov.va.vetservices.lib.filemanager.testutil;

public class TestingConstants {

	/** Prefix used on src/test/resources/files/** filenames that are the type the extension claims */
	public static String TEST_FILE_PREFIX_LEGITIMATE = "IS_";
	/** Prefix used on src/test/resources/files/** filenames that are NOT the type the extensions claims */
	public static String TEST_FILE_PREFIX_ILLEGITIMATE = "NOT_";
	/** Prefix used on src/test/resources/files/** filenames that are BAD (corrupted contents) */
	public static String TEST_FILE_PREFIX_CORRUPT = "BAD_";

	/** File extension: "bmp" */
	public static String FILE_EXT_BMP = "bmp";
	/** File extension: "gif" */
	public static String FILE_EXT_GIF = "gif";
	/** File extension: "jpeg" */
	public static String FILE_EXT_JPEG = "jpeg";
	/** File extension: "jpg" */
	public static String FILE_EXT_JPG = "jpg";
	/** File extension: "pdf" */
	public static String FILE_EXT_PDF = "pdf";
	/** File extension: "png" */
	public static String FILE_EXT_PNG = "png";
	/** File extension: "tif" */
	public static String FILE_EXT_TIF = "tif";
	/** File extension: "tiff" */
	public static String FILE_EXT_TIFF = "tiff";
	/** File extension: "txt" */
	public static String FILE_EXT_TXT = "txt";
	/** File extension: "chat" */
	public static String FILE_EXT_UNSUPPORTED = "chat";

	/** Raw MimeType (a string): "image/bmp" */
	public static String MIME_RAW_BMP = "image/bmp";
	/** Raw MimeType (a string): "image/gif" */
	public static String MIME_RAW_GIF = "image/gif";
	/** Raw MimeType (a string): "image/jpeg" */
	public static String MIME_RAW_JPEG = "image/jpeg";
	/** Raw MimeType (a string): "image/jpeg" */
	public static String MIME_RAW_JPG = "image/jpeg";
	/** Raw MimeType (a string): "application/pdf" */
	public static String MIME_RAW_PDF = "application/pdf";
	/** Raw MimeType (a string): "image/png" */
	public static String MIME_RAW_PNG = "image/png";
	/** Raw MimeType (a string): "image/tiff" */
	public static String MIME_RAW_TIF = "image/tiff";
	/** Raw MimeType (a string): "image/tiff" */
	public static String MIME_RAW_TIFF = "image/tiff";
	/** Raw MimeType (a string): "text/plain" */
	public static String MIME_RAW_TXT = "text/plain";
	/** Raw MimeType (a string): "application/x-chat" */
	public static String MIME_RAW_UNSUPPORTED = "application/x-chat";

	/**
	 * Do not instantiate.
	 */
	private TestingConstants() {
		throw new IllegalAccessError("TestingConstants is a static class. Do not instantiate it.");
	}

}
