package com.constellio.model.services.schemas.validators;

import com.constellio.model.entities.schemas.ConfigProvider;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.validation.RecordMetadataValidator;
import com.constellio.model.frameworks.validation.ValidationErrors;

import java.util.HashMap;
import java.util.Map;

public class TemporaryRecordValidator implements RecordMetadataValidator<Double> {

	public static final int MIN_NUMBER_OF_DAYS = -1;

	@Override
	public void validate(Metadata metadata, Double value, ConfigProvider configProvider,
						 ValidationErrors validationErrors) {
		if (value != null && !validateNumberOfDays(value)) {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("NUMBER_OF_DAYS", value);
			validationErrors.add(getClass(), "INVALID_NUMBER_OF_DAYS", parameters);
		}
	}

	public boolean validateNumberOfDays(Double value) {
		return value >= MIN_NUMBER_OF_DAYS;
	}
}
