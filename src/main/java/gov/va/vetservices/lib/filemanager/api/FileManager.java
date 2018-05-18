package gov.va.vetservices.lib.filemanager.api;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;

/**
 * The public interface for operations contained in this artifact.
 *
 * @author aburkholder
 */
public interface FileManager {

	/**
	 * Evaluate the suitability of a file for conversion to PDF.
	 * Supported file types may change over time, with added (or removed) capabilities.
	 * <p>
	 * If no messages are on the response, the file can be converted.
	 *
	 * @param request the {@code FileManagerRequest} to validate
	 * @return FileManagerResponse the response with any messages
	 */
	public FileManagerResponse validateFileForPDFConversion(FileManagerRequest request) throws FileManagerException;

	/**
	 * Convert a file to PDF, and stamp its header area. Note that the file should have been previously validated by the
	 * {@link #validateFileForPDFConversion(FileDto)} method.
	 *
	 * @param request the {@code FileManagerRequest} containing the byte array to be converted
	 * @return FileManagerResponse the response with messages and PDF if successful
	 */
	public FileManagerResponse convertToPdf(FileManagerRequest request) throws FileManagerException;

}
