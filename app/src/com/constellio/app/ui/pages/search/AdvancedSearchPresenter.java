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

import static com.constellio.app.ui.i18n.i18n.$;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.all;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.allConditions;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.constellio.app.entities.schemasDisplay.MetadataDisplayConfig;
import com.constellio.app.entities.schemasDisplay.enums.MetadataInputType;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.framework.builders.MetadataToVOBuilder;
import com.constellio.app.ui.pages.search.criteria.ConditionBuilder;
import com.constellio.app.ui.pages.search.criteria.ConditionException;
import com.constellio.app.ui.pages.search.criteria.ConditionException.ConditionException_EmptyCondition;
import com.constellio.app.ui.pages.search.criteria.ConditionException.ConditionException_TooManyClosedParentheses;
import com.constellio.app.ui.pages.search.criteria.ConditionException.ConditionException_UnclosedParentheses;
import com.constellio.model.entities.batchprocess.BatchProcess;
import com.constellio.model.entities.batchprocess.BatchProcessAction;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.entities.schemas.entries.DataEntryType;
import com.constellio.model.services.batch.actions.ChangeValueOfMetadataBatchProcessAction;
import com.constellio.model.services.batch.manager.BatchProcessesManager;
import com.constellio.model.services.search.query.logical.condition.LogicalSearchCondition;
import com.google.common.base.Strings;

public class AdvancedSearchPresenter extends SearchPresenter<AdvancedSearchView> {
	String searchExpression;
	String schemaTypeCode;
	private transient LogicalSearchCondition condition;

	public AdvancedSearchPresenter(AdvancedSearchView view) {
		super(view);
	}

	@Override
	public AdvancedSearchPresenter forRequestParameters(String params) {
		searchExpression = view.getSearchExpression();
		schemaTypeCode = view.getSchemaType();
		resetFacetSelection();
		return this;
	}

	@Override
	public int getPageNumber() {
		return 1;
	}

	@Override
	public boolean mustDisplayResults() {
		if (Strings.isNullOrEmpty(schemaTypeCode)) {
			return false;
		}
		try {
			buildSearchCondition();
			return true;
		} catch (ConditionException_EmptyCondition e) {
			view.showErrorMessage($("AdvancedSearchView.emptyCondition"));
		} catch (ConditionException_TooManyClosedParentheses e) {
			view.showErrorMessage($("AdvancedSearchView.tooManyClosedParentheses"));
		} catch (ConditionException_UnclosedParentheses e) {
			view.showErrorMessage($("AdvancedSearchView.unclosedParentheses"));
		} catch (ConditionException e) {
			throw new RuntimeException("BUG: Uncaught ConditionException", e);
		}
		return false;
	}

	public void batchEditRequested(List<String> selectedRecordIds, String code, Object value) {
		Map<String, Object> changes = new HashMap<>();
		changes.put(code, value);
		BatchProcessAction action = new ChangeValueOfMetadataBatchProcessAction(changes);

		BatchProcessesManager manager = modelLayerFactory.getBatchProcessesManager();
		BatchProcess process = manager.add(selectedRecordIds, view.getCollection(), action);
		manager.markAsPending(process);
	}

	public List<MetadataVO> getMetadatasAllowedInBatchEdit() {
		MetadataToVOBuilder builder = new MetadataToVOBuilder();

		List<MetadataVO> result = new ArrayList<>();
		for (Metadata metadata : types().getSchemaType(schemaTypeCode).getAllMetadatas()) {
			if (isBatchEditable(metadata)) {
				result.add(builder.build(metadata));
			}
		}
		return result;
	}

	@Override
	protected LogicalSearchCondition getSearchCondition() {
		if (condition == null) {
			try {
				buildSearchCondition();
			} catch (ConditionException e) {
				throw new RuntimeException("Unexpected exception (should be unreachable)", e);
			}
		}
		return condition;
	}

	void buildSearchCondition()
			throws ConditionException {
		MetadataSchemaType type = schemaType(schemaTypeCode);

		List<LogicalSearchCondition> conditions = new ArrayList<>();
		if (!Strings.isNullOrEmpty(searchExpression)) {
			conditions.add(from(type).whereAny(searchFieldsFor(searchExpression)).is(all(queries(searchExpression))));
		}
		if (!view.getSearchCriteria().isEmpty()) {
			conditions.add(new ConditionBuilder(type).build(view.getSearchCriteria()));
		}

		switch (conditions.size()) {
		case 0:
			condition = from(type).returnAll();
			break;
		case 1:
			condition = conditions.get(0);
			break;
		default:
			condition = allConditions(conditions);
			break;
		}
	}

	private boolean isBatchEditable(Metadata metadata) {
		return !metadata.isSystemReserved()
				&& !metadata.isUnmodifiable()
				&& metadata.isEnabled()
				&& !metadata.getType().isStructureOrContent()
				&& metadata.getDataEntry().getType() == DataEntryType.MANUAL
				&& isNotHidden(metadata)
				// XXX: Not supported in the backend
				&& metadata.getType() != MetadataValueType.ENUM
				;
	}

	private boolean isNotHidden(Metadata metadata) {
		MetadataDisplayConfig config = schemasDisplayManager().getMetadata(view.getCollection(), metadata.getCode());
		return config.getInputType() != MetadataInputType.HIDDEN;
	}
}
