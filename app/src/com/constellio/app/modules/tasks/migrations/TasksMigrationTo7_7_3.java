package com.constellio.app.modules.tasks.migrations;

import com.constellio.app.entities.modules.MetadataSchemasAlterationHelper;
import com.constellio.app.entities.modules.MigrationHelper;
import com.constellio.app.entities.modules.MigrationResourcesProvider;
import com.constellio.app.entities.modules.MigrationScript;
import com.constellio.app.modules.tasks.TasksEmailTemplates;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.data.dao.managers.config.ConfigManagerException;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class TasksMigrationTo7_7_3 extends MigrationHelper implements MigrationScript {
	private String collection;

	private MigrationResourcesProvider migrationResourcesProvider;

	private AppLayerFactory appLayerFactory;

	@Override
	public String getVersion() {
		return "7.7.3";
	}

	@Override
	public void migrate(String collection, MigrationResourcesProvider migrationResourcesProvider,
						AppLayerFactory appLayerFactory)
			throws Exception {
		this.collection = collection;
		this.migrationResourcesProvider = migrationResourcesProvider;
		this.appLayerFactory = appLayerFactory;
		new TasksMigrationTo7_7_3.SchemaAlterationsFor7_7_3(collection, migrationResourcesProvider, appLayerFactory).migrate();
		reloadEmailTemplates();
	}


	private void reloadEmailTemplates() {
		if (appLayerFactory.getModelLayerFactory().getCollectionsListManager().getCollectionLanguages(collection).get(0).equals("fr")) {
			reloadEmailTemplate("subTasksModificationTemplate.html", TasksEmailTemplates.TASK_SUB_TASKS_MODIFIED);
			reloadEmailTemplate("taskAssigneeModificationTemplate.html", TasksEmailTemplates.TASK_ASSIGNEE_MODIFIED);
			reloadEmailTemplate("taskAssigneeToYouTemplate.html", TasksEmailTemplates.TASK_ASSIGNED_TO_YOU);
			reloadEmailTemplate("taskReminderTemplate.html", TasksEmailTemplates.TASK_REMINDER);
			reloadEmailTemplate("taskStatusModificationTemplate.html", TasksEmailTemplates.TASK_STATUS_MODIFIED);
		}
	}

	private void reloadEmailTemplate(final String templateFileName, final String templateId) {
		final InputStream templateInputStream = migrationResourcesProvider.getStream(templateFileName);

		try {
			appLayerFactory.getModelLayerFactory().getEmailTemplatesManager().replaceCollectionTemplate(templateId, collection, templateInputStream);
		} catch (IOException | ConfigManagerException.OptimisticLockingConfiguration e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(templateInputStream);
		}
	}

	private class SchemaAlterationsFor7_7_3 extends MetadataSchemasAlterationHelper {
		public SchemaAlterationsFor7_7_3(String collection, MigrationResourcesProvider migrationResourcesProvider,
										 AppLayerFactory appLayerFactory) {
			super(collection, migrationResourcesProvider, appLayerFactory);
		}

		@Override
		protected void migrate(MetadataSchemaTypesBuilder typesBuilder) {
		}
	}
}
