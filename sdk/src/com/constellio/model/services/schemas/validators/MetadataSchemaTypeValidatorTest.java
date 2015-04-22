/*Constellio Enterprise Information Management

Copyright (c) 2015 "Constellio inc."

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.constellio.model.services.schemas.validators;

import static com.constellio.sdk.tests.TestUtils.mockMetadata;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.constellio.model.entities.schemas.AllowedReferences;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.frameworks.validation.ValidationErrors;
import com.constellio.model.frameworks.validation.ValidationException;
import com.constellio.model.services.schemas.MetadataList;
import com.constellio.sdk.tests.ConstellioTest;

public class MetadataSchemaTypeValidatorTest extends ConstellioTest {

	private static final String aMetadataLabel = "aMetadataLabel";
	private static final String aMetadataCode = "aMetadataCode";
	MetadataList customSchema2Metadatas;
	private MetadataSchemaTypeValidator validator;
	@Mock private ValidationErrors validationErrors;
	@Mock private Map<String, String> parameters;
	@Mock private MetadataSchemaType schemaType;
	@Mock private MetadataSchema defaultSchema;
	private MetadataList defaultMetadatas;
	private Metadata defaultMetadata1 = mockMetadata("zeType_default_metadata1");
	private Metadata defaultMetadata2 = mockMetadata("zeType_default_metadata2");
	private Metadata defaultMetadata3 = mockMetadata("zeType_default_metadata3");
	@Mock private MetadataSchema customSchema1;
	private MetadataList customMetadatas;
	private Metadata customMetadata1 = mockMetadata("zeType_custom1_customMetadata1");
	private Metadata customMetadata2 = mockMetadata("zeType_custom1_customMetadata2");
	@Mock private MetadataSchema customSchema2;
	@Mock private Metadata aMetadata;

	private Set<String> allowedSchemas;
	@Mock private AllowedReferences allowedReferences;

	@Before
	public void setup() {
		validator = spy(new MetadataSchemaTypeValidator());

		configureSchemas();

		allowedSchemas = new HashSet<String>();
	}

	@Test
	public void whenValidatingSchemaTypeThenAllSchemasValidated() {
		doNothing().when(validator).validateReferenceMetadata(any(Metadata.class), eq(validationErrors));

		validator.validate(schemaType, validationErrors);

		verify(validator, times(1)).validateDefaultSchema(defaultSchema, validationErrors);
		verify(validator, times(1)).validateCustomSchema(customSchema1, validationErrors);
		verify(validator, times(1)).validateCustomSchema(customSchema2, validationErrors);
	}

	@Test
	public void whenValidatingMetadataThenEveryValidationCalled() {
		doNothing().when(validator).validateReferenceMetadata(any(Metadata.class), eq(validationErrors));

		validator.validateMetadata(aMetadata, validationErrors);

		verify(validator, times(1)).validateMetadataBasicInfo(aMetadata, validationErrors);
		verify(validator, times(1)).validateReferenceMetadata(aMetadata, validationErrors);
	}

	@Test
	public void whenValidatingReferenceMetadataWithAllowedReferencesThenNoErrorAdded()
			throws ValidationException {
		allowedSchemas.add(aMetadataCode);

		when(aMetadata.getAllowedReferences()).thenReturn(allowedReferences);
		when(allowedReferences.getAllowedSchemas()).thenReturn(allowedSchemas);
		when(aMetadata.getType()).thenReturn(MetadataValueType.REFERENCE);

		validator.validateReferenceMetadata(aMetadata, validationErrors);

		verifyZeroInteractions(validationErrors);
	}

	@Test
	public void whenValidatingNonReferenceMetadataWithNoAllowedReferencesThenNoErrorAdded()
			throws ValidationException {
		when(aMetadata.getAllowedReferences()).thenReturn(allowedReferences);
		when(allowedReferences.getAllowedSchemas()).thenReturn(allowedSchemas);
		when(aMetadata.getType()).thenReturn(MetadataValueType.STRING);

		validator.validateReferenceMetadata(aMetadata, validationErrors);

		verifyZeroInteractions(validationErrors);
	}

	@Test
	public void whenValidatingReferenceMetadataWithNoAllowedReferencesThenRightErrorAdded() {
		when(aMetadata.getAllowedReferences()).thenReturn(allowedReferences);
		when(allowedReferences.getAllowedSchemas()).thenReturn(allowedSchemas);
		when(aMetadata.getType()).thenReturn(MetadataValueType.REFERENCE);

		doReturn(parameters).when(validator).createMapWithCodeAndLabel(aMetadata);
		validator.validateReferenceMetadata(aMetadata, validationErrors);

		verify(validationErrors, times(1)).add(validator.getClass(), "NoAllowedReferencesInReferenceMetadata", parameters);
	}

	@Test
	public void whenValidatingNonReferenceMetadataWithAllowedReferencesThenRightErrorAdded() {
		allowedSchemas.add(aMetadataCode);

		when(aMetadata.getAllowedReferences()).thenReturn(allowedReferences);
		when(allowedReferences.getAllowedSchemas()).thenReturn(allowedSchemas);
		when(aMetadata.getType()).thenReturn(MetadataValueType.STRING);
		doReturn(parameters).when(validator).createMapWithCodeLabelAndType(aMetadata);
		validator.validateReferenceMetadata(aMetadata, validationErrors);

		verify(validationErrors, times(1)).add(validator.getClass(), "AllowedReferencesInNonReferenceMetadata", parameters);
	}

	@Test
	public void whenValidatingInheritedMetadataThenBasicInfoNotValidated() {
		when(customMetadata1.getInheritance()).thenReturn(aMetadata);
		doNothing().when(validator).validateReferenceMetadata(any(Metadata.class), eq(validationErrors));

		validator.validateMetadata(customMetadata1, validationErrors);

		verify(validator, never()).validateMetadataBasicInfo(customMetadata1, validationErrors);
		verify(validator, times(1)).validateReferenceMetadata(customMetadata1, validationErrors);
	}

	@Test
	public void whenValidatingMetadataBasicInfoThenLabelAndTypeValidated() {
		validator.validateMetadataBasicInfo(aMetadata, validationErrors);

		verify(validator, times(1)).validateMetadataLabelNotNull(aMetadata, validationErrors);
		verify(validator, times(1)).validateMetadataTypeNotNull(aMetadata, validationErrors);
	}

	@Test
	public void whenValidatingDefaultSchemaThenAllDefaultMetadatasValidated() {
		doNothing().when(validator).validateReferenceMetadata(any(Metadata.class), eq(validationErrors));

		validator.validateDefaultSchema(defaultSchema, validationErrors);

		verify(validator, times(1)).validateMetadata(defaultMetadata1, validationErrors);
		verify(validator, times(1)).validateMetadata(defaultMetadata2, validationErrors);
		verify(validator, times(1)).validateMetadata(defaultMetadata3, validationErrors);
	}

	@Test
	public void whenValidatingCustomSchemaThenAllCustomMetadatasValidated() {
		doNothing().when(validator).validateReferenceMetadata(any(Metadata.class), eq(validationErrors));

		validator.validateCustomSchema(customSchema1, validationErrors);

		verify(validator, times(1)).validateMetadata(customMetadata1, validationErrors);
		verify(validator, times(1)).validateMetadata(customMetadata2, validationErrors);
	}

	@Test
	public void givenMetadataHasLabelWhenValidatingLabelThenNoErrorAdded() {
		when(aMetadata.getLabel()).thenReturn(aMetadataLabel);

		validator.validateMetadataLabelNotNull(aMetadata, validationErrors);

		verifyZeroInteractions(validationErrors);
	}

	@Test
	public void givenMetadataHasNoLabelWhenValidatingLabelThenCorrectErrorAdded() {
		doReturn(parameters).when(validator).createMapWithCodeAndLabel(aMetadata);

		validator.validateMetadataLabelNotNull(aMetadata, validationErrors);

		verify(validationErrors, times(1)).add(validator.getClass(), "NoLabelInNonInheritedMetadata", parameters);
	}

	@Test
	public void givenMetadataHasNoTypeWhenValidatingTypeThenCorrectErrorAdded() {
		doReturn(parameters).when(validator).createMapWithCodeAndLabel(aMetadata);

		validator.validateMetadataTypeNotNull(aMetadata, validationErrors);

		verify(validationErrors, times(1)).add(validator.getClass(), "NoTypeInNonInheritedMetadata", parameters);
	}

	@Test
	public void givenMetadataHasTypeWhenValidatingTypeThenNoErrorAdded() {
		when(aMetadata.getType()).thenReturn(MetadataValueType.STRING);

		validator.validateMetadataTypeNotNull(aMetadata, validationErrors);

		verifyZeroInteractions(validationErrors);
	}

	@Test
	public void whenCreatingMapWithCodeAndLabelThenContentIsCorrect() {
		when(aMetadata.getLocalCode()).thenReturn(aMetadataCode);
		when(aMetadata.getLabel()).thenReturn(aMetadataLabel);

		Map<String, String> returnedMap = validator.createMapWithCodeAndLabel(aMetadata);

		assertEquals(aMetadataCode, returnedMap.get("localCode"));
		assertEquals(aMetadataLabel, returnedMap.get("label"));
		assertEquals(2, returnedMap.size());
	}

	@Test
	public void whenCreatingMapWithCodeLabelAndTypeThenContentIsCorrect() {
		when(aMetadata.getLocalCode()).thenReturn(aMetadataCode);
		when(aMetadata.getLabel()).thenReturn(aMetadataLabel);
		when(aMetadata.getType()).thenReturn(MetadataValueType.STRING);

		Map<String, String> returnedMap = validator.createMapWithCodeLabelAndType(aMetadata);

		assertEquals(aMetadataCode, returnedMap.get("localCode"));
		assertEquals(aMetadataLabel, returnedMap.get("label"));
		assertEquals("STRING", returnedMap.get("type"));
		assertEquals(3, returnedMap.size());
	}

	private void configureSchemas() {
		defaultMetadatas = new MetadataList(defaultMetadata1, defaultMetadata2, defaultMetadata3);
		List<MetadataSchema> customSchemas = Arrays.asList(customSchema1, customSchema2);
		customMetadatas = new MetadataList(customMetadata1, customMetadata2);

		when(schemaType.getDefaultSchema()).thenReturn(defaultSchema);
		when(schemaType.getSchemas()).thenReturn(customSchemas);

		when(defaultSchema.getMetadatas()).thenReturn(defaultMetadatas);
		when(customSchema1.getMetadatas()).thenReturn(customMetadatas);
		when(customSchema2.getMetadatas()).thenReturn(customMetadatas);

		when(aMetadata.getAllowedReferences()).thenReturn(allowedReferences);
		when(allowedReferences.getAllowedSchemas()).thenReturn(allowedSchemas);
	}

}
