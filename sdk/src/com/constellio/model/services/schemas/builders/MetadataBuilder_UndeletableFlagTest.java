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

public class MetadataBuilder_UndeletableFlagTest extends MetadataBuilderTest {

	@Test
	public void givenUndeletableFlagUndefinedOnMetadataWithoutInheritanceWhenBuildingThenDeletable()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING);

		build();

		assertThat(metadataWithoutInheritance.isUndeletable()).isFalse();
	}

	@Test
	public void givenUndeletableFlagUndefinedOnMetadataWithoutInheritanceWhenModifyingThenDeletable()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING);

		buildAndModify();

		assertThat(metadataWithoutInheritanceBuilder.isUndeletable()).isFalse();
	}

	@Test
	public void givenUndeletableFlagSetToFalseOnMetadataWithoutInheritanceWhenBuildingThenDeletable()
			throws Exception {
		metadataWithoutInheritanceBuilder.setType(STRING).setUndeletable(false);

		build();

		assertThat(metadataWithoutInheritance.isUndeletable()).isFalse();
	}

	@Test
	public void givenUndeletableFlagSetToFalseOnMetadataWithoutInheritanceWhenModifyingThenDeletable()
			throws Exception {
		metadataWithoutInheritanceBuilder.setType(STRING).setUndeletable(false);

		buildAndModify();

		assertThat(metadataWithoutInheritanceBuilder.isUndeletable()).isFalse();
	}

	@Test
	public void givenUndeletableFlagSetToTrueOnMetadataWithoutInheritanceWhenBuildingThenUndeletable()
			throws Exception {
		metadataWithoutInheritanceBuilder.setType(STRING).setUndeletable(true);

		build();

		assertThat(metadataWithoutInheritance.isUndeletable()).isTrue();
	}

	@Test
	public void givenUndeletableFlagSetToTrueOnMetadataWithoutInheritanceWhenModifyingThenUndeletable()
			throws Exception {
		metadataWithoutInheritanceBuilder.setType(STRING).setUndeletable(true);

		buildAndModify();

		assertThat(metadataWithoutInheritanceBuilder.isUndeletable()).isTrue();
	}

	@Test
	public void givenUndeletableFlagOnMetadataWithInheritanceWhenBuildingThenSetToInheritedValue()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING).setUndeletable(true);

		build();

		assertThat(metadataWithInheritance.isUndeletable()).isTrue();
	}

	@Test
	public void givenUndeletableFlagOnMetadataWithInheritanceWhenModifyingThenSetToInheritedValue()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING).setUndeletable(true);

		buildAndModify();

		assertThat(metadataWithInheritanceBuilder.isUndeletable()).isTrue();
	}

	@Test(expected = MetadataSchemaBuilderRuntimeException.CannotModifyAttributeOfInheritingMetadata.class)
	public void givenUndeletableFlagOnMetadataWithInheritanceWhenDefinedThenException()
			throws Exception {
		metadataWithInheritanceBuilder.setUndeletable(true);
	}

	@Test
	public void givenTrueFlagModifiedInInheritedMetadataBuilderThenModifiedInMetadataWithHeritance()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING).setUndeletable(true);
		assertThat(metadataWithInheritanceBuilder.isUndeletable()).isTrue();

	}

	@Test
	public void givenFalseFlagModifiedInInheritedMetadataBuilderThenModifiedInMetadataWithHeritance()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING).setUndeletable(false);
		assertThat(metadataWithInheritanceBuilder.isUndeletable()).isFalse();

	}
}
