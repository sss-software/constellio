package com.constellio.app.modules.rm.extensions;

import com.constellio.app.api.extensions.PagesComponentsExtension;
import com.constellio.app.api.extensions.params.DecorateMainComponentAfterInitExtensionParams;
import com.constellio.app.modules.rm.constants.RMPermissionsTo;
import com.constellio.app.modules.rm.model.enums.FolderMediaType;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.ui.pages.containers.DisplayContainerViewImpl;
import com.constellio.app.modules.rm.ui.pages.folder.DisplayFolderViewImpl;
import com.constellio.app.modules.rm.wrappers.ContainerRecord;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.modules.tasks.model.wrappers.Task;
import com.constellio.app.modules.tasks.services.TasksSchemasRecordsServices;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.ui.framework.buttons.BaseButton;
import com.constellio.app.ui.framework.buttons.ConfirmDialogButton;
import com.constellio.app.ui.framework.buttons.WindowButton;
import com.constellio.app.ui.framework.components.BaseForm;
import com.constellio.app.ui.framework.components.fields.number.BaseIntegerField;
import com.constellio.app.ui.framework.decorators.base.ActionMenuButtonsDecorator;
import com.constellio.app.ui.pages.base.BasePresenterUtils;
import com.constellio.app.ui.pages.base.BaseView;
import com.constellio.app.ui.pages.base.BaseViewImpl;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.frameworks.validation.ValidationException;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.records.RecordServicesException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.joda.time.LocalDate;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.constellio.app.ui.i18n.i18n.$;

/**
 * Created by Constellio on 2017-03-16.
 */
public class RMRequestTaskButtonExtension extends PagesComponentsExtension {

    enum RequestType {
        EXTENSION, BORROW
    }

    String collection;
    AppLayerFactory appLayerFactory;
    ModelLayerFactory modelLayerFactory;
    TasksSchemasRecordsServices taskSchemas;
    RMSchemasRecordsServices rmSchemas;

    Button borrowRequestButton, returnRequestButton, reactivationRequestButton, borrowExtensionRequestButton;

    Button borrowFolderButton, borrowContainerButton;

    public RMRequestTaskButtonExtension(String collection, AppLayerFactory appLayerFactory) {
        this.collection = collection;
        this.appLayerFactory = appLayerFactory;
        this.modelLayerFactory = appLayerFactory.getModelLayerFactory();
        this.taskSchemas = new TasksSchemasRecordsServices(collection, appLayerFactory);
        this.rmSchemas = new RMSchemasRecordsServices(collection, appLayerFactory);
    }

    @Override
    public void decorateMainComponentAfterViewAssembledOnViewEntered(DecorateMainComponentAfterInitExtensionParams params) {
        super.decorateMainComponentAfterViewAssembledOnViewEntered(params);
        Component mainComponent = params.getMainComponent();
        Folder folder;
        ContainerRecord container;
        User currentUser;
        if (mainComponent instanceof DisplayFolderViewImpl) {
            DisplayFolderViewImpl view = (DisplayFolderViewImpl) mainComponent;
            folder = rmSchemas.getFolder(view.getRecord().getId());
            if(folder.getContainer() != null) {
                container = rmSchemas.getContainerRecord(folder.getContainer());
            } else {
                container = null;
            }
            currentUser = getCurrentUser(view);
            adjustButtons(folder, container, currentUser);
        } else if (mainComponent instanceof DisplayContainerViewImpl) {
            DisplayContainerViewImpl view = (DisplayContainerViewImpl) mainComponent;
            folder = null;
            container = rmSchemas.getContainerRecord(view.getPresenter().getContainerId());
            currentUser = getCurrentUser(view);
            adjustButtons(folder, container, currentUser);
        }
    }

    public void adjustButtons(Folder folder, ContainerRecord containerRecord, User currentUser) {
        borrowRequestButton.setVisible(isFolderBorrowable(folder, currentUser) || isContainerBorrowable(containerRecord, currentUser));
        returnRequestButton.setVisible(isPrincipalRecordReturnable(folder, containerRecord, currentUser));
        reactivationRequestButton.setVisible(isFolderReactivable(folder, currentUser));
        borrowExtensionRequestButton.setVisible(isPrincipalRecordReturnable(folder, containerRecord, currentUser));
    }

    private boolean isFolderBorrowable(Folder folder, User currentUser) {
        return folder != null && !Boolean.TRUE.equals(folder.getBorrowed()) && currentUser.has(RMPermissionsTo.BORROWING_REQUEST_ON_FOLDER).on(folder);
    }

    private boolean isContainerBorrowable(ContainerRecord container, User currentUser) {
        return container != null && !Boolean.TRUE.equals(container.getBorrowed()) && currentUser.has(RMPermissionsTo.BORROWING_REQUEST_ON_FOLDER).on(container);
    }

    private boolean isPrincipalRecordReturnable(Folder folder, ContainerRecord container, User currentUser) {
        if(folder != null) {
            return folder != null && Boolean.TRUE.equals(folder.getBorrowed()) && currentUser.getId().equals(folder.getBorrowUser());
        } else {
            return container != null && Boolean.TRUE.equals(container.getBorrowed()) && currentUser.getId().equals(container.getBorrower());
        }
    }

    private boolean isFolderReactivable(Folder folder, User currentUser) {
        return folder != null && folder.getArchivisticStatus().isSemiActiveOrInactive() && folder.getMediaType().equals(FolderMediaType.ANALOG);
    }

    @Override
    public void decorateMainComponentBeforeViewInstanciated(DecorateMainComponentAfterInitExtensionParams params) {

    }

    @Override
    public void decorateMainComponentBeforeViewAssembledOnViewEntered(DecorateMainComponentAfterInitExtensionParams params) {
        super.decorateMainComponentAfterViewAssembledOnViewEntered(params);
        Component mainComponent = params.getMainComponent();
        if (mainComponent instanceof DisplayFolderViewImpl || mainComponent instanceof DisplayContainerViewImpl) {
            BaseViewImpl view = (BaseViewImpl) mainComponent;
            view.addActionMenuButtonsDecorator(new ActionMenuButtonsDecorator() {
                @Override
                public void decorate(final BaseViewImpl view, List<Button> actionMenuButtons) {
                    actionMenuButtons.add(buildRequestBorrowButton(view));
                    actionMenuButtons.add(buildRequestReturnButton(view));
                    actionMenuButtons.add(buildRequestReactivationButton(view));
                    actionMenuButtons.add(buildRequestBorrowExtensionButton(view));
                }
            });
        }
    }

    private User getCurrentUser(BaseView view) {
        BasePresenterUtils basePresenterUtils = new BasePresenterUtils(view.getConstellioFactories(), view.getSessionContext());
        return basePresenterUtils.getCurrentUser();
    }

    private Button buildRequestBorrowButton(final BaseViewImpl view) {
        return borrowRequestButton = new WindowButton($("RMRequestTaskButtonExtension.borrowRequest"), $("RMRequestTaskButtonExtension.requestBorrowButtonTitle")) {
            @Override
            protected Component buildWindowContent() {
                final Context context = buildContext(view);
                VerticalLayout mainLayout = new VerticalLayout();
                TextArea messageField = new TextArea();
                messageField.setValue($("RMRequestTaskButtonExtension.requestBorrowFolderOrContainerButtonMessage"));
                messageField.setReadOnly(true);
                messageField.setWidth("100%");
                messageField.addStyleName(ValoTheme.TEXTAREA_BORDERLESS);
                final BaseIntegerField borrowDurationField = new BaseIntegerField($("RMRequestTaskButtonExtension.borrowDuration"));
                HorizontalLayout buttonLayout = new HorizontalLayout();

                BaseButton borrowFolderButton = new BaseButton($("RMRequestTaskButtonExtension.confirmBorrowFolder")) {
                    @Override
                    protected void buttonClick(ClickEvent event) {
                        borrowRequest(view, false, borrowDurationField.getValue());
                        getWindow().close();
                    }

                    @Override
                    public boolean isVisible() {
                        return isFolderBorrowable(context.getFolder(), context.getCurrentUser());
                    }
                };
                BaseButton borrowContainerButton = new BaseButton($("RMRequestTaskButtonExtension.confirmBorrowContainer")) {
                    @Override
                    protected void buttonClick(ClickEvent event) {
                        borrowRequest(view, true, borrowDurationField.getValue());
                        getWindow().close();
                    }

                    @Override
                    public boolean isVisible() {
                        return isContainerBorrowable(context.getContainer(), context.getCurrentUser());
                    }
                };
                BaseButton cancelButton = new BaseButton($("cancel")) {
                    @Override
                    protected void buttonClick(ClickEvent event) {
                        getWindow().close();
                    }
                };

                buttonLayout.setSpacing(true);
                buttonLayout.addComponents(cancelButton, borrowContainerButton, borrowFolderButton);

                mainLayout.setHeight("100%");
                mainLayout.setWidth("100%");
                mainLayout.setSpacing(false);
                mainLayout.addComponents(messageField, borrowDurationField, buttonLayout);

                return mainLayout;
            }
        };
    }

    private Button buildRequestReturnButton(final BaseViewImpl view) {
        return returnRequestButton = new ConfirmDialogButton($("RMRequestTaskButtonExtension.returnRequest")) {

            @Override
            protected String getConfirmDialogMessage() {
                return $("DisplayFolderView.confirmReturnMessage");
            }

            @Override
            protected void confirmButtonClick(ConfirmDialog dialog) {
                returnRequest(view);
            }
        };
    }

    private Button buildRequestReactivationButton(final BaseViewImpl view) {
        return reactivationRequestButton = new ConfirmDialogButton($("RMRequestTaskButtonExtension.reactivationRequest")) {

            @Override
            protected String getConfirmDialogMessage() {
                return $("DisplayFolderView.confirmReactivationMessage");
            }

            @Override
            protected void confirmButtonClick(ConfirmDialog dialog) {
                reactivationRequested(view);
            }
        };
    }

    private Button buildRequestBorrowExtensionButton(final BaseViewImpl view) {
        return borrowExtensionRequestButton = new WindowButton($("RMRequestTaskButtonExtension.borrowExtensionRequest"), $("RMRequestTaskButtonExtension.borrowExtensionRequest")) {
            @PropertyId("value")
            private InlineDateField datefield;

            @Override
            protected Component buildWindowContent() {
                Label dateLabel = new Label($("DisplayFolderView.chooseDateToExtends"));

                datefield = new InlineDateField();
                List<BaseForm.FieldAndPropertyId> fields = Collections.singletonList(new BaseForm.FieldAndPropertyId(datefield, "value"));
                Request req = new Request(new Date(), RequestType.EXTENSION);
                BaseForm form = new BaseForm<Request>(req, this, datefield) {

                    @Override
                    protected void saveButtonClick(Request viewObject) throws ValidationException {
                        viewObject.setValue(datefield.getValue());
                        borrowExtensionRequested(view, viewObject);
                        getWindow().close();
                    }

                    @Override
                    protected void cancelButtonClick(Request viewObject) {
                        getWindow().close();
                    }
                };
                return form;
            }

            public void setDatefield(InlineDateField datefield) {
                this.datefield = datefield;
            }

            public InlineDateField getDatefield() {
                return this.datefield;
            }
        };
    }

    public void borrowRequest(BaseViewImpl view, boolean isContainer, String inputForNumberOfDays) {
        Context context = buildContext(view);
        Folder folder = context.getFolder();
        ContainerRecord container = context.getContainer();
        User currentUser = context.getCurrentUser();
        int numberOfDays = 1;
        if(inputForNumberOfDays != null && inputForNumberOfDays.matches("^-?\\d+$")) {
            numberOfDays = Integer.parseInt(inputForNumberOfDays);

            if(numberOfDays <= 0) {
                view.showErrorMessage($("RMRequestTaskButtonExtension.invalidBorrowDuration"));
                return;
            }
        } else {
            view.showErrorMessage($("RMRequestTaskButtonExtension.invalidBorrowDuration"));
            return;
        }
        try {
            Task borrowRequest;
            if(isContainer) {
                String recordId = container.getId();
                borrowRequest = taskSchemas.newBorrowContainerRequestTask(currentUser.getId(), getAssignees(recordId), recordId, numberOfDays, container.getTitle());
            } else {
                String recordId = folder.getId();
                borrowRequest = taskSchemas.newBorrowFolderRequestTask(currentUser.getId(), getAssignees(recordId), recordId, numberOfDays, folder.getTitle());
            }
            modelLayerFactory.newRecordServices().add(borrowRequest);
        } catch (RecordServicesException e) {
            e.printStackTrace();
        }
    }

    public void returnRequest(BaseViewImpl view) {
        Context context = buildContext(view);
        Folder folder = context.getFolder();
        ContainerRecord container = context.getContainer();
        User currentUser = context.getCurrentUser();
        try {
            if(folder != null) {
                String folderId = folder.getId();
                Task returnRequest = taskSchemas.newReturnFolderRequestTask(currentUser.getId(), getAssignees(folderId), folderId, folder.getTitle());
                modelLayerFactory.newRecordServices().add(returnRequest);
            } else if (container != null) {
                String containerId = container.getId();
                Task returnRequest = taskSchemas.newReturnContainerRequestTask(currentUser.getId(), getAssignees(containerId), containerId, container.getTitle());
                modelLayerFactory.newRecordServices().add(returnRequest);
            }
        } catch (RecordServicesException e) {
            e.printStackTrace();
        }
    }

    public void reactivationRequested(BaseViewImpl view) {
        Context context = buildContext(view);
        Folder folder = context.getFolder();
        ContainerRecord container = context.getContainer();
        User currentUser = context.getCurrentUser();
        try {
            if(folder != null) {
                String folderId = folder.getId();
                Task reactivationRequest = taskSchemas.newReactivateFolderRequestTask(currentUser.getId(), getAssignees(folderId), folderId, folder.getTitle());
                modelLayerFactory.newRecordServices().add(reactivationRequest);
            } else if (container != null) {
                String containerId = container.getId();
                Task reactivationRequest = taskSchemas.newReactivationContainerRequestTask(currentUser.getId(), getAssignees(containerId), containerId, container.getTitle());
                modelLayerFactory.newRecordServices().add(reactivationRequest);
            }
        } catch (RecordServicesException e) {
            e.printStackTrace();
        }
    }

    public void borrowExtensionRequested(BaseViewImpl view, Request req) {
        Context context = buildContext(view);
        Folder folder = context.getFolder();
        ContainerRecord container = context.getContainer();
        User currentUser = context.getCurrentUser();
        try {
            if(folder != null) {
                String folderId = folder.getId();
                Task borrowExtensionRequest = taskSchemas.newBorrowFolderExtensionRequestTask(currentUser.getId(), getAssignees(folderId), folderId, folder.getTitle(), new LocalDate(req.getValue()));
                modelLayerFactory.newRecordServices().add(borrowExtensionRequest);
            } else if (container != null) {
                String containerId = container.getId();
                Task borrowExtensionRequest = taskSchemas.newBorrowContainerExtensionRequestTask(currentUser.getId(), getAssignees(containerId), containerId, container.getTitle(), new LocalDate(req.getValue()));
                modelLayerFactory.newRecordServices().add(borrowExtensionRequest);
            }
        } catch (RecordServicesException e) {
            e.printStackTrace();
        }
    }

    private List<String> getAssignees(String recordId) {
        return modelLayerFactory.newAuthorizationsServices()
                .getUserIdsWithPermissionOnRecord(RMPermissionsTo.MANAGE_REQUEST_ON_FOLDER, modelLayerFactory.newRecordServices().getDocumentById(recordId));
    }

    static public class Request {

        @PropertyId("value")
        public Object value;

        @PropertyId("type")
        public RequestType type;

        public Request() {

        }

        public Request(Object value, RequestType requestType) {
            this.type = requestType;
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        public Request setValue(Object value) {
            this.value = value;
            return this;
        }

        public RequestType getType() {
            return type;
        }

        public Request setType(RequestType type) {
            this.type = type;
            return this;
        }
    }

    public class Context {
        Folder folder;
        ContainerRecord container;
        User currentUser;

        public Context(Folder folder, ContainerRecord container, User currentUser) {
            this.folder = folder;
            this.container = container;
            this.currentUser = currentUser;
        }

        public Folder getFolder() {
            return folder;
        }

        public ContainerRecord getContainer() {
            return container;
        }

        public User getCurrentUser() {
            return currentUser;
        }
    }

    public Context buildContext(BaseViewImpl baseView) {
        Folder folder;
        ContainerRecord container;
        User currentUser;
        if (baseView instanceof DisplayFolderViewImpl) {
            DisplayFolderViewImpl view = (DisplayFolderViewImpl) baseView;
            folder = rmSchemas.getFolder(view.getRecord().getId());
            if(folder.getContainer() != null) {
                container = rmSchemas.getContainerRecord(folder.getContainer());
            } else {
                container = null;
            }
            currentUser = getCurrentUser(view);

        } else if (baseView instanceof DisplayContainerViewImpl) {
            DisplayContainerViewImpl view = (DisplayContainerViewImpl) baseView;
            folder = null;
            container = rmSchemas.getContainerRecord(view.getPresenter().getContainerId());
            currentUser = getCurrentUser(view);
        } else {
            return null;
        }
        return new Context(folder, container, currentUser);
    }
}
