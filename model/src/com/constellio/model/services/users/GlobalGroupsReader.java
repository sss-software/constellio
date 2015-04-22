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
package com.constellio.model.services.users;

import com.constellio.model.entities.security.global.GlobalGroup;
import com.constellio.model.entities.security.global.GlobalGroupStatus;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalGroupsReader {

	private static final String USERS_AUTOMATICALLY_ADDED_TO_COLLECTIONS = "usersAutomaticallyAddedToCollections";
	private static final String PARENT = "parent";
	private static final String CODE = "code";
	private static final String NAME = "name";
	public static final String STATUS = "status";
	Document document;

	public GlobalGroupsReader(Document document) {
		this.document = document;
	}

	public Map<String, GlobalGroup> readAll() {
		GlobalGroup globalGroup;
		Map<String, GlobalGroup> globalGroups = new HashMap<>();
		Element globalGroupsElements = document.getRootElement();
		for (Element globalGroupElement : globalGroupsElements.getChildren()) {
			globalGroup = createGlobalGroupObject(globalGroupElement);
			globalGroups.put(globalGroup.getCode(), globalGroup);
		}
		return globalGroups;
	}

	private GlobalGroup createGlobalGroupObject(Element globalGroupElement) {
		GlobalGroup globalGroup;
		Element usersAutomaticallyAddedToCollectionsElements = globalGroupElement
				.getChild(USERS_AUTOMATICALLY_ADDED_TO_COLLECTIONS);
		List<String> usersAutomaticallyAddedToCollections = new ArrayList<>();
		for (Element usersAutomaticallyAddedToCollectionsElement : usersAutomaticallyAddedToCollectionsElements.getChildren()) {
			usersAutomaticallyAddedToCollections.add(usersAutomaticallyAddedToCollectionsElement.getText());
		}
		String name = globalGroupElement.getChildText(NAME);
		String parent = globalGroupElement.getChildText(PARENT);
		if (StringUtils.isBlank(parent)) {
			parent = null;
		}
		String code = globalGroupElement.getAttributeValue(CODE);
		String statusStr = globalGroupElement.getChildText(STATUS);
		GlobalGroupStatus status;
		if (statusStr != null) {
			status = GlobalGroupStatus.valueOf(statusStr);
		} else {
			status = GlobalGroupStatus.ACTIVE;
		}

		globalGroup = new GlobalGroup(code, name, usersAutomaticallyAddedToCollections, parent, status);
		return globalGroup;
	}
}
