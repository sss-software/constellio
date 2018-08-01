package com.constellio.model.services.schemas.calculators;

import com.constellio.model.entities.calculators.CalculatorParameters;
import com.constellio.model.entities.calculators.ReferenceListMetadataValueCalculator;
import com.constellio.model.entities.calculators.dependencies.*;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.entities.security.global.AuthorizationDetails;
import com.constellio.model.services.schemas.builders.CommonMetadataBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NonTaxonomyAuthorizationsCalculator extends ReferenceListMetadataValueCalculator {

	LocalDependency<List<String>> allRemovedAuthsParam = LocalDependency
			.toAStringList(CommonMetadataBuilder.ALL_REMOVED_AUTHS);

	SpecialDependency<HierarchyDependencyValue> hierarchyDependencyValuesParam = SpecialDependencies.HIERARCHY;

	SpecialDependency<AllAuthorizationsTargettingRecordDependencyValue> authorizationsParam = SpecialDependencies.AURHORIZATIONS_TARGETTING_RECORD;

	LocalDependency<Boolean> isDetachedParams = LocalDependency.toABoolean(Schemas.IS_DETACHED_AUTHORIZATIONS.getLocalCode());

	@Override
	public List<String> calculate(CalculatorParameters parameters) {
		AllAuthorizationsTargettingRecordDependencyValue authorizations = parameters.get(authorizationsParam);
		List<String> allRemovedAuths = parameters.get(allRemovedAuthsParam);
		HierarchyDependencyValue hierarchyDependencyValues = parameters.get(hierarchyDependencyValuesParam);
		boolean detached = Boolean.TRUE.equals(parameters.get(isDetachedParams));

		List<String> returnedIds = new ArrayList<>();

		if (!parameters.isPrincipalTaxonomyConcept()) {

			for (AuthorizationDetails auth : authorizations.getAuthorizationDetailsOnRecord()) {
				if (auth.isActiveAuthorization()) {
					returnedIds.add(auth.getId());
				}
			}

			for (AuthorizationDetails auth : authorizations.getAuthorizationDetailsOnMetadatasProvidingSecurity()) {
				if (auth.isActiveAuthorization()) {
					returnedIds.add(auth.getId());
				}
			}

			if (!detached) {
				for (String auth : hierarchyDependencyValues.getInheritedNonTaxonomyAuthorizations()) {
					if (!allRemovedAuths.contains(auth)
						&& !authorizations.isInheritedAuthorizationsOverridenByMetadatasProvidingSecurity()) {
						returnedIds.add(auth);
					}
				}
			}

		}

		return returnedIds;
	}

	@Override
	public List<? extends Dependency> getDependencies() {
		return Arrays.asList(authorizationsParam, allRemovedAuthsParam, hierarchyDependencyValuesParam, isDetachedParams);
	}
}
