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
package com.constellio.model.services.search.query.logical;

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.constellio.model.entities.schemas.DataStoreField;
import com.constellio.model.services.search.query.logical.criteria.CompositeLogicalSearchValueOperator;

public abstract class LogicalSearchValueCondition {

	public LogicalSearchValueCondition and(LogicalSearchValueCondition condition) {

		return new CompositeLogicalSearchValueOperator(LogicalOperator.AND, Arrays.asList(this, condition));
	}

	public LogicalSearchValueCondition or(LogicalSearchValueCondition condition) {
		return new CompositeLogicalSearchValueOperator(LogicalOperator.OR, Arrays.asList(this, condition));
	}

	public abstract boolean isValidFor(DataStoreField metadata);

	public abstract String getSolrQuery(DataStoreField metadata);

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

}
