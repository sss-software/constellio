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
package com.constellio.app.services.systemSetup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.constellio.app.conf.AppLayerConfiguration;
import com.constellio.app.entities.modules.InstallableModule;
import com.constellio.app.modules.rm.ConstellioRMModule;
import com.constellio.app.modules.rm.constants.RMPermissionsTo;
import com.constellio.app.services.extensions.ConstellioPluginManager;
import com.constellio.model.entities.modules.Module;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.security.global.UserCredential;
import com.constellio.model.services.security.authentification.AuthenticationService;
import com.constellio.sdk.tests.ConstellioTest;

public class SystemSetupServicesAcceptTest extends ConstellioTest {

	SystemSetupService systemSetupService;

	File setupFile;

	ConstellioPluginManager pluginManager;

	@Mock AppLayerConfiguration appLayerConfiguration;

	@Before
	public void setUp()
			throws Exception {
		withSpiedServices(ConstellioPluginManager.class);
		setupFile = newTempFileWithContent("constellio.setup.properties", "");

		when(appLayerConfiguration.getSetupProperties()).thenReturn(setupFile);
		systemSetupService = new SystemSetupService(getAppLayerFactory(),
				appLayerConfiguration);

		List<InstallableModule> modules = new ArrayList<>();
		modules.add(new ConstellioRMModule());

		pluginManager = getAppLayerFactory().getPluginManager();
		when(pluginManager.getPlugins(InstallableModule.class)).thenReturn(modules);
	}

	@Test
	public void whenSetupSystemWithCollectionThenInitializeCorrectly()
			throws Exception {

		String content = "#These configs are used when Constellio is started the first time\n"
				+ "\n"
				+ "admin.servicekey=zeAdminKey\n"
				+ "admin.password=zePassword\n"
				+ "collections=myCollection1, myCollection2\n"
				+ "collection.myCollection1.modules=com.constellio.app.modules.rm.ConstellioRMModule";

		getIOLayerFactory().newFileService().replaceFileContent(setupFile, content);

		systemSetupService.setup();

		thenHasAdminUserWithCorrectServiceKeyAndPassword();
		thenMyCollectionIsCreatedWithModulesAndAdminUser();

	}

	@Test
	public void whenSetupSystemWithoutCollectionThenInitializeCorrectly()
			throws Exception {

		String content = "#These configs are used when Constellio is started the first time\n"
				+ "\n"
				+ "admin.servicekey=zeAdminKey\n"
				+ "admin.password=zePassword";

		getIOLayerFactory().newFileService().replaceFileContent(setupFile, content);

		systemSetupService.setup();

		thenHasAdminUserWithCorrectServiceKeyAndPassword();
		thenHasNoCollection();

	}

	private void thenHasNoCollection() {
		assertThat(getModelLayerFactory().getCollectionsListManager().getCollections()).isEmpty();
	}

	private void thenMyCollectionIsCreatedWithModulesAndAdminUser() {

		Record aRecord = mock(Record.class);
		assertThat(getModelLayerFactory().getCollectionsListManager().getCollections())
				.containsOnly("myCollection1", "myCollection2");

		List<Module> modules = getAppLayerFactory().getModulesManager().getEnabledModules("myCollection1");
		assertThat(modules).hasSize(1);
		assertThat(modules.get(0).getClass()).isEqualTo(ConstellioRMModule.class);

		User admin = getModelLayerFactory().newUserServices().getUserInCollection("admin", "myCollection1");
		assertThat(admin.isSystemAdmin()).isTrue();
		assertThat(admin.hasCollectionReadAccess()).isTrue();
		assertThat(admin.hasCollectionWriteAccess()).isTrue();
		assertThat(admin.hasCollectionDeleteAccess()).isTrue();
		assertThat(getModelLayerFactory().newUserServices().has("admin")
				.allGlobalPermissionsInAnyCollection(RMPermissionsTo.getAllPermissions())).isTrue();
		assertThat(admin.hasAll(RMPermissionsTo.getAllPermissions()).globally()).isTrue();
		assertThat(admin.hasAll(RMPermissionsTo.getAllPermissions()).on(aRecord)).isTrue();
		assertThat(admin.hasAll(RMPermissionsTo.getAllPermissions()).onAll(aRecord, aRecord)).isTrue();
		assertThat(admin.hasAll(RMPermissionsTo.getAllPermissions()).onAny(aRecord, aRecord)).isTrue();
	}

	private void thenHasAdminUserWithCorrectServiceKeyAndPassword() {
		UserCredential admin = getModelLayerFactory().newUserServices().getUser("admin");
		assertThat(admin).isNotNull();
		assertThat(admin.getServiceKey()).isEqualTo("zeAdminKey");

		AuthenticationService authenticationService = getModelLayerFactory().newAuthenticationService();
		if (authenticationService.supportPasswordChange()) {
			//Otherwise, the password is not set in setup
			assertThat(authenticationService.authenticate("admin", "zePassword")).isTrue();
		}

	}
}
