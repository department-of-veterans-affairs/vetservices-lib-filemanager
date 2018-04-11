package gov.va.vetservices.lib.filemanager;

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
	/** Constant key for max filename length allowed by VBMS */
	public static final String KEY_VBMS_FILENAME_MAX_LEN = "filemanager.config.vbms.filename.max.length";
	/** Constant key for max file extension length allowed by VBMS */
	public static final String KEY_VBMS_FILEEXT_MAX_LEN = "filemanager.config.vbms.fileextension.max.length";

	/* DEFAULT constants */

	/** Constant for the default value of {@value #KEY_FILE_MAX_BYTES} */
	public static final String DEFAULT_FILE_MAX_BYTES = "26214400";
	/** Constant for the default value of {@value #KEY_FILE_MAX_TEXT_MB} */
	private static final String DEFAULT_FILE_MAX_TEXT_MB = "25 MB";
	/** Constant for the default value of {@value #KEY_VBMS_FILENAME_MAX_LEN} */
	private static final String DEFAULT_VBMS_FILENAME_MAX_LEN = "245";
	/** Constant for the default value of {@value #KEY_VBMS_FILENAME_MAX_LEN} */
	private static final String DEFAULT_VBMS_FILEEXT_MAX_LEN = "4";

	@Value("${" + KEY_FILE_MAX_BYTES + ":" + DEFAULT_FILE_MAX_BYTES + "}")
	private int maxFileBytes;

	@Value("${" + KEY_FILE_MAX_TEXT_MB + ":" + DEFAULT_FILE_MAX_TEXT_MB + "}")
	private String maxFileMegaBytes;

	@Value("${" + KEY_VBMS_FILENAME_MAX_LEN + ":" + DEFAULT_VBMS_FILENAME_MAX_LEN + "}")
	private int vbmsMaxFilenameLength;

	@Value("${" + KEY_VBMS_FILEEXT_MAX_LEN + ":" + DEFAULT_VBMS_FILEEXT_MAX_LEN + "}")
	private int vbmsMaxFileExtensionLength;

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
	public int getMaxFileBytes() {
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
	public void setMaxFileBytes(int maxFileBytes) {
		this.maxFileBytes = maxFileBytes;
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
	public String getMaxFileMegaBytes() {
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
	public void setMaxFileMegaBytes(String maxFileMegaBytes) {
		this.maxFileMegaBytes = maxFileMegaBytes;
	}

	/**
	 * <p>
	 * The max length that VBMS will accept for a filename, expressed in bytes (note that extended characters will use two bytes each).
	 * </p>
	 * <p>
	 * This property can be overridden by including it in the application configuration as:<br/>
	 * {@value #KEY_VBMS_FILENAME_MAX_LEN} (default is {@value #DEFAULT_VBMS_FILENAME_MAX_LEN}).
	 * </p>
	 *
	 * @return the vbmsMaxFilenameLength
	 */
	public int getVbmsMaxFilenameLength() {
		return vbmsMaxFilenameLength;
	}

	/**
	 * <p>
	 * The max length that VBMS will accept for a filename, expressed in bytes (note that extended characters will use two bytes each).
	 * </p>
	 * <p>
	 * This property can be overridden by including it in the application configuration as:<br/>
	 * {@value #KEY_VBMS_FILENAME_MAX_LEN} (default is {@value #DEFAULT_VBMS_FILENAME_MAX_LEN}).
	 * </p>
	 *
	 * @param vbmsMaxFilenameLength the vbmsMaxFilenameLength to set
	 */
	public void setVbmsMaxFilenameLength(int vbmsMaxFilenameLength) {
		this.vbmsMaxFilenameLength = vbmsMaxFilenameLength;
	}

	/**
	 * <p>
	 * The max length that VBMS will accept for a file extension, expressed in bytes (note that extended characters will use two bytes
	 * each).
	 * </p>
	 * <p>
	 * This property can be overridden by including it in the application configuration as:<br/>
	 * {@value #KEY_VBMS_FILEEXT_MAX_LEN} (default is {@value #DEFAULT_VBMS_FILEEXT_MAX_LEN}).
	 * </p>
	 *
	 * @return the vbmsMaxFileExtensionLength
	 */
	public int getVbmsMaxFileExtensionLength() {
		return vbmsMaxFileExtensionLength;
	}

	/**
	 * <p>
	 * The max length that VBMS will accept for a file extension, expressed in bytes (note that extended characters will use two bytes
	 * each).
	 * </p>
	 * <p>
	 * This property can be overridden by including it in the application configuration as:<br/>
	 * {@value #KEY_VBMS_FILEEXT_MAX_LEN} (default is {@value #DEFAULT_VBMS_FILEEXT_MAX_LEN}).
	 * </p>
	 *
	 * @param vbmsMaxFileExtensionLength the vbmsMaxFileExtensionLength to set
	 */
	public void setVbmsMaxFileExtensionLength(int vbmsMaxFileExtensionLength) {
		this.vbmsMaxFileExtensionLength = vbmsMaxFileExtensionLength;
	}

}
