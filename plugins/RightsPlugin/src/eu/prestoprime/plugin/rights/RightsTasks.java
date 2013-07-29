/**
 * RightsTasks.java
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
package eu.prestoprime.plugin.rights;

import it.eurix.archtools.data.DataException;
import it.eurix.archtools.data.model.AIP;
import it.eurix.archtools.data.model.DIP;
import it.eurix.archtools.data.model.DIP.DCField;
import it.eurix.archtools.data.model.IPException;
import it.eurix.archtools.data.model.SIP;
import it.eurix.archtools.persistence.DatabaseException;
import it.eurix.archtools.tool.ToolException;
import it.eurix.archtools.tool.ToolOutput;
import it.eurix.archtools.tool.impl.MessageDigestExtractor;
import it.eurix.archtools.workflow.exceptions.TaskExecutionFailedException;
import it.eurix.archtools.workflow.plugin.WfPlugin;
import it.eurix.archtools.workflow.plugin.WfPlugin.WfService;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import eu.prestoprime.conf.ConfigurationManager;
import eu.prestoprime.conf.P4PropertyManager.P4Property;
import eu.prestoprime.datamanagement.P4DataManager;
import eu.prestoprime.model.ext.rights.Results;
import eu.prestoprime.model.ext.rights.Results.Row;
import eu.prestoprime.model.ext.rights.RightsIndex;
import eu.prestoprime.plugin.p4.tools.Dot;
import eu.prestoprime.plugin.p4.tools.XSLTProc;

@WfPlugin(name = "RightsPlugin")
public class RightsTasks {

	private static final Logger logger = LoggerFactory.getLogger(RightsTasks.class);
	
	@WfService(name = "rights_management", version = "2.0.0")
	public void management(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamFile) throws TaskExecutionFailedException {

		// get sipID
		String sipID = dParamsString.get("sipID");

		// get sip
		SIP sip = null;
		try {
			sip = P4DataManager.getInstance().getSIPByID(sipID);

			// check if rights are either in mdWrap or in mdRef
			List<String> rightsList = sip.executeQuery("//mets:rightsMD/mets:mdRef/@xlink:href");

			Node rights = null;
			if (rightsList.size() != 0) {
				// in mdRef
				String rightsURL = rightsList.get(0);

				// download
				try {
					HttpClient client = new DefaultHttpClient();
					HttpUriRequest request = new HttpGet(rightsURL);
					HttpEntity entity = client.execute(request).getEntity();
					if (entity != null) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
						String line;
						StringBuffer sb = new StringBuffer();
						while ((line = reader.readLine()) != null) {
							sb.append(line.trim());
						}
						// parse
						rights = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(sb.toString().getBytes()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// in mdWrap
				// parse
				List<Node> rightsNodeList = sip.executeNodeQuery("//mets:rightsMD/mets:mdWrap/mets:xmlData/*[1]");
				if (rightsNodeList.size() != 0) {
					rights = rightsNodeList.get(0);
				}
			}

			if (rights != null) {
				TransformerFactory.newInstance().newTransformer().transform(new DOMSource(rights), new StreamResult(System.out));

				// add rights to sip resources
				String rightsID = sip.setRights(rights);

				// send rightsID to other tasks
				dParamsString.put("rightsID", rightsID);

				// create rightsFile
				File owlFile = File.createTempFile("rights-", ".owl");
				TransformerFactory.newInstance().newTransformer().transform(new DOMSource(rights), new StreamResult(owlFile));

				// create graph
				XSLTProc xsltproc = new XSLTProc();
				String digraphxsl = xsltproc.addResourceFile("writedigraph.xsl");
				xsltproc.setXSLFile(digraphxsl);
				xsltproc.addStringParam("colors", "on");

				xsltproc.extract(owlFile.getAbsolutePath());
				String dotGraphFilePath = xsltproc.getOutputFile();

				String owlGraphName = rightsID + ".png";
				File owlGraph = new File(dParamsString.get("graphFolder"), owlGraphName);

				new Dot().createGraph(dotGraphFilePath, owlGraph.getAbsolutePath());

				// index rights
				RightsUtils.indexRights(sipID, owlFile);
				owlFile.delete();

			} else {
				logger.debug("No rights found...");
				// there aren't rights
			}
		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to retrieve the SIP...");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to add rights to the SIP...");
		} catch (IOException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to write rights on file...");
		} catch (ToolException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to run XSLTproc...");
		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to extract graph...");
		} finally {
			P4DataManager.getInstance().releaseIP(sip);
		}
	}

	@WfService(name = "configure_rights_storage", version = "0.8.0")
	public void configureStorage(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		String aipID = dParamsString.get("id");

		String p4storeFolder = ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_STORAGE_VOLUME) + File.separator + ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_STORAGE_FOLDER) + File.separator + aipID;

		String graphFolder = p4storeFolder + File.separator + ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_GRAPH_FOLDER);
		new File(graphFolder).mkdirs();
		dParamsString.put("graphFolder", graphFolder);
	}

	@WfService(name = "rights_update", version = "0.8.0")
	public void update(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// retrieve dynamic parameters
		String id = dParamsString.get("id");
		File rightsFile = dParamsFile.get("resultFile");

		// update AIP
		try {
			// parse resultFile
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Node node = dbf.newDocumentBuilder().parse(rightsFile);

			// get AIP
			AIP aip = P4DataManager.getInstance().getAIPByID(id);

			// update AIP
			String rightsID = aip.updateSection(node, "rights");

			// add MPEG-21 MCO as additional format
			List<String> formatList = aip.getDCField(DCField.format);
			boolean hasMPEG21MCOFormat = false;
			for (String format : formatList) {
				if (format.contains("MPEG-21 MCO")) {
					hasMPEG21MCOFormat = true;
					break;
				}
			}
			if (!hasMPEG21MCOFormat) {
				formatList.add("MPEG-21 MCO (ISO/IEC 21000-21)");
				aip.setDCField(DCField.format, formatList);
			}

			// create graph
			XSLTProc xsltproc = new XSLTProc();
			String digraphxsl = xsltproc.addResourceFile("writedigraph.xsl");
			xsltproc.setXSLFile(digraphxsl);
			xsltproc.addStringParam("colors", "on");

			xsltproc.extract(rightsFile.getAbsolutePath());
			String dotGraphFilePath = xsltproc.getOutputFile();

			String owlGraphName = rightsID + ".png";
			File owlGraph = new File(dParamsString.get("graphFolder"), owlGraphName);

			new Dot().createGraph(dotGraphFilePath, owlGraph.getAbsolutePath());

			// index rights
			RightsUtils.indexRights(id, rightsFile);
			rightsFile.delete();

			// release AIP
			P4DataManager.getInstance().releaseIP(aip);

			logger.debug("Updated QA section...");
		} catch (Exception e) {
			throw new TaskExecutionFailedException("Unable to update AIP...\n" + e.getMessage());
		}
	}
	
	@WfService(name = "query_rights_by_owl", version = "1.2.0")
	public void queryRights(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {

		// get dynamic parameters
		File rightsFile = dParamsFile.get("rightsFile");

		try {
			Results queryResults = new Results();
			long searchTime = Long.MAX_VALUE, totalTime = Long.MAX_VALUE;

			long time1 = System.currentTimeMillis();

			List<String> rightsResult = queryRights(rightsFile.getAbsolutePath());

			long time2 = System.currentTimeMillis();

			searchTime = time2 - time1;

			if (rightsResult == null) {
				queryResults.setFailure("Error retrieving information");

			} else {

				logger.debug("Number of RightsModel objects: " + rightsResult.size());

				queryResults.setSuccess(true);

				for (String aipId : rightsResult) {

					logger.debug("Retrieving rigths info for AIP " + aipId);
					Row row = new Row();
					row.setEeIdentifier(aipId);

					DIP dip = P4DataManager.getInstance().getDIPByID(aipId);

					StringBuffer sb = new StringBuffer();

					for (String title : dip.getDCField(DCField.title)) {
						sb.append(title + " ");
					}
					sb.append("###");
					for (String description : dip.getDCField(DCField.description)) {
						sb.append(description + " ");
					}
					row.setContent(sb.toString());

					String downloadIcon = null;
					if (dip.getThumbnail() != null)
						downloadIcon = dip.getThumbnail().toString();
					logger.debug("Icon URL for download: " + downloadIcon);

					row.setIcon(downloadIcon);

					String graphHRef = dip.getGraph().toString();

					logger.debug("Graph URL for download: " + graphHRef);

					row.setRightsgraph(graphHRef);

					queryResults.getRow().add(row);
					logger.debug("Added query results for AIP: " + aipId);

				}

				StringWriter sw = new StringWriter();
				RightsUtils.getRightsContext().createMarshaller().marshal(queryResults, sw);
				dParamsString.put("result", sw.toString());

				long time3 = System.currentTimeMillis();

				totalTime = time3 - time1;
			}

			logger.debug("Search time: " + searchTime + " ms");
			logger.debug("Total time: " + totalTime + " ms");

		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to execute rights query");
		}
	}

	/**
	 * Execution: xsltproc --stringparam ppavro /tmp/ppavro.owl --stringparam
	 * countrycodes /tmp/ebu_Iso3166CountryCodeCS.xml --stringparam
	 * languagecodes /tmp/ebu_Iso639_1LanguageCodeCS.xml --stringparam querydoc
	 * /tmp/query.owl /tmp/RightsCompareFromIndex.xsl /tmp/RightsIndex.xml
	 * 
	 * @throws DataException
	 * @throws ToolException
	 * @throws IOException
	 * @throws JAXBException
	 */

	private List<String> queryRights(String owlQueryFilePath) throws DataException, ToolException, IOException, JAXBException {

		// List<RightsModel> resultRights = new ArrayList<RightsModel>();
		List<String> resultRights = new ArrayList<String>();

		XSLTProc xsltproc = new XSLTProc();

		logger.debug("Initialized XSLTProc...");

		// step 1: clean up OWL file
		String byPassXsl = xsltproc.addResourceFile("ByPassIntrsctns.xsl");
		xsltproc.setXSLFile(byPassXsl);
		xsltproc.extract(owlQueryFilePath);
		String step1Result = xsltproc.getOutputFile();

		// step2: extract RightsInstance

		// stylesheet
		String rightsxsl = xsltproc.addResourceFile("RightsCompareFromIndex.xsl");
		xsltproc.setXSLFile(rightsxsl);
		// add stringparams
		String ppavro = xsltproc.addResourceFile("ppavro.owl");
		xsltproc.addStringParam("ppavro", ppavro);
		String countrycodes = xsltproc.addResourceFile("ebu_Iso3166CountryCodeCS.xml");
		xsltproc.addStringParam("countrycodes", countrycodes);
		String languagecodes = xsltproc.addResourceFile("ebu_Iso639_LanguageCodeCS.xml");
		xsltproc.addStringParam("languagecodes", languagecodes);
		xsltproc.addStringParam("querydoc", step1Result);

		// index file
		RightsIndex rightsIndex = RightsUtils.getRightsIndex();

		if (rightsIndex == null)
			return null;

		File indexTempFile = File.createTempFile("RightsIndex-", ".xml");
		indexTempFile.deleteOnExit();

		RightsUtils.getRightsContext().createMarshaller().marshal(rightsIndex, indexTempFile);

		String indexFile = indexTempFile.getAbsolutePath();

		logger.debug("Looping on Rights Index...");

		xsltproc.extract(indexFile);
		String resultFilePath = xsltproc.getOutputFile();

		logger.debug("Scanning file: " + resultFilePath);

		FileInputStream resultInputStream = new FileInputStream(resultFilePath);

		Scanner scanner = new Scanner(resultInputStream);

		while (scanner.hasNextLine()) {
			String line = (String) scanner.nextLine();
			if (line.startsWith("true")) {
				logger.debug("--------------------------------------------------------");
				logger.debug("Found matching EE with identifier: " + line.split(",")[1]);
				String dcIdentifier = line.split(",")[1].trim();
				String dcIdentifierNoFrags = dcIdentifier.split("#")[0]; // remove
																			// media
																			// fragments
				logger.debug("DC identifier: " + dcIdentifierNoFrags);

				Map<String, String> elements = new HashMap<String, String>();
				elements.put("identifier", dcIdentifierNoFrags);
				String aipId = P4DataManager.getInstance().getAIPByDCID(dcIdentifierNoFrags);

				/*
				 * logger.debug("Retrieving RightsModel for AIP "+aipId);
				 * 
				 * List<RightsModel> rightsModelList =
				 * manager.findRightsByAIPId(aipId);
				 * 
				 * if(rightsModelList.size()>1){
				 * logger.error("RightsModel is not unique!!! Skipping...");
				 * continue; }
				 * 
				 * resultRights.add(rightsModelList.get(0));
				 */
				resultRights.add(aipId);
				logger.debug("Added AIP to RightsResult list: " + aipId);
				logger.debug("--------------------------------------------------------");

			}
		}

		logger.debug("XSLTProc elapsed time: " + xsltproc.getExecTime());

		return resultRights;

	}
	
	@WfService(name = "rebuild_rights_index", version = "2.2.0")
	public void rebuildIndex(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamFile) throws TaskExecutionFailedException {

		try {

			RightsUtils.deleteRightsIndex();

			logger.debug("Successfully cleared search index...");

			List<String> aipIdList;

			aipIdList = P4DataManager.getInstance().getAllAIP(null);

			if (aipIdList.isEmpty()) {
				logger.debug("No AIPs found.");
			}

			int indexedAIP = 0;

			for (String aipId : aipIdList) {

				logger.debug("Indexing AIP " + aipId);

				DIP dip = P4DataManager.getInstance().getDIPByID(aipId);

				if (Boolean.valueOf(dip.hasDatatype("rights"))) {

					List<Node> rightsNodeList = dip.getMDResourceAsDOM("rights");

					if (rightsNodeList == null || rightsNodeList.size() == 0)
						continue;

					Node rights = rightsNodeList.get(0);

					// create rightsFile
					File owlFile = File.createTempFile("rights-", ".owl");
					TransformerFactory.newInstance().newTransformer().transform(new DOMSource(rights), new StreamResult(owlFile));

					// index rights
					RightsUtils.indexRights(aipId, owlFile);
					owlFile.delete();

					logger.debug("Successfully indexed rights for AIP: " + aipId);

					indexedAIP++;
				}

			}

			logger.debug("Rights Indexing completed. Number of indexed AIPs: " + indexedAIP + " out of " + aipIdList.size());

		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to access data from DB");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to get rights from DIP");
		} catch (DatabaseException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to store Rights Index to DB");
		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to rebuild rights index");
		}
	}
	
	@WfService(name = "avmaterial_update", version = "2.2.0")
	public void addAVToRigthsOnly(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamFile) throws TaskExecutionFailedException {

		// dynamic parameters
		String aipID = dParamsString.get("aipID");
		String mimeType = dParamsString.get("mimetype");
		String filePath = dParamsString.get("filePath");
		String fileMD5 = dParamsString.get("fileMD5");
		long fileSize = 0;

		DIP dip = null;
		try {
			dip = P4DataManager.getInstance().getDIPByID(aipID);

			// if is rights_only
			if (dip.hasRights() && !dip.hasAVMaterial()) {
				// check file
				File file = new File(filePath);
				if (file.isFile()) {
					fileSize = file.length();

					MessageDigestExtractor mde = new MessageDigestExtractor();
					ToolOutput<MessageDigestExtractor.AttributeType> output = mde.extract(filePath);
					if (fileMD5 != null && output.getAttribute(MessageDigestExtractor.AttributeType.MD5) != fileMD5) {
						throw new TaskExecutionFailedException("MD5 doesn't corresponds...");
					}
					fileMD5 = output.getAttribute(MessageDigestExtractor.AttributeType.MD5);
				} else {
					throw new TaskExecutionFailedException("Invalid file...");
				}
			} else {
				throw new TaskExecutionFailedException("Trying to update a non rights_only AIP with aipID " + aipID + "...");
			}
		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Could not retrieve AIP " + aipID + "...");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Could not determine if AIP has rights nor AVMaterial...");
		} catch (ToolException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to extract MD5 from file " + filePath + "...");
		} finally {
			dip = null;
		}

		try {
			P4DataManager.getInstance().invalidateAIP(aipID);
		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to invalidate AIP with aipID " + aipID + "...");
		}

		String sipID = aipID;
		dParamsString.remove("aipID");
		dParamsString.put("sipID", sipID);

		SIP sip = null;
		try {
			sip = P4DataManager.getInstance().getSIPByID(sipID);

			sip.addExternalFile(mimeType, filePath, fileMD5, fileSize);
		} catch (DataException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to delete rights_only AIP with aipID " + aipID + "...");
		} catch (IPException e) {
			e.printStackTrace();
			throw new TaskExecutionFailedException("Unable to add new file to invalidated SIP " + sipID + "(ex AIP)...");
		} finally {
			P4DataManager.getInstance().releaseIP(sip);
		}
	}
}
