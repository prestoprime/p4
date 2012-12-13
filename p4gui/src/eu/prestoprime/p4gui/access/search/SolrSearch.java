/**
 * SolrSearch.java
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
package eu.prestoprime.p4gui.access.search;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.prestoprime.model.search.SearchResults;
import eu.prestoprime.model.search.SearchResults.Params.SelectedFacets.SelectedFacet;
import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.connection.SearchConnection;
import eu.prestoprime.p4gui.model.P4Service;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.services.auth.RoleManager;
import eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE;
import eu.prestoprime.p4gui.util.Tools;
import eu.prestoprime.search.util.Schema;

@WebServlet("/access/search/index")
public class SolrSearch extends HttpServlet {
	private final static Logger LOGGER = Logger.getLogger(SolrSearch.class);
	private final static long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		User user = ((User) Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class));

		RoleManager.checkRequestedRole(USER_ROLE.consumer, user.getCurrentP4Service().getRole(), response);

		P4Service service = user.getCurrentP4Service();
		try {
			// DEBUG stuff for checking server config.
			// System.out.println("request.getCharacterEncoding()) => " +
			// request.getCharacterEncoding());
			// System.out.println("request.getContentType() => " +
			// request.getContentType());
			// System.out.println("request.getQueryString() => " +
			// request.getQueryString());
			// System.out.println("request.getParameter(\"term\") => " +
			// request.getParameter("term"));
			// System.out.println("Charset.defaultCharset() => " +
			// Charset.defaultCharset());
			// System.out.println("System.getProperty(\"file.encoding\") => " +
			// System.getProperty("file.encoding"));

			String from = request.getParameter("from");
			String resultCount = request.getParameter("resultCount");
			String sortAsc = request.getParameter("sortAsc");
			String sortField = request.getParameter("sortField");
			String searchType = request.getParameter("searchType");

			SearchResults res = null;
			HashMap<String, String> fieldMap = new HashMap<>();
			HashMap<String, String> facetMap = new HashMap<>();

			// extract facet parameters to a map
			for (Schema.P4FacetField ff : Schema.P4FacetField.values()) {
				String value = request.getParameter(ff.getFieldName());
				if (value != null && !value.isEmpty()) {
					LOGGER.debug("Facet filter: " + ff.getFieldName() + " = " + value);
					facetMap.put(ff.getFieldName(), value);
				}
			}

			if (searchType == null || searchType.equals("quick")) {
				searchType = "quick";
				String query = request.getParameter("term");
				fieldMap.put("term", query);
				res = SearchConnection.simpleSolrSearch(service, query, from, resultCount, sortField, sortAsc, facetMap);
			} else {
				searchType = "adv";

				for (Schema.searchField sf : Schema.searchField.values()) {
					String value = request.getParameter(sf.getUrlParam());
					if (value != null && !value.isEmpty()) {
						LOGGER.debug("User input: " + sf.getFieldName() + " = " + value);
						fieldMap.put(sf.getUrlParam(), value);
					}
				}

				res = SearchConnection.advancedSolrSearch(service, fieldMap, from, resultCount, sortField, sortAsc, facetMap);
			}

			if (res != null) {

				/*
				 * TODO searchresult xml might be just <searchResults/> for a
				 * invalid query. Do correct check when unmarshalling in
				 * AccessConnection, return null in this case and remove this
				 * check here.
				 */
				if (res.getParams() != null) {

					TreeMap<String, String> urlParamMap = buildParamMap(fieldMap, res, searchType);

					request.setAttribute("search_result", res);
					request.setAttribute("url_param_map", urlParamMap);
				}
			}
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
			e.printStackTrace();
		}

		response.setContentType("text/html;charset=UTF-8");

		Tools.servletInclude(this, request, response, "/body/access/search/Solr/Results.jsp");
	}

	/**
	 * build a map containing url params for subsequent queries
	 * 
	 * @param fieldMap
	 *            search field queries (either containing only "term" or
	 *            specific dc fields)
	 * @param res
	 *            unmarshalled SearchResults for extracting preselected facets
	 * @param searchType
	 *            "adv" | "quick"
	 * @return new map ready for building parameter strings in refine links of
	 *         search result JSP
	 */
	private TreeMap<String, String> buildParamMap(HashMap<String, String> fieldMap, SearchResults res, String searchType) {
		TreeMap<String, String> urlParamMap = new TreeMap<String, String>();

		urlParamMap.putAll(fieldMap);
		if (searchType.equals("quick")) {
			urlParamMap.put("searchType", "quick");
		} else {
			urlParamMap.put("searchType", "adv");
		}
		urlParamMap.put("sortField", res.getParams().getSortField());

		urlParamMap.put("sortAsc", "" + res.getParams().isSortAscending());

		// process facetParams from returned SearchResults object
		for (SelectedFacet field : res.getParams().getSelectedFacets().getSelectedFacet()) {
			String facetValue = field.getFilterQuery();
			if (facetValue != null && !facetValue.equals("")) {
				// put selected values to the map and build respective
				// url params for subsequent faceting
				urlParamMap.put(field.getFacetName(), facetValue);
			}
		}
		return urlParamMap;
	}
}