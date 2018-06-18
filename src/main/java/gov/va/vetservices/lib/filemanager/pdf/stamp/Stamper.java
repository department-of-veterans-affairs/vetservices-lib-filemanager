package gov.va.vetservices.lib.filemanager.pdf.stamp;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.exception.PdfStamperException;
import gov.va.vetservices.lib.filemanager.impl.dto.DocMetadataDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.pdf.itext.LayoutAwarePdfDocument;
import gov.va.vetservices.lib.filemanager.pdf.stamp.dto.StampDataDto;

/**
 * Writes {@link StampDataDto} information into the header area of a PDF.
 *
 * @author aburkholder
 */
public class Stamper {
	private static final Logger LOGGER = LoggerFactory.getLogger(Stamper.class);

	/**
	 * Write {@link StampDataDto} information into the header area of a PDF.
	 * All arguments are required.
	 * <p>
	 * If any element of any argument is empty or null, there will be NullPointerExceptions.
	 *
	 * @param stampDataDto the StampDataDto text and font info
	 * @param bytes the PDF to stamp
	 * @return byte[] the stamped PDF
	 * @throws IllegalArgumentException if any argument is {@code null} or empty
	 * @throws PdfStamperException if the PDF cannot be stamped
	 */
	public final byte[] stamp(final DocMetadataDto metadata, final StampDataDto stampDataDto, final FileDto fileDto)
			throws PdfStamperException {

		byte[] stampedPdf = null;

		if (metadata == null || stampDataDto == null || fileDto == null) {
			LOGGER.error("Arguments cannot be null.");
			throw new IllegalArgumentException(
					"Arguments to " + this.getClass().getSimpleName() + ".stamp(..) cannot be null or empty.");
		}

		LayoutAwarePdfDocument pdfDoc = null;
		try { // NOSONAR - try-with-resource does not work if you have to use the var in the finally block
			pdfDoc = new LayoutAwarePdfDocument(fileDto.getFilebytes());

			final String stampText = StampsEnum.getStampText(stampDataDto.getProcessType(), metadata.getClaimId());

			Rectangle rect = null;
			PdfCanvas canvas = null;
			for (int pageNum = 1; pageNum <= pdfDoc.getNumberOfPages(); pageNum++) {
				final PdfPage page = pdfDoc.getPage(pageNum);
				rect = page.getPageSize();
				canvas = new PdfCanvas(page);
				canvas.beginText()
						.setFontAndSize(PdfFontFactory.createFont(stampDataDto.getFontName()),
								Float.parseFloat(Integer.toString(stampDataDto.getFontSizeInPoints())))
						.setFillColor(new DeviceCmyk(100, 100, 100, 0))
						.moveText(5, rect.getHeight() - stampDataDto.getFontSizeInPoints()).showText(stampText).endText();
			}

			stampedPdf = pdfDoc.getOutput();

		} catch (final Throwable e) { // NOSONAR intentionally catching everything
			final MessageKeysEnum mke = MessageKeysEnum.PDF_STAMPING;
			final String msg = MessageFormat.format(mke.getMessage(), fileDto.getFilename(),
					e.getClass().getSimpleName() + " - " + StringUtils.substringBefore(e.getMessage(), "\n"));
			LOGGER.error(mke.getKey() + ": " + msg, e);
			throw new PdfStamperException(e, MessageSeverity.ERROR, mke.getKey(), msg);
		} finally {
			if (pdfDoc != null) {
				pdfDoc.close();
			}
		}
		return stampedPdf;
	}

}
