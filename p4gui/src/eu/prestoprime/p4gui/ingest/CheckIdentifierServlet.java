/**
 * CheckIdentifierServlet.java
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
package eu.prestoprime.p4gui.ingest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.connection.AccessConnection;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.services.auth.RoleManager;
import eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE;
import eu.prestoprime.p4gui.util.Tools;

@WebServlet("/ingest/checkIdentifier.do")
public class CheckIdentifierServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);

		RoleManager.checkRequestedRole(USER_ROLE.producer, user.getCurrentP4Service().getRole(), response);

		String identifier = request.getParameter("identifier");

		if (identifier != null && !identifier.equals("")) {
			if (AccessConnection.checkIdentifier(user.getCurrentP4Service(), identifier)) {
				// available
				response.getWriter().write("<img src=\"" + request.getContextPath() + "/resources/ingest/ok.png\">");
			} else {
				// not available
				response.getWriter().write("<img src=\"" + request.getContextPath() + "/resources/ingest/alert2.png\">");
			}
		}
	}
}
