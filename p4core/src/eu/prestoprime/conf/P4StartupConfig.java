/**
 * StartupConfig.java
 * Author: Francesco Rosso (rosso@eurix.it)
 * 
 * This file is part of PrestoPRIME Preservation Platform (P4).
 * 
 * Copyright (C) 2013 EURIX Srl, Torino, Italy
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
package eu.prestoprime.conf;

import it.eurix.archtools.config.StartupConfig;

import java.io.InputStream;
import java.util.jar.Manifest;


public class P4StartupConfig extends StartupConfig<P4StartupConfig.StartupProperty> {

	public static enum StartupProperty {
		DBhostname,
		DBport,
		DBcontext,
		DBusername,
		DBpassword,
		DBrootcollection
	}
	
	private static P4StartupConfig instance;
	
	private P4StartupConfig() {
		super(".p4");
	}
	
	public static P4StartupConfig getInstance() {
		if (instance == null)
			instance = new P4StartupConfig();
		return instance;
	}
	
	public static String getVersion() {
		try {
			InputStream is = P4StartupConfig.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
			Manifest manifest = new Manifest(is);
			return manifest.getMainAttributes().getValue("Version");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
