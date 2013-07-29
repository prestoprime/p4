/**
 * LTFSArchiverTasks.java
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
package eu.prestoprime.plugin.ltfsarchiver;

import it.eurix.archtools.data.DataException;
import it.eurix.archtools.data.model.DIP;
import it.eurix.archtools.data.model.IPException;
import it.eurix.archtools.data.model.SIP;
import it.eurix.archtools.workflow.exceptions.TaskExecutionFailedException;
import it.eurix.archtools.workflow.plugin.WfPlugin;
import it.eurix.archtools.workflow.plugin.WfPlugin.WfService;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.datamanagement.P4DataManager;
import eu.prestoprime.plugin.ltfsarchiver.client.LTFSClient;
import eu.prestoprime.plugin.ltfsarchiver.client.LTFSException;
import eu.prestoprime.plugin.ltfsarchiver.client.LTFSRequest;
import eu.prestoprime.plugin.ltfsarchiver.client.LTFSRequestBuilder;
import eu.prestoprime.plugin.ltfsarchiver.client.LTFSResponse;

@WfPlugin(name = "LTFSArchiverPlugin")
public class LTFSArchiverTasks {

	private static final Logger logger = LoggerFactory.getLogger(LTFSArchiverTasks.class);
	
	@WfService(name = "ltfs_first_copy", version = "2.0.0")
	public void firstCopy(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get static parameters
		String url = sParams.get("ltfs.url");
		String poolName = sParams.get("ltfs.poolA");

		// get dynamic parameters
		String sipID = dParamsString.get("sipID");

		SIP sip = null;
		try {
			sip = P4DataManager.getInstance().getSIPByID(sipID);

			// get MQ file
			String fileName = null;
			String[] MQformats = sParams.get("MQformats").split(",");

			logger.debug("Checking for MQ formats: " + MQformats);

			for (String format : MQformats) {
				List<String> videoFileList = sip.getAVMaterial(format, "FILE");
				if (videoFileList.size() > 0) {
					fileName = videoFileList.get(0);

					LTFSRequest request = LTFSRequestBuilder.newInstance(new URL(url)).write().from(fileName).pool(poolName).build();
					LTFSResponse response = new LTFSClient().execute(request);

					if (response.getCode() == 200) {
						sip.addFile(format, "LTFS-1", response.getMessage(), response.getParam(), 0);
					} else {
						throw new TaskExecutionFailedException("Unable to store file on LTFS. Error: " + response.getMessage());
					}

					break;
				}
			}
			if (fileName == null) {
				throw new TaskExecutionFailedException("Unable to find supported MQ format...");
			}
		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve the SIP...");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to add new File to SIP...");
		} catch (MalformedURLException | LTFSException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to execute LTFSRequest...");
		} finally {
			P4DataManager.getInstance().releaseIP(sip);
		}
	}
	
	@WfService(name = "ltfs_second_copy", version = "2.0.0")
	public void secondCopy(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get static parameters
		String URL = sParams.get("ltfs.url");
		String poolName = sParams.get("ltfs.poolB");

		// get dynamic parameters
		String sipID = dParamsString.get("sipID");

		SIP sip = null;
		try {
			sip = P4DataManager.getInstance().getSIPByID(sipID);

			// get MQ file
			String fileName = null;
			String[] MQformats = sParams.get("MQformats").split(",");

			logger.debug("Checking for MQ formats: " + MQformats.toString());

			for (String format : MQformats) {
				List<String> videoFileList = sip.getAVMaterial(format, "FILE");
				if (videoFileList.size() > 0) {
					fileName = videoFileList.get(0);

					LTFSRequest request = LTFSRequestBuilder.newInstance(new URL(URL)).write().from(fileName).pool(poolName).build();
					LTFSResponse response = new LTFSClient().execute(request);

					if (response.getCode() == 200) {
						sip.addFile(format, "LTFS-2", response.getMessage(), response.getParam(), 0);
					} else {
						throw new TaskExecutionFailedException("Unable to store file on LTFS. Error: " + response.getMessage());
					}

					break;
				}
			}
			if (fileName == null) {
				throw new TaskExecutionFailedException("Unable to find supported MQ format...");
			}
		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve the SIP...");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to add new File to SIP...");
		} catch (MalformedURLException | LTFSException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to execute LTFSRequest...");
		} finally {
			P4DataManager.getInstance().releaseIP(sip);
		}
	}
	
	@WfService(name = "ltfs_make_consumer_copy", version = "2.0.0")
	public void makeConsumerCopy(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		if (!Boolean.parseBoolean(dParamsString.get("isCopied"))) {
			// get static parameters
			String destVolume = sParams.get("dest.path.volume").trim();
			String destFolder = sParams.get("dest.path.folder").trim();
			String outputFolder = destVolume + File.separator + destFolder;
			String url = sParams.get("ltfs.url");

			// get dynamic parameters
			String dipID = dParamsString.get("id");
			String mimetype = dParamsString.get("mimetype");

			try {
				DIP dip = P4DataManager.getInstance().getDIPByID(dipID);

				// get MQ file
				if (mimetype != null) {
					List<String> videoFileList = dip.getAVMaterial(mimetype, "LTFS-1");
					if (videoFileList.size() > 0) {
						String videoFile = videoFileList.get(0);
						String targetFileName = dipID + videoFile.substring(videoFile.lastIndexOf("."));

						String outputFile = outputFolder + File.separator + targetFileName;

						LTFSRequest request = LTFSRequestBuilder.newInstance(new URL(url)).restore().from(videoFile).to(outputFile).build();
						LTFSResponse response = new LTFSClient().execute(request);

						if (response.getCode() == 200) {
							dParamsString.put("isCopied", "true");
							dParamsString.put("target.file.name", targetFileName);
						} else {
							throw new TaskExecutionFailedException("Unable to restore file from LTFS. Error: " + response.getMessage());
						}
					}
				}
			} catch (DataException e) {
				e.printStackTrace();
			} catch (IPException e) {
				e.printStackTrace();
			} catch (MalformedURLException | LTFSException e) {
				e.printStackTrace();
			}
		}
	}
	
	@WfService(name = "find_ltfs_master_file", version = "2.2.0")
	public void findMaster(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		String sourceFilePath = dParamsString.get("source.file.path");
		if (sourceFilePath == null) {
			// not set yet, find local master quality FILE

			String dipID = dParamsString.get("dipID");

			DIP dip = null;
			try {
				dip = P4DataManager.getInstance().getDIPByID(dipID);

				String[] MQFormats = sParams.get("MQformats").split(",");
				for (String MQFormat : MQFormats) {
					List<String> AVMaterialList = dip.getAVMaterial(MQFormat, "LTFS-1");
					if (AVMaterialList.size() > 0) {
						String AVMaterial = AVMaterialList.get(0);

						logger.debug("Found local MQ file: " + AVMaterial + "...");
						dParamsString.put("source.file.path", AVMaterial);
						dParamsString.put("source.file.type", "LTFS-1");
						dParamsString.put("source.file.mimetype", MQFormat);

						break;
					}
				}
			} catch (DataException e) {
				e.printStackTrace();
			} catch (IPException e) {
				e.printStackTrace();
			}
		}
	}
	
	@WfService(name = "ltfs_make_available_mount", version = "2.2.0")
	public void mount(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> arg2) throws TaskExecutionFailedException {

		String sourceFileType = dParamsString.get("source.file.type");

		if (sourceFileType.contains("LTFS")) {

			String url = sParams.get("ltfs.url");
			String sourceFilePath = dParamsString.get("source.file.path");

			try {

				String tapeID = sourceFilePath.split(":")[1];

				LTFSClient c = new LTFSClient();
				LTFSRequest request = LTFSRequestBuilder.newInstance(new URL(url)).makeAvailable().mount().tapeID(tapeID).build();
				LTFSResponse response = c.execute(request);

				if (response.getCode() == 200) {

					String LTFSVolume = sParams.get("ltfs.volume");
					String pathOnLTO = sourceFilePath.split(":")[2];

					sourceFilePath = LTFSVolume + File.separator + tapeID + File.separator + pathOnLTO;

					dParamsString.put("source.file.path", sourceFilePath);
					dParamsString.put("tape.to.unmount", tapeID);

					logger.debug("Set source file path on LTFS to " + sourceFilePath + "...");
				} else {
					throw new TaskExecutionFailedException("Unable to unmount the tape " + tapeID + " - " + response.getCode() + " error...");
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Bad LTFSArchiver URL in wfDescriptor...");
			} catch (LTFSException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to mount tape - General error...");
			}
		}
	}
	
	@WfService(name = "ltfs_make_available_unmount", version = "2.2.0")
	public void unmount(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> arg2) throws TaskExecutionFailedException {

		String tapeID = dParamsString.get("tape.to.unmount");

		if (tapeID != null) {

			String url = sParams.get("ltfs.url");

			try {
				LTFSClient c = new LTFSClient();
				LTFSRequest request = LTFSRequestBuilder.newInstance(new URL(url)).makeAvailable().unmount().tapeID(tapeID).build();
				LTFSResponse response = c.execute(request);

				if (response.getCode() != 200) {
					throw new TaskExecutionFailedException("Unable to unmount the tape " + tapeID + " - " + response.getCode() + " error...");
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Bad LTFSArchiver URL in wfDescriptor...");
			} catch (LTFSException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to unmount the tape " + tapeID + " - General error...");
			}
		}
	}
}
