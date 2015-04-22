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
package com.constellio.app.ui.pages.management.valueDomains;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.constellio.app.modules.rm.services.ValueListServices;
import com.constellio.app.ui.application.ConstellioNavigator;
import com.constellio.app.ui.entities.MetadataSchemaTypeVO;
import com.constellio.app.ui.framework.builders.MetadataSchemaTypeToVOBuilder;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.FakeSessionContext;
import com.constellio.sdk.tests.MockedFactories;

public class ListValueDomainPresenterTest extends ConstellioTest {

	@Mock ListValueDomainViewImpl view;
	@Mock ConstellioNavigator navigator;
	@Mock ValueListServices valueListServices;
	@Mock MetadataSchemaType valueDomainType1;
	@Mock MetadataSchemaTypeToVOBuilder metadataSchemaTypeToVOBuilder;
	@Mock MetadataSchemaTypeVO metadataSchemaTypeVO;
	ListValueDomainPresenter presenter;
	String newValueDomainTitle;
	MockedFactories mockedFactories = new MockedFactories();

	@Before
	public void setUp()
			throws Exception {

		when(view.getConstellioFactories()).thenReturn(mockedFactories.getConstellioFactories());
		when(view.getSessionContext()).thenReturn(FakeSessionContext.dakotaInCollection(zeCollection));
		when(view.navigateTo()).thenReturn(navigator);

		newValueDomainTitle = "new value domain";
		when(valueDomainType1.getLabel()).thenReturn(newValueDomainTitle);

		presenter = spy(new ListValueDomainPresenter(view));

	}

	@Test
	public void whenValueDomainCreationRequestedThenCreateIt()
			throws Exception {

		doReturn(valueListServices).when(presenter).valueListServices();

		presenter.valueDomainCreationRequested(newValueDomainTitle);

		verify(valueListServices).createValueDomain(newValueDomainTitle);
		verify(view).refreshTable();
	}

	@Test
	public void givenExistentTitleWhenValueDomainCreationRequestedThenDoNotCreateIt()
			throws Exception {

		List<MetadataSchemaType> existentMetadataSchemaTypes = new ArrayList<>();
		existentMetadataSchemaTypes.add(valueDomainType1);
		doReturn(valueListServices).when(presenter).valueListServices();
		doReturn(existentMetadataSchemaTypes).when(valueListServices).getValueDomainTypes();
		when(presenter.newMetadataSchemaTypeToVOBuilder()).thenReturn(metadataSchemaTypeToVOBuilder);
		when(metadataSchemaTypeToVOBuilder.build(valueDomainType1)).thenReturn(metadataSchemaTypeVO);

		presenter.valueDomainCreationRequested(newValueDomainTitle);

		verify(valueListServices, never()).createTaxonomy(newValueDomainTitle);
		verify(view, never()).refreshTable();
	}

	@Test
	public void givenEmptyTitleWhenTaxonomyCreationRequestedThenDoNotCreateIt()
			throws Exception {

		presenter.valueDomainCreationRequested(" ");

		verify(valueListServices, never()).createTaxonomy(newValueDomainTitle);
		verify(view, never()).refreshTable();
	}

	@Test
	public void givenExistentTitleWithSpacesWhenValueDomainCreationRequestedThenDoNotCreateIt()
			throws Exception {

		List<MetadataSchemaType> existentMetadataSchemaTypes = new ArrayList<>();
		existentMetadataSchemaTypes.add(valueDomainType1);
		doReturn(valueListServices).when(presenter).valueListServices();
		doReturn(existentMetadataSchemaTypes).when(valueListServices).getValueDomainTypes();
		when(presenter.newMetadataSchemaTypeToVOBuilder()).thenReturn(metadataSchemaTypeToVOBuilder);
		when(metadataSchemaTypeToVOBuilder.build(valueDomainType1)).thenReturn(metadataSchemaTypeVO);

		presenter.valueDomainCreationRequested(" " + newValueDomainTitle + " ");

		verify(valueListServices, never()).createTaxonomy(newValueDomainTitle);
		verify(view, never()).refreshTable();
	}
}
