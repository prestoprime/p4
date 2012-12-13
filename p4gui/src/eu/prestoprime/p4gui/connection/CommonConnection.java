/**
 * CommonConnection.java
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
package eu.prestoprime.p4gui.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.prestoprime.p4gui.model.P4Service;
import eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE;
import eu.prestoprime.workflow.plugin.WfPlugin;
import eu.prestoprime.workflow.plugin.WfPlugin.WfService;

public abstract class CommonConnection {

	public static USER_ROLE getUserRole(P4Service service) {
		String path = service.getURL() + "/checkrole";

		try {
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line;
			if ((line = reader.readLine()) != null) {
				try {
					return USER_ROLE.valueOf(line);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					return USER_ROLE.guest;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return USER_ROLE.guest;
	}

	public static String getTermsOfUse(P4Service service, String role) {
		String path = service.getURL() + "/terms/" + role;

		StringBuffer result = new StringBuffer();
		try {
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	public static List<String> getAvailableFileList(P4Service service) throws ConnectionException {
		List<String> fileList = new ArrayList<>();

		String path = service.getURL() + "/filelist";

		try {
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					String line;
					while ((line = reader.readLine()) != null)
						if (!line.contains("Thumbs.db")) {
							fileList.add(line);
						}
					return fileList;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new ConnectionException("Error listings files...");
	}

	public static Map<WfPlugin, Set<WfService>> getAvailablePlugins(P4Service service) throws ConnectionException {
		Map<WfPlugin, Set<WfService>> plugins = new HashMap<>();

		String path = service.getURL() + "/pluginlist";

		try {
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null && entity.getContent() != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					StringBuffer sb = new StringBuffer();
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line.trim());
					}

					JSONObject json = new JSONObject(sb.toString());
					Iterator<String> it = json.keys();
					while (it.hasNext()) {
						final String pluginName = it.next();
						WfPlugin plugin = new WfPlugin() {

							@Override
							public Class<? extends Annotation> annotationType() {
								return WfPlugin.class;
							}

							@Override
							public String name() {
								return pluginName;
							}
						};

						Set<WfService> tasks = new HashSet<>();

						JSONArray jTasks = json.getJSONArray(pluginName);
						for (int i = 0; i < jTasks.length(); i++) {
							JSONObject jTask = jTasks.getJSONObject(i);
							final String taskService = jTask.getString("service");
							final String taskVersion = jTask.getString("version");
							WfService task = new WfService() {

								@Override
								public Class<? extends Annotation> annotationType() {
									return WfService.class;
								}

								@Override
								public String version() {
									return taskVersion;
								}

								@Override
								public String name() {
									return taskService;
								}
							};

							tasks.add(task);
						}

						plugins.put(plugin, tasks);
					}

					return plugins;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		throw new ConnectionException("Error listing plugins...");
	}
}
