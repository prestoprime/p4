/**
 * FFprobe.java
 * Author: Francesco Gallo (gallo@eurix.it)
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
package eu.prestoprime.plugin.p4.tools;

import eu.prestoprime.conf.Constants;
import eu.prestoprime.tools.P4Tool;
import eu.prestoprime.tools.ToolException;

public class FFprobe extends P4Tool {

	public FFprobe() {
		super(Constants.FFPROBE_NAME);
	}

	public void extract(String inputFile) throws ToolException {
		this.execute("-v", "quiet", "-print_format", "json", "-show_format", "-show_streams", inputFile);
		this.attributeMap.put("json", getProcessOutput());
	}
}