/**
 * P4HttpClient.java
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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class P4HttpClient extends DefaultHttpClient {

	private static final Logger logger = LoggerFactory.getLogger(P4HttpClient.class);
	private static final List<Integer> redirectCodes = Arrays.asList(301, 302, 303, 305, 307);

	private String userID;

	public P4HttpClient(String userID) {
		HttpParams params = new BasicHttpParams();

		// setup SSL
		try {
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new TrustManager[] { easyTrustManager }, null);

			SSLSocketFactory sf = new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000L);
			SSLSocket socket = (SSLSocket) sf.createSocket(params);
			socket.setEnabledCipherSuites(new String[] { "SSL_RSA_WITH_RC4_128_MD5" });

			Scheme sch = new Scheme("https", 443, sf);
			this.getConnectionManager().getSchemeRegistry().register(sch);
		} catch (IOException | KeyManagementException | NoSuchAlgorithmException e) {
			logger.error("Unable to create SSL handler for HttpClient...");
			e.printStackTrace();
		}

		// save userID
		this.userID = userID;
	}

	public HttpResponse executeRequest(HttpRequestBase request) throws IOException {
		// set userID
		request.setHeader(new BasicHeader("userID", this.userID));

		// disable redirect handling
		HttpParams params = new BasicHttpParams();
		params.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		request.setParams(params);

		// execute request
		HttpResponse response = super.execute(request);

		// check redirect
		if (redirectCodes.contains(response.getStatusLine().getStatusCode())) {
			logger.debug("Redirecting...");

			// get newURL
			String newURL = response.getFirstHeader("Location").getValue();

			// create newRequest
			try {
				HttpUriRequest newRequest = request.getClass().getDeclaredConstructor(String.class).newInstance(newURL);

				// copy entity
				if (request instanceof HttpEntityEnclosingRequestBase) {
					HttpEntity entity = ((HttpEntityEnclosingRequestBase) request).getEntity();
					if (entity != null) {
						logger.debug("Cloning entity...");

						((HttpEntityEnclosingRequestBase) newRequest).setEntity(entity);
					}
				}

				// set userID
				newRequest.setHeader(new BasicHeader("userID", this.userID));

				// retry
				response = new P4HttpClient(userID).execute(newRequest);
			} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
			}
		}

		return response;
	}

	private static TrustManager easyTrustManager = new X509TrustManager() {

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
	};

	public static void main(String[] args) throws Exception {
		P4HttpClient client = new P4HttpClient("pr3st0.2012");
		HttpGet request = new HttpGet("http://p4.eurixgroup.com/p4ws/search/quick?createDateFacet=%5B2012-08-02T00%3A00%3A00Z+TO+2012-08-02T00%3A00%3A00Z%2B7DAY%5D&publisherFacet=&sortAsc=true&sortField=titleSort&waisdaFacet=&term=&resultCount=&dateFacet=&from=&creatorFacet=");
		client.executeRequest(request);
	}
}
