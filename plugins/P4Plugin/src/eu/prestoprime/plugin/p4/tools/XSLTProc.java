/**
 * XSLTProc.java
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.conf.Constants;
import eu.prestoprime.tools.P4ToolManager;

public class XSLTProc extends AbstractTool {

	private static Logger logger = LoggerFactory.getLogger(XSLTProc.class);

	private long execTime;
	private String xslFile;
	private String outputFile;
	private Map<String, String> stringParams;

	public XSLTProc() {
		super(P4ToolManager.getInstance().getToolDescriptor(Constants.XSLTPROC_NAME));
		stringParams = new HashMap<String, String>();
	}

	public void setXSLFile(String xslFile) {
		this.xslFile = xslFile;
	}

	public void addStringParam(String paramName, String paramValue) {
		stringParams.put(paramName, paramValue);
	}

	public Map<String, String> getStringParams() {
		return stringParams;
	}

	public long getExecTime() {
		return execTime;
	}

	public String getOutputFile() {
		return this.outputFile;
	}

	public void extract(String targetFile) throws ToolException {

		try {
			File tmpResult = File.createTempFile("xsltproc-", ".out");
			tmpResult.deleteOnExit();
			this.outputFile = tmpResult.getAbsolutePath();
			long time1 = System.currentTimeMillis();
			execute(buildCommandParams(targetFile));
			long time2 = System.currentTimeMillis();
			execTime += (time2 - time1);
			logger.debug("xsltproc execution time: " + (time2 - time1));
		} catch (IOException e) {
			throw new ToolException("Unable to create temp file");
		}

	}

	private String[] buildCommandParams(String targetFile) {

		List<String> cmdParams = new ArrayList<String>();

		Iterator<String> paramIterator = stringParams.keySet().iterator();
		while (paramIterator.hasNext()) {
			String stringParamName = (String) paramIterator.next();
			cmdParams.add("--stringparam");
			cmdParams.add(stringParamName);
			cmdParams.add(stringParams.get(stringParamName));
		}

		cmdParams.add("-o");
		cmdParams.add(outputFile);
		cmdParams.add(xslFile);
		cmdParams.add(targetFile);

		String[] cmd = new String[cmdParams.size()];
		cmdParams.toArray(cmd);

		return cmd;

	}

	public String addResourceFile(String resFile) throws ToolException {

		File outDir = new File(System.getProperty("java.io.tmpdir"), Constants.XSLTPROC_NAME.toUpperCase());

		if (!outDir.exists()) {
			outDir.mkdirs();
			outDir.deleteOnExit();
		}

		File targetFile = new File(outDir, resFile);

		InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(resFile);

		if (input != null && !targetFile.exists()) {

			try {
				OutputStream output = new FileOutputStream(targetFile);
				byte[] buf = new byte[1024];
				int len;
				int outlen = 0;
				while ((len = input.read(buf)) > 0) {
					output.write(buf, 0, len);
					outlen += len;
				}
				output.close();
				input.close();

				logger.info("Extracted resource " + resFile + " from jar, output size: " + outlen);

			} catch (FileNotFoundException e) {
				throw new ToolException("Error writing target file " + targetFile.getAbsolutePath());
			} catch (IOException e) {
				throw new ToolException("Error adding resource file " + resFile);
			}

		}
		return targetFile.getAbsolutePath();
	}
}
