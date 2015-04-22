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
package com.constellio.app.ui.pages.search;

import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.constellio.app.entities.schemasDisplay.MetadataDisplayConfig;
import com.constellio.app.modules.rm.reports.factories.ExampleReportFactory;
import com.constellio.app.reports.builders.administration.plan.ReportBuilderFactory;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.framework.builders.MetadataToVOBuilder;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.framework.components.ReportPresenter;
import com.constellio.app.ui.framework.data.SearchResultVODataProvider;
import com.constellio.app.ui.pages.base.BasePresenter;
import com.constellio.data.dao.dto.records.FacetValue;
import com.constellio.model.entities.schemas.DataStoreField;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.schemas.SchemaUtils;
import com.constellio.model.services.search.SPEQueryResponse;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.services.search.query.logical.LogicalSearchValueCondition;
import com.constellio.model.services.search.query.logical.condition.LogicalSearchCondition;

public abstract class SearchPresenter<T extends SearchView> extends BasePresenter<T> implements ReportPresenter {
	Map<String, Set<String>> facetSelections;
	List<String> suggestions;

	public SearchPresenter(T view) {
		super(view);
		resetFacetSelection();
	}

	public abstract SearchPresenter<T> forRequestParameters(String params);

	public abstract boolean mustDisplayResults();

	public abstract int getPageNumber();

	public String getUserSearchExpression() {
		return null;
	}

	public boolean mustDisplaySuggestions() {
		if (getSearchResults().size() == 0) {
			SPEQueryResponse suggestionsResponse = searchServices()
					.query(getUserSearchExpression(), getSearchQuery().setNumberOfRows(0).setSpellcheck(true));
			if (suggestionsResponse.isCorrectlySpelled()) {
				return false;
			}
			suggestions = suggestionsResponse.getSpellCheckerSuggestions();
			return !suggestions.isEmpty();
		} else {
			return false;
		}
	}

	public List<String> getSuggestions() {
		return suggestions;
	}

	public SearchResultVODataProvider getSearchResults() {
		return new SearchResultVODataProvider(new RecordToVOBuilder(), modelLayerFactory) {
			@Override
			protected LogicalSearchQuery getQuery() {
				return getSearchQuery().setHighlighting(true);
			}

			@Override
			protected String getUserSearchExpression() {
				return SearchPresenter.this.getUserSearchExpression();
			}
		};
	}

	public Map<MetadataVO, List<FacetValue>> getFacets() {
		LogicalSearchQuery query = getSearchQuery().setNumberOfRows(0);
		injectFacetFields(query);
		SPEQueryResponse response = searchServices().query(getUserSearchExpression(), query);

		MetadataToVOBuilder builder = new MetadataToVOBuilder();
		Map<MetadataVO, List<FacetValue>> result = new LinkedHashMap<>();
		for (String code : getActiveFacets()) {
			for (Entry<DataStoreField, List<FacetValue>> each : response.getFieldFacetValues().entrySet()) {
				Metadata metadata = (Metadata) each.getKey();
				if (metadata.getCode().equals(code)) {
					result.put(builder.build(metadata, view.getSessionContext()), each.getValue());
					break;
				}
			}
		}
		return result;
	}

	public void facetValueSelected(String code, String value) {
		facetSelections.get(code).add(value);
		view.refreshSearchResultsAndFacets();
	}

	public void facetValueDeselected(String code, String value) {
		facetSelections.get(code).remove(value);
		view.refreshSearchResultsAndFacets();
	}

	public Map<String, Set<String>> getFacetSelections() {
		return facetSelections;
	}

	@Override
	public List<String> getSupportedReports() {
		//return Arrays.asList("Reports.fakeReport");
		return new ArrayList<>();
	}

	@Override
	public ReportBuilderFactory getReport(String report) {
		switch (report) {
		case "Reports.fakeReport":
			return new ExampleReportFactory(view.getSelectedRecordIds());
		}
		throw new RuntimeException("BUG: Unknown report " + report);
	}

	protected abstract LogicalSearchCondition getSearchCondition();

	private LogicalSearchQuery getSearchQuery() {
		LogicalSearchQuery query = new LogicalSearchQuery(getSearchCondition()).filteredWithUser(getCurrentUser());
		for (Entry<String, Set<String>> selection : facetSelections.entrySet()) {
			if (!selection.getValue().isEmpty()) {
				query.filteredByFacetValues(getMetadata(selection.getKey()), selection.getValue());
			}
		}
		return query;
	}

	void injectFacetFields(LogicalSearchQuery query) {
		for (String facetCode : getActiveFacets()) {
			query.addFieldFacet(getMetadata(facetCode));
		}
	}

	protected void resetFacetSelection() {
		facetSelections = new HashMap<>();
		for (String facetCode : getActiveFacets()) {
			facetSelections.put(facetCode, new HashSet<String>());
		}
	}

	Metadata getMetadata(String code) {
		SchemaUtils utils = new SchemaUtils();
		String schemaCode = utils.getSchemaCode(code);
		return schema(schemaCode).getMetadata(utils.getLocalCode(code, schemaCode));
	}

	List<String> getActiveFacets() {
		return schemasDisplayManager().getTypes(view.getCollection()).getFacetMetadataCodes();
	}

	protected List<MetadataVO> getMetadatasAllowedInAdvancedSearch(String schemaTypeCode) {
		MetadataToVOBuilder builder = new MetadataToVOBuilder();
		MetadataSchemaType schemaType = schemaType(schemaTypeCode);

		List<MetadataVO> result = new ArrayList<>();
		for (Metadata metadata : schemaType.getAllMetadatas()) {
			MetadataDisplayConfig config = schemasDisplayManager().getMetadata(view.getCollection(), metadata.getCode());
			if (config.isVisibleInAdvancedSearch()) {
				result.add(builder.build(metadata, view.getSessionContext()));
			}
		}
		return result;
	}

	List<Metadata> searchFieldsFor(String searchExpression) {
		// XXX: Temporarily bypass language detection
		return Schemas.getAllSearchFields();
		//		String languageCode = modelLayerFactory.getLanguageDetectionManager().tryDetectLanguage(searchExpression);
		//		if (languageCode == null || Language.UNKNOWN.getCode().equals(languageCode)) {
		//			return Schemas.getAllSearchFields();
		//		} else {
		//			return Arrays.asList(Schemas.getSearchFieldForLanguage(languageCode));
		//		}
	}

	protected List<LogicalSearchValueCondition> queries(String searchExpression) {
		List<LogicalSearchValueCondition> result = new ArrayList<>();
		for (String word : searchExpression.split(" ")) {
			result.add(query(word));
		}
		return result;
	}
}
