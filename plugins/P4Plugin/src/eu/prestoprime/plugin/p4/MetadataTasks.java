/**
 * MetadataTasks.java
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
import it.eurix.archtools.data.model.DIP;
import it.eurix.archtools.data.model.DIP.DCField;
import it.eurix.archtools.data.model.IPException;
import it.eurix.archtools.data.model.SIP;
import it.eurix.archtools.tool.ToolException;
import it.eurix.archtools.tool.ToolOutput;
import it.eurix.archtools.workflow.exceptions.TaskExecutionFailedException;
import it.eurix.archtools.workflow.plugin.WfPlugin;
import it.eurix.archtools.workflow.plugin.WfPlugin.WfService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.datamanagement.P4DataManager;
import eu.prestoprime.model.dnx.Dnx;
import eu.prestoprime.model.dnx.Key;
import eu.prestoprime.model.dnx.Record;
import eu.prestoprime.model.dnx.Section;
import eu.prestoprime.plugin.p4.tools.D10SumChecker;
import eu.prestoprime.plugin.p4.tools.FFprobe;
import eu.prestoprime.plugin.p4.tools.MXFTechMDExtractor;
import eu.prestoprime.search.P4Indexer;

@WfPlugin(name = "P4Plugin")
public class MetadataTasks {

	public static final Logger logger = LoggerFactory.getLogger(MetadataTasks.class);

	@WfService(name = "ffprobe", version = "2.1.0")
	public void ffprobe(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamFile) throws TaskExecutionFailedException {

		// get sipID
		String sipID = dParamsString.get("sipID");

		SIP sip = null;
		try {
			// get sip
			sip = P4DataManager.getInstance().getSIPByID(sipID);

			// get MQ file
			String videoFile = null;
			String[] MQformats = sParams.get("MQformats").split(",");
			logger.debug("Checking for MQ formats: " + Arrays.toString(MQformats));
			for (String format : MQformats) {
				List<String> videoFileList = sip.getAVMaterial(format, "FILE");
				if (videoFileList.size() > 0) {
					videoFile = videoFileList.get(0);
					break;
				}
			}
			if (videoFile == null) {
				throw new TaskExecutionFailedException("Unable to find supported MQ format...");
			}

			// run FFprobe
			FFprobe ffprobe = new FFprobe();

			ToolOutput<FFprobe.AttributeType> output = ffprobe.extract(videoFile);
			String ffprobeOutput = output.getAttribute(FFprobe.AttributeType.json);

			Section section = new Section();
			section.setId("ffprobe");

			// get JSON Object
			JSONObject jsonOutput = new JSONObject(ffprobeOutput);

			// FFprobe show format
			JSONObject jsonFormat = jsonOutput.getJSONObject("format");

			Iterator<String> itFormat = jsonFormat.keys();

			while (itFormat.hasNext()) {

				String key = itFormat.next();
				String value = jsonFormat.getString(key);

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
			JSONArray jsonStreams = jsonOutput.getJSONArray("streams");

			String secDuration = null;
			String frameRate = null;
			int numOfStreams = jsonStreams.length();

			for (int i = 0; i < numOfStreams; i++) {

				JSONObject jsonStream = jsonStreams.getJSONObject(i);
				String index = jsonStream.getString("index");

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

		} catch (JSONException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to parse JSON output from ffprobe...");
		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve the SIP...");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to work with SIP...");
		} catch (ToolException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to run FFProbe...");
		} finally {
			// release SIP
			P4DataManager.getInstance().releaseIP(sip);
		}
	}

	@WfService(name = "mxf_techmd_extract", version = "2.0.0")
	public void mxfTechMD(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamFile) throws TaskExecutionFailedException {

		if (Boolean.parseBoolean(dParamsString.get("isMXF"))) {
			// get sipID
			String sipID = dParamsString.get("sipID");

			SIP sip = null;
			try {
				// get SIP
				sip = P4DataManager.getInstance().getSIPByID(sipID);

				// get MQ file
				String mxfFile = sip.getAVMaterial("application/mxf", "FILE").get(0);

				// run MXFTechMD
				MXFTechMDExtractor mxfTechMD = new MXFTechMDExtractor();

				logger.debug("Inside extractMxfTechMd...");
				logger.debug("File passed to MXFTechMD: " + mxfFile);
				mxfTechMD.extract(mxfFile);
				logger.info("Extracted MXF Technical Metadata...");
				List<String> attributeList = mxfTechMD.getSupportedAttributeNames();

				Section section = new Section();
				section.setId("significantProperties");

				Record record = null;

				for (String attributeName : attributeList) {
					record = new Record();
					Key keyType = new Key();
					keyType.setId("significantPropertiesType");
					keyType.setValue("MXFTechMDExtractor:" + attributeName);
					record.getKey().add(keyType);
					Key keyValue = new Key();
					keyValue.setId("significantPropertiesValue");
					keyValue.setValue(mxfTechMD.getAttributeByName(attributeName));
					record.getKey().add(keyValue);
					section.getRecord().add(record);
				}

				section.setId("mxf-techmd-extractor");
				Dnx dnx = new Dnx();
				dnx.getSection().add(section);
				logger.info("Added new section to dnx...");

				// set format
				String containers = mxfTechMD.getAttributeByName("EssenceContainers");

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
			} catch (ToolException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to run MXFTechMD extractor...");
			} finally {
				// release SIP
				P4DataManager.getInstance().releaseIP(sip);
			}
		}
	}

	@WfService(name = "d10_fixity_checker", version = "1.3.0")
	public void d10FixityChecker(Map<String, String> arg0, Map<String, String> dParamsString, Map<String, File> arg2) throws TaskExecutionFailedException {

		if (Boolean.parseBoolean(dParamsString.get("isD10"))) {

			String sipID = dParamsString.get("sipID");

			SIP sip = null;
			try {
				sip = P4DataManager.getInstance().getSIPByID(sipID);

				String videoFilePath = sip.getAVMaterial("application/mxf", "FILE").get(0);

				D10SumChecker d10SumChecker = new D10SumChecker();
				ToolOutput<D10SumChecker.AttributeType> output = d10SumChecker.extract(videoFilePath);

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
				d10eunKey.setValue(output.getAttribute(D10SumChecker.AttributeType.D10SumChecker));
				d10Record.getKey().add(d10eunKey);

				Section section = new Section();
				section.setId("fileFixity");
				section.getRecord().add(d10Record);

				Dnx dnx = new Dnx();
				dnx.getSection().add(section);

				sip.addDNX(dnx, "d10sumchecker", true);
			} catch (DataException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to get the SIP...");
			} catch (IPException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to get MQ file...");
			} catch (ToolException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to run D10SumChecker...");
			} finally {
				P4DataManager.getInstance().releaseIP(sip);
			}
		}
	}

	@WfService(name = "metadata_indexing", version = "2.1.0")
	public void indexMetadata(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {
		String sipID = dParamsString.get("sipID");
		P4Indexer indexer = new P4Indexer();

		boolean success = false;
		DIP sip = null;

		if (sipID != null) {
			try {

				logger.debug("Indexing metadata of SIP " + sipID);

				logger.debug("Retrieving SIP " + sipID + " from Persistence DB");

				sip = P4DataManager.getInstance().getDIPByID(sipID);

				logger.debug("Got DIP object " + sip);

				// TODO use indexer.indexIP as soon as dc record getter works on
				// P4InformationPackage
				success = indexer.indexIP(sip);

			} catch (DataException e) {
				logger.warn("DC Records could not be obtained.");
				logger.warn(e.getMessage());
			}
			if (!success) {
				throw new TaskExecutionFailedException("Unable to index Metadata");
			}
		} else {
			throw new TaskExecutionFailedException("SIP ID was not set in workflow");
		}
	}

	@WfService(name = "rebuild_search_index", version = "2.2.0")
	public void reindexMetadata(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		try {

			boolean success = true;
			List<String> failList = new ArrayList<>();

			P4Indexer indexer = new P4Indexer();
			success &= indexer.clearIndex();

			logger.debug("Successfully cleared search index...");

			List<String> aipIdList = P4DataManager.getInstance().getAllAIP(null);

			if (aipIdList.isEmpty()) {
				logger.debug("No AIPs found.");
			}

			for (String aipId : aipIdList) {

				logger.debug("Indexing AIP " + aipId);

				DIP dip = P4DataManager.getInstance().getDIPByID(aipId);

				boolean aipSuccess = indexer.indexIP(dip);
				success &= aipSuccess;
				if (!aipSuccess) {
					failList.add(aipId);
				}
			}

			if (success) {
				logger.debug("Indexing done! Number of indexed AIPs: " + aipIdList.size());
			} else {
				StringBuilder sb = new StringBuilder();
				String delim = "";
				for (String id : failList) {
					sb.append(delim);
					sb.append(id);
					delim = ", ";
				}
				logger.error("Indexing failed for AIPs: " + sb.toString());
				throw new TaskExecutionFailedException("Unable to rebuild search index...");
			}

		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve AIP list from database...");
		}

	}
}
