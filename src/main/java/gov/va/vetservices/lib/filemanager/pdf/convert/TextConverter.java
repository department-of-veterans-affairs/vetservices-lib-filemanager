/**
 *
 */
package gov.va.vetservices.lib.filemanager.pdf.convert;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileParts;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.exception.PdfConverterException;

/**
 * Convert the byte array of a supported "text/plain" file into a PDF.
 * It is assumed that the submitted file has already been validated to be convertible.
 * If the file cannot be converted, an exception is thrown.
 *
 * @author aburkholder
 */
public class TextConverter extends AbstractConverter {

	/**
	 * Convert a text file into a PDF.
	 * The {@code bytes} file byte array must be of type "text/plain".
	 * This method assumes that the {@code bytes} parameter has already been validated and confirmed to be convertible.
	 * If the byte array cannot be converted, an exception is thrown.
	 *
	 * @param bytes the text file byte array
	 * @return byte[] the PDF byte array
	 * @throws PdfConverterException some problem processing the bytes
	 */
	@Override
	public byte[] getPdf(byte[] bytes, FileParts parts) throws FileManagerException {
		final ByteArrayOutputStream pdfContent = new ByteArrayOutputStream();
		final Document pdfDocument = new Document();
		PdfWriter writer = null;
		final BufferedReader bufferReader = getPlainTextReader(bytes);

		try {
			writer = PdfWriter.getInstance(pdfDocument, pdfContent);

			initializePdfDocument(pdfDocument);
			String line = bufferReader.readLine();

			StringBuilder paragraph = new StringBuilder();
			while (line != null) {
				if (line.isEmpty()) {
					pdfDocument.add(new Paragraph(paragraph.toString()));
					pdfDocument.add(new Paragraph(NEW_LINE));
					paragraph.setLength(0);
				} else {
					paragraph.append(paragraph.length() > 0 ? " " : "").append(line.trim());
				}
				line = bufferReader.readLine();
			}
			if (paragraph.length() > 0) {
				pdfDocument.add(new Paragraph(paragraph.toString()));
			}

		} catch (DocumentException | IOException e) {
			throwException(e, parts.getName() + "." + parts.getExtension());
		} finally {
			close(pdfDocument, writer);
		}

		return pdfContent.toByteArray();
	}

}
