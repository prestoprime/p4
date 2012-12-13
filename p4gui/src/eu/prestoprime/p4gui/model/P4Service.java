/**
 * P4Service.java
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

import java.net.URL;

import eu.prestoprime.p4gui.connection.CommonConnection;
import eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE;

public class P4Service {

	private URL url;
	private String userID;
	private USER_ROLE role;

	public P4Service(URL url, String userID) {
		this.url = url;
		this.userID = userID;
		this.role = CommonConnection.getUserRole(this);
	}

	public URL getURL() {
		return url;
	}

	public String getUserID() {
		return userID;
	}

	public USER_ROLE getRole() {
		return role;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof P4Service) {
			return ((P4Service) obj).getURL().equals(url);
		} else {
			return false;
		}
	}
}
