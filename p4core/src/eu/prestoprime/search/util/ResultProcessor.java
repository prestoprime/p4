/**
 * ResultProcessor.java
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;

import eu.prestoprime.model.search.SearchResults;
import eu.prestoprime.model.search.SearchResults.Facets;
import eu.prestoprime.model.search.SearchResults.Facets.Facet;
import eu.prestoprime.model.search.SearchResults.Facets.Facet.Value;
import eu.prestoprime.model.search.SearchResults.Hits;
import eu.prestoprime.model.search.SearchResults.Hits.Hit;
import eu.prestoprime.model.search.SearchResults.Params;
import eu.prestoprime.model.search.SearchResults.Params.SelectedFacets;
import eu.prestoprime.model.search.SearchResults.Params.SelectedFacets.SelectedFacet;
import eu.prestoprime.search.SearchConstants;
import eu.prestoprime.search.util.Schema.FieldType;
import eu.prestoprime.search.util.Schema.P4FacetField;

public class ResultProcessor {

	private static final Logger LOGGER = Logger.getLogger(ResultProcessor.class);

	private static final String hlTag = SearchConstants.getString("highlightingTag");

	public static SearchResults extractResults(QueryResponse resp, Map<P4FacetField, String> facetFilters) {

		SearchResults result = new SearchResults();

		Params params = new Params();
		params.setFrom(resp.getResults().getStart());
		result.setParams(params);

		Facets facets = extractFacetsFromResponse(resp, facetFilters);
		result.setFacets(facets);

		SelectedFacets selFac = getSelectedFacets(facetFilters);
		result.getParams().setSelectedFacets(selFac);

		result.setHits(processHits(resp));

		return result;

	}

	public static Hits processHits(QueryResponse resp) {
		List<P4IndexObject> objects = resp.getBeans(P4IndexObject.class);
		Map<String, List<String>> highlights = null;
		Hits hits = new Hits();
		hits.setTotal(resp.getResults().getNumFound());
		for (P4IndexObject object : objects) {
			highlights = resp.getHighlighting().get(object.getId());
			Hit hit = new Hit();
			hit.setId(object.getId());
			hit.setPos("" + objects.indexOf(object));
			hit.setCreateDate("" + object.getCreateDate());

			// if highlight snippet is available, use it. Otherwise set the
			// value from the bean.
			hit.getCreator().addAll(merge(highlights.get(Schema.searchField.CREATOR.getFieldName()), object.getDcCreator()));
			hit.getDate().addAll(object.getDcDateStrings());
			hit.getPublisher().addAll(merge(highlights.get(Schema.searchField.PUBLISHER.getFieldName()), object.getDcPublisher()));
			hit.getTitle().addAll(merge(highlights.get(Schema.searchField.TITLE.getFieldName()), object.getDcTitle()));

			hit.getDescription().addAll(merge(highlights.get(Schema.searchField.DESC.getFieldName()), object.getDcDescription()));
			hit.getFormat().addAll(merge(highlights.get(Schema.searchField.FORMAT.getFieldName()), object.getDcFormat()));
			hit.getIdentifier().addAll(merge(highlights.get(Schema.searchField.IDENT.getFieldName()), object.getDcIdentifier()));
			hit.getLanguage().addAll(merge(highlights.get(Schema.searchField.LANG.getFieldName()), object.getDcLang()));
			hit.getSubject().addAll(merge(highlights.get(Schema.searchField.SUBJECT.getFieldName()), object.getDcSubject()));
			hit.getUserAnnot().addAll(merge(highlights.get(Schema.searchField.WAISDA.getFieldName()), object.getUserAnnot()));
			hit.setDuration(object.getDuration());
			hit.setCodec(object.getCodec());
			hit.setAspectRat(object.getAspect());
			hit.setResolution(object.getResolution());
			hits.getHit().add(hit);
		}
		return hits;
	}

	/**
	 * Merge highlighted values and dcrecord values in one list, as they are
	 * returned seperately by Solr. Overwrite non-highlighted values.
	 * 
	 * @param highlights
	 *            the returned list of highlighted values that match the search
	 * @param dc
	 *            all values of the dip for one dc element.
	 * @return a List containing all values in this dc element ready for
	 *         display.
	 */
	private static List<String> merge(List<String> highlights, String[] dc) {
		List<String> result = new ArrayList<String>();

		if (dc != null && dc.length > 0) {
			for (String s : dc) {
				result.add(s);
			}
		}

		if (highlights != null && !highlights.isEmpty()) {
			for (String s : highlights) {
				int index = result.indexOf(s.replace("<" + hlTag + ">", "").replace("</" + hlTag + ">", ""));
				if (index != -1) {
					result.set(index, s);
				} else { // This should not happen as each highlighted value
							// should match a value in the DIP. Fire Log Message
							// if we get here.
					LOGGER.debug("Highlighted result with no matching dc record: " + s);
					result.add(s);
				}
			}
		}

		return result;
	}

	/**
	 * Match highlighted values to dcrecord values
	 * 
	 * @param highlights
	 *            the returned list of highlighted values that match the search
	 * @param dc
	 *            all values of the dip for one dc element.
	 * @return a List containing all values in this dc element ready for
	 *         display.
	 */
	private static List<String> matchHighlights(List<String> highlights, String[] dc) {
		List<String> result = new ArrayList<String>();

		if (highlights == null || highlights.isEmpty()) {
			result = Arrays.asList(dc);
		} else {
			// extract highlighted terms from snippets
			for (String s : highlights) {
				// use regex here to extract stuff between "<" + hlTag + ">" and
				// "</" + hlTag + ">"
				// put all these into a list
			}
		}

		// iterate the dc array and
		// check on occurrence of values from the previously built list
		// surround matches with emphasize tags

		return result;
	}

	public static Facets extractFacetsFromResponse(QueryResponse response, Map<P4FacetField, String> facetFilters) {
		// Map for all facetFields with according values from the queryResponse
		Facets facets = new Facets();
		// Put facetValues to this map

		for (FacetField field : response.getFacetFields()) {
			// validate FacetField against P4Schema and get the respective
			// FacetField

			P4FacetField p4Field = P4FacetField.get(field.getName());

			if (field.getValueCount() >= 1 && p4Field != null) {
				Facet facet = new Facet();
				facet.setFacetName(p4Field.getFieldName());
				for (Count facetValue : field.getValues()) {
					if (!facetValue.getName().isEmpty()) {
						Value value = new Value();
						value.setCount(facetValue.getCount());
						value.setTerm(facetValue.getName());

						// check if this value is already selected
						boolean isSelected = (facetFilters.get(p4Field) != null && facetFilters.get(p4Field).contains(facetValue.getName()));
						value.setSelected(isSelected);

						LOGGER.debug("Facet value: " + facetValue.getName() + " is selected? " + isSelected);

						// build a filterquery that is used when this facet is
						// selected
						String filter = "";
						if (isSelected) { // deselect
							String[] singleValues = facetFilters.get(p4Field).replaceAll("\"", "").split("( AND )");
							LOGGER.debug("Building new filter for deselecting. Starting from: " + facetFilters.get(p4Field));
							String delim = "";
							int i = 0;
							for (String s : singleValues) {
								LOGGER.debug("Iteration " + (++i) + ": " + s + " eq " + facetValue.getName() + " ? " + s.equals(facetValue.getName()));
								if (!s.equals(facetValue.getName())) {
									LOGGER.debug("No. Append this one -> ");
									filter += delim + "\"" + s + "\"";
									delim = " AND ";

								}
								LOGGER.debug("Iteration " + (++i) + ": " + filter);
							}
						} else {
							filter = facetFilters.get(p4Field);
							LOGGER.debug("Altering old filter for selecting. Starting from: " + filter);
							if (filter == null || filter.isEmpty()) {
								filter = "\"" + facetValue.getName() + "\"";
							} else {
								filter += " AND \"" + facetValue.getName() + "\"";
							}
						}
						LOGGER.debug("Result filter = " + filter);
						value.setFilterQuery(filter);
						facet.getValue().add(value);
					}
				}
				facets.getFacet().add(facet);
			}
		}

		// do the same for rangeFacets
		for (RangeFacet range : response.getFacetRanges()) {
			P4FacetField p4Field = P4FacetField.get(range.getName());

			if (range.getCounts().size() >= 1 && p4Field != null) {
				Facet facet = new Facet();
				facet.setFacetName(p4Field.getFieldName());
				if (p4Field.getAssocField().getType().equals(FieldType.TDATE)) {
					for (RangeFacet.Count count : (List<RangeFacet.Count>) range.getCounts()) {
						Value value = new Value();
						value.setTerm(formatDateString(count.getValue()));
						value.setCount(count.getCount());

						boolean isSelected = (facetFilters.get(p4Field) != null && !facetFilters.get(p4Field).isEmpty());
						value.setSelected(isSelected);

						if (!isSelected) {
							value.setFilterQuery(SolrQueryUtils.getDateRangeFilter(count.getValue()));
						} else {
							value.setFilterQuery("");
						}

						facet.getValue().add(value);
					}
				} else {
					// TODO implement for other ranged facets. right now no
					// other fieldTypes used yet...
				}
				facets.getFacet().add(facet);
			}
		}

		return facets;
	}

	private static SelectedFacets getSelectedFacets(Map<P4FacetField, String> facets) {
		SelectedFacets selFac = new SelectedFacets();
		if (facets != null) {
			for (Entry<P4FacetField, String> entry : facets.entrySet()) {
				SelectedFacet facet = new SelectedFacet();
				facet.setFacetName(entry.getKey().getFieldName());
				if (entry.getValue() == null || entry.getValue().isEmpty()) {
					facet.setFilterQuery("");
				} else {
					facet.setFilterQuery(entry.getValue());

					String[] singleValues = entry.getValue().split("( AND )");
					for (String s : singleValues) {
						facet.getValue().add(s);
					}
				}

				selFac.getSelectedFacet().add(facet);
			}

		}
		return selFac;
	}

	private static String formatDateString(String dateString) {
		// 2012-08-09T00:00:00Z
		String formatted = "";
		final String inFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		final String outFormat = "dd.MM.yyyy";
		DateFormat formatter = new SimpleDateFormat(inFormat);
		DateFormat formatter2 = new SimpleDateFormat(outFormat);

		try {
			Date inDate = (Date) formatter.parse(dateString);
			formatted = formatter2.format(inDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(inDate);
			cal.add(Calendar.DAY_OF_MONTH, 6); // TODO make this read gap value
												// from solr.properties
			formatted += " - " + formatter2.format(cal.getTime());
		} catch (ParseException e) {
			formatted = dateString;
			e.printStackTrace();
		}
		return formatted;
	}
}
