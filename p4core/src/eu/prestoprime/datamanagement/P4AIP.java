/**
 * P4AIP.java
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

import eu.prestoprime.conf.ConfigurationManager;
import eu.prestoprime.conf.ConfigurationManager.P4Property;
import eu.prestoprime.datamanagement.PersistenceManager.P4Collection;
import eu.prestoprime.model.ModelUtils;
import eu.prestoprime.model.datatypes.Datatype;
import eu.prestoprime.model.mets.AmdSecType;
import eu.prestoprime.model.mets.MdSecType;
import eu.prestoprime.model.mets.MdSecType.MdRef;
import eu.prestoprime.model.mets.MdSecType.MdWrap;
import eu.prestoprime.model.mets.MdSecType.MdWrap.XmlData;
import eu.prestoprime.model.mets.Mets;
import eu.prestoprime.model.oais.AIP;
import eu.prestoprime.model.premis.Event;
import eu.prestoprime.model.premis.Event.EventIdentifier;
import eu.prestoprime.model.premis.Event.LinkingAgentIdentifier;
import eu.prestoprime.tools.MessageDigestExtractor;
import eu.prestoprime.tools.ToolException;

public class P4AIP extends P4RWInformationPackage implements AIP {

	public P4AIP(String id, Node content) {
		super(id, content);
	}

	@Override
	public void selfRelease() throws P4IPException {
		for (P4Resource resource : this.resources) {
			try {
				PersistenceManager.getInstance().storeXMLResource(resource.getCollection(), resource.getResourceID(), resource.getResourceNode());
			} catch (PersistenceDBException e) {
				e.printStackTrace();
				logger.error("Unable to store P4Resource " + resource.getResourceID() + " on persistence DB...");
				throw new P4IPException("Unable to store P4Resource " + resource.getResourceID() + " on persistence DB...");
			}
		}
		try {
			PersistenceManager.getInstance().storeXMLResource(P4Collection.AIP_COLLECTION, id, content);
		} catch (PersistenceDBException e) {
			e.printStackTrace();
			logger.error("Unable to store AIP " + id + " on persistence DB...");
			throw new P4IPException("Unable to store AIP " + id + " on persistence DB...");
		}
	}

	@Override
	public synchronized String updateSection(Node resultNode, String updateType) throws P4IPException {

		String resourceId = null;

		try {

			Datatype datatype = ConfigurationManager.getDataTypesInstance().getDatatype(updateType);
			String secType = datatype.getSection().getType();
			String mdLabel = datatype.getRef().getLabel();
			String multiple = datatype.getMultiple();

			logger.debug("Retrieved datatype: " + datatype.getName() + " " + secType + " " + mdLabel + " " + multiple);

			Mets mets = this.getContentAsMets();

			logger.debug("Unmarshalled METS: " + mets.getOBJID());

			P4Collection updateCollection = null;
			MdSecType updateMdSec = null;
			AmdSecType updateAmdSec = null;

			if (secType.equals("dmdSec")) {

				logger.debug("Updating dmdSec");

				if (multiple.equalsIgnoreCase("true")) {

					logger.debug("Multiple...");

					updateMdSec = new MdSecType();
					updateMdSec.setID(datatype.getSection().getId() + "-" + System.currentTimeMillis());
					mets.getDmdSec().add(updateMdSec);
					updateMdSec.setMdRef(new MdRef());
					updateCollection = P4Collection.DMD_COLLECTION;

				} else {

					logger.debug("Not multiple...");

					if (mets.getDmdSec().size() == 0) {

						logger.debug("Creating new dmdSec");

						updateMdSec = new MdSecType();
						updateMdSec.setID(datatype.getSection().getId() + "-" + System.currentTimeMillis());

						mets.getDmdSec().add(updateMdSec);
					}

					List<MdSecType> mdSecList = mets.getDmdSec();
					updateCollection = P4Collection.DMD_COLLECTION;

					for (MdSecType mdSecType : mdSecList) {
						if (mdSecType.getMdRef() != null && mdSecType.getMdRef().getMDTYPE() != null && mdSecType.getMdRef().getLABEL().equals(mdLabel)) {
							updateMdSec = mdSecType;
							updateMdSec.setID(datatype.getSection().getId() + "-" + System.currentTimeMillis());
							logger.debug("Found existing mdSectype: " + updateMdSec.getID());
							break;
						}
					}
				}

			} else {

				if (multiple.equalsIgnoreCase("true")) {

					logger.debug("Multiple...");

					updateAmdSec = new AmdSecType();
					updateAmdSec.setID("amd-" + System.currentTimeMillis());
					mets.getAmdSec().add(updateAmdSec);
					updateMdSec = new MdSecType();
					updateMdSec.setID(datatype.getSection().getId());
					updateMdSec.setMdRef(new MdRef());

					switch (secType) {

					case ("techMD"):
						updateAmdSec.getTechMD().add(updateMdSec);
						updateCollection = P4Collection.TECHMD_COLLECTION;
						break;

					case ("rightsMD"):
						updateAmdSec.getRightsMD().add(updateMdSec);
						updateCollection = P4Collection.RIGHTSMD_COLLECTION;
						break;

					case ("sourceMD"):
						updateAmdSec.getSourceMD().add(updateMdSec);
						updateCollection = P4Collection.SOURCEMD_COLLECTION;
						break;

					case ("digiprovMD"):
						updateAmdSec.getDigiprovMD().add(updateMdSec);
						updateCollection = P4Collection.DIGIPROVMD_COLLECTION;
						break;

					}

					logger.debug("Added new MdSec to new AmdSec");

				} else {

					logger.debug("Not multiple...");

					if (mets.getAmdSec().size() == 0) {
						updateAmdSec = new AmdSecType();
						updateAmdSec.setID("amd-" + System.currentTimeMillis());
						mets.getAmdSec().add(updateAmdSec);

						logger.debug("Created new AmdSec");
					}

					main: for (AmdSecType amdSecType : mets.getAmdSec()) {

						List<MdSecType> mdSecList = null;

						switch (secType) {

						case ("techMD"):
							mdSecList = amdSecType.getTechMD();
							updateCollection = P4Collection.TECHMD_COLLECTION;
							break;

						case ("rightsMD"):
							mdSecList = amdSecType.getRightsMD();
							updateCollection = P4Collection.RIGHTSMD_COLLECTION;
							break;

						case ("sourceMD"):
							mdSecList = amdSecType.getSourceMD();
							updateCollection = P4Collection.SOURCEMD_COLLECTION;
							break;

						case ("digiprovMD"):
							mdSecList = amdSecType.getDigiprovMD();
							updateCollection = P4Collection.DIGIPROVMD_COLLECTION;
							break;

						}

						logger.debug("Looping on mdSecList for type: " + secType);

						for (MdSecType mdSecType : mdSecList) {
							logger.debug("mdSecType " + mdSecType.getID());

							if (mdSecType.getMdRef() != null && mdSecType.getMdRef().getMDTYPE() != null && mdSecType.getMdRef().getLABEL() != null && mdSecType.getMdRef().getLABEL().equals(mdLabel)) {
								updateMdSec = mdSecType;
								updateAmdSec = amdSecType;
								updateMdSec.setID(datatype.getSection().getId());
								updateAmdSec.setID("amd-" + System.currentTimeMillis());
								logger.debug("Found updateMdSec: " + updateMdSec);
								break main;
							}
						}

					}

				}

			}

			if (updateMdSec == null || updateMdSec.getMdRef() == null) {

				logger.debug("MdSecType to be updated not found... creating a new one");

				updateMdSec = new MdSecType();
				updateMdSec.setMdRef(new MdRef());
				updateMdSec.setID(datatype.getSection().getId());

				if (secType.equals("dmdSec")) {

					updateMdSec.setID(datatype.getSection().getId() + "-" + System.currentTimeMillis());
					mets.getDmdSec().add(updateMdSec);

					logger.debug("Added new empty update sec in dmdSec");

				} else {

					updateAmdSec = new AmdSecType();
					updateAmdSec.setID("amd-" + System.currentTimeMillis());
					mets.getAmdSec().add(updateAmdSec);

					logger.debug("Added new empty update amd sec in mets");

					switch (secType) {
					case ("techMD"):
						updateAmdSec.getTechMD().add(updateMdSec);
						break;
					case ("rightsMD"):
						updateAmdSec.getRightsMD().add(updateMdSec);
						break;
					case ("sourceMD"):
						updateAmdSec.getSourceMD().add(updateMdSec);
						break;
					case ("digiprovMD"):
						updateAmdSec.getDigiprovMD().add(updateMdSec);
						break;
					}

					logger.debug("Added new empty update sec in amdSec");
				}
			}

			String status = datatype.getSection().getStatus();
			if (status != null && !status.isEmpty())
				updateMdSec.setSTATUS(status);

			updateMdSec.getMdRef().setLABEL(datatype.getRef().getLabel());
			updateMdSec.getMdRef().setMDTYPE(datatype.getRef().getMdtype());
			updateMdSec.getMdRef().setOTHERMDTYPE(datatype.getRef().getOthermdtype());
			updateMdSec.getMdRef().setCREATED(ModelUtils.Date2XMLGC(new Date()));

			logger.debug("Updated references in update mdSec...");

			UUID resourceUUID = UUID.randomUUID();
			resourceId = resourceUUID.toString();

			// FIXME dynamic URL
			String upPermURL = ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_PLACEHOLDER) + "/access/resource/" + updateCollection + "/" + resourceId;

			File tmpUpdateFile = File.createTempFile("update-", ".tmp");
			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(resultNode), new StreamResult(new FileOutputStream(tmpUpdateFile)));

			MessageDigestExtractor mde = new MessageDigestExtractor();
			mde.extract(tmpUpdateFile.getAbsolutePath());
			String md5 = mde.getAttributeByName("MD5");

			Long size = tmpUpdateFile.length();

			tmpUpdateFile.delete();

			updateMdSec.getMdRef().setHref(upPermURL);
			updateMdSec.getMdRef().setLOCTYPE("URL");
			updateMdSec.getMdRef().setMIMETYPE("application/xml");
			updateMdSec.getMdRef().setSIZE(size);
			updateMdSec.getMdRef().setCHECKSUM(md5);
			updateMdSec.getMdRef().setCHECKSUMTYPE("MD5");

			logger.debug("Updated resource references in update mdSec...");

			this.setContent(mets);

			this.resources.add(new P4Resource(updateCollection, resourceId.toString(), resultNode));

			logger.debug("Updated AIP " + id + " for datatype " + datatype.getName());

			return resourceId.toString();

		} catch (ToolException e) {
			logger.error("Error in computing checksum...");
			throw new P4IPException("Error in computing checksum...");
		} catch (DatatypeConfigurationException e) {
			logger.error("Error in datatypeConfiguration...");
			throw new P4IPException("Error in datatypeConfiguration...");
		} catch (IOException e) {
			logger.error("Error in IO...");
			throw new P4IPException("Error in IO...");
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
		return updateType;
	}

	@Override
	public synchronized void addPreservationEvent(String type, String info) throws P4IPException {

		try {

			Event event = new Event();
			event.setEventType(type);
			event.setEventDetail(info);
			event.setEventDateTime(ModelUtils.Date2XMLGC(new Date()).toString());

			EventIdentifier eventIdentifier = new EventIdentifier();
			eventIdentifier.setEventIdentifierType("UUID");
			eventIdentifier.setEventIdentifierValue(UUID.randomUUID().toString());
			event.setEventIdentifier(eventIdentifier);

			LinkingAgentIdentifier agentIdentifier = new LinkingAgentIdentifier();
			agentIdentifier.setLinkingAgentIdentifierType("Preservation System");
			agentIdentifier.setLinkingAgentIdentifierValue("P4");

			Mets mets = this.getContentAsMets();

			List<AmdSecType> amdSecList = mets.getAmdSec();

			AmdSecType premisSec = null;

			for (AmdSecType amdSecType : amdSecList) {

				if (amdSecType.getID() != null && amdSecType.getID().equalsIgnoreCase("AMD-PREMIS")) {
					premisSec = amdSecType;
					break;
				}
			}

			if (premisSec == null) {
				premisSec = new AmdSecType();
				premisSec.setID("AMD-PREMIS");
				mets.getAmdSec().add(premisSec);
			}

			List<MdSecType> digiprovSecList = premisSec.getDigiprovMD();

			String digiprovSecID = "PREMIS-" + type.toUpperCase();
			MdSecType digiprovSec = null;
			for (MdSecType mdSec : digiprovSecList) {
				if (mdSec.getID().equalsIgnoreCase(digiprovSecID)) {
					digiprovSec = mdSec;
					break;
				}
			}

			if (digiprovSec == null) {
				digiprovSec = new MdSecType();
				digiprovSec.setID(digiprovSecID);

				digiprovSecList.add(digiprovSec);
			}

			digiprovSec.setCREATED(ModelUtils.Date2XMLGC(new Date()));

			MdWrap mdWrap = new MdWrap();
			mdWrap.setMDTYPE("PREMIS");
			digiprovSec.setMdWrap(mdWrap);

			XmlData xmlData = new XmlData();
			mdWrap.setXmlData(xmlData);

			xmlData.getAny().add(event);

			logger.debug("Updated digital provenance MD for event " + type);

			this.setContent(mets);

		} catch (DatatypeConfigurationException e) {
			logger.error("Error in setting digiprovMD CREATED time");
			throw new P4IPException("Error in setting digiprovMD CREATED time");
		}
	}
}
