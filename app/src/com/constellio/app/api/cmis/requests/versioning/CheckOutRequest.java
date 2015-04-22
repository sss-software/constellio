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
package com.constellio.app.api.cmis.requests.versioning;

import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.api.cmis.ConstellioCmisException;
import com.constellio.app.api.cmis.binding.collection.ConstellioCollectionRepository;
import com.constellio.app.api.cmis.binding.global.ConstellioCmisContextParameters;
import com.constellio.app.api.cmis.binding.utils.CmisContentUtils;
import com.constellio.app.api.cmis.binding.utils.ContentCmisDocument;
import com.constellio.app.api.cmis.requests.CmisCollectionRequest;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.model.entities.records.Content;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesException;

public class CheckOutRequest extends CmisCollectionRequest<Boolean> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CheckOutRequest.class);
	String repositoryId;
	Holder<String> objectId;
	ExtensionsData extension;
	Holder<Boolean> contentCopied;
	private CallContext context;

	public CheckOutRequest(ConstellioCollectionRepository repository, CallContext context,
			AppLayerFactory appLayerFactory,
			String repositoryId,
			Holder<String> objectId, ExtensionsData extension, Holder<Boolean> contentCopied) {
		super(repository, appLayerFactory);
		this.context = context;
		this.repositoryId = repositoryId;
		this.objectId = objectId;
		this.extension = extension;
		this.contentCopied = contentCopied;
	}

	@Override
	protected Boolean process()
			throws ConstellioCmisException {
		RecordServices recordServices = modelLayerFactory.newRecordServices();
		MetadataSchemaTypes types = modelLayerFactory.getMetadataSchemasManager().getSchemaTypes(repository.getCollection());
		ContentCmisDocument content = CmisContentUtils.getContent(objectId.getValue(), recordServices, types);

		Content pwcContent = content.getContent().checkOut((User) context.get(ConstellioCmisContextParameters.USER));
		ContentCmisDocument updatedContent = CmisContentUtils.getContent(objectId.getValue(), recordServices, types);

		try {
			recordServices.update(content.getRecord());
		} catch (RecordServicesException e) {
			throw new ConstellioCmisException.ConstellioCmisException_RecordServicesError(e);
		}

		objectId.setValue(updatedContent.getPrivateWorkingCopyVersionId());

		return true;
	}

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
