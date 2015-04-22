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
package com.constellio.app.modules.rm.ui.pages.decommissioning;

import static com.constellio.app.ui.i18n.i18n.$;

import java.util.List;

import com.constellio.app.ui.framework.components.SearchResultTable;
import com.constellio.app.ui.pages.search.AdvancedSearchCriteriaComponent;
import com.constellio.app.ui.pages.search.SearchViewImpl;
import com.constellio.app.ui.pages.search.criteria.Criterion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class AddExistingContainerViewImpl extends SearchViewImpl<AddExistingContainerPresenter>
		implements AddExistingContainerView {
	private AdvancedSearchCriteriaComponent criteria;

	public AddExistingContainerViewImpl() {
		presenter = new AddExistingContainerPresenter(this);
		criteria = new AdvancedSearchCriteriaComponent(presenter);
	}

	@Override
	protected String getTitle() {
		return $("AddExistingContainerView.viewTitle");
	}

	@Override
	protected ClickListener getBackButtonClickListener() {
		return new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				presenter.backButtonClicked();
			}
		};
	}

	@Override
	public void addEmptyCriterion() {
		criteria.addEmptyCriterion();
	}

	@Override
	public void setCriteriaSchemaType(String schemaType) {
		criteria.setSchemaType(schemaType);
	}

	@Override
	public List<Criterion> getSearchCriteria() {
		return criteria.getSearchCriteria();
	}

	@Override
	protected Component buildSearchUI() {
		Button addCriterion = new Button($("AddExistingContainerView.addCriterion"));
		addCriterion.addStyleName(ValoTheme.BUTTON_LINK);
		addCriterion.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				presenter.addCriterionRequested();
			}
		});

		criteria.setWidth("100%");

		Button searchButton = new Button($("AddExistingContainerView.search"));
		searchButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		searchButton.setEnabled(true);
		searchButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				presenter.searchRequested();
			}
		});

		VerticalLayout searchUI = new VerticalLayout(addCriterion, criteria, searchButton);
		searchUI.setComponentAlignment(addCriterion, Alignment.MIDDLE_RIGHT);
		searchUI.setSpacing(true);

		return searchUI;
	}

	@Override
	protected Component buildSummary(SearchResultTable results) {
		Button addContainers = new Button($("AddExistingContainerView.addContainers"));
		addContainers.addStyleName(ValoTheme.BUTTON_LINK);
		addContainers.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				presenter.containerAdditionRequested(getSelectedRecordIds());
			}
		});

		return results.createSummary(addContainers);
	}
}
