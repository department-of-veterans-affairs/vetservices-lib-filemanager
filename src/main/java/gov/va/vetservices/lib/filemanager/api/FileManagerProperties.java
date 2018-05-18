package gov.va.vetservices.lib.filemanager.api;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;

/**
 * <p>
 * Configuration properties that can be overridden by the application.
 * <p>
 * There are public KEY_** constants that resolve to the property names for FileManager configuration.
 * <p>
 * Include the property in your application config, as declared in the
 * {@code Value} annotation of each private member in this class.
 *
 * @author aburkholder
 */
@Component(FileManagerProperties.BEAN_NAME)
public class FileManagerProperties {

	public static final String BEAN_NAME = "fileManagerProperties";

	/* KEY constants. These resolve to the property names that configure FileManager */

	/** Constant key for max Operating System (Windows/Linux/macOS) file size in bytes */
	public static final String KEY_FILE_MAX_BYTES = "filemanager.config.file.max.bytes";
	/** Constant key for human-readable max file size in MB */
	public static final String KEY_FILE_MAX_TEXT_MB = "filemanager.config.file.max.text.megabytes";
	/** Constant key for file extensions that can be converted to PDF */
	public static final String KEY_CONVERTIBLE_FILE_EXTENSIONS = "filemanager.config.file.convertible.extensions";
	/** Constant key for max filename length in bytes */
	public static final String KEY_FILENAME_MAX_LENGTH = "filemanager.config.filname.max.length";

	/* DEFAULT constants */

	/** Constant for the default value of {@value #KEY_FILE_MAX_BYTES} */
	protected static final String DEFAULT_FILE_MAX_BYTES = "26214400";
	/** Constant for the default value of {@value #KEY_FILE_MAX_TEXT_MB} */
	protected static final String DEFAULT_FILE_MAX_TEXT_MB = "25 MB";
	/** Constant for the default value of {@value #KEY_FILE_MAX_TEXT_MB} */
	protected static final String DEFAULT_FILENAME_MAX_LENGTH = "255";

	/* SUPPORTING constants */

	/** Array of characters that are not allowed at the beginning of a file names: {@code . / \ :} */
	public static final String[] FILE_NAME_ILLEGAL_CHARS = { "/", "\\", ":" }; // NOSONAR - will be usedok
	/** Constant for file extensions that are supported for conversion to PDF */
	public static final String CONVERTIBLE_FILE_EXTENSIONS_STRING = Arrays.toString(ConvertibleTypesEnum.values());
	/** Maximum length of absolute file path, constrained by lowest size allowed on common operating systems */
	public static final int MAX_OS_FILEPATH_LENGTH = 4096;

	/* MEMBERS FOR EXPOSED CONSTANTS */

	// KEY_FILE_MAX_BYTES + ":" + DEFAULT_FILE_MAX_BYTES
	@Value("${filemanager.config.file.max.bytes:26214400}")
	private int maxFileBytes;

	// KEY_FILE_MAX_TEXT_MB + ":" + DEFAULT_FILE_MAX_TEXT_MB
	@Value("${filemanager.config.file.max.text.megabytes:25 MB}")
	private String maxFileMegaBytes;

	// KEY_FILENAME_MAX_LENGTH + ":" + DEFAULT_FILENAME_MAX_LENGTH
	@Value("${filemanager.config.filname.max.length:255}")
	private int maxFilenameLen;

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

	/**
	 * The max length that a filename (without path elements) can be in bytes.
	 * <p>
	 * This property can be overridden by including it in the application configuration as:<br/>
	 * {@value #KEY_FILE_MAX_TEXT_MB} (default is {@value #DEFAULT_FILE_MAX_TEXT_MB}, equivalent to {@value #DEFAULT_FILE_MAX_BYTES}
	 * bytes).
	 *
	 * @return the maxFilenameLen
	 */
	public int getMaxFilenameLen() {
		return maxFilenameLen;
	}
}
