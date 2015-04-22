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
package com.constellio.app.modules.rm.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.constellio.app.modules.rm.RMTestRecords;
import com.constellio.app.modules.rm.constants.RMTaxonomies;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.model.services.records.RecordServices;
import com.constellio.sdk.tests.ConstellioTest;

public class DocumentAcceptanceTest extends ConstellioTest {

	RMSchemasRecordsServices schemas;
	RMTestRecords records;
	RecordServices recordServices;

	@Before
	public void setUp()
			throws Exception {
		givenCollection(zeCollection).withConstellioRMModule();
		assertThat(getModelLayerFactory().getTaxonomiesManager().getPrincipalTaxonomy(zeCollection).getCode())
				.isEqualTo(RMTaxonomies.ADMINISTRATIVE_UNITS);

		schemas = new RMSchemasRecordsServices(zeCollection, getModelLayerFactory());
		records = new RMTestRecords(zeCollection).setup(getModelLayerFactory()).withFoldersAndContainersOfEveryStatus();
		recordServices = getModelLayerFactory().newRecordServices();

	}

	@Test
	public void whenCreatingADocumentWithoutDescriptionThenOK()
			throws Exception {

		Document document = schemas.newDocument().setTitle("My document").setDescription("test").setFolder(records.folder_A03);
		recordServices.add(document);
		document.setDescription(null).setTitle("Z");
		recordServices.update(document);

	}
}
