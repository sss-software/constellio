package com.constellio.app.modules.tasks.migrations;

import com.constellio.app.entities.modules.MigrationHelper;
import com.constellio.app.entities.modules.MigrationResourcesProvider;
import com.constellio.app.entities.modules.MigrationScript;
import com.constellio.app.modules.tasks.TasksEmailTemplates;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.data.dao.managers.config.ConfigManagerException.OptimisticLockingConfiguration;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class TasksMigrationTo7_7_4 extends MigrationHelper implements MigrationScript {

    private String collection;

    private MigrationResourcesProvider migrationResourcesProvider;

    private AppLayerFactory appLayerFactory;

	@Override
	public String getVersion() {
		return "7.7.4";
	}

	@Override
	public void migrate(String collection, MigrationResourcesProvider migrationResourcesProvider, AppLayerFactory appLayerFactory)
			throws Exception {
        this.collection = collection;
        this.migrationResourcesProvider = migrationResourcesProvider;
        this.appLayerFactory = appLayerFactory;

		reloadEmailTemplates();
	}

	private void reloadEmailTemplates() {
        if (appLayerFactory.getModelLayerFactory().getCollectionsListManager().getCollectionLanguages(collection).get(0).equals("en")) {
            reloadEmailTemplate("taskStatusModificationToCompletedTemplate_en.html", TasksEmailTemplates.TASK_COMPLETED);
        } else {
            reloadEmailTemplate("taskStatusModificationToCompletedTemplate.html", TasksEmailTemplates.TASK_COMPLETED);
        }
	}

	private void reloadEmailTemplate(final String templateFileName, final String templateId) {
		final InputStream templateInputStream = migrationResourcesProvider.getStream(templateFileName);

		try {
            appLayerFactory.getModelLayerFactory().getEmailTemplatesManager().replaceCollectionTemplate(templateId, collection, templateInputStream);
		} catch (IOException | OptimisticLockingConfiguration e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(templateInputStream);
		}
	}
}
