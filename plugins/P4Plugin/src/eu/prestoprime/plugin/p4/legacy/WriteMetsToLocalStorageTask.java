/**
 * WriteMetsToLocalStorageTask.java
 * Author: Philip Kahle (philip.kahle@uibk.ac.at) 
 * Contributors: Francesco Gallo (gallo@eurix.it), Francesco Rosso (rosso@eurix.it)
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
package eu.prestoprime.plugin.p4.legacy;

import it.eurix.archtools.data.DataException;
import it.eurix.archtools.workflow.exceptions.TaskExecutionFailedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import eu.prestoprime.datamanagement.P4DataManager;
import eu.prestoprime.datamanagement.impl.P4DIP;
import eu.prestoprime.workflow.P4Task;

/**
 * Store a copy of the current DIP content as XML in the object's folder in
 * p4store as $SIP-ID_$TIMESTAMP.xml
 */
public class WriteMetsToLocalStorageTask implements P4Task {

	private static final Logger LOGGER = LoggerFactory.getLogger(WriteMetsToLocalStorageTask.class);
	private static final String DATE_FORMAT = "yy-MM-dd-HH-mm-ss";
	private String outputFileName = "";

	@Override
	public void execute(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get sipID
		final String sipID = dParamsString.get("sipID");
		final String p4Store = dParamsString.get("p4storeFolder");
		if (sipID == null) {
			throw new TaskExecutionFailedException("SIP ID was not stored in workflow...");
		}
		if (p4Store == null) {
			throw new TaskExecutionFailedException("p4 storage folder was not specified in workflow...");
		}

		try {
			// get DIP from DB
			final P4DIP dip = (P4DIP) P4DataManager.getInstance().getDIPByID(sipID);

			// get xml content
			final Node content = dip.getContent(); // TODO aggregate content
													// from all MD resources in
													// this Node.

			// get a date string for having a unique xml file name
			final DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
			final String dateString = formatter.format(new Date());

			// define the output file's name
			outputFileName = p4Store + File.separator + sipID + "_" + dateString + ".xml";

			LOGGER.info("Dumping Mets to " + outputFileName);

			// dump content to a new file
			transformToFile(content, outputFileName);

		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Information Package could not be retrieved...");
		} catch (TransformerException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Content could not be transformed to a String...");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("File " + outputFileName + " could not be written...");

		}
	}

	private void transformToFile(Node content, String resultFile) throws TransformerException, FileNotFoundException {
		PrintWriter pw = new PrintWriter(new FileOutputStream(resultFile));
		StreamResult result = new StreamResult(pw);
		DOMSource source = new DOMSource(content);
		TransformerFactory.newInstance().newTransformer().transform(source, result);
		pw.flush();
		pw.close();
	}
}
