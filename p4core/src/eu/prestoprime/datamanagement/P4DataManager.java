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

import it.eurix.archtools.data.DataException;
import it.eurix.archtools.data.DataManager;
import it.eurix.archtools.data.model.AIP;
import it.eurix.archtools.data.model.DIP;
import it.eurix.archtools.data.model.DIP.DCField;
import it.eurix.archtools.data.model.IPException;
import it.eurix.archtools.data.model.InformationPackage;
import it.eurix.archtools.data.model.SIP;
import it.eurix.archtools.persistence.DatabaseException;
import it.eurix.archtools.user.UserManager.UserRole;
import it.eurix.archtools.workflow.jaxb.StatusType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.prestoprime.conf.ConfigurationManager;
import eu.prestoprime.datamanagement.P4PersistenceManager.P4Collection;
import eu.prestoprime.datamanagement.impl.P4AIP;
import eu.prestoprime.datamanagement.impl.P4DIP;
import eu.prestoprime.datamanagement.impl.P4InformationPackage;
import eu.prestoprime.datamanagement.impl.P4SIP;
import eu.prestoprime.model.ModelUtils;
import eu.prestoprime.model.ModelUtils.P4JAXBPackage;
import eu.prestoprime.model.datatypes.Datatype;
import eu.prestoprime.model.mets.MetsType;
import eu.prestoprime.model.terms.Service;
import eu.prestoprime.model.terms.Service.Terms;

public class P4DataManager extends DataManager<P4PersistenceManager.P4Collection> {

	private static enum P4Query {
		IP_BY_ID("xmldb/queryAIPByID.xq"),
		DC_BY_ID("xmldb/queryDC.xq"),
		IP_BY_MD("xmldb/queryAIPByMD.xq"),
		IP_BY_DCID("xmldb/queryIPByDCID.xq"),
		WFSTATUS("xmldb/queryWfStatus.xq"),
		IP_BY_OAIPMH("xmldb/queryAIPByOAIPMH.xq"),
		AIP_BY_FORMAT_RISK("xmldb/queryAIPByFormatRisk.xq");

		private String queryFile;

		private P4Query(String queryFile) {
			this.queryFile = queryFile;
		}

		public URL getQueryFile() {
			return Thread.currentThread().getContextClassLoader().getResource(queryFile);
		}
		
		@Override
		public String toString() {
			return queryFile;
		}
	};

	private static P4DataManager instance;

	private P4DataManager() {
		super(P4PersistenceManager.getInstance());
	}
	
	public static P4DataManager getInstance() {
		if (instance == null)
			instance = new P4DataManager();
		return instance;
	}

	@Override
	public synchronized String createNewSIP(File file) throws DataException {
		// get resource id
		UUID uuid = super.generateUniqueId();

		try {
			// get mets
			Unmarshaller unmarshaller = ModelUtils.getUnmarshaller(P4JAXBPackage.DATA_MODEL);
			MetsType mets = (MetsType) unmarshaller.unmarshal(file);

			// set id
			mets.setOBJID(uuid.toString());

			// get node
			Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			ModelUtils.getMarshaller(P4JAXBPackage.DATA_MODEL).marshal(mets, node);
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

	@Override
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
//			params.put("COLLECTION", P4Collection.TEMP_COLLECTION.getCollectionName());

			try {
				List<String> res = persistenceManager.executeQuery(P4Query.IP_BY_ID.getQueryFile(), P4Collection.TEMP_COLLECTION, params, false);

				if (res.size() == 1) {
					String result = res.get(0);

					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(true);

					P4SIP p4SIP = new P4SIP(id, dbf.newDocumentBuilder().parse(new ByteArrayInputStream(result.getBytes())));
					this.registerRunningIP(key, p4SIP);

					return p4SIP;
				}
			} catch (DatabaseException e) {
				e.printStackTrace();
			} catch (SAXException | IOException | ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		throw new DataException("Unable to find an SIP with id " + id);
	}

	@Override
	public synchronized void consolidateSIP(SIP sip) throws DataException {
		// check if already consolidated
		Map<String, String> params = new HashMap<>();
		params.put("AIPID", sip.getId());

		try {
			List<String> res = persistenceManager.executeQuery(P4Query.IP_BY_ID.getQueryFile(), P4Collection.AIP_COLLECTION, params, false);

			if (res.size() != 0)
				throw new DataException("SIP already consolidated...");
		} catch (DatabaseException e) {
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
				P4PersistenceManager.getInstance().storeXMLResource(P4Collection.AIP_COLLECTION, sip.getId(), p4IP.getContent());
			} catch (DatabaseException e) {
				e.printStackTrace();
				throw new DataException("Unable to store the new AIP...");
			}

			// finally clean up
			try {
				P4PersistenceManager.getInstance().deleteXMLResource(P4Collection.TEMP_COLLECTION, sip.getId());
			} catch (DatabaseException e) {
				e.printStackTrace();
				throw new DataException("Unable to remove consolidated SIP " + sip.getId() + "...");
			}
		} else {
			throw new DataException("Invalid consolidating SIP...");
		}

	}

	@Override
	public synchronized void deleteSIP(String id) throws DataException {
		try {
			P4PersistenceManager.getInstance().deleteXMLResource(P4Collection.TEMP_COLLECTION, id);
		} catch (DatabaseException e) {
			e.printStackTrace();
			logger.error("Unable to delete from TEMP_COLLECTION SIP " + id + "...");
			throw new DataException("Unable to delete from TEMP_COLLECTION SIP " + id + "...");
		}
	}

	public synchronized void invalidateAIP(String id) throws DataException {
		try {
			Node aip = P4PersistenceManager.getInstance().readXMLResource(P4Collection.AIP_COLLECTION, id);
			P4PersistenceManager.getInstance().deleteXMLResource(P4Collection.AIP_COLLECTION, id);
			P4PersistenceManager.getInstance().storeXMLResource(P4Collection.TEMP_COLLECTION, id, aip);
		} catch (DatabaseException e) {
			e.printStackTrace();
			throw new DataException("Unable to invalidate AIP " + id + "...");
		}
	}

	@Override
	public List<String> getAllAIP(Map<String, String> elements) throws DataException {

		Map<String, String> params = new HashMap<>();

		if (elements == null)
			elements = new HashMap<>();

		params.put("qtitle", elements.get("title") != null ? elements.get("title") : "");
		params.put("qdescription", elements.get("description") != null ? elements.get("description") : "");
		params.put("qformat", elements.get("format") != null ? elements.get("format") : "");
		params.put("qidentifier", elements.get("identifier") != null ? elements.get("identifier") : "");

		try {
			List<String> res = persistenceManager.executeQuery(P4Query.DC_BY_ID.getQueryFile(), P4Collection.AIP_COLLECTION, params, false);

			return res;
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		throw new DataException("Unable to execute query on DC fields...");
	}

	@Override
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

			try {
				List<String> res = persistenceManager.executeQuery(P4Query.IP_BY_ID.getQueryFile(), P4Collection.AIP_COLLECTION, params, false);

				if (res.size() == 1) {
					String result = res.get(0);

					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(true);

					P4AIP p4AIP = new P4AIP(id, dbf.newDocumentBuilder().parse(new ByteArrayInputStream(result.getBytes())));
					this.registerRunningIP(key, p4AIP);

					return p4AIP;
				}
			} catch (DatabaseException e) {
				e.printStackTrace();
			} catch (SAXException | IOException | ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		throw new DataException("Unable to find an AIP with id " + id);
	}
	
	/**
	 * Returns a list of AIP containing or not a specific metadata information
	 */
	@Override
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
			params.put("SEC_TYPE", datatype.getSection().getType());
			params.put("MD_LABEL", datatype.getRef().getLabel());
			params.put("STATUS", status);

			aipIdSelectedList = persistenceManager.executeQuery(P4Query.IP_BY_MD.getQueryFile(), P4Collection.AIP_COLLECTION, params, true);
		} catch (DatabaseException e) {
			throw new DataException("Unable to execute query on datatype fields...");
		}

		if (isAvailable)
			return aipIdSelectedList;
		else {
			aipIdList.removeAll(aipIdSelectedList);
			return aipIdList;
		}
	}

	//TODO: add @Override and add method in abstract parent class
	//FIXME: parameterize methods and puth them into parent class
	public List<String> getAIPByOAIPMH(String fromDate, String untilDate, String spec) throws DataException {
		Map<String, String> params = new HashMap<>();
		params.put("FROM_DATE", fromDate);
		params.put("UNTIL_DATE", untilDate);
		params.put("SET", spec);

		try {
			List<String> aipIdList = persistenceManager.executeQuery(P4Query.IP_BY_OAIPMH.getQueryFile(), P4Collection.AIP_COLLECTION, params, true);
			return aipIdList;
		} catch (DatabaseException e) {
			throw new DataException("Unable to execute OAI-PMH query...");
		}
	}

	public List<String> getAIPByFormatRisk() throws DataException {
		Map<String, String> params = new HashMap<>();

		try {
			List<String> aipIdList = persistenceManager.executeQuery(P4Query.AIP_BY_FORMAT_RISK.getQueryFile(), P4Collection.AIP_COLLECTION, params, true);
			return aipIdList;
		} catch (DatabaseException e) {
			throw new DataException("Unable to execute FORMAT-RISK query...");
		}
	}

	public String getAIPByDCID(String dcID) throws DataException {

		Map<String, String> params = new HashMap<>();
		params.put("IDENTIFIER", dcID);

		try {
			List<String> res = persistenceManager.executeQuery(P4Query.IP_BY_DCID.getQueryFile(), P4Collection.AIP_COLLECTION, params, false);

			if (res != null) {

				assert (res.size() <= 1) : "Something bad is going on... found more than one IP with same DC identifier ?!?";

				if (res.size() > 0) {
					return res.get(0);
				} else {
					return null;
				}
			}

		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		throw new DataException("Unable to execute query on DC fields...");

	}

	public List<String> getSIPByDCID(String dcID) throws DataException {

		Map<String, String> params = new HashMap<>();
		params.put("IDENTIFIER", dcID);

		try {
			List<String> res = persistenceManager.executeQuery(P4Query.IP_BY_DCID.getQueryFile(), P4Collection.TEMP_COLLECTION, params, false);

			if (res != null) {
				assert (res.size() <= 1) : "Something bad is going on... found more than one IP with same DC identifier ?!?";

				return res;
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		throw new DataException("Unable to execute query on DC fields...");

	}

	public DIP getDIPByID(String id) throws DataException {
		Map<String, String> params = new HashMap<>();
		params.put("AIPID", id);

		try {
			List<String> res = persistenceManager.executeQuery(P4Query.IP_BY_ID.getQueryFile(), P4Collection.AIP_COLLECTION, params, false);

			if (res.size() == 1) {
				String result = res.get(0);

				// TODO prepare for dissemination
				// i.e. update URLs

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);

				P4DIP p4DIP = new P4DIP(id, dbf.newDocumentBuilder().parse(new ByteArrayInputStream(result.getBytes())));

				return p4DIP;
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		throw new DataException("Unable to find an DIP with id " + id);
	}

	@Override
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

		try {
			List<String> res = persistenceManager.executeQuery(P4Query.WFSTATUS.getQueryFile(), P4Collection.WF_COLLECTION, params, false);

			return res;
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		throw new DataException("Unable to find wfStatus with status " + status + "...");
	}

	public String getTermsOfUse(UserRole role) throws DataException {

		try {
			Node serviceNode = persistenceManager.readXMLResource(P4Collection.ADMIN_COLLECTION, "terms.xml");

			Unmarshaller unmarshaller = ModelUtils.getUnmarshaller(P4JAXBPackage.CONF);
			Service p4Service = (Service) unmarshaller.unmarshal(serviceNode);

			for (Terms terms : p4Service.getTerms())
				if (terms.getRole().equalsIgnoreCase(role.toString()))
					return terms.getValue();

		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		throw new DataException("Unable to retrieve terms for role " + role);
	}
}
