/**
 * PersistenceManager.java
 * Author: Francesco Gallo (gallo@eurix.it)
 * Contributors: Francesco Rosso (rosso@eurix.it)
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
package eu.prestoprime.datamanagement;

import it.eurix.archtools.persistence.DatabaseException;
import it.eurix.archtools.persistence.DatabaseManagerFactory;
import it.eurix.archtools.persistence.impl.ExistDatabaseManager;
import eu.prestoprime.conf.P4StartupConfig;
import eu.prestoprime.conf.P4StartupConfig.StartupProperty;

/**
 * Implements a connection to persistence DB, offering CRUD interfaces.
 */
public class P4PersistenceManager extends ExistDatabaseManager<P4PersistenceManager.P4Collection>{

	public static enum P4Collection {
		ADMIN_COLLECTION("admin"), WF_COLLECTION("wf"), AIP_COLLECTION("aip"), DMD_COLLECTION("dmd"), RIGHTSMD_COLLECTION("rightsmd"), TECHMD_COLLECTION("techmd"), SOURCEMD_COLLECTION("sourcemd"), DIGIPROVMD_COLLECTION("digiprovmd"), TEMP_COLLECTION("temp");
		
		private String collectionName;
		private P4Collection(String collectionName) {
			this.collectionName = collectionName;
		}
		
		@Deprecated
		public static P4Collection getP4Collection(String collectionName) throws DatabaseException {
			collectionName = collectionName.toLowerCase();
			for (P4Collection p4collection : P4Collection.values()) {
				if (collectionName.equals(p4collection.collectionName))
					return p4collection;
			}
			throw new DatabaseException("Unable to find a P4Collection for collection " + collectionName + "...");
		}
		
		@Override
		public String toString() {
			return collectionName;
		}
	}
	
	public P4PersistenceManager() {
		super(P4StartupConfig.getInstance().getProperty(StartupProperty.DBhostname),
				Integer.parseInt(P4StartupConfig.getInstance().getProperty(StartupProperty.DBport)),
				P4StartupConfig.getInstance().getProperty(StartupProperty.DBcontext),
				P4StartupConfig.getInstance().getProperty(StartupProperty.DBusername),
				P4StartupConfig.getInstance().getProperty(StartupProperty.DBpassword),
				P4StartupConfig.getInstance().getProperty(StartupProperty.DBrootcollection));
	}
	
	public static P4PersistenceManager getInstance() {
		try {
			return DatabaseManagerFactory.getInstance().getDB(P4PersistenceManager.class);
		} catch (DatabaseException e) {
			logger.error(e.getMessage() + "...");
			throw new RuntimeException(e.getMessage());
		}
	}
}
