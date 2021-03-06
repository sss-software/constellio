package com.constellio.app.ui.framework.components.viewers.panel;

import com.constellio.app.modules.rm.ui.pages.folder.DisplayFolderView;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.pages.base.BaseView;
import com.constellio.app.ui.pages.search.SearchView;
import com.vaadin.navigator.ViewChangeListener;

import java.util.HashMap;
import java.util.Map;

public class ViewableRecordVOViewChangeListener implements ViewChangeListener {

	private Map<String, Integer> searchViewReturnIndexes = new HashMap<>();

	private Map<String, RecordVO> searchViewReturnRecordVOs = new HashMap<>();

	private Map<String, Integer> displayFolderViewReturnIndexes = new HashMap<>();

	private Map<String, RecordVO> displayFolderViewReturnRecordVOs = new HashMap<>();

	@Override
	public boolean beforeViewChange(ViewChangeEvent event) {
		BaseView oldView = (BaseView) event.getOldView();
		if (oldView instanceof SearchView) {
			SearchView searchView = (SearchView) oldView;
			String savedSearchId = searchView.getSavedSearchId();
			if (savedSearchId != null) {
				Integer returnIndex = searchView.getReturnIndex();
				RecordVO returnRecordVO = searchView.getReturnRecordVO();
				searchViewReturnIndexes.put(savedSearchId, returnIndex);
				searchViewReturnRecordVOs.put(savedSearchId, returnRecordVO);
			}
		} else if (oldView instanceof DisplayFolderView) {
			DisplayFolderView displayFolderView = (DisplayFolderView) oldView;
			RecordVO recordVO = displayFolderView.getRecord();
			Integer returnIndex = displayFolderView.getReturnIndex();
			RecordVO returnRecordVO = displayFolderView.getReturnRecordVO();
			if (recordVO != null) {
				displayFolderViewReturnIndexes.put(recordVO.getId(), returnIndex);
				displayFolderViewReturnRecordVOs.put(recordVO.getId(), returnRecordVO);
			}
		}
		return true;
	}

	@Override
	public void afterViewChange(ViewChangeEvent event) {
		BaseView newView = (BaseView) event.getNewView();
		if (newView instanceof DisplayFolderView) {
			DisplayFolderView displayFolderView = (DisplayFolderView) newView;
			RecordVO recordVO = displayFolderView.getRecord();
			String recordId = recordVO.getId();
			Integer returnIndex = displayFolderViewReturnIndexes.get(recordId);
			RecordVO returnRecordVO = displayFolderViewReturnRecordVOs.get(recordId);
			if (returnIndex == null || returnRecordVO == null || !displayFolderView.scrollIntoView(returnIndex, returnRecordVO.getId())) {
				displayFolderViewReturnIndexes.remove(recordId);
				displayFolderViewReturnRecordVOs.remove(recordId);
			}
		} else if (newView instanceof SearchView) {
			SearchView searchView = (SearchView) newView;
			String savedSearchId = searchView.getSavedSearchId();
			if (savedSearchId != null) {
				Integer returnIndex = searchViewReturnIndexes.get(savedSearchId);
				RecordVO returnRecordVO = searchViewReturnRecordVOs.get(savedSearchId);
				if (returnIndex == null || returnRecordVO == null || !searchView.scrollIntoView(returnIndex, returnRecordVO.getId())) {
					searchViewReturnIndexes.remove(savedSearchId);
					searchViewReturnRecordVOs.remove(savedSearchId);
				}
			}
		}
	}

}
