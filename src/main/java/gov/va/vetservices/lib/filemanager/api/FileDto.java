package gov.va.vetservices.lib.filemanager.api;

import java.util.Arrays;

/**
 * Transfer object for files.
 *
 * @author aburkholder
 */
public class FileDto {

	/** The file byte array. */
	private byte[] fileBytes;

	/** The filename associated with the fileBytes. */
	private String fileName;

	/** The maximum number of bytes allowed in the fileBytes. Default is 25,600 bytes. */
	private int maxBytes = 25600;

	public byte[] getFileBytes() {
		return fileBytes;
	}

	public void setFileBytes(byte[] fileBytes) {
		this.fileBytes = fileBytes;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + Arrays.hashCode(fileBytes);
		result = (prime * result) + ((fileName == null) ? 0 : fileName.hashCode());
		result = (prime * result) + maxBytes;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FileDto)) {
			return false;
		}
		FileDto other = (FileDto) obj;
		if (!Arrays.equals(fileBytes, other.fileBytes)) {
			return false;
		}
		if (fileName == null) {
			if (other.fileName != null) {
				return false;
			}
		} else if (!fileName.equals(other.fileName)) {
			return false;
		}
		if (maxBytes != other.maxBytes) {
			return false;
		}
		return true;
	}

	/**
	 * Returns a human-readable representation of the object, in the form of standard java log output. If the fileBytes field is
	 * populated, its output as part of this method will be truncated at 512 bytes.
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		byte[] fb = fileBytes == null ? new byte[] {} : Arrays.copyOf(fileBytes, Math.min(fileBytes.length, 509));
		return "FileDto [fileBytes=" + Arrays.toString(fb) + "..., fileName=" + fileName + ", maxBytes=" + maxBytes + "]";
	}

}
