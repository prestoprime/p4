/**
 * P4WorkflowManager.java
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
package eu.prestoprime.workflow;

import it.eurix.archtools.persistence.DatabaseException;
import it.eurix.archtools.workflow.WorkflowManager;
import it.eurix.archtools.workflow.WorkflowPersistenceManager;
import it.eurix.archtools.workflow.jaxb.WfDescriptor;
import it.eurix.archtools.workflow.jaxb.WfStatus;

import java.io.File;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;

import eu.prestoprime.datamanagement.P4PersistenceManager;
import eu.prestoprime.datamanagement.P4PersistenceManager.P4Collection;

public class P4WorkflowManager extends WorkflowManager {

	private static P4WorkflowManager instance;

	private P4WorkflowManager() {
		super(new WorkflowPersistenceManager() {

			private static final String DESCRIPTOR_NAME = "workflows.xml";

			@Override
			public void setWfStatus(WfStatus wfStatus) {
				if (wfStatus != null) {
					String jobID = wfStatus.getId();
					if (jobID == null)
						return;
					try {
						logger.debug("Creating wfStatus for workflow" + jobID);
	
						Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
						super.marshaller.marshal(wfStatus, node);
						P4PersistenceManager.getInstance().storeXMLResource(P4Collection.WF_COLLECTION, jobID, node);
	
						logger.debug("Created wfStatus for workflow" + jobID);
					} catch (ParserConfigurationException e) {
						e.printStackTrace();
					} catch (JAXBException e) {
						e.printStackTrace();
					} catch (DatabaseException e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void deleteWfStatus(String jobID) {
				try {
					P4PersistenceManager.getInstance().deleteXMLResource(P4Collection.WF_COLLECTION, jobID);
				} catch (DatabaseException e) {
					logger.error("Unable to delete wfStatus from persistenceDB...");
				}
			}
			
			@Override
			public void setWfDescriptor(File descriptor) {
				try {
					// get an empty node
					Node descriptorNode = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

					// validate the new workflows descriptor
					WfDescriptor props = (WfDescriptor) super.unmarshaller.unmarshal(descriptor);
					super.marshaller.marshal(props, descriptorNode);

					// store the new validated workflows descriptor
					P4PersistenceManager.getInstance().storeXMLResource(P4Collection.ADMIN_COLLECTION, DESCRIPTOR_NAME, descriptorNode);
				} catch (ParserConfigurationException e) {
					logger.error("Unable to create an empty Node...");
					// throw new ConfigurationException("Unable to create an empty Node...");
				} catch (JAXBException e) {
					logger.error("Unable to validate the new workflows descriptor");
					// throw new ConfigurationException("Unable to validate the new workflows descriptor");
				} catch (DatabaseException e) {
					logger.error("Unable to store the new validated workflows descriptor");
					// throw new ConfigurationException("Unable to store the new validated workflows descriptor");
				}
			}

			@Override
			public WfStatus getWfStatus(String jobID) {
				try {
					Node node = P4PersistenceManager.getInstance().readXMLResource(P4Collection.WF_COLLECTION, jobID);
					return (WfStatus) super.unmarshaller.unmarshal(node);
				} catch (DatabaseException e) {
					e.printStackTrace();
				} catch (JAXBException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public WfDescriptor getWfDescriptor() {
				WfDescriptor wfDescriptor = null;
				try {
					Node descriptorNode = P4PersistenceManager.getInstance().readXMLResource(P4Collection.ADMIN_COLLECTION, DESCRIPTOR_NAME);
					wfDescriptor = (WfDescriptor) super.unmarshaller.unmarshal(descriptorNode);
				} catch (DatabaseException | JAXBException | NullPointerException e) {
					logger.error("Unable to load wfDescriptor: using empty one...");
					logger.error(e.getMessage() + "...");
				}
				if (wfDescriptor == null)
					wfDescriptor = new WfDescriptor();
				return wfDescriptor;
			}
		});
	}

	public static P4WorkflowManager getInstance() {
		if (instance == null)
			instance = new P4WorkflowManager();
		return instance;
	}
}
