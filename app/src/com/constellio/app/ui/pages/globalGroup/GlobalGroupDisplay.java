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
package com.constellio.app.ui.pages.globalGroup;

import static com.constellio.app.ui.i18n.i18n.$;

import com.constellio.app.ui.entities.GlobalGroupVO;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class GlobalGroupDisplay extends CustomComponent {

	public static final String STYLE_CAPTION = "display-globalGroup-caption";
	public static final String STYLE_VALUE = "display-globalGroup-value";

	GridLayout mainLayout;

	public GlobalGroupDisplay(GlobalGroupVO globalGroupVO) {
		setSizeFull();

		mainLayout = new GridLayout(2, 1);

		int row = 0;
		Component displayComponentCode = new Label(globalGroupVO.getCode());
		if (displayComponentCode != null) {
			String caption = $("Code");
			row = createComponentRow(globalGroupVO, row, displayComponentCode, caption);
		}

		Component displayComponentName = new Label(globalGroupVO.getName());
		if (displayComponentName != null) {
			String caption = $("Name");
			createComponentRow(globalGroupVO, row, displayComponentName, caption);
		}

		setCompositionRoot(mainLayout);
	}

	private int createComponentRow(GlobalGroupVO globalGroupVO, int row, Component displayComponent, String caption) {
		Label captionLabel = new Label(caption);

		captionLabel.addStyleName(STYLE_CAPTION);
		captionLabel.addStyleName(STYLE_CAPTION + "-" + globalGroupVO.getCode());

		displayComponent.addStyleName(STYLE_VALUE);
		displayComponent.addStyleName(STYLE_VALUE + "-" + globalGroupVO.getCode());

		mainLayout.addComponent(captionLabel, 0, row);
		mainLayout.addComponent(displayComponent, 1, row);

		row++;
		mainLayout.setRows(row + 1);
		return row;
	}

}
