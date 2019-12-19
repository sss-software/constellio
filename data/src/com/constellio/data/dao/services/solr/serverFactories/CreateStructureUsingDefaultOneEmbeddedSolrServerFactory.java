package com.constellio.data.dao.services.solr.serverFactories;

import com.constellio.data.dao.services.solr.FileSystemSolrManagerException;
import com.constellio.data.dao.services.solr.SolrServerFactory;
import com.constellio.data.io.concurrent.filesystem.AtomicFileSystem;
import com.constellio.data.io.services.facades.FileService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;

import java.io.File;
import java.io.IOException;

public class CreateStructureUsingDefaultOneEmbeddedSolrServerFactory implements SolrServerFactory {

	private static final String DEFAULT_CORE = "default";

	File structureFolder;

	File defaultStructure;

	FileService fileService;

	CoreContainer coreContainer;

	public CreateStructureUsingDefaultOneEmbeddedSolrServerFactory(FileService fileService, File structureFolder,
																   File defaultStructure) {
		super();
		this.fileService = fileService;
		this.structureFolder = structureFolder;
		this.defaultStructure = defaultStructure;
	}

	private static void validateStructure(File structureFolder) {
		if (!structureFolder.exists()) {
			throw FileSystemSolrManagerException.noSuchFolder(structureFolder);
		}

		File defaultCore = new File(structureFolder, DEFAULT_CORE);
		validateCoreFolder(defaultCore);
	}

	private static void validateCoreFolder(File coreFolder) {
		if (!coreFolder.exists()) {
			throw FileSystemSolrManagerException.noSuchFolder(coreFolder);
		}

		File confFolder = new File(coreFolder, "conf");

		File solrConfigFile = new File(confFolder, "solrconfig.xml");
		if (!solrConfigFile.exists()) {
			throw FileSystemSolrManagerException.noSuchSolrConfig(confFolder);
		}

		File schemaFile = new File(confFolder, "schema.xml");
		if (!schemaFile.exists()) {
			throw FileSystemSolrManagerException.noSuchSchema(confFolder);
		}
	}

	@Override
	public SolrClient newSolrServer(String coreName) {
		try {
			CoreContainer loadedCoreContainer = getLoadedCoreContainer();
			return new EmbeddedSolrServer(loadedCoreContainer, coreName);

		} catch (IOException e) {
			throw new CreateStructureUsingDefaultOneEmbeddedSolrServerFactoryRuntimeException.CannotCreateSolrServer(e);
		}
	}

	private CoreContainer getLoadedCoreContainer()
			throws IOException {
		synchronized (this) {
			if (coreContainer == null) {

				validateStructure(defaultStructure);

				fileService.copyDirectory(defaultStructure, structureFolder);

				validateStructure(structureFolder);

				coreContainer = new CoreContainer(structureFolder.getAbsolutePath());
				coreContainer.load();
			}
		}
		return coreContainer;
	}

	@Override
	public void clear() {
		if (coreContainer != null) {
			coreContainer.shutdown();
			coreContainer = null;
		}
	}

	@Override
	public AtomicFileSystem getConfigFileSystem(String core) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public AtomicFileSystem getConfigFileSystem() {
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public void reloadSolrServer(String core) {
		throw new UnsupportedOperationException("TODO");
	}

}