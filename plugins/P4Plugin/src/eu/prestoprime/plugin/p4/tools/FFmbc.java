/**
 * FFmbc.java
 * Author: Francesco Gallo (gallo@eurix.it)
 * Contributors: Laurent Boch (l.boch@rai.it)
 * 
 * This file is part of PrestoPRIME Preservation Platform (P4).
 * 
 * Copyright (C) 2009-2012 EURIX Srl, Torino, Italy
 * Copyright (C) 2009-2012 RAI, Torino, Italy 
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
import eu.prestoprime.conf.Constants;
import eu.prestoprime.tools.P4ToolManager;

public class FFmbc extends AbstractTool {

	public FFmbc() {
		super(P4ToolManager.getInstance().getToolDescriptor(Constants.FFMBC_NAME));
	}

	/**
	 * Example: ffmbc -i <inputfilename.mxf> -map_audio_channel 0:1:0:0:1 -vf
	 * 'crop=720:576:0:32' -vf 'scale=360:288' -f webm <outputfilename.webm>
	 */
	public void transcodeToWebM(String inputFile, String outputFile, String mimeType, String mapping, String numOfThreads) throws ToolException {

		if (numOfThreads == null)
			numOfThreads = "2";

		if (mimeType != null && mapping != null && mimeType.equalsIgnoreCase("application/mxf") && mapping.contains("D10"))
			execute("-i", inputFile, "-threads", numOfThreads, "-map_audio_channel", "0:1:0:0:1", "-vf", "crop=720:576:0:32", "-vf", "scale=360:288", "-f", "webm", outputFile);
		else {
			// Remove map audio channel and cropping for no-D10 files
			execute("-i", inputFile, "-threads", numOfThreads, "-vf", "scale=360:288", "-f", "webm", outputFile);
		}

	}

	/**
	 * Example: Using RAI recipe for different file types (L. Boch) For MXF only
	 * D10 and Uncompressed are supported.
	 * 
	 * for MXF/Uncompressed:
	 * 
	 * ffmbc -i <inputFile> -threads $numthreads -ss $secondstart -t
	 * $secondtargetdur -ac 4 -map_audio_channel 0:1:0:0:1 -map_audio_channel
	 * 0:2:0:0:1 -map_audio_channel 0:3:0:0:1 -map_audio_channel 0:4:0:0:1
	 * -target imx50 -f mxf_d10 -acodec copy -vcodec copy <outputFile>
	 * 
	 * for MXF/D10:
	 * 
	 * ffmbc -i <inputFile> -threads $numthreads -ss $secondstart -t
	 * $secondtargetdur -f mxf_d10 -acodec copy -vcodec copy <outputFile>
	 * 
	 * for others MXF (e.g. XDCAM) and for no MXF:
	 * 
	 * ffmbc -i <inputFile> -threads $numthreads -ss $secondstart -t
	 * $secondtargetdur -acodec copy -vcodec copy <outputFile>
	 * 
	 * 
	 * where secondstart = editunitstart/25, secondtargetdur = numofeditunits/25
	 */
	public void extractSegment(String inputFile, String outputFile, String start, String duration, String mimeType, String mapping, String numOfThreads) throws ToolException {

		if (numOfThreads == null)
			numOfThreads = "2";

		if (mimeType != null && mimeType.equalsIgnoreCase("application/mxf")) {
			if (mapping.contains("D10")) {
				// MXF/D10
				execute("-y", "-i", inputFile, "-threads", numOfThreads, "-ss", start, "-t", duration, "-f", "mxf_d10", "-acodec", "copy", "-vcodec", "copy", outputFile);
			} else if (mapping.contains("Uncompressed")) {
				// MXF/Uncompressed
				execute("-y", "-i", inputFile, "-threads", numOfThreads, "-ss", start, "-t", duration, "-ac", "4", "-map_audio_channel", "0:1:0:0:1", "-map_audio_channel", "0:2:0:0:1", "-map_audio_channel", "0:3:0:0:1", "-map_audio_channel", "0:4:0:0:1", "-target", "imx50", "-f", "mxf_d10", outputFile);
			} else {
				// Other MXF (e.g. XDCAM), generic command
				execute("-y", "-i", inputFile, "-threads", numOfThreads, "-ss", start, "-t", duration, "-acodec", "copy", "-vcodec", "copy", outputFile);
			}

		} else {
			// Others, no MXF, generic command
			execute("-y", "-i", inputFile, "-threads", numOfThreads, "-ss", start, "-t", duration, "-acodec", "copy", "-vcodec", "copy", outputFile);
		}

	}

	public void setCustomFFmbc(String ffmbcPath) {
		setCustomExecutable(ffmbcPath);
	}

}