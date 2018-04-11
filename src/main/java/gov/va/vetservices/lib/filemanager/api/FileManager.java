package gov.va.vetservices.lib.filemanager.api;

/**
 * The public interface for operations contained in this artifact.
 *
 * @author aburkholder
 */
// TODO Dev notes:
// See the IMPL class for TODO dev notes
public interface FileManager {

	/**
	 * Evaluate the suitability of a file for conversion to PDF.
	 * The type of file may be subject to change over time,
	 * but is currently limited to specific image types, text files, and PDF files.
	 *
	 * @param fileDto the {@code FileDto} to validate
	 * @return boolean {@code true} if validation passes
	 */
	public boolean validateFileForPDFConversion(FileDto fileDto);

	/**
	 * Convert a file to PDF. Note that the file should have been previously validated by the
	 * {@link #validateFileForPDFConversion(FileDto)} method.
	 *
	 * @param file the file in form of byte array to be converted
	 * @return byte[] the PDF
	 */
	public byte[] convertToPdf(byte[] file);

	/**
	 * Put a VA approved banner stamp on the provided PDF byte array.
	 *
	 * @param file the PDF file as a byte array
	 * @return byte[] the stamped PDF
	 */
	public byte[] stampPdf(String stampContent, byte[] file);
}
