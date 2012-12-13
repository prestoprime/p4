/**
 * ToolFactory.java
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

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.conf.Constants;
import eu.prestoprime.model.ModelUtils;
import eu.prestoprime.model.ModelUtils.P4Namespace;
import eu.prestoprime.model.tools.Dynlib;
import eu.prestoprime.model.tools.Executable;
import eu.prestoprime.model.tools.Tool;
import eu.prestoprime.model.tools.Tools;

public class ToolFactory {

	private static Logger logger = LoggerFactory.getLogger(ToolFactory.class);
	private static ToolFactory instance = null;
	private static Tools tools;

	protected ToolFactory() {
		this.init();

	}

	public static ToolFactory getInstance() {
		if (instance == null)
			instance = new ToolFactory();
		return instance;
	}

	private void init() {
		try {
			Unmarshaller unmarshaller = ModelUtils.getUnmarshaller(P4Namespace.CONF.getValue());
			logger.debug("Created unmarshaller...");
			InputStream toolListInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(Constants.TOOL_LIST_FILE);
			// InputStream toolListInputStream =
			// ClassLoader.getSystemResourceAsStream(Constants.TOOL_LIST_FILE);
			tools = (Tools) unmarshaller.unmarshal(toolListInputStream);
			logger.info("Loaded tool list from " + Constants.TOOL_LIST_FILE);
			this.list();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	public void list() {
		List<Tool> toolList = tools.getTool();
		for (Tool tool : toolList) {
			logger.info("##### " + tool.getName() + " #####");
			List<Executable> exeList = tool.getExecutable();
			for (Executable executable : exeList) {
				logger.info("Executable: " + executable.getValue() + " (" + executable.getOsName() + ")");
			}
			List<Dynlib> libList = tool.getDynlib();
			for (Dynlib dynlib : libList) {
				logger.info("Dynamic Library: " + dynlib.getValue() + " (" + dynlib.getOsName() + ")");
			}
		}
	}

	public void show(Tool tool) {
		List<Tool> toolList = tools.getTool();
		for (Tool aTool : toolList) {
			if (aTool.getName().equalsIgnoreCase(tool.getName())) {
				logger.info("##### " + tool.getName() + " #####");
				List<Executable> exeList = tool.getExecutable();
				for (Executable executable : exeList) {
					logger.info("Executable: " + executable.getValue() + " (" + executable.getOsName() + ")");
				}
				List<Dynlib> libList = tool.getDynlib();
				for (Dynlib dynlib : libList) {
					logger.info("Dynamic Library: " + dynlib.getValue() + " (" + dynlib.getOsName() + ")");
				}
			}
		}
	}

	public Tool getTool(String toolName) {
		Tool tool = null;

		List<Tool> toolList = tools.getTool();

		for (Tool aTool : toolList) {
			if (aTool.getName().equalsIgnoreCase(toolName)) {
				tool = aTool;
				break;
			}
		}

		return tool;

	}

	public Tool getCustomTool() {
		return new Tool();
	}

}
