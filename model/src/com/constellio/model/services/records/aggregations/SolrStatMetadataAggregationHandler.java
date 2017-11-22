package com.constellio.model.services.records.aggregations;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.constellio.data.dao.services.bigVault.SearchResponseIterator;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.entities.schemas.entries.SearchAggregatedValuesParams;
import com.constellio.model.services.search.SPEQueryResponse;
import com.constellio.model.services.search.query.ReturnedMetadatasFilter;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;

public abstract class SolrStatMetadataAggregationHandler implements MetadataAggregationHandler {

	protected String statName;

	public SolrStatMetadataAggregationHandler(String statName) {
		this.statName = statName;
	}

	@Override
	public Object calculate(SearchAggregatedValuesParams params) {

		Metadata inputMetadata = params.getTypes().getMetadata(params.getAggregatedDataEntry().getFirstInputMetadata());
		LogicalSearchQuery query = params.getQuery();

		if (inputMetadata.getType() == MetadataValueType.NUMBER) {
			query.computeStatsOnField(inputMetadata);
			query.setNumberOfRows(0);
			SPEQueryResponse response = params.getSearchServices().query(query);

			Map<String, Object> statsValues = response.getStatValues(inputMetadata);
			return statsValues == null || statsValues.get(statName) == null ? 0.0 : (Double) statsValues.get(statName);

		} else {
			query.setReturnedMetadatas(ReturnedMetadatasFilter.onlyMetadatas(inputMetadata));
			SearchResponseIterator<Record> iterator = params.getSearchServices().recordsIterator(query, 10000);

			Set<Object> values = new HashSet<>();
			while (iterator.hasNext()) {
				Record record = iterator.next();
				values.addAll(record.<Object>getValues(inputMetadata));

			}

			return calculateForNonNumber(params.getMetadata().getType(), values);
		}
	}

	protected abstract Object calculateForNonNumber(MetadataValueType metadataValueType, Set<Object> values);

	@Override
	public List<Metadata> getMetadatasUsedToCalculate(GetMetadatasUsedToCalculateParams params) {
		return params.getInputMetadatas();
	}

}
