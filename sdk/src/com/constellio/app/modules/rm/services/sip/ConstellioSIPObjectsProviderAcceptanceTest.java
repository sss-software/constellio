package com.constellio.app.modules.rm.services.sip;

import com.constellio.app.modules.rm.RMTestRecords;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.services.sip.data.intelligid.ConstellioSIPObjectsProvider;
import com.constellio.app.modules.rm.services.sip.filter.SIPFilter;
import com.constellio.app.modules.rm.services.sip.model.EntityRetriever;
import com.constellio.app.modules.rm.services.sip.model.SIPDocument;
import com.constellio.app.modules.rm.wrappers.Email;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.modules.rm.wrappers.SIParchive;
import com.constellio.app.ui.framework.buttons.SIPButton.SIPBuildAsyncTask;
import com.constellio.data.io.services.facades.IOServices;
import com.constellio.model.entities.batchprocess.AsyncTaskCreationRequest;
import com.constellio.model.entities.records.Content;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.services.contents.ContentManager;
import com.constellio.model.services.contents.ContentVersionDataSummary;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.schemas.MetadataList;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators;
import com.constellio.model.services.search.query.logical.condition.LogicalSearchCondition;
import com.constellio.sdk.tests.ConstellioTest;
import com.lowagie.text.Meta;
import jdk.internal.util.xml.impl.Input;
import org.apache.commons.codec.digest.DigestUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.ALL;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.in;
import static com.constellio.sdk.tests.TestUtils.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ConstellioSIPObjectsProviderAcceptanceTest extends ConstellioTest {
    RMTestRecords records = new RMTestRecords(zeCollection);
    SearchServices searchServices;
    RMSchemasRecordsServices rm;
    RecordServices recordServices;
    ContentManager contentManager;
    IOServices ioServices;
    EntityRetriever entityRetriever;
    List<Metadata> folderMetadata, documentMetadata;

    SIPFilter sipFilter;
    ConstellioSIPObjectsProvider objectsProvider;
    @Before
    public void setup(){
        prepareSystem(
                withZeCollection().withConstellioRMModule().withRMTest(records)
                        .withFoldersAndContainersOfEveryStatus().withAllTestUsers()
        );
        this.searchServices = getModelLayerFactory().newSearchServices();
        this.rm = new RMSchemasRecordsServices(zeCollection, getAppLayerFactory());
        this.contentManager = getModelLayerFactory().getContentManager();
        this.recordServices = getModelLayerFactory().newRecordServices();
        this.ioServices = getModelLayerFactory().getIOServicesFactory().newIOServices();
        this.sipFilter = new SIPFilter(zeCollection, getAppLayerFactory()).withIncludeFolderIds(Collections.singletonList(records.getFolder_A01().getId()));
        this.objectsProvider = new ConstellioSIPObjectsProvider(zeCollection, getAppLayerFactory(), sipFilter);
        this.entityRetriever = new EntityRetriever(zeCollection, getAppLayerFactory());
        this.folderMetadata = getModelLayerFactory().getMetadataSchemasManager().getSchemaTypes(zeCollection).getSchemaType(Folder.SCHEMA_TYPE).getAllMetadatas();
        this.documentMetadata = getModelLayerFactory().getMetadataSchemasManager().getSchemaTypes(zeCollection).getSchemaType(com.constellio.app.modules.rm.wrappers.Document.SCHEMA_TYPE).getAllMetadatas();
    }

    @Test
    public void testMetadataGettingFromDocumentContainsOnlyOne(){
        SIPDocument sipDocument = new SIPDocument(rm.newDocument(), this.documentMetadata, this.folderMetadata, this.entityRetriever);
        assertThat(this.objectsProvider.getMetadataIds(sipDocument)).containsOnly("typeDocument");
    }

    @Test
    public void testMetadataGettingFromEmailContainsCorrectMetadata(){
        SIPDocument sipDocument = new SIPDocument(rm.newEmail(), this.documentMetadata, this.folderMetadata, this.entityRetriever);
        assertThat(this.objectsProvider.getMetadataIds(sipDocument)).containsOnly(Email.EMAIL_TO, Email.EMAIL_FROM, Email.EMAIL_IN_NAME_OF, Email.EMAIL_CC_TO, Email.EMAIL_BCC_TO, Email.EMAIL_OBJECT);
    }

    @Test
    public void testThatEmailReturnsJoinFiles() throws Exception {
        SIPDocument sipDocument = null;
        InputStream fileinputstream = null;
        try{
            File emailFile = getTestResourceFile("testFile.msg");
            ContentVersionDataSummary summary = contentManager.upload(emailFile);
            String emailFileName = "emailTest.msg";
            Email email = rm.newEmail();
            email.setContent(contentManager.createMajor(records.getAdmin(), emailFileName, summary));
            email.setFolder(records.getFolder_A01());
            Transaction transaction = new Transaction();
            transaction.add(email);
            recordServices.execute(transaction);
            sipDocument = new SIPDocument(email, this.documentMetadata, this.folderMetadata, this.entityRetriever);
            fileinputstream = newFileInputStream(emailFile);
            assertThat(objectsProvider.getExtraFiles(sipDocument).keySet()).hasSize(((Map) rm.parseEmail(emailFile.getName(), fileinputstream).get("attachments")).size());
        } finally {
            if(sipDocument != null) {
                ioServices.deleteQuietly(sipDocument.getFile());
                ioServices.closeQuietly(fileinputstream);
            }
        }

    }

    private String[] getCodeFromMetadatas(List<Metadata> list){
        List<String> codes = new ArrayList<>();
        for(Metadata metadata : list) {
            codes.add(metadata.getCode());
        }
        return codes.toArray(new String[0]);
    }
}
