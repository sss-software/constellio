package com.constellio.app.ui.framework.components.viewers.panel;

import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.framework.components.table.RecordVOTable;
import com.constellio.app.ui.framework.components.table.columns.TableColumnsManager;
import com.constellio.app.ui.framework.containers.ContainerAdapter;
import com.jensjansson.pagedtable.PagedTableContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Image;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.themes.ValoTheme;
import elemental.json.JsonArray;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.UUID;

public class ViewableRecordVOTable extends RecordVOTable {

	//	private int uncompressedPageLength;

	private boolean compressed;

	public ViewableRecordVOTable(ViewableRecordVOContainer dataSource) {
		super();
		setContainerDataSource(dataSource);
		init();
	}

	@Override
	public boolean isMenuBarColumn() {
		return !isCompressed();
	}

	@Override
	public boolean isContextMenuPossible() {
		return false;
	}

	private void init() {
		setId(UUID.randomUUID().toString());
		addStyleName("viewable-record-table");
		addStyleName(ValoTheme.TABLE_BORDERLESS);
		addStyleName(ValoTheme.TABLE_NO_HEADER);
		addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
		setColumnWidth(ViewableRecordVOContainer.THUMBNAIL_PROPERTY, ViewableRecordVOContainer.THUMBNAIL_WIDTH);
		setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		setColumnAlignment(ViewableRecordVOContainer.SEARCH_RESULT_PROPERTY, Align.LEFT);
	}

	public boolean isCompressed() {
		return compressed;
	}

	private ViewableRecordVOContainer getViewableRecordVOContainer() {
		ViewableRecordVOContainer result = null;
		Container dataSource = getContainerDataSource();
		if (dataSource instanceof PagedTableContainer) {
			dataSource = ((PagedTableContainer) dataSource).getContainer();
		}
		if (dataSource instanceof ContainerAdapter) {
			ContainerAdapter<?> currentAdapter = (ContainerAdapter<?>) dataSource;
			while (result == null && currentAdapter != null) {
				Container nestedContainer = currentAdapter.getNestedContainer();
				if (nestedContainer instanceof ViewableRecordVOContainer) {
					result = (ViewableRecordVOContainer) nestedContainer;
				} else if (nestedContainer instanceof ContainerAdapter) {
					currentAdapter = (ContainerAdapter<?>) nestedContainer;
				} else {
					currentAdapter = null;
				}
			}
		} else if (dataSource instanceof ViewableRecordVOContainer) {
			result = (ViewableRecordVOContainer) dataSource;
		}
		return result;
	}

	public void setCompressed(boolean compressed) {
		boolean compressedChanged = this.compressed != compressed;
		this.compressed = compressed;

		if (compressedChanged) {
			ViewableRecordVOContainer container = getViewableRecordVOContainer();
			container.setCompressed(compressed);
			manageColumns(getTableId());
			//			refreshRenderedCells();
			refreshRowCache();
			adjustHeight();
		}
	}

	private void adjustHeight() {
		ConstellioUI.getCurrent().runAsync(new Runnable() {
			@Override
			public void run() {
				ConstellioUI.getCurrent().access(new Runnable() {
					@Override
					public void run() {
						final String functionId = "zeFunction";
						JavaScript.getCurrent().addFunction(functionId,
								new JavaScriptFunction() {
									@Override
									public void call(JsonArray arguments) {
										String tableBodyWrapperDivHeight = StringUtils.removeEnd(arguments.getString(0), "px");
										//										if (StringUtils.isNotBlank(tableBodyWrapperDivHeight)) {
										StringBuilder js = new StringBuilder();
										js.append("  var tableBodyWrapperDiv =  document.getElementById('" + getId() + "').getElementsByClassName('v-table-body-wrapper')[0].getElementsByTagName('div')[0];");
										js.append("  tableBodyWrapperDiv.style.height = '" + tableBodyWrapperDivHeight + "';");
										JavaScript.getCurrent().execute(js.toString());
										//										}
									}
								});

						StringBuilder js = new StringBuilder();
						js.append("  var tableBodyWrapperDiv =  document.getElementById('" + getId() + "').getElementsByClassName('v-table-body-wrapper')[0].getElementsByTagName('div')[0];");
						js.append("  var tableBodyWrapperDivHeight =  tableBodyWrapperDiv.style.height;");
						js.append("  tableBodyWrapperDiv.style.height = '';");
						js.append(functionId + "(tableBodyWrapperDivHeight);");
						JavaScript.getCurrent().execute(js.toString());
					}
				});
			}
		}, 10, this);
	}

	@Override
	protected String getTableId() {
		// All tables have the same columns
		return getClass().getName();
	}

	@Override
	protected TableColumnsManager newColumnsManager() {
		return new ViewableRecordVOTableColumnsManager();
	}

	@Override
	public boolean isSelectColumn() {
		return true;
	}

	@Override
	protected Property<?> loadContainerProperty(final Object itemId, final Object propertyId) {
		Property<?> property = super.loadContainerProperty(itemId, propertyId);
		if (ViewableRecordVOContainer.SEARCH_RESULT_PROPERTY.equals(propertyId)) {
			Object propertyValue = property.getValue();
			if (propertyValue instanceof LayoutClickNotifier) {
				LayoutClickNotifier layout = (LayoutClickNotifier) propertyValue;
				layout.addLayoutClickListener(new LayoutClickListener() {
					@Override
					public void layoutClick(LayoutClickEvent event) {
						if (!(event.getSource() instanceof MenuBar)) {
							Collection<?> itemClickListeners = getListeners(ItemClickEvent.class);
							MouseEventDetails mouseEventDetails = new MouseEventDetails();
							mouseEventDetails.setButton(event.getButton());
							mouseEventDetails.setClientX(event.getClientX());
							mouseEventDetails.setClientY(event.getClientY());
							mouseEventDetails.setRelativeX(event.getRelativeX());
							mouseEventDetails.setRelativeY(event.getRelativeY());
							Item item = getItem(itemId);
							ItemClickEvent itemClickEvent = new ItemClickEvent(ViewableRecordVOTable.this, item, itemId, propertyId, mouseEventDetails);
							for (Object itemClickListenerObj : itemClickListeners) {
								ItemClickListener itemClickListener = (ItemClickListener) itemClickListenerObj;
								itemClickListener.itemClick(itemClickEvent);
							}
						}
					}
				});
				property = new ObjectProperty<>(layout);
			}
		} else if (ViewableRecordVOContainer.THUMBNAIL_PROPERTY.equals(propertyId)) {
			Object propertyValue = property.getValue();
			if (propertyValue instanceof Image) {
				Image image = (Image) propertyValue;
				image.addClickListener(new ClickListener() {
					@Override
					public void click(ClickEvent event) {
						Collection<?> itemClickListeners = getListeners(ItemClickEvent.class);
						MouseEventDetails mouseEventDetails = new MouseEventDetails();
						mouseEventDetails.setButton(event.getButton());
						mouseEventDetails.setClientX(event.getClientX());
						mouseEventDetails.setClientY(event.getClientY());
						mouseEventDetails.setRelativeX(event.getRelativeX());
						mouseEventDetails.setRelativeY(event.getRelativeY());
						Item item = getItem(itemId);
						for (Object itemClickListenerObj : itemClickListeners) {
							ItemClickListener itemClickListener = (ItemClickListener) itemClickListenerObj;
							itemClickListener.itemClick(new ItemClickEvent(ViewableRecordVOTable.this, item, itemId, propertyId, mouseEventDetails));
						}
					}
				});
				property = new ObjectProperty<>(image);
			}
		}
		return property;
	}

}
