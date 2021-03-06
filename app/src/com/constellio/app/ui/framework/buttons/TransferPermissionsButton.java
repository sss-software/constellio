package com.constellio.app.ui.framework.buttons;

import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.components.fields.list.ListAddRemoveRecordLookupField;
import com.constellio.app.ui.pages.management.authorizations.TransferPermissionPresenter;
import com.constellio.app.ui.pages.management.authorizations.TransferPermissionPresenterException;
import com.constellio.model.entities.records.wrappers.User;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.List;

import static com.constellio.app.ui.framework.components.BaseForm.BUTTONS_LAYOUT;
import static com.constellio.app.ui.framework.components.BaseForm.SAVE_BUTTON;
import static com.constellio.app.ui.i18n.i18n.$;
import static com.vaadin.ui.themes.ValoTheme.BUTTON_PRIMARY;

public class TransferPermissionsButton extends WindowButton {
	@PropertyId("users") protected ListAddRemoveRecordLookupField users;

	private Label label;
	private CheckBox removeUserAccessCheckbox;
	private Button saveButton;
	private Button cancelButton;
	private TransferPermissionPresenter presenter;

	private RecordVO sourceUser;

	public TransferPermissionsButton(String caption, String windowCaption, TransferPermissionPresenter presenter) {
		super(caption, windowCaption);
		this.presenter = presenter;
	}


	@Override
	protected Component buildWindowContent() {
		VerticalLayout mainLayout = new VerticalLayout();
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		label = new Label($("TransferPermissionsButton.chooseDestinationUsers"));
		buildUsersSearchField();
		buildRemoveCurrentUserRightsCheckbox();

		buildSaveButton();
		buildCancelButton();
		configureButtonsLayout(buttonsLayout);

		mainLayout.addComponents(label, users, removeUserAccessCheckbox);
		mainLayout.addComponent(buttonsLayout);
		return mainLayout;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}

	private void buildUsersSearchField() {
		users = new ListAddRemoveRecordLookupField(User.SCHEMA_TYPE);
		users.setCaption($("TransferPermissionsButton.authorizedUsers"));
		users.setId("users");
	}

	private void buildRemoveCurrentUserRightsCheckbox() {
		removeUserAccessCheckbox = new CheckBox();
		removeUserAccessCheckbox.setCaption($("TransferPermissionsButton.removeUserAccess"));
		removeUserAccessCheckbox.addValueChangeListener(
				(ValueChangeListener) event -> presenter.setRemoveUserAccessCheckboxValue(removeUserAccessIsChecked()));
	}

	private void buildSaveButton() {
		saveButton = new Button($("save"));
		saveButton.addStyleName(SAVE_BUTTON);
		saveButton.addStyleName(BUTTON_PRIMARY);
		saveButton.addClickListener((ClickListener) event -> {
			sourceUser = presenter.getUser();
			confirmSaveDialog();
		});
	}

	private void buildCancelButton() {
		cancelButton = new Button($("cancel"));
		cancelButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				getWindow().close();
			}
		});
	}

	private void configureButtonsLayout(HorizontalLayout buttonsLayout) {
		buttonsLayout.addStyleName(BUTTONS_LAYOUT);
		buttonsLayout.setSpacing(true);
		buttonsLayout.addComponents(saveButton, cancelButton);
	}

	private boolean removeUserAccessIsChecked() {
		return removeUserAccessCheckbox.getValue();
	}

	private String buildUserListString() {
		List<String> usersList = presenter.convertUserIdListToUserNames(users.getValue());
		return String.join(", ", usersList);
	}

	private boolean validateUserInputInformation() {
		try {
			presenter.validateAccessTransfer(sourceUser.getRecord(), users.getValue());
			return true;
		} catch (TransferPermissionPresenterException e) {
			presenter.displayErrorMessage(e.getMessage());
			return false;
		}
	}

	private void confirmSaveDialog() {
		if (validateUserInputInformation()) {
			String selectedUsersString = buildUserListString();
			presenter.setRemoveUserAccessCheckboxValue(removeUserAccessIsChecked());
			String confirmMessage = presenter.buildTransferRightsConfirmMessage(sourceUser.getTitle(), selectedUsersString,
					users.getValue().size() > 1, removeUserAccessIsChecked());
			ConfirmDialog.show(ConstellioUI.getCurrent(), $("TransferPermissionsButton.title"), confirmMessage,
					$("Ok"), $("cancel"), (ConfirmDialog.Listener) dialog -> {
						if (dialog.isConfirmed()) {
							presenter.transferAccessSaveButtonClicked(sourceUser, users.getValue(), getWindow());
						}
					});
		}
	}

}
