/**
 * LTFSRequestBuilder.java
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
import java.util.Map;

public class LTFSRequestBuilder {

	private URL url;
	private String script;
	private Map<String, String> params = new HashMap<>();

	private LTFSRequestBuilder(URL url) {
		this.url = url;
	}

	/**
	 * Initializes a new <code>LTFSRequestBuilder</code> that points to the Web
	 * Service passed as parameter.
	 * 
	 * @param URL
	 *            The location of the LTFSArchiver Web Service.
	 */
	public static LTFSRequestBuilder newInstance(URL url) {
		return new LTFSRequestBuilder(url);
	}

	/**
	 * Builds a new <code>LTFSRequest</code> object with defined script, command
	 * and parameters.
	 */
	public LTFSRequest build() {
		return new LTFSRequest(url, script, params);
	}

	/**
	 * Set the action to <code>WriteToLTO</code> and the command to
	 * <code>WriteFile</code>.
	 */
	public LTFSRequestBuilder write() {
		this.script = "WriteToLTO";
		this.params.put("Command", "WriteFile");
		this.params.put("MD5", "Y");
		return this;
	}

	/**
	 * Set the action to <code>RestoreFromLTO</code> and the command to
	 * <code>RestoreFile</code>.
	 */
	public LTFSRequestBuilder restore() {
		this.script = "RestoreFromLTO";
		this.params.put("Command", "RestoreFile");
		return this;
	}

	/**
	 * Set the action to <code>MakeAvail</code> but doesn't set the command.<br/>
	 * Allowed commands are: <code>Mount</code> <code>Unmount</code>
	 * <code>Cancel</code>
	 */
	public LTFSRequestBuilder makeAvailable() {
		this.script = "MakeAvailable";
		return this;
	}

	/**
	 * Set the <code>Command</code> to <code>WriteFolder</code>. This method
	 * must be called in conjunction with the method <code>write</code>.
	 */
	public LTFSRequestBuilder folder() {
		this.params.put("Command", "WriteFolder");
		return this;
	}

	/**
	 * Sets the <code>Command</code> parameter to <code>Mount</code>.<br/>
	 * This method must be called in conjunction with the method
	 * <code>makeAvailable</code>.
	 */
	public LTFSRequestBuilder mount() {
		this.params.put("Command", "Mount");
		return this;
	}

	/**
	 * Sets the <code>Command</code> parameter to <code>Unmount</code>.<br/>
	 * This method must be called in conjunction with the method
	 * <code>makeAvailable</code>.
	 */
	public LTFSRequestBuilder unmount() {
		this.params.put("Command", "Unmount");
		return this;
	}

	/**
	 * Sets the <code>Command</code> parameter to <code>Cancel</code>.<br/>
	 * This method could be called in conjunction with every action.
	 */
	public LTFSRequestBuilder cancel() {
		this.params.put("Command", "Cancel");
		return this;
	}

	/**
	 * Sets the <code>FileName</code> parameter.
	 * 
	 * @param fileName
	 */
	public LTFSRequestBuilder from(String fileName) {
		this.params.put("FileName", fileName);
		return this;
	}

	/**
	 * Sets the <code>DestPath</code> parameter.
	 * 
	 * @param destPath
	 */
	public LTFSRequestBuilder to(String destPath) {
		this.params.put("DestPath", destPath);
		return this;
	}

	/**
	 * Sets the <code>PoolName</code> parameter.
	 * 
	 * @param poolName
	 */
	public LTFSRequestBuilder pool(String poolName) {
		this.params.put("PoolName", poolName);
		return this;
	}

	/**
	 * Sets the <code>TapeID</code> parameter.
	 * 
	 * @param tapeID
	 */
	public LTFSRequestBuilder tapeID(String tapeID) {
		this.params.put("TapeID", tapeID);
		return this;
	}

	/**
	 * Sets the <code>TaskID</code> parameter.
	 * 
	 * @param taskID
	 */
	public LTFSRequestBuilder taskID(String taskID) {
		this.params.put("TaskID", taskID);
		return this;
	}
}
