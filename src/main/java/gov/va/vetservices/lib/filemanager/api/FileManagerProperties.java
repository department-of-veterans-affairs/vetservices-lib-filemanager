package gov.va.vetservices.lib.filemanager.api;

import org.springframework.beans.factory.annotation.Value;

/**
 * <p>
 * Configuration properties that can be overridden by the application.
 * </p>
 * <p>
 * Include the property in your application config, as declared in the
 * {@code Value} annotation of each private member in this class.
 * </p>
 *
 * @author aburkholder
 */
public class FileManagerProperties {

	/* KEY constants */

	/** Constant key for max file size in bytes */
	public static final String KEY_FILE_MAX_BYTES = "filemanager.config.file.max.bytes";
	/** Constant key for textual max file size in MB */
	public static final String KEY_FILE_MAX_TEXT_MB = "filemanager.config.file.max.text.megabytes";
//	/** Constant key for file extensions that can be converted to PDF */
//	public static final String KEY_CONVERTIBLE_FILE_EXTENSIONS = "filemanager.config.file.convertible.extensions";

	/* DEFAULT constants */

	/** Constant for the default value of {@value #KEY_FILE_MAX_BYTES} */
	public static final String DEFAULT_FILE_MAX_BYTES = "26214400";
	/** Constant for the default value of {@value #KEY_FILE_MAX_TEXT_MB} */
	private static final String DEFAULT_FILE_MAX_TEXT_MB = "25 MB";
	/** Constant for file extensions that can be converted to PDF */
	public static final String[] CONVERTIBLE_FILE_EXTENSIONS = { "BMP", "GIF", "JPEG", "JPG", "PDF", "PNG", "TIF", "TIFF", "TXT" };

	/* MEMBERS FOR EXPOSED CONSTANTS */

	/**
	 * Do not instantiate
	 */
	FileManagerProperties() {
		throw new IllegalAccessError("FileManagerProperties is a static class. Do not instantiate it.");
	}

	@Value("${" + KEY_FILE_MAX_BYTES + ":" + DEFAULT_FILE_MAX_BYTES + "}")
	private static int maxFileBytes;

	@Value("${" + KEY_FILE_MAX_TEXT_MB + ":" + DEFAULT_FILE_MAX_TEXT_MB + "}")
	private static String maxFileMegaBytes;

	/**
	 * <p>
	 * The max size that a file can be, expressed in actual bytes.
	 * </p>
	 * <p>
	 * This property can be overridden by including it in the application configuration as:<br/>
	 * {@value #KEY_FILE_MAX_BYTES} (default is {@value #DEFAULT_FILE_MAX_BYTES}, equivalent to {@value #DEFAULT_FILE_MAX_TEXT_MB}).
	 * </p>
	 *
	 * @return the maxFileBytes
	 */
	public static int getMaxFileBytes() {
		return maxFileBytes;
	}

	/**
	 * <p>
	 * The max size that a file can be, expressed in actual bytes.
	 * </p>
	 * <p>
	 * This property can be overridden by including it in the application configuration as:<br/>
	 * {@value #KEY_FILE_MAX_BYTES} (default is {@value #DEFAULT_FILE_MAX_BYTES}, equivalent to {@value #DEFAULT_FILE_MAX_TEXT_MB}).
	 * </p>
	 *
	 * @param maxFileBytes the maxFileBytes to set
	 */
	public static void setMaxFileBytes(int maxFileBytes) {
		FileManagerProperties.maxFileBytes = maxFileBytes;
	}

	/**
	 * <p>
	 * The max size that a file can be, expressed in the common vernacular of rounded megabytes.
	 * </p>
	 * <p>
	 * This property can be overridden by including it in the application configuration as:<br/>
	 * {@value #KEY_FILE_MAX_TEXT_MB} (default is {@value #DEFAULT_FILE_MAX_TEXT_MB}, equivalent to {@value #DEFAULT_FILE_MAX_BYTES}
	 * bytes).
	 * </p>
	 *
	 * @return the maxFileMegaBytes
	 */
	public static String getMaxFileMegaBytes() {
		return maxFileMegaBytes;
	}

	/**
	 * <p>
	 * The max size that a file can be, expressed in the common vernacular of rounded megabytes.
	 * </p>
	 * <p>
	 * This property can be overridden by including it in the application configuration as:<br/>
	 * {@value #KEY_FILE_MAX_TEXT_MB} (default is {@value #DEFAULT_FILE_MAX_TEXT_MB}, equivalent to {@value #DEFAULT_FILE_MAX_BYTES}
	 * bytes).
	 * </p>
	 *
	 * @param maxFileMegaBytes the maxFileMegaBytes to set
	 */
	public static void setMaxFileMegaBytes(String maxFileMegaBytes) {
		FileManagerProperties.maxFileMegaBytes = maxFileMegaBytes;
	}

}
