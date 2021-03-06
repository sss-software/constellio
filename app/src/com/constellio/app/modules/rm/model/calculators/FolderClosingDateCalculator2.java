package com.constellio.app.modules.rm.model.calculators;

import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.modules.rm.model.CopyRetentionRule;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.model.entities.calculators.AbstractMetadataValueCalculator;
import com.constellio.model.entities.calculators.CalculatorParameters;
import com.constellio.model.entities.calculators.dependencies.ConfigDependency;
import com.constellio.model.entities.calculators.dependencies.Dependency;
import com.constellio.model.entities.calculators.dependencies.LocalDependency;
import com.constellio.model.entities.calculators.dependencies.ReferenceDependency;
import com.constellio.model.entities.schemas.MetadataValueType;
import org.joda.time.LocalDate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.constellio.app.modules.rm.model.calculators.CalculatorUtils.toNextEndOfYearDate;

public class FolderClosingDateCalculator2 extends AbstractMetadataValueCalculator<LocalDate> {

	LocalDependency<LocalDate> openingDateParam = LocalDependency.toADate(Folder.OPENING_DATE);
	LocalDependency<LocalDate> enteredClosingDateParam = LocalDependency.toADate(Folder.ENTERED_CLOSING_DATE);
	LocalDependency<List<CopyRetentionRule>> copiesParam = LocalDependency.toAStructure(Folder.APPLICABLE_COPY_RULES)
			.whichIsMultivalue();
	ConfigDependency<Boolean> configCalculatedClosingDateParam = RMConfigs.CALCULATED_CLOSING_DATE.dependency();
	ConfigDependency<Integer> configNumberOfYearWhenFixedDelayParam =
			RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_FIXED_RULE.dependency();
	ConfigDependency<Integer> configNumberOfYearWhenVariableDelayParam =
			RMConfigs.CALCULATED_CLOSING_DATE_NUMBER_OF_YEAR_WHEN_VARIABLE_RULE.dependency();
	ConfigDependency<Integer> configRequiredDaysBeforeYearEndParam =
			RMConfigs.REQUIRED_DAYS_BEFORE_YEAR_END_FOR_NOT_ADDING_A_YEAR.dependency();
	ConfigDependency<Boolean> configAddYEarIfDateIsEndOfYearParam =
			RMConfigs.ADD_YEAR_IF_CALULATION_DATE_IS_END_IF_YEAR.dependency();
	ConfigDependency<String> configYearEndParam = RMConfigs.YEAR_END_DATE.dependency();
	LocalDependency<String> mainCopyRuleIdEnteredParam = LocalDependency.toAString(Folder.MAIN_COPY_RULE_ID_ENTERED);
	ConfigDependency<Boolean> configSubFoldersDecommissionParam = RMConfigs.SUB_FOLDER_DECOMMISSIONING.dependency();
	LocalDependency<String> parentFolderParam = LocalDependency.toAReference(Folder.PARENT_FOLDER);
	ReferenceDependency<LocalDate> parentClosingDateParam =
			ReferenceDependency.toADate(Folder.PARENT_FOLDER, Folder.CLOSING_DATE);

	@Override
	public LocalDate calculate(CalculatorParameters parameters) {
		boolean folderDecommissionedSeparately = parameters.get(configSubFoldersDecommissionParam);
		String parentFolder = parameters.get(parentFolderParam);
		if (!folderDecommissionedSeparately && parentFolder != null) {
			return parameters.get(parentClosingDateParam);
		}

		LocalDate enteredClosingDate = parameters.get(enteredClosingDateParam);
		LocalDate openingDate = parameters.get(openingDateParam);
		boolean configCalculatedClosingDate = parameters.get(configCalculatedClosingDateParam);
		if (enteredClosingDate != null || !configCalculatedClosingDate || openingDate == null) {
			return enteredClosingDate;
		}
		String mainCopyRuleIdEntered = parameters.get(mainCopyRuleIdEnteredParam);
		List<CopyRetentionRule> copies = parameters.get(copiesParam);

		String yearEnd = parameters.get(configYearEndParam);
		boolean addYEarIfDateIsEndOfYear = parameters.get(configAddYEarIfDateIsEndOfYearParam);
		int requiredDaysBeforeYearEnd = parameters.get(configRequiredDaysBeforeYearEndParam);

		LocalDate smallestClosingDate = null;
		Set<String> copyIds = new HashSet<>();
		for (CopyRetentionRule copy : copies) {
			copyIds.add(copy.getId());
		}
		for (CopyRetentionRule copy : copies) {
			if (mainCopyRuleIdEntered == null || copy.getId().equals(mainCopyRuleIdEntered)
				|| !copyIds.contains(mainCopyRuleIdEntered)) {
				LocalDate copyClosingDate = calculateForCopy(copy, parameters);
				LocalDate yearEndDate = toNextEndOfYearDate(copyClosingDate, yearEnd, requiredDaysBeforeYearEnd,
						addYEarIfDateIsEndOfYear);
				if (smallestClosingDate == null || (yearEndDate != null && smallestClosingDate.isAfter(yearEndDate))) {
					smallestClosingDate = yearEndDate;
				}
			}
		}
		return smallestClosingDate;
	}

	LocalDate calculateForCopy(CopyRetentionRule copy, CalculatorParameters parameters) {
		LocalDate openingDate = parameters.get(openingDateParam);
		int numberOfYearWhenVariableDelay = parameters.get(configNumberOfYearWhenVariableDelayParam);
		int numberOfYearWhenFixedDelay = parameters.get(configNumberOfYearWhenFixedDelayParam);

		if (copy.getActiveRetentionPeriod().isVariablePeriod()) {
			return calculateWithVariableDelay(openingDate, numberOfYearWhenVariableDelay);
		} else {
			return calculateWithFixedDelay(copy, openingDate, numberOfYearWhenFixedDelay);
		}

	}

	LocalDate calculateWithVariableDelay(LocalDate openingDate,
										 int numberOfYearWhenVariableDelay) {
		if (numberOfYearWhenVariableDelay == -1) {
			return null;
		} else {
			return openingDate.plusYears(numberOfYearWhenVariableDelay);
		}
	}

	LocalDate calculateWithFixedDelay(CopyRetentionRule copy, LocalDate openingDate,
									  int numberOfYearWhenFixedDelay) {
		if (numberOfYearWhenFixedDelay == -1) {
			return openingDate.plusYears(copy.getActiveRetentionPeriod().getFixedPeriod());
		} else {
			return openingDate.plusYears(numberOfYearWhenFixedDelay);
		}
	}

	@Override
	public LocalDate getDefaultValue() {
		return null;
	}

	@Override
	public MetadataValueType getReturnType() {
		return MetadataValueType.DATE;
	}

	@Override
	public boolean isMultiValue() {
		return false;
	}

	@Override
	public List<? extends Dependency> getDependencies() {
		return Arrays.asList(openingDateParam,
				enteredClosingDateParam,
				copiesParam,
				configCalculatedClosingDateParam,
				configNumberOfYearWhenFixedDelayParam,
				configNumberOfYearWhenVariableDelayParam,
				configRequiredDaysBeforeYearEndParam,
				configYearEndParam,
				configAddYEarIfDateIsEndOfYearParam,
				mainCopyRuleIdEnteredParam,
				configSubFoldersDecommissionParam,
				parentFolderParam,
				parentClosingDateParam);
	}

}