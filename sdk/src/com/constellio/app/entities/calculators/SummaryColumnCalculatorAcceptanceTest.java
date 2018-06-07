package com.constellio.app.entities.calculators;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Locale;

import com.constellio.model.services.records.RecordServicesException;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.constellio.app.modules.rm.RMTestRecords;
import com.constellio.app.modules.rm.model.enums.CopyType;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.app.ui.pages.summarycolumn.SummaryColumnParams;
import com.constellio.app.ui.pages.summarycolumn.SummaryColumnPresenter;
import com.constellio.app.ui.pages.summarycolumn.SummaryColumnView;
import com.constellio.model.services.records.RecordServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.MockedNavigation;
import com.constellio.sdk.tests.setups.Users;

public class SummaryColumnCalculatorAcceptanceTest extends ConstellioTest {

	RMTestRecords records = new RMTestRecords(zeCollection);
	Users users = new Users();
	RMSchemasRecordsServices rmRecordSchemaManager;
	RecordServices recordServices;

	@Mock
	SummaryColumnView view;
	MockedNavigation navigator;
	@Mock
	SessionContext sessionContext;

	SummaryColumnPresenter summaryColumnPresenter;

	@Before
	public void setUp() {
		prepareSystem(withZeCollection().withConstellioRMModule().withRMTest(records).withAllTest(users));
		rmRecordSchemaManager = new RMSchemasRecordsServices(zeCollection, getAppLayerFactory());

		navigator = new MockedNavigation();

		when(view.getConstellioFactories()).thenReturn(getConstellioFactories());
		when(view.getCollection()).thenReturn(zeCollection);
		when(view.getSessionContext()).thenReturn(sessionContext);
		when(sessionContext.getCurrentCollection()).thenReturn(zeCollection);
		when(sessionContext.getCurrentLocale()).thenReturn(Locale.FRENCH);

		summaryColumnPresenter = new SummaryColumnPresenter(view, Folder.DEFAULT_SCHEMA);
		recordServices = getModelLayerFactory().newRecordServices();
	}

	@Test
	public void givenFolderWithMetadataValueAndReferenceSummaryColumnParameterThenSummaryMetadataHaveAValueTest()
			throws Exception {
		SummaryColumnParams summaryColumnParams = new SummaryColumnParams();

		summaryColumnParams.setMetadataVO(findMetadata(Folder.ADMINISTRATIVE_UNIT));
		summaryColumnParams.setPrefix("prefix :");
		summaryColumnParams.setDisplayCondition(SummaryColumnParams.DisplayCondition.ALWAYS);
		summaryColumnParams.setReferenceMetadataDisplay(SummaryColumnParams.ReferenceMetadataDisplay.CODE);

		summaryColumnPresenter.addMetadaForSummary(summaryColumnParams);

		Folder folder = rmRecordSchemaManager.newFolder();

		folder.setAdministrativeUnitEntered(records.unitId_11b);
		folder.setDescription("Ze description");
		folder.setCategoryEntered(records.categoryId_X110);
		folder.setTitle("Ze folder");
		folder.setRetentionRuleEntered(records.ruleId_2);
		folder.setCopyStatusEntered(CopyType.PRINCIPAL);
		folder.setOpenDate(new LocalDate(2014, 11, 4));

		recordServices.add(folder);

		Folder resultFolder = rmRecordSchemaManager.getFolder(folder.getId());

		String summary = resultFolder.get(Folder.SUMMARY);

		assertThat(summary).isEqualTo("prefix : 11B");
	}

	@Test
	public void givenFolderWithMetadataValueAndReferenceSummaryColumnTitleParameterThenSummaryMetadataHaveAValueTest()
			throws Exception {
		SummaryColumnParams summaryColumnParams = new SummaryColumnParams();

		summaryColumnParams.setMetadataVO(findMetadata(Folder.ADMINISTRATIVE_UNIT));
		summaryColumnParams.setPrefix("prefix :");
		summaryColumnParams.setDisplayCondition(SummaryColumnParams.DisplayCondition.ALWAYS);
		summaryColumnParams.setReferenceMetadataDisplay(SummaryColumnParams.ReferenceMetadataDisplay.TITLE);

		summaryColumnPresenter.addMetadaForSummary(summaryColumnParams);

		Folder folder = addFolder();

		Folder resultFolder = rmRecordSchemaManager.getFolder(folder.getId());

		String summary = resultFolder.get(Folder.SUMMARY);

		assertThat(summary).isEqualTo("prefix : Unité 11-B");
	}

	@NotNull
	private Folder addFolder() throws RecordServicesException {
		Folder folder = rmRecordSchemaManager.newFolder();

		folder.setAdministrativeUnitEntered(records.unitId_11b);
		folder.setDescription("Ze description");
		folder.setCategoryEntered(records.categoryId_X110);
		folder.setTitle("Ze folder");
		folder.setRetentionRuleEntered(records.ruleId_2);
		folder.setCopyStatusEntered(CopyType.PRINCIPAL);
		folder.setOpenDate(new LocalDate(2014, 11, 4));

		recordServices.add(folder);
		return folder;
	}


	@Test
	public void givenOneFolderWithValueAndDynamicDependancyThenSummaryMetadataHaveAValueTest()
			throws Exception {
		SummaryColumnParams summaryColumnParams = new SummaryColumnParams();

		summaryColumnParams.setMetadataVO(findMetadata(Folder.TITLE));
		summaryColumnParams.setPrefix("prefix :");
		summaryColumnParams.setDisplayCondition(SummaryColumnParams.DisplayCondition.ALWAYS);
		summaryColumnParams.setReferenceMetadataDisplay(SummaryColumnParams.ReferenceMetadataDisplay.CODE);

		summaryColumnPresenter.addMetadaForSummary(summaryColumnParams);

		Folder folder = rmRecordSchemaManager.newFolder();

		folder.setAdministrativeUnitEntered(records.unitId_11b);
		folder.setDescription("Ze description");
		folder.setCategoryEntered(records.categoryId_X110);
		folder.setTitle("Ze folder");
		folder.setRetentionRuleEntered(records.ruleId_2);
		folder.setCopyStatusEntered(CopyType.PRINCIPAL);
		folder.setOpenDate(new LocalDate(2014, 11, 4));

		recordServices.add(folder);

		Folder resultFolder = rmRecordSchemaManager.getFolder(folder.getId());

		String summary = resultFolder.get(Folder.SUMMARY);

		// Les paramèetres sont setter.
		assertThat(summary).isEqualTo("prefix : Ze folder");
	}

	@Test
	public void givenOneFolderWithValueAndDynamicDependancyMultiValueThenSummaryMetadataHaveAValueTest()
			throws Exception {
		SummaryColumnParams summaryColumnParams = new SummaryColumnParams();

		summaryColumnParams.setMetadataVO(findMetadata(Folder.KEYWORDS));
		summaryColumnParams.setPrefix("prefix :");
		summaryColumnParams.setDisplayCondition(SummaryColumnParams.DisplayCondition.ALWAYS);
		summaryColumnParams.setReferenceMetadataDisplay(SummaryColumnParams.ReferenceMetadataDisplay.CODE);

		summaryColumnPresenter.addMetadaForSummary(summaryColumnParams);

		Folder folder = rmRecordSchemaManager.newFolder();

		folder.setAdministrativeUnitEntered(records.unitId_11b);
		folder.setDescription("Ze description");
		folder.setCategoryEntered(records.categoryId_X110);
		folder.setTitle("Ze folder");
		folder.setRetentionRuleEntered(records.ruleId_2);
		folder.setCopyStatusEntered(CopyType.PRINCIPAL);

		folder.setKeywords(asList("keyword1, keyword2, keyword3"));
		folder.setOpenDate(new LocalDate(2014, 11, 4));

		recordServices.add(folder);

		Folder resultFolder = rmRecordSchemaManager.getFolder(folder.getId());

		String summary = resultFolder.get(Folder.SUMMARY);

		assertThat(summary).isEqualTo("prefix : keyword1, keyword2, keyword3");

	}

	@Test
	public void givenOneFolderWithValueDynamicDependancyMultiValueAndDynamicDepencySingleThenSummaryMetadataHaveAValueTest()
			throws Exception {
		SummaryColumnParams summaryColumnParams = new SummaryColumnParams();

		summaryColumnParams.setMetadataVO(findMetadata(Folder.KEYWORDS));
		summaryColumnParams.setPrefix("prefix :");
		summaryColumnParams.setDisplayCondition(SummaryColumnParams.DisplayCondition.ALWAYS);
		summaryColumnParams.setReferenceMetadataDisplay(SummaryColumnParams.ReferenceMetadataDisplay.CODE);

		summaryColumnPresenter.addMetadaForSummary(summaryColumnParams);

		Folder folder = rmRecordSchemaManager.newFolder();

		folder.setAdministrativeUnitEntered(records.unitId_11b);
		folder.setDescription("Ze description");
		folder.setCategoryEntered(records.categoryId_X110);
		folder.setTitle("Ze folder");
		folder.setRetentionRuleEntered(records.ruleId_2);
		folder.setCopyStatusEntered(CopyType.PRINCIPAL);

		folder.setKeywords(asList("keyword1, keyword2, keyword3"));
		folder.setOpenDate(new LocalDate(2014, 11, 4));

		recordServices.add(folder);

		Folder resultFolder = rmRecordSchemaManager.getFolder(folder.getId());

		String summary = resultFolder.get(Folder.SUMMARY);

		assertThat(summary).isEqualTo("prefix : keyword1, keyword2, keyword3");

	}

	private MetadataVO findMetadata(String localCode) {
		for (MetadataVO metadataVO : summaryColumnPresenter.getMetadatas()) {
			if (metadataVO.getLocalCode().equalsIgnoreCase(localCode)) {
				return metadataVO;
			}
		}

		return null;
	}
}

