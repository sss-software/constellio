package com.constellio.app.modules.rm.ui.pages.document;

import java.util.List;

import com.constellio.app.modules.rm.ui.components.document.DocumentActionsComponent;
import com.constellio.app.modules.rm.ui.pages.viewGroups.RecordsManagementViewGroup;
import com.constellio.app.ui.entities.ContentVersionVO;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.BaseView;

public interface DisplayDocumentView extends BaseView, RecordsManagementViewGroup, DocumentActionsComponent {

	void refreshMetadataDisplay();

	void setContentVersions(List<ContentVersionVO> contentVersions);

	void setTasks(RecordVODataProvider tasksDataProvider);
	
}
