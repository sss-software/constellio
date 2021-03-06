package com.constellio.app.modules.tasks.model.calculators;

import com.constellio.app.modules.tasks.model.wrappers.Task;
import com.constellio.app.modules.tasks.model.wrappers.structures.TaskReminder;
import com.constellio.model.entities.calculators.AbstractMetadataValueCalculator;
import com.constellio.model.entities.calculators.CalculatorParameters;
import com.constellio.model.entities.calculators.dependencies.Dependency;
import com.constellio.model.entities.calculators.dependencies.LocalDependency;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.entities.schemas.Schemas;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.List;

import static java.util.Arrays.asList;

public class TaskNextReminderOnCalculator extends AbstractMetadataValueCalculator<LocalDate> {
	LocalDependency<List<TaskReminder>> remindersLocalDependency = LocalDependency.toAStructure(Task.REMINDERS)
			.whichIsMultivalue().whichIsRequired();
	LocalDependency<LocalDateTime> creationDateLocalDependency = LocalDependency.toADateTime(Schemas.CREATED_ON.getLocalCode());
	LocalDependency<LocalDate> startDateLocalDependency = LocalDependency.toADate(Task.START_DATE);
	LocalDependency<LocalDate> endDateLocalDependency = LocalDependency.toADate(Task.DUE_DATE);

	@Override
	public LocalDate calculate(CalculatorParameters parameters) {
		LocalDate oldestReminderDate = null;
		List<TaskReminder> taskReminderList = parameters.get(remindersLocalDependency);
		LocalDateTime creationDate = parameters.get(creationDateLocalDependency);
		LocalDate startDate = parameters.get(startDateLocalDependency);
		LocalDate endDate = parameters.get(endDateLocalDependency);

		if (taskReminderList != null) {
			for (TaskReminder currentTaskReminder : taskReminderList) {
				LocalDate currentReminderDate = computeReminders(currentTaskReminder, creationDate, startDate, endDate);
				if (oldestReminderDate == null) {
					oldestReminderDate = currentReminderDate;
				} else if (currentReminderDate != null && currentReminderDate.isBefore(oldestReminderDate)) {
					oldestReminderDate = currentReminderDate;
				}
			}
		}

		return oldestReminderDate;
	}

	private LocalDate computeReminders(TaskReminder taskReminder, LocalDateTime creationDate, LocalDate startDate,
									   LocalDate endDate) {
		if (taskReminder.isProcessed()) {
			return null;
		}
		return taskReminder.computeDate(creationDate, startDate, endDate);
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
		return asList(remindersLocalDependency, creationDateLocalDependency, startDateLocalDependency, endDateLocalDependency);
	}

}
