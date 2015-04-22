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
package com.constellio.app.ui.pages.base;

import java.io.Serializable;
import java.util.Locale;

import com.constellio.app.ui.entities.UserVO;

public interface SessionContext extends Serializable {

	UserVO getCurrentUser();

	void setCurrentUser(UserVO user);

	String getCurrentCollection();

	void setCurrentCollection(String collection);

	Locale getCurrentLocale();

	void setCurrentLocale(Locale locale);

	String getCurrentUserIPAddress();
}
