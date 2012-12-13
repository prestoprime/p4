/**
 * DRACMASegment.java
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
package eu.prestoprime.plugin.dracma.client;

public class DRACMASegment implements Comparable<DRACMASegment> {

	public static enum STATUS {
		waiting, processing, copying, resolved, failed
	};

	private String id;
	private String dracmaURN;
	private int begin;
	private int offset;
	private STATUS status;
	private String href;

	public DRACMASegment(String dracmaURN, int begin, int end) {
		this.dracmaURN = dracmaURN;
		this.begin = begin;
		this.offset = (end - begin);
		status = STATUS.waiting;
		href = "";
	}

	public String getId() {
		return id;
	}

	public String getUMID() {
		return dracmaURN;
	}

	public int getStart() {
		return begin;
	}

	public int getOffset() {
		return offset;
	}

	public void setWaiting(String id) {
		this.id = id;
		this.status = STATUS.waiting;
	}

	public void setProcessing() {
		this.status = STATUS.processing;
	}

	public void setCopying() {
		this.status = STATUS.copying;
	}

	public void setStoring(String href) {
		this.href = href;
	}

	public void setResolved() {
		this.status = STATUS.resolved;
	}

	public void setFailed() {
		this.status = STATUS.failed;
	}

	public STATUS getStatus() {
		return status;
	}

	public String getHref() {
		return href;
	}

	@Override
	public int compareTo(DRACMASegment ds) {
		return 1;
	}
}
