/**
 * FFProbeTaskExtended.java
 * Author: Philip Kahle (philip.kahle@uibk.ac.at)
 * 
 * This file is part of PrestoPRIME Preservation Platform (P4).
 * 
 * Copyright (C) 2009-2012 University of Innsbruck, Austria
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
import it.eurix.archtools.data.model.DIP.DCField;
import it.eurix.archtools.data.model.IPException;
import it.eurix.archtools.data.model.SIP;
import it.eurix.archtools.tool.ToolException;
import it.eurix.archtools.tool.ToolOutput;
import it.eurix.archtools.workflow.exceptions.TaskExecutionFailedException;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.prestoprime.datamanagement.P4DataManager;
import eu.prestoprime.model.dnx.Dnx;
import eu.prestoprime.model.dnx.Key;
import eu.prestoprime.model.dnx.Record;
import eu.prestoprime.model.dnx.Section;
import eu.prestoprime.plugin.p4.tools.FFprobe;
import eu.prestoprime.workflow.P4Task;

/**
 * Same as FFProbeTask but injects more techMd values directly into the AIP,
 * granting immediate access to other components.
 */
public class FFProbeTaskExtended implements P4Task {

	private static final Logger logger = Logger.getLogger(FFProbeTaskExtended.class);

	@Override
	public void execute(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamFile) throws TaskExecutionFailedException {

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
				Record record = getDnxKvRecord(key, value);
				section.getRecord().add(record);
			}

			// prepare videoMd map
			Map<String, String> vidMd = new HashMap<>();

			// set format
			String formatN = jsonFormat.getString("format_name");
			String formatLN = jsonFormat.getString("format_long_name");

			vidMd.put("format_name", formatN);
			vidMd.put("format_long_name", formatLN);

			// size
			String size = jsonFormat.getString("size");
			vidMd.put("size", size);

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

			int numOfStreams = jsonStreams.length();

			vidMd.put("nb_streams", "" + numOfStreams);

			for (int i = 0; i < numOfStreams; i++) {

				JSONObject jsonStream = jsonStreams.getJSONObject(i);
				String index = jsonStream.getString("index");

				Iterator<String> itStream = jsonStream.keys();

				while (itStream.hasNext()) {
					String key = itStream.next();
					String value = jsonStream.getString(key);
					Record record = getDnxKvRecord(key, value, index);
					section.getRecord().add(record);
				}

				if (jsonStream.getString("codec_type").equals("video")) {
					vidMd.put("duration", jsonStream.getString("duration"));
					vidMd.put("framerate", jsonStream.getString("r_frame_rate"));
					vidMd.put("aspect_ratio", jsonStream.getString("display_aspect_ratio"));
					vidMd.put("video_codec", jsonStream.getString("codec_name"));
					vidMd.put("width", jsonStream.getString("width"));
					vidMd.put("height", jsonStream.getString("height"));
				}

				if (jsonStream.getString("codec_type").equals("audio")) {
					vidMd.put("audio_codec", jsonStream.getString("codec_name"));
				}

			}

			try {
				dParamsString.put("duration", "" + (int) Double.parseDouble(vidMd.get("duration")));
			} catch (NumberFormatException e) {
				dParamsString.put("duration", "0");
			}

			String frameRate = vidMd.get("framerate");
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
			Key key = null;
			for (Entry<String, String> e : vidMd.entrySet()) {
				key = getDnxKey(e.getKey(), e.getValue());
				videoRecord.getKey().add(key);
			}

			Section videoSec = new Section();
			videoSec.setId("videoMD");
			videoSec.getRecord().add(videoRecord);

			Dnx videoDnx = new Dnx();
			videoDnx.getSection().add(videoSec);

			sip.addDNX(videoDnx, "videoMD", false);

		} catch (JSONException e) {
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

	public static Key getDnxKey(String name, String value) {
		Key key = new Key();
		key.setId(name);
		key.setValue(value);
		return key;
	}

	public static Record getDnxKvRecord(String key, String value) {
		return getDnxKvRecord(key, value, null);
	}

	public static Record getDnxKvRecord(String key, String value, String index) {
		String sigPropType = (index == null) ? "ffprobe:" + key : "ffprobe:" + key + "_" + index;

		Record record = new Record();
		Key keyType = new Key();
		keyType.setId("significantPropertiesType");
		keyType.setValue(sigPropType);
		record.getKey().add(keyType);
		Key keyValue = new Key();
		keyValue.setId("significantPropertiesValue");
		keyValue.setValue(value);
		record.getKey().add(keyValue);
		return record;
	}
}
