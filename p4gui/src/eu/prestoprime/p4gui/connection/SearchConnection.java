/**
 * SearchConnection.java
 * Author: Francesco Rosso (rosso@eurix.it)
 * Contributors: Philip Kahle (philip.kahle@uibk.ac.at)
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.model.ModelUtils;
import eu.prestoprime.model.ModelUtils.P4JAXBPackage;
import eu.prestoprime.model.search.SearchResults;
import eu.prestoprime.p4gui.model.P4Service;
import eu.prestoprime.p4gui.util.URLUtils;
import eu.prestoprime.search.SearchConstants;

public abstract class SearchConnection {

	private static final Logger logger = LoggerFactory.getLogger(SearchConnection.class);
	private static final String QUICK_URI = "/search/quick";
	private static final String ADVANCED_URI = "/search/advanced";
	private static final String SUGGEST_URI = "/search/suggest";

	public static ArrayList<String> searchByDC(P4Service service, String title, String description, String format, String identifier) {
		ArrayList<String> records = new ArrayList<String>();
		try {
			String path = service.getURL() + "/search/?" + "title=" + title + "&description=" + description + "&format=" + format + "&identifier=" + identifier;
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String line = null;
				while ((line = reader.readLine()) != null) {
					records.add(line);
				}
				is.close();
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.reverse(records);
		return records;
	}

	public static String searchByOaiPmh(P4Service service, String params) {
		String string = null;
		try {
			params = params.replaceAll("--", "&");
			String path = service.getURL() + "/search/oai?" + params;
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is1 = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is1));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				string = sb.toString();
				is1.close();
			}
			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return string;
	}

	private static SearchResults solrSearch(final P4Service service, final String searchUri, String query, final String from, final String resultCount, final String sortField, final String sortAsc, final Map<String, String> facetMap) {

		HashMap<String, String> urlParamMap = new HashMap<>();

		urlParamMap.put("queryTerms", query);

		urlParamMap.put("from", from);
		urlParamMap.put("resultCount", resultCount);
		urlParamMap.put("sortAsc", sortAsc);
		urlParamMap.put("sortField", sortField);

		urlParamMap.put("facetFilters", wrapParams(facetMap));

		final String path = service.getURL() + searchUri + URLUtils.buildUrlParamString(urlParamMap);

		SearchResults results = new SearchResults();
		Unmarshaller unmarshaller = null;
		String resultString = null;

		try {
			logger.debug("Query to P4WS:\n" + path);

			P4HttpClient client = new P4HttpClient(service.getUserID());

			client.getParams().setParameter("http.protocol.content-charset", "UTF-8");

			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				resultString = sb.toString();
				is.close();
			}
			EntityUtils.consume(entity);

			unmarshaller = ModelUtils.getUnmarshaller(P4JAXBPackage.CONF);
			results = (SearchResults) unmarshaller.unmarshal(new ByteArrayInputStream(resultString.getBytes()));
		} catch (JAXBException | IOException e) {
			String message = e.getMessage() + " - ";
			logger.error(e.getMessage());

			if (e instanceof JAXBException) {
				message += "The returned xml from P4WS was not a valid searchresult.";
			} else {
				message += "P4WS could not be accessed.";
			}
			results.setErrorMessage(message);
		}
		return results;
	}

	/**
	 * encode all facet values into a string.
	 * 
	 * @param facetMap
	 * @return
	 */
	private static String wrapParams(Map<String, String> facetMap) {
		StringBuilder sb = new StringBuilder();

		for (Entry<String, String> e : facetMap.entrySet()) {
			if (sb.length() > 0) {
				sb.append(SearchConstants.KV_SEP);
			}
			if (e.getValue() != null && !filter(e.getValue()).isEmpty()) {
				sb.append(filter(e.getKey()) + SearchConstants.KV_CON + filter(e.getValue()));
			}
		}
		return sb.toString();
	}

	private static String filter(String s) {
		return s.replace(SearchConstants.KV_CON, "").replace(SearchConstants.KV_SEP, "");
	}

	public static SearchResults simpleSolrSearch(P4Service service, final String query, final String from, final String resultCount, final String sortField, final String sortAsc, final Map<String, String> facetMap) {

		final SearchResults results = solrSearch(service, QUICK_URI, query, from, resultCount, sortField, sortAsc, facetMap);
		return results;
	}

	public static SearchResults advancedSolrSearch(P4Service service, HashMap<String, String> queryMap, final String from, final String resultCount, final String sortField, final String sortAsc, final Map<String, String> facetMap) {

		String query = wrapParams(queryMap);

		final SearchResults results = solrSearch(service, ADVANCED_URI, query, from, resultCount, sortField, sortAsc, facetMap);
		return results;
	}

	public static String solrSuggest(P4Service service, final String term) {
		Writer writer = new StringWriter();

		String results = "";
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(service.getURL());
			sb.append(SUGGEST_URI);
			sb.append("?term=");
			sb.append(term);
			String path = sb.toString();

			logger.debug("Query to P4WS:\n" + path);

			P4HttpClient client = new P4HttpClient(service.getUserID());

			client.getParams().setParameter("http.protocol.content-charset", "UTF-8");

			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream is = entity.getContent();
				if (is != null) {
					char[] buffer = new char[1024];

					Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
					is.close();
				}
			}
			results = writer.toString();
			EntityUtils.consume(entity);
			writer.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error("Either the query was invalid or P4WS could not be accessed.");
			results = null;
		}
		return results;
	}

}
