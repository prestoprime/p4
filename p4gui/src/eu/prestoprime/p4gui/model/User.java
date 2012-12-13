/**
 * User.java
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

import java.sql.SQLException;
import java.util.List;

import eu.prestoprime.p4gui.util.DataBaseManager;

public class User {

	private String username;
	private String email;

	private P4Service currentP4Service;
	private String currentJobId;

	public User() {
		this.username = null;
		this.email = null;
	}

	public User(String username, String email) {
		this.username = username;
		this.email = email;

		List<P4Service> services = this.getP4Services();
		if (services.size() > 0)
			this.setCurrentP4Service(services.get(0));
	}

	public String getUsername() {
		if (this.isLogged())
			return username;
		else
			return null;
	}

	@Deprecated
	public String getName() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public boolean isLogged() {
		if (username != null)
			return true;
		else
			return false;
	}

	public List<P4Service> getP4Services() {
		try {
			return DataBaseManager.getInstance().getP4Services(this.getUsername());
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void addP4Service(P4Service p4service) {
		try {
			DataBaseManager.getInstance().addP4Service(this.getUsername(), p4service.getURL(), p4service.getUserID());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public P4Service getCurrentP4Service() {
		return currentP4Service;
	}

	public void setCurrentP4Service(P4Service p4service) {
		this.currentP4Service = p4service;
	}

	public void setCurrentJobId(String currentJobId) {
		this.currentJobId = currentJobId;
	}

	public String getCurrentJobId() {
		return currentJobId;
	}
}