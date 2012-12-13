/**
 * AbstractPOJOIndexer.java
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
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;

import eu.prestoprime.search.util.IndexObject;

public class AbstractPOJOIndexer extends AbstractIndexer {

	protected static final Logger LOGGER = Logger.getLogger(AbstractPOJOIndexer.class);

	public AbstractPOJOIndexer() {
		super();
	}

	protected boolean addObjectToIndex(IndexObject object) {
		UpdateResponse ur = null;
		try {
			ur = getSolrServer().addBean(object);
			getSolrServer().commit();
		} catch (SolrServerException e) {
			LOGGER.fatal("Could not connect to Solr Server.");
			LOGGER.fatal(e.getMessage());
		} catch (IOException e) {
			LOGGER.fatal("IO Operation failed while indexing.");
			LOGGER.fatal(e.getMessage());
		}
		LOGGER.debug("Update status = " + ur.getStatus());
		// TODO get useful stuff from the updateResponse and return it here
		return ur != null ? true : false;
	}

	protected boolean addObjectsToIndex(List<IndexObject> objectList) {
		UpdateResponse ur = null;
		try {
			ur = getSolrServer().addBeans(objectList);
			getSolrServer().commit();
		} catch (SolrServerException e) {
			LOGGER.fatal("Could not connect to Solr Server.");
			LOGGER.fatal(e.getMessage());
		} catch (IOException e) {
			LOGGER.fatal("IO Operation failed while indexing.");
			LOGGER.fatal(e.getMessage());
		}

		// TODO get useful stuff from the updateResponse and return it here
		return ur != null ? true : false;
	}

	protected boolean removeObjectFromIndex(String id) {
		boolean success = true;
		try {
			getSolrServer().deleteByQuery("id: " + id);
			getSolrServer().commit();
		} catch (SolrServerException e) {
			LOGGER.fatal("Removing document from Solr server failed.");
			LOGGER.fatal(e);
			success = false;
		} catch (IOException e) {
			LOGGER.fatal("Removing document from Solr server failed.");
			LOGGER.fatal(e);
			success = false;
		}
		return success;
	}

}