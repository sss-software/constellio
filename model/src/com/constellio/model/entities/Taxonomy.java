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
package com.constellio.model.entities;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Taxonomy {

	private final String code;

	private final String title;

	private final List<String> schemaTypes;

	private final boolean visibleInHomePage;

	private final List<String> userIds;

	private final List<String> groupIds;

	private final String collection;

	public Taxonomy(String code, String title, String collection, String taxonomySchemaType) {
		this(code, title, collection, false, new ArrayList<String>(), new ArrayList<String>(), asList(taxonomySchemaType));
	}

	public Taxonomy(String code, String title, String collection, boolean visibleInHomePage,
			List<String> userIds, List<String> groupIds, String taxonomySchemaType) {
		this(code, title, collection, visibleInHomePage, userIds, groupIds, asList(taxonomySchemaType));
	}

	public Taxonomy(String code, String title, String collection, boolean visibleInHomePage,
			List<String> userIds, List<String> groupIds, List<String> taxonomySchemaTypes) {
		this.code = code;
		this.title = title;
		this.collection = collection;
		this.visibleInHomePage = visibleInHomePage;
		this.userIds = Collections.unmodifiableList(userIds);
		this.groupIds = Collections.unmodifiableList(groupIds);
		this.schemaTypes = Collections.unmodifiableList(taxonomySchemaTypes);

	}

	public String getCollection() {
		return collection;
	}

	public String getCode() {
		return code;
	}

	public String getTitle() {
		return title;
	}

	public List<String> getSchemaTypes() {
		return schemaTypes;
	}

	public boolean isVisibleInHomePage() {
		return visibleInHomePage;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public List<String> getGroupIds() {
		return groupIds;
	}

	@Override
	public String toString() {
		return code;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public Taxonomy withTitle(String title) {
		return new Taxonomy(code, title, collection, visibleInHomePage, userIds, groupIds, schemaTypes);
	}

	public Taxonomy withVisibleInHomeFlag(boolean visibleInHomePage) {
		return new Taxonomy(code, title, collection, visibleInHomePage, userIds, groupIds, schemaTypes);
	}

	public Taxonomy withUserIds(List<String> userIds) {
		return new Taxonomy(code, title, collection, visibleInHomePage, userIds, groupIds, schemaTypes);
	}

	public Taxonomy withGroupIds(List<String> groupIds) {
		return new Taxonomy(code, title, collection, visibleInHomePage, userIds, groupIds, schemaTypes);
	}

	public static Taxonomy createHiddenInHomePage(String code, String title, String collection,
			String taxonomySchemaType) {
		return new Taxonomy(code, title, collection, false, new ArrayList<String>(), new ArrayList<String>(),
				asList(taxonomySchemaType));
	}

	public static Taxonomy createHiddenInHomePage(String code, String title, String collection,
			List<String> taxonomySchemaTypes) {
		return new Taxonomy(code, title, collection, false, new ArrayList<String>(), new ArrayList<String>(),
				taxonomySchemaTypes);
	}

	public static Taxonomy createPublic(String code, String title, String collection, List<String> taxonomySchemaTypes) {
		return new Taxonomy(code, title, collection, true, new ArrayList<String>(), new ArrayList<String>(), taxonomySchemaTypes);
	}

	public static Taxonomy createPublic(String code, String title, String collection, String taxonomySchemaType) {
		return new Taxonomy(code, title, collection, true, new ArrayList<String>(), new ArrayList<String>(),
				asList(taxonomySchemaType));
	}

	public static Taxonomy createHomeTaxonomyForGroups(String code, String title, String collection, String taxonomySchemaType,
			List<String> groupIds) {
		return new Taxonomy(code, title, collection, true, new ArrayList<String>(), groupIds, asList(taxonomySchemaType));
	}

	public static Taxonomy createPublic(String code, String title, String collection, List<String> userIds, List<String> groupIds,
			List<String> taxonomySchemaTypes, boolean isVisibleInHomePage) {
		return new Taxonomy(code, title, collection, isVisibleInHomePage, userIds, groupIds, taxonomySchemaTypes);
	}
}
