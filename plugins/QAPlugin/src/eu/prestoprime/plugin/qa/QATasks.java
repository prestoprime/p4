/**
 * QATasks.java
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
package eu.prestoprime.plugin.qa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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

@WfPlugin(name = "QAPlugin")
public class QATasks {

	private static final Logger logger = LoggerFactory.getLogger(QATasks.class);
	
	@WfService(name = "qa_upload", version = "0.8.0")
	public void upload(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// retrieve dynamic params
		String dipID = dParamsString.get("id");

		// retrieve static params
		String baseUNC = sParams.get("baseUNC");

		try {
			// get AIP
			DIP dip = DataManager.getInstance().getDIPByID(dipID);

			// get MQ file
			String videoFile = null;
			String[] MQformats = sParams.get("MQformats").split(",");
			logger.debug("Checking for MQ formats: " + MQformats.toString());
			for (String format : MQformats) {
				List<String> videoFileList = dip.getAVMaterial(format, "FILE");
				if (videoFileList.size() > 0) {
					videoFile = videoFileList.get(0);
					break;
				}
			}

			if (videoFile == null) {
				throw new TaskExecutionFailedException("Unable to find supported MQ format...");
			}

			// compute mediaURI
			File file = new File(videoFile);
			if (!file.isFile())
				throw new TaskExecutionFailedException("File retrieved from AIP is not a file...");

			String fileName = file.getName();
			String mediaURI = baseUNC + "\\" + dipID + "\\videos\\" + fileName;

			// send mediaURI to following task
			// (eu.prestoprime.plugin.qaplugin.ExecuteQATask)
			dParamsString.put("mediaURI", mediaURI);

			logger.info("mediaURI = " + mediaURI);

		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve AIP with ID: " + dipID);
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve AV material location...");
		}
	}

	public static enum PROFILES {
		DefaultProfile
	};

	public static enum STATUS {
		idle, queued, processing, paused, cancelled, failed, finished
	};

	@WfService(name = "qa_execute", version = "0.8.0")
	public void execute(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// retrieve static parameters
		String host = sParams.get("host");

		// retrieve dynamic parameters
		String mediaURI = dParamsString.get("mediaURI");
		PROFILES analysisProfileName = PROFILES.DefaultProfile;
		File resultFile;
		try {
			resultFile = File.createTempFile("qa-result", ".tmp");
		} catch (IOException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to create temp file");
		}

		String path, line, params, id;
		DefaultHttpClient client;
		HttpUriRequest request;
		HttpEntity entity;
		BufferedReader reader;

		// interact with QA service
		main: do {
			try {
				path = host + "/service/numberOfFreeSlots";
				request = new HttpGet(path);
				client = new DefaultHttpClient();
				entity = client.execute(request).getEntity();
				reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				if ((line = reader.readLine()) != null) {
					if (Integer.parseInt(line) > 0) {
						logger.debug("Free slot found, requesting... - L1");

						path = host + "/job";
						request = new HttpPost(path);
						params = "mediaUri=" + mediaURI;
						params += "\nanalisysProfileName=" + analysisProfileName.toString();
						((HttpPost) request).setEntity(new StringEntity(params));
						entity = client.execute(request).getEntity();
						reader = new BufferedReader(new InputStreamReader(entity.getContent()));
						if ((line = reader.readLine()) != null) {
							if (!line.startsWith("error")) {
								id = line;
								logger.debug("Requested, ID: " + id + " - L2");

								path = host + "/job/" + id + "/";
								HttpPut put = new HttpPut(path);
								StringEntity entity2 = new StringEntity("control: start");
								put.setEntity(entity2);
								entity = client.execute(put).getEntity();
								reader = new BufferedReader(new InputStreamReader(entity.getContent()));
								if ((line = reader.readLine()) != null) {
									if (line.equals("success")) {
										do {
											path = host + "/job/" + id + "/status";
											request = new HttpGet(path);
											entity = client.execute(request).getEntity();
											reader = new BufferedReader(new InputStreamReader(entity.getContent()));
											if ((line = reader.readLine()) != null) {
												line = line.toLowerCase();
												switch (STATUS.valueOf(line)) {
												case cancelled:
													logger.debug("Cancelled - L3");
													throw new TaskExecutionFailedException("Request unexpectly cancelled...");

												case failed:
													throw new TaskExecutionFailedException("Processing failed...");

												case finished:
													logger.debug("Finished!! Downloading results - L4");

													// download mpeg7 result
													path = host + "/job/" + id + "/result/mpeg7";
													request = new HttpGet(path);
													entity = client.execute(request).getEntity();

													InputStream is = entity.getContent();
													OutputStream os = new FileOutputStream(resultFile);
													byte[] buf = new byte[1024];
													int len;
													while ((len = is.read(buf)) > 0)
														os.write(buf, 0, len);
													os.close();
													is.close();

													logger.debug("Saved result, deleting from QA server...");

													path = host + "/job/" + id + "/";
													request = new HttpDelete(path);
													entity = client.execute(request).getEntity();
													reader = new BufferedReader(new InputStreamReader(entity.getContent()));
													if ((line = reader.readLine()) != null) {
														if (line.equals("success")) {
															break main;
														}
													}

												default:
													logger.debug("Not finished yet, waiting... - L4");
													try {
														Thread.sleep(5000);
													} catch (Exception e) {
														e.printStackTrace();
													}
													continue;
												}
											}
										} while (true);
									} else {
										logger.debug("Error in the PUT method - L3");
									}
								}
							} else {
								logger.debug(line + " - L2");
							}
						}
					} else {
						logger.debug("No free slot, waiting... - L1");
						try {
							Thread.sleep(5000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						continue;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		} while (true);

		// send file to following task
		dParamsFile.put("resultFile", resultFile);

		logger.debug("ResultFile: " + resultFile.getAbsolutePath());
	}

	@WfService(name = "qa_update_auto", version = "0.8.0")
	public void autoUpdate(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

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
			aip.updateSection(node, "qa_auto");

			logger.debug("Updated QA section...");
		} catch (Exception e) {
			throw new TaskExecutionFailedException("Unable to update AIP...\n" + e.getMessage());
		} finally {
			// release AIP
			DataManager.getInstance().releaseIP(aip);
		}
	}

	@WfService(name = "qa_update_manual", version = "0.8.0")
	public void manualUpdate(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

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
