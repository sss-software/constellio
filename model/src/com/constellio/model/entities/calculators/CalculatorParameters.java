package com.constellio.model.entities.calculators;

import com.constellio.model.entities.CollectionInfo;
import com.constellio.model.entities.calculators.dependencies.ConfigDependency;
import com.constellio.model.entities.calculators.dependencies.Dependency;
import com.constellio.model.entities.calculators.dependencies.DynamicLocalDependency;
import com.constellio.model.entities.calculators.dependencies.LocalDependency;
import com.constellio.model.entities.calculators.dependencies.ReferenceDependency;
import com.constellio.model.entities.calculators.dependencies.SpecialDependency;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchemaType;

import java.util.Map;

public class CalculatorParameters {

	final Map<Dependency, Object> values;
	final String collection;
	final MetadataSchemaType schemaType;
	final String id;
	final String legacyId;
	final boolean principalTaxonomyConcept;
	final Metadata metadata;

	public CalculatorParameters(Map<Dependency, Object> values, String id, String legacyId,
								MetadataSchemaType schemaType,
								String collection, boolean principalTaxonomyConcept, Metadata metadata) {
		super();
		this.values = values;
		this.id = id;
		this.schemaType = schemaType;
		this.legacyId = legacyId;
		this.collection = collection;
		this.principalTaxonomyConcept = principalTaxonomyConcept;
		this.metadata = metadata;
	}

	public boolean isPrincipalTaxonomyConcept() {
		return principalTaxonomyConcept;
	}

	public MetadataSchemaType getSchemaType() {
		return schemaType;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(LocalDependency<T> dependency) {
		return (T) values.get(dependency);
	}

	@SuppressWarnings("unchecked")
	public DynamicDependencyValues get(DynamicLocalDependency dependency) {
		return (DynamicDependencyValues) values.get(dependency);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(ReferenceDependency<T> dependency) {
		return (T) values.get(dependency);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(SpecialDependency<T> dependency) {
		return (T) values.get(dependency);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(ConfigDependency<T> dependency) {
		return (T) values.get(dependency);
	}

	public Object get(Dependency dependency) {
		return values.get(dependency);
	}

	public String getCollection() {
		return collection;
	}

	public String getId() {
		return id;
	}

	public String getLegacyId() {
		return legacyId;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public CollectionInfo getCollectionInfo() {
		return schemaType.getCollectionInfo();
	}
}
