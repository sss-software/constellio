package com.constellio.app.modules.rm.migrations;

import com.constellio.app.entities.modules.MetadataSchemasAlterationHelper;
import com.constellio.app.entities.modules.MigrationResourcesProvider;
import com.constellio.app.entities.modules.MigrationScript;
import com.constellio.app.modules.rm.model.enums.CopyType;
import com.constellio.app.modules.rm.wrappers.Category;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.services.schemasDisplay.SchemasDisplayManager;
import com.constellio.app.ui.framework.components.BooleanLabel;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;

/**
 * Created by constellios on 2017-05-02.
 */
public class RMMigrationTo7_3 implements MigrationScript {


    @Override
    public String getVersion() {
        return "7.3";
    }

    @Override
    public void migrate(String collection, MigrationResourcesProvider migrationResourcesProvider, AppLayerFactory appLayerFactory) throws Exception {
        new SchemaAlterationFor7_3(collection, migrationResourcesProvider, appLayerFactory).migrate();

        SchemasDisplayManager manager = appLayerFactory.getMetadataSchemasDisplayManager();

        manager.saveSchema(manager.getSchema(collection,Category.DEFAULT_SCHEMA).withNewFormAndDisplayMetadatas(Category.DEFAULT_SCHEMA + "_" +Category.ACTIVATED));

    }

    class SchemaAlterationFor7_3 extends MetadataSchemasAlterationHelper {

        protected SchemaAlterationFor7_3(String collection, MigrationResourcesProvider migrationResourcesProvider,
                                         AppLayerFactory appLayerFactory) {
            super(collection, migrationResourcesProvider, appLayerFactory);
        }

        public String getVersion() {
            return "7.3";
        }

        @Override
        protected void migrate(MetadataSchemaTypesBuilder typesBuilder) {
            typesBuilder.getDefaultSchema(Folder.SCHEMA_TYPE).get(Folder.COPY_STATUS_ENTERED).setDefaultValue(CopyType.PRINCIPAL);
            typesBuilder.getDefaultSchema(Category.SCHEMA_TYPE).create(Category.ACTIVATED).setType(MetadataValueType.BOOLEAN).setDefaultValue(true);
        }
    }

}
