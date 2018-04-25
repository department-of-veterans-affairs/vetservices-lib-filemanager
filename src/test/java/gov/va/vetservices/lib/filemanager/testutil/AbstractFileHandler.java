/**
 *
 */
package gov.va.vetservices.lib.filemanager.testutil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimeType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

/**
 * Common methods for handling paths, filenames, etc.
 *
 * @author aburkholder
 */
public abstract class AbstractFileHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFileHandler.class);

	/** Can be used to decide if output files in {@value #OUTPUT_DIR} should be deleted. Default={@code true}. */
	protected boolean TEST_DELETE_OUTPUTS_WHEN_DONE = true;

	/** Default relative path for files on the classpath */
	public static final String DEFAULT_INPUT_DIR = "files/";
	/** If no directory is specified, this directory will be prepended to the filename. */
	public static final String DEFAULT_OUTPUT_DIR = "/tmp/vetservice-lib-filemanager/";
	/** Characters that are not allowed in output file names */
	protected static final String[] ILLEGAL_OUTPUT_FILE_CHARS = { "/", "\\", ":" };

	/**
	 * Attempts to read from the classpath. If that fails, attempts to read from the file system.
	 * If that fails, return {@code null}.
	 * <p>
	 * For relative paths, include any directories under which the file is placed.<br/>
	 * Absolute paths must begin with the root directory "/".
	 * <p>
	 * Note that the {@link Path} parameter must include a filename. Use Paths.get() to get a Path from a String.
	 *
	 * @param path a Path object that
	 * @return byte[] the file content, or {@code null}
	 * @throws IOException
	 */
	public byte[] readFile(Path path) throws IOException {
		if ((path != null) && !StringUtils.isBlank(path.toString())) {
			return readBytes(path);
		}
		LOGGER.info("Path '" + (path == null ? null : path.toString()) + "' is null or empty");
		return null;
	}

	/**
	 * Attempts to read from the classpath. If that fails, attempts to read from the file system.
	 * If that fails, return {@code null}.
	 *
	 * @param path the file to read
	 * @return byte[] the file contents, or {@code null}
	 * @throws IOException any IO issue
	 */
	private byte[] readBytes(Path path) throws IOException {
		byte[] bytes = null;

		bytes = readFromClasspath(path);
		if (bytes == null) {
			bytes = readFromPath(path);
		}

		return bytes;
	}

	/**
	 * Attempts to read a file from the classpath. If the file cannot be found, {@code null} is returned.
	 * <p>
	 * The Path parameter must include a filename that is not empty and does not start with {@value #ILLEGAL_OUTPUT_FILE_CHARS}.<br/>
	 * The path itself also must not begin with any of {@value #ILLEGAL_OUTPUT_FILE_CHARS}.
	 * <p>
	 * This approach only works for files that can be found on the classpath.
	 *
	 * @param path the path as a String
	 * @return byte[] the contents of the file, or {@code null}
	 * @throws IOException if any IO issue occurs
	 */
	private byte[] readFromClasspath(Path path) throws IOException {
		if (!isRelativePath(path)) {
			return null;
		}
		InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(path.toString());
		if (inputStream != null) {
			return IOUtils.toByteArray(inputStream);
		}
		LOGGER.info("Path '" + path.toString() + "' does not exist, so cannot be read.");
		return null;
	}

	/**
	 * Attempts to read a file from a {@link Path} object. If the file cannot be found, {@code null} is returned.
	 * <p>
	 * The Path parameter must include a filename that is not empty and does not start with {@value #ILLEGAL_OUTPUT_FILE_CHARS}.<br/>
	 * The path itself must begin with "/".
	 * <p>
	 * This approach only works for files that can be found directly on the file system (e.g. not in a JAR).
	 *
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private byte[] readFromPath(Path path) throws IOException {
		if (!isAbsolutePath(path) || !FileManagerUtils.hasFilename(path.toFile().getName())) {
			LOGGER.info(
					"Path '" + path.toString() + "' is null or empty, or is not an absolute path, or does not include a filename.");
			return null;
		}
		File file = path.toFile();
		if (file.exists()) {
			return Files.readAllBytes(path);
		}
		LOGGER.info("Path '" + path.toString() + "' does not exist.");
		return null;
	}

	/**
	 * Determines if {@code path} is an absolute {@link Path}.
	 * <p>
	 * The the {@code path} must start with "/", and may optionally include a filename.
	 *
	 * @param path
	 * @return boolean
	 */
	protected boolean isAbsolutePath(Path path) {
		return (path != null) && path.toString().startsWith("/");
	}

	/**
	 * Determines if {@code path} is a relative {@link Path}.
	 * <p>
	 * The path cannot not start with any of {@link FileManagerUtils#ILLEGAL_FILE_START_CHARS}, and may optionally include a filename.
	 *
	 * @param path the path to check
	 * @return boolean
	 */
	protected boolean isRelativePath(Path path) {
		return (path != null) && FileManagerUtils.hasFilename(path.toFile().getName())
				&& !StringUtils.startsWithAny(path.toString(), FileManagerProperties.FILE_NAME_ILLEGAL_STRING);
	}

	/**
	 * If {@code path} directory does not exist, then attempt to create it.
	 * If the directory creation attempt fails, {@code null} will be returned.
	 *
	 * @return boolean success ({@code true}) or failure ({@code false}) due to invalid path
	 * @throws SecurityException if file system privileges do not allow creation of the directory
	 */
	protected boolean createOutputPath(Path path) {
		boolean success = (path != null) && !StringUtils.isBlank(path.toString()) && path.toString().startsWith("/");

		if (success) {
			File dir = path.toFile();
			if (FileManagerUtils.hasFilename(dir.getName()) && dir.getName().contains(".")) {
				dir = path.subpath(0, path.getNameCount() - 1).toFile();
			}
			if (!dir.exists()) {
				success = dir.mkdirs();
			}
		}

		return success;
	}

	/**
	 * If the {@code path} directory exists, attempt to delete all subdirectories and files under it.
	 * <p>
	 * If {@code path} directory does not exist, then do nothing and return {@code true}.
	 * If the directory exists but file or directory deletion attempt fails, IOException or SecurityException is thrown.
	 *
	 * @param path the path in which all files and subdirectories should be deleted
	 * @return boolean success ({@code true}) or failure ({@code false}) if deletion problems
	 * @throws IOException if any i/o issues while deleting files & directories
	 * @throws SecurityException if file system privileges do not allow deletion of a directory
	 * @throws NoSuchFileException if the file does not exist (optional specific exception)
	 * @throws DirectoryNotEmptyException if the directory and could not be deleted because it is not empty
	 */
	public boolean deleteOutputFiles(Path path) throws IOException {
		boolean success = (path != null) && !StringUtils.isBlank(path.toString()) && path.toString().startsWith("/");

		if (success) {
			if (!StringUtils.isBlank(path.toFile().getName())) {
				path = path.subpath(0, path.getNameCount() - 1);
			}
			File fileObj = path.toFile();

			if (fileObj.exists()) {
				Path pathObj = fileObj.toPath();

				Files.walkFileTree(pathObj, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
						if (e == null) {
							if (!dir.toString().equals(pathObj.toString())) {
								Files.delete(dir);
							}
							return FileVisitResult.CONTINUE;
						} else {
							// directory iteration failed
							LOGGER.error("Directory iteration failed with " + e.getClass().getSimpleName() + ": " + e.getMessage());
							throw e;
						}
					}
				});
			}
		}

		return success;
	}

	/**
	 * Generic file writer. If the {@code filepath} argument is not absolute,
	 * {@value #DEFAULT_OUTPUT_DIR} will be appended to the beginning of the filepath.
	 * If no filename has been provided, {@code false} will be returned.
	 * <p>
	 * If any security or i/o issues occur while attempting to create the output directory and write the file,
	 * the issue will be logged and {@code false} will be returned. If the filepath or bytes arguments are {@code null}
	 * or empty, or are in some way malformed, {@link IllegalArgumentException} will be returned.
	 * <p>
	 * Use {@code Paths.get()} to convert a String to a Path.
	 *
	 * @param filepath the absolute path and filename
	 * @return boolean success ({@code true}, or failure
	 * @throws FileManagerException if some problem creating the output directory
	 */
	private File write(Path filepath, byte[] bytes) throws FileManagerException {
		File savedFile = null;

		boolean success = FileManagerUtils.hasFilename(filepath.toFile().getName());
		if (!success) {
			LOGGER.error("Provided filepath '" + filepath.toString() + "' does not include a filename.");
			return null;
		}
		success = isAbsolutePath(filepath);
		if (!success) {
			LOGGER.info("Adjusted filepath from '" + filepath + "' to '" + DEFAULT_OUTPUT_DIR + filepath + "'");
			filepath = Paths.get(DEFAULT_OUTPUT_DIR + filepath);
		}

		if (success) {
			Path pathOnly = Paths.get("/", filepath.subpath(0, filepath.getNameCount() - 1).toString());
			if (!createOutputPath(pathOnly)) {
				LOGGER.error("Could not create directory: " + pathOnly);
				throw new FileManagerException(MessageSeverity.ERROR, MessageKeys.FILEMANAGER_ISSUE.getKey(),
						MessageKeys.FILEMANAGER_ISSUE.getMessage());
			}

			try {
				Path newFilePath = Files.write(filepath, bytes, StandardOpenOption.CREATE);
				savedFile = newFilePath.toFile();
			} catch (Throwable e) {
				e.printStackTrace();
				throw new UnsupportedOperationException("Unexpected error while attempting to write file: " + filepath, e);
			}
		}

		return savedFile;
	}

	/**
	 * Saves a byte array as a file.
	 * <p>
	 * The {@code filename} parameter is only concerned with the filename.
	 * The filename cannot begin with any of {@value #ILLEGAL_OUTPUT_FILE_CHARS}.
	 * Any path elements will be stripped from the filename. The file is saved in {@value #DEFAULT_OUTPUT_DIR}.
	 * <p>
	 * If the {@code bytes} parameter is null, the file is not saved, otherwise
	 * this operation will overwrite any pre-existing file of the same name.
	 * <p>
	 * If any problem is encountered while attempting to output the file, or if either parameter is not valid,
	 * {@code null} is returned.
	 *
	 * @param bytes the file content
	 * @param filename the name of the file (no path components)
	 * @return File the full path and filename of the file that was created
	 * @throws FileManagerException
	 * @throws IllegalArgumentException if filename is invalid
	 * @throws UnsupportedOperationException if some error writing the file
	 */
	public File saveFile(final byte[] bytes, final String filename) throws FileManagerException {
		File savedFile = null;

		if (FileManagerUtils.hasBytes(bytes) && FileManagerUtils.hasFilename(filename)) {
			Path path = Paths.get(DEFAULT_OUTPUT_DIR + Paths.get(filename).toFile().getName());
			savedFile = write(path, bytes);
		}

		return savedFile;
	}

	/**
	 * This method uses the MIME Type to define the path from which to get a list of files on the classpath.
	 * <p>
	 * The {@code src/test/resources} root directory will always be {@value #DEFAULT_INPUT_DIR}.
	 * The remainder of the path is determined from the PrimaryType and the SubType. For example,
	 * a MimeType of "image/png" would reference a directory path of "image/png".
	 * <p>
	 * The returned list will include any files found under the specified path.
	 * If no files are found, the returned list will be empty.
	 * Subdirectories are not processed - directory File types, and any files in those directories, will be excluded.
	 * <p>
	 * If the specified path does not exist, this method returns {@code null}.
	 *
	 * @param mimetype the MimeType representing the relative path under {@value #DEFAULT_INPUT_DIR}
	 * @return List&lt;File&gt; a list of files, or {@code null}
	 * @throws IllegalArgumentException if directory does not exist, or other file access issue
	 */
	protected List<File> listFilesByMimePath(MimeType mimetype) {
		List<File> files = null;

		// using hasMimeType() eliminates primary/* and other non-relevant directory search attempts
//		if (ConvertibleTypesEnum.hasMimeType(mimetype)) {

		try {
			// directory to get files from is based on the mime type, eg .../text/plain
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resolver.getResources("classpath*:" + DEFAULT_INPUT_DIR + mimetype.toString() + "/**/*.*");
			if ((resources != null) && (resources.length > 0)) {
				if (files == null) {
					files = new ArrayList<>();
				}

				for (Resource resource : resources) {
					if (resource.exists()) {
						File file = resource.getFile();
						if (file.isFile()) {
							files.add(file);
						}
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw new IllegalArgumentException(
					"File IO or security problem getting files in direcoty: " + DEFAULT_INPUT_DIR + mimetype.toString(), e);
		}
//		}
		return files;
	}
}
