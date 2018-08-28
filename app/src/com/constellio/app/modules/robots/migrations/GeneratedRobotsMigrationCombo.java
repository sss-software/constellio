package com.constellio.app.modules.robots.migrations;

import com.constellio.app.entities.modules.MigrationResourcesProvider;
import com.constellio.app.entities.schemasDisplay.SchemaTypesDisplayConfig;
import com.constellio.app.entities.schemasDisplay.enums.MetadataInputType;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.services.schemasDisplay.SchemaTypesDisplayTransactionBuilder;
import com.constellio.app.services.schemasDisplay.SchemasDisplayManager;
import com.constellio.app.ui.pages.search.criteria.CriterionFactory;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.services.schemas.builders.MetadataBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypeBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;
import com.constellio.model.services.schemas.calculators.AllAuthorizationsCalculator;
import com.constellio.model.services.schemas.calculators.AllReferencesCalculator;
import com.constellio.model.services.schemas.calculators.AllRemovedAuthsCalculator;
import com.constellio.model.services.schemas.calculators.AttachedAncestorsCalculator;
import com.constellio.model.services.schemas.calculators.AutocompleteFieldCalculator;
import com.constellio.model.services.schemas.calculators.DefaultTokensOfHierarchyCalculator;
import com.constellio.model.services.schemas.calculators.InheritedAuthorizationsCalculator;
import com.constellio.model.services.schemas.calculators.NonTaxonomyAuthorizationsCalculator;
import com.constellio.model.services.schemas.calculators.ParentPathCalculator;
import com.constellio.model.services.schemas.calculators.PathCalculator;
import com.constellio.model.services.schemas.calculators.PathPartsCalculator;
import com.constellio.model.services.schemas.calculators.PrincipalPathCalculator;
import com.constellio.model.services.schemas.calculators.TokensCalculator4;
import com.constellio.model.services.schemas.validators.ManualTokenValidator;
import com.constellio.model.services.security.roles.RolesManager;

import java.util.ArrayList;

import static java.util.Arrays.asList;

public final class GeneratedRobotsMigrationCombo {
	String collection;

	AppLayerFactory appLayerFactory;

	MigrationResourcesProvider resourcesProvider;

	GeneratedRobotsMigrationCombo(String collection, AppLayerFactory appLayerFactory,
								  MigrationResourcesProvider resourcesProvider) {
		this.collection = collection;
		this.appLayerFactory = appLayerFactory;
		this.resourcesProvider = resourcesProvider;
	}

	public void applyGeneratedSchemaAlteration(MetadataSchemaTypesBuilder typesBuilder) {
		MetadataSchemaTypeBuilder collectionSchemaType = typesBuilder.getSchemaType("collection");
		MetadataSchemaBuilder collectionSchema = collectionSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder groupSchemaType = typesBuilder.getSchemaType("group");
		MetadataSchemaBuilder groupSchema = groupSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder userSchemaType = typesBuilder.getSchemaType("user");
		MetadataSchemaBuilder userSchema = userSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder authorizationDetailsSchemaType = typesBuilder.getSchemaType("authorizationDetails");
		MetadataSchemaBuilder authorizationDetailsSchema = authorizationDetailsSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder capsuleSchemaType = typesBuilder.getSchemaType("capsule");
		MetadataSchemaBuilder capsuleSchema = capsuleSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder ddvCapsuleLanguageSchemaType = typesBuilder.getSchemaType("ddvCapsuleLanguage");
		MetadataSchemaBuilder ddvCapsuleLanguageSchema = ddvCapsuleLanguageSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder emailToSendSchemaType = typesBuilder.getSchemaType("emailToSend");
		MetadataSchemaBuilder emailToSendSchema = emailToSendSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder eventSchemaType = typesBuilder.getSchemaType("event");
		MetadataSchemaBuilder eventSchema = eventSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder facetSchemaType = typesBuilder.getSchemaType("facet");
		MetadataSchemaBuilder facet_fieldSchema = facetSchemaType.getCustomSchema("field");
		MetadataSchemaBuilder facet_querySchema = facetSchemaType.getCustomSchema("query");
		MetadataSchemaBuilder facetSchema = facetSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder printableSchemaType = typesBuilder.getSchemaType("printable");
		MetadataSchemaBuilder printableSchema = printableSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder reportSchemaType = typesBuilder.getSchemaType("report");
		MetadataSchemaBuilder reportSchema = reportSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder savedSearchSchemaType = typesBuilder.getSchemaType("savedSearch");
		MetadataSchemaBuilder savedSearchSchema = savedSearchSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder searchEventSchemaType = typesBuilder.getSchemaType("searchEvent");
		MetadataSchemaBuilder searchEventSchema = searchEventSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder taskSchemaType = typesBuilder.getSchemaType("task");
		MetadataSchemaBuilder task_approvalSchema = taskSchemaType.getCustomSchema("approval");
		MetadataSchemaBuilder taskSchema = taskSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder temporaryRecordSchemaType = typesBuilder.getSchemaType("temporaryRecord");
		MetadataSchemaBuilder temporaryRecord_batchProcessReportSchema = temporaryRecordSchemaType.getCustomSchema("batchProcessReport");
		MetadataSchemaBuilder temporaryRecord_exportAuditSchema = temporaryRecordSchemaType.getCustomSchema("exportAudit");
		MetadataSchemaBuilder temporaryRecord_importAuditSchema = temporaryRecordSchemaType.getCustomSchema("importAudit");
		MetadataSchemaBuilder temporaryRecord_scriptReportSchema = temporaryRecordSchemaType.getCustomSchema("scriptReport");
		MetadataSchemaBuilder temporaryRecord_vaultScanReportSchema = temporaryRecordSchemaType.getCustomSchema("vaultScanReport");
		MetadataSchemaBuilder temporaryRecordSchema = temporaryRecordSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder thesaurusConfigSchemaType = typesBuilder.getSchemaType("thesaurusConfig");
		MetadataSchemaBuilder thesaurusConfigSchema = thesaurusConfigSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder userDocumentSchemaType = typesBuilder.getSchemaType("userDocument");
		MetadataSchemaBuilder userDocumentSchema = userDocumentSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder userFolderSchemaType = typesBuilder.getSchemaType("userFolder");
		MetadataSchemaBuilder userFolderSchema = userFolderSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder actionParametersSchemaType = typesBuilder.createNewSchemaType("actionParameters", false);
		MetadataSchemaBuilder actionParametersSchema = actionParametersSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder robotSchemaType = typesBuilder.createNewSchemaType("robot", false);
		MetadataSchemaBuilder robotSchema = robotSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder robotLogSchemaType = typesBuilder.createNewSchemaType("robotLog", false);
		MetadataSchemaBuilder robotLogSchema = robotLogSchemaType.getDefaultSchema();
		createCollectionSchemaTypeMetadatas(typesBuilder, collectionSchemaType, collectionSchema);
		createGroupSchemaTypeMetadatas(typesBuilder, groupSchemaType, groupSchema);
		createUserSchemaTypeMetadatas(typesBuilder, userSchemaType, userSchema);
		createActionParametersSchemaTypeMetadatas(typesBuilder, actionParametersSchemaType, actionParametersSchema);
		createAuthorizationDetailsSchemaTypeMetadatas(typesBuilder, authorizationDetailsSchemaType, authorizationDetailsSchema);
		createCapsuleSchemaTypeMetadatas(typesBuilder, capsuleSchemaType, capsuleSchema);
		createDdvCapsuleLanguageSchemaTypeMetadatas(typesBuilder, ddvCapsuleLanguageSchemaType, ddvCapsuleLanguageSchema);
		createEmailToSendSchemaTypeMetadatas(typesBuilder, emailToSendSchemaType, emailToSendSchema);
		createEventSchemaTypeMetadatas(typesBuilder, eventSchemaType, eventSchema);
		createFacetSchemaTypeMetadatas(typesBuilder, facetSchemaType, facet_fieldSchema, facet_querySchema, facetSchema);
		createPrintableSchemaTypeMetadatas(typesBuilder, printableSchemaType, printableSchema);
		createReportSchemaTypeMetadatas(typesBuilder, reportSchemaType, reportSchema);
		createRobotSchemaTypeMetadatas(typesBuilder, robotSchemaType, robotSchema);
		createRobotLogSchemaTypeMetadatas(typesBuilder, robotLogSchemaType, robotLogSchema);
		createSavedSearchSchemaTypeMetadatas(typesBuilder, savedSearchSchemaType, savedSearchSchema);
		createSearchEventSchemaTypeMetadatas(typesBuilder, searchEventSchemaType, searchEventSchema);
		createTaskSchemaTypeMetadatas(typesBuilder, taskSchemaType, task_approvalSchema, taskSchema);
		createTemporaryRecordSchemaTypeMetadatas(typesBuilder, temporaryRecordSchemaType, temporaryRecord_batchProcessReportSchema, temporaryRecord_exportAuditSchema, temporaryRecord_importAuditSchema, temporaryRecord_scriptReportSchema, temporaryRecord_vaultScanReportSchema, temporaryRecordSchema);
		createThesaurusConfigSchemaTypeMetadatas(typesBuilder, thesaurusConfigSchemaType, thesaurusConfigSchema);
		createUserDocumentSchemaTypeMetadatas(typesBuilder, userDocumentSchemaType, userDocumentSchema);
		createUserFolderSchemaTypeMetadatas(typesBuilder, userFolderSchemaType, userFolderSchema);
		actionParametersSchema.get("allReferences").defineDataEntry().asCalculated(AllReferencesCalculator.class);
		actionParametersSchema.get("allRemovedAuths").defineDataEntry().asCalculated(AllRemovedAuthsCalculator.class);
		actionParametersSchema.get("allauthorizations").defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		actionParametersSchema.get("attachedAncestors").defineDataEntry().asCalculated(AttachedAncestorsCalculator.class);
		actionParametersSchema.get("autocomplete").defineDataEntry().asCalculated(AutocompleteFieldCalculator.class);
		actionParametersSchema.get("inheritedauthorizations").defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		actionParametersSchema.get("nonTaxonomyAuthorizations").defineDataEntry().asCalculated(NonTaxonomyAuthorizationsCalculator.class);
		actionParametersSchema.get("parentpath").defineDataEntry().asCalculated(ParentPathCalculator.class);
		actionParametersSchema.get("path").defineDataEntry().asCalculated(PathCalculator.class);
		actionParametersSchema.get("pathParts").defineDataEntry().asCalculated(PathPartsCalculator.class);
		actionParametersSchema.get("principalpath").defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		actionParametersSchema.get("tokens").defineDataEntry().asCalculated(TokensCalculator4.class);
		actionParametersSchema.get("tokensHierarchy").defineDataEntry().asCalculated(DefaultTokensOfHierarchyCalculator.class);
		robotSchema.get("allReferences").defineDataEntry().asCalculated(AllReferencesCalculator.class);
		robotSchema.get("allRemovedAuths").defineDataEntry().asCalculated(AllRemovedAuthsCalculator.class);
		robotSchema.get("allauthorizations").defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		robotSchema.get("attachedAncestors").defineDataEntry().asCalculated(AttachedAncestorsCalculator.class);
		robotSchema.get("autocomplete").defineDataEntry().asCalculated(AutocompleteFieldCalculator.class);
		robotSchema.get("inheritedauthorizations").defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		robotSchema.get("nonTaxonomyAuthorizations").defineDataEntry().asCalculated(NonTaxonomyAuthorizationsCalculator.class);
		robotSchema.get("parentpath").defineDataEntry().asCalculated(ParentPathCalculator.class);
		robotSchema.get("path").defineDataEntry().asCalculated(PathCalculator.class);
		robotSchema.get("pathParts").defineDataEntry().asCalculated(PathPartsCalculator.class);
		robotSchema.get("principalpath").defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		robotSchema.get("tokens").defineDataEntry().asCalculated(TokensCalculator4.class);
		robotSchema.get("tokensHierarchy").defineDataEntry().asCalculated(DefaultTokensOfHierarchyCalculator.class);
		robotLogSchema.get("allReferences").defineDataEntry().asCalculated(AllReferencesCalculator.class);
		robotLogSchema.get("allRemovedAuths").defineDataEntry().asCalculated(AllRemovedAuthsCalculator.class);
		robotLogSchema.get("allauthorizations").defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		robotLogSchema.get("attachedAncestors").defineDataEntry().asCalculated(AttachedAncestorsCalculator.class);
		robotLogSchema.get("autocomplete").defineDataEntry().asCalculated(AutocompleteFieldCalculator.class);
		robotLogSchema.get("inheritedauthorizations").defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		robotLogSchema.get("nonTaxonomyAuthorizations").defineDataEntry().asCalculated(NonTaxonomyAuthorizationsCalculator.class);
		robotLogSchema.get("parentpath").defineDataEntry().asCalculated(ParentPathCalculator.class);
		robotLogSchema.get("path").defineDataEntry().asCalculated(PathCalculator.class);
		robotLogSchema.get("pathParts").defineDataEntry().asCalculated(PathPartsCalculator.class);
		robotLogSchema.get("principalpath").defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		robotLogSchema.get("tokens").defineDataEntry().asCalculated(TokensCalculator4.class);
		robotLogSchema.get("tokensHierarchy").defineDataEntry().asCalculated(DefaultTokensOfHierarchyCalculator.class);
	}

	private void createCapsuleSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
												  MetadataSchemaTypeBuilder capsuleSchemaType,
												  MetadataSchemaBuilder capsuleSchema) {
	}

	private void createCollectionSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
													 MetadataSchemaTypeBuilder collectionSchemaType,
													 MetadataSchemaBuilder collectionSchema) {
	}

	private void createPrintableSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
													MetadataSchemaTypeBuilder printableSchemaType,
													MetadataSchemaBuilder printableSchema) {
	}

	private void createThesaurusConfigSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
														  MetadataSchemaTypeBuilder thesaurusConfigSchemaType,
														  MetadataSchemaBuilder thesaurusConfigSchema) {
	}

	private void createSavedSearchSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
													  MetadataSchemaTypeBuilder savedSearchSchemaType,
													  MetadataSchemaBuilder savedSearchSchema) {
	}

	private void createAuthorizationDetailsSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
															   MetadataSchemaTypeBuilder authorizationDetailsSchemaType,
															   MetadataSchemaBuilder authorizationDetailsSchema) {
	}

	private void createRobotSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
												MetadataSchemaTypeBuilder robotSchemaType,
												MetadataSchemaBuilder robotSchema) {
		MetadataBuilder robot_action = robotSchema.create("action").setType(MetadataValueType.STRING);
		robot_action.setUndeletable(true);
		robot_action.setEssential(true);
		MetadataBuilder robot_actionParameters = robotSchema.create("actionParameters").setType(MetadataValueType.REFERENCE);
		robot_actionParameters.setUndeletable(true);
		robot_actionParameters.setEssential(true);
		robot_actionParameters.defineReferencesTo(types.getSchemaType("actionParameters"));
		MetadataBuilder robot_allReferences = robotSchema.create("allReferences").setType(MetadataValueType.STRING);
		robot_allReferences.setMultivalue(true);
		robot_allReferences.setSystemReserved(true);
		robot_allReferences.setUndeletable(true);
		robot_allReferences.setMultiLingual(false);
		MetadataBuilder robot_allRemovedAuths = robotSchema.create("allRemovedAuths").setType(MetadataValueType.STRING);
		robot_allRemovedAuths.setMultivalue(true);
		robot_allRemovedAuths.setSystemReserved(true);
		robot_allRemovedAuths.setUndeletable(true);
		robot_allRemovedAuths.setEssential(true);
		robot_allRemovedAuths.setMultiLingual(false);
		MetadataBuilder robot_allauthorizations = robotSchema.create("allauthorizations").setType(MetadataValueType.STRING);
		robot_allauthorizations.setMultivalue(true);
		robot_allauthorizations.setSystemReserved(true);
		robot_allauthorizations.setUndeletable(true);
		robot_allauthorizations.setMultiLingual(false);
		MetadataBuilder robot_attachedAncestors = robotSchema.create("attachedAncestors").setType(MetadataValueType.STRING);
		robot_attachedAncestors.setMultivalue(true);
		robot_attachedAncestors.setSystemReserved(true);
		robot_attachedAncestors.setUndeletable(true);
		robot_attachedAncestors.setEssential(true);
		robot_attachedAncestors.setMultiLingual(false);
		MetadataBuilder robot_authorizations = robotSchema.create("authorizations").setType(MetadataValueType.STRING);
		robot_authorizations.setMultivalue(true);
		robot_authorizations.setSystemReserved(true);
		robot_authorizations.setUndeletable(true);
		robot_authorizations.setMultiLingual(false);
		MetadataBuilder robot_autoExecute = robotSchema.create("autoExecute").setType(MetadataValueType.BOOLEAN);
		robot_autoExecute.setDefaultRequirement(true);
		robot_autoExecute.setUndeletable(true);
		robot_autoExecute.setDefaultValue(false);
		MetadataBuilder robot_autocomplete = robotSchema.create("autocomplete").setType(MetadataValueType.STRING);
		robot_autocomplete.setMultivalue(true);
		robot_autocomplete.setSystemReserved(true);
		robot_autocomplete.setUndeletable(true);
		robot_autocomplete.setEssential(true);
		robot_autocomplete.setMultiLingual(true);
		MetadataBuilder robot_caption = robotSchema.create("caption").setType(MetadataValueType.STRING);
		robot_caption.setSystemReserved(true);
		robot_caption.setUndeletable(true);
		robot_caption.setMultiLingual(false);
		robot_caption.setSortable(true);
		MetadataBuilder robot_code = robotSchema.create("code").setType(MetadataValueType.STRING);
		robot_code.setDefaultRequirement(true);
		robot_code.setUndeletable(true);
		robot_code.setEssential(true);
		robot_code.setUniqueValue(true);
		MetadataBuilder robot_createdBy = robotSchema.create("createdBy").setType(MetadataValueType.REFERENCE);
		robot_createdBy.setSystemReserved(true);
		robot_createdBy.setUndeletable(true);
		robot_createdBy.setMultiLingual(false);
		robot_createdBy.defineReferencesTo(types.getSchemaType("user"));
		MetadataBuilder robot_createdOn = robotSchema.create("createdOn").setType(MetadataValueType.DATE_TIME);
		robot_createdOn.setSystemReserved(true);
		robot_createdOn.setUndeletable(true);
		robot_createdOn.setMultiLingual(false);
		robot_createdOn.setSortable(true);
		MetadataBuilder robot_deleted = robotSchema.create("deleted").setType(MetadataValueType.BOOLEAN);
		robot_deleted.setSystemReserved(true);
		robot_deleted.setUndeletable(true);
		robot_deleted.setMultiLingual(false);
		MetadataBuilder robot_denyTokens = robotSchema.create("denyTokens").setType(MetadataValueType.STRING);
		robot_denyTokens.setMultivalue(true);
		robot_denyTokens.setSystemReserved(true);
		robot_denyTokens.setUndeletable(true);
		robot_denyTokens.setMultiLingual(false);
		robot_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder robot_description = robotSchema.create("description").setType(MetadataValueType.TEXT);
		MetadataBuilder robot_detachedauthorizations = robotSchema.create("detachedauthorizations").setType(MetadataValueType.BOOLEAN);
		robot_detachedauthorizations.setSystemReserved(true);
		robot_detachedauthorizations.setUndeletable(true);
		robot_detachedauthorizations.setMultiLingual(false);
		MetadataBuilder robot_errorOnPhysicalDeletion = robotSchema.create("errorOnPhysicalDeletion").setType(MetadataValueType.BOOLEAN);
		robot_errorOnPhysicalDeletion.setSystemReserved(true);
		robot_errorOnPhysicalDeletion.setUndeletable(true);
		robot_errorOnPhysicalDeletion.setMultiLingual(false);
		MetadataBuilder robot_excludeProcessedByChildren = robotSchema.create("excludeProcessedByChildren").setType(MetadataValueType.BOOLEAN);
		robot_excludeProcessedByChildren.setDefaultRequirement(true);
		robot_excludeProcessedByChildren.setUndeletable(true);
		robot_excludeProcessedByChildren.setEssential(true);
		robot_excludeProcessedByChildren.setDefaultValue(false);
		MetadataBuilder robot_followers = robotSchema.create("followers").setType(MetadataValueType.STRING);
		robot_followers.setMultivalue(true);
		robot_followers.setSystemReserved(true);
		robot_followers.setUndeletable(true);
		robot_followers.setMultiLingual(false);
		robot_followers.setSearchable(true);
		MetadataBuilder robot_id = robotSchema.create("id").setType(MetadataValueType.STRING);
		robot_id.setDefaultRequirement(true);
		robot_id.setSystemReserved(true);
		robot_id.setUndeletable(true);
		robot_id.setMultiLingual(false);
		robot_id.setSearchable(true);
		robot_id.setSortable(true);
		robot_id.setUniqueValue(true);
		robot_id.setUnmodifiable(true);
		MetadataBuilder robot_inheritedauthorizations = robotSchema.create("inheritedauthorizations").setType(MetadataValueType.STRING);
		robot_inheritedauthorizations.setMultivalue(true);
		robot_inheritedauthorizations.setSystemReserved(true);
		robot_inheritedauthorizations.setUndeletable(true);
		robot_inheritedauthorizations.setMultiLingual(false);
		MetadataBuilder robot_legacyIdentifier = robotSchema.create("legacyIdentifier").setType(MetadataValueType.STRING);
		robot_legacyIdentifier.setDefaultRequirement(true);
		robot_legacyIdentifier.setSystemReserved(true);
		robot_legacyIdentifier.setUndeletable(true);
		robot_legacyIdentifier.setMultiLingual(false);
		robot_legacyIdentifier.setSearchable(true);
		robot_legacyIdentifier.setUniqueValue(true);
		robot_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder robot_logicallyDeletedOn = robotSchema.create("logicallyDeletedOn").setType(MetadataValueType.DATE_TIME);
		robot_logicallyDeletedOn.setSystemReserved(true);
		robot_logicallyDeletedOn.setUndeletable(true);
		robot_logicallyDeletedOn.setMultiLingual(false);
		MetadataBuilder robot_manualTokens = robotSchema.create("manualTokens").setType(MetadataValueType.STRING);
		robot_manualTokens.setMultivalue(true);
		robot_manualTokens.setSystemReserved(true);
		robot_manualTokens.setUndeletable(true);
		robot_manualTokens.setMultiLingual(false);
		robot_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder robot_markedForParsing = robotSchema.create("markedForParsing").setType(MetadataValueType.BOOLEAN);
		robot_markedForParsing.setSystemReserved(true);
		robot_markedForParsing.setUndeletable(true);
		robot_markedForParsing.setMultiLingual(false);
		MetadataBuilder robot_markedForPreviewConversion = robotSchema.create("markedForPreviewConversion").setType(MetadataValueType.BOOLEAN);
		robot_markedForPreviewConversion.setSystemReserved(true);
		robot_markedForPreviewConversion.setUndeletable(true);
		robot_markedForPreviewConversion.setMultiLingual(false);
		MetadataBuilder robot_markedForReindexing = robotSchema.create("markedForReindexing").setType(MetadataValueType.BOOLEAN);
		robot_markedForReindexing.setSystemReserved(true);
		robot_markedForReindexing.setUndeletable(true);
		robot_markedForReindexing.setMultiLingual(false);
		MetadataBuilder robot_migrationDataVersion = robotSchema.create("migrationDataVersion").setType(MetadataValueType.NUMBER);
		robot_migrationDataVersion.setSystemReserved(true);
		robot_migrationDataVersion.setUndeletable(true);
		robot_migrationDataVersion.setMultiLingual(false);
		robot_migrationDataVersion.setEssentialInSummary(true);
		MetadataBuilder robot_modifiedBy = robotSchema.create("modifiedBy").setType(MetadataValueType.REFERENCE);
		robot_modifiedBy.setSystemReserved(true);
		robot_modifiedBy.setUndeletable(true);
		robot_modifiedBy.setMultiLingual(false);
		robot_modifiedBy.defineReferencesTo(types.getSchemaType("user"));
		MetadataBuilder robot_modifiedOn = robotSchema.create("modifiedOn").setType(MetadataValueType.DATE_TIME);
		robot_modifiedOn.setSystemReserved(true);
		robot_modifiedOn.setUndeletable(true);
		robot_modifiedOn.setMultiLingual(false);
		robot_modifiedOn.setSortable(true);
		MetadataBuilder robot_nonTaxonomyAuthorizations = robotSchema.create("nonTaxonomyAuthorizations").setType(MetadataValueType.REFERENCE);
		robot_nonTaxonomyAuthorizations.setMultivalue(true);
		robot_nonTaxonomyAuthorizations.setSystemReserved(true);
		robot_nonTaxonomyAuthorizations.setUndeletable(true);
		robot_nonTaxonomyAuthorizations.setMultiLingual(false);
		robot_nonTaxonomyAuthorizations.defineReferencesTo(types.getSchemaType("authorizationDetails"));
		MetadataBuilder robot_parent = robotSchema.create("parent").setType(MetadataValueType.REFERENCE);
		robot_parent.setUndeletable(true);
		robot_parent.setEssential(true);
		robot_parent.defineChildOfRelationshipToType(types.getSchemaType("robot"));
		MetadataBuilder robot_parentpath = robotSchema.create("parentpath").setType(MetadataValueType.STRING);
		robot_parentpath.setMultivalue(true);
		robot_parentpath.setSystemReserved(true);
		robot_parentpath.setUndeletable(true);
		robot_parentpath.setMultiLingual(false);
		MetadataBuilder robot_path = robotSchema.create("path").setType(MetadataValueType.STRING);
		robot_path.setMultivalue(true);
		robot_path.setSystemReserved(true);
		robot_path.setUndeletable(true);
		robot_path.setMultiLingual(false);
		MetadataBuilder robot_pathParts = robotSchema.create("pathParts").setType(MetadataValueType.STRING);
		robot_pathParts.setMultivalue(true);
		robot_pathParts.setSystemReserved(true);
		robot_pathParts.setUndeletable(true);
		robot_pathParts.setMultiLingual(false);
		MetadataBuilder robot_principalpath = robotSchema.create("principalpath").setType(MetadataValueType.STRING);
		robot_principalpath.setSystemReserved(true);
		robot_principalpath.setUndeletable(true);
		robot_principalpath.setMultiLingual(false);
		MetadataBuilder robot_removedauthorizations = robotSchema.create("removedauthorizations").setType(MetadataValueType.STRING);
		robot_removedauthorizations.setMultivalue(true);
		robot_removedauthorizations.setSystemReserved(true);
		robot_removedauthorizations.setUndeletable(true);
		robot_removedauthorizations.setMultiLingual(false);
		MetadataBuilder robot_schema = robotSchema.create("schema").setType(MetadataValueType.STRING);
		robot_schema.setDefaultRequirement(true);
		robot_schema.setSystemReserved(true);
		robot_schema.setUndeletable(true);
		robot_schema.setMultiLingual(false);
		MetadataBuilder robot_schemaFilter = robotSchema.create("schemaFilter").setType(MetadataValueType.STRING);
		robot_schemaFilter.setDefaultRequirement(true);
		robot_schemaFilter.setUndeletable(true);
		robot_schemaFilter.setEssential(true);
		MetadataBuilder robot_searchCriteria = robotSchema.create("searchCriteria").setType(MetadataValueType.STRUCTURE);
		robot_searchCriteria.setMultivalue(true);
		robot_searchCriteria.setDefaultRequirement(true);
		robot_searchCriteria.setUndeletable(true);
		robot_searchCriteria.setEssential(true);
		robot_searchCriteria.defineStructureFactory(CriterionFactory.class);
		MetadataBuilder robot_searchable = robotSchema.create("searchable").setType(MetadataValueType.BOOLEAN);
		robot_searchable.setSystemReserved(true);
		robot_searchable.setUndeletable(true);
		robot_searchable.setMultiLingual(false);
		MetadataBuilder robot_shareDenyTokens = robotSchema.create("shareDenyTokens").setType(MetadataValueType.STRING);
		robot_shareDenyTokens.setMultivalue(true);
		robot_shareDenyTokens.setSystemReserved(true);
		robot_shareDenyTokens.setUndeletable(true);
		robot_shareDenyTokens.setMultiLingual(false);
		robot_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder robot_shareTokens = robotSchema.create("shareTokens").setType(MetadataValueType.STRING);
		robot_shareTokens.setMultivalue(true);
		robot_shareTokens.setSystemReserved(true);
		robot_shareTokens.setUndeletable(true);
		robot_shareTokens.setMultiLingual(false);
		robot_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder robot_title = robotSchema.create("title").setType(MetadataValueType.STRING);
		robot_title.setDefaultRequirement(true);
		robot_title.setUndeletable(true);
		robot_title.setMultiLingual(true);
		robot_title.setSchemaAutocomplete(true);
		robot_title.setSearchable(true);
		MetadataBuilder robot_tokens = robotSchema.create("tokens").setType(MetadataValueType.STRING);
		robot_tokens.setMultivalue(true);
		robot_tokens.setSystemReserved(true);
		robot_tokens.setUndeletable(true);
		robot_tokens.setMultiLingual(false);
		MetadataBuilder robot_tokensHierarchy = robotSchema.create("tokensHierarchy").setType(MetadataValueType.STRING);
		robot_tokensHierarchy.setMultivalue(true);
		robot_tokensHierarchy.setSystemReserved(true);
		robot_tokensHierarchy.setUndeletable(true);
		robot_tokensHierarchy.setMultiLingual(false);
		MetadataBuilder robot_visibleInTrees = robotSchema.create("visibleInTrees").setType(MetadataValueType.BOOLEAN);
		robot_visibleInTrees.setSystemReserved(true);
		robot_visibleInTrees.setUndeletable(true);
		robot_visibleInTrees.setMultiLingual(false);
	}

	private void createActionParametersSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
														   MetadataSchemaTypeBuilder actionParametersSchemaType,
														   MetadataSchemaBuilder actionParametersSchema) {
		MetadataBuilder actionParameters_allReferences = actionParametersSchema.create("allReferences").setType(MetadataValueType.STRING);
		actionParameters_allReferences.setMultivalue(true);
		actionParameters_allReferences.setSystemReserved(true);
		actionParameters_allReferences.setUndeletable(true);
		actionParameters_allReferences.setMultiLingual(false);
		MetadataBuilder actionParameters_allRemovedAuths = actionParametersSchema.create("allRemovedAuths").setType(MetadataValueType.STRING);
		actionParameters_allRemovedAuths.setMultivalue(true);
		actionParameters_allRemovedAuths.setSystemReserved(true);
		actionParameters_allRemovedAuths.setUndeletable(true);
		actionParameters_allRemovedAuths.setEssential(true);
		actionParameters_allRemovedAuths.setMultiLingual(false);
		MetadataBuilder actionParameters_allauthorizations = actionParametersSchema.create("allauthorizations").setType(MetadataValueType.STRING);
		actionParameters_allauthorizations.setMultivalue(true);
		actionParameters_allauthorizations.setSystemReserved(true);
		actionParameters_allauthorizations.setUndeletable(true);
		actionParameters_allauthorizations.setMultiLingual(false);
		MetadataBuilder actionParameters_attachedAncestors = actionParametersSchema.create("attachedAncestors").setType(MetadataValueType.STRING);
		actionParameters_attachedAncestors.setMultivalue(true);
		actionParameters_attachedAncestors.setSystemReserved(true);
		actionParameters_attachedAncestors.setUndeletable(true);
		actionParameters_attachedAncestors.setEssential(true);
		actionParameters_attachedAncestors.setMultiLingual(false);
		MetadataBuilder actionParameters_authorizations = actionParametersSchema.create("authorizations").setType(MetadataValueType.STRING);
		actionParameters_authorizations.setMultivalue(true);
		actionParameters_authorizations.setSystemReserved(true);
		actionParameters_authorizations.setUndeletable(true);
		actionParameters_authorizations.setMultiLingual(false);
		MetadataBuilder actionParameters_autocomplete = actionParametersSchema.create("autocomplete").setType(MetadataValueType.STRING);
		actionParameters_autocomplete.setMultivalue(true);
		actionParameters_autocomplete.setSystemReserved(true);
		actionParameters_autocomplete.setUndeletable(true);
		actionParameters_autocomplete.setEssential(true);
		actionParameters_autocomplete.setMultiLingual(true);
		MetadataBuilder actionParameters_caption = actionParametersSchema.create("caption").setType(MetadataValueType.STRING);
		actionParameters_caption.setSystemReserved(true);
		actionParameters_caption.setUndeletable(true);
		actionParameters_caption.setMultiLingual(false);
		actionParameters_caption.setSortable(true);
		MetadataBuilder actionParameters_createdBy = actionParametersSchema.create("createdBy").setType(MetadataValueType.REFERENCE);
		actionParameters_createdBy.setSystemReserved(true);
		actionParameters_createdBy.setUndeletable(true);
		actionParameters_createdBy.setMultiLingual(false);
		actionParameters_createdBy.defineReferencesTo(types.getSchemaType("user"));
		MetadataBuilder actionParameters_createdOn = actionParametersSchema.create("createdOn").setType(MetadataValueType.DATE_TIME);
		actionParameters_createdOn.setSystemReserved(true);
		actionParameters_createdOn.setUndeletable(true);
		actionParameters_createdOn.setMultiLingual(false);
		actionParameters_createdOn.setSortable(true);
		MetadataBuilder actionParameters_deleted = actionParametersSchema.create("deleted").setType(MetadataValueType.BOOLEAN);
		actionParameters_deleted.setSystemReserved(true);
		actionParameters_deleted.setUndeletable(true);
		actionParameters_deleted.setMultiLingual(false);
		MetadataBuilder actionParameters_denyTokens = actionParametersSchema.create("denyTokens").setType(MetadataValueType.STRING);
		actionParameters_denyTokens.setMultivalue(true);
		actionParameters_denyTokens.setSystemReserved(true);
		actionParameters_denyTokens.setUndeletable(true);
		actionParameters_denyTokens.setMultiLingual(false);
		actionParameters_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder actionParameters_detachedauthorizations = actionParametersSchema.create("detachedauthorizations").setType(MetadataValueType.BOOLEAN);
		actionParameters_detachedauthorizations.setSystemReserved(true);
		actionParameters_detachedauthorizations.setUndeletable(true);
		actionParameters_detachedauthorizations.setMultiLingual(false);
		MetadataBuilder actionParameters_errorOnPhysicalDeletion = actionParametersSchema.create("errorOnPhysicalDeletion").setType(MetadataValueType.BOOLEAN);
		actionParameters_errorOnPhysicalDeletion.setSystemReserved(true);
		actionParameters_errorOnPhysicalDeletion.setUndeletable(true);
		actionParameters_errorOnPhysicalDeletion.setMultiLingual(false);
		MetadataBuilder actionParameters_followers = actionParametersSchema.create("followers").setType(MetadataValueType.STRING);
		actionParameters_followers.setMultivalue(true);
		actionParameters_followers.setSystemReserved(true);
		actionParameters_followers.setUndeletable(true);
		actionParameters_followers.setMultiLingual(false);
		actionParameters_followers.setSearchable(true);
		MetadataBuilder actionParameters_id = actionParametersSchema.create("id").setType(MetadataValueType.STRING);
		actionParameters_id.setDefaultRequirement(true);
		actionParameters_id.setSystemReserved(true);
		actionParameters_id.setUndeletable(true);
		actionParameters_id.setMultiLingual(false);
		actionParameters_id.setSearchable(true);
		actionParameters_id.setSortable(true);
		actionParameters_id.setUniqueValue(true);
		actionParameters_id.setUnmodifiable(true);
		MetadataBuilder actionParameters_inheritedauthorizations = actionParametersSchema.create("inheritedauthorizations").setType(MetadataValueType.STRING);
		actionParameters_inheritedauthorizations.setMultivalue(true);
		actionParameters_inheritedauthorizations.setSystemReserved(true);
		actionParameters_inheritedauthorizations.setUndeletable(true);
		actionParameters_inheritedauthorizations.setMultiLingual(false);
		MetadataBuilder actionParameters_legacyIdentifier = actionParametersSchema.create("legacyIdentifier").setType(MetadataValueType.STRING);
		actionParameters_legacyIdentifier.setDefaultRequirement(true);
		actionParameters_legacyIdentifier.setSystemReserved(true);
		actionParameters_legacyIdentifier.setUndeletable(true);
		actionParameters_legacyIdentifier.setMultiLingual(false);
		actionParameters_legacyIdentifier.setSearchable(true);
		actionParameters_legacyIdentifier.setUniqueValue(true);
		actionParameters_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder actionParameters_logicallyDeletedOn = actionParametersSchema.create("logicallyDeletedOn").setType(MetadataValueType.DATE_TIME);
		actionParameters_logicallyDeletedOn.setSystemReserved(true);
		actionParameters_logicallyDeletedOn.setUndeletable(true);
		actionParameters_logicallyDeletedOn.setMultiLingual(false);
		MetadataBuilder actionParameters_manualTokens = actionParametersSchema.create("manualTokens").setType(MetadataValueType.STRING);
		actionParameters_manualTokens.setMultivalue(true);
		actionParameters_manualTokens.setSystemReserved(true);
		actionParameters_manualTokens.setUndeletable(true);
		actionParameters_manualTokens.setMultiLingual(false);
		actionParameters_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder actionParameters_markedForParsing = actionParametersSchema.create("markedForParsing").setType(MetadataValueType.BOOLEAN);
		actionParameters_markedForParsing.setSystemReserved(true);
		actionParameters_markedForParsing.setUndeletable(true);
		actionParameters_markedForParsing.setMultiLingual(false);
		MetadataBuilder actionParameters_markedForPreviewConversion = actionParametersSchema.create("markedForPreviewConversion").setType(MetadataValueType.BOOLEAN);
		actionParameters_markedForPreviewConversion.setSystemReserved(true);
		actionParameters_markedForPreviewConversion.setUndeletable(true);
		actionParameters_markedForPreviewConversion.setMultiLingual(false);
		MetadataBuilder actionParameters_markedForReindexing = actionParametersSchema.create("markedForReindexing").setType(MetadataValueType.BOOLEAN);
		actionParameters_markedForReindexing.setSystemReserved(true);
		actionParameters_markedForReindexing.setUndeletable(true);
		actionParameters_markedForReindexing.setMultiLingual(false);
		MetadataBuilder actionParameters_migrationDataVersion = actionParametersSchema.create("migrationDataVersion").setType(MetadataValueType.NUMBER);
		actionParameters_migrationDataVersion.setSystemReserved(true);
		actionParameters_migrationDataVersion.setUndeletable(true);
		actionParameters_migrationDataVersion.setMultiLingual(false);
		actionParameters_migrationDataVersion.setEssentialInSummary(true);
		MetadataBuilder actionParameters_modifiedBy = actionParametersSchema.create("modifiedBy").setType(MetadataValueType.REFERENCE);
		actionParameters_modifiedBy.setSystemReserved(true);
		actionParameters_modifiedBy.setUndeletable(true);
		actionParameters_modifiedBy.setMultiLingual(false);
		actionParameters_modifiedBy.defineReferencesTo(types.getSchemaType("user"));
		MetadataBuilder actionParameters_modifiedOn = actionParametersSchema.create("modifiedOn").setType(MetadataValueType.DATE_TIME);
		actionParameters_modifiedOn.setSystemReserved(true);
		actionParameters_modifiedOn.setUndeletable(true);
		actionParameters_modifiedOn.setMultiLingual(false);
		actionParameters_modifiedOn.setSortable(true);
		MetadataBuilder actionParameters_nonTaxonomyAuthorizations = actionParametersSchema.create("nonTaxonomyAuthorizations").setType(MetadataValueType.REFERENCE);
		actionParameters_nonTaxonomyAuthorizations.setMultivalue(true);
		actionParameters_nonTaxonomyAuthorizations.setSystemReserved(true);
		actionParameters_nonTaxonomyAuthorizations.setUndeletable(true);
		actionParameters_nonTaxonomyAuthorizations.setMultiLingual(false);
		actionParameters_nonTaxonomyAuthorizations.defineReferencesTo(types.getSchemaType("authorizationDetails"));
		MetadataBuilder actionParameters_parentpath = actionParametersSchema.create("parentpath").setType(MetadataValueType.STRING);
		actionParameters_parentpath.setMultivalue(true);
		actionParameters_parentpath.setSystemReserved(true);
		actionParameters_parentpath.setUndeletable(true);
		actionParameters_parentpath.setMultiLingual(false);
		MetadataBuilder actionParameters_path = actionParametersSchema.create("path").setType(MetadataValueType.STRING);
		actionParameters_path.setMultivalue(true);
		actionParameters_path.setSystemReserved(true);
		actionParameters_path.setUndeletable(true);
		actionParameters_path.setMultiLingual(false);
		MetadataBuilder actionParameters_pathParts = actionParametersSchema.create("pathParts").setType(MetadataValueType.STRING);
		actionParameters_pathParts.setMultivalue(true);
		actionParameters_pathParts.setSystemReserved(true);
		actionParameters_pathParts.setUndeletable(true);
		actionParameters_pathParts.setMultiLingual(false);
		MetadataBuilder actionParameters_principalpath = actionParametersSchema.create("principalpath").setType(MetadataValueType.STRING);
		actionParameters_principalpath.setSystemReserved(true);
		actionParameters_principalpath.setUndeletable(true);
		actionParameters_principalpath.setMultiLingual(false);
		MetadataBuilder actionParameters_removedauthorizations = actionParametersSchema.create("removedauthorizations").setType(MetadataValueType.STRING);
		actionParameters_removedauthorizations.setMultivalue(true);
		actionParameters_removedauthorizations.setSystemReserved(true);
		actionParameters_removedauthorizations.setUndeletable(true);
		actionParameters_removedauthorizations.setMultiLingual(false);
		MetadataBuilder actionParameters_schema = actionParametersSchema.create("schema").setType(MetadataValueType.STRING);
		actionParameters_schema.setDefaultRequirement(true);
		actionParameters_schema.setSystemReserved(true);
		actionParameters_schema.setUndeletable(true);
		actionParameters_schema.setMultiLingual(false);
		MetadataBuilder actionParameters_searchable = actionParametersSchema.create("searchable").setType(MetadataValueType.BOOLEAN);
		actionParameters_searchable.setSystemReserved(true);
		actionParameters_searchable.setUndeletable(true);
		actionParameters_searchable.setMultiLingual(false);
		MetadataBuilder actionParameters_shareDenyTokens = actionParametersSchema.create("shareDenyTokens").setType(MetadataValueType.STRING);
		actionParameters_shareDenyTokens.setMultivalue(true);
		actionParameters_shareDenyTokens.setSystemReserved(true);
		actionParameters_shareDenyTokens.setUndeletable(true);
		actionParameters_shareDenyTokens.setMultiLingual(false);
		actionParameters_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder actionParameters_shareTokens = actionParametersSchema.create("shareTokens").setType(MetadataValueType.STRING);
		actionParameters_shareTokens.setMultivalue(true);
		actionParameters_shareTokens.setSystemReserved(true);
		actionParameters_shareTokens.setUndeletable(true);
		actionParameters_shareTokens.setMultiLingual(false);
		actionParameters_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder actionParameters_title = actionParametersSchema.create("title").setType(MetadataValueType.STRING);
		actionParameters_title.setUndeletable(true);
		actionParameters_title.setEnabled(false);
		actionParameters_title.setMultiLingual(false);
		actionParameters_title.setSchemaAutocomplete(true);
		actionParameters_title.setSearchable(true);
		MetadataBuilder actionParameters_tokens = actionParametersSchema.create("tokens").setType(MetadataValueType.STRING);
		actionParameters_tokens.setMultivalue(true);
		actionParameters_tokens.setSystemReserved(true);
		actionParameters_tokens.setUndeletable(true);
		actionParameters_tokens.setMultiLingual(false);
		MetadataBuilder actionParameters_tokensHierarchy = actionParametersSchema.create("tokensHierarchy").setType(MetadataValueType.STRING);
		actionParameters_tokensHierarchy.setMultivalue(true);
		actionParameters_tokensHierarchy.setSystemReserved(true);
		actionParameters_tokensHierarchy.setUndeletable(true);
		actionParameters_tokensHierarchy.setMultiLingual(false);
		MetadataBuilder actionParameters_visibleInTrees = actionParametersSchema.create("visibleInTrees").setType(MetadataValueType.BOOLEAN);
		actionParameters_visibleInTrees.setSystemReserved(true);
		actionParameters_visibleInTrees.setUndeletable(true);
		actionParameters_visibleInTrees.setMultiLingual(false);
	}

	private void createUserDocumentSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
													   MetadataSchemaTypeBuilder userDocumentSchemaType,
													   MetadataSchemaBuilder userDocumentSchema) {
	}

	private void createTaskSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
											   MetadataSchemaTypeBuilder taskSchemaType,
											   MetadataSchemaBuilder task_approvalSchema,
											   MetadataSchemaBuilder taskSchema) {
		MetadataBuilder task_approval_allReferences = task_approvalSchema.get("allReferences");
		MetadataBuilder task_approval_allRemovedAuths = task_approvalSchema.get("allRemovedAuths");
		MetadataBuilder task_approval_allauthorizations = task_approvalSchema.get("allauthorizations");
		MetadataBuilder task_approval_assignCandidates = task_approvalSchema.get("assignCandidates");
		MetadataBuilder task_approval_assignedOn = task_approvalSchema.get("assignedOn");
		MetadataBuilder task_approval_assignedTo = task_approvalSchema.get("assignedTo");
		MetadataBuilder task_approval_attachedAncestors = task_approvalSchema.get("attachedAncestors");
		MetadataBuilder task_approval_authorizations = task_approvalSchema.get("authorizations");
		MetadataBuilder task_approval_autocomplete = task_approvalSchema.get("autocomplete");
		MetadataBuilder task_approval_caption = task_approvalSchema.get("caption");
		MetadataBuilder task_approval_createdBy = task_approvalSchema.get("createdBy");
		MetadataBuilder task_approval_createdOn = task_approvalSchema.get("createdOn");
		MetadataBuilder task_approval_deleted = task_approvalSchema.get("deleted");
		MetadataBuilder task_approval_denyTokens = task_approvalSchema.get("denyTokens");
		MetadataBuilder task_approval_detachedauthorizations = task_approvalSchema.get("detachedauthorizations");
		MetadataBuilder task_approval_dueDate = task_approvalSchema.get("dueDate");
		MetadataBuilder task_approval_errorOnPhysicalDeletion = task_approvalSchema.get("errorOnPhysicalDeletion");
		MetadataBuilder task_approval_finishedBy = task_approvalSchema.get("finishedBy");
		MetadataBuilder task_approval_finishedOn = task_approvalSchema.get("finishedOn");
		MetadataBuilder task_approval_followers = task_approvalSchema.get("followers");
		MetadataBuilder task_approval_id = task_approvalSchema.get("id");
		MetadataBuilder task_approval_inheritedauthorizations = task_approvalSchema.get("inheritedauthorizations");
		MetadataBuilder task_approval_legacyIdentifier = task_approvalSchema.get("legacyIdentifier");
		MetadataBuilder task_approval_logicallyDeletedOn = task_approvalSchema.get("logicallyDeletedOn");
		MetadataBuilder task_approval_manualTokens = task_approvalSchema.get("manualTokens");
		MetadataBuilder task_approval_markedForParsing = task_approvalSchema.get("markedForParsing");
		MetadataBuilder task_approval_markedForPreviewConversion = task_approvalSchema.get("markedForPreviewConversion");
		MetadataBuilder task_approval_markedForReindexing = task_approvalSchema.get("markedForReindexing");
		MetadataBuilder task_approval_migrationDataVersion = task_approvalSchema.get("migrationDataVersion");
		MetadataBuilder task_approval_modifiedBy = task_approvalSchema.get("modifiedBy");
		MetadataBuilder task_approval_modifiedOn = task_approvalSchema.get("modifiedOn");
		MetadataBuilder task_approval_nonTaxonomyAuthorizations = task_approvalSchema.get("nonTaxonomyAuthorizations");
		MetadataBuilder task_approval_parentpath = task_approvalSchema.get("parentpath");
		MetadataBuilder task_approval_path = task_approvalSchema.get("path");
		MetadataBuilder task_approval_pathParts = task_approvalSchema.get("pathParts");
		MetadataBuilder task_approval_principalpath = task_approvalSchema.get("principalpath");
		MetadataBuilder task_approval_removedauthorizations = task_approvalSchema.get("removedauthorizations");
		MetadataBuilder task_approval_schema = task_approvalSchema.get("schema");
		MetadataBuilder task_approval_searchable = task_approvalSchema.get("searchable");
		MetadataBuilder task_approval_shareDenyTokens = task_approvalSchema.get("shareDenyTokens");
		MetadataBuilder task_approval_shareTokens = task_approvalSchema.get("shareTokens");
		MetadataBuilder task_approval_title = task_approvalSchema.get("title");
		MetadataBuilder task_approval_tokens = task_approvalSchema.get("tokens");
		MetadataBuilder task_approval_tokensHierarchy = task_approvalSchema.get("tokensHierarchy");
		MetadataBuilder task_approval_visibleInTrees = task_approvalSchema.get("visibleInTrees");
		MetadataBuilder task_approval_workflowIdentifier = task_approvalSchema.get("workflowIdentifier");
		MetadataBuilder task_approval_workflowRecordIdentifiers = task_approvalSchema.get("workflowRecordIdentifiers");
	}

	private void createUserFolderSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
													 MetadataSchemaTypeBuilder userFolderSchemaType,
													 MetadataSchemaBuilder userFolderSchema) {
	}

	private void createDdvCapsuleLanguageSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
															 MetadataSchemaTypeBuilder ddvCapsuleLanguageSchemaType,
															 MetadataSchemaBuilder ddvCapsuleLanguageSchema) {
	}

	private void createReportSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
												 MetadataSchemaTypeBuilder reportSchemaType,
												 MetadataSchemaBuilder reportSchema) {
	}

	private void createEmailToSendSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
													  MetadataSchemaTypeBuilder emailToSendSchemaType,
													  MetadataSchemaBuilder emailToSendSchema) {
	}

	private void createEventSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
												MetadataSchemaTypeBuilder eventSchemaType,
												MetadataSchemaBuilder eventSchema) {
	}

	private void createRobotLogSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
												   MetadataSchemaTypeBuilder robotLogSchemaType,
												   MetadataSchemaBuilder robotLogSchema) {
		MetadataBuilder robotLog_allReferences = robotLogSchema.create("allReferences").setType(MetadataValueType.STRING);
		robotLog_allReferences.setMultivalue(true);
		robotLog_allReferences.setSystemReserved(true);
		robotLog_allReferences.setUndeletable(true);
		robotLog_allReferences.setMultiLingual(false);
		MetadataBuilder robotLog_allRemovedAuths = robotLogSchema.create("allRemovedAuths").setType(MetadataValueType.STRING);
		robotLog_allRemovedAuths.setMultivalue(true);
		robotLog_allRemovedAuths.setSystemReserved(true);
		robotLog_allRemovedAuths.setUndeletable(true);
		robotLog_allRemovedAuths.setEssential(true);
		robotLog_allRemovedAuths.setMultiLingual(false);
		MetadataBuilder robotLog_allauthorizations = robotLogSchema.create("allauthorizations").setType(MetadataValueType.STRING);
		robotLog_allauthorizations.setMultivalue(true);
		robotLog_allauthorizations.setSystemReserved(true);
		robotLog_allauthorizations.setUndeletable(true);
		robotLog_allauthorizations.setMultiLingual(false);
		MetadataBuilder robotLog_attachedAncestors = robotLogSchema.create("attachedAncestors").setType(MetadataValueType.STRING);
		robotLog_attachedAncestors.setMultivalue(true);
		robotLog_attachedAncestors.setSystemReserved(true);
		robotLog_attachedAncestors.setUndeletable(true);
		robotLog_attachedAncestors.setEssential(true);
		robotLog_attachedAncestors.setMultiLingual(false);
		MetadataBuilder robotLog_authorizations = robotLogSchema.create("authorizations").setType(MetadataValueType.STRING);
		robotLog_authorizations.setMultivalue(true);
		robotLog_authorizations.setSystemReserved(true);
		robotLog_authorizations.setUndeletable(true);
		robotLog_authorizations.setMultiLingual(false);
		MetadataBuilder robotLog_autocomplete = robotLogSchema.create("autocomplete").setType(MetadataValueType.STRING);
		robotLog_autocomplete.setMultivalue(true);
		robotLog_autocomplete.setSystemReserved(true);
		robotLog_autocomplete.setUndeletable(true);
		robotLog_autocomplete.setEssential(true);
		robotLog_autocomplete.setMultiLingual(true);
		MetadataBuilder robotLog_caption = robotLogSchema.create("caption").setType(MetadataValueType.STRING);
		robotLog_caption.setSystemReserved(true);
		robotLog_caption.setUndeletable(true);
		robotLog_caption.setMultiLingual(false);
		robotLog_caption.setSortable(true);
		MetadataBuilder robotLog_count = robotLogSchema.create("count").setType(MetadataValueType.NUMBER);
		robotLog_count.setUndeletable(true);
		MetadataBuilder robotLog_createdBy = robotLogSchema.create("createdBy").setType(MetadataValueType.REFERENCE);
		robotLog_createdBy.setSystemReserved(true);
		robotLog_createdBy.setUndeletable(true);
		robotLog_createdBy.setMultiLingual(false);
		robotLog_createdBy.defineReferencesTo(types.getSchemaType("user"));
		MetadataBuilder robotLog_createdOn = robotLogSchema.create("createdOn").setType(MetadataValueType.DATE_TIME);
		robotLog_createdOn.setSystemReserved(true);
		robotLog_createdOn.setUndeletable(true);
		robotLog_createdOn.setMultiLingual(false);
		robotLog_createdOn.setSortable(true);
		MetadataBuilder robotLog_deleted = robotLogSchema.create("deleted").setType(MetadataValueType.BOOLEAN);
		robotLog_deleted.setSystemReserved(true);
		robotLog_deleted.setUndeletable(true);
		robotLog_deleted.setMultiLingual(false);
		MetadataBuilder robotLog_denyTokens = robotLogSchema.create("denyTokens").setType(MetadataValueType.STRING);
		robotLog_denyTokens.setMultivalue(true);
		robotLog_denyTokens.setSystemReserved(true);
		robotLog_denyTokens.setUndeletable(true);
		robotLog_denyTokens.setMultiLingual(false);
		robotLog_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder robotLog_detachedauthorizations = robotLogSchema.create("detachedauthorizations").setType(MetadataValueType.BOOLEAN);
		robotLog_detachedauthorizations.setSystemReserved(true);
		robotLog_detachedauthorizations.setUndeletable(true);
		robotLog_detachedauthorizations.setMultiLingual(false);
		MetadataBuilder robotLog_errorOnPhysicalDeletion = robotLogSchema.create("errorOnPhysicalDeletion").setType(MetadataValueType.BOOLEAN);
		robotLog_errorOnPhysicalDeletion.setSystemReserved(true);
		robotLog_errorOnPhysicalDeletion.setUndeletable(true);
		robotLog_errorOnPhysicalDeletion.setMultiLingual(false);
		MetadataBuilder robotLog_followers = robotLogSchema.create("followers").setType(MetadataValueType.STRING);
		robotLog_followers.setMultivalue(true);
		robotLog_followers.setSystemReserved(true);
		robotLog_followers.setUndeletable(true);
		robotLog_followers.setMultiLingual(false);
		robotLog_followers.setSearchable(true);
		MetadataBuilder robotLog_id = robotLogSchema.create("id").setType(MetadataValueType.STRING);
		robotLog_id.setDefaultRequirement(true);
		robotLog_id.setSystemReserved(true);
		robotLog_id.setUndeletable(true);
		robotLog_id.setMultiLingual(false);
		robotLog_id.setSearchable(true);
		robotLog_id.setSortable(true);
		robotLog_id.setUniqueValue(true);
		robotLog_id.setUnmodifiable(true);
		MetadataBuilder robotLog_inheritedauthorizations = robotLogSchema.create("inheritedauthorizations").setType(MetadataValueType.STRING);
		robotLog_inheritedauthorizations.setMultivalue(true);
		robotLog_inheritedauthorizations.setSystemReserved(true);
		robotLog_inheritedauthorizations.setUndeletable(true);
		robotLog_inheritedauthorizations.setMultiLingual(false);
		MetadataBuilder robotLog_legacyIdentifier = robotLogSchema.create("legacyIdentifier").setType(MetadataValueType.STRING);
		robotLog_legacyIdentifier.setDefaultRequirement(true);
		robotLog_legacyIdentifier.setSystemReserved(true);
		robotLog_legacyIdentifier.setUndeletable(true);
		robotLog_legacyIdentifier.setMultiLingual(false);
		robotLog_legacyIdentifier.setSearchable(true);
		robotLog_legacyIdentifier.setUniqueValue(true);
		robotLog_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder robotLog_logicallyDeletedOn = robotLogSchema.create("logicallyDeletedOn").setType(MetadataValueType.DATE_TIME);
		robotLog_logicallyDeletedOn.setSystemReserved(true);
		robotLog_logicallyDeletedOn.setUndeletable(true);
		robotLog_logicallyDeletedOn.setMultiLingual(false);
		MetadataBuilder robotLog_manualTokens = robotLogSchema.create("manualTokens").setType(MetadataValueType.STRING);
		robotLog_manualTokens.setMultivalue(true);
		robotLog_manualTokens.setSystemReserved(true);
		robotLog_manualTokens.setUndeletable(true);
		robotLog_manualTokens.setMultiLingual(false);
		robotLog_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder robotLog_markedForParsing = robotLogSchema.create("markedForParsing").setType(MetadataValueType.BOOLEAN);
		robotLog_markedForParsing.setSystemReserved(true);
		robotLog_markedForParsing.setUndeletable(true);
		robotLog_markedForParsing.setMultiLingual(false);
		MetadataBuilder robotLog_markedForPreviewConversion = robotLogSchema.create("markedForPreviewConversion").setType(MetadataValueType.BOOLEAN);
		robotLog_markedForPreviewConversion.setSystemReserved(true);
		robotLog_markedForPreviewConversion.setUndeletable(true);
		robotLog_markedForPreviewConversion.setMultiLingual(false);
		MetadataBuilder robotLog_markedForReindexing = robotLogSchema.create("markedForReindexing").setType(MetadataValueType.BOOLEAN);
		robotLog_markedForReindexing.setSystemReserved(true);
		robotLog_markedForReindexing.setUndeletable(true);
		robotLog_markedForReindexing.setMultiLingual(false);
		MetadataBuilder robotLog_migrationDataVersion = robotLogSchema.create("migrationDataVersion").setType(MetadataValueType.NUMBER);
		robotLog_migrationDataVersion.setSystemReserved(true);
		robotLog_migrationDataVersion.setUndeletable(true);
		robotLog_migrationDataVersion.setMultiLingual(false);
		robotLog_migrationDataVersion.setEssentialInSummary(true);
		MetadataBuilder robotLog_modifiedBy = robotLogSchema.create("modifiedBy").setType(MetadataValueType.REFERENCE);
		robotLog_modifiedBy.setSystemReserved(true);
		robotLog_modifiedBy.setUndeletable(true);
		robotLog_modifiedBy.setMultiLingual(false);
		robotLog_modifiedBy.defineReferencesTo(types.getSchemaType("user"));
		MetadataBuilder robotLog_modifiedOn = robotLogSchema.create("modifiedOn").setType(MetadataValueType.DATE_TIME);
		robotLog_modifiedOn.setSystemReserved(true);
		robotLog_modifiedOn.setUndeletable(true);
		robotLog_modifiedOn.setMultiLingual(false);
		robotLog_modifiedOn.setSortable(true);
		MetadataBuilder robotLog_nonTaxonomyAuthorizations = robotLogSchema.create("nonTaxonomyAuthorizations").setType(MetadataValueType.REFERENCE);
		robotLog_nonTaxonomyAuthorizations.setMultivalue(true);
		robotLog_nonTaxonomyAuthorizations.setSystemReserved(true);
		robotLog_nonTaxonomyAuthorizations.setUndeletable(true);
		robotLog_nonTaxonomyAuthorizations.setMultiLingual(false);
		robotLog_nonTaxonomyAuthorizations.defineReferencesTo(types.getSchemaType("authorizationDetails"));
		MetadataBuilder robotLog_parentpath = robotLogSchema.create("parentpath").setType(MetadataValueType.STRING);
		robotLog_parentpath.setMultivalue(true);
		robotLog_parentpath.setSystemReserved(true);
		robotLog_parentpath.setUndeletable(true);
		robotLog_parentpath.setMultiLingual(false);
		MetadataBuilder robotLog_path = robotLogSchema.create("path").setType(MetadataValueType.STRING);
		robotLog_path.setMultivalue(true);
		robotLog_path.setSystemReserved(true);
		robotLog_path.setUndeletable(true);
		robotLog_path.setMultiLingual(false);
		MetadataBuilder robotLog_pathParts = robotLogSchema.create("pathParts").setType(MetadataValueType.STRING);
		robotLog_pathParts.setMultivalue(true);
		robotLog_pathParts.setSystemReserved(true);
		robotLog_pathParts.setUndeletable(true);
		robotLog_pathParts.setMultiLingual(false);
		MetadataBuilder robotLog_principalpath = robotLogSchema.create("principalpath").setType(MetadataValueType.STRING);
		robotLog_principalpath.setSystemReserved(true);
		robotLog_principalpath.setUndeletable(true);
		robotLog_principalpath.setMultiLingual(false);
		MetadataBuilder robotLog_removedauthorizations = robotLogSchema.create("removedauthorizations").setType(MetadataValueType.STRING);
		robotLog_removedauthorizations.setMultivalue(true);
		robotLog_removedauthorizations.setSystemReserved(true);
		robotLog_removedauthorizations.setUndeletable(true);
		robotLog_removedauthorizations.setMultiLingual(false);
		MetadataBuilder robotLog_robot = robotLogSchema.create("robot").setType(MetadataValueType.REFERENCE);
		robotLog_robot.setDefaultRequirement(true);
		robotLog_robot.setUndeletable(true);
		robotLog_robot.setEssential(true);
		robotLog_robot.defineReferencesTo(types.getSchemaType("robot"));
		MetadataBuilder robotLog_schema = robotLogSchema.create("schema").setType(MetadataValueType.STRING);
		robotLog_schema.setDefaultRequirement(true);
		robotLog_schema.setSystemReserved(true);
		robotLog_schema.setUndeletable(true);
		robotLog_schema.setMultiLingual(false);
		MetadataBuilder robotLog_searchable = robotLogSchema.create("searchable").setType(MetadataValueType.BOOLEAN);
		robotLog_searchable.setSystemReserved(true);
		robotLog_searchable.setUndeletable(true);
		robotLog_searchable.setMultiLingual(false);
		MetadataBuilder robotLog_shareDenyTokens = robotLogSchema.create("shareDenyTokens").setType(MetadataValueType.STRING);
		robotLog_shareDenyTokens.setMultivalue(true);
		robotLog_shareDenyTokens.setSystemReserved(true);
		robotLog_shareDenyTokens.setUndeletable(true);
		robotLog_shareDenyTokens.setMultiLingual(false);
		robotLog_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder robotLog_shareTokens = robotLogSchema.create("shareTokens").setType(MetadataValueType.STRING);
		robotLog_shareTokens.setMultivalue(true);
		robotLog_shareTokens.setSystemReserved(true);
		robotLog_shareTokens.setUndeletable(true);
		robotLog_shareTokens.setMultiLingual(false);
		robotLog_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder robotLog_title = robotLogSchema.create("title").setType(MetadataValueType.STRING);
		robotLog_title.setUndeletable(true);
		robotLog_title.setMultiLingual(false);
		robotLog_title.setSchemaAutocomplete(true);
		robotLog_title.setSearchable(true);
		MetadataBuilder robotLog_tokens = robotLogSchema.create("tokens").setType(MetadataValueType.STRING);
		robotLog_tokens.setMultivalue(true);
		robotLog_tokens.setSystemReserved(true);
		robotLog_tokens.setUndeletable(true);
		robotLog_tokens.setMultiLingual(false);
		MetadataBuilder robotLog_tokensHierarchy = robotLogSchema.create("tokensHierarchy").setType(MetadataValueType.STRING);
		robotLog_tokensHierarchy.setMultivalue(true);
		robotLog_tokensHierarchy.setSystemReserved(true);
		robotLog_tokensHierarchy.setUndeletable(true);
		robotLog_tokensHierarchy.setMultiLingual(false);
		MetadataBuilder robotLog_visibleInTrees = robotLogSchema.create("visibleInTrees").setType(MetadataValueType.BOOLEAN);
		robotLog_visibleInTrees.setSystemReserved(true);
		robotLog_visibleInTrees.setUndeletable(true);
		robotLog_visibleInTrees.setMultiLingual(false);
	}

	private void createSearchEventSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
													  MetadataSchemaTypeBuilder searchEventSchemaType,
													  MetadataSchemaBuilder searchEventSchema) {
	}

	private void createTemporaryRecordSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
														  MetadataSchemaTypeBuilder temporaryRecordSchemaType,
														  MetadataSchemaBuilder temporaryRecord_batchProcessReportSchema,
														  MetadataSchemaBuilder temporaryRecord_exportAuditSchema,
														  MetadataSchemaBuilder temporaryRecord_importAuditSchema,
														  MetadataSchemaBuilder temporaryRecord_scriptReportSchema,
														  MetadataSchemaBuilder temporaryRecord_vaultScanReportSchema,
														  MetadataSchemaBuilder temporaryRecordSchema) {
		MetadataBuilder temporaryRecord_batchProcessReport_allReferences = temporaryRecord_batchProcessReportSchema.get("allReferences");
		MetadataBuilder temporaryRecord_batchProcessReport_allRemovedAuths = temporaryRecord_batchProcessReportSchema.get("allRemovedAuths");
		MetadataBuilder temporaryRecord_batchProcessReport_allauthorizations = temporaryRecord_batchProcessReportSchema.get("allauthorizations");
		MetadataBuilder temporaryRecord_batchProcessReport_attachedAncestors = temporaryRecord_batchProcessReportSchema.get("attachedAncestors");
		MetadataBuilder temporaryRecord_batchProcessReport_authorizations = temporaryRecord_batchProcessReportSchema.get("authorizations");
		MetadataBuilder temporaryRecord_batchProcessReport_autocomplete = temporaryRecord_batchProcessReportSchema.get("autocomplete");
		MetadataBuilder temporaryRecord_batchProcessReport_caption = temporaryRecord_batchProcessReportSchema.get("caption");
		MetadataBuilder temporaryRecord_batchProcessReport_content = temporaryRecord_batchProcessReportSchema.get("content");
		MetadataBuilder temporaryRecord_batchProcessReport_createdBy = temporaryRecord_batchProcessReportSchema.get("createdBy");
		MetadataBuilder temporaryRecord_batchProcessReport_createdOn = temporaryRecord_batchProcessReportSchema.get("createdOn");
		MetadataBuilder temporaryRecord_batchProcessReport_daysBeforeDestruction = temporaryRecord_batchProcessReportSchema.get("daysBeforeDestruction");
		MetadataBuilder temporaryRecord_batchProcessReport_deleted = temporaryRecord_batchProcessReportSchema.get("deleted");
		MetadataBuilder temporaryRecord_batchProcessReport_denyTokens = temporaryRecord_batchProcessReportSchema.get("denyTokens");
		MetadataBuilder temporaryRecord_batchProcessReport_destructionDate = temporaryRecord_batchProcessReportSchema.get("destructionDate");
		MetadataBuilder temporaryRecord_batchProcessReport_detachedauthorizations = temporaryRecord_batchProcessReportSchema.get("detachedauthorizations");
		MetadataBuilder temporaryRecord_batchProcessReport_errorOnPhysicalDeletion = temporaryRecord_batchProcessReportSchema.get("errorOnPhysicalDeletion");
		MetadataBuilder temporaryRecord_batchProcessReport_followers = temporaryRecord_batchProcessReportSchema.get("followers");
		MetadataBuilder temporaryRecord_batchProcessReport_id = temporaryRecord_batchProcessReportSchema.get("id");
		MetadataBuilder temporaryRecord_batchProcessReport_inheritedauthorizations = temporaryRecord_batchProcessReportSchema.get("inheritedauthorizations");
		MetadataBuilder temporaryRecord_batchProcessReport_legacyIdentifier = temporaryRecord_batchProcessReportSchema.get("legacyIdentifier");
		MetadataBuilder temporaryRecord_batchProcessReport_logicallyDeletedOn = temporaryRecord_batchProcessReportSchema.get("logicallyDeletedOn");
		MetadataBuilder temporaryRecord_batchProcessReport_manualTokens = temporaryRecord_batchProcessReportSchema.get("manualTokens");
		MetadataBuilder temporaryRecord_batchProcessReport_markedForParsing = temporaryRecord_batchProcessReportSchema.get("markedForParsing");
		MetadataBuilder temporaryRecord_batchProcessReport_markedForPreviewConversion = temporaryRecord_batchProcessReportSchema.get("markedForPreviewConversion");
		MetadataBuilder temporaryRecord_batchProcessReport_markedForReindexing = temporaryRecord_batchProcessReportSchema.get("markedForReindexing");
		MetadataBuilder temporaryRecord_batchProcessReport_migrationDataVersion = temporaryRecord_batchProcessReportSchema.get("migrationDataVersion");
		MetadataBuilder temporaryRecord_batchProcessReport_modifiedBy = temporaryRecord_batchProcessReportSchema.get("modifiedBy");
		MetadataBuilder temporaryRecord_batchProcessReport_modifiedOn = temporaryRecord_batchProcessReportSchema.get("modifiedOn");
		MetadataBuilder temporaryRecord_batchProcessReport_nonTaxonomyAuthorizations = temporaryRecord_batchProcessReportSchema.get("nonTaxonomyAuthorizations");
		MetadataBuilder temporaryRecord_batchProcessReport_parentpath = temporaryRecord_batchProcessReportSchema.get("parentpath");
		MetadataBuilder temporaryRecord_batchProcessReport_path = temporaryRecord_batchProcessReportSchema.get("path");
		MetadataBuilder temporaryRecord_batchProcessReport_pathParts = temporaryRecord_batchProcessReportSchema.get("pathParts");
		MetadataBuilder temporaryRecord_batchProcessReport_principalpath = temporaryRecord_batchProcessReportSchema.get("principalpath");
		MetadataBuilder temporaryRecord_batchProcessReport_removedauthorizations = temporaryRecord_batchProcessReportSchema.get("removedauthorizations");
		MetadataBuilder temporaryRecord_batchProcessReport_schema = temporaryRecord_batchProcessReportSchema.get("schema");
		MetadataBuilder temporaryRecord_batchProcessReport_searchable = temporaryRecord_batchProcessReportSchema.get("searchable");
		MetadataBuilder temporaryRecord_batchProcessReport_shareDenyTokens = temporaryRecord_batchProcessReportSchema.get("shareDenyTokens");
		MetadataBuilder temporaryRecord_batchProcessReport_shareTokens = temporaryRecord_batchProcessReportSchema.get("shareTokens");
		MetadataBuilder temporaryRecord_batchProcessReport_title = temporaryRecord_batchProcessReportSchema.get("title");
		MetadataBuilder temporaryRecord_batchProcessReport_tokens = temporaryRecord_batchProcessReportSchema.get("tokens");
		MetadataBuilder temporaryRecord_batchProcessReport_tokensHierarchy = temporaryRecord_batchProcessReportSchema.get("tokensHierarchy");
		MetadataBuilder temporaryRecord_batchProcessReport_visibleInTrees = temporaryRecord_batchProcessReportSchema.get("visibleInTrees");
		MetadataBuilder temporaryRecord_exportAudit_allReferences = temporaryRecord_exportAuditSchema.get("allReferences");
		MetadataBuilder temporaryRecord_exportAudit_allRemovedAuths = temporaryRecord_exportAuditSchema.get("allRemovedAuths");
		MetadataBuilder temporaryRecord_exportAudit_allauthorizations = temporaryRecord_exportAuditSchema.get("allauthorizations");
		MetadataBuilder temporaryRecord_exportAudit_attachedAncestors = temporaryRecord_exportAuditSchema.get("attachedAncestors");
		MetadataBuilder temporaryRecord_exportAudit_authorizations = temporaryRecord_exportAuditSchema.get("authorizations");
		MetadataBuilder temporaryRecord_exportAudit_autocomplete = temporaryRecord_exportAuditSchema.get("autocomplete");
		MetadataBuilder temporaryRecord_exportAudit_caption = temporaryRecord_exportAuditSchema.get("caption");
		MetadataBuilder temporaryRecord_exportAudit_content = temporaryRecord_exportAuditSchema.get("content");
		MetadataBuilder temporaryRecord_exportAudit_createdBy = temporaryRecord_exportAuditSchema.get("createdBy");
		MetadataBuilder temporaryRecord_exportAudit_createdOn = temporaryRecord_exportAuditSchema.get("createdOn");
		MetadataBuilder temporaryRecord_exportAudit_daysBeforeDestruction = temporaryRecord_exportAuditSchema.get("daysBeforeDestruction");
		MetadataBuilder temporaryRecord_exportAudit_deleted = temporaryRecord_exportAuditSchema.get("deleted");
		MetadataBuilder temporaryRecord_exportAudit_denyTokens = temporaryRecord_exportAuditSchema.get("denyTokens");
		MetadataBuilder temporaryRecord_exportAudit_destructionDate = temporaryRecord_exportAuditSchema.get("destructionDate");
		MetadataBuilder temporaryRecord_exportAudit_detachedauthorizations = temporaryRecord_exportAuditSchema.get("detachedauthorizations");
		MetadataBuilder temporaryRecord_exportAudit_errorOnPhysicalDeletion = temporaryRecord_exportAuditSchema.get("errorOnPhysicalDeletion");
		MetadataBuilder temporaryRecord_exportAudit_followers = temporaryRecord_exportAuditSchema.get("followers");
		MetadataBuilder temporaryRecord_exportAudit_id = temporaryRecord_exportAuditSchema.get("id");
		MetadataBuilder temporaryRecord_exportAudit_inheritedauthorizations = temporaryRecord_exportAuditSchema.get("inheritedauthorizations");
		MetadataBuilder temporaryRecord_exportAudit_legacyIdentifier = temporaryRecord_exportAuditSchema.get("legacyIdentifier");
		MetadataBuilder temporaryRecord_exportAudit_logicallyDeletedOn = temporaryRecord_exportAuditSchema.get("logicallyDeletedOn");
		MetadataBuilder temporaryRecord_exportAudit_manualTokens = temporaryRecord_exportAuditSchema.get("manualTokens");
		MetadataBuilder temporaryRecord_exportAudit_markedForParsing = temporaryRecord_exportAuditSchema.get("markedForParsing");
		MetadataBuilder temporaryRecord_exportAudit_markedForPreviewConversion = temporaryRecord_exportAuditSchema.get("markedForPreviewConversion");
		MetadataBuilder temporaryRecord_exportAudit_markedForReindexing = temporaryRecord_exportAuditSchema.get("markedForReindexing");
		MetadataBuilder temporaryRecord_exportAudit_migrationDataVersion = temporaryRecord_exportAuditSchema.get("migrationDataVersion");
		MetadataBuilder temporaryRecord_exportAudit_modifiedBy = temporaryRecord_exportAuditSchema.get("modifiedBy");
		MetadataBuilder temporaryRecord_exportAudit_modifiedOn = temporaryRecord_exportAuditSchema.get("modifiedOn");
		MetadataBuilder temporaryRecord_exportAudit_nonTaxonomyAuthorizations = temporaryRecord_exportAuditSchema.get("nonTaxonomyAuthorizations");
		MetadataBuilder temporaryRecord_exportAudit_parentpath = temporaryRecord_exportAuditSchema.get("parentpath");
		MetadataBuilder temporaryRecord_exportAudit_path = temporaryRecord_exportAuditSchema.get("path");
		MetadataBuilder temporaryRecord_exportAudit_pathParts = temporaryRecord_exportAuditSchema.get("pathParts");
		MetadataBuilder temporaryRecord_exportAudit_principalpath = temporaryRecord_exportAuditSchema.get("principalpath");
		MetadataBuilder temporaryRecord_exportAudit_removedauthorizations = temporaryRecord_exportAuditSchema.get("removedauthorizations");
		MetadataBuilder temporaryRecord_exportAudit_schema = temporaryRecord_exportAuditSchema.get("schema");
		MetadataBuilder temporaryRecord_exportAudit_searchable = temporaryRecord_exportAuditSchema.get("searchable");
		MetadataBuilder temporaryRecord_exportAudit_shareDenyTokens = temporaryRecord_exportAuditSchema.get("shareDenyTokens");
		MetadataBuilder temporaryRecord_exportAudit_shareTokens = temporaryRecord_exportAuditSchema.get("shareTokens");
		MetadataBuilder temporaryRecord_exportAudit_title = temporaryRecord_exportAuditSchema.get("title");
		MetadataBuilder temporaryRecord_exportAudit_tokens = temporaryRecord_exportAuditSchema.get("tokens");
		MetadataBuilder temporaryRecord_exportAudit_tokensHierarchy = temporaryRecord_exportAuditSchema.get("tokensHierarchy");
		MetadataBuilder temporaryRecord_exportAudit_visibleInTrees = temporaryRecord_exportAuditSchema.get("visibleInTrees");
		MetadataBuilder temporaryRecord_importAudit_allReferences = temporaryRecord_importAuditSchema.get("allReferences");
		MetadataBuilder temporaryRecord_importAudit_allRemovedAuths = temporaryRecord_importAuditSchema.get("allRemovedAuths");
		MetadataBuilder temporaryRecord_importAudit_allauthorizations = temporaryRecord_importAuditSchema.get("allauthorizations");
		MetadataBuilder temporaryRecord_importAudit_attachedAncestors = temporaryRecord_importAuditSchema.get("attachedAncestors");
		MetadataBuilder temporaryRecord_importAudit_authorizations = temporaryRecord_importAuditSchema.get("authorizations");
		MetadataBuilder temporaryRecord_importAudit_autocomplete = temporaryRecord_importAuditSchema.get("autocomplete");
		MetadataBuilder temporaryRecord_importAudit_caption = temporaryRecord_importAuditSchema.get("caption");
		MetadataBuilder temporaryRecord_importAudit_content = temporaryRecord_importAuditSchema.get("content");
		MetadataBuilder temporaryRecord_importAudit_createdBy = temporaryRecord_importAuditSchema.get("createdBy");
		MetadataBuilder temporaryRecord_importAudit_createdOn = temporaryRecord_importAuditSchema.get("createdOn");
		MetadataBuilder temporaryRecord_importAudit_daysBeforeDestruction = temporaryRecord_importAuditSchema.get("daysBeforeDestruction");
		MetadataBuilder temporaryRecord_importAudit_deleted = temporaryRecord_importAuditSchema.get("deleted");
		MetadataBuilder temporaryRecord_importAudit_denyTokens = temporaryRecord_importAuditSchema.get("denyTokens");
		MetadataBuilder temporaryRecord_importAudit_destructionDate = temporaryRecord_importAuditSchema.get("destructionDate");
		MetadataBuilder temporaryRecord_importAudit_detachedauthorizations = temporaryRecord_importAuditSchema.get("detachedauthorizations");
		MetadataBuilder temporaryRecord_importAudit_errorOnPhysicalDeletion = temporaryRecord_importAuditSchema.get("errorOnPhysicalDeletion");
		MetadataBuilder temporaryRecord_importAudit_followers = temporaryRecord_importAuditSchema.get("followers");
		MetadataBuilder temporaryRecord_importAudit_id = temporaryRecord_importAuditSchema.get("id");
		MetadataBuilder temporaryRecord_importAudit_inheritedauthorizations = temporaryRecord_importAuditSchema.get("inheritedauthorizations");
		MetadataBuilder temporaryRecord_importAudit_legacyIdentifier = temporaryRecord_importAuditSchema.get("legacyIdentifier");
		MetadataBuilder temporaryRecord_importAudit_logicallyDeletedOn = temporaryRecord_importAuditSchema.get("logicallyDeletedOn");
		MetadataBuilder temporaryRecord_importAudit_manualTokens = temporaryRecord_importAuditSchema.get("manualTokens");
		MetadataBuilder temporaryRecord_importAudit_markedForParsing = temporaryRecord_importAuditSchema.get("markedForParsing");
		MetadataBuilder temporaryRecord_importAudit_markedForPreviewConversion = temporaryRecord_importAuditSchema.get("markedForPreviewConversion");
		MetadataBuilder temporaryRecord_importAudit_markedForReindexing = temporaryRecord_importAuditSchema.get("markedForReindexing");
		MetadataBuilder temporaryRecord_importAudit_migrationDataVersion = temporaryRecord_importAuditSchema.get("migrationDataVersion");
		MetadataBuilder temporaryRecord_importAudit_modifiedBy = temporaryRecord_importAuditSchema.get("modifiedBy");
		MetadataBuilder temporaryRecord_importAudit_modifiedOn = temporaryRecord_importAuditSchema.get("modifiedOn");
		MetadataBuilder temporaryRecord_importAudit_nonTaxonomyAuthorizations = temporaryRecord_importAuditSchema.get("nonTaxonomyAuthorizations");
		MetadataBuilder temporaryRecord_importAudit_parentpath = temporaryRecord_importAuditSchema.get("parentpath");
		MetadataBuilder temporaryRecord_importAudit_path = temporaryRecord_importAuditSchema.get("path");
		MetadataBuilder temporaryRecord_importAudit_pathParts = temporaryRecord_importAuditSchema.get("pathParts");
		MetadataBuilder temporaryRecord_importAudit_principalpath = temporaryRecord_importAuditSchema.get("principalpath");
		MetadataBuilder temporaryRecord_importAudit_removedauthorizations = temporaryRecord_importAuditSchema.get("removedauthorizations");
		MetadataBuilder temporaryRecord_importAudit_schema = temporaryRecord_importAuditSchema.get("schema");
		MetadataBuilder temporaryRecord_importAudit_searchable = temporaryRecord_importAuditSchema.get("searchable");
		MetadataBuilder temporaryRecord_importAudit_shareDenyTokens = temporaryRecord_importAuditSchema.get("shareDenyTokens");
		MetadataBuilder temporaryRecord_importAudit_shareTokens = temporaryRecord_importAuditSchema.get("shareTokens");
		MetadataBuilder temporaryRecord_importAudit_title = temporaryRecord_importAuditSchema.get("title");
		MetadataBuilder temporaryRecord_importAudit_tokens = temporaryRecord_importAuditSchema.get("tokens");
		MetadataBuilder temporaryRecord_importAudit_tokensHierarchy = temporaryRecord_importAuditSchema.get("tokensHierarchy");
		MetadataBuilder temporaryRecord_importAudit_visibleInTrees = temporaryRecord_importAuditSchema.get("visibleInTrees");
		MetadataBuilder temporaryRecord_scriptReport_allReferences = temporaryRecord_scriptReportSchema.get("allReferences");
		MetadataBuilder temporaryRecord_scriptReport_allRemovedAuths = temporaryRecord_scriptReportSchema.get("allRemovedAuths");
		MetadataBuilder temporaryRecord_scriptReport_allauthorizations = temporaryRecord_scriptReportSchema.get("allauthorizations");
		MetadataBuilder temporaryRecord_scriptReport_attachedAncestors = temporaryRecord_scriptReportSchema.get("attachedAncestors");
		MetadataBuilder temporaryRecord_scriptReport_authorizations = temporaryRecord_scriptReportSchema.get("authorizations");
		MetadataBuilder temporaryRecord_scriptReport_autocomplete = temporaryRecord_scriptReportSchema.get("autocomplete");
		MetadataBuilder temporaryRecord_scriptReport_caption = temporaryRecord_scriptReportSchema.get("caption");
		MetadataBuilder temporaryRecord_scriptReport_content = temporaryRecord_scriptReportSchema.get("content");
		MetadataBuilder temporaryRecord_scriptReport_createdBy = temporaryRecord_scriptReportSchema.get("createdBy");
		MetadataBuilder temporaryRecord_scriptReport_createdOn = temporaryRecord_scriptReportSchema.get("createdOn");
		MetadataBuilder temporaryRecord_scriptReport_daysBeforeDestruction = temporaryRecord_scriptReportSchema.get("daysBeforeDestruction");
		MetadataBuilder temporaryRecord_scriptReport_deleted = temporaryRecord_scriptReportSchema.get("deleted");
		MetadataBuilder temporaryRecord_scriptReport_denyTokens = temporaryRecord_scriptReportSchema.get("denyTokens");
		MetadataBuilder temporaryRecord_scriptReport_destructionDate = temporaryRecord_scriptReportSchema.get("destructionDate");
		MetadataBuilder temporaryRecord_scriptReport_detachedauthorizations = temporaryRecord_scriptReportSchema.get("detachedauthorizations");
		MetadataBuilder temporaryRecord_scriptReport_errorOnPhysicalDeletion = temporaryRecord_scriptReportSchema.get("errorOnPhysicalDeletion");
		MetadataBuilder temporaryRecord_scriptReport_followers = temporaryRecord_scriptReportSchema.get("followers");
		MetadataBuilder temporaryRecord_scriptReport_id = temporaryRecord_scriptReportSchema.get("id");
		MetadataBuilder temporaryRecord_scriptReport_inheritedauthorizations = temporaryRecord_scriptReportSchema.get("inheritedauthorizations");
		MetadataBuilder temporaryRecord_scriptReport_legacyIdentifier = temporaryRecord_scriptReportSchema.get("legacyIdentifier");
		MetadataBuilder temporaryRecord_scriptReport_logicallyDeletedOn = temporaryRecord_scriptReportSchema.get("logicallyDeletedOn");
		MetadataBuilder temporaryRecord_scriptReport_manualTokens = temporaryRecord_scriptReportSchema.get("manualTokens");
		MetadataBuilder temporaryRecord_scriptReport_markedForParsing = temporaryRecord_scriptReportSchema.get("markedForParsing");
		MetadataBuilder temporaryRecord_scriptReport_markedForPreviewConversion = temporaryRecord_scriptReportSchema.get("markedForPreviewConversion");
		MetadataBuilder temporaryRecord_scriptReport_markedForReindexing = temporaryRecord_scriptReportSchema.get("markedForReindexing");
		MetadataBuilder temporaryRecord_scriptReport_migrationDataVersion = temporaryRecord_scriptReportSchema.get("migrationDataVersion");
		MetadataBuilder temporaryRecord_scriptReport_modifiedBy = temporaryRecord_scriptReportSchema.get("modifiedBy");
		MetadataBuilder temporaryRecord_scriptReport_modifiedOn = temporaryRecord_scriptReportSchema.get("modifiedOn");
		MetadataBuilder temporaryRecord_scriptReport_nonTaxonomyAuthorizations = temporaryRecord_scriptReportSchema.get("nonTaxonomyAuthorizations");
		MetadataBuilder temporaryRecord_scriptReport_parentpath = temporaryRecord_scriptReportSchema.get("parentpath");
		MetadataBuilder temporaryRecord_scriptReport_path = temporaryRecord_scriptReportSchema.get("path");
		MetadataBuilder temporaryRecord_scriptReport_pathParts = temporaryRecord_scriptReportSchema.get("pathParts");
		MetadataBuilder temporaryRecord_scriptReport_principalpath = temporaryRecord_scriptReportSchema.get("principalpath");
		MetadataBuilder temporaryRecord_scriptReport_removedauthorizations = temporaryRecord_scriptReportSchema.get("removedauthorizations");
		MetadataBuilder temporaryRecord_scriptReport_schema = temporaryRecord_scriptReportSchema.get("schema");
		MetadataBuilder temporaryRecord_scriptReport_searchable = temporaryRecord_scriptReportSchema.get("searchable");
		MetadataBuilder temporaryRecord_scriptReport_shareDenyTokens = temporaryRecord_scriptReportSchema.get("shareDenyTokens");
		MetadataBuilder temporaryRecord_scriptReport_shareTokens = temporaryRecord_scriptReportSchema.get("shareTokens");
		MetadataBuilder temporaryRecord_scriptReport_title = temporaryRecord_scriptReportSchema.get("title");
		MetadataBuilder temporaryRecord_scriptReport_tokens = temporaryRecord_scriptReportSchema.get("tokens");
		MetadataBuilder temporaryRecord_scriptReport_tokensHierarchy = temporaryRecord_scriptReportSchema.get("tokensHierarchy");
		MetadataBuilder temporaryRecord_scriptReport_visibleInTrees = temporaryRecord_scriptReportSchema.get("visibleInTrees");
		MetadataBuilder temporaryRecord_vaultScanReport_allReferences = temporaryRecord_vaultScanReportSchema.get("allReferences");
		MetadataBuilder temporaryRecord_vaultScanReport_allRemovedAuths = temporaryRecord_vaultScanReportSchema.get("allRemovedAuths");
		MetadataBuilder temporaryRecord_vaultScanReport_allauthorizations = temporaryRecord_vaultScanReportSchema.get("allauthorizations");
		MetadataBuilder temporaryRecord_vaultScanReport_attachedAncestors = temporaryRecord_vaultScanReportSchema.get("attachedAncestors");
		MetadataBuilder temporaryRecord_vaultScanReport_authorizations = temporaryRecord_vaultScanReportSchema.get("authorizations");
		MetadataBuilder temporaryRecord_vaultScanReport_autocomplete = temporaryRecord_vaultScanReportSchema.get("autocomplete");
		MetadataBuilder temporaryRecord_vaultScanReport_caption = temporaryRecord_vaultScanReportSchema.get("caption");
		MetadataBuilder temporaryRecord_vaultScanReport_content = temporaryRecord_vaultScanReportSchema.get("content");
		MetadataBuilder temporaryRecord_vaultScanReport_createdBy = temporaryRecord_vaultScanReportSchema.get("createdBy");
		MetadataBuilder temporaryRecord_vaultScanReport_createdOn = temporaryRecord_vaultScanReportSchema.get("createdOn");
		MetadataBuilder temporaryRecord_vaultScanReport_daysBeforeDestruction = temporaryRecord_vaultScanReportSchema.get("daysBeforeDestruction");
		MetadataBuilder temporaryRecord_vaultScanReport_deleted = temporaryRecord_vaultScanReportSchema.get("deleted");
		MetadataBuilder temporaryRecord_vaultScanReport_denyTokens = temporaryRecord_vaultScanReportSchema.get("denyTokens");
		MetadataBuilder temporaryRecord_vaultScanReport_destructionDate = temporaryRecord_vaultScanReportSchema.get("destructionDate");
		MetadataBuilder temporaryRecord_vaultScanReport_detachedauthorizations = temporaryRecord_vaultScanReportSchema.get("detachedauthorizations");
		MetadataBuilder temporaryRecord_vaultScanReport_errorOnPhysicalDeletion = temporaryRecord_vaultScanReportSchema.get("errorOnPhysicalDeletion");
		MetadataBuilder temporaryRecord_vaultScanReport_followers = temporaryRecord_vaultScanReportSchema.get("followers");
		MetadataBuilder temporaryRecord_vaultScanReport_id = temporaryRecord_vaultScanReportSchema.get("id");
		MetadataBuilder temporaryRecord_vaultScanReport_inheritedauthorizations = temporaryRecord_vaultScanReportSchema.get("inheritedauthorizations");
		MetadataBuilder temporaryRecord_vaultScanReport_legacyIdentifier = temporaryRecord_vaultScanReportSchema.get("legacyIdentifier");
		MetadataBuilder temporaryRecord_vaultScanReport_logicallyDeletedOn = temporaryRecord_vaultScanReportSchema.get("logicallyDeletedOn");
		MetadataBuilder temporaryRecord_vaultScanReport_manualTokens = temporaryRecord_vaultScanReportSchema.get("manualTokens");
		MetadataBuilder temporaryRecord_vaultScanReport_markedForParsing = temporaryRecord_vaultScanReportSchema.get("markedForParsing");
		MetadataBuilder temporaryRecord_vaultScanReport_markedForPreviewConversion = temporaryRecord_vaultScanReportSchema.get("markedForPreviewConversion");
		MetadataBuilder temporaryRecord_vaultScanReport_markedForReindexing = temporaryRecord_vaultScanReportSchema.get("markedForReindexing");
		MetadataBuilder temporaryRecord_vaultScanReport_migrationDataVersion = temporaryRecord_vaultScanReportSchema.get("migrationDataVersion");
		MetadataBuilder temporaryRecord_vaultScanReport_modifiedBy = temporaryRecord_vaultScanReportSchema.get("modifiedBy");
		MetadataBuilder temporaryRecord_vaultScanReport_modifiedOn = temporaryRecord_vaultScanReportSchema.get("modifiedOn");
		MetadataBuilder temporaryRecord_vaultScanReport_nonTaxonomyAuthorizations = temporaryRecord_vaultScanReportSchema.get("nonTaxonomyAuthorizations");
		MetadataBuilder temporaryRecord_vaultScanReport_parentpath = temporaryRecord_vaultScanReportSchema.get("parentpath");
		MetadataBuilder temporaryRecord_vaultScanReport_path = temporaryRecord_vaultScanReportSchema.get("path");
		MetadataBuilder temporaryRecord_vaultScanReport_pathParts = temporaryRecord_vaultScanReportSchema.get("pathParts");
		MetadataBuilder temporaryRecord_vaultScanReport_principalpath = temporaryRecord_vaultScanReportSchema.get("principalpath");
		MetadataBuilder temporaryRecord_vaultScanReport_removedauthorizations = temporaryRecord_vaultScanReportSchema.get("removedauthorizations");
		MetadataBuilder temporaryRecord_vaultScanReport_schema = temporaryRecord_vaultScanReportSchema.get("schema");
		MetadataBuilder temporaryRecord_vaultScanReport_searchable = temporaryRecord_vaultScanReportSchema.get("searchable");
		MetadataBuilder temporaryRecord_vaultScanReport_shareDenyTokens = temporaryRecord_vaultScanReportSchema.get("shareDenyTokens");
		MetadataBuilder temporaryRecord_vaultScanReport_shareTokens = temporaryRecord_vaultScanReportSchema.get("shareTokens");
		MetadataBuilder temporaryRecord_vaultScanReport_title = temporaryRecord_vaultScanReportSchema.get("title");
		MetadataBuilder temporaryRecord_vaultScanReport_tokens = temporaryRecord_vaultScanReportSchema.get("tokens");
		MetadataBuilder temporaryRecord_vaultScanReport_tokensHierarchy = temporaryRecord_vaultScanReportSchema.get("tokensHierarchy");
		MetadataBuilder temporaryRecord_vaultScanReport_visibleInTrees = temporaryRecord_vaultScanReportSchema.get("visibleInTrees");
	}

	private void createUserSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
											   MetadataSchemaTypeBuilder userSchemaType,
											   MetadataSchemaBuilder userSchema) {
	}

	private void createFacetSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
												MetadataSchemaTypeBuilder facetSchemaType,
												MetadataSchemaBuilder facet_fieldSchema,
												MetadataSchemaBuilder facet_querySchema,
												MetadataSchemaBuilder facetSchema) {
		MetadataBuilder facet_field_active = facet_fieldSchema.get("active");
		MetadataBuilder facet_field_allReferences = facet_fieldSchema.get("allReferences");
		MetadataBuilder facet_field_allRemovedAuths = facet_fieldSchema.get("allRemovedAuths");
		MetadataBuilder facet_field_allauthorizations = facet_fieldSchema.get("allauthorizations");
		MetadataBuilder facet_field_attachedAncestors = facet_fieldSchema.get("attachedAncestors");
		MetadataBuilder facet_field_authorizations = facet_fieldSchema.get("authorizations");
		MetadataBuilder facet_field_autocomplete = facet_fieldSchema.get("autocomplete");
		MetadataBuilder facet_field_caption = facet_fieldSchema.get("caption");
		MetadataBuilder facet_field_createdBy = facet_fieldSchema.get("createdBy");
		MetadataBuilder facet_field_createdOn = facet_fieldSchema.get("createdOn");
		MetadataBuilder facet_field_deleted = facet_fieldSchema.get("deleted");
		MetadataBuilder facet_field_denyTokens = facet_fieldSchema.get("denyTokens");
		MetadataBuilder facet_field_detachedauthorizations = facet_fieldSchema.get("detachedauthorizations");
		MetadataBuilder facet_field_elementPerPage = facet_fieldSchema.get("elementPerPage");
		MetadataBuilder facet_field_errorOnPhysicalDeletion = facet_fieldSchema.get("errorOnPhysicalDeletion");
		MetadataBuilder facet_field_facetType = facet_fieldSchema.get("facetType");
		MetadataBuilder facet_field_fieldDatastoreCode = facet_fieldSchema.get("fieldDatastoreCode");
		MetadataBuilder facet_field_followers = facet_fieldSchema.get("followers");
		MetadataBuilder facet_field_id = facet_fieldSchema.get("id");
		MetadataBuilder facet_field_inheritedauthorizations = facet_fieldSchema.get("inheritedauthorizations");
		MetadataBuilder facet_field_legacyIdentifier = facet_fieldSchema.get("legacyIdentifier");
		MetadataBuilder facet_field_logicallyDeletedOn = facet_fieldSchema.get("logicallyDeletedOn");
		MetadataBuilder facet_field_manualTokens = facet_fieldSchema.get("manualTokens");
		MetadataBuilder facet_field_markedForParsing = facet_fieldSchema.get("markedForParsing");
		MetadataBuilder facet_field_markedForPreviewConversion = facet_fieldSchema.get("markedForPreviewConversion");
		MetadataBuilder facet_field_markedForReindexing = facet_fieldSchema.get("markedForReindexing");
		MetadataBuilder facet_field_migrationDataVersion = facet_fieldSchema.get("migrationDataVersion");
		MetadataBuilder facet_field_modifiedBy = facet_fieldSchema.get("modifiedBy");
		MetadataBuilder facet_field_modifiedOn = facet_fieldSchema.get("modifiedOn");
		MetadataBuilder facet_field_nonTaxonomyAuthorizations = facet_fieldSchema.get("nonTaxonomyAuthorizations");
		MetadataBuilder facet_field_openByDefault = facet_fieldSchema.get("openByDefault");
		MetadataBuilder facet_field_order = facet_fieldSchema.get("order");
		MetadataBuilder facet_field_orderResult = facet_fieldSchema.get("orderResult");
		MetadataBuilder facet_field_pages = facet_fieldSchema.get("pages");
		MetadataBuilder facet_field_parentpath = facet_fieldSchema.get("parentpath");
		MetadataBuilder facet_field_path = facet_fieldSchema.get("path");
		MetadataBuilder facet_field_pathParts = facet_fieldSchema.get("pathParts");
		MetadataBuilder facet_field_principalpath = facet_fieldSchema.get("principalpath");
		MetadataBuilder facet_field_removedauthorizations = facet_fieldSchema.get("removedauthorizations");
		MetadataBuilder facet_field_schema = facet_fieldSchema.get("schema");
		MetadataBuilder facet_field_searchable = facet_fieldSchema.get("searchable");
		MetadataBuilder facet_field_shareDenyTokens = facet_fieldSchema.get("shareDenyTokens");
		MetadataBuilder facet_field_shareTokens = facet_fieldSchema.get("shareTokens");
		MetadataBuilder facet_field_title = facet_fieldSchema.get("title");
		MetadataBuilder facet_field_tokens = facet_fieldSchema.get("tokens");
		MetadataBuilder facet_field_tokensHierarchy = facet_fieldSchema.get("tokensHierarchy");
		MetadataBuilder facet_field_usedByModule = facet_fieldSchema.get("usedByModule");
		MetadataBuilder facet_field_visibleInTrees = facet_fieldSchema.get("visibleInTrees");
		MetadataBuilder facet_query_active = facet_querySchema.get("active");
		MetadataBuilder facet_query_allReferences = facet_querySchema.get("allReferences");
		MetadataBuilder facet_query_allRemovedAuths = facet_querySchema.get("allRemovedAuths");
		MetadataBuilder facet_query_allauthorizations = facet_querySchema.get("allauthorizations");
		MetadataBuilder facet_query_attachedAncestors = facet_querySchema.get("attachedAncestors");
		MetadataBuilder facet_query_authorizations = facet_querySchema.get("authorizations");
		MetadataBuilder facet_query_autocomplete = facet_querySchema.get("autocomplete");
		MetadataBuilder facet_query_caption = facet_querySchema.get("caption");
		MetadataBuilder facet_query_createdBy = facet_querySchema.get("createdBy");
		MetadataBuilder facet_query_createdOn = facet_querySchema.get("createdOn");
		MetadataBuilder facet_query_deleted = facet_querySchema.get("deleted");
		MetadataBuilder facet_query_denyTokens = facet_querySchema.get("denyTokens");
		MetadataBuilder facet_query_detachedauthorizations = facet_querySchema.get("detachedauthorizations");
		MetadataBuilder facet_query_elementPerPage = facet_querySchema.get("elementPerPage");
		MetadataBuilder facet_query_errorOnPhysicalDeletion = facet_querySchema.get("errorOnPhysicalDeletion");
		MetadataBuilder facet_query_facetType = facet_querySchema.get("facetType");
		MetadataBuilder facet_query_fieldDatastoreCode = facet_querySchema.get("fieldDatastoreCode");
		MetadataBuilder facet_query_followers = facet_querySchema.get("followers");
		MetadataBuilder facet_query_id = facet_querySchema.get("id");
		MetadataBuilder facet_query_inheritedauthorizations = facet_querySchema.get("inheritedauthorizations");
		MetadataBuilder facet_query_legacyIdentifier = facet_querySchema.get("legacyIdentifier");
		MetadataBuilder facet_query_logicallyDeletedOn = facet_querySchema.get("logicallyDeletedOn");
		MetadataBuilder facet_query_manualTokens = facet_querySchema.get("manualTokens");
		MetadataBuilder facet_query_markedForParsing = facet_querySchema.get("markedForParsing");
		MetadataBuilder facet_query_markedForPreviewConversion = facet_querySchema.get("markedForPreviewConversion");
		MetadataBuilder facet_query_markedForReindexing = facet_querySchema.get("markedForReindexing");
		MetadataBuilder facet_query_migrationDataVersion = facet_querySchema.get("migrationDataVersion");
		MetadataBuilder facet_query_modifiedBy = facet_querySchema.get("modifiedBy");
		MetadataBuilder facet_query_modifiedOn = facet_querySchema.get("modifiedOn");
		MetadataBuilder facet_query_nonTaxonomyAuthorizations = facet_querySchema.get("nonTaxonomyAuthorizations");
		MetadataBuilder facet_query_openByDefault = facet_querySchema.get("openByDefault");
		MetadataBuilder facet_query_order = facet_querySchema.get("order");
		MetadataBuilder facet_query_orderResult = facet_querySchema.get("orderResult");
		MetadataBuilder facet_query_pages = facet_querySchema.get("pages");
		MetadataBuilder facet_query_parentpath = facet_querySchema.get("parentpath");
		MetadataBuilder facet_query_path = facet_querySchema.get("path");
		MetadataBuilder facet_query_pathParts = facet_querySchema.get("pathParts");
		MetadataBuilder facet_query_principalpath = facet_querySchema.get("principalpath");
		MetadataBuilder facet_query_removedauthorizations = facet_querySchema.get("removedauthorizations");
		MetadataBuilder facet_query_schema = facet_querySchema.get("schema");
		MetadataBuilder facet_query_searchable = facet_querySchema.get("searchable");
		MetadataBuilder facet_query_shareDenyTokens = facet_querySchema.get("shareDenyTokens");
		MetadataBuilder facet_query_shareTokens = facet_querySchema.get("shareTokens");
		MetadataBuilder facet_query_title = facet_querySchema.get("title");
		MetadataBuilder facet_query_tokens = facet_querySchema.get("tokens");
		MetadataBuilder facet_query_tokensHierarchy = facet_querySchema.get("tokensHierarchy");
		MetadataBuilder facet_query_usedByModule = facet_querySchema.get("usedByModule");
		MetadataBuilder facet_query_visibleInTrees = facet_querySchema.get("visibleInTrees");
	}

	private void createGroupSchemaTypeMetadatas(MetadataSchemaTypesBuilder types,
												MetadataSchemaTypeBuilder groupSchemaType,
												MetadataSchemaBuilder groupSchema) {
	}

	public void applySchemasDisplay(SchemasDisplayManager manager) {
		SchemaTypesDisplayTransactionBuilder transaction = manager.newTransactionBuilderFor(collection);
		SchemaTypesDisplayConfig typesConfig = manager.getTypes(collection);
		transaction.add(manager.getSchema(collection, "actionParameters_default").withFormMetadataCodes(new ArrayList<String>()).withDisplayMetadataCodes(asList("actionParameters_default_title")).withSearchResultsMetadataCodes(asList("actionParameters_default_title", "actionParameters_default_modifiedOn")).withTableMetadataCodes(asList("actionParameters_default_title", "actionParameters_default_modifiedOn")));
		transaction.add(manager.getType(collection, "robot").withSimpleSearchStatus(false).withAdvancedSearchStatus(false).withManageableStatus(false).withMetadataGroup(resourcesProvider.getLanguageMap(asList("init.robot.tabs.criteria", "default:init.robot.tabs.definition", "init.robot.tabs.action"))));
		transaction.add(manager.getSchema(collection, "robot_default").withFormMetadataCodes(asList("robot_default_code", "robot_default_title", "robot_default_parent", "robot_default_schemaFilter", "robot_default_description", "robot_default_searchCriteria", "robot_default_action", "robot_default_actionParameters", "robot_default_excludeProcessedByChildren", "robot_default_autoExecute")).withDisplayMetadataCodes(asList("robot_default_code", "robot_default_title", "robot_default_createdBy", "robot_default_createdOn", "robot_default_modifiedBy", "robot_default_modifiedOn", "robot_default_action", "robot_default_actionParameters", "robot_default_excludeProcessedByChildren", "robot_default_parent", "robot_default_schemaFilter", "robot_default_description")).withSearchResultsMetadataCodes(asList("robot_default_title", "robot_default_modifiedOn")).withTableMetadataCodes(asList("robot_default_title", "robot_default_modifiedOn")));
		transaction.add(manager.getMetadata(collection, "robot_default_action").withMetadataGroup("init.robot.tabs.action").withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "robot_default_actionParameters").withMetadataGroup("init.robot.tabs.action").withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "robot_default_excludeProcessedByChildren").withMetadataGroup("init.robot.tabs.action").withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "robot_default_parent").withMetadataGroup("").withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "robot_default_schemaFilter").withMetadataGroup("init.robot.tabs.criteria").withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "robot_default_searchCriteria").withMetadataGroup("init.robot.tabs.criteria").withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getSchema(collection, "robotLog_default").withFormMetadataCodes(asList("robotLog_default_title", "robotLog_default_count", "robotLog_default_robot")).withDisplayMetadataCodes(asList("robotLog_default_title", "robotLog_default_createdBy", "robotLog_default_createdOn", "robotLog_default_modifiedBy", "robotLog_default_modifiedOn", "robotLog_default_count", "robotLog_default_robot")).withSearchResultsMetadataCodes(asList("robotLog_default_title", "robotLog_default_modifiedOn")).withTableMetadataCodes(asList("robotLog_default_title", "robotLog_default_modifiedOn", "robotLog_default_count")));
		manager.execute(transaction.build());
	}

	public void applyGeneratedRoles() {
		RolesManager rolesManager = appLayerFactory.getModelLayerFactory().getRolesManager();
		;
		rolesManager.updateRole(rolesManager.getRole(collection, "ADM").withNewPermissions(asList("core.accessDeleteAllTemporaryRecords", "core.deleteContentVersion", "core.deletePublicSavedSearch", "core.ldapConfigurationManagement", "core.manageConnectors", "core.manageEmailServer", "core.manageExcelReport", "core.manageFacets", "core.manageLabels", "core.manageMetadataExtractor", "core.manageMetadataSchemas", "core.managePrintableReport", "core.manageSearchBoost", "core.manageSecurity", "core.manageSystemCollections", "core.manageSystemConfiguration", "core.manageSystemDataImports", "core.manageSystemGroups", "core.manageSystemGroupsActivation", "core.manageSystemUpdates", "core.manageSystemUsers", "core.manageTaxonomies", "core.manageTrash", "core.manageValueList", "core.managerTemporaryRecords", "core.seeAllTemporaryRecords", "core.useExternalAPIS", "core.viewEvents", "core.viewSystemBatchProcesses", "robots.manageRobots")));
	}
}