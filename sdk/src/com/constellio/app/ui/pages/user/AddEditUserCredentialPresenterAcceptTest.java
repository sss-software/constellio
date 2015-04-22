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
package com.constellio.app.ui.pages.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.constellio.app.ui.application.ConstellioNavigator;
import com.constellio.app.ui.entities.UserCredentialVO;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.entities.security.global.UserCredential;
import com.constellio.model.services.users.UserServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.FakeSessionContext;

public class AddEditUserCredentialPresenterAcceptTest extends ConstellioTest {

	public static final String HEROES = "heroes";
	public static final String DAKOTA = "dakota";

	@Mock AddEditUserCredentialView userCredentialView;
	@Mock ConstellioNavigator navigator;
	UserServices userServices;
	UserCredential dakotaCredential, newUserCredential;
	UserCredentialVO dakotaCredentialVO, newUserCredentialVO;
	AddEditUserCredentialPresenter presenter;
	SessionContext sessionContext;

	@Before
	public void setUp()
			throws Exception {

		givenCollection(zeCollection).withAllTestUsers();
		givenCollection("otherCollection");
		sessionContext = FakeSessionContext.adminInCollection(zeCollection);
		sessionContext.setCurrentLocale(Locale.FRENCH);
		userServices = getModelLayerFactory().newUserServices();
		when(userCredentialView.getSessionContext()).thenReturn(sessionContext);
		when(userCredentialView.getCollection()).thenReturn(zeCollection);
		when(userCredentialView.getConstellioFactories()).thenReturn(getConstellioFactories());
		when(userCredentialView.navigateTo()).thenReturn(navigator);

		presenter = spy(new AddEditUserCredentialPresenter(userCredentialView));

		doNothing().when(presenter).showErrorMessageView(anyString());

		givenBreadCrumbAndParameters();
	}

	private void givenBreadCrumbAndParameters() {
		Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put("username", DAKOTA);
		presenter.setParamsMap(paramsMap);
		presenter.setBreadCrumb("url1/url2/url3");
	}

	@Test
	public void givenUsernameWhenGetUserCredentialVOThenReturnVO()
			throws Exception {

		UserCredentialVO userCredentialVO = presenter.getUserCredentialVO(DAKOTA);

		assertThat(userCredentialVO.getUsername()).isEqualTo(DAKOTA);
	}

	@Test
	public void givenNoUserNameWhenGetUserCredentialVOThenNewUserCredentialVO()
			throws Exception {

		UserCredentialVO userCredentialVO = presenter.getUserCredentialVO("");

		assertThat(userCredentialVO).isNotNull();
		assertThat(userCredentialVO.getUsername()).isNull();
	}

	@Test
	public void givenEditActionWhenSaveButtonClickedThenSaveChanges()
			throws Exception {

		dakotaCredentialVO = presenter.getUserCredentialVO(DAKOTA);
		dakotaCredentialVO.setFirstName("Dakota1");
		Set collectionsSet = new HashSet<>();
		collectionsSet.add(zeCollection);
		collectionsSet.add("otherCollection");
		dakotaCredentialVO.setCollections(collectionsSet);
		dakotaCredentialVO.setEmail("dakota1@constellio.com");
		dakotaCredentialVO.setGlobalGroups(Arrays.asList(HEROES));
		dakotaCredentialVO.setLastName("Lindien1");

		presenter.saveButtonClicked(dakotaCredentialVO);

		dakotaCredential = userServices.getUserCredential(DAKOTA);
		verify(userCredentialView.navigateTo()).url("url3/url1/url2/" + URLEncoder.encode("username=dakota", "UTF-8"));
		assertThat(dakotaCredential.getFirstName()).isEqualTo("Dakota1");
		assertThat(dakotaCredential.getGlobalGroups()).containsOnly(HEROES);
		assertThat(dakotaCredential.getCollections()).containsOnly(zeCollection, "otherCollection");
		assertThat(dakotaCredential.getLastName()).isEqualTo("Lindien1");
		assertThat(dakotaCredential.getEmail()).isEqualTo("dakota1@constellio.com");
	}

	@Test
	public void givenEditActionAndChangedUsernameWhenSaveButtonClickedThenDoNothing()
			throws Exception {

		dakotaCredentialVO = presenter.getUserCredentialVO(DAKOTA);
		dakotaCredentialVO.setUsername("dakota1");
		dakotaCredentialVO.setFirstName("Dakota1");
		Set collectionsSet = new HashSet<>();
		collectionsSet.add(zeCollection);
		collectionsSet.add("otherCollection");
		dakotaCredentialVO.setCollections(collectionsSet);
		dakotaCredentialVO.setEmail("dakota1@constellio.com");
		dakotaCredentialVO.setGlobalGroups(Arrays.asList(HEROES));
		dakotaCredentialVO.setLastName("Lindien1");

		presenter.saveButtonClicked(dakotaCredentialVO);

		dakotaCredential = userServices.getUserCredential(DAKOTA);
		verify(userCredentialView, never()).navigateTo();
		assertThat(dakotaCredential.getFirstName()).isEqualTo("Dakota");
		assertThat(dakotaCredential.getGlobalGroups()).containsOnly(HEROES);
		assertThat(dakotaCredential.getCollections()).containsOnly(zeCollection);
		assertThat(dakotaCredential.getLastName()).isEqualTo("L'Indien");
		assertThat(dakotaCredential.getEmail()).isEqualTo("dakota@doculibre.com");
	}

	@Test
	public void givenAddActionWhenSaveButtonClickedThenSaveChanges()
			throws Exception {

		newUserCredentialVO = presenter.getUserCredentialVO("");
		newUserCredentialVO.setUsername("user");
		newUserCredentialVO.setFirstName("User");
		Set collectionsSet = new HashSet<>();
		collectionsSet.add(zeCollection);
		collectionsSet.add("otherCollection");
		newUserCredentialVO.setCollections(collectionsSet);
		newUserCredentialVO.setEmail("user@constellio.com");
		newUserCredentialVO.setGlobalGroups(Arrays.asList(HEROES));
		newUserCredentialVO.setLastName("lastName");
		newUserCredentialVO.setPassword("password");
		newUserCredentialVO.setConfirmPassword("password");

		presenter.saveButtonClicked(newUserCredentialVO);

		verify(userCredentialView.navigateTo()).url("url3/url1/url2/" + URLEncoder.encode("username=user", "UTF-8"));
		newUserCredential = userServices.getUserCredential("user");
		assertThat(newUserCredential.getFirstName()).isEqualTo("User");
		assertThat(newUserCredential.getGlobalGroups()).containsOnly(HEROES);
		assertThat(newUserCredential.getCollections()).containsOnly(zeCollection, "otherCollection");
		assertThat(newUserCredential.getLastName()).isEqualTo("lastName");
		assertThat(newUserCredential.getEmail()).isEqualTo("user@constellio.com");
	}

	@Test
	public void givenAddActionAndDifferentConfirmPasswordWhenSaveButtonClickedThenDoNotSaveChanges()
			throws Exception {

		newUserCredentialVO = presenter.getUserCredentialVO("");
		newUserCredentialVO.setFirstName("User");
		Set collectionsSet = new HashSet<>();
		collectionsSet.add(zeCollection);
		collectionsSet.add("otherCollection");
		newUserCredentialVO.setCollections(collectionsSet);
		newUserCredentialVO.setEmail("user@constellio.com");
		newUserCredentialVO.setGlobalGroups(Arrays.asList(HEROES));
		newUserCredentialVO.setLastName("lastName");
		newUserCredentialVO.setPassword("password");
		newUserCredentialVO.setConfirmPassword("password1");

		presenter.saveButtonClicked(newUserCredentialVO);

		verify(userCredentialView, never()).navigateTo();
		assertThat(userServices.getUserCredential("user")).isNull();
	}

	@Test
	public void givenAddActionAndExistingUsernameWhenSaveButtonClickedThenDoNotSaveChanges()
			throws Exception {

		newUserCredentialVO = presenter.getUserCredentialVO("");
		newUserCredentialVO.setUsername("bob");
		newUserCredentialVO.setFirstName("User");
		Set collectionsSet = new HashSet<>();
		collectionsSet.add(zeCollection);
		collectionsSet.add("otherCollection");
		newUserCredentialVO.setCollections(collectionsSet);
		newUserCredentialVO.setEmail("user@constellio.com");
		newUserCredentialVO.setGlobalGroups(Arrays.asList(HEROES));
		newUserCredentialVO.setLastName("lastName");
		newUserCredentialVO.setPassword("password");
		newUserCredentialVO.setConfirmPassword("password");

		presenter.saveButtonClicked(newUserCredentialVO);

		verify(userCredentialView, never()).navigateTo();
		assertThat(userServices.getUserCredential("user")).isNull();
	}

	@Test
	public void whenCancelButtonClickedThenNavigateToBackPage()
			throws Exception {

		presenter.cancelButtonClicked();

		verify(userCredentialView.navigateTo()).url("url3/url1/url2/" + URLEncoder.encode("username=dakota",
				"UTF-8"));
	}
}
