package com.constellio.sdk.tests;

import com.constellio.app.modules.rm.DemoTestRecords;
import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.modules.rm.RMTestRecords;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.migrations.ConstellioEIMConfigs;
import com.constellio.sdk.tests.annotations.MainTest;
import com.constellio.sdk.tests.annotations.UiTest;
import org.junit.Test;

@UiTest
@MainTest
public class InitialStateSaverAcceptTest extends ConstellioTest {

	@Test
	public void saveCurrentInitialState()
			throws Exception {

		givenTransactionLogIsEnabled();
		givenCollection(zeCollection).withConstellioRMModule();

		getSaveStateFeature().saveCurrentStateToInitialStatesFolder();
	}

	@Test
	public void saveModifiedState()
			throws Exception {

		givenTransactionLogIsEnabled();
		givenCollection(zeCollection).withConstellioRMModule().withAllTestUsers();

		getSaveStateFeature().saveStateAfterTestWithTitle("with_manual_modifications");

		ModelLayerFactory modelLayerFactory = getModelLayerFactory();
		//modelLayerFactory.getSystemConfigurationsManager().setValue(RMConfigs.DOCUMENT_RETENTION_RULES, true);

		newWebDriver(loggedAsUserInCollection(admin, zeCollection));
		waitUntilICloseTheBrowsers();

	}

	@Test
	public void saveWithEnterpriseSearchModule()
			throws Exception {

		givenTransactionLogIsEnabled();
		givenCollection(zeCollection).withConstellioESModule().withAllTestUsers();

		getSaveStateFeature().saveStateAfterTestWithTitle("with_enterprise_search_module");

		newWebDriver(loggedAsUserInCollection(admin, zeCollection));
		waitUntilICloseTheBrowsers();

	}

	@Test
	public void saveModifiedStateStartingFromNewSystem()
			throws Exception {

		givenTransactionLogIsEnabled();
		//		givenCollection(zeCollection).withConstellioRMModule().withAllTestUsers();
		//
		getSaveStateFeature().saveStateAfterTestWithTitle("from_new_system");

		newWebDriver();
		waitUntilICloseTheBrowsers();

	}

	@Test
	public void saveStateWithTestRecordsInOneCollection()
			throws Exception {

		givenTransactionLogIsEnabled();
		givenBackgroundThreadsEnabled();
		givenCollection(zeCollection).withConstellioRMModule().withAllTestUsers();
		RMTestRecords records = new RMTestRecords(zeCollection);
		records.setup(getAppLayerFactory());

		getSaveStateFeature().saveStateAfterTestWithTitle("collection");

		ModelLayerFactory modelLayerFactory = getModelLayerFactory();
		modelLayerFactory.getSystemConfigurationsManager().setValue(ConstellioEIMConfigs.INCLUDE_CONTENTS_IN_SAVESTATE, true);

		newWebDriver(loggedAsUserInCollection(admin, zeCollection));
		waitUntilICloseTheBrowsers();

	}

	@Test
	public void saveStateWithTestRecords()
			throws Exception {

		givenTransactionLogIsEnabled();
		givenCollection(zeCollection).withConstellioRMModule().withAllTestUsers();
		RMTestRecords records = new RMTestRecords(zeCollection);
		records.setup(getAppLayerFactory()).withFoldersAndContainersOfEveryStatus();
		givenCollection("zeDeuxiemeCollection").withConstellioRMModule().withAllTestUsers();
		DemoTestRecords records2 = new DemoTestRecords("zeDeuxiemeCollection");
		records2.setup(getAppLayerFactory()).withFoldersAndContainersOfEveryStatus();

		getSaveStateFeature().saveStateAfterTestWithTitle("with_document_rules");

		ModelLayerFactory modelLayerFactory = getModelLayerFactory();
		modelLayerFactory.getSystemConfigurationsManager().setValue(RMConfigs.DOCUMENT_RETENTION_RULES, true);

		newWebDriver(loggedAsUserInCollection(admin, zeCollection));
		waitUntilICloseTheBrowsers();

	}

}
