/**
 * IPManagementTasks.java
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
package eu.prestoprime.plugin.p4;

import it.eurix.archtools.data.DataException;
import it.eurix.archtools.data.model.AIP;
import it.eurix.archtools.data.model.DIP.DCField;
import it.eurix.archtools.data.model.IPException;
import it.eurix.archtools.data.model.SIP;
import it.eurix.archtools.tool.ToolException;
import it.eurix.archtools.tool.ToolOutput;
import it.eurix.archtools.tool.impl.MessageDigestExtractor;
import it.eurix.archtools.workflow.exceptions.TaskExecutionFailedException;
import it.eurix.archtools.workflow.plugin.WfPlugin;
import it.eurix.archtools.workflow.plugin.WfPlugin.WfService;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.datamanagement.P4DataManager;

@WfPlugin(name = "P4Plugin")
public class IPManagementTasks {
	
	private static final Logger logger = LoggerFactory.getLogger(IPManagementTasks.class);
	
	@WfService(name = "validate_sip", version = "2.2.0")
	public void validateSIP(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get dynamic variables
		File sipFile = dParamsFile.get("sipFile");

		// parse the SIP
		String sipID;
		try {
			sipID = P4DataManager.getInstance().createNewSIP(sipFile);
		} catch (DataException e) {
			e.printStackTrace();
			logger.error("Unable to create new SIP...");
			throw new TaskExecutionFailedException("Unable to create new SIP...");
		}

		// send sipID to following tasks
		dParamsString.put("sipID", sipID);

		// get the SIP
		SIP sip = null;
		try {
			sip = P4DataManager.getInstance().getSIPByID(sipID);

			// check DC identifier in AIP_COLLECTION
			for (String identifier : sip.getDCField(DCField.identifier)) {
				String tmpID = P4DataManager.getInstance().getAIPByDCID(identifier);
				if (tmpID != null)
					throw new TaskExecutionFailedException("Found another AIP with same DC identifier...");
			}
			// check DC title
			if (sip.getDCField(DCField.title).size() == 0)
				throw new TaskExecutionFailedException("Invalid SIP, missing title...");
			for (String title : sip.getDCField(DCField.title)) {
				if (title.length() > 0)
					continue;
				else
					throw new TaskExecutionFailedException("Invalid SIP, empty title...");
			}
			// set DC date
			if (sip.getDCField(DCField.date).size() == 0) {
				List<String> dateList = new ArrayList<>();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // ISO 8601
				String date = df.format(new Date());
				dateList.add(date);
				sip.setDCField(DCField.date, dateList);
			}
		} catch (DataException e) {
			e.printStackTrace();
			logger.error("Unable to retrieve the just created SIP...");
			throw new TaskExecutionFailedException("Unable to retrieve the just created SIP...");
		} catch (IPException e) {
			e.printStackTrace();
			logger.error("Unable to retrieve DC identifier...");
			throw new TaskExecutionFailedException("Unable to retrieve DC identifier...");
		} finally {
			P4DataManager.getInstance().releaseIP(sip);
		}
	}

	@WfService(name = "verify_sip", version = "2.2.0")
	public void verifySIP(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get SIP id
		String sipID = dParamsString.get("sipID");

		// get SIP
		SIP sip = null;
		try {
			sip = P4DataManager.getInstance().getSIPByID(sipID);

			if (sip.getDCField(DCField.description).size() == 0) {
				List<String> description = new ArrayList<>();
				description.add("");
				sip.setDCField(DCField.description, description);
			}

			boolean rightsOnly = Boolean.parseBoolean(sParams.get("rights_only"));

			boolean hasAVMaterial = sip.hasAVMaterial();
			boolean hasRights = sip.hasRights();

			// check rights
			if (rightsOnly) {
				// rights only
				if (hasAVMaterial || !hasRights) {
					throw new TaskExecutionFailedException("SIP rights_only with AV material or without Rights...");
				}
			} else {
				// not rights only
				if (!hasAVMaterial) {
					throw new TaskExecutionFailedException("SIP not rights_only without AV material...");
				} else {

					List<String> formatList = sip.getAVFormats();
					String mqFormat = null;
					String mqFilePath = null;
					int numOfAVFiles = 0;

					// check SIP with only one valid MQ file
					for (String format : formatList) {

						if (sip.getAVMaterial(format, "FILE").size() != 1)
							throw new TaskExecutionFailedException("SIP with too few or too many AV files...");
						else {
							numOfAVFiles = numOfAVFiles + sip.getAVMaterial(format, "FILE").size();
							mqFormat = format;
							mqFilePath = sip.getAVMaterial(format, "FILE").get(0);
						}
					}

					if (numOfAVFiles != 1 && mqFormat != null && mqFilePath != null)
						throw new TaskExecutionFailedException("SIP with no suitable AV format or different master quality formats...");

					logger.debug("Number of AV files: " + numOfAVFiles + " MQ format: " + mqFilePath + " MQ path: " + mqFilePath);

					// compare computed checksum with the value from provider
					// (if any)
					String providerChecksum = sip.getChecksum(mqFormat, "MD5");

					logger.debug("Checksum from provider: " + providerChecksum);

					if (providerChecksum != null) {

						MessageDigestExtractor mde = new MessageDigestExtractor();
						ToolOutput<MessageDigestExtractor.AttributeType> output = mde.extract(mqFilePath);
						String computedChecksum = output.getAttribute(MessageDigestExtractor.AttributeType.MD5);

						if (!providerChecksum.equals(computedChecksum))
							throw new TaskExecutionFailedException("Invalid checksum from SIP provider or wrong file...");

					}

				}
			}
		} catch (DataException e) {
			e.printStackTrace();
		} catch (IPException e) {
			e.printStackTrace();
		} catch (ToolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			P4DataManager.getInstance().releaseIP(sip);
		}
	}

	@WfService(name = "consolidate_aip", version = "2.1.0")
	public void consolidateAIP(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get sipID
		String sipID = dParamsString.get("sipID");

		// consolidate
		try {
			SIP sip = P4DataManager.getInstance().getSIPByID(sipID);

			try {
				sip.purgeFiles();
			} catch (IPException e) {
				e.printStackTrace();
				P4DataManager.getInstance().releaseIP(sip);
				throw new TaskExecutionFailedException("Unable to purge files...");
			}

			dParamsString.put("result", sip.toString());

			P4DataManager.getInstance().consolidateSIP(sip);
			sip = null;

		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to consolidate the SIP...");
		}

		String aipID = sipID;
		AIP aip = null;
		try {
			aip = P4DataManager.getInstance().getAIPByID(aipID);

			aip.addPreservationEvent("INGESTION", "status=success");

		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve the just created AIP...");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to add a new preservation event...");
		} finally {
			P4DataManager.getInstance().releaseIP(aip);
		}
	}
}
