/**
 * DataManager.java
 * Author: Francesco Rosso (rosso@eurix.it)
 * Contributors: Francesco Gallo (gallo@eurix.it)
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.prestoprime.conf.ConfigurationManager;
import eu.prestoprime.conf.ConfigurationManager.P4Role;
import eu.prestoprime.datamanagement.PersistenceManager.P4Collection;
import eu.prestoprime.model.ModelUtils;
import eu.prestoprime.model.ModelUtils.P4Namespace;
import eu.prestoprime.model.datatypes.Datatype;
import eu.prestoprime.model.mets.MetsType;
import eu.prestoprime.model.oais.AIP;
import eu.prestoprime.model.oais.DIP;
import eu.prestoprime.model.oais.DIP.DCField;
import eu.prestoprime.model.oais.IPException;
import eu.prestoprime.model.oais.InformationPackage;
import eu.prestoprime.model.oais.SIP;
import eu.prestoprime.model.terms.Service;
import eu.prestoprime.model.terms.Service.Terms;
import eu.prestoprime.model.workflow.StatusType;

public class DataManager {

	private static final Logger logger = LoggerFactory.getLogger(DataManager.class);
	private static DataManager instance = new DataManager();

	private PersistenceManager persistenceManager;
	private Map<String, P4InformationPackage> runningIPMap;

	private static enum P4Query {
		IP_BY_ID("xmldb/queryAIPByID.xq"), DC_BY_ID("xmldb/queryDC.xq"), IP_BY_MD("xmldb/queryAIPByMD.xq"), IP_BY_DCID("xmldb/queryIPByDCID.xq"), WFSTATUS("xmldb/queryWfStatus.xq"), IP_BY_OAIPMH("xmldb/queryAIPByOAIPMH.xq"), AIP_BY_FORMAT_RISK("xmldb/queryAIPByFormatRisk.xq");

		private String queryFile;

		private P4Query(String queryFile) {
			this.queryFile = queryFile;
		}

		public String getQueryFile() {
			return queryFile;
		}
	};

	public static DataManager getInstance() {
		return instance;
	}

	private DataManager() {
		persistenceManager = PersistenceManager.getInstance();
		runningIPMap = new HashMap<>();
	}

	private InformationPackage getRunningIP(String key) {
		P4InformationPackage p4IP = runningIPMap.get(key);
		if (p4IP != null) {
			p4IP.incrementCounter();

			logger.debug("Incremented counter for IP " + key + ": total " + p4IP.getCounter());

			return p4IP;
		} else {
			return null;
		}
	}

	private void registerRunningIP(String key, P4InformationPackage p4IP) {
		p4IP.incrementCounter();
		runningIPMap.put(key, p4IP);

		logger.debug("Running IPs: " + runningIPMap.keySet());
		logger.debug("Incremented counter for IP " + p4IP.getId() + ": total " + p4IP.getCounter());
	}

	public void releaseIP(InformationPackage ip) {
		if (ip != null && ip instanceof P4InformationPackage) {
			P4InformationPackage p4IP = (P4InformationPackage) ip;
			p4IP.decrementCounter();

			logger.debug("Running IPs: " + runningIPMap.keySet());
			logger.debug("Decremented counter for IP " + ip.getId() + ": total " + p4IP.getCounter());

			if (p4IP.getCounter() <= 0) {
				try {
					p4IP.selfRelease();
				} catch (P4IPException e) {
					e.printStackTrace();
					throw new RuntimeException("Unable to self release the IP...");
				}
				runningIPMap.remove(p4IP.getId());

				logger.debug("Released IP " + p4IP.getId());
			}
		} else {
			logger.error("Trying releasing an InformationPackage unreleasable...");
			throw new RuntimeException("Invalid releasing IP...");
		}
	}

	public synchronized String createNewSIP(File file) throws DataException {
		// get resource id
		UUID uuid = generateUniqueId();

		try {
			// get mets
			Unmarshaller unmarshaller = ModelUtils.getUnmarshaller(P4Namespace.DATA_MODEL.getValue());
			MetsType mets = (MetsType) unmarshaller.unmarshal(file);

			// set id
			mets.setOBJID(uuid.toString());

			// get node
			Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			ModelUtils.getMarshaller(P4Namespace.DATA_MODEL.getValue()).marshal(mets, node);
			P4SIP p4SIP = new P4SIP(uuid.toString(), node);

			// write to persistence DB
			p4SIP.selfRelease();

			// check DC identifier uniqueness
			SIP sip = this.getSIPByID(uuid.toString());
			List<String> currentDCList = sip.getDCField(DCField.identifier);
			for (String identifier : currentDCList) {
				List<String> otherDCList = this.getSIPByDCID(identifier);
				for (String tmpID : otherDCList) {
					if (!tmpID.equals(uuid.toString())) {
						// delete new SIP
						this.deleteSIP(uuid.toString());

						logger.error("Found another SIP with same DC identifier...");
						throw new DataException("Found another SIP with same DC identifier...");
					}
				}
			}
			this.releaseIP(sip);

			return uuid.toString();
		} catch (JAXBException e) {
			logger.error("Unable to parse SIP file...");
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IPException e) {
			logger.error("Unable to write for first time the new SIP...");
			e.printStackTrace();
		}
		throw new DataException("Unable to create new SIP...");
	}

	private UUID generateUniqueId() {
		UUID uuid;
		boolean idIsUnique;

		do {
			uuid = UUID.randomUUID();
			idIsUnique = false;

			logger.debug("Check ID uniqueness for ID = " + uuid.toString());

			InformationPackage p4Ip = this.getRunningIP(uuid.toString());
			if (p4Ip == null) { // no conflicting running IP
				idIsUnique = true;
			}

			if (idIsUnique) {
				logger.debug("No conflicting running SIP.");
				try {
					// we should get a DataException if there is no AIP with
					// this ID in the database
					this.getAIPByID(uuid.toString());
					idIsUnique = false;
					logger.debug("Found a conflicting AIP on the database. Generating another ID...");
				} catch (DataException e) {
					// in this case the Exception is good
					logger.debug("No conflicting AIP with this ID in DB. Proceed...");
				}
			}
		} while (!idIsUnique);

		return uuid;
	}

	public synchronized SIP getSIPByID(String id) throws DataException {
		String key = id;
		InformationPackage IP = this.getRunningIP(key);
		if (IP != null)
			if (IP instanceof SIP)
				return (SIP) IP;
			else
				throw new DataException("Error with keys: this key corresponds to a running InformationPackage, but it isn't an SIP...");
		else {
			logger.debug("SIP not cached, quering persistence DB for new instance...");

			Map<String, String> params = new HashMap<>();
			params.put("AIPID", id);
			params.put("COLLECTION", P4Collection.TEMP_COLLECTION.getCollectionName());

			try {
				List<String> res = persistenceManager.executeQuery(P4Query.IP_BY_ID.getQueryFile(), params, false);

				if (res.size() == 1) {
					String result = res.get(0);

					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(true);

					P4SIP p4SIP = new P4SIP(id, dbf.newDocumentBuilder().parse(new ByteArrayInputStream(result.getBytes())));
					this.registerRunningIP(key, p4SIP);

					return p4SIP;
				}
			} catch (PersistenceDBException e) {
				e.printStackTrace();
			} catch (SAXException | IOException | ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		throw new DataException("Unable to find an SIP with id " + id);
	}

	public synchronized void consolidateSIP(SIP sip) throws DataException {
		// check if already consolidated
		Map<String, String> params = new HashMap<>();
		params.put("AIPID", sip.getId());
		params.put("COLLECTION", P4Collection.AIP_COLLECTION.getCollectionName());

		try {
			List<String> res = persistenceManager.executeQuery(P4Query.IP_BY_ID.getQueryFile(), params, false);

			if (res.size() != 0)
				throw new DataException("SIP already consolidated...");
		} catch (PersistenceDBException e) {
			e.printStackTrace();
		}

		if (sip instanceof P4InformationPackage) {
			// if not consolidated, release
			P4InformationPackage p4IP = (P4InformationPackage) sip;
			while (p4IP.getCounter() != 0)
				this.releaseIP(sip);

			// assign or update CREATEDATE
			GregorianCalendar gCal = new GregorianCalendar();
			gCal.setTime(new Date());
			try {
				sip.setCreateDate(gCal);
			} catch (IPException e) {
				throw new DataException("Unable to assign create date to SIP");
			}

			// then consolidate
			try {
				PersistenceManager.getInstance().storeXMLResource(P4Collection.AIP_COLLECTION, sip.getId(), p4IP.getContent());
			} catch (PersistenceDBException e) {
				e.printStackTrace();
				throw new DataException("Unable to store the new AIP...");
			}

			// finally clean up
			try {
				PersistenceManager.getInstance().deleteXMLResource(P4Collection.TEMP_COLLECTION, sip.getId());
			} catch (PersistenceDBException e) {
				e.printStackTrace();
				throw new DataException("Unable to remove consolidated SIP " + sip.getId() + "...");
			}
		} else {
			throw new DataException("Invalid consolidating SIP...");
		}

	}

	public synchronized void deleteSIP(String id) throws DataException {
		try {
			PersistenceManager.getInstance().deleteXMLResource(P4Collection.TEMP_COLLECTION, id);
		} catch (PersistenceDBException e) {
			e.printStackTrace();
			logger.error("Unable to delete from TEMP_COLLECTION SIP " + id + "...");
			throw new DataException("Unable to delete from TEMP_COLLECTION SIP " + id + "...");
		}
	}

	public synchronized void invalidateAIP(String id) throws DataException {
		try {
			Node aip = PersistenceManager.getInstance().readXMLResource(P4Collection.AIP_COLLECTION, id);
			PersistenceManager.getInstance().deleteXMLResource(P4Collection.AIP_COLLECTION, id);
			PersistenceManager.getInstance().storeXMLResource(P4Collection.TEMP_COLLECTION, id, aip);
		} catch (PersistenceDBException e) {
			e.printStackTrace();
			throw new DataException("Unable to invalidate AIP " + id + "...");
		}
	}

	public List<String> getAllAIP(Map<String, String> elements) throws DataException {

		Map<String, String> params = new HashMap<>();

		if (elements == null)
			elements = new HashMap<>();

		params.put("qtitle", elements.get("title") != null ? elements.get("title") : "");
		params.put("qdescription", elements.get("description") != null ? elements.get("description") : "");
		params.put("qformat", elements.get("format") != null ? elements.get("format") : "");
		params.put("qidentifier", elements.get("identifier") != null ? elements.get("identifier") : "");

		params.put("COLLECTION", P4Collection.AIP_COLLECTION.getCollectionName());

		try {
			List<String> res = persistenceManager.executeQuery(P4Query.DC_BY_ID.getQueryFile(), params, false);

			return res;
		} catch (PersistenceDBException e) {
			e.printStackTrace();
		}
		throw new DataException("Unable to execute query on DC fields...");
	}

	/**
	 * Returns a list of AIP containing or not a specific metadata information
	 */
	public List<String> getAIPByMD(String label, boolean isAvailable) throws DataException {

		Datatype datatype = ConfigurationManager.getDataTypesInstance().getDatatype(label);

		String status = datatype.getSection().getStatus();

		if (status == null || status.isEmpty())
			status = "";

		List<String> aipIdList = this.getAllAIP(null);

		List<String> aipIdSelectedList = new ArrayList<>();

		Map<String, String> params = null;

		try {
			params = new HashMap<>();
			params.put("COLLECTION", P4Collection.AIP_COLLECTION.getCollectionName());
			params.put("SEC_TYPE", datatype.getSection().getType());
			params.put("MD_LABEL", datatype.getRef().getLabel());
			params.put("STATUS", status);

			aipIdSelectedList = persistenceManager.executeQuery(P4Query.IP_BY_MD.getQueryFile(), params, true);
		} catch (PersistenceDBException e) {
			throw new DataException("Unable to execute query on datatype fields...");
		}

		if (isAvailable)
			return aipIdSelectedList;
		else {
			aipIdList.removeAll(aipIdSelectedList);
			return aipIdList;
		}
	}

	public List<String> getAIPByOAIPMH(String fromDate, String untilDate, String spec) throws DataException {
		Map<String, String> params = new HashMap<>();
		params.put("COLLECTION", P4Collection.AIP_COLLECTION.getCollectionName());
		params.put("FROM_DATE", fromDate);
		params.put("UNTIL_DATE", untilDate);
		params.put("SET", spec);

		try {
			List<String> aipIdList = persistenceManager.executeQuery(P4Query.IP_BY_OAIPMH.getQueryFile(), params, true);
			return aipIdList;
		} catch (PersistenceDBException e) {
			throw new DataException("Unable to execute OAI-PMH query...");
		}
	}

	public List<String> getAIPByFormatRisk() throws DataException {
		Map<String, String> params = new HashMap<>();
		params.put("COLLECTION", P4Collection.AIP_COLLECTION.getCollectionName());

		try {
			List<String> aipIdList = persistenceManager.executeQuery(P4Query.AIP_BY_FORMAT_RISK.getQueryFile(), params, true);
			return aipIdList;
		} catch (PersistenceDBException e) {
			throw new DataException("Unable to execute FORMAT-RISK query...");
		}
	}

	public synchronized AIP getAIPByID(String id) throws DataException {
		String key = id;
		InformationPackage IP = this.getRunningIP(key);
		if (IP != null)
			if (IP instanceof AIP)
				return (AIP) IP;
			else
				throw new DataException("Error with keys: this key corresponds to a running InformationPackage, but it isn't an AIP...");
		else {
			logger.debug("AIP not cached, quering persistence DB for new instance...");

			Map<String, String> params = new HashMap<>();
			params.put("AIPID", id);
			params.put("COLLECTION", P4Collection.AIP_COLLECTION.getCollectionName());

			try {
				List<String> res = persistenceManager.executeQuery(P4Query.IP_BY_ID.getQueryFile(), params, false);

				if (res.size() == 1) {
					String result = res.get(0);

					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(true);

					P4AIP p4AIP = new P4AIP(id, dbf.newDocumentBuilder().parse(new ByteArrayInputStream(result.getBytes())));
					this.registerRunningIP(key, p4AIP);

					return p4AIP;
				}
			} catch (PersistenceDBException e) {
				e.printStackTrace();
			} catch (SAXException | IOException | ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		throw new DataException("Unable to find an AIP with id " + id);
	}

	public String getAIPByDCID(String dcID) throws DataException {

		Map<String, String> params = new HashMap<>();
		params.put("IDENTIFIER", dcID);
		params.put("COLLECTION", P4Collection.AIP_COLLECTION.getCollectionName());

		try {
			List<String> res = persistenceManager.executeQuery(P4Query.IP_BY_DCID.getQueryFile(), params, false);

			if (res != null) {

				assert (res.size() <= 1) : "Something bad is going on... found more than one IP with same DC identifier ?!?";

				if (res.size() > 0) {
					return res.get(0);
				} else {
					return null;
				}
			}

		} catch (PersistenceDBException e) {
			e.printStackTrace();
		}
		throw new DataException("Unable to execute query on DC fields...");

	}

	public List<String> getSIPByDCID(String dcID) throws DataException {

		Map<String, String> params = new HashMap<>();
		params.put("IDENTIFIER", dcID);
		params.put("COLLECTION", P4Collection.TEMP_COLLECTION.getCollectionName());

		try {
			List<String> res = persistenceManager.executeQuery(P4Query.IP_BY_DCID.getQueryFile(), params, false);

			if (res != null) {
				assert (res.size() <= 1) : "Something bad is going on... found more than one IP with same DC identifier ?!?";

				return res;
			}
		} catch (PersistenceDBException e) {
			e.printStackTrace();
		}
		throw new DataException("Unable to execute query on DC fields...");

	}

	public DIP getDIPByID(String id) throws DataException {
		Map<String, String> params = new HashMap<>();
		params.put("AIPID", id);
		params.put("COLLECTION", P4Collection.AIP_COLLECTION.getCollectionName());

		try {
			List<String> res = persistenceManager.executeQuery(P4Query.IP_BY_ID.getQueryFile(), params, false);

			if (res.size() == 1) {
				String result = res.get(0);

				// TODO prepare for dissemination
				// i.e. update URLs

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);

				P4DIP p4DIP = new P4DIP(id, dbf.newDocumentBuilder().parse(new ByteArrayInputStream(result.getBytes())));

				return p4DIP;
			}
		} catch (PersistenceDBException e) {
			e.printStackTrace();
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		throw new DataException("Unable to find an DIP with id " + id);
	}

	public List<String> getWfStatus(String userID, StatusType status) throws DataException {
		Map<String, String> params = new HashMap<>();
		if (userID == null)
			params.put("userID", "^");
		else
			params.put("userID", userID);
		if (status == null)
			params.put("qstatus", "");
		else
			params.put("qstatus", status.toString());
		params.put("COLLECTION", P4Collection.WF_COLLECTION.getCollectionName());

		try {
			List<String> res = persistenceManager.executeQuery(P4Query.WFSTATUS.getQueryFile(), params, false);

			return res;
		} catch (PersistenceDBException e) {
			e.printStackTrace();
		}
		throw new DataException("Unable to find wfStatus with status " + status + "...");
	}

	public Node getResource(P4Collection collection, String resId) throws DataException {
		try {
			return PersistenceManager.getInstance().readXMLResource(collection, resId);
		} catch (PersistenceDBException e) {
			throw new DataException("Unable to find resource " + resId + " in collection " + collection + "...");
		}
	}

	public String getTermsOfUse(P4Role role) throws DataException {

		try {
			Node serviceNode = persistenceManager.readXMLResource(P4Collection.ADMIN_COLLECTION, "terms.xml");

			Unmarshaller unmarshaller = ModelUtils.getUnmarshaller(P4Namespace.CONF.getValue());
			Service p4Service = (Service) unmarshaller.unmarshal(serviceNode);

			for (Terms terms : p4Service.getTerms())
				if (terms.getRole().equalsIgnoreCase(role.toString()))
					return terms.getValue();

		} catch (PersistenceDBException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		throw new DataException("Unable to retrieve terms for role " + role);
	}
}
