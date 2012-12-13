/**
 * AbstractDocumentIndexer.java
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
import java.util.Collection;
import java.util.LinkedList;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

public abstract class AbstractDocumentIndexer extends AbstractIndexer {

	public AbstractDocumentIndexer() {
		LOGGER.info("Instance of Indexer was created.");
	}

	@Deprecated
	private boolean submitDocToSolr(SolrInputDocument doc) {
		SolrServer server = getSolrServer();
		boolean success = true;
		try {
			server.add(doc);
			server.commit();
			server.optimize();
		} catch (SolrServerException e) {
			LOGGER.fatal("Adding documents to Solr server failed.");
			LOGGER.fatal(e);
			success = false;
		} catch (IOException e) {
			LOGGER.fatal("Adding documents to Solr server failed.");
			LOGGER.fatal(e);
			success = false;
		}
		return success;
	}

	@Deprecated
	private Collection<SolrInputDocument> submitDocsToSolr(Collection<SolrInputDocument> docs) {
		SolrServer server = getSolrServer();
		Collection<SolrInputDocument> fails = new LinkedList<SolrInputDocument>();

		for (SolrInputDocument doc : docs) {
			try {

				server.add(doc);
				LOGGER.info("Add doc to Solr");
			} catch (Exception e) {
				fails.add(doc);
				LOGGER.fatal("Adding doc to Solr failed!");
				LOGGER.fatal(e);
			}
		}
		// TODO check if all server.add() calls really finish before the commit
		// https://issues.apache.org/jira/browse/SOLR-1976
		try {
			LOGGER.info("Commit to Solr.");
			server.commit();
		} catch (Exception e) {
			fails = docs;
			LOGGER.fatal("Committing to Solr failed!");
			LOGGER.fatal(e);
		}
		try {
			LOGGER.info("Optimize Solr index.");
			server.optimize();
		} catch (Exception e) {
			LOGGER.fatal("Optimizing Solr index failed!");
			LOGGER.fatal(e);
		}
		return fails;
	}
}
