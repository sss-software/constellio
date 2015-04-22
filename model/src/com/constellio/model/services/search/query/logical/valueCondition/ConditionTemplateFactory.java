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
package com.constellio.model.services.search.query.logical.valueCondition;

import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.all;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.any;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.not;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.query;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.constellio.data.utils.AccentApostropheCleaner;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.parser.LanguageDetectionManager;
import com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators;
import com.constellio.model.services.search.query.logical.LogicalSearchValueCondition;
import com.constellio.model.services.search.query.logical.condition.ConditionTemplate;
import com.constellio.model.services.search.query.logical.criteria.IsEqualCriterion;
import com.constellio.model.services.search.query.logical.criteria.IsStartingWithTextCriterion;

public class ConditionTemplateFactory {

	private String collection;
	private ModelLayerFactory modelLayerFactory;

	public ConditionTemplateFactory(ModelLayerFactory modelLayerFactory, String collection) {
		this.modelLayerFactory = modelLayerFactory;
		this.collection = collection;
	}

	public static ConditionTemplate schemaTypeIs(MetadataSchemaType type) {
		return schemaTypeIs(type.getCode());
	}

	public static ConditionTemplate schemaTypeIs(String type) {
		return schemaTypeIsIn(asList(type));
	}

	public static ConditionTemplate schemaTypeIsIn(List<String> types) {
		List<LogicalSearchValueCondition> schemaTypesCriteria = new ArrayList<>();
		for (String type : types) {
			schemaTypesCriteria.add(LogicalSearchQueryOperators.startingWithText(type));
		}
		return ConditionTemplate.field(Schemas.SCHEMA, any(schemaTypesCriteria));
	}

	public static ConditionTemplate schemaTypeIsNotIn(List<String> types) {
		List<LogicalSearchValueCondition> schemaTypesCriteria = new ArrayList<>();
		for (String type : types) {
			schemaTypesCriteria.add(LogicalSearchQueryOperators.startingWithText(type));
		}
		return ConditionTemplate.field(Schemas.SCHEMA, not(any(schemaTypesCriteria)));
	}

	public static ConditionTemplate autocompleteFieldMatching(String text) {
		if (StringUtils.isBlank(text)) {
			return ConditionTemplate.field(Schemas.IDENTIFIER, new IsEqualCriterion("a38"));
		}
		String cleanedText = AccentApostropheCleaner.removeAccents(text).toLowerCase();

		String[] cleanedTextWords = cleanedText.split(" ");

		LogicalSearchValueCondition condition;
		if (cleanedTextWords.length == 1) {
			if (cleanedText.endsWith(" ")) {
				condition = new IsEqualCriterion(cleanedText.trim());
			} else {
				condition = new IsStartingWithTextCriterion(cleanedText.trim());
			}
		} else {
			List<LogicalSearchValueCondition> conditions = new ArrayList<>();
			for (int i = 0; i < cleanedTextWords.length; i++) {
				if (i + 1 == cleanedTextWords.length) {
					if (cleanedText.endsWith(" ")) {
						conditions.add(new IsEqualCriterion(cleanedTextWords[i]));
					} else {
						conditions.add(new IsStartingWithTextCriterion(cleanedTextWords[i]));
					}
				} else {
					conditions.add(new IsEqualCriterion(cleanedTextWords[i]));
				}
			}
			condition = all(conditions);
		}

		return ConditionTemplate.field(Schemas.SCHEMA_AUTOCOMPLETE_FIELD, condition);
	}

	public ConditionTemplate metadatasHasAnalyzedValue(String value, Metadata... metadatas) {
		return metadatasHasAnalyzedValue(value, asList(metadatas));
	}

	public ConditionTemplate metadatasHasAnalyzedValue(String value, List<Metadata> metadatas) {

		List<String> availableLanguages = modelLayerFactory.getCollectionsListManager().getCollectionLanguages(collection);
		LanguageDetectionManager languageDetectionManager = modelLayerFactory.getLanguageDetectionManager();

		String language =
				availableLanguages.size() == 1 ? availableLanguages.get(0) : languageDetectionManager.tryDetectLanguage(value);
		List<Metadata> searchedMetadatas = new ArrayList<>();
		for (Metadata metadata : metadatas) {
			if (!availableLanguages.contains(language) || language == null) {
				for (String availableLanguage : availableLanguages) {
					searchedMetadatas.add(metadata.getSearchableMetadataWithLanguage(availableLanguage));
				}
			} else {
				searchedMetadatas.add(metadata.getSearchableMetadataWithLanguage(language));
			}
		}

		return ConditionTemplate.anyField(searchedMetadatas, query(value));
	}

	public ConditionTemplate titleHasAnalyzedValue(String value) {
		return metadatasHasAnalyzedValue(value, asList(Schemas.TITLE));
	}

	public ConditionTemplate searchFieldHasAnalyzedValue(String value) {
		return metadatasHasAnalyzedValue(value, asList(Schemas.SEARCH_FIELD));
	}
}
