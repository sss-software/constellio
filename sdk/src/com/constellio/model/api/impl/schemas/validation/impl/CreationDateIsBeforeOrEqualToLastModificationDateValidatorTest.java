package com.constellio.model.api.impl.schemas.validation.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.schemas.ConfigProvider;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.frameworks.validation.ValidationErrors;
import com.constellio.sdk.tests.ConstellioTest;

public class CreationDateIsBeforeOrEqualToLastModificationDateValidatorTest extends ConstellioTest {

	@Mock Record record;

	LocalDateTime date, dateAfter, dateBefore;

	@Mock MetadataSchemaTypes types;
	@Mock Metadata creationDate, modificationDate;
	@Mock ValidationErrors validationErrors;
	@Mock MetadataSchema schema;
	@Mock ConfigProvider configProvider;

	SimpleDateFormat sdf = new SimpleDateFormat();

	CreationDateIsBeforeOrEqualToLastModificationDateValidator validator;

	@Before
	public void setUp()
			throws Exception {
		validator = new CreationDateIsBeforeOrEqualToLastModificationDateValidator();

		dateBefore = new LocalDateTime(2014, 7, 6, 23, 59, 59);
		date = new LocalDateTime(2014, 7, 7, 0, 0, 0);
		dateAfter = new LocalDateTime(2014, 7, 7, 0, 0, 1);

		when(schema.getMetadata("creationDate")).thenReturn(creationDate);
		when(schema.getMetadata("modificationDate")).thenReturn(modificationDate);
	}

	@Test
	public void whenModificationDateIsBeforeCreationDateThenValidationMessage()
			throws Exception {
		when(record.get(creationDate)).thenReturn(date);
		when(record.get(modificationDate)).thenReturn(dateBefore);

		validator.validate(record, types, schema, configProvider, validationErrors);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(CreationDateIsBeforeOrEqualToLastModificationDateValidator.CREATION_DATE_MESSAGE_PARAM, date.toString());
		parameters.put(CreationDateIsBeforeOrEqualToLastModificationDateValidator.MODIFICATION_DATE_MESSAGE_PARAM,
				dateBefore.toString());
		verify(validationErrors).add(CreationDateIsBeforeOrEqualToLastModificationDateValidator.class,
				CreationDateIsBeforeOrEqualToLastModificationDateValidator.CREATION_DATE_IS_AFTER_MODIFICATION_DATE, parameters);
	}

	@Test
	public void whenModificationDateIsEqualCreationDateThenValidationMessage()
			throws Exception {
		when(record.get(creationDate)).thenReturn(date);
		when(record.get(modificationDate)).thenReturn(date);

		validator.validate(record, types, schema, configProvider, validationErrors);

		verifyZeroInteractions(validationErrors);
	}

	@Test
	public void whenModificationDateIsAfterCreationDateThenValidationMessage()
			throws Exception {
		when(record.get(creationDate)).thenReturn(date);
		when(record.get(modificationDate)).thenReturn(dateAfter);

		validator.validate(record, types, schema, configProvider, validationErrors);

		verifyZeroInteractions(validationErrors);
	}

}
