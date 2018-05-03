package gov.va.vetservices.lib.filemanager.api;

import gov.va.vetservices.lib.filemanager.api.stamper.StampData;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;

/**
 * The public interface for operations contained in this artifact.
 *
 * @author aburkholder
 */
// NOSONAR TODO Dev notes:
// See the IMPL class for TO-DO dev notes
public interface FileManager {

	/**
	 * Evaluate the suitability of a file for conversion to PDF.
	 * The type of file may be subject to change over time,
	 * but is currently limited to specific image types, text files, and PDF files.
	 *
	 * @param fileDto the {@code FileDto} to validate
	 * @return boolean {@code true} if validation passes
	 */
	public FileManagerResponse validateFileForPDFConversion(FileDto fileDto);

	/**
	 * Convert a file to PDF. Note that the file should have been previously validated by the
	 * {@link #validateFileForPDFConversion(FileDto)} method.
	 *
	 * @param file the file in form of byte array to be converted
	 * @return FileManagerResponse the response with messages and PDF if successful
	 */
	public FileManagerResponse convertToPdf(FileDto fileDto);

	/**
	 * Put a VA approved banner stamp on the provided PDF byte array.
	 *
	 * @param file the PDF file as a byte array
	 * @param stampData the info to be stamped into the PDF
	 * @return byte[] the stamped PDF
	 */
	public FileManagerResponse stampPdf(StampData stampData, FileDto fileDto);
}
