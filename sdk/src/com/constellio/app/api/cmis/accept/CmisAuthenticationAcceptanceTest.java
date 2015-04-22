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
package com.constellio.app.api.cmis.accept;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.apache.chemistry.opencmis.client.api.Session;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.api.cmis.accept.CmisAcceptanceTestSetup.Records;
import com.constellio.data.dao.managers.config.ConfigManager;
import com.constellio.data.utils.hashing.HashingService;
import com.constellio.data.utils.hashing.HashingServiceException;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.schemas.MetadataSchemasManager;
import com.constellio.model.services.security.authentification.AuthenticationService;
import com.constellio.model.services.taxonomies.TaxonomiesManager;
import com.constellio.model.services.taxonomies.TaxonomiesSearchServices;
import com.constellio.model.services.users.UserServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.annotations.DriverTest;
import com.constellio.sdk.tests.annotations.SlowTest;
import com.constellio.sdk.tests.setups.Users;

@SlowTest
@DriverTest
public class CmisAuthenticationAcceptanceTest extends ConstellioTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(CmisAuthenticationAcceptanceTest.class);

	String anotherCollection = "anotherCollection";
	CmisAcceptanceTestSetup anotherCollectionSchemas = new CmisAcceptanceTestSetup(anotherCollection);
	String thirdCollection = "thirdCollection";
	UserServices userServices;
	TaxonomiesManager taxonomiesManager;
	MetadataSchemasManager metadataSchemasManager;
	RecordServices recordServices;
	Users users = new Users();
	CmisAcceptanceTestSetup zeCollectionSchemas = new CmisAcceptanceTestSetup(zeCollection);
	Records zeCollectionRecords;
	Records anotherCollectionRecords;
	TaxonomiesSearchServices taxonomiesSearchServices;

	Session cmisSessionChuck;
	Session cmisSessionBob;

	AuthenticationService authenticationService;
	ConfigManager configManager;
	HashingService hashingService;

	@Before
	public void setUp()
			throws Exception {

		authenticationService = getModelLayerFactory().newAuthenticationService();
		configManager = getDataLayerFactory().getConfigManager();
		hashingService = getIOLayerFactory().newHashingService();

		userServices = getModelLayerFactory().newUserServices();
		taxonomiesManager = getModelLayerFactory().getTaxonomiesManager();
		metadataSchemasManager = getModelLayerFactory().getMetadataSchemasManager();
		recordServices = getModelLayerFactory().newRecordServices();

		taxonomiesSearchServices = getModelLayerFactory().newTaxonomiesSearchService();

		users.setUp(userServices);

		defineSchemasManager().using(zeCollectionSchemas);
		defineSchemasManager().using(anotherCollectionSchemas);
		taxonomiesManager.addTaxonomy(zeCollectionSchemas.getTaxonomy1(), metadataSchemasManager);
		taxonomiesManager.addTaxonomy(zeCollectionSchemas.getTaxonomy2(), metadataSchemasManager);
		taxonomiesManager.setPrincipalTaxonomy(zeCollectionSchemas.getTaxonomy1(), metadataSchemasManager);
		taxonomiesManager.addTaxonomy(anotherCollectionSchemas.getTaxonomy1(), metadataSchemasManager);
		taxonomiesManager.setPrincipalTaxonomy(anotherCollectionSchemas.getTaxonomy1(), metadataSchemasManager);
		zeCollectionRecords = zeCollectionSchemas.givenRecords(recordServices);
		anotherCollectionRecords = anotherCollectionSchemas.givenRecords(recordServices);

		givenChuckAndBobPasswordsProperties();
		userServices.addUserToCollection(users.bob(), zeCollection);
		userServices.addUserToCollection(users.chuckNorris(), zeCollection);
		userServices.addUserToCollection(users.chuckNorris(), anotherCollection);

	}

	@Test
	public void givenMultipleCollectionsWithMultipleTaxonomies()
			throws Exception {

		assertThat(authenticationService.authenticate(chuckNorris, "1qaz2wsx")).isTrue();
		assertThat(authenticationService.authenticate(chuckNorris, "soleil")).isFalse();

		runSubTest(new thenChuckAndBobCanConnectInZeCollection());
		givenWrongPasswordThenChuckCannotConnectInZeCollection();
		thenChuckCanConnectInAnotherCollection();
		thenBobCannotConnectInAnotherCollection();
		thenChuckCannotConnectInInexistentCollection();
		whenCreateNewCollectionThenUsersCanConnectInIt();
	}

	@Test
	public void givenWriteDeletePermissionsToChuckInCollectionAndReadPermissionToBobWhenChuckLoginAsBobThenCanConnect()
			throws Exception {

		recordServices.update(users.chuckNorrisIn(zeCollection).setCollectionWriteAccess(true).getWrappedRecord());
		recordServices.update(users.chuckNorrisIn(zeCollection).setCollectionDeleteAccess(true).getWrappedRecord());
		Session cmisSessionChuckAsBob = newCmisSessionBuilder().authenticatedBy(chuckNorris, "1qaz2wsx")
				.logedAs(bobGratton).onCollection(zeCollection).build();

		assertThat(cmisSessionChuckAsBob.getRootFolder().getProperty("cmis:path").getValue()).isEqualTo("/");
	}

	@Test
	public void givenWriteDeletePermissionsToChuckInCollectionAndReadPermissionToBoboWhenBobLoginAsChuckThenCannotConnect()
			throws Exception {

		recordServices.update(users.chuckNorrisIn(zeCollection).setCollectionWriteAccess(true).getWrappedRecord());
		recordServices.update(users.chuckNorrisIn(zeCollection).setCollectionDeleteAccess(true).getWrappedRecord());
		try {
			newCmisSessionBuilder().authenticatedBy(bobGratton, "1qaz2wsx").logedAs(chuckNorris).onCollection(zeCollection)
					.build();
			fail();
		} catch (Exception e) {
			assertThat(true);
		}
	}

	@Test
	public void givenWritePermissionsToChuckInCollectionAndReadPermissionToBobWhenChuckLoginAsBobThenCannotConnect()
			throws Exception {

		recordServices.update(users.chuckNorrisIn(zeCollection).setCollectionWriteAccess(true).getWrappedRecord());
		try {
			newCmisSessionBuilder().authenticatedBy(chuckNorris, "1qaz2wsx").logedAs(bobGratton).onCollection(zeCollection)
					.build();
			fail();
		} catch (Exception e) {
			assertThat(true);
		}
	}

	@Test
	public void givenDeletePermissionsToChuckInCollectionAndReadPermissionToBobWhenChuckLoginAsBobThenCannotConnect()
			throws Exception {

		recordServices.update(users.chuckNorrisIn(zeCollection).setCollectionDeleteAccess(true).getWrappedRecord());
		try {
			newCmisSessionBuilder().authenticatedBy(chuckNorris, "1qaz2wsx").logedAs(bobGratton).onCollection(zeCollection)
					.build();
			fail();
		} catch (Exception e) {
			assertThat(true);
		}
	}

	@Test
	public void givenWriteDeletePermissionsToChuckInAnotherCollectionAndNoPermissionToBobWhenChuckLoginAsBobThenCannotConnect()
			throws Exception {

		recordServices.update(users.chuckNorrisIn(anotherCollection).setCollectionWriteAccess(true).getWrappedRecord());
		recordServices.update(users.chuckNorrisIn(anotherCollection).setCollectionDeleteAccess(true).getWrappedRecord());
		try {
			newCmisSessionBuilder().authenticatedBy(chuckNorris, "1qaz2wsx").logedAs(bobGratton).onCollection(
					anotherCollection)
					.build();
			fail();
		} catch (Exception e) {
			assertThat(true);
		}
	}

	@Test
	public void givenNewCollectionAndAddChuckInCollectionWhenAuthenticateThenChuckCanConnectInIt()
			throws Exception {
		whenCreateNewCollectionThenUsersCanConnectInIt();
	}

	private void givenWrongPasswordThenChuckCannotConnectInZeCollection() {
		try {
			Session cmisSessionChuck = newCmisSessionBuilder().authenticatedBy(chuckNorris, "wrongPassword")
					.onCollection(anotherCollection).build();
			cmisSessionChuck.getRootFolder().getProperty("cmis:path").getValue();
			fail();
		} catch (Exception e) {
			assertThat(true);
		}
	}

	private void thenChuckCanConnectInAnotherCollection() {
		cmisSessionChuck = newCmisSessionBuilder().authenticatedBy(chuckNorris, "1qaz2wsx").onCollection(anotherCollection)
				.build();
		assertThat(cmisSessionChuck.getRootFolder().getProperty("cmis:path").getValue()).isEqualTo("/");
	}

	private void thenBobCannotConnectInAnotherCollection() {
		try {
			cmisSessionBob = newCmisSessionBuilder().authenticatedBy(bobGratton, "xsw21qaz").onCollection(anotherCollection)
					.build();
			cmisSessionBob.getRootFolder().getProperty("cmis:path").getValue();
			fail();
		} catch (Exception e) {
			assertThat(true);
		}
	}

	private void thenChuckCannotConnectInInexistentCollection() {
		try {
			cmisSessionChuck = newCmisSessionBuilder().authenticatedBy(chuckNorris, "1qaz2wsx").onCollection(
					"inexistentCollection")
					.build();
			cmisSessionChuck.getRootFolder().getProperty("cmis:path").getValue();
			fail();
		} catch (Exception e) {
			assertThat(true);
		}
	}

	private void whenCreateNewCollectionThenUsersCanConnectInIt() {
		givenCollection(thirdCollection);

		userServices.addUserToCollection(users.chuckNorris(), thirdCollection);

		Session cmisSessionChuckThirdCollection = newCmisSessionBuilder().authenticatedBy(chuckNorris, "1qaz2wsx")
				.onCollection(thirdCollection).build();
		assertThat(cmisSessionChuckThirdCollection.getRootFolder().getProperty("cmis:path").getValue()).isEqualTo("/");
	}

	// --- ---
	private void givenChuckAndBobPasswordsProperties()
			throws HashingServiceException {
		authenticationService.changePassword(chuckNorris, "1qaz2wsx");
		authenticationService.changePassword(bobGratton, "xsw21qaz");
	}

	public class thenChuckAndBobCanConnectInZeCollection extends SubTest {

		@Override
		public void run() {
			cmisSessionChuck = newCmisSessionBuilder().authenticatedBy(chuckNorris, "1qaz2wsx").onCollection(zeCollection)
					.build();
			assertThat(cmisSessionChuck.getRootFolder().getProperty("cmis:path").getValue()).isEqualTo("/");
			cmisSessionBob = newCmisSessionBuilder().authenticatedBy(bobGratton, "xsw21qaz").onCollection(zeCollection)
					.build();
			assertThat(cmisSessionBob.getRootFolder().getProperty("cmis:path").getValue()).isEqualTo("/");
		}
	}

}
