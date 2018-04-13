package gov.va.vetservices.lib.filemanager.api;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Configuration properties that can be overridden by the application.
 * <p>
 * Include the property in your application config, as declared in the
 * {@code Value} annotation of each private member in this class.
 *
 * @author aburkholder
 */
@Component(FileManagerProperties.BEAN_NAME)
public class FileManagerProperties {

	public static final String BEAN_NAME = "fileManagerProperties";

	/* KEY constants */

	/** Constant key for max file size in bytes */
	public static final String KEY_FILE_MAX_BYTES = "filemanager.config.file.max.bytes";
	/** Constant key for textual max file size in MB */
	public static final String KEY_FILE_MAX_TEXT_MB = "filemanager.config.file.max.text.megabytes";
	/** Constant key for file extensions that can be converted to PDF */
	public static final String KEY_CONVERTIBLE_FILE_EXTENSIONS = "filemanager.config.file.convertible.extensions";

	/* DEFAULT constants */

	/** Constant for the default value of {@value #KEY_FILE_MAX_BYTES} */
	public static final String DEFAULT_FILE_MAX_BYTES = "26214400";
	/** Constant for the default value of {@value #KEY_FILE_MAX_TEXT_MB} */
	public static final String DEFAULT_FILE_MAX_TEXT_MB = "25 MB";
	/** Constant for file extensions that can be converted to PDF */
	public static final String[] CONVERTIBLE_FILE_EXTENSIONS = { "BMP", "GIF", "JPEG", "JPG", "PDF", "PNG", "TIF", "TIFF", "TXT" };

	/* MEMBERS FOR EXPOSED CONSTANTS */

//	/**
//	 * Do not instantiate
//	 */
//	FileManagerProperties() {
//		throw new IllegalAccessError("FileManagerProperties is a static class. Do not instantiate it.");
//	}

//	@Value("${" + KEY_FILE_MAX_BYTES + ":" + DEFAULT_FILE_MAX_BYTES + "}")
	@Value("${filemanager.config.file.max.bytes:26214400}")
	private int maxFileBytes;

//	@Value("${" + KEY_FILE_MAX_TEXT_MB + ":" + DEFAULT_FILE_MAX_TEXT_MB + "}")
	@Value("${filemanager.config.file.max.text.megabytes:25 MB}")
	private String maxFileMegaBytes;

	@PostConstruct
	public final void postConstruct() {
//		Defense.isTrue(maxFileBytes > 0, "The maxFileBytes value is not defined.");
//		Defense.isNull(maxFileMegaBytes, "The maxFileMegaBytes value cannot be null.");
	}

	/**
	 * <p>
	 * The max size that a file can be, expressed in actual bytes.
	 * <p>
	 * This property can be overridden by including it in the application configuration as:<br/>
	 * {@value #KEY_FILE_MAX_BYTES} (default is {@value #DEFAULT_FILE_MAX_BYTES}, equivalent to {@value #DEFAULT_FILE_MAX_TEXT_MB}).
	 *
	 * @return the maxFileBytes
	 */
	public int getMaxFileBytes() {
		return maxFileBytes;
	}

	/**
	 * <p>
	 * The max size that a file can be, expressed in the common vernacular of rounded megabytes.
	 * <p>
	 * This property can be overridden by including it in the application configuration as:<br/>
	 * {@value #KEY_FILE_MAX_TEXT_MB} (default is {@value #DEFAULT_FILE_MAX_TEXT_MB}, equivalent to {@value #DEFAULT_FILE_MAX_BYTES}
	 * bytes).
	 *
	 * @return the maxFileMegaBytes
	 */
	public String getMaxFileMegaBytes() {
		return maxFileMegaBytes;
	}
}
