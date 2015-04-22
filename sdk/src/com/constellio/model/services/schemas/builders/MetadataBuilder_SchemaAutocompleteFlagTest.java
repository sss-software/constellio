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
package com.constellio.model.services.schemas.builders;

import static com.constellio.model.entities.schemas.MetadataValueType.STRING;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class MetadataBuilder_SchemaAutocompleteFlagTest extends MetadataBuilderTest {

	@Test
	public void givenSchemaAutocompleteFlagUndefinedOnMetadataWithoutInheritanceWhenBuildingThenSingleValue()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING);

		build();

		assertThat(metadataWithoutInheritance.isSchemaAutocomplete()).isFalse();
	}

	@Test
	public void givenSchemaAutocompleteFlagUndefinedOnMetadataWithoutInheritanceWhenModifyingThenSingleValue()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING);

		buildAndModify();

		assertThat(metadataWithoutInheritanceBuilder.isSchemaAutocomplete()).isFalse();
	}

	@Test
	public void givenSchemaAutocompleteFlagSetToFalseOnMetadataWithoutInheritanceWhenBuildingThenSingleValue()
			throws Exception {
		metadataWithoutInheritanceBuilder.setType(STRING).setSchemaAutocomplete(false);

		build();

		assertThat(metadataWithoutInheritance.isSchemaAutocomplete()).isFalse();
	}

	@Test
	public void givenSchemaAutocompleteFlagSetToFalseOnMetadataWithoutInheritanceWhenModifyingThenSingleValue()
			throws Exception {
		metadataWithoutInheritanceBuilder.setType(STRING).setSchemaAutocomplete(false);

		buildAndModify();

		assertThat(metadataWithoutInheritanceBuilder.isSchemaAutocomplete()).isFalse();
	}

	@Test
	public void givenSchemaAutocompleteFlagSetToTrueOnMetadataWithoutInheritanceWhenBuildingThenSchemaAutocomplete()
			throws Exception {
		metadataWithoutInheritanceBuilder.setType(STRING).setSchemaAutocomplete(true);

		build();

		assertThat(metadataWithoutInheritance.isSchemaAutocomplete()).isTrue();
	}

	@Test
	public void givenSchemaAutocompleteFlagSetToTrueOnMetadataWithoutInheritanceWhenModifyingThenSchemaAutocomplete()
			throws Exception {
		metadataWithoutInheritanceBuilder.setType(STRING).setSchemaAutocomplete(true);

		buildAndModify();

		assertThat(metadataWithoutInheritanceBuilder.isSchemaAutocomplete()).isTrue();
	}

	@Test
	public void givenMutlivalueFlagOnMetadataWithInheritanceWhenBuildingThenSetToInheritedValue()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING).setSchemaAutocomplete(true);

		build();

		assertThat(metadataWithInheritance.isSchemaAutocomplete()).isTrue();
	}

	@Test
	public void givenSchemaAutocompleteFlagOnMetadataWithInheritanceWhenModifyingThenSetToInheritedValue()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING).setSchemaAutocomplete(true);

		buildAndModify();

		assertThat(metadataWithInheritanceBuilder.isSchemaAutocomplete()).isTrue();
	}

	@Test
	public void givenTrueFlagModifiedInInheritedMetadataBuilderThenModifiedInMetadataWithHeritance()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING).setSchemaAutocomplete(true);
		assertThat(metadataWithInheritanceBuilder.isSchemaAutocomplete()).isTrue();

	}

	@Test
	public void givenFalseFlagModifiedInInheritedMetadataBuilderThenModifiedInMetadataWithHeritance()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING).setSchemaAutocomplete(false);
		assertThat(metadataWithInheritanceBuilder.isSchemaAutocomplete()).isFalse();

	}
}
