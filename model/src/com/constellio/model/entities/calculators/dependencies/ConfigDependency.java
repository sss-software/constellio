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

import com.constellio.model.entities.configs.SystemConfiguration;
import com.constellio.model.entities.schemas.MetadataValueType;

public class ConfigDependency<T> implements Dependency {

	private SystemConfiguration configuration;

	public ConfigDependency(SystemConfiguration configuration) {
		this.configuration = configuration;
	}

	public SystemConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public MetadataValueType getReturnType() {
		return null;
	}

	@Override
	public boolean isMultivalue() {
		return false;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public String getLocalMetadataCode() {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ConfigDependency)) {
			return false;
		}

		ConfigDependency that = (ConfigDependency) o;

		if (configuration != null ? !configuration.equals(that.configuration) : that.configuration != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return configuration.hashCode();
	}

	@Override
	public String toString() {
		return "ConfigDependency{" +
				"configuration=" + configuration.getCode() +
				'}';
	}
}
