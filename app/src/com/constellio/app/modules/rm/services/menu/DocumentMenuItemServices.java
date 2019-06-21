package com.constellio.app.modules.rm.services.menu;

import com.constellio.app.modules.rm.services.actions.DocumentRecordActionsServices;
import com.constellio.app.modules.rm.services.menu.behaviors.DocumentMenuItemActionBehaviors;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.services.menu.MenuItemAction;
import com.constellio.app.services.menu.MenuItemActionState;
import com.constellio.app.services.menu.behavior.MenuItemActionBehaviorParams;
import com.constellio.app.ui.i18n.i18n;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.app.ui.util.FileIconUtils;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

import java.util.ArrayList;
import java.util.List;

import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_ADD_AUTHORIZATION;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_ADD_TO_CART;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_ADD_TO_SELECTION;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_CHECK_IN;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_CHECK_OUT;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_COPY;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_CREATE_PDF;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_DELETE;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_DISPLAY;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_DOWNLOAD;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_EDIT;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_FINALIZE;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_GENERATE_REPORT;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_OPEN;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_PRINT_LABEL;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_PUBLISH;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_REMOVE_TO_SELECTION;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_UNPUBLISH;
import static com.constellio.app.modules.rm.services.menu.DocumentMenuItemServices.DocumentMenuItemActionType.DOCUMENT_UPLOAD;
import static com.constellio.app.services.menu.MenuItemActionState.MenuItemActionStateStatus.HIDDEN;
import static com.constellio.app.services.menu.MenuItemActionState.MenuItemActionStateStatus.VISIBLE;

public class DocumentMenuItemServices {

	private DocumentRecordActionsServices documentRecordActionsServices;
	private String collection;
	private AppLayerFactory appLayerFactory;

	public DocumentMenuItemServices(String collection, AppLayerFactory appLayerFactory) {
		this.collection = collection;
		this.appLayerFactory = appLayerFactory;

		documentRecordActionsServices = new DocumentRecordActionsServices(collection, appLayerFactory);
	}

	public List<MenuItemAction> getActionsForRecord(Document document, User user, List<String> filteredActionTypes,
													MenuItemActionBehaviorParams params) {
		List<MenuItemAction> menuItemActions = new ArrayList<>();

		if (!filteredActionTypes.contains(DOCUMENT_DISPLAY.name())) {
			menuItemActions.add(buildMenuItemAction(DOCUMENT_DISPLAY.name(),
					isMenuItemActionPossible(DOCUMENT_DISPLAY.name(), document, user, params),
					"DisplayDocumentView.displayDocument", FontAwesome.FILE_O, -1, 100,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).display(document, params)));
		}

		if (!filteredActionTypes.contains(DOCUMENT_OPEN.name())) {
			// FIXME better way? get icon by mime-type?
			Resource icon = FileIconUtils.getIcon(document.getContent() != null ?
												  document.getContent().getCurrentVersion().getFilename() : "");
			menuItemActions.add(buildMenuItemAction(DOCUMENT_OPEN.name(),
					isMenuItemActionPossible(DOCUMENT_OPEN.name(), document, user, params),
					"DisplayDocumentView.openDocument", icon, -1, 200,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).open(document, params)));
		}

		if (!filteredActionTypes.contains(DOCUMENT_EDIT.name())) {
			menuItemActions.add(buildMenuItemAction(DOCUMENT_EDIT.name(),
					isMenuItemActionPossible(DOCUMENT_EDIT.name(), document, user, params),
					"DisplayDocumentView.editDocument", FontAwesome.EDIT, -1, 250,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).edit(document, params)));
		}

		if (!filteredActionTypes.contains(DOCUMENT_DOWNLOAD.name())) {
			menuItemActions.add(buildMenuItemAction(DOCUMENT_DOWNLOAD.name(),
					isMenuItemActionPossible(DOCUMENT_DOWNLOAD.name(), document, user, params),
					"DocumentContextMenu.downloadDocument", FontAwesome.DOWNLOAD, -1, 400,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).download(document, params)));
		}

		if (!filteredActionTypes.contains(DOCUMENT_DELETE.name())) {
			MenuItemAction menuItemAction = buildMenuItemAction(DOCUMENT_DELETE.name(),
					isMenuItemActionPossible(DOCUMENT_DELETE.name(), document, user, params),
					"DocumentContextMenu.deleteDocument", FontAwesome.TRASH_O, -1, 500,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).delete(document, params));

			menuItemAction.setConfirmMessage(i18n.$("ConfirmDialog.confirmDelete"));

			menuItemActions.add(menuItemAction);
		}

		if (!filteredActionTypes.contains(DOCUMENT_COPY.name())) {
			menuItemActions.add(buildMenuItemAction(DOCUMENT_COPY.name(),
					isMenuItemActionPossible(DOCUMENT_COPY.name(), document, user, params),
					"DocumentContextMenu.copyContent", null, -1, 600,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).copy(document, params)));
		}

		if (!filteredActionTypes.contains(DOCUMENT_FINALIZE.name())) {
			MenuItemAction menuItemAction = buildMenuItemAction(DOCUMENT_FINALIZE.name(),
					isMenuItemActionPossible(DOCUMENT_FINALIZE.name(), document, user, params),
					"DocumentContextMenu.finalize", FontAwesome.LEVEL_UP, -1, 700,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).finalize(document, params));

			menuItemAction.setConfirmMessage(i18n.$("DocumentActionsComponent.finalize.confirm"));
			menuItemActions.add(menuItemAction);
		}

		if (!filteredActionTypes.contains(DOCUMENT_PUBLISH.name())) {
			MenuItemAction menuItemAction = buildMenuItemAction(DOCUMENT_PUBLISH.name(),
					isMenuItemActionPossible(DOCUMENT_PUBLISH.name(), document, user, params),
					"DocumentContextMenu.publish", null, -1, 800,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).publish(document, params));

			menuItemActions.add(menuItemAction);
		}

		if (!filteredActionTypes.contains(DOCUMENT_UNPUBLISH.name())) {
			MenuItemAction menuItemAction = buildMenuItemAction(DOCUMENT_UNPUBLISH.name(),
					isMenuItemActionPossible(DOCUMENT_UNPUBLISH.name(), document, user, params),
					"DocumentContextMenu.unpublish", null, -1, 800,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).unPublish(document, params));

			menuItemActions.add(menuItemAction);
		}


		if (!filteredActionTypes.contains(DOCUMENT_CREATE_PDF.name())) {
			MenuItemAction menuItemAction = buildMenuItemAction(DOCUMENT_CREATE_PDF.name(),
					isMenuItemActionPossible(DOCUMENT_CREATE_PDF.name(), document, user, params),
					"DocumentContextMenu.createPDFA", null, -1, 900,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).createPdf(document, params));
			menuItemAction.setConfirmMessage(i18n.$("ConfirmDialog.confirmCreatePDFA"));

			menuItemActions.add(menuItemAction);
		}

		if (!filteredActionTypes.contains(DOCUMENT_ADD_TO_SELECTION.name())) {
			MenuItemAction menuItemAction = buildMenuItemAction(DOCUMENT_ADD_TO_SELECTION.name(),
					isMenuItemActionPossible(DOCUMENT_ADD_TO_SELECTION.name(), document, user, params),
					"DocumentContextMenu.addToSelection", null, -1, 1000,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).addToSelection(document, params));

			menuItemActions.add(menuItemAction);
		}


		if (!filteredActionTypes.contains(DOCUMENT_REMOVE_TO_SELECTION.name())) {
			MenuItemAction menuItemAction = buildMenuItemAction(DOCUMENT_REMOVE_TO_SELECTION.name(),
					isMenuItemActionPossible(DOCUMENT_REMOVE_TO_SELECTION.name(), document, user, params),
					"DocumentContextMenu.removeToSelection", null, -1, 1100,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).removeToSelection(document, params));

			menuItemActions.add(menuItemAction);
		}

		if (!filteredActionTypes.contains(DOCUMENT_ADD_TO_CART.name())) {
			MenuItemAction menuItemAction = buildMenuItemAction(DOCUMENT_ADD_TO_CART.name(),
					isMenuItemActionPossible(DOCUMENT_ADD_TO_CART.name(), document, user, params),
					"DisplayFolderView.addToCart", null, -1, 1200,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).addToCart(document, params));

			menuItemActions.add(menuItemAction);
		}

		if (!filteredActionTypes.contains(DOCUMENT_UPLOAD.name())) {
			MenuItemAction menuItemAction = buildMenuItemAction(DOCUMENT_UPLOAD.name(),
					isMenuItemActionPossible(DOCUMENT_UPLOAD.name(), document, user, params),
					"DocumentContextMenu.upload", null, -1, 1250,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).upload(document, params));

			menuItemActions.add(menuItemAction);
		}

		if (!filteredActionTypes.contains(DOCUMENT_PRINT_LABEL.name())) {
			MenuItemAction menuItemAction = buildMenuItemAction(DOCUMENT_PRINT_LABEL.name(),
					isMenuItemActionPossible(DOCUMENT_PRINT_LABEL.name(), document, user, params),
					"DisplayFolderView.printLabel", null, -1, 1300,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).printLabel(document, params));

			menuItemActions.add(menuItemAction);
		}

		if (!filteredActionTypes.contains(DOCUMENT_CHECK_IN.name())) {
			MenuItemAction menuItemAction = buildMenuItemAction(DOCUMENT_CHECK_IN.name(),
					isMenuItemActionPossible(DOCUMENT_CHECK_IN.name(), document, user, params),
					"DocumentContextMenu.checkIn", null, -1, 1400,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).checkIn(document, params));

			menuItemActions.add(menuItemAction);
		}

		if (!filteredActionTypes.contains(DOCUMENT_CHECK_OUT.name())) {
			MenuItemAction menuItemAction = buildMenuItemAction(DOCUMENT_CHECK_OUT.name(),
					isMenuItemActionPossible(DOCUMENT_CHECK_OUT.name(), document, user, params),
					"DocumentContextMenu.checkOut", null, -1, 1400,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).checkOut(document, params));

			menuItemActions.add(menuItemAction);
		}

		if (!filteredActionTypes.contains(DOCUMENT_ADD_AUTHORIZATION.name())) {
			MenuItemAction menuItemAction = buildMenuItemAction(DOCUMENT_ADD_AUTHORIZATION.name(),
					isMenuItemActionPossible(DOCUMENT_ADD_AUTHORIZATION.name(), document, user, params),
					"DocumentContextMenu.addAuthorization", null, -1, 1500,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).addAuthorization(document, params));

			menuItemActions.add(menuItemAction);
		}

		if (!filteredActionTypes.contains(DOCUMENT_GENERATE_REPORT.name())) {
			MenuItemAction menuItemAction = buildMenuItemAction(DOCUMENT_GENERATE_REPORT.name(),
					isMenuItemActionPossible(DOCUMENT_GENERATE_REPORT.name(), document, user, params),
					"DocumentContextMenu.ReportGeneratorButton", null, -1, 1600,
					() -> new DocumentMenuItemActionBehaviors(collection, appLayerFactory).reportGeneratorButton(document, params));

			menuItemActions.add(menuItemAction);
		}

		return menuItemActions;
	}

	public boolean isMenuItemActionPossible(String menuItemActionType, Document document, User user,
											MenuItemActionBehaviorParams params) {
		SessionContext sessionContext = params.getView().getSessionContext();
		Record record = document.getWrappedRecord();

		switch (DocumentMenuItemActionType.valueOf(menuItemActionType)) {
			case DOCUMENT_EDIT:
				return documentRecordActionsServices.isEditActionPossible(record, user);
			case DOCUMENT_DISPLAY:
				return documentRecordActionsServices.isDisplayActionPossible(record, user);
			case DOCUMENT_OPEN:
				return documentRecordActionsServices.isOpenActionPossible(record, user);
			case DOCUMENT_DOWNLOAD:
				return documentRecordActionsServices.isDownloadActionPossible(record, user);
			case DOCUMENT_DELETE:
				return documentRecordActionsServices.isDeleteActionPossible(record, user);
			case DOCUMENT_COPY:
				return documentRecordActionsServices.isEditActionPossible(record, user);
			case DOCUMENT_FINALIZE:
				return documentRecordActionsServices.isFinalizeActionPossible(record, user);
			case DOCUMENT_PUBLISH:
				return documentRecordActionsServices.isPublishActionPossible(record, user);
			case DOCUMENT_UNPUBLISH:
				return documentRecordActionsServices.isUnPublishActionPossible(record, user);
			case DOCUMENT_CREATE_PDF:
				return documentRecordActionsServices.isCreatePdfActionPossible(record, user);
			case DOCUMENT_ADD_TO_SELECTION:
				return documentRecordActionsServices.isAddToSelectionActionPossible(record, user, sessionContext) &&
					   (sessionContext.getSelectedRecordIds() == null ||
						!sessionContext.getSelectedRecordIds().contains(record.getId()));
			case DOCUMENT_REMOVE_TO_SELECTION:
				return documentRecordActionsServices.isRemoveToSelectionActionPossible(record, user) &&
					   sessionContext.getSelectedRecordIds() != null &&
					   sessionContext.getSelectedRecordIds().contains(record.getId());
			case DOCUMENT_ADD_TO_CART:
				return documentRecordActionsServices.isAddToCartActionPossible(record, user);
			case DOCUMENT_UPLOAD:
				return documentRecordActionsServices.isUploadActionPossible(record, user);
			case DOCUMENT_PRINT_LABEL:
				return documentRecordActionsServices.isPrintLabelActionPossible(record, user);
			case DOCUMENT_CHECK_OUT:
				return documentRecordActionsServices.isCheckOutActionPossible(record, user);
			case DOCUMENT_CHECK_IN:
				return documentRecordActionsServices.isCheckInActionPossible(record, user);
			case DOCUMENT_ADD_AUTHORIZATION:
				return documentRecordActionsServices.isAddAuthorizationActionPossible(record, user);
			case DOCUMENT_GENERATE_REPORT:
				return documentRecordActionsServices.isGenerateReportActionPossible(record, user);
			default:
				throw new RuntimeException("Unknown MenuItemActionType : " + menuItemActionType);
		}
	}

	private MenuItemAction buildMenuItemAction(String type, boolean possible, String caption, Resource icon,
											   int group, int priority, Runnable command) {
		return MenuItemAction.builder()
				.type(type)
				.state(new MenuItemActionState(possible ? VISIBLE : HIDDEN))
				.caption(caption)
				.icon(icon)
				.group(group)
				.priority(priority)
				.command(command)
				.build();
	}

	enum DocumentMenuItemActionType {
		DOCUMENT_DISPLAY,
		DOCUMENT_OPEN,
		DOCUMENT_EDIT,
		DOCUMENT_DOWNLOAD,
		DOCUMENT_DELETE,
		DOCUMENT_COPY,
		DOCUMENT_FINALIZE,
		DOCUMENT_PUBLISH,
		DOCUMENT_UNPUBLISH,
		DOCUMENT_CREATE_PDF,
		DOCUMENT_ADD_TO_SELECTION,
		DOCUMENT_REMOVE_TO_SELECTION,
		DOCUMENT_ADD_TO_CART,
		DOCUMENT_UPLOAD,
		DOCUMENT_PRINT_LABEL,
		DOCUMENT_CHECK_OUT,
		DOCUMENT_CHECK_IN,
		DOCUMENT_ADD_AUTHORIZATION,
		DOCUMENT_GENERATE_REPORT;
	}
}