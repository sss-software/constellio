package com.constellio.model.extensions.behaviors;

import com.constellio.model.entities.Taxonomy;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.Schemas;

public class TaxonomyExtension {
	
	public Metadata[] getSortMetadatas(Taxonomy taxonomy) {
		return new Metadata[]{ Schemas.CODE, Schemas.TITLE };
	}

}
