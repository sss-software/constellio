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
package com.constellio.app.modules.rm.ui.pages.containers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.constellio.app.modules.rm.RMTestRecords;
import com.constellio.app.modules.rm.wrappers.AdministrativeUnit;
import com.constellio.app.modules.rm.wrappers.FilingSpace;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.UserVO;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.records.RecordServices;
import com.constellio.sdk.tests.ConstellioTest;

public class ContainersInAdministrativeUnitPresenterAcceptTest extends ConstellioTest {

	RecordServices recordServices;
	RMTestRecords records;
	@Mock ContainersInAdministrativeUnitView view;
	@Mock SessionContext sessionContext;
	@Mock UserVO currentUser;
	ContainersInAdministrativeUnitPresenter presenter;

	@Before
	public void setUp()
			throws Exception {
		givenCollectionWithTitle(zeCollection, "Collection de test").withConstellioRMModule().withAllTestUsers();

		recordServices = getModelLayerFactory().newRecordServices();

		records = new RMTestRecords(zeCollection).setup(getModelLayerFactory()).withFoldersAndContainersOfEveryStatus()
				.withEvents();

		when(view.getConstellioFactories()).thenReturn(getConstellioFactories());
		when(view.getCollection()).thenReturn(zeCollection);
		when(view.getSessionContext()).thenReturn(sessionContext);
		when(sessionContext.getCurrentCollection()).thenReturn(zeCollection);
		when(sessionContext.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getUsername()).thenReturn(chuckNorris);

		presenter = new ContainersInAdministrativeUnitPresenter(view);
	}

	@Test
	public void givenAdminUnit10InTransferNoStorageSpaceWhenGettingDataProvidersThenContainsRightData()
			throws Exception {
		presenter.forParams(ContainersByAdministrativeUnitsPresenter.TAB_TRANSFER_NO_STORAGE_SPACE + "/" + records.unitId_10);

		RecordVODataProvider childAdminUnitsProvider = presenter.getChildrenAdminUnitsDataProvider();
		RecordVODataProvider filingSpacesProvider = presenter.getFilingSpacesDataProvider();

		assertThat(childAdminUnitsProvider.getSchema().getCode()).isEqualTo(AdministrativeUnit.DEFAULT_SCHEMA);
		assertThat(recordIdsFrom(childAdminUnitsProvider)).containsOnly(records.unitId_11, records.unitId_12);
		assertThat(filingSpacesProvider.getSchema().getCode()).isEqualTo(FilingSpace.DEFAULT_SCHEMA);
		assertThat(recordIdsFrom(filingSpacesProvider)).containsOnly(records.filingId_A);
	}

	@Test
	public void givenAdminUnit10InTransferNoStorageSpaceWhenGettingAdminUnitThenAdminUnit10Returned()
			throws Exception {
		presenter.forParams(ContainersByAdministrativeUnitsPresenter.TAB_TRANSFER_NO_STORAGE_SPACE + "/" + records.unitId_10);

		RecordVO adminUnit = presenter.getAdministrativeUnit();

		assertThat(adminUnit.getId()).isEqualTo(records.unitId_10);
		assertThat(adminUnit.get(AdministrativeUnit.CODE)).isEqualTo(records.getUnit10().getCode());
		assertThat(adminUnit.get(Schemas.TITLE.getLocalCode())).isEqualTo(records.getUnit10().getTitle());
		assertThat(adminUnit.get(Schemas.CREATED_ON.getLocalCode())).isEqualTo(records.getUnit10().getCreatedOn());
	}

	@Test
	public void givenAdminUnit10InTransferWithStorageSpaceWhenGettingDataProvidersThenContainsRightData()
			throws Exception {
		presenter.forParams(ContainersByAdministrativeUnitsPresenter.TAB_TRANSFER_WITH_STORAGE_SPACE + "/" + records.unitId_10);

		RecordVODataProvider childAdminUnitsProvider = presenter.getChildrenAdminUnitsDataProvider();
		RecordVODataProvider filingSpacesProvider = presenter.getFilingSpacesDataProvider();

		assertThat(childAdminUnitsProvider.getSchema().getCode()).isEqualTo(AdministrativeUnit.DEFAULT_SCHEMA);
		assertThat(recordIdsFrom(childAdminUnitsProvider)).containsOnly(records.unitId_11, records.unitId_12);
		assertThat(filingSpacesProvider.getSchema().getCode()).isEqualTo(FilingSpace.DEFAULT_SCHEMA);
		assertThat(recordIdsFrom(filingSpacesProvider)).containsOnly(records.filingId_A);
	}

	@Test
	public void givenAdminUnit10InDepositNoStorageSpaceWhenGettingDataProvidersThenContainsRightData()
			throws Exception {
		presenter.forParams(ContainersByAdministrativeUnitsPresenter.TAB_DEPOSIT_NO_STORAGE_SPACE + "/" + records.unitId_10);

		RecordVODataProvider childAdminUnitsProvider = presenter.getChildrenAdminUnitsDataProvider();
		RecordVODataProvider filingSpacesProvider = presenter.getFilingSpacesDataProvider();

		assertThat(childAdminUnitsProvider.getSchema().getCode()).isEqualTo(AdministrativeUnit.DEFAULT_SCHEMA);
		assertThat(recordIdsFrom(childAdminUnitsProvider)).containsOnly(records.unitId_11, records.unitId_12);
		assertThat(filingSpacesProvider.getSchema().getCode()).isEqualTo(FilingSpace.DEFAULT_SCHEMA);
		assertThat(recordIdsFrom(filingSpacesProvider)).containsOnly(records.filingId_A);
	}

	@Test
	public void givenAdminUnit10InDepositWithStorageSpaceWhenGettingDataProvidersThenContainsRightData()
			throws Exception {
		presenter.forParams(ContainersByAdministrativeUnitsPresenter.TAB_DEPOSIT_WITH_STORAGE_SPACE + "/" + records.unitId_10);

		RecordVODataProvider childAdminUnitsProvider = presenter.getChildrenAdminUnitsDataProvider();
		RecordVODataProvider filingSpacesProvider = presenter.getFilingSpacesDataProvider();

		assertThat(childAdminUnitsProvider.getSchema().getCode()).isEqualTo(AdministrativeUnit.DEFAULT_SCHEMA);
		assertThat(recordIdsFrom(childAdminUnitsProvider)).containsOnly(records.unitId_11, records.unitId_12);
		assertThat(filingSpacesProvider.getSchema().getCode()).isEqualTo(FilingSpace.DEFAULT_SCHEMA);
		assertThat(recordIdsFrom(filingSpacesProvider)).containsOnly(records.filingId_A);
	}

	private List<String> recordIdsFrom(RecordVODataProvider dataProvider) {
		List<String> IDs = new ArrayList<>();
		for (RecordVO recordVO : dataProvider.listRecordVOs(0, dataProvider.size())) {
			IDs.add(recordVO.getId());
		}
		return IDs;
	}
}
