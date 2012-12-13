/**
 * SolrQueryUtils.java
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
package eu.prestoprime.search.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;

import eu.prestoprime.search.SearchConstants;
import eu.prestoprime.search.util.Schema.P4FacetField;
import eu.prestoprime.search.util.Schema.P4SortField;
import eu.prestoprime.search.util.Schema.searchField;

/**
 * Helper class containing methods for configuring a SolrQuery object
 * 
 * @author Philip Kahle
 * 
 */
public class SolrQueryUtils {
	private static final Logger LOGGER = Logger.getLogger(SolrQueryUtils.class);

	/**
	 * Enable faceting on this solrQuery. Also sets the fields for faceting from
	 * the Hashmaps key. If this is already a subsequent query you have to put
	 * the selected facetValues to the appropriate value fields of the Hashmap
	 * 
	 * @param solrQuery
	 *            solrQuery object
	 * @param facets
	 *            a HashMap containing facetFields and values as key and value
	 *            respectively
	 */
	public static void setFacets(SolrQuery solrQuery, Map<P4FacetField, String> facets) {
		solrQuery.setFacet(true).setFacetLimit(10).setFacetMinCount(2);

		if (facets == null)
			facets = getDefaultFacetMap();

		for (Entry<P4FacetField, String> facet : facets.entrySet()) {
			// enable faceting on this field
			if (facet.getKey().getAssocField().getType().equals(Schema.FieldType.TDATE)) {
				// rangefacet on all fields of type tdate
				solrQuery.addDateRangeFacet(facet.getKey().getFieldName(), new Date(0), new Date(), SearchConstants.getString("dateRangeFacetGranularity"));
				LOGGER.debug("Enable range faceting on field '" + facet.getKey().getFieldName() + "'");
			} else {
				solrQuery.addFacetField(facet.getKey().getFieldName());
				LOGGER.debug("Enable faceting on field '" + facet.getKey().getFieldName() + "'");
			}

			// if there is already a value for filtering on this facet then add
			// a filterquery for this
			if (facet.getValue() != null && facet.getValue().length() > 0) {
				LOGGER.debug("Set facet value '" + facet.getValue() + "' for field '" + facet.getKey().getFieldName() + "'");
				solrQuery.addFilterQuery(facet.getKey().getFieldName() + ":" + facet.getValue());
			}
		}
	}

	/**
	 * Enable faceting on this solrQuery for all default facet fields.
	 * 
	 * @param solrQuery
	 *            solrQuery object
	 */
	public static void enableFacets(SolrQuery solrQuery) {
		setFacets(solrQuery, getDefaultFacetMap());
	}

	/**
	 * Enable sorting on a specific field in descending order
	 * 
	 * @param solrQuery
	 *            solrQuery object to alter
	 * @param sortField
	 *            the sortField (enumeration in Schema.java)
	 */
	public static void setSortField(SolrQuery solrQuery, P4SortField sortField) {
		setSortField(solrQuery, sortField, false);
	}

	/**
	 * Enable sorting on a specific field in customizable order
	 * 
	 * @param solrQuery
	 *            solrQuery object to alter
	 * @param sortField
	 *            the sortField (see enumeration in {@link Schema})
	 * @param sortAscending
	 */
	public static void setSortField(SolrQuery solrQuery, P4SortField sortField, boolean sortAscending) {
		LOGGER.debug("Enable sorting on field '" + sortField.getFieldName() + "'");
		LOGGER.debug(sortAscending ? "Order ASCENDING -> " + ORDER.asc.toString() : "Order DESCENDING -> " + ORDER.desc.toString());
		solrQuery.setSortField(sortField.getFieldName(), sortAscending ? ORDER.asc : ORDER.desc);
	}

	/**
	 * Specify the chunk of search results to be returned which is needed for
	 * paging in the front-end.
	 * 
	 * @param query
	 * @param from
	 * @param resultCount
	 */
	public static void setResultRange(SolrQuery query, int from, int resultCount) {
		LOGGER.debug("Setting result range -> " + from + "-" + (from + resultCount));
		query.setStart(from).setRows(resultCount);

	}

	/**
	 * Builds a Map containing all allowed FacetFields (see enumeration in
	 * {@link Schema}) with empty value fields.
	 * 
	 * @return the map
	 */
	public static Map<P4FacetField, String> getDefaultFacetMap() {
		Map<P4FacetField, String> facets = new HashMap<P4FacetField, String>();
		for (P4FacetField facet : P4FacetField.values()) {
			facets.put(facet, "");
		}
		return facets;
	}

	/**
	 * Enable Solr's Highlighting Component on this query object. Each search
	 * term is highlighted in all returned fields.
	 * 
	 * @param solrQuery
	 */
	public static void enableHighlightAllFields(SolrQuery solrQuery) {
		LOGGER.debug("Enable highlighting on all fields covered in catchAll field.");
		enableHighlight(solrQuery, null);
	}

	/**
	 * Enable Solr's Highlighting Component on this query object. Search terms
	 * in the map (as values) are only highlighted in the belonging search
	 * fields.
	 * 
	 * @param solrQuery
	 * @param fieldMap
	 */
	public static void enableHighlight(SolrQuery solrQuery, final Map<Schema.searchField, String> fieldMap) {
		// enable highlighting on Query
		solrQuery.setHighlight(true);

		// unlimited fragment size and hightlights
		solrQuery.setHighlightFragsize(0);

		StringBuilder logMsg = new StringBuilder("Enable highlighting on fields: ");

		// use these to set a start/end tag for highlights other than <em>
		// (defined in Solr Config)
		// solrQuery.setHighlightSimplePre(Constants.getString("HIGHLIGHT_PRE"));
		// solrQuery.setHighlightSimplePost(Constants.getString("HIGHLIGHT_POST"));

		if (fieldMap == null || fieldMap.isEmpty()) { // dismax query -> enable
														// highlighting on all
														// fields
			solrQuery.set("hl.highlightMultiTerm", true);

			for (searchField s : Schema.catchAll) {
				solrQuery.addHighlightField(s.getFieldName());
				logMsg.append(s.getFieldName() + ", ");
			}

			LOGGER.debug(logMsg.toString().substring(0, logMsg.length() - 2));
		} else {
			// highlight matching words in queried fields only
			solrQuery.set("hl.usePhraseHighlighter", true);
			solrQuery.setHighlightRequireFieldMatch(true);
			for (Entry<Schema.searchField, String> e : fieldMap.entrySet()) {
				// highlighting on range fields does no work. Just use text
				// fields
				if (e.getKey().getType().equals(Schema.FieldType.P4_TEXT) || e.getKey().getType().equals(Schema.FieldType.STRING)) {
					logMsg.append(e.getKey().getFieldName() + ", ");
					solrQuery.addHighlightField(e.getKey().getFieldName());
				}
			}
			LOGGER.debug(logMsg.toString().substring(0, logMsg.length() - 2));
		}
	}

	public static String getDateRangeFilter(String value) {
		// createdate:[1976-03-06T23:59:59.999Z TO
		// 1976-03-06T23:59:59.999Z+1YEAR]

		return "[" + value + " TO " + value + SearchConstants.getString("dateRangeFacetGranularity") + "]";// +
																											// "-1DAY]";
	}

}
