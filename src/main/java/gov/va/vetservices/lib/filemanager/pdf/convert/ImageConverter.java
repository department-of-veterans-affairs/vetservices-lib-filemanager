package gov.va.vetservices.lib.filemanager.pdf.convert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileParts;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.exception.PdfConverterException;

/**
 * Convert the byte array of a supported "image/*" file into a PDF.
 * It is assumed that the submitted file has already been validated to be convertible.
 * If the file cannot be converted, an exception is thrown.
 *
 * @author aburkholder
 */
public class ImageConverter extends AbstractConverter {

	/**
	 * Convert an image into a PDF.
	 * The {@code bytes} file byte array must be of type "image/*".
	 * This method assumes that the image bytes have been validated and are supported.
	 * If the byte array cannot be converted, an exception is thrown.
	 *
	 * @param bytes the image file byte array
	 * @param parts the file name information
	 * @return byte[] the PDF
	 * @throws PdfConverterException some problem processing the bytes
	 */
	@Override
	public byte[] getPdf(byte[] bytes, FileParts parts) throws FileManagerException {
		final ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
		final Document pdfDocument = new Document();
		PdfWriter pdfWriter = null;
		try {
			pdfWriter = PdfWriter.getInstance(pdfDocument, bytesOutputStream);

			initializePdfDocument(pdfDocument);

			final Image image = Image.getInstance(bytes);
			final float fitWidth = Math.min(FIT_500F, image.getWidth());
			final float fitHeight = Math.min(FIT_500F, image.getHeight());

			image.scaleToFit(fitWidth, fitHeight);
			pdfDocument.add(image);

		} catch (DocumentException | IOException e) {
			throwException(e, parts.getName() + "." + parts.getExtension());
		} finally {
			close(pdfDocument, pdfWriter);
		}

		return bytesOutputStream.toByteArray();
	}

}
