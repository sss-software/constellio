package com.constellio.model.services.schemas.calculators;

import com.constellio.data.utils.LangUtils;
import com.constellio.model.entities.calculators.CalculatorParameters;
import com.constellio.model.entities.calculators.IntegerListMetadataValueCalculator;
import com.constellio.model.entities.calculators.dependencies.Dependency;
import com.constellio.model.entities.calculators.dependencies.LocalDependency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.constellio.model.services.schemas.builders.CommonMetadataBuilder.LOGICALLY_DELETED;
import static com.constellio.model.services.schemas.builders.CommonMetadataBuilder.TOKENS;

public class DefaultIntAccessTokensOfHierarchyCalculator extends IntegerListMetadataValueCalculator {

	LocalDependency<Boolean> logicallyDeletedParam = LocalDependency.toABoolean(LOGICALLY_DELETED);
	LocalDependency<List<Integer>> tokensParam = LocalDependency.toAStringList(TOKENS).whichIsRequired();

	@Override
	public List<Integer> calculate(CalculatorParameters parameters) {
		Set<Integer> allTokens = new HashSet<>();

		List<Integer> tokens = parameters.get(tokensParam);

		Boolean logicallyDeleted = parameters.get(logicallyDeletedParam);

		if (tokens != null && LangUtils.isFalseOrNull(logicallyDeleted)) {
			allTokens.addAll(tokens);
		}

		List<Integer> tokensList = new ArrayList<>(allTokens);
		Collections.sort(tokensList);
		return tokensList;

	}

	@Override
	public List<? extends Dependency> getDependencies() {
		return Arrays.asList(tokensParam, logicallyDeletedParam);
	}
}
