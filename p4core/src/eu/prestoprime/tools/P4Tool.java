/**
 * P4Tool.java
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
package eu.prestoprime.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.prestoprime.conf.Constants;

public class P4Tool implements GenericTool {

	protected Map<String, String> attributeMap;
	private Map<String, String> processResult;
	protected String executable;
	private ToolManager manager;

	public P4Tool(String toolName) {

		attributeMap = new LinkedHashMap<String, String>();
		processResult = new HashMap<String, String>();

		manager = new ToolManager(toolName);
		executable = manager.getExecutable();
		manager.showInfo();

	}

	public final void execute(String... params) throws ToolException {

		List<String> cmdList = new ArrayList<String>();
		Collections.addAll(cmdList, executable);
		Collections.addAll(cmdList, params);

		processResult = manager.run(cmdList.toArray(new String[cmdList.size()]));

	}

	public final String getAttributeByName(String name) {
		return attributeMap.get(name);
	}

	public final List<String> getSupportedAttributeNames() {
		List<String> attributeList = new ArrayList<String>();
		Set<String> attributeSet = attributeMap.keySet();
		Iterator<String> iterator = attributeSet.iterator();
		while (iterator.hasNext()) {
			String attribute = (String) iterator.next();
			attributeList.add(attribute);
		}
		return attributeList;
	}

	public final void setCustomExecutable(String execPath) {
		manager.setCustomExecutable(execPath);
		executable = manager.getExecutable();
	}

	public final String getProcessOutput() {

		return processResult.get(Constants.PROCESS_OUTPUT);

	}

	public final String getProcessError() {

		return processResult.get(Constants.PROCESS_ERROR);

	}

}
