package com.constellio.model.services.records;

import static com.constellio.model.entities.Language.Arabic;
import static com.constellio.model.entities.Language.English;
import static com.constellio.model.entities.Language.French;
import static com.constellio.model.entities.schemas.MetadataValueType.STRING;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.schemas.MetadataSchemaTypesConfigurator;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RecordServicesMultilingualAcceptanceTest extends ConstellioTest {

	RecordServicesTestSchemaSetup monolingualCollectionSchemas = new RecordServicesTestSchemaSetup("monolingual");
	RecordServicesTestSchemaSetup.ZeSchemaMetadatas monolingualCollectionSchema
			= monolingualCollectionSchemas.new ZeSchemaMetadatas();

	RecordServicesTestSchemaSetup
			multilingualCollectionSchemas = new RecordServicesTestSchemaSetup("multilingual");
	RecordServicesTestSchemaSetup.ZeSchemaMetadatas multilingualCollectionSchema
			= multilingualCollectionSchemas.new ZeSchemaMetadatas();

	@Test
	public void givenFrenchSystemWithAMultilingualCollectionsThenSchemasHaveValidLanguages()
			throws Exception {

		givenFrenchSystemOneMonolingualAndOneMultilingualCollection();
		assertThat(monolingualCollectionSchema.instance().getCollectionInfo().getMainSystemLanguage()).isEqualTo(French);
		assertThat(monolingualCollectionSchema.instance().getCollectionInfo().getCollectionLanguages())
				.containsOnly(French);

		assertThat(monolingualCollectionSchema.type().getCollectionInfo().getMainSystemLanguage()).isEqualTo(French);
		assertThat(monolingualCollectionSchema.type().getCollectionInfo().getCollectionLanguages())
				.containsOnly(French);

		assertThat(multilingualCollectionSchema.instance().getCollectionInfo().getMainSystemLanguage()).isEqualTo(French);
		assertThat(multilingualCollectionSchema.instance().getCollectionInfo().getCollectionLanguages())
				.containsOnly(French, English);

		assertThat(multilingualCollectionSchema.type().getCollectionInfo().getMainSystemLanguage()).isEqualTo(French);
		assertThat(multilingualCollectionSchema.type().getCollectionInfo().getCollectionLanguages())
				.containsOnly(French, English);

	}

	@Test
	public void givenEnglishSystemWithAMultilingualCollectionsThenSchemasHaveValidLanguages()
			throws Exception {

		givenEnglishSystemOneMonolingualAndOneTrilingualCollection();
		assertThat(monolingualCollectionSchema.instance().getCollectionInfo().getMainSystemLanguage()).isEqualTo(English);
		assertThat(monolingualCollectionSchema.instance().getCollectionInfo().getCollectionLanguages())
				.containsOnly(English);

		assertThat(monolingualCollectionSchema.type().getCollectionInfo().getMainSystemLanguage()).isEqualTo(English);
		assertThat(monolingualCollectionSchema.type().getCollectionInfo().getCollectionLanguages())
				.containsOnly(English);

		assertThat(multilingualCollectionSchema.instance().getCollectionInfo().getMainSystemLanguage()).isEqualTo(English);
		assertThat(multilingualCollectionSchema.instance().getCollectionInfo().getCollectionLanguages())
				.containsOnly(French, English, Arabic);

		assertThat(multilingualCollectionSchema.type().getCollectionInfo().getMainSystemLanguage()).isEqualTo(English);
		assertThat(multilingualCollectionSchema.type().getCollectionInfo().getCollectionLanguages())
				.containsOnly(French, English, Arabic);

	}

	@Test
	public void givenFrenchSystemWithAMultilingualCollectionsRecordWhenMetadatasHasMultilingualMetadatasThenValuesPersisted()
			throws Exception {

		givenFrenchSystemOneMonolingualAndOneMultilingualCollection(withAMultilingualStringMetadata);

		//TODO
	}

	//-----------------------------------------------------------------------------------------------------------------------

	private SetupAlteration withAMultilingualStringMetadata = new SetupAlteration() {
		@Override
		public void setupMonolingualCollection(MetadataSchemaTypesBuilder schemaTypes) {
			schemaTypes.getSchema("zeSchemaType_default").create("stringMetadata").setType(STRING).setMultiLingual(true);
		}

		@Override
		public void setupMultilingualCollection(MetadataSchemaTypesBuilder schemaTypes) {
			schemaTypes.getSchema("zeSchemaType_default").create("stringMetadata").setType(STRING).setMultiLingual(true);
		}

		@Override
		public void after() {

		}
	};

	private SetupAlteration withAMultilingualListStringMetadata = new SetupAlteration() {
		@Override
		public void setupMonolingualCollection(MetadataSchemaTypesBuilder schemaTypes) {
			schemaTypes.getSchema("zeSchemaType_default").create("stringMetadata").setType(STRING)
					.setMultiLingual(true).setMultivalue(true);
		}

		@Override
		public void setupMultilingualCollection(MetadataSchemaTypesBuilder schemaTypes) {
			schemaTypes.getSchema("zeSchemaType_default").create("stringMetadata").setType(STRING)
					.setMultiLingual(true).setMultivalue(true);
		}

		@Override
		public void after() {

		}
	};

	protected void givenFrenchSystemOneMonolingualAndOneMultilingualCollection(final SetupAlteration... setupAlterations) {
		givenSystemLanguageIs("fr");
		givenCollection("monolingual", asList("fr")).withAllTestUsers();
		givenCollection("multilingual", asList("fr", "en")).withAllTestUsers();
		defineSchemasManager().using(monolingualCollectionSchemas.with(new MetadataSchemaTypesConfigurator() {
			@Override
			public void configure(MetadataSchemaTypesBuilder schemaTypes) {
				for (SetupAlteration setupAlteration : setupAlterations) {
					setupAlteration.setupMonolingualCollection(schemaTypes);
				}
			}
		}));

		defineSchemasManager().using(multilingualCollectionSchemas.with(new MetadataSchemaTypesConfigurator() {
			@Override
			public void configure(MetadataSchemaTypesBuilder schemaTypes) {
				for (SetupAlteration setupAlteration : setupAlterations) {
					setupAlteration.setupMultilingualCollection(schemaTypes);
				}
			}
		}));
	}

	protected void givenFrenchSystemOneMonolingualAndOneTrilingualCollection(final SetupAlteration... setupAlterations) {
		givenSystemLanguageIs("fr");
		givenCollection("monolingual", asList("fr")).withAllTestUsers();
		givenCollection("multilingual", asList("fr", "en", "ar")).withAllTestUsers();
		defineSchemasManager().using(monolingualCollectionSchemas.with(new MetadataSchemaTypesConfigurator() {
			@Override
			public void configure(MetadataSchemaTypesBuilder schemaTypes) {
				for (SetupAlteration setupAlteration : setupAlterations) {
					setupAlteration.setupMonolingualCollection(schemaTypes);
				}
			}
		}));

		defineSchemasManager().using(multilingualCollectionSchemas.with(new MetadataSchemaTypesConfigurator() {
			@Override
			public void configure(MetadataSchemaTypesBuilder schemaTypes) {

				for (SetupAlteration setupAlteration : setupAlterations) {
					setupAlteration.setupMultilingualCollection(schemaTypes);
				}
			}
		}));
	}

	protected void givenEnglishSystemOneMonolingualAndOneTrilingualCollection(final SetupAlteration... setupAlterations) {
		givenSystemLanguageIs("en");
		givenCollection("monolingual", asList("en")).withAllTestUsers();
		givenCollection("multilingual", asList("fr", "en", "ar")).withAllTestUsers();
		defineSchemasManager().using(monolingualCollectionSchemas.with(new MetadataSchemaTypesConfigurator() {
			@Override
			public void configure(MetadataSchemaTypesBuilder schemaTypes) {
				for (SetupAlteration setupAlteration : setupAlterations) {
					setupAlteration.setupMonolingualCollection(schemaTypes);
				}
			}
		}));

		defineSchemasManager().using(multilingualCollectionSchemas.with(new MetadataSchemaTypesConfigurator() {
			@Override
			public void configure(MetadataSchemaTypesBuilder schemaTypes) {
				for (SetupAlteration setupAlteration : setupAlterations) {
					setupAlteration.setupMultilingualCollection(schemaTypes);
				}
			}
		}));
	}

	private interface SetupAlteration {
		void setupMonolingualCollection(MetadataSchemaTypesBuilder schemaTypes);

		void setupMultilingualCollection(MetadataSchemaTypesBuilder schemaTypes);

		void after();
	}
}
