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
package com.constellio.app.ui.framework.components.content;

import static com.constellio.app.ui.i18n.i18n.$;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.constellio.app.services.factories.ConstellioFactories;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.entities.ContentVersionVO;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.components.BaseForm;
import com.constellio.app.ui.framework.components.BaseForm.FieldAndPropertyId;
import com.constellio.app.ui.framework.components.BaseWindow;
import com.constellio.app.ui.framework.components.fields.upload.ContentVersionUploadField;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.frameworks.validation.ValidationException;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class UpdateContentVersionWindowImpl extends BaseWindow implements UpdateContentVersionWindow, DropHandler {
	
	private boolean checkingIn;
	
	private String nullValue = new String();
	
	private ContentVersionVO newVersionVO;
	
	private Object majorVersion;
	
	private Property<ContentVersionVO> contentVersionProperty = new NestedMethodProperty<ContentVersionVO>(this, "contentVersion");
	
	private Property<Object> majorVersionProperty = new NestedMethodProperty<Object>(this, "majorVersion");

	private VerticalLayout mainLayout;
	
	private BaseForm<RecordVO> uploadForm;
	
	private Label titleLabel;
	
	private Label errorLabel;
	
	private ContentVersionUploadField uploadField;
	
	private OptionGroup majorVersionField;
	
	private UpdateContentVersionPresenter presenter;
	
	public UpdateContentVersionWindowImpl(RecordVO recordVO, MetadataVO metadataVO) {
		setModal(true);
		setWidth("70%");
		setHeight("450px");
		setZIndex(null);
		
		setCaption($("UpdateContentVersionWindow.title"));
		
		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setWidth("100%");
		
		titleLabel = new Label($("UpdateContentVersionWindow.title"));
		titleLabel.addStyleName(ValoTheme.LABEL_H1);
		
		errorLabel = new Label();
		errorLabel.addStyleName("error-label");
		errorLabel.setVisible(false);
		
		uploadField = new ContentVersionUploadField(false) {
			@Override
			protected boolean isMajorVersionField(ContentVersionVO contentVersionVO) {
				return false;
			}
		};
		uploadField.setCaption($("UpdateContentVersionWindow.uploadField"));
		uploadField.setImmediate(true);
		uploadField.addValidator(new Validator() {
			@Override
			public void validate(Object value)
					throws InvalidValueException {
				if (getContentVersion() == null && getMajorVersion() instanceof Boolean) {
					throw new InvalidValueException($("UpdateContentVersionWindow.validate.noVersionIfContentVersionUploaded"));
				}
			}
		});
		
		majorVersionField = new OptionGroup();
		majorVersionField.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		majorVersionField.setCaption($("UpdateContentVersionWindow.version"));
		majorVersionField.setRequired(true);
		majorVersionField.setImmediate(true);
		
		List<FieldAndPropertyId> fieldsAndPropertyIds = new ArrayList<FieldAndPropertyId>();
		fieldsAndPropertyIds.add(new FieldAndPropertyId(uploadField, "contentVersion"));
		fieldsAndPropertyIds.add(new FieldAndPropertyId(majorVersionField, "majorVersion"));
		
		uploadForm = new BaseForm<RecordVO>(recordVO, fieldsAndPropertyIds) {
			@Override
			protected Item newItem(RecordVO viewObject) {
				return new Item() {
					@SuppressWarnings("rawtypes")
					@Override
					public Property getItemProperty(Object id) {
						Property property;
						if ("contentVersion".equals(id)) {
							property = contentVersionProperty;
						} else if ("majorVersion".equals(id)) {
							property = majorVersionProperty;
						} else {
							property = null;
						}
						return property;
					}

					@Override
					public Collection<?> getItemPropertyIds() {
						return Arrays.asList("contentVersion", "majorVersion");
					}

					@SuppressWarnings("rawtypes")
					@Override
					public boolean addItemProperty(Object id, Property property)
							throws UnsupportedOperationException {
						throw new UnsupportedOperationException("Read-only item");
					}

					@Override
					public boolean removeItemProperty(Object id)
							throws UnsupportedOperationException {
						throw new UnsupportedOperationException("Read-only item");
					}
				};
			}

			@Override
			protected void saveButtonClick(RecordVO viewObject)
					throws ValidationException {
				Boolean bMajorVersion;
				if (nullValue.equals(majorVersion)) {
					bMajorVersion = null;
				} else {
					bMajorVersion = (Boolean) majorVersion;
				}
				newVersionVO = (ContentVersionVO) uploadField.getValue();
				presenter.contentVersionSaved(newVersionVO, bMajorVersion);
			}

			@Override
			protected void cancelButtonClick(RecordVO viewObject) {
				close();
			}
		};
		uploadForm.setSizeFull();
		
		mainLayout.addComponents(titleLabel, errorLabel, uploadForm);

		DragAndDropWrapper dragAndDropWrapper = new DragAndDropWrapper(mainLayout);
		dragAndDropWrapper.setSizeFull();
		setContent(dragAndDropWrapper);
		dragAndDropWrapper.setDropHandler(uploadField);
		
		presenter = new UpdateContentVersionPresenter(this, recordVO, metadataVO);
	}

	@Override
	public void attach() {
		super.attach();
		errorLabel.setVisible(false);
		presenter.windowAttached(checkingIn);
	}

	public final ContentVersionVO getContentVersion() {
		return newVersionVO;
	}

	public final void setContentVersion(ContentVersionVO newVersionVO) {
		this.newVersionVO = newVersionVO;
	}

	public final Object getMajorVersion() {
		return majorVersion;
	}

	public final void setMajorVersion(Object majorVersion) {
		this.majorVersion = majorVersion;
	}

	@Override
	public void drop(DragAndDropEvent event) {
		uploadField.drop(event);
	}

	@Override
	public AcceptCriterion getAcceptCriterion() {
		return uploadField.getAcceptCriterion();
	}

	@Override
	public void showErrorMessage(String key, Object...args) {
		errorLabel.setVisible(true);
		errorLabel.setValue($(key, args));
	}

	@Override
	public SessionContext getSessionContext() {
		return ConstellioUI.getCurrentSessionContext();
	}

	@Override
	public ConstellioFactories getConstellioFactories() {
		return ConstellioFactories.getInstance();
	}

	@Override
	public boolean isFormVisible() {
		return uploadField.isVisible();
	}

	@Override
	public void setFormVisible(boolean visible) {
		uploadField.setVisible(visible);
	}
	
	private void initMajorVersionFieldOptions() {
		majorVersionField.removeAllItems();
		majorVersionField.addItem(true);
		majorVersionField.addItem(false);
	}

	@Override
	public void addMajorMinorSameOptions() {
		initMajorVersionFieldOptions();
		majorVersionField.addItem(nullValue);
		majorVersionField.setItemCaption(true, $("UpdateContentVersionWindow.options.newMajorVersion"));
		majorVersionField.setItemCaption(false, $("UpdateContentVersionWindow.options.newMinorVersion"));
		majorVersionField.setItemCaption(nullValue, $("UpdateContentVersionWindow.options.sameVersion"));
	}

	@Override
	public void addMajorMinorOptions() {
		initMajorVersionFieldOptions();
		majorVersionField.setItemCaption(true, $("UpdateContentVersionWindow.options.majorVersion"));
		majorVersionField.setItemCaption(false, $("UpdateContentVersionWindow.options.minorVersion"));
	}
	
	public void open(boolean checkingIn) {
		this.checkingIn = checkingIn;
		UI.getCurrent().addWindow(this);
	}

	@Override
	public void close() {
		if (newVersionVO != null && newVersionVO.getInputStreamProvider() != null) {
			newVersionVO.getInputStreamProvider().deleteTemp();
		}
		super.close();
	}

}
