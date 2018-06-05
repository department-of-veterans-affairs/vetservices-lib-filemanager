package gov.va.vetservices.lib.filemanager.pdf.itext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.itextpdf.kernel.pdf.PdfWriter;

/**
 * A wrapper for {@link PdfWriter} that provides a reference to the writer's output stream.
 * Without this wrapper, there is no way to access the output stream from the writer.
 * The output stream is automatically closed when the PdfWriter's output stream is closed.
 * <p>
 * Intended for use only by {@link LayoutAwarePdfDocument} and other wrappers in this package.
 *
 * @author aburkholder
 */
class ByteArrayPdfWriter extends PdfWriter {
	private static final long serialVersionUID = -3270651130851410516L;

	transient ByteArrayOutputStream destination;

	/**
	 * Creates a {@link PdfWriter} that provides a reference to the writer's output stream via {@link #getDestinationBytes()}.
	 * The output stream is automatically closed when the PdfWriter's output stream is closed.
	 * <p>
	 * When instantiating this class, the preferred way to specify the parameter is: {@code new ByteArrayOutputStream()}.
	 *
	 * @param destination the byte array stream to write into.
	 */
	ByteArrayPdfWriter(final ByteArrayOutputStream destination) {
		super(destination);
		this.destination = destination;
	}

	/**
	 * Get the bytes from the output stream that was used to create the {@link PdfWriter}.
	 * The PdfWriter output is automatically flushed prior to retrieving the byte array.
	 *
	 * @return byte[] the output bytes from the PdfWriter
	 */
	final byte[] getDestinationBytes() {
		try {
			super.getOutputStream().flush();
		} catch (final IOException e) { // NOSONAR it is a byte array - this will never happen and if it does is irrelevant
			// NOSONAR it is a byte array - this will never happen and if it does is irrelevant
		}
		return destination == null ? null : destination.toByteArray();
	}
}
