package com.constellio.app.services.importExport.records;

import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.*;
import static com.constellio.sdk.tests.TestUtils.asList;
import static com.constellio.sdk.tests.TestUtils.assertThatRecords;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.constellio.app.modules.rm.model.CopyRetentionRule;
import com.constellio.app.modules.rm.model.RetentionPeriod;
import com.constellio.app.modules.rm.model.enums.CopyType;
import com.constellio.app.modules.rm.model.enums.DisposalType;
import com.constellio.app.modules.rm.wrappers.*;
import com.constellio.app.modules.rm.wrappers.structures.Comment;
import com.constellio.app.modules.rm.wrappers.type.ContainerRecordType;
import com.constellio.app.modules.rm.wrappers.type.MediumType;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.records.wrappers.Group;
import com.constellio.model.entities.records.wrappers.Report;
import com.constellio.model.entities.records.wrappers.structure.ReportedMetadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.schemas.MetadataSchemasManager;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.groups.Tuple;
import org.junit.Test;

import com.constellio.app.modules.rm.RMTestRecords;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.type.DocumentType;
import com.constellio.app.services.schemas.bulkImport.BulkImportParams;
import com.constellio.app.services.schemas.bulkImport.LoggerBulkImportProgressionListener;
import com.constellio.app.services.schemas.bulkImport.RecordsImportServices;
import com.constellio.app.services.schemas.bulkImport.data.ImportDataProvider;
import com.constellio.app.services.schemas.bulkImport.data.xml.XMLImportDataProvider;
import com.constellio.app.ui.i18n.i18n;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.frameworks.validation.ValidationException;
import com.constellio.model.services.users.UserServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.setups.Users;

public class RecordExportServicesAcceptanceTest extends ConstellioTest {

	RMTestRecords records = new RMTestRecords(zeCollection);
	RecordExportOptions options = new RecordExportOptions();
	Users users = new Users();

	@Test(expected = RecordExportServicesRuntimeException.ExportServicesRuntimeException_NoRecords.class)
	public void givenEmptyCollectionWhenExportRecordsThenExceptionThrown()
			throws Exception {

		prepareSystem(
				withZeCollection().withConstellioRMModule().withConstellioRMModule().withAllTest(users),
				withCollection("anotherCollection").withConstellioRMModule().withAllTest(users));

		exportThenImportInAnotherCollection(options);

	}

	@Test
	public void whenExportingReport() throws Exception {
		prepareSystem(
				withZeCollection().withConstellioRMModule().withConstellioRMModule().withAllTest(users)
						.withRMTest(records).withFoldersAndContainersOfEveryStatus().withDocumentsDecommissioningList(),
				withCollection("anotherCollection").withConstellioRMModule().withAllTest(users));


		RMSchemasRecordsServices rm = new RMSchemasRecordsServices(zeCollection, getAppLayerFactory());

		final String TITLE = "title";
		final String SCHEMA_TYPE_CODE = Document.DEFAULT_SCHEMA;

		Report report = rm.newReport();
		report.setColumnsCount(1);
		report.setLinesCount(1);
		report.setTitle(TITLE);
		report.setSchemaTypeCode(SCHEMA_TYPE_CODE);

		ReportedMetadata reportedMetadata1 = new ReportedMetadata("title", 1);
		reportedMetadata1.setYPosition(2);
		ReportedMetadata reportedMetadata2 = new ReportedMetadata("id", 3);
		reportedMetadata2.setYPosition(4);

		List<ReportedMetadata> reportedMetadataList = new ArrayList<>();
		reportedMetadataList.add(reportedMetadata1);
		reportedMetadataList.add(reportedMetadata2);

		report.setReportedMetadata(reportedMetadataList);

		RecordServices recordServices = getModelLayerFactory().newRecordServices();

		Transaction transaction = new Transaction();

		transaction.add(report);

		recordServices.execute(transaction);

		exportThenImportInAnotherCollection(
				options.setExportedSchemaTypes(asList(Report.SCHEMA_TYPE)));

		List<Record> listRecordReport;

		RMSchemasRecordsServices rmFromAnOtherCollection = new RMSchemasRecordsServices("anotherCollection", getAppLayerFactory());
		MetadataSchemasManager schemasManager = getAppLayerFactory().getModelLayerFactory().getMetadataSchemasManager();
		MetadataSchema metadataSchemaTypes = schemasManager.getSchemaTypes("anotherCollection").getSchema(Report.DEFAULT_SCHEMA);

		LogicalSearchQuery query = new LogicalSearchQuery();
		query.setCondition(from(metadataSchemaTypes).returnAll());

		listRecordReport = getModelLayerFactory().newSearchServices().search(query);

		assertThatRecords(listRecordReport).extractingMetadatas("columnsCount", "title",
				"linesCount", "schemaTypeCode").contains(tuple(1.0, TITLE, 1.0, SCHEMA_TYPE_CODE));

		Record record = listRecordReport.get(0);
		Report reportFromAnOtherCollection = rmFromAnOtherCollection.wrapReport(record);

		assertThat(reportFromAnOtherCollection.getReportedMetadata().get(0).getXPosition()).isEqualTo(1);
		assertThat(reportFromAnOtherCollection.getReportedMetadata().get(0).getYPosition()).isEqualTo(2);
		assertThat(reportFromAnOtherCollection.getReportedMetadata().get(1).getXPosition()).isEqualTo(3);
		assertThat(reportFromAnOtherCollection.getReportedMetadata().get(1).getYPosition()).isEqualTo(4);
	}

	@Test
	public void whenExportingDecommissionList()
	{
		prepareSystem(
				withZeCollection().withConstellioRMModule().withConstellioRMModule().withAllTest(users)
						.withRMTest(records).withFoldersAndContainersOfEveryStatus().withDocumentsDecommissioningList(),
				withCollection("anotherCollection").withConstellioRMModule().withAllTest(users));


		exportThenImportInAnotherCollection(
				options.setExportedSchemaTypes(asList(AdministrativeUnit.SCHEMA_TYPE, Document.SCHEMA_TYPE, DocumentType.SCHEMA_TYPE,
						Folder.SCHEMA_TYPE,	DecommissioningList.SCHEMA_TYPE, RetentionRule.SCHEMA_TYPE,
						Category.SCHEMA_TYPE, MediumType.SCHEMA_TYPE, ContainerRecord.SCHEMA_TYPE,
						ContainerRecordType.SCHEMA_TYPE, StorageSpace.SCHEMA_TYPE, User.SCHEMA_TYPE, Group.SCHEMA_TYPE)));


		RMSchemasRecordsServices rmAnotherCollection = new RMSchemasRecordsServices("anotherCollection", getAppLayerFactory());

		List<DecommissioningList> listSearchDecommissiongList = rmAnotherCollection.searchDecommissioningLists(returnAll());

		assertThatRecords(listSearchDecommissiongList).extractingMetadatas("ID");

		assertThat(listSearchDecommissiongList.size()).isEqualTo(1);
	}

	@Test
	public void whenExportingComment() throws Exception {
		prepareSystem(
				withZeCollection().withConstellioRMModule().withConstellioRMModule().withAllTest(users).withRMTest(records),
				withCollection("anotherCollection").withConstellioRMModule().withAllTest(users));
		final String MESSAGE = "Message";
		final User user = records.getAdmin();

		Comment comment = new Comment();
		comment.setUser(records.getAdmin());
		comment.setMessage(MESSAGE);


		RecordServices recordServices = getModelLayerFactory().newRecordServices();

		Transaction transaction = new Transaction();

		Category category = records.getCategory_X().setComments(asList(comment));
		transaction.update(category.getWrappedRecord());

		recordServices.execute(transaction);

		exportThenImportInAnotherCollection(
				options.setExportedSchemaTypes(asList(AdministrativeUnit.SCHEMA_TYPE, RetentionRule.SCHEMA_TYPE,Category.SCHEMA_TYPE)));


		RMSchemasRecordsServices rmAnotherCollection = new RMSchemasRecordsServices("anotherCollection", getAppLayerFactory());

		Category categoryFromAnOtherCollection = rmAnotherCollection.getCategoryWithCode("X");

		assertThat(categoryFromAnOtherCollection.getComments().size()).isEqualTo(1);

		Comment commentFromAnOtherCollection = categoryFromAnOtherCollection.getComments().get(0);
		assertThat(commentFromAnOtherCollection.getMessage()).isEqualTo(MESSAGE);
		assertThat(commentFromAnOtherCollection.getUsername()).isEqualTo(user.getUsername());
		assertThat(commentFromAnOtherCollection.getUserId()).isEqualTo(user.getId());
	}

	@Test
	public void whenExportingSpecificExportValueLists() {
		prepareSystem(
				withZeCollection().withConstellioRMModule().withConstellioRMModule().withAllTest(users).withRMTest(records),
				withCollection("anotherCollection").withConstellioRMModule().withAllTest(users));

		exportThenImportInAnotherCollection(
				options.setExportValueLists(true));

		RMSchemasRecordsServices rmAnotherCollection = new RMSchemasRecordsServices("anotherCollection", getAppLayerFactory());

		assertThatRecords(rmAnotherCollection.searchDocumentTypes(ALL)).extractingMetadatas("legacyIdentifier", "code", "title")
				.contains(
						tuple("documentTypeId_1", "1", "Livre de recettes"), tuple("documentTypeId_2", "2", "Typologie"),
						tuple("documentTypeId_3", "3", "Petit guide"), tuple("documentTypeId_4", "4", "Histoire"),
						tuple("documentTypeId_5", "5", "Calendrier des réunions"), tuple("documentTypeId_6", "6",
								"Dossier de réunion : avis de convocation, ordre du jour, procès-verbal, extraits de procès-verbaux, résolutions, documents déposés, correspondance"),
						tuple("documentTypeId_7", "7", "Notes de réunion"), tuple("documentTypeId_8", "8",
								"Dossiers des administrateurs : affirmations solennelles, serments de discrétion"),
						tuple("documentTypeId_9", "9", "Contrat"), tuple("documentTypeId_10", "10", "Procès-verbal"));

	}

	final String TITLE = "Title1";
	final String CODE = "CODE1";
	final String DESCRIPTION = "DESCRIPTION1";
	final String CONTENT_TYPES_COMMENT = "CONTENT_TYPES_COMMENT1";
	final String ACTIVE_RETENTION_COMMENT = "ACTIVE_RETENTION_COMMENT";
	final RetentionPeriod ACTIVE_RETENTION_PERIOD = RetentionPeriod.OPEN_888;
	final String SEMI_ACTIVE_RETENTION_COMMENT = "SEMI_ACTIVE_RETENTION_COMMENT";
	final RetentionPeriod SEMI_ACTIVE_RETENTION_PERIOD = RetentionPeriod.OPEN_888;
	final String INACTIVE_DISPOSAL_COMMENT = "DISPOSAL_COMMENT";
	final DisposalType INACTIVE_DISPOSAL_TYPE = DisposalType.DESTRUCTION;
	final Integer OPEN_ACTIVE_RETENTION_PERIOD = new Integer(100);
	final boolean REQUIRED_COPYRULE_FIELD = true;
	final String SET_ID = "ID1";

	@Test
	public void whenExportingFolderRetentionRuleThenExported()
			throws Exception {
		givenDisabledAfterTestValidations();
		prepareSystem(
				withZeCollection().withConstellioRMModule().withConstellioRMModule().withAllTest(users).withRMTest(records),
				withCollection("anotherCollection").withConstellioRMModule().withAllTest(users));

		RMSchemasRecordsServices rm = new RMSchemasRecordsServices(zeCollection, getAppLayerFactory());

		RetentionRule retentionRule = rm.newRetentionRule();

		Transaction transaction = new Transaction();

		ArrayList<CopyRetentionRule> arrayList = new ArrayList<>();

		CopyRetentionRule copyRetentionRule1 = getPrimaryCopyRetentionRule();
		CopyRetentionRule copyRetentionRule2 = getCopySecondaryRetentionRule();

		arrayList.add(copyRetentionRule1);
		arrayList.add(copyRetentionRule2);

		retentionRule.setTitle(TITLE);
		retentionRule.setCode(CODE);
		retentionRule.setResponsibleAdministrativeUnits(true);

		retentionRule.setCopyRetentionRules(arrayList);

		RecordServices recordService = getModelLayerFactory().newRecordServices();

		transaction.add(retentionRule);

		recordService.execute(transaction);

		// GetCopyRetentionRule.
		// Save avec une transaction.

		// Category.SCHEMA_TYPE, RetentionRule.SCHEMA_TYPE
		exportThenImportInAnotherCollection(
				options.setExportedSchemaTypes(
						asList(AdministrativeUnit.SCHEMA_TYPE, RetentionRule.SCHEMA_TYPE)));

		RMSchemasRecordsServices rmAnOtherCollection = new RMSchemasRecordsServices("anotherCollection", getAppLayerFactory());

		RetentionRule currentRetentionRule = rmAnOtherCollection.getRetentionRuleWithCode(CODE);

		List<CopyRetentionRule> retentionRuleList = currentRetentionRule.getCopyRetentionRules();

		// Test primary rententionRule.

		assertPrincipalCopyRetentionRule(retentionRuleList.get(0));
		assertSecondaryCopyRetentionRule(retentionRuleList.get(1));

		assertPrincipalCopyRetentionRule(currentRetentionRule.getPrincipalDefaultDocumentCopyRetentionRule());
		assertSecondaryCopyRetentionRule(currentRetentionRule.getSecondaryDefaultDocumentCopyRetentionRule());

		assertPrincipalCopyRetentionRule(currentRetentionRule.getDocumentCopyRetentionRules().get(0));
		assertSecondaryCopyRetentionRule(currentRetentionRule.getDocumentCopyRetentionRules().get(0));

		assertThatRecords(rmAnOtherCollection.searchAdministrativeUnits(ALL)).extractingMetadatas("code", "title", "parent.code")
				.containsOnly(
						tuple("10A", "Unité 10-A", "10"), tuple("11B", "Unité 11-B", "11"), tuple("11", "Unité 11", "10"),
						tuple("12", "Unité 12", "10"), tuple("20", "Unité 20", null), tuple("30", "Unité 30", null),
						tuple("10", "Unité 10", null), tuple("30C", "Unité 30-C", "30"), tuple("12B", "Unité 12-B", "12"),
						tuple("12C", "Unité 12-C", "12"), tuple("20D", "Unité 20-D", "20"), tuple("20E", "Unité 20-E", "20")
				);

	}

	private CopyRetentionRule getPrimaryCopyRetentionRule() {
		return new CopyRetentionRule().setCopyType(CopyType.PRINCIPAL).setCode(CODE)
                    .setTitle(TITLE)
                    .setDescription(DESCRIPTION).setContentTypesComment(CONTENT_TYPES_COMMENT)
                    .setActiveRetentionComment(ACTIVE_RETENTION_COMMENT)
                    .setActiveRetentionPeriod(ACTIVE_RETENTION_PERIOD).setSemiActiveRetentionComment(SEMI_ACTIVE_RETENTION_COMMENT)
                    .setSemiActiveRetentionPeriod(SEMI_ACTIVE_RETENTION_PERIOD)
                    .setInactiveDisposalComment(INACTIVE_DISPOSAL_COMMENT).setInactiveDisposalType(INACTIVE_DISPOSAL_TYPE)
                    .setOpenActiveRetentionPeriod(OPEN_ACTIVE_RETENTION_PERIOD)
                    .setEssential(REQUIRED_COPYRULE_FIELD).setId(SET_ID).setMediumTypeIds(records.PA_MD).setIgnoreActivePeriod(false);
	}

	private CopyRetentionRule getCopySecondaryRetentionRule() {
		return new CopyRetentionRule().setCopyType(CopyType.SECONDARY).setCode(CODE)
                    .setTitle(TITLE)
                    .setDescription(DESCRIPTION).setContentTypesComment(CONTENT_TYPES_COMMENT)
                    .setActiveRetentionComment(ACTIVE_RETENTION_COMMENT)
                    .setActiveRetentionPeriod(ACTIVE_RETENTION_PERIOD).setSemiActiveRetentionComment(SEMI_ACTIVE_RETENTION_COMMENT)
                    .setSemiActiveRetentionPeriod(SEMI_ACTIVE_RETENTION_PERIOD)
                    .setInactiveDisposalComment(INACTIVE_DISPOSAL_COMMENT).setInactiveDisposalType(INACTIVE_DISPOSAL_TYPE)
                    .setOpenActiveRetentionPeriod(OPEN_ACTIVE_RETENTION_PERIOD)
                    .setEssential(REQUIRED_COPYRULE_FIELD).setId(SET_ID).setMediumTypeIds(records.PA_MD).setIgnoreActivePeriod(true);
	}

	public void assertSecondaryCopyRetentionRule(CopyRetentionRule copyRetentionRule) {

		// Test secondary rententionRule.

		assertThat(copyRetentionRule.getCopyType()).isEqualTo(CopyType.SECONDARY);
		assertThat(copyRetentionRule.getCode()).isEqualTo(CODE);
		assertThat(copyRetentionRule.getTitle()).isEqualTo(TITLE);
		assertThat(copyRetentionRule.getDescription()).isEqualTo(DESCRIPTION);
		assertThat(copyRetentionRule.getContentTypesComment()).isEqualTo(CONTENT_TYPES_COMMENT);
		assertThat(copyRetentionRule.getActiveRetentionPeriod()).isEqualTo(ACTIVE_RETENTION_PERIOD);
		assertThat(copyRetentionRule.getSemiActiveRetentionComment()).isEqualTo(SEMI_ACTIVE_RETENTION_COMMENT);
		assertThat(copyRetentionRule.getSemiActiveRetentionPeriod()).isEqualTo(SEMI_ACTIVE_RETENTION_PERIOD);
		assertThat(copyRetentionRule.getInactiveDisposalComment()).isEqualTo(INACTIVE_DISPOSAL_COMMENT);
		assertThat(copyRetentionRule.getInactiveDisposalType()).isEqualTo(INACTIVE_DISPOSAL_TYPE);
		assertThat(copyRetentionRule.getActiveRetentionPeriod()).isEqualTo(ACTIVE_RETENTION_PERIOD);
		assertThat(copyRetentionRule.isEssential()).isEqualTo(REQUIRED_COPYRULE_FIELD);
		assertThat(copyRetentionRule.getId()).isEqualTo(SET_ID);
		assertThat(copyRetentionRule.getMediumTypeIds()).isNotNull();
		assertThat(copyRetentionRule.getTitle()).isEqualTo(TITLE);
		assertThat(copyRetentionRule.getCode()).isEqualTo(CODE);
		assertThat(copyRetentionRule.getTypeId()).isNull();
		assertThat(copyRetentionRule.isIgnoreActivePeriod()).isTrue();
	}

	public void assertPrincipalCopyRetentionRule(CopyRetentionRule copyRetentionRule) {
		assertThat(copyRetentionRule.getCopyType()).isEqualTo(CopyType.PRINCIPAL);
		assertThat(copyRetentionRule.getCode()).isEqualTo(CODE);
		assertThat(copyRetentionRule.getTitle()).isEqualTo(TITLE);
		assertThat(copyRetentionRule.getDescription()).isEqualTo(DESCRIPTION);
		assertThat(copyRetentionRule.getContentTypesComment()).isEqualTo(CONTENT_TYPES_COMMENT);
		assertThat(copyRetentionRule.getActiveRetentionPeriod()).isEqualTo(ACTIVE_RETENTION_PERIOD);
		assertThat(copyRetentionRule.getSemiActiveRetentionComment()).isEqualTo(SEMI_ACTIVE_RETENTION_COMMENT);
		assertThat(copyRetentionRule.getSemiActiveRetentionPeriod()).isEqualTo(SEMI_ACTIVE_RETENTION_PERIOD);
		assertThat(copyRetentionRule.getInactiveDisposalComment()).isEqualTo(INACTIVE_DISPOSAL_COMMENT);
		assertThat(copyRetentionRule.getInactiveDisposalType()).isEqualTo(INACTIVE_DISPOSAL_TYPE);
		assertThat(copyRetentionRule.getActiveRetentionPeriod()).isEqualTo(ACTIVE_RETENTION_PERIOD);
		assertThat(copyRetentionRule.isEssential()).isEqualTo(REQUIRED_COPYRULE_FIELD);
		assertThat(copyRetentionRule.getId()).isEqualTo(SET_ID);
		assertThat(copyRetentionRule.getMediumTypeIds()).isNotNull();
		assertThat(copyRetentionRule.getTypeId()).isNull();
		assertThat(copyRetentionRule.isIgnoreActivePeriod()).isFalse();
	}

	private void exportThenImportInAnotherCollection(RecordExportOptions options) {
		File zipFile = new RecordExportServices(getAppLayerFactory()).exportRecords(zeCollection, SDK_STREAM, options);
		ImportDataProvider importDataProvider = null;
		try {
			importDataProvider = XMLImportDataProvider.forZipFile(getModelLayerFactory(), zipFile);

			UserServices userServices = getModelLayerFactory().newUserServices();
			User user = userServices.getUserInCollection("admin", "anotherCollection");
			BulkImportParams importParams = BulkImportParams.STRICT();
			LoggerBulkImportProgressionListener listener = new LoggerBulkImportProgressionListener();
			try {
				new RecordsImportServices(getModelLayerFactory()).bulkImport(importDataProvider, listener, user, importParams);
			} catch (ValidationException e) {

				fail(StringUtils.join(i18n.asListOfMessages(e.getValidationErrors()), "\n"));

			}
		} finally {
			getIOLayerFactory().newIOServices().deleteQuietly(zipFile);
		}
	}
}
