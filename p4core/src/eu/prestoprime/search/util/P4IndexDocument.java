/**
 * P4IndexDocument.java
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

import java.util.List;

import org.apache.solr.common.SolrInputDocument;

import eu.prestoprime.model.dc.Record;

@Deprecated
public class P4IndexDocument extends SolrInputDocument {

	private static final long serialVersionUID = 1L;

	public P4IndexDocument() {
	}

	public void addDcRecords(List<Record> dcRecords) {
		for (Record record : dcRecords) {

			this.addField(Schema.searchField.CONTRIB.getFieldName(), record.getContributor().toArray());
			this.addField(Schema.searchField.COVERAGE.getFieldName(), record.getCoverage().toArray());
			this.addField(Schema.searchField.CREATOR.getFieldName(), record.getCreator().toArray());
			this.addField(Schema.searchField.DATE.getFieldName(), record.getDate().toArray());
			this.addField(Schema.searchField.DESC.getFieldName(), record.getDescription().toArray());
			this.addField(Schema.searchField.FORMAT.getFieldName(), record.getFormat().toArray());
			this.addField(Schema.searchField.IDENT.getFieldName(), record.getIdentifier().toArray());
			this.addField(Schema.searchField.LANG.getFieldName(), record.getLanguage().toArray());
			this.addField(Schema.searchField.PUBLISHER.getFieldName(), record.getPublisher().toArray());
			this.addField(Schema.searchField.REL.getFieldName(), record.getRelation().toArray());
			this.addField(Schema.searchField.SOURCE.getFieldName(), record.getSource().toArray());
			this.addField(Schema.searchField.SUBJECT.getFieldName(), record.getSubject().toArray());
			this.addField(Schema.searchField.TITLE.getFieldName(), record.getTitle().toArray());
			this.addField(Schema.searchField.TYPE.getFieldName(), record.getType().toArray());
		}
	}
}
