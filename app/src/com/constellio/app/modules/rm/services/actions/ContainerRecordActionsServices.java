package com.constellio.app.modules.rm.services.actions;

import com.constellio.app.modules.rm.ConstellioRMModule;
import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.modules.rm.constants.RMPermissionsTo;
import com.constellio.app.modules.rm.extensions.api.RMModuleExtensions;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.ContainerRecord;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.ui.framework.reports.NewReportWriterFactory;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.search.StatusFilter;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.services.search.query.logical.condition.LogicalSearchCondition;
import com.constellio.model.services.security.AuthorizationsServices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;

public class ContainerRecordActionsServices {

	private RMSchemasRecordsServices rm;
	private RMModuleExtensions rmModuleExtensions;
	private AppLayerFactory appLayerFactory;
	private ModelLayerFactory modelLayerFactory;
	private String collection;
	private SearchServices searchServices;
	private RecordServices recordServices;

	public ContainerRecordActionsServices(String collection, AppLayerFactory appLayerFactory) {
		this.rm = new RMSchemasRecordsServices(collection, appLayerFactory);
		this.collection = collection;
		this.appLayerFactory = appLayerFactory;
		this.modelLayerFactory = appLayerFactory.getModelLayerFactory();
		this.recordServices = appLayerFactory.getModelLayerFactory().newRecordServices();
		this.rmModuleExtensions = appLayerFactory.getExtensions().forCollection(collection).forModule(ConstellioRMModule.ID);
		this.searchServices = modelLayerFactory.newSearchServices();
	}

	public boolean isEditActionPossible(Record record, User user) {
		return user.hasWriteAccess().on(record) && rmModuleExtensions
				.isEditActionPossibleOnContainerRecord(rm.wrapContainerRecord(record), user);
	}

	public boolean isSlipActionPossible(Record record, User user) {
		return user.hasReadAccess().on(record)
			   && rmModuleExtensions.isSlipActionPossibleOnContainerRecord(rm.wrapContainerRecord(record), user) && canPrintReports();
	}

	public boolean canPrintReports() {
		try {
			getReportWriterFactory();
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	public NewReportWriterFactory getReportWriterFactory() {
		RMModuleExtensions rmModuleExtensions = appLayerFactory.getExtensions().forCollection(collection).forModule(ConstellioRMModule.ID);
		return rmModuleExtensions.getReportBuilderFactories().transferContainerRecordBuilderFactory.getValue();
	}

	public boolean isDisplayActionPossible(Record record, User user) {
		return user.hasReadAccess().on(record)
			   && rmModuleExtensions.isConsultActionPossibleOnContainerRecord(rm.wrapContainerRecord(record), user);
	}

	public boolean isAddToCartActionPossible(Record record, User user) {
		return user.hasReadAccess().on(record) &&
			   (hasUserPermissionToUseCart(user) || hasUserPermissionToUseMyCart(user)) &&
			   rmModuleExtensions.isAddToCartActionPossibleOnContainerRecord(rm.wrapContainerRecord(record), user);
	}

	private boolean hasUserPermissionToUseCart(User user) {
		return user.has(RMPermissionsTo.USE_GROUP_CART).globally();
	}

	private boolean hasUserPermissionToUseMyCart(User user) {
		return user.has(RMPermissionsTo.USE_MY_CART).globally();
	}

	public boolean isPrintLabelActionPossible(Record record, User user) {
		return user.hasReadAccess().on(record)
			   && rmModuleExtensions.isLabelsActionPossibleOnContainerRecord(rm.wrapContainerRecord(record), user)
			   && canPrintReports();
	}

	public boolean canDeleteContainers(List<String> ids, User user) {
		for (Record record : recordServices.getRecordsById(collection, ids)) {
			if (!record.getSchemaCode().startsWith(ContainerRecord.SCHEMA_TYPE)) {
				continue;
			}

			if (!isDeleteActionPossible(record, user)) {
				return false;
			}
		}
		return true;
	}

	public boolean isDeleteActionPossible(Record record, User user) {
		ContainerRecord containerRecord = rm.wrapContainerRecord(record);
		List<String> adminUnitIdsWithPermissions = getConceptsWithPermissionsForCurrentUser(user, RMPermissionsTo.DELETE_CONTAINERS);
		List<String> adminUnitIds = new ArrayList<>(containerRecord.getAdministrativeUnits());
		if (adminUnitIds.isEmpty() && containerRecord.getAdministrativeUnit() != null) {
			adminUnitIds.add(containerRecord.getAdministrativeUnit());
		}

		if (adminUnitIdsWithPermissions.isEmpty()) {
			return false;
		}

		for (String adminUnitId : adminUnitIds) {
			if (!adminUnitIdsWithPermissions.contains(adminUnitId)) {
				return false;
			}
		}

		if (containerRecord.isLogicallyDeletedStatus()) {
			return false;
		}

		return user.hasDeleteAccess().on(record)
			   && rmModuleExtensions.isDeleteActionPossibleOnContainerRecord(rm.wrapContainerRecord(record), user);
	}

	public List<String> getConceptsWithPermissionsForCurrentUser(User user, String... permissions) {
		Set<String> recordIds = new HashSet<>();
		AuthorizationsServices authorizationsServices = modelLayerFactory.newAuthorizationsServices();
		for (String permission : permissions) {
			recordIds.addAll(authorizationsServices.getConceptsForWhichUserHasPermission(permission, user));
		}
		return new ArrayList<>(recordIds);
	}

	public boolean isEmptyTheBoxActionPossible(Record record, User user) {

		if (!(user.hasWriteAccess().on(record) && isContainerRecyclingAllowed()
			  && rmModuleExtensions.isEmptyTheBoxActionPossibleOnContainerRecord(rm.wrapContainerRecord(record), user))) {
			return false;
		}

		boolean approveDecommissioningListPermission = false;
		if (user.has(RMPermissionsTo.APPROVE_DECOMMISSIONING_LIST).globally()) {
			approveDecommissioningListPermission = true;
		} else {
			ContainerRecord containerRecord = rm.wrapContainerRecord(record);
			List<String> adminUnitIdsWithPermissions = getConceptsWithPermissionsForCurrentUser(user, RMPermissionsTo.APPROVE_DECOMMISSIONING_LIST);
			List<String> adminUnitIds = new ArrayList<>(containerRecord.getAdministrativeUnits());
			if (adminUnitIds.isEmpty() && containerRecord.getAdministrativeUnit() != null) {
				adminUnitIds.add(containerRecord.getAdministrativeUnit());
			}
			for (String adminUnitId : adminUnitIds) {
				if (adminUnitIdsWithPermissions.contains(adminUnitId)) {
					approveDecommissioningListPermission = true;
					break;
				}
			}
		}

		return approveDecommissioningListPermission && searchServices.hasResults(getFoldersQuery(user, record.getId()));
	}

	public boolean isConsultLinkActionPossible(Record record, User user) {
		return user.hasReadAccess().on(record)
			   && rmModuleExtensions.isConsultLinkActionPossibleOnContainerRecord(rm.wrapContainerRecord(record), user);
	}

	private LogicalSearchQuery getFoldersQuery(User user, String containerId) {
		LogicalSearchCondition condition = from(rm.folder.schemaType())
				.where(rm.folder.container()).isEqualTo(containerId)
				.andWhere(Schemas.LOGICALLY_DELETED_STATUS).isFalseOrNull();
		return new LogicalSearchQuery(condition).filteredWithUser(user).filteredByStatus(StatusFilter.ACTIVES);
	}

	private boolean isContainerRecyclingAllowed() {
		return new RMConfigs(modelLayerFactory.getSystemConfigurationsManager()).isContainerRecyclingAllowed();
	}
}
