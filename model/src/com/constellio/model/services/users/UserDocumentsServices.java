package com.constellio.model.services.users;

import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.configs.SystemConfigurationsManager;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.migrations.ConstellioEIMConfigs;

public class UserDocumentsServices {

	private SystemConfigurationsManager systemConfigurationsManager;
	private UserServices userServices;

	public UserDocumentsServices(ModelLayerFactory modelLayerFactory) {
		systemConfigurationsManager = modelLayerFactory.getSystemConfigurationsManager();
		userServices = modelLayerFactory.newUserServices();
	}

	public boolean isSpaceLimitReached(String username, String collection, long userDocumentSizeInBytes) {
		return isQuotaSpaceConfigActivated() &&
			   convertToMegaByte(userDocumentSizeInBytes) > getAvailableSpaceInMegaBytes(username, collection);
	}

	public double getAvailableSpaceInMegaBytes(String username, String collection) {
		if (!isQuotaSpaceConfigActivated()) {
			return -1;
		}

		User user = userServices.getUserInCollection(username, collection);
		double usedSpace = user.getUserDocumentSizeSum();
		double availableSpace = getSpaceQuota() - convertToMegaByte(usedSpace);
		return Math.max(0, availableSpace);
	}

	public boolean isQuotaSpaceConfigActivated() {
		return getSpaceQuota() >= 0;
	}

	private int getSpaceQuota() {
		ConstellioEIMConfigs configs = new ConstellioEIMConfigs(systemConfigurationsManager);
		return configs.getSpaceQuotaForUserDocuments();
	}

	private double convertToMegaByte(double valueInMegaBytes) {
		return valueInMegaBytes * Math.pow(10, -6);
	}

}
