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
package com.constellio.model.services.contents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.constellio.model.entities.records.ParsedContent;

public class ParsedContentConverter {

	private static String SEPARATOR = "----------";

	public String convertToString(ParsedContent parsedContent) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(parsedContent.getLanguage() + "\n");
		stringBuilder.append(parsedContent.getLength() + "\n");
		stringBuilder.append(parsedContent.getMimeType() + "\n");
		for (Entry<String, Object> mapEntry : parsedContent.getProperties().entrySet()) {
			stringBuilder.append(mapEntry.getKey() + "=" + mapEntry.getValue() + "\n");
		}

		stringBuilder.append(SEPARATOR);
		stringBuilder.append(parsedContent.getParsedContent());
		return stringBuilder.toString();
	}

	public ParsedContent convertToParsedContent(String string) {
		int separatorIndex = string.indexOf(SEPARATOR);

		Map<String, Object> parameters = new HashMap<>();

		String[] attributeLines = string.substring(0, separatorIndex).split("\n");
		String lang = attributeLines[0];
		long length = Long.valueOf(attributeLines[1]);
		String mime = attributeLines[2];
		for (int i = 3; i < attributeLines.length; i++) {
			String attributeLine = attributeLines[i];
			int equalSignIndex = attributeLine.indexOf("=");
			String key = attributeLine.substring(0, equalSignIndex);
			String value = attributeLine.substring(equalSignIndex + 1);
			if(key.contains("List:")) { 
				putStringListInHashMap(parameters, key, value);
			} else {
				parameters.put(key, value);
			}
		}
		String parsedContent = string.substring(separatorIndex + SEPARATOR.length());
		return new ParsedContent(parsedContent, lang, mime, length, parameters);
	}

	private void putStringListInHashMap(Map<String, Object> parameters, String key, String value) {
		value = value.substring(1, value.length() - 1);
		String[] valuesAfterSplit = value.split(",");
		List<String> valueList = new ArrayList<String>();
		for(String aValue : valuesAfterSplit) { 
			valueList.add(aValue.trim());
		}
		parameters.put(key, valueList);
	}
}
