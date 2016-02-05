package com.constellio.app.modules.rm.model;

import static com.constellio.app.modules.rm.model.enums.DecommissioningDateBasedOn.CLOSE_DATE;
import static com.constellio.app.modules.rm.model.enums.FolderStatus.ACTIVE;
import static com.constellio.app.modules.rm.model.enums.FolderStatus.INACTIVE_DEPOSITED;
import static com.constellio.app.modules.rm.model.enums.FolderStatus.INACTIVE_DESTROYED;
import static com.constellio.app.modules.rm.model.enums.FolderStatus.SEMI_ACTIVE;
import static com.constellio.model.entities.schemas.MetadataValueType.STRING;
import static com.constellio.sdk.tests.TestUtils.assertThatRecord;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.modules.rm.RMTestRecords;
import com.constellio.app.modules.rm.constants.RMTaxonomies;
import com.constellio.app.modules.rm.model.enums.CopyType;
import com.constellio.app.modules.rm.model.enums.FolderStatus;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.Category;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.modules.rm.wrappers.RetentionRule;
import com.constellio.app.modules.rm.wrappers.structures.Comment;
import com.constellio.app.modules.rm.wrappers.type.FolderType;
import com.constellio.model.entities.calculators.CalculatorParameters;
import com.constellio.model.entities.calculators.MetadataValueCalculator;
import com.constellio.model.entities.calculators.dependencies.Dependency;
import com.constellio.model.entities.calculators.dependencies.ReferenceDependency;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.extensions.behaviors.RecordExtension;
import com.constellio.model.extensions.events.records.RecordInCreationBeforeSaveEvent;
import com.constellio.model.extensions.events.records.RecordInModificationBeforeSaveEvent;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.schemas.MetadataSchemaTypesAlteration;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.TestUtils;

public class FolderAcceptanceTest extends ConstellioTest {

	LocalDate november4_2009 = new LocalDate(2009, 11, 4);
	LocalDate november4_2010 = new LocalDate(2010, 11, 4);

	LocalDate december12_2009 = new LocalDate(2009, 12, 12);
	LocalDate january12_2010 = new LocalDate(2010, 1, 12);
	LocalDate february16_2012 = new LocalDate(2012, 1, 12);

	LocalDate january1_2015 = new LocalDate(2015, 1, 1);
	LocalDate february2_2015 = new LocalDate(2015, 2, 1);
	LocalDate february11_2015 = new LocalDate(2015, 2, 11);
	LocalDate march31_2015 = new LocalDate(2015, 3, 31);
	LocalDate march31_2016 = new LocalDate(2016, 3, 31);
	LocalDate march31_2017 = new LocalDate(2017, 3, 31);
	LocalDate march31_2018 = new LocalDate(2018, 3, 31);
	LocalDate march31_2019 = new LocalDate(2019, 3, 31);
	LocalDate march31_2020 = new LocalDate(2020, 3, 31);
	LocalDate march31_2021 = new LocalDate(2021, 3, 31);
	LocalDate march31_2022 = new LocalDate(2022, 3, 31);
	LocalDate march31_2023 = new LocalDate(2023, 3, 31);
	LocalDate march31_2024 = new LocalDate(2024, 3, 31);
	LocalDate march31_2025 = new LocalDate(2025, 3, 31);

	LocalDate april1_2004 = new LocalDate(2004, 4, 1);
	LocalDate april1_2014 = new LocalDate(2014, 4, 1);
	LocalDate march31_2005 = new LocalDate(2005, 3, 31);
	LocalDate march31_2026 = new LocalDate(2026, 3, 31);
	LocalDate march31_2029 = new LocalDate(2029, 3, 31);
	LocalDate march31_2035 = new LocalDate(2035, 3, 31);
	LocalDate march31_2036 = new LocalDate(2036, 3, 31);
	LocalDate march31_2046 = new LocalDate(2046, 3, 31);
	LocalDate march31_2056 = new LocalDate(2056, 3, 31);
	LocalDate march31_2061 = new LocalDate(2061, 3, 31);
	LocalDate march31_2065 = new LocalDate(2065, 3, 31);
	LocalDate march31_2066 = new LocalDate(2066, 3, 31);

	RMSchemasRecordsServices rm;
	RMTestRecords records = new RMTestRecords(zeCollection);
	RecordServices recordServices;

	Transaction transaction = new Transaction();

	String zeRule = "zeRule";
	String zeCategory;
	String aPrincipalAdminUnit;
	String anotherPrincipalAdminUnit;
	String aSecondaryAdminUnit;

	String PA;
	String MV;
	String MD;

	CopyType noEnteredCopyType = null;

	@Before
	public void setUp()
			throws Exception {
		prepareSystem(
				withZeCollection().withConstellioRMModule().withRMTest(records)
		);
		assertThat(getModelLayerFactory().getTaxonomiesManager().getPrincipalTaxonomy(zeCollection).getCode())
				.isEqualTo(RMTaxonomies.ADMINISTRATIVE_UNITS);

		rm = new RMSchemasRecordsServices(zeCollection, getModelLayerFactory());
		recordServices = getModelLayerFactory().newRecordServices();

		zeCategory = records.categoryId_ZE42;
		aPrincipalAdminUnit = records.unitId_10a;
		anotherPrincipalAdminUnit = records.unitId_20;
		aSecondaryAdminUnit = records.unitId_30;
		PA = records.PA;
		MV = records.MV;
		MD = records.MD;
	}

	@Test
	public void whenSaveFolderThenMetadataValuesSaved()
			throws Exception {

		Comment comment1 = new Comment("Ze message", records.getDakota_managerInA_userInB(), new LocalDateTime().minusWeeks(4));
		Comment comment2 = new Comment("An other message", records.getEdouard_managerInB_userInC(),
				new LocalDateTime().minusWeeks(1));

		Folder folder = rm.newFolder();
		folder.setAdministrativeUnitEntered(records.unitId_11b);
		folder.setDescription("Ze description");
		folder.setCategoryEntered(records.categoryId_X110);
		folder.setRetentionRuleEntered(records.ruleId_2);
		folder.setCopyStatusEntered(CopyType.PRINCIPAL);
		folder.setTitle("Ze folder");
		folder.setMediumTypes(Arrays.asList(PA, MV));
		folder.setUniformSubdivisionEntered(records.subdivId_2);
		folder.setOpenDate(november4_2009);
		folder.setCloseDateEntered(december12_2009);
		folder.setComments(asList(comment1, comment2));

		folder = saveAndLoad(folder);

		assertThat(folder.getAdministrativeUnitEntered()).isEqualTo(records.unitId_11b);
		assertThat(folder.getDescription()).isEqualTo("Ze description");
		assertThat(folder.getUniformSubdivisionEntered()).isEqualTo(records.subdivId_2);
		assertThat(folder.getCategoryEntered()).isEqualTo(records.categoryId_X110);
		assertThat(folder.getCategoryCode()).isEqualTo(records.getCategory_X110().getCode());
		assertThat(folder.getRetentionRuleEntered()).isEqualTo(records.ruleId_2);
		assertThat(folder.getActiveRetentionCode()).isNull();
		assertThat(folder.getSemiActiveRetentionCode()).isNull();
		assertThat(folder.getRetentionRuleEntered()).isEqualTo(records.ruleId_2);

		assertThat(folder.getCopyStatus()).isEqualTo(CopyType.PRINCIPAL);
		assertThat(folder.getTitle()).isEqualTo("Ze folder");
		assertThat(folder.getMediumTypes()).isEqualTo(Arrays.asList(PA, MV));
		assertThat(folder.getOpenDate()).isEqualTo(november4_2009);
		assertThat(folder.getComments()).isEqualTo(asList(comment1, comment2));
		assertThat(folder.hasAnalogicalMedium()).isTrue();
		assertThat(folder.hasElectronicMedium()).isFalse();
		assertThat(folder.getCloseDateEntered()).isEqualTo(december12_2009);

	}

	@Test
	public void givenFolderWithFormCreatedModifiedByOnInfosThenPersisted()
			throws RecordServicesException {

		LocalDateTime dateTime1 = new LocalDateTime().plusDays(1);
		LocalDateTime dateTime2 = dateTime1.plusDays(2);

		Folder folder = saveAndLoad(folderWithSingleCopyRule(principal("888-0-D", PA))
				.setOpenDate(november4_2009)
				.setCloseDateEntered(december12_2009)
				.setFormCreatedBy(records.getBob_userInAC().getId())
				.setFormCreatedOn(dateTime1)
				.setFormModifiedBy(records.getCharles_userInA().getId())
				.setFormModifiedOn(dateTime2));

		assertThat(folder.getFormCreatedBy()).isEqualTo(records.getBob_userInAC().getId());
		assertThat(folder.getFormCreatedOn()).isEqualTo(dateTime1);
		assertThat(folder.getFormModifiedBy()).isEqualTo(records.getCharles_userInA().getId());
		assertThat(folder.getFormModifiedOn()).isEqualTo(dateTime2);
	}

	@Test
	public void givenFolderWithoutTransferDispoalAndDestructionDatesThenActive()
			throws RecordServicesException {
		Folder folder = saveAndLoad(folderWithSingleCopyRule(principal("888-0-D", PA))
				.setOpenDate(november4_2009)
				.setCloseDateEntered(december12_2009));

		assertThat(folder.hasAnalogicalMedium()).isTrue();
		assertThat(folder.hasElectronicMedium()).isTrue();
		assertThat(folder.getOpenDate()).isEqualTo(november4_2009);
		assertThat(folder.getCloseDateEntered()).isEqualTo(december12_2009);
		assertThat(folder.getArchivisticStatus()).isEqualTo(ACTIVE);
		assertThat(folder.getActualTransferDate()).isNull();
		assertThat(folder.getActualDepositDate()).isNull();
		assertThat(folder.getActualDestructionDate()).isNull();
	}

	@Test
	public void givenFolderWithTransferDateAndWithoutDestructionOrDepositThenSemiActive()
			throws Exception {

		Folder folder = saveAndLoad(folderWithSingleCopyRule(principal("888-0-D", PA))
				.setOpenDate(november4_2009)
				.setCloseDateEntered(december12_2009)
				.setActualTransferDate(january12_2010));

		assertThat(folder.getOpenDate()).isEqualTo(november4_2009);
		assertThat(folder.getCloseDateEntered()).isEqualTo(december12_2009);
		assertThat(folder.getArchivisticStatus()).isEqualTo(SEMI_ACTIVE);
		assertThat(folder.getActualTransferDate()).isEqualTo(january12_2010);
		assertThat(folder.getActualDepositDate()).isNull();
		assertThat(folder.getActualDestructionDate()).isNull();
	}

	@Test
	public void givenFolderWithDepositDateThenInactive()
			throws Exception {

		Folder folder = saveAndLoad(folderWithSingleCopyRule(principal("888-0-D", PA))
				.setOpenDate(november4_2009)
				.setCloseDateEntered(december12_2009)
				.setActualTransferDate(january12_2010)
				.setActualDepositDate(february16_2012));

		assertThat(folder.getOpenDate()).isEqualTo(november4_2009);
		assertThat(folder.getCloseDateEntered()).isEqualTo(december12_2009);
		assertThat(folder.getArchivisticStatus()).isEqualTo(INACTIVE_DEPOSITED);
		assertThat(folder.getActualTransferDate()).isEqualTo(january12_2010);
		assertThat(folder.getActualDepositDate()).isEqualTo(february16_2012);
		assertThat(folder.getActualDestructionDate()).isNull();
	}

	@Test
	public void givenFolderWithDestructionDateThenInactive()
			throws Exception {

		Folder folder = saveAndLoad(folderWithSingleCopyRule(principal("888-0-D", PA))
				.setOpenDate(november4_2009)
				.setCloseDateEntered(december12_2009)
				.setActualTransferDate(january12_2010)
				.setActualDestructionDate(february16_2012));

		assertThat(folder.getOpenDate()).isEqualTo(november4_2009);
		assertThat(folder.getCloseDateEntered()).isEqualTo(december12_2009);
		assertThat(folder.getArchivisticStatus()).isEqualTo(INACTIVE_DESTROYED);
		assertThat(folder.getActualTransferDate()).isEqualTo(january12_2010);
		assertThat(folder.getActualDepositDate()).isNull();
		assertThat(folder.getActualDestructionDate()).isEqualTo(february16_2012);
	}

	@Test
	//Tested on IntelliGID 4!
	public void givenPrincipalFolderWithTwoMediumTypesAndYearEndInInsufficientPeriodThenHasValidCalculedDates()
			throws Exception {

		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE, true);
		givenConfig(RMConfigs.DECOMMISSIONING_DATE_BASED_ON, CLOSE_DATE);
		givenConfig(RMConfigs.YEAR_END_DATE, "03/31");
		givenConfig(RMConfigs.REQUIRED_DAYS_BEFORE_YEAR_END_FOR_NOT_ADDING_A_YEAR, 90);
		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_FIXED_RULE, 0);
		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_RULE, 0);
		givenConfig(RMConfigs.CALCULATED_SEMIACTIVE_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_PERIOD, 0);
		givenConfig(RMConfigs.CALCULATED_INACTIVE_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_PERIOD, 0);

		givenRuleWithResponsibleAdminUnitsFlagAndCopyRules(principal("2-2-C", PA), principal("5-5-D", MD),
				secondary("1-0-D", MD, PA));

		Folder folder = saveAndLoad(principalFolderWithZeRule()
				.setOpenDate(february2_2015)
				.setMediumTypes(MD, PA));

		assertThat(folder.hasAnalogicalMedium()).isTrue();
		assertThat(folder.hasElectronicMedium()).isTrue();
		assertThat(folder.getArchivisticStatus()).isEqualTo(FolderStatus.ACTIVE);
		assertThat(folder.getOpenDate()).isEqualTo(february2_2015);
		assertThat(folder.getCloseDate()).isEqualTo(march31_2016);
		assertThat(folder.getDecommissioningDate()).isEqualTo(march31_2016);
		assertThat(folder.getActualTransferDate()).isNull();
		assertThat(folder.getActualDepositDate()).isNull();
		assertThat(folder.getActualDestructionDate()).isNull();
		assertThat(folder.getApplicableCopyRules()).containsExactly(principal("2-2-C", PA), principal("5-5-D", MD));
		assertThat(folder.getMainCopyRule()).isEqualTo(principal("2-2-C", PA));
		assertThat(folder.getCopyRulesExpectedTransferDates()).containsExactly(march31_2018, march31_2021);
		assertThat(folder.getCopyRulesExpectedDestructionDates()).containsExactly(null, march31_2026);
		assertThat(folder.getCopyRulesExpectedDepositDates()).containsExactly(march31_2020, null);
		assertThat(folder.getExpectedTransferDate()).isEqualTo(march31_2018);
		//TODO Valider si la date de destruction ne devrait pas plutôt être non-nulle
		assertThat(folder.getExpectedDestructionDate()).isEqualTo(null);
		assertThat(folder.getExpectedDepositDate()).isEqualTo(march31_2020);

	}

	@Test
	public void givenCustomFolderWhenModifyTaxonomyWithCopiedMetadatasThenReindexed()
			throws Exception {

		RecordServices recordServices = getModelLayerFactory().newRecordServices();
		getModelLayerFactory().getMetadataSchemasManager().modify(zeCollection, new MetadataSchemaTypesAlteration() {
			@Override
			public void alter(MetadataSchemaTypesBuilder types) {
				types.getSchemaType(Folder.SCHEMA_TYPE).createCustomSchema("customFolder");
				types.getSchema(Folder.DEFAULT_SCHEMA).create("zeCalculatedMetadata").setType(STRING)
						.defineDataEntry().asCalculated(ZeCategoryCodeCalculator.class);
			}
		});

		FolderType folderType = rm.newFolderType().setCode("ze type").setTitle("ze type").setLinkedSchema("customFolder");
		recordServices.add(folderType);

		Folder folder = rm.newFolderWithType(folderType)
				.setTitle("Ze custom folder")
				.setOpenDate(new LocalDate())
				.setAdministrativeUnitEntered(records.unitId_10a)
				.setCategoryEntered(records.categoryId_X13)
				.setRetentionRuleEntered(records.ruleId_1);
		recordServices.add(folder);

		assertThatRecord(rm.getFolder(folder.getId()))
				.hasMetadata(Folder.CATEGORY_CODE, "X13")
				.hasMetadata("zeCalculatedMetadata", "Ze ultimate X13");

		recordServices.update(rm.getCategoryWithCode("X13").setCode("X-13"));
		waitForBatchProcess();

		assertThatRecord(rm.getFolder(folder.getId()))
				.hasMetadata(Folder.CATEGORY_CODE, "X-13")
				.hasMetadata("zeCalculatedMetadata", "Ze ultimate X-13");

	}

	public static class ZeCategoryCodeCalculator implements MetadataValueCalculator<String> {

		ReferenceDependency<String> codeParam = ReferenceDependency.toAString(Folder.CATEGORY, Category.CODE);

		@Override
		public String calculate(CalculatorParameters parameters) {
			String code = parameters.get(codeParam);
			return "Ze ultimate " + code;
		}

		@Override
		public String getDefaultValue() {
			return null;
		}

		@Override
		public MetadataValueType getReturnType() {
			return STRING;
		}

		@Override
		public boolean isMultiValue() {
			return false;
		}

		@Override
		public List<? extends Dependency> getDependencies() {
			return TestUtils.asList(codeParam);
		}
	}

	@Test
	//Tested on IntelliGID 4!
	public void givenPrincipalFolderWithTwoMediumTypesAndYearEndInSufficientPeriodThenHasValidCalculedDates()
			throws Exception {

		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE, true);
		givenConfig(RMConfigs.DECOMMISSIONING_DATE_BASED_ON, CLOSE_DATE);
		givenConfig(RMConfigs.YEAR_END_DATE, "03/31");
		givenConfig(RMConfigs.REQUIRED_DAYS_BEFORE_YEAR_END_FOR_NOT_ADDING_A_YEAR, 60);
		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_FIXED_RULE, 0);
		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_RULE, 0);
		givenConfig(RMConfigs.CALCULATED_SEMIACTIVE_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_PERIOD, 0);
		givenConfig(RMConfigs.CALCULATED_INACTIVE_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_PERIOD, 0);

		givenRuleWithResponsibleAdminUnitsFlagAndCopyRules(principal("5-5-D", MD), principal("2-2-C", PA),
				secondary("1-0-D", MD, PA));

		Folder folder = saveAndLoad(principalFolderWithZeRule()
				.setOpenDate(january1_2015)
				.setMediumTypes(MD, PA));

		assertThat(folder.getArchivisticStatus()).isEqualTo(FolderStatus.ACTIVE);
		assertThat(folder.getOpenDate()).isEqualTo(january1_2015);
		assertThat(folder.getCloseDate()).isEqualTo(march31_2015);
		assertThat(folder.getActualTransferDate()).isNull();
		assertThat(folder.getActualDepositDate()).isNull();
		assertThat(folder.getActualDestructionDate()).isNull();
		assertThat(folder.getApplicableCopyRules()).containsExactly(principal("5-5-D", MD), principal("2-2-C", PA));
		assertThat(folder.getMainCopyRule()).isEqualTo(principal("2-2-C", PA));
		assertThat(folder.getCopyRulesExpectedTransferDates()).containsExactly(march31_2020, march31_2017);
		assertThat(folder.getCopyRulesExpectedDestructionDates()).containsExactly(march31_2025, null);
		assertThat(folder.getCopyRulesExpectedDepositDates()).containsExactly(null, march31_2019);

		assertThat(folder.getExpectedTransferDate()).isEqualTo(march31_2017);
		assertThat(folder.getExpectedDestructionDate()).isNull();
		assertThat(folder.getExpectedDepositDate()).isEqualTo(march31_2019);

	}

	@Test
	//Tested on IntelliGID 4!
	public void givenSemiActiveFoldersThenHasValidCalculedDates()
			throws Exception {

		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE, true);
		givenConfig(RMConfigs.DECOMMISSIONING_DATE_BASED_ON, CLOSE_DATE);
		givenConfig(RMConfigs.YEAR_END_DATE, "03/31");
		givenConfig(RMConfigs.REQUIRED_DAYS_BEFORE_YEAR_END_FOR_NOT_ADDING_A_YEAR, 90);
		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_FIXED_RULE, 0);
		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_RULE, 0);
		givenConfig(RMConfigs.CALCULATED_SEMIACTIVE_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_PERIOD, 0);
		givenConfig(RMConfigs.CALCULATED_INACTIVE_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_PERIOD, 0);

		givenRuleWithResponsibleAdminUnitsFlagAndCopyRules(principal("888-5-T", PA), principal("888-5-D", MD),
				secondary("888-0-D", PA));

		Folder folder = saveAndLoad(principalFolderWithZeRule()
				.setOpenDate(april1_2004)
				.setActualTransferDate(april1_2014)
				.setMediumTypes(PA, MD));

		assertThat(folder.getArchivisticStatus()).isEqualTo(FolderStatus.SEMI_ACTIVE);
		assertThat(folder.getOpenDate()).isEqualTo(april1_2004);
		assertThat(folder.getCloseDate()).isEqualTo(march31_2005);
		assertThat(folder.getActualTransferDate()).isEqualTo(april1_2014);
		assertThat(folder.getActualDepositDate()).isNull();
		assertThat(folder.getActualDestructionDate()).isNull();
		assertThat(folder.getApplicableCopyRules()).containsExactly(principal("888-5-T", PA), principal("888-5-D", MD));
		assertThat(folder.getMainCopyRule()).isEqualTo(principal("888-5-T", PA));
		assertThat(folder.getActiveRetentionCode()).isEqualTo("888");
		assertThat(folder.getSemiActiveRetentionCode()).isNull();
		assertThat(folder.getCopyRulesExpectedTransferDates()).isEqualTo(asList(new LocalDate[] { null, null }));
		assertThat(folder.getCopyRulesExpectedDestructionDates()).containsExactly(march31_2020, march31_2020);
		assertThat(folder.getCopyRulesExpectedDepositDates()).containsExactly(march31_2020, null);

		assertThat(folder.getExpectedTransferDate()).isNull();
		assertThat(folder.getExpectedDestructionDate()).isEqualTo(march31_2020);
		assertThat(folder.getExpectedDepositDate()).isEqualTo(march31_2020);

	}

	@Test
	//Tested on IntelliGID 4!
	public void givenActiveFoldersWithOpenPeriodsThenValidCalculatedDates()
			throws Exception {

		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE, true);
		givenConfig(RMConfigs.DECOMMISSIONING_DATE_BASED_ON, CLOSE_DATE);
		givenConfig(RMConfigs.YEAR_END_DATE, "03/31");
		givenConfig(RMConfigs.REQUIRED_DAYS_BEFORE_YEAR_END_FOR_NOT_ADDING_A_YEAR, 90);
		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_FIXED_RULE, 0);
		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_RULE, 0);
		givenConfig(RMConfigs.CALCULATED_SEMIACTIVE_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_PERIOD, 0);
		givenConfig(RMConfigs.CALCULATED_INACTIVE_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_PERIOD, 0);
		givenRuleWithResponsibleAdminUnitsFlagAndCopyRules(principal("888-5-T", PA), principal("888-5-D", MD),
				secondary("999-0-D", PA));

		Folder folder = saveAndLoad(principalFolderWithZeRule()
				.setOpenDate(february2_2015)
				.setMediumTypes(PA, MD));

		assertThat(folder.getArchivisticStatus()).isEqualTo(FolderStatus.ACTIVE);
		assertThat(folder.getOpenDate()).isEqualTo(february2_2015);
		assertThat(folder.getCloseDate()).isEqualTo(march31_2016);
		assertThat(folder.getCopyStatus()).isEqualTo(CopyType.PRINCIPAL);
		assertThat(folder.getActualTransferDate()).isNull();
		assertThat(folder.getActualDepositDate()).isNull();
		assertThat(folder.getActualDestructionDate()).isNull();
		assertThat(folder.getApplicableCopyRules()).containsExactly(principal("888-5-T", PA), principal("888-5-D", MD));
		assertThat(folder.getMainCopyRule()).isEqualTo(principal("888-5-T", PA));
		assertThat(folder.getCopyRulesExpectedTransferDates()).containsExactly(march31_2016, march31_2016);
		assertThat(folder.getCopyRulesExpectedDestructionDates()).containsExactly(march31_2021, march31_2021);
		assertThat(folder.getCopyRulesExpectedDepositDates()).containsExactly(march31_2021, null);

		assertThat(folder.getExpectedTransferDate()).isEqualTo(march31_2016);
		assertThat(folder.getExpectedDestructionDate()).isEqualTo(march31_2021);
		assertThat(folder.getExpectedDepositDate()).isEqualTo(march31_2021);

	}

	@Test
	//Tested on IntelliGID 4!
	public void givenActiveFoldersWithOpenPeriodsAndDecommissioningDelaysThenValidCalculatedDates()
			throws Exception {

		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE, true);
		givenConfig(RMConfigs.DECOMMISSIONING_DATE_BASED_ON, CLOSE_DATE);
		givenConfig(RMConfigs.YEAR_END_DATE, "03/31");
		givenConfig(RMConfigs.REQUIRED_DAYS_BEFORE_YEAR_END_FOR_NOT_ADDING_A_YEAR, 90);
		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_RULE, 10);
		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_FIXED_RULE, 20);
		givenConfig(RMConfigs.CALCULATED_SEMIACTIVE_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_PERIOD, 30);
		givenConfig(RMConfigs.CALCULATED_INACTIVE_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_PERIOD, 40);
		givenRuleWithResponsibleAdminUnitsFlagAndCopyRules(principal("888-5-T", PA), principal("888-5-D", MD),
				secondary("999-0-D", PA));

		Folder folder = saveAndLoad(principalFolderWithZeRule()
				.setOpenDate(february2_2015)
				.setMediumTypes(MD, PA));

		assertThat(folder.getArchivisticStatus()).isEqualTo(FolderStatus.ACTIVE);
		assertThat(folder.getOpenDate()).isEqualTo(february2_2015);
		assertThat(folder.getCloseDate()).isEqualTo(march31_2026);
		assertThat(folder.getActualTransferDate()).isNull();
		assertThat(folder.getActualDepositDate()).isNull();
		assertThat(folder.getActualDestructionDate()).isNull();
		assertThat(folder.getApplicableCopyRules()).containsExactly(principal("888-5-T", PA), principal("888-5-D", MD));
		assertThat(folder.getMainCopyRule()).isEqualTo(principal("888-5-T", PA));
		assertThat(folder.getCopyRulesExpectedTransferDates()).containsExactly(march31_2056, march31_2056);
		assertThat(folder.getCopyRulesExpectedDestructionDates()).containsExactly(march31_2061, march31_2061);
		assertThat(folder.getCopyRulesExpectedDepositDates()).containsExactly(march31_2061, null);

		assertThat(folder.getExpectedTransferDate()).isEqualTo(march31_2056);
		assertThat(folder.getExpectedDestructionDate()).isEqualTo(march31_2061);
		assertThat(folder.getExpectedDepositDate()).isEqualTo(march31_2061);

	}

	@Test
	//Tested on IntelliGID 4!
	public void givenActiveSecondaryFoldersWithOpenPeriodsAndDecommissioningDelaysThenValidCalculatedDates()
			throws Exception {

		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE, true);
		givenConfig(RMConfigs.DECOMMISSIONING_DATE_BASED_ON, CLOSE_DATE);
		givenConfig(RMConfigs.YEAR_END_DATE, "03/31");
		givenConfig(RMConfigs.REQUIRED_DAYS_BEFORE_YEAR_END_FOR_NOT_ADDING_A_YEAR, 30);
		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_RULE, 20);
		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_FIXED_RULE, 10);
		givenConfig(RMConfigs.CALCULATED_SEMIACTIVE_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_PERIOD, 30);
		givenConfig(RMConfigs.CALCULATED_INACTIVE_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_PERIOD, 40);
		givenRuleWithResponsibleAdminUnitsFlagAndCopyRules(principal("3-3-T", PA), principal("888-888-D", MD),
				secondary("999-0-D", PA));

		Folder folder = saveAndLoad(secondaryFolderWithZeRule()
				.setOpenDate(february2_2015)
				.setMediumTypes(MD, PA));

		assertThat(folder.getArchivisticStatus()).isEqualTo(FolderStatus.ACTIVE);
		assertThat(folder.getOpenDate()).isEqualTo(february2_2015);
		assertThat(folder.getCloseDate()).isEqualTo(march31_2035);
		assertThat(folder.getActualTransferDate()).isNull();
		assertThat(folder.getActualDepositDate()).isNull();
		assertThat(folder.getActualDestructionDate()).isNull();
		assertThat(folder.getApplicableCopyRules()).containsExactly(secondary("999-0-D", PA));
		assertThat(folder.getMainCopyRule()).isEqualTo(secondary("999-0-D", PA));
		assertThat(folder.getActiveRetentionCode()).isEqualTo("999");
		assertThat(folder.getSemiActiveRetentionCode()).isNull();
		assertThat(folder.getCopyRulesExpectedTransferDates()).containsExactly(march31_2065);
		assertThat(folder.getCopyRulesExpectedDestructionDates()).containsExactly(march31_2065);
		assertThat(folder.getCopyRulesExpectedDepositDates()).isEqualTo(asList(new LocalDate[] { null }));

		assertThat(folder.getExpectedTransferDate()).isEqualTo(march31_2065);
		assertThat(folder.getExpectedDestructionDate()).isEqualTo(march31_2065);
		assertThat(folder.getExpectedDepositDate()).isNull();

	}

	@Test
	//Tested on IntelliGID 4!
	public void givenActiveFoldersWithOpenPeriodsAndDisabledDecommissioningCalculationThenValidCalculatedDates()
			throws Exception {

		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE, true);
		givenConfig(RMConfigs.DECOMMISSIONING_DATE_BASED_ON, CLOSE_DATE);
		givenConfig(RMConfigs.YEAR_END_DATE, "03/31");
		givenConfig(RMConfigs.REQUIRED_DAYS_BEFORE_YEAR_END_FOR_NOT_ADDING_A_YEAR, 90);
		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_FIXED_RULE, -1);
		givenConfig(RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_RULE, -1);
		givenConfig(RMConfigs.CALCULATED_SEMIACTIVE_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_PERIOD, -1);
		givenConfig(RMConfigs.CALCULATED_INACTIVE_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_PERIOD, -1);
		givenRuleWithResponsibleAdminUnitsFlagAndCopyRules(principal("888-5-T", PA), principal("888-5-D", MD),
				secondary("999-0-D", PA));

		Folder folder = saveAndLoad(principalFolderWithZeRule()
				.setOpenDate(february2_2015)
				.setCloseDateEntered(february11_2015)
				.setMediumTypes(MD, PA));

		assertThat(folder.getArchivisticStatus()).isEqualTo(FolderStatus.ACTIVE);
		assertThat(folder.getOpenDate()).isEqualTo(february2_2015);
		assertThat(folder.getCloseDate()).isEqualTo(february11_2015);
		assertThat(folder.getDecommissioningDate()).isEqualTo(march31_2016);
		assertThat(folder.getActualTransferDate()).isNull();
		assertThat(folder.getActualDepositDate()).isNull();
		assertThat(folder.getActualDestructionDate()).isNull();
		assertThat(folder.getApplicableCopyRules()).containsExactly(principal("888-5-T", PA), principal("888-5-D", MD));
		assertThat(folder.getMainCopyRule()).isEqualTo(principal("888-5-T", PA));
		assertThat(folder.getCopyRulesExpectedTransferDates()).isEqualTo(asList(new LocalDate[] { null, null }));
		assertThat(folder.getCopyRulesExpectedDestructionDates()).isEqualTo(asList(new LocalDate[] { null, null }));
		assertThat(folder.getCopyRulesExpectedDepositDates()).isEqualTo(asList(new LocalDate[] { null, null }));

		assertThat(folder.getExpectedTransferDate()).isNull();
		assertThat(folder.getExpectedDestructionDate()).isNull();
		assertThat(folder.getExpectedDepositDate()).isNull();

	}

	@Test
	public void givenFolderCreatedWithRuleWithoutPrincipalCopyTypeThenSecondaryEvenIfAdministrativeUnitIsInList()
			throws Exception {
		givenConfig(RMConfigs.COPY_RULE_PRINCIPAL_REQUIRED, false);
		givenRuleHasNoPrincipalCopyType(records.ruleId_1);

		Folder folder = rm.newFolder();
		folder.setAdministrativeUnitEntered(records.unitId_10a);
		folder.setCategoryEntered(records.categoryId_X13);
		folder.setTitle("Ze folder");
		folder.setRetentionRuleEntered(records.ruleId_1);
		folder.setOpenDate(february2_2015);
		folder.setCloseDateEntered(february11_2015);
		folder.setMediumTypes(MD, PA);
		getModelLayerFactory().newRecordServices().add(folder);

		assertThat(folder.getCopyStatus()).isEqualTo(CopyType.SECONDARY);

	}

	@Test
	public void givenFolderCreatedWithRuleWithoutPrincipalCopyTypeThenSecondaryEvenIfPrincipalEntered()
			throws Exception {
		givenConfig(RMConfigs.COPY_RULE_PRINCIPAL_REQUIRED, false);
		givenRuleHasNoPrincipalCopyType(records.ruleId_2);

		Folder folder = rm.newFolder();
		folder.setAdministrativeUnitEntered(records.unitId_10a);
		folder.setCategoryEntered(records.categoryId_X13);
		folder.setTitle("Ze folder");
		folder.setRetentionRuleEntered(records.ruleId_2);
		folder.setCopyStatusEntered(CopyType.PRINCIPAL);
		folder.setOpenDate(february2_2015);
		folder.setCloseDateEntered(february11_2015);
		folder.setMediumTypes(MD, PA);
		getModelLayerFactory().newRecordServices().add(folder);

		assertThat(folder.getCopyStatus()).isEqualTo(CopyType.SECONDARY);
	}

	@Test
	public void whenModifyingActualDatesThenStatusIsUpdated()
			throws Exception {

		final AtomicInteger folderStatusAddCounter = new AtomicInteger();
		final AtomicInteger folderStatusUpdateCounter = new AtomicInteger();

		getModelLayerFactory().getExtensions().forCollection(zeCollection).recordExtensions.add(new RecordExtension() {

			@Override
			public void recordInCreationBeforeSave(RecordInCreationBeforeSaveEvent event) {
				if (event.isSchemaType(Folder.SCHEMA_TYPE)) {

					folderStatusAddCounter.incrementAndGet();

				}
			}

			@Override
			public void recordInModificationBeforeSave(RecordInModificationBeforeSaveEvent event) {
				if (event.isSchemaType(Folder.SCHEMA_TYPE)) {

					if (event.hasModifiedMetadata(Folder.ARCHIVISTIC_STATUS)) {
						folderStatusUpdateCounter.incrementAndGet();
					}

				}
			}
		});

		Folder folder = rm.newFolder();
		folder.setAdministrativeUnitEntered(records.unitId_10a);
		folder.setCategoryEntered(records.categoryId_X13);
		folder.setTitle("Ze folder");
		folder.setRetentionRuleEntered(records.ruleId_2);
		folder.setCopyStatusEntered(CopyType.PRINCIPAL);
		folder.setOpenDate(february2_2015);
		folder.setMediumTypes(MD, PA);
		getModelLayerFactory().newRecordServices().add(folder);
		assertThat(folderStatusAddCounter.get()).isEqualTo(1);
		assertThat(folderStatusUpdateCounter.get()).isEqualTo(0);

		folder.setActualTransferDate(february11_2015);
		getModelLayerFactory().newRecordServices().update(folder);
		assertThat(folder.getArchivisticStatus()).isEqualTo(FolderStatus.SEMI_ACTIVE);
		assertThat(folderStatusUpdateCounter.get()).isEqualTo(1);

		folder.setActualTransferDate(null);
		getModelLayerFactory().newRecordServices().update(folder);
		assertThat(folder.getArchivisticStatus()).isEqualTo(FolderStatus.ACTIVE);
		assertThat(folderStatusUpdateCounter.get()).isEqualTo(2);

		folder.setActualTransferDate(february11_2015);
		folder.setActualDepositDate(february11_2015);
		getModelLayerFactory().newRecordServices().update(folder);
		assertThat(folder.getArchivisticStatus()).isEqualTo(FolderStatus.INACTIVE_DEPOSITED);
		assertThat(folderStatusAddCounter.get()).isEqualTo(1);
		assertThat(folderStatusUpdateCounter.get()).isEqualTo(3);
	}

	// -------------------------------------------------------------------------

	private void givenRuleHasNoPrincipalCopyType(String id)
			throws Exception {
		RetentionRule rule = rm.getRetentionRule(id);
		List<CopyRetentionRule> copyRules = new ArrayList<>(rule.getCopyRetentionRules());
		for (Iterator<CopyRetentionRule> iterator = copyRules.iterator(); iterator.hasNext(); ) {
			if (iterator.next().getCopyType() == CopyType.PRINCIPAL) {
				iterator.remove();
			}
		}
		rule.setCopyRetentionRules(copyRules);
		try {
			getModelLayerFactory().newRecordServices().update(rule);
		} catch (RecordServicesException e) {
			throw new RuntimeException(e);
		}

	}

	private Folder folderWithSingleCopyRule(CopyRetentionRule copyRetentionRule) {

		givenRuleWithResponsibleAdminUnitsFlagAndCopyRules(copyRetentionRule, secondary("888-888-D", PA));

		if (!transaction.getRecords().isEmpty()) {
			try {
				recordServices.execute(transaction);
			} catch (RecordServicesException e) {
				throw new RuntimeException(e);
			}
			transaction = new Transaction();
		}

		Folder folder = rm.newFolder();
		folder.setAdministrativeUnitEntered(aPrincipalAdminUnit);
		folder.setCategoryEntered(records.categoryId_X110);
		folder.setTitle("Ze folder");
		folder.setRetentionRuleEntered(zeRule);
		folder.setCopyStatusEntered(copyRetentionRule.getCopyType());
		return folder;
	}

	private Folder principalFolderWithZeRule() {

		if (!transaction.getRecords().isEmpty()) {
			try {
				recordServices.execute(transaction);
			} catch (RecordServicesException e) {
				throw new RuntimeException(e);
			}
			transaction = new Transaction();
		}

		Folder folder = rm.newFolder();
		folder.setAdministrativeUnitEntered(aPrincipalAdminUnit);
		folder.setCategoryEntered(records.categoryId_X110);
		folder.setTitle("Ze folder");
		folder.setRetentionRuleEntered(zeRule);
		folder.setCopyStatusEntered(CopyType.PRINCIPAL);
		return folder;
	}

	private Folder secondaryFolderWithZeRule() {

		if (!transaction.getRecords().isEmpty()) {
			try {
				recordServices.execute(transaction);
			} catch (RecordServicesException e) {
				throw new RuntimeException(e);
			}
			transaction = new Transaction();
		}

		Folder folder = rm.newFolder();
		folder.setAdministrativeUnitEntered(aPrincipalAdminUnit);
		folder.setCategoryEntered(records.categoryId_X110);
		folder.setTitle("Ze folder");
		folder.setRetentionRuleEntered(zeRule);
		folder.setCopyStatusEntered(CopyType.SECONDARY);
		return folder;
	}

	private Folder saveAndLoad(Folder folder)
			throws RecordServicesException {
		recordServices.add(folder.getWrappedRecord());
		return rm.getFolder(folder.getId());
	}

	private RetentionRule givenRuleWithResponsibleAdminUnitsFlagAndCopyRules(CopyRetentionRule... rules) {
		return givenRetentionRule(rules).setResponsibleAdministrativeUnits(true);
	}

	private RetentionRule givenRuleWithAdminUnitsAndCopyRules(CopyRetentionRule... rules) {
		return givenRetentionRule(rules).setAdministrativeUnits(Arrays.asList(aPrincipalAdminUnit, anotherPrincipalAdminUnit));
	}

	private RetentionRule givenRetentionRule(CopyRetentionRule... rules) {
		RetentionRule retentionRule = rm.newRetentionRuleWithId(zeRule);

		retentionRule.setCode("Ze rule");
		retentionRule.setTitle("Ze rule");
		retentionRule.setApproved(true);
		//		retentionRule.setCategories(asList(zeCategory));
		retentionRule.setCopyRetentionRules(rules);

		return transaction.add(retentionRule);
	}

	private CopyRetentionRule principal(String status, String... mediumTypes) {
		return CopyRetentionRule.newPrincipal(asList(mediumTypes), status);
	}

	private CopyRetentionRule secondary(String status, String... mediumTypes) {
		return CopyRetentionRule.newSecondary(asList(mediumTypes), status);
	}

}
