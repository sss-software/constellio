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
package com.constellio.app.ui.pages.management.schemas.type;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.mockito.Mock;

import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.schemas.MetadataSchemasManager;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.MockedFactories;

public class ListSchemaPresenterTest extends ConstellioTest {

	@Mock MetadataSchemasManager metadataSchemasManager;
	@Mock MetadataSchemaVO schemaVO;
	@Mock RecordServices recordServices;
	ListSchemaPresenter presenter;
	MockedFactories mockedFactories;
	@Mock ListSchemaViewImpl view;
	@Mock MetadataSchemaTypes types;

	@Before
	public void setUp() {
		mockedFactories = new MockedFactories();
		presenter = spy(new ListSchemaPresenter(view));
	}

	//@Test
	public void whenEditButtonClickedThenNavigateToEditSchemas()
			throws Exception {

		when(schemaVO.getCode()).thenReturn("zeId");

		presenter.editButtonClicked(schemaVO);

		//verify(view.navigateTo(), times(1)).editSchema("zeId");
	}

	//@Test
	public void whenAddButtonClickedThenNavigateToAddSchemas()
			throws Exception {

		when(schemaVO.getCode()).thenReturn("zeId");

		presenter.editButtonClicked(schemaVO);

		//verify(view.navigateTo(), times(1)).editSchema("zeId");
	}

	//@Test
	public void whenOrderButtonClickedThenNavigateToOrderSchemas()
			throws Exception {

		when(schemaVO.getCode()).thenReturn("zeId");

		presenter.editButtonClicked(schemaVO);

		//verify(view.navigateTo(), times(1)).editSchema("zeId");
	}

	//@Test
	public void whenFormButtonClickedThenNavigateToFormSchemas()
			throws Exception {

		when(schemaVO.getCode()).thenReturn("zeId");

		presenter.editButtonClicked(schemaVO);

		//verify(view.navigateTo(), times(1)).editSchema("zeId");
	}
}
