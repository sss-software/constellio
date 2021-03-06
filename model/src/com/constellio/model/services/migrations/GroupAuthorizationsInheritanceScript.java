package com.constellio.model.services.migrations;

import com.constellio.model.entities.configs.SystemConfigurationScript;
import com.constellio.model.entities.enums.GroupAuthorizationsInheritance;
import com.constellio.model.frameworks.validation.ValidationErrors;
import com.constellio.model.services.factories.ModelLayerFactory;

public class GroupAuthorizationsInheritanceScript implements SystemConfigurationScript<GroupAuthorizationsInheritance> {
	@Override
	public void onNewCollection(GroupAuthorizationsInheritance newValue, String collection,
								ModelLayerFactory modelLayerFactory) {

	}

	@Override
	public void validate(GroupAuthorizationsInheritance newValue, ValidationErrors errors) {

	}

	@Override
	public void onValueChanged(GroupAuthorizationsInheritance previousValue, GroupAuthorizationsInheritance newValue,
							   ModelLayerFactory modelLayerFactory) {

	}

	@Override
	public void onValueChanged(GroupAuthorizationsInheritance previousValue, GroupAuthorizationsInheritance newValue,
							   ModelLayerFactory modelLayerFactory, String collection) {
		modelLayerFactory.getSecurityModelCache().invalidate(collection);
	}
}
