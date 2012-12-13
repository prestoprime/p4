/**
 * Schema.java
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

import java.util.HashMap;
import java.util.Map;

import eu.prestoprime.search.SearchConstants;

/**
 * Convenience class that reflects the Solr Schema as enumerations containing
 * all index field names joined with url parameter names for automatic matching
 * of url_parameters to fields and a label for displaying without revealing the
 * index Schema to end users. The SortField enumeration contains sortable type
 * fields that are filled via copyfield directive by Solr. Other fields for
 * sorting may be added too at the risk of performance loss. The FacetField
 * enumeration contains all fields that may be interesting for faceting but do
 * not have a special type. So feel free to add fields here.
 * 
 * @author Philip Kahle
 * 
 */
public class Schema {
	// These constants define URL Parameter names for communication between
	// Frontend, Servlets and Rest APIs.
	// Jersey's QueryParam annotation won't work when defining these constants
	// in the enum itself.
	// Instead, these here are passed to the enum below.
	// ALTERING THESE VALUES WILL AFFECT P4WS ADVANCED SEARCH METHOD PARAMETERS!
	public static final String TITLE_URI_PARAM = "titl";
	public static final String CONTR_URI_PARAM = "cont";
	public static final String COV_URI_PARAM = "cov";
	public static final String CREA_URI_PARAM = "crea";
	public static final String DATE_URI_PARAM = "dat";
	public static final String DESC_URI_PARAM = "desc";
	public static final String FORM_URI_PARAM = "form";
	public static final String IDENT_URI_PARAM = "ident";
	public static final String LANG_URI_PARAM = "lang";
	public static final String PUB_URI_PARAM = "pub";
	public static final String REL_URI_PARAM = "rel";
	public static final String SOURCE_URI_PARAM = "sourc";
	public static final String SUBJ_URI_PARAM = "subj";
	public static final String TYPE_URI_PARAM = "type";
	public static final String ID_URI_PARAM = "id";
	public static final String CRDAT_URI_PARAM = "crDat";
	public static final String ANNOT_URI_PARAM = "annot"; // user annotations
															// from waisda
	public static final String ASPECT_URI_PARAM = "aspct";
	public static final String WIDTH_URI_PARAM = "width";
	public static final String HEIGHT_URI_PARAM = "height";
	public static final String RES_URI_PARAM = "res";
	public static final String CODEC_URI_PARAM = "codec";
	public static final String DURAT_URI_PARAM = "durat";

	public static final searchField[] catchAll = { searchField.TITLE, searchField.CREATOR, searchField.PUBLISHER, searchField.DESC, searchField.FORMAT, searchField.IDENT, searchField.SUBJECT, searchField.WAISDA };

	/**
	 * Enumeration of allowed sortFields. In case of numbers: Use Sortable type
	 * fields in Solr's schema.xml In case of multivaluedFields: define
	 * singlevalued field in the schema.xml and copy the right value.
	 */
	public static enum P4SortField {
		CREATOR("creatorSort"), DATE("dateSort"), TITLE("titleSort");

		private final String fieldName;

		private P4SortField(String s) {
			fieldName = s;
		}

		public String getFieldName() {
			return fieldName;
		}

		private static final Map<String, P4SortField> lookup = new HashMap<>();
		static {
			for (P4SortField s : P4SortField.values())
				lookup.put(s.getFieldName(), s);
		}

		public static P4SortField get(String fieldName) {
			return lookup.get(fieldName);
		}
	}

	/**
	 * Enumeration of allowed facetFields.
	 */
	public static enum P4FacetField {
		CREATOR("creatorFacet", searchField.CREATOR), DATE("dateFacet", searchField.DATE), PUBLISHER("publisherFacet", searchField.PUBLISHER), FORMAT("formatFacet", searchField.FORMAT), ANNOTATION("waisdaFacet", searchField.WAISDA), CREATEDATE("createDateFacet", searchField.CREATEDATE), SUBJECT("subjectFacet", searchField.SUBJECT), LANGUAGE("languageFacet", searchField.LANG), ASPECT("aspectRatioFacet", searchField.ASPECT), DURATION("durationFacet", searchField.DURATION);
		// RESOLUTION("resolutionFacet", searchField.RESOLUTION);

		private final String fieldName; // the facet field name in the schema
		private final searchField associatedField; // the index field that holds
													// the processed values for
													// this facet
		private final Boolean active; // a flag that is read from
										// solr.properties. Allows to quickly
										// switch facets on and off via
										// configuration.

		private P4FacetField(String fieldN, searchField associatedField) {
			this.fieldName = fieldN;
			this.associatedField = associatedField;
			this.active = SearchConstants.getActiveFacets().contains(fieldN);
		}

		public String getFieldName() {
			return fieldName;
		}

		public searchField getAssocField() {
			return associatedField;
		}

		public boolean isActive() {
			return (active == null) ? false : active.booleanValue();
		}

		// Reverse-lookup map for getting a facetField from an abbreviation
		private static final Map<String, P4FacetField> lookup = new HashMap<>();

		static {
			for (P4FacetField s : P4FacetField.values())
				lookup.put(s.getFieldName(), s);
		}

		public static P4FacetField get(String fieldName) {
			return lookup.get(fieldName);
		}

		public static boolean isFacet(String fieldName) {
			return lookup.containsKey(fieldName);
		}
	}

	public static enum searchField {
		CONTRIB("dcContributor", CONTR_URI_PARAM, "Contributor", FieldType.P4_TEXT), COVERAGE("dcCoverage", COV_URI_PARAM, "Coverage", FieldType.P4_TEXT), CREATOR("dcCreator", CREA_URI_PARAM, "Creator", FieldType.P4_TEXT), DATE("dcDate", DATE_URI_PARAM, "Date", FieldType.TDATE), DESC("dcDescription", DESC_URI_PARAM, "Description", FieldType.P4_TEXT), FORMAT("dcFormat", FORM_URI_PARAM, "Format", FieldType.P4_TEXT), IDENT("dcIdentifier", IDENT_URI_PARAM, "Identifier", FieldType.STRING), LANG("dcLanguage", LANG_URI_PARAM, "Language", FieldType.P4_TEXT), PUBLISHER("dcPublisher", PUB_URI_PARAM, "Publisher", FieldType.P4_TEXT), REL("dcRelation", REL_URI_PARAM, "Relation", FieldType.P4_TEXT), SOURCE("dcSource", SOURCE_URI_PARAM, "Source", FieldType.P4_TEXT), SUBJECT("dcSubject", SUBJ_URI_PARAM, "Subject", FieldType.P4_TEXT), TITLE("dcTitle", TITLE_URI_PARAM, "Title", FieldType.P4_TEXT), TYPE("dcType", TYPE_URI_PARAM, "Type", FieldType.P4_TEXT), ID("id", ID_URI_PARAM, "AIP-ID",
				FieldType.STRING), CREATEDATE("createDate", CRDAT_URI_PARAM, "Creation Date", FieldType.TDATE), WAISDA("waisda", ANNOT_URI_PARAM, "User Annotation", FieldType.P4_TEXT), ASPECT("techAspectRatio", ASPECT_URI_PARAM, "Aspect Ratio", FieldType.STRING), // type="string"
																																																																		// indexed="true"
																																																																		// stored="true"
																																																																		// multiValued="false"
																																																																		// />
		WIDTH("techWidth", WIDTH_URI_PARAM, "Width", FieldType.TINT), // <field
																		// name="width"
																		// type="tint"
																		// indexed="true"
																		// stored="true"
																		// multiValued="false"
																		// />
		HEIGHT("techHeight", HEIGHT_URI_PARAM, "Height", FieldType.TINT), // <field
																			// name="height"
																			// type="tint"
																			// indexed="true"
																			// stored="true"
																			// multiValued="false"
																			// />
		RESOLUTION("techResolution", RES_URI_PARAM, "Resolution", FieldType.STRING), // <field
																						// name="resolution"
																						// type="string"
																						// indexed="true"
																						// stored="true"
																						// multiValued="false"
																						// />
		CODEC("techCodec", CODEC_URI_PARAM, "Codec", FieldType.STRING), // <field
																		// name="codec"
																		// type="string"
																		// indexed="true"
																		// stored="true"
																		// multiValued="false"
																		// />
		DURATION("techDuration", DURAT_URI_PARAM, "Duration", FieldType.TFLOAT); // <field
																					// name="duration"
																					// type="tfloat"
																					// indexed="true"
																					// stored="true"
																					// multiValued="false"
																					// />

		private final String fieldName;
		private final String urlParam;
		private final String label;
		private final FieldType type;

		private searchField(String fieldN, String urlP, String labl, FieldType type) {
			this.fieldName = fieldN;
			this.urlParam = urlP;
			this.label = labl;
			this.type = type;
		}

		public final String getFieldName() {
			return fieldName;
		}

		public final String getUrlParam() {
			return urlParam;
		}

		public final String getLabel() {
			return label;
		}

		public final FieldType getType() {
			return type;
		}

		private static final Map<String, searchField> lookup = new HashMap<>();
		static {
			for (searchField d : searchField.values())
				lookup.put(d.getUrlParam(), d);
		}

		public static searchField get(String urlParam) {
			return lookup.get(urlParam);
		}
	}

	/**
	 * Enum reflecting the datatypes used for fields in solr's schema.xml</br>
	 * </br> STRING = no processing on indexing/query time. Exact matching (i.e.
	 * for identifier)</br> P4_TEXT = remove stopwords, lowercase, whitespace
	 * tokenization, remove latin accents, process synonyms</br> TDATE = no
	 * processing. Allows range queries.</br> TINT = integer. Trie based</br>
	 * TFLOAT = float. Trie based</br>
	 */
	public static enum FieldType {
		P4_TEXT, STRING, TDATE, TINT, TFLOAT;
	}
}
