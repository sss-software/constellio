package com.constellio.app.modules.restapi.folder;

import com.constellio.app.modules.restapi.ace.AceService;
import com.constellio.app.modules.restapi.core.dao.BaseDao;
import com.constellio.app.modules.restapi.core.util.SchemaTypes;
import com.constellio.app.modules.restapi.folder.adaptor.FolderAdaptor;
import com.constellio.app.modules.restapi.folder.dao.FolderDao;
import com.constellio.app.modules.restapi.folder.dto.FolderDto;
import com.constellio.app.modules.restapi.resource.adaptor.ResourceAdaptor;
import com.constellio.app.modules.restapi.resource.service.ResourceService;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.MetadataSchema;

import javax.inject.Inject;
import java.util.Set;

import static com.constellio.app.modules.restapi.core.util.ListUtils.isNullOrEmpty;
import static com.constellio.app.modules.restapi.core.util.ListUtils.nullToEmpty;

public class FolderService extends ResourceService {

	@Inject
	private FolderDao folderDao;
	@Inject
	private FolderAdaptor folderAdaptor;
	@Inject
	private AceService aceService;

	public FolderDto get(String host, String id, String serviceKey, String method, String date, int expiration,
						 String signature, Set<String> filters) throws Exception {
		return getResource(host, id, serviceKey, method, date, expiration, signature, filters);
	}

	public FolderDto update(String host, String id, String serviceKey, String method, String date, int expiration,
							String signature, FolderDto folder, boolean partial,
							String flushMode, Set<String> filters) throws Exception {
		validateParameters(host, id, serviceKey, method, date, expiration, null, null, null, signature);

		Record folderRecord = getRecord(id, true);
		if (folder.getETag() != null) {
			validateETag(id, folder.getETag(), folderRecord.getVersion());
		}

		User user = getUser(serviceKey, folderRecord.getCollection());
		validateUserAccess(user, folderRecord, method);

		MetadataSchema folderSchema;
		if (partial && folder.getType() == null) {
			String folderTypeId = folderDao.getMetadataValue(folderRecord, Folder.TYPE);
			Record folderTypeRecord = folderTypeId != null ? getRecord(folderTypeId, true) : null;
			folderSchema = folderDao.getResourceMetadataSchema(folderTypeRecord, folderRecord.getCollection());
		} else {
			folderSchema = folderDao.getLinkedMetadataSchema(folder.getType(), folderRecord.getCollection());
		}

		validateExtendedAttributes(folder.getExtendedAttributes(), folderSchema);
		validateAuthorizations(folder.getDirectAces(), folderRecord.getCollection());

		boolean acesModified = false;
		folderRecord = folderDao.updateFolder(user, folderRecord, folderSchema, folder, partial, flushMode);
		if (!partial || folder.getDirectAces() != null) {
			acesModified = aceService.updateAces(user, folderRecord, nullToEmpty(folder.getDirectAces()));
		}

		return getAdaptor().adapt(folder, folderRecord, folderSchema, acesModified, filters);
	}

	public FolderDto create(String host, String parentFolderId, String serviceKey, String method, String date,
							int expiration, String signature, FolderDto folderDto, String flushMode,
							Set<String> filters) throws Exception {
		validateParameters(host, parentFolderId, serviceKey, method, date, expiration, null, null, null, signature);

		String id = parentFolderId != null ? parentFolderId : folderDto.getAdministrativeUnit().getId();
		Record record = getRecord(id, true);

		String collection = record.getCollection();
		User user = getUser(serviceKey, collection);
		validateUserAccess(user, record, method);

		MetadataSchema folderSchema = folderDao.getLinkedMetadataSchema(folderDto.getType(), collection);
		validateExtendedAttributes(folderDto.getExtendedAttributes(), folderSchema);
		validateAuthorizations(folderDto.getDirectAces(), collection);

		boolean acesModified = false;
		Record createdFolderRecord = folderDao.createFolder(user, folderSchema, folderDto, flushMode);
		if (!isNullOrEmpty(folderDto.getDirectAces())) {
			aceService.addAces(user, createdFolderRecord, folderDto.getDirectAces());
			acesModified = true;
		}
		return getAdaptor().adapt(folderDto, createdFolderRecord, folderSchema, acesModified, filters);
	}

	public FolderDto copy(String host, String parentFolderId, String copySourceId, String serviceKey, String method,
						  String date, int expiration, String signature, FolderDto folderDto, String flushMode,
						  Set<String> filters) throws Exception {
		validateParameters(host, parentFolderId, serviceKey, method, date, expiration, null, null, copySourceId, signature);

		Record sourceFolder = getRecord(copySourceId, true);
		String collection = sourceFolder.getCollection();
		User user = getUser(serviceKey, collection);
		validateUserAccess(user, sourceFolder, method);

		if (parentFolderId != null) {
			validateUserAccess(user, getRecord(parentFolderId, true), method);
		}

		MetadataSchema folderSchema = folderDto != null && folderDto.getType() != null ?
									  folderDao.getLinkedMetadataSchema(folderDto.getType(), collection) :
									  folderDao.getMetadataSchema(sourceFolder);
		if (folderDto != null) {
			validateExtendedAttributes(folderDto.getExtendedAttributes(), folderSchema);
			validateAuthorizations(folderDto.getDirectAces(), collection);
		}

		boolean acesModified = false;
		Record copiedFolderRecord = folderDao.copyFolder(user, folderSchema, copySourceId, folderDto, flushMode);
		if (folderDto != null && !isNullOrEmpty(folderDto.getDirectAces())) {
			aceService.addAces(user, copiedFolderRecord, folderDto.getDirectAces());
			acesModified = true;
		}
		return getAdaptor().adapt(folderDto, copiedFolderRecord, folderSchema, acesModified, filters);
	}

	@Override
	protected BaseDao getDao() {
		return folderDao;
	}

	@Override
	protected SchemaTypes getSchemaType() {
		return SchemaTypes.FOLDER;
	}

	@Override
	protected ResourceAdaptor<FolderDto> getAdaptor() {
		return folderAdaptor;
	}
}
