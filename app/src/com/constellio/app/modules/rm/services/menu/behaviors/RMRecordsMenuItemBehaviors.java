package com.constellio.app.modules.rm.services.menu.behaviors;

import com.constellio.app.api.extensions.params.EmailMessageParams;
import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.modules.rm.extensions.RMSelectionPanelExtension;
import com.constellio.app.modules.rm.model.labelTemplate.LabelTemplate;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.services.actions.ContainerRecordActionsServices;
import com.constellio.app.modules.rm.services.actions.DocumentRecordActionsServices;
import com.constellio.app.modules.rm.services.actions.FolderRecordActionsServices;
import com.constellio.app.modules.rm.services.cart.CartEmailService;
import com.constellio.app.modules.rm.services.cart.CartEmailServiceRuntimeException;
import com.constellio.app.modules.rm.services.decommissioning.DecommissioningService;
import com.constellio.app.modules.rm.services.menu.behaviors.util.RMMessageUtil;
import com.constellio.app.modules.rm.services.menu.behaviors.util.RMUrlUtil;
import com.constellio.app.modules.rm.ui.builders.UserToVOBuilder;
import com.constellio.app.modules.rm.ui.buttons.CartWindowButton;
import com.constellio.app.modules.rm.ui.buttons.CartWindowButton.AddedRecordType;
import com.constellio.app.modules.rm.ui.components.folder.fields.LookupFolderField;
import com.constellio.app.modules.rm.ui.pages.pdf.ConsolidatedPdfButton;
import com.constellio.app.modules.rm.wrappers.ContainerRecord;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.modules.rm.wrappers.RMTask;
import com.constellio.app.modules.tasks.services.menu.behaviors.util.TaskUrlUtil;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.services.factories.ConstellioFactories;
import com.constellio.app.services.menu.behavior.MenuItemActionBehaviorParams;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.entities.UserVO;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.framework.buttons.BaseButton;
import com.constellio.app.ui.framework.buttons.BaseLink;
import com.constellio.app.ui.framework.buttons.DeleteButton;
import com.constellio.app.ui.framework.buttons.DeleteWithJustificationButton;
import com.constellio.app.ui.framework.buttons.SIPButton.SIPButtonImpl;
import com.constellio.app.ui.framework.buttons.WindowButton;
import com.constellio.app.ui.framework.buttons.report.LabelButtonV2;
import com.constellio.app.ui.framework.components.BaseWindow;
import com.constellio.app.ui.framework.stream.DownloadStreamResource;
import com.constellio.app.ui.framework.window.ConsultLinkWindow.ConsultLinkParams;
import com.constellio.app.ui.pages.base.BaseView;
import com.constellio.app.ui.pages.base.SchemaPresenterUtils;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.app.ui.util.MessageUtils;
import com.constellio.data.io.services.facades.IOServices;
import com.constellio.data.utils.Factory;
import com.constellio.model.entities.records.Content;
import com.constellio.model.entities.records.ContentVersion;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.RecordUpdateOptions;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.extensions.ModelLayerCollectionExtensions;
import com.constellio.model.frameworks.validation.ValidationErrors;
import com.constellio.model.services.contents.ContentManager;
import com.constellio.model.services.contents.ContentVersionDataSummary;
import com.constellio.model.services.emails.EmailServices;
import com.constellio.model.services.emails.EmailServices.EmailMessage;
import com.constellio.model.services.migrations.ConstellioEIMConfigs;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.records.RecordServicesRuntimeException;
import com.constellio.model.services.search.zipContents.ZipContentsService;
import com.constellio.model.services.search.zipContents.ZipContentsService.NoContentToZipRuntimeException;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDateTime;
import org.vaadin.dialogs.ConfirmDialog;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.constellio.app.ui.framework.clipboard.CopyToClipBoard.copyConsultationLinkToClipBoard;
import static com.constellio.app.ui.i18n.i18n.$;
import static com.constellio.app.ui.util.UrlUtil.getConstellioUrl;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Slf4j
public class RMRecordsMenuItemBehaviors {

	private String collection;
	private AppLayerFactory appLayerFactory;
	private RecordServices recordServices;
	private DecommissioningService decommissioningService;
	private IOServices ioServices;

	private FolderRecordActionsServices folderRecordActionsServices;
	private DocumentRecordActionsServices documentRecordActionsServices;
	private ContainerRecordActionsServices containerRecordActionsServices;
	private ModelLayerCollectionExtensions modelCollectionExtensions;

	private static final String ZIP_CONTENT_RESOURCE = "zipContentsFolder";

	public RMRecordsMenuItemBehaviors(String collection, AppLayerFactory appLayerFactory) {
		this.collection = collection;
		this.appLayerFactory = appLayerFactory;
		recordServices = appLayerFactory.getModelLayerFactory().newRecordServices();
		decommissioningService = new DecommissioningService(collection, appLayerFactory);
		ioServices = appLayerFactory.getModelLayerFactory().getDataLayerFactory().getIOServicesFactory().newIOServices();
		this.modelCollectionExtensions = appLayerFactory.getModelLayerFactory().getExtensions().forCollection(collection);

		folderRecordActionsServices = new FolderRecordActionsServices(collection, appLayerFactory);
		documentRecordActionsServices = new DocumentRecordActionsServices(collection, appLayerFactory);
		containerRecordActionsServices = new ContainerRecordActionsServices(collection, appLayerFactory);
	}

	public void addToCart(List<String> recordIds, MenuItemActionBehaviorParams params) {
		CartWindowButton cartWindowButton =
				new CartWindowButton(getSelectedRecords(recordIds), params, AddedRecordType.MULTIPLE);
		cartWindowButton.addToCart();
	}

	public void move(List<String> recordIds, MenuItemActionBehaviorParams params) {
		WindowButton moveInFolderButton = new WindowButton($("ConstellioHeader.selection.actions.moveInFolder"),
				$("ConstellioHeader.selection.actions.moveInFolder"),
				WindowButton.WindowConfiguration.modalDialog("50%", "140px")) {
			@Override
			protected Component buildWindowContent() {
				VerticalLayout verticalLayout = new VerticalLayout();
				verticalLayout.addStyleName("no-scroll");
				verticalLayout.setSpacing(true);
				final LookupFolderField field = new LookupFolderField(true);
				field.focus();
				field.setWindowZIndex(BaseWindow.OVER_ADVANCED_SEARCH_FORM_Z_INDEX + 1);
				verticalLayout.addComponent(field);
				BaseButton saveButton = new BaseButton($("save")) {
					@Override
					protected void buttonClick(ClickEvent event) {
						String parentId = (String) field.getValue();
						try {
							moveFolder(parentId, getSelectedRecords(recordIds), params.getUser(), params.getView());
						} catch (Throwable e) {
							e.printStackTrace();
						}
						getWindow().close();
					}
				};
				saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
				HorizontalLayout hLayout = new HorizontalLayout();
				hLayout.setSpacing(true);
				hLayout.setSizeFull();
				hLayout.addComponent(saveButton);
				hLayout.setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);
				verticalLayout.addComponent(hLayout);
				return verticalLayout;
			}
		};
		moveInFolderButton.click();
	}

	public void copy(List<String> recordIds, MenuItemActionBehaviorParams params) {
		WindowButton duplicateButton = new WindowButton($("ConstellioHeader.selection.actions.duplicate"),
				$("ConstellioHeader.selection.actions.duplicate"),
				WindowButton.WindowConfiguration.modalDialog("550px", "200px")) {
			@Override
			protected Component buildWindowContent() {
				VerticalLayout verticalLayout = new VerticalLayout();
				verticalLayout.setSizeFull();
				verticalLayout.setSpacing(true);
				final LookupFolderField field = new LookupFolderField(true);
				field.focus();
				field.setWindowZIndex(BaseWindow.OVER_ADVANCED_SEARCH_FORM_Z_INDEX + 1);
				verticalLayout.addComponent(field);
				BaseButton saveButton = new BaseButton($("save")) {
					@Override
					protected void buttonClick(ClickEvent event) {
						String parentId = (String) field.getValue();
						duplicateButtonClicked(parentId, getSelectedRecords(recordIds), params.getUser(), params.getView());
						getWindow().close();
					}
				};
				saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
				HorizontalLayout hLayout = new HorizontalLayout();
				hLayout.setSizeFull();
				hLayout.addComponent(saveButton);
				hLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
				verticalLayout.addComponent(hLayout);
				return verticalLayout;
			}
		};
		duplicateButton.click();
	}


	public void createSipArchive(List<String> recordIds, MenuItemActionBehaviorParams params) {
		SIPButtonImpl sipButton = new SIPButtonImpl($("SIPButton.caption"), $("SIPButton.caption"),
				ConstellioUI.getCurrent().getHeader(), true) {
			@Override
			public void buttonClick(ClickEvent event) {
				RecordVO[] recordVOS = getRecordVOList(recordIds, params.getView()).toArray(new RecordVO[0]);
				setAllObject(recordVOS);
				super.buttonClick(event);
			}
		};
		sipButton.click();
	}

	public void sendEmail(List<String> recordIds, MenuItemActionBehaviorParams params) {
		Button button = new Button($("ConstellioHeader.selection.actions.prepareEmail"));
		button.addClickListener((event) -> {
			EmailMessage emailMessage = createEmail(recordIds, params.getUser(), params.getView());
			String filename = emailMessage.getFilename();
			InputStream stream = emailMessage.getInputStream();
			startDownload(stream, filename);
		});
		button.click();
	}

	public void createPdf(List<String> recordIds, MenuItemActionBehaviorParams params) {
		WindowButton pdfButton = new ConsolidatedPdfButton(recordIds) {
			@Override
			public void buttonClick(ClickEvent event) {
				List<Record> records = recordServices.getRecordsById(collection, recordIds);
				for (Record record : records) {
					if (!documentRecordActionsServices.isCreatePdfActionPossible(record, params.getUser())) {
						return;
					}
				}
				super.buttonClick(event);
			}
		};
		pdfButton.click();
	}

	public void printLabels(List<String> recordIds, MenuItemActionBehaviorParams params) {
		if (recordIds.isEmpty()) {
			return;
		}
		String schemaType = recordServices.getDocumentById(recordIds.get(0)).getSchemaCode().split("_")[0];

		Factory<List<LabelTemplate>> customLabelTemplatesFactory =
				() -> appLayerFactory.getLabelTemplateManager().listExtensionTemplates(schemaType);
		Factory<List<LabelTemplate>> defaultLabelTemplatesFactory =
				() -> appLayerFactory.getLabelTemplateManager().listTemplates(schemaType);
		UserToVOBuilder userToVOBuilder = new UserToVOBuilder();
		UserVO userVO = userToVOBuilder.build(params.getUser().getWrappedRecord(),
				VIEW_MODE.DISPLAY, params.getView().getSessionContext());

		final LabelButtonV2 labelsButton = new LabelButtonV2($("SearchView.printLabels"),
				$("SearchView.printLabels"),
				customLabelTemplatesFactory,
				defaultLabelTemplatesFactory,
				appLayerFactory, collection, userVO);
		labelsButton.setSchemaType(schemaType);
		labelsButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				labelsButton.setElementsWithIds(recordIds, schemaType, params.getView().getSessionContext());
			}
		});
		labelsButton.click();
	}

	public void addToSelection(List<String> recordIds, MenuItemActionBehaviorParams params) {
		BaseView view = params.getView();

		SessionContext sessionContext = view.getSessionContext();
		boolean someElementsNotAdded = false;
		for (String selectedRecordId : recordIds) {
			Record record = recordServices.getDocumentById(selectedRecordId);

			if (asList(Folder.SCHEMA_TYPE, Document.SCHEMA_TYPE, ContainerRecord.SCHEMA_TYPE).contains(record.getTypeCode())) {
				sessionContext.addSelectedRecordId(selectedRecordId, record.getTypeCode());
			} else {
				someElementsNotAdded = true;
			}
		}

		if (someElementsNotAdded) {
			view.showErrorMessage($("ConstellioHeader.selection.cannotAddRecords"));
		}
	}

	public void downloadZip(List<String> recordIds, MenuItemActionBehaviorParams params) {
		BaseView view = params.getView();

		StreamSource streamSource = () -> {
			File folder = ioServices.newTemporaryFolder(ZIP_CONTENT_RESOURCE);
			File file = new File(folder, $("SearchView.contentZip"));
			try {
				new ZipContentsService(appLayerFactory.getModelLayerFactory(), collection)
						.zipContentsOfRecords(recordIds, file);
				return new FileInputStream(file);
			} catch (NoContentToZipRuntimeException e) {
				log.error("Error while zipping", e);
				view.showErrorMessage($("SearchView.noContentInSelectedRecords"));
				return null;
			} catch (Exception e) {
				log.error("Error while zipping", e);
				view.showErrorMessage($("SearchView.zipContentsError"));
				return null;
			}
		};

		BaseLink zipButton = new BaseLink($("ReportViewer.download", "(zip)"),
				new DownloadStreamResource(streamSource, $("SearchView.contentZip")));
		zipButton.click();
	}

	public boolean isNeedingAReasonToDeleteRecords() {
		return new RMConfigs(appLayerFactory.getModelLayerFactory().getSystemConfigurationsManager()).isNeedingAReasonBeforeDeletingFolders();
	}

	private int countPerShemaType(String schemaType, List<String> recordIds) {
		int counter = 0;

		if (recordIds == null) {
			return 0;
		}

		for (Record record : recordServices.getRecordsById(collection, recordIds)) {
			if (record.getSchemaCode().startsWith(schemaType)) {
				counter++;
			}
		}

		return counter;
	}

	public void showConsultLink(List<String> recordIds, MenuItemActionBehaviorParams params) {
		String constellioURL = getConstellioUrl(appLayerFactory.getModelLayerFactory());

		List<ConsultLinkParams> linkList = new ArrayList<>();

		List<Record> recordList = recordServices.getRecordsById(collection, recordIds);

		for (Record currentRecord : recordList) {
			if (currentRecord.getSchemaCode().startsWith(Document.SCHEMA_TYPE)) {
				linkList.add(new ConsultLinkParams(constellioURL + RMUrlUtil.getPathToConsultLinkForDocument(currentRecord.getId()),
						currentRecord.getTitle()));
			} else if (currentRecord.getSchemaCode().startsWith(Folder.SCHEMA_TYPE)) {
				linkList.add(new ConsultLinkParams(constellioURL + RMUrlUtil.getPathToConsultLinkForFolder(currentRecord.getId()),
						currentRecord.getTitle()));
			} else if (currentRecord.getSchemaCode().startsWith(ContainerRecord.SCHEMA_TYPE)) {
				linkList.add(new ConsultLinkParams(constellioURL + RMUrlUtil.getPathToConsultLinkForContainerRecord(currentRecord.getId()),
						currentRecord.getTitle()));
			} else if (currentRecord.getSchemaCode().startsWith(RMTask.SCHEMA_TYPE)) {
				linkList.add(new ConsultLinkParams(constellioURL + TaskUrlUtil.getPathToConsultLinkForTask(currentRecord.getId()),
						currentRecord.getTitle()));
			}
		}

		copyConsultationLinkToClipBoard(linkList);
	}

	public void batchDelete(List<String> recordIds, MenuItemActionBehaviorParams params) {
		Button button;
		if (!isNeedingAReasonToDeleteRecords()) {
			button = new DeleteButton(false) {
				@Override
				protected void confirmButtonClick(ConfirmDialog dialog) {
					deletionRequested(null, recordIds, params);
					Page.getCurrent().reload();
				}

				@Override
				protected String getConfirmDialogMessage() {
					int folderCount = countPerShemaType(Folder.SCHEMA_TYPE, recordIds);
					int documentCount = countPerShemaType(Document.SCHEMA_TYPE, recordIds);
					int containerCount = countPerShemaType(ContainerRecord.SCHEMA_TYPE, recordIds);

					StringBuilder stringBuilder = RMMessageUtil.getRecordCountByTypeAsText(folderCount, documentCount, containerCount);

					return $("CartView.deleteConfirmationMessageWithoutJustification", stringBuilder.toString());
				}
			};
		} else {
			button = new DeleteWithJustificationButton(false) {
				@Override
				protected void deletionConfirmed(String reason) {
					deletionRequested(reason, recordIds, params);
					Page.getCurrent().reload();
				}

				@Override
				protected String getConfirmDialogMessage() {
					int folderCount = countPerShemaType(Folder.SCHEMA_TYPE, recordIds);
					int documentCount = countPerShemaType(Document.SCHEMA_TYPE, recordIds);
					int containerCount = countPerShemaType(ContainerRecord.SCHEMA_TYPE, recordIds);

					StringBuilder stringBuilder = RMMessageUtil.getRecordCountByTypeAsText(folderCount, documentCount, containerCount);

					return $("CartView.deleteConfirmationMessage", stringBuilder.toString());
				}
			};
			((DeleteWithJustificationButton) button).setMessageContentMode(ContentMode.HTML);
		}

		button.click();
	}


	private boolean isBatchDeletePossible(List<String> recordIds, MenuItemActionBehaviorParams params) {
		return documentRecordActionsServices.canDeleteDocuments(recordIds, params.getUser())
			   && folderRecordActionsServices.canDeleteFolders(recordIds, params.getUser())
			   && containerRecordActionsServices.canDeleteContainers(recordIds, params.getUser());
	}

	private void deletionRequested(String reason, List<String> recordIds, MenuItemActionBehaviorParams params) {
		if (!isBatchDeletePossible(recordIds, params)) {
			params.getView().showErrorMessage($("cannotDelete"));
			return;
		}
		List<Record> recordsById = recordServices.getRecordsById(collection, recordIds);
		for (Record record : recordsById) {
			ValidationErrors validateDeleteAuthorized = modelCollectionExtensions.validateDeleteAuthorized(record, params.getUser());
			if (!validateDeleteAuthorized.isEmpty()) {
				MessageUtils.getCannotDeleteWindow(validateDeleteAuthorized).openWindow();
				return;
			}
		}

		for (Record record : recordsById) {
			delete(record, reason, params);
		}
	}

	protected final void delete(Record record, String reason,
								MenuItemActionBehaviorParams menuItemActionBehaviorParams) {
		delete(record, reason, true, false, menuItemActionBehaviorParams);
	}


	protected final void delete(Record record, String reason, boolean physically, boolean throwException,
								MenuItemActionBehaviorParams params) {
		SchemaPresenterUtils presenterUtils = new SchemaPresenterUtils(Document.DEFAULT_SCHEMA,
				params.getView().getConstellioFactories(), params.getView().getSessionContext());
		presenterUtils.delete(record, null, true, 1);
	}

	//
	// PRIVATE
	//

	private void moveFolder(String parentId, List<Record> records, User user, BaseView view)
			throws RecordServicesException {
		List<String> couldNotMove = new ArrayList<>();
		if (isNotBlank(parentId)) {
			RMSchemasRecordsServices rmSchemas = new RMSchemasRecordsServices(collection, appLayerFactory);
			List<Record> recordsToMove = new ArrayList<>();
			for (Record record : records) {
				try {
					switch (record.getTypeCode()) {
						case Folder.SCHEMA_TYPE:
							Folder folder = rmSchemas.wrapFolder(record);
							if (!folderRecordActionsServices.isMoveActionPossible(record, user)) {
								couldNotMove.add(record.getTitle());
								break;
							}
							Transaction txValidateFolder = new Transaction(folder.setParentFolder(parentId));
							txValidateFolder.setOptions(RecordUpdateOptions.userModificationsSafeOptions());
							recordServices.validateTransaction(txValidateFolder);
							recordsToMove.add(record);

							break;
						case Document.SCHEMA_TYPE:
							if (!documentRecordActionsServices.isMoveActionPossible(record, user)) {
								couldNotMove.add(record.getTitle());
								break;
							}
							Transaction txValidateDocument = new Transaction(rmSchemas.wrapDocument(record).setFolder(parentId));
							txValidateDocument.setOptions(RecordUpdateOptions.userModificationsSafeOptions());
							recordServices.validateTransaction(txValidateDocument);
							recordsToMove.add(record);

							break;
						default:
							couldNotMove.add(record.getTitle());
					}
				} catch (RecordServicesException.ValidationException e) {
					e.printStackTrace();
					couldNotMove.add(record.getTitle());
				}
			}

			Transaction tx = new Transaction(recordsToMove);
			tx.setOptions(RecordUpdateOptions.userModificationsSafeOptions());
			recordServices.executeHandlingImpactsAsync(tx);
		}


		if (couldNotMove.isEmpty()) {
			view.showErrorMessage($("ConstellioHeader.selection.actions.actionCompleted", records.size()));
		} else {
			int successCount = records.size() - couldNotMove.size();
			view.showErrorMessage($("ConstellioHeader.selection.actions.couldNotMove", successCount, records.size()));
		}
	}

	private void duplicateButtonClicked(String parentId, List<Record> records, User user, BaseView view) {
		List<String> couldNotDuplicate = new ArrayList<>();
		if (isNotBlank(parentId)) {
			RMSchemasRecordsServices rmSchemas = new RMSchemasRecordsServices(collection, appLayerFactory);
			for (Record record : records) {
				try {
					switch (record.getTypeCode()) {
						case Folder.SCHEMA_TYPE:
							if (!folderRecordActionsServices.isCopyActionPossible(record, user)) {
								couldNotDuplicate.add(record.getTitle());
								break;
							}
							Folder oldFolder = rmSchemas.wrapFolder(record);
							Folder newFolder = decommissioningService.duplicateStructureAndDocuments(oldFolder, user, false);
							newFolder.setParentFolder(parentId);
							recordServices.add(newFolder);
							break;
						case Document.SCHEMA_TYPE:
							if (!documentRecordActionsServices.isCopyActionPossible(record, user)) {
								couldNotDuplicate.add(record.getTitle());
								break;
							}
							Document oldDocument = rmSchemas.wrapDocument(record);
							Document newDocument = rmSchemas.newDocumentWithType(oldDocument.getType());
							for (Metadata metadata : oldDocument.getSchema().getMetadatas().onlyNonSystemReserved().onlyManuals().onlyDuplicable()) {
								newDocument.set(metadata, record.get(metadata));
							}
							LocalDateTime now = LocalDateTime.now();
							newDocument.setFormCreatedBy(user);
							newDocument.setFormCreatedOn(now);
							newDocument.setCreatedBy(user.getId()).setModifiedBy(user.getId());
							newDocument.setCreatedOn(now).setModifiedOn(now);
							if (newDocument.getContent() != null) {
								Content content = newDocument.getContent();
								ContentVersion contentVersion = content.getCurrentVersion();
								String filename = contentVersion.getFilename();
								ContentManager contentManager = rmSchemas.getModelLayerFactory().getContentManager();
								ContentVersionDataSummary contentVersionDataSummary = contentManager.getContentVersionSummary(contentVersion.getHash()).getContentVersionDataSummary();
								Content newContent = contentManager.createMajor(user, filename, contentVersionDataSummary);
								newDocument.setContent(newContent);
							}
							if (Boolean.TRUE == newDocument.getBorrowed()) {
								newDocument.setBorrowed(false);
							}
							String title = record.getTitle() + " (" + $("AddEditDocumentViewImpl.copy") + ")";
							newDocument.setTitle(title);
							newDocument.setFolder(parentId);
							recordServices.add(newDocument);
							break;
						default:
							couldNotDuplicate.add(record.getTitle());
					}
				} catch (RecordServicesException e) {
					couldNotDuplicate.add(record.getTitle());
				}
			}
		}

		if (couldNotDuplicate.isEmpty()) {
			view.showErrorMessage($("ConstellioHeader.selection.actions.actionCompleted", records.size()));
		} else {
			int successCount = records.size() - couldNotDuplicate.size();
			view.showErrorMessage($("ConstellioHeader.selection.actions.couldNotDuplicate", successCount, records.size()));
		}
	}

	private EmailMessage createEmail(List<String> recordIds, User user, BaseView view) {
		File newTempFile = null;
		try {
			newTempFile = ioServices.newTemporaryFile("RMSelectionPanelExtension-emailFile");
			return createEmail(recordIds, user, view, newTempFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			ioServices.deleteQuietly(newTempFile);
		}
	}

	private EmailMessage createEmail(List<String> recordIds, User user, BaseView view, File messageFile) {
		try (OutputStream outputStream = ioServices.newFileOutputStream(messageFile, RMSelectionPanelExtension.class.getSimpleName() + ".createMessage.out")) {
			String signature = user.getSignature() != null ? user.getSignature() : user.getTitle();
			String subject = "";
			String from = user.getEmail();
			//FIXME current version get only cart documents attachments
			List<EmailServices.MessageAttachment> attachments = getDocumentsAttachments(recordIds);
			if (attachments == null || attachments.isEmpty()) {
				view.showErrorMessage($("ConstellioHeader.selection.actions.noApplicableRecords"));
				return null;
			} else if (attachments.size() != recordIds.size()) {
				view.showErrorMessage($("ConstellioHeader.selection.actions.couldNotSendEmail", attachments.size(), recordIds.size()));
			}

			AppLayerFactory appLayerFactory = ConstellioFactories.getInstance().getAppLayerFactory();
			EmailMessageParams params = new EmailMessageParams("selection", signature, subject, from, attachments);
			EmailMessage emailMessage = appLayerFactory.getExtensions().getSystemWideExtensions().newEmailMessage(params);
			if (emailMessage == null) {
				EmailServices emailServices = new EmailServices();
				ConstellioEIMConfigs configs = new ConstellioEIMConfigs(appLayerFactory.getModelLayerFactory());
				MimeMessage message = emailServices.createMimeMessage(from, subject, signature, attachments, configs);
				message.writeTo(outputStream);
				String filename = "cart.eml";
				InputStream inputStream = ioServices.newFileInputStream(messageFile, CartEmailService.class.getSimpleName() + ".createMessageForCart.in");
				emailMessage = new EmailMessage(filename, inputStream);
				attachments.forEach(attachment -> {
					ioServices.closeQuietly(attachment.getInputStream());
					IOUtils.closeQuietly(attachment.getInputStream());
				});
			}
			if (attachments.size() == recordIds.size()) {
				view.showErrorMessage($("ConstellioHeader.selection.actions.actionCompleted", attachments.size()));
			}
			return emailMessage;
		} catch (MessagingException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private List<EmailServices.MessageAttachment> getDocumentsAttachments(List<String> recordIds) {
		List<EmailServices.MessageAttachment> returnList = new ArrayList<>();
		RecordServices recordServices = appLayerFactory.getModelLayerFactory().newRecordServices();
		RMSchemasRecordsServices rmSchemasRecordsServices = new RMSchemasRecordsServices(collection, appLayerFactory);
		for (String currentDocumentId : recordIds) {
			Record record = recordServices.getDocumentById(currentDocumentId);
			if (record.isOfSchemaType(Document.SCHEMA_TYPE)) {
				try {
					Document document = rmSchemasRecordsServices.wrapDocument(record);
					if (document.getContent() != null) {
						EmailServices.MessageAttachment contentFile = createAttachment(document);
						returnList.add(contentFile);
					}
				} catch (RecordServicesRuntimeException.NoSuchRecordWithId e) {
					throw new CartEmailServiceRuntimeException.CartEmlServiceRuntimeException_InvalidRecordId(e);
				}
			}
		}
		return returnList;
	}

	private EmailServices.MessageAttachment createAttachment(Document document) {
		Content content = document.getContent();
		String hash = content.getCurrentVersion().getHash();
		ContentManager contentManager = appLayerFactory.getModelLayerFactory().getContentManager();
		InputStream inputStream = contentManager.getContentInputStream(hash, content.getCurrentVersion().getFilename());
		String mimeType = content.getCurrentVersion().getMimetype();
		String attachmentName = content.getCurrentVersion().getFilename();
		return new EmailServices.MessageAttachment().setMimeType(mimeType).setAttachmentName(attachmentName).setInputStream(inputStream);
	}

	private void startDownload(final InputStream stream, String filename) {
		Resource resource = new DownloadStreamResource(new StreamResource.StreamSource() {
			@Override
			public InputStream getStream() {
				return stream;
			}
		}, filename);
		Page.getCurrent().open(resource, null, false);
	}

	private List<RecordVO> getRecordVOList(List<String> ids, BaseView view) {
		List<RecordVO> recordsVO = new ArrayList<>();
		RecordToVOBuilder builder = new RecordToVOBuilder();
		for (String id : ids) {
			recordsVO.add(builder.build(recordServices.getDocumentById(id), RecordVO.VIEW_MODE.FORM, view.getSessionContext()));
		}
		return recordsVO;
	}

	private List<Record> getSelectedRecords(List<String> selectedRecordIds) {
		return recordServices.getRecordsById(collection, selectedRecordIds);
	}

}
