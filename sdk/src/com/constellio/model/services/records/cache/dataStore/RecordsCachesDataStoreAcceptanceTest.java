package com.constellio.model.services.records.cache.dataStore;

import com.constellio.app.modules.rm.model.enums.FolderStatus;
import com.constellio.data.dao.dto.records.RecordDTO;
import com.constellio.data.dao.dto.records.SolrRecordDTO;
import com.constellio.data.utils.LangUtils;
import com.constellio.data.utils.ThreadList;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.RecordCacheType;
import com.constellio.model.services.collections.CollectionsListManager;
import com.constellio.model.services.collections.exceptions.CollectionIdNotSetRuntimeException;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.cache.ByteArrayRecordDTO;
import com.constellio.model.services.records.reindexing.ReindexingServices;
import com.constellio.model.services.schemas.MetadataSchemaTypesAlteration;
import com.constellio.model.services.schemas.MetadataSchemasManager;
import com.constellio.model.services.schemas.builders.MetadataSchemaBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.schemas.TestsSchemasSetup;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.ListAssert;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.constellio.data.dao.dto.records.RecordDTOMode.FULLY_LOADED;
import static com.constellio.data.dao.dto.records.RecordDTOMode.SUMMARY;
import static com.constellio.model.entities.schemas.MetadataValueType.BOOLEAN;
import static com.constellio.model.entities.schemas.MetadataValueType.ENUM;
import static com.constellio.model.entities.schemas.MetadataValueType.INTEGER;
import static com.constellio.model.entities.schemas.MetadataValueType.NUMBER;
import static com.constellio.model.entities.schemas.MetadataValueType.REFERENCE;
import static com.constellio.model.entities.schemas.MetadataValueType.STRING;
import static com.constellio.model.entities.schemas.RecordCacheType.FULLY_CACHED;
import static com.constellio.model.entities.schemas.RecordCacheType.SUMMARY_CACHED_WITHOUT_VOLATILE;
import static com.constellio.sdk.tests.schemas.TestsSchemasSetup.whichIsEssentialInSummary;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

public class RecordsCachesDataStoreAcceptanceTest extends ConstellioTest {

	RecordsCachesDataStore dataStore;

	TestsSchemasSetup setup = new TestsSchemasSetup(zeCollection);
	TestsSchemasSetup.ZeSchemaMetadatas zeSchema = setup.new ZeSchemaMetadatas();
	TestsSchemasSetup.AnotherSchemaMetadatas anotherSchema = setup.new AnotherSchemaMetadatas();

	MetadataSchemasManager schemasManager;
	RecordServices recordServices;
	ReindexingServices reindexingServices;

	@Before
	public void setUp() throws Exception {
		schemasManager = getModelLayerFactory().getMetadataSchemasManager();
		recordServices = getModelLayerFactory().newRecordServices();
		reindexingServices = getModelLayerFactory().newReindexingServices();

		//SummaryCacheSingletons.dataStore = new FileSystemRecordsValuesCacheDataStore(new File(newTempFolder(), "test.db"));
	}


	MetadataSchemasManager metadataSchemasManager;
	CollectionsListManager collectionsListManager;

	byte zeCollectionId;
	byte anotherCollectionId;
	int zeCollectionIndex;
	int anotherCollectionIndex;
	short zeCollectionType1Id;
	short zeCollectionType2Id;
	short zeSchemaDefaultId;

	String[] collections = new String[]{"zeCollection", "anotherCollection"};
	String[] types = new String[]{"type1", "type2"};

	private void initTestVariables() {
		collectionsListManager = getModelLayerFactory().getCollectionsListManager();
		metadataSchemasManager = getModelLayerFactory().getMetadataSchemasManager();
		try {
			zeCollectionId = collectionsListManager.getCollectionId(zeCollection);
			zeCollectionIndex = zeCollectionId - Byte.MIN_VALUE;
			try {
				zeCollectionType1Id = metadataSchemasManager.getSchemaTypes(zeCollectionId).getSchemaType(zeSchema.typeCode()).getId();
				zeCollectionType2Id = metadataSchemasManager.getSchemaTypes(zeCollectionId).getSchemaType(anotherSchema.typeCode()).getId();
				zeSchemaDefaultId = zeSchema.instance().getId();
			} catch (Throwable t) {
				t.printStackTrace();
			}

		} catch (CollectionIdNotSetRuntimeException e) {
			//Collection may not exist
		}
		try {
			anotherCollectionId = collectionsListManager.getCollectionId(collections[1]);
			anotherCollectionIndex = anotherCollectionId - Byte.MIN_VALUE;
		} catch (CollectionIdNotSetRuntimeException e) {
			//Collection may not exist
		}


		dataStore = new RecordsCachesDataStore(getModelLayerFactory());

	}

	@Test
	public void whenPreloadingCacheWithSpacedIdsThenNoSpaceReservedForIdsBetween() throws Exception {
		defineSchemasManager().using(setup.withABooleanMetadata(whichIsEssentialInSummary));

		getModelLayerFactory().getMetadataSchemasManager().modify(zeCollection, new MetadataSchemaTypesAlteration() {
			@Override
			public void alter(MetadataSchemaTypesBuilder types) {
				types.getSchemaType(zeSchema.typeCode()).setRecordCacheType(RecordCacheType.SUMMARY_CACHED_WITH_VOLATILE);
			}
		});
		initTestVariables();

		ByteArrayRecordDTO dto1, dto2, dto3, dto6, dto7, dto8;
		dto1 = create(new SolrRecordDTO(zeroPadded(1), 12L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto3 = create(new SolrRecordDTO(zeroPadded(3), 23L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto6 = create(new SolrRecordDTO(zeroPadded(6), 34L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto8 = create(new SolrRecordDTO(zeroPadded(8), 45L, fields("zeCollection", zeSchema.code()), SUMMARY));

		dataStore.insertWithoutReservingSpaceForPreviousIds(dto1);
		dataStore.insertWithoutReservingSpaceForPreviousIds(dto3);
		dataStore.insertWithoutReservingSpaceForPreviousIds(dto6);
		dataStore.insert(dto8);

		assertThat(dataStore.getIntIdsDataStore().ids.stream().collect(toList()))
				.containsExactly(1, 3, 6, 7, 8);

		assertThat(dataStore.intIdsDataStore.versions.stream().collect(toList()))
				.containsExactly(12L, 23L, 34L, 0L, 45L);

		assertThat(dataStore.intIdsDataStore.schema.stream().collect(toList()))
				.containsExactly(zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, (short) 0, zeSchemaDefaultId);

		assertThat(dataStore.stream(zeCollectionId, zeCollectionType1Id).collect(toList()))
				.containsExactly(dto1, dto3, dto6, dto8);


	}


	@Test
	public void givenRecordAddedAfterPreloadingTheAddedReservingSpaceAllowingToAddTheRecordsWithoutReallocate()
			throws Exception {
		defineSchemasManager().using(setup.withABooleanMetadata(whichIsEssentialInSummary));

		getModelLayerFactory().getMetadataSchemasManager().modify(zeCollection, new MetadataSchemaTypesAlteration() {
			@Override
			public void alter(MetadataSchemaTypesBuilder types) {
				types.getSchemaType(zeSchema.typeCode()).setRecordCacheType(RecordCacheType.SUMMARY_CACHED_WITHOUT_VOLATILE);
			}
		});
		initTestVariables();

		ByteArrayRecordDTO dto1, dto2, dto3, dto6, dto7, dto8, dto10, dto12;
		dto1 = create(new SolrRecordDTO(zeroPadded(1), 12L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto3 = create(new SolrRecordDTO(zeroPadded(3), 23L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto6 = create(new SolrRecordDTO(zeroPadded(6), 34L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto7 = create(new SolrRecordDTO(zeroPadded(7), 56L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto8 = create(new SolrRecordDTO(zeroPadded(8), 67L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto10 = create(new SolrRecordDTO(zeroPadded(10), 111L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto12 = create(new SolrRecordDTO(zeroPadded(12), 222L, fields("zeCollection", zeSchema.code()), SUMMARY));

		dataStore.insertWithoutReservingSpaceForPreviousIds(dto1);
		//assertThat(dataStore.intIdsDataStore.typesIndexes[zeCollectionIndex][zeCollectionType1Id].size()).isEqualTo(1);
		dataStore.insertWithoutReservingSpaceForPreviousIds(dto3);
		//assertThat(dataStore.intIdsDataStore.typesIndexes[zeCollectionIndex][zeCollectionType1Id].size()).isEqualTo(2);
		dataStore.insertWithoutReservingSpaceForPreviousIds(dto6);
		//assertThat(dataStore.intIdsDataStore.typesIndexes[zeCollectionIndex][zeCollectionType1Id].size()).isEqualTo(3);


		assertThat(dataStore.intIdsDataStore.collection.stream().collect(toList()))
				.containsExactly(zeCollectionId, zeCollectionId, zeCollectionId);

		dataStore.insert(dto8);
		//assertThat(dataStore.intIdsDataStore.typesIndexes[zeCollectionIndex][zeCollectionType1Id].size()).isEqualTo(4);

		assertThat(dataStore.intIdsDataStore.collection.stream().collect(toList()))
				.containsExactly(zeCollectionId, zeCollectionId, zeCollectionId, (byte) 0, zeCollectionId);

		dataStore.insert(dto7);
		//assertThat(dataStore.intIdsDataStore.typesIndexes[zeCollectionIndex][zeCollectionType1Id].size()).isEqualTo(5);

		assertThat(dataStore.intIdsDataStore.collection.stream().collect(toList()))
				.containsExactly(zeCollectionId, zeCollectionId, zeCollectionId, zeCollectionId, zeCollectionId);

		assertThat(dataStore.intIdsDataStore.ids.stream().collect(toList()))
				.containsExactly(1, 3, 6, 7, 8);

		assertThat(dataStore.intIdsDataStore.versions.stream().collect(toList()))
				.containsExactly(12L, 23L, 34L, 56L, 67L);

		assertThat(dataStore.intIdsDataStore.type.stream().collect(toList()))
				.containsExactly(zeCollectionType1Id, zeCollectionType1Id, zeCollectionType1Id, zeCollectionType1Id, zeCollectionType1Id);

		assertThat(dataStore.intIdsDataStore.collection.stream().collect(toList()))
				.containsExactly(zeCollectionId, zeCollectionId, zeCollectionId, zeCollectionId, zeCollectionId);

		assertThat(dataStore.intIdsDataStore.schema.stream().collect(toList()))
				.containsExactly(zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId);

		//short zeCollectionIndex = (short) (zeCollectionId - Byte.MIN_VALUE);
		//assertThat(dataStore.intIdsDataStore.typesIndexes[zeCollectionIndex][zeCollectionType1Id].size()).isEqualTo(5);
		assertThat(dataStore.stream(zeCollectionId, zeCollectionType1Id).collect(toList()))
				.containsOnly(dto1, dto3, dto6, dto7, dto8);

		dataStore.insert(dto10);

		assertThat(dataStore.intIdsDataStore.ids.stream().collect(toList()))
				.containsExactly(1, 3, 6, 7, 8, 9, 10);

		assertThat(dataStore.intIdsDataStore.versions.stream().collect(toList()))
				.containsExactly(12L, 23L, 34L, 56L, 67L, 0L, 111L);

		assertThat(dataStore.intIdsDataStore.schema.stream().collect(toList()))
				.containsExactly(zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, (short) 0, zeSchemaDefaultId);

		assertThat(dataStore.stream(zeCollectionId, zeCollectionType1Id).collect(toList()))
				.containsOnly(dto1, dto3, dto6, dto7, dto8, dto10);

		dataStore.insertWithoutReservingSpaceForPreviousIds(dto12);

		assertThat(dataStore.intIdsDataStore.ids.stream().collect(toList()))
				.containsExactly(1, 3, 6, 7, 8, 9, 10, 12);

		assertThat(dataStore.intIdsDataStore.versions.stream().collect(toList()))
				.containsExactly(12L, 23L, 34L, 56L, 67L, 0L, 111L, 222L);

		assertThat(dataStore.intIdsDataStore.schema.stream().collect(toList()))
				.containsExactly(zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, (short) 0, zeSchemaDefaultId, zeSchemaDefaultId);

		assertThat(dataStore.stream(zeCollectionId, zeCollectionType1Id).collect(toList()))
				.containsOnly(dto1, dto3, dto6, dto7, dto8, dto10, dto12);

	}


	@Test
	public void whenSavingSolrDTOThenPersistedInHeapWithoutConversion()
			throws Exception {
		defineSchemasManager().using(setup.withABooleanMetadata(whichIsEssentialInSummary));

		getModelLayerFactory().getMetadataSchemasManager().modify(zeCollection, new MetadataSchemaTypesAlteration() {
			@Override
			public void alter(MetadataSchemaTypesBuilder types) {
				types.getSchemaType(zeSchema.typeCode()).setRecordCacheType(FULLY_CACHED);
			}
		});
		initTestVariables();

		SolrRecordDTO dto1, dto3, dto6, dto7, dto8, dto9, dto12, dto14;
		dto1 = new SolrRecordDTO(zeroPadded(1), 12L, fields("zeCollection", zeSchema.code()), FULLY_LOADED);
		dto3 = new SolrRecordDTO(zeroPadded(3), 23L, fields("zeCollection", zeSchema.code()), FULLY_LOADED);
		dto6 = new SolrRecordDTO(zeroPadded(6), 34L, fields("zeCollection", zeSchema.code()), FULLY_LOADED);
		dto7 = new SolrRecordDTO(zeroPadded(7), 56L, fields("zeCollection", zeSchema.code()), FULLY_LOADED);
		dto8 = new SolrRecordDTO(zeroPadded(8), 67L, fields("zeCollection", zeSchema.code()), FULLY_LOADED);
		dto9 = new SolrRecordDTO(zeroPadded(9), 45L, fields("zeCollection", zeSchema.code()), FULLY_LOADED);
		dto12 = new SolrRecordDTO(zeroPadded(12), 111L, fields("zeCollection", zeSchema.code()), FULLY_LOADED);
		dto14 = new SolrRecordDTO(zeroPadded(14), 222L, fields("zeCollection", zeSchema.code()), FULLY_LOADED);

		dataStore.insertWithoutReservingSpaceForPreviousIds(dto1);
		dataStore.insertWithoutReservingSpaceForPreviousIds(dto3);
		dataStore.insertWithoutReservingSpaceForPreviousIds(dto6);
		dataStore.insert(dto9);
		dataStore.insert(dto7);

		assertThat(dataStore.intIdsDataStore.ids.stream().collect(toList()))
				.containsExactly(1, 3, 6, 7, 8, 9);

		assertThat(dataStore.intIdsDataStore.versions.stream().collect(toList()))
				.containsExactly(12L, 23L, 34L, 56L, 0L, 45L);

		assertThat(dataStore.intIdsDataStore.schema.stream().collect(toList()))
				.containsExactly(zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, (short) 0, zeSchemaDefaultId);

		assertThat(dataStore.stream().collect(toList()))
				.containsOnly(dto1, dto3, dto6, dto7, dto9);

		assertThat(dataStore.stream(zeCollectionId, zeCollectionType1Id).collect(toList()))
				.containsOnly(dto1, dto3, dto6, dto7, dto9);

		dataStore.insert(dto8);

		assertThat(dataStore.stream(zeCollectionId, zeCollectionType1Id).collect(toList()))
				.containsOnly(dto1, dto3, dto6, dto7, dto8, dto9);

		dataStore.insert(dto12);

		assertThat(dataStore.intIdsDataStore.ids.stream().collect(toList()))
				.containsExactly(1, 3, 6, 7, 8, 9, 10, 11, 12);

		assertThat(dataStore.intIdsDataStore.versions.stream().collect(toList()))
				.containsExactly(12L, 23L, 34L, 56L, 67L, 45L, 0L, 0L, 111L);

		assertThat(dataStore.stream(zeCollectionId, zeCollectionType1Id).collect(toList()))
				.containsOnly(dto1, dto3, dto6, dto7, dto8, dto9, dto12);


		dataStore.insertWithoutReservingSpaceForPreviousIds(dto14);

		assertThat(dataStore.intIdsDataStore.ids.stream().collect(toList()))
				.containsExactly(1, 3, 6, 7, 8, 9, 10, 11, 12, 14);

		assertThat(dataStore.intIdsDataStore.versions.stream().collect(toList()))
				.containsExactly(12L, 23L, 34L, 56L, 67L, 45L, 0L, 0L, 111L, 222L);

		assertThat(dataStore.stream(zeCollectionId, zeCollectionType1Id).collect(toList()))
				.containsOnly(dto1, dto3, dto6, dto7, dto8, dto9, dto12, dto14);
	}


	@Test
	public void whenAddingAFullyCachedRecordInASpaceThatWasNotReservedThenRecreateArraysAddingNewReservedIndex()
			throws Exception {
		defineSchemasManager().using(setup.withABooleanMetadata(whichIsEssentialInSummary));

		getModelLayerFactory().getMetadataSchemasManager().modify(zeCollection, new MetadataSchemaTypesAlteration() {
			@Override
			public void alter(MetadataSchemaTypesBuilder types) {
				types.getSchemaType(zeSchema.typeCode()).setRecordCacheType(FULLY_CACHED);
			}
		});
		initTestVariables();

		SolrRecordDTO dto1, dto2, dto3, dto6, dto7, dto8;
		dto1 = new SolrRecordDTO(zeroPadded(1), 12L, fields("zeCollection", zeSchema.code()), FULLY_LOADED);
		dto2 = new SolrRecordDTO(zeroPadded(2), 89L, fields("zeCollection", zeSchema.code()), FULLY_LOADED);
		dto3 = new SolrRecordDTO(zeroPadded(3), 23L, fields("zeCollection", zeSchema.code()), FULLY_LOADED);
		dto6 = new SolrRecordDTO(zeroPadded(6), 34L, fields("zeCollection", zeSchema.code()), FULLY_LOADED);
		dto7 = new SolrRecordDTO(zeroPadded(7), 11L, fields("zeCollection", zeSchema.code()), FULLY_LOADED);
		dto8 = new SolrRecordDTO(zeroPadded(8), 67L, fields("zeCollection", zeSchema.code()), FULLY_LOADED);

		dataStore.insertWithoutReservingSpaceForPreviousIds(dto1);
		dataStore.insertWithoutReservingSpaceForPreviousIds(dto3);
		dataStore.insertWithoutReservingSpaceForPreviousIds(dto6);

		assertThat(dataStore.intIdsDataStore.ids.stream().collect(toList()))
				.containsExactly(1, 3, 6);
		dataStore.insertWithoutReservingSpaceForPreviousIds(dto8);

		//There is no space for record 2, which is a normal case
		assertThat(dataStore.intIdsDataStore.ids.stream().collect(toList()))
				.containsExactly(1, 3, 6, 8);

		assertThat(dataStore.intIdsDataStore.versions.stream().collect(toList()))
				.containsExactly(12L, 23L, 34L, 67L);

		assertThat(dataStore.intIdsDataStore.schema.stream().collect(toList()))
				.containsExactly(zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId);

		assertThat(dataStore.stream(zeCollectionId, zeCollectionType1Id).collect(toList()))
				.containsOnly(dto1, dto3, dto6, dto8);

		dataStore.insertWithoutReservingSpaceForPreviousIds(dto2);

		assertThat(dataStore.intIdsDataStore.ids.stream().collect(toList()))
				.containsExactly(1, 2, 3, 6, 8);

		assertThat(dataStore.intIdsDataStore.versions.stream().collect(toList()))
				.containsExactly(12L, 89L, 23L, 34L, 67L);

		assertThat(dataStore.intIdsDataStore.schema.stream().collect(toList()))
				.containsExactly(zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId);

		assertThat(dataStore.stream(zeCollectionId, zeCollectionType1Id).collect(toList()))
				.containsOnly(dto1, dto2, dto3, dto6, dto8);

		dataStore.insertWithoutReservingSpaceForPreviousIds(dto7);

		assertThat(dataStore.intIdsDataStore.ids.stream().collect(toList()))
				.containsExactly(1, 2, 3, 6, 7, 8);

		assertThat(dataStore.intIdsDataStore.versions.stream().collect(toList()))
				.containsExactly(12L, 89L, 23L, 34L, 11L, 67L);

		assertThat(dataStore.intIdsDataStore.schema.stream().collect(toList()))
				.containsExactly(zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId);

		assertThat(dataStore.stream(zeCollectionId, zeCollectionType1Id).collect(toList()))
				.containsOnly(dto1, dto2, dto3, dto6, dto7, dto8);
	}


	@Test
	public void whenAddingASummaryCachedRecordInASpaceThatWasNotReservedThenRecreateArraysAddingNewReservedIndex()
			throws Exception {
		//TODO : Boss de la fin
		defineSchemasManager().using(setup.withABooleanMetadata(whichIsEssentialInSummary));


		getModelLayerFactory().getMetadataSchemasManager().modify(zeCollection, new MetadataSchemaTypesAlteration() {
			@Override
			public void alter(MetadataSchemaTypesBuilder types) {
				types.getSchemaType(zeSchema.typeCode()).setRecordCacheType(RecordCacheType.SUMMARY_CACHED_WITHOUT_VOLATILE);
			}
		});
		initTestVariables();

		ByteArrayRecordDTO dto1, dto2, dto3, dto6, dto8;
		dto1 = create(new SolrRecordDTO(zeroPadded(1), 12L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto3 = create(new SolrRecordDTO(zeroPadded(3), 23L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto6 = create(new SolrRecordDTO(zeroPadded(6), 34L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto8 = create(new SolrRecordDTO(zeroPadded(8), 45L, fields("zeCollection", zeSchema.code()), SUMMARY));

		dataStore.insertWithoutReservingSpaceForPreviousIds(dto1);
		dataStore.insertWithoutReservingSpaceForPreviousIds(dto3);
		dataStore.insertWithoutReservingSpaceForPreviousIds(dto6);
		dataStore.insert(dto8);

		//There is no space for record 2, which is a normal case
		assertThat(dataStore.intIdsDataStore.ids.stream().collect(toList()))
				.containsExactly(1, 3, 6, 7, 8);

		//No space was reserved for id 2, recreating all arrays with a space for 2
		dto2 = create(new SolrRecordDTO(zeroPadded(2), 56L, fields("zeCollection", zeSchema.code(), "booleanMetadata_s", false), SUMMARY));
		dataStore.insertWithoutReservingSpaceForPreviousIds(dto2);

		assertThat(dataStore.intIdsDataStore.ids.stream().collect(toList()))
				.containsExactly(1, 2, 3, 6, 7, 8);

		assertThat(dataStore.intIdsDataStore.versions.stream().collect(toList()))
				.containsExactly(12L, 56L, 23L, 34L, 0L, 45L);

		assertThat(dataStore.intIdsDataStore.schema.stream().collect(toList()))
				.containsExactly(zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, zeSchemaDefaultId, (short) 0, zeSchemaDefaultId);

		assertThat(dataStore.stream(zeCollectionId, zeCollectionType1Id).collect(toList()))
				.containsOnly(dto1, dto2, dto3, dto6, dto8);

	}

	@Test
	public void whenInvalidatingSchemaTypeThenInvalidateAllRecordsUsingPredicate()
			throws Exception {
		defineSchemasManager().using(setup.withABooleanMetadata(whichIsEssentialInSummary));


		getModelLayerFactory().getMetadataSchemasManager().modify(zeCollection, new MetadataSchemaTypesAlteration() {
			@Override
			public void alter(MetadataSchemaTypesBuilder types) {
				types.getSchemaType(zeSchema.typeCode()).setRecordCacheType(RecordCacheType.SUMMARY_CACHED_WITHOUT_VOLATILE);
			}
		});
		initTestVariables();

		ByteArrayRecordDTO dto1, dto2, dto3, dto6, dto8;
		dto1 = create(new SolrRecordDTO(zeroPadded(1), 12L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto2 = create(new SolrRecordDTO(zeroPadded(2), 56L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto3 = create(new SolrRecordDTO(zeroPadded(3), 23L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto6 = create(new SolrRecordDTO(zeroPadded(6), 34L, fields("zeCollection", zeSchema.code()), SUMMARY));
		dto8 = create(new SolrRecordDTO(zeroPadded(8), 45L, fields("zeCollection", zeSchema.code()), SUMMARY));

		dataStore.insertWithoutReservingSpaceForPreviousIds(dto1);
		dataStore.insertWithoutReservingSpaceForPreviousIds(dto2);
		dataStore.insertWithoutReservingSpaceForPreviousIds(dto3);
		dataStore.insertWithoutReservingSpaceForPreviousIds(dto6);
		dataStore.insert(dto8);

		assertThat(dataStore.intIdsDataStore.ids.stream().collect(toList()))
				.containsExactly(1, 2, 3, 6, 7, 8);
		assertThat(dataStore.stream(zeCollectionId, zeCollectionType1Id).collect(toList()))
				.containsExactly(dto1, dto2, dto3, dto6, dto8);

		assertThat(intArrayToList(dataStore.intIdsDataStore.typesIndexes[zeCollectionIndex][zeCollectionType1Id]))
				.containsExactly(0, 1, 2, 3, 5);

		//Invalidating other collection and types : Nothing happens
		dataStore.invalidate(anotherCollectionId, zeCollectionType1Id, (r) -> r.getVersion() % 2 == 0);
		assertThat(intArrayToList(dataStore.intIdsDataStore.typesIndexes[zeCollectionIndex][zeCollectionType1Id]))
				.containsExactly(0, 1, 2, 3, 5);

		dataStore.invalidate(zeCollectionId, zeCollectionType2Id, (r) -> r.getVersion() % 2 == 0);
		assertThat(intArrayToList(dataStore.intIdsDataStore.typesIndexes[zeCollectionIndex][zeCollectionType1Id]))
				.containsExactly(0, 1, 2, 3, 5);

		assertThat(dataStore.stream(zeCollectionId, zeCollectionType1Id).collect(toList()))
				.containsExactly(dto1, dto2, dto3, dto6, dto8);


		dataStore.invalidate(zeCollectionId, zeCollectionType1Id, (r) -> r.getVersion() % 2 == 0);

		assertThat(dataStore.intIdsDataStore.ids.stream().collect(toList()))
				.containsExactly(1, 2, 3, 6, 7, 8);

		assertThat(intArrayToList(dataStore.intIdsDataStore.typesIndexes[zeCollectionIndex][zeCollectionType1Id]))
				.containsExactly(2, 5);

		assertThat(dataStore.intIdsDataStore.versions.stream().collect(toList()))
				.containsExactly(0L, 0L, 23L, 0L, 0L, 45L);

		assertThat(dataStore.intIdsDataStore.schema.stream().collect(toList()))
				.containsExactly((short) 0, (short) 0, zeSchemaDefaultId, (short) 0, (short) 0, zeSchemaDefaultId);

		assertThat(dataStore.stream(zeCollectionId, zeCollectionType1Id).collect(toList()))
				.containsExactly(dto3, dto8);

	}

	private List<Integer> intArrayToList(IntArrayList intArrayList) {
		List<Integer> list = new ArrayList<>();
		intArrayList.forEach((v) -> list.add(v));
		return list;
	}


	@Test
	public void givenRecordsInMultipleCollectionsSchemaTypesAndDifferentIdPatternsThenAllSavedAndRetrievableWithStreams()
			throws Exception {

		givenCollection("collection1");
		givenCollection("collection2");

		getModelLayerFactory().getMetadataSchemasManager().modify("collection1", new MetadataSchemaTypesAlteration() {
			@Override
			public void alter(MetadataSchemaTypesBuilder types) {
				types.createNewSchemaType("type1").setRecordCacheType(RecordCacheType.SUMMARY_CACHED_WITHOUT_VOLATILE);
				types.createNewSchemaType("type2").setRecordCacheType(RecordCacheType.SUMMARY_CACHED_WITHOUT_VOLATILE);
			}
		});

		getModelLayerFactory().getMetadataSchemasManager().modify("collection2", new MetadataSchemaTypesAlteration() {
			@Override
			public void alter(MetadataSchemaTypesBuilder types) {
				types.createNewSchemaType("type1").setRecordCacheType(RecordCacheType.SUMMARY_CACHED_WITHOUT_VOLATILE);
				types.createNewSchemaType("type2").setRecordCacheType(RecordCacheType.SUMMARY_CACHED_WITHOUT_VOLATILE);
			}
		});

		CollectionsListManager collectionsListManager = getModelLayerFactory().getCollectionsListManager();
		MetadataSchemasManager schemasManager = getModelLayerFactory().getMetadataSchemasManager();
		dataStore = new RecordsCachesDataStore(getModelLayerFactory());

		byte collection1Id = collectionsListManager.getCollectionId("collection1");
		byte collection2Id = collectionsListManager.getCollectionId("collection2");
		short collection1Type1 = schemasManager.getSchemaTypes(collection1Id).getSchemaType("type1").getId();
		short collection1Type2 = schemasManager.getSchemaTypes(collection1Id).getSchemaType("type2").getId();
		short collection2Type1 = schemasManager.getSchemaTypes(collection2Id).getSchemaType("type1").getId();
		short collection2Type2 = schemasManager.getSchemaTypes(collection2Id).getSchemaType("type2").getId();

		List<RecordDTO> collection1Type1Records = new ArrayList<>();
		List<RecordDTO> collection1Type2Records = new ArrayList<>();
		List<RecordDTO> collection2Type1Records = new ArrayList<>();
		List<RecordDTO> collection2Type2Records = new ArrayList<>();

		BiFunction<String, String, RecordDTO> dtoCreator = new BiFunction<String, String, RecordDTO>() {
			int id = 1000;

			@Override
			public RecordDTO apply(String collection, String schema) {
				long version = id + 10000;

				String strId;
				if (id % 7 == 0) {
					strId = "mouhahahaha" + id;
				} else {
					strId = zeroPadded(id);
				}
				id++;

				RecordDTO dto = create(new SolrRecordDTO(strId, version, fields(collection, schema), SUMMARY));
				dataStore.insert(dto);
				return dto;
			}
		};

		range(0, 1000).forEach((i -> collection1Type1Records.add(dtoCreator.apply("collection1", "type1_default"))));
		range(0, 1000).forEach((i -> collection1Type2Records.add(dtoCreator.apply("collection1", "type2_default"))));
		range(0, 1000).forEach((i -> collection2Type1Records.add(dtoCreator.apply("collection2", "type1_default"))));
		range(0, 1000).forEach((i -> collection2Type2Records.add(dtoCreator.apply("collection2", "type2_default"))));


		assertThat(dataStore.stream().collect(toList()))
				.containsAll(collection1Type1Records)
				.containsAll(collection1Type2Records)
				.containsAll(collection2Type1Records)
				.containsAll(collection2Type2Records)
				.hasSize(4000);

		assertThat(dataStore.stream(collection1Id).collect(toList()))
				.containsAll(collection1Type1Records)
				.containsAll(collection1Type2Records)
				.hasSize(2000);

		assertThat(dataStore.stream(collection2Id).collect(toList()))
				.containsAll(collection2Type1Records)
				.containsAll(collection2Type2Records)
				.hasSize(2000);

		assertThat(dataStore.stream(collection1Id, collection1Type1).collect(toList()))
				.containsAll(collection1Type1Records)
				.hasSize(1000);

		assertThat(dataStore.stream(collection1Id, collection1Type2).collect(toList()))
				.containsAll(collection1Type2Records)
				.hasSize(1000);

		assertThat(dataStore.stream(collection2Id, collection2Type1).collect(toList()))
				.containsAll(collection2Type1Records)
				.hasSize(1000);

		assertThat(dataStore.stream(collection2Id, collection2Type2).collect(toList()))
				.containsAll(collection2Type2Records)
				.hasSize(1000);

		for (List<RecordDTO> list : asList(collection1Type1Records, collection1Type2Records,
				collection2Type1Records, collection2Type2Records)) {
			for (RecordDTO recordDTO : list) {
				assertThat(dataStore.get(recordDTO.getId())).isEqualTo(recordDTO);
			}
		}

	}

	//@Test
	public void givenHighCacheConcurrencyThenNoExceptionAndStreamsNeverReturnNulls()
			throws Exception {

		givenCollection("collection1");

		getModelLayerFactory().getMetadataSchemasManager().modify("collection1", new MetadataSchemaTypesAlteration() {
			@Override
			public void alter(MetadataSchemaTypesBuilder types) {
				types.createNewSchemaType("type1").setRecordCacheType(RecordCacheType.SUMMARY_CACHED_WITHOUT_VOLATILE);
			}
		});

		CollectionsListManager collectionsListManager = getModelLayerFactory().getCollectionsListManager();
		MetadataSchemasManager schemasManager = getModelLayerFactory().getMetadataSchemasManager();
		dataStore = new RecordsCachesDataStore(getModelLayerFactory());

		byte collection1Id = collectionsListManager.getCollectionId("collection1");
		short collection1Type1 = schemasManager.getSchemaTypes(collection1Id).getSchemaType("type1").getId();

		Random random = new Random();
		List<String> ids = new ArrayList<>();

		notAUnitItest = true;
		Function<Boolean, RecordDTO> dtoCreator = new Function<Boolean, RecordDTO>() {
			int id = 1000;

			@Override
			public RecordDTO apply(Boolean remove) {
				long version = id + 10000;

				String strId;
				if (id % 7 == 0) {
					strId = "mouhahahaha" + id;
				} else {
					strId = zeroPadded(id);
				}
				id++;


				RecordDTO dto = create(new SolrRecordDTO(strId, version, fields("collection1", "type1_default"), SUMMARY));
				dataStore.insert(dto);

				synchronized (ids) {
					ids.add(strId);

					if (remove) {
						String idToRemove = ids.remove(random.nextInt(ids.size() - 20));
						dataStore.remove(dto);
					}
				}

				return dto;
			}
		};

		//Adding 1000 records
		range(0, 1000).forEach((i -> ids.add(dtoCreator.apply(false).getId())));

		AtomicBoolean running = new AtomicBoolean(true);
		AtomicInteger finishedWithoutErrors = new AtomicInteger(0);


		ThreadList threadList = new ThreadList();

		int creatingThreads = 50;
		int creatingDeletingThreads = 10;
		int streamingThreads = 100;

		for (int i = 0; i < creatingThreads; i++) {
			threadList.add(new Thread(() -> {
				while (running.get()) {
					dtoCreator.apply(false);
				}
				finishedWithoutErrors.incrementAndGet();
			}));
		}

		for (int i = 0; i < creatingDeletingThreads; i++) {
			threadList.add(new Thread(() -> {
				while (running.get()) {
					dtoCreator.apply(true);
				}
				finishedWithoutErrors.incrementAndGet();
			}));
		}

		for (int i = 0; i < streamingThreads; i++) {
			threadList.add(new Thread(() -> {
				while (running.get()) {
					int returnedCount = 0;
					int minimumExpected = 0;
					synchronized (ids) {
						minimumExpected = ids.size();
					}
					for (RecordDTO recordDTO : dataStore.stream().collect(toList())) {
						if (recordDTO == null) {
							throw new RuntimeException("Null returned when streaming");
						}
						returnedCount++;
					}

				}
				finishedWithoutErrors.incrementAndGet();
			}));
		}

		threadList.startAll();

		while (ids.size() < 1_000_000) {
			System.out.println(ids.size());
			Thread.sleep(100);
		}
		running.set(false);
		threadList.joinAll();

		System.out.println("Datastore size : " + ids.size());
		assertThat(finishedWithoutErrors.get()).isEqualTo(creatingThreads + creatingDeletingThreads + streamingThreads);

	}

	//	@Test
	//	public void whenInsertingInSummaryCacheWithFullVolatileCacheThenBothCacheAreUpdated() {
	//		givenCollection("collection1");
	//
	//		getModelLayerFactory().getMetadataSchemasManager().modify("collection1", new MetadataSchemaTypesAlteration() {
	//			@Override
	//			public void alter(MetadataSchemaTypesBuilder types) {
	//				types.createNewSchemaType("type1").setRecordCacheType(RecordCacheType.SUMMARY_CACHED_WITH_VOLATILE);
	//			}
	//		});
	//
	//		CollectionsListManager collectionsListManager = getModelLayerFactory().getCollectionsListManager();
	//		MetadataSchemasManager schemasManager = getModelLayerFactory().getMetadataSchemasManager();
	//		dataStore = new RecordsCachesDataStore(getModelLayerFactory());
	//
	//		byte collection1Id = collectionsListManager.getCollectionId("collection1");
	//		short collection1Type1 = schemasManager.getSchemaTypes(collection1Id).getSchemaType("type1").getId();
	//
	//	}

	@Test
	public void whenInsertingFullyCachedZeroPaddedRecordsWithIndexedMetadataValuesThenRecordsFindableUsingMetadata() {
		//ICI
		whenInsertingCachedRecordsWithIndexedMetadataValuesThenRecordsFindableUsingMetadata(true, true);
	}


	@Test
	public void whenInsertingSummaryCachedZeroPaddedRecordsWithIndexedMetadataValuesThenRecordsFindableUsingMetadata() {
		whenInsertingCachedRecordsWithIndexedMetadataValuesThenRecordsFindableUsingMetadata(false, true);
	}

	@Test
	public void whenInsertingFullyCachedNonZeroPaddedRecordsWithIndexedMetadataValuesThenRecordsFindableUsingMetadata() {
		whenInsertingCachedRecordsWithIndexedMetadataValuesThenRecordsFindableUsingMetadata(true, false);
	}


	@Test
	public void whenInsertingSummaryCachedNonZeroPaddedRecordsWithIndexedMetadataValuesThenRecordsFindableUsingMetadata() {
		whenInsertingCachedRecordsWithIndexedMetadataValuesThenRecordsFindableUsingMetadata(false, false);
	}


	private void whenInsertingCachedRecordsWithIndexedMetadataValuesThenRecordsFindableUsingMetadata(
			boolean fullyCached, boolean zeroPaddedId) {
		givenCollection(zeCollection);
		getModelLayerFactory().getMetadataSchemasManager().modify(zeCollection, new MetadataSchemaTypesAlteration() {
			@Override
			public void alter(MetadataSchemaTypesBuilder types) {

				MetadataSchemaBuilder typeSchema1 = types.createNewSchemaType("type1")
						.setRecordCacheType(fullyCached ? FULLY_CACHED : SUMMARY_CACHED_WITHOUT_VOLATILE).getDefaultSchema();

				MetadataSchemaBuilder typeSchema2 = types.createNewSchemaType("type2")
						.setRecordCacheType(fullyCached ? FULLY_CACHED : SUMMARY_CACHED_WITHOUT_VOLATILE).getDefaultSchema();


				typeSchema1.create("uniqueIntegerMetadata").setType(INTEGER).setUniqueValue(true);
				typeSchema1.create("uniqueStringMetadata").setType(STRING).setUniqueValue(true);

				typeSchema1.create("integersMetadata").setType(INTEGER).setMultivalue(true);
				typeSchema1.create("numbersMetadata").setType(NUMBER).setMultivalue(true);
				typeSchema1.create("booleanMetadata").setType(BOOLEAN);
				typeSchema1.create("stringsMetadata").setType(STRING).setMultivalue(true).setEssentialInSummary(true);
				typeSchema1.create("enumsMetadata").setType(ENUM).defineAsEnum(FolderStatus.class);
				typeSchema1.create("referencesMetadata").setType(REFERENCE).setMultivalue(true)
						.defineReferencesTo(types.getSchemaType("type2"));


				typeSchema2.create("uniqueIntegerMetadata").setType(INTEGER).setUniqueValue(true);
				typeSchema2.create("uniqueStringMetadata").setType(STRING).setUniqueValue(true);


			}
		});
		initTestVariables();
		MetadataSchemaType type1 = metadataSchemasManager.getSchemaTypes(zeCollection).getSchemaType("type1");
		MetadataSchemaType type2 = metadataSchemasManager.getSchemaTypes(zeCollection).getSchemaType("type2");
		String uniqueIntegerMetadata = type1.getDefaultSchema().get("uniqueIntegerMetadata").getDataStoreCode();
		String uniqueStringMetadata = type1.getDefaultSchema().get("uniqueStringMetadata").getDataStoreCode();
		String integersMetadata = type1.getDefaultSchema().get("integersMetadata").getDataStoreCode();
		String numbersMetadata = type1.getDefaultSchema().get("numbersMetadata").getDataStoreCode();
		String booleanMetadata = type1.getDefaultSchema().get("booleanMetadata").getDataStoreCode();
		String stringsMetadata = type1.getDefaultSchema().get("stringsMetadata").getDataStoreCode();
		String enumsMetadata = type1.getDefaultSchema().get("enumsMetadata").getDataStoreCode();
		String referencesMetadata = type1.getDefaultSchema().get("referencesMetadata").getDataStoreCode();


		short collection1Type1 = schemasManager.getSchemaTypes(zeCollectionId).getSchemaType("type1").getId();
		short collection1Type2 = schemasManager.getSchemaTypes(zeCollectionId).getSchemaType("type2").getId();

		String id1 = zeroPaddedId ? zeroPadded(1) : "1";
		String id2 = zeroPaddedId ? zeroPadded(2) : "2";
		String id3 = zeroPaddedId ? zeroPadded(3) : "3";
		String id4 = zeroPaddedId ? zeroPadded(4) : "4";
		String id5 = zeroPaddedId ? zeroPadded(5) : "5";
		String id6 = zeroPaddedId ? zeroPadded(6) : "6";
		String id7 = zeroPaddedId ? zeroPadded(7) : "7";
		String id8 = zeroPaddedId ? zeroPadded(8) : "8";

		List<SolrRecordDTO> dtosToInsert = new ArrayList<>();

		dtosToInsert.add(new SolrRecordDTO(id1, 11L, fields(zeCollection, "type2_default",
				uniqueIntegerMetadata, 1,
				uniqueStringMetadata, "A"
		), SUMMARY));
		dtosToInsert.add(new SolrRecordDTO(id2, 22L, fields(zeCollection, "type2_default",
				uniqueIntegerMetadata, 2,
				uniqueStringMetadata, "B"
		), SUMMARY));

		dtosToInsert.add(new SolrRecordDTO(id3, 33L, fields(zeCollection, "type2_default",
				uniqueIntegerMetadata, 3,
				uniqueStringMetadata, "C"
		), SUMMARY));


		dtosToInsert.add(new SolrRecordDTO(id4, 44L, fields(zeCollection, "type1_default",
				uniqueIntegerMetadata, 1,
				uniqueStringMetadata, "A",
				integersMetadata, asList(42, 56),
				numbersMetadata, asList(12.3, 45.90),
				booleanMetadata, true,
				stringsMetadata, asList("abc", "def"),
				enumsMetadata, FolderStatus.SEMI_ACTIVE.getCode(),
				referencesMetadata, asList(id2)
		), SUMMARY));

		dtosToInsert.add(new SolrRecordDTO(id5, 55L, fields(zeCollection, "type1_default",
				uniqueIntegerMetadata, 2,
				uniqueStringMetadata, "B",
				integersMetadata, asList(123, 456),
				numbersMetadata, asList(11.1, 45.6),
				booleanMetadata, false,
				stringsMetadata, asList("gh", "ij"),
				enumsMetadata, FolderStatus.ACTIVE.getCode(),
				referencesMetadata, asList(id1)
		), SUMMARY));

		dtosToInsert.add(new SolrRecordDTO(id6, 66L, fields(zeCollection, "type1_default",
				uniqueIntegerMetadata, 3,
				uniqueStringMetadata, "C",
				integersMetadata, asList(444, 555),
				numbersMetadata, asList(1000.0001, 2000.0002),
				booleanMetadata, false,
				stringsMetadata, asList("yyyy", "zzzz"),
				enumsMetadata, FolderStatus.INACTIVE_DEPOSITED.getCode(),
				referencesMetadata, asList(id3)
		), SUMMARY));

		dtosToInsert.add(new SolrRecordDTO(id7, 77L, fields(zeCollection, "type1_default",
				uniqueIntegerMetadata, 4,
				uniqueStringMetadata, "E",
				integersMetadata, asList(460, 461),
				numbersMetadata, asList(1300.0001, 21000.0002),
				booleanMetadata, false,
				stringsMetadata, asList("pfpfp", "asdas"),
				enumsMetadata, FolderStatus.INACTIVE_DEPOSITED.getCode(),
				referencesMetadata, asList(id5, id6)
		), SUMMARY));

		dtosToInsert.add(new SolrRecordDTO(id8, 88L, fields(zeCollection, "type1_default",
				uniqueIntegerMetadata, 5,
				uniqueStringMetadata, "F",
				integersMetadata, asList(460, 461),
				numbersMetadata, asList(1300.0001, 21000.0002),
				booleanMetadata, false,
				stringsMetadata, asList("pfpfp", "asdas"),
				enumsMetadata, FolderStatus.INACTIVE_DEPOSITED.getCode(),
				referencesMetadata, asList(id4, id6)
		), SUMMARY));

		dtosToInsert.forEach((dto -> {
			if (fullyCached) {
				dataStore.insert(dto);
			} else {
				dataStore.insert(create(dto));
			}
		}));

		assertThatRecordsWithValue(collection1Type2, uniqueIntegerMetadata, 1).containsOnly(id1);

		assertThatRecordsWithValue(collection1Type2, uniqueIntegerMetadata, 2).containsOnly(id2);

		assertThatRecordsWithValue(collection1Type2, uniqueIntegerMetadata, 3).containsOnly(id3);

		assertThatRecordsWithValue(collection1Type1, uniqueIntegerMetadata, 1).containsOnly(id4);

		assertThatRecordsWithValue(collection1Type1, uniqueIntegerMetadata, 2).containsOnly(id5);

		assertThatRecordsWithValue(collection1Type1, uniqueIntegerMetadata, 3).containsOnly(id6);

		assertThatRecordsWithValue(collection1Type1, uniqueIntegerMetadata, 42).isEmpty();

		assertThatRecordsWithValue(collection1Type2, uniqueStringMetadata, "A").containsOnly(id1);

		assertThatRecordsWithValue(collection1Type2, uniqueStringMetadata, "B").containsOnly(id2);

		assertThatRecordsWithValue(collection1Type2, uniqueStringMetadata, "C").containsOnly(id3);

		assertThatRecordsWithValue(collection1Type1, uniqueStringMetadata, "A").containsOnly(id4);

		assertThatRecordsWithValue(collection1Type1, uniqueStringMetadata, "B").containsOnly(id5);

		assertThatRecordsWithValue(collection1Type1, uniqueStringMetadata, "C").containsOnly(id6);

		assertThatRecordsWithValue(collection1Type1, integersMetadata, 42).containsOnly(id4);

		assertThatRecordsWithValue(collection1Type1, integersMetadata, 456).containsOnly(id5);

		assertThatRecordsWithValue(collection1Type1, integersMetadata, 555).containsOnly(id6);

		assertThatRecordsWithValue(collection1Type1, integersMetadata, 460).containsOnly(id7, id8);

		assertThatRecordsWithValue(collection1Type1, numbersMetadata, 12.3).containsOnly(id4);

		assertThatRecordsWithValue(collection1Type1, numbersMetadata, 45.6).containsOnly(id5);

		assertThatRecordsWithValue(collection1Type1, numbersMetadata, 1000.0001).containsOnly(id6);

		assertThatRecordsWithValue(collection1Type1, numbersMetadata, 21000.0002).containsOnly(id7, id8);

		assertThatRecordsWithValue(collection1Type1, booleanMetadata, true).containsOnly(id4);

		assertThatRecordsWithValue(collection1Type1, booleanMetadata, false).containsOnly(id5, id6, id7, id8);

		assertThatRecordsWithValue(collection1Type1, stringsMetadata, "abc").containsOnly(id4);

		assertThatRecordsWithValue(collection1Type1, stringsMetadata, "gh").containsOnly(id5);

		assertThatRecordsWithValue(collection1Type1, stringsMetadata, "yyyy").containsOnly(id6);

		assertThatRecordsWithValue(collection1Type1, stringsMetadata, "pfpfp").containsOnly(id7, id8);

		assertThatRecordsWithValue(collection1Type1, enumsMetadata, FolderStatus.SEMI_ACTIVE.getCode()).containsOnly(id4);

		assertThatRecordsWithValue(collection1Type1, enumsMetadata, FolderStatus.INACTIVE_DEPOSITED.getCode()).containsOnly(id6, id7, id8);

		assertThatRecordsWithValue(collection1Type1, referencesMetadata, id1).containsOnly(id5);

		assertThatRecordsWithValue(collection1Type1, referencesMetadata, id6).containsOnly(id7, id8);

		dataStore.remove(dataStore.get(id4));

		assertThatRecordsWithValue(collection1Type1, uniqueIntegerMetadata, 1).isEmpty();
		assertThatRecordsWithValue(collection1Type1, uniqueStringMetadata, "A").isEmpty();

		SolrRecordDTO solrRecordDTO = new SolrRecordDTO(id4, 44L, fields(zeCollection, "type1_default",
				uniqueIntegerMetadata, 14,
				uniqueStringMetadata, "W",
				integersMetadata, asList(4212, 564),
				numbersMetadata, asList(12.332, 45.9023),
				booleanMetadata, false,
				stringsMetadata, asList("lalala", "def"),
				enumsMetadata, FolderStatus.ACTIVE.getCode(),
				referencesMetadata, asList(id3)
		), SUMMARY);

		dataStore.insert(solrRecordDTO);

		assertThatRecordsWithValue(collection1Type1, uniqueIntegerMetadata, 1).isEmpty();
		assertThatRecordsWithValue(collection1Type1, uniqueStringMetadata, "A").isEmpty();
		assertThatRecordsWithValue(collection1Type1, integersMetadata, 42).isEmpty();
		assertThatRecordsWithValue(collection1Type1, numbersMetadata, 12.3).isEmpty();
		assertThatRecordsWithValue(collection1Type1, booleanMetadata, true).isEmpty();
		assertThatRecordsWithValue(collection1Type1, stringsMetadata, "abc").isEmpty();
		assertThatRecordsWithValue(collection1Type1, enumsMetadata, FolderStatus.SEMI_ACTIVE.getCode()).doesNotContain(id4);
		assertThatRecordsWithValue(collection1Type1, referencesMetadata, id2).doesNotContain(id4);

		assertThatRecordsWithValue(collection1Type1, uniqueIntegerMetadata, 14).containsOnly(id4);
		assertThatRecordsWithValue(collection1Type1, uniqueStringMetadata, "W").containsOnly(id4);
		assertThatRecordsWithValue(collection1Type1, integersMetadata, 4212).containsOnly(id4);
		assertThatRecordsWithValue(collection1Type1, numbersMetadata, 12.332).containsOnly(id4);
		assertThatRecordsWithValue(collection1Type1, booleanMetadata, false).containsOnly(id4, id5, id6, id7, id8);
		assertThatRecordsWithValue(collection1Type1, stringsMetadata, "lalala").containsOnly(id4);
		assertThatRecordsWithValue(collection1Type1, enumsMetadata, FolderStatus.ACTIVE.getCode()).containsOnly(id5, id4);
		assertThatRecordsWithValue(collection1Type1, referencesMetadata, id3).containsOnly(id6, id4);
	}

	private ListAssert<String> assertThatRecordsWithValue(short typeId, String metadataDataStoreCode,
														  Object searchedValue) {
		return assertThat(dataStore.stream(zeCollectionId, typeId)
				.filter(dto -> {
					Object fieldValue = dto.getFields().get(metadataDataStoreCode);
					if (fieldValue instanceof List) {
						return ((List) fieldValue).contains(searchedValue);
					} else {
						return LangUtils.isEqual(fieldValue, searchedValue);
					}


				}).map(RecordDTO::getId).collect(toList()));

	}

	private ByteArrayRecordDTO create(SolrRecordDTO solrRecordDTO) {
		return ByteArrayRecordDTO.create(getModelLayerFactory(), solrRecordDTO);
	}

	private String zeroPadded(int i) {
		return StringUtils.leftPad("" + i, 11, '0');
	}

	private Map<String, Object> fields(String collection, String schema, Object... extraFields) {
		Map<String, Object> fields = new HashMap<>();
		fields.put("collection_s", collection);
		fields.put("schema_s", schema);


		for (int i = 0; i < extraFields.length; i += 2) {
			fields.put((String) extraFields[i], extraFields[i + 1]);
		}

		return fields;
	}
}
