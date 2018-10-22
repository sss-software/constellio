package com.constellio.app.modules.rm.ui.pages.folder;

import com.constellio.app.api.extensions.params.DocumentFolderBreadCrumbParams;
import com.constellio.app.api.extensions.params.NavigateToFromAPageParams;
import com.constellio.app.api.extensions.params.AvailableActionsParam;
import com.constellio.app.api.extensions.taxonomies.FolderDeletionEvent;
import com.constellio.app.modules.rm.ConstellioRMModule;
import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.modules.rm.RMEmailTemplateConstants;
import com.constellio.app.modules.rm.constants.RMPermissionsTo;
import com.constellio.app.modules.rm.extensions.api.RMModuleExtensions;
import com.constellio.app.modules.rm.model.enums.DefaultTabInFolderDisplay;
import com.constellio.app.modules.rm.model.labelTemplate.LabelTemplate;
import com.constellio.app.modules.rm.navigation.RMNavigationConfiguration;
import com.constellio.app.modules.rm.navigation.RMViews;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.services.borrowingServices.BorrowingServices;
import com.constellio.app.modules.rm.services.borrowingServices.BorrowingType;
import com.constellio.app.modules.rm.services.decommissioning.DecommissioningService;
import com.constellio.app.modules.rm.services.decommissioning.SearchType;
import com.constellio.app.modules.rm.services.events.RMEventsSearchServices;
import com.constellio.app.modules.rm.ui.builders.DocumentToVOBuilder;
import com.constellio.app.modules.rm.ui.builders.FolderToVOBuilder;
import com.constellio.app.modules.rm.ui.components.breadcrumb.FolderDocumentContainerBreadcrumbTrail;
import com.constellio.app.modules.rm.ui.components.content.ConstellioAgentClickHandler;
import com.constellio.app.modules.rm.ui.entities.DocumentVO;
import com.constellio.app.modules.rm.ui.entities.FolderVO;
import com.constellio.app.modules.rm.ui.pages.decommissioning.DecommissioningBuilderViewImpl;
import com.constellio.app.modules.rm.ui.pages.decommissioning.breadcrumb.DecommissionBreadcrumbTrail;
import com.constellio.app.modules.rm.ui.util.ConstellioAgentUtils;
import com.constellio.app.modules.rm.util.DecommissionNavUtil;
import com.constellio.app.modules.rm.util.RMNavigationUtils;
import com.constellio.app.modules.rm.wrappers.Cart;
import com.constellio.app.modules.rm.wrappers.ContainerRecord;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.modules.rm.wrappers.RMTask;
import com.constellio.app.modules.tasks.TasksPermissionsTo;
import com.constellio.app.modules.tasks.model.wrappers.BetaWorkflow;
import com.constellio.app.modules.tasks.model.wrappers.Task;
import com.constellio.app.modules.tasks.navigation.TaskViews;
import com.constellio.app.modules.tasks.services.BetaWorkflowServices;
import com.constellio.app.modules.tasks.services.TasksSchemasRecordsServices;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.services.factories.ConstellioFactories;
import com.constellio.app.ui.application.Navigation;
import com.constellio.app.ui.entities.ContentVersionVO;
import com.constellio.app.ui.entities.ContentVersionVO.InputStreamProvider;
import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.framework.builders.EventToVOBuilder;
import com.constellio.app.ui.framework.builders.MetadataSchemaToVOBuilder;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.framework.components.ComponentState;
import com.constellio.app.ui.framework.components.RMSelectionPanelReportPresenter;
import com.constellio.app.ui.framework.components.breadcrumb.BaseBreadcrumbTrail;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.SchemaPresenterUtils;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.app.ui.pages.base.SingleSchemaBasePresenter;
import com.constellio.app.ui.params.ParamUtils;
import com.constellio.data.utils.TimeProvider;
import com.constellio.model.entities.CorePermissions;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.records.wrappers.EmailToSend;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.entities.structures.EmailAddress;
import com.constellio.model.extensions.ModelLayerCollectionExtensions;
import com.constellio.model.services.configs.SystemConfigurationsManager;
import com.constellio.model.services.contents.ContentFactory;
import com.constellio.model.services.contents.icap.IcapException;
import com.constellio.model.services.migrations.ConstellioEIMConfigs;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.schemas.MetadataSchemasManager;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.search.StatusFilter;
import com.constellio.model.services.search.query.logical.FunctionLogicalSearchQuerySort;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators;
import com.constellio.model.services.search.query.logical.LogicalSearchQuerySort;
import com.constellio.model.services.search.query.logical.condition.LogicalSearchCondition;
import com.constellio.model.services.security.AuthorizationsServices;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.constellio.app.modules.tasks.model.wrappers.Task.STARRED_BY_USERS;
import static com.constellio.app.ui.i18n.i18n.$;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class DisplayFolderPresenter extends SingleSchemaBasePresenter<DisplayFolderView> {

	private static final int WAIT_ONE_SECOND = 1;
	private static Logger LOGGER = LoggerFactory.getLogger(DisplayFolderPresenter.class);
	private RecordVODataProvider documentsDataProvider;
	private RecordVODataProvider tasksDataProvider;
	private RecordVODataProvider subFoldersDataProvider;
	private RecordVODataProvider eventsDataProvider;
	private MetadataSchemaToVOBuilder schemaVOBuilder = new MetadataSchemaToVOBuilder();
	private FolderToVOBuilder folderVOBuilder;
	private DocumentToVOBuilder documentVOBuilder;

	private FolderVO folderVO;

	private transient RMConfigs rmConfigs;
	private transient RMSchemasRecordsServices rmSchemasRecordsServices;
	private transient BorrowingServices borrowingServices;
	private transient MetadataSchemasManager metadataSchemasManager;
	private transient RecordServices recordServices;
	private transient ModelLayerCollectionExtensions extensions;
	private transient RMModuleExtensions rmModuleExtensions;
	private transient ConstellioEIMConfigs eimConfigs;
	private String taxonomyCode;

	Boolean allItemsSelected = false;

	Boolean allItemsDeselected = false;

	private boolean popup;

	private Map<String, String> params = null;

	public DisplayFolderPresenter(DisplayFolderView view, RecordVO recordVO, boolean popup) {
		super(view, Folder.DEFAULT_SCHEMA);
		this.popup = popup;
		initTransientObjects();
		if (recordVO != null) {
			forParams(recordVO.getId());
		}

	}

	private void readObject(java.io.ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		initTransientObjects();
	}

	private void initTransientObjects() {
		rmSchemasRecordsServices = new RMSchemasRecordsServices(collection, appLayerFactory);
		borrowingServices = new BorrowingServices(collection, modelLayerFactory);
		folderVOBuilder = new FolderToVOBuilder();
		documentVOBuilder = new DocumentToVOBuilder(modelLayerFactory);
		metadataSchemasManager = modelLayerFactory.getMetadataSchemasManager();
		recordServices = modelLayerFactory.newRecordServices();
		extensions = modelLayerFactory.getExtensions().forCollection(collection);
		rmModuleExtensions = appLayerFactory.getExtensions().forCollection(collection).forModule(ConstellioRMModule.ID);
		rmConfigs = new RMConfigs(modelLayerFactory.getSystemConfigurationsManager());
		eimConfigs = new ConstellioEIMConfigs(modelLayerFactory.getSystemConfigurationsManager());
	}

	protected void setTaxonomyCode(String taxonomyCode) {
		this.taxonomyCode = taxonomyCode;
	}

	@Override
	protected boolean hasPageAccess(String params, User user) {
		return true;
	}

	public void forParams(String params) {
		String id;

		if(params.contains("id")) {
			this.params = ParamUtils.getParamsMap(params);
			id = this.params.get("id");
		} else {
			id = params;
		}

		String taxonomyCode = view.getUIContext().getAttribute(FolderDocumentContainerBreadcrumbTrail.TAXONOMY_CODE);
		view.setTaxonomyCode(taxonomyCode);

		Record record = getRecord(id);
		this.folderVO = folderVOBuilder.build(record, VIEW_MODE.DISPLAY, view.getSessionContext());
		setSchemaCode(record.getSchemaCode());
		view.setRecord(folderVO);

		MetadataSchema documentsSchema = getDocumentsSchema();
		MetadataSchemaVO documentsSchemaVO = schemaVOBuilder.build(documentsSchema, VIEW_MODE.TABLE, view.getSessionContext());
		documentsDataProvider = new RecordVODataProvider(
				documentsSchemaVO, documentVOBuilder, modelLayerFactory, view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				return getDocumentsQuery();
			}
		};

		MetadataSchemaVO foldersSchemaVO = schemaVOBuilder.build(defaultSchema(), VIEW_MODE.TABLE, view.getSessionContext());
		subFoldersDataProvider = new RecordVODataProvider(
				foldersSchemaVO, folderVOBuilder, modelLayerFactory, view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				return getSubFoldersQuery();
			}
		};

		MetadataSchemaVO tasksSchemaVO = schemaVOBuilder
				.build(getTasksSchema(), VIEW_MODE.TABLE, Arrays.asList(STARRED_BY_USERS), view.getSessionContext(), true);
		tasksDataProvider = new RecordVODataProvider(
				tasksSchemaVO, folderVOBuilder, modelLayerFactory, view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				LogicalSearchQuery query = getTasksQuery();
				addStarredSortToQuery(query);
				query.sortDesc(Schemas.MODIFIED_ON);
				return query;
			}

			@Override
			protected void clearSort(LogicalSearchQuery query) {
				super.clearSort(query);
				addStarredSortToQuery(query);
			}
		};

		view.setFolderContent(Arrays.asList(subFoldersDataProvider, documentsDataProvider));
		view.setTasks(tasksDataProvider);

		eventsDataProvider = getEventsDataProvider();
		view.setEvents(eventsDataProvider);

		computeAllItemsSelected();
	}

	public Map<String, String> getParams() {
		return params;
	}

	public String getFolderId() {
		return folderVO.getId();
	}

	LogicalSearchQuery getDocumentsQuery() {
		Record record = getRecord(folderVO.getId());

		RMSchemasRecordsServices rm = new RMSchemasRecordsServices(collection, appLayerFactory);
		Folder folder = rm.wrapFolder(record);
		List<String> referencedDocuments = new ArrayList<>();
		for (Metadata folderMetadata : folder.getSchema().getMetadatas().onlyReferencesToType(Document.SCHEMA_TYPE)) {
			referencedDocuments.addAll(record.<String>getValues(folderMetadata));
		}

		LogicalSearchCondition condition = from(rm.document.schemaType()).where(rm.document.folder()).is(record);

		if (!referencedDocuments.isEmpty()) {
			condition = condition.orWhere(Schemas.IDENTIFIER).isIn(referencedDocuments);
		}

		LogicalSearchQuery query = new LogicalSearchQuery(condition);
		query.filteredWithUser(getCurrentUser());
		query.filteredByStatus(StatusFilter.ACTIVES);
		query.sortAsc(Schemas.TITLE);
		return query;
	}

	private LogicalSearchQuery getSubFoldersQuery() {
		Record record = getRecord(folderVO.getId());
		MetadataSchemaType foldersSchemaType = getFoldersSchemaType();
		MetadataSchema foldersSchema = getFoldersSchema();
		Metadata parentFolderMetadata = foldersSchema.getMetadata(Folder.PARENT_FOLDER);
		LogicalSearchQuery query = new LogicalSearchQuery();
		query.setCondition(from(foldersSchemaType).where(parentFolderMetadata).is(record));
		query.filteredWithUser(getCurrentUser());
		query.filteredByStatus(StatusFilter.ACTIVES);
		query.sortAsc(Schemas.TITLE);
		return query;
	}

	private LogicalSearchQuery getTasksQuery() {
		TasksSchemasRecordsServices tasks = new TasksSchemasRecordsServices(collection, appLayerFactory);
		Metadata taskFolderMetadata = tasks.userTask.schema().getMetadata(RMTask.LINKED_FOLDERS);
		LogicalSearchQuery query = new LogicalSearchQuery();
		query.setCondition(from(tasks.userTask.schemaType()).where(taskFolderMetadata).is(folderVO.getId()));
		query.filteredByStatus(StatusFilter.ACTIVES);
		query.filteredWithUser(getCurrentUser());
		return query;
	}

	public void selectInitialTabForUser() {
		SystemConfigurationsManager systemConfigurationsManager = modelLayerFactory.getSystemConfigurationsManager();
		RMConfigs rmConfigs = new RMConfigs(systemConfigurationsManager);

		String userDefaultTabInFolderDisplayCode = getCurrentUser().getDefaultTabInFolderDisplay();
		String configDefaultTabInFolderDisplayCode = rmConfigs.getDefaultTabInFolderDisplay();
		String defaultTabInFolderDisplayCode = StringUtils.isNotBlank(userDefaultTabInFolderDisplayCode) ?
											   userDefaultTabInFolderDisplayCode :
											   configDefaultTabInFolderDisplayCode;
		if (isNotBlank(defaultTabInFolderDisplayCode)) {
			if (DefaultTabInFolderDisplay.METADATA.getCode().equals(defaultTabInFolderDisplayCode)) {
				view.selectMetadataTab();
			} else if (DefaultTabInFolderDisplay.CONTENT.getCode().equals(defaultTabInFolderDisplayCode)) {
				view.selectFolderContentTab();
			}
		}
	}

	public BaseBreadcrumbTrail getBreadCrumbTrail() {
		String saveSearchDecommissioningId = null;
		String searchTypeAsString = null;

		Map<String, String> params = getParams();
		if (params != null && params.get("decommissioningSearchId") != null) {
			saveSearchDecommissioningId = params.get("decommissioningSearchId");
			view.getUIContext()
					.setAttribute(DecommissioningBuilderViewImpl.SAVE_SEARCH_DECOMMISSIONING, saveSearchDecommissioningId);
		}

		if (params != null && params.get("decommissioningType") != null) {
			searchTypeAsString = params.get("decommissioningType");
			view.getUIContext().setAttribute(DecommissioningBuilderViewImpl.DECOMMISSIONING_BUILDER_TYPE, searchTypeAsString);
		}

		SearchType searchType = null;
		if (searchTypeAsString != null) {
			searchType = SearchType.valueOf((searchTypeAsString));
		}
		BaseBreadcrumbTrail breadcrumbTrail;

		RMModuleExtensions rmModuleExtensions = view.getConstellioFactories().getAppLayerFactory().getExtensions()
				.forCollection(view.getCollection()).forModule(ConstellioRMModule.ID);
		breadcrumbTrail = rmModuleExtensions
				.getBreadCrumbtrail(new DocumentFolderBreadCrumbParams(getFolderId(), params, view));

		if (breadcrumbTrail != null) {
			return breadcrumbTrail;
		} else if (saveSearchDecommissioningId == null) {
			String containerId = null;
			if (params != null && params instanceof Map) {
				containerId = params.get("containerId");
			}
			return new FolderDocumentContainerBreadcrumbTrail(view.getRecord().getId(), taxonomyCode, containerId, this.view);
		} else {
			return new DecommissionBreadcrumbTrail($("DecommissioningBuilderView.viewTitle." + searchType.name()),
					searchType, saveSearchDecommissioningId, view.getRecord().getId(), this.view);
		}
	}

	public int getFolderContentCount() {
		return subFoldersDataProvider.size() + documentsDataProvider.size();
	}

	public int getTaskCount() {
		return tasksDataProvider.size();
	}

	public RecordVODataProvider getWorkflows() {
		MetadataSchemaVO schemaVO = new MetadataSchemaToVOBuilder().build(
				schema(BetaWorkflow.DEFAULT_SCHEMA), VIEW_MODE.TABLE, view.getSessionContext());

		return new RecordVODataProvider(schemaVO, new RecordToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				return new BetaWorkflowServices(view.getCollection(), appLayerFactory).getWorkflowsQuery();
			}
		};
	}

	public void workflowStartRequested(RecordVO record) {
		Map<String, List<String>> parameters = new HashMap<>();
		parameters.put(RMTask.LINKED_FOLDERS, asList(folderVO.getId()));
		BetaWorkflow workflow = new TasksSchemasRecordsServices(view.getCollection(), appLayerFactory)
				.getBetaWorkflow(record.getId());
		new BetaWorkflowServices(view.getCollection(), appLayerFactory).start(workflow, getCurrentUser(), parameters);
	}

	@Override
	protected boolean hasRestrictedRecordAccess(String params, User user, Record restrictedRecord) {
		return user.hasReadAccess().on(restrictedRecord);
	}

	@Override
	protected List<String> getRestrictedRecordIds(String params) {
		return asList(folderVO.getId());
	}

	private void disableMenuItems(Folder folder) {
		if (!folder.isLogicallyDeletedStatus()) {
			RMConfigs rmConfigs = new RMConfigs(modelLayerFactory.getSystemConfigurationsManager());

			User user = getCurrentUser();
			view.setLogicallyDeletable(getDeleteButtonState(user, folder));
			view.setEditButtonState(getEditButtonState(user, folder));
			view.setMoveInFolderState(getMoveInFolderButtonState(user, folder));
			view.setAddSubFolderButtonState(getAddFolderButtonState(user, folder));
			view.setAddDocumentButtonState(getAddDocumentButtonState(user, folder));
			view.setDuplicateFolderButtonState(getDuplicateFolderButtonState(user, folder));
			view.setAuthorizationButtonState(getAuthorizationButtonState(user, folder));
			view.setShareFolderButtonState(getShareButtonState(user, folder));
			view.setPrintButtonState(getPrintButtonState(user, folder));
			view.setBorrowButtonState(getBorrowButtonState(user, folder));
			view.setReturnFolderButtonState(getReturnFolderButtonState(user, folder));
			view.setReminderReturnFolderButtonState(getReminderReturnFolderButtonState(user, folder));
			view.setAlertWhenAvailableButtonState(getAlertWhenAvailableButtonState(user, folder));
			view.setBorrowedMessage(getBorrowMessageState(folder));
			view.setStartWorkflowButtonState(ComponentState.visibleIf(rmConfigs.areWorkflowsEnabled()));
		}
	}

	String getBorrowMessageState(Folder folder) {
		String borrowedMessage = null;
		if (folder.getBorrowed() != null && folder.getBorrowed()) {
			String borrowUserEntered = folder.getBorrowUserEntered();
			if (borrowUserEntered != null) {
				String userTitle = rmSchemasRecordsServices.getUser(borrowUserEntered).getTitle();
				LocalDateTime borrowDateTime = folder.getBorrowDate();
				LocalDate borrowDate = borrowDateTime != null ? borrowDateTime.toLocalDate() : null;
				borrowedMessage = $("DisplayFolderView.borrowedFolder", userTitle, borrowDate);
			} else {
				borrowedMessage = $("DisplayFolderView.borrowedByNullUserFolder");
			}
		} else if (folder.getContainer() != null) {
			try {
				ContainerRecord containerRecord = rmSchemasRecordsServices.getContainerRecord(folder.getContainer());
				boolean borrowed = Boolean.TRUE.equals(containerRecord.getBorrowed());
				String borrower = containerRecord.getBorrower();
				if (borrowed && borrower != null) {
					String userTitle = rmSchemasRecordsServices.getUser(borrower).getTitle();
					LocalDate borrowDate = containerRecord.getBorrowDate();
					borrowedMessage = $("DisplayFolderView.borrowedContainer", userTitle, borrowDate);
				} else if (borrowed) {
					borrowedMessage = $("DisplayFolderView.borrowedByNullUserContainer");
				}
			} catch (Exception e) {
				LOGGER.error("Could not find linked container");
			}
		}
		return borrowedMessage;
	}

	protected ComponentState getBorrowButtonState(User user, Folder folder) {
		try {
			borrowingServices.validateCanBorrow(user, folder, null);
			return ComponentState
					.visibleIf(user.hasAll(RMPermissionsTo.BORROW_FOLDER, RMPermissionsTo.BORROWING_FOLDER_DIRECTLY).on(folder)
							   && rmModuleExtensions.isBorrowingActionPossibleOnFolder(folder, user));
		} catch (Exception e) {
			return ComponentState.INVISIBLE;
		}
	}

	private ComponentState getReturnFolderButtonState(User user, Folder folder) {
		try {
			borrowingServices.validateCanReturnFolder(user, folder);
			return ComponentState
					.visibleIf(user.hasAll(RMPermissionsTo.BORROW_FOLDER, RMPermissionsTo.BORROWING_FOLDER_DIRECTLY).on(folder));
		} catch (Exception e) {
			return ComponentState.INVISIBLE;
		}
	}

	protected ComponentState getReminderReturnFolderButtonState(User user, Folder folder) {
		return isBorrowedByOtherUser(user, folder);
	}

	protected ComponentState getAlertWhenAvailableButtonState(User user, Folder folder) {
		return isBorrowedByOtherUser(user, folder);
	}

	private ComponentState isBorrowedByOtherUser(User currentUser, Folder folder) {
		Boolean borrowed = folder.getBorrowed();
		if ((borrowed != null && borrowed) && borrowed && !isCurrentUserBorrower(currentUser, folder)) {
			return ComponentState.ENABLED;
		} else {
			return ComponentState.INVISIBLE;
		}
	}

	private boolean isCurrentUserBorrower(User currentUser, Folder folder) {
		return currentUser.getId().equals(folder.getBorrowUserEntered());
	}

	ComponentState getPrintButtonState(User user, Folder folder) {
		AuthorizationsServices authorizationsServices = modelLayerFactory.newAuthorizationsServices();
		if (authorizationsServices.canRead(user, folder.getWrappedRecord())) {
			if (folder.getPermissionStatus().isInactive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return ComponentState.visibleIf(user.has(RMPermissionsTo.MODIFY_INACTIVE_BORROWED_FOLDER).on(folder) && user
							.has(RMPermissionsTo.MODIFY_INACTIVE_FOLDERS).on(folder));
				}
				return ComponentState.visibleIf(user.has(RMPermissionsTo.MODIFY_INACTIVE_FOLDERS).on(folder));
			}
			if (folder.getPermissionStatus().isSemiActive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return ComponentState.visibleIf(user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_BORROWED_FOLDER).on(folder) && user
							.has(RMPermissionsTo.MODIFY_SEMIACTIVE_FOLDERS).on(folder));
				}
				return ComponentState.visibleIf(user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_FOLDERS).on(folder));
			}
			//			if(rmModuleExtensions.getReportBuilderFactories().labelsBuilderFactory.getValue() == null) {
			//				return ComponentState.DISABLED;
			//			}
			return ComponentState.ENABLED;
		}
		return ComponentState.INVISIBLE;
	}

	private ComponentState getDuplicateFolderButtonState(User user, Folder folder) {
		return ComponentState.visibleIf(isDuplicateFolderPossible(user, folder));
	}

	private boolean isDuplicateFolderPossible(User user, Folder folder) {
		AuthorizationsServices authorizationsServices = modelLayerFactory.newAuthorizationsServices();
		if (!authorizationsServices.canWrite(user, folder.getWrappedRecord())) {
			return false;
		}

		if (folder.getPermissionStatus().isInactive() && !user.has(RMPermissionsTo.DUPLICATE_INACTIVE_FOLDER).on(folder)) {
			return false;
		}
		if (folder.getPermissionStatus().isSemiActive() && !user.has(RMPermissionsTo.DUPLICATE_SEMIACTIVE_FOLDER).on(folder)) {
			return false;
		}

		if (!rmModuleExtensions.isCopyActionPossibleOnFolder(folder, user)) {
			return false;
		}
		return true;
	}

	private ComponentState getAuthorizationButtonState(User user, Folder folder) {
		return ComponentState.visibleIf(user.has(RMPermissionsTo.MANAGE_FOLDER_AUTHORIZATIONS).on(folder));
	}

	ComponentState getShareButtonState(User user, Folder folder) {
		return ComponentState.visibleIf(isShareFolderPossible(user, folder));
	}

	private boolean isShareFolderPossible(User user, Folder folder) {
		if (!user.has(RMPermissionsTo.SHARE_FOLDER).on(folder)) {
			return false;
		}
		if (folder.getPermissionStatus().isInactive() && !user.has(RMPermissionsTo.SHARE_A_INACTIVE_FOLDER).on(folder)) {
			return false;
		}
		if (folder.getPermissionStatus().isSemiActive() && !user.has(RMPermissionsTo.SHARE_A_SEMIACTIVE_FOLDER).on(folder)) {
			return false;
		}
		if (isNotBlank(folder.getLegacyId()) && !user.has(RMPermissionsTo.SHARE_A_IMPORTED_FOLDER).on(folder)) {
			return false;
		}

		if (!rmModuleExtensions.isShareActionPossibleOnFolder(folder, user)) {
			return false;
		}
		return true;
	}

	ComponentState getDeleteButtonState(User user, Folder folder) {
		if (user.hasDeleteAccess().on(folder) && !extensions.isDeleteBlocked(folder.getWrappedRecord(), user)) {
			if (folder.getPermissionStatus().isInactive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return ComponentState.visibleIf(user.has(RMPermissionsTo.MODIFY_INACTIVE_BORROWED_FOLDER).on(folder) && user
							.has(RMPermissionsTo.DELETE_INACTIVE_FOLDERS).on(folder));
				}
				return ComponentState.visibleIf(user.has(RMPermissionsTo.DELETE_INACTIVE_FOLDERS).on(folder));
			}
			if (folder.getPermissionStatus().isSemiActive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return ComponentState.visibleIf(user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_BORROWED_FOLDER).on(folder) && user
							.has(RMPermissionsTo.DELETE_SEMIACTIVE_FOLDERS).on(folder));
				}
				return ComponentState.visibleIf(user.has(RMPermissionsTo.DELETE_SEMIACTIVE_FOLDERS).on(folder));
			}
			return ComponentState.ENABLED;
		}
		return ComponentState.INVISIBLE;
	}

	ComponentState getMoveInFolderButtonState(User user, Folder folder) {
		//		return getEditButtonState(user, folder);
		return ComponentState.INVISIBLE;
	}

	ComponentState getEditButtonState(User user, Folder folder) {
		if (isNotBlank(folder.getLegacyId()) && !user.has(RMPermissionsTo.MODIFY_IMPORTED_FOLDERS).on(folder)) {
			return ComponentState.INVISIBLE;
		}
		return ComponentState.visibleIf(user.hasWriteAccess().on(folder)
										&& !extensions.isModifyBlocked(folder.getWrappedRecord(), user) && extensions
												.isRecordModifiableBy(folder.getWrappedRecord(), user));

	}

	ComponentState getAddFolderButtonState(User user, Folder folder) {
		if (user.hasWriteAccess().on(folder) &&
			user.hasAll(RMPermissionsTo.CREATE_SUB_FOLDERS, RMPermissionsTo.CREATE_FOLDERS).on(folder)) {
			if (folder.getPermissionStatus().isInactive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return ComponentState.visibleIf(user.has(RMPermissionsTo.MODIFY_INACTIVE_BORROWED_FOLDER).on(folder) && user
							.has(RMPermissionsTo.CREATE_SUB_FOLDERS_IN_INACTIVE_FOLDERS).on(folder));
				}
				return ComponentState.visibleIf(user.has(RMPermissionsTo.CREATE_SUB_FOLDERS_IN_INACTIVE_FOLDERS).on(folder));
			}
			if (folder.getPermissionStatus().isSemiActive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return ComponentState.visibleIf(user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_BORROWED_FOLDER).on(folder) && user
							.has(RMPermissionsTo.CREATE_SUB_FOLDERS_IN_SEMIACTIVE_FOLDERS).on(folder));
				}
				return ComponentState.visibleIf(user.has(RMPermissionsTo.CREATE_SUB_FOLDERS_IN_SEMIACTIVE_FOLDERS).on(folder));
			}
			return ComponentState.ENABLED;
		}
		return ComponentState.INVISIBLE;
	}

	ComponentState getAddDocumentButtonState(User user, Folder folder) {
		if (user.hasWriteAccess().on(folder) &&
			user.has(RMPermissionsTo.CREATE_DOCUMENTS).on(folder)) {
			if (folder.getPermissionStatus().isInactive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return ComponentState.visibleIf(user.has(RMPermissionsTo.MODIFY_INACTIVE_BORROWED_FOLDER).on(folder) && user
							.has(RMPermissionsTo.CREATE_INACTIVE_DOCUMENT).on(folder));
				}
				return ComponentState.visibleIf(user.has(RMPermissionsTo.CREATE_INACTIVE_DOCUMENT).on(folder));
			}
			if (folder.getPermissionStatus().isSemiActive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return ComponentState.visibleIf(user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_BORROWED_FOLDER).on(folder) && user
							.has(RMPermissionsTo.CREATE_SEMIACTIVE_DOCUMENT).on(folder));
				}
				return ComponentState.visibleIf(user.has(RMPermissionsTo.CREATE_SEMIACTIVE_DOCUMENT).on(folder));
			}
			return ComponentState.ENABLED;
		}
		return ComponentState.INVISIBLE;
	}

	private MetadataSchemaType getFoldersSchemaType() {
		return schemaType(Folder.SCHEMA_TYPE);
	}

	private MetadataSchemaType getDocumentsSchemaType() {
		return schemaType(Document.SCHEMA_TYPE);
	}

	private MetadataSchema getFoldersSchema() {
		return schema(Folder.DEFAULT_SCHEMA);
	}

	private MetadataSchema getDocumentsSchema() {
		return schema(Document.DEFAULT_SCHEMA);
	}

	private MetadataSchema getTasksSchema() {
		return schema(Task.DEFAULT_SCHEMA);
	}

	public void viewAssembled() {
		view.setFolderContent(Arrays.asList(subFoldersDataProvider, documentsDataProvider));
		view.setTasks(tasksDataProvider);
		view.setEvents(eventsDataProvider);

		RMSchemasRecordsServices schemas = new RMSchemasRecordsServices(collection, appLayerFactory);
		Folder folder = schemas.wrapFolder(toRecord(folderVO));
		disableMenuItems(folder);
		modelLayerFactory.newLoggingServices().logRecordView(folder.getWrappedRecord(), getCurrentUser());
	}

	public void updateTaskStarred(boolean isStarred, String taskId, RecordVODataProvider dataProvider) {
		TasksSchemasRecordsServices taskSchemas = new TasksSchemasRecordsServices(collection, appLayerFactory);
		Task task = taskSchemas.getTask(taskId);
		if (isStarred) {
			task.addStarredBy(getCurrentUser().getId());
		} else {
			task.removeStarredBy(getCurrentUser().getId());
		}
		try {
			recordServices().update(task);
		} catch (RecordServicesException e) {
			e.printStackTrace();
		}
		dataProvider.fireDataRefreshEvent();
	}

	private Navigation navigate() {
		return view.navigate();
	}

	public void backButtonClicked() {
		navigate().to().previousView();
	}

	public void addDocumentButtonClicked() {
		navigate().to(RMViews.class).addDocument(folderVO.getId());
	}

	public void addSubFolderButtonClicked() {
		navigate().to(RMViews.class).addFolder(folderVO.getId());
	}

	public void editFolderButtonClicked() {
		RMNavigationUtils.navigateToEditFolder(folderVO.getId(), params, appLayerFactory, collection);
	}

	public void deleteFolderButtonClicked(String reason) {
		String parentId = folderVO.get(Folder.PARENT_FOLDER);
		Record record = toRecord(folderVO);

		if (recordServices.isLogicallyDeletable(record, getCurrentUser())) {
			appLayerFactory.getExtensions().forCollection(collection)
					.notifyFolderDeletion(new FolderDeletionEvent(rmSchemasRecordsServices.wrapFolder(record)));
			delete(record, reason, false, WAIT_ONE_SECOND);
			if (parentId != null) {
				navigateToFolder(parentId);
			} else {
				navigate().to().home();
			}
		} else {
			view.showErrorMessage($("ListSchemaRecordsView.cannotDelete"));
		}
	}

	public void duplicateFolderButtonClicked() {
		Folder folder = rmSchemasRecordsServices().getFolder(folderVO.getId());
		if (isDuplicateFolderPossible(getCurrentUser(), folder)) {
			navigateToDuplicateFolder(folder, false);
		}
		if (!popup) {
			view.closeAllWindows();
		}
	}

	private void navigateToDuplicateFolder(Folder folder, boolean isStructure) {
		boolean areTypeAndSearchIdPresent = DecommissionNavUtil.areTypeAndSearchIdPresent(params);

		if (areTypeAndSearchIdPresent) {
			navigate().to(RMViews.class).duplicateFolderFromDecommission(folderVO.getId(), isStructure,
					DecommissionNavUtil.getSearchId(params), DecommissionNavUtil.getSearchType(params));
		} else if (rmModuleExtensions
				.navigateToDuplicateFolderWhileKeepingTraceOfPreviousView(new NavigateToFromAPageParams(params, isStructure, folderVO.getId()))) {
		} else {
			navigate().to(RMViews.class).duplicateFolder(folder.getId(), isStructure);
		}
	}

	public void duplicateStructureButtonClicked() {
		Folder folder = rmSchemasRecordsServices().getFolder(folderVO.getId());
		if (isDuplicateFolderPossible(getCurrentUser(), folder)) {
			try {
				decommissioningService().validateDuplicateStructure(folder, getCurrentUser(), false);
				navigateToDuplicateFolder(folder, true);
			} catch (RecordServicesException.ValidationException e) {
				view.showErrorMessage($(e.getErrors()));
			} catch (Exception e) {
				view.showErrorMessage(e.getMessage());
			}
		}
		if (!popup) {
			view.closeAllWindows();
		}
	}

	public void linkToFolderButtonClicked() {
		// TODO ZeroClipboardComponent
		view.showMessage("Clipboard integration TODO!");
	}

	public void addAuthorizationButtonClicked() {
		navigate().to().listObjectAccessAndRoleAuthorizations(folderVO.getId());
	}

	public void shareFolderButtonClicked() {
		Folder folder = rmSchemasRecordsServices().getFolder(folderVO.getId());
		if (!isShareFolderPossible(getCurrentUser(), folder)) {
			return;
		}

		navigate().to().shareContent(folderVO.getId());
	}

	public void editDocumentButtonClicked(RecordVO recordVO) {
		RMNavigationUtils.navigateToEditDocument(recordVO.getId(), params, appLayerFactory, collection);

	}

	public void downloadDocumentButtonClicked(RecordVO recordVO) {
		ContentVersionVO contentVersionVO = recordVO.get(Document.CONTENT);
		view.downloadContentVersion(recordVO, contentVersionVO);
	}

	public void displayDocumentButtonClicked(RecordVO record) {
		navigateToDocument(record);
	}

	public void documentClicked(RecordVO recordVO) {
		ContentVersionVO contentVersionVO = recordVO.get(Document.CONTENT);
		if (contentVersionVO == null) {
			navigateToDocument(recordVO);
			return;
		}
		String agentURL = ConstellioAgentUtils.getAgentURL(recordVO, contentVersionVO);
		if (agentURL != null) {
			//			view.openAgentURL(agentURL);
			new ConstellioAgentClickHandler().handleClick(agentURL, recordVO, contentVersionVO, params);
		} else {
			navigateToDocument(recordVO);
		}
	}

	private void navigateToDocument(RecordVO recordVO) {
		RMNavigationUtils.navigateToDisplayDocument(recordVO.getId(), params, appLayerFactory,
				collection);
	}

	public void navigateToFolder(String folderId) {
		RMNavigationUtils.navigateToDisplayFolder(folderId, params, appLayerFactory, collection);
	}

	public void taskClicked(RecordVO taskVO) {
		navigate().to(TaskViews.class).displayTask(taskVO.getId());
	}

	private DecommissioningService decommissioningService() {
		return new DecommissioningService(getCurrentUser().getCollection(), appLayerFactory);
	}

	private RMSchemasRecordsServices rmSchemasRecordsServices() {
		return new RMSchemasRecordsServices(getCurrentUser().getCollection(), appLayerFactory);
	}

	private boolean documentExists(String fileName) {
		Record record = getRecord(folderVO.getId());

		MetadataSchemaType documentsSchemaType = getDocumentsSchemaType();
		MetadataSchema documentsSchema = getDocumentsSchema();
		Metadata folderMetadata = documentsSchema.getMetadata(Document.FOLDER);
		Metadata titleMetadata = documentsSchema.getMetadata(Schemas.TITLE.getCode());
		LogicalSearchQuery query = new LogicalSearchQuery();
		LogicalSearchCondition parentCondition = from(documentsSchemaType).where(folderMetadata).is(record)
				.andWhere(Schemas.LOGICALLY_DELETED_STATUS).isFalseOrNull();
		query.setCondition(parentCondition.andWhere(titleMetadata).is(fileName));

		SearchServices searchServices = modelLayerFactory.newSearchServices();
		return searchServices.query(query).getNumFound() > 0;
	}

	private Record currentFolder() {
		return recordServices.getDocumentById(folderVO.getId());
	}

	public void contentVersionUploaded(ContentVersionVO uploadedContentVO) {
		view.selectFolderContentTab();
		String fileName = uploadedContentVO.getFileName();
		if (!documentExists(fileName) && !extensions.isModifyBlocked(currentFolder(), getCurrentUser())) {
			try {
				if (Boolean.TRUE.equals(uploadedContentVO.hasFoundDuplicate())) {
					RMSchemasRecordsServices rm = new RMSchemasRecordsServices(collection, appLayerFactory);
					LogicalSearchQuery duplicateDocumentsQuery = new LogicalSearchQuery()
							.setCondition(LogicalSearchQueryOperators.from(rm.documentSchemaType())
									.where(rm.document.content()).is(ContentFactory.isHash(uploadedContentVO.getDuplicatedHash()))
									.andWhere(Schemas.LOGICALLY_DELETED_STATUS).isFalseOrNull()
							)
							.filteredWithUser(getCurrentUser());
					List<Document> duplicateDocuments = rm.searchDocuments(duplicateDocumentsQuery);
					if (duplicateDocuments.size() > 0) {
						StringBuilder message = new StringBuilder(
								$("ContentManager.hasFoundDuplicateWithConfirmation", StringUtils.defaultIfBlank(fileName, "")));
						message.append("<br>");
						for (Document document : duplicateDocuments) {
							message.append("<br>-");
							message.append(document.getTitle());
							message.append(": ");
							message.append(generateDisplayLink(document));
						}
						view.showClickableMessage(message.toString());
					}
				}
				uploadedContentVO.setMajorVersion(true);
				Record newRecord;
				if (rmSchemasRecordsServices().isEmail(fileName)) {
					InputStreamProvider inputStreamProvider = uploadedContentVO.getInputStreamProvider();
					InputStream in = inputStreamProvider.getInputStream(DisplayFolderPresenter.class + ".contentVersionUploaded");
					Document document = rmSchemasRecordsServices.newEmail(fileName, in);
					newRecord = document.getWrappedRecord();
				} else {
					Document document = rmSchemasRecordsServices.newDocument();
					newRecord = document.getWrappedRecord();
				}
				DocumentVO documentVO = documentVOBuilder.build(newRecord, VIEW_MODE.FORM, view.getSessionContext());
				documentVO.setFolder(folderVO);
				documentVO.setTitle(fileName);
				documentVO.setContent(uploadedContentVO);

				String schemaCode = newRecord.getSchemaCode();
				ConstellioFactories constellioFactories = view.getConstellioFactories();
				SessionContext sessionContext = view.getSessionContext();
				SchemaPresenterUtils documentPresenterUtils = new SchemaPresenterUtils(schemaCode, constellioFactories,
						sessionContext);
				newRecord = documentPresenterUtils.toRecord(documentVO);

				documentPresenterUtils.addOrUpdate(newRecord);
				documentsDataProvider.fireDataRefreshEvent();
				view.refreshFolderContentTab();
				//				view.selectFolderContentTab();
			} catch (final IcapException e) {
				view.showErrorMessage(e.getMessage());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				view.clearUploadField();
			}
		}
	}

	public boolean borrowFolder(LocalDate borrowingDate, LocalDate previewReturnDate, String userId,
								BorrowingType borrowingType,
								LocalDate returnDate) {
		boolean borrowed;
		String errorMessage = borrowingServices
				.validateBorrowingInfos(userId, borrowingDate, previewReturnDate, borrowingType, returnDate);
		if (errorMessage != null) {
			view.showErrorMessage($(errorMessage));
			borrowed = false;
		} else {
			Record record = recordServices().getDocumentById(userId);
			User borrowerEntered = wrapUser(record);
			try {
				borrowingServices
						.borrowFolder(folderVO.getId(), borrowingDate, previewReturnDate, getCurrentUser(), borrowerEntered,
								borrowingType, true);
				navigateToFolder(folderVO.getId());
				borrowed = true;
			} catch (RecordServicesException e) {
				LOGGER.error(e.getMessage(), e);
				view.showErrorMessage($("DisplayFolderView.cannotBorrowFolder"));
				borrowed = false;
			}
		}
		if (returnDate != null) {
			return returnFolder(returnDate, borrowingDate);
		}
		return borrowed;
	}

	public boolean returnFolder(LocalDate returnDate) {
		LocalDateTime borrowDateTime = folderVO.getBorrowDate();
		LocalDate borrowDate = borrowDateTime != null ? borrowDateTime.toLocalDate() : null;
		return returnFolder(returnDate, borrowDate);
	}

	protected boolean returnFolder(LocalDate returnDate, LocalDate borrowingDate) {
		String errorMessage = borrowingServices.validateReturnDate(returnDate, borrowingDate);
		if (errorMessage != null) {
			view.showErrorMessage($(errorMessage));
			return false;
		}
		try {
			borrowingServices.returnFolder(folderVO.getId(), getCurrentUser(), returnDate, true);
			navigateToFolder(folderVO.getId());
			return true;
		} catch (RecordServicesException e) {
			view.showErrorMessage($("DisplayFolderView.cannotReturnFolder"));
			return false;
		}
	}

	private EmailToSend newEmailToSend() {
		MetadataSchemaTypes types = metadataSchemasManager.getSchemaTypes(getCurrentUser().getCollection());
		MetadataSchema schema = types.getSchemaType(EmailToSend.SCHEMA_TYPE).getDefaultSchema();
		Record emailToSendRecord = recordServices.newRecordWithSchema(schema);
		return new EmailToSend(emailToSendRecord, types);
	}

	public void reminderReturnFolder() {

		try {
			EmailToSend emailToSend = newEmailToSend();
			String constellioUrl = eimConfigs.getConstellioUrl();
			User borrower = null;
			if (folderVO.getBorrowUserEnteredId() != null) {
				borrower = rmSchemasRecordsServices.getUser(folderVO.getBorrowUserEnteredId());
			} else {
				borrower = rmSchemasRecordsServices.getUser(folderVO.getBorrowUserId());
			}
			EmailAddress borrowerAddress = new EmailAddress(borrower.getTitle(), borrower.getEmail());
			emailToSend.setTo(Arrays.asList(borrowerAddress));
			emailToSend.setSendOn(TimeProvider.getLocalDateTime());
			emailToSend.setSubject($("DisplayFolderView.returnFolderReminder") + folderVO.getTitle());
			emailToSend.setTemplate(RMEmailTemplateConstants.REMIND_BORROW_TEMPLATE_ID);
			List<String> parameters = new ArrayList<>();
			String previewReturnDate = folderVO.getPreviewReturnDate().toString();
			parameters.add("previewReturnDate" + EmailToSend.PARAMETER_SEPARATOR + previewReturnDate);
			parameters.add("borrower" + EmailToSend.PARAMETER_SEPARATOR + borrower.getUsername());
			String borrowedFolderTitle = folderVO.getTitle();
			parameters.add("borrowedFolderTitle" + EmailToSend.PARAMETER_SEPARATOR + borrowedFolderTitle);
			parameters.add("title" + EmailToSend.PARAMETER_SEPARATOR + $("DisplayFolderView.returnFolderReminder") + " \""
						   + folderVO.getTitle() + "\"");
			parameters.add("constellioURL" + EmailToSend.PARAMETER_SEPARATOR + constellioUrl);
			parameters.add("recordURL" + EmailToSend.PARAMETER_SEPARATOR + constellioUrl + "#!"
						   + RMNavigationConfiguration.DISPLAY_FOLDER + "/" + folderVO.getId());
			emailToSend.setParameters(parameters);

			recordServices.add(emailToSend);
			view.showMessage($("DisplayFolderView.reminderEmailSent"));
		} catch (RecordServicesException e) {
			LOGGER.error("DisplayFolderView.cannotSendEmail", e);
			view.showMessage($("DisplayFolderView.cannotSendEmail"));
		}
	}

	public void alertWhenAvailable() {
		try {
			RMSchemasRecordsServices schemas = new RMSchemasRecordsServices(view.getCollection(), appLayerFactory);
			Folder folder = schemas.getFolder(folderVO.getId());
			List<String> usersToAlert = folder.getAlertUsersWhenAvailable();
			String currentUserId = getCurrentUser().getId();
			if (!currentUserId.equals(folder.getBorrowUser()) && !currentUserId.equals(folder.getBorrowUserEntered())) {
				List<String> newUsersToAlert = new ArrayList<>();
				newUsersToAlert.addAll(usersToAlert);
				if (!newUsersToAlert.contains(currentUserId)) {
					newUsersToAlert.add(currentUserId);
					folder.setAlertUsersWhenAvailable(newUsersToAlert);
					addOrUpdate(folder.getWrappedRecord());
				}
			}
			view.showMessage($("RMObject.createAlert"));
		} catch (Exception e) {
			LOGGER.error("RMObject.cannotCreateAlert", e);
			view.showErrorMessage($("RMObject.cannotCreateAlert"));
		}
	}

	public List<LabelTemplate> getCustomTemplates() {
		return appLayerFactory.getLabelTemplateManager().listExtensionTemplates(Folder.SCHEMA_TYPE);
	}

	public List<LabelTemplate> getDefaultTemplates() {
		return appLayerFactory.getLabelTemplateManager().listTemplates(Folder.SCHEMA_TYPE);
	}

	public Date getPreviewReturnDate(Date borrowDate, Object borrowingTypeValue) {
		BorrowingType borrowingType;
		Date previewReturnDate = TimeProvider.getLocalDate().toDate();
		if (borrowDate != null && borrowingTypeValue != null) {
			borrowingType = (BorrowingType) borrowingTypeValue;
			if (borrowingType == BorrowingType.BORROW) {
				int addDays = rmConfigs.getBorrowingDurationDays();
				previewReturnDate = LocalDate.fromDateFields(borrowDate).plusDays(addDays).toDate();
			} else {
				previewReturnDate = borrowDate;
			}
		}
		return previewReturnDate;
	}

	boolean isDocument(RecordVO record) {
		return record.getSchema().getCode().startsWith("document");
	}

	public boolean canModifyDocument(RecordVO record) {
		boolean hasContent = record.get(Document.CONTENT) != null;
		boolean hasAccess = getCurrentUser().hasWriteAccess().on(getRecord(record.getId()));
		return hasContent && hasAccess;
	}

	public void addToCartRequested(RecordVO recordVO) {
		Cart cart = rmSchemasRecordsServices.getCart(recordVO.getId()).addFolders(Arrays.asList(folderVO.getId()));
		addOrUpdate(cart.getWrappedRecord());
		view.showMessage($("DisplayFolderView.addedToCart"));
	}

	public RecordVODataProvider getOwnedCartsDataProvider() {
		final MetadataSchemaVO cartSchemaVO = schemaVOBuilder
				.build(rmSchemasRecordsServices.cartSchema(), VIEW_MODE.TABLE, view.getSessionContext());
		return new RecordVODataProvider(cartSchemaVO, new RecordToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				return new LogicalSearchQuery(
						from(rmSchemasRecordsServices.cartSchema()).where(rmSchemasRecordsServices.cartOwner())
								.isEqualTo(getCurrentUser().getId())).sortAsc(Schemas.TITLE);
			}
		};
	}

	public RecordVODataProvider getSharedCartsDataProvider() {
		final MetadataSchemaVO cartSchemaVO = schemaVOBuilder
				.build(rmSchemasRecordsServices.cartSchema(), VIEW_MODE.TABLE, view.getSessionContext());
		return new RecordVODataProvider(cartSchemaVO, new RecordToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				return new LogicalSearchQuery(
						from(rmSchemasRecordsServices.cartSchema()).where(rmSchemasRecordsServices.cartSharedWithUsers())
								.isContaining(asList(getCurrentUser().getId()))).sortAsc(Schemas.TITLE);
			}
		};
	}

	public void parentFolderButtonClicked(String parentId)
			throws RecordServicesException {
		RMSchemasRecordsServices rmSchemas = new RMSchemasRecordsServices(collection, appLayerFactory);

		String currentFolderId = folderVO.getId();
		if (isNotBlank(parentId)) {
			try {
				recordServices.update(rmSchemas.getFolder(currentFolderId).setParentFolder(parentId));
				navigate().to(RMViews.class).displayFolder(currentFolderId);
			} catch (RecordServicesException.ValidationException e) {
				view.showErrorMessage($(e.getErrors()));
			}
		}
	}

	public void createNewCartAndAddToItRequested(String title) {
		Cart cart = rmSchemasRecordsServices.newCart();
		cart.setTitle(title);
		cart.setOwner(getCurrentUser());
		try {
			cart.addFolders(Arrays.asList(folderVO.getId()));
			recordServices().execute(new Transaction(cart.getWrappedRecord()).setUser(getCurrentUser()));
			view.showMessage($("DisplayFolderView.addedToCart"));
		} catch (RecordServicesException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public RecordVODataProvider getEventsDataProvider() {
		final MetadataSchemaVO eventSchemaVO = schemaVOBuilder
				.build(rmSchemasRecordsServices.eventSchema(), VIEW_MODE.TABLE, view.getSessionContext());
		return new RecordVODataProvider(eventSchemaVO, new EventToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				RMEventsSearchServices rmEventsSearchServices = new RMEventsSearchServices(modelLayerFactory, collection);
				return rmEventsSearchServices.newFindEventByRecordIDQuery(getCurrentUser(), folderVO.getId());
			}
		};
	}

	protected boolean hasCurrentUserPermissionToViewEvents() {
		return getCurrentUser().has(CorePermissions.VIEW_EVENTS).on(toRecord(folderVO));
	}

	void metadataTabSelected() {
		view.selectMetadataTab();
	}

	void folderContentTabSelected() {
		view.selectFolderContentTab();
	}

	void tasksTabSelected() {
		view.selectTasksTab();
	}

	void eventsTabSelected() {
		view.selectEventsTab();
	}

	public boolean hasCurrentUserPermissionToUseCart() {
		return getCurrentUser().has(RMPermissionsTo.USE_CART).globally();
	}

	public boolean hasPermissionToStartWorkflow() {
		return getCurrentUser().has(TasksPermissionsTo.START_WORKFLOWS).globally();
	}

	public boolean isSelected(RecordVO recordVO) {
		String recordId = recordVO.getId();
		SessionContext sessionContext = view.getSessionContext();
		return sessionContext.getSelectedRecordIds().contains(recordId);
	}

	public void recordSelectionChanged(RecordVO recordVO, Boolean selected) {
		String recordId = recordVO.getId();
		SessionContext sessionContext = view.getSessionContext();
		if (selected) {
			sessionContext.addSelectedRecordId(recordId, recordVO.getSchema().getTypeCode());
		} else {
			sessionContext.removeSelectedRecordId(recordId, recordVO.getSchema().getTypeCode());
		}
	}

	void computeAllItemsSelected() {
		SessionContext sessionContext = view.getSessionContext();
		List<String> selectedRecordIds = sessionContext.getSelectedRecordIds();
		SearchServices searchServices = modelLayerFactory.newSearchServices();

		if (selectedRecordIds.isEmpty()) {
			allItemsSelected = false;
			return;
		}

		List<String> subFolderIds = searchServices.searchRecordIds(getSubFoldersQuery());
		for (String subFolderId : subFolderIds) {
			if (!selectedRecordIds.contains(subFolderId)) {
				allItemsSelected = false;
				return;
			}
		}
		List<String> documentIds = searchServices.searchRecordIds(getDocumentsQuery());
		for (String documentId : documentIds) {
			if (!selectedRecordIds.contains(documentId)) {
				allItemsSelected = false;
				return;
			}
		}
		allItemsSelected = !subFolderIds.isEmpty() || !documentIds.isEmpty();
	}

	boolean isAllItemsSelected() {
		return allItemsSelected;
	}

	boolean isAllItemsDeselected() {
		return allItemsDeselected;
	}

	void selectAllClicked() {
		allItemsSelected = true;
		allItemsDeselected = false;

		SessionContext sessionContext = view.getSessionContext();
		SearchServices searchServices = modelLayerFactory.newSearchServices();
		List<String> subFolderIds = searchServices.searchRecordIds(getSubFoldersQuery());
		for (String subFolderId : subFolderIds) {
			sessionContext.addSelectedRecordId(subFolderId, Folder.SCHEMA_TYPE);
		}
		List<String> documentIds = searchServices.searchRecordIds(getDocumentsQuery());
		for (String documentId : documentIds) {
			sessionContext.addSelectedRecordId(documentId, Document.SCHEMA_TYPE);
		}
	}

	void deselectAllClicked() {
		allItemsSelected = false;
		allItemsDeselected = true;

		SessionContext sessionContext = view.getSessionContext();
		SearchServices searchServices = modelLayerFactory.newSearchServices();
		List<String> subFolderIds = searchServices.searchRecordIds(getSubFoldersQuery());
		for (String subFolderId : subFolderIds) {
			sessionContext.removeSelectedRecordId(subFolderId, Folder.SCHEMA_TYPE);
		}
		List<String> documentIds = searchServices.searchRecordIds(getDocumentsQuery());
		for (String documentId : documentIds) {
			sessionContext.removeSelectedRecordId(documentId, Document.SCHEMA_TYPE);
		}
	}

	String generateDisplayLink(Document document) {
		String constellioUrl = eimConfigs.getConstellioUrl();
		String displayURL = RMNavigationConfiguration.DISPLAY_DOCUMENT;
		String url = constellioUrl + "#!" + displayURL + "/" + document.getId();
		return "<a href=\"" + url + "\">" + url + "</a>";
	}

	public boolean isLogicallyDeleted() {
		return Boolean.TRUE
				.equals(folderVO.getMetadataValue(folderVO.getMetadata(Schemas.LOGICALLY_DELETED_STATUS.getLocalCode()))
						.getValue());
	}

	private void addStarredSortToQuery(LogicalSearchQuery query) {
		Metadata metadata = types().getSchema(Task.DEFAULT_SCHEMA).getMetadata(STARRED_BY_USERS);
		LogicalSearchQuerySort sortField = new FunctionLogicalSearchQuerySort(
				"termfreq(" + metadata.getDataStoreCode() + ",\'" + getCurrentUser().getId() + "\')", false);
		query.sortFirstOn(sortField);
	}

	public RMSelectionPanelReportPresenter buildReportPresenter() {
		return new RMSelectionPanelReportPresenter(appLayerFactory, collection, getCurrentUser()) {
			@Override
			public String getSelectedSchemaType() {
				return Folder.SCHEMA_TYPE;
			}

			@Override
			public List<String> getSelectedRecordIds() {
				return asList(folderVO.getId());
			}
		};
	}

	public AppLayerFactory getApplayerFactory() {
		return appLayerFactory;
	}
}
