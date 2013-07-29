/**
 * P4ToolManager.java
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
package eu.prestoprime.tools;

import it.eurix.archtools.persistence.DatabaseException;
import it.eurix.archtools.tool.ToolManager;
import it.eurix.archtools.tool.ToolPersistenceManager;
import it.eurix.archtools.tool.jaxb.Tool;
import it.eurix.archtools.tool.jaxb.Tools;

import javax.xml.bind.JAXBException;

import org.w3c.dom.Node;

import eu.prestoprime.datamanagement.P4PersistenceManager;
import eu.prestoprime.datamanagement.P4PersistenceManager.P4Collection;

public class P4ToolManager extends ToolManager {

	private static P4ToolManager instance;
	
	public static P4ToolManager getInstance() {
		if (instance == null)
			instance = new P4ToolManager();
		return instance;
	}
	
	private P4ToolManager() {
		super(new ToolPersistenceManager() {
			
			private final String DESCRIPTOR_NAME = "tools.xml";

			@Override
			public Tool[] getTools() {
				try {
					Node node = P4PersistenceManager.getInstance().readXMLResource(P4Collection.ADMIN_COLLECTION, DESCRIPTOR_NAME);
					Tools tools = (Tools) unmarshaller.unmarshal(node);
					return tools.getTool().toArray(new Tool[0]);
				} catch (DatabaseException e) {
					e.printStackTrace();
				} catch (JAXBException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			public Tool getTool(String name) {
				for (Tool tool : this.getTools())
					if (tool.getName().equalsIgnoreCase(name))
						return tool;
				return null;
			}
		});
	}
}
