/**
 * D10SumChecker.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import eu.prestoprime.conf.Constants;
import eu.prestoprime.tools.P4ToolManager;

public class D10SumChecker extends AbstractTool<D10SumChecker.AttributeType> {

	public static enum AttributeType {
		MD5,
		D10SumChecker,
		D10SumCheckerOutput
	}
	
	private String d10Checksums = null;

	public D10SumChecker() {
		super(P4ToolManager.getInstance().getToolDescriptor(Constants.D10SUMCHECKER_NAME));
	}

	public ToolOutput<D10SumChecker.AttributeType> extract(String mxfFile) throws ToolException {

		File d10OutputFile;
		try {
			d10OutputFile = File.createTempFile("d10checksums-", ".out");
		} catch (IOException e) {
			throw new ToolException("Error writing D10 checksums file");
		}

		ToolOutput<D10SumChecker.AttributeType> output = execute("-i", mxfFile, "-o", d10OutputFile.getAbsolutePath());

		this.d10Checksums = d10OutputFile.getAbsolutePath();
		this.parseD10Checksums(output);
		return output;

	}

	private void parseD10Checksums(ToolOutput<D10SumChecker.AttributeType> output) throws ToolException {
		logger.info("D10 Check Sums in " + d10Checksums + "...");
		InputStream is;
		try {
			is = new FileInputStream(d10Checksums);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();
			sb.append("\n");
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("File checksum")) {
					String checksum = line.split("\t+")[1].trim();
					output.setAttribute(D10SumChecker.AttributeType.MD5, checksum);
				} else {

					String[] checksArray = line.split("\t+");

					String result = "EDIT_UNIT_NUMBER: " + checksArray[0].trim() + "\t" + "TIMECODE: " + checksArray[1].trim() + "\t" + "EDIT_UNIT_MD5: " + checksArray[2].trim() + "\t" + "PICTURE_ITEM_MD5: " + checksArray[3].trim() + "\t" + "AUDIO_ITEM_MD5: " + checksArray[4].trim();

					sb.append(result + "\n");

				}
				output.setAttribute(D10SumChecker.AttributeType.D10SumChecker, sb.toString());
			}

			output.setAttribute(D10SumChecker.AttributeType.D10SumCheckerOutput, d10Checksums);

		} catch (IOException e) {
			throw new ToolException("Error parsing D10SumChecker output");
		}

	}

}
