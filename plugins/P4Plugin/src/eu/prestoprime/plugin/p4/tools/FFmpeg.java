/**
 * FFmpeg.java
 * Author: Francesco Gallo (gallo@eurix.it)
 * Contributors: Philip Kahle (philip.kahle@uibk.ac.at)
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
package eu.prestoprime.plugin.p4.tools;

import it.eurix.archtools.tool.AbstractTool;
import it.eurix.archtools.tool.ToolException;

import java.io.File;

import eu.prestoprime.conf.Constants;
import eu.prestoprime.tools.P4ToolManager;

public class FFmpeg extends AbstractTool<FFmpeg.AttributeType> {

	public static enum AttributeType {
		
	}
	
	public FFmpeg() {
		super(P4ToolManager.getInstance().getToolDescriptor(Constants.FFMPEG_NAME));
	}

	public void encode(String inputFile, String outputFile) throws ToolException {
		execute("-i", inputFile, outputFile);
	}

	/**
	 * Single-threaded transcoding to WebM. Parameters tested with uibk mp4
	 * material only. </br> Produces VBR compressed, deinterlaced files with
	 * target bitrate of 1200k/sec. </br> Compression factor of 1/4 for the
	 * tested mp4 files.</br> About 14fps on Core2Quad Q9400 @ 2,66Ghz.</br>
	 * </br> Example: ffmpeg -i inFile.mp4 -vcodec libvpx -acodec libvorbis
	 * -qmin 2 -qmax 42 -vb 1200k -filter:v yadif -f webm outFile.webm</br>
	 * </br> Thus, mimeTypes other than mp4 are ignored and no processing takes
	 * place.</br>
	 * 
	 * @param inputFile
	 * @param outputFile
	 * @param mimeType
	 * @throws ToolException
	 */
	public void transcodeToWebM(String inputFile, String outputFile, String mimeType) throws ToolException {

		if (mimeType.equalsIgnoreCase("video/mp4")) {
			execute("-i", inputFile, "-vcodec", "libvpx", "-acodec", "libvorbis", "-qmin", "2", "-qmax", "42", "-vb", "1200k", "-filter:v", "yadif", "-f", "webm", outputFile);
		} else {
			// TODO Only tested for uibk mp4 files. Test command manually and
			// alter if-clause or add commands here.
		}

	}

	/**
	 * Transcode to WebM with a minimum set of parameters. Allows to pass custom
	 * parameters from the executing plugin.</br> </br> Default parameters are:
	 * </br> -vcodec libvpx : vp8 video</br> -acodec libvorbis : vorbis
	 * audio</br> -f webm : force format webm</br> </br> Reasonable parameters
	 * to pass in params:</br> -qmin 2 : range minumum for quantizers :
	 * "-qmin","2"</br> -qmax 42 : range maximum for quantizers :
	 * "-qmax","42"</br> -vb 1200k: target bitrate for video stream :
	 * "-vb","1200k"</br> -ar 44100: downsample audio rate :
	 * ,"-ar","44100",</br> -filter:v yadif : deinterlace filter is needed for
	 * vhs material : "-filter:v","yadif"</br> -metadata title="title" : force
	 * title in metadata : ,"-metadata title=\""+outputFile+"\"",</br> -threads
	 * x: use x cpu cores : "-threads","2"</br> </br>
	 * 
	 * @param inputFile
	 * @param outputFile
	 * @param mimeType
	 * @param params
	 *            a custom parameter array. If null, then quality settings will
	 *            depend on the used version of FFMpeg. Resulting files may be
	 *            too large for streaming though.
	 * @throws ToolException
	 */
	public void transcodeToWebM(String inputFile, String outputFile, String mimeType, String... params) throws ToolException {

		if (params == null) { // default fallback
			params = new String[] {};
		}

		String[] execParams = new String[params.length + 9];

		execParams[0] = "-i";
		execParams[1] = inputFile;
		execParams[2] = "-vcodec";
		execParams[3] = "libvpx";
		execParams[4] = "-acodec";
		execParams[5] = "libvorbis";
		execParams[6] = "-f";
		execParams[7] = "webm";
		execParams[execParams.length - 1] = outputFile; // outputFile as last
														// parameter

		for (int i = 0; i < params.length; i++) { // fill params in between
			execParams[i + 8] = params[i];
		}
		execute(execParams);
	}

	public void extractFrames(String inputFile, String outputFolder, int duration, int fps, int sampling) throws ToolException {

		if (sampling > fps)
			sampling = fps;

		File outputDir = new File(outputFolder);
		if (!outputDir.isDirectory() || !outputDir.canWrite())
			throw new ToolException("Error reading or creating output dir " + outputDir);

		String outputName = outputFolder + File.separator + "%d.jpg";

		execute("-i", inputFile, "-an", "-r", "1/" + Integer.toString(sampling), outputName);

		logger.info("Extracted frames: " + outputDir.list().length + " Folder: " + outputFolder);

		for (File frameFile : outputDir.listFiles()) {
			int frameNum = Integer.parseInt(frameFile.getName().split(".jpg")[0]);
			if (frameNum == 1 || frameNum == outputDir.length())
				frameNum = frameNum - 1;
			else
				frameNum = frameNum - 2;
			int frame = fps * sampling * frameNum;
			frameFile.renameTo(new File(outputDir, frame + "F" + fps + ".jpg"));
		}

	}

	/**
	 * Same as extractFrames but scales keyframes to a given height in pixels,
	 * preserving aspect ratio. For use in web applications in low bandwidth
	 * environments.
	 * 
	 * @param inputFile
	 * @param outputFolder
	 * @param fps
	 * @param sampling
	 * @param height
	 *            in pixels
	 * @throws ToolException
	 */
	public void extractRescaledFrames(String inputFile, String outputFolder, int fps, int sampling, int height) throws ToolException {

		if (sampling > fps)
			sampling = fps;

		File outputDir = new File(outputFolder);
		if (!outputDir.isDirectory() || !outputDir.canWrite())
			throw new ToolException("Error reading or creating output dir " + outputDir);

		String outputName = outputFolder + File.separator + "%d.jpg";

		execute("-i", inputFile, "-an", "-r", "1/" + Integer.toString(sampling), "-vf", "scale=-1:" + height, outputName);

		logger.info("Extracted frames: " + outputDir.list().length + " Folder: " + outputFolder);

		for (File frameFile : outputDir.listFiles()) {
			int frameNum = Integer.parseInt(frameFile.getName().split(".jpg")[0]);
			if (frameNum == 1 || frameNum == outputDir.length())
				frameNum = frameNum - 1;
			else
				frameNum = frameNum - 2;
			int frame = fps * sampling * frameNum;
			frameFile.renameTo(new File(outputDir, frame + "F" + fps + ".jpg"));
		}

	}

	/**
	 * @param inputFile
	 * @param thumbFile
	 * @param time
	 *            String format like: "00:00:05"
	 * @throws ToolException
	 */
	public void extractThumb(String inputFile, String thumbFile, String time) throws ToolException {

		execute("-i", inputFile, "-an", "-ss", time, "-vframes", "1", thumbFile);

	}

	public void extractThumb(String inputFile, String thumbFile) throws ToolException {

		execute("-i", inputFile, "-an", "-ss", "00:00:05", "-vframes", "1", thumbFile);

	}

	public void setCustomFFmpeg(String ffmpegPath) {
		setCustomExecutable(ffmpegPath);
	}

	public void encodeToRawVideo(String inputFile, String outputFile) throws ToolException {

		execute("-i", inputFile, "-pix_fmt", "yuvj422p", "-an", "-vcodec", "rawvideo", "-y", outputFile);

	}

	public void encodeToRawAudio(String inputFile, String outputFile) throws ToolException {

		execute("-i", inputFile, "-vn", "-f", "s16le", "-acodec", "pcm_s16le", "-y", outputFile);

	}

}