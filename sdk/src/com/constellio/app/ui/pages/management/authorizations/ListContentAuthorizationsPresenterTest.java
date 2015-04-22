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
package com.constellio.app.ui.pages.management.authorizations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import com.constellio.app.modules.rm.wrappers.AdministrativeUnit;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.ui.application.ConstellioNavigator;
import com.constellio.app.ui.entities.AuthorizationVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.framework.builders.AuthorizationToVOBuilder;
import com.constellio.app.ui.pages.base.PresenterService;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.data.dao.services.idGenerator.UniqueIdGenerator;
import com.constellio.model.entities.Taxonomy;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.security.Authorization;
import com.constellio.model.entities.security.AuthorizationDetails;
import com.constellio.model.entities.security.CustomizedAuthorizationsBehavior;
import com.constellio.model.entities.security.Role;
import com.constellio.model.services.security.AuthorizationsServices;
import com.constellio.model.services.taxonomies.TaxonomiesManager;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.FakeSessionContext;
import com.constellio.sdk.tests.MockedFactories;

public class ListContentAuthorizationsPresenterTest extends ConstellioTest {
	public static final String ZE_SECURED_OBJECT = "zeObject";
	public static final String ZENOTHER_SECURED_OBJECT = "zenotherObject";
	public static final String ZE_PRINCIPAL = "zePrincipal";

	@Mock AuthorizationsServices authorizationsServices;
	@Mock PresenterService presenterService;
	@Mock ListContentAuthorizationsView view;
	@Mock ConstellioNavigator navigator;
	@Mock User user;
	@Mock RecordVO object;
	@Mock AuthorizationVO authorizationVO;
	@Mock AuthorizationVO inherited1;
	@Mock AuthorizationVO inherited2;
	@Mock AuthorizationVO own1;
	@Mock AuthorizationVO own2;
	@Mock Authorization authorization;
	@Mock AuthorizationDetails details;
	MockedFactories factories = new MockedFactories();

	ListAuthorizationsPresenter presenter;

	@Before
	public void setUp()
			throws Exception {
		when(view.getConstellioFactories()).thenReturn(factories.getConstellioFactories());
		when(view.getSessionContext()).thenReturn(FakeSessionContext.gandalfInCollection(zeCollection));
		when(view.getCollection()).thenReturn(zeCollection);
		when(view.navigateTo()).thenReturn(navigator);

		when(factories.getAppLayerFactory().newPresenterService()).thenReturn(presenterService);
		when(presenterService.getRecordVO(ZE_SECURED_OBJECT, VIEW_MODE.DISPLAY)).thenReturn(object);
		when(presenterService.getCurrentUser(isA(SessionContext.class))).thenReturn(user);

		when(factories.getModelLayerFactory().newAuthorizationsServices()).thenReturn(authorizationsServices);

		presenter = spy(new ListContentAuthorizationsPresenter(view).forRequestParams(ZE_SECURED_OBJECT));
	}

	@Test
	public void givenIdThenReturnTheCorrectObject() {
		assertThat(presenter.getRecordVO()).isEqualTo(object);
	}

	@Test
	public void givenBackButtonPressedWhenObjectIsFolderWithDefaultSchemaThenNavigateToFolder() {
		presenter.backButtonClicked(Folder.DEFAULT_SCHEMA);
		verify(navigator, times(1)).displayFolder(ZE_SECURED_OBJECT);
	}

	@Test
	public void givenBackButtonPressedWhenObjectIsFolderWithCustomSchemaThenNavigateToFolder() {
		presenter.backButtonClicked(Folder.SCHEMA_TYPE + "_custom");
		verify(navigator, times(1)).displayFolder(ZE_SECURED_OBJECT);
	}

	@Test
	public void givenBackButtonPressedWhenObjectIsDocumentWithDefaultSchemaThenNavigateToFolder() {
		presenter.backButtonClicked(Document.DEFAULT_SCHEMA);
		verify(navigator, times(1)).displayDocument(ZE_SECURED_OBJECT);
	}

	@Test
	public void givenBackButtonPressedWhenObjectIsDocumentWithCustomSchemaThenNavigateToFolder() {
		presenter.backButtonClicked(Document.SCHEMA_TYPE + "_custom");
		verify(navigator, times(1)).displayDocument(ZE_SECURED_OBJECT);
	}

	@Test
	public void givenBackButtonPressedWhenObjectIsTaxonomyConceptThenNavigateToTaxonomyManagement() {
		TaxonomiesManager manager = mock(TaxonomiesManager.class, "TaxonomiesManager");
		when(factories.getModelLayerFactory().getTaxonomiesManager()).thenReturn(manager);
		Taxonomy taxonomy = mock(Taxonomy.class, "Taxonomy");
		when(manager.getPrincipalTaxonomy(zeCollection)).thenReturn(taxonomy);
		when(taxonomy.getCode()).thenReturn("taxo");

		presenter.backButtonClicked(AdministrativeUnit.DEFAULT_SCHEMA);
		verify(navigator, times(1)).taxonomyManagement("taxo", ZE_SECURED_OBJECT);
	}

	@Test
	public void givenOwnAndInheritedAuthorizationsThenReturnOwnAuthorizations() {
		givenObjectWithTwoInheritedAndTwoOwnAuthorizations();
		assertThat(presenter.getOwnAuthorizations()).containsOnly(own1, own2);
	}

	@Test
	public void givenOwnAndInheritedAuthorizationsThenReturnInheritedAuthorizations() {
		givenObjectWithTwoInheritedAndTwoOwnAuthorizations();
		assertThat(presenter.getInheritedAuthorizations()).containsOnly(inherited1, inherited2);
	}

	@Test
	public void givenAuthorizationDeletedThenRemoveTheAuthorizationAndRefreshTheView() {
		givenAuthorizationWithId(aString());
		presenter.deleteButtonClicked(authorizationVO);
		verify(authorizationsServices, times(1)).delete(details, user);
		verify(view, times(1)).removeAuthorization(authorizationVO);
	}

	@Test
	public void givenAuthorizationAddedThenAddTheAuthorizationAndRefreshTheView() {
		String id = aString();
		UniqueIdGenerator generator = mock(UniqueIdGenerator.class, "UniqueIdGenerator");
		when(factories.getModelLayerFactory().getDataLayerFactory()).thenReturn(factories.getDataLayerFactory());
		when(factories.getDataLayerFactory().getUniqueIdGenerator()).thenReturn(generator);
		when(generator.next()).thenReturn(id);

		LocalDate start = aDate();
		LocalDate end = aDate().plusMonths(7);
		List<String> users = Arrays.asList("USER_A", "USER_B");
		List<String> accessRoles = Arrays.asList(Role.READ, Role.WRITE, Role.DELETE);
		AuthorizationVO newAuthorization = new AuthorizationVO(
				users, new ArrayList<String>(), Arrays.asList(ZE_SECURED_OBJECT), accessRoles, new ArrayList<String>(),
				new ArrayList<String>(), null,
				start, end, false);

		presenter.authorizationCreationRequested(newAuthorization);

		ArgumentCaptor<Authorization> captor = ArgumentCaptor.forClass(Authorization.class);
		verify(authorizationsServices, times(1)).add(
				captor.capture(), eq(CustomizedAuthorizationsBehavior.KEEP_ATTACHED), eq(user));
		verify(view, times(1)).addAuthorization(newAuthorization);

		Authorization authorization = captor.getValue();
		assertThat(authorization.getGrantedToPrincipals()).containsExactlyElementsOf(users);
		assertThat(authorization.getGrantedOnRecords()).containsOnly(ZE_SECURED_OBJECT);
		assertThat(authorization.getDetail().getCollection()).isEqualTo(zeCollection);
		assertThat(authorization.getDetail().getRoles()).containsExactlyElementsOf(accessRoles);
		assertThat(authorization.getDetail().getStartDate()).isEqualTo(start);
		assertThat(authorization.getDetail().getEndDate()).isEqualTo(end);
	}

	private void givenObjectWithTwoInheritedAndTwoOwnAuthorizations() {
		Authorization authorization1 = mock(Authorization.class, "Authorization1");
		Authorization authorization2 = mock(Authorization.class, "Authorization2");
		Authorization authorization3 = mock(Authorization.class, "Authorization3");
		Authorization authorization4 = mock(Authorization.class, "Authorization4");
		Authorization authorization5 = mock(Authorization.class, "Authorization5");
		Authorization authorization6 = mock(Authorization.class, "Authorization6");

		when(authorization1.getGrantedOnRecords()).thenReturn(Arrays.asList(ZE_SECURED_OBJECT));
		when(authorization1.getGrantedToPrincipals()).thenReturn(Arrays.asList(ZE_PRINCIPAL));

		when(authorization2.getGrantedOnRecords()).thenReturn(Arrays.asList(ZENOTHER_SECURED_OBJECT));
		when(authorization2.getGrantedToPrincipals()).thenReturn(Arrays.asList(ZE_PRINCIPAL));

		when(authorization3.getGrantedOnRecords()).thenReturn(Arrays.asList(ZE_SECURED_OBJECT));
		when(authorization3.getGrantedToPrincipals()).thenReturn(Arrays.asList(ZE_PRINCIPAL));

		when(authorization4.getGrantedOnRecords()).thenReturn(Arrays.asList(ZENOTHER_SECURED_OBJECT));
		when(authorization4.getGrantedToPrincipals()).thenReturn(Arrays.asList(ZE_PRINCIPAL));

		when(authorization5.getGrantedOnRecords()).thenReturn(Arrays.asList(ZE_SECURED_OBJECT));
		when(authorization5.getGrantedToPrincipals()).thenReturn(new ArrayList<String>());

		when(authorization6.getGrantedOnRecords()).thenReturn(Arrays.asList(ZENOTHER_SECURED_OBJECT));
		when(authorization6.getGrantedToPrincipals()).thenReturn(new ArrayList<String>());

		Record record = mock(Record.class, "Record");
		when(presenterService.getRecord(ZE_SECURED_OBJECT)).thenReturn(record);

		when(authorizationsServices.getRecordAuthorizations(record)).thenReturn(
				Arrays.asList(authorization1, authorization2, authorization3, authorization4, authorization5, authorization6));

		AuthorizationToVOBuilder builder = mock(AuthorizationToVOBuilder.class, "AuthorizationToVOBuilder");
		when(builder.build(authorization1)).thenReturn(own1);
		when(builder.build(authorization2)).thenReturn(inherited1);
		when(builder.build(authorization3)).thenReturn(own2);
		when(builder.build(authorization4)).thenReturn(inherited2);

		doReturn(builder).when(presenter).newAuthorizationToVOBuilder();
	}

	private void givenAuthorizationWithId(String authId) {
		when(authorizationVO.getAuthId()).thenReturn(authId);
		when(authorizationsServices.getAuthorization(zeCollection, authId)).thenReturn(authorization);
		when(authorization.getDetail()).thenReturn(details);
	}
}
