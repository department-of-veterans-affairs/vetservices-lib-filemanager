
package gov.va.vetservices.lib.filemanager.impl.dto;

import gov.va.ascent.framework.transfer.AbstractTransferObject;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

/**
 * The Data Transfer Object for transporting data across the layer boundaries for the API and the impelmentation.
 * Creation of this DTO can be eased by using {@link FileManagerUtils#getFileParts(String)}.
 * <p>
 * The file name extension is defined as any characters that follow the last "." (period) in the filename.
 * The name portion is defined as any characters that precede the last "." (period) in the filename.
 *
 * <pre>
 * null        : name=null      : ext=null
 * ""          : name=null      : ext=null
 * "file.name" : name="file"    ; ext="name"
 * ".name"     : name=null      ; ext="name"
 * "file."     : name="file"    ; ext=null
 * "filename"  : name="filename"; ext=null
 * </pre>
 */
public class FilePartsDto extends AbstractTransferObject {
	private static final long serialVersionUID = 7028333764092299053L;

	protected String name;
	protected String extension;

	/**
	 * Gets the name portion of a filename.
	 *
	 * @return String the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name portion of a filename.
	 *
	 * @param value the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the extension portion of the filename.
	 *
	 * @return String the file name extension
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * Sets the extension portion of the filename.
	 *
	 * @param extension the file name extension
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

}
