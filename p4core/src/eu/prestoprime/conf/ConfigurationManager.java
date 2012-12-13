/**
 * ConfigurationManager.java
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
package eu.prestoprime.conf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.prestoprime.datamanagement.PersistenceDBException;
import eu.prestoprime.datamanagement.PersistenceManager;
import eu.prestoprime.datamanagement.PersistenceManager.P4Collection;
import eu.prestoprime.model.ModelUtils;
import eu.prestoprime.model.ModelUtils.P4Namespace;
import eu.prestoprime.model.datatypes.Datatype;
import eu.prestoprime.model.datatypes.Datatypes;
import eu.prestoprime.model.users.Users;
import eu.prestoprime.model.users.Users.User;
import eu.prestoprime.model.users.Users.User.Service;

public class ConfigurationManager {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);

	private static PropertyManager propertyInstance;
	private static UserManager userInstance;
	private static DataTypesManager datatypesInstance;

	public static PropertyManager getPropertyInstance() {
		if (propertyInstance == null)
			propertyInstance = new ConfigurationManager().new PropertyManager();
		return propertyInstance;
	}

	public static UserManager getUserInstance() {
		if (userInstance == null)
			userInstance = new ConfigurationManager().new UserManager();
		return userInstance;
	}

	public static DataTypesManager getDataTypesInstance() {
		if (datatypesInstance == null)
			datatypesInstance = new ConfigurationManager().new DataTypesManager();
		return datatypesInstance;
	}

	private ConfigurationManager() {

	}

	public static enum P4Property {
		P4_SHARE("p4.share", "/mnt/pprime/producers"), P4_STORAGE_VOLUME("p4.storage.volume", "/opt/p4"), P4_STORAGE_FOLDER("p4.storage.folder", "p4store"), P4_VIDEOS_FOLDER("p4.videos.folder", "videos"), P4_GRAPH_FOLDER("p4.graph.folder", "rights"), P4_FRAMES_FOLDER("p4.frames.folder", "frames"),

		P4_URL("p4.url", "http://p4.prestoprime.eu"), P4_WS_URL("p4.ws.url", "https://p4.prestoprime.eu/p4ws"), P4_PLACEHOLDER("p4.placeholder", "P4_PH"), P4_WS_ADMIN("p4.ws.admin", "p4admin@prestoprime.eu"), MASTER_QUALITY_FORMATS("master.quality.formats", "application/mxf,video/mp4"), BROWSING_QUALITY_FORMATS("browsing.quality.formats", "video/webm,video/ogg");

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

	public static enum P4Role {
		guest(0), consumer(1), producer(2), admin(3), superadmin(4);

		private int level;

		private P4Role(int level) {
			this.level = level;
		}

		public int getLevel() {
			return level;
		}
	};

	public class PropertyManager {

		private final String PROPERTIES_RESOURCE = "p4core.xml";
		private Properties properties;

		private PropertyManager() {
			properties = this.loadPropertiesConfiguration();
		}

		private Properties loadPropertiesConfiguration() {
			try {
				Properties properties = new Properties();

				Node propertiesNode = PersistenceManager.getInstance().readXMLResource(P4Collection.ADMIN_COLLECTION, this.PROPERTIES_RESOURCE);

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				Source xmlSource = new DOMSource(propertiesNode);
				Result outputTarget = new StreamResult(outputStream);
				Transformer tFormer = TransformerFactory.newInstance().newTransformer();

				tFormer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://java.sun.com/dtd/properties.dtd");
				tFormer.transform(xmlSource, outputTarget);
				InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

				properties.loadFromXML(is);

				return properties;
			} catch (PersistenceDBException e) {
				logger.error("Unable to retrieve from XMLDB p4core properties descriptor");
			} catch (TransformerException e) {
				logger.error("Unable to transform p4core properties descriptor");
			} catch (IOException e) {
				logger.error("Unable to parse p4core properties descriptor");
			}

			return new Properties();
		}

		private void storeProperties(Properties properties) {
			try {
				File tmp = File.createTempFile("p4core-descriptor", ".tmp");
				properties.storeToXML(new FileOutputStream(tmp), "User-defined p4core properties descriptor");

				Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tmp);

				PersistenceManager.getInstance().storeXMLResource(P4Collection.ADMIN_COLLECTION, this.PROPERTIES_RESOURCE, node);
				tmp.delete();
			} catch (IOException e) {
				logger.error("Unable to create temp file");
			} catch (SAXException | ParserConfigurationException e) {
				logger.error("Unable to parse new properties descriptor");
			} catch (PersistenceDBException e) {
				logger.error("Unable to store new p4core properties descriptor");
			}
		}

		public Properties getProperties() {
			return properties;
		}

		public String getProperty(P4Property property) {
			return properties.getProperty(property.getKey(), property.getDefaultValue());
		}

		public void setProperty(P4Property property, String value) {
			properties.setProperty(property.getKey(), value);
			this.storeProperties(properties);
		}
	}

	public class UserManager {

		private final String USERS_RESOURCE = "users.xml";
		private Users users;

		private UserManager() {
			users = this.loadUsersConfiguration();
		}

		private Users loadUsersConfiguration() {
			logger.debug("Loading users descriptor...");

			try {
				Node usersNode = PersistenceManager.getInstance().readXMLResource(P4Collection.ADMIN_COLLECTION, this.USERS_RESOURCE);

				Unmarshaller unmarshaller = ModelUtils.getUnmarshaller(P4Namespace.CONF.getValue());
				Users users = (Users) unmarshaller.unmarshal(usersNode);

				logger.debug("Loaded users descriptor...");

				return users;
			} catch (Exception e) {
				logger.error("Unable to load users descriptor...");
				logger.debug("Using empty users descriptor...");
				return new Users();
			}
		}

		private void writeUsersConfiguration(Users users) {
			logger.debug("Writing users descriptor...");

			try {
				Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

				Marshaller marshaller = ModelUtils.getMarshaller(P4Namespace.CONF.getValue());
				marshaller.marshal(users, node);

				PersistenceManager.getInstance().storeXMLResource(P4Collection.ADMIN_COLLECTION, this.USERS_RESOURCE, node);

				logger.debug("Loaded users descriptor...");
			} catch (JAXBException | ParserConfigurationException e) {
				e.printStackTrace();
				logger.error("Unable to marshall users descriptor...");
			} catch (PersistenceDBException e) {
				e.printStackTrace();
				logger.error("Unable to store users descriptor...");
			}
		}

		public boolean isValidUser(String userID) {
			if (users != null) {
				for (User user : users.getUser()) {
					if (user.getId().equals(userID)) {
						return true;
					}
				}
			}
			return false;
		}

		public P4Role getUserRole(String userID) {
			if (users != null) {
				for (User user : users.getUser()) {
					if (user.getId().equals(userID)) {
						try {
							P4Role role = P4Role.valueOf(user.getRole());
							return role;
						} catch (IllegalArgumentException e) {
							logger.error("No role found for user with serviceID " + userID);
						}
					}
				}
			}
			return P4Role.guest;
		}

		public String getService(String userID, String serviceName) {
			if (users != null) {
				for (User user : users.getUser()) {
					if (user.getId().equals(userID)) {
						for (Service service : user.getService()) {
							if (service.getKey().equals(serviceName)) {
								return service.getValue();
							}
						}
					}
				}
			}
			return null;
		}

		public String addUser(String role) {
			if (users != null) {

				// create new userID
				String userID = UUID.randomUUID().toString();

				// else add new user
				User user = new User();
				user.setId(userID);
				user.setRole(role);
				users.getUser().add(user);
				this.writeUsersConfiguration(users);

				logger.debug("Added new user with userID " + userID);

				return userID;
			}
			return null;
		}

		public void deleteUser(String userID) {
			if (users != null) {

				// check if already existing
				for (User user : users.getUser()) {
					if (user.getId().equals(userID)) {
						users.getUser().remove(user);
						this.writeUsersConfiguration(users);

						logger.debug("Deleted user with userID " + userID);

						break;
					}
				}
			}
		}

		public void addUserService(String userID, String key, String value) {
			if (users != null) {

				// search user
				for (User user : users.getUser()) {
					if (user.getId().equals(userID)) {

						// search service
						for (Service service : user.getService()) {
							if (service.getKey().equals(key)) {

								// delete service
								user.getService().remove(service);
								break;
							}
						}

						// add new service
						Service service = new Service();
						service.setKey(key);
						service.setValue(value);
						user.getService().add(service);

						// commit
						this.writeUsersConfiguration(users);

						logger.debug("Added new service for userID " + userID);

						break;
					}
				}
			}
		}

		public void deleteUserService(String userID, String key) {
			// search user
			for (User user : users.getUser()) {
				if (user.getId().equals(userID)) {

					// search service
					for (Service service : user.getService()) {
						if (service.getKey().equals(key)) {

							// delete service
							user.getService().remove(service);
							break;
						}
					}

					// commit
					this.writeUsersConfiguration(users);

					logger.debug("Deleted service " + key + " for userID " + userID);

					break;
				}
			}
		}
	}

	public class DataTypesManager {

		private Datatypes loadDatatypesConfiguration() {

			try {
				Node datatypesNode = PersistenceManager.getInstance().readXMLResource(P4Collection.ADMIN_COLLECTION, "datatypes.xml");

				Unmarshaller unmarshaller = ModelUtils.getUnmarshaller(P4Namespace.CONF.getValue());
				Datatypes updates = (Datatypes) unmarshaller.unmarshal(datatypesNode);

				return updates;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Unable to load updates descriptor, using empty data types descriptor...");
				return new Datatypes();
			}

		}

		public void writeDatatypesConfiguration(Datatypes datatypes) {

			try {
				Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

				Marshaller marshaller = ModelUtils.getMarshaller(P4Namespace.CONF.getValue());
				marshaller.marshal(datatypes, node);

				PersistenceManager.getInstance().storeXMLResource(P4Collection.ADMIN_COLLECTION, "datatypes.xml", node);

			} catch (JAXBException | ParserConfigurationException e) {
				e.printStackTrace();
				logger.error("Unable to marshall data types descriptor...");
			} catch (PersistenceDBException e) {
				e.printStackTrace();
				logger.error("Unable to store data types descriptor...");
			}
		}

		public void addDatatypeConfiguration(Datatype datatype) {

			Datatypes datatypes = this.loadDatatypesConfiguration();

			for (Datatype dt : datatypes.getDatatype()) {

				if (dt.getName().equals(datatype.getName())) {
					datatypes.getDatatype().remove(dt);
					break;
				}

			}

			datatypes.getDatatype().add(datatype);
			logger.debug("Added Datatype configuration with name " + datatype.getName());

			this.writeDatatypesConfiguration(datatypes);

		}

		public Datatype getDatatype(String name) {

			Datatypes datatypes = this.loadDatatypesConfiguration();

			for (Datatype dt : datatypes.getDatatype()) {

				if (dt.getName().equals(name))
					return dt;

			}

			return null;

		}

	}

}
