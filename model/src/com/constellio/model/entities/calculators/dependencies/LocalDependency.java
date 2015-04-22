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
package com.constellio.model.entities.calculators.dependencies;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.constellio.model.entities.schemas.MetadataValueType;

public class LocalDependency<T> implements Dependency {

	final String metadataCode;

	final boolean multivalue;
	final boolean required;
	final MetadataValueType returnType;

	private LocalDependency(String metadataCode, boolean required, boolean multivalue, MetadataValueType returnType) {
		super();
		this.metadataCode = metadataCode;
		this.required = required;
		this.multivalue = multivalue;
		this.returnType = returnType;
	}

	public <Z> LocalDependency<Z> whichIsRequired() {
		return new LocalDependency<>(metadataCode, true, multivalue, returnType);
	}

	public <Z> LocalDependency<List<Z>> whichIsMultivalue() {
		return new LocalDependency<>(metadataCode, required, true, returnType);
	}

	public static LocalDependency<String> toAString(String metadataCode) {
		return new LocalDependency<>(metadataCode, false, false, MetadataValueType.STRING);
	}

	public static <T> LocalDependency<T> toAnEnum(String metadataCode) {
		return new LocalDependency<>(metadataCode, false, false, MetadataValueType.ENUM);
	}

	public static LocalDependency<List<String>> toAStringList(String metadataCode) {
		return new LocalDependency<>(metadataCode, false, true, MetadataValueType.STRING);
	}

	public static LocalDependency<List<String>> toARequiredStringList(String metadataCode) {
		return new LocalDependency<>(metadataCode, true, true, MetadataValueType.STRING);
	}

	public static LocalDependency<String> toAReference(String metadataCode) {
		return new LocalDependency<>(metadataCode, false, false, MetadataValueType.REFERENCE);
	}

	public static LocalDependency<Boolean> toABoolean(String metadataCode) {
		return new LocalDependency<>(metadataCode, false, false, MetadataValueType.BOOLEAN);
	}

	public static LocalDependency<Double> toANumber(String metadataCode) {
		return new LocalDependency<>(metadataCode, false, false, MetadataValueType.NUMBER);
	}

	public static LocalDependency<LocalDate> toADate(String metadataCode) {
		return new LocalDependency<>(metadataCode, false, false, MetadataValueType.DATE);
	}

	public static LocalDependency<LocalDateTime> toADateTime(String metadataCode) {
		return new LocalDependency<>(metadataCode, false, false, MetadataValueType.DATE_TIME);
	}

	public static <T> LocalDependency<T> toAStructure(String metadataCode) {
		return new LocalDependency<>(metadataCode, false, false, MetadataValueType.STRUCTURE);
	}

	@Override
	public String getLocalMetadataCode() {
		return metadataCode;
	}

	@Override
	public MetadataValueType getReturnType() {
		return returnType;
	}

	@Override
	public boolean isRequired() {
		return required;
	}

	@Override
	public boolean isMultivalue() {
		return multivalue;
	}

	@Override
	public int hashCode() {
		return metadataCode.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return "LocalDependency{" +
				"metadataCode='" + metadataCode + '\'' +
				'}';
	}
}
