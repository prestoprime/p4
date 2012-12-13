/**
 * P4GUI.java
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
package eu.prestoprime.p4gui;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public abstract class P4GUI {

	private static final Logger logger = Logger.getLogger(P4GUI.class);

	public static final String USER_BEAN_NAME = "user";
	public static final String JOBS_BEAN_NAME = "jobList";
	public static final String SIP_BEAN_NAME = "currentSip";

	public static final String SHORT_DATE_PATTERN = "yyyy-MM-dd HH:mm";

	public static enum P4guiProperty {
		GUI_VERSION("gui.version", "2.1.1"), DERBY_HOME("derby.home", "/opt/p4/p4guidb"), MAIL_SERVER("mail.server", "62.152.112.34"), MAIL_ADDRESS("mail.address", "p4admin@eurixgroup.com");

		private String key;
		private String defaultValue;

		private P4guiProperty(String name, String defaultValue) {
			this.key = name;
			this.defaultValue = defaultValue;
		}

		public String getKey() {
			return key;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		@Override
		public String toString() {
			return key;
		}
	}

	private static Properties loadProperties() {
		Properties properties = new Properties();
		try {
			logger.info("Loading default properties from p4gui-default.xml");
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("p4gui-default.xml");
			properties.loadFromXML(is);
		} catch (Exception e) {
			logger.info("Loading default properties from Java code...");
			for (P4guiProperty property : P4guiProperty.values())
				properties.setProperty(property.getKey(), property.getDefaultValue());
		}
		return properties;
	}

	public static String getProperty(P4guiProperty property) {
		return P4GUI.loadProperties().getProperty(property.getKey(), property.getDefaultValue());
	}

	@Deprecated
	public static String getDRACMAServer() {
		return P4GUI.loadProperties().getProperty("dracma.server");
	}

	@Deprecated
	public static String getDRACMAStorage() {
		return P4GUI.loadProperties().getProperty("dracma.storage");
	}
}
