package gov.va.vetservices.lib.filemanager.pdf.stamp;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Chunk;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.exception.PdfStamperException;
import gov.va.vetservices.lib.filemanager.impl.dto.DocMetadataDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.pdf.font.PdfFontFactory;
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
	 * @param bytes the PDF
	 * @return byte[] the stamped PDF
	 * @throws IllegalArgumentException if any argument is {@code null} or empty
	 * @throws PdfStamperException if the PDF cannot be stamped
	 */
	public final byte[] stamp(final DocMetadataDto metadata, final StampDataDto stampDataDto, final FileDto fileDto)
			throws PdfStamperException {

		final ByteArrayOutputStream pdf = new ByteArrayOutputStream();

		if ((metadata == null) || (stampDataDto == null) || (fileDto == null)) {
			LOGGER.error("Arguments cannot be null.");
			throw new IllegalArgumentException(
					"Arguments to " + this.getClass().getSimpleName() + ".stamp(..) cannot be null or empty.");
		}

		PdfReader pdfReader = null;
		PdfStamper pdfStamper = null;
		try {
			pdfReader = new PdfReader(fileDto.getFilebytes());
			pdfReader.setAppendable(true); // make sure we can append

			pdfStamper = new PdfStamper(pdfReader, pdf);
			String stampText = StampsEnum.getStampText(stampDataDto.getProcessType(), metadata.getClaimId());

			final ColumnText columnText = new ColumnText(null);
			for (int pageNum = 1; pageNum <= pdfReader.getNumberOfPages(); pageNum++) {
				final Rectangle rect = pdfReader.getPageSize(pageNum);
				final Chunk textAsChunk = new Chunk(stampText, PdfFontFactory.getFont(stampDataDto.getFontName()));
				textAsChunk.setBackground(new Color(255, 255, 255), 5f, 5f, 25f, 10f);

				ColumnText.showTextAligned(pdfStamper.getOverContent(pageNum), PdfContentByte.ALIGN_LEFT, new Phrase(textAsChunk), 5f,
						rect.getHeight() - stampDataDto.getFontSizeInPoints(), 0, 0, 0);
			}
			while (true) {
				if (!ColumnText.hasMoreText(columnText.go())) {
					break;
				}
			}
		} catch (final Throwable e) { // NOSONAR intentionally catching everything
			MessageKeysEnum mke = MessageKeysEnum.PDF_STAMPING;
			String msg = MessageFormat.format(mke.getMessage(), fileDto.getFilename(),
					e.getClass().getSimpleName() + " - " + StringUtils.substringBefore(e.getMessage(), "\n"));
			LOGGER.error(mke.getKey() + ": " + msg, e);
			throw new PdfStamperException(e, MessageSeverity.ERROR, mke.getKey(), msg);
		} finally {
			close(pdfStamper, pdfReader);
		}
		return pdf.toByteArray();
	}

	/**
	 * Close stamper and pdfReader.
	 *
	 * @param pdfStamper the stamper.
	 * @param pdfReader the pdfReader.
	 */
	private void close(final PdfStamper pdfStamper, final PdfReader pdfReader) {
		try {
			if (pdfStamper != null) {
				pdfStamper.close();
			}
		} catch (final Exception e) { // NOSONAR - intentionally catching generic exception
			LOGGER.error("pdfStamper.close() failed", e);
		}
		try {
			if (pdfReader != null) {
				pdfReader.close();
			}
		} catch (final Exception e) { // NOSONAR - intentionally catching generic exception
			LOGGER.error("pdfReader.close() failed", e);
		}
	}
}
