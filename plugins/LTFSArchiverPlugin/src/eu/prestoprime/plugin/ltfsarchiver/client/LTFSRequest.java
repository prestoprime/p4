/**
 * LTFSRequest.java
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

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class LTFSRequest {

	private URL url;
	private String script;
	private Map<String, String> params = new HashMap<>();

	public LTFSRequest(URL url, String script, Map<String, String> params) {
		this.url = url;
		this.script = script;
		this.params = params;
	}

	public LTFSRequest nextRequest(LTFSResponse response) throws LTFSException {
		switch (response.getCode()) {
		case 200:
		default:
			LTFSRequest nextRequest = new LTFSRequest(url, script, params);
			String nextCommand;
			switch (params.get("Command")) {
			case "GetStatus":
				switch (response.getMessage().split(" ")[0]) {
				case "wait":
				case "running":
				case "starting":
					return this;
				case "completed":
				default:
					nextCommand = "GetResult";
					if (script.equals("WriteToLTO")) {
						nextRequest.params.put("Output", "JSON");
					}
				}
				break;
			case "GetResult":
				return null;
			case "Cancel":
				return null;
			default:
				nextCommand = "GetStatus";
				nextRequest.params.put("TaskID", response.getMessage());
				break;
			}
			nextRequest.params.put("Command", nextCommand);
			return nextRequest;
		case 400:
		case 500:
			throw new LTFSException(response.getMessage());
		}
	}

	public String toURL() {
		String insertPath = url + "/" + script + "?";
		Iterator<Entry<String, String>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			insertPath += entry.getKey() + "=" + entry.getValue();
			if (it.hasNext())
				insertPath += "&";
		}
		return insertPath;
	}

	@Override
	public String toString() {
		return this.toURL();
	}

	@Deprecated
	public String getInsertPath() {
		return this.toURL();
	}

	@Deprecated
	public String getMonitorPath(String taskID) {
		return url + "/" + script + "?Command=GetStatus&TaskID=" + taskID;
	}

	@Deprecated
	public String getResultPath(String taskID) {
		return url + "/" + script + "?Command=GetResult&TaskID=" + taskID;
	}

	@Deprecated
	public String getResubmitPath(String taskID) {
		return url + "/" + script + "?Command=Resubmit&TaskID=" + taskID;
	}
}