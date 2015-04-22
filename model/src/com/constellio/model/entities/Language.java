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
package com.constellio.model.entities;

public enum Language {

	UNKNOWN("unknown"),
	//Arabic("ar"),
	//	Armenian,
	//	Basque,
	//	Bulgarian,
	//	Catalan,
	//	Chinese,
	//	Japanese,
	//	Korean,
	//	Czech,
	//	Danish,
	//	Dutch,
	English("en"),
	//	Finnish,
	French("fr");
	//	Galician,
	//  German("de"),
	//	Greek,
	//	Hebrew,
	//	Hindi,
	//	Hungarian,
	//	Indonesian,
	//	Italian,
	//	Norwegian,
	//	Persian,
	//	Polish,
	//  Portuguese("pt"),
	//	Romanian,
	//	Russian,
	//  Spanish("es"),
	//	Swedish,
	//	Thai,
	//	Turkish;

	final String code;

	Language(String code) {
		this.code = code;
	}

	public static boolean isSupported(String languageCode) {

		for (Language language : Language.values()) {
			if (language.code.equals(languageCode)) {
				return true;
			}
		}
		return false;
	}

	public String getCode() {
		return code;
	}

}
