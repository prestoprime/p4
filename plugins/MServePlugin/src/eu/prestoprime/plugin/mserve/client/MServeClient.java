/**
 * MServeClient.java
 * Author: Francesco Rosso (rosso@eurix.it)
 * Contributors: Francesco Gallo (gallo@eurix.it)
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
package eu.prestoprime.plugin.mserve.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MServeClient {

	private static final Logger logger = LoggerFactory.getLogger(MServeClient.class);

	public enum MServeTask {
		d10mxfchecksum("prestoprime.tasks.d10mxfchecksum"), extractkeyframes("prestoprime.tasks.extractkeyframes"), ffmbc("prestoprime.tasks.ffmbc"), ffmpeg2theora("prestoprime.tasks.ffmpeg2theora"), ffprobe("prestoprime.tasks.ffprobe"), mxftechmdextractor("prestoprime.tasks.mxftechmdextractor");

		private String name;

		private MServeTask(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	};

	private String host;
	private String serviceID;

	public MServeClient(String host, String serviceID) {
		this.host = host;
		this.serviceID = serviceID;
	}

	/**
	 * Uploads a file on MServe and returns its fileID
	 * 
	 * @param file
	 * @return
	 * @throws MServeException
	 */
	public String uploadFile(File file) throws MServeException {
		try {
			URL url = new URL(host + "/auths/" + serviceID + "/mfiles/");

			logger.debug("MServe URL: " + url.toString());

			HttpClient client = new DefaultHttpClient();
			HttpUriRequest request = new HttpPost(url.toString());
			request.setHeader("Accept", "application/json");

			MultipartEntity part = new MultipartEntity();
			part.addPart("file", new FileBody(file));

			((HttpPost) request).setEntity(part);

			HttpEntity stream = client.execute(request).getEntity();
			InputStream istream = stream.getContent();

			BufferedReader buf = new BufferedReader(new InputStreamReader(istream));
			String line;
			StringBuffer sb = new StringBuffer();
			while (null != ((line = buf.readLine()))) {
				sb.append(line.trim());
			}
			buf.close();
			istream.close();

			logger.debug(sb.toString());

			JSONObject response = new JSONObject(sb.toString());

			return response.getString("id");
		} catch (MalformedURLException e) {
			throw new MServeException("Bad REST interface...");
		} catch (IOException e) {
			throw new MServeException("Unable to make IO...");
		} catch (JSONException e) {
			throw new MServeException("Bad JSON response...");
		}
	}

	/**
	 * Runs a specific task on MServe, wait until it completes or fails and
	 * returns the output.
	 * 
	 * @param fileID
	 * @param task
	 * @param params
	 * @return
	 * @throws MServeException
	 */
	public File runMServeTask(String fileID, MServeTask task, Map<String, String> params) throws MServeException {
		String jobID = this.createJob(fileID, task, params);

		String joboutputID;
		while ((joboutputID = this.pollJob(jobID)) == null) {
			try {
				System.out.println("waiting...");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("joboutputID: " + joboutputID);

		File outputFile = this.getJobFile(joboutputID);

		return outputFile;
	}

	private String createJob(String fileID, MServeTask task, Map<String, String> params) throws MServeException {
		try {
			URL url = new URL(host + "/mfiles/" + fileID + "/jobs/");
			HttpClient client = new DefaultHttpClient();
			HttpUriRequest request = new HttpPost(url.toString());
			request.setHeader("Accept", "application/json");

			List<NameValuePair> parameters = new ArrayList<>();
			parameters.add(new BasicNameValuePair("jobtype", task.getName()));
			if (params != null)
				for (Entry<String, String> param : params.entrySet())
					parameters.add(new BasicNameValuePair(param.getKey(), param.getValue()));
			UrlEncodedFormEntity part = new UrlEncodedFormEntity(parameters);

			((HttpPost) request).setEntity(part);

			HttpEntity stream = client.execute(request).getEntity();
			InputStream istream = stream.getContent();

			BufferedReader buf = new BufferedReader(new InputStreamReader(istream));
			String output = "";
			String line;
			while (null != ((line = buf.readLine()))) {
				output += line.trim();
			}
			buf.close();
			istream.close();

			JSONObject response = new JSONObject(output);
			System.out.println(response);
			return response.getString("id");
		} catch (MalformedURLException e) {
			throw new MServeException("Bad REST interface...");
		} catch (IOException e) {
			throw new MServeException("Unable to make IO...");
		} catch (JSONException e) {
			throw new MServeException("Bad JSON response...");
		}
	}

	private String pollJob(String jobID) throws MServeException {
		try {
			URL url = new URL(host + "/jobs/" + jobID);
			HttpClient client = new DefaultHttpClient();
			HttpUriRequest request = new HttpGet(url.toString());
			request.setHeader("Accept", "application/json");

			HttpEntity stream = client.execute(request).getEntity();
			InputStream istream = stream.getContent();

			BufferedReader buf = new BufferedReader(new InputStreamReader(istream));
			String output = "";
			String line;
			while (null != ((line = buf.readLine()))) {
				output += line.trim();
			}
			buf.close();
			istream.close();

			JSONObject response = new JSONObject(output);
			if (response.getJSONObject("tasks").getBoolean("failed"))
				throw new MServeException("Job failed...");
			if (!(response.getJSONObject("tasks").getBoolean("successful")))
				return null;
			return response.getJSONArray("joboutput_set").getJSONObject(0).getString("id");
		} catch (MalformedURLException e) {
			throw new MServeException("Bad REST interface...");
		} catch (IOException e) {
			throw new MServeException("Unable to make IO...");
		} catch (JSONException e) {
			throw new MServeException("Bad JSON response...");
		}
	}

	private File getJobFile(String joboutputID) {
		try {
			URL url = new URL(host + "/joboutputs/" + joboutputID + "/file");

			File outputFile = File.createTempFile("mserve-", ".out");

			InputStream is = url.openStream();
			OutputStream os = new FileOutputStream(outputFile);
			IOUtils.copy(is, os);

			return outputFile;
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}