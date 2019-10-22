package com.constellio.model.services.schemas.calculators;

import com.constellio.model.entities.calculators.CalculatorParameters;
import com.constellio.model.entities.calculators.MetadataValueCalculator;
import com.constellio.model.entities.calculators.dependencies.Dependency;
import com.constellio.model.entities.calculators.dependencies.LocalDependency;
import com.constellio.model.entities.calculators.dependencies.SpecialDependencies;
import com.constellio.model.entities.calculators.dependencies.SpecialDependency;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.entities.security.Role;
import com.constellio.model.entities.security.SecurityModel;
import com.constellio.model.entities.security.SecurityModelAuthorization;
import com.constellio.model.services.schemas.builders.CommonMetadataBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.constellio.model.entities.security.TransactionSecurityModel.hasActiveOverridingAuth;
import static com.constellio.model.services.schemas.builders.CommonMetadataBuilder.ALL_REMOVED_AUTHS;
import static com.constellio.model.services.schemas.builders.CommonMetadataBuilder.LOGICALLY_DELETED;
import static com.constellio.model.services.schemas.builders.CommonMetadataBuilder.MANUAL_TOKENS;
import static com.constellio.model.services.schemas.calculators.IntegerTokenFactory.newAccessToken;
import static java.lang.Boolean.TRUE;

public class IntAccessTokensCalculator implements MetadataValueCalculator<List<Integer>> {


	LocalDependency<List<String>> manualTokensParam = LocalDependency.toAStringList(MANUAL_TOKENS);

	LocalDependency<List<String>> allRemovedAuthsParam = LocalDependency.toAStringList(ALL_REMOVED_AUTHS);

	LocalDependency<Boolean> logicallyDeletedParam = LocalDependency.toABoolean(LOGICALLY_DELETED);

	SpecialDependency<SecurityModel> securityModelSpecialDependency = SpecialDependencies.SECURITY_MODEL;

	LocalDependency<Boolean> isDetachedParams = LocalDependency.toABoolean(Schemas.IS_DETACHED_AUTHORIZATIONS.getLocalCode());

	MetadatasProvidingSecurityDynamicDependency metadatasProvidingSecurityParams = new MetadatasProvidingSecurityDynamicDependency();

	LocalDependency<List<String>> attachedAncestorsParam = LocalDependency.toAStringList(CommonMetadataBuilder.ATTACHED_ANCESTORS);

	@Override
	public List<Integer> calculate(CalculatorParameters parameters) {
		SecurityModel securityModel = parameters.get(securityModelSpecialDependency);
		List<SecurityModelAuthorization> authorizations = new ArrayList<>();
		List<SecurityModelAuthorization> removedOrDetachedAuthorizations = new ArrayList<>();
		calculateAppliedAuthorizations(parameters, securityModel, authorizations, removedOrDetachedAuthorizations);

		final String typeSmallCode = getSchemaTypeSmallCode(parameters);
		final Set<Integer> negativeTokens = buildNegativeTokens(securityModel, authorizations);
		final Set<Integer> removedNegativeTokens = buildRemovedNegativeTokens(securityModel, removedOrDetachedAuthorizations);
		final Set<Integer> positiveTokens = buildPositiveTokens(securityModel, authorizations, negativeTokens);

		return mergeTokens(removedNegativeTokens, negativeTokens, positiveTokens);
	}

	private List<SecurityModelAuthorization> getInheritedAuthorizationsTargettingSecurisedRecords(
			SecurityModel securityModel,
			List<String> attachedAncestors,
			boolean detached) {

		List<SecurityModelAuthorization> returnedAuths = new ArrayList<>();

		if (!detached) {
			for (String attachedAncestor : attachedAncestors) {
				if (!attachedAncestor.startsWith("-")) {
					for (SecurityModelAuthorization inheritedNonTaxonomyAuth : securityModel.getAuthorizationsOnTarget(attachedAncestor)) {
						if (inheritedNonTaxonomyAuth.isSecurableRecord()) {
							returnedAuths.add(inheritedNonTaxonomyAuth);
						}
					}
				}
			}
		}
		return returnedAuths;
	}

	private List<SecurityModelAuthorization> getInheritedAuthorizationsTargettingAnyRecordsNoMatterIfDetached(
			SecurityModel securityModel,
			List<String> attachedAncestors) {

		List<SecurityModelAuthorization> returnedAuths = new ArrayList<>();

		for (String attachedAncestor : attachedAncestors) {

			String ancestor = attachedAncestor;
			if (attachedAncestor.startsWith("-")) {
				ancestor = attachedAncestor.substring(1);
			}

			returnedAuths.addAll(securityModel.getAuthorizationsOnTarget(ancestor));
		}
		return returnedAuths;
	}

	private void calculateAppliedAuthorizations(CalculatorParameters parameters,
												SecurityModel securityModel,
												List<SecurityModelAuthorization> authorizations,
												List<SecurityModelAuthorization> removedOrDetachedAuthorizations) {

		authorizations.addAll(securityModel.getAuthorizationsOnTarget(parameters.getId()));
		boolean detached = TRUE.equals(parameters.get(isDetachedParams));

		List<String> allRemovedAuths = parameters.get(allRemovedAuthsParam);
		List<String> attachedAncestors = parameters.get(attachedAncestorsParam);

		List<SecurityModelAuthorization> authsFromMetadatas = securityModel.getAuthorizationDetailsOnMetadatasProvidingSecurity(
				parameters.get(metadatasProvidingSecurityParams));

		authorizations.addAll(authsFromMetadatas);

		if (!hasActiveOverridingAuth(authsFromMetadatas)) {


			if (!detached) {
				List<SecurityModelAuthorization> inheritedAuthorizationsTargettingSecurisedRecords =
						getInheritedAuthorizationsTargettingSecurisedRecords(securityModel, attachedAncestors, detached);
				for (SecurityModelAuthorization auth : inheritedAuthorizationsTargettingSecurisedRecords) {
					if (!allRemovedAuths.contains(auth.getDetails().getId())) {
						authorizations.add(auth);
					} else {
						removedOrDetachedAuthorizations.add(auth);
					}
				}
			} else {
				List<SecurityModelAuthorization> inheritedAuthorizationsTargettingAnyRecords =
						getInheritedAuthorizationsTargettingAnyRecordsNoMatterIfDetached(securityModel, attachedAncestors);
				removedOrDetachedAuthorizations.addAll(inheritedAuthorizationsTargettingAnyRecords);
			}
		}
	}


	private String getSchemaTypeSmallCode(CalculatorParameters parameters) {
		final String typeSmallCode;
		if (parameters.getSchemaType().getSmallCode() != null) {
			typeSmallCode = parameters.getSchemaType().getSmallCode();

		} else {
			typeSmallCode = parameters.getSchemaType().getCode();

		}
		return typeSmallCode;
	}

	@NotNull
	private Set<Integer> buildPositiveTokens(SecurityModel securityModel,
											 List<SecurityModelAuthorization> authorizations,
											 final Set<Integer> negativeTokens) {
		final Set<Integer> positiveTokens = new HashSet<>();
		for (SecurityModelAuthorization authorization : authorizations) {
			if (authorization.getDetails().isActiveAuthorization() && authorization.isSecurableRecord()
				&& !authorization.getDetails().isNegative()) {

				forEachAccessAndPrincipalInheriting(securityModel, authorization,
						(access, principalId) -> addPrincipalPositiveTokens(positiveTokens, negativeTokens, access, principalId));
			}
		}
		return positiveTokens;
	}

	@NotNull
	private Set<Integer> buildNegativeTokens(SecurityModel securityModel,
											 List<SecurityModelAuthorization> authorizations) {
		final Set<Integer> negativeTokens = new HashSet<>();

		for (SecurityModelAuthorization authorization : authorizations) {
			if (authorization.getDetails().isActiveAuthorization()
				&& authorization.isSecurableRecord()
				&& authorization.getDetails().isNegative()) {

				forEachAccessAndPrincipalInheriting(securityModel, authorization, new Caller() {
					@Override
					public void call(String access, int principalId) {
						addPrincipalNegativeTokens(negativeTokens, access, principalId);
					}
				});
			}
		}
		return negativeTokens;
	}

	@NotNull
	private Set<Integer> buildRemovedNegativeTokens(SecurityModel securityModel,
													List<SecurityModelAuthorization> removedOrDetachedAuthorizations) {
		final Set<Integer> removedNegativeTokens = new HashSet<>();
		for (SecurityModelAuthorization authorization : removedOrDetachedAuthorizations) {
			if (authorization.getDetails().isActiveAuthorization() && authorization.isSecurableRecord()
				&& authorization.getDetails().isNegative()) {

				forEachAccessAndPrincipalInheriting(securityModel, authorization, new Caller() {
					@Override
					public void call(String access, int principalId) {
						addPrincipalNegativeTokens(removedNegativeTokens, access, principalId);
					}
				});
			}
		}
		return removedNegativeTokens;
	}

	@NotNull
	private List<Integer> mergeTokens(Set<Integer> removedNegativeTokens,
									  Set<Integer> negativeTokens, Set<Integer> positiveTokens) {
		Set<Integer> tokens = new HashSet<>(positiveTokens);
		for (Integer negativeToken : negativeTokens) {
			tokens.add(negativeToken * -1);
		}

		for (int removedNegativeToken : removedNegativeTokens) {
			if (!negativeTokens.contains(removedNegativeToken)) {
				tokens.add(IntegerTokenFactory.withRemovedNegativeFlag(removedNegativeToken));
			}
		}

		List<Integer> tokensList = new ArrayList<>(tokens);
		Collections.sort(tokensList);
		return tokensList;
	}

	private void forEachAccessAndPrincipalInheriting(SecurityModel securityModel,
													 SecurityModelAuthorization authorization,
													 Caller caller) {
		for (String access : authorization.getDetails().getRoles()) {
			for (String userId : authorization.getUserIds()) {
				int userIntId = securityModel.getPrincipalIntId(userId);
				caller.call(access, userIntId);
			}

			for (String groupId : authorization.getGroupIds()) {
				if (securityModel.isGroupActive(groupId)) {
					for (String aGroup : securityModel.getGroupsInheritingAuthorizationsFrom(groupId)) {
						int aGroupIntId = securityModel.getPrincipalIntId(aGroup);
						caller.call(access, aGroupIntId);
					}
				}
			}
		}
	}

	private interface Caller {
		void call(String access, int principalId);
	}

	private void addPrincipalPositiveTokens(Set<Integer> positiveTokens, Set<Integer> negativeTokens,
											String access, int principalId) {

		if (Role.READ.equals(access)) {
			addPrincipalPositiveReadTokens(positiveTokens, negativeTokens, principalId);

		} else if (Role.WRITE.equals(access)) {
			addPrincipalPositiveWriteTokens(positiveTokens, negativeTokens, principalId);

		} else if (Role.DELETE.equals(access)) {
			addPrincipalPositiveDeleteTokens(positiveTokens, negativeTokens, principalId);

		}
	}

	private void addPrincipalPositiveWriteTokens(Set<Integer> positiveTokens, Set<Integer> negativeTokens,
												 int principalId) {
		int readOnRecordsOfAnyTypeToken = newAccessToken(principalId, Role.READ);
		if (!negativeTokens.contains(readOnRecordsOfAnyTypeToken)) {
			positiveTokens.add(readOnRecordsOfAnyTypeToken);
		}

		int writeOnRecordsOfAnyTypeToken = newAccessToken(principalId, Role.WRITE);
		if (!negativeTokens.contains(writeOnRecordsOfAnyTypeToken)) {
			positiveTokens.add(writeOnRecordsOfAnyTypeToken);
		}
	}

	private void addPrincipalPositiveDeleteTokens(Set<Integer> positiveTokens, Set<Integer> negativeTokens,
												  int principalId) {

		int readOnRecordsOfAnyTypeToken = newAccessToken(principalId, Role.READ);

		if (!negativeTokens.contains(readOnRecordsOfAnyTypeToken)) {
			positiveTokens.add(readOnRecordsOfAnyTypeToken);
		}
	}

	private void addPrincipalPositiveReadTokens(Set<Integer> positiveTokens, Set<Integer> negativeTokens,
												int principalId) {

		int readOnRecordsOfAnyTypeToken = newAccessToken(principalId, Role.READ);

		if (!negativeTokens.contains(readOnRecordsOfAnyTypeToken)) {
			positiveTokens.add(readOnRecordsOfAnyTypeToken);
		}
	}

	private void addPrincipalNegativeTokens(Set<Integer> negativeTokens, String access,
											int principalId) {

		if (Role.READ.equals(access)) {
			negativeTokens.add(newAccessToken(principalId, Role.READ));
			negativeTokens.add(newAccessToken(principalId, Role.WRITE));
			negativeTokens.add(newAccessToken(principalId, Role.DELETE));

		} else if (Role.WRITE.equals(access)) {
			negativeTokens.add(newAccessToken(principalId, Role.WRITE));
			//TODO Check to remove this token


		} else if (Role.DELETE.equals(access)) {
			negativeTokens.add(newAccessToken(principalId, Role.DELETE));
			//TODO Check to remove this token

		}
	}

	@Override
	public List<Integer> getDefaultValue() {
		return Collections.emptyList();
	}

	@Override
	public MetadataValueType getReturnType() {
		return MetadataValueType.STRING;
	}

	@Override
	public boolean isMultiValue() {
		return true;
	}

	@Override
	public List<? extends Dependency> getDependencies() {
		return Arrays.asList(securityModelSpecialDependency, manualTokensParam, logicallyDeletedParam,
				attachedAncestorsParam, allRemovedAuthsParam, isDetachedParams,
				metadatasProvidingSecurityParams);
	}
}
