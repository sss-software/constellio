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
package com.constellio.model.api.impl.schemas.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.entities.schemas.validation.RecordValidator;
import com.constellio.model.frameworks.validation.ValidationErrors;

public class CreationDateIsBeforeOrEqualToLastModificationDateValidator implements RecordValidator {

	public static final String MODIFICATION_DATE_MESSAGE_PARAM = "modificationDate";

	public static final String CREATION_DATE_MESSAGE_PARAM = "creationDate";

	public static final String CREATION_DATE_IS_AFTER_MODIFICATION_DATE = "creationDateIsAfterModificationDate";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CreationDateIsBeforeOrEqualToLastModificationDateValidator.class);

	@Override
	public void validate(Record record, MetadataSchemaTypes types, MetadataSchema schema, ValidationErrors validationErrors) {

		Metadata creationDateMetadata = schema.getMetadata("creationDate");
		Metadata modificationDateMetadata = schema.getMetadata("modificationDate");

		LocalDateTime creationDate = record.get(creationDateMetadata);
		LocalDateTime modificationDate = record.get(modificationDateMetadata);
		validate(creationDate, modificationDate, validationErrors);

	}

	private void validate(LocalDateTime creationDate, LocalDateTime modificationDate, ValidationErrors validationErrors) {

		if (creationDate != null && modificationDate != null && creationDate.isAfter(modificationDate)) {

			Map<String, String> parameters = new HashMap<>();
			parameters.put(CREATION_DATE_MESSAGE_PARAM, creationDate.toString());
			parameters.put(MODIFICATION_DATE_MESSAGE_PARAM, modificationDate.toString());
			validationErrors.add(getClass(), CREATION_DATE_IS_AFTER_MODIFICATION_DATE, parameters);
		}
	}
}
