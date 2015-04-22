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
package com.constellio.app.ui.pages.user;

import static com.constellio.app.ui.i18n.i18n.$;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.ui.entities.UserCredentialVO;
import com.constellio.app.ui.framework.builders.UserCredentialToVOBuilder;
import com.constellio.app.ui.pages.base.BasePresenter;
import com.constellio.app.ui.params.ParamUtils;
import com.constellio.app.ui.util.MessageUtils;
import com.constellio.model.entities.security.global.UserCredential;
import com.constellio.model.entities.security.global.UserCredentialStatus;
import com.constellio.model.services.collections.CollectionsListManager;
import com.constellio.model.services.security.authentification.AuthenticationService;
import com.constellio.model.services.users.UserServices;

public class AddEditUserCredentialPresenter extends BasePresenter<AddEditUserCredentialView> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AddEditUserCredentialPresenter.class);
	private transient UserServices userServices;
	private transient AuthenticationService authenticationService;
	private transient CollectionsListManager collectionsListManager;
	private boolean editMode = false;
	private Map<String, String> paramsMap;
	private String username;
	private String breadCrumb;

	public AddEditUserCredentialPresenter(AddEditUserCredentialView view) {
		super(view);
		init();
	}

	private void readObject(java.io.ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		init();
	}

	private void init() {
		userServices = modelLayerFactory.newUserServices();
		collectionsListManager = modelLayerFactory.getCollectionsListManager();
		authenticationService = modelLayerFactory.newAuthenticationService();
	}

	public UserCredentialVO getUserCredentialVO(String username) {
		UserCredential userCredential = null;
		this.username = username;
		if (!username.isEmpty()) {
			editMode = true;
			userCredential = userServices.getUserCredential(username);
		}
		UserCredentialToVOBuilder voBuilder = new UserCredentialToVOBuilder();
		return userCredential != null ? voBuilder.build(userCredential) : new UserCredentialVO();
	}

	public void saveButtonClicked(UserCredentialVO entity) {
		String username = entity.getUsername();

		if (validateEntityInfos(entity, username)) {
			return;
		}
		UserCredential userCredential = toUserCredential(entity);
		try {
			if (!isEditMode() || entity.getPassword() != null && !entity.getPassword().isEmpty()) {
				authenticationService.changePassword(entity.getUsername(), entity.getPassword());
			}
			userServices.addUpdateUserCredential(userCredential);
		} catch (Exception e) {
			view.showErrorMessage(MessageUtils.toMessage(e));
			return;
		}
		paramsMap.put("username", entity.getUsername());
		setupNavigateBackPage();
	}

	private boolean validateEntityInfos(UserCredentialVO entity, String username) {
		if (isEditMode()) {
			if (isUsernameChanged(username)) {
				showErrorMessageView("AddEditUserCredentialView.cannotChangeUsername");
				return true;
			}
		} else {
			if (userExists(username)) {
				showErrorMessageView("AddEditUserCredentialView.usernameAlredyExists");
				return true;
			}
			if (!(entity.getPassword() != null && StringUtils.isNotBlank(entity.getPassword()) && entity.getPassword()
					.equals(entity.getConfirmPassword()))) {
				showErrorMessageView("AddEditUserCredentialView.passwordsFieldsMustBeEquals");
				return true;
			}
		}
		return false;
	}

	void showErrorMessageView(String text) {
		view.showErrorMessage($(text));
	}

	private boolean userExists(String username) {
		try {
			UserCredential userCredential = userServices.getUserCredential(username);
			if (userCredential != null) {
				return true;
			}
		} catch (Exception e) {
			//Ok
			LOGGER.info(e.getMessage(), e);
		}
		return false;
	}

	private boolean isUsernameChanged(String username) {
		if (getUsername() != null && !getUsername().isEmpty() && !getUsername().equals(username)) {
			return true;
		}
		return false;
	}

	UserCredential toUserCredential(UserCredentialVO userCredentialVO) {
		List<String> globalGroups = new ArrayList<>();
		List<String> collections = new ArrayList<>();
		Map<String, LocalDateTime> tokens = new HashMap<>();
		if (userCredentialVO.getGlobalGroups() != null) {
			globalGroups = userCredentialVO.getGlobalGroups();
		}
		if (userCredentialVO.getCollections() != null) {
			collections.addAll(userCredentialVO.getCollections());
		}
		if (userCredentialVO.getTokensMap() != null) {
			tokens = userCredentialVO.getTokensMap();
		}
		UserCredentialStatus status = userCredentialVO.getStatus();
		String domain = userCredentialVO.getDomain();
		UserCredential newUserCredential = new UserCredential(userCredentialVO.getUsername(), userCredentialVO.getFirstName(),
				userCredentialVO.getLastName(), userCredentialVO.getEmail(), userCredentialVO.getServiceKey(),
				userCredentialVO.isSystemAdmin(), globalGroups, collections, tokens, status, domain);
		return newUserCredential;
	}

	public void cancelButtonClicked() {
		setupNavigateBackPage();
	}

	public boolean isEditMode() {
		return editMode;
	}

	public List<String> getAllCollections() {
		return collectionsListManager.getCollections();
	}

	public void setParamsMap(Map<String, String> paramsMap) {
		this.paramsMap = paramsMap;
	}

	public String getUsername() {
		return username;
	}

	public void setBreadCrumb(String breadCrumb) {
		this.breadCrumb = breadCrumb;
	}

	private void setupNavigateBackPage() {
		String viewNames[] = breadCrumb.split("/");
		String backPage = viewNames[viewNames.length - 1];
		breadCrumb = breadCrumb.replace(backPage, "");
		if (breadCrumb.endsWith("/")) {
			breadCrumb = breadCrumb.substring(0, breadCrumb.length() - 1);
		}
		Map<String, Object> newParamsMap = new HashMap<>();
		newParamsMap.putAll(paramsMap);
		String parameters = ParamUtils.addParams(breadCrumb, newParamsMap);
		while (parameters.contains("//")) {
			parameters = parameters.replace("//", "/");
		}
		if (!backPage.endsWith("/") && !parameters.startsWith("/")) {
			backPage += "/";
		}
		view.navigateTo().url(backPage + parameters);
	}

	public boolean canModifyStatus() {
		return (!view.getSessionContext().getCurrentUser().getUsername().equals("admin") && canAndOrModify());
	}

	public boolean canAndOrModify() {
		return userServices.canAddOrModifyUserAndGroup();
	}

	public boolean canModifyPassword() {
		UserCredential userCredential = userServices.getUserCredential(view.getSessionContext().getCurrentUser().getUsername());
		return userServices.canModifyPassword(userCredential);
	}
}