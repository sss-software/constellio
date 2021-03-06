package com.constellio.app.modules.rm.migrations.records;

import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.model.entities.records.Content;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.RecordMigrationScript;
import com.constellio.model.entities.schemas.Schemas;
import org.apache.commons.io.FilenameUtils;

import static com.constellio.data.io.ConversionManager.isSupportedExtension;

public class RMDocumentMigrationTo8_0_1_2 extends RecordMigrationScript {

	private RMSchemasRecordsServices rm;

	public RMDocumentMigrationTo8_0_1_2(String collection, AppLayerFactory appLayerFactory) {
		this.rm = new RMSchemasRecordsServices(collection, appLayerFactory);
	}

	@Override
	public String getSchemaType() {
		return Document.SCHEMA_TYPE;
	}

	@Override
	public void migrate(Record record) {

		Document document = rm.wrapDocument(record);
		Content content = document.getContent();
		if (content != null && isRequiringConversion(document.getContent())) {
			document.set(Schemas.MARKED_FOR_PREVIEW_CONVERSION, true);
		}

	}

	private boolean isRequiringConversion(Content content) {

		String filename = content.getCurrentVersion().getFilename();
		String ext = FilenameUtils.getExtension(filename).toLowerCase();
		return isSupportedExtension(ext);

	}

	@Override
	public void afterLastMigratedRecord() {
	}
}
