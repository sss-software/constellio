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
package com.constellio.model.services.search;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.constellio.data.dao.dto.records.FacetValue;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.schemas.DataStoreField;

public class SPEQueryResponse {

	private final Map<DataStoreField, List<FacetValue>> fieldFacetValues;

	private final Map<String, Integer> queryFacetsValues;

	private final Map<String, Map<String, List<String>>> highlights;

	private final long qtime;

	private final long numFound;

	private final List<Record> records;
	
	private final boolean correctlySpelled;
	private final List<String> spellcheckerSuggestions;

	public SPEQueryResponse(
			Map<DataStoreField, List<FacetValue>> fieldFacetValues, Map<String, Integer> queryFacetsValues, long qtime,
			long numFound, List<Record> records, Map<String, Map<String, List<String>>> highlights, boolean correctlySpelled, List<String> spellcheckerSuggestions) {
		this.fieldFacetValues = fieldFacetValues;
		this.queryFacetsValues = queryFacetsValues;
		this.qtime = qtime;
		this.numFound = numFound;
		this.records = records;
		this.highlights = highlights;
		this.correctlySpelled = correctlySpelled;
		this.spellcheckerSuggestions = spellcheckerSuggestions;
	}

	public List<FacetValue> getFieldFacetValues(DataStoreField metadata) {
		return getFieldFacetValues(metadata);
	}

	public Integer getQueryFacetCount(String query) {
		return queryFacetsValues.get(query);
	}

	public List<String> getFieldFacetValuesWithResults(DataStoreField field) {
		List<String> values = new ArrayList<>();

		for (FacetValue facetValue : getFieldFacetValues(field)) {
			if (facetValue.getQuantity() > 0) {
				values.add(facetValue.getValue());
			}
		}

		return values;
	}

	public List<Record> getRecords() {
		return records;
	}

	public long getQtime() {
		return qtime;
	}

	public long getNumFound() {
		return numFound;
	}

	public Map<DataStoreField, List<FacetValue>> getFieldFacetValues() {
		return fieldFacetValues;
	}

	public Map<String, Integer> getQueryFacetsValues() {
		return queryFacetsValues;
	}

	public SPEQueryResponse withModifiedRecordList(List<Record> records) {
		return new SPEQueryResponse(fieldFacetValues, queryFacetsValues, qtime, numFound, records, null, correctlySpelled, spellcheckerSuggestions);
	}

	public SPEQueryResponse withNumFound(int numFound) {
		return new SPEQueryResponse(fieldFacetValues, queryFacetsValues, qtime, numFound, records, null, correctlySpelled, spellcheckerSuggestions);
	}

	public Map<String, Map<String, List<String>>> getHighlights() {
		return highlights;
	}

	public boolean isCorrectlySpelled() {
		return correctlySpelled;
	}

	public List<String> getSpellCheckerSuggestions() {
		return spellcheckerSuggestions;
	}

}
