package com.constellio.app.modules.tasks.migrations;

import com.constellio.app.entities.modules.MetadataSchemasAlterationHelper;
import com.constellio.app.entities.modules.MigrationHelper;
import com.constellio.app.entities.modules.MigrationResourcesProvider;
import com.constellio.app.entities.modules.MigrationScript;
import com.constellio.app.modules.tasks.model.wrappers.Task;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.services.schemasDisplay.SchemasDisplayManager;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.schemas.builders.MetadataSchemaBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;

public class TasksMigrationTo7_7 extends MigrationHelper implements MigrationScript {
    @Override
    public String getVersion() {
        return "7.7";
    }

    @Override
    public void migrate(String collection, MigrationResourcesProvider migrationResourcesProvider, AppLayerFactory appLayerFactory)
            throws Exception {
        new TasksMigrationTo7_7.SchemaAlterationFor7_7(collection, migrationResourcesProvider, appLayerFactory).migrate();
        SchemasDisplayManager manager = appLayerFactory.getMetadataSchemasDisplayManager();
        manager.saveSchema(manager.getSchema(collection, Task.DEFAULT_SCHEMA)
                .withNewFormMetadataBefore(Task.DEFAULT_SCHEMA + "_" + Task.QUESTION, Task.DEFAULT_SCHEMA + "_" + Task.DECISION)
                .withNewDisplayMetadataBefore(Task.DEFAULT_SCHEMA + "_" + Task.QUESTION, Task.DEFAULT_SCHEMA + "_" + Task.DECISION));
    }

    class SchemaAlterationFor7_7 extends MetadataSchemasAlterationHelper {

        protected SchemaAlterationFor7_7(String collection, MigrationResourcesProvider migrationResourcesProvider,
                                         AppLayerFactory appLayerFactory) {
            super(collection, migrationResourcesProvider, appLayerFactory);
        }

        public String getVersion() {
            return "7.7";
        }

        @Override
        protected void migrate(MetadataSchemaTypesBuilder typesBuilder) {
            MetadataSchemaBuilder task = typesBuilder.getSchemaType(Task.SCHEMA_TYPE)
                    .getDefaultSchema();
            task.createUndeletable(Task.QUESTION).setType(MetadataValueType.STRING)
                    .setEssentialInSummary(true);
        }
    }
}
