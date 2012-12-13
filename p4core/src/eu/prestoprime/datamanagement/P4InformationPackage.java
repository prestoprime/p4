/**
 * P4InformationPackage.java
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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.prestoprime.conf.ConfigurationManager;
import eu.prestoprime.conf.ConfigurationManager.P4Property;
import eu.prestoprime.datamanagement.PersistenceManager.P4Collection;
import eu.prestoprime.model.ModelUtils;
import eu.prestoprime.model.ModelUtils.P4Namespace;
import eu.prestoprime.model.P4NamespaceContext;
import eu.prestoprime.model.mets.AmdSecType;
import eu.prestoprime.model.mets.FileType;
import eu.prestoprime.model.mets.MdSecType;
import eu.prestoprime.model.mets.Mets;
import eu.prestoprime.model.mets.MetsType.FileSec;
import eu.prestoprime.model.mets.MetsType.FileSec.FileGrp;
import eu.prestoprime.model.oais.DIP.DCField;
import eu.prestoprime.model.oais.InformationPackage;

public abstract class P4InformationPackage implements InformationPackage {

	protected static final Logger logger = LoggerFactory.getLogger(P4InformationPackage.class);

	private int counter;

	protected final String id;
	protected Node content;
	protected List<P4Resource> resources;

	public P4InformationPackage(String id, Node content) {
		this.counter = 0;

		this.id = id;
		this.content = content;

		this.resources = new ArrayList<>();
	}

	public int getCounter() {
		return counter;
	}

	public void incrementCounter() {
		counter++;
	}

	public void decrementCounter() {
		counter--;
	}

	public Node getContent() {
		return content;
	}

	public synchronized Mets getContentAsMets() throws P4IPException {
		try {
			return ((Mets) ModelUtils.getUnmarshaller(P4Namespace.DATA_MODEL.getValue()).unmarshal(content));
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new P4IPException("Unable to unmarshal content...");
		}
	}

	public String getContentAsString(boolean indented) {

		String xmlString = null;

		try {
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(content);
			TransformerFactory.newInstance().newTransformer().transform(source, result);

			if (!indented) {
				xmlString = sw.toString().replaceAll("(\\r|\\n)", "");
			} else {
				xmlString = sw.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return xmlString;
	}

	public synchronized void setContent(Mets mets) throws P4IPException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Node node = dbf.newDocumentBuilder().newDocument();
			ModelUtils.getMarshaller(P4Namespace.DATA_MODEL.getValue()).marshal(mets, node);
			content = node;

			try {
				Transformer t = TransformerFactory.newInstance().newTransformer();
				t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				t.transform(new DOMSource(content), new StreamResult(System.out));
			} catch (TransformerException te) {
				logger.error("nodeToString Transformer Exception");
			}

		} catch (ParserConfigurationException | JAXBException e) {
			e.printStackTrace();
			throw new P4IPException("Unable to marshal mets...");
		}
	}

	public abstract void selfRelease() throws P4IPException;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		try {
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(content);
			TransformerFactory.newInstance().newTransformer().transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sw.toString();
	}

	@Override
	public synchronized List<Node> executeNodeQuery(String xPath) throws P4IPException {
		List<Node> resultList = new ArrayList<>();

		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(new P4NamespaceContext());
			XPathExpression expr = xpath.compile(xPath);

			NodeList nodes = (NodeList) expr.evaluate(content, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++)
				resultList.add(nodes.item(i));
		} catch (Exception e) {
			throw new P4IPException("Unable to execute xPath " + xPath + " on IP " + id);
		}

		return resultList;
	}

	@Override
	public synchronized List<String> executeQuery(String xPath) throws P4IPException {
		List<String> resultList = new ArrayList<>();

		for (Node result : this.executeNodeQuery(xPath))
			resultList.add(result.getNodeValue());

		return resultList;
	}

	@Override
	public synchronized boolean hasAVMaterial() throws P4IPException {
		Mets mets = this.getContentAsMets();

		FileSec fileSec = mets.getFileSec();
		if (fileSec == null) {
			logger.debug("File Section not available...");
			return false;
		} else {
			logger.debug("File Section found... validating.");

			List<FileGrp> fileGrpList = fileSec.getFileGrp();
			if (fileGrpList == null || fileGrpList.size() != 1) {
				logger.debug("Unable to retrive FileGrp or wrong number of FileGrp");
				throw new P4IPException("Unable to retrive FileGrp or wrong number of FileGrp");
			}

			List<FileType> fileTypeList = fileGrpList.get(0).getFile();
			if (fileTypeList == null || fileTypeList.size() == 0) {
				logger.debug("Unable to retrieve File List or no File found");
				throw new P4IPException("Unable to retrieve File List or no File found");
			}

			// FIXME: only one file is allowed for each SIP
			if (fileTypeList.size() != 1) {
				logger.debug("More than one File found in the SIP...");
				throw new P4IPException("More than one File found in the SIP...");
			}

			return true;
		}
	}

	@Override
	public synchronized boolean hasRights() throws P4IPException {

		Mets mets = this.getContentAsMets();

		List<AmdSecType> amdSecList = mets.getAmdSec();

		int rightsCounter = 0;
		for (AmdSecType amdSecType : amdSecList) {

			List<MdSecType> rightsMdList = amdSecType.getRightsMD();

			if (rightsMdList == null) {
				continue;
			} else {
				for (MdSecType mdSecType : rightsMdList) {

					if (mdSecType.getMdRef() != null && mdSecType.getMdRef().getHref() != null && !mdSecType.getMdRef().getHref().isEmpty()) {
						rightsCounter++;
						continue;
					} else if (mdSecType.getMdWrap() != null && mdSecType.getMdWrap().getXmlData() != null) {
						rightsCounter++;
						continue;
					} else {
						throw new P4IPException("Invalid rightsMD section...");
					}
				}
			}
		}
		switch (rightsCounter) {
		case 0:
			return false;
		case 1:
			return true;
		default:
			throw new P4IPException("Too many rightsMD sections...");
		}
	}

	@Override
	public synchronized List<String> getAVFormats() throws P4IPException {
		return this.executeQuery("//mets:file/@MIMETYPE");
	}

	@Override
	public synchronized List<String> getAVMaterial(String mimeType, String location) throws P4IPException {
		List<String> fLocats;
		if (location.equals("URL"))
			fLocats = this.executeQuery("//mets:file[@MIMETYPE='" + mimeType + "']/mets:FLocat[@LOCTYPE='OTHER' and @OTHERLOCTYPE='FILE']/@xlink:href");
		else
			fLocats = this.executeQuery("//mets:file[@MIMETYPE='" + mimeType + "']/mets:FLocat[@LOCTYPE='" + location + "' or @LOCTYPE='OTHER' and @OTHERLOCTYPE='" + location + "']/@xlink:href");

		List<String> finalFLocats = new ArrayList<>();

		for (String fLocat : fLocats) {
			String finalFLocat;
			switch (location) {
			case "FILE":
				finalFLocat = fLocat.replaceAll(ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_PLACEHOLDER), ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_STORAGE_VOLUME));
				break;
			case "URL":
				finalFLocat = fLocat.replaceAll(ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_PLACEHOLDER), ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_URL));
				break;
			default:
				finalFLocat = fLocat;
				break;
			}
			finalFLocats.add(finalFLocat);
		}
		return finalFLocats;
	}

	@Override
	public synchronized List<String> getAVMaterial(String mimeType, String metsLocType, String outputLocType) throws P4IPException {
		List<String> fLocats;
		if (metsLocType.equals("URL"))
			fLocats = this.executeQuery("//mets:file[@MIMETYPE='" + mimeType + "']/mets:FLocat[@LOCTYPE='URL']/@xlink:href");
		else
			fLocats = this.executeQuery("//mets:file[@MIMETYPE='" + mimeType + "']/mets:FLocat[@LOCTYPE='" + metsLocType + "' or @LOCTYPE='OTHER' and @OTHERLOCTYPE='" + metsLocType + "']/@xlink:href");

		List<String> finalFLocats = new ArrayList<>();

		for (String fLocat : fLocats) {
			String finalFLocat;
			switch (outputLocType) {
			case "FILE":
				finalFLocat = fLocat.replaceAll(ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_PLACEHOLDER), ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_STORAGE_VOLUME));
				break;
			case "URL":
				finalFLocat = fLocat.replaceAll(ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_PLACEHOLDER), ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_URL));
				break;
			default:
				finalFLocat = fLocat;
				break;
			}
			finalFLocats.add(finalFLocat);
		}
		return finalFLocats;
	}

	@Override
	public String getChecksum(String mimeType, String checksumType) throws P4IPException {
		List<String> checksums = this.executeQuery("//mets:file[@MIMETYPE='" + mimeType + "' and @CHECKSUMTYPE='" + checksumType + "']/@CHECKSUM");
		if (checksums.size() > 0)
			return checksums.get(0);
		else
			return null;
	}

	@Override
	public int getDuration() throws P4IPException {
		return (int) (Double.parseDouble(this.executeQuery("//dnx:record/dnx:key[@id = 'duration']/text()").get(0)) * 1000);
	}

	@Override
	public GregorianCalendar getCreateDate() throws P4IPException {

		List<String> dates = this.executeQuery("//mets:metsHdr/@CREATEDATE");

		if (dates != null && !dates.isEmpty()) {
			String createDate = dates.get(0);

			if (createDate != null && !createDate.isEmpty()) {

				try {

					return DatatypeFactory.newInstance().newXMLGregorianCalendar(createDate).toGregorianCalendar();

				} catch (DatatypeConfigurationException e) {

					throw new P4IPException("Unable to parse METS date...");

				}

			}
		}

		return null;
	}

	@Override
	public Map<String, List<String>> getDCFields() throws P4IPException {
		Map<String, List<String>> dcFields = new HashMap<>();

		for (DCField field : DCField.values())
			dcFields.put(field.toString(), this.executeQuery("//dc:record/dc:" + field + "/node()"));

		return dcFields;
	}

	@Override
	public synchronized List<String> getDCField(DCField field) throws P4IPException {
		return this.getDCFields().get(field.toString());
	}

	public class P4Resource {

		private P4Collection collection;
		private String resourceID;
		private Node resourceNode;

		public P4Resource(P4Collection collection, String resourceID, Node resourceNode) {
			this.collection = collection;
			this.resourceID = resourceID;
			this.resourceNode = resourceNode;
		}

		public P4Collection getCollection() {
			return collection;
		}

		public String getResourceID() {
			return resourceID;
		}

		public Node getResourceNode() {
			return resourceNode;
		}
	}
}
