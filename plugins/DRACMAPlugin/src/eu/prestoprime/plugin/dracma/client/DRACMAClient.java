/**
 * DRACMAClient.java
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
package eu.prestoprime.plugin.dracma.client;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DRACMAClient {

	private static final Logger logger = LoggerFactory.getLogger(DRACMAClient.class);

	private static final String indexingPath = "/dracma/cgi-bin/index_request.sh";
	private static final String statusPath = "/dracma/cgi-bin/get_status.sh";
	private static final String updatePath = "/DracmaWeb/rest/wcgi/SetMaterialFilePath";
	private static final String segmentPath = "/DracmaWeb/rest/wcgi/extract";

	private final URI serverURI;

	/**
	 * Builds a DRACMA client that point to the specified server.
	 */
	public DRACMAClient(URI serverURI) {
		this.serverURI = serverURI;
	}

	/**
	 * Indexes the MXF file passed as parameter into DRACMA service.<br/>
	 * The DRACMA server must have access to the MXF file.
	 * 
	 * @return The UMID
	 */
	public String index(File mxfFile) throws DRACMAException {

		String ticket = null;
		URI uri = null;

		try {
			// request indexing
			uri = new URIBuilder(serverURI).setPath(DRACMAClient.indexingPath).setParameter("file", mxfFile.getAbsolutePath()).build();

			logger.debug("DRACMA indexing URI: " + uri);

			HttpClient client = new DefaultHttpClient();
			HttpUriRequest request = new HttpGet(uri);
			HttpParams params = request.getParams();
			params = params.setParameter("file", mxfFile.getAbsolutePath());
			request.setParams(params);
			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					// parse the response
					Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(entity.getContent());
					EntityUtils.consume(entity);

					// retrieve the ticket
					XPathExpression getTicket = XPathFactory.newInstance().newXPath().compile("/info/id/text()");
					ticket = getTicket.evaluate(dom);
				}
			}

			if (ticket == null)
				throw new DRACMAException("Invalid ticket from response to indexing request...");

			logger.debug("TICKET: " + ticket);

			uri = new URIBuilder(serverURI).setPath(DRACMAClient.statusPath).setParameter("id", ticket).build();

			logger.debug("DRACMA indexing status URI: " + uri);

		} catch (IOException e) {
			throw new DRACMAException("Unable to request indexing: network error...");
		} catch (ParserConfigurationException | SAXException e) {
			throw new DRACMAException("Unable to parse the response to indexing request...");
		} catch (XPathExpressionException e) {
			throw new DRACMAException("Unable to extract ticket from response to indexing request...");
		} catch (URISyntaxException e) {
			throw new DRACMAException("Unable to build a valid URI...\n" + e.getMessage());
		}

		do {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpUriRequest request = new HttpGet(uri);
				HttpResponse response = client.execute(request);
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						// parse the response
						Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(entity.getContent());
						EntityUtils.consume(entity);

						// retrieve the status
						XPathExpression getStatus = XPathFactory.newInstance().newXPath().compile("/process_info/info[@context='indexing']/status/text()");
						String status = getStatus.evaluate(dom);
						switch (status) {
						case "completed":
							// retrieve the UMID
							XPathExpression getUMID = XPathFactory.newInstance().newXPath().compile("/process_info/info[@context='indexing']/umid/text()");
							return getUMID.evaluate(dom);
						case "error":
							throw new DRACMAException("Error indexing...");
						default:
							break;
						}
					}
				}

				logger.debug("Not finished yet -> waiting...");

				Thread.sleep(5000);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (true);
	}

	/**
	 * Segments the file.<br/>
	 * 
	 * @return The URL pointing to the extracted segment.
	 */
	public URL segment(DRACMASegment segment) throws DRACMAException {

		try {
			// request segmentation
			String path = DRACMAClient.segmentPath + "/" + segment.getUMID() + "/" + segment.getStart() + "/" + segment.getOffset();
			URI uri = new URIBuilder(serverURI).setPath(path).setParameter("process-rh", "on").build();

			logger.debug("DRACMA segment URI " + uri);

			HttpClient client = new DefaultHttpClient();
			HttpUriRequest request = new HttpGet(uri);
			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					// parse the response
					Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(entity.getContent());
					EntityUtils.consume(entity);

					// retrieve the ticket
					XPathExpression getURL = XPathFactory.newInstance().newXPath().compile("/process/phases/phase[label='Building mxf file']/url/text()");
					return new URL(getURL.evaluate(dom));
				}
			}

			throw new DRACMAException("General error executing segmentation request...\n" + response.getStatusLine());
		} catch (IOException e) {
			throw new DRACMAException("Unable to execute DRACMA segment request...");
		} catch (ParserConfigurationException | SAXException e) {
			throw new DRACMAException("Unable to parse the response to segmentation request...");
		} catch (XPathExpressionException e) {
			throw new DRACMAException("Unable to extract URL from response to segmentation request...");
		} catch (URISyntaxException e) {
			throw new DRACMAException("Unable to build a valid URI...\n" + e.getMessage());
		}
	}

	/**
	 * Updates the file path of specified resource, identified by the UMID.
	 */
	public void update(String UMID, File file) throws DRACMAException {

		try {
			String path = DRACMAClient.updatePath + "/" + UMID + "/";
			URI uri = new URIBuilder(serverURI).setPath(path).build(); // setParameter("path",
																		// file.getAbsolutePath())

			logger.debug("DRACMA update URI " + uri);

			HttpClient client = new DefaultHttpClient();
			HttpUriRequest request = new HttpPut(uri);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("path", file.getAbsolutePath()));
			((HttpEntityEnclosingRequest) request).setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				logger.debug("DRACMA success: " + response.getStatusLine().toString());
				return;
			} else {
				throw new DRACMAException("DRACMA error: status code " + response.getStatusLine().getStatusCode() + "...");
			}
		} catch (IOException e) {
			throw new DRACMAException("Unable to execute DRACMA update request...");
		} catch (URISyntaxException e) {
			throw new DRACMAException("Unable to create a valid URI...\n" + e.getMessage());
		}
	}
}
