package gov.va.vetservices.lib.filemanager.pdf.stamp;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.ascent.framework.util.SanitizationUtil;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.exception.PdfStamperException;
import gov.va.vetservices.lib.filemanager.impl.dto.DocMetadataDto;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.pdf.itext.LayoutAwarePdfDocument;
import gov.va.vetservices.lib.filemanager.pdf.stamp.dto.StampDataDto;
import gov.va.vetservices.lib.filemanager.util.MessageUtils;

/**
 * Writes {@link StampDataDto} information into the header area of a PDF.
 *
 * @author aburkholder
 */
@Component(Stamper.BEAN_NAME)
public class Stamper {

	private static final Logger LOGGER = LoggerFactory.getLogger(Stamper.class);

	public static final String BEAN_NAME = "stamper";

	@Autowired
	@Qualifier(MessageUtils.BEAN_NAME)
	private MessageUtils messageUtils;

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

			final String stampText =
					StampsEnum.getStampText(stampDataDto.getProcessType(), metadata.getClaimId(), metadata.getDocDate());

			Rectangle rect = null;
			PdfCanvas canvas = null;
			for (int pageNum = 1; pageNum <= pdfDoc.getNumberOfPages(); pageNum++) {
				final PdfPage page = pdfDoc.getPage(pageNum);
				rect = page.getPageSize();
				canvas = new PdfCanvas(page);
				// prep text font and colors
				canvas.beginText();
				canvas.setFontAndSize(PdfFontFactory.createFont(stampDataDto.getFontName()),
						Float.parseFloat(Integer.toString(stampDataDto.getFontSizeInPoints())));
				canvas.setFillColor(new DeviceCmyk(100, 100, 100, 0));
				// position the text and place it on the page
				final double xCoord = Double.parseDouble("5.0");
				final double yCoord = rect.getHeight() - stampDataDto.getFontSizeInPoints();
				canvas.moveText(xCoord, yCoord).showText(stampText).endText().endText().release();
			}

			stampedPdf = pdfDoc.closeAndGetOutput();

		} catch (final Throwable e) { // NOSONAR intentionally catching everything
			final String mke = LibFileManagerMessageKeys.PDF_STAMPING;
			final String msg = messageUtils.returnMessage(mke, SanitizationUtil.safeFilename(fileDto.getFilename()),
					e.getClass().getSimpleName() + " - " + StringUtils.substringBefore(e.getMessage(), "\\n"));
			LOGGER.error(mke + ": " + msg, e);
			throw new PdfStamperException(e, MessageSeverity.ERROR, mke, msg);
		} finally {
			if (pdfDoc != null) {
				pdfDoc.close();
			}
		}
		return stampedPdf;
	}

}
