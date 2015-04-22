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
package com.constellio.model.services.schemas.validators;

import java.util.HashMap;
import java.util.Map;

import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.frameworks.validation.ValidationErrors;
import com.constellio.model.frameworks.validation.Validator;

public class MetadataSchemaTypeValidator implements Validator<MetadataSchemaType> {

	private static final String NO_TYPE_IN_METADATA_WITHOUT_INHERITANCE = "NoTypeInNonInheritedMetadata";
	private static final String NO_LABEL_IN_METADATA_WITHOUT_INHERITANCE = "NoLabelInNonInheritedMetadata";
	private static final String ALLOWED_REFERENCES_IN_NON_REFERENCE_METADATA = "AllowedReferencesInNonReferenceMetadata";
	private static final String ALLOWED_REFERENCES_IN_REFERENCE_METADATA_NOT_SPECIFIED = "NoAllowedReferencesInReferenceMetadata";
	//TODO Valider que 2 métadonnées de profil custom sans héritage ne peuvent avoir le même nom

	@Override
	public void validate(MetadataSchemaType schemaType, ValidationErrors validationErrors) {
		validateDefaultSchema(schemaType.getDefaultSchema(), validationErrors);
		for (MetadataSchema customSchema : schemaType.getSchemas()) {
			validateCustomSchema(customSchema, validationErrors);
		}
	}

	void validateDefaultSchema(MetadataSchema defaultSchema, ValidationErrors validationErrors) {
		for (Metadata metadata : defaultSchema.getMetadatas()) {
			validateMetadata(metadata, validationErrors);
		}
	}

	void validateMetadata(Metadata metadata, ValidationErrors validationErrors) {
		if (metadata.getInheritance() == null) {
			validateMetadataBasicInfo(metadata, validationErrors);
		}
		validateReferenceMetadata(metadata, validationErrors);
	}

	void validateMetadataBasicInfo(Metadata metadata, ValidationErrors validationErrors) {
		validateMetadataLabelNotNull(metadata, validationErrors);
		validateMetadataTypeNotNull(metadata, validationErrors);
	}

	void validateMetadataTypeNotNull(Metadata metadata, ValidationErrors validationErrors) {
		if (metadata.getType() == null) {
			validationErrors.add(getClass(), NO_TYPE_IN_METADATA_WITHOUT_INHERITANCE, createMapWithCodeAndLabel(metadata));
		}
	}

	void validateMetadataLabelNotNull(Metadata metadata, ValidationErrors validationErrors) {
		if (metadata.getLabel() == null) {
			validationErrors.add(getClass(), NO_LABEL_IN_METADATA_WITHOUT_INHERITANCE, createMapWithCodeAndLabel(metadata));
		}
	}

	Map<String, String> createMapWithCodeAndLabel(Metadata metadata) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("localCode", metadata.getLocalCode());
		parameters.put("label", metadata.getLabel());
		return parameters;
	}

	void validateCustomSchema(MetadataSchema customSchema, ValidationErrors validationErrors) {
		for (Metadata customMetadata : customSchema.getMetadatas()) {
			validateMetadata(customMetadata, validationErrors);
		}
	}

	void validateReferenceMetadata(Metadata metadata, ValidationErrors validationErrors) {
		if (isReferenceType(metadata) && !hasAllowedReferences(metadata)) {
			addNoAllowedReferencesInReferenceMetadataError(metadata, validationErrors);
		} else if (!isReferenceType(metadata) && hasAllowedReferences(metadata)) {
			addAllowedReferencesInNonReferenceMetadataError(metadata, validationErrors);
		}
	}

	private boolean hasAllowedReferences(Metadata metadata) {
		return !metadata.getAllowedReferences().getAllowedSchemas().isEmpty();
	}

	private boolean isReferenceType(Metadata metadata) {
		return metadata.getType() == MetadataValueType.REFERENCE;
	}

	private void addAllowedReferencesInNonReferenceMetadataError(Metadata metadata, ValidationErrors validationErrors) {
		Map<String, String> parameters = createMapWithCodeLabelAndType(metadata);
		validationErrors.add(getClass(), ALLOWED_REFERENCES_IN_NON_REFERENCE_METADATA, parameters);
	}

	private void addNoAllowedReferencesInReferenceMetadataError(Metadata metadata, ValidationErrors validationErrors) {
		Map<String, String> parameters = createMapWithCodeAndLabel(metadata);
		validationErrors.add(getClass(), ALLOWED_REFERENCES_IN_REFERENCE_METADATA_NOT_SPECIFIED, parameters);
	}

	Map<String, String> createMapWithCodeLabelAndType(Metadata metadata) {
		Map<String, String> parameters = createMapWithCodeAndLabel(metadata);
		parameters.put("type", metadata.getType().toString());
		return parameters;
	}
}
