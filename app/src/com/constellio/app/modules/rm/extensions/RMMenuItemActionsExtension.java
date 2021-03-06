package com.constellio.app.modules.rm.extensions;

import com.constellio.app.extensions.menu.MenuItemActionsExtension;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.services.menu.CartMenuItemServices;
import com.constellio.app.modules.rm.services.menu.ContainerMenuItemServices;
import com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices;
import com.constellio.app.modules.rm.services.menu.FolderMenuItemServices;
import com.constellio.app.modules.rm.services.menu.RMRecordsMenuItemServices;
import com.constellio.app.modules.rm.services.menu.RMRecordsMenuItemServices.RMRecordsMenuItemActionType;
import com.constellio.app.modules.rm.wrappers.Cart;
import com.constellio.app.modules.rm.wrappers.ContainerRecord;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.services.menu.MenuItemAction;
import com.constellio.app.services.menu.MenuItemActionState;
import com.constellio.app.services.menu.behavior.MenuItemActionBehaviorParams;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;

import java.util.List;

public class RMMenuItemActionsExtension extends MenuItemActionsExtension {

	private RMSchemasRecordsServices rm;

	private DocumentMenuItemServices documentMenuItemServices;
	private FolderMenuItemServices folderMenuItemServices;
	private ContainerMenuItemServices containerMenuItemServices;
	private CartMenuItemServices cartMenuItemServices;
	private RMRecordsMenuItemServices rmRecordsMenuItemServices;

	public RMMenuItemActionsExtension(String collection, AppLayerFactory appLayerFactory) {
		rm = new RMSchemasRecordsServices(collection, appLayerFactory);

		documentMenuItemServices = new DocumentMenuItemServices(collection, appLayerFactory);
		folderMenuItemServices = new FolderMenuItemServices(collection, appLayerFactory);
		containerMenuItemServices = new ContainerMenuItemServices(collection, appLayerFactory);
		cartMenuItemServices = new CartMenuItemServices(collection, appLayerFactory);
		rmRecordsMenuItemServices = new RMRecordsMenuItemServices(collection, appLayerFactory);
	}

	@Override
	public void addMenuItemActionsForRecord(MenuItemActionExtensionAddMenuItemActionsForRecordParams params) {
		Record record = params.getRecord();
		User user = params.getBehaviorParams().getUser();
		List<MenuItemAction> menuItemActions = params.getMenuItemActions();
		List<String> excludedActionTypes = params.getExcludedActionTypes();
		MenuItemActionBehaviorParams behaviorParams = params.getBehaviorParams();

		if (record != null) {
			if (record.isOfSchemaType(Document.SCHEMA_TYPE)) {
				menuItemActions.addAll(documentMenuItemServices.getActionsForRecord(rm.wrapDocument(record), user,
						excludedActionTypes, behaviorParams));
			} else if (record.isOfSchemaType(Folder.SCHEMA_TYPE)) {
				menuItemActions.addAll(folderMenuItemServices.getActionsForRecord(rm.wrapFolder(record), user,
						excludedActionTypes, behaviorParams));
			} else if (record.isOfSchemaType(ContainerRecord.SCHEMA_TYPE)) {
				menuItemActions.addAll(containerMenuItemServices.getActionsForRecord(rm.wrapContainerRecord(record), user,
						excludedActionTypes, behaviorParams));
			} else if (record.isOfSchemaType(Cart.SCHEMA_TYPE)) {
				menuItemActions.addAll(cartMenuItemServices.getActionsForRecord(rm.wrapCart(record), user, excludedActionTypes, behaviorParams));
			}
		}
	}

	@Override
	public void addMenuItemActionsForRecords(MenuItemActionExtensionAddMenuItemActionsForRecordsParams params) {
		List<Record> records = params.getRecords();
		User user = params.getBehaviorParams().getUser();
		List<MenuItemAction> menuItemActions = params.getMenuItemActions();
		List<String> excludedActionTypes = params.getExcludedActionTypes();
		MenuItemActionBehaviorParams behaviorParams = params.getBehaviorParams();

		menuItemActions.addAll(rmRecordsMenuItemServices.getActionsForRecords(records, user,
				excludedActionTypes, behaviorParams));
	}

	@Override
	public MenuItemActionState getActionStateForRecord(MenuItemActionExtensionGetActionStateForRecordParams params) {
		Record record = params.getRecord();
		User user = params.getBehaviorParams().getUser();
		String actionType = params.getMenuItemActionType();
		MenuItemActionBehaviorParams behaviorParams = params.getBehaviorParams();

		if (record.isOfSchemaType(Folder.SCHEMA_TYPE)) {
			return toState(folderMenuItemServices.isMenuItemActionPossible(actionType, rm.wrapFolder(record),
					user, behaviorParams));
		} else if (record.isOfSchemaType(Document.SCHEMA_TYPE)) {
			return toState(documentMenuItemServices.isMenuItemActionPossible(actionType, rm.wrapDocument(record),
					user, behaviorParams));
		} else if (record.isOfSchemaType(ContainerRecord.SCHEMA_TYPE)) {
			return toState(containerMenuItemServices.isMenuItemActionPossible(actionType,
					rm.wrapContainerRecord(record), user, behaviorParams));
		}
		return null;
	}

	@Override
	public MenuItemActionState getActionStateForRecords(MenuItemActionExtensionGetActionStateForRecordsParams params) {
		List<Record> records = params.getRecords();
		User user = params.getBehaviorParams().getUser();
		String actionType = params.getMenuItemActionType();
		MenuItemActionBehaviorParams behaviorParams = params.getBehaviorParams();

		if (RMRecordsMenuItemActionType.contains(actionType)) {
			return rmRecordsMenuItemServices.getMenuItemActionStateForRecords(
					RMRecordsMenuItemActionType.valueOf(actionType), records, user, behaviorParams);
		}
		return null;
	}
}
