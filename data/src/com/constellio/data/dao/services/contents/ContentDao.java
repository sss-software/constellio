package com.constellio.data.dao.services.contents;

import com.constellio.data.dao.services.contents.ContentDaoException.ContentDaoException_NoSuchContent;
import com.constellio.data.io.streamFactories.CloseableStreamFactory;
import org.joda.time.LocalDateTime;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface ContentDao {

	void moveFileToVault(File file, String newContentId);

	void add(String newContentId, InputStream newInputStream);

	void delete(List<String> contentIds);

	LocalDateTime getLastModification(String contentId);

	InputStream getContentInputStream(String contentId, String streamName)
			throws ContentDaoException_NoSuchContent;

	List<String> getFolderContents(String folderId);

	boolean isFolderExisting(String folderId);

	boolean isDocumentExisting(String documentId);

	long getContentLength(String vaultContentId);

	void moveFolder(String folderId, String newFolderId);

	void deleteFolder(String folderId);

	CloseableStreamFactory<InputStream> getContentInputStreamFactory(String id)
			throws ContentDaoException_NoSuchContent;


	File getFileOf(String contentId);

	void readLogsAndRepairs();
}