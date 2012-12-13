/**
 * RightsUtils.java
 * Author: Francesco Gallo (gallo@eurix.it)
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
package eu.prestoprime.plugin.rights;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import eu.prestoprime.datamanagement.PersistenceDBException;
import eu.prestoprime.datamanagement.PersistenceManager;
import eu.prestoprime.datamanagement.PersistenceManager.P4Collection;
import eu.prestoprime.model.ext.rights.RightsIndex;
import eu.prestoprime.model.ext.rights.RightsInstance;
import eu.prestoprime.plugin.p4.tools.XSLTProc;
import eu.prestoprime.tools.ToolException;
import eu.prestoprime.workflow.exceptions.TaskExecutionFailedException;

public abstract class RightsUtils {

	private static Logger logger = LoggerFactory.getLogger(RightsUtils.class);

	public static JAXBContext getRightsContext() throws JAXBException {
		return JAXBContext.newInstance("eu.prestoprime.model.ext.rights");
	}

	public static RightsIndex getRightsIndex() {
		RightsIndex index;
		try {
			Node rightsIndexNode = PersistenceManager.getInstance().readXMLResource(P4Collection.RIGHTSMD_COLLECTION, "RightsIndex.xml");
			index = (RightsIndex) RightsUtils.getRightsContext().createUnmarshaller().unmarshal(rightsIndexNode);
		} catch (PersistenceDBException | JAXBException e) {
			index = new RightsIndex();
		}
		return index;
	}

	public static void deleteRightsIndex() throws TaskExecutionFailedException {

		try {
			PersistenceManager.getInstance().deleteXMLResource(P4Collection.RIGHTSMD_COLLECTION, "RightsIndex.xml");
		} catch (PersistenceDBException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to clear Rights Index");
		}
	}

	public static void setRightsIndex(RightsIndex index) throws TaskExecutionFailedException {

		try {
			Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			Marshaller marshaller = RightsUtils.getRightsContext().createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(index, node);

			PersistenceManager.getInstance().storeXMLResource(P4Collection.RIGHTSMD_COLLECTION, "RightsIndex.xml", node);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to set Rights Index");
		}
	}

	public static void indexRights(String sipID, File owlFile) throws JAXBException, ToolException, PersistenceDBException, ParserConfigurationException, TaskExecutionFailedException {

		RightsIndex index = RightsUtils.getRightsIndex();

		XSLTProc xsltproc = new XSLTProc();

		// step 1: clean up OWL file
		String byPassXsl = xsltproc.addResourceFile("ByPassIntrsctns.xsl");
		xsltproc.setXSLFile(byPassXsl);

		xsltproc.extract(owlFile.getAbsolutePath());

		String step1Result = xsltproc.getOutputFile();

		// step2: extract RightsInstance

		String ppavro = xsltproc.addResourceFile("ppavro.owl");
		xsltproc.addStringParam("ppavro", ppavro);
		String countrycodes = xsltproc.addResourceFile("ebu_Iso3166CountryCodeCS.xml");
		xsltproc.addStringParam("countrycodes", countrycodes);
		String languagecodes = xsltproc.addResourceFile("ebu_Iso639_LanguageCodeCS.xml");
		xsltproc.addStringParam("languagecodes", languagecodes);

		xsltproc.addStringParam("instanceid", sipID);

		String makeRightsXsl = xsltproc.addResourceFile("MakeRightsIndex.xsl");
		xsltproc.setXSLFile(makeRightsXsl);

		xsltproc.extract(step1Result);

		String step2Result = xsltproc.getOutputFile();

		File rightsInstance = new File(step2Result);

		logger.debug("RightsInstance available at: " + step2Result);

		RightsInstance instance = (RightsInstance) RightsUtils.getRightsContext().createUnmarshaller().unmarshal(rightsInstance);

		List<RightsInstance> rightsInstanceList = index.getRightsInstance();

		boolean isNewInstance = true;
		for (int i = 0; i < rightsInstanceList.size(); i++) {
			if (rightsInstanceList.get(i).getId() != null && rightsInstanceList.get(i).getId().equals(sipID)) {
				logger.debug("Replacing instance " + i + " " + rightsInstanceList.get(i).getId());
				rightsInstanceList.set(i, instance);
				isNewInstance = false;
				break;
			}
		}

		if (isNewInstance)
			index.getRightsInstance().add(instance);

		logger.debug("Sanity check: instance id " + instance.getId());

		RightsUtils.setRightsIndex(index);
	}
}
