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

import static com.constellio.app.ui.i18n.i18n.$;

import java.util.ArrayList;
import java.util.List;

import com.constellio.app.services.factories.ConstellioFactories;
import com.constellio.app.ui.application.ConstellioNavigator;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.framework.buttons.BackButton;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public abstract class BaseViewImpl extends VerticalLayout implements View, BaseView {

	public static final String BACK_BUTTON_CODE = "seleniumBackButtonCode";
	private Label titleLabel;

	private BackButton backButton;

	private HorizontalLayout titleBackButtonLayout;

	private Component mainComponent;
	private Component actionMenu;

	@Override
	public final void enter(ViewChangeEvent event) {
		initBeforeCreateComponents(event);

		addStyleName("main-component-wrapper");
		setSizeFull();

		removeAllComponents();

		titleBackButtonLayout = new HorizontalLayout();
		titleBackButtonLayout.setWidth("100%");

		String title = getTitle();
		if (title != null) {
			titleLabel = new Label(title);
			titleLabel.addStyleName(ValoTheme.LABEL_H1);
		}

		backButton = new BackButton();
		ClickListener backButtonClickListener = getBackButtonClickListener();
		if (backButtonClickListener != null) {
			backButton.setVisible(true);
			backButton.addStyleName(BACK_BUTTON_CODE);
			backButton.addClickListener(backButtonClickListener);
		} else {
			backButton.setVisible(false);
		}

		mainComponent = buildMainComponent(event);
		mainComponent.addStyleName("main-component");

		actionMenu = buildActionMenu(event);
		if (actionMenu != null || !isFullWidthIfActionMenuAbsent()) {
			addStyleName("action-menu-wrapper");
			if (actionMenu != null) {
				actionMenu.addStyleName("action-menu");
			}
		}

		if (titleLabel != null || backButton.isVisible()) {
			addComponent(titleBackButtonLayout);
		}

		addComponent(mainComponent);
		if (actionMenu != null) {
			addComponent(actionMenu);
		}

		if (titleLabel != null || backButton.isVisible()) {
			if (titleLabel != null) {
				titleBackButtonLayout.addComponents(titleLabel);
			}
			titleBackButtonLayout.addComponents(backButton);
		}

		setExpandRatio(mainComponent, 1f);
		if (titleLabel != null) {
			titleBackButtonLayout.setExpandRatio(titleLabel, 1);
		}

		afterViewAssembled(event);
	}

	protected void initBeforeCreateComponents(ViewChangeEvent event) {
	}

	protected void afterViewAssembled(ViewChangeEvent event) {
	}

	protected boolean isFullWidthIfActionMenuAbsent() {
		return false;
	}

	protected String getTitle() {
		return getClass().getSimpleName();
	}

	/**
	 * Adapted from https://vaadin.com/forum#!/thread/8150555/8171634
	 *
	 * @param event
	 * @return
	 */
	protected Component buildActionMenu(ViewChangeEvent event) {
		VerticalLayout actionMenuLayout;
		List<Button> actionMenuButtons = buildActionMenuButtons(event);
		if (actionMenuButtons == null || actionMenuButtons.isEmpty()) {
			actionMenuLayout = null;
		} else {
			actionMenuLayout = new VerticalLayout();
			actionMenuLayout.setSizeUndefined();
			for (Button actionMenuButton : actionMenuButtons) {
				actionMenuButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
				actionMenuButton.removeStyleName(ValoTheme.BUTTON_LINK);
				actionMenuButton.addStyleName("action-menu-button");
				actionMenuLayout.addComponent(actionMenuButton);
			}
		}

		return actionMenuLayout;
	}

	protected List<Button> buildActionMenuButtons(ViewChangeEvent event) {
		List<Button> actionMenuButtons = new ArrayList<Button>();
		return actionMenuButtons;
	}

	@Override
	public String getCollection() {
		return ConstellioUI.getCurrentSessionContext().getCurrentCollection();
	}

	@Override
	public ConstellioNavigator navigateTo() {
		return ConstellioUI.getCurrent().navigateTo();
	}

	@Override
	public void showMessage(String message) {
		Notification.show(message, Type.WARNING_MESSAGE);
	}

	@Override
	public void showErrorMessage(String errorMessage) {
		Notification.show(errorMessage + "\n\n" + $("clickToClose"), Type.ERROR_MESSAGE);
	}

	@Override
	public SessionContext getSessionContext() {
		return ConstellioUI.getCurrentSessionContext();
	}

	@Override
	public ConstellioFactories getConstellioFactories() {
		return ConstellioFactories.getInstance();
	}

	protected ClickListener getBackButtonClickListener() {
		return null;
	}

	protected abstract Component buildMainComponent(ViewChangeEvent event);

}
