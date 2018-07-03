package com.constellio.app.ui.pages.unicitymetadataconf;

import com.constellio.app.modules.rm.RMTestRecords;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.ui.framework.builders.MetadataToVOBuilder;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.app.ui.pages.summarycolumn.SummaryColumnParams;
import com.constellio.app.ui.pages.summarycolumn.SummaryColumnPresenter;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.FakeSessionContext;
import com.constellio.sdk.tests.MockedNavigation;
import com.constellio.sdk.tests.setups.Users;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class FolderUnicityMetadataPresenterAcceptanceTest extends ConstellioTest {
    @Mock
    FolderUnicityMetadataView view;
    MockedNavigation navigator;
    @Mock
    SessionContext sessionContext;

    FolderUnicityMetadataPresenter folderUnicityMetadataPresenter;
    Users users;
    RMTestRecords records = new RMTestRecords(zeCollection);
    RMSchemasRecordsServices rmSchemasRecordsServices;

    @Before
    public void setUp() {
        users = new Users();
        prepareSystem(withZeCollection().withConstellioRMModule().withRMTest(records).withAllTest(users));

        navigator = new MockedNavigation();

        when(view.getConstellioFactories()).thenReturn(getConstellioFactories());
        when(view.getCollection()).thenReturn(zeCollection);
        when(view.getSessionContext()).thenReturn(sessionContext);
        when(sessionContext.getCurrentCollection()).thenReturn(zeCollection);
        when(sessionContext.getCurrentLocale()).thenReturn(Locale.FRENCH);

        folderUnicityMetadataPresenter = new FolderUnicityMetadataPresenter(view, Folder.DEFAULT_SCHEMA);

        rmSchemasRecordsServices = new RMSchemasRecordsServices(zeCollection, getAppLayerFactory());
    }

    @Test
    public void givenOneMetatSummaryThenMetadataHaveMetadataSummy() {

        MetadataToVOBuilder builder = new MetadataToVOBuilder();
        folderUnicityMetadataPresenter.getMetadatas();
        FolderUnicityMetadataParams folderUnicityMetadataParams = new FolderUnicityMetadataParams();
        folderUnicityMetadataParams.setMetadataVO(builder.build(Schemas.TITLE, FakeSessionContext.adminInCollection(zeCollection)));

        folderUnicityMetadataPresenter.addMetadaForUnicity(folderUnicityMetadataParams);

        Metadata metadata = folderUnicityMetadataPresenter.getFolderUnicityMetadata();

        List summaryComlomnList = (List) metadata.getCustomParameter().get(FolderUnicityMetadataPresenter.UNICITY_CONFIG);

        assertThat(summaryComlomnList).isNotNull();
        assertThat(summaryComlomnList.size()).isEqualTo(1);
        assertThat(folderUnicityMetadataParams.getMetadataVO().getCode()).isEqualTo(Schemas.TITLE.getCode());
    }

    @Test
    public void givenOneMetatSummaryInFolderEmployeThenMetadataHaveMetadataSummy() {
        FolderUnicityMetadataPresenter folderUnicityMetadataParams = new FolderUnicityMetadataPresenter(view, "folder_employe");

        MetadataToVOBuilder builder = new MetadataToVOBuilder();
        FolderUnicityMetadataParams folderUnicityMetadataParams1 = new FolderUnicityMetadataParams();
        folderUnicityMetadataParams1.setMetadataVO(builder.build(rmSchemasRecordsServices.folder.description(), FakeSessionContext.adminInCollection(zeCollection)));

        folderUnicityMetadataPresenter.addMetadaForUnicity(folderUnicityMetadataParams1);

        folderUnicityMetadataParams.getMetadatas();
        FolderUnicityMetadataParams folderUnicityMetadataParams2 = new FolderUnicityMetadataParams();
        folderUnicityMetadataParams2.setMetadataVO(builder.build(rmSchemasRecordsServices.folderSchemaType().getSchema("employe").get("title"), FakeSessionContext.adminInCollection(zeCollection)));

        folderUnicityMetadataParams.addMetadaForUnicity(folderUnicityMetadataParams2);

        Metadata metadata = folderUnicityMetadataParams.getFolderUnicityMetadata();

        List summaryColomnList = (List) metadata.getCustomParameter().get(FolderUnicityMetadataPresenter.UNICITY_CONFIG);

        Map<String,Object> summarymap = (Map<String, Object>) summaryColomnList.get(0);

        assertThat(summaryColomnList).isNotNull();
        assertThat(summaryColomnList.size()).isEqualTo(1);
        assertThat(summarymap.get(SummaryColumnPresenter.METADATA_CODE)).isEqualTo(rmSchemasRecordsServices.folderSchemaType().getSchema("employe").get("title").getCode());
    }
}
