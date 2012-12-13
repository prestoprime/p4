/**
 * MessageDigestExtractor.java
 * Author: Francesco Gallo (gallo@eurix.it)
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
package eu.prestoprime.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MessageDigestExtractor implements GenericTool {

	private Logger logger = LoggerFactory.getLogger(MessageDigestExtractor.class);
	private MessageDigest mdAlgorithm;
	private Map<String, String> attributeMap;

	public MessageDigestExtractor() {

		attributeMap = new LinkedHashMap<String, String>();

	}

	public void extract(String input) throws ToolException {
		byte[] md5Bytes = getMessageDigest(input, "MD5");
		String md5Sum = getHexString(md5Bytes);
		attributeMap.put("MD5", md5Sum);

		byte[] sha1Bytes = getMessageDigest(input, "SHA-1");
		String sha1Sum = getHexString(sha1Bytes);
		attributeMap.put("SHA-1", sha1Sum);

		logger.debug("File: " + input + " MD5: " + md5Sum + " SHA-1: " + sha1Sum);

	}

	private byte[] getMessageDigest(String input, String algorithm) throws ToolException {
		FileInputStream fis;
		try {
			fis = new FileInputStream(input);
			mdAlgorithm = MessageDigest.getInstance(algorithm);

			byte[] dataBytes = new byte[1024];
			int nread = 0;
			while ((nread = fis.read(dataBytes)) != -1) {
				mdAlgorithm.update(dataBytes, 0, nread);
			}
		} catch (IOException e) {
			throw new ToolException("Error reading input file " + input);
		} catch (NoSuchAlgorithmException e) {
			throw new ToolException("Unable to find algorithm " + algorithm);
		}
		return mdAlgorithm.digest();
	}

	private String getHexString(byte[] bytes) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xff & bytes[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public String getAttributeByName(String name) {
		return attributeMap.get(name);
	}

	public List<String> getSupportedAttributeNames() {
		List<String> attributeList = new ArrayList<String>();
		Set<String> attributeSet = attributeMap.keySet();
		Iterator<String> iterator = attributeSet.iterator();
		while (iterator.hasNext()) {
			String attribute = (String) iterator.next();
			attributeList.add(attribute);
		}
		return attributeList;
	}

}
