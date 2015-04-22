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
package com.constellio.app.services.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_CannotCreateCollectionRecord;
import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_CannotMigrateCollection;
import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_CannotRemoveCollection;
import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_CollectionLanguageMustIncludeSystemMainDataLanguage;
import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_CollectionNotFound;
import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_CollectionWithGivenCodeAlreadyExists;
import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_InvalidCode;
import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_InvalidLanguage;
import com.constellio.app.services.extensions.ConstellioModulesManagerImpl;
import com.constellio.app.services.migrations.MigrationServices;
import com.constellio.data.dao.dto.records.RecordsFlushing;
import com.constellio.data.dao.dto.records.TransactionDTO;
import com.constellio.data.dao.managers.StatefulService;
import com.constellio.data.dao.managers.config.ConfigManager;
import com.constellio.data.dao.managers.config.ConfigManagerException.OptimisticLockingConfiguration;
import com.constellio.data.dao.services.bigVault.RecordDaoException.OptimisticLocking;
import com.constellio.data.dao.services.factories.DataLayerFactory;
import com.constellio.data.utils.Delayed;
import com.constellio.model.entities.Language;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.Collection;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.services.collections.CollectionsListManager;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.records.RecordServicesRuntimeException;

public class CollectionsManager implements StatefulService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionsManager.class);

	private final Delayed<MigrationServices> migrationServicesDelayed;

	private final CollectionsListManager collectionsListManager;

	private final ConstellioModulesManagerImpl constellioModulesManager;

	private final ModelLayerFactory modelLayerFactory;

	private final DataLayerFactory dataLayerFactory;

	private final String mainDataLanguage;

	private Map<String, List<String>> collectionLanguagesCache = new HashMap<>();

	public CollectionsManager(ModelLayerFactory modelLayerFactory, ConstellioModulesManagerImpl constellioModulesManager,
			Delayed<MigrationServices> migrationServicesDelayed, String mainDataLanguage) {
		this.modelLayerFactory = modelLayerFactory;
		this.constellioModulesManager = constellioModulesManager;
		this.collectionsListManager = modelLayerFactory.getCollectionsListManager();
		this.dataLayerFactory = modelLayerFactory.getDataLayerFactory();
		this.mainDataLanguage = mainDataLanguage;
		this.migrationServicesDelayed = migrationServicesDelayed;
	}

	@Override
	public void initialize() {
		// No initialization required.
	}

	public List<String> getCollectionCodes() {
		return collectionsListManager.getCollections();
	}

	void addGlobalGroupsInCollection(String code) {
		modelLayerFactory.newUserServices().addGlobalGroupsInCollection(code);
	}

	Record createCollectionRecordWithCode(String code, List<String> languages) {
		RecordServices recordServices = modelLayerFactory.newRecordServices();
		Record record = recordServices.newRecordWithSchema(collectionSchema(code));
		record.set(collectionCodeMetadata(code), code);
		record.set(collectionNameMetadata(code), code);
		record.set(collectionLanguages(code), languages);
		try {
			recordServices.add(record);
			return record;
		} catch (RecordServicesException e) {
			throw new CollectionsManagerRuntimeException_CannotCreateCollectionRecord(code, e);
		}
	}

	MetadataSchema collectionSchema(String collection) {
		MetadataSchemaTypes types = modelLayerFactory.getMetadataSchemasManager().getSchemaTypes(collection);
		return types.getSchema(Collection.SCHEMA_TYPE + "_default");
	}

	private Metadata collectionCodeMetadata(String collection) {
		return collectionSchema(collection).getMetadata(Collection.CODE);
	}

	private Metadata collectionNameMetadata(String collection) {
		return collectionSchema(collection).getMetadata(Collection.NAME);
	}

	private Metadata collectionLanguages(String collection) {
		return collectionSchema(collection).getMetadata(Collection.LANGUAGES);
	}

	public void createCollectionConfigs(String code) {
		modelLayerFactory.getMetadataSchemasManager().createCollectionSchemas(code);
		modelLayerFactory.getTaxonomiesManager().createCollectionTaxonomies(code);
		modelLayerFactory.getAuthorizationDetailsManager().createCollectionAuthorizationDetail(code);
		modelLayerFactory.getRolesManager().createCollectionRole(code);
		modelLayerFactory.getWorkflowsConfigManager().createCollectionWorkflows(code);
		modelLayerFactory.getWorkflowExecutionIndexManager().createCollectionWorkflowsExecutionIndex(code);
	}

	public Collection getCollection(String code) {
		try {
			Record record = modelLayerFactory.newRecordServices().getDocumentById(code);
			MetadataSchemaTypes types = modelLayerFactory.getMetadataSchemasManager().getSchemaTypes(code);
			return new Collection(record, types);
		} catch (RecordServicesRuntimeException.NoSuchRecordWithId e) {
			throw new CollectionsManagerRuntimeException_CollectionNotFound(code, e);
		}
	}

	public void deleteCollection(final String collection) {
		ConfigManager configManager = dataLayerFactory.getConfigManager();
		removeCollectionFromUserCredentials(collection);
		removeCollectionFromGlobalGroups(collection);
		removeFromCollectionsListManager(collection);
		removeCollectionFromBigVault(collection);
		removeCollectionFromVersionProperties(collection, configManager);
		removeRemoveAllConfigsOfCollection(collection, configManager);
	}

	private void removeRemoveAllConfigsOfCollection(final String collection, ConfigManager configManager) {
		configManager.deleteAllConfigsIn("/" + collection);
	}

	private void removeCollectionFromBigVault(final String collection) {
		TransactionDTO transactionDTO = newTransactionDTO();
		ModifiableSolrParams params = newModifiableSolrParams();
		params.set("q", "collection_s:" + collection);
		transactionDTO = transactionDTO.withDeletedByQueries(params);
		try {
			dataLayerFactory.newRecordDao().execute(transactionDTO);
		} catch (OptimisticLocking optimisticLocking) {
			throw new CollectionsManagerRuntimeException_CannotRemoveCollection(collection, optimisticLocking);
		}
	}

	private void removeCollectionFromVersionProperties(final String collection, ConfigManager configManager) {
		constellioModulesManager.removeCollectionFromVersionProperties(collection, configManager);
	}

	private void removeCollectionFromUserCredentials(final String collection) {
		modelLayerFactory.getUserCredentialsManager().removeCollection(collection);
	}

	private void removeCollectionFromGlobalGroups(final String collection) {
		modelLayerFactory.getGlobalGroupsManager().removeCollection(collection);
	}

	private void removeFromCollectionsListManager(final String collection) {
		collectionsListManager.remove(collection);
	}

	// private PropertiesAlteration newRemoveCollectionPropertiesAlteration(final String collection) {
	// return new PropertiesAlteration() {
	// @Override
	// public void alter(Map<String, String> properties) {
	// if (properties.containsKey(collection + "_version")) {
	// properties.remove(collection + "_version");
	// }
	// }
	// };
	// }

	TransactionDTO newTransactionDTO() {
		return new TransactionDTO(RecordsFlushing.NOW);
	}

	ModifiableSolrParams newModifiableSolrParams() {
		return new ModifiableSolrParams();
	}

	public List<String> getCollectionLanguages(final String collection) {

		List<String> collectionLanguages = collectionLanguagesCache.get(collection);

		if (collectionLanguages == null) {
			try {
				collectionLanguages = getCollection(collection).getLanguages();
				collectionLanguagesCache.put(collection, collectionLanguages);
			} catch (CollectionsManagerRuntimeException_CollectionNotFound e) {
				LOGGER.debug("Collection '" + collection + "' not found.", e);
				return Collections.emptyList();
			}
		}
		return collectionLanguages;
	}

	@Override
	public void close() {
		// No finalization required.
	}

	public Record createCollectionInCurrentVersion(String code, List<String> languages) {
		return createCollectionInVersion(code, languages, null);
	}

	public Record createCollectionInVersion(String code, List<String> languages, String version) {
		validateCode(code);

		if (collectionsListManager.getCollections().contains(code)) {
			throw new CollectionsManagerRuntimeException_CollectionWithGivenCodeAlreadyExists(code);
		}

		if (!languages.contains(mainDataLanguage)) {
			throw new CollectionsManagerRuntimeException_CollectionLanguageMustIncludeSystemMainDataLanguage(mainDataLanguage);
		}

		for (String language : languages) {
			if (!Language.isSupported(language)) {
				throw new CollectionsManagerRuntimeException_InvalidLanguage(language);
			}
		}

		createCollectionConfigs(code);
		collectionsListManager.addCollection(code, languages);
		try {
			migrationServicesDelayed.get().migrate(code, version);
		} catch (OptimisticLockingConfiguration optimisticLockingConfiguration) {
			throw new CollectionsManagerRuntimeException_CannotMigrateCollection(
					code, version, optimisticLockingConfiguration);
		}
		Record collectionRecord = createCollectionRecordWithCode(code, languages);
		addGlobalGroupsInCollection(code);
		return collectionRecord;
	}

	private void validateCode(String code) {
		String pattern = "([a-zA-Z0-9])+";
		if (code == null || !code.matches(pattern)) {
			throw new CollectionsManagerRuntimeException_InvalidCode(code);
		}
	}
}
