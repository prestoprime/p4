/**
 * MServeTasks.java
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
package eu.prestoprime.plugin.mserve;

import it.eurix.archtools.data.DataException;
import it.eurix.archtools.data.model.DIP.DCField;
import it.eurix.archtools.data.model.IPException;
import it.eurix.archtools.data.model.SIP;
import it.eurix.archtools.tool.ToolException;
import it.eurix.archtools.tool.ToolOutput;
import it.eurix.archtools.tool.impl.MessageDigestExtractor;
import it.eurix.archtools.workflow.exceptions.TaskExecutionFailedException;
import it.eurix.archtools.workflow.plugin.WfPlugin;
import it.eurix.archtools.workflow.plugin.WfPlugin.WfService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.conf.ConfigurationManager;
import eu.prestoprime.datamanagement.P4DataManager;
import eu.prestoprime.model.dnx.Dnx;
import eu.prestoprime.model.dnx.Key;
import eu.prestoprime.model.dnx.Record;
import eu.prestoprime.model.dnx.Section;
import eu.prestoprime.plugin.mserve.client.MServeClient;
import eu.prestoprime.plugin.mserve.client.MServeClient.MServeTask;
import eu.prestoprime.plugin.mserve.client.MServeException;

@WfPlugin(name = "MServePlugin")
public class MServeTasks {

	private static final Logger logger = LoggerFactory.getLogger(MServeTasks.class);
	
	@WfService(name = "create_mserve_service", version = "1.0.0")
	public void createService(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamFile) throws TaskExecutionFailedException {

		// retrieve static parameters
		String tingServer = sParams.get("ting.server");
		String slaTemplate = sParams.get("ting.sla.template");// serviceFactory
		String resourceManager = sParams.get("ting.resource.manager");// cap

		// retrieve dynamic parameters
		String userID = dParamsString.get("userID");

		// create new SLA
		String uri = null;
		try {
			URL url = new URL(tingServer + "/resourcemanager/slas?cap=" + resourceManager);
			HttpClient client = new DefaultHttpClient();
			HttpUriRequest request = new HttpPost(url.toString());

			List<NameValuePair> parameters = new ArrayList<>();
			parameters.add(new BasicNameValuePair("name", "P4-" + System.currentTimeMillis()));
			parameters.add(new BasicNameValuePair("serviceFactory", slaTemplate));
			UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(parameters);

			((HttpEntityEnclosingRequestBase) request).setEntity(requestEntity);

			logger.debug(request.getRequestLine().toString());
			logger.debug(request.toString());

			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					String line;
					StringBuffer sb = new StringBuffer();
					while ((line = reader.readLine()) != null)
						sb.append(line);
					JSONObject json = new JSONObject(sb.toString());

					logger.debug(json.toString());

					uri = json.getString("uri");
				}
			} else {
				throw new TaskExecutionFailedException("Ting error...");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		logger.debug("URI: " + uri);

		if (uri == null) {
			throw new TaskExecutionFailedException("URI null");
		}

		String mserveURL = null;
		try {
			String sla = uri.split("#")[1];

			logger.debug(sla);

			URL url = new URL(tingServer + "/slas/sla?cap=" + sla);
			HttpClient client = new DefaultHttpClient();
			HttpUriRequest request = new HttpGet(url.toString());
			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					String line;
					StringBuffer sb = new StringBuffer();
					while ((line = reader.readLine()) != null)
						sb.append(line);
					JSONObject json = new JSONObject(sb.toString());

					logger.debug(json.toString());

					mserveURL = json.getString("browse");
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		logger.debug(mserveURL);

		if (mserveURL == null) {
			throw new TaskExecutionFailedException("mserveURL null");
		}

		String[] fields = mserveURL.split("/");
		String mserveID = fields[fields.length - 1];

		logger.debug(mserveID);

		ConfigurationManager.getUserInstance().addUserService(userID, "mserve", mserveID);
	}

	@WfService(name = "mserve_file_upload", version = "1.0.0")
	public void upload(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> arg2) throws TaskExecutionFailedException {

		// get static parameters
		String url = sParams.get("mserve.url");

		// get dynamic parameters
		String sipID = dParamsString.get("sipID");
		String userID = dParamsString.get("userID");

		// get serviceID
		String serviceID = ConfigurationManager.getUserInstance().getService(userID, "mserve");

		SIP sip = null;
		try {
			sip = P4DataManager.getInstance().getSIPByID(sipID);

			File videoFile = new File(sip.getAVMaterial("application/mxf", "FILE").get(0));
			if (videoFile.isFile()) {
				String fileID = new MServeClient(url, serviceID).uploadFile(videoFile);
				dParamsString.put("mserveFileID", fileID);
			}
		} catch (DataException | IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve the SIP...");
		} catch (MServeException e) {
			throw new TaskExecutionFailedException(e.getMessage());
		} finally {
			P4DataManager.getInstance().releaseIP(sip);
		}
	}
	
	@WfService(name = "mserve_ffprobe", version = "1.1.0")
	public void ffprobe(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamFile) throws TaskExecutionFailedException {

		// get static parameters
		String url = sParams.get("mserve.url");

		// get dynamic parameters
		String sipID = dParamsString.get("sipID");
		String fileID = dParamsString.get("mserveFileID");
		String userID = dParamsString.get("userID");

		// get serviceID
		String serviceID = ConfigurationManager.getUserInstance().getService(userID, "mserve");

		SIP sip = null;
		try {
			// get sip
			sip = P4DataManager.getInstance().getSIPByID(sipID);

			// run FFprobe
			MServeClient cl = new MServeClient(url, serviceID);
			Map<String, String> params = new HashMap<>();
			params.put("format", "json");
			File output = cl.runMServeTask(fileID, MServeTask.ffprobe, params);

			logger.debug("Completed FFProbe Task on MServe for fileID: " + fileID);

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(output)));
			String line;
			StringBuffer sb = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			logger.debug("Appended output FFProbe...");

			// get JSON Object
			JSONObject json = new JSONObject(sb.toString());

			logger.debug("Parsed JSONObject response...");

			Section section = new Section();
			section.setId("ffprobe");

			// FFprobe show format
			JSONObject jsonFormat = json.getJSONObject("format");

			logger.debug("Parsed JSONObject Format...");

			Iterator<String> itFormat = jsonFormat.keys();

			while (itFormat.hasNext()) {
				String key = itFormat.next();

				logger.debug("key: " + key);

				String value = jsonFormat.getString(key);

				logger.debug(" value: " + value);

				Record record = new Record();
				Key keyType = new Key();
				keyType.setId("significantPropertiesType");
				keyType.setValue("ffprobe:" + key);
				record.getKey().add(keyType);
				Key keyValue = new Key();
				keyValue.setId("significantPropertiesValue");
				keyValue.setValue(value);
				record.getKey().add(keyValue);
				section.getRecord().add(record);
			}

			// set format
			String formatN = jsonFormat.getString("format_name");
			String formatLN = jsonFormat.getString("format_long_name");

			// size
			String size = jsonFormat.getString("size");

			dParamsString.put("isMXF", "false");
			if (formatN != null) {
				synchronized (sip) {
					List<String> formats = sip.getDCField(DCField.format);
					formats.add(formatLN + "(" + formatN.toUpperCase() + ")");
					sip.setDCField(DCField.format, formats);
				}

				if (formatN.equalsIgnoreCase("MXF")) {
					dParamsString.put("isMXF", "true");
				}
			}

			// FFprobe show streams
			JSONArray jsonStreams = json.getJSONArray("streams");

			String secDuration = null;
			String frameRate = null;
			int numOfStreams = jsonStreams.length();

			for (int i = 0; i < jsonStreams.length(); i++) {

				JSONObject jsonStream = jsonStreams.getJSONObject(i);
				String index = jsonStream.getString("index");

				logger.debug("Parsing index " + index);

				Iterator<String> itStream = jsonStream.keys();

				while (itStream.hasNext()) {
					String key = itStream.next();
					String value = jsonStream.getString(key);

					Record record = new Record();
					Key keyType = new Key();
					keyType.setId("significantPropertiesType");
					keyType.setValue("ffprobe:" + key + "_" + index);
					record.getKey().add(keyType);
					Key keyValue = new Key();
					keyValue.setId("significantPropertiesValue");
					keyValue.setValue(value);
					record.getKey().add(keyValue);
					section.getRecord().add(record);
				}

				if (jsonStream.getString("codec_type").equals("video")) {

					secDuration = jsonStream.getString("duration");
					frameRate = jsonStream.getString("r_frame_rate");

				}

			}

			try {
				dParamsString.put("duration", "" + (int) Double.parseDouble(secDuration));
			} catch (NumberFormatException e) {
				dParamsString.put("duration", "0");
			}

			if (frameRate != null) {
				String fpsString = frameRate.split("/")[0];
				try {
					dParamsString.put("fps", "" + Integer.parseInt(fpsString));
				} catch (NumberFormatException e) {
					dParamsString.put("fps", "25");
				}
			}

			// set new DNX section
			Dnx dnx = new Dnx();
			dnx.getSection().add(section);
			sip.addDNX(dnx, "ffprobe", true);

			// add DNX section with video information
			Record videoRecord = new Record();

			Key key1 = new Key();
			key1.setId("duration");
			key1.setValue(secDuration);
			videoRecord.getKey().add(key1);

			Key key2 = new Key();
			key2.setId("nb_streams");
			key2.setValue(String.valueOf(numOfStreams));
			videoRecord.getKey().add(key2);

			Key key3 = new Key();
			key3.setId("format_long_name");
			key3.setValue(formatLN);
			videoRecord.getKey().add(key3);

			Key key4 = new Key();
			key4.setId("size");
			key4.setValue(size);
			videoRecord.getKey().add(key4);

			Section videoSec = new Section();
			videoSec.setId("videoMD");
			videoSec.getRecord().add(videoRecord);

			Dnx videoDnx = new Dnx();
			videoDnx.getSection().add(videoSec);

			sip.addDNX(videoDnx, "videoMD", false);

		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to find SIP...");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to work with SIP...");
		} catch (MServeException | IOException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to run FFProbe extractor on MServe...");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to parse JSON from MServe...");
		} finally {
			// release SIP
			P4DataManager.getInstance().releaseIP(sip);
		}
	}

	@WfService(name = "mserve_mxf_techmd_extract", version = "1.0.0")
	public void mxfTechMD(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> arg2) throws TaskExecutionFailedException {

		if (Boolean.parseBoolean(dParamsString.get("isMXF"))) {
			// get static parameters
			String url = sParams.get("mserve.url");

			// get dynamic parameters
			String sipID = dParamsString.get("sipID");
			String fileID = dParamsString.get("mserveFileID");
			String userID = dParamsString.get("userID");

			// get serviceID
			String serviceID = ConfigurationManager.getUserInstance().getService(userID, "mserve");

			SIP sip = null;
			try {
				// get SIP
				sip = P4DataManager.getInstance().getSIPByID(sipID);

				// run MXFTechMD
				MServeClient cl = new MServeClient(url, serviceID);
				File output = cl.runMServeTask(fileID, MServeTask.mxftechmdextractor, null);

				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(output)));
				String line;
				StringBuffer sb = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				JSONObject json = new JSONObject(sb.toString());
				Iterator<String> it = json.keys();

				Section section = new Section();
				section.setId("significantProperties");

				while (it.hasNext()) {
					String key = it.next();
					String value = json.getString(key);

					Record record = new Record();
					Key keyType = new Key();
					keyType.setId("significantPropertiesType");
					keyType.setValue("MXFTechMDExtractor:" + key);
					record.getKey().add(keyType);
					Key keyValue = new Key();
					keyValue.setId("significantPropertiesValue");
					keyValue.setValue(value);
					record.getKey().add(keyValue);
					section.getRecord().add(record);
				}

				section.setId("mxf-techmd-extractor");
				Dnx dnx = new Dnx();
				dnx.getSection().add(section);

				// set format
				String containers = json.getString("EssenceContainers");

				dParamsString.put("isD10", "false");
				if (containers != null) {
					synchronized (sip) {
						List<String> formats = sip.getDCField(DCField.format);
						formats.add(containers);
						sip.setDCField(DCField.format, formats);
					}

					if (containers.contains("D10 Mapping")) {
						dParamsString.put("isD10", "true");
					}
				}

				// add new DNX section
				sip.addDNX(dnx, "mxftechmd", true);
			} catch (DataException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to find SIP...");
			} catch (IPException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to work with SIP...");
			} catch (MServeException | IOException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to run MXFTechMD extractor on MServe...");
			} catch (JSONException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to parse JSON from MServe...");
			} finally {
				// release SIP
				P4DataManager.getInstance().releaseIP(sip);
			}
		}
	}
	
	@WfService(name = "mserve_d10_fixity_checker", version = "1.0.0")
	public void d10FixityChecker(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> arg2) throws TaskExecutionFailedException {

		if (Boolean.parseBoolean(dParamsString.get("isD10"))) {
			// get static parameters
			String url = sParams.get("mserve.url");

			// get dynamic parameters
			String sipID = dParamsString.get("sipID");
			String fileID = dParamsString.get("mserveFileID");
			String userID = dParamsString.get("userID");

			// get serviceID
			String serviceID = ConfigurationManager.getUserInstance().getService(userID, "mserve");

			SIP sip = null;
			try {
				sip = P4DataManager.getInstance().getSIPByID(sipID);

				MServeClient cl = new MServeClient(url, serviceID);
				File outputFile = cl.runMServeTask(fileID, MServeTask.d10mxfchecksum, null);

				// extract
				InputStream is = new FileInputStream(outputFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				StringBuffer sb = new StringBuffer();
				sb.append("\n");
				String line;
				while ((line = br.readLine()) != null) {
					if (line.contains("File checksum")) {
						String checksum = line.split("\t+")[1].trim();
					} else {

						String[] checksArray = line.split("\t+");

						String result = "EDIT_UNIT_NUMBER: " + checksArray[0].trim() + "\t" + "TIMECODE: " + checksArray[1].trim() + "\t" + "EDIT_UNIT_MD5: " + checksArray[2].trim() + "\t" + "PICTURE_ITEM_MD5: " + checksArray[3].trim() + "\t" + "AUDIO_ITEM_MD5: " + checksArray[4].trim();

						sb.append(result + "\n");

					}
				}
				is.close();

				Record d10Record = new Record();
				Key d10agKey = new Key();
				d10agKey.setId("agent");
				d10agKey.setValue("D10SumChecker");
				d10Record.getKey().add(d10agKey);
				Key d10ftKey = new Key();
				d10ftKey.setId("fixityType");
				d10ftKey.setValue("D10SumChecker EditUnit MD5");
				d10Record.getKey().add(d10ftKey);
				Key d10eunKey = new Key();
				d10eunKey.setId("fixityValue");
				d10eunKey.setValue(sb.toString());
				d10Record.getKey().add(d10eunKey);

				Section section = new Section();
				section.setId("fileFixity");
				section.getRecord().add(d10Record);

				Dnx dnx = new Dnx();
				dnx.getSection().add(section);

				sip.addDNX(dnx, "d10sumchecker", true);
			} catch (IOException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to do IO job...");
			} catch (DataException | IPException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to retrieve the SIP...");
			} catch (MServeException e) {
				throw new TaskExecutionFailedException(e.getMessage());
			} finally {
				P4DataManager.getInstance().releaseIP(sip);
			}
		}
	}
	
	@WfService(name = "mserve_transcode2webm", version = "1.1.2")
	public void transcodeToWebM(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get static parameters
		String url = sParams.get("mserve.url");

		// get dynamic parameters
		String sipID = dParamsString.get("sipID");
		String fileID = dParamsString.get("mserveFileID");
		String userID = dParamsString.get("userID");

		// get serviceID
		String serviceID = ConfigurationManager.getUserInstance().getService(userID, "mserve");

		// get SIP
		SIP sip = null;
		try {
			sip = P4DataManager.getInstance().getSIPByID(sipID);

			// run FFmbc
			MServeClient cl = new MServeClient(url, serviceID);

			Map<String, String> params = new HashMap<>();

			if (Boolean.parseBoolean(dParamsString.get("isMXF")))
				params.put("args", "-map_audio_channel 0:1:0:0:1 -vf crop=720:576:0:32 -vf scale=360:288 -f webm");
			else
				params.put("args", "-vf scale=360:288 -f webm");

			File output = cl.runMServeTask(fileID, MServeTask.ffmbc, params);

			// MD5
			MessageDigestExtractor mde = new MessageDigestExtractor();
			ToolOutput<MessageDigestExtractor.AttributeType> toolOutput = mde.extract(output.getAbsolutePath());
			String md5sum = toolOutput.getAttribute(MessageDigestExtractor.AttributeType.MD5);

			// get outputVideo
			String outputVideoName = FilenameUtils.removeExtension(FilenameUtils.getName(sip.getAVMaterial("application/mxf", "FILE").get(0))) + ".webm";
			String outputVideo = dParamsString.get("videosFolder") + File.separator + outputVideoName;
			File targetFile = new File(outputVideo);

			// copy webm to p4 storage
			FileUtils.copyFile(output, targetFile);

			// update SIP
			sip.addFile("video/webm", "FILE", outputVideo, md5sum, targetFile.length());

			dParamsFile.put("webm", new File(outputVideo));

		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to find SIP...");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to work with SIP...");
		} catch (MServeException | ToolException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to run FFmbc extractor on MServe...");
		} catch (IOException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to copy the output file...");
		} finally {
			// release SIP
			P4DataManager.getInstance().releaseIP(sip);
		}
	}
	
	@WfService(name = "mserve_transcode2ogv", version = "1.0.0")
	public void transcodeToOGV(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get static parameters
		String url = sParams.get("mserve.url");

		// get dynamic parameters
		String sipID = dParamsString.get("sipID");
		String fileID = dParamsString.get("mserveFileID");
		String userID = dParamsString.get("userID");

		// get serviceID
		String serviceID = ConfigurationManager.getUserInstance().getService(userID, "mserve");

		// get SIP
		SIP sip = null;
		try {
			sip = P4DataManager.getInstance().getSIPByID(sipID);

			// run FFmbc
			MServeClient cl = new MServeClient(url, serviceID);

			Map<String, String> params = new HashMap<>();
			params.put("args", "-a 3 -v 7");

			File output = cl.runMServeTask(fileID, MServeTask.ffmpeg2theora, params);

			// MD5
			MessageDigestExtractor mde = new MessageDigestExtractor();
			ToolOutput<MessageDigestExtractor.AttributeType> toolOutput = mde.extract(output.getAbsolutePath());
			String md5sum = toolOutput.getAttribute(MessageDigestExtractor.AttributeType.MD5);

			// get outputVideo
			String outputVideoName = FilenameUtils.removeExtension(FilenameUtils.getName(sip.getAVMaterial("application/mxf", "FILE").get(0))) + ".ogv";
			String outputVideo = dParamsString.get("videosFolder") + File.separator + outputVideoName;
			File targetFile = new File(outputVideo);

			// copy webm to p4 storage
			output.renameTo(targetFile);

			// update SIP
			sip.addFile("video/ogg", "FILE", outputVideo, md5sum, targetFile.length());

			dParamsFile.put("webm", new File(outputVideo));

		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to find SIP...");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to work with SIP...");
		} catch (MServeException | ToolException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to run FFmbc extractor on MServe...");
		} finally {
			// release SIP
			P4DataManager.getInstance().releaseIP(sip);
		}
	}
}
