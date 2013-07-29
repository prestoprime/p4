/**
 * UserMDTasks.java
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
package eu.prestoprime.plugin.usermd;

import it.eurix.archtools.data.DataException;
import it.eurix.archtools.data.model.AIP;
import it.eurix.archtools.data.model.DIP;
import it.eurix.archtools.data.model.DIP.DCField;
import it.eurix.archtools.data.model.IPException;
import it.eurix.archtools.workflow.exceptions.TaskExecutionFailedException;
import it.eurix.archtools.workflow.plugin.WfPlugin;
import it.eurix.archtools.workflow.plugin.WfPlugin.WfService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import eu.prestoprime.datamanagement.P4DataManager;

@WfPlugin(name = "UserMDPlugin")
public class UserMDTasks {

	private static final Logger logger = LoggerFactory.getLogger(UserMDTasks.class);
	
	@WfService(name = "usermd_upload", version = "0.2.1")
	public void upload(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {
		logger.debug("Called " + this.getClass().getName());

		// prepare dynamic variables
		String dipID = dParamsString.get("dipID");

		// prepare static variables
		String host = sParams.get("host");

		// retrieve DIP
		try {
			DIP dip = P4DataManager.getInstance().getDIPByID(dipID);
			List<String> fLocatList = dip.getAVMaterial("video/webm", "FILE");
			String fileLocation = fLocatList.get(0);

			logger.debug("Found video/webm location: " + fileLocation);

			// create JSON object
			JSONObject video = new JSONObject();
			video.put("title", dip.getDCField(DCField.title).get(0));
			video.put("playerType", "JW");
			video.put("sourceUrl", dip.getAVMaterial("video/webm", "URL").get(0));
			video.put("fragmentID", dipID);
			video.put("imageUrl", dip.getThumbnail());
			video.put("enabled", true);
			video.put("startTime", 0);
			video.put("duration", dip.getDuration());

			JSONArray ja = new JSONArray();
			ja.put(video);

			// upload file
			System.out.println(ja);

			String path = "http://" + host + "/add";
			HttpClient client = new DefaultHttpClient();
			HttpUriRequest request = new HttpPost(path);
			((HttpPost) request).setHeader("Content-Type", "application/json");
			((HttpPost) request).setEntity(new StringEntity(ja.toString()));
			HttpEntity entity = client.execute(request).getEntity();

			StringBuffer sb = new StringBuffer();
			if (entity != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
			}

			JSONObject response = new JSONObject(sb.toString());
			switch (response.getString("status")) {
			case "ok":
				dParamsString.put("result", response.toString());
				break;
			case "error":
			default:
				throw new TaskExecutionFailedException("Unable to add new fragmentID... " + response.toString());
			}
		} catch (DataException | IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve the fileLocation of the Master Quality...");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to create the JSON object...");
		} catch (IOException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to send/recv...");
		}
	}

	@WfService(name = "usermd_update", version = "0.2.5")
	public void execute(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// retrieve dynamic parameters
		String id = dParamsString.get("id");
		File resultFile = dParamsFile.get("resultFile");

		// update AIP
		AIP aip = null;
		try {
			// get AIP
			aip = P4DataManager.getInstance().getAIPByID(id);

			// parse resultFile
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Node node = dbf.newDocumentBuilder().parse(resultFile);

			// update AIP
			aip.updateSection(node, "usermd");

			logger.debug("Updated UserMD section...");

			dParamsString.put("sipID", id);
		} catch (Exception e) {
			throw new TaskExecutionFailedException("Unable to update AIP...\n" + e.getMessage());
		} finally {
			P4DataManager.getInstance().releaseIP(aip);
		}
	}
}
