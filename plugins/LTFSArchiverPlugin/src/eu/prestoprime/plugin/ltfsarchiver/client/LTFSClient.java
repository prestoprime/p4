/**
 * LTFSClient.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LTFSClient {

	private static final Logger logger = LoggerFactory.getLogger(LTFSClient.class);

	public LTFSResponse execute(LTFSRequest request) throws LTFSException {
		LTFSResponse response;
		do {
			response = this.executeRequest(request);
			LTFSRequest nextRequest = request.nextRequest(response);

			if (nextRequest != null && nextRequest == request) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			request = nextRequest;
		} while (request != null);
		return response;
	}

	private LTFSResponse executeRequest(LTFSRequest ltfsRequest) throws LTFSException {
		logger.debug("Calling: " + ltfsRequest);

		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(ltfsRequest.toURL());
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					StringBuffer sb = new StringBuffer();
					String line;
					while ((line = reader.readLine()) != null)
						sb.append(line.trim());
					reader.close();
					EntityUtils.consume(entity);

					logger.debug("Received: " + sb);

					LTFSResponse ltfsResponse;
					switch (response.getHeaders("content-Type")[0].getValue()) {
					case "text/plain":
					default:
						ltfsResponse = new LTFSResponse(sb.toString());
						break;
					case "application/json":
						ltfsResponse = new LTFSResponse(new JSONObject(sb.toString()));
						break;
					}
					return ltfsResponse;
				} else {
					throw new LTFSException("LTFSArchiver returns with empty entity...");
				}
			} else {
				throw new LTFSException("LTFSArchiver returns with error " + response.getStatusLine().getStatusCode() + "...");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new LTFSException("Unable to contact the LTFSArchiver Web Service...");
		} catch (IOException e) {
			e.printStackTrace();
			throw new LTFSException("Unable to read the response...");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new LTFSException("Unable to parse JSON response...");
		}
	}
}
