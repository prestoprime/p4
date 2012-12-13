/**
 * LTFSResponse.java
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
package eu.prestoprime.plugin.ltfsarchiver.client;

import org.json.JSONException;
import org.json.JSONObject;

public class LTFSResponse {

	private int code;
	private String message;
	private String param;

	/**
	 * Builds a <code>LTFSResponse</code> starting from a text plain response,
	 * with fields separated by <code>\t</code>.<br/>
	 * The response must contains only 2 or 3 fields.
	 * 
	 * @param line
	 *            The response from LTFSArchiver in text plain format.
	 */
	public LTFSResponse(String line) {
		String fields[] = line.split("\t");
		this.code = Integer.parseInt(fields[0]);
		this.message = fields[1];
		if (fields.length == 3)
			this.param = fields[2];
		else
			this.param = "no_param";

		System.out.println(line);
	}

	/**
	 * Builds a <code>LTFSResponse</code> starting from a JSON object.<br/>
	 * JSON format is the following:<br/>
	 * <code>{"exit_code":"200", "output":[{"FLocat":"<FLocat>","MD5":"<md5sum>"}]}</code>
	 * 
	 * @param json
	 *            The response from LTFSArchiver in JSON format.
	 * @throws JSONException
	 *             If there aren't some fields.
	 */
	public LTFSResponse(JSONObject json) throws JSONException {
		// {"exit_code":"200",
		// "output":[{"FLocat":"<FLocat>","MD5":"<md5sum>"}]}
		code = Integer.parseInt(json.getString("exit_code"));
		if (code == 200) {
			JSONObject output = json.getJSONArray("output").getJSONObject(0);
			message = output.getString("FLocat");
			param = output.optString("MD5");
		}
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getParam() {
		return param;
	}
}
