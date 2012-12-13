/**
 * FFMPEGTranscodeToWebMTask.java
 * Author: Philip Kahle (philip.kahle@uibk.ac.at) 
 * Contributors: Francesco Gallo (gallo@eurix.it), Francesco Rosso (rosso@eurix.it)
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
package eu.prestoprime.plugin.p4.legacy;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.datamanagement.DataException;
import eu.prestoprime.datamanagement.DataManager;
import eu.prestoprime.model.oais.IPException;
import eu.prestoprime.model.oais.SIP;
import eu.prestoprime.plugin.p4.tools.FFmpeg;
import eu.prestoprime.tools.MessageDigestExtractor;
import eu.prestoprime.tools.ToolException;
import eu.prestoprime.workflow.exceptions.TaskExecutionFailedException;
import eu.prestoprime.workflow.tasks.P4Task;

public class FFMPEGTranscodeToWebMTask implements P4Task {

	private Logger logger = LoggerFactory.getLogger(FFMPEGTranscodeToWebMTask.class);

	@Override
	public void execute(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get sipID
		final String sipID = dParamsString.get("sipID");

		// get nr of Threads used for transcoding. If not set, then the
		// parameter is omitted resulting in one thread.
		final String nrOfThreads = sParams.get("TranscodingThreads");

		// get SIP
		SIP sip = null;

		try {
			sip = DataManager.getInstance().getSIPByID(sipID);

			// get MQ file
			String videoFile = null;
			String[] formats = sParams.get("MQformats").split(",");
			for (String format : formats) {

				List<String> videoFileList = sip.getAVMaterial(format, "FILE");
				if (videoFileList.size() > 0) {
					videoFile = videoFileList.get(0);

					// get inputVideo
					String inputVideo = videoFile;

					// get outputVideo
					String outputVideoName = inputVideo.substring(inputVideo.lastIndexOf(File.separator) + 1, inputVideo.lastIndexOf(".")) + ".webm";
					String outputVideo = dParamsString.get("videosFolder") + File.separator + outputVideoName;
					// transcode
					logger.debug("Transcoding to WebM file: " + inputVideo + " format: " + format);

					if (nrOfThreads == null) {
						new FFmpeg().transcodeToWebM(inputVideo, outputVideo, format, "-qmin", "2", "-qmax", "42", "-vb", "1200k", "-filter:v", "yadif");

					} else {
						new FFmpeg().transcodeToWebM(inputVideo, outputVideo, format, "-qmin", "2", "-qmax", "42", "-vb", "1200k", "-filter:v", "yadif", "-threads", nrOfThreads);
					}
					// MD5
					MessageDigestExtractor mde = new MessageDigestExtractor();
					mde.extract(outputVideo);
					String md5sum = mde.getAttributeByName("MD5");

					// update SIP
					sip.addFile("video/webm", "FILE", outputVideo, md5sum, new File(outputVideo).length());

					dParamsFile.put("webm", new File(outputVideo));
					return;
				}
			}
			if (videoFile == null) {
				throw new TaskExecutionFailedException("Unable to find supported MQ format...");
			}
		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve SIP...");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to find MQ file...");
		} catch (ToolException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to transcode video file...");
		} finally {
			DataManager.getInstance().releaseIP(sip);
		}
	}
}
