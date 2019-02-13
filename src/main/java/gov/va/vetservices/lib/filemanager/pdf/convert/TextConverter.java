/**
 *
 */
package gov.va.vetservices.lib.filemanager.pdf.convert;

import java.io.BufferedReader;

import org.apache.commons.lang3.StringUtils;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.exception.PdfConverterException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.pdf.itext.LayoutAwarePdfDocument;

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
	public byte[] getPdf(final byte[] bytes, final FilePartsDto parts)  {

		byte[] pdfBytes = null;
		LayoutAwarePdfDocument pdfDocument = null;

		final BufferedReader bufferReader = getPlainTextReader(bytes);

		try { // NOSONAR - try-with-resource does not work if var is needed for finally block
			pdfDocument = new LayoutAwarePdfDocument();
			@SuppressWarnings("resource")
			final Document doc = new Document(pdfDocument); // NOSONAR - this gets closed by pdfDoc

			final StringBuilder str = new StringBuilder();
			String line = bufferReader.readLine();
			while (line != null) {
				if (StringUtils.isBlank(line)) {
					doc.add(makeParagraph(str));
				} else {
					str.append(str.length() > 0 ? " " : "").append(line);
				}
				line = bufferReader.readLine();
			}
			if (str.length() > 0) {
				doc.add(makeParagraph(str));
			}

			pdfBytes = pdfDocument.closeAndGetOutput();

		} catch (final Exception e) { // NOSONAR - intentionally catching everything
			super.doThrowException(e, parts.getName() + "." + parts.getExtension());
		} finally {
			super.closeDocument(pdfDocument, parts);
		}

		return pdfBytes;
	}

	/**
	 * Adds a newline to the StringBuilder, creates the iText Paragraph, resets the StringBuilder value to empty, and returns the
	 * Paragraph.
	 *
	 * @param strbldr the String content of the paragraph
	 * @return Paragraph the iText Paragraph
	 */
	protected Paragraph makeParagraph(final StringBuilder strbldr) {
		strbldr.append(NEW_LINE);
		final Paragraph para = new Paragraph(strbldr.toString());
		strbldr.setLength(0);
		return para;
	}
}
