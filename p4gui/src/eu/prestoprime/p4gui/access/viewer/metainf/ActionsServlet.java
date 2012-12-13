/**
 * ActionsServlet.java
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
package eu.prestoprime.p4gui.access.viewer.metainf;

import java.io.IOException;
import java.util.Date;

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

@WebServlet("/access/viewer/metainf/4")
public class ActionsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);

		RoleManager.checkRequestedRole(USER_ROLE.consumer, user.getCurrentP4Service().getRole(), response);

		String id = request.getParameter("id");

		// qa
		Date qa = AccessConnection.checkDataTypeAvailability(user.getCurrentP4Service(), id, "qa");
		if (qa != null) {
			Date qa_auto = AccessConnection.checkDataTypeAvailability(user.getCurrentP4Service(), id, "qa_auto");
			if (qa_auto != null) {
				request.setAttribute("qa_status", "NOT VALIDATED (Automatic)");
			} else {
				request.setAttribute("qa_status", "VALIDATED (Manual)");
			}
			request.setAttribute("qa_date", qa);
		} else {
			request.setAttribute("qa_status", "NOT AVAILABLE");
		}
		request.setAttribute("qa_actions", "qa_update_auto");

		// fprint
		Date fprint = AccessConnection.checkDataTypeAvailability(user.getCurrentP4Service(), id, "fprint");
		if (fprint != null) {
			request.setAttribute("fprint_status", "AVAILABLE");
			request.setAttribute("fprint_date", fprint);
		} else {
			request.setAttribute("fprint_status", "NOT AVAILABLE");
		}
		request.setAttribute("fprint_actions", "fprint_upload");

		// userMD
		Date usermd = AccessConnection.checkDataTypeAvailability(user.getCurrentP4Service(), id, "usermd");
		if (usermd != null) {
			request.setAttribute("usermd_status", "AVAILABLE");
			request.setAttribute("usermd_date", usermd);
		} else {
			request.setAttribute("usermd_status", "NOT AVAILABLE");
		}
		request.setAttribute("usermd_actions", "usermd_upload");

		Tools.servletInclude(this, request, response, "/body/access/viewer/metainf/actions/actions.jsp");
	}
}
