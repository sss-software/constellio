package com.constellio.app.modules.tasks.model.wrappers.request;

import com.constellio.app.modules.tasks.model.wrappers.Task;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;

public class ReactivationRequest extends RequestTask {

	public static final String SCHEMA_NAME = "reactivationRequest";
	public static final String FULL_SCHEMA_NAME = Task.SCHEMA_TYPE + "_" + SCHEMA_NAME;
	public static final String REACTIVATION_DATE = "reactivationDate";

	public ReactivationRequest(Record record, MetadataSchemaTypes types) {
		super(record, types);
	}
}
