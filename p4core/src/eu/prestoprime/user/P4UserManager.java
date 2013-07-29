/**
 * P4UserManager.java
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
package eu.prestoprime.user;

import it.eurix.archtools.persistence.DatabaseException;
import it.eurix.archtools.user.UserManager;
import it.eurix.archtools.user.UserPersistenceManager;
import it.eurix.archtools.user.jaxb.Users;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;

import eu.prestoprime.datamanagement.P4PersistenceManager;
import eu.prestoprime.datamanagement.P4PersistenceManager.P4Collection;

public class P4UserManager extends UserManager {

	private static P4UserManager instance;
	
	public static P4UserManager getInstance() {
		if (instance == null)
			instance = new P4UserManager();
		return instance;
	}
	
	public P4UserManager() {
		super(new UserPersistenceManager() {
			
			private final String USERS_RESOURCE = "users.xml";
			
			@Override
			public void setUserDescriptor(Users users) {
				logger.debug("Writing users descriptor...");

				try {
					Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

					marshaller.marshal(users, node);

					P4PersistenceManager.getInstance().storeXMLResource(P4Collection.ADMIN_COLLECTION, this.USERS_RESOURCE, node);

					logger.debug("Loaded users descriptor...");
				} catch (JAXBException | ParserConfigurationException e) {
					e.printStackTrace();
					logger.error("Unable to marshall users descriptor...");
				} catch (DatabaseException e) {
					e.printStackTrace();
					logger.error("Unable to store users descriptor...");
				}
			}
			
			@Override
			public Users getUsersDescriptor() {
				logger.debug("Loading users descriptor...");

				try {
					Node usersNode = P4PersistenceManager.getInstance().readXMLResource(P4Collection.ADMIN_COLLECTION, this.USERS_RESOURCE);

					Users users = (Users) unmarshaller.unmarshal(usersNode);

					logger.debug("Loaded users descriptor...");

					return users;
				} catch (Exception e) {
					logger.error("Unable to load users descriptor...");
					logger.debug("Using empty users descriptor...");
					return new Users();
				}
			}
		});
	}
}
