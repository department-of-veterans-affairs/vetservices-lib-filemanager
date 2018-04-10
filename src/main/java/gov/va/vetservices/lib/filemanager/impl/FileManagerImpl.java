package gov.va.vetservices.lib.filemanager.impl;

import gov.va.vetservices.lib.filemanager.api.FileManager;

/**
 * Implementation of the {@link FileManager} capabilities
 *
 * @author aburkholder
 */
/*
 * TODO Dev notes:
 * In WSS there is a disconnect between PDFServiceImpl and PdfGenerator.
 * One objective of this class is to combine the best of both for consistent results.
 */
public class FileManagerImpl implements FileManager {

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.vetservices.lib.filemanager.api.FileManager#validateFileForPDFConversion(gov.va.vetservices.lib.filemanager.api.
	 * FileManager.FileDto)
	 *
	 * TODO Dev notes:
	 * - Method is sourced from wss ImageServiceImpl.validateFile(..) & gov.va.wss.document.services.service.itext.validate.*Validator
	 * - FileDto is just name change from ImageDto.
	 * - As any good validation should do, boolean is now returned.
	 */
	@Override
	public boolean validateFileForPDFConversion(FileDto fileDto) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.vetservices.lib.filemanager.api.FileManager#convertToPdf(byte[])
	 *
	 * TODO Dev notes:
	 * - Method is sourced from wss PDFServiceImpl.convertPDF(..) & PDFGenerate.generateBody()
	 */
	@Override
	public byte[] convertToPdf(byte[] file) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.vetservices.lib.filemanager.api.FileManager#stampPdf(java.lang.String, byte[])
	 *
	 * TODO Dev notes:
	 * - Method is sourced from wss PDFServiceImpl.stampPDF(..) & PDFGenerate.generateBody()
	 * - It may be necesary to provide more information to this method
	 * - ... could replace String param with a model (content, font size, etc as needed)
	 */
	@Override
	public byte[] stampPdf(String stampContent, byte[] file) {
		// TODO Auto-generated method stub
		return null;
	}

}
