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

import it.eurix.archtools.persistence.DatabaseException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import eu.prestoprime.datamanagement.P4PersistenceManager;
import eu.prestoprime.datamanagement.P4PersistenceManager.P4Collection;
import eu.prestoprime.model.ModelUtils;
import eu.prestoprime.model.ModelUtils.P4JAXBPackage;
import eu.prestoprime.model.datatypes.Datatype;
import eu.prestoprime.model.datatypes.Datatypes;
import eu.prestoprime.user.P4UserManager;

public class ConfigurationManager {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);

	private static DataTypesManager datatypesInstance;

	@Deprecated
	public static P4PropertyManager getPropertyInstance() {
		return P4PropertyManager.getInstance();
	}

	@Deprecated
	public static P4UserManager getUserInstance() {
		return P4UserManager.getInstance();
	}

	public static DataTypesManager getDataTypesInstance() {
		if (datatypesInstance == null)
			datatypesInstance = new ConfigurationManager().new DataTypesManager();
		return datatypesInstance;
	}

	private ConfigurationManager() {

	}



	

	public class DataTypesManager {

		private Datatypes loadDatatypesConfiguration() {

			try {
				Node datatypesNode = P4PersistenceManager.getInstance().readXMLResource(P4Collection.ADMIN_COLLECTION, "datatypes.xml");

				Unmarshaller unmarshaller = ModelUtils.getUnmarshaller(P4JAXBPackage.CONF);
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

				Marshaller marshaller = ModelUtils.getMarshaller(P4JAXBPackage.CONF);
				marshaller.marshal(datatypes, node);

				P4PersistenceManager.getInstance().storeXMLResource(P4Collection.ADMIN_COLLECTION, "datatypes.xml", node);

			} catch (JAXBException | ParserConfigurationException e) {
				e.printStackTrace();
				logger.error("Unable to marshall data types descriptor...");
			} catch (DatabaseException e) {
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
