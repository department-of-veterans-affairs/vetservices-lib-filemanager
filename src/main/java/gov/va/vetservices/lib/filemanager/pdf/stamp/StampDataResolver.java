package gov.va.vetservices.lib.filemanager.pdf.stamp;

import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.pdf.stamp.dto.StampDataDto;

/**
 * Resolve the stamp data that applies to the file contained in the data transfer object.
 *
 * @author aburkholder
 */
public class StampDataResolver {

	/**
	 * Resolve the stamp data that applies to the file contained in the data transfer object.
	 *
	 * @param implDto the file information
	 * @return the StampDataDto
	 */
	public StampDataDto resolve(ImplDto implDto) {
		// NOSONAR TODO this is all junk code for the moment ...
		implDto = implDto == null ? new ImplDto() : implDto;
		if ((implDto.getFileDto() == null) || (implDto.getFileDto().getFormName() == null)) {
			// eventually throw an exception here
			implDto.getMessages().add(new Message());
		}
		return new StampDataDto();
	}

}
