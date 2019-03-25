package com.constellio.data.dao.services.bigVault.solr.serverFactories;

import com.constellio.data.dao.services.solr.serverFactories.CloudSolrServerFactory;
import com.constellio.sdk.tests.ConstellioTest;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CloudSolrServerFactoryTest extends ConstellioTest {

	String coreName = "core";
	String zkBase = "1.2.3.4:5";
	private CloudSolrServerFactory factory;

	@Before
	public void setUp()
			throws Exception {
		factory = new CloudSolrServerFactory(zkBase);
	}

	@After
	public void tearDown()
			throws Exception {
	}

	@Test
	public void whenGettingNewSolrServerThenSolrServerCreatedForCoreUrl() throws IOException {
		CloudSolrClient returnedSolrServer = (CloudSolrClient) factory.newSolrServer(coreName);

		assertEquals(coreName, returnedSolrServer.getDefaultCollection());

		returnedSolrServer.close();
	}

}