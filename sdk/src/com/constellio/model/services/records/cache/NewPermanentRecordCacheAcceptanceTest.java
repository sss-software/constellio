package com.constellio.model.services.records.cache;

import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.frameworks.validation.ValidationErrors;
import com.constellio.model.services.records.RecordImpl;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.schemas.MetadataSchemaTypesAlteration;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.search.query.ReturnedMetadatasFilter;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.services.users.UserServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.QueryCounter;
import com.constellio.sdk.tests.TestRecord;
import com.constellio.sdk.tests.schemas.TestsSchemasSetup;
import com.constellio.sdk.tests.schemas.TestsSchemasSetup.AnotherSchemaMetadatas;
import com.constellio.sdk.tests.schemas.TestsSchemasSetup.ZeSchemaMetadatas;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.constellio.model.entities.schemas.RecordCacheType.FULLY_CACHED;
import static com.constellio.model.entities.schemas.Schemas.TITLE;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.fromAllSchemasIn;
import static com.constellio.sdk.tests.QueryCounter.ON_SCHEMA_TYPES;
import static com.constellio.sdk.tests.TestUtils.assertThatStream;
import static com.constellio.sdk.tests.TestUtils.englishMessages;
import static com.constellio.sdk.tests.schemas.TestsSchemasSetup.whichIsUnique;
import static org.apache.ignite.internal.util.lang.GridFunc.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NewPermanentRecordCacheAcceptanceTest extends ConstellioTest {

	String anotherCollection = "anotherCollection";

	Transaction transaction;
	TestRecord record1, record2, record3, record4, record5, record18, record42;

	TestsSchemasSetup zeCollectionSchemas = new TestsSchemasSetup(zeCollection).withSecurityFlag(false);
	ZeSchemaMetadatas zeCollectionSchemaType1 = zeCollectionSchemas.new ZeSchemaMetadatas();
	AnotherSchemaMetadatas zeCollectionSchemaType2 = zeCollectionSchemas.new AnotherSchemaMetadatas();

	TestsSchemasSetup anotherCollectionSchemas = new TestsSchemasSetup(anotherCollection).withSecurityFlag(false);
	ZeSchemaMetadatas anotherCollectionSchemaType1 = anotherCollectionSchemas.new ZeSchemaMetadatas();

	RecordsCaches recordsCaches;

	UserServices userServices;
	RecordServices recordServices;
	SearchServices searchServices;

	StatsBigVaultServerExtension queriesListener;

	boolean useZeroPaddedIds;

	QueryCounter queryCounter;

	public NewPermanentRecordCacheAcceptanceTest(String testCase) {
		this.useZeroPaddedIds = testCase.equals("zero-padded-ids");
	}

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> testCases() {
		return Arrays.asList(new Object[][]{{"zero-padded-ids"}, {"string-ids"}});
	}

	@Before
	public void setUp()
			throws Exception {

		prepareSystem(
				withZeCollection().withAllTestUsers(),
				withCollection(anotherCollection).withAllTestUsers()
		);
		defineSchemasManager().using(zeCollectionSchemas.withAStringMetadata(whichIsUnique).withAnotherStringMetadata());
		defineSchemasManager().using(anotherCollectionSchemas);

		recordServices = getModelLayerFactory().newRecordServices();
		searchServices = getModelLayerFactory().newSearchServices();

		userServices = getModelLayerFactory().newUserServices();
		recordsCaches = getModelLayerFactory().getRecordsCaches();

		getModelLayerFactory().getMetadataSchemasManager().modify(zeCollection, new MetadataSchemaTypesAlteration() {
			@Override
			public void alter(MetadataSchemaTypesBuilder types) {
				types.getSchemaType(zeCollectionSchemaType1.typeCode()).setRecordCacheType(FULLY_CACHED);
				types.getSchemaType(zeCollectionSchemaType2.typeCode()).setRecordCacheType(FULLY_CACHED);
				types.getSchemaType(anotherCollectionSchemaType1.typeCode()).setRecordCacheType(FULLY_CACHED);
			}
		});

		queryCounter = new QueryCounter(getDataLayerFactory(), ON_SCHEMA_TYPES(zeCollectionSchemaType1.typeCode()));

	}

	@After
	public void tearDown() throws Exception {

		if (!failureDetectionTestWatcher.isFailed()) {
			RecordsCache2IntegrityDiagnosticService service = new RecordsCache2IntegrityDiagnosticService(getModelLayerFactory());
			ValidationErrors errors = service.validateIntegrity(false, true);
			//List<String> messages = englishMessages(errors).stream().map((s) -> substringBefore(s, " :")).collect(toList());

			List<String> messages = englishMessages(errors);
			assertThat(messages).isEmpty();
		}

	}

	//Not supporting other than full records
	//	@Test
	//	public void whenInsertingNotFullyLoadedRecordInPermanentCacheThenExceptionThrown()
	//			throws Exception {
	//
	//		Record record = newZeCollectionType1Record(1234).set(TITLE, "val1")
	//				.set(zeCollectionSchemaType1.stringMetadata(), "val2")
	//				.set(zeCollectionSchemaType1.anotherStringMetadata(), "val3");
	//		recordServices.add(record);
	//
	//		Record partiallyLoadedRecord = ((RecordImpl) getCustomlyLoaded(1234).set(TITLE, "val4"));
	//		partiallyLoadedRecord.markAsSaved(partiallyLoadedRecord.getVersion() - 1000, zeCollectionSchemaType1.instance());
	//
	//		try {
	//			cache.insert(partiallyLoadedRecord, WAS_OBTAINED);
	//			fail("Exception expected");
	//		} catch (IllegalStateException e) {
	//			//OK
	//		}
	//
	//		try {
	//			cache.insert(partiallyLoadedRecord, WAS_MODIFIED);
	//			fail("Exception expected");
	//		} catch (IllegalStateException e) {
	//			//OK
	//		}
	//
	//		partiallyLoadedRecord = ((RecordImpl) getCustomlyLoaded(1234).set(TITLE, "val5"));
	//		partiallyLoadedRecord.markAsSaved(partiallyLoadedRecord.getVersion() + 1000, zeCollectionSchemaType1.instance());
	//
	//		try {
	//			cache.insert(partiallyLoadedRecord, WAS_OBTAINED);
	//
	//			fail("Exception expected");
	//		} catch (IllegalStateException e) {
	//			//OK
	//		}
	//
	//		//		Record recordFromCache = cache.getRecord(id(1234));
	//		//		assertThat(recordFromCache.<String>get(TITLE)).isEqualTo("val4");
	//		//		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.stringMetadata())).isEqualTo("val2");
	//		//		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.anotherStringMetadata())).isEqualTo("val3");
	//
	//	}


	@Test
	public void whenInsertingFullyCachedRecordsThenFullVersionObtainedEvenIfAskedForSummary()
			throws Exception {

		recordServices.add(newZeCollectionType1Record(1234).set(TITLE, "val1")
				.set(zeCollectionSchemaType1.stringMetadata(), "val2")
				.set(zeCollectionSchemaType1.anotherStringMetadata(), "val3"));

		recordServices.add(newZeCollectionType1Record(2345).set(TITLE, "valA")
				.set(zeCollectionSchemaType1.stringMetadata(), "valAA")
				.set(zeCollectionSchemaType1.anotherStringMetadata(), "valAAA"));

		queryCounter.reset();

		// Get by id

		Record recordFromCache = recordsCaches.getRecord(id(1234));
		assertThat(recordFromCache.isSummary()).isFalse();
		assertThat(recordFromCache.<String>get(TITLE)).isEqualTo("val1");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.stringMetadata())).isEqualTo("val2");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.anotherStringMetadata())).isEqualTo("val3");

		recordFromCache = recordsCaches.getRecordSummary(id(1234));
		assertThat(recordFromCache.isSummary()).isFalse();
		assertThat(recordFromCache.<String>get(TITLE)).isEqualTo("val1");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.stringMetadata())).isEqualTo("val2");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.anotherStringMetadata())).isEqualTo("val3");


		recordFromCache = recordsCaches.getCache(zeCollection).get(id(1234));
		assertThat(recordFromCache.isSummary()).isFalse();
		assertThat(recordFromCache.<String>get(TITLE)).isEqualTo("val1");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.stringMetadata())).isEqualTo("val2");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.anotherStringMetadata())).isEqualTo("val3");

		recordFromCache = recordsCaches.getCache(zeCollection).getSummary(id(1234));
		assertThat(recordFromCache.isSummary()).isFalse();
		assertThat(recordFromCache.<String>get(TITLE)).isEqualTo("val1");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.stringMetadata())).isEqualTo("val2");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.anotherStringMetadata())).isEqualTo("val3");


		// Get by metadata
		recordFromCache = recordsCaches.getCache(zeCollection).getByMetadata(zeCollectionSchemaType1.stringMetadata(), "val2");
		assertThat(recordFromCache.isSummary()).isFalse();
		assertThat(recordFromCache.<String>get(TITLE)).isEqualTo("val1");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.stringMetadata())).isEqualTo("val2");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.anotherStringMetadata())).isEqualTo("val3");

		recordFromCache = recordsCaches.getCache(zeCollection).getSummaryByMetadata(zeCollectionSchemaType1.stringMetadata(), "val2");
		assertThat(recordFromCache.isSummary()).isFalse();
		assertThat(recordFromCache.<String>get(TITLE)).isEqualTo("val1");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.stringMetadata())).isEqualTo("val2");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.anotherStringMetadata())).isEqualTo("val3");


		recordFromCache = recordsCaches.getCache(zeCollection).getByMetadata(zeCollectionSchemaType1.stringMetadata(), "valAA");
		assertThat(recordFromCache.isSummary()).isFalse();
		assertThat(recordFromCache.<String>get(TITLE)).isEqualTo("valA");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.stringMetadata())).isEqualTo("valAA");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.anotherStringMetadata())).isEqualTo("valAAA");

		recordFromCache = recordsCaches.getCache(zeCollection).getSummaryByMetadata(zeCollectionSchemaType1.stringMetadata(), "valAA");
		assertThat(recordFromCache.isSummary()).isFalse();
		assertThat(recordFromCache.<String>get(TITLE)).isEqualTo("valA");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.stringMetadata())).isEqualTo("valAA");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.anotherStringMetadata())).isEqualTo("valAAA");

		assertThat(recordsCaches.getCache(zeCollection).getAllValues(zeCollectionSchemaType1.typeCode())).hasSize(2);
		assertThat(recordsCaches.getCache(zeCollection).
				getAllValuesInUnmodifiableState(zeCollectionSchemaType1.typeCode())).hasSize(2);

		assertThat(recordsCaches.stream(zeCollectionSchemaType1.type()).count()).isEqualTo(2);

		assertThat(queryCounter.newQueryCalls()).isEqualTo(0);
	}

	@Test
	public void whenInsertingRecordsThenLastVersionInCache()
			throws Exception {

		Record record = newZeCollectionType1Record(1234).set(TITLE, "val1")
				.set(zeCollectionSchemaType1.stringMetadata(), "val2")
				.set(zeCollectionSchemaType1.anotherStringMetadata(), "val3");
		recordServices.add(record);

		Record recordFromCache = recordsCaches.getRecord(id(1234));
		assertThat(recordFromCache.isSummary()).isFalse();
		assertThat(recordFromCache.<String>get(TITLE)).isEqualTo("val1");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.stringMetadata())).isEqualTo("val2");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.anotherStringMetadata())).isEqualTo("val3");

		record.set(TITLE, "val4")
				.set(zeCollectionSchemaType1.stringMetadata(), "val5")
				.set(zeCollectionSchemaType1.anotherStringMetadata(), "val6");
		recordServices.add(record);

		recordFromCache = recordsCaches.getRecord(id(1234));
		assertThat(recordFromCache.isSummary()).isFalse();
		assertThat(recordFromCache.<String>get(TITLE)).isEqualTo("val4");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.stringMetadata())).isEqualTo("val5");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.anotherStringMetadata())).isEqualTo("val6");

		recordServices.logicallyDelete(record, User.GOD);

		recordFromCache = recordsCaches.getRecord(id(1234));
		assertThat(recordFromCache.isSummary()).isFalse();
		assertThat(recordFromCache.<String>get(TITLE)).isEqualTo("val4");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.stringMetadata())).isEqualTo("val5");
		assertThat(recordFromCache.<String>get(zeCollectionSchemaType1.anotherStringMetadata())).isEqualTo("val6");
		assertThatStream(recordsCaches.stream(zeCollection).map(Record::getId)).contains(id(1234));
		assertThatStream(recordsCaches.stream(zeCollectionSchemaType1.type()).map(Record::getId)).contains(id(1234));

		recordServices.physicallyDelete(record, User.GOD);

		recordFromCache = recordsCaches.getRecord(id(1234));
		assertThat(recordFromCache).isNull();
		assertThatStream(recordsCaches.stream(zeCollection).map(Record::getId)).doesNotContain(id(1234));
		assertThatStream(recordsCaches.stream(zeCollectionSchemaType1.type()).map(Record::getId)).doesNotContain(id(1234));

		assertThatStream(recordsCaches.stream(zeCollection).map(Record::getId)).doesNotContain(id(1234));
		assertThatStream(recordsCaches.stream(zeCollectionSchemaType1.type()).map(Record::getId)).doesNotContain(id(1234));

	}


	@Test
	public void givenInsertedRecordsWhenReloadingRecordsOfTypeThenNoDuplcatedAndAllReloaded()
			throws Exception {

		Record record1_state1 = newZeCollectionType1Record(1234).set(TITLE, "val1a");
		recordServices.add(record1_state1);

		Record record2_state1 = newZeCollectionType1Record(2345).set(TITLE, "val2a");
		recordServices.add(record2_state1);

		Record otherTypeRecord1_state1 = newZeCollectionType2Record(3456).set(TITLE, "val3a");
		recordServices.add(otherTypeRecord1_state1);

		Record otherCollectionRecord1_state1 = newAnotherCollectionType1Record(4567).set(TITLE, "val4a");
		recordServices.add(otherCollectionRecord1_state1);

		assertThatStream(recordsCaches.stream(zeCollectionSchemaType1.type()).map(Record::getId)).containsOnly(id(1234), id(2345));
		assertThatStream(recordsCaches.stream(zeCollection).map(Record::getId)).containsOnlyOnce(id(1234), id(2345), id(3456));

		recordsCaches.getCache(zeCollection).invalidateVolatileReloadPermanent(asList(zeCollectionSchemaType1.typeCode()));

		assertThatStream(recordsCaches.stream(zeCollectionSchemaType1.type()).map(Record::getId)).containsOnly(id(1234), id(2345));
		assertThatStream(recordsCaches.stream(zeCollection).map(Record::getId)).containsOnlyOnce(id(1234), id(2345), id(3456));


	}

	private Record newZeCollectionType1Record(int intId) {
		return getModelLayerFactory().newRecordServices().newRecordWithSchema(zeCollectionSchemaType1.instance(), id(intId));
	}

	private Record newZeCollectionType2Record(int intId) {
		return getModelLayerFactory().newRecordServices().newRecordWithSchema(zeCollectionSchemaType2.instance(), id(intId));
	}

	private Record newAnotherCollectionType1Record(int intId) {
		return getModelLayerFactory().newRecordServices().newRecordWithSchema(anotherCollectionSchemaType1.instance(), id(intId));
	}

	private String id(int intId) {
		return useZeroPaddedIds ? StringUtils.leftPad("" + intId, 11, "0") : "" + intId;
	}

	private RecordImpl getCustomlyLoaded(int id) {
		return (RecordImpl) searchServices.search(new LogicalSearchQuery().setCondition(
				fromAllSchemasIn(zeCollection).where(Schemas.IDENTIFIER).isEqualTo(id(id)))
				.setReturnedMetadatas(ReturnedMetadatasFilter.idVersionSchemaTitlePath())).get(0);

	}
}
