package com.constellio.app.ui.pages.management.schemaRecords;

import com.constellio.app.modules.rm.ui.pages.extrabehavior.ProvideSecurityWithNoUrlParamSupport;
import com.constellio.app.modules.rm.ui.pages.extrabehavior.SecurityWithNoUrlParamSupport;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.buttons.AddButton;
import com.constellio.app.ui.framework.buttons.DeleteButton;
import com.constellio.app.ui.framework.buttons.DisplayButton;
import com.constellio.app.ui.framework.buttons.EditButton;
import com.constellio.app.ui.framework.buttons.ListSequencesButton;
import com.constellio.app.ui.framework.components.RecordDisplay;
import com.constellio.app.ui.framework.components.breadcrumb.BaseBreadcrumbTrail;
import com.constellio.app.ui.framework.components.breadcrumb.IntermediateBreadCrumbTailItem;
import com.constellio.app.ui.framework.components.breadcrumb.TitleBreadcrumbTrail;
import com.constellio.app.ui.framework.components.buttons.RecordVOActionButtonFactory;
import com.constellio.app.ui.framework.components.table.RecordVOTable;
import com.constellio.app.ui.framework.containers.ButtonsContainer;
import com.constellio.app.ui.framework.containers.RecordVOLazyContainer;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.framework.items.RecordVOItem;
import com.constellio.app.ui.pages.base.BaseViewImpl;
import com.constellio.app.ui.pages.breadcrumb.BreadcrumbTrailUtil;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.constellio.app.ui.i18n.i18n.$;

@SuppressWarnings("serial")
public class DisplaySchemaRecordViewImpl extends BaseViewImpl implements DisplaySchemaRecordView, ProvideSecurityWithNoUrlParamSupport {

	private DisplaySchemaRecordPresenter presenter;

	private RecordVO recordVO;

	private RecordVODataProvider subRecordsDataProvider;

	private VerticalLayout mainLayout;

	private RecordDisplay recordDisplay;

	private Button editButton;

	private Button deleteButton;

	private Button addSubRecordButton;

	private RecordVOTable subRecordsTable;

	private boolean nestedView;

	private boolean isInWindow;

	private boolean isViewRecordMode;


	public DisplaySchemaRecordViewImpl() {
		this(null, false, false, false);
	}

	public DisplaySchemaRecordViewImpl(RecordVO recordVO, boolean nestedView, boolean inWindow,
									   boolean isViewRecordMode) {
		this.presenter = new DisplaySchemaRecordPresenter(this, recordVO, nestedView, inWindow);
		this.nestedView = nestedView;
		this.isInWindow = inWindow;
		this.isViewRecordMode = isViewRecordMode;
	}

	@Override
	protected void initBeforeCreateComponents(ViewChangeEvent event) {
		if (event != null) {
			presenter.forParams(event.getParameters());
		} else if (recordVO != null) {
			presenter.forParams(recordVO.getId());
		}
	}

	public boolean isViewRecordMode() {
		return isViewRecordMode;
	}

	@Override
	protected String getTitle() {
		return $("DisplaySchemaRecordView.viewTitle");
	}

	@Override
	public void setRecordVO(RecordVO recordVO) {
		this.recordVO = recordVO;
	}

	@Override
	public void setSubRecords(RecordVODataProvider dataProvider) {
		this.subRecordsDataProvider = dataProvider;
	}

	@Override
	protected Component buildMainComponent(ViewChangeEvent event) {
		addStyleName("display-schema-record-view");
		
		mainLayout = new VerticalLayout();
		mainLayout.setWidth("100%");
		mainLayout.setSpacing(true);

		recordDisplay = new RecordDisplay(recordVO);
		mainLayout.addComponent(recordDisplay);

		if (subRecordsDataProvider != null) {
			addSubRecordButton = new AddButton(true) {
				@Override
				protected void buttonClick(ClickEvent event) {
					presenter.addSubRecordButtonClicked();
				}
			};

			final RecordVOLazyContainer subRecordsContainer = new RecordVOLazyContainer(subRecordsDataProvider);
			ButtonsContainer<RecordVOLazyContainer> buttonsContainer = new ButtonsContainer<>(subRecordsContainer);

			buttonsContainer.addButton(new ButtonsContainer.ContainerButton() {
				@Override
				protected Button newButtonInstance(final Object itemId, final ButtonsContainer<?> container) {
					RecordVOItem item = (RecordVOItem) subRecordsContainer.getItem(itemId);
					final RecordVO subRecordVO = item.getRecord();
					Button editSubRecordButton = new DisplayButton() {
						@Override
						protected void buttonClick(ClickEvent event) {
							presenter.displaySubRecordButtonClicked(subRecordVO);
						}
					};
					editSubRecordButton.setVisible(presenter.isEditSubRecordButtonVisible(subRecordVO));
					return editSubRecordButton;
				}
			});
			buttonsContainer.addButton(new ButtonsContainer.ContainerButton() {
				@Override
				protected Button newButtonInstance(final Object itemId, final ButtonsContainer<?> container) {
					RecordVOItem item = (RecordVOItem) subRecordsContainer.getItem(itemId);
					final RecordVO subRecordVO = item.getRecord();
					Button editSubRecordButton = new EditButton(true) {
						@Override
						protected void buttonClick(ClickEvent event) {
							presenter.editSubRecordButtonClicked(subRecordVO);
						}
					};
					editSubRecordButton.setVisible(presenter.isEditSubRecordButtonVisible(subRecordVO));
					return editSubRecordButton;
				}
			});
			buttonsContainer.addButton(new ButtonsContainer.ContainerButton() {
				@Override
				protected Button newButtonInstance(final Object itemId, final ButtonsContainer<?> container) {
					RecordVOItem item = (RecordVOItem) subRecordsContainer.getItem(itemId);
					final RecordVO subRecordVO = item.getRecord();
					Button deleteSubRecordButton = new DeleteButton(true) {
						@Override
						protected void confirmButtonClick(ConfirmDialog dialog) {
							presenter.deleteSubRecordButtonClicked(subRecordVO);
						}
					};
					deleteSubRecordButton.setVisible(presenter.isDeleteSubRecordButtonVisible(subRecordVO));
					return deleteSubRecordButton;
				}
			});
			subRecordsTable = new RecordVOTable(buttonsContainer);
			subRecordsTable.setColumnHeader(ButtonsContainer.DEFAULT_BUTTONS_PROPERTY_ID, "");
			subRecordsTable.setColumnWidth(ButtonsContainer.DEFAULT_BUTTONS_PROPERTY_ID, 120);
			subRecordsTable.setWidth("100%");

			mainLayout.addComponents(addSubRecordButton, subRecordsTable);
			mainLayout.setComponentAlignment(addSubRecordButton, Alignment.TOP_RIGHT);
		}

		return mainLayout;
	}

	@Override
	protected String getActionMenuBarCaption() {
		return $("DisplaySchemaRecordView.actions");
	}

	@Override
	protected boolean isActionMenuBar() {
		return nestedView || isInWindow;
	}

	@Override
	protected boolean isBreadcrumbsVisible() {
		return !nestedView && !isInWindow;
	}

	@Override
	protected boolean isFullWidthIfActionMenuAbsent() {
		return true;
	}

	@Override
	protected List<Button> buildActionMenuButtons(ViewChangeEvent event) {
		List<Button> actionMenuButtons = new ArrayList<Button>();
		if (presenter.isEditButtonVisible()) {
			editButton = new EditButton(false) {
				@Override
				protected void buttonClick(ClickEvent event) {
					presenter.editButtonClicked();
				}
			};
			if (!nestedView) {
				actionMenuButtons.add(editButton);
			}
		}
		if (presenter.isDeleteButtonVisible()) {
			deleteButton = new DeleteButton(false) {
				@Override
				protected void confirmButtonClick(ConfirmDialog dialog) {
					presenter.deleteButtonClicked();
				}
			};
			actionMenuButtons.add(deleteButton);
		}
		if (presenter.isSequenceTable()) {
			actionMenuButtons.add(new ListSequencesButton(recordVO.getId(), $("DisplaySchemaRecordView.sequences")));
		}


		//		return actionMenuButtons;
		return new RecordVOActionButtonFactory(recordVO, this, Collections.emptyList()).build();
	}

	@Override
	protected List<Button> getQuickActionMenuButtons() {
		List<Button> quickActionMenuButtons = new ArrayList<>();
		if (nestedView) {
			if (presenter.isEditButtonVisible()) {
				quickActionMenuButtons.add(editButton);
			}
		}
		return quickActionMenuButtons;
	}

	@Override
	protected ClickListener getBackButtonClickListener() {
		if (isInWindow) {
			return null;
		} else {
			return new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					presenter.backButtonClicked();
				}
			};
		}
	}


	@Override
	protected BaseBreadcrumbTrail buildBreadcrumbTrail() {
		return new TitleBreadcrumbTrail(this, getTitle()) {
			@Override
			public List<? extends IntermediateBreadCrumbTailItem> getIntermediateItems() {
				return Arrays.asList(BreadcrumbTrailUtil.valueDomain(), BreadcrumbTrailUtil.listSchemaRecord(presenter.getSchemaCode()));
			}
		};
	}


	@Override
	public SecurityWithNoUrlParamSupport getSecurityWithNoUrlParamSupport() {
		return presenter;
	}
}
