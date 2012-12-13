/**
 * P4SIP.java
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
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import eu.prestoprime.conf.ConfigurationManager;
import eu.prestoprime.conf.ConfigurationManager.P4Property;
import eu.prestoprime.datamanagement.PersistenceManager.P4Collection;
import eu.prestoprime.model.ModelUtils;
import eu.prestoprime.model.ModelUtils.P4Namespace;
import eu.prestoprime.model.datatypes.Datatype;
import eu.prestoprime.model.dnx.Dnx;
import eu.prestoprime.model.mets.AmdSecType;
import eu.prestoprime.model.mets.FileType;
import eu.prestoprime.model.mets.FileType.FLocat;
import eu.prestoprime.model.mets.MdSecType;
import eu.prestoprime.model.mets.MdSecType.MdRef;
import eu.prestoprime.model.mets.MdSecType.MdWrap;
import eu.prestoprime.model.mets.MdSecType.MdWrap.XmlData;
import eu.prestoprime.model.mets.Mets;
import eu.prestoprime.model.mets.MetsType.FileSec.FileGrp;
import eu.prestoprime.model.mets.MetsType.MetsHdr;
import eu.prestoprime.model.oais.DIP.DCField;
import eu.prestoprime.model.oais.SIP;
import eu.prestoprime.tools.MessageDigestExtractor;
import eu.prestoprime.tools.ToolException;

public class P4SIP extends P4RWInformationPackage implements SIP {

	private static final Logger logger = LoggerFactory.getLogger(P4SIP.class);

	public P4SIP(String id, Node content) {
		super(id, content);
	}

	@Override
	public synchronized void selfRelease() throws P4IPException {
		// store SIP
		try {
			PersistenceManager.getInstance().storeXMLResource(P4Collection.TEMP_COLLECTION, id, content);
		} catch (PersistenceDBException e) {
			e.printStackTrace();
			logger.error("Unable to store SIP " + id + " on persistence DB...");
			throw new P4IPException("Unable to store SIP " + id + " on persistence DB...");
		}
		// store related resources
		for (P4Resource resource : this.resources) {
			try {
				PersistenceManager.getInstance().storeXMLResource(resource.getCollection(), resource.getResourceID(), resource.getResourceNode());
			} catch (PersistenceDBException e) {
				e.printStackTrace();
				logger.error("Unable to store P4Resource " + resource.getResourceID() + " on persistence DB...");
			}
		}
	}

	@Override
	public synchronized String setRights(Node rights) throws P4IPException {

		if (rights != null) {

			// get rights datatype information
			Datatype rightsDT = ConfigurationManager.getDataTypesInstance().getDatatype("rights");

			// get rights file size and checksum
			String md5 = null;
			Long size = null;

			try {

				File tmpUpdateFile = File.createTempFile("rights-", ".tmp");
				TransformerFactory.newInstance().newTransformer().transform(new DOMSource(rights), new StreamResult(new FileOutputStream(tmpUpdateFile)));
				MessageDigestExtractor mde = new MessageDigestExtractor();
				mde.extract(tmpUpdateFile.getAbsolutePath());
				md5 = mde.getAttributeByName("MD5");
				size = tmpUpdateFile.length();
				tmpUpdateFile.delete();

			} catch (IOException | ToolException | TransformerException e) {
				e.printStackTrace();
				throw new P4IPException("Unable to calculate size or checksum for rights file...");
			}

			// add rights node to resources
			String rightsID = UUID.randomUUID().toString();
			this.resources.add(new P4Resource(P4Collection.RIGHTSMD_COLLECTION, rightsID, rights));

			// add MPEG-21 MCO as additional format
			List<String> formatList = this.getDCField(DCField.format);
			boolean hasMPEG21MCOFormat = false;
			for (String format : formatList) {
				if (format.contains("MPEG-21 MCO")) {
					hasMPEG21MCOFormat = true;
					break;
				}
			}
			if (!hasMPEG21MCOFormat) {
				formatList.add("MPEG-21 MCO (ISO/IEC 21000-21)");
				this.setDCField(DCField.format, formatList);
			}

			// update rights reference in METS
			Mets mets = this.getContentAsMets();

			List<MdSecType> rightsList = null;

			for (AmdSecType amdSecType : mets.getAmdSec()) {

				List<MdSecType> rightsMdList = amdSecType.getRightsMD();

				if (rightsMdList.isEmpty())
					continue;

				if (rightsMdList.size() != 1)
					throw new P4IPException("Multiple rights sections found...");

				rightsMdList.remove(0);

				if (rightsList != null)
					throw new P4IPException("Multiple rights sections found...");

				rightsList = rightsMdList;

			}

			// rights mdRef according to datatype
			MdRef mdRef = new MdRef();
			mdRef.setLABEL(rightsDT.getRef().getLabel());
			mdRef.setMDTYPE(rightsDT.getRef().getMdtype());
			mdRef.setOTHERMDTYPE(rightsDT.getRef().getOthermdtype());
			try {
				mdRef.setCREATED(ModelUtils.Date2XMLGC(new Date()));
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
				throw new P4IPException("Unable to assign new date to rights mdRef...");
			}
			mdRef.setHref(ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_PLACEHOLDER) + "/access/resource/" + P4Collection.RIGHTSMD_COLLECTION + File.separator + rightsID);
			mdRef.setMIMETYPE("application/xml");
			mdRef.setSIZE(size);
			mdRef.setCHECKSUM(md5);
			mdRef.setCHECKSUMTYPE("MD5");

			// rights section according to datatype
			MdSecType rightsSecType = new MdSecType();
			rightsSecType.setMdRef(mdRef);
			rightsSecType.setID(rightsDT.getSection().getId());
			String status = rightsDT.getSection().getStatus();
			if (status != null && !status.isEmpty())
				rightsSecType.setSTATUS(status);

			rightsList.add(rightsSecType);

			// clean up identifiers of SIP sections
			for (MdSecType mdSecType : mets.getDmdSec())
				mdSecType.setID("dmd-" + System.currentTimeMillis());

			for (AmdSecType amdSecType : mets.getAmdSec())
				amdSecType.setID("amd-" + System.currentTimeMillis());

			this.setContent(mets);

			return rightsID;

		} else {
			throw new P4IPException("Unable to add rights to SIP...");
		}
	}

	@Override
	public synchronized void addDNX(Dnx dnx, String id, boolean isMdRef) throws P4IPException {

		MdSecType techMdType = new MdSecType();
		techMdType.setID(id);

		if (isMdRef) {

			MdRef mdRef = new MdRef();
			mdRef.setMDTYPE("OTHER");
			mdRef.setOTHERMDTYPE("DNX");

			try {

				// add node to resources
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				Document dnxNode = dbf.newDocumentBuilder().newDocument();
				ModelUtils.getMarshaller(P4Namespace.DATA_MODEL.getValue()).marshal(dnx, dnxNode);
				String dnxID = UUID.randomUUID().toString();
				this.resources.add(new P4Resource(P4Collection.TECHMD_COLLECTION, dnxID, dnxNode));

				mdRef.setHref(ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_PLACEHOLDER) + "/access/resource/" + P4Collection.TECHMD_COLLECTION + File.separator + dnxID);
				mdRef.setMIMETYPE("application/xml");

				File tmpFile = File.createTempFile("dnx-", ".tmp");
				TransformerFactory.newInstance().newTransformer().transform(new DOMSource(dnxNode), new StreamResult(new FileOutputStream(tmpFile)));
				MessageDigestExtractor mde = new MessageDigestExtractor();
				mde.extract(tmpFile.getAbsolutePath());
				mdRef.setCHECKSUMTYPE("MD5");
				mdRef.setCHECKSUM(mde.getAttributeByName("MD5"));
				mdRef.setSIZE(tmpFile.length());
				tmpFile.delete();

				mdRef.setCREATED(ModelUtils.Date2XMLGC(new Date()));

				techMdType.setMdRef(mdRef);

			} catch (IOException | ToolException | TransformerException e) {
				e.printStackTrace();
				throw new P4IPException("Unable to calculate size or checksum for dnx file...");
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
				throw new P4IPException("Unable to assign new date to rights mdRef...");
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				throw new P4IPException("Unable to create DNX node in mdRef...");
			} catch (JAXBException e) {
				e.printStackTrace();
				throw new P4IPException("Unable to marshal DNX in mdRef...");
			}

		} else {

			MdWrap mdWrap = new MdWrap();
			mdWrap.setMDTYPE("OTHER");
			mdWrap.setOTHERMDTYPE("DNX");
			XmlData xmlData = new XmlData();
			xmlData.getAny().add(dnx);
			mdWrap.setXmlData(xmlData);
			techMdType.setMdWrap(mdWrap);

		}

		AmdSecType amdSecType = new AmdSecType();
		amdSecType.getTechMD().add(techMdType);

		amdSecType.setID("amd-" + System.currentTimeMillis());

		Mets mets = this.getContentAsMets();
		mets.getAmdSec().add(amdSecType);

		this.setContent(mets);
	}

	@Override
	public synchronized void setCreateDate(GregorianCalendar date) throws P4IPException {

		try {

			Mets mets = this.getContentAsMets();

			if (mets.getMetsHdr() == null)
				mets.setMetsHdr(new MetsHdr());
			mets.getMetsHdr().setCREATEDATE(DatatypeFactory.newInstance().newXMLGregorianCalendar(date));

			this.setContent(mets);

			logger.debug("Assigned new METS to SIP... " + mets.getMetsHdr().getCREATEDATE());

		} catch (DatatypeConfigurationException e) {

			e.printStackTrace();
			throw new P4IPException("Unable to set SIP create date...");

		}
	}

	@Override
	public synchronized void purgeFiles() throws P4IPException {

		Mets mets = this.getContentAsMets();

		if (mets.getFileSec() != null) {
			for (FileGrp fileGroup : mets.getFileSec().getFileGrp()) {
				if (fileGroup != null) {
					for (FileType file : fileGroup.getFile()) {
						if (file != null) {
							List<FLocat> toBeRemoved = new ArrayList<>();
							for (FLocat fLocat : file.getFLocat()) {
								String placeholder = ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_PLACEHOLDER);
								if (fLocat.getOTHERLOCTYPE().equals("FILE") && !fLocat.getHref().startsWith(placeholder)) {
									toBeRemoved.add(fLocat);
								}
							}
							for (FLocat fLocat : toBeRemoved) {
								file.getFLocat().remove(fLocat);
							}
						}
						if (file.getFLocat().size() == 0) {
							fileGroup.getFile().remove(file);
						}
					}
					if (fileGroup.getFile().size() == 0) {
						mets.getFileSec().getFileGrp().remove(fileGroup);
					}
				}
			}
			if (mets.getFileSec().getFileGrp().size() == 0) {
				mets.setFileSec(null);
			}
		}

		this.setContent(mets);
	}
}
