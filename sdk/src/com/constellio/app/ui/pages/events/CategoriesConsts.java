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
package com.constellio.app.ui.pages.events;

import static com.constellio.app.ui.i18n.i18n.$;

public final class CategoriesConsts {
	public static final String USERS_AND_GROUPS_ADD_OR_REMOVE_CATEGORY_TITLE = $("ListEventsView.usersAndGroupsAddOrRemoveEvents");
	public static final String SYSTEM_USAGE_CATEGORY_TITLE = $("ListEventsView.systemUsage");
	public static final String FOLDERS_AND_DOCUMENTS_CREATION_CATEGORY_TITLE = $("ListEventsView.foldersAndDocumentsCreation");
	public static final String FOLDERS_AND_DOCUMENTS_MODIFICATION_CATEGORY_TITLE = $("ListEventsView.foldersAndDocumentsModification");
	public static final String FOLDERS_AND_DOCUMENTS_DELETION_CATEGORY_TITLE = $("ListEventsView.foldersAndDocumentsDeletion");
	public static final String CURRENTLY_BORROWED_DOCUMENTS_CATEGORY_TITLE = $("ListEventsView.currentlyBorrowedDocuments");
	public static final String DOCUMENTS_BORROW_OR_RETURN_CATEGORY_TITLE = $("ListEventsView.documentsBorrowOrReturn");
	public static final String BY_ADMINISTRATIVE_UNIT_CATEGORY_TITLE = $("ListEventsView.eventsByAdministrativeUnit");
	public static final String BY_FOLDER_CATEGORY_TITLE = $("ListEventsView.eventsByFolder");
	public static final String BY_USER_CATEGORY_TITLE = $("ListEventsView.eventsByUser");
	public static final String DECOMMISSIONING_CATEGORY_TITLE = $("ListEventsView.decommissioningEvents");

//indices
	public static final int OPENED_SESSIONS_INDEX_IN_SYSTEM_USAGE_CATEGORY = 0;

	public static final int ADD_USER_STAT_INDEX_IN_USERS_AND_GROUPS_ADD_OR_REMOVE_CATEGORY = 0;
	public static final int REMOVE_USER_STAT_INDEX_IN_USERS_AND_GROUPS_ADD_OR_REMOVE_CATEGORY = 1;
	public static final int ADD_GROUP_STAT_INDEX_IN_USERS_AND_GROUPS_ADD_OR_REMOVE_CATEGORY = 2;
	public static final int REMOVE_GROUP_STAT_INDEX_IN_USERS_AND_GROUPS_ADD_OR_REMOVE_CATEGORY = 3;

	public static final int FOLDERS_CREATION_IN_FOLDERS_AND_DOCUMENTS_CREATION_CATEGORY = 0;
	public static final int DOCUMENTS_CREATION_IN_FOLDERS_AND_DOCUMENTS_CREATION_CATEGORY = 1;

	public static final int FOLDERS_MODIFICATION_IN_FOLDERS_AND_DOCUMENTS_MODIFICATION_CATEGORY = 0;
	public static final int DOCUMENTS_MODIFICATION_IN_FOLDERS_AND_DOCUMENTS_MODIFICATION_CATEGORY = 1;

	public static final int FOLDERS_DELETION_IN_FOLDERS_AND_DOCUMENTS_DELETION_CATEGORY = 0;
	public static final int DOCUMENTS_DELETION_IN_FOLDERS_AND_DOCUMENTS_DELETION_CATEGORY = 1;

	public static final int DOCUMENT_CURRENT_BORROW_STAT_INDEX_IN_CURRENTLY_BORROWED_DOCUMENTS_CATEGORY = 0;

	public static final int DOCUMENTS_BORROW_IN_DOCUMENTS_BORROW_OR_RETURN_CATEGORY = 0;
	public static final int DOCUMENTS_RETURN_DOCUMENTS_BORROW_OR_RETURN_CATEGORY = 1;

	public static final int FOLDER_CREATE_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 0;
	public static final int FOLDER_MODIFY_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 1;
	public static final int FOLDER_DELETE_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 2;
	public static final int DOCUMENT_VIEW_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 3;
	public static final int DOCUMENT_CREATE_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 4;
	public static final int DOCUMENT_MODIFY_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 5;
	public static final int DOCUMENT_DELETE_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 6;
	public static final int ADD_USER_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 7;
	public static final int ADD_MODIFY_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 8;
	public static final int REMOVE_USER_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 9;
	public static final int GRANT_PERMISSION_FOLDER_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 10;
	public static final int MODIFY_PERMISSION_FOLDER_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 11;
	public static final int DELETE_PERMISSION_FOLDER_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 12;
	public static final int GRANT_PERMISSION_DOCUMENT_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 13;
	public static final int MODIFY_PERMISSION_DOCUMENT_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 14;
	public static final int DELETE_PERMISSION_DOCUMENT_STAT_INDEX_IN_BY_ADMINISTRATIVE_UNIT_CATEGORY = 15;

	public static final int FOLDER_CREATE_STAT_INDEX_IN_BY_FOLDER_CATEGORY = 0;
	public static final int FOLDER_MODIFY_STAT_INDEX_IN_BY_FOLDER_CATEGORY = 1;
	public static final int FOLDER_VIEW_STAT_INDEX_IN_BY_FOLDER_CATEGORY = 2;
	public static final int GRANT_PERMISSION_FOLDER_STAT_INDEX_IN_BY_FOLDER_CATEGORY = 3;
	public static final int MODIFY_PERMISSION_FOLDER_STAT_INDEX_IN_BY_FOLDER_CATEGORY = 4;
	public static final int DELETE_PERMISSION_FOLDER_STAT_INDEX_IN_BY_FOLDER_CATEGORY = 5;

	public static final int OPEN_SESSION_STAT_INDEX_IN_BY_USER_CATEGORY = 0;
	public static final int FOLDER_VIEW_STAT_INDEX_IN_BY_USER_CATEGORY = 1;
	public static final int FOLDER_CREATE_STAT_INDEX_IN_BY_USER_CATEGORY = 2;
	public static final int FOLDER_MODIFY_STAT_INDEX_IN_BY_USER_CATEGORY = 3;
	public static final int FOLDER_DELETE_STAT_INDEX_IN_BY_USER_CATEGORY = 4;
	public static final int DOCUMENT_VIEW_STAT_INDEX_IN_BY_USER_CATEGORY = 5;
	public static final int DOCUMENT_BORROW_STAT_INDEX_IN_BY_USER_CATEGORY = 6;
	public static final int DOCUMENT_CREATE_STAT_INDEX_IN_BY_USER_CATEGORY = 7;
	public static final int DOCUMENT_MODIFY_STAT_INDEX_IN_BY_USER_CATEGORY = 8;
	public static final int DOCUMENT_DELETE_STAT_INDEX_IN_BY_USER_CATEGORY = 9;
	public static final int ADD_USER_STAT_INDEX_IN_BY_USER_CATEGORY = 10;
	public static final int REMOVE_USER_STAT_INDEX_IN_BY_USER_CATEGORY = 11;
	public static final int ADD_GROUP_STAT_INDEX_IN_BY_USER_CATEGORY = 12;
	public static final int REMOVE_GROUP_STAT_INDEX_IN_BY_USER_CATEGORY = 13;
	public static final int GRANT_PERMISSION_FOLDER_STAT_INDEX_IN_BY_USER_CATEGORY = 14;
	public static final int MODIFY_PERMISSION_FOLDER_STAT_INDEX_IN_BY_USER_CATEGORY = 15;
	public static final int DELETE_PERMISSION_FOLDER_STAT_INDEX_IN_BY_USER_CATEGORY = 16;
	public static final int GRANT_PERMISSION_DOCUMENT_STAT_INDEX_IN_BY_USER_CATEGORY = 17;
	public static final int MODIFY_PERMISSION_DOCUMENT_STAT_INDEX_IN_BY_USER_CATEGORY = 18;
	public static final int DELETE_PERMISSION_DOCUMENT_STAT_INDEX_IN_BY_USER_CATEGORY = 19;

	public static final int FOLDER_RELOCATION_IN_DECOMMISSIONING_CATEGORY = 0;
	public static final int FOLDER_DEPOSIT_IN_DECOMMISSIONING_CATEGORY = 1;
	public static final int FOLDER_DESTRUCTION_IN_DECOMMISSIONING_CATEGORY = 2;

}
