/**
 * Searcher.java
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
package eu.prestoprime.search;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;

import eu.prestoprime.model.search.SearchResults;
import eu.prestoprime.search.util.ResultProcessor;
import eu.prestoprime.search.util.Schema;
import eu.prestoprime.search.util.Schema.P4SortField;
import eu.prestoprime.search.util.SolrQueryBuilder;
import eu.prestoprime.search.util.SolrQueryUtils;
import eu.prestoprime.search.util.suggestion.P4Suggestion;
import eu.prestoprime.search.util.suggestion.P4Suggestions;

public class Searcher {

	private static final Logger LOGGER = Logger.getLogger(Searcher.class);

	public Searcher() {
		LOGGER.info("Instance of Searcher was created.");
	}

	/**
	 * Searches all fields that are quicksearch enabled (i.e. fields that are
	 * added to the "text" field in solr's schema.xml via a copyfield
	 * directive). The portion of the results that is shown ranges from result
	 * #from to #(from+resultCount) Furthermore this method uses Solr's dismax
	 * query handler which strips reserved character from the query string.
	 * Thus, specifying fields for the search or conjuction/disjunction are not
	 * allowed. Including and Excluding terms with +/- is possible though. The
	 * user input string can be simply passed through as searchTerm for simple
	 * error-prone search.
	 * 
	 * @param searchTerm
	 * @param from
	 *            specifies which portion of all results is shown
	 * @param resultCount
	 *            specifies how many results are displayed
	 * @param sortField
	 *            the field that is used for sorting
	 * @param sortAsc
	 *            sortorder, if true -> ascending, else -> descending
	 * @param facets
	 *            Map object containing all fields to facet on as key and, if
	 *            already selected, the filter value for this field
	 * @return a SearchResults object containing all POJOs from the
	 *         QueryResponse
	 */
	public SearchResults simpleSearch(final String searchTerm, final int from, final int resultCount, final P4SortField sortField, final boolean sortAsc, Map<Schema.P4FacetField, String> facets) {

		SolrQuery query = new SolrQuery(searchTerm);

		// use this for simple error-prone syntax in simple search field:
		// add dismax requesthandler with deftype=dismax in solrconfig.xml
		// set default searchfield to catchall field in schema.xml
		// use this handler for the simple search:
		query.setQueryType("/dismax");

		SolrQueryUtils.enableHighlightAllFields(query);

		SearchResults results = search(query, from, resultCount, sortField, sortAsc, facets);

		return results;
	}

	/**
	 * Searches all fields that are quicksearch enabled if no fieldnames are
	 * specified (i.e. fields that are added to the "text" field in solr's
	 * schema.xml via a copyfield directive). The portion of the results that is
	 * shown ranges from result #from to #(from+resultCount) The query is
	 * handled by the Solr's standard queryHandler which allows the full-blown
	 * query syntax.
	 * 
	 * @param searchTerm
	 * @param from
	 *            specifies which portion of all results is shown
	 * @param resultCount
	 *            specifies how many results are displayed
	 * @param sortField
	 *            the field that is used for sorting
	 * @param sortAsc
	 *            sortorder, if true -> ascending, else -> descending
	 * @param facets
	 *            Map object containing all fields to facet on as key and, if
	 *            already selected, the filter value for this field
	 * @return a SearchResults object containing all POJOs from the
	 *         QueryResponse
	 */
	public SearchResults advancedSearch(final Map<Schema.searchField, String> fieldMap, final int from, final int resultCount, final P4SortField sortField, final boolean sortAsc, Map<Schema.P4FacetField, String> facets) {
		SearchResults results = new SearchResults();

		String queryString = SolrQueryBuilder.buildQuery(fieldMap);
		if (!queryString.isEmpty()) {
			SolrQuery query = new SolrQuery(queryString);

			SolrQueryUtils.enableHighlight(query, fieldMap);

			results = search(query, from, resultCount, sortField, sortAsc, facets);
		} else {
			results.setErrorMessage("The built query was empty!");
		}
		return results;
	}

	/**
	 * Submits the SolrQuery object to SolrServer and handles sorting and
	 * faceting. The portion of the results that is shown ranges from result
	 * #from to #(from+resultCount)
	 * 
	 * @param query
	 *            SolrQuery object
	 * @param from
	 *            specifies which portion of all results is shown
	 * @param resultCount
	 *            specifies how many results are displayed
	 * @param sortField
	 *            the field that is used for sorting
	 * @param sortAsc
	 *            sortorder, if true -> ascending, else -> descending
	 * @param facetFilters
	 *            Map object containing all fields to facet on as key and, if
	 *            already selected, the filter value for this field
	 * @return a SearchResults object containing all POJOs from the
	 *         QueryResponse
	 */
	private SearchResults search(SolrQuery query, final int from, final int resultCount, final P4SortField sortField, final boolean sortAsc, Map<Schema.P4FacetField, String> facetFilters) {

		QueryResponse response = new QueryResponse();
		SearchResults results = new SearchResults();

		SolrQueryUtils.setResultRange(query, from, resultCount);

		// TODO deal with sorting on multivalued fields -> copyfield of one
		// value to a dedicated sortfield is done in P4IndexObject's respective
		// setters
		if (sortField == null) {
			LOGGER.debug("SortField is NULL! setting sortTitle and descending");
			SolrQueryUtils.setSortField(query, P4SortField.TITLE, sortAsc);
		} else {
			LOGGER.debug("SortField is " + sortField.getFieldName() + ". SortAsc? " + sortAsc);
			SolrQueryUtils.setSortField(query, sortField, sortAsc);
		}

		if (facetFilters == null) {
			SolrQueryUtils.enableFacets(query);
			facetFilters = SolrQueryUtils.getDefaultFacetMap();
		} else {
			SolrQueryUtils.setFacets(query, facetFilters);
		}

		try {
			LOGGER.debug("Query = " + query.getQuery());

			response = SolrServerConnection.getInstance().getSolrServer().query(query);

			results = ResultProcessor.extractResults(response, facetFilters);
			// ResultProcessor.setSelectedFacets(results, facetFilters);

			// set query params in results object
			results.getParams().setQuery(query.getQuery());
			results.getParams().setFrom(from);
			results.getParams().setResultCount(resultCount);
			results.getParams().setSortField(sortField != null ? sortField.getFieldName() : P4SortField.TITLE.getFieldName());
			results.getParams().setSortAscending(sortAsc);

		} catch (SolrServerException e) {
			LOGGER.fatal(e.getMessage());
			LOGGER.fatal("Invalid Query = '" + query.getQuery() + "'.");
			results.setErrorMessage(e.getMessage());
		}

		return results;
	}

	/**
	 * Queries Solr for auto-complete suggestions for an entered term. See
	 * SearchHandler "suggest" in solrConfig.xml for tweaking.
	 * 
	 * @param term
	 * @return
	 */
	public P4Suggestions getSuggestion(String term) {
		P4Suggestions suggs = new P4Suggestions(term);
		QueryResponse response = new QueryResponse();
		SolrQuery query = new SolrQuery(term);
		query.setQueryType("/suggest");

		try {
			response = SolrServerConnection.getInstance().getSolrServer().query(query);
			if (response.getSpellCheckResponse() != null && !response.getSpellCheckResponse().getSuggestionMap().entrySet().isEmpty()) {
				Map<String, Suggestion> resultMap = response.getSpellCheckResponse().getSuggestionMap();
				for (Entry<String, Suggestion> entry : resultMap.entrySet()) {
					suggs.suggestions.add(new P4Suggestion(entry.getKey(), entry.getValue().getAlternatives()));
				}
			}
		} catch (SolrServerException e) {
			LOGGER.fatal(e);
			LOGGER.fatal("Could not query Solr for suggestions. Query = '" + query.getQuery() + "'.");
		}
		return suggs;
	}
}
