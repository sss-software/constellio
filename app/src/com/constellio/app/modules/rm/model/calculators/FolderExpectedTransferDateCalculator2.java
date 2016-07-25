package com.constellio.app.modules.rm.model.calculators;

import java.util.List;

import org.joda.time.LocalDate;

import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.model.entities.calculators.MetadataValueCalculator;
import com.constellio.model.entities.calculators.dependencies.LocalDependency;

public class FolderExpectedTransferDateCalculator2
		extends AbstractFolderExpectedDateCalculator
		implements MetadataValueCalculator<LocalDate> {

	LocalDependency<List<LocalDate>> transferDatesParam = LocalDependency
			.toADate(Folder.COPY_RULES_EXPECTED_TRANSFER_DATES).whichIsMultivalue();

	@Override
	LocalDependency<List<LocalDate>> getDatesDependency() {
		return transferDatesParam;
	}
}