package com.constellio.app.services.schemas.bulkImport.data;

import java.util.List;

public interface ImportDataProvider {

	int size(String schemaType)
			throws ImportDataProviderRuntimeException;

	void initialize();

	void close();

	List<String> getAvailableSchemaTypes();

	ImportDataIterator newDataIterator(String schemaType);

}
