package com.constellio.app.ui.framework.buttons.SIPButton;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.application.Navigation;
import com.constellio.app.ui.entities.BagInfoVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.buttons.WindowButton;
import com.constellio.app.ui.pages.SIP.BagInfoSIPForm;
import com.constellio.app.ui.pages.base.ConstellioHeader;
import com.constellio.model.frameworks.validation.ValidationException;
import com.vaadin.ui.Component;

public class SIPButtonImpl extends WindowButton {

    private List<RecordVO> objectList = new ArrayList<>();

    private ConstellioHeader view;
    private SIPButtonPresenter presenter;

    public SIPButtonImpl(String caption, String windowCaption, ConstellioHeader view) {
        super(caption, windowCaption, new WindowConfiguration(true, true, "75%", "75%"));
        this.view = view;
        this.presenter = new SIPButtonPresenter(this, objectList);
    }

    @Override
    protected Component buildWindowContent() {
        return new BagInfoSIPForm() {
            @Override
            protected void saveButtonClick(BagInfoVO viewObject) throws ValidationException {
                presenter.saveButtonClick(viewObject);
            }
        };
    }

    protected void showMessage(String value) {
        this.view.getCurrentView().showMessage(value);
    }

    protected void closeAllWindows(){
        this.view.getCurrentView().closeAllWindows();
    }

    public void showErrorMessage(String value) {
        this.view.getCurrentView().showErrorMessage(value);
    }

    public Navigation navigate(){
        return ConstellioUI.getCurrent().navigate();
    }

    public ConstellioHeader getView() {
        return view;
    }

    public void addAllObject(RecordVO... objects) {
        objectList.addAll(asList(objects));
    }

    public void setAllObject(RecordVO... objects) {
        objectList = new ArrayList<>();
        objectList.addAll(asList(objects));
    }
}
