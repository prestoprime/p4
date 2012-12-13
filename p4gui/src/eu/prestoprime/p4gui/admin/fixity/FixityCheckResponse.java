/**
 * FixityCheckResponse.java
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
package eu.prestoprime.p4gui.admin.fixity;

public class FixityCheckResponse {

	private int code;
	private String id;
	private String path;
	private String URN;

	public FixityCheckResponse(String line) {
		String[] fields = line.split("\t");
		code = Integer.parseInt(fields[0]);
		id = fields[1];
		switch (code) {
		case 200:
			path = "";
			URN = "";
			break;

		case 400:
			path = fields[2];
			URN = fields[3];
			break;

		case 500:
			break;
		}
	}

	public int getCode() {
		return code;
	}

	public String getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public String getURN() {
		return URN;
	}
}
