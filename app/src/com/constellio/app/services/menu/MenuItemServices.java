package com.constellio.app.services.menu;

import com.constellio.app.extensions.menu.MenuItemActionsExtension;
import com.constellio.app.extensions.menu.MenuItemActionsExtension.MenuItemActionExtensionAddMenuItemActionsParams;
import com.constellio.app.extensions.menu.MenuItemActionsExtension.MenuItemActionExtensionGetStateForActionParams;
import com.constellio.app.modules.rm.services.menu.RecordListMenuItemServices;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.services.menu.behavior.MenuItemActionBehaviorParams;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.Group;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuItemServices {

	private List<MenuItemActionsExtension> menuItemActionsExtensions;

	private RecordListMenuItemServices recordListMenuItemServices;

	public MenuItemServices(String collection, AppLayerFactory appLayerFactory) {
		menuItemActionsExtensions = appLayerFactory.getExtensions()
				.forCollection(collection).menuItemActionsExtensions.getExtensions();

		recordListMenuItemServices = new RecordListMenuItemServices(collection, appLayerFactory);
	}

	public List<MenuItemAction> getActionsForRecord(Record record, MenuItemActionBehaviorParams params) {
		return getActionsForRecord(record, Collections.emptyList(), params);
	}

	public List<MenuItemAction> getActionsForRecord(Record record, List<String> filteredActionTypes,
													MenuItemActionBehaviorParams params) {
		if (params.getView() == null) {
			return Collections.emptyList();
		}

		List<MenuItemAction> menuItemActions = new ArrayList<>();
		if (record.isOfSchemaType(User.SCHEMA_TYPE)) {
			// TODO
		} else if (record.isOfSchemaType(Group.SCHEMA_TYPE)) {
			// TODO
		}

		addMenuItemActionsFromExtensions(record, filteredActionTypes, params, menuItemActions);

		return menuItemActions;
	}

	public List<MenuItemAction> getActionsForRecords(List<Record> records, MenuItemActionBehaviorParams params) {
		return getActionsForRecords(records, Collections.emptyList(), params);
	}

	public List<MenuItemAction> getActionsForRecords(List<Record> records, List<String> filteredActionTypes,
													 MenuItemActionBehaviorParams params) {
		List<MenuItemAction> menuItemActions = new ArrayList<>();

		menuItemActions.addAll(recordListMenuItemServices.getActionsForRecords(records, params.getUser(),
				filteredActionTypes, params));

		// TODO extensions
		//addMenuItemActionsFromExtensions(record, user, params.getView(), menuItemActions);

		return menuItemActions;
	}

	public List<MenuItemAction> getActionsForRecords(LogicalSearchQuery query, MenuItemActionBehaviorParams params) {
		// TODO
		return null;
	}

	public MenuItemActionState getStateForAction(MenuItemAction action, Record record,
												 MenuItemActionBehaviorParams params) {
		if (record.isOfSchemaType(User.SCHEMA_TYPE)) {
			// TODO
		} else if (record.isOfSchemaType(Group.SCHEMA_TYPE)) {
			// TODO
		} else {
			// TODO
		}

		return geStateForActionFromExtensions(action, record, params);
	}

	public MenuItemActionState getStateForAction(MenuItemAction action, List<Record> records,
												 MenuItemActionBehaviorParams params) {
		return recordListMenuItemServices.getMenuItemActionState(MenuItemActionType.valueOf(action.getType()),
				records, params.getUser(), params);
	}

	public MenuItemActionState getStateForAction(MenuItemAction action, LogicalSearchQuery query,
												 MenuItemActionBehaviorParams params) {
		// TODO
		return null;
	}

	private void addMenuItemActionsFromExtensions(Record record, List<String> filteredActionTypes,
												  MenuItemActionBehaviorParams params,
												  List<MenuItemAction> menuItemActions) {
		for (MenuItemActionsExtension menuItemActionsExtension : menuItemActionsExtensions) {
			menuItemActionsExtension.addMenuItemActions(
					new MenuItemActionExtensionAddMenuItemActionsParams(record, menuItemActions,
							filteredActionTypes, params));
		}
	}

	private MenuItemActionState geStateForActionFromExtensions(MenuItemAction action, Record record,
															   MenuItemActionBehaviorParams behaviorParams) {
		MenuItemActionState state;
		for (MenuItemActionsExtension menuItemActionsExtension : menuItemActionsExtensions) {
			state = menuItemActionsExtension.getStateForAction(
					new MenuItemActionExtensionGetStateForActionParams(record, action, behaviorParams));
			if (state != null) {
				return state;
			}
		}
		return MenuItemActionState.HIDDEN;
	}

}
