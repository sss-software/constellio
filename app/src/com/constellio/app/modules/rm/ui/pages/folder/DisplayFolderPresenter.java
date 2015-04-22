/*Constellio Enterprise Information Management

Copyright (c) 2015 "Constellio inc."

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.constellio.app.modules.rm.ui.pages.folder;

import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.modules.rm.constants.RMPermissionsTo;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.services.decommissioning.DecommissioningService;
import com.constellio.app.modules.rm.ui.builders.DocumentToVOBuilder;
import com.constellio.app.modules.rm.ui.builders.FolderToVOBuilder;
import com.constellio.app.modules.rm.ui.entities.ComponentState;
import com.constellio.app.modules.rm.ui.entities.DocumentVO;
import com.constellio.app.modules.rm.ui.entities.FolderVO;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.services.factories.ConstellioFactories;
import com.constellio.app.ui.entities.ContentVersionVO;
import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.framework.builders.MetadataSchemaToVOBuilder;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.SchemaPresenterUtils;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.app.ui.pages.base.SingleSchemaBasePresenter;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.services.search.query.logical.condition.LogicalSearchCondition;
import com.constellio.model.services.security.AuthorizationsServices;

public class DisplayFolderPresenter extends SingleSchemaBasePresenter<DisplayFolderView> {

	private static Logger LOGGER = LoggerFactory.getLogger(DisplayFolderPresenter.class);

	private RecordVODataProvider documentsDataProvider;

	private RecordVODataProvider subFoldersDataProvider;

	private MetadataSchemaToVOBuilder schemaVOBuilder = new MetadataSchemaToVOBuilder();

	private FolderToVOBuilder voBuilder = new FolderToVOBuilder();

	private DocumentToVOBuilder documentVOBuilder = new DocumentToVOBuilder();

	private SchemaPresenterUtils documentPresenterUtils;

	private FolderVO folderVO;

	public DisplayFolderPresenter(DisplayFolderView view) {
		super(view, Folder.DEFAULT_SCHEMA);
		initTransientObjects();

		ConstellioFactories constellioFactories = view.getConstellioFactories();
		SessionContext sessionContext = view.getSessionContext();
		documentPresenterUtils = new SchemaPresenterUtils(Document.DEFAULT_SCHEMA, constellioFactories, sessionContext);
	}

	private void readObject(java.io.ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		initTransientObjects();
	}

	private void initTransientObjects() {
	}

	public void forParams(String params) {
		Record record = getRecord(params);
		this.folderVO = voBuilder.build(record, VIEW_MODE.DISPLAY);
		view.setRecord(folderVO);
	}

	private void disableMenuItems(Folder folder) {
		User user = getCurrentUser();
		view.setLogicallyDeletable(getDeleteButtonState(user, folder));
		view.setEditButtonState(getEditButtonState(user, folder));
		view.setAddSubFolderButtonState(getAddFolderButtonState(user, folder));
		view.setAddDocumentButtonState(getAddDocumentButtonState(user, folder));
		view.setDuplicateFolderButtonState(getDuplicateFolderButtonState(user, folder));
		view.setShareFolderButtonState(getShareButtonState(user, folder));
		view.setPrintButtonState(getPrintButtonState(user, folder));
	}

	private ComponentState getPrintButtonState(User user, Folder folder) {
		AuthorizationsServices authorizationsServices = modelLayerFactory.newAuthorizationsServices();
		if (authorizationsServices.canRead(user, folder.getWrappedRecord())) {
			
			if (folder.getArchivisticStatus().isInactive()) {
				return ComponentState.enabledIf(user.has(RMPermissionsTo.MODIFY_INACTIVE_FOLDERS).on(folder));
			} else {
				
				if(folder.getArchivisticStatus().isSemiActive()) {
					return ComponentState.enabledIf(user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_FOLDERS).on(folder));
				}
				
				return ComponentState.ENABLED;
			}
		}
		return ComponentState.INVISIBLE;
	}

	private ComponentState getDuplicateFolderButtonState(User user, Folder folder) {
		AuthorizationsServices authorizationsServices = modelLayerFactory.newAuthorizationsServices();
		if (authorizationsServices.canWrite(user, folder.getWrappedRecord())) {
			
			if (folder.getArchivisticStatus().isInactive()) {
				return ComponentState.enabledIf(user.has(RMPermissionsTo.DUPLICATE_INACTIVE_FOLDER).on(folder));
			} else {
				
				if(folder.getArchivisticStatus().isSemiActive()) {
					return ComponentState.enabledIf(user.has(RMPermissionsTo.DUPLICATE_SEMIACTIVE_FOLDER).on(folder));
				}
				
				return ComponentState.ENABLED;
			}
		}
		return ComponentState.INVISIBLE;
	}

	private ComponentState getShareButtonState(User user, Folder folder) {
		if (user.has(RMPermissionsTo.SHARE_A_FOLDER).on(folder)) {
			
			if (folder.getArchivisticStatus().isInactive()) {
				return ComponentState.enabledIf(user.has(RMPermissionsTo.SHARE_A_INACTIVE_FOLDER).on(folder));
			} else {
				
				if(folder.getArchivisticStatus().isSemiActive()) {
					return ComponentState.enabledIf(user.has(RMPermissionsTo.SHARE_A_SEMIACTIVE_FOLDER).on(folder));
				}
				
				return ComponentState.ENABLED;
			}
		}
		return ComponentState.INVISIBLE;
	}

	private ComponentState getDeleteButtonState(User user, Folder folder) {
		if (user.hasAll(RMPermissionsTo.DELETE_FOLDERS).on(folder)) {
			
			if (folder.getArchivisticStatus().isInactive()) {
				return ComponentState.enabledIf(user.has(RMPermissionsTo.DELETE_INACTIVE_FOLDERS).on(folder));
			} else {
				
				if(folder.getArchivisticStatus().isSemiActive()) {
					return ComponentState.enabledIf(user.has(RMPermissionsTo.DELETE_SEMIACTIVE_FOLDERS).on(folder));
				}
				
				return ComponentState.ENABLED;
			}
		}
		return ComponentState.INVISIBLE;
	}

	private ComponentState getEditButtonState(User user, Folder folder) {
		if (user.has(RMPermissionsTo.MODIFY_FOLDERS).on(folder)) {
			
			if (folder.getArchivisticStatus().isInactive()) {
				return ComponentState.enabledIf(user.has(RMPermissionsTo.MODIFY_INACTIVE_FOLDERS).on(folder));
			} else {
				
				if(folder.getArchivisticStatus().isSemiActive()) {
					return ComponentState.enabledIf(user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_FOLDERS).on(folder));
				}
				
				return ComponentState.ENABLED;
			}
		}
		return ComponentState.INVISIBLE;
	}

	private ComponentState getAddFolderButtonState(User user, Folder folder) {
		if (user.hasAll(RMPermissionsTo.CREATE_SUB_FOLDERS, RMPermissionsTo.CREATE_FOLDERS).on(folder)) {
			
			if (folder.getArchivisticStatus().isInactive()) {
				return ComponentState.enabledIf(user.has(RMPermissionsTo.CREATE_SUB_FOLDERS_IN_INACTIVE_FOLDERS).on(folder));
			} else {
				
				if(folder.getArchivisticStatus().isSemiActive()) {
					return ComponentState.enabledIf(user.has(RMPermissionsTo.CREATE_SUB_FOLDERS_IN_SEMIACTIVE_FOLDERS).on(folder));
				}
				
				return ComponentState.ENABLED;
			}
		}
		return ComponentState.INVISIBLE;
	}

	private ComponentState getAddDocumentButtonState(User user, Folder folder) {
		if (user.has(RMPermissionsTo.CREATE_DOCUMENTS).on(folder)) {
			
			if (folder.getArchivisticStatus().isInactive()) {
				return ComponentState.enabledIf(user.has(RMPermissionsTo.CREATE_INACTIVE_DOCUMENT).on(folder));
			} else {
				
				if(folder.getArchivisticStatus().isSemiActive()) {
					return ComponentState.enabledIf(user.has(RMPermissionsTo.CREATE_SEMIACTIVE_DOCUMENT).on(folder));
				}
				
				return ComponentState.ENABLED;
			}
		}
		return ComponentState.INVISIBLE;
	}

	private MetadataSchema getFoldersSchema() {
		return schema(Folder.DEFAULT_SCHEMA);
	}

	private MetadataSchema getDocumentsSchema() {
		return schema(Document.DEFAULT_SCHEMA);
	}

	public void viewAssembled() {
		MetadataSchema documentsSchema = getDocumentsSchema();
		MetadataSchemaVO documentsSchemaVO = schemaVOBuilder.build(documentsSchema, VIEW_MODE.TABLE);
		documentsDataProvider = new RecordVODataProvider(documentsSchemaVO, voBuilder, modelLayerFactory) {
			@Override
			protected LogicalSearchQuery getQuery() {
				Record record = getRecord(folderVO.getId());
				MetadataSchema documentsSchema = getDocumentsSchema();
				Metadata folderMetadata = documentsSchema.getMetadata(Document.FOLDER);
				LogicalSearchQuery query = new LogicalSearchQuery();
				query.setCondition(from(documentsSchema).where(folderMetadata).is(record));
				return query.sortDesc(Schemas.MODIFIED_ON);
			}
		};
		view.setDocuments(documentsDataProvider);

		MetadataSchemaVO foldersSchemaVO = schemaVOBuilder.build(schema(), VIEW_MODE.TABLE);
		subFoldersDataProvider = new RecordVODataProvider(foldersSchemaVO, voBuilder, modelLayerFactory) {
			@Override
			protected LogicalSearchQuery getQuery() {
				Record record = getRecord(folderVO.getId());
				MetadataSchema foldersSchema = getFoldersSchema();
				Metadata parentFolderMetadata = foldersSchema.getMetadata(Folder.PARENT_FOLDER);
				LogicalSearchQuery query = new LogicalSearchQuery();
				query.setCondition(from(foldersSchema).where(parentFolderMetadata).is(record));
				return query.sortDesc(Schemas.MODIFIED_ON);
			}
		};
		view.setSubFolders(subFoldersDataProvider);

		RMSchemasRecordsServices schemas = new RMSchemasRecordsServices(collection, modelLayerFactory);
		Folder folder = schemas.wrapFolder(toRecord(folderVO));
		disableMenuItems(folder);
		modelLayerFactory.newLoggingServices().logRecordView(folder.getWrappedRecord(), getCurrentUser());
	}

	public void backButtonClicked() {
		String parentId = folderVO.getParentFolder();
		if (parentId != null) {
			view.navigateTo().displayFolder(parentId);
		} else {
			view.navigateTo().recordsManagement();
		}
	}

	public void addDocumentButtonClicked() {
		view.navigateTo().addDocument(folderVO.getId(), null);
	}

	public void addSubFolderButtonClicked() {
		view.navigateTo().addFolder(folderVO.getId(), null);
	}

	public void editFolderButtonClicked() {
		view.navigateTo().editFolder(folderVO.getId());
	}

	public void deleteFolderButtonClicked(String reason) {
		String parentId = folderVO.get(Folder.PARENT_FOLDER);
		Record record = toRecord(folderVO);
		delete(record, reason);
		if (parentId != null) {
			view.navigateTo().displayFolder(parentId);
		} else {
			view.navigateTo().recordsManagement();
		}
	}

	public void duplicateFolderButtonClicked() {
		Folder folder = rmSchemasRecordsServices().getFolder(folderVO.getId());
		Folder duplicatedFolder = decommissioningService().duplicateAndSave(folder);
		view.navigateTo().editFolder(duplicatedFolder.getId());
	}

	public void duplicateStructureButtonClicked() {
		Folder folder = rmSchemasRecordsServices().getFolder(folderVO.getId());
		Folder duplicatedFolder = decommissioningService().duplicateStructureAndSave(folder);
		view.navigateTo().displayFolder(duplicatedFolder.getId());
		view.showMessage("Le dossier et son arborescence ont été dupliqués");
	}

	public void linkToFolderButtonClicked() {
		// TODO ZeroClipboardComponent
		view.showMessage("Clipboard integration TODO!");
	}

	public void addAuthorizationButtonClicked() {
		view.navigateTo().listObjectAuthorizations(folderVO.getId());
	}

	public void printLabelButtonClicked() {
		// TODO Plug reports component
		view.showMessage("Print label window!");
	}

	public void documentClicked(RecordVO documentVO) {
		view.navigateTo().displayDocument(documentVO.getId());
	}

	public void subFolderClicked(RecordVO subFolderVO) {
		view.navigateTo().displayFolder(subFolderVO.getId());
	}

	private DecommissioningService decommissioningService() {
		return new DecommissioningService(getCurrentUser().getCollection(), modelLayerFactory);
	}

	private RMSchemasRecordsServices rmSchemasRecordsServices() {
		return new RMSchemasRecordsServices(getCurrentUser().getCollection(), modelLayerFactory);
	}

	private boolean documentExists(String fileName) {
		Record record = getRecord(folderVO.getId());
		MetadataSchema documentsSchema = getDocumentsSchema();
		Metadata folderMetadata = documentsSchema.getMetadata(Document.FOLDER);
		Metadata titleMetadata = documentsSchema.getMetadata(Schemas.TITLE.getCode());
		LogicalSearchQuery query = new LogicalSearchQuery();

		LogicalSearchCondition parentCondition = from(documentsSchema).where(folderMetadata).is(record);
		query.setCondition(parentCondition.andWhere(titleMetadata).is(fileName));

		SearchServices searchServices = modelLayerFactory.newSearchServices();
		return searchServices.query(query).getNumFound() > 0;
	}

	public void contentVersionUploaded(ContentVersionVO uploadedContentVO) {
		view.selectDocumentsTab();
		String fileName = uploadedContentVO.getFileName();
		if (!documentExists(fileName)) {
			try {
				uploadedContentVO.setMajorVersion(true);
				Record newRecord = documentPresenterUtils.newRecord();
				DocumentVO documentVO = documentVOBuilder.build(newRecord, VIEW_MODE.FORM);
				documentVO.setFolder(folderVO);
				documentVO.setTitle(fileName);
				documentVO.setContent(uploadedContentVO);
				newRecord = documentPresenterUtils.toRecord(documentVO);
				documentPresenterUtils.addOrUpdate(newRecord);
				documentsDataProvider.fireDataRefreshEvent();
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

}
