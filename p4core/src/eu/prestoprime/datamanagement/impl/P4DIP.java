/**
 * P4DIP.java
 * Author: Francesco Rosso (rosso@eurix.it)
 * Contributors: Francesco Gallo (gallo@eurix.it)
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
package eu.prestoprime.datamanagement.impl;

import it.eurix.archtools.data.model.DIP;
import it.eurix.archtools.persistence.DatabaseException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Node;

import eu.prestoprime.conf.ConfigurationManager;
import eu.prestoprime.conf.P4PropertyManager;
import eu.prestoprime.conf.P4PropertyManager.P4Property;
import eu.prestoprime.datamanagement.P4PersistenceManager;
import eu.prestoprime.datamanagement.P4PersistenceManager.P4Collection;
import eu.prestoprime.model.datatypes.Datatype;

public class P4DIP extends P4InformationPackage implements DIP {

	public P4DIP(String id, Node content) {
		super(id, content);
	}

	@Override
	public void selfRelease() {

	}

	@Override
	public List<String> getDCField(DCField field) throws P4IPException {
		return this.getDCFields().get(field.toString());
	}

	public String getPubFolder() {
		P4PropertyManager properties = ConfigurationManager.getPropertyInstance();

		return properties.getProperty(P4Property.P4_STORAGE_VOLUME) + File.separator + properties.getProperty(P4Property.P4_STORAGE_FOLDER) + File.separator + id;
	}

	public URL getPubURL(String pubPath) throws P4IPException {
		P4PropertyManager properties = ConfigurationManager.getPropertyInstance();

		String file = pubPath.substring(this.getPubFolder().length());

		try {
			URL url = new URL(properties.getProperty(P4Property.P4_URL) + File.separator + properties.getProperty(P4Property.P4_STORAGE_FOLDER) + File.separator + id + File.separator + file);

			return url;
		} catch (MalformedURLException e) {
			throw new P4IPException("Unable to create valid URL...");
		}
	}

	@Override
	public List<URL> getFrames() throws P4IPException {
		P4PropertyManager properties = ConfigurationManager.getPropertyInstance();

		String framesPath = this.getPubFolder() + File.separator + properties.getProperty(P4Property.P4_FRAMES_FOLDER);

		List<String> frames = Arrays.asList(new File(framesPath).list());

		Collections.sort(frames, new Comparator<String>() {

			@Override
			public int compare(String frame1, String frame2) {
				String frameName1 = FilenameUtils.removeExtension(frame1);
				String frameName2 = FilenameUtils.removeExtension(frame2);
				String nFrame1 = frameName1.split("F")[0];
				String nFrame2 = frameName2.split("F")[0];

				int key1 = Integer.parseInt(nFrame1);
				int key2 = Integer.parseInt(FilenameUtils.removeExtension(nFrame2));
				return key1 - key2;
			}
		});

		List<URL> urls = new ArrayList<>();
		try {
			String p4URL = properties.getProperty(P4Property.P4_URL);
			String p4Storage = properties.getProperty(P4Property.P4_STORAGE_FOLDER);
			String p4Frames = properties.getProperty(P4Property.P4_FRAMES_FOLDER);
			for (String frame : frames)
				urls.add(new URL(p4URL + File.separator + p4Storage + File.separator + id + File.separator + p4Frames + File.separator + frame));
		} catch (MalformedURLException e) {
			throw new P4IPException("Unable to create valid URL...");
		}

		return urls;
	}

	@Override
	public URL getThumbnail() throws P4IPException {

		// Return a random frame as thumbnail, used to reduce the preview of
		// black frames at the beginning of the video

		List<URL> frameList = this.getFrames();

		if (frameList == null || frameList.size() == 0)
			throw new P4IPException("Unable to retrieve thumbnail URL...");
		else if (frameList.size() == 1)
			return frameList.get(0);
		else {
			Random rnd = new Random();
			int rndFrameNumber = rnd.nextInt(frameList.size());
			return frameList.get(rndFrameNumber);
		}

	}

	@Override
	public URL getGraph() throws P4IPException {
		P4PropertyManager properties = ConfigurationManager.getPropertyInstance();

		String graphPath = this.getPubFolder() + File.separator + properties.getProperty(P4Property.P4_GRAPH_FOLDER);

		String[] graphs = new File(graphPath).list();
		if (graphs != null && graphs.length > 0)
			try {
				return new URL(properties.getProperty(P4Property.P4_URL) + File.separator + properties.getProperty(P4Property.P4_STORAGE_FOLDER) + File.separator + id + File.separator + properties.getProperty(P4Property.P4_GRAPH_FOLDER) + File.separator + graphs[0]);
			} catch (MalformedURLException e) {
				throw new P4IPException("Unable to create valid URL...");
			}
		throw new P4IPException("No graph found...");
	}

	@Override
	public String hasDatatype(String type) throws P4IPException {

		Datatype dataType = ConfigurationManager.getDataTypesInstance().getDatatype(type);

		if (dataType == null)
			return null;
		List<String> dates = null;

		if (dataType.getSection().getStatus() == null || dataType.getSection().getStatus().isEmpty()) {
			dates = this.executeQuery("//mets:" + dataType.getSection().getType() + "/mets:mdRef[@LABEL = '" + dataType.getRef().getLabel() + "']/@CREATED");
		} else {
			dates = this.executeQuery("//mets:" + dataType.getSection().getType() + "[@STATUS = '" + dataType.getSection().getStatus() + "']/mets:mdRef[@LABEL = '" + dataType.getRef().getLabel() + "']/@CREATED");
		}

		return (dates != null && dates.size() > 0) ? dates.get(0) : null;

	}

	@Override
	public List<String> getMDResource(String type) throws P4IPException {

		Datatype dataType = ConfigurationManager.getDataTypesInstance().getDatatype(type);

		if (dataType == null)
			return null;
		List<String> resources = null;

		if (dataType.getSection().getStatus() == null || dataType.getSection().getStatus().isEmpty()) {
			resources = this.executeQuery("//mets:" + dataType.getSection().getType() + "/mets:mdRef[@LABEL = '" + dataType.getRef().getLabel() + "']/@xlink:href");
		} else {
			resources = this.executeQuery("//mets:" + dataType.getSection().getType() + "[@STATUS = '" + dataType.getSection().getStatus() + "']/mets:mdRef[@LABEL = '" + dataType.getRef().getLabel() + "']/@xlink:href");
		}

		String p4PH = ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_PLACEHOLDER);
		String p4URL = ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_WS_URL);

		List<String> published = new ArrayList<>();
		if (resources != null) {
			for (String resource : resources) {
				String pubres = resource.replace(p4PH, p4URL);
				published.add(pubres);
			}
		}

		return published;
	}

	@Override
	public List<Node> getMDResourceAsDOM(String type) throws P4IPException {

		List<Node> nodeList = null;

		Datatype dataType = ConfigurationManager.getDataTypesInstance().getDatatype(type);

		if (dataType == null)
			return null;

		List<String> resources = null;

		if (dataType.getSection().getStatus() == null || dataType.getSection().getStatus().isEmpty()) {
			resources = this.executeQuery("//mets:" + dataType.getSection().getType() + "/mets:mdRef[@LABEL = '" + dataType.getRef().getLabel() + "']/@xlink:href");
		} else {
			resources = this.executeQuery("//mets:" + dataType.getSection().getType() + "[@STATUS = '" + dataType.getSection().getStatus() + "']/mets:mdRef[@LABEL = '" + dataType.getRef().getLabel() + "']/@xlink:href");
		}

		if (resources == null || resources.size() == 0)
			return null;
		else {
			nodeList = new ArrayList<>();

			for (String resource : resources) {

				String[] resPath = resource.split("/");

				String resId = resPath[resPath.length - 1];

				try {
					P4Collection collectionName = null;

					if (dataType.getSection().getType().equals("dmdSec"))
						collectionName = P4Collection.DMD_COLLECTION;
					else
						collectionName = P4Collection.getP4Collection(dataType.getSection().getType().toLowerCase());

					Node resNode = P4PersistenceManager.getInstance().readXMLResource(collectionName, resId);

					nodeList.add(resNode);
				} catch (DatabaseException e) {
					e.printStackTrace();
					throw new P4IPException("Unable to find DIP resource " + resId + "...");
				}

			}

		}

		return nodeList;
	}

	/**
	 * Get full ffprobe output from the referenced DNX xml.
	 * 
	 * @return
	 * @throws P4IPException
	 */
	public List<Node> getTechMdAsDOM() throws P4IPException {

		final P4Collection collectionName = P4Collection.TECHMD_COLLECTION;
		final String xQuery = "//mets:techMD[@ID='ffprobe']/mets:mdRef[@OTHERMDTYPE='DNX']/@xlink:href";

		List<Node> nodeList = null;
		List<String> resources = null;

		resources = this.executeQuery(xQuery);

		if (resources != null && !resources.isEmpty()) {

			nodeList = new ArrayList<>();

			for (String resource : resources) {

				String[] resPath = resource.split("/");
				String resId = resPath[resPath.length - 1];

				try {
					Node resNode = P4PersistenceManager.getInstance().readXMLResource(collectionName, resId);
					nodeList.add(resNode);
				} catch (DatabaseException e) {
					e.printStackTrace();
					throw new P4IPException("Unable to find DIP resource " + resId + " in Collection " + collectionName + "...");
				}
			}
		}

		return nodeList;
	}
}
