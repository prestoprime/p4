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

import it.eurix.archtools.tool.AbstractTool;
import it.eurix.archtools.tool.ToolException;
import it.eurix.archtools.tool.ToolOutput;
import eu.prestoprime.conf.Constants;
import eu.prestoprime.tools.P4ToolManager;

public class FFprobe extends AbstractTool<FFprobe.AttributeType> {

	public static enum AttributeType {
		json
	}
	
	public FFprobe() {
		super(P4ToolManager.getInstance().getToolDescriptor(Constants.FFPROBE_NAME));
	}

	public ToolOutput<FFprobe.AttributeType> extract(String inputFile) throws ToolException {
		ToolOutput<FFprobe.AttributeType> output = this.execute("-v", "quiet", "-print_format", "json", "-show_format", "-show_streams", inputFile);
		output.setAttribute(FFprobe.AttributeType.json, output.getProcessOutput());
		return output;
	}
}
