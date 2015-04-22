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
package com.constellio.model.entities.security.global;

import com.constellio.model.entities.EnumWithSmallCode;

public enum UserCredentialStatus implements EnumWithSmallCode {

	ACTIVE("a"), PENDING("p"), SUPENDED("s"), DELETED("d");

	private String code;

	UserCredentialStatus(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
