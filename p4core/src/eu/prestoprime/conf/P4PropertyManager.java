/**
 * P4PropertyManager.java
 * Author: Francesco Rosso (rosso@eurix.it)
 * 
 * This file is part of PrestoPRIME Preservation Platform (P4).
 * 
 * Copyright (C) 2013 EURIX Srl, Torino, Italy
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
package eu.prestoprime.conf;

import it.eurix.archtools.persistence.DatabaseException;
import it.eurix.archtools.property.PropertyManager;
import it.eurix.archtools.property.PropertyPersistenceManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.prestoprime.datamanagement.P4PersistenceManager;
import eu.prestoprime.datamanagement.P4PersistenceManager.P4Collection;

public class P4PropertyManager extends PropertyManager<P4PropertyManager.P4Property> {
	
	public static enum P4Property {
		P4_SHARE("p4.share", "/mnt/pprime/producers"),
		P4_STORAGE_VOLUME("p4.storage.volume", "/opt/p4"),
		P4_STORAGE_FOLDER("p4.storage.folder", "p4store"),
		P4_VIDEOS_FOLDER("p4.videos.folder", "videos"),
		P4_GRAPH_FOLDER("p4.graph.folder", "rights"),
		P4_FRAMES_FOLDER("p4.frames.folder", "frames"),

		P4_URL("p4.url", "http://p4.prestoprime.eu"),
		P4_WS_URL("p4.ws.url", "https://p4.prestoprime.eu/p4ws"),
		P4_PLACEHOLDER("p4.placeholder", "P4_PH"),
		P4_WS_ADMIN("p4.ws.admin", "p4admin@prestoprime.eu"),
		MASTER_QUALITY_FORMATS("master.quality.formats", "application/mxf,video/mp4"),
		BROWSING_QUALITY_FORMATS("browsing.quality.formats", "video/webm,video/ogg");

		private String key;
		private String defaultValue;

		private P4Property(String name, String defaultValue) {
			this.key = name;
			this.defaultValue = defaultValue;
		}

		public String getKey() {
			return key;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		@Override
		public String toString() {
			return key;
		}
	};
	
	private static P4PropertyManager instance;
	
	public static P4PropertyManager getInstance() {
		if (instance == null)
			instance = new P4PropertyManager();
		return instance;
	}
	
	private P4PropertyManager() {
		super(new PropertyPersistenceManager() {
			
			private final String PROPERTIES_RESOURCE = "p4core.xml";
			
			@Override
			public void setProperties(Properties properties) {
				try {
					File tmp = File.createTempFile("p4core-descriptor", ".tmp");
					properties.storeToXML(new FileOutputStream(tmp), "User-defined p4core properties descriptor");

					Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tmp);

					P4PersistenceManager.getInstance().storeXMLResource(P4Collection.ADMIN_COLLECTION, this.PROPERTIES_RESOURCE, node);
					tmp.delete();
				} catch (IOException e) {
					logger.error("Unable to create temp file");
				} catch (SAXException | ParserConfigurationException e) {
					logger.error("Unable to parse new properties descriptor");
				} catch (DatabaseException e) {
					logger.error("Unable to store new p4core properties descriptor");
				}				
			}
			
			@Override
			public Properties getProperties() {
				try {
					Properties properties = new Properties();

					Node propertiesNode = P4PersistenceManager.getInstance().readXMLResource(P4Collection.ADMIN_COLLECTION, this.PROPERTIES_RESOURCE);

					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					Source xmlSource = new DOMSource(propertiesNode);
					Result outputTarget = new StreamResult(outputStream);
					Transformer tFormer = TransformerFactory.newInstance().newTransformer();

					tFormer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://java.sun.com/dtd/properties.dtd");
					tFormer.transform(xmlSource, outputTarget);
					InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

					properties.loadFromXML(is);

					return properties;
				} catch (DatabaseException e) {
					logger.error("Unable to retrieve from XMLDB p4core properties descriptor");
				} catch (TransformerException e) {
					logger.error("Unable to transform p4core properties descriptor");
				} catch (IOException e) {
					logger.error("Unable to parse p4core properties descriptor");
				}

				return new Properties();
			}
		});
	}
}
