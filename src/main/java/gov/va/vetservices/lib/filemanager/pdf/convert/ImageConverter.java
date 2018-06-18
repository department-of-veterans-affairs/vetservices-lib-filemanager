package gov.va.vetservices.lib.filemanager.pdf.convert;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.exception.PdfConverterException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.pdf.itext.LayoutAwarePdfDocument;

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
	public byte[] getPdf(final byte[] bytes, final FilePartsDto parts) throws FileManagerException {

		byte[] pdfBytes = null;
		final LayoutAwarePdfDocument pdfDocument = new LayoutAwarePdfDocument();

		try {
			final Image image = new Image(ImageDataFactory.create(bytes));
			final float fitWidth = Math.min(FIT_500F, image.getImageScaledWidth());
			final float fitHeight = Math.min(FIT_500F, image.getImageScaledHeight());

			image.scaleToFit(fitWidth, fitHeight);
			pdfDocument.getLayoutDocument().add(image);

			pdfBytes = pdfDocument.getOutput();

		} catch (final Throwable e) { // NOSONAR - intentionally catching everything
			super.doThrowException(e, parts.getName() + "." + parts.getExtension());
		} finally {
			super.closeDocument(pdfDocument, parts);
		}

		return pdfBytes;
	}

}
