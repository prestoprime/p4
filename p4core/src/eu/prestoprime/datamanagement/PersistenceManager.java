/**
 * PersistenceManager.java
 * Author: Francesco Gallo (gallo@eurix.it)
 * Contributors: Francesco Rosso (rosso@eurix.it)
 * 
 * This file is part of PrestoPRIME Preservation Platform (P4).
 * 
 * Copyright (C) 2009-2012 EURIX Srl, Torino, Italy
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
package eu.prestoprime.datamanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;

import org.exist.storage.DBBroker;
import org.exist.xmldb.EXistResource;
import org.exist.xmldb.XQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

/**
 * Implements a connection to persistence DB, offering CRUD interfaces.
 */
public class PersistenceManager {

	private static final Logger logger = LoggerFactory.getLogger(PersistenceManager.class);
	private static PersistenceManager instance;
	private static final String DB_URI = "xmldb:exist://localhost:8080/p4db/xmlrpc";
	private static final String DB_USERNAME = "pprime";
	private static final String DB_PASSWORD = "pprime";
	private static final String ROOT_COLLECTION = DB_URI + DBBroker.ROOT_COLLECTION + "/p4";

	public static enum P4Collection {
		ADMIN_COLLECTION("admin"), WF_COLLECTION("wf"), AIP_COLLECTION("aip"), DMD_COLLECTION("dmd"), RIGHTSMD_COLLECTION("rightsmd"), TECHMD_COLLECTION("techmd"), SOURCEMD_COLLECTION("sourcemd"), DIGIPROVMD_COLLECTION("digiprovmd"), TEMP_COLLECTION("temp");

		private String collectionName;

		private P4Collection(String collectionName) {
			this.collectionName = collectionName;
		}

		private Collection getCollection() throws XMLDBException {

			// try to get collection
			Collection collection = DatabaseManager.getCollection(PersistenceManager.ROOT_COLLECTION + "/" + collectionName, PersistenceManager.DB_USERNAME, PersistenceManager.DB_PASSWORD);
			if (collection == null) {
				// if collection does not exist, get root collection and create
				// the new collection as a direct child of the root collection
				Collection rootCollection = DatabaseManager.getCollection(PersistenceManager.ROOT_COLLECTION, PersistenceManager.DB_USERNAME, PersistenceManager.DB_PASSWORD);
				CollectionManagementService mgtService = (CollectionManagementService) rootCollection.getService("CollectionManagementService", "1.0");
				collection = mgtService.createCollection(PersistenceManager.ROOT_COLLECTION + "/" + collectionName);

				logger.info("Created new Collection " + collectionName);
			}

			return collection;
		}

		public String getCollectionName() {
			return PersistenceManager.ROOT_COLLECTION + "/" + collectionName;
		}

		public static P4Collection getP4Collection(String collectionName) throws PersistenceDBException {
			collectionName = collectionName.toLowerCase();
			for (P4Collection p4collection : P4Collection.values()) {
				if (collectionName.equals(p4collection.collectionName))
					return p4collection;
			}
			throw new PersistenceDBException("Unable to find a P4Collection for collection " + collectionName + "...");
		}

		@Override
		public String toString() {
			return collectionName;
		}
	};

	private XQueryService service;
	private Map<String, CompiledExpression> queryMap;

	public static PersistenceManager getInstance() {
		if (instance == null)
			instance = new PersistenceManager();
		return instance;
	}

	private PersistenceManager() {
		try {
			// initialize driver
			String driver = "org.exist.xmldb.DatabaseImpl";
			Database database = (Database) Class.forName(driver).newInstance();

			logger.debug("Successfully loaded persistence DB driver...");

			// initialize persistence DB properties
			database.setProperty("create-database", "true");
			DatabaseManager.registerDatabase(database);

			logger.debug("Successfully registered persistence DB instance...");

			// get root-collection
			Collection rootCollection = DatabaseManager.getCollection(DB_URI + DBBroker.ROOT_COLLECTION);
			CollectionManagementService mgtService = (CollectionManagementService) rootCollection.getService("CollectionManagementService", "1.0");
			mgtService.createCollection(PersistenceManager.ROOT_COLLECTION);

			// get query-service
			service = (XQueryService) rootCollection.getService("XQueryService", "1.0");

			// set pretty-printing on
			service.setProperty(OutputKeys.INDENT, "yes");
			service.setProperty(OutputKeys.ENCODING, "UTF-8");

			logger.debug("Successfully created XQueryService...");

			// initialize query-map
			queryMap = new HashMap<>();

			logger.debug("Successfully initialized XQueryService...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cleanUpResources(P4Collection p4collection, Resource... resources) {
		try {
			for (int i = 0; i < resources.length; i++) {
				if (resources[i] != null)
					((EXistResource) resources[i]).freeResources();
			}
			if (p4collection != null)
				p4collection.getCollection().close();
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
	}

	private CompiledExpression getCompiledExpression(String queryFile) throws PersistenceDBException {

		// get compiledExpression
		CompiledExpression expression = queryMap.get(queryFile);

		// if it doesn't exists yet, compile&store
		if (expression == null) {
			try {
				BufferedReader f = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(queryFile)));

				logger.debug("Query File " + queryFile + " found...");

				String line;
				StringBuffer xml = new StringBuffer();
				while ((line = f.readLine()) != null)
					xml.append(line + "\n");
				f.close();

				expression = service.compile(xml.toString());

				queryMap.put(queryFile, expression);

				logger.debug("============== Added new CompiledExpression ==============");
				logger.debug(xml.toString());
				logger.debug("==========================================================");
			} catch (IOException e) {
				logger.error("Unable to read the query file " + queryFile);
				throw new PersistenceDBException();
			} catch (XMLDBException e) {
				logger.error("Unable to compile the query in file " + queryFile);
				throw new PersistenceDBException();
			}
		}

		return expression;
	}

	/**
	 * Connects to persistence DB and creates a new XML resource or updates an
	 * existing one.
	 */
	public void storeXMLResource(P4Collection p4collection, String resId, Node resource) throws PersistenceDBException {

		Collection collection = null;
		XMLResource xmlResource = null;
		try {
			collection = p4collection.getCollection();
			xmlResource = (XMLResource) collection.createResource(resId, "XMLResource");
			xmlResource.setContentAsDOM(resource);
			collection.storeResource(xmlResource);

			logger.debug("Stored XML resource to persistence DB (Collection: " + p4collection + " - Resource: " + xmlResource.getId() + ")");
		} catch (XMLDBException e) {
			e.printStackTrace();
			logger.error("Unable to store XML resource to persistence DB (Collection: " + p4collection + " - Resource: " + resId + ")");
			throw new PersistenceDBException();
		} finally {
			cleanUpResources(p4collection, xmlResource);
		}
	}

	/**
	 * Connects to persistence DB and reads an already present XML resource.
	 */
	public Node readXMLResource(P4Collection p4collection, String resId) throws PersistenceDBException {
		Node node = null;
		XMLResource resource = null;

		try {
			Collection collection = p4collection.getCollection();
			resource = (XMLResource) collection.getResource(resId);
			if (resource != null) {
				node = resource.getContentAsDOM();

				logger.debug("Retrieved XML resource from persistence DB (Collection: " + p4collection + " - Resource: " + resource.getId() + ")");

				return node;
			}
			throw new PersistenceDBException();
		} catch (XMLDBException e) {
			e.printStackTrace();
			logger.error("Unable to read XML resource from persistence DB (Collection: " + p4collection + " - Resource: " + resId + ")");
			throw new PersistenceDBException();
		} finally {
			cleanUpResources(p4collection, resource);
		}
	}

	/**
	 * Connects to persistence DB and deletes an already present XML resource.
	 */
	public void deleteXMLResource(P4Collection p4collection, String resId) throws PersistenceDBException {
		Collection collection = null;
		Resource res = null;

		try {
			collection = p4collection.getCollection();
			res = collection.getResource(resId);
			if (res != null) {
				collection.removeResource(res);

				logger.debug("Deleted XML resource from persistence DB (Collection: " + p4collection + " - Resource: " + resId + ")");
				return;
			}

			logger.debug("No XML resource to delete from persistence DB (Collection: " + p4collection + " - Resource: " + resId + ")");
		} catch (XMLDBException e) {
			e.printStackTrace();
			logger.error("Unable to delete XML resource from persistence DB (Collection: " + p4collection + " - Resource: " + resId + ")");
			throw new PersistenceDBException();
		} finally {
			cleanUpResources(p4collection, res);
		}
	}

	public List<String> executeQuery(String queryFile, Map<String, String> params, boolean recompile) throws PersistenceDBException {

		CompiledExpression expression = this.getCompiledExpression(queryFile);

		List<String> resultList = new ArrayList<>();

		try {
			for (String key : params.keySet()) {
				service.declareVariable(key, params.get(key));
			}

			ResourceSet res = service.execute(expression);
			ResourceIterator it = res.getIterator();

			while (it.hasMoreResources()) {
				Resource resource = it.nextResource();
				resultList.add(resource.getContent().toString());
				cleanUpResources(null, resource);
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			throw new PersistenceDBException();
		} finally {
			// TODO
		}

		return resultList;
	}
}
