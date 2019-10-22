package com.constellio.model.entities.calculators;

import com.constellio.model.entities.schemas.MetadataValueType;

import java.util.List;

public abstract class IntegerListMetadataValueCalculator extends AbstractMetadataValueCalculator<List<Integer>> {

	@Override
	public List<Integer> getDefaultValue() {
		return null;
	}

	@Override
	public MetadataValueType getReturnType() {
		return MetadataValueType.INTEGER;
	}

	@Override
	public boolean isMultiValue() {
		return true;
	}

}
