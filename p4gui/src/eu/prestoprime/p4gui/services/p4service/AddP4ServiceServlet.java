/**
 * AddP4ServiceServlet.java
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
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.connection.CommonConnection;
import eu.prestoprime.p4gui.model.P4Service;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE;
import eu.prestoprime.p4gui.util.Tools;

@WebServlet("/p4service/add")
public class AddP4ServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);

		if (user.isLogged()) {
			String p4service = request.getParameter("p4service");
			String userID = request.getParameter("userID");

			if (p4service != null && userID != null) {
				P4Service service = new P4Service(new URL(p4service), userID);

				// check userID
				USER_ROLE role = CommonConnection.getUserRole(service);
				if (role == USER_ROLE.guest) {// userID doesn't exist
					// error message
					request.setAttribute("messageTitle", "Sorry...");
					request.setAttribute("messageBody", "The userID that you provided\nis not valid for P4 Service " + p4service + ".");

					Tools.servletInclude(this, request, response, "?id=message");
					return;
				} else {// userID is ok
					// add or overwrite
					user.addP4Service(service);

					// select new
					user.setCurrentP4Service(service);

					// confirmation message
					request.setAttribute("messageTitle", "Congratulations!");
					request.setAttribute("messageBody", "Your userID has been accepted and stored.\nYou will find this P4 Service in the MyP4 menu.");

					Tools.servletInclude(this, request, response, "?id=message");
					return;
				}
			}
		}
		Tools.servletInclude(this, request, response, "?_b=");
	}
}
