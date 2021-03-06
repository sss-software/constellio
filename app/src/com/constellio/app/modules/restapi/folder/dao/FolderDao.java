package com.constellio.app.modules.restapi.folder.dao;

import com.constellio.app.modules.restapi.core.exception.InvalidParameterException;
import com.constellio.app.modules.restapi.core.exception.OptimisticLockException;
import com.constellio.app.modules.restapi.core.exception.RecordCopyNotPermittedException;
import com.constellio.app.modules.restapi.core.exception.UnresolvableOptimisticLockException;
import com.constellio.app.modules.restapi.folder.dto.FolderDto;
import com.constellio.app.modules.restapi.resource.dao.ResourceDao;
import com.constellio.app.modules.restapi.resource.dto.BaseReferenceDto;
import com.constellio.app.modules.rm.ConstellioRMModule;
import com.constellio.app.modules.rm.extensions.api.RMModuleExtensions;
import com.constellio.app.modules.rm.model.enums.CopyType;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.services.decommissioning.DecommissioningService;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.modules.rm.wrappers.type.FolderType;
import com.constellio.app.modules.rm.wrappers.type.MediumType;
import com.constellio.data.dao.dto.records.OptimisticLockingResolution;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.services.records.RecordServicesException;

import java.util.ArrayList;
import java.util.List;

public class FolderDao extends ResourceDao {

	public Record createFolder(User user, MetadataSchema folderSchema, FolderDto folderDto, String flush)
			throws Exception {
		Transaction transaction = buildTransaction(flush, user);

		Record folderRecord = recordServices.newRecordWithSchema(folderSchema);

		Record folderTypeRecord = getResourceTypeRecord(folderDto.getType(), folderSchema.getCollection());

		updateFolderMetadataValues(folderRecord, folderTypeRecord, folderSchema, folderDto, false);
		updateCustomMetadataValues(folderRecord, folderSchema, folderDto.getExtendedAttributes(), false);

		transaction.add(folderRecord);

		recordServices.execute(transaction);

		return folderRecord;
	}

	public Record copyFolder(User user, MetadataSchema folderSchema, String sourceFolderId, FolderDto folderDto,
							 String flush) throws Exception {
		Record sourceFolderRecord = recordServices.getDocumentById(sourceFolderId);
		String collection = sourceFolderRecord.getCollection();
		RMSchemasRecordsServices rmSchemas =
				new RMSchemasRecordsServices(sourceFolderRecord.getCollection(), appLayerFactory);
		Folder sourceFolder = rmSchemas.wrapFolder(sourceFolderRecord);

		RMModuleExtensions rmModuleExtensions = appLayerFactory.getExtensions().forCollection(collection)
				.forModule(ConstellioRMModule.ID);
		if (!rmModuleExtensions.isCopyActionPossibleOnFolder(sourceFolder, user)) {
			throw new RecordCopyNotPermittedException(sourceFolderId);
		}

		Transaction transaction = buildTransaction(flush, user);

		DecommissioningService decommissioningService = new DecommissioningService(collection, appLayerFactory);
		Folder copyFolder = decommissioningService.duplicateStructureAndDocuments(sourceFolder, user, false);
		if (folderDto != null) {
			updateFolderMetadataValues(copyFolder.getWrappedRecord(), null, folderSchema, folderDto, true);
			updateCustomMetadataValues(copyFolder.getWrappedRecord(), folderSchema, folderDto.getExtendedAttributes(), true);
		}

		transaction.add(copyFolder);

		recordServices.execute(transaction);

		return copyFolder.getWrappedRecord();
	}

	public Record updateFolder(User user, Record folderRecord, MetadataSchema folderSchema, FolderDto folder,
							   boolean partial, String flushMode) throws Exception {
		Transaction transaction = buildTransaction(flushMode, user);

		if (folder.getETag() != null) {
			transaction.setOptimisticLockingResolution(OptimisticLockingResolution.EXCEPTION);
		}

		if (!partial || folder.getType() != null) {
			MetadataSchema oldFolderSchema = getMetadataSchema(folderRecord);
			if (!folderSchema.getCode().equals(oldFolderSchema.getCode())) {
				folderRecord.changeSchema(oldFolderSchema, folderSchema);
			}
		}
		Record folderTypeRecord;
		if (partial && folder.getType() == null) {
			String folderTypeId = getMetadataValue(folderRecord, Folder.TYPE);
			folderTypeRecord = folderTypeId != null ? getRecordById(folderTypeId) : null;
		} else {
			folderTypeRecord = getResourceTypeRecord(folder.getType(), folderRecord.getCollection());
		}

		updateFolderMetadataValues(folderRecord, folderTypeRecord, folderSchema, folder, partial);
		updateCustomMetadataValues(folderRecord, folderSchema, folder.getExtendedAttributes(), partial);

		transaction.add(folderRecord);

		try {
			recordServices.execute(transaction);
		} catch (RecordServicesException.UnresolvableOptimisticLockingConflict e) {
			throw new UnresolvableOptimisticLockException(folder.getId());
		} catch (RecordServicesException.OptimisticLocking e) {
			throw new OptimisticLockException(folder.getId(), folder.getETag(), e.getVersion());
		}

		return folderRecord;
	}

	private void updateFolderMetadataValues(Record folderRecord, Record folderTypeRecord, MetadataSchema schema,
											FolderDto folderDto, boolean partial) {
		String collection = folderRecord.getCollection();

		updateDocumentMetadataValue(folderRecord, schema, Folder.PARENT_FOLDER, folderDto.getParentFolderId(), partial);
		updateDocumentMetadataValue(folderRecord, schema, Folder.CATEGORY_ENTERED, getIdOrNull(folderDto.getCategory()), partial);
		updateDocumentMetadataValue(folderRecord, schema, Folder.RETENTION_RULE_ENTERED, getIdOrNull(folderDto.getRetentionRule()), partial);
		updateDocumentMetadataValue(folderRecord, schema, Folder.ADMINISTRATIVE_UNIT_ENTERED, getIdOrNull(folderDto.getAdministrativeUnit()), partial);
		updateDocumentMetadataValue(folderRecord, schema, Folder.MAIN_COPY_RULE_ID_ENTERED, folderDto.getMainCopyRule(), partial);
		updateDocumentMetadataValue(folderRecord, schema, Folder.COPY_STATUS_ENTERED, toCopyType(folderDto.getCopyStatus()), partial);
		updateDocumentMetadataValue(folderRecord, schema, Folder.MEDIUM_TYPES, toMediumTypeIds(collection, folderDto.getMediumTypes()), partial);
		updateDocumentMetadataValue(folderRecord, schema, Folder.CONTAINER, getIdOrNull(folderDto.getContainer()), partial);
		updateDocumentMetadataValue(folderRecord, schema, Folder.TITLE, folderDto.getTitle(), partial);
		updateDocumentMetadataValue(folderRecord, schema, Folder.DESCRIPTION, folderDto.getDescription(), partial);
		updateDocumentMetadataValue(folderRecord, schema, Folder.KEYWORDS, folderDto.getKeywords(), partial);
		updateDocumentMetadataValue(folderRecord, schema, Folder.OPENING_DATE, folderDto.getOpeningDate(), partial);
		updateDocumentMetadataValue(folderRecord, schema, Folder.ENTERED_CLOSING_DATE, folderDto.getClosingDate(), partial);
		updateDocumentMetadataValue(folderRecord, schema, Folder.ACTUAL_TRANSFER_DATE, folderDto.getActualTransferDate(), partial);
		updateDocumentMetadataValue(folderRecord, schema, Folder.ACTUAL_DEPOSIT_DATE, folderDto.getActualDepositDate(), partial);
		updateDocumentMetadataValue(folderRecord, schema, Folder.ACTUAL_DESTRUCTION_DATE, folderDto.getActualDestructionDate(), partial);

		String folderTypeId = folderTypeRecord != null ? folderTypeRecord.getId() : null;
		updateMetadataValue(folderRecord, schema, Folder.TYPE, folderTypeId);
	}

	private CopyType toCopyType(String copyStatus) {
		if (copyStatus == null) {
			return null;
		}

		for (CopyType copyType : CopyType.values()) {
			if (copyType.getCode().equals(copyStatus)) {
				return copyType;
			}
		}
		throw new InvalidParameterException("folder.copyType", copyStatus);
	}

	private List<String> toMediumTypeIds(String collection, List<String> mediumTypeCodes) {
		if (mediumTypeCodes == null) {
			return null;
		}

		RMSchemasRecordsServices rm = new RMSchemasRecordsServices(collection, appLayerFactory);

		List<String> ids = new ArrayList<>();
		for (String mediumTypeCode : mediumTypeCodes) {
			MediumType mediumType = rm.getMediumTypeByCode(mediumTypeCode);
			if (mediumType == null) {
				throw new InvalidParameterException("folder.mediumTypes", mediumTypeCode);
			}
			ids.add(mediumType.getId());
		}
		return ids;
	}

	private static String getIdOrNull(BaseReferenceDto baseReferenceDto) {
		if (baseReferenceDto == null) {
			return null;
		}

		return baseReferenceDto.getId();
	}

	@Override
	protected String getResourceSchemaType() {
		return Folder.SCHEMA_TYPE;
	}

	@Override
	protected String getResourceTypeSchemaType() {
		return FolderType.SCHEMA_TYPE;
	}
}
