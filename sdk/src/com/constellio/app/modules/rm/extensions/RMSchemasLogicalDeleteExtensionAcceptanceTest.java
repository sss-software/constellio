package com.constellio.app.modules.rm.extensions;

import com.constellio.app.modules.rm.RMTestRecords;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.model.entities.records.Content;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.contents.ContentManager;
import com.constellio.model.services.contents.ContentVersionDataSummary;
import com.constellio.model.services.records.RecordServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.QueryCounter;
import com.constellio.sdk.tests.setups.Users;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Random;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class RMSchemasLogicalDeleteExtensionAcceptanceTest extends ConstellioTest {

	RMTestRecords records = new RMTestRecords(zeCollection);
	RMSchemasRecordsServices rm;
	Users users = new Users();
	RecordServices recordServices;

	@Before
	public void setUp() throws Exception {
		prepareSystem(withZeCollection().withAllTest(users).withConstellioRMModule().withRMTest(records)
				.withFoldersAndContainersOfEveryStatus());
		rm = new RMSchemasRecordsServices(zeCollection, getAppLayerFactory());
		recordServices = getModelLayerFactory().newRecordServices();
	}

	@Test
	public void givenFolderIsContainingADocumentWithABorrowedDocumentThenNotLogicallyDeletable() throws Exception {
		Transaction tx = new Transaction();
		Folder subFolder = tx.add(rm.newFolder().setParentFolder(records.folder_A04)
				.setOpenDate(new LocalDate()).setTitle("sd"));
		Document document = tx.add(rm.newDocument().setFolder(subFolder).setTitle("test").setContent(newContent()));
		tx.add(users.robinIn(zeCollection).setCollectionDeleteAccess(true));
		recordServices.execute(tx);

		QueryCounter queryCounter = new QueryCounter(getDataLayerFactory());
		assertThat(recordServices.validateLogicallyDeletable(records.getFolder_A04(), User.GOD).getValidationErrors()).isEmpty();
		assertThat(recordServices.validateLogicallyDeletable(subFolder, User.GOD).getValidationErrors()).isEmpty();
		assertThat(recordServices.validateLogicallyDeletable(document, User.GOD).getValidationErrors()).isEmpty();
		//assertThat(queryCounter.newQueryCalls()).isZero();

		document.getContent().checkOut(users.charlesIn(zeCollection));
		recordServices.update(document);

		queryCounter.reset();
		assertThat(recordServices.validateLogicallyDeletable(records.getFolder_A04(), users.adminIn(zeCollection)).getValidationErrors()).isEmpty();
		assertThat(recordServices.validateLogicallyDeletable(subFolder, users.adminIn(zeCollection)).getValidationErrors()).isEmpty();
		assertThat(recordServices.validateLogicallyDeletable(document, users.adminIn(zeCollection)).getValidationErrors()).isEmpty();
		//assertThat(queryCounter.newQueryCalls()).isZero();

		queryCounter.reset();
		assertThat(recordServices.validateLogicallyDeletable(records.getFolder_A04(), users.robinIn(zeCollection)).getValidationErrors()).isNotEmpty();
		assertThat(recordServices.validateLogicallyDeletable(subFolder, users.robinIn(zeCollection)).getValidationErrors()).isNotEmpty();
		assertThat(recordServices.validateLogicallyDeletable(document, users.robinIn(zeCollection)).getValidationErrors()).isNotEmpty();
		//assertThat(queryCounter.newQueryCalls()).isZero();

		queryCounter.reset();
		assertThat(recordServices.validateLogicallyDeletable(records.getFolder_A04(), User.GOD).getValidationErrors()).isNotEmpty();
		assertThat(recordServices.validateLogicallyDeletable(subFolder, User.GOD).getValidationErrors()).isNotEmpty();
		assertThat(recordServices.validateLogicallyDeletable(document, User.GOD).getValidationErrors()).isNotEmpty();
		//assertThat(queryCounter.newQueryCalls()).isZero();
	}


	@Test
	public void givenFolderIsLinkedInTaskThenNotDeletableUnlessDeleted() throws Exception {
		Transaction tx = new Transaction();
		Folder subFolder = tx.add(rm.newFolder().setParentFolder(records.folder_A04)
				.setOpenDate(new LocalDate()).setTitle("sd"));
		Document document = tx.add(rm.newDocument().setFolder(subFolder).setTitle("test").setContent(newContent()));
		tx.add(users.robinIn(zeCollection).setCollectionDeleteAccess(true));
		recordServices.execute(tx);

		QueryCounter queryCounter = new QueryCounter(getDataLayerFactory());
		assertThat(recordServices.validateLogicallyDeletable(records.getFolder_A04(), User.GOD).getValidationErrors()).isEmpty();
		assertThat(recordServices.validateLogicallyDeletable(subFolder, User.GOD).getValidationErrors()).isEmpty();
		//assertThat(queryCounter.newQueryCalls()).isZero();

		document.getContent().checkOut(users.charlesIn(zeCollection));
		recordServices.add(rm.newRMTask().setTitle("test")
				.setAssigner(users.aliceIn(zeCollection).getId())
				.setAssignee(users.bobIn(zeCollection).getId()).setAssignationDate(new LocalDate())
				.setLinkedFolders(asList(subFolder.getId())));

		queryCounter.reset();
		assertThat(recordServices.validateLogicallyDeletable(records.getFolder_A04(), users.adminIn(zeCollection)).getValidationErrors()).isEmpty();
		assertThat(recordServices.validateLogicallyDeletable(subFolder, users.adminIn(zeCollection)).getValidationErrors()).isEmpty();
		//assertThat(queryCounter.newQueryCalls()).isZero();

		queryCounter.reset();
		assertThat(recordServices.validateLogicallyDeletable(records.getFolder_A04(), users.robinIn(zeCollection)).getValidationErrors()).isNotEmpty();
		assertThat(recordServices.validateLogicallyDeletable(subFolder, users.robinIn(zeCollection)).getValidationErrors()).isNotEmpty();
		assertThat(recordServices.validateLogicallyDeletable(document, users.robinIn(zeCollection)).getValidationErrors()).isNotEmpty();
		//assertThat(queryCounter.newQueryCalls()).isZero();

		queryCounter.reset();
		assertThat(recordServices.validateLogicallyDeletable(records.getFolder_A04(), User.GOD).getValidationErrors()).isNotEmpty();
		assertThat(recordServices.validateLogicallyDeletable(subFolder, User.GOD).getValidationErrors()).isNotEmpty();
		//assertThat(queryCounter.newQueryCalls()).isZero();
	}


	private Content newContent() throws Exception {
		ContentManager contentManager = getModelLayerFactory().getContentManager();
		File tempFileWithContent = newTempFileWithContent("toto.txt", "Ceci est un test " + new Random().nextLong());
		ContentVersionDataSummary dataSummary = contentManager.upload(tempFileWithContent);
		return contentManager.createMinor(users.adminIn(zeCollection), "zetest.pdf", dataSummary);
	}
}
