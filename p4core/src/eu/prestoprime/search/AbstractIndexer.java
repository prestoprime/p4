/**
 * AbstractIndexer.java
 * Author: Philip Kahle (philip.kahle@uibk.ac.at)
 * 
 * This file is part of PrestoPRIME Preservation Platform (P4).
 * 
 * Copyright (C) 2009-2012 University of Innsbruck, Austria
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.prestoprime.search;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;

abstract class AbstractIndexer {

	protected static final Logger LOGGER = Logger.getLogger(AbstractIndexer.class);
	private SolrServer solr = null;

	public AbstractIndexer() {
		super();
	}

	// protected SolrServer getEmbeddedSolrServer() {
	// return EmbeddedSolrConnection.getInstance().getEmbeddedSolrServer();
	// }

	protected SolrServer getSolrServer() {
		if (solr == null) {
			String solrServerUrl = SearchConstants.getString("solrUrl");
			int solrQueueSize = SearchConstants.getInt("solrQueueSize");

			try {
				LOGGER.debug("Establishing new connection to StreamingUpdateSolrServer at " + solrServerUrl);
				// TODO threadCount set to 3 for first tests. Investigate for
				// optimal value
				solr = new ConcurrentUpdateSolrServer(solrServerUrl, solrQueueSize, 3);
				((ConcurrentUpdateSolrServer) solr).setRequestWriter(new BinaryRequestWriter());
				LOGGER.info("Connected to Solr server for indexing.");
			} catch (MalformedURLException e) {
				LOGGER.fatal("Connecting to Solr server for indexing failed.");
				LOGGER.fatal(e);
			}
		}
		return solr;
	}

	protected boolean clearIndex() {
		boolean success = false;
		try {
			getSolrServer().deleteByQuery("*:*");
			getSolrServer().commit();
			success = true;
		} catch (SolrServerException e) {
			LOGGER.fatal("Connecting to Solr server for clearing the index failed.");
			LOGGER.fatal(e);
		} catch (IOException e) {
			LOGGER.fatal("Clearing the index was impossible due to some IO problem.");
			LOGGER.fatal(e);
		}
		return success;
	}

	protected boolean commit() {
		boolean success = false;
		try {
			getSolrServer().commit();
			success = true;
		} catch (SolrServerException e) {
			LOGGER.fatal("Connecting to Solr server for clearing the index failed.");
			LOGGER.fatal(e);
		} catch (IOException e) {
			LOGGER.fatal("Clearing the index was impossible due to some IO problem.");
			LOGGER.fatal(e);
		}
		return success;
	}

	protected boolean optimize() {
		boolean success = false;
		try {
			getSolrServer().optimize();
			success = true;
		} catch (SolrServerException e) {
			LOGGER.fatal("Connecting to Solr server for clearing the index failed.");
			LOGGER.fatal(e);
		} catch (IOException e) {
			LOGGER.fatal("Clearing the index was impossible due to some IO problem.");
			LOGGER.fatal(e);
		}
		return success;
	}
}