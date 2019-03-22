package com.constellio.app.entities.schemasDisplay.enums;

import com.constellio.model.entities.schemas.MetadataValueType;

import java.util.ArrayList;
import java.util.List;

public enum MetadataSortingType {

	ENTRY_ORDER,
	ALPHANUMERICAL_ORDER;

	public static String getCaptionFor(MetadataSortingType type) {
		String caption = "";

		switch (type) {
			case ENTRY_ORDER:
				caption = "MetadataSortingType.entryOrder";
				break;
			case ALPHANUMERICAL_ORDER:
				caption = "MetadataSortingType.alphanumericalOrder";
				break;
		}

		return caption;
	}

	public static List<MetadataSortingType> getAvailableMetadataSortingTypesFor(MetadataValueType type,
																				boolean isMultivalue) {
		List<MetadataSortingType> displayTypes = new ArrayList<>();


		if (type != null && type.equals(MetadataValueType.REFERENCE) &&	isMultivalue) {

			displayTypes.add(ALPHANUMERICAL_ORDER);
		}

		displayTypes.add(ENTRY_ORDER);

		return displayTypes;
	}
}