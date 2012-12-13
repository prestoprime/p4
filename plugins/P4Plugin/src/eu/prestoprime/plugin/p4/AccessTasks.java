/**
 * AccessTasks.java
 * Author: Francesco Rosso (rosso@eurix.it)
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
package eu.prestoprime.plugin.p4;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.conf.ConfigurationManager;
import eu.prestoprime.conf.ConfigurationManager.P4Property;
import eu.prestoprime.datamanagement.DataException;
import eu.prestoprime.datamanagement.DataManager;
import eu.prestoprime.model.oais.DIP;
import eu.prestoprime.model.oais.IPException;
import eu.prestoprime.plugin.p4.tools.FFmbc;
import eu.prestoprime.tools.ToolException;
import eu.prestoprime.workflow.exceptions.TaskExecutionFailedException;
import eu.prestoprime.workflow.plugin.WfPlugin;
import eu.prestoprime.workflow.plugin.WfPlugin.WfService;

@WfPlugin(name = "P4Plugin")
public class AccessTasks {

	private static final Logger logger = LoggerFactory.getLogger(AccessTasks.class);
	
	@WfService(name = "make_consumer_segment", version = "2.2.0")
	public void execute(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamFile) throws TaskExecutionFailedException {

		if (!Boolean.parseBoolean(dParamsString.get("isSegmented"))) {

			// retrieve static params
			String destVolume = sParams.get("dest.path.volume").trim();
			String destFolder = sParams.get("dest.path.folder").trim();

			// retrieve dynamic params
			String dipID = dParamsString.get("dipID");
			String inputFilePath = dParamsString.get("source.file.path");
			String mimeType = dParamsString.get("source.file.mimetype");
			int startFrame = Integer.parseInt(dParamsString.get("start.frame"));
			int stopFrame = Integer.parseInt(dParamsString.get("stop.frame"));

			String outputFolder = destVolume + File.separator + destFolder;

			try {

				if (dipID == null)
					throw new TaskExecutionFailedException("Missing AIP ID to extract fragment");

				if (mimeType == null)
					throw new TaskExecutionFailedException("Missing MIME Type to extract fragment");

				DIP dip = DataManager.getInstance().getDIPByID(dipID);

				if (inputFilePath == null) {

					// trying to retrieve file path from DIP, last chance...

					List<String> videoFileList = dip.getAVMaterial(mimeType, "FILE");

					if (videoFileList.size() == 0) {

						throw new TaskExecutionFailedException("Missing input file path to extract fragment");

					} else {

						inputFilePath = videoFileList.get(0);

					}

				}

				File targetDir = new File(outputFolder);
				targetDir.mkdirs();

				if (!targetDir.canWrite())
					throw new TaskExecutionFailedException("Unable to write to output dir " + outputFolder);

				String targetFileName = dipID + "." + startFrame + "." + stopFrame + ".mxf";
				File targetFile = new File(targetDir, targetFileName);

				int startSec = startFrame / 25;
				int endSec = stopFrame / 25;
				int durationSec = endSec - startSec;

				String start = Integer.toString(startSec);
				String duration = Integer.toString(durationSec);

				List<String> formats = dip.getAVFormats();

				StringBuilder formatSB = new StringBuilder();

				for (String format : formats) {

					formatSB.append(format + "\t");

				}

				// extract fragment using ffmbc
				FFmbc ffmbc = new FFmbc();
				ffmbc.extractSegment(inputFilePath, targetFile.getAbsolutePath(), start, duration, mimeType, formatSB.toString(), "2");

				dParamsString.put("isSegmented", "true");
				dParamsString.put("segment.file.path", targetFileName);

				logger.debug("Consumer copy available at: " + targetFile.getAbsolutePath());

			} catch (DataException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to retrieve DIP with id: " + dipID);
			} catch (IPException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to retrieve MQ file");
			} catch (ToolException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to extract segment with FFMBC");
			}
		}
	}
	
	@WfService(name = "check_consumer_segment", version = "2.0.0")
	public void checkConsumerSegment(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		if (Boolean.parseBoolean(dParamsString.get("isSegmented"))) {

			String p4url = ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_URL);

			String mimeType = dParamsString.get("source.file.mimetype");
			String targetFileName = dParamsString.get("segment.file.path");
			String destVolume = sParams.get("dest.path.volume").trim();
			String destFolder = sParams.get("dest.path.folder").trim();
			String outputFolder = destVolume + File.separator + destFolder;

			try {
				JSONObject json = new JSONObject();

				json.put("file", outputFolder + File.separator + targetFileName);
				json.put("CIFS", "//" + new URL(p4url).getHost() + "/" + destFolder);
				json.put("URL", p4url + "/" + destFolder + "/" + targetFileName);
				json.put("mimetype", mimeType);

				dParamsString.put("result", json.toString());
			} catch (JSONException e) {
				dParamsString.put("result", "Unable to print output file information...");
			} catch (MalformedURLException e) {
				throw new TaskExecutionFailedException("Malformed URL...");
			}
		} else {
			throw new TaskExecutionFailedException("Unable to find any file with requested mimetype...");
		}
	}
}
