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
package com.constellio.app.ui.entities;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import com.constellio.app.ui.application.ConstellioUI;

public class MetadataSchemaTypeVO implements Serializable {

	private final String code;
	private final Map<Locale, String> labels;

	public MetadataSchemaTypeVO(String code, Map<Locale, String> labels) {
		this.code = code;
		this.labels = labels;
	}

	public String getCode() {
		return code;
	}

	public String getLabel(Locale locale) {
		return labels.get(locale);
	}

	public String getLabel() {
		return getLabel(ConstellioUI.getCurrentSessionContext().getCurrentLocale());
	}
}
