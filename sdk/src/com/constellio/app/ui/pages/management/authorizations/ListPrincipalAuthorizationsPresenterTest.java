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
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.constellio.app.ui.application.ConstellioNavigator;
import com.constellio.app.ui.entities.AuthorizationVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.framework.builders.AuthorizationToVOBuilder;
import com.constellio.app.ui.pages.base.PresenterService;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.Group;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.security.Authorization;
import com.constellio.model.entities.security.AuthorizationDetails;
import com.constellio.model.entities.security.CustomizedAuthorizationsBehavior;
import com.constellio.model.services.security.AuthorizationsServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.FakeSessionContext;
import com.constellio.sdk.tests.MockedFactories;

public class ListPrincipalAuthorizationsPresenterTest extends ConstellioTest {
	public static final String ZE_PRINCIPAL = "zePrincipal";
	public static final String ZENOTHER_PRINCIPAL = "zenotherPrincipal";

	@Mock AuthorizationsServices authorizationsServices;
	@Mock PresenterService presenterService;
	@Mock ListPrincipalAuthorizationsView view;
	@Mock ConstellioNavigator navigator;
	@Mock User user;
	@Mock RecordVO principal;
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
		when(presenterService.getRecordVO(ZE_PRINCIPAL, VIEW_MODE.DISPLAY)).thenReturn(principal);
		when(presenterService.getCurrentUser(isA(SessionContext.class))).thenReturn(user);

		when(factories.getModelLayerFactory().newAuthorizationsServices()).thenReturn(authorizationsServices);

		presenter = spy(new ListPrincipalAuthorizationsPresenter(view).forRequestParams(ZE_PRINCIPAL));
	}

	@Test
	public void givenIdThenReturnTheCorrectPrincipal() {
		assertThat(presenter.getRecordVO()).isEqualTo(principal);
	}

	@Test
	public void givenBackButtonPressedWhenPrincipalIsGroupThenNavigateToGroup() {
		presenter.backButtonClicked(Group.DEFAULT_SCHEMA);
		verify(navigator, times(1)).displayCollectionGroup(ZE_PRINCIPAL);
	}

	@Test
	public void givenBackButtonPressedWhenPrincipalIsUserThenNavigateToUser() {
		presenter.backButtonClicked(User.DEFAULT_SCHEMA);
		verify(navigator, times(1)).displayCollectionUser(ZE_PRINCIPAL);
	}

	@Test
	public void givenOwnAndInheritedAuthorizationsThenReturnOwnAuthorizations() {
		givenPrincipalWithTwoInheritedAndTwoOwnAuthorizations();
		assertThat(presenter.getOwnAuthorizations()).containsOnly(own1, own2);
	}

	@Test
	public void givenOwnAndInheritedAuthorizationsThenReturnInheritedAuthorizations() {
		givenPrincipalWithTwoInheritedAndTwoOwnAuthorizations();
		assertThat(presenter.getInheritedAuthorizations()).containsOnly(inherited1, inherited2);
	}

	@Test
	public void givenAuthorizationDeletedWhenSinglePrincipalThenRemoveTheAuthorizationAndRefreshTheView() {
		givenAuthorizationWithId(aString(), false);
		presenter.deleteButtonClicked(authorizationVO);
		verify(authorizationsServices, times(1)).delete(details, user);
		verify(view, times(1)).removeAuthorization(authorizationVO);
	}

	@Test
	public void givenAuthorizationDeletedWhenMultiPrincipalThenRemoveThePrincipalAndRefreshTheView() {
		givenAuthorizationWithId(aString(), true);
		presenter.deleteButtonClicked(authorizationVO);
		assertThat(authorization.getGrantedToPrincipals()).containsOnly(ZENOTHER_PRINCIPAL);
		verify(authorizationsServices, times(1)).modify(authorization, CustomizedAuthorizationsBehavior.KEEP_ATTACHED, user);
		verify(view, times(1)).removeAuthorization(authorizationVO);
	}

	private void givenPrincipalWithTwoInheritedAndTwoOwnAuthorizations() {
		Authorization authorization1 = mock(Authorization.class, "Authorization1");
		Authorization authorization2 = mock(Authorization.class, "Authorization2");
		Authorization authorization3 = mock(Authorization.class, "Authorization3");
		Authorization authorization4 = mock(Authorization.class, "Authorization4");

		when(authorization1.getGrantedToPrincipals()).thenReturn(Arrays.asList(ZE_PRINCIPAL));
		when(authorization2.getGrantedToPrincipals()).thenReturn(Arrays.asList(ZENOTHER_PRINCIPAL));
		when(authorization3.getGrantedToPrincipals()).thenReturn(Arrays.asList(ZE_PRINCIPAL));
		when(authorization4.getGrantedToPrincipals()).thenReturn(Arrays.asList(ZENOTHER_PRINCIPAL));

		Record record = mock(Record.class, "Record");
		when(presenterService.getRecord(ZE_PRINCIPAL)).thenReturn(record);

		when(authorizationsServices.getRecordAuthorizations(record)).thenReturn(
				Arrays.asList(authorization1, authorization2, authorization3, authorization4));

		AuthorizationToVOBuilder builder = mock(AuthorizationToVOBuilder.class, "AuthorizationToVOBuilder");
		when(builder.build(authorization1)).thenReturn(own1);
		when(builder.build(authorization2)).thenReturn(inherited1);
		when(builder.build(authorization3)).thenReturn(own2);
		when(builder.build(authorization4)).thenReturn(inherited2);

		doReturn(builder).when(presenter).newAuthorizationToVOBuilder();
	}

	private void givenAuthorizationWithId(String authId, boolean multiPrincipal) {
		when(authorizationVO.getAuthId()).thenReturn(authId);
		when(authorizationsServices.getAuthorization(zeCollection, authId)).thenReturn(authorization);
		when(authorization.getGrantedToPrincipals()).thenReturn(multiPrincipal ?
				new ArrayList<>(Arrays.asList(ZE_PRINCIPAL, ZENOTHER_PRINCIPAL)) :
				Arrays.asList(ZE_PRINCIPAL));
		when(authorization.getDetail()).thenReturn(details);
	}
}
