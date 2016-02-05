package com.constellio.model.entities.schemas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.constellio.model.entities.Taxonomy;
import com.constellio.model.entities.schemas.validation.RecordValidator;
import com.constellio.model.services.schemas.MetadataList;
import com.constellio.model.services.schemas.SchemaUtils;

public class MetadataSchema {

	private static final String UNDERSCORE = "_";

	private final String localCode;

	private final String code;

	private final String collection;

	private final String label;

	private final List<Metadata> automaticMetadatas;

	private final MetadataList metadatas;

	private final Boolean undeletable;

	private final boolean inTransactionLog;

	private final Set<RecordValidator> schemaValidators;

	private final Map<String, Metadata> indexByAtomicCode;

	public MetadataSchema(String localCode, String code, String collection, String label, List<Metadata> metadatas,
			Boolean undeletable, boolean inTransactionLog, Set<RecordValidator> schemaValidators,
			List<Metadata> automaticMetadatas) {
		super();
		this.localCode = localCode;
		this.code = code;
		this.collection = collection;
		this.label = label;
		this.inTransactionLog = inTransactionLog;
		this.metadatas = new MetadataList(metadatas).unModifiable();
		this.undeletable = undeletable;
		this.schemaValidators = schemaValidators;
		this.automaticMetadatas = automaticMetadatas;
		this.indexByAtomicCode = Collections.unmodifiableMap(new SchemaUtils().buildIndexByLocalCode(metadatas));
	}

	public String getLocalCode() {
		return localCode;
	}

	public String getCode() {
		return code;
	}

	public String getCollection() {
		return collection;
	}

	public String getLabel() {
		return label;
	}

	public MetadataList getMetadatas() {
		return metadatas;
	}

	public Set<RecordValidator> getValidators() {
		return schemaValidators;
	}

	public Boolean isUndeletable() {
		return undeletable;
	}

	public boolean hasMetadataWithCode(String metadataCode) {
		String localCode = new SchemaUtils().getLocalCode(metadataCode, code);

		return indexByAtomicCode.get(localCode) != null;
	}

	public Metadata get(String metadataCode) {
		return getMetadata(metadataCode);
	}

	public Metadata getMetadata(String metadataCode) {

		String localCode = new SchemaUtils().getLocalCode(metadataCode, code);

		Metadata metadata = indexByAtomicCode.get(localCode);
		if (metadata == null) {
			throw new MetadataSchemasRuntimeException.NoSuchMetadata(localCode);
		} else {
			return metadata;
		}
	}

	public List<Metadata> getAutomaticMetadatas() {
		return automaticMetadatas;
	}

	public List<Metadata> getTaxonomyRelationshipReferences(List<Taxonomy> taxonomies) {
		List<Metadata> returnedMetadata = new ArrayList<>();

		String schemaTypeCode = new SchemaUtils().getSchemaTypeCode(code);
		for (Taxonomy taxonomy : taxonomies) {
			if (!taxonomy.getSchemaTypes().contains(schemaTypeCode)) {
				for (Metadata metadata : metadatas) {
					if (metadata.isTaxonomyRelationship() && metadata.getType() == MetadataValueType.REFERENCE) {
						String referencedType = metadata.getAllowedReferences().getTypeWithAllowedSchemas();
						if (taxonomy.getSchemaTypes().contains(referencedType) && metadata.isTaxonomyRelationship()) {
							returnedMetadata.add(metadata);
						}
					}
				}
			}
		}

		return returnedMetadata;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, "schemaValidators");
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, "schemaValidators");
	}

	@Override
	public String toString() {
		return localCode;
	}

	public List<Metadata> getParentReferences() {
		return metadatas.onlyParentReferences();
	}

	public List<Metadata> getNonParentReferences() {
		return metadatas.onlyNonParentReferences();
	}

	public Map<String, Metadata> getIndexByAtomicCode() {
		return indexByAtomicCode;
	}

	public boolean isInTransactionLog() {
		return inTransactionLog;
	}
}
