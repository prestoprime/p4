package eu.prestoprime.plugin.dracma;
import it.eurix.archtools.data.DataException;
import it.eurix.archtools.data.model.DIP;
import it.eurix.archtools.data.model.IPException;
import it.eurix.archtools.data.model.SIP;
import it.eurix.archtools.tool.ToolException;
import it.eurix.archtools.tool.ToolOutput;
import it.eurix.archtools.tool.impl.MessageDigestExtractor;
import it.eurix.archtools.workflow.exceptions.TaskExecutionFailedException;
import it.eurix.archtools.workflow.plugin.WfPlugin;
import it.eurix.archtools.workflow.plugin.WfPlugin.WfService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.datamanagement.P4DataManager;
import eu.prestoprime.plugin.dracma.client.DRACMAClient;
import eu.prestoprime.plugin.dracma.client.DRACMAException;
import eu.prestoprime.plugin.dracma.client.DRACMASegment;

/**
 * DRACMATasks.java
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

@WfPlugin(name = "DRACMAPlugin")
public class DRACMATasks {

	private static final Logger logger = LoggerFactory.getLogger(DRACMATasks.class); 
	
	@WfService(name = "dracma_indexing", version="2.0.0")
	public void index(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		if (Boolean.parseBoolean(dParamsString.get("isD10"))) {
			// retrieve dynamic parameters
			String sipID = dParamsString.get("sipID");

			// retrieve static parameters
			String DRACMAIndexServer = sParams.get("dracma.indexing.url");
			String[] MQFormats = sParams.get("MQformats").split(",");

			SIP sip = null;
			try {
				sip = P4DataManager.getInstance().getSIPByID(sipID);

				for (String format : MQFormats) {
					List<String> MQFilePathList = sip.getAVMaterial(format, "FILE");
					if (MQFilePathList.size() > 0) {

						URI indexServer = new URI(DRACMAIndexServer);
						File mxfFile = new File(MQFilePathList.get(0));

						String UMID = new DRACMAClient(indexServer).index(mxfFile);

						MessageDigestExtractor mde = new MessageDigestExtractor();
						ToolOutput<MessageDigestExtractor.AttributeType> output = mde.extract(mxfFile.getAbsolutePath());

						sip.addFile(format, "DRACMA", UMID, output.getAttribute(MessageDigestExtractor.AttributeType.MD5), mxfFile.length());

						break;
					}
				}

			} catch (DataException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to retrieve the SIP...");
			} catch (IPException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to retrieve the MQ file...");
			} catch (DRACMAException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to execute DRACMA indexing...");
			} catch (ToolException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to compute the MD5...");
			} catch (URISyntaxException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Bad DRACMA indexing URL in wfDescriptor...");
			} finally {
				P4DataManager.getInstance().releaseIP(sip);
			}
		}
	}

	@WfService(name = "dracma_make_consumer_segment", version = "2.0.0")
	public void segment(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		if (!Boolean.parseBoolean(dParamsString.get("isSegmented"))) {

			// retrieve static params
			String DRACMASegmentServer = sParams.get("dracma.segmenter.url");
			String destVolume = sParams.get("dest.path.volume").trim();
			String destFolder = sParams.get("dest.path.folder").trim();

			// retrieve dynamic params
			String dipID = dParamsString.get("dipID");
			String sourceFilePath = dParamsString.get("source.file.path");
			String mimetype = dParamsString.get("source.file.mimetype");
			int startFrame = Integer.parseInt(dParamsString.get("start.frame"));
			int stopFrame = Integer.parseInt(dParamsString.get("stop.frame"));

			String outputFolder = destVolume + File.separator + destFolder;

			try {
				DIP dip = P4DataManager.getInstance().getDIPByID(dipID);

				// get UMID
				String UMID = null;

				if (mimetype != null) {
					List<String> videoFileList = dip.getAVMaterial(mimetype, "DRACMA");

					if (videoFileList.size() > 0) {
						UMID = videoFileList.get(0);

						File targetDir = new File(outputFolder);
						targetDir.mkdirs();

						if (!targetDir.canWrite())
							throw new TaskExecutionFailedException("Unable to write to output dir " + outputFolder);

						String targetFileName = dipID + "." + startFrame + "." + stopFrame + ".mxf";
						File targetFile = new File(targetDir, targetFileName);

						URI segmentServer = new URI(DRACMASegmentServer);
						DRACMAClient c = new DRACMAClient(segmentServer);

						c.update(UMID, new File(sourceFilePath));

						logger.debug("Updated DRACMA " + UMID + " resource location to " + sourceFilePath);
						logger.debug("Requesting segment to DRACMA...");

						DRACMASegment segment = new DRACMASegment(UMID, startFrame, stopFrame);
						URL targetFileURL = c.segment(segment);

						logger.debug("DRACMA segmented available on " + targetFileURL);
						logger.debug("Downloadin' DRACMA segment...");

						HttpClient client = new DefaultHttpClient();
						HttpUriRequest request = new HttpGet(targetFileURL.toString());
						HttpResponse response = client.execute(request);
						if (response.getStatusLine().getStatusCode() == 200) {
							HttpEntity entity = response.getEntity();

							InputStream in = entity.getContent();
							FileOutputStream out = new FileOutputStream(targetFile);

							byte[] buffer = new byte[4096];
							int bytesRead;
							while ((bytesRead = in.read(buffer)) != -1)
								out.write(buffer, 0, bytesRead);

							in.close();
							out.close();

							dParamsString.put("isSegmented", "true");
							dParamsString.put("segment.file.path", targetFileName);

							logger.debug("Consumer copy available at: " + targetFile.getAbsolutePath());
						} else {
							logger.debug("DRACMA response with status code " + response.getStatusLine().getStatusCode());
						}
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
			} catch (DRACMAException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to execute segmentation with DRACMA...");
			} catch (URISyntaxException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Bad DRACMA segmenter URL in wfDescriptor...");
			}
		}
	}
}
