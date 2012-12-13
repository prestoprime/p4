/**
 * DC.java
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

import java.util.LinkedHashMap;
import java.util.Set;

public class DC {

	private LinkedHashMap<String, String> fields;

	public DC() {
		fields = new LinkedHashMap<String, String>();
		fields.put("identifier", "");
		fields.put("title", "");
		fields.put("description", "");
		fields.put("creator", "");
		fields.put("source", "");
		fields.put("format", "");
		fields.put("language", "");
		fields.put("publisher", "");
	}

	public void setDcField(String key, String value) {
		if (fields.containsKey(key)) {
			fields.remove(key);
			fields.put(key, value);
		}
	}

	public String getDcField(String key) {
		return fields.get(key);
	}

	public Set<String> getKeySet() {
		fields.put("identifier", fields.remove("identifier"));
		fields.put("title", fields.remove("title"));
		fields.put("description", fields.remove("description"));
		fields.put("creator", fields.remove("creator"));
		fields.put("source", fields.remove("source"));
		fields.put("format", fields.remove("format"));
		fields.put("language", fields.remove("language"));
		fields.put("publisher", fields.remove("publisher"));
		return fields.keySet();
	}
}
