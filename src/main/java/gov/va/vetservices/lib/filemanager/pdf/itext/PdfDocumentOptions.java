package gov.va.vetservices.lib.filemanager.pdf.itext;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.layout.property.TextAlignment;

/**
 * A convenience class to set options that can be set on the iText {@link PdfDocument} and {@link Document} objects.
 *
 * @author aburkholder
 */
public class PdfDocumentOptions {

	/** The default page size, applied when instantiated: default is {@code PageSize.LETTER} */
	public static final PageSize DEFAULT_PAGE_SIZE = PageSize.LETTER;
	/** The default rotation action, applied when instantiated; Portrait = false, Landscape = true: default is {@code false} */
	public static final boolean DEFAULT_ROTATED = false;
	/** The default text alignment, applied when instantiated: default is {@code TextAlignment.JUSTIFIED} */
	public static final TextAlignment DEFAULT_TEXT_ALIGNMENT = TextAlignment.JUSTIFIED;
	/** The default hyphenation config, applied when intantiated: default is {@code new HyphenationConfig("en", "us", 3, 3)} */
	public static final HyphenationConfig DEFAULT_HYPHENATION = new HyphenationConfig("en", "us", 3, 3);
	/** The default {@link StandardFonts} font face, applied when instantiated: default is {@code StandardFonts.HELVETICA} */
	public static final String DEFAULT_FONT = StandardFonts.HELVETICA;
	/** The default font size in points, applied when instantiated: default is {@code 11} */
	public static final int DEFAULT_FONT_SIZE = 11;
	/** The default Stamping modes (append mode, preserve encryption): default is: {@code new StampingProperties().useAppendMode()} */
	public static final StampingProperties DEFAULT_STAMPING_PROPERTIES = new StampingProperties().useAppendMode();

	private PageSize pageSize;
	private boolean rotated;
	private TextAlignment textAlignment;
	private HyphenationConfig hyphenation;
	private String font;
	private int fontSize;
	private StampingProperties stampingProperties;

	/**
	 * Create PDF Document options, instantiating using the default value for each option. Options can be set individually using the
	 * setters.
	 */
	public PdfDocumentOptions() {
		this.pageSize = DEFAULT_PAGE_SIZE;
		this.rotated = DEFAULT_ROTATED;
		this.textAlignment = DEFAULT_TEXT_ALIGNMENT;
		this.hyphenation = DEFAULT_HYPHENATION;
		this.font = DEFAULT_FONT;
		this.fontSize = DEFAULT_FONT_SIZE;
		this.stampingProperties = DEFAULT_STAMPING_PROPERTIES;
	}

	/**
	 * Create PDF Document options, specifying the value for each option. Options can also be set individually using the setters.
	 *
	 * @param pageSize the {@link PageSize}, default {@link #DEFAULT_PAGE_SIZE}
	 * @param rotated the rotation directive, default {@link #DEFAULT_ROTATED}
	 * @param textAlignment the {@link TextAlignment}, default {@link #DEFAULT_TEXT_ALIGNMENT}
	 * @param hyphenation the {@link HyphenationConfig}, default {@link #DEFAULT_HYPHENATION}
	 * @param font the {@link StandardFonts} font face, default {@link #DEFAULT_FONT}
	 * @param fontSize the font size in points, default {@link #DEFAULT_FONT_SIZE}
	 * @param stampingProperties the stamping properties, default {@link #DEFAULT_STAMPING_PROPERTIES}
	 *
	 */
	public PdfDocumentOptions(final PageSize pageSize, final boolean rotated, final TextAlignment textAlignment,
			final HyphenationConfig hyphenation, final String font, final int fontSize, final StampingProperties stampingProperties) {
		this.pageSize = pageSize;
		this.rotated = rotated;
		this.textAlignment = textAlignment;
		this.hyphenation = hyphenation;
		this.font = font;
		this.fontSize = fontSize;
		this.stampingProperties = stampingProperties;
	}

	/**
	 * Get the {@link PageSize}.
	 * Default is {@link #DEFAULT_PAGE_SIZE}.
	 *
	 * @return the pageSize
	 */
	public PageSize getPageSize() {
		return pageSize;
	}

	/**
	 * Set the {@link PageSize}.
	 * Default is {@link #DEFAULT_PAGE_SIZE}.
	 *
	 * @param the pageSize to set
	 */
	public void setPageSize(final PageSize pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Get the rotation directive. If {@code true}, the page is rotated to Landscape orientation, otherwise it remains in Portrait
	 * orientation.
	 * Default is {@link #DEFAULT_ROTATED}.
	 *
	 * @return the rotation directive
	 */
	public boolean isRotated() {
		return rotated;
	}

	/**
	 * Set the rotation directive. If {@code true}, the page is rotated to Landscape orientation, otherwise it remains in Portrait
	 * orientation.
	 * Default is {@link #DEFAULT_ROTATED}.
	 *
	 * @param rotated if {@code true}, is rotated from Portrait to Landscape orientation
	 */
	public void setRotated(final boolean rotated) {
		this.rotated = rotated;
	}

	/**
	 * Get the {@link TextAlignment}.
	 * Default is {@link #DEFAULT_TEXT_ALIGNMENT}.
	 *
	 * @return the textAlignment
	 */
	public TextAlignment getTextAlignment() {
		return textAlignment;
	}

	/**
	 * Set the {@link TextAlignment}.
	 * Default is {@link #DEFAULT_TEXT_ALIGNMENT}.
	 *
	 * @param textAlignment the textAlignment to set
	 */
	public void setTextAlignment(final TextAlignment textAlignment) {
		this.textAlignment = textAlignment;
	}

	/**
	 * Get the {@link HyphenationConfig}.
	 * Default is {@link #DEFAULT_HYPHENATION}.
	 *
	 * @return the hyphenation
	 */
	public HyphenationConfig getHyphenation() {
		return hyphenation;
	}

	/**
	 * Set the {@link HyphenationConfig} as, for example, {@code new HyphenationConfig(lang, country, leftMin, rightMin)}.
	 * Default is {@link #DEFAULT_HYPHENATION}.
	 *
	 * @param hyphenation the hyphenation to set
	 */
	public void setHyphenation(final HyphenationConfig hyphenation) {
		this.hyphenation = hyphenation;
	}

	/**
	 * Get the {@link StandardFonts} font face name.
	 * Default is {@link #DEFAULT_FONT}.
	 *
	 * @return the font
	 */
	public String getFont() {
		return font;
	}

	/**
	 * Set the {@link StandardFonts} font face name.
	 * Default is {@link #DEFAULT_FONT}.
	 *
	 * @param font the font to set
	 */
	public void setFont(final String font) {
		this.font = font;
	}

	/**
	 * Get the font size in points.
	 * Default is {@link #DEFAULT_FONT_SIZE}.
	 *
	 * @return the fontSize
	 */
	public int getFontSize() {
		return fontSize;
	}

	/**
	 * Set the font size in points.
	 * Default is {@link #DEFAULT_FONT_SIZE}.
	 *
	 * @param fontSize the fontSize to set
	 */
	public void setFontSize(final int fontSize) {
		this.fontSize = fontSize;
	}

	/**
	 * Get the Stamping properties for append mode and/or preserving encryption.
	 * This method is only useful for null checks. You can set the properties easily. Examples:
	 *
	 * <pre>
	 * // only turn on append mode
	 * new StampingProperties().useAppendMode();
	 * // only preserve encryption
	 * new StampingProperties().preserveEncryption();
	 * // turn on append mode and preserve encryption
	 * new StampingProperties().useAppendMode().preserveEncryption();
	 * </pre>
	 *
	 * Default is {@link #DEFAULT_STAMPING_PROPERTIES}.
	 *
	 * @return the stampingProperties
	 */
	public StampingProperties getStampingProperties() {
		return stampingProperties;
	}

	/**
	 * Set the Stamping properties for append mode and/or preserving encryption.
	 * Examples:
	 *
	 * <pre>
	 * // only turn on append mode
	 * new StampingProperties().useAppendMode();
	 * // only preserve encryption
	 * new StampingProperties().preserveEncryption();
	 * // turn on append mode and preserve encryption
	 * new StampingProperties().useAppendMode().preserveEncryption();
	 * </pre>
	 *
	 * Default is {@link #DEFAULT_STAMPING_PROPERTIES}.
	 *
	 * @param stampingProperties the stampingProperties to set
	 */
	public void setStampingProperties(final StampingProperties stampingProperties) {
		this.stampingProperties = stampingProperties;
	}

}
