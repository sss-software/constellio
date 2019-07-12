package com.constellio.model.services.search.query.logical.criteria;

import com.constellio.data.utils.LangUtils;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.schemas.DataStoreField;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.services.search.query.logical.LogicalSearchValueCondition;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class IsNotEqualCriterion extends LogicalSearchValueCondition {

	private final Object value;
	private final Object memoryQueryValue;

	public IsNotEqualCriterion(Object value) {
		super();
		this.value = value;
		this.memoryQueryValue = CriteriaUtils.convertToMemoryQueryValue(value);
	}

	public Object getValue() {
		return value;
	}

	@Override
	public boolean isValidFor(DataStoreField dataStoreField) {
		return true;
	}

	@Override
	public String getSolrQuery(DataStoreField dataStoreField) {
		StringBuilder query = new StringBuilder();
		query.append("(*:* -(");
		query.append(dataStoreField.getDataStoreCode());
		query.append(":\"");
		query.append(CriteriaUtils.toSolrStringValue(value, dataStoreField));
		query.append("\"))");
		return query.toString();
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + value;
	}

	@Override
	public boolean testConditionOnField(Metadata metadata, Record record) {
		Object recordValue = CriteriaUtils.convertMetadataValue(metadata, record);

		if (recordValue instanceof List) {
			return !((List) recordValue).contains(memoryQueryValue);
		} else {
			return !LangUtils.isEqual(recordValue, memoryQueryValue);
		}

	}

	@Override
	public boolean isSupportingMemoryExecution() {
		return true;
	}
}
