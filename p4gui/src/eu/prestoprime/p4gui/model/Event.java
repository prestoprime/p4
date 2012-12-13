/**
 * Event.java
 * Author: Francesco Rosso (rosso@eurix.it)
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
package eu.prestoprime.p4gui.model;

import java.util.Calendar;

public class Event {

	private String type;
	private Calendar dateTime;
	private String detail;

	public Event(String type, Calendar dateTime, String detail) {
		super();
		this.type = type;
		this.dateTime = dateTime;
		this.detail = detail;
	}

	public String getType() {
		return type;
	}

	public Calendar getDateTime() {
		return dateTime;
	}

	public String getDetail() {
		return detail;
	}

	@Override
	public String toString() {
		return dateTime + " " + type + " " + detail;
	}
}
