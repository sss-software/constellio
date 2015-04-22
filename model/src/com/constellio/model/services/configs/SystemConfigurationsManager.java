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
package com.constellio.model.services.configs;

import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.data.dao.managers.StatefulService;
import com.constellio.data.dao.managers.config.ConfigManager;
import com.constellio.data.dao.managers.config.PropertiesAlteration;
import com.constellio.data.dao.managers.config.events.ConfigUpdatedEventListener;
import com.constellio.data.dao.managers.config.values.PropertiesConfiguration;
import com.constellio.data.utils.BatchBuilderIterator;
import com.constellio.data.utils.Delayed;
import com.constellio.data.utils.ImpossibleRuntimeException;
import com.constellio.data.utils.LangUtils;
import com.constellio.model.entities.batchprocess.BatchProcess;
import com.constellio.model.entities.batchprocess.BatchProcessAction;
import com.constellio.model.entities.calculators.dependencies.ConfigDependency;
import com.constellio.model.entities.calculators.dependencies.Dependency;
import com.constellio.model.entities.configs.SystemConfiguration;
import com.constellio.model.entities.configs.SystemConfigurationGroup;
import com.constellio.model.entities.configs.SystemConfigurationScript;
import com.constellio.model.entities.modules.Module;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.entities.schemas.entries.CalculatedDataEntry;
import com.constellio.model.frameworks.validation.ValidationErrors;
import com.constellio.model.services.batch.actions.ReindexMetadatasBatchProcessAction;
import com.constellio.model.services.batch.manager.BatchProcessesManager;
import com.constellio.model.services.configs.SystemConfigurationsManagerRuntimeException.SystemConfigurationsManagerRuntimeException_InvalidConfigValue;
import com.constellio.model.services.configs.SystemConfigurationsManagerRuntimeException.SystemConfigurationsManagerRuntimeException_UpdateScriptFailed;
import com.constellio.model.services.extensions.ConstellioModulesManager;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.migrations.ConstellioEIMConfigs;
import com.constellio.model.services.schemas.SchemaUtils;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.utils.InstanciationUtils;

public class SystemConfigurationsManager implements StatefulService, ConfigUpdatedEventListener {

	private static Logger LOGGER = LoggerFactory.getLogger(SystemConfigurationsManager.class);

	ModelLayerFactory modelLayerFactory;

	PropertiesConfiguration configValues;

	static final String CONFIG_FILE_PATH = "/systemConfigs.properties";
	ConfigManager configManager;

	Delayed<ConstellioModulesManager> constellioModulesManagerDelayed;

	public SystemConfigurationsManager(ModelLayerFactory modelLayerFactory, ConfigManager configManager,
			Delayed<ConstellioModulesManager> constellioModulesManagerDelayed) {
		this.modelLayerFactory = modelLayerFactory;
		this.configManager = configManager;
		this.constellioModulesManagerDelayed = constellioModulesManagerDelayed;
		this.configManager.registerListener(CONFIG_FILE_PATH, this);
	}

	@Override
	public void initialize() {
		configManager.createPropertiesDocumentIfInexistent(CONFIG_FILE_PATH, new PropertiesAlteration() {
			@Override
			public void alter(Map<String, String> properties) {

			}
		});
		reloadConfigValues();
	}

	private void reloadConfigValues() {
		configValues = configManager.getProperties(CONFIG_FILE_PATH);
	}

	@Override
	public void close() {

	}

	public void setValue(final SystemConfiguration config, final Object newValue) {

		final Object oldValue = getValue(config);

		ValidationErrors errors = new ValidationErrors();
		validate(config, newValue, errors);
		if (!errors.getValidationErrors().isEmpty()) {
			throw new SystemConfigurationsManagerRuntimeException_InvalidConfigValue(config.getCode(), newValue);
		}
		BatchProcessesManager batchProcessesManager = modelLayerFactory.getBatchProcessesManager();
		SystemConfigurationScript<Object> listener = getInstanciatedScriptFor(config);
		List<String> collections = modelLayerFactory.getCollectionsListManager().getCollections();
		ConstellioModulesManager modulesManager = constellioModulesManagerDelayed.get();
		Module module = config.getModule() == null ? null : modulesManager.getInstalledModule(config.getModule());

		List<BatchProcess> batchProcesses = startBatchProcessesToReindex(config);
		try {

			if (listener != null) {
				listener.onValueChanged(oldValue, newValue, modelLayerFactory);

				for (String collection : collections) {
					if (module == null || modulesManager.isModuleEnabled(collection, module)) {
						listener.onValueChanged(oldValue, newValue, modelLayerFactory, collection);
					}
				}
			}

			configManager.updateProperties(CONFIG_FILE_PATH, updateConfigValueAlteration(config, newValue));

			for (BatchProcess batchProcess : batchProcesses) {
				batchProcessesManager.markAsPending(batchProcess);
			}

		} catch (RuntimeException e) {
			if (listener != null) {
				listener.onValueChanged(newValue, oldValue, modelLayerFactory);

				for (String collection : modelLayerFactory.getCollectionsListManager().getCollections()) {
					if (module == null || modulesManager.isModuleEnabled(collection, module)) {
						listener.onValueChanged(newValue, oldValue, modelLayerFactory, collection);
					}
				}
			}
			for (BatchProcess batchProcess : batchProcesses) {

				batchProcessesManager.cancelStandByBatchProcess(batchProcess);
			}

			throw new SystemConfigurationsManagerRuntimeException_UpdateScriptFailed(config.getCode(), newValue, e);
		}

	}

	private PropertiesAlteration updateConfigValueAlteration(final SystemConfiguration config, final Object newValue) {
		return new PropertiesAlteration() {
			@Override
			public void alter(Map<String, String> properties) {
				if (LangUtils.isEqual(newValue, config.getDefaultValue())) {
					properties.remove(getPropertyKey(config));
				} else {
					properties.put(getPropertyKey(config), SystemConfigurationsManager.this.toString(config, newValue));
				}
			}
		};
	}

	List<BatchProcess> startBatchProcessesToReindex(SystemConfiguration config) {
		List<BatchProcess> batchProcesses = new ArrayList<>();
		for (String collection : modelLayerFactory.getCollectionsListManager().getCollections()) {
			MetadataSchemaTypes types = modelLayerFactory.getMetadataSchemasManager().getSchemaTypes(collection);
			for (String typeCode : types.getSchemaTypesSortedByDependency()) {
				MetadataSchemaType type = types.getSchemaType(typeCode);
				List<Metadata> metadatasToReindex = findMetadatasToReindex(type, config);
				if (!metadatasToReindex.isEmpty()) {
					batchProcesses.addAll(startBatchProcessesToReindex(metadatasToReindex, type));
				}
			}
		}
		return batchProcesses;
	}

	List<BatchProcess> startBatchProcessesToReindex(List<Metadata> metadatasToReindex, MetadataSchemaType type) {

		List<BatchProcess> batchProcesses = new ArrayList<>();
		BatchProcessesManager batchProcessesManager = modelLayerFactory.getBatchProcessesManager();
		SearchServices searchServices = modelLayerFactory.newSearchServices();
		List<String> schemaCodes = new SchemaUtils().toMetadataCodes(metadatasToReindex);
		BatchProcessAction action = new ReindexMetadatasBatchProcessAction(schemaCodes);
		Iterator<String> allRecordsIterator = searchServices.recordsIdsIterator(new LogicalSearchQuery(from(type).returnAll()));
		BatchBuilderIterator<String> batchBuilderIterator = new BatchBuilderIterator<>(allRecordsIterator, 100000);
		while (batchBuilderIterator.hasNext()) {
			List<String> records = batchBuilderIterator.next();
			LOGGER.info("Reindexing " + schemaCodes + " on records " + records);
			batchProcesses.add(batchProcessesManager.add(records, type.getCollection(), action));
		}
		return batchProcesses;
	}

	private List<Metadata> findMetadatasToReindex(MetadataSchemaType schemaType, SystemConfiguration systemConfiguration) {
		Set<Metadata> reindexedMetadatas = new HashSet<>();
		for (Metadata metadata : schemaType.getCalculatedMetadatas()) {
			for (Dependency dependency : ((CalculatedDataEntry) metadata.getDataEntry()).getCalculator().getDependencies()) {
				if (dependency instanceof ConfigDependency) {
					ConfigDependency configDependency = (ConfigDependency) dependency;
					if (configDependency.getConfiguration().equals(systemConfiguration)) {
						reindexedMetadatas.add(metadata);
					}
				}
			}
		}
		return new ArrayList<>(reindexedMetadatas);
	}

	public void validate(SystemConfiguration config, Object newValue, ValidationErrors errors) {
		SystemConfigurationScript<Object> listener = getInstanciatedScriptFor(config);
		if (listener != null) {
			listener.validate(newValue, errors);
		}
	}

	public void reset(final SystemConfiguration config) {
		setValue(config, config.getDefaultValue());

		configManager.updateProperties(CONFIG_FILE_PATH, new PropertiesAlteration() {
			@Override
			public void alter(Map<String, String> properties) {
				properties.remove(getPropertyKey(config));
			}
		});

	}

	private String toString(SystemConfiguration config, Object value) {
		if (value == null) {
			return null;
		}
		switch (config.getType()) {

		case STRING:
			return value.toString();
		case BOOLEAN:
			return ((Boolean) value) ? "true" : "false";
		case INTEGER:
			return "" + value;
		case ENUM:
			return ((Enum<?>) value).name();
		}
		throw new ImpossibleRuntimeException("Unsupported config type : " + config.getType());
	}

	private Object toObject(SystemConfiguration config, String value) {
		if (value == null) {
			return null;
		}
		switch (config.getType()) {

		case STRING:
			return value;
		case BOOLEAN:
			return "true".equals(value);
		case INTEGER:
			return Integer.valueOf(value);
		case ENUM:
			return EnumUtils.getEnum((Class) config.getEnumClass(), value);
		}
		throw new ImpossibleRuntimeException("Unsupported config type : " + config.getType());
	}

	private String getPropertyKey(SystemConfiguration config) {
		return config.getConfigGroupCode() + "_" + config.getCode();
	}

	public <T> T getValue(SystemConfiguration config) {
		String propertyKey = getPropertyKey(config);

		if (configValues.getProperties().containsKey(propertyKey)) {
			String value = configValues.getProperties().get(propertyKey);
			return (T) toObject(config, value);
		} else {
			return (T) config.getDefaultValue();
		}
	}

	@Override
	public void onConfigUpdated(String configPath) {
		reloadConfigValues();
	}

	public SystemConfigurationScript<Object> getInstanciatedScriptFor(SystemConfiguration config) {
		if (config.getScriptClass() != null) {
			return new InstanciationUtils()
					.instanciateWithoutExpectableExceptions(config.getScriptClass());
		} else {
			return null;
		}
	}

	public List<SystemConfigurationGroup> getConfigurationGroups() {

		List<SystemConfigurationGroup> groups = new ArrayList<>();
		for (SystemConfiguration config : getAllConfigurations()) {
			SystemConfigurationGroup group = new SystemConfigurationGroup(config.getModule(), config.getConfigGroupCode());
			if (!groups.contains(group)) {
				groups.add(group);
			}
		}

		return groups;
	}

	public List<SystemConfiguration> getAllConfigurations() {
		List<SystemConfiguration> configurations = new ArrayList<>();
		configurations.addAll(ConstellioEIMConfigs.getCoreConfigs());
		for (Module module : constellioModulesManagerDelayed.get().getInstalledModules()) {
			configurations.addAll(module.getConfigurations());
		}

		return configurations;
	}

	public SystemConfiguration getConfigurationWithCode(String code) {

		for (SystemConfiguration config : getAllConfigurations()) {
			if (config.getCode().equals(code)) {
				return config;
			}

		}

		return null;
	}

	public List<SystemConfiguration> getGroupConfigurations(SystemConfigurationGroup wantedGroup) {
		List<SystemConfiguration> configs = new ArrayList<>();
		for (SystemConfiguration config : getAllConfigurations()) {
			SystemConfigurationGroup group = new SystemConfigurationGroup(config.getModule(), config.getConfigGroupCode());
			if (group.equals(wantedGroup)) {
				configs.add(config);
			}
		}

		return configs;
	}

	public List<SystemConfiguration> getGroupConfigurationsWithCode(String code) {
		List<SystemConfiguration> configs = new ArrayList<>();
		for (SystemConfiguration config : getAllConfigurations()) {
			SystemConfigurationGroup group = new SystemConfigurationGroup(config.getModule(), config.getConfigGroupCode());
			if (group.getCode().equals(code)) {
				configs.add(config);
			}
		}

		return configs;
	}
}
