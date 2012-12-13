/**
 * TranscodeTasks.java
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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.datamanagement.DataException;
import eu.prestoprime.datamanagement.DataManager;
import eu.prestoprime.model.oais.AIP;
import eu.prestoprime.model.oais.IPException;
import eu.prestoprime.model.oais.SIP;
import eu.prestoprime.plugin.p4.tools.FFmbc;
import eu.prestoprime.plugin.p4.tools.FFmpeg;
import eu.prestoprime.plugin.p4.tools.FFmpeg2theora;
import eu.prestoprime.tools.MessageDigestExtractor;
import eu.prestoprime.tools.ToolException;
import eu.prestoprime.workflow.exceptions.TaskExecutionFailedException;
import eu.prestoprime.workflow.plugin.WfPlugin;
import eu.prestoprime.workflow.plugin.WfPlugin.WfService;

@WfPlugin(name = "P4Plugin")
public class TranscodeTasks {

	private static final Logger logger = LoggerFactory.getLogger(TranscodeTasks.class);

	@WfService(name = "transcode2webm", version = "2.2.0")
	public void transcodeToWebM(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get sipID
		String sipID = dParamsString.get("sipID");

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
					String mapping = null;
					if (Boolean.parseBoolean(dParamsString.get("isD10")))
						mapping = "D10";
					logger.debug("Transcoding to WebM file: " + inputVideo + " format: " + format);
					new FFmbc().transcodeToWebM(inputVideo, outputVideo, format, mapping, null);

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

	@WfService(name = "transcode2ogv", version = "0.3.0")
	public void transcode2OGV(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamFile) throws TaskExecutionFailedException {

		// get sipID
		String sipID = dParamsString.get("sipID");

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
					String outputVideoName = inputVideo.substring(inputVideo.lastIndexOf(File.separator) + 1, inputVideo.lastIndexOf(".")) + ".ogv";
					String outputVideo = dParamsString.get("videosFolder") + File.separator + outputVideoName;
					// transcode
					new FFmpeg2theora().transcode(inputVideo, outputVideo);

					// MD5
					MessageDigestExtractor mde = new MessageDigestExtractor();
					mde.extract(outputVideo);
					String md5sum = mde.getAttributeByName("MD5");

					// update SIP
					sip.addFile("video/ogg", "FILE", outputVideo, md5sum, new File(outputVideo).length());
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

	@WfService(name = "frames_extraction", version = "2.0.0")
	public void extractFrames(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get sipID
		String sipID = dParamsString.get("sipID");

		// get SIP
		SIP sip = null;

		try {
			sip = DataManager.getInstance().getSIPByID(sipID);

			// get LQ file
			File lqFile = dParamsFile.get("webm");
			String inputVideo = lqFile.getAbsolutePath();

			// get outputThumb
			String outputThumbName = inputVideo.substring(inputVideo.lastIndexOf(File.separator) + 1, inputVideo.lastIndexOf(".")) + ".jpg";
			String outputThumb = dParamsString.get("videosFolder") + File.separator + outputThumbName;

			// get thumb
			new FFmpeg().extractThumb(inputVideo, outputThumb);

			// get outputFrames
			String outputFrames = dParamsString.get("framesFolder");

			// get duration & fps
			int duration = Integer.parseInt(dParamsString.get("duration"));
			int fps = Integer.parseInt(dParamsString.get("fps"));

			// get frames
			new FFmpeg().extractFrames(inputVideo, outputFrames, duration, fps, 5);

		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve SIP...");
		} catch (ToolException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to extract thumb or frames...");
		} finally {
			DataManager.getInstance().releaseIP(sip);
		}
	}

	@WfService(name = "uncompressed_format_migration", version = "1.0.0")
	public void migrateToUncompressed(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamFile) throws TaskExecutionFailedException {

		// retrieve dynamic parameters
		String aipID = dParamsString.get("aipID");
		String outputFolder = dParamsString.get("videosFolder");

		AIP aip = null;
		try {
			aip = DataManager.getInstance().getAIPByID(aipID);
			List<String> videoFiles = aip.getAVMaterial("video/x-raw-yuv", "FILE");
			// check if already migrated
			if (videoFiles == null || videoFiles.size() == 0) {
				// only compressed, migrate
				String[] MQFormats = sParams.get("MQformats").split(",");
				for (String MQFormat : MQFormats) {
					List<String> compressedVideoFiles = aip.getAVMaterial(MQFormat, "FILE");
					if (compressedVideoFiles.size() != 0) {
						String MQFile = compressedVideoFiles.get(0);
						String MQFileName = MQFile.substring(MQFile.lastIndexOf(File.separator), MQFile.lastIndexOf("."));
						FFmpeg ffmpeg = new FFmpeg();
						MessageDigestExtractor mde = new MessageDigestExtractor();

						// encode raw video
						String outputVideoName = MQFileName + ".avi";
						File outputVideo = new File(outputFolder, outputVideoName);
						ffmpeg.encodeToRawVideo(MQFile, outputVideo.getAbsolutePath());
						mde.extract(outputVideo.getAbsolutePath());
						String outputVideoMD5 = mde.getAttributeByName("MD5");

						// encode raw audio
						String outputAudioName = MQFileName + ".pcm";
						File outputAudio = new File(outputFolder, outputAudioName);
						ffmpeg.encodeToRawAudio(MQFile, outputAudio.getAbsolutePath());
						mde.extract(outputAudio.getAbsolutePath());
						String outputAudioMD5 = mde.getAttributeByName("MD5");

						// add both video and audio to AIP
						aip.addFile("video/x-raw-yuv", "FILE", outputVideo.getAbsolutePath(), outputVideoMD5, outputVideo.length());
						aip.addFile("audio/x-raw-int", "FILE", outputAudio.getAbsolutePath(), outputAudioMD5, outputAudio.length());

						aip.addPreservationEvent("FORMAT_MIGRATION", "mimetype=" + MQFormat + ";to=uncompressed;loctype=FILE;");

						break;
					}
				}
			}
		} catch (DataException e) {
			throw new TaskExecutionFailedException("Unable to find DIP...");
		} catch (IPException e) {
			throw new TaskExecutionFailedException("");
		} catch (ToolException e) {
			throw new TaskExecutionFailedException("Unable to encode to raw audio or raw video...");
		} finally {
			DataManager.getInstance().releaseIP(aip);
		}
	}
}
