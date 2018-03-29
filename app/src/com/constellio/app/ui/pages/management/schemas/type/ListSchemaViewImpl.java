package com.constellio.app.ui.pages.management.schemas.type;

import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.framework.buttons.AddButton;
import com.constellio.app.ui.framework.components.menuBar.BaseMenuBar;
import com.constellio.app.ui.framework.components.table.BaseTable;
import com.constellio.app.ui.framework.data.SchemaVODataProvider;
import com.constellio.app.ui.pages.base.BaseViewImpl;
import com.constellio.app.ui.params.ParamUtils;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.Schemas;
import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import java.util.Map;

import static com.constellio.app.ui.i18n.i18n.$;

public class ListSchemaViewImpl extends BaseViewImpl implements ListSchemaView, ClickListener {
	ListSchemaPresenter presenter;
	public static final String OPTIONS_COL = "options";

	public ListSchemaViewImpl() {
		this.presenter = new ListSchemaPresenter(this);
	}

	@Override
	protected boolean isFullWidthIfActionMenuAbsent() {
		return true;
	}

	@Override
	protected String getTitle() {
		return $("ListSchemaView.viewTitle");
	}

	@Override
	protected ClickListener getBackButtonClickListener() {
		return this;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		presenter.backButtonClicked();
	}

	@Override
	protected Component buildMainComponent(ViewChangeEvent event) {
		String parameters = event.getParameters();
		Map<String, String> paramsMap = ParamUtils.getParamsMap(parameters);
		presenter.setSchemaTypeCode(paramsMap.get("schemaTypeCode"));
		presenter.setParameters(paramsMap);

		VerticalLayout viewLayout = new VerticalLayout();
		viewLayout.setSizeFull();

		Button addButton = new AddButton() {
			@Override
			public void buttonClick(ClickEvent event) {
				presenter.addButtonClicked();
			}
		};

		viewLayout.addComponents(addButton, buildTables());
		viewLayout.setComponentAlignment(addButton, Alignment.TOP_RIGHT);
		return viewLayout;
	}

	public void addSchemaToTable(final MetadataSchemaVO metadataSchemaVO, Container indexedContainer) {
		MenuBar menuBar = new BaseMenuBar();
		MenuBar.MenuItem rootItem = menuBar.addItem("", FontAwesome.BARS, null);


		final MenuBar.Command editListener = new MenuBar.Command() {
			@Override
			public void menuSelected(MenuBar.MenuItem selectedItem) {
				presenter.editButtonClicked(metadataSchemaVO);
			}
		};


		rootItem.addItem($("edit"), editListener);

		final MenuBar.Command formOrderListener = new MenuBar.Command() {
			@Override
			public void menuSelected(MenuBar.MenuItem selectedItem) {
				presenter.orderButtonClicked(metadataSchemaVO);
			}
		};

		rootItem.addItem($("ListSchemaView.button.form"), formOrderListener);

		final MenuBar.Command formListener = new MenuBar.Command() {
			@Override
			public void menuSelected(MenuBar.MenuItem selectedItem) {
				presenter.formButtonClicked(metadataSchemaVO);
			}
		};

		rootItem.addItem($("ListSchemaView.button.display"), formListener);

		if (!(metadataSchemaVO == null || metadataSchemaVO.getCode() == null || !metadataSchemaVO.getCode().endsWith("default"))) {
			final MenuBar.Command tableListener = new MenuBar.Command() {
				@Override
				public void menuSelected(MenuBar.MenuItem selectedItem) {
					presenter.tableButtonClicked();
				}
			};

			rootItem.addItem($("ListSchemaView.button.table"), tableListener);

		}


		final MenuBar.Command searchListener = new MenuBar.Command() {
			@Override
			public void menuSelected(MenuBar.MenuItem selectedItem) {
				presenter.searchButtonClicked(metadataSchemaVO);
			}
		};

		rootItem.addItem($("ListSchemaView.button.search"), searchListener);

		if (super.isVisible() && presenter.isDeleteButtonVisible(metadataSchemaVO.getCode())) {
			final MenuBar.Command deleteListener = new MenuBar.Command() {
				@Override
				public void menuSelected(MenuBar.MenuItem selectedItem) {
					presenter.deleteButtonClicked(metadataSchemaVO.getCode());
				}
			};
			rootItem.addItem($("delete"), deleteListener);
		}


		HorizontalLayout buttonVerticalLayout = new HorizontalLayout();

		Button metadataButton = new Button($("ListSchemaView.button.metadata"));
		metadataButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				presenter.orderButtonClicked(metadataSchemaVO);
			}
		});

		buttonVerticalLayout.addComponent(metadataButton);
		buttonVerticalLayout.addComponent(menuBar);
		buttonVerticalLayout.setSpacing(true);


		indexedContainer.addItem(metadataSchemaVO);

		indexedContainer.getContainerProperty(metadataSchemaVO, "title").setValue(metadataSchemaVO.getLabel());
		indexedContainer.getContainerProperty(metadataSchemaVO, "localCode").setValue(metadataSchemaVO.getLocalCode());
		indexedContainer.getContainerProperty(metadataSchemaVO, OPTIONS_COL).setValue(buttonVerticalLayout);
	}

	private Component buildTables() {
		final SchemaVODataProvider dataProvider = presenter.getDataProvider();

		Container indexContainer = new IndexedContainer();
		indexContainer.addContainerProperty("localCode", String.class, "");
		indexContainer.addContainerProperty("title", String.class, "");
		indexContainer.addContainerProperty(OPTIONS_COL, HorizontalLayout.class, null);

		for(Integer integer: dataProvider.list()) {
			MetadataSchemaVO metadataSchemaVO = dataProvider.getSchemaVO(integer);
			addSchemaToTable(metadataSchemaVO, indexContainer);
		}

		Table table = new BaseTable(getClass().getName(), $("ListSchemaView.tableTitle", indexContainer.size()));
		table.setSizeFull();
		table.setContainerDataSource(indexContainer);
		table.setPageLength(Math.min(15, indexContainer.size()));
		table.setColumnHeader("localCode", $("ListSchemaView.localCode"));
		table.setColumnHeader("title", $("ListSchemaView.caption"));
		table.setColumnHeader(OPTIONS_COL, "");
		table.setColumnExpandRatio("title", 1);
		table.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				presenter.editButtonClicked((MetadataSchemaVO) event.getItemId());
			}
		});

		return table;
	}

}
