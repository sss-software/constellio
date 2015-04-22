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
package com.constellio.app.ui.framework.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Window;

public class BaseWindow extends Window {
	
	public static final int OVER_ADVANCED_SEARCH_FORM_Z_INDEX = 20001;
	
	private Integer zIndex = null;

	public BaseWindow() {
	}

	public BaseWindow(String caption) {
		super(caption);
	}

	public BaseWindow(String caption, Component content) {
		super(caption, content);
	}

	public final Integer getZIndex() {
		return zIndex;
	}

	public final void setZIndex(Integer zIndex) {
		this.zIndex = zIndex;
	}

	@Override
	public void attach() {
		super.attach();
		if (zIndex != null) {
			executeZIndexAdjustJavascript(zIndex);
		}
	}
	
	public static void executeZIndexAdjustJavascript(int zIndex) {
		String jsVarName = "var_" + ((int) (Math.random() * 1000)) + "_" + System.currentTimeMillis();
		StringBuffer zIndexFixJS = new StringBuffer();
		zIndexFixJS.append("var " + jsVarName + " = document.getElementsByClassName('v-window');\n");
		zIndexFixJS.append("for (i = 0; i < " + jsVarName + ".length; i++) {\n");
	    zIndexFixJS.append("    " + jsVarName + "[i].style.zIndex=" + zIndex + ";\n");
	    zIndexFixJS.append("}");
		
		JavaScript.getCurrent().execute(zIndexFixJS.toString());
	}
	
}
