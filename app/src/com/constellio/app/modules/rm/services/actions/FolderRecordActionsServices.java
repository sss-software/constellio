package com.constellio.app.modules.rm.services.actions;

import com.constellio.app.modules.rm.ConstellioRMModule;
import com.constellio.app.modules.rm.constants.RMPermissionsTo;
import com.constellio.app.modules.rm.extensions.api.RMModuleExtensions;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.ContainerRecord;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class FolderRecordActionsServices {

	private RMSchemasRecordsServices rm;
	private RMModuleExtensions rmModuleExtensions;

	public FolderRecordActionsServices(String collection, AppLayerFactory appLayerFactory) {
		rm = new RMSchemasRecordsServices(collection, appLayerFactory);
		rmModuleExtensions = appLayerFactory.getExtensions().forCollection(collection).forModule(ConstellioRMModule.ID);
	}

	public boolean isAddDocumentActionPossible(Record record, User user) {
		Folder folder = rm.wrapFolder(record);
		if (user.hasWriteAccess().on(folder) && isEditActionPossible(record, user) &&
			rmModuleExtensions.isAddDocumentActionPossibleOnFolder(rm.wrapFolder(record), user) &&
			user.has(RMPermissionsTo.CREATE_DOCUMENTS).on(folder)) {
			if (folder.getPermissionStatus().isInactive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return user.has(RMPermissionsTo.MODIFY_INACTIVE_BORROWED_FOLDER).on(folder) &&
						   user.has(RMPermissionsTo.CREATE_INACTIVE_DOCUMENT).on(folder);
				}
				return user.has(RMPermissionsTo.CREATE_INACTIVE_DOCUMENT).on(folder);
			}
			if (folder.getPermissionStatus().isSemiActive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_BORROWED_FOLDER).on(folder) &&
						   user.has(RMPermissionsTo.CREATE_SEMIACTIVE_DOCUMENT).on(folder);
				}
				return user.has(RMPermissionsTo.CREATE_SEMIACTIVE_DOCUMENT).on(folder);
			}
			return true;
		}
		return false;
	}

	public boolean isMoveActionPossible(Record record, User user) {
		return hasUserWriteAccess(record, user) && isEditActionPossible(record, user) &&
			   rmModuleExtensions.isMoveActionPossibleOnFolder(rm.wrapFolder(record), user);
	}

	public boolean isAddSubFolderActionPossible(Record record, User user) {
		Folder folder = rm.wrapFolder(record);
		if (user.hasWriteAccess().on(folder) && isEditActionPossible(record, user) &&
			rmModuleExtensions.isAddSubFolderActionPossibleOnFolder(rm.wrapFolder(record), user) &&
			user.hasAll(RMPermissionsTo.CREATE_SUB_FOLDERS, RMPermissionsTo.CREATE_FOLDERS).on(folder)) {
			if (folder.getPermissionStatus().isInactive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return user.has(RMPermissionsTo.MODIFY_INACTIVE_BORROWED_FOLDER).on(folder) && user
							.has(RMPermissionsTo.CREATE_SUB_FOLDERS_IN_INACTIVE_FOLDERS).on(folder);
				}
				return user.has(RMPermissionsTo.CREATE_SUB_FOLDERS_IN_INACTIVE_FOLDERS).on(folder);
			}
			if (folder.getPermissionStatus().isSemiActive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_BORROWED_FOLDER).on(folder) && user
							.has(RMPermissionsTo.CREATE_SUB_FOLDERS_IN_SEMIACTIVE_FOLDERS).on(folder);
				}
				return user.has(RMPermissionsTo.CREATE_SUB_FOLDERS_IN_SEMIACTIVE_FOLDERS).on(folder);
			}
			return true;
		}
		return false;
	}

	public boolean isDisplayActionPossible(Record record, User user) {
		return hasUserReadAccess(record, user) &&
			   rmModuleExtensions.isDisplayActionPossibleOnFolder(rm.wrapFolder(record), user);
	}

	public boolean isEditActionPossible(Record record, User user) {
		Folder folder = rm.wrapFolder(record);
		if (isNotBlank(folder.getLegacyId()) && !user.has(RMPermissionsTo.MODIFY_IMPORTED_FOLDERS).on(folder)) {
			return false;
		}
		return hasUserWriteAccess(record, user) &&
			   rmModuleExtensions.isEditActionPossibleOnFolder(folder, user);
	}

	public boolean isDeleteActionPossible(Record record, User user) {
		Folder folder = rm.wrapFolder(record);
		if (user.hasDeleteAccess().on(record) && rmModuleExtensions.isDeleteActionPossibleOnFolder(folder, user)) {
			if (folder.getPermissionStatus().isInactive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return user.has(RMPermissionsTo.MODIFY_INACTIVE_BORROWED_FOLDER).on(folder) && user
							.has(RMPermissionsTo.DELETE_INACTIVE_FOLDERS).on(folder);
				}
				return user.has(RMPermissionsTo.DELETE_INACTIVE_FOLDERS).on(folder);
			}
			if (folder.getPermissionStatus().isSemiActive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_BORROWED_FOLDER).on(folder) && user
							.has(RMPermissionsTo.DELETE_SEMIACTIVE_FOLDERS).on(folder);
				}
				return user.has(RMPermissionsTo.DELETE_SEMIACTIVE_FOLDERS).on(folder);
			}
			return true;
		}
		return false;
	}

	public boolean isCopyActionPossible(Record record, User user) {
		Folder folder = rm.wrapFolder(record);
		if (!hasUserReadAccess(record, user) ||
			(folder.getPermissionStatus().isInactive() && !user.has(RMPermissionsTo.DUPLICATE_INACTIVE_FOLDER).on(folder)) ||
			(folder.getPermissionStatus().isSemiActive() && !user.has(RMPermissionsTo.DUPLICATE_SEMIACTIVE_FOLDER).on(folder))) {
			return false;
		}
		return rmModuleExtensions.isCopyActionPossibleOnFolder(rm.wrapFolder(record), user);
	}

	public boolean isDownloadActionPossible(Record record, User user) {
		Folder folder = rm.wrapFolder(record);
		return hasUserReadAccess(record, user) && folder.hasContent() &&
			   rmModuleExtensions.isDownloadActionPossibleOnFolder(rm.wrapFolder(record), user);
	}

	public boolean isCreateSipActionPossible(Record record, User user) {
		return hasUserReadAccess(record, user) && user.has(RMPermissionsTo.GENERATE_SIP_ARCHIVES).globally() &&
			   rmModuleExtensions.isCreateSipActionPossibleOnFolder(rm.wrapFolder(record), user);
	}

	// linkTo

	public boolean isAddAuthorizationActionPossible(Record record, User user) {
		return isEditActionPossible(record, user) &&
			   user.has(RMPermissionsTo.MANAGE_FOLDER_AUTHORIZATIONS).on(record) &&
			   user.hasWriteAndDeleteAccess().on(record) &&
			   rmModuleExtensions.isAddAuthorizationActionPossibleOnFolder(rm.wrapFolder(record), user);
	}

	public boolean isShareActionPossible(Record record, User user) {
		Folder folder = rm.wrapFolder(record);
		if (!hasUserWriteAccess(record, user) || !user.has(RMPermissionsTo.SHARE_FOLDER).on(folder) ||
			(folder.getPermissionStatus().isInactive() && !user.has(RMPermissionsTo.SHARE_A_INACTIVE_FOLDER).on(folder)) ||
			(folder.getPermissionStatus().isSemiActive() && !user.has(RMPermissionsTo.SHARE_A_SEMIACTIVE_FOLDER).on(folder)) ||
			(isNotBlank(folder.getLegacyId()) && !user.has(RMPermissionsTo.SHARE_A_IMPORTED_FOLDER).on(folder))) {
			return false;
		}
		return rmModuleExtensions.isShareActionPossibleOnFolder(rm.wrapFolder(record), user);
	}

	public boolean isAddToCartActionPossible(Record record, User user) {
		return hasUserReadAccess(record, user) &&
			   (hasUserPermissionToUseCart(user) || hasUserPermissionToUseMyCart(user)) &&
			   rmModuleExtensions.isAddToCartActionPossibleOnFolder(rm.wrapFolder(record), user);
	}

	public boolean isBorrowActionPossible(Record record, User user) {
		Folder folder = rm.wrapFolder(record);
		if (!hasUserReadAccess(record, user) ||
			(folder.getBorrowed() != null && folder.getBorrowed())) {
			return false;
		} else if (folder.getContainer() != null) {
			ContainerRecord containerRecord = rm.getContainerRecord(folder.getContainer());
			if (containerRecord.getBorrowed() != null && containerRecord.getBorrowed()) {
				return false;
			}
		}
		return user.hasAll(RMPermissionsTo.BORROW_FOLDER, RMPermissionsTo.BORROWING_FOLDER_DIRECTLY).on(folder) &&
			   rmModuleExtensions.isBorrowingActionPossibleOnFolder(folder, user);
	}

	public boolean isReturnActionPossible(Record record, User user) {
		Folder folder = rm.wrapFolder(record);
		boolean hasPermissionToReturnOtherUsersFolder = user.has(RMPermissionsTo.RETURN_OTHER_USERS_FOLDERS).on(folder);
		boolean hasPermissionToReturnOwnFolderDirectly = user.has(RMPermissionsTo.BORROW_FOLDER).on(folder) &&
														 user.has(RMPermissionsTo.BORROWING_FOLDER_DIRECTLY).on(folder);
		if (!user.hasReadAccess().on(folder) ||
			(folder.getBorrowed() == null || !folder.getBorrowed())) {
			return false;
		} else if (!hasPermissionToReturnOtherUsersFolder && !user.getId().equals(folder.getBorrowUserEntered())) {
			return false;
		} else if (!hasPermissionToReturnOwnFolderDirectly && user.getId().equals(folder.getBorrowUserEntered())) {
			return false;
		}
		return rmModuleExtensions.isReturnActionPossibleOnFolder(rm.wrapFolder(record), user);
	}

	public boolean isPrintLabelActionPossible(Record record, User user) {
		Folder folder = rm.wrapFolder(record);
		if (hasUserReadAccess(record, user) && rmModuleExtensions.isPrintLabelActionPossibleOnFolder(folder, user)) {
			if (folder.getPermissionStatus().isInactive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return user.has(RMPermissionsTo.MODIFY_INACTIVE_BORROWED_FOLDER).on(folder) &&
						   user.has(RMPermissionsTo.MODIFY_INACTIVE_FOLDERS).on(folder);
				}
				return user.has(RMPermissionsTo.MODIFY_INACTIVE_FOLDERS).on(folder);
			}
			if (folder.getPermissionStatus().isSemiActive()) {
				if (folder.getBorrowed() != null && folder.getBorrowed()) {
					return user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_BORROWED_FOLDER).on(folder) &&
						   user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_FOLDERS).on(folder);
				}
				return user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_FOLDERS).on(folder);
			}
			return true;
		}
		return false;
	}

	public boolean isGenerateReportActionPossible(Record record, User user) {
		return hasUserWriteAccess(record, user) &&
			   rmModuleExtensions.isGenerateReportActionPossibleOnFolder(rm.wrapFolder(record), user);
	}

	/*
			linkToFolderButton = new LinkButton($("DisplayFolderView.linkToFolder")) {
				@Override
				protected void buttonClick(ClickEvent event) {
					presenter.linkToFolderButtonClicked();
				}
			};
			linkToFolderButton.setVisible(false);
	 */

	private boolean hasUserWriteAccess(Record record, User user) {
		return user.hasWriteAccess().on(record);
	}

	private boolean hasUserReadAccess(Record record, User user) {
		return user.hasReadAccess().on(record);
	}

	private boolean hasUserPermissionToUseCart(User user) {
		return user.has(RMPermissionsTo.USE_GROUP_CART).globally();
	}

	private boolean hasUserPermissionToUseMyCart(User user) {
		return user.has(RMPermissionsTo.USE_MY_CART).globally();
	}

}