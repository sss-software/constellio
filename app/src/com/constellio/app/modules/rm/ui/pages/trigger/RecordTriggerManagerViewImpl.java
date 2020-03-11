package com.constellio.app.modules.rm.ui.pages.trigger;

import com.constellio.app.modules.rm.navigation.RMViews;
import com.constellio.app.modules.rm.ui.pages.cart.CartsListViewImpl;
import com.constellio.app.ui.application.Navigation;
import com.constellio.app.ui.framework.buttons.BaseButton;
import com.constellio.app.ui.framework.components.breadcrumb.BaseBreadcrumbTrail;
import com.constellio.app.ui.framework.components.breadcrumb.IntermediateBreadCrumbTailItem;
import com.constellio.app.ui.framework.components.breadcrumb.TitleBreadcrumbTrail;
import com.constellio.app.ui.framework.components.table.RecordVOTable;
import com.constellio.app.ui.framework.containers.RecordVOLazyContainer;
import com.constellio.app.ui.pages.base.BaseViewImpl;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import java.util.Arrays;
import java.util.List;

import static com.constellio.app.ui.i18n.i18n.$;

public class RecordTriggerManagerViewImpl extends BaseViewImpl implements RecordTriggerManagerView {
	private RecordTriggerManagerPresenter presenter;
	private RecordVOLazyContainer dataSource;
	private String recordTitle;

	public RecordTriggerManagerViewImpl() {
		this.presenter = new RecordTriggerManagerPresenter(this);
	}

	@Override
	protected void initBeforeCreateComponents(ViewChangeEvent event) {
		presenter.forParams(event.getParameters());
	}

	public void setRecordTitle(String title) {
		this.recordTitle = title;
	}

	@Override
	protected List<Button> getQuickActionMenuButtons() {
		return Arrays.asList(buildAddRuleQuickActionButton());
	}

	//	@Override
	//	protected boolean isOnlyQuickMenuActionVisible() {
	//		return true;
	//	}

	private Button buildAddRuleQuickActionButton() {
		Button addRuleButton = new BaseButton($("addRule")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				//				navigate().to(RMViews.class)
			}
		};

		return addRuleButton;
	}

	@Override
	public List<Button> getActionMenuButtons() {
		return Arrays.asList(new Button("aahaha"));
	}

	@Override
	protected String getTitle() {
		return $("RecordTriggerManagerViewImpl.title", recordTitle);
	}

	@Override
	protected Component buildMainComponent(ViewChangeEvent event) {
		VerticalLayout verticalLayout = new VerticalLayout();

		dataSource = new RecordVOLazyContainer(presenter.getDataProvider());
		RecordVOTable recordTable = new RecordVOTable(dataSource);

		verticalLayout.addComponent(recordTable);
		verticalLayout.setSizeFull();
		verticalLayout.setMargin(new MarginInfo(true, true));
		recordTable.setSizeFull();
		verticalLayout.setExpandRatio(recordTable, 1);

		return verticalLayout;
	}


	@Override
	protected boolean isBreadcrumbsVisible() {
		return true;
	}

	@Override
	protected List<Button> buildActionMenuButtons(ViewChangeEvent event) {
		return Arrays.asList(new Button("ahaha"));
	}

	@Override
	protected BaseBreadcrumbTrail buildBreadcrumbTrail() {
		return new TitleBreadcrumbTrail(this, "title", false) {
			@Override
			public List<? extends IntermediateBreadCrumbTailItem> getIntermediateItems() {
				return Arrays.asList(new IntermediateBreadCrumbTailItem() {
					@Override
					public String getTitle() {
						return $(CartsListViewImpl.TITLE);
					}

					@Override
					public void activate(Navigation navigate) {
						navigate.to(RMViews.class).listCarts();
					}

					@Override
					public boolean isEnabled() {
						return true;
					}
				});
			}
		};
	}
}
