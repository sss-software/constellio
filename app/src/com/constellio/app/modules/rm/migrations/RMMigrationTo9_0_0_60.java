package com.constellio.app.modules.rm.migrations;

import com.constellio.app.entities.modules.MetadataSchemasAlterationHelper;
import com.constellio.app.entities.modules.MigrationHelper;
import com.constellio.app.entities.modules.MigrationResourcesProvider;
import com.constellio.app.entities.modules.MigrationScript;
import com.constellio.app.modules.rm.model.calculators.document.DocumentCheckedOutDateCalculator;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.services.schemas.builders.MetadataSchemaBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;

public class RMMigrationTo9_0_0_60 extends MigrationHelper implements MigrationScript {

	@Override
	public String getVersion() {
		return "9.0.0.60";
	}

	@Override
	public void migrate(String collection, MigrationResourcesProvider migrationResourcesProvider,
						AppLayerFactory appLayerFactory)
			throws Exception {
		new SchemaAlterationFor9_0_0_60(collection, migrationResourcesProvider, appLayerFactory).migrate();
	}

	class SchemaAlterationFor9_0_0_60 extends MetadataSchemasAlterationHelper {

		protected SchemaAlterationFor9_0_0_60(String collection, MigrationResourcesProvider migrationResourcesProvider,
											  AppLayerFactory appLayerFactory) {
			super(collection, migrationResourcesProvider, appLayerFactory);
		}

		@Override
		protected void migrate(MetadataSchemaTypesBuilder typesBuilder) {

			MetadataSchemaBuilder document = typesBuilder.getDefaultSchema(Document.SCHEMA_TYPE);
			document.createUndeletable(Document.CONTENT_CHECKED_OUT_DATE)
					.setType(MetadataValueType.DATE_TIME)
					.defineDataEntry().asCalculated(DocumentCheckedOutDateCalculator.class);
		}
	}
}
