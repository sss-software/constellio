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
package com.constellio.app.ui.pages.management.updates;

import static com.constellio.app.ui.i18n.i18n.$;

import java.io.OutputStream;

import com.constellio.app.entities.modules.ProgressInfo;
import com.constellio.app.services.migrations.VersionsComparator;
import com.constellio.app.ui.pages.base.BaseViewImpl;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

public class UpdateManagerViewImpl extends BaseViewImpl implements UpdateManagerView, Receiver, SucceededListener {

	private final UpdateManagerPresenter presenter;
	private UploadWaitWindow uploadWaitWindow;
	private boolean error;
	final private Label restartMessage = new Label("<p style=\"color:red\">" + $("UpdateManagerViewImpl.restart") + "</p>",
			ContentMode.HTML);

	public UpdateManagerViewImpl() {
		presenter = new UpdateManagerPresenter(this);
	}

	@Override
	protected String getTitle() {
		return $("UpdateManagerViewImpl.viewTitle");
	}

	@Override
	protected Component buildMainComponent(ViewChangeEvent event) {
		error = false;

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setWidth("100%");

		uploadWaitWindow = new UploadWaitWindow();

		Label versionTitle = new Label($("UpdateManagerViewImpl.version"));
		Label currentVersion = new Label(presenter.getCurrentVersion());

		String changelog = presenter.getChangelog();

		layout.addComponent(versionTitle);
		layout.addComponent(currentVersion);

		if (changelog == null) {
			setupManualUpload(layout);
		} else {
			setupAutomaticUpload(layout, changelog);
		}

		Button restart = new Button($("UpdateManagerViewImpl.restartButton"));
		restart.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				presenter.restart();
			}
		});
		restartMessage.setVisible(false);
		layout.addComponent(restartMessage);
		layout.addComponent(restart);

		return layout;
	}

	private void setupManualUpload(VerticalLayout layout) {
		Upload upload = new Upload($("UpdateManagerViewImpl.caption"), this);
		upload.addSucceededListener(this);
		upload.setButtonCaption($("UpdateManagerViewImpl.upload"));

		layout.addComponent(upload);
	}

	private void setupAutomaticUpload(VerticalLayout layout, String changelog) {
		Label changelogTitle = new Label($("UpdateManagerViewImpl.changelog"));
		Label changelogLabel = new Label(changelog, ContentMode.HTML);
		Button update = new Button($("UpdateManagerViewImpl.updateButton"));
		update.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				final ProgressInfo progressInfo = new ProgressInfo() {
					@Override
					public void setTask(String task) {
						uploadWaitWindow.setTask(task);
					}

					@Override
					public void setProgressMessage(String progressMessage) {
						uploadWaitWindow.setProgressMessage(progressMessage);
					}
				};
				// Important to allow update of components in current UI from another Thread
				UI.getCurrent().setPollInterval(200);
				UI.getCurrent().addWindow(uploadWaitWindow);
				new Thread(UpdateManagerViewImpl.class.getName() + "-updateFromServer") {
					@Override
					public void run() {
						try {
							presenter.updateFromServer(progressInfo);
							restartMessage.setVisible(true);
							uploadWaitWindow.close();
						} catch (Throwable t) {
							uploadWaitWindow.setProgressMessage($("UpdateManagerViewImpl.error.automaticUpdate"));
							throw t;
						} finally {
							UI.getCurrent().access(new Runnable() {
								@Override
								public void run() {
									// No need to update components in current UI from another Thread anymore
									UI.getCurrent().setPollInterval(-1);
								}
							});
						}
					}
				}.start();
			}
		});

		boolean needUpdate = VersionsComparator
				.isFirstVersionBeforeSecond(presenter.getCurrentVersion(), presenter.getChangelogVersion());
		update.setEnabled(needUpdate);

		layout.addComponent(changelogTitle);
		layout.addComponent(changelogLabel);
		layout.addComponent(update);
	}

	@Override
	public void showError(String message) {
		error = true;
		showErrorMessage(message);
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		UI.getCurrent().addWindow(uploadWaitWindow);
		return presenter.getOutputStreamFor(filename, mimeType);
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		presenter.uploadSucceeded(new ProgressInfo());
		if (!error) {
			restartMessage.setVisible(true);
		}
		uploadWaitWindow.close();
	}
}
