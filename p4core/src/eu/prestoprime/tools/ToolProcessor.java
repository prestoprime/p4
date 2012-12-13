/**
 * ToolProcessor.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToolProcessor {

	private Logger logger = LoggerFactory.getLogger(ToolProcessor.class);
	private Process process;
	private StringBuilder processResult = null;
	private StringBuilder processError = null;

	public void exec(String[] cmd, String[] env) throws ToolException {

		processResult = new StringBuilder();
		processError = new StringBuilder();

		try {
			logger.info("Executing command: " + join(cmd));
			process = Runtime.getRuntime().exec(cmd, env, null);

			ProcessKiller terminator = new ProcessKiller(process);
			Runtime.getRuntime().addShutdownHook(terminator);
			String line;
			BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
			logger.info("##### Process Error Messages #####");
			while ((line = errorStream.readLine()) != null) {
				processError.append(line + "\n");
				logger.info(line);
			}
			errorStream.close();
			logger.info("##### Process Output Messages #####");
			while ((line = inputStream.readLine()) != null) {
				processResult.append(line + "\n");
				logger.info(line);
			}
			inputStream.close();

			if (process.waitFor() != 0) {
				throw new ToolException("exec failed: " + join(cmd));
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new ToolException("Unable to execute command " + join(cmd));
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new ToolException("Wrong process execution...");
		}

	}

	public String getProcessOutput() {
		return processResult.toString();
	}

	public String getProcessError() {
		return processError.toString();
	}

	private String join(String[] cmd) {
		StringBuffer cmdline = new StringBuffer();
		for (String s : cmd) {
			cmdline.append(s + " ");
		}
		return cmdline.toString();
	}

	public class ProcessKiller extends Thread {

		private Process deadProcessWalking;

		public ProcessKiller(Process process) {
			deadProcessWalking = process;
		}

		@Override
		public void run() {
			deadProcessWalking.destroy();
		}
	}

}
