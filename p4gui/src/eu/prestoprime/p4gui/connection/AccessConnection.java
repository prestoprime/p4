/**
 * AccessConnection.java
 * Author: Francesco Rosso (rosso@eurix.it)
 * Contributors: Philip Kahle (philip.kahle@uibk.ac.at)
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
package eu.prestoprime.p4gui.connection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.prestoprime.p4gui.model.Event;
import eu.prestoprime.p4gui.model.P4Service;
import eu.prestoprime.p4gui.model.oais.DIP;
import eu.prestoprime.p4gui.util.parse.DCField;
import eu.prestoprime.p4gui.util.parse.Frame;
import eu.prestoprime.p4gui.util.parse.Preview;

public abstract class AccessConnection {

	private static final Logger logger = LoggerFactory.getLogger(AccessConnection.class);

	public static DIP getDIP(P4Service service, String id) {
		try {
			String path = service.getURL() + "/access/dip/" + id;
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				Node dom = dbf.newDocumentBuilder().parse(is);
				is.close();

				return new DIP(id, dom);
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean checkIdentifier(P4Service service, String identifier) {
		try {
			String path = service.getURL() + "/access/dip/checkdcid/" + identifier;
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpEntity entity = client.executeRequest(request).getEntity();
			if (entity != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				String line;
				if ((line = reader.readLine()) != null) {
					if (line.equals("available")) {
						return true;
					} else {
						return false;
					}
				}
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static ArrayList<DCField> getDCFields(P4Service service, String id) {
		ArrayList<DCField> dc_fields = new ArrayList<DCField>();
		try {
			String path = service.getURL() + "/access/dip/" + id + "/info";
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] fields = line.split("\\t");
					for (int i = 1; i < fields.length; i++)
						dc_fields.add(new DCField(fields[0], fields[i]));
				}
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dc_fields;
	}

	public static List<Event> getEvents(P4Service service, String id) {
		final String PREMIS_NS = "http://www.loc.gov/standards/premis/v1";

		DIP dip = AccessConnection.getDIP(service, id);
		NodeList eventNodes = ((Document) dip.getContent()).getDocumentElement().getElementsByTagNameNS(PREMIS_NS, "event");
		logger.debug("Found " + eventNodes.getLength() + " PREMIS events...");

		List<Event> events = new ArrayList<>();
		for (int i = 0; i < eventNodes.getLength(); i++) {
			Node eventNode = eventNodes.item(i);
			do {
				if (eventNode instanceof Element) {
					Node typeNode = ((Element) eventNode).getElementsByTagNameNS(PREMIS_NS, "eventType").item(0);
					Node dateTimeNode = ((Element) eventNode).getElementsByTagNameNS(PREMIS_NS, "eventDateTime").item(0);
					Node detailNode = ((Element) eventNode).getElementsByTagNameNS(PREMIS_NS, "eventDetail").item(0);

					String type = typeNode.getTextContent();
					Calendar dateTime;
					try {
						dateTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeNode.getTextContent()).toGregorianCalendar();
					} catch (Exception e) {
						dateTime = new GregorianCalendar();
					}
					String detail = detailNode.getTextContent();

					logger.debug("Found event " + type + "...");

					Event event = new Event(type, dateTime, detail);
					events.add(event);
				}
			} while ((eventNode = eventNode.getNextSibling()) != null);
		}
		return events;
	}

	public static ArrayList<Preview> getPreviewsPath(P4Service service, String id) {
		ArrayList<Preview> previews = new ArrayList<Preview>();
		try {
			String path = service.getURL() + "/access/dip/" + id + "/preview";
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));

				String line;
				while ((line = reader.readLine()) != null) {
					String[] fields = line.split("\t");
					previews.add(new Preview(fields[0], fields[1]));
				}
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return previews;
	}

	public static String getThumbPath(P4Service service, String id) {
		String thumb_path = null;
		try {
			String path = service.getURL() + "/access/dip/" + id + "/thumb";
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			// If no image is found for some reason, the Tomcat 404 page was
			// injected into the page. So I made this check. Philip
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream is = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));

					String line;
					if ((line = reader.readLine()) != null) {
						thumb_path = line;
					}
				}
				EntityUtils.consume(entity);
			} else {
				logger.debug("Thumbnail could not be found!");
				thumb_path = "/p4gui/resources/access/image-missing.svg";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return thumb_path;
	}

	public static ArrayList<Frame> getFrames(P4Service service, String id) {
		ArrayList<Frame> frames = new ArrayList<Frame>();
		try {
			String path = service.getURL() + "/access/dip/" + id + "/frames";
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));

				String line;
				while ((line = reader.readLine()) != null) {
					try {
						String[] fields = line.substring(line.lastIndexOf("/") + 1, line.lastIndexOf(".")).split("F");
						int frame = Integer.parseInt(fields[0]);
						int frameRate = Integer.parseInt(fields[1]);
						frames.add(new Frame(frame, line, frameRate));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return frames;
	}

	public static String getGraphPath(P4Service service, String id) {
		String graph_path = null;
		try {
			String path = service.getURL() + "/access/dip/" + id + "/rights/graph";
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));

				String line;
				if ((line = reader.readLine()) != null) {
					graph_path = line;
				}
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return graph_path;
	}

	public static String getContainer(P4Service service, String id) {
		String container = null;
		try {
			String path = service.getURL() + "/access/dip/" + id + "/info/container";
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));

				String line;
				if ((line = reader.readLine()) != null) {
					container = line;
				}
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return container;
	}

	public static String getDracmaURN(P4Service service, String id) {
		String UMID = null;
		try {
			String path = service.getURL() + "/access/dip/" + id + "/info/dracma";
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));

				String line;
				if ((line = reader.readLine()) != null) {
					UMID = line;
				}
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("UMID: " + UMID);
		return UMID;
	}

	public static Date checkDataTypeAvailability(P4Service service, String id, String dataType) {
		String path = service.getURL() + "/access/dip/" + id + "/" + dataType;

		try {
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				String line;
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				if ((line = reader.readLine()) != null) {
					XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(line.trim());
					return cal.toGregorianCalendar().getTime();
				}
			}
		} catch (Exception e) {
			logger.error("Unable to parse the date...");
		}
		return null;
	}

	public static List<String> getDataTypeResult(P4Service service, String id, String dataType) {
		List<String> results = new ArrayList<>();

		String path = service.getURL() + "/access/dip/" + id + "/" + dataType + "/result";

		try {
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				String line;
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				while ((line = reader.readLine()) != null) {
					results.add(line.trim());
				}
			}
		} catch (Exception e) {
			logger.error("Unable to get the datatype result...");
		}
		return results;
	}

	public static String getResource(P4Service service, URL url) {
		String path = url.toString();

		try {
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				String line;
				StringBuffer sb = new StringBuffer();
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				while ((line = reader.readLine()) != null) {
					sb.append(line.trim());
				}

				return sb.toString();
			}
		} catch (Exception e) {
			logger.error("Unable to get the result...");
		}

		return null;
	}
}
