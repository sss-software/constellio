package com.constellio.app.modules.rm.model.SIPArchivedGenerator.constellio.sip.model;


public interface SIPFolder extends SIPObject {
	
	String FOLDER_TYPE = "DOSSIER";

	String getTitle();
	
	SIPFolder getParentFolder();
	
	SIPCategory getCategory();
	
}
