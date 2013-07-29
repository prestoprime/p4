/**
 * InformationPackage.java
 * Author: Francesco Rosso (rosso@eurix.it)
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
package eu.prestoprime.p4gui.model.oais;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.prestoprime.model.ModelUtils;
import eu.prestoprime.model.ModelUtils.P4JAXBPackage;
import eu.prestoprime.model.mets.Mets;
import eu.prestoprime.p4gui.util.parse.DC;
import eu.prestoprime.p4gui.util.parse.Location;
import eu.prestoprime.p4gui.util.parse.Resource;

public abstract class InformationPackage {

	protected String ipID;
	protected Node content;

	protected InformationPackage(String ipID, Node content) {
		this.ipID = ipID;
		this.content = content;
	}

	public String getID() {
		return ipID;
	}

	public Node getContent() {
		return content;
	}

	public Mets getMets() {

		try {
			Unmarshaller unmarshaller = ModelUtils.getUnmarshaller(P4JAXBPackage.DATA_MODEL);
			Mets mets = (Mets) unmarshaller.unmarshal(content);

			return mets;
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getContentAsString(boolean indented) {
		try {
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(content);
			TransformerFactory.newInstance().newTransformer().transform(source, result);
			if (!indented)
				return sw.toString().replaceAll("(\\r|\\n)", "");
			return sw.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public DC getDublinCore() {
		DC dc = new DC();
		Node dcRecords = ((Document) content).getElementsByTagName("dc:record").item(0);
		if (dcRecords != null) {
			NodeList nl = dcRecords.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				String title = n.getNodeName();
				if (!title.equals("#text")) {
					title = title.split(":", 2)[1];
					dc.setDcField(title, n.getTextContent());
				}
			}
		}
		return dc;
	}

	public String getOWLPath() {
		String OWLpath;
		try {
			OWLpath = ((Document) content).getElementsByTagName("mets:mdRef").item(0).getAttributes().getNamedItem("xlink:href").getNodeValue();
		} catch (Exception e) {
			OWLpath = "N/A";
		}
		return OWLpath;
	}

	public List<Resource> getResources() {
		ArrayList<Resource> res = new ArrayList<Resource>();
		NodeList nlFiles = ((Document) content).getElementsByTagName("mets:file");
		for (int i = 0; i < nlFiles.getLength(); i++) {
			Node tmpFile = nlFiles.item(i);

			String id;
			try {
				id = tmpFile.getAttributes().getNamedItem("ID").getNodeValue();
			} catch (Exception e) {
				id = "N/A";
			}

			String mimetype;
			try {
				mimetype = tmpFile.getAttributes().getNamedItem("MIMETYPE").getNodeValue();
			} catch (Exception e) {
				mimetype = "N/A";
			}

			NodeList nlLocations = tmpFile.getChildNodes();
			ArrayList<Location> locations = new ArrayList<Location>();
			for (int j = 0; j < nlLocations.getLength(); j++) {
				Node tmpLocation = nlLocations.item(j);
				if (!tmpLocation.getNodeName().equals("mets:FLocat"))
					continue;

				String loctype;
				try {
					loctype = tmpLocation.getAttributes().getNamedItem("LOCTYPE").getNodeValue();
					if (loctype.equals("OTHER"))
						loctype = tmpLocation.getAttributes().getNamedItem("OTHERLOCTYPE").getNodeValue();
				} catch (Exception e) {
					loctype = "N/A";
				}

				String href;
				try {
					href = tmpLocation.getAttributes().getNamedItem("xlink:href").getNodeValue();
				} catch (Exception e) {
					href = "";
				}

				String title;
				try {
					title = tmpLocation.getAttributes().getNamedItem("xlink:title").getNodeValue();
				} catch (Exception e) {
					title = ">> Show info";
				}

				locations.add(new Location(loctype, href, title));
			}

			String checksumtype;
			try {
				checksumtype = tmpFile.getAttributes().getNamedItem("CHECKSUMTYPE").getNodeValue();
			} catch (Exception e) {
				checksumtype = "MD5";
			}

			String checksum;
			try {
				checksum = tmpFile.getAttributes().getNamedItem("CHECKSUM").getNodeValue();
			} catch (Exception e) {
				checksum = "not available";
			}

			res.add(new Resource(id, mimetype, locations, checksumtype, checksum));
		}
		return res;
	}

	@Override
	public String toString() {
		return ipID;
	}
}
