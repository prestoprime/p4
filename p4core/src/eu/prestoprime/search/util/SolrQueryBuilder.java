/**
 * SolrQueryBuilder.java
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

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.search.util.Schema.FieldType;

/**
 * Helper class for forming Solr query syntax from seperate field-specific query
 * Strings
 * 
 * @author Philip Kahle
 * 
 */
public class SolrQueryBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(SolrQueryBuilder.class);
	// Pattern includes Solr's reserved characters { +-&|!(){}[]^~*?:\ } (Quote
	// " also belongs to them but is treated differently..)
	private static final String specialCharPattern = "[\\s\\+\\-&|!(){}\\[\\]^~*?:\\\\]+";
	private static final String dateRegex = "(([0-9]{4}-{1}[0-9]{2}-{1}[0-9]{2})T00:00:00Z)?#{1}(([0-9]{4}-{1}[0-9]{2}-{1}[0-9]{2})T00:00:00Z)?";
	private static final Pattern datePattern = Pattern.compile(dateRegex);

	/**
	 * Takes a map containing Solr fieldnames and search terms from the user
	 * interface as input and builds fieldspecific queries in the form
	 * +(fieldname:query) which are concatenated. User input allows using quotes
	 * for exact matches. Single words are wrapped in asterisks for matching
	 * also parts of words.
	 * 
	 * @param paramMap
	 * @return a query in Solr syntax
	 */
	public static String buildQuery(Map<Schema.searchField, String> paramMap) {
		StringBuilder sb = new StringBuilder();
		String delim = "";
		String filter = "";
		for (Entry<Schema.searchField, String> e : paramMap.entrySet()) {
			if (e.getValue() != null && !e.getValue().isEmpty()) {
				LOGGER.debug("At Field:" + e.getKey() + " | Value = " + e.getValue());

				if (e.getKey().getType().equals(FieldType.STRING)) {
					// String -> exact match. Keep all reserved characters but
					// quote it.
					filter = buildEqualsFilter(e.getKey().getFieldName(), e.getValue());
				} else if (e.getKey().getType().equals(FieldType.TDATE)) {
					filter = buildDateFilter(e.getKey().getFieldName(), e.getValue());
				} else {
					// expect P4_TEXT and apply complete filter logic
					filter = buildP4Filter(e.getKey().getFieldName(), e.getValue());
				}
				if (filter.length() > 0) {
					sb.append(delim);
					sb.append("+(");
					sb.append(filter);
					sb.append(")");
					delim = " ";
				}
			}
		}
		LOGGER.debug("Built query: " + sb.toString());
		return sb.toString();
	}

	/**
	 * Build a field specific query for matching parts of words
	 * 
	 * @param fieldName
	 * @param value
	 *            input string with whitespace seperated query words
	 * @return fieldName:(*word1* *word2* ... )
	 */
	private static String buildContainsFilter(String fieldName, String value) {
		String filter = "";
		if (value != null && !value.trim().isEmpty()) {
			StringBuilder term = new StringBuilder();
			String[] values = value.toLowerCase().split("\\s+");
			String delim = "";
			for (String v : values) {
				if (!v.trim().isEmpty()) {
					term.append(delim);
					term.append("*");
					term.append(v);
					term.append("*");
					delim = " ";
				}
			}
			filter = fieldName + ":(" + term.toString() + ")";
		}
		return filter;
	}

	/**
	 * Build a field specific query for matching an exact sequence of words
	 * 
	 * @param fieldName
	 * @param value
	 *            input string with whitespace seperated query words
	 * @return fieldName:"word1 word2 ..."
	 */
	private static String buildEqualsFilter(String fieldName, String value) {
		String filter = "";
		if (value != null && value != "") {
			filter = fieldName + ":\"" + value + "\"";
		}
		return filter;
	}

	private static String buildDateFilter(String fieldName, String value) {
		String filter = "";
		if (value != null && value != "") {
			// value = 2012-08-09T00:00:00Z#2012-08-10T00:00:00Z ; transform
			// into i.e. createDate:[* TO NOW]
			if (isValidDate(value)) {
				if (value.startsWith("#")) {
					filter = fieldName + ":[* TO " + value.substring(1) + "]";
				} else if (value.endsWith("#")) {
					filter = fieldName + ":[" + value.substring(0, value.length() - 1) + " TO NOW]";
				} else {
					String[] range = value.split("#");
					if (range.length == 2) {
						filter = fieldName + ":[" + range[0] + " TO " + range[1] + "]";
					} else {
						LOGGER.error("Got weird date format: " + value);
						return "";
					}

				}
			}
		}
		return filter;
	}

	/**
	 * determine quoted parts of a query and build respective exact queries and
	 * loosely matching queries with getContainsFilter() and getExactFilter().
	 * In case of an odd number of quotes, all quotes are dropped and
	 * 
	 * @param fieldName
	 * @param value
	 * @return
	 */
	private static String buildP4Filter(String fieldName, String value) {
		StringBuilder filter = new StringBuilder();
		StringBuilder contains = new StringBuilder();
		if (hasOddNumberOfQuotes(value)) { // odd number of quotation marks is
											// ignored (remove quotes and build
											// containsFilter)
			LOGGER.debug("Detected " + (value.split("\"").length - 1) + " quotes. Building contains-filter...");
			return buildContainsFilter(fieldName, value.replace("\"", ""));
		} else { // even number of quotes means correct quoting.
			String delim = "";
			while (value.indexOf('"') != -1) { // Extract quoted parts and
												// transform to exact queries.
												// Store all loose words to a
												// separate string.
				int pos = value.indexOf('"');
				String start = value.substring(0, pos);
				value = value.substring(pos + 1);
				if (start.length() > 0) {
					contains.append(start);
					contains.append(" ");
				}
				pos = value.indexOf('"');
				filter.append(delim);
				filter.append(buildEqualsFilter(fieldName, value.substring(0, pos)));
				value = value.substring(pos + 1);
				delim = " OR ";
			}
			if (value.length() > 0)
				contains.append(value);
			// Build Contains filter query from all remaining, non-quoted words.
			// Remove reserved characters (these are only allowed in quoted
			// terms)
			String containsString = replaceSpecialChars(contains.toString());
			if (containsString.length() != 0) {
				filter.append(delim);
				filter.append(buildContainsFilter(fieldName, containsString));
			}
		}
		return filter.toString();
	}

	private static boolean hasOddNumberOfQuotes(String value) {
		return (value.split("\"", -1).length - 1) % 2 != 0;
	}

	private static String replaceSpecialChars(String value) {
		return value.replaceAll(specialCharPattern, " ").trim();
	}

	private static String wrapFilter(String filter) {
		if (filter != null && !filter.isEmpty()) {
			filter = "+(" + filter + ")";
		} else {
			filter = "";
		}
		return filter;
	}

	private static boolean isValidDate(String dateString) {
		Matcher m = datePattern.matcher(dateString);
		return m.matches();
	}

	// public static String encode(String s, String encoding){
	// if(encoding == null) encoding = "UTF-8";
	// if(!encoding.equals("UTF-8"))
	// LOGGER.warn("URL encoding does not use UTF-8! Might cause problems");
	// String codS = "";
	// if(s != null && !s.isEmpty()){
	// try{
	// codS = URLEncoder.encode(s, encoding);
	// } catch(UnsupportedEncodingException e){ //Should never happen
	// LOGGER.error(e.getMessage() +
	// ": Using system default charset for encoding URL parameters which might cause problems!");
	// e.printStackTrace();
	// codS = URLEncoder.encode(s);
	// }
	// }
	// return codS;
	// }

	public static void main(String[] schorsch) {

		String regex = "([0-9]{4}-{1}[0-9]{2}-{1}[0-9]{2}T00:00:00Z)?#{1}([0-9]{4}-{1}[0-9]{2}-{1}[0-9]{2}T00:00:00Z)?";
		Pattern p = Pattern.compile(regex);

		String[] heinz = { "2012-08-09T00:00:00Z#2012-08-10T00:00:00Z", "2012-08-09T00:00:00Z#", "#2012-08-10T00:00:00Z" };

		for (String s : heinz) {
			try {
				// System.out.println("===== " + s + " ======");
				//
				// Matcher m = p.matcher(s);
				// System.out.println("Matches ? " + m.matches());
				// int i = 0;
				// System.out.println("Find ? " + m.find());
				// System.out.println(1 + " : " + m.group());
				System.out.println(buildDateFilter("test", s));

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

}
