package com.constellio.app.ui.framework.components.contextmenu;

import com.constellio.app.ui.framework.buttons.ConfirmDialogButton;
import com.constellio.app.ui.framework.buttons.ConfirmDialogButton.DialogMode;
import com.vaadin.ui.Button;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickEvent;

import static com.constellio.app.ui.i18n.i18n.$;

public abstract class ConfirmDialogContextMenuItemClickListener implements BaseContextMenuItemClickListener, Button.ClickListener {

	private DialogMode dialogMode;

	public ConfirmDialogContextMenuItemClickListener() {
		this(null);
	}

	public ConfirmDialogContextMenuItemClickListener(DialogMode dialogMode) {
		this.dialogMode = dialogMode;
	}

	@Override
	public void buttonClick(Button.ClickEvent event) {
		ConfirmDialogButton.showDialog(
				dialogMode,
				getConfirmDialogTitle(),
				getConfirmDialogMessage(),
				getConfirmDialogOKCaption(),
				null,
				getConfirmDialogCancelCaption(),
				new ConfirmDialog.Listener() {
					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							confirmButtonClick(dialog);
						} else {
							dialogClosedWitoutConfirm(dialog);
						}
					}
				});
	}

	@Override
	public void contextMenuItemClicked(ContextMenuItemClickEvent event) {
		ConfirmDialogButton.showDialog(
				dialogMode,
				getConfirmDialogTitle(),
				getConfirmDialogMessage(),
				getConfirmDialogOKCaption(),
				null,
				getConfirmDialogCancelCaption(),
				new ConfirmDialog.Listener() {
					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							confirmButtonClick(dialog);
						} else {
							dialogClosedWitoutConfirm(dialog);
						}
					}
				});
	}

	protected String getConfirmDialogTitle() {
		return $("ConfirmDialog.title");
	}

	protected String getConfirmDialogOKCaption() {
		return $("ConfirmDialog.yes");
	}

	protected String getConfirmDialogCancelCaption() {
		return $("ConfirmDialog.no");
	}

	protected void dialogClosedWitoutConfirm(ConfirmDialog dialog) {
	}

	protected abstract String getConfirmDialogMessage();

	protected abstract void confirmButtonClick(ConfirmDialog dialog);

}
