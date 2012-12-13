/**
 * SolrServerConnection.java
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

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

/**
 * Singleton class for connecting to Solr. Keeps static Solr Connection.
 * 
 * @author philip
 */
public final class SolrServerConnection {
	private static final Logger LOGGER = Logger.getLogger(SolrServerConnection.class);
	private static SolrServer solr = null;
	private static SolrServerConnection conn = null;
	private static final String solrServerUrl = SearchConstants.getString("solrUrl");

	private SolrServerConnection() {
	}

	public static synchronized SolrServerConnection getInstance() {
		if (conn == null) {
			LOGGER.debug("Establishing new connection to SolrServer at " + solrServerUrl);
			conn = new SolrServerConnection();
			LOGGER.debug("...done. Returning it.");
		} else
			LOGGER.debug("Returning existing connection to SolrServer");
		return conn;
	}

	/**
	 * Connects to solrServer and returns the instance.
	 * 
	 * @return
	 */
	public SolrServer getSolrServer() {
		if (solr == null) {
			try {
				solr = new HttpSolrServer(solrServerUrl);
				((HttpSolrServer) solr).setRequestWriter(new BinaryRequestWriter());
				LOGGER.info("Connected to Solr server at " + solrServerUrl);
			} catch (Exception e) {
				LOGGER.fatal("Connecting to Solr server failed. URL = " + solrServerUrl);
				LOGGER.fatal(e);
			}
		}
		return solr;
	}
}
