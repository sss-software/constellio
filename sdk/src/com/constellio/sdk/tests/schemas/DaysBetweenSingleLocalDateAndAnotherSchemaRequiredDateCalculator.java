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
package com.constellio.sdk.tests.schemas;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.Days;
import org.joda.time.LocalDateTime;

import com.constellio.model.entities.calculators.CalculatorParameters;
import com.constellio.model.entities.calculators.MetadataValueCalculator;
import com.constellio.model.entities.calculators.dependencies.Dependency;
import com.constellio.model.entities.calculators.dependencies.LocalDependency;
import com.constellio.model.entities.calculators.dependencies.ReferenceDependency;
import com.constellio.model.entities.schemas.MetadataValueType;

public class DaysBetweenSingleLocalDateAndAnotherSchemaRequiredDateCalculator implements MetadataValueCalculator<Double> {

	public static AtomicInteger invokationCounter = new AtomicInteger();

	ReferenceDependency<LocalDateTime> anotherSchemaDateParam = ReferenceDependency.toADateTime("dateRef", "dateMeta")
			.whichIsRequired();
	LocalDependency<LocalDateTime> dateParam = LocalDependency.toADateTime("dateTimeMetadata").whichIsRequired();

	@Override
	public Double calculate(CalculatorParameters values) {
		invokationCounter.incrementAndGet();
		LocalDateTime anotherSchemaDate = values.get(anotherSchemaDateParam);
		LocalDateTime date = values.get(dateParam);
		return Math.abs((double) Days.daysBetween(date, anotherSchemaDate).getDays());
	}

	@Override
	public Double getDefaultValue() {
		return -1.0;
	}

	@Override
	public MetadataValueType getReturnType() {
		return MetadataValueType.NUMBER;
	}

	@Override
	public List<? extends Dependency> getDependencies() {
		return Arrays.asList(anotherSchemaDateParam, dateParam);
	}

	@Override
	public boolean isMultiValue() {
		return false;
	}

}
