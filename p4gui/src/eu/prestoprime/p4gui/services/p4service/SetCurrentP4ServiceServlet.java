/**
 * SetCurrentP4ServiceServlet.java
 * Author: Francesco Rosso (rosso@eurix.it)
 * Contributors: Francesco Gallo (gallo@eurix.it)
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
package eu.prestoprime.p4gui.services.p4service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.model.P4Service;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.util.Tools;

@WebServlet("/p4service")
public class SetCurrentP4ServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);

		if (user.isLogged()) {
			String p4url = request.getParameter("p4service");
			if (p4url != null) {
				try {
					P4Service p4service = new P4Service(new URL(p4url), null);
					for (P4Service service : user.getP4Services()) {
						if (service.equals(p4service)) {
							user.setCurrentP4Service(service);
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		Tools.servletInclude(this, request, response, "?_b=");
	}
}
