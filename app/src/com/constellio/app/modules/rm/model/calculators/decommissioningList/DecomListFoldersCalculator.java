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
package com.constellio.app.modules.rm.model.calculators.decommissioningList;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import com.constellio.app.modules.rm.wrappers.DecommissioningList;
import com.constellio.app.modules.rm.wrappers.structures.DecomListFolderDetail;
import com.constellio.model.entities.calculators.CalculatorParameters;
import com.constellio.model.entities.calculators.MetadataValueCalculator;
import com.constellio.model.entities.calculators.dependencies.Dependency;
import com.constellio.model.entities.calculators.dependencies.LocalDependency;
import com.constellio.model.entities.schemas.MetadataValueType;

public class DecomListFoldersCalculator implements MetadataValueCalculator<List<String>> {

	LocalDependency<List<DecomListFolderDetail>> folderDetailsParam = LocalDependency
			.toAStructure(DecommissioningList.FOLDER_DETAILS).whichIsMultivalue();

	@Override
	public List<String> calculate(CalculatorParameters parameters) {
		List<String> ids = new ArrayList<>();
		List<DecomListFolderDetail> details = parameters.get(folderDetailsParam);

		for (DecomListFolderDetail detail : details) {
			ids.add(detail.getFolderId());
		}

		return ids;
	}

	@Override
	public List<String> getDefaultValue() {
		return new ArrayList<>();
	}

	@Override
	public MetadataValueType getReturnType() {
		return MetadataValueType.REFERENCE;
	}

	@Override
	public boolean isMultiValue() {
		return true;
	}

	@Override
	public List<? extends Dependency> getDependencies() {
		return asList(folderDetailsParam);
	}
}
