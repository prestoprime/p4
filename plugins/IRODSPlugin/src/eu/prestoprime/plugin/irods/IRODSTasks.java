/**
 * IRODSTasks.java
 * Author: Francesco Gallo (gallo@eurix.it)
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
package eu.prestoprime.plugin.irods;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.packinstr.TransferOptions;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.pub.io.IRODSFileInputStream;
import org.irods.jargon.core.transfer.DefaultTransferControlBlock;
import org.irods.jargon.core.transfer.TransferControlBlock;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.datamanagement.DataException;
import eu.prestoprime.datamanagement.DataManager;
import eu.prestoprime.model.oais.AIP;
import eu.prestoprime.model.oais.DIP;
import eu.prestoprime.model.oais.IPException;
import eu.prestoprime.model.oais.SIP;
import eu.prestoprime.workflow.exceptions.TaskExecutionFailedException;
import eu.prestoprime.workflow.plugin.WfPlugin;
import eu.prestoprime.workflow.plugin.WfPlugin.WfService;

@WfPlugin(name = "IRODSPlugin")
public class IRODSTasks {

	private static final Logger logger = LoggerFactory.getLogger(IRODSTasks.class);
	
	@WfService(name = "irods_put", version = "2.0.0")
	public void put(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get static parameters
		String host = sParams.get("irods.host");
		int port = Integer.parseInt(sParams.get("irods.port"));
		String username = sParams.get("irods.username");
		String password = sParams.get("irods.password");
		String homeDir = sParams.get("irods.home.dir");
		String zone = sParams.get("irods.zone");
		String defaultResource = sParams.get("irods.default.resource");

		// get dynamic parameters
		String sipID = dParamsString.get("sipID");

		SIP sip = null;

		try {

			IRODSFileSystem irodsFileSystem = IRODSFileSystem.instance();

			sip = DataManager.getInstance().getSIPByID(sipID);

			IRODSAccount account = IRODSAccount.instance(host, port, username, password, homeDir, zone, defaultResource);

			IRODSAccessObjectFactory accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();

			IRODSFileFactory fileFactory = accessObjectFactory.getIRODSFileFactory(account);

			TransferOptions transferOptions = new TransferOptions();
			transferOptions.setComputeAndVerifyChecksumAfterTransfer(true);

			TransferControlBlock transferControlBlock = DefaultTransferControlBlock.instance();
			transferControlBlock.setTransferOptions(transferOptions);

			DataTransferOperations dataTransferOperations = irodsFileSystem.getIRODSAccessObjectFactory().getDataTransferOperations(account);

			List<String> formats = sip.getAVFormats();

			for (String format : formats) {
				String localFilePath = sip.getAVMaterial(format, "FILE").get(0);
				File localFile = new File(localFilePath);

				IRODSFile irodsFile = fileFactory.instanceIRODSFile(sip.getId() + localFilePath.substring(localFilePath.lastIndexOf(".")));
				dataTransferOperations.putOperation(localFile, irodsFile, null, transferControlBlock);

				String irodsFilePath = irodsFile.getAbsolutePath();

				DataObjectAO dataObjectAO = accessObjectFactory.getDataObjectAO(account);
				DataObject dataObject = dataObjectAO.findByAbsolutePath(irodsFilePath);

				logger.debug("Transferred IRODSFile " + irodsFilePath + " size: " + dataObject.getDataSize() + " MD5: " + dataObject.getChecksum());

				sip.addFile(format, "IRODS", irodsFilePath, dataObject.getChecksum(), dataObject.getDataSize());

				irodsFileSystem.close();
			}

		} catch (JargonException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to connect with IRODS server or to create transfer object...");
		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve SIP...");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve AV Material...");
		} finally {
			DataManager.getInstance().releaseIP(sip);
		}
	}

	@WfService(name = "irods_get", version = "2.0.0")
	public void get(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get static parameters
		String host = sParams.get("irods.host");
		int port = Integer.parseInt(sParams.get("irods.port"));
		String username = sParams.get("irods.username");
		String password = sParams.get("irods.password");
		String homeDir = sParams.get("irods.home.dir");
		String zone = sParams.get("irods.zone");
		String defaultResource = sParams.get("irods.default.resource");

		// get dynamic parameters
		String dipID = dParamsString.get("dipID");
		String targetFolder = dParamsString.get("targetFolder");

		IRODSFileSystem irodsFileSystem = null;

		try {

			DIP dip = DataManager.getInstance().getDIPByID(dipID);

			String irodsFilePath = dip.getAVMaterial("application/mxf", "IRODS").get(0);

			irodsFileSystem = IRODSFileSystem.instance();

			IRODSAccount account = IRODSAccount.instance(host, port, username, password, homeDir, zone, defaultResource);

			IRODSFileFactory fileFactory = irodsFileSystem.getIRODSFileFactory(account);

			logger.debug("IRODS File Path: " + irodsFilePath);

			IRODSFileInputStream sourceFileIS = fileFactory.instanceIRODSFileInputStream(irodsFilePath);

			File targetFile = new File(targetFolder, FilenameUtils.getName(irodsFilePath));

			IOUtils.copy(sourceFileIS, new FileOutputStream(targetFile));

		} catch (JargonException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to connect with IRODS...");
		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve SIP...");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve AV Material...");
		} catch (IOException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to execute file operation...");
		} finally {
			try {
				if (irodsFileSystem != null)
					irodsFileSystem.close();
			} catch (JargonException e) {
			}
		}
	}

	@WfService(name = "irods_make_consumer_copy", version = "2.0.0")
	public void makeConsumerCopy(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		if (!Boolean.parseBoolean(dParamsString.get("isCopied"))) {

			// get static parameters
			String host = sParams.get("irods.host");
			int port = Integer.parseInt(sParams.get("irods.port"));
			String username = sParams.get("irods.username");
			String password = sParams.get("irods.password");
			String homeDir = sParams.get("irods.home.dir");
			String zone = sParams.get("irods.zone");
			String defaultResource = sParams.get("irods.default.resource");

			String destVolume = sParams.get("dest.path.volume").trim();
			String destFolder = sParams.get("dest.path.folder").trim();
			String outputFolder = destVolume + File.separator + destFolder;

			// get dynamic parameters
			String dipID = dParamsString.get("id");
			String mimetype = dParamsString.get("mimetype");

			IRODSFileSystem irodsFileSystem = null;

			DIP dip = null;

			try {

				dip = DataManager.getInstance().getDIPByID(dipID);

				// get MQ file
				String videoFile = null;
				if (mimetype != null) {
					List<String> videoFileList = dip.getAVMaterial(mimetype, "IRODS");
					if (videoFileList.size() > 0) {
						videoFile = videoFileList.get(0);

						irodsFileSystem = IRODSFileSystem.instance();

						IRODSAccount account = IRODSAccount.instance(host, port, username, password, homeDir, zone, defaultResource);

						IRODSFileFactory fileFactory = irodsFileSystem.getIRODSFileFactory(account);

						IRODSFileInputStream sourceFileIS = fileFactory.instanceIRODSFileInputStream(videoFile);

						String targetFileName = dipID + videoFile.substring(videoFile.lastIndexOf("."));

						IOUtils.copy(sourceFileIS, new FileOutputStream(new File(outputFolder, targetFileName)));

						dParamsString.put("isCopied", "true");
						dParamsString.put("target.file.name", targetFileName);
					}
				}
			} catch (JargonException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to connect with IRODS...");
			} catch (DataException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to retrieve SIP...");
			} catch (IPException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to retrieve AV Material...");
			} catch (IOException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to execute file operation...");
			} finally {
				try {
					if (irodsFileSystem != null)
						irodsFileSystem.close();
				} catch (JargonException e) {
				}
			}
		}
	}

	@WfService(name = "irods_fixity_restore", version = "2.0.0")
	public void fixityRestore(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get static parameters
		String host = sParams.get("irods.host");
		int port = Integer.parseInt(sParams.get("irods.port"));
		String username = sParams.get("irods.username");
		String password = sParams.get("irods.password");
		String homeDir = sParams.get("irods.home.dir");
		String zone = sParams.get("irods.zone");
		String defaultResource = sParams.get("irods.default.resource");

		// get dynamic parameters
		String aipID = dParamsString.get("aipID");
		String format = dParamsString.get("toBeRestored");

		if (format != null) {
			IRODSFileSystem irodsFileSystem = null;
			AIP aip = null;
			try {
				aip = DataManager.getInstance().getAIPByID(aipID);

				List<String> videoFileList = aip.getAVMaterial(format, "FILE");
				List<String> videoIRODSList = aip.getAVMaterial(format, "IRODS");
				if (videoFileList.size() > 0 && videoIRODSList.size() > 0) {
					irodsFileSystem = IRODSFileSystem.instance();
					IRODSAccount account = IRODSAccount.instance(host, port, username, password, homeDir, zone, defaultResource);
					IRODSFileFactory fileFactory = irodsFileSystem.getIRODSFileFactory(account);

					File illVideo = new File(videoFileList.get(0));
					IRODSFileInputStream healthyVideo = fileFactory.instanceIRODSFileInputStream(videoIRODSList.get(0));

					IOUtils.copy(healthyVideo, new FileOutputStream(illVideo));

					JSONObject result = new JSONObject();
					result.put("status", "restored");
					result.put("from", videoIRODSList.get(0));
					result.put("to", illVideo.getAbsolutePath());
					result.put("MD5", aip.getChecksum(format, "MD5"));
					dParamsString.put("result", result.toString());

					dParamsString.remove("toBeRestored");
				}
			} catch (JargonException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to connect with IRODS...");
			} catch (DataException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to retrieve SIP...");
			} catch (IPException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to retrieve AV Material...");
			} catch (IOException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to execute file operation...");
			} catch (JSONException e) {
				e.printStackTrace();
				throw new TaskExecutionFailedException("Unable to create JSON response...");
			} finally {
				DataManager.getInstance().releaseIP(aip);
				try {
					if (irodsFileSystem != null)
						irodsFileSystem.close();
				} catch (JargonException e) {
				}
			}
		}
	}
}
