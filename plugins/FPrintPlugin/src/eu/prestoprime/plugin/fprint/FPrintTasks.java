/**
 * FPrintTasks.java
 * Author: Francesco Rosso (rosso@eurix.it)
 * 
 * This file is part of PrestoPRIME Preservation Platform (P4).
 * 
 * Copyright (C) 2012 EURIX Srl, Torino, Italy
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
package eu.prestoprime.plugin.fprint;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import eu.prestoprime.datamanagement.DataException;
import eu.prestoprime.datamanagement.DataManager;
import eu.prestoprime.model.oais.AIP;
import eu.prestoprime.model.oais.DIP;
import eu.prestoprime.model.oais.IPException;
import eu.prestoprime.workflow.exceptions.TaskExecutionFailedException;
import eu.prestoprime.workflow.plugin.WfPlugin;
import eu.prestoprime.workflow.plugin.WfPlugin.WfService;

@WfPlugin(name = "FPrintPlugin")
public class FPrintTasks {

	private static final Logger logger = LoggerFactory.getLogger(FPrintTasks.class);

	@WfService(name = "fprint_upload", version = "0.8.0")
	public void upload(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		logger.debug("Called " + this.getClass().getName());

		// prepare dynamic variables
		String id = dParamsString.get("id");
		String targetName = id + ".webm";
		String fileLocation = null;

		// prepare static variables
		String host = sParams.get("host");
		int port = Integer.parseInt(sParams.get("port"));
		String username = sParams.get("username");
		String password = sParams.get("password");
		String workdir = sParams.get("workdir");

		// retrieve AIP
		try {
			DIP dip = DataManager.getInstance().getDIPByID(id);
			List<String> fLocatList = dip.getAVMaterial("video/webm", "FILE");
			fileLocation = fLocatList.get(0);
		} catch (DataException | IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve the fileLocation of the Master Quality...");
		}

		logger.debug("Found video/webm location: " + fileLocation);

		// send to remote FTP folder
		FTPClient client = new FTPClient();
		try {
			client.connect(host, port);
			if (FTPReply.isPositiveCompletion(client.getReplyCode())) {
				if (client.login(username, password)) {
					client.setFileType(FTP.BINARY_FILE_TYPE);
					if (client.changeWorkingDirectory(workdir)) {
						// TODO add behavior if file name is already present in
						// remote ftp folder
						// now OVERWRITES
						File file = new File(fileLocation);
						if (file.isFile()) {
							if (client.storeFile(targetName, new FileInputStream(file))) {
								logger.info("Stored file on server " + host + ":" + port + workdir);
							} else {
								throw new TaskExecutionFailedException("Cannot store file on server");
							}
						} else {
							throw new TaskExecutionFailedException("Local file doesn't exist or is not acceptable");
						}
					} else {
						throw new TaskExecutionFailedException("Cannot browse directory " + workdir + " on server");
					}
				} else {
					throw new TaskExecutionFailedException("Username and Password not accepted by the server");
				}
			} else {
				throw new TaskExecutionFailedException("Cannot establish connection with server " + host + ":" + port);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("General exception with FTPClient");
		}

		logger.debug("Executed without errors " + this.getClass().getName());
	}

	@WfService(name = "fprint_update", version = "0.8.0")
	public void update(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// retrieve dynamic parameters
		String id = dParamsString.get("id");
		File resultFile = dParamsFile.get("resultFile");

		// update AIP
		try {
			// parse resultFile
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Node node = dbf.newDocumentBuilder().parse(resultFile);

			// get AIP
			AIP aip = DataManager.getInstance().getAIPByID(id);

			// update AIP
			aip.updateSection(node, "fprint");

			// release AIP
			DataManager.getInstance().releaseIP(aip);

			logger.debug("Updated FPrint section...");
		} catch (Exception e) {
			throw new TaskExecutionFailedException("Unable to update AIP...\n" + e.getMessage());
		}
	}
}
