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
package com.constellio.app.modules.rm.model.calculators;

import java.util.List;

import org.joda.time.LocalDate;

import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.model.entities.calculators.MetadataValueCalculator;
import com.constellio.model.entities.calculators.dependencies.LocalDependency;

public class FolderExpectedDestructionDateCalculator
		extends AbstractFolderExpectedDateCalculator
		implements MetadataValueCalculator<LocalDate> {

	LocalDependency<List<LocalDate>> destructionDatesParam = LocalDependency
			.toADate(Folder.COPY_RULES_EXPECTED_DESTRUCTION_DATES).whichIsMultivalue();

	@Override
	LocalDependency<List<LocalDate>> getDatesDependency() {
		return destructionDatesParam;
	}
}