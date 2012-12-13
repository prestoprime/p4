/**
 * CopyTasks.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
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
import eu.prestoprime.model.oais.SIP;
import eu.prestoprime.tools.MessageDigestExtractor;
import eu.prestoprime.tools.ToolException;
import eu.prestoprime.workflow.exceptions.TaskExecutionFailedException;
import eu.prestoprime.workflow.plugin.WfPlugin;
import eu.prestoprime.workflow.plugin.WfPlugin.WfService;

@WfPlugin(name = "P4Plugin")
public class CopyTasks {

	private static final Logger logger = LoggerFactory.getLogger(CopyTasks.class);

	@WfService(name = "configure_storage", version = "1.0.0")
	public void configureStorage(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		String sipID = dParamsString.get("sipID");
		if (sipID == null)
			sipID = dParamsString.get("aipID");

		String p4storeFolder = ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_STORAGE_VOLUME) + File.separator + ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_STORAGE_FOLDER) + File.separator + sipID;
		dParamsString.put("p4storeFolder", p4storeFolder);

		String videosFolder = p4storeFolder + File.separator + ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_VIDEOS_FOLDER);
		new File(videosFolder).mkdirs();
		dParamsString.put("videosFolder", videosFolder);

		String framesFolder = p4storeFolder + File.separator + ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_FRAMES_FOLDER);
		new File(framesFolder).mkdirs();
		dParamsString.put("framesFolder", framesFolder);

		String graphFolder = p4storeFolder + File.separator + ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_GRAPH_FOLDER);
		new File(graphFolder).mkdirs();
		dParamsString.put("graphFolder", graphFolder);
	}

	@WfService(name = "master_file_first_copy", version = "2.0.0")
	public void masterFileFirstCopy(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get sipID
		String sipID = dParamsString.get("sipID");

		SIP sip = null;
		try {
			// get sip
			sip = DataManager.getInstance().getSIPByID(sipID);

			String inputVideo = null;

			// get MQ file
			String[] formats = sParams.get("MQformats").split(",");
			for (String format : formats) {

				List<String> videoFileList = sip.getAVMaterial(format, "FILE");
				if (videoFileList.size() > 0) {
					inputVideo = videoFileList.get(0);
					String outputVideoName = inputVideo.substring(inputVideo.lastIndexOf(File.separator) + 1);
					String outputVideo = dParamsString.get("videosFolder") + File.separator + outputVideoName;

					File inputFile = new File(inputVideo);
					File outputFile = new File(outputVideo);

					if (!outputFile.exists()) {
						outputFile.createNewFile();
					}
					FileChannel source = new FileInputStream(inputFile).getChannel();
					FileChannel destination = new FileOutputStream(outputFile).getChannel();
					destination.transferFrom(source, 0, source.size());

					// MD5
					MessageDigestExtractor mde = new MessageDigestExtractor();
					mde.extract(outputVideo);
					String md5sum = mde.getAttributeByName("MD5");

					// update SIP
					sip.addFile(format, "FILE", outputVideo, md5sum, new File(outputVideo).length());

					break;
				}
			}
			if (inputVideo == null) {
				throw new TaskExecutionFailedException("Unable to find supported MQ format...");
			}
		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve the SIP...");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve MQ file from SIP...");
		} catch (IOException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to copy MQ file...");
		} catch (ToolException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to compute MD5 for WebM video file...");
		} finally {
			DataManager.getInstance().releaseIP(sip);
		}
	}

	@WfService(name = "master_file_second_copy", version = "2.0.0")
	public void masterFileSecondCopy(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get sipID
		String sipID = dParamsString.get("sipID");

		SIP sip = null;
		try {
			// get sip
			sip = DataManager.getInstance().getSIPByID(sipID);

			String inputVideo = null;

			// get MQ file
			String[] formats = sParams.get("MQformats").split(",");
			for (String format : formats) {

				List<String> videoFileList = sip.getAVMaterial(format, "FILE");
				if (videoFileList.size() > 0) {
					inputVideo = videoFileList.get(0);

					String outputFolder = sParams.get("second.local.copy.folder") + File.separator + sipID;
					new File(outputFolder).mkdirs();
					String outputVideoName = inputVideo.substring(inputVideo.lastIndexOf(File.separator) + 1);
					String outputVideo = outputFolder + File.separator + outputVideoName;

					File inputFile = new File(inputVideo);
					File outputFile = new File(outputVideo);

					if (!outputFile.exists()) {
						outputFile.createNewFile();
					}
					FileChannel source = new FileInputStream(inputFile).getChannel();
					FileChannel destination = new FileOutputStream(outputFile).getChannel();
					destination.transferFrom(source, 0, source.size());

					// MD5
					MessageDigestExtractor mde = new MessageDigestExtractor();
					mde.extract(outputVideo);
					String md5sum = mde.getAttributeByName("MD5");

					// update SIP
					sip.addFile(format, "FILE-BCK", outputVideo, md5sum, new File(outputVideo).length());

					break;
				}
			}
			if (inputVideo == null) {
				throw new TaskExecutionFailedException("Unable to find supported MQ format...");
			}
		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve the SIP...");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve MQ file from SIP...");
		} catch (IOException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to copy MQ file...");
		} catch (ToolException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to compute MD5 for WebM video file...");
		} finally {
			DataManager.getInstance().releaseIP(sip);
		}
	}

	@WfService(name = "find_local_master_file", version = "2.2.0")
	public void findLocalMasterFile(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		String sourceFilePath = dParamsString.get("source.file.path");
		if (sourceFilePath == null) {
			// not set yet, find local master quality FILE

			String dipID = dParamsString.get("dipID");

			DIP dip = null;
			try {
				dip = DataManager.getInstance().getDIPByID(dipID);

				String[] MQFormats = sParams.get("MQformats").split(",");
				for (String MQFormat : MQFormats) {
					List<String> AVMaterialList = dip.getAVMaterial(MQFormat, "FILE");
					if (AVMaterialList.size() > 0) {
						String AVMaterial = AVMaterialList.get(0);
						AVMaterial = AVMaterial.replace(ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_PLACEHOLDER), ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_STORAGE_VOLUME));

						logger.debug("Found local MQ file: " + AVMaterial + "...");
						dParamsString.put("source.file.path", AVMaterial);
						dParamsString.put("source.file.type", "FILE");
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

	@WfService(name = "make_consumer_copy", version = "2.0.0")
	public void makeConsumerCopy(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamFile) throws TaskExecutionFailedException {

		if (!Boolean.parseBoolean(dParamsString.get("isCopied"))) {

			// retrieve static params
			String destVolume = sParams.get("dest.path.volume").trim();
			String destFolder = sParams.get("dest.path.folder").trim();

			// retrieve dynamic params
			String dipID = dParamsString.get("id");
			String mimetype = dParamsString.get("mimetype");

			String outputFolder = destVolume + File.separator + destFolder;

			try {
				DIP dip = DataManager.getInstance().getDIPByID(dipID);

				// get MQ file
				String videoFile = null;

				if (mimetype != null) {
					List<String> videoFileList = dip.getAVMaterial(mimetype, "FILE");

					if (videoFileList.size() > 0) {
						videoFile = videoFileList.get(0);

						File sourceFile = new File(videoFile);

						if (!sourceFile.canRead() || !sourceFile.isFile())
							throw new TaskExecutionFailedException("Unable to read source video file...");

						File targetDir = new File(outputFolder);
						targetDir.mkdirs();

						if (!targetDir.canWrite())
							throw new TaskExecutionFailedException("Unable to write to output dir " + outputFolder);

						String targetFileName = dipID + videoFile.substring(videoFile.lastIndexOf("."));
						File targetFile = new File(outputFolder, targetFileName);

						FileInputStream in = new FileInputStream(sourceFile);
						FileOutputStream out = new FileOutputStream(targetFile);

						byte[] buffer = new byte[4096];
						int bytesRead;
						while ((bytesRead = in.read(buffer)) != -1)
							out.write(buffer, 0, bytesRead);

						in.close();
						out.close();

						dParamsString.put("isCopied", "true");
						dParamsString.put("target.file.name", targetFileName);

						logger.debug("Consumer copy available at: " + targetFile.getAbsolutePath());
					}
				}
			} catch (DataException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to retrieve DIP with id: " + dipID);
			} catch (IPException | FileNotFoundException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to retrieve MQ file");
			} catch (IOException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to create consumer copy");
			}
		}
	}

	@WfService(name = "check_consumer_copy", version = "2.0.5")
	public void checkConsumerCopy(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		if (Boolean.parseBoolean(dParamsString.get("isCopied"))) {
			String dipID = dParamsString.get("id");
			String mimeType = dParamsString.get("mimetype");

			String p4url = ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_URL);
			String targetFileName = dParamsString.get("target.file.name");
			String destVolume = sParams.get("dest.path.volume").trim();
			String destFolder = sParams.get("dest.path.folder").trim();
			String outputFolder = destVolume + File.separator + destFolder;

			try {
				DIP dip = DataManager.getInstance().getDIPByID(dipID);

				JSONObject json = new JSONObject();

				json.put("file", targetFileName);
				json.put("CIFS", "//" + new URL(p4url).getHost() + "/" + destFolder);
				json.put("URL", p4url + "/" + destFolder + "/" + targetFileName);

				String MD5 = dip.getChecksum(mimeType, "MD5");
				if (MD5 != null) {
					File targetFile = new File(outputFolder, targetFileName);
					MessageDigestExtractor mde = new MessageDigestExtractor();
					mde.extract(targetFile.getAbsolutePath());
					String currentMD5 = mde.getAttributeByName("MD5");
					if (!currentMD5.equals(MD5))
						throw new TaskExecutionFailedException("MD5 doesn't correspond...");
					else
						json.put("MD5", MD5);
				}

				dParamsString.put("result", json.toString());
			} catch (JSONException e) {
				dParamsString.put("result", "Unable to print output file information...");
			} catch (DataException | IPException e) {
				throw new TaskExecutionFailedException("Unable to retrieve the DIP...");
			} catch (MalformedURLException e) {
				throw new TaskExecutionFailedException("Malformed URL...");
			} catch (ToolException e) {
				throw new TaskExecutionFailedException("Unable to extract the checksum...");
			}
		} else {
			throw new TaskExecutionFailedException("Unable to find any file with requested mimetype...");
		}
	}
}
