/**
 * ManualUpdateAIPTask.java
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
package eu.prestoprime.plugin.qa;

import java.io.File;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import eu.prestoprime.datamanagement.DataManager;
import eu.prestoprime.model.oais.AIP;
import eu.prestoprime.workflow.exceptions.TaskExecutionFailedException;
import eu.prestoprime.workflow.tasks.P4Task;

public class ManualUpdateAIPTask implements P4Task {

	private static final Logger logger = Logger.getLogger(ManualUpdateAIPTask.class);

	@Override
	public void execute(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// retrieve dynamic parameters
		String id = dParamsString.get("id");
		File resultFile = dParamsFile.get("resultFile");

		// update AIP
		AIP aip = null;
		try {
			// parse resultFile
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Node node = dbf.newDocumentBuilder().parse(resultFile);

			// get AIP
			aip = DataManager.getInstance().getAIPByID(id);

			// update AIP
			aip.updateSection(node, "qa_manual");

			logger.debug("Updated QA section...");
		} catch (Exception e) {
			throw new TaskExecutionFailedException("Unable to update AIP...\n" + e.getMessage());
		} finally {
			// release AIP
			DataManager.getInstance().releaseIP(aip);
		}
	}
}
