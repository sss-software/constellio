package com.constellio.app.ui.pages.synonyms;

import com.constellio.app.ui.pages.base.BasePresenter;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.search.SearchConfigurationsManager;

import java.util.Arrays;
import java.util.List;

public class EditSynonymsPresenter extends BasePresenter<EditSynonymsView> {
    List<String> synonyms;
    SearchConfigurationsManager searchConfigurationsManager;

    public EditSynonymsPresenter(EditSynonymsView view) {
        super(view);
        searchConfigurationsManager = modelLayerFactory.getSearchConfigurationsManager();
        this.synonyms = searchConfigurationsManager.getSynonyms();
    }

    @Override
    protected boolean hasPageAccess(String params, User user) {
        return true;
    }

    public void saveSynonyms(String synonymsAsOneString) {
        String[] stringList = synonymsAsOneString.split("\\r\\n|\\n|\\r");
        synonyms = Arrays.asList(stringList);
        searchConfigurationsManager.setSynonyms(synonyms);
    }

    public String getSynonmsAsOneString() {
        StringBuilder stringBuilder = new StringBuilder();

        for(String string : synonyms) {
            stringBuilder.append(string).append("\n");
        }

        return stringBuilder.toString();
    }

}
