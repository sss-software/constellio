package com.constellio.model.services.records.cache.cacheIndexHook.impl;

import com.constellio.model.entities.Taxonomy;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.records.RecordId;
import com.constellio.model.services.records.cache.cacheIndexHook.MetadataIndexCacheDataStoreHook;
import com.constellio.model.services.schemas.MetadataSchemasManager;
import com.constellio.model.services.taxonomies.TaxonomiesManager;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.constellio.model.entities.schemas.Schemas.PRINCIPALS_ANCESTORS_INT_IDS;
import static com.constellio.model.entities.schemas.Schemas.SECONDARY_CONCEPTS_INT_IDS;
import static com.constellio.model.entities.schemas.Schemas.ATTACHED_PRINCIPAL_CONCEPTS_INT_IDS;
import static com.constellio.model.entities.schemas.Schemas.TOKENS;
import static com.constellio.model.entities.schemas.Schemas.VISIBLE_IN_TREES;
import static com.constellio.model.services.records.RecordId.toId;
import static com.constellio.model.services.records.cache.cacheIndexHook.impl.TaxonomyRecordsHookKey.principalAccessOnRecordInConcept;
import static com.constellio.model.services.records.cache.cacheIndexHook.impl.TaxonomyRecordsHookKey.principalConceptAuthGivingAccessToRecordInSecondaryConceptKey;
import static com.constellio.model.services.records.cache.cacheIndexHook.impl.TaxonomyRecordsHookKey.recordInPrincipalConcept;
import static com.constellio.model.services.records.cache.cacheIndexHook.impl.TaxonomyRecordsHookKey.recordInSecondaryConcept;
import static java.util.stream.Collectors.toList;

public class TaxonomyRecordsHook implements MetadataIndexCacheDataStoreHook<TaxonomyRecordsHookKey> {

	ModelLayerFactory modelLayerFactory;
	TaxonomiesManager taxonomiesManager;
	MetadataSchemasManager schemasManager;
	String collection;

	public TaxonomyRecordsHook(String collection, ModelLayerFactory modelLayerFactory) {
		this.collection = collection;
		this.modelLayerFactory = modelLayerFactory;
		this.taxonomiesManager = modelLayerFactory.getTaxonomiesManager();
		this.schemasManager = modelLayerFactory.getMetadataSchemasManager();
	}

	@Override
	public String getCollection() {
		return collection;
	}

	@Override
	public boolean isHooked(MetadataSchemaType schemaType) {
		List<Taxonomy> taxonomies = taxonomiesManager.getEnabledTaxonomies(schemaType.getCollection());
		return !schemaType.getAllReferencesToTaxonomySchemas(taxonomies).isEmpty();
	}

	@Override
	public boolean requiresDataUpdate(Record record) {
		return record.isAnyModified(TOKENS, VISIBLE_IN_TREES, ATTACHED_PRINCIPAL_CONCEPTS_INT_IDS, SECONDARY_CONCEPTS_INT_IDS);
	}

	@Override
	public Set<TaxonomyRecordsHookKey> getKeys(Record record) {


		boolean visible = !Boolean.FALSE.equals(record.get(VISIBLE_IN_TREES));

		List<RecordId> principalConcepts = record.<Integer>getList(PRINCIPALS_ANCESTORS_INT_IDS)
				.stream().map(RecordId::toId).collect(toList());
		List<RecordId> attachedPrincipalConcepts = record.<Integer>getList(ATTACHED_PRINCIPAL_CONCEPTS_INT_IDS)
				.stream().map(RecordId::toId).collect(toList());
		List<RecordId> allAncestorsExceptPrincipals = record.<Integer>getList(SECONDARY_CONCEPTS_INT_IDS)
				.stream().map(RecordId::toId).collect(toList());

		Set<RecordId> principalIdsWithTokenReadAccess = new HashSet<>();
		Set<RecordId> principalIdsWithTokenWriteAccess = new HashSet<>();

		for (String token : record.<String>getList(TOKENS)) {
			if (token.startsWith("w_")) {
				principalIdsWithTokenWriteAccess.add(toId(StringUtils.substringAfterLast(token, "_")));
			} else if (token.startsWith("r_")) {
				principalIdsWithTokenReadAccess.add(toId(StringUtils.substringAfterLast(token, "_")));
			}
		}

		Set<TaxonomyRecordsHookKey> keys = new HashSet<>();
		principalIdsWithTokenReadAccess.removeAll(principalIdsWithTokenWriteAccess);

		for (RecordId secondaryTaxonomyRecordId : allAncestorsExceptPrincipals) {
			keys.add(recordInSecondaryConcept(secondaryTaxonomyRecordId, visible));
			for (RecordId principalId : principalIdsWithTokenReadAccess) {
				keys.add(principalAccessOnRecordInConcept(principalId, secondaryTaxonomyRecordId, false, visible));
			}

			for (RecordId principalId : principalIdsWithTokenWriteAccess) {
				keys.add(principalAccessOnRecordInConcept(principalId, secondaryTaxonomyRecordId, true, visible));
			}

			for (RecordId principalRecordId : attachedPrincipalConcepts) {
				keys.add(principalConceptAuthGivingAccessToRecordInSecondaryConceptKey(principalRecordId, secondaryTaxonomyRecordId, visible));
			}
		}

		for (RecordId secondaryTaxonomyRecordId : principalConcepts) {
			for (RecordId principalId : principalIdsWithTokenReadAccess) {
				keys.add(principalAccessOnRecordInConcept(principalId, secondaryTaxonomyRecordId, false, visible));
			}

			for (RecordId principalId : principalIdsWithTokenWriteAccess) {
				keys.add(principalAccessOnRecordInConcept(principalId, secondaryTaxonomyRecordId, true, visible));
			}
		}

		for (RecordId principalRecordId : attachedPrincipalConcepts) {
			keys.add(recordInPrincipalConcept(principalRecordId, visible));
		}

		return keys;
	}


	@Override
	public Class<?> getKeyType() {
		return TaxonomyRecordsHookKey.class;
	}

	//
	//	public static void findMatchesInSortedLists(List<Integer> list1, List<Integer> list2, List<RecordId> matches,
	//												List<RecordId> nonMatchesFromList1) {
	//
	//		int index1 = 0;
	//		int index2 = 0;
	//
	//		while (index1 < list1.size() && index2 < list2.size()) {
	//			Integer v1 = list1.get(index1);
	//			int result = v1.compareTo(list2.get(index2));
	//			RecordId id1 = RecordId.toId(v1);
	//			if (result < 0) {
	//				nonMatchesFromList1.add(id1);
	//				index1++;
	//			} else if (result > 0) {
	//				index2++;
	//			} else {
	//				matches.add(id1);
	//				index1++;
	//				index2++;
	//			}
	//		}
	//	}
}
