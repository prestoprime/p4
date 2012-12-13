/**
 * P4Suggestions.java
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
package eu.prestoprime.search.util.suggestion;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class P4Suggestions {
	@XmlElement
	public String terms;
	@XmlElement
	public List<P4Suggestion> suggestions;

	public P4Suggestions() {
	}

	public P4Suggestions(String terms) {
		this.terms = terms;
		this.suggestions = new LinkedList<P4Suggestion>();
	}

	public P4Suggestions(String terms, List<P4Suggestion> suggestions) {
		this.terms = terms;
		this.suggestions = (suggestions != null) ? new LinkedList<P4Suggestion>(suggestions) : null;
	}

}
