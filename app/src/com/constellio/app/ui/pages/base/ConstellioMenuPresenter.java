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
package com.constellio.app.ui.pages.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.constellio.app.modules.rm.ui.builders.UserToVOBuilder;
import com.constellio.app.services.factories.ConstellioFactories;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.entities.UserVO;
import com.constellio.data.utils.TimeProvider;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.users.UserPhotosServices;
import com.constellio.model.services.users.UserPhotosServicesRuntimeException.UserPhotosServicesRuntimeException_UserHasNoPhoto;
import com.constellio.model.services.users.UserServices;

public class ConstellioMenuPresenter implements Serializable {

	private static final String SHOW_PICTURE_STREAM = "ConstellioMenuPresenter-ShowPicture";

	private UserToVOBuilder voBuilder = new UserToVOBuilder();

	private ConstellioMenu constellioMenu;

	private SessionContext sessionContext;

	private String username;

	private transient ConstellioFactories constellioFactories;

	private transient ModelLayerFactory modelLayerFactory;

	private transient UserServices userServices;

	public ConstellioMenuPresenter(ConstellioMenu constellioMenu) {
		this.constellioMenu = constellioMenu;

		constellioFactories = constellioMenu.getConstellioFactories();
		sessionContext = constellioMenu.getSessionContext();
		UserVO userVO = sessionContext.getCurrentUser();
		username = userVO.getUsername();

		initTransientObjects();

		List<String> collections = userServices.getUser(userVO.getUsername()).getCollections();
		constellioMenu.setCollections(collections);
	}

	private void readObject(java.io.ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		initTransientObjects();
	}

	private void initTransientObjects() {
		if (constellioFactories == null) {
			constellioFactories = ConstellioFactories.getInstance();
		}
		modelLayerFactory = constellioFactories.getModelLayerFactory();
		userServices = modelLayerFactory.newUserServices();
	}

	public void homeButtonClicked() {
		constellioMenu.navigateTo().home();
	}

	public void collectionClicked(String newCollection) {
		SessionContext sessionContext = constellioMenu.getSessionContext();
		String currentCollection = sessionContext.getCurrentCollection();
		if (!currentCollection.equals(newCollection)) {
			User newUser = userServices.getUserInCollection(username, newCollection);
			try {
				modelLayerFactory.newRecordServices().update(newUser
						.setLastLogin(TimeProvider.getLocalDateTime())
						.setLastIPAddress(sessionContext.getCurrentUserIPAddress()));

			} catch (RecordServicesException e) {
				throw new RuntimeException(e);
			}
			UserVO newUserVO = voBuilder.build(newUser.getWrappedRecord(), VIEW_MODE.DISPLAY);
			sessionContext.setCurrentCollection(newCollection);
			sessionContext.setCurrentUser(newUserVO);

			constellioMenu.updateUIContent();
			constellioMenu.navigateTo().home();
		}
	}

	public void editProfileButtonClicked(String params) {
		constellioMenu.navigateTo().modifyProfil(params);
	}

	public void preferencesButtonClicked() {
	}

	public boolean isUserManagementButtonVisible() {
		return true;
	}

	public void userManagementButtonClicked() {

	}

	public boolean isAdminModuleButtonVisible() {
		return true;
	}

	public void adminModuleButtonClicked() {

	}

	public boolean isArchivesManagementButtonVisible() {
		return true;
	}

	public void archivesManagementButtonClicked() {

	}

	public boolean isLogsButtonVisible() {
		return true;
	}

	public void logsButtonClicked() {

	}

	public void signOutButtonClicked() {
		SessionContext sessionContext = constellioMenu.getSessionContext();
		ModelLayerFactory modelLayerFactory = ConstellioFactories.getInstance().getModelLayerFactory();
		UserServices userServices = modelLayerFactory.newUserServices();
		User user = userServices.getUserInCollection(
				sessionContext.getCurrentUser().getUsername(), sessionContext.getCurrentCollection());
		modelLayerFactory.newLoggingServices().logout(user);

		sessionContext.setCurrentCollection(null);
		sessionContext.setCurrentUser(null);
		constellioMenu.updateUIContent();
	}

	public InputStream newUserPhotoInputStream() {
		UserVO currentUser = constellioMenu.getSessionContext().getCurrentUser();
		UserPhotosServices photosServices = ConstellioFactories.getInstance().getModelLayerFactory().newUserPhotosServices();

		try {
			return photosServices.getPhotoInputStream(currentUser.getUsername()).create(SHOW_PICTURE_STREAM);
		} catch (UserPhotosServicesRuntimeException_UserHasNoPhoto u) {
			return null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean hasCurrentUserPhoto() {
		UserVO currentUser = constellioMenu.getSessionContext().getCurrentUser();
		UserPhotosServices photosServices = ConstellioFactories.getInstance().getModelLayerFactory().newUserPhotosServices();
		return photosServices.hasPhoto(currentUser.getUsername());
	}

	public String getCollectionCaption(String collectionName) {
		String collectionTitle = constellioFactories.getAppLayerFactory().getCollectionsManager().getCollection(collectionName)
				.getTitle();
		return StringUtils.isNotBlank(collectionTitle) ? collectionTitle : collectionName;
	}

}
