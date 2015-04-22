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
package com.constellio.data.dao.services.factories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.constellio.data.conf.DataLayerConfiguration;
import com.constellio.data.dao.managers.config.ConfigManager;
import com.constellio.data.dao.services.bigVault.BigVaultRecordDao;
import com.constellio.data.dao.services.idGenerator.UniqueIdGenerator;
import com.constellio.data.dao.services.solr.SolrDataStoreTypesFactory;
import com.constellio.data.dao.services.solr.SolrServers;
import com.constellio.data.dao.services.transactionLog.XMLSecondTransactionLogManager;
import com.constellio.data.io.IOServicesFactory;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.DataLayerConfigurationAlteration;

public class DataLayerFactoryRealTest extends ConstellioTest {

	@Mock SolrServers solrServers;

	@Mock IOServicesFactory ioServicesFactory;

	@Mock DataLayerConfiguration dataLayerConfiguration;

	private DataLayerFactory factory;

	@Before
	public void setUp()
			throws Exception {

	}

	@Test
	public void whenGettingSettingsManagerThenAlwaysSameInstance()
			throws Exception {
		factory = getDataLayerFactory();
		ConfigManager settingsManager1 = factory.getConfigManager();
		ConfigManager settingsManager2 = factory.getConfigManager();

		assertThat(settingsManager1).isNotNull().isSameAs(settingsManager2);
	}

	@Test
	public void whenGettingTypesFactoryThenReturnSolrTypesFactory()
			throws Exception {
		factory = getDataLayerFactory();
		assertThat(factory.newTypesFactory()).isInstanceOf(SolrDataStoreTypesFactory.class);
	}

	@Test
	public void whenGetUniqueIdGeneratorThenAlwaysSameInstance()
			throws Exception {
		factory = getDataLayerFactory();
		UniqueIdGenerator uniqueIdGenerator1 = factory.getUniqueIdGenerator();
		UniqueIdGenerator uniqueIdGenerator2 = factory.getUniqueIdGenerator();

		assertThat(uniqueIdGenerator1).isNotNull().isSameAs(uniqueIdGenerator2);
	}

	@Test
	public void givenSecondTransactionLogDisabledThenDisabledInEachCollection() {
		configure(new DataLayerConfigurationAlteration() {
			@Override
			public void alter(DataLayerConfiguration configuration) {
				doReturn(false).when(configuration).isSecondTransactionLogEnabled();
			}
		});

		factory = getDataLayerFactory();

		assertThat(((BigVaultRecordDao) factory.newRecordDao()).getSecondTransactionLogManager()).isNull();
		assertThat(((BigVaultRecordDao) factory.newEventsDao()).getSecondTransactionLogManager()).isNull();
		assertThat(((BigVaultRecordDao) factory.newNotificationsDao()).getSecondTransactionLogManager()).isNull();
	}

	@Test
	public void givenSecondTransactionLogEnabledThenOnlyEnabledForRecordSolrCollection() {
		final File secondTransactionLogBaseFolder = newTempFolder();
		configure(new DataLayerConfigurationAlteration() {
			@Override
			public void alter(DataLayerConfiguration configuration) {
				doReturn(true).when(configuration).isSecondTransactionLogEnabled();
				doReturn(secondTransactionLogBaseFolder).when(configuration).getSecondTransactionLogBaseFolder();
			}
		});

		factory = getDataLayerFactory();

		XMLSecondTransactionLogManager transactionLog = (XMLSecondTransactionLogManager) ((BigVaultRecordDao) factory
				.newRecordDao()).getSecondTransactionLogManager();
		assertThat(transactionLog.getFolder()).isEqualTo(secondTransactionLogBaseFolder);
		assertThat(((BigVaultRecordDao) factory.newEventsDao()).getSecondTransactionLogManager()).isNull();
		assertThat(((BigVaultRecordDao) factory.newNotificationsDao()).getSecondTransactionLogManager()).isNull();
	}

}
