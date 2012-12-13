/**
 * P4Indexer.java
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.prestoprime.datamanagement.DataException;
import eu.prestoprime.model.oais.DIP;
import eu.prestoprime.model.oais.DIP.DCField;
import eu.prestoprime.model.oais.IPException;
import eu.prestoprime.model.oais.InformationPackage;
import eu.prestoprime.search.util.P4IndexObject;

public class P4Indexer extends AbstractPOJOIndexer {

	public P4Indexer() {
		LOGGER.info("Instance of Indexer was created.");
	}

	public boolean indexIP(InformationPackage ip) {
		boolean success = false;
		try {
			P4IndexObject iObject = createIndexObject(ip);

			if (iObject != null) {
				success = super.addObjectToIndex(iObject);
			}
		} catch (IPException e) {
			success = false;
			LOGGER.error("Indexing IP " + ip.getId() + " failed!");
			LOGGER.error(e.getMessage());
		}
		return success;
	}

	/**
	 * Helper method that gets all DublinCore data of an IP from the DB and
	 * builds a P4IndexObject
	 * 
	 * @param ip
	 *            an Information Package object, i.e. SIP, AIP, DIP
	 * @return a P4IndexObject ready to be committed to the Solr Index
	 * @throws JAXBException
	 * @throws DataException
	 * @throws IPException
	 */
	public P4IndexObject createIndexObject(InformationPackage ip) throws IPException {
		P4IndexObject object = new P4IndexObject();

		if (ip != null) {

			object.setId(ip.getId());

			if (ip.getCreateDate() != null) {
				LOGGER.debug("Adding create date: " + ip.getCreateDate().getTime().toGMTString());
				object.setCreateDate(ip.getCreateDate().getTime());
				LOGGER.debug("Added create date...");
			} else {
				LOGGER.warn("DIP " + ip.getId() + " has no create date!");
			}

			LOGGER.debug("Retrieving DCFields...");
			Map<String, List<String>> dcRec = ip.getDCFields();
			LOGGER.debug("Retrieved DCFields " + dcRec);

			if (dcRec != null && !dcRec.isEmpty()) {
				LOGGER.debug("Adding DC record: " + dcRec.get(DCField.identifier.toString()));
				addDcRecord(object, dcRec);
			} else {
				LOGGER.warn("DIP " + ip.getId() + " has no dc record!");
			}

			// Convert DcDate Strings like '2012-10-15' to java.util.Date
			object.setDcDate(convertToDates(dcRec.get(DCField.date.toString())));

			// set user annotations
			if (ip instanceof DIP) {
				DIP dip = (DIP) ip;
				List<Node> userAnnotNodes = dip.getMDResourceAsDOM("usermd");
				if (userAnnotNodes != null && !userAnnotNodes.isEmpty()) {
					String[] keywordsToAdd = extractKeywords(userAnnotNodes);

					if (keywordsToAdd != null && keywordsToAdd.length > 0) {
						LOGGER.debug("Adding " + keywordsToAdd.length + " user annotations to index object.");
						object.setUserAnnot(keywordsToAdd);
					}
				}
			}

			// extract techMd
			object.setAspect(getTechMdProp(ip, "aspect_ratio"));
			object.setCodec(getTechMdProp(ip, "video_codec"));
			object.setDuration(getTechMdFloatProp(ip, "duration"));
			object.setDimensions(getTechMdIntProp(ip, "width"), getTechMdIntProp(ip, "height"));
		} else {
			object = null;
		}
		return object;
	}

	private Date[] convertToDates(List<String> list) {
		// 2012-08-09T00:00:00Z - 2012-10-15
		ArrayList<Date> dateArr = new ArrayList<>(list.size());
		String inFormat = SearchConstants.getString("dcDateFormat");
		if (inFormat == null) {
			// default:
			inFormat = "yyyy-MM-dd";
		}
		DateFormat formatter = new SimpleDateFormat(inFormat);
		Date date;

		for (String s : list) {
			try {
				date = formatter.parse(s);
				dateArr.add(date);
			} catch (ParseException e) {
				LOGGER.warn("Invalid date in DcDate = " + s);
			}
		}
		return (Date[]) dateArr.toArray(new Date[0]);
	}

	/**
	 * for each Node apply XPath to retrieve userAnnot and then add to index
	 * 
	 * @param userAnnotNodes
	 * @return
	 */
	public String[] extractKeywords(List<Node> userAnnotNodes) {

		List<String> keywordsToAdd = new ArrayList<>();
		for (Node n : userAnnotNodes) {
			if (n.getChildNodes() != null && n.getChildNodes().getLength() > 0) {
				Element e = (Element) n.getChildNodes().item(0);
				NodeList keywords = e.getElementsByTagName("mpeg7:Keyword");

				if (keywords.getLength() > 0) {
					String keyword = "";
					for (int i = 0; i < keywords.getLength(); i++) {
						if (keywords.item(i).getTextContent() != null && !keywords.item(i).getTextContent().isEmpty()) {

							keyword = keywords.item(i).getTextContent();

							if (keyword.startsWith("http") || keyword.startsWith("C:")) {
								// don't use these for now
								LOGGER.debug("Discarding keyword : " + keyword);
							} else {
								LOGGER.debug("Adding keyword to list: " + keyword);
								keywordsToAdd.add(keyword);
							}
						}
					}
				}
			}
		}
		if (keywordsToAdd.isEmpty()) {
			return null;
		} else {
			return keywordsToAdd.toArray(new String[0]);
		}
	}

	private void addDcRecord(P4IndexObject object, Map<String, List<String>> dcRec) throws IPException {
		for (String field : dcRec.keySet()) {
			DCField dcField = DCField.valueOf(field);
			switch (dcField) {
			case contributor:
				object.setDcContrib(dcRec.get(DCField.contributor.toString()).toArray(new String[0]));
				break;
			case coverage:
				object.setDcCoverage(dcRec.get(DCField.coverage.toString()).toArray(new String[0]));
				break;
			case creator:
				object.setDcCreator(dcRec.get(DCField.creator.toString()).toArray(new String[0]));
				break;
			// case date: //date is set elsewhere
			// object.setDcDate(dcRec.get(DCField.date.toString()).toArray(new
			// Date[0]));
			// break;
			case description:
				object.setDcDescription(dcRec.get(DCField.description.toString()).toArray(new String[0]));
				break;
			case format:
				object.setDcFormat(dcRec.get(DCField.format.toString()).toArray(new String[0]));
				break;
			case identifier:
				object.setDcIdentifier(dcRec.get(DCField.identifier.toString()).toArray(new String[0]));
				break;
			case language:
				object.setDcLang(dcRec.get(DCField.language.toString()).toArray(new String[0]));
				break;
			case publisher:
				object.setDcPublisher(dcRec.get(DCField.publisher.toString()).toArray(new String[0]));
				break;
			case relation:
				object.setDcRelation(dcRec.get(DCField.relation.toString()).toArray(new String[0]));
				break;
			case source:
				object.setDcSource(dcRec.get(DCField.source.toString()).toArray(new String[0]));
				break;
			case subject:
				object.setDcSubject(dcRec.get(DCField.subject.toString()).toArray(new String[0]));
				break;
			case title:
				object.setDcTitle(dcRec.get(DCField.title.toString()).toArray(new String[0]));
				break;
			case type:
				object.setDcType(dcRec.get(DCField.type.toString()).toArray(new String[0]));
				break;
			}
		}
		LOGGER.debug("...done");
		for (String s : object.getDcCreator()) {
			LOGGER.debug("Creator= " + s);
		}
	}

	public boolean removeIPfromIndex(String ipId) {
		return super.removeObjectFromIndex(ipId);
	}

	public boolean clearIndex() {
		return super.clearIndex();
	}

	public String getTechMdProp(InformationPackage ip, String name) {
		String value = null;
		String xPath = "//dnx:section[@id='videoMD']/dnx:record/dnx:key[@id='" + name + "']/text()";
		try {
			value = ip.executeQuery(xPath).get(0);

		} catch (Exception e) {
			LOGGER.warn("techMD property '" + name + "' could not be found in the DIP! " + e.getMessage());
			LOGGER.warn("XPath = " + xPath);
			// e.printStackTrace();
		}
		return value;
	}

	public Integer getTechMdIntProp(InformationPackage ip, String name) {
		String prop = getTechMdProp(ip, name);
		Integer intProp = null;
		if (prop != null) {
			try {
				intProp = Integer.parseInt(prop);
			} catch (NumberFormatException e) {
				LOGGER.warn(prop + " is not an Integer.");
			}
		}
		return intProp;
	}

	public Float getTechMdFloatProp(InformationPackage ip, String name) {
		String prop = getTechMdProp(ip, name);
		Float floatProp = null;
		if (prop != null) {
			try {
				floatProp = Float.parseFloat(prop);
			} catch (NumberFormatException e) {
				LOGGER.warn(prop + " is not a Float.");
			}
		}
		return floatProp;
	}
}
