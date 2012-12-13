/**
 * SearchConstants.java
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
package eu.prestoprime.search;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SearchConstants {

	private static final Logger LOGGER = Logger.getLogger(SearchConstants.class);

	private static final String PROPERTY_FILENAME = "solr.properties";

	private static Properties props = null;

	public static final String KV_SEP = "§§"; // the char for separating key
												// value pairs in a wrapped
												// param string (like & in URLs)
	public static final String KV_CON = "::"; // the char for connecting key
												// value pairs in a wrapped
												// param string (like = in URLs)

	private static List<String> activeFacets = null;

	private static void loadProperties() {

		try {
			LOGGER.debug("loading file: " + PROPERTY_FILENAME);
			InputStream propertiesAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTY_FILENAME);
			if (propertiesAsStream == null) {
				propertiesAsStream = new FileInputStream(PROPERTY_FILENAME);
			}

			props = new Properties();
			props.load(propertiesAsStream);
		} catch (InvalidPropertiesFormatException e) {
			LOGGER.fatal("Invalid properties file: " + PROPERTY_FILENAME + "\n" + e.getMessage());
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			LOGGER.fatal("Could not load properties file: " + PROPERTY_FILENAME + "\n" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.fatal("Could not load: " + PROPERTY_FILENAME + "\n" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.fatal("Could not load: " + PROPERTY_FILENAME + "\n" + e.getMessage());
			e.printStackTrace();
		}
	}

	public static int getInt(String name) {

		return Integer.parseInt(getString(name));
	}

	public static String getString(String name) {
		if (props == null) {
			loadProperties();
		}
		return props.getProperty(name);
	}

	public static List<String> getActiveFacets() {
		if (activeFacets == null) {
			activeFacets = getCSV("activeFacets");
		}
		return activeFacets;
	}

	private static List<String> getCSV(String propName) {
		String csv = getString(propName);
		String[] csvValues = csv.split(",");
		LOGGER.info("Activating facets: ");
		for (String s : csvValues) {
			LOGGER.info(s);
		}
		return Arrays.asList(csvValues);
	}

}
