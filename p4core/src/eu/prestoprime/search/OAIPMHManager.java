/**
 * OAIPMHManager.java
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
package eu.prestoprime.search;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.datamanagement.DataException;
import eu.prestoprime.datamanagement.DataManager;
import eu.prestoprime.model.ModelUtils;
import eu.prestoprime.model.oaipmh.HeaderType;
import eu.prestoprime.model.oaipmh.ListIdentifiersType;
import eu.prestoprime.model.oaipmh.OAIPMH;
import eu.prestoprime.model.oaipmh.RequestType;
import eu.prestoprime.model.oaipmh.VerbType;
import eu.prestoprime.model.oais.DIP;
import eu.prestoprime.model.oais.IPException;

public class OAIPMHManager {

	private static Logger logger = LoggerFactory.getLogger(OAIPMHManager.class);

	public static OAIPMH search(Map<String, String> params) {

		OAIPMH oaipmh = new OAIPMH();

		try {

			logger.debug("OAI-PMH query:\n" + params);

			String verb = params.get("verb");
			String identifier = params.get("identifier");
			String metadataPrefix = params.get("metadataPrefix");
			String from = params.get("from");
			String until = params.get("until");
			String set = params.get("set");
			String resumptionToken = params.get("resumptionToken");

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String fromDate = (from != null && !from.isEmpty()) ? df.parse(from).toString() : "";
			String untilDate = (until != null && !until.isEmpty()) ? df.parse(until).toString() : "";
			String setSpec = (set != null && !set.isEmpty()) ? setSpec = set : "";

			RequestType requestType = new RequestType();
			requestType.setVerb(VerbType.LIST_IDENTIFIERS);
			requestType.setMetadataPrefix(metadataPrefix);
			requestType.setValue("http://p4.eurixgroup.com/p4ws/search/oai");

			oaipmh.setRequest(requestType);

			List<String> aipIdList = DataManager.getInstance().getAIPByOAIPMH(fromDate, untilDate, setSpec);

			logger.debug("OAI-PMH Query results: " + aipIdList.size());

			ListIdentifiersType listIdentifiers = new ListIdentifiersType();

			List<HeaderType> listHeader = listIdentifiers.getHeader();

			for (String aipId : aipIdList) {

				DIP dip = DataManager.getInstance().getDIPByID(aipId);

				HeaderType headerType = new HeaderType();
				headerType.setDatestamp(dip.getCreateDate().getTime().toString());
				headerType.setIdentifier(aipId);

				listHeader.add(headerType);

			}

			oaipmh.setListIdentifiers(listIdentifiers);

			oaipmh.setResponseDate(ModelUtils.Date2XMLGC(new Date()));

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (DataException e) {
			e.printStackTrace();
		} catch (IPException e) {
			e.printStackTrace();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}

		return oaipmh;
	}

}