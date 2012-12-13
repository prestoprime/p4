/**
 * FixityTasks.java
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
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.datamanagement.DataManager;
import eu.prestoprime.model.oais.AIP;
import eu.prestoprime.tools.MessageDigestExtractor;
import eu.prestoprime.workflow.exceptions.TaskExecutionFailedException;
import eu.prestoprime.workflow.plugin.WfPlugin;
import eu.prestoprime.workflow.plugin.WfPlugin.WfService;

@WfPlugin(name = "P4Plugin")
public class FixityTasks {

	private static final Logger logger = LoggerFactory.getLogger(FixityTasks.class);

	@WfService(name = "fixity_checks", version = "1.0.0")
	public void fixityCheck(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// static parameters
		String[] formats = sParams.get("MQformats").split(",");

		// dynamic parameters
		String aipID = dParamsString.get("aipID");

		AIP aip = null;
		try {
			aip = DataManager.getInstance().getAIPByID(aipID);
			for (String format : formats) {
				List<String> videoFileList = aip.getAVMaterial(format, "FILE");
				if (videoFileList.size() > 0) {
					String inputVideo = videoFileList.get(0);
					MessageDigestExtractor mde = new MessageDigestExtractor();
					mde.extract(inputVideo);

					String MD5 = aip.getChecksum(format, "MD5");
					String currentMD5 = mde.getAttributeByName("MD5");

					if (!currentMD5.equals(MD5)) {
						aip.addPreservationEvent("FIXITY_CHECK", "type=md5sum;mimetype=" + format + ";locatype=FILE;result=failed");

						dParamsString.put("toBeRestored", format);
						break;
					} else {
						aip.addPreservationEvent("FIXITY_CHECK", "type=md5sum;mimetype=" + format + ";loctype=FILE;result=ok");

						JSONObject result = new JSONObject();
						result.put("status", "verified");
						result.put("MD5", MD5);
						dParamsString.put("result", result.toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataManager.getInstance().releaseIP(aip);
		}
	}

	@WfService(name = "fixity_restore", version = "2.0.0")
	public void fixityRestore(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		String aipID = dParamsString.get("aipID");
		String format = dParamsString.get("toBeRestored");

		AIP aip = null;
		try {
			aip = DataManager.getInstance().getAIPByID(aipID);

			if (format != null) {
				List<String> videoFileList = aip.getAVMaterial(format, "FILE");
				List<String> videoFileBckList = aip.getAVMaterial(format, "FILE-BCK");
				if (videoFileList.size() > 0 && videoFileBckList.size() > 0) {
					File illVideo = new File(videoFileList.get(0));
					File healthyVideo = new File(videoFileBckList.get(0));

					FileOutputStream fos = new FileOutputStream(illVideo);
					FileInputStream fis = new FileInputStream(healthyVideo);

					byte[] buf = new byte[1024];
					int len;
					while ((len = fis.read(buf)) > 0) {
						fos.write(buf, 0, len);
					}
					fis.close();
					fos.close();

					aip.addPreservationEvent("RESTORE", "mimetype=" + format + ";loctype=FILE");

					JSONObject result = new JSONObject();
					result.put("status", "restored");
					result.put("from", healthyVideo.getAbsolutePath());
					result.put("to", illVideo.getAbsolutePath());
					result.put("MD5", aip.getChecksum(format, "MD5"));
					dParamsString.put("result", result.toString());

					dParamsString.remove("toBeRestored");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataManager.getInstance().releaseIP(aip);
		}
	}
}
