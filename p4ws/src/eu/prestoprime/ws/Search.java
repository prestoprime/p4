/**
 * Search.java
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
package eu.prestoprime.ws;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.ResourceConfig;

import eu.prestoprime.datamanagement.DataManager;
import eu.prestoprime.model.ModelUtils;
import eu.prestoprime.model.ModelUtils.P4Namespace;
import eu.prestoprime.model.oaipmh.OAIPMH;
import eu.prestoprime.model.search.SearchResults;
import eu.prestoprime.search.OAIPMHManager;
import eu.prestoprime.search.SearchConstants;
import eu.prestoprime.search.Searcher;
import eu.prestoprime.search.util.Schema;
import eu.prestoprime.search.util.Schema.P4FacetField;
import eu.prestoprime.search.util.Schema.P4SortField;
import eu.prestoprime.search.util.Schema.searchField;
import eu.prestoprime.search.util.SolrQueryUtils;
import eu.prestoprime.search.util.suggestion.P4Suggestions;

@Path("/search")
public class Search {

	// @Context SecurityContext security;
	@Context
	HttpContext context;
	@Context
	ResourceConfig config;

	private final static Logger LOGGER = Logger.getLogger(Search.class);
	// private URI wsBaseUri; // URI of application server
	private Searcher searcher;

	private void init() {
		try {
			searcher = new Searcher();
			// wsBaseUri = context.getRequest().getBaseUri();
		} catch (Exception e) {
			LOGGER.debug("Searcher could not be created!\n" + e.getMessage());
			e.printStackTrace();
		}
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/checkCredentials")
	public String checkCredentials() {
		LOGGER.debug("Called checkCredentials --> User authenticated");
		return "authenticated";
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/")
	public Response searchAIPByDC(@QueryParam("title") String title, @QueryParam("description") String description, @QueryParam("format") String format, @QueryParam("identifier") String identifier) {

		ResponseBuilder rb;

		try {
			Map<String, String> elements = new HashMap<String, String>();
			elements.put("title", title);
			elements.put("description", description);
			elements.put("format", format);
			elements.put("identifier", identifier);
			List<String> aipList = DataManager.getInstance().getAllAIP(elements);

			StringBuffer sb = new StringBuffer();
			for (String aipId : aipList)
				sb.append(aipId + "\n");

			rb = Response.status(Status.OK).entity(sb.toString());
		} catch (Exception e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/identifier/{id}")
	public Response searchAIPByDC(@PathParam("id") String identifier) {

		ResponseBuilder rb;

		try {

			String aipId = DataManager.getInstance().getAIPByDCID(identifier);

			StringBuffer sb = new StringBuffer();

			if (aipId != null)
				sb.append(aipId + "\n");

			rb = Response.status(Status.OK).entity(sb.toString());
		} catch (Exception e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

	/**
	 * Do a search on all fields, i.e. the ones that are stored to the
	 * catchall-field in Solr's schema. At the moment: dcTitle & dcCreator
	 * 
	 * @param queryString
	 *            the user input to search for
	 * @param from
	 *            the first result to return depending on the order of the
	 *            results. 0 gets results starting with the first. For the
	 *            second page this number has to be increased by #resultCount.
	 * @param resultCount
	 *            the number of results to return.
	 * @param sortAsc
	 *            if true, then results are sorted ascending
	 * @param sortFieldName
	 *            the field to sort on. see
	 *            {@link eu.prestoprime.search.util.Schema.java} for allowed
	 *            sortFields
	 * @param dateFacetValue
	 *            the selected value for the date facet or just NULL in case
	 *            there is none
	 * @param creatorFacetValue
	 *            the selected value for the creator facet or just NULL in case
	 *            there is none
	 * @param publisherFacetValue
	 *            the selected value for the publisher facet or just NULL in
	 *            case there is none
	 * @return a results XML according to the schema in
	 *         search.conf.xsdSchema.searchResults.xsd
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/quick")
	public String quickSearch(@QueryParam("queryTerms") String queryString, @QueryParam("from") final String fromString, @QueryParam("resultCount") final String resultCountString, @QueryParam("sortAsc") final String sortAscString, @QueryParam("sortField") final String sortFieldName, @QueryParam("facetFilters") final String facetValues) {

		this.init();

		// process parameters
		int from = validateFromParam(fromString);
		int resultCount = validateResultCountParam(resultCountString);
		boolean sortAsc = validateSortAscParam(sortAscString);
		P4SortField sortField = getSortField(sortFieldName);

		Map<String, String> paramMap = unwrap(facetValues);

		Map<P4FacetField, String> facetParams = buildFacetParamMap(paramMap);

		// do quick search
		SearchResults results = searcher.simpleSearch(queryString, from, resultCount, sortField, sortAsc, facetParams);

		// marshall the results Object
		String resultString = "";
		if (results != null) {
			try {
				resultString = marshallSearchResults(results);
			} catch (JAXBException e) {
				System.out.println(e.getMessage());
			}
		}
		return resultString;
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/advanced")
	public String advancedSearch(@QueryParam("queryTerms") final String wrappedQuery, @QueryParam("from") final String fromString, @QueryParam("resultCount") final String resultCountString, @QueryParam("sortAsc") final String sortAscString, @QueryParam("sortField") final String sortFieldName, @QueryParam("facetFilters") final String facetValues) {
		this.init();

		// process parameters
		Map<String, String> paramMap = unwrap(wrappedQuery);
		Map<Schema.searchField, String> fieldMap = buildQueryParamMap(paramMap);
		// putIfSet(fieldMap, Schema.searchField.TITLE, dcTitle);
		// putIfSet(fieldMap, Schema.searchField.CREATOR, dcCreator);
		// putIfSet(fieldMap, Schema.searchField.DESC, dcDescription);
		// putIfSet(fieldMap, Schema.searchField.FORMAT, dcFormat);
		// putIfSet(fieldMap, Schema.searchField.IDENT, dcIdentifier);
		// putIfSet(fieldMap, Schema.searchField.LANG, dcLanguage);
		// putIfSet(fieldMap, Schema.searchField.PUBLISHER, dcPublisher);
		// putIfSet(fieldMap, Schema.searchField.SOURCE, dcSource);
		// putIfSet(fieldMap, Schema.searchField.SUBJECT, dcSubject);
		// putIfSet(fieldMap, Schema.searchField.DATE, dcDate);
		// putIfSet(fieldMap, Schema.searchField.CREATEDATE, createDate);
		// putIfSet(fieldMap, Schema.searchField.WAISDA, userAnnot);
		// putIfSet(fieldMap, Schema.searchField.ID, id);

		int from = validateFromParam(fromString);
		int resultCount = validateResultCountParam(resultCountString);
		boolean sortAsc = validateSortAscParam(sortAscString);
		P4SortField sortField = getSortField(sortFieldName);

		paramMap = unwrap(facetValues);
		Map<P4FacetField, String> facetParams = buildFacetParamMap(paramMap);

		// do advanced search
		SearchResults results = searcher.advancedSearch(fieldMap, from, resultCount, sortField, sortAsc, facetParams);

		// marshall the results Object
		String resultString = "";
		if (results != null) {
			try {
				resultString = marshallSearchResults(results);
			} catch (JAXBException e) {
				System.out.println(e.getMessage());
			}
		}
		return resultString;
	}

	/**
	 * Get Auto complete suggestions for an entered search string. Suggestions
	 * are produced per single token in the whole term (tokenized on whitespace)
	 * by Solr. As this feature will be accessed via AJAX in the frontend only,
	 * it produces JSON data for ease of access.
	 * 
	 * @param term
	 *            the search string
	 * @return JSON containing suggestions for each token in the term
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/suggest")
	public P4Suggestions getSuggestions(@QueryParam("term") String term) {
		this.init();
		P4Suggestions results = searcher.getSuggestion(term);
		return results;
	}

	private P4SortField getSortField(String sortFieldParamValue) {
		// process sortParams
		P4SortField sortField = P4SortField.TITLE;
		if (sortFieldParamValue != null && !sortFieldParamValue.equals("")) {
			for (P4SortField field : P4SortField.values()) {
				if (field.getFieldName().equals(sortFieldParamValue))
					sortField = field;
			}
		}
		return sortField;
	}

	private Map<String, String> unwrap(String wrappedQuery) {
		Map<String, String> paramMap = new HashMap<String, String>();
		if (wrappedQuery != null && !wrappedQuery.isEmpty()) {
			String[] kvPairs = wrappedQuery.split(SearchConstants.KV_SEP);
			String[] kvPair;
			for (int i = 0; i < kvPairs.length; i++) {
				kvPair = kvPairs[i].split(SearchConstants.KV_CON);
				if (kvPair != null && kvPair.length == 2) {
					LOGGER.debug("Unwrapping KV Pair: " + kvPair[0] + " - " + kvPair[1]);
					paramMap.put(kvPair[0], kvPair[1]);
				} else {
					LOGGER.error("Unwrapping the query failed! Query string was: " + wrappedQuery);
				}
			}

		}
		return paramMap;
	}

	private Map<P4FacetField, String> buildFacetParamMap(Map<String, String> kvMap) {
		Map<P4FacetField, String> selectedFacets = SolrQueryUtils.getDefaultFacetMap();

		for (P4FacetField ff : P4FacetField.values()) {
			if (kvMap.containsKey(ff.getFieldName())) {
				selectedFacets.put(P4FacetField.get(ff.getFieldName()), kvMap.get(ff.getFieldName()));
			}
		}
		return selectedFacets;
	}

	private Map<searchField, String> buildQueryParamMap(Map<String, String> kvMap) {
		Map<searchField, String> selectedFacets = new HashMap<searchField, String>();

		for (searchField sf : searchField.values()) {
			if (kvMap.containsKey(sf.getUrlParam())) {
				selectedFacets.put(searchField.get(sf.getUrlParam()), kvMap.get(sf.getUrlParam()));
			}
		}
		return selectedFacets;
	}

	private int validateFromParam(String fromString) {
		int from = 0;
		if (fromString != null && !fromString.equals("")) {
			try {
				from = Integer.parseInt(fromString);
			} catch (NumberFormatException e) {
				LOGGER.debug("Parameter 'from' value is not a number. Getting results with from = 0.");
			}
		}
		return from;
	}

	private int validateResultCountParam(String resultCountString) {
		int resultCount = 10;
		if (resultCountString != null && !resultCountString.equals("")) {
			try {
				resultCount = Integer.parseInt(resultCountString);
			} catch (NumberFormatException e) {
				LOGGER.debug("Parameter 'resultCount' value is not a number. Getting 10 results.");
			}
		}
		return resultCount;
	}

	private boolean validateSortAscParam(String sortAscString) {
		boolean sortAsc = true;
		if (sortAscString != null && !sortAscString.equals("")) {
			sortAsc = Boolean.parseBoolean(sortAscString);
		}
		return sortAsc;
	}

	private String marshallSearchResults(SearchResults results) throws JAXBException {
		Marshaller marshaller = ModelUtils.getMarshaller(P4Namespace.CONF.getValue());
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		// DocumentBuilderFactory docBuilderFactory =
		// DocumentBuilderFactory.newInstance();
		// DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		// Document doc = docBuilder.newDocument();
		// marshaller.marshal(results, doc);

		StringWriter sw = new StringWriter();
		marshaller.marshal(results, sw);
		final String resultString = sw.toString();
		LOGGER.debug(resultString);
		return resultString;
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/oai")
	public Response oaiRequest(@QueryParam("verb") String verb, @QueryParam("identifier") String identifier, @QueryParam("metadataPrefix") String metadataPrefix, @QueryParam("set") String set, @QueryParam("from") String from, @QueryParam("until") String until, @QueryParam("resumptionToken") String resumptionToken) {

		LOGGER.debug("Called oaiRequest with verb: " + verb);

		Map<String, String> params = new HashMap<String, String>();
		params.put("verb", verb);
		params.put("identifier", identifier);
		params.put("metadataPrefix", metadataPrefix);
		params.put("set", set);
		params.put("from", from);
		params.put("until", until);
		params.put("resumptionToken", resumptionToken);

		OAIPMH oaipmh = OAIPMHManager.search(params);

		ResponseBuilder rb = null;

		try {
			StringWriter sw = new StringWriter();
			Marshaller marshaller = ModelUtils.getMarshaller(P4Namespace.OAI_PMH.getValue());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(oaipmh, sw);
			rb = Response.status(Status.OK).entity(sw.toString());
		} catch (JAXBException e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unable to serialize OAIPMH repsonse");
		}

		return rb.build();

	}

}
