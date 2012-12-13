/**
 * Resource.java
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
package eu.prestoprime.p4gui.util.parse;

import java.util.ArrayList;

public class Resource {

	private String id;
	private String mimetype;
	private ArrayList<Location> locations;
	private String checksumtype;
	private String checksum;

	public Resource(String id, String mimetype, ArrayList<Location> locations, String checksumtype, String checksum) {
		this.id = id;
		this.mimetype = mimetype;
		this.locations = locations;
		this.checksumtype = checksumtype;
		this.checksum = checksum;
	}

	public String getId() {
		return id;
	}

	public String getMimetype() {
		return mimetype;
	}

	public ArrayList<Location> getLocations() {
		return locations;
	}

	public String getChecksumtype() {
		return checksumtype;
	}

	public String getChecksum() {
		return checksum;
	}
}
