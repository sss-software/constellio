package com.constellio.app.ui.framework.components.viewers.panel;

import com.constellio.app.modules.rm.ui.components.content.ConstellioAgentLink;
import com.constellio.app.modules.rm.ui.pages.document.DisplayDocumentViewImpl;
import com.constellio.app.modules.rm.ui.pages.document.DisplayDocumentWindow;
import com.constellio.app.modules.rm.ui.pages.folder.DisplayFolderViewImpl;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.entities.SearchResultVO;
import com.constellio.app.ui.entities.UserVO;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.framework.buttons.BaseButton;
import com.constellio.app.ui.framework.buttons.IconButton;
import com.constellio.app.ui.framework.buttons.SelectDeselectAllButton;
import com.constellio.app.ui.framework.components.RecordDisplayFactory;
import com.constellio.app.ui.framework.components.SearchResultDisplay;
import com.constellio.app.ui.framework.components.ViewWindow;
import com.constellio.app.ui.framework.components.display.ReferenceDisplay;
import com.constellio.app.ui.framework.components.layouts.I18NHorizontalLayout;
import com.constellio.app.ui.framework.components.mouseover.NiceTitle;
import com.constellio.app.ui.framework.components.table.BaseTable;
import com.constellio.app.ui.framework.components.table.BaseTable.PagingControls;
import com.constellio.app.ui.framework.components.table.BaseTable.SelectionChangeListener;
import com.constellio.app.ui.framework.components.table.BaseTable.SelectionManager;
import com.constellio.app.ui.framework.components.table.RecordVOTable;
import com.constellio.app.ui.framework.containers.ContainerAdapter;
import com.constellio.app.ui.framework.containers.RecordVOContainer;
import com.constellio.app.ui.pages.management.schemaRecords.DisplaySchemaRecordWindow;
import com.constellio.app.ui.util.ComponentTreeUtils;
import com.constellio.app.ui.util.ResponsiveUtils;
import com.constellio.model.entities.records.Record;
import com.constellio.model.services.records.RecordServices;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.peter.contextmenu.ContextMenu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.constellio.app.ui.i18n.i18n.$;

//@JavaScript({ "theme://jquery/jquery-2.1.4.min.js", "theme://scroll/fix-vertical-scroll.js" })
public class ViewableRecordVOTablePanel extends I18NHorizontalLayout {

	public static enum TableMode {
		LIST, TABLE;
	} 
	
	private VerticalLayout tableLayout;

	private I18NHorizontalLayout tableButtonsLayout;

	private I18NHorizontalLayout previousNextButtonsLayout;

	private I18NHorizontalLayout tableActionButtonsLayout;

	private VerticalLayout closeButtonViewerMetadataLayout;

	private RecordVOContainer recordVOContainer;

	private BaseTable table;
	
	private ViewerMetadataPanel viewerMetadataPanel;

	private BaseButton previousButton;

	private BaseButton nextButton;

	private TableModeButton tableModeButton;

	private SelectDeselectAllButton selectDeselectAllToggleButton;
	
	private BaseButton closeViewerButton;

	private Object selectedItemId;
	
	private RecordVO selectedRecordVO;

	private Object previousItemId;

	private Object nextItemId;

	private List<SelectionChangeListener> selectionChangeListeners = new ArrayList<>();

	private List<ItemClickListener> itemClickListeners = new ArrayList<>();
	
	private List<TableCompressListener> tableCompressListeners = new ArrayList<>();

	private List<TableModeChangeListener> tableModeChangeListeners = new ArrayList<>();

	private TableMode tableMode = TableMode.LIST;

	private PagingControls pagingControls;

	private List<Object> tableModeVisibleColumns = new ArrayList<>();

	private Map<Object, String> tableModeColumnHeaders = new HashMap<>();

	private Map<Object, Integer> tableModeColumnExpandRatios = new HashMap<>();

	public ViewableRecordVOTablePanel(RecordVOContainer container) {
		this(container, TableMode.LIST);
	}

	public ViewableRecordVOTablePanel(RecordVOContainer container, TableMode tableMode) {
		this.recordVOContainer = container;
		this.tableMode = tableMode != null ? tableMode : TableMode.LIST;
		buildUI();
	}

	public RecordVOContainer getRecordVOContainer() {
		return recordVOContainer;
	}

	private void buildUI() {
		setSizeFull();
		setSpacing(true);
		addStyleName("viewable-record-table-panel");

		table = buildResultsTable();
		if (isSelectColumn()) {
			selectDeselectAllToggleButton = table.newSelectDeselectAllToggleButton();
			selectDeselectAllToggleButton.addStyleName(ValoTheme.BUTTON_LINK);
		}

		viewerMetadataPanel = buildViewerMetadataPanel();
		tableModeButton = buildTableModeButton();
		previousButton = buildPreviousButton();
		nextButton = buildNextButton();
		closeViewerButton = buildCloseViewerButton();

		tableLayout = new VerticalLayout();
		tableLayout.setHeight("100%");

		tableButtonsLayout = new I18NHorizontalLayout();
		tableButtonsLayout.addStyleName("table-buttons-layout");
		tableButtonsLayout.setWidth("100%");
		tableButtonsLayout.setSpacing(true);

		tableActionButtonsLayout = new I18NHorizontalLayout();
		tableActionButtonsLayout.addStyleName("table-action-buttons-layout");
		tableActionButtonsLayout.setSpacing(true);

		previousNextButtonsLayout = new I18NHorizontalLayout();
		previousNextButtonsLayout.addStyleName("previous-next-buttons-layout");
		previousNextButtonsLayout.setSpacing(true);

		if (isSelectColumn()) {
			tableActionButtonsLayout.addComponent(selectDeselectAllToggleButton);
		}
		tableActionButtonsLayout.addComponent(tableModeButton);

		previousNextButtonsLayout.addComponents(previousButton, nextButton);
		tableButtonsLayout.addComponents(tableActionButtonsLayout, previousNextButtonsLayout);

		tableButtonsLayout.setComponentAlignment(tableActionButtonsLayout, Alignment.TOP_LEFT);
		tableButtonsLayout.setComponentAlignment(previousNextButtonsLayout, Alignment.TOP_RIGHT);

		tableLayout.addComponent(tableButtonsLayout);
		tableLayout.addComponent(table);
		if (table.isPaged()) {
			tableLayout.addComponent(pagingControls = table.createPagingControls());
		}

		closeButtonViewerMetadataLayout = new VerticalLayout(closeViewerButton, viewerMetadataPanel);
		closeButtonViewerMetadataLayout.addStyleName("close-button-viewer-metadata-layout");
		closeButtonViewerMetadataLayout.setId("close-button-viewer-metadata-layout");
		closeButtonViewerMetadataLayout.setHeight("100%");
		closeButtonViewerMetadataLayout.setComponentAlignment(closeViewerButton, Alignment.TOP_RIGHT);
		//		closeButtonViewerMetadataLayout.setWidthUndefined();

		addComponent(tableLayout);
		addComponent(closeButtonViewerMetadataLayout);
		if (isCompressionSupported()) {
			addTableCompressListener(new TableCompressListener() {
				@Override
				public void tableCompressChange(TableCompressEvent event) {
					if (table instanceof ViewableRecordVOTable) {
						((ViewableRecordVOTable) table).setCompressed(event.isCompressed());
					}
				}
			});
		} else {
			closeButtonViewerMetadataLayout.setVisible(false);
		}
		adjustTableExpansion();
	}

	int computeCompressedWidth() {
		return Page.getCurrent().getBrowserWindowWidth() > 1400 ? 650 : 500;
	}

	boolean isCompressionSupported() {
		return ResponsiveUtils.isDesktop() && !isNested();
	}

	protected boolean isNested() {
		return false;
	}

	protected Component newSearchResultComponent(Object itemId) {
		UserVO currentUser = ConstellioUI.getCurrentSessionContext().getCurrentUser();
		RecordDisplayFactory displayFactory = new RecordDisplayFactory(currentUser);
		RecordVO recordVO = recordVOContainer.getRecordVO(itemId);
		SearchResultVO searchResultVO = new SearchResultVO(recordVO, new HashMap<String, List<String>>());
		SearchResultDisplay searchResultDisplay = displayFactory.build(searchResultVO, null, null, null, null);
		searchResultDisplay.getTitleComponent().setIcon(null);
		return searchResultDisplay;
	}

	//	private void ensureHeight(Object itemId) {
	//		int l = table.getPageLength();
	//		int index = table.indexOfId(itemId);
	//		int indexToSelectAbove = index - (l / 2);
	//		if (indexToSelectAbove < 0) {
	//			indexToSelectAbove = 0;
	//		}
	//		table.setCurrentPageFirstItemIndex(indexToSelectAbove);
	//	}

	void adjustTableExpansion() {
		if (tableMode == TableMode.LIST) {
			if (isCompressionSupported()) {
				String compressedStyleName = "viewable-record-table-compressed";
				if (selectedItemId != null) {
					if (!closeButtonViewerMetadataLayout.isVisible()) {
						int compressedWidth = computeCompressedWidth();
						if (table != null) {
							int searchResultPropertyWidth = compressedWidth - BaseTable.SELECT_PROPERTY_WIDTH - ViewableRecordVOContainer.THUMBNAIL_WIDTH - 3;
							table.setColumnWidth(ViewableRecordVOContainer.SEARCH_RESULT_PROPERTY, searchResultPropertyWidth);
							table.addStyleName(compressedStyleName);
						}
						closeButtonViewerMetadataLayout.setVisible(true);
						tableLayout.setWidth(compressedWidth + "px");
						setExpandRatio(tableLayout, 0);
						setExpandRatio(closeButtonViewerMetadataLayout, 1);
					}
				} else {
					if (closeButtonViewerMetadataLayout.isVisible()) {
						if (table != null) {
							table.setColumnWidth(ViewableRecordVOContainer.SEARCH_RESULT_PROPERTY, -1);
							table.removeStyleName(compressedStyleName);
						}
						closeButtonViewerMetadataLayout.setVisible(false);
						tableLayout.setWidth("100%");
						setExpandRatio(tableLayout, 1);
						setExpandRatio(closeButtonViewerMetadataLayout, 0);
					}
				}
			}
		} else {
			closeButtonViewerMetadataLayout.setVisible(false);
			tableLayout.setWidth("100%");
			setExpandRatio(tableLayout, 1);
			setExpandRatio(closeButtonViewerMetadataLayout, 0);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private BaseTable buildResultsTable() {
		BaseTable resultsTable;
		if (tableMode == TableMode.LIST) {
			ViewableRecordVOContainer viewableRecordVOContainer = new ViewableRecordVOContainer(recordVOContainer) {
				@Override
				protected Component getRecordDisplay(Object itemId) {
					return ViewableRecordVOTablePanel.this.newSearchResultComponent(itemId);
				}
			};

			final ViewableRecordVOTable viewableRecordVOTable = new ViewableRecordVOTable(viewableRecordVOContainer) {
				@Override
				public boolean isSelectColumn() {
					return ViewableRecordVOTablePanel.this.isSelectColumn();
				}

				@Override
				public boolean isIndexColumn() {
					return ViewableRecordVOTablePanel.this.isIndexColumn();
				}

				@Override
				protected SelectionManager newSelectionManager() {
					SelectionManager selectionManager = ViewableRecordVOTablePanel.this.newSelectionManager();
					if (selectionManager == null) {
						selectionManager = super.newSelectionManager();
					}
					return selectionManager;
				}

				@Override
				public boolean isPaged() {
					return ViewableRecordVOTablePanel.this.isPagedInListMode();
				}

				@Override
				protected RecordVO getRecordVOForTitleColumn(Item item) {
					RecordVO recordVO = ViewableRecordVOTablePanel.this.getRecordVOForTitleColumn(item);
					if (recordVO == null) {
						recordVO = super.getRecordVOForTitleColumn(item);
					}
					return recordVO;
				}
			};
			viewableRecordVOTable.setWidth("100%");

			resultsTable = viewableRecordVOTable;
			resultsTable.setContainerDataSource(new ContainerAdapter(viewableRecordVOContainer) {
				@Override
				public Property getContainerProperty(Object itemId, Object propertyId) {
					Property result = super.getContainerProperty(itemId, propertyId);
					Object propertyValue = result.getValue();
					if (propertyValue instanceof Component) {
						List<ReferenceDisplay> referenceDisplays = ComponentTreeUtils.getChildren((Component) propertyValue, ReferenceDisplay.class);
						for (ReferenceDisplay referenceDisplay : referenceDisplays) {
							for (Object listenerObject : new ArrayList<>(referenceDisplay.getListeners(ClickEvent.class))) {
								referenceDisplay.removeClickListener((ClickListener) listenerObject);
							}
						}
						List<ConstellioAgentLink> constellioAgentLinks = ComponentTreeUtils.getChildren((Component) propertyValue, ConstellioAgentLink.class);
						for (ConstellioAgentLink constellioAgentLink : constellioAgentLinks) {
							for (Object listenerObject : new ArrayList<>(constellioAgentLink.getAgentLink().getListeners(ClickEvent.class))) {
								constellioAgentLink.getAgentLink().removeClickListener((ClickListener) listenerObject);
							}
						}
					}
					return new ObjectProperty<>(propertyValue);
				}
			});

			resultsTable.setSelectable(true);
			resultsTable.setMultiSelect(false);

			final CellStyleGenerator cellStyleGenerator = resultsTable.getCellStyleGenerator();
			resultsTable.setCellStyleGenerator(new CellStyleGenerator() {
				@Override
				public String getStyle(Table source, Object itemId, Object propertyId) {
					String baseStyle = cellStyleGenerator != null ? cellStyleGenerator.getStyle(source, itemId, propertyId) : "";
					if (StringUtils.isNotBlank(baseStyle)) {
						baseStyle += " ";
					}
					return baseStyle + "viewer-results-table-row-" + itemId;
				}
			});
		} else {
			resultsTable = new RecordVOTable(recordVOContainer) {
				@Override
				protected String getTableId() {
					String tableId = super.getTableId();
					if (tableId == null) {
						tableId = getClass().getName() + ".tableMode";
					}
					return tableId;
				}

				@Override
				protected RecordVO getRecordVOForTitleColumn(Item item) {
					RecordVO recordVO = ViewableRecordVOTablePanel.this.getRecordVOForTitleColumn(item);
					if (recordVO == null) {
						recordVO = super.getRecordVOForTitleColumn(item);
					}
					return recordVO;
				}

				@Override
				public boolean isSelectColumn() {
					return ViewableRecordVOTablePanel.this.isSelectColumn();
				}

				@Override
				protected SelectionManager newSelectionManager() {
					SelectionManager selectionManager = ViewableRecordVOTablePanel.this.newSelectionManager();
					if (selectionManager == null) {
						selectionManager = super.newSelectionManager();
					}
					return selectionManager;
				}

				@Override
				public boolean isIndexColumn() {
					return ViewableRecordVOTablePanel.this.isIndexColumn();
				}

				@Override
				public boolean isPaged() {
					// Never paged in table mode
					return false;
				}
			};
			resultsTable.setWidth("100%");

			if (!tableModeVisibleColumns.isEmpty()) {
				resultsTable.setVisibleColumns(tableModeVisibleColumns.toArray(new Object[0]));
			}
			for (Object propertyId : tableModeColumnHeaders.keySet()) {
				resultsTable.setColumnHeader(propertyId, tableModeColumnHeaders.get(propertyId));
			}
			for (Object propertyId : tableModeColumnExpandRatios.keySet()) {
				resultsTable.setColumnExpandRatio(propertyId, tableModeColumnExpandRatios.get(propertyId));
			}
		}

		resultsTable.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				rowClicked(event);
			}
		});
		for (ItemClickListener listener : itemClickListeners) {
			resultsTable.addItemClickListener(listener);
		}
		resultsTable.removeStyleName(RecordVOTable.CLICKABLE_ROW_STYLE_NAME);

		return resultsTable;
	}

	public TableMode getTableMode() {
		return tableMode;
	}

	public void setTableMode(TableMode tableMode) {
		if (tableMode == null) {
			tableMode = TableMode.LIST;
		}
		if (tableMode != this.tableMode) {
			this.tableMode = tableMode;
			if (table != null) {
				if (tableMode == TableMode.TABLE) {
					closeViewer();
				}

				BaseTable tableBefore = table;
				table = buildResultsTable();
				if (tableBefore != null) {
					table.setValue(tableBefore.getValue());
				}

				tableLayout.replaceComponent(tableBefore, table);
				if (pagingControls != null) {
					tableLayout.removeComponent(pagingControls);
				}
				if (table.isPaged()) {
					tableLayout.addComponent(pagingControls = table.createPagingControls());
				}
				adjustTableExpansion();

				updateTableButtonsAfterTableModeChange(tableBefore);
			}

			TableModeChangeEvent event = new TableModeChangeEvent(tableMode, null);
			for (TableModeChangeListener listener : tableModeChangeListeners) {
				listener.tableModeChanged(event);
			}
		}
	}

	private void updateTableButtonsAfterTableModeChange(BaseTable tableBefore) {
		if (isSelectColumn()) {
			SelectDeselectAllButton selectDeselectAllToggleButtonBefore = selectDeselectAllToggleButton;
			selectDeselectAllToggleButton = table.newSelectDeselectAllToggleButton();
			selectDeselectAllToggleButton.addStyleName(ValoTheme.BUTTON_LINK);
			if (selectDeselectAllToggleButtonBefore != null && selectDeselectAllToggleButtonBefore.isSelectAllMode() != selectDeselectAllToggleButton.isSelectAllMode()) {
				selectDeselectAllToggleButton.setSelectAllMode(selectDeselectAllToggleButtonBefore.isSelectAllMode());
			}
			tableActionButtonsLayout.replaceComponent(selectDeselectAllToggleButtonBefore, selectDeselectAllToggleButton);
		}
	}
	
	void rowClicked(ItemClickEvent event) {
		Object itemId = event.getItemId();
		if (!isNested() && tableMode == TableMode.LIST) {
			selectRecordVO(itemId, event, false);
			previousButton.setVisible(itemId != null);
			nextButton.setVisible(itemId != null);
		} else {
			RecordVO recordVO = getRecordVO(itemId);
			displayInWindowOrNavigate(recordVO);
		}
	}

	boolean isSelected(Object itemId) {
		return selectedItemId != null && selectedItemId.equals(itemId);
	}

	Object getSelectedItemId() {
		return selectedItemId;
	}

	public RecordVO getRecordVO(Object itemId) {
		return recordVOContainer.getRecordVO(itemId);
	}

	public List<MetadataSchemaVO> getSchemas() {
		return recordVOContainer.getSchemas();
	}

	private void displayRecordVOInWindow(RecordVO recordVO) {
		ViewWindow viewWindow;
		String schemaTypeCode = recordVO.getSchema().getTypeCode();
		if (Document.SCHEMA_TYPE.equals(schemaTypeCode)) {
			viewWindow = new DisplayDocumentWindow(recordVO);
		} else {
			viewWindow = new DisplaySchemaRecordWindow(recordVO);
		}
		viewWindow.addCloseListener(new Window.CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				refreshMetadata();
			}
		});
		ConstellioUI.getCurrent().addWindow(viewWindow);
	}

	private void navigateToRecordVO(RecordVO recordVO) {
		new ReferenceDisplay(recordVO).click();
	}

	protected boolean isDisplayInWindowOnSelection(RecordVO recordVO) {
		boolean displayInWindowOnSelection;
		String schemaTypeCode = recordVO.getSchema().getTypeCode();
		if (Folder.SCHEMA_TYPE.equals(schemaTypeCode)) {
			displayInWindowOnSelection = false;
		} else {
			displayInWindowOnSelection = true;
		}
		return displayInWindowOnSelection;
	}

	private void displayInWindowOrNavigate(RecordVO recordVO) {
		if (isDisplayInWindowOnSelection(recordVO)) {
			displayRecordVOInWindow(recordVO);
		} else {
			navigateToRecordVO(recordVO);
		}
	}

	void selectRecordVO(Object itemId, ItemClickEvent event, boolean reload) {
		Object newRowSelected = itemId;
		table.setValue(newRowSelected);

		Boolean compressionChange;
		if (selectedItemId == null && newRowSelected != null) {
			compressionChange = true;
		} else {
			compressionChange = null;
		}

		if (!newRowSelected.equals(this.selectedItemId) || reload) {
			selectedItemId = newRowSelected;
			if (reload) {
				recordVOContainer.refresh();
			}
			selectedRecordVO = getRecordVO(selectedItemId);
			previousItemId = recordVOContainer.prevItemId(itemId);
			nextItemId = recordVOContainer.nextItemId(itemId);

			if ((isNested() || !ResponsiveUtils.isDesktop()) && selectedRecordVO != null) {
				displayInWindowOrNavigate(selectedRecordVO);
			} else {
				previousButton.setEnabled(previousItemId != null);
				nextButton.setEnabled(nextItemId != null);

				if (tableMode == TableMode.LIST && isCompressionSupported()) {
					viewerMetadataPanel.setRecordVO(selectedRecordVO);
					if (compressionChange != null && event != null) {
						TableCompressEvent tableCompressEvent = new TableCompressEvent(event, compressionChange);
						for (TableCompressListener tableCompressListener : tableCompressListeners) {
							tableCompressListener.tableCompressChange(tableCompressEvent);
						}
					}
					adjustTableExpansion();
				}
			}
		}
	}

	private void closeViewer() {
		selectedItemId = null;
		
		TableCompressEvent tableCompressEvent = new TableCompressEvent(null, false);
		for (TableCompressListener tableCompressListener : tableCompressListeners) {
			tableCompressListener.tableCompressChange(tableCompressEvent);
		}

		adjustTableExpansion();
		previousButton.setVisible(false);
		nextButton.setVisible(false);
	}

	protected boolean isSelectColumn() {
		return false;
	}

	protected boolean isIndexColumn() {
		return false;
	}

	protected SelectionManager newSelectionManager() {
		return null;
	}

	protected boolean isPagedInListMode() {
		return false;
	}

	private ViewerMetadataPanel buildViewerMetadataPanel() {
		return new ViewerMetadataPanel();
	}

	private TableModeButton buildTableModeButton() {
		return new TableModeButton();
	}

	private BaseButton buildPreviousButton() {
		String caption = $("ViewableRecordVOTablePanel.previous");
		BaseButton previousButton = new IconButton(FontAwesome.CHEVRON_LEFT, caption) {
			@Override
			protected void buttonClick(ClickEvent event) {
				if (previousItemId != null) {
					selectRecordVO(previousItemId, null, false);
				}
			}
		};
		previousButton.addStyleName("previous-button");
		previousButton.setWidth("24px");
		previousButton.addExtension(new NiceTitle(caption));
		previousButton.setVisible(false);
		return previousButton;
	}

	private BaseButton buildNextButton() {
		String caption = $("ViewableRecordVOTablePanel.next");
		BaseButton nextButton = new IconButton(FontAwesome.CHEVRON_RIGHT, caption) {
			@Override
			protected void buttonClick(ClickEvent event) {
				if (nextItemId != null) {
					selectRecordVO(nextItemId, null, false);
				}
			}
		};
		nextButton.addStyleName("next-button");
		nextButton.setWidth("24px");
		nextButton.addExtension(new NiceTitle(caption));
		nextButton.setVisible(false);
		return nextButton;
	}

	private BaseButton buildCloseViewerButton() {
		BaseButton closeViewerButton = new IconButton(FontAwesome.TIMES, $("FilteredSearchResultsViewerTable.closeViewer")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				closeViewer();
			}
		};
		closeViewerButton.addStyleName("close-viewer-button");
		return closeViewerButton;
	}

	private void refreshMetadata() {
		if (selectedRecordVO != null) {
			RecordServices recordServices = ConstellioUI.getCurrent().getConstellioFactories().getModelLayerFactory().newRecordServices();
			Record selectedRecord = recordServices.getDocumentById(selectedRecordVO.getId());
			selectedRecordVO = new RecordToVOBuilder().build(selectedRecord, VIEW_MODE.DISPLAY, ConstellioUI.getCurrentSessionContext());
			selectRecordVO(selectedItemId, null, true);
			table.resetPageBuffer();
			table.refreshRenderedCells();
		}
	}
	
	public List<TableCompressListener> getTableCompressListeners() {
		return tableCompressListeners;
	}

	public void addTableCompressListener(TableCompressListener listener) {
		if (isCompressionSupported() && !tableCompressListeners.contains(listener)) {
			tableCompressListeners.add(listener);
		}
	}

	public void removeTableCompressListener(TableCompressListener listener) {
		tableCompressListeners.remove(listener);
	}

	public List<TableModeChangeListener> getTableModeChangeListeners() {
		return tableModeChangeListeners;
	}

	public void addTableModeChangeListener(TableModeChangeListener listener) {
		if (!tableModeChangeListeners.contains(listener)) {
			tableModeChangeListeners.add(listener);
		}
	}

	public void removeTableModeChangeListener(TableModeChangeListener listener) {
		tableModeChangeListeners.remove(listener);
	}

	public List<ItemClickListener> getItemClickListeners() {
		return itemClickListeners;
	}

	public void addItemClickListener(ItemClickListener listener) {
		if (!itemClickListeners.contains(listener)) {
			itemClickListeners.add(listener);
			if (table != null) {
				table.addItemClickListener(listener);
			}
		}
	}

	public void removeItemClickListener(ItemClickListener listener) {
		itemClickListeners.remove(listener);
		if (table != null) {
			table.removeItemClickListener(listener);
		}
	}

	public List<SelectionChangeListener> getSelectionChangeListeners() {
		return selectionChangeListeners;
	}

	public void addSelectionChangeListener(SelectionChangeListener listener) {
		if (!selectionChangeListeners.contains(listener)) {
			selectionChangeListeners.add(listener);
			if (table != null) {
				table.addSelectionChangeListener(listener);
			}
		}
	}

	public void removeSelectionChangeListener(SelectionChangeListener listener) {
		selectionChangeListeners.remove(listener);
		if (table != null) {
			table.removeSelectionChangeListener(listener);
		}
	}

	public HorizontalLayout createPagingControls() {
		return table.createPagingControls();
	}

	public void setItemsPerPageValue(int value) {
		if (table.isPaged()) {
			pagingControls.setItemsPerPageValue(value);
		}
	}

	public void select(Object itemId) {
		table.select(itemId);
	}

	public void deselect(Object itemId) {
		table.deselect(itemId);
	}

	public void selectAll() {
		table.selectAll();
	}

	public void deselectAll() {
		table.deselectAll();
	}

	public void selectCurrentPage() {
		table.selectCurrentPage();
	}

	public void deselectCurrentPage() {
		table.deselectCurrentPage();
	}

	protected boolean contextMenuOpened(ContextMenu contextMenu, Object itemId) {
		return false;
	}

	public void setVisibleColumns(Object... visibleColumns) {
		if (visibleColumns != null) {
			tableModeVisibleColumns = Arrays.asList(visibleColumns);
		} else {
			tableModeVisibleColumns = Collections.emptyList();
		}
		if (table != null && tableMode == TableMode.TABLE) {
			table.setVisibleColumns(visibleColumns);
		}
	}

	public void setColumnHeader(Object propertyId, String header) {
		tableModeColumnHeaders.put(propertyId, header);
		if (table != null && tableMode == TableMode.TABLE) {
			table.setColumnHeader(propertyId, header);
		}
	}

	public void setColumnExpandRatio(Object propertyId, int expandRatio) {
		tableModeColumnExpandRatios.put(propertyId, expandRatio);
		if (table != null && tableMode == TableMode.TABLE) {
			table.setColumnExpandRatio(propertyId, expandRatio);
		}
	}

	protected RecordVO getRecordVOForTitleColumn(Item item) {
		return null;
	}

	public BaseTable getActualTable() {
		return table;
	}

	private class ViewerMetadataPanel extends Panel {

		private VerticalLayout mainLayout;

		public ViewerMetadataPanel() {
			buildUI();
		}

		private void setRecordVO(RecordVO recordVO) {
			mainLayout.removeAllComponents();

			if (recordVO != null) {
				Component panelContent;
				String schemaTypeCode = recordVO.getSchema().getTypeCode();
				if (Document.SCHEMA_TYPE.equals(schemaTypeCode)) {
					DisplayDocumentViewImpl view = new DisplayDocumentViewImpl(recordVO, true, false);
					view.enter(null);
					view.addEditWindowCloseListener(new Window.CloseListener() {
						@Override
						public void windowClose(CloseEvent e) {
							refreshMetadata();
						}
					});
					panelContent = view;
				} else if (Folder.SCHEMA_TYPE.equals(schemaTypeCode)) {
					DisplayFolderViewImpl view = new DisplayFolderViewImpl(recordVO, true, false);
					view.enter(null);
					panelContent = view;
				} else {
					UserVO currentUser = ConstellioUI.getCurrentSessionContext().getCurrentUser();
					panelContent = new RecordDisplayFactory(currentUser).build(recordVO, true);
				}
				mainLayout.addComponent(panelContent);
			}
		}

		private void buildUI() {
			addStyleName(ValoTheme.PANEL_BORDERLESS);
			addStyleName("viewer-metadata-panel");
			setSizeFull();

			mainLayout = new VerticalLayout();
			mainLayout.addStyleName("viewer-metadata-panel-main-layout");
			mainLayout.setSizeFull();
			setContent(mainLayout);
		}

	}

	public class TableCompressEvent implements Serializable {

		private final ItemClickEvent itemClickEvent;

		private final boolean compressed;

		public TableCompressEvent(ItemClickEvent itemClickEvent, boolean compressed) {
			this.itemClickEvent = itemClickEvent;
			this.compressed = compressed;
		}

		public ItemClickEvent getItemClickEvent() {
			return itemClickEvent;
		}

		public boolean isCompressed() {
			return compressed;
		}

	}

	public interface TableCompressListener extends Serializable {

		void tableCompressChange(TableCompressEvent event);

	}

	public class TableModeChangeEvent implements Serializable {

		private Component component;

		private TableMode tableMode;

		public TableModeChangeEvent(TableMode tableMode, Component component) {
			this.tableMode = tableMode;
			this.component = component;
		}

		public TableMode getTableMode() {
			return tableMode;
		}

		public Component getComponent() {
			return component;
		}
	}

	public interface TableModeChangeListener extends Serializable {

		void tableModeChanged(TableModeChangeEvent event);

	}

	private class TableModeButton extends BaseButton {

		private final String MODE_LIST_CAPTION = $("ViewableRecordVOTablePanel.tableMode.list");
		private final String MODE_TABLE_CAPTION = $("ViewableRecordVOTablePanel.tableMode.table");

		public TableModeButton() {
			super();
			addStyleName(ValoTheme.BUTTON_LINK);
			addStyleName("table-mode-button");
			updateCaption();
			addTableModeChangeListener(new TableModeChangeListener() {
				@Override
				public void tableModeChanged(TableModeChangeEvent event) {
					updateCaption();
				}
			});
		}

		private void updateCaption() {
			String caption;
			if (tableMode == TableMode.LIST) {
				caption = MODE_TABLE_CAPTION;
			} else {
				caption = MODE_LIST_CAPTION;
			}
			setCaption(caption);
		}

		@Override
		protected void buttonClick(ClickEvent event) {
			if (tableMode == TableMode.LIST) {
				setTableMode(TableMode.TABLE);
			} else {
				setTableMode(TableMode.LIST);
			}
		}

	}

}