/**
 * URLUtils.java
 * Author: Philip Kahle (philip.kahle@uibk.ac.at)
 * 
 * This file is part of PrestoPRIME Preservation Platform (P4).
 * 
 * Copyright (C) 2009-2012 University of Innsbruck, Austria
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
package eu.prestoprime.p4gui.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URLUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(URLUtils.class);
	private static final String ENCODING = "UTF-8";

	/**
	 * Get a complete query string with UTF-8 encoded parameter values.
	 * 
	 * @param paramMap
	 *            String parameterName, String parameterValue
	 * @return QueryString in the form
	 *         "?paramName1=paramValue1&paramName2=paramValue2&..."
	 */
	public static String buildUrlParamString(Map<String, String> paramMap) {
		StringBuilder paramString = new StringBuilder();
		if (!paramMap.isEmpty()) {
			paramString.append("?");
		}
		for (Entry<String, String> entry : paramMap.entrySet()) {
			if (paramString.length() > 1) {
				paramString.append("&");
			}
			paramString.append(entry.getKey());
			paramString.append("=");
			paramString.append(encode(entry.getValue()));
		}
		return paramString.toString();
	}

	/**
	 * Encode a string for URL as UTF-8
	 * 
	 * @param s
	 * @return
	 */
	public static String encode(String s) {
		return encode(s, ENCODING);
	}

	/**
	 * Encode a string for URL
	 * 
	 * @param s
	 * @param encoding
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String encode(String s, String encoding) {
		if (encoding == null)
			encoding = ENCODING;
		if (!encoding.equals("UTF-8"))
			LOGGER.warn("URL encoding does not use UTF-8! Might cause problems");
		String codS = "";
		if (s != null && !s.isEmpty()) {
			try {
				codS = URLEncoder.encode(s, encoding);
			} catch (UnsupportedEncodingException e) { // Should never happen
				LOGGER.error(e.getMessage() + ": Using system default charset for encoding URL parameters which might cause problems!");
				e.printStackTrace();
				codS = URLEncoder.encode(s);
			}
		}
		return codS;
	}

	/**
	 * Decode a UTF-8 encoded String
	 * 
	 * @param s
	 * @return
	 */
	public static String decode(String s) {
		return decode(s, ENCODING);
	}

	/**
	 * Decode an encoded String
	 * 
	 * @param s
	 * @param encoding
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String decode(String s, String encoding) {
		if (encoding == null)
			encoding = ENCODING;
		if (!encoding.equals("UTF-8"))
			LOGGER.warn("URL decoding does not use UTF-8! Might cause problems");
		String decS = "";
		if (s != null && !s.isEmpty()) {
			try {
				decS = URLDecoder.decode(s, encoding);
			} catch (UnsupportedEncodingException e) { // Should never happen
				LOGGER.error(e.getMessage() + ": Using system default charset for decoding URL parameters which might cause problems!");
				e.printStackTrace();
				decS = URLDecoder.decode(s);
			}
		}
		return decS;
	}
}
