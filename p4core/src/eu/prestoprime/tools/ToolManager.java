/**
 * ToolManager.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.conf.Constants;
import eu.prestoprime.model.tools.Dynlib;
import eu.prestoprime.model.tools.Executable;
import eu.prestoprime.model.tools.Tool;

/**
 * Returns instances of each tool using {@link ToolFactory} and manages tool
 * execution invoking {@link ToolProcessor}.
 */
public class ToolManager {

	private Logger logger = LoggerFactory.getLogger(ToolManager.class);

	private static ToolFactory factory;
	private static ToolProcessor processor;
	private String OS;
	private String[] env;
	private Tool tool;

	/**
	 * Default constructor, instantiates a custom tool with default
	 * configuration.
	 */
	public ToolManager() {
		OS = System.getProperty("os.name").toLowerCase();
		factory = ToolFactory.getInstance();
		tool = factory.getCustomTool();
	}

	/**
	 * Returns an instance of the tool identified by
	 * 
	 * @param toolName
	 *            .
	 * 
	 * @param toolName
	 *            Tool identifier, defined in {@link Constants}.
	 */
	public ToolManager(String toolName) {
		OS = System.getProperty("os.name").toLowerCase();
		factory = ToolFactory.getInstance();
		tool = factory.getTool(toolName);
		try {
			configLibraryPath();
		} catch (ToolException e) {
			e.printStackTrace();
			logger.error("Unable to create instance for tool " + toolName);
		}
	}

	/**
	 * 
	 * @return Instance of the tool.
	 */
	public Tool getTool() {

		return tool;

	}

	/**
	 * Prints tool configuration (executables and dinamic libraries) for the
	 * current architecture.
	 */
	public void showInfo() {
		logger.info("##### " + tool.getName() + " Configuration #####");
		List<Executable> exeList = tool.getExecutable();
		for (Executable executable : exeList) {
			logger.info("Executable: " + executable.getValue() + " (" + executable.getOsName() + ")");
		}
		List<Dynlib> libList = tool.getDynlib();
		for (Dynlib dynlib : libList) {
			logger.info("Dynamic Library: " + dynlib.getValue() + " (" + dynlib.getOsName() + ")");
		}
	}

	/**
	 * Overwrites default executable path with a custom one. Used also for
	 * custom tools.
	 * 
	 * @param execPath
	 *            Absolute path of the new executable.
	 */
	public void setCustomExecutable(String execPath) {

		List<Executable> execList = tool.getExecutable();
		for (Executable executable : execList) {
			if (executable.getOsName().compareToIgnoreCase(OS) == 0)
				executable.setValue(execPath);
			break;
		}

	}

	/**
	 * @return Absolute path of the executable.
	 */
	public String getExecutable() {
		String execPath = null;
		List<Executable> execList = tool.getExecutable();
		for (Executable executable : execList) {
			if (executable.getOsName().compareToIgnoreCase(OS) == 0)
				execPath = executable.getValue();
			break;
		}
		return execPath;
	}

	/**
	 * Executes command for a particular tool invoking {@link ToolProcessor}.
	 * Provides access to process output and error streams for parsing.
	 * 
	 * @param cmd
	 *            Command for executing a tool.
	 * @return Map with process output stream and process error stream.
	 * @throws Exception
	 */
	public Map<String, String> run(String[] cmd) throws ToolException {
		processor = new ToolProcessor();
		processor.exec(cmd, env);

		Map<String, String> processResult = new HashMap<String, String>();
		processResult.put(Constants.PROCESS_OUTPUT, processor.getProcessOutput());
		processResult.put(Constants.PROCESS_ERROR, processor.getProcessError());

		return processResult;
	}

	/**
	 * Retrieves tool information from tools.xml, copies dynamic libraries to a
	 * temp path and sets up the LD_LIBRARY_PATH for command execution.
	 * 
	 * @throws ToolException
	 */
	private void configLibraryPath() throws ToolException {

		// tool dynamic libraries (if any) exported on disk
		List<Dynlib> dynlibList = tool.getDynlib();
		if (dynlibList.size() == 0)
			return;

		// creates temp dir for LD_LIBRARY_PATH
		File outDir = new File(System.getProperty("java.io.tmpdir"), tool.getName().toUpperCase());
		if (!outDir.exists()) {
			outDir.mkdirs();
			outDir.deleteOnExit();
		}
		String[] linuxEnv = { "LD_LIBRARY_PATH=" + System.getenv("LD_LIBRARY_PATH") + File.pathSeparator + outDir.getAbsolutePath() };
		env = linuxEnv;

		// copy dyn libs to temp path
		for (Dynlib dynlib : dynlibList) {
			if (!dynlib.getOsName().equalsIgnoreCase(OS))
				continue;
			String dynlibPath = dynlib.getValue();
			int slash = dynlibPath.lastIndexOf(File.separatorChar);
			dynlibPath = dynlibPath.substring(slash + 1);
			File dynlibFile = new File(outDir, dynlibPath);
			if (!dynlibFile.exists())
				copyResource(dynlib.getValue(), dynlibFile.getAbsolutePath());
			dynlib.setValue(dynlibFile.getAbsolutePath()); // update Tool
															// library
		}

	}

	private void copyResource(String source, String target) throws ToolException {

		try {
			FileChannel sourceFile = new FileInputStream(source).getChannel();
			FileChannel targetFile = new FileOutputStream(target).getChannel();

			sourceFile.transferTo(0, sourceFile.size(), targetFile);

		} catch (IOException e) {
			e.printStackTrace();
			throw new ToolException("Unable to copy Tool Resource: " + source);
		}
	}

}
