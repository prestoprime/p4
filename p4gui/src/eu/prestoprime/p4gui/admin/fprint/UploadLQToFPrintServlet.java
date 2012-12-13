/**
 * UploadLQToFPrintServlet.java
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
package eu.prestoprime.p4gui.admin.fprint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.prestoprime.p4gui.connection.WorkflowConnection;
import eu.prestoprime.p4gui.connection.WorkflowConnection.P4Workflow;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.services.auth.RoleManager;
import eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE;
import eu.prestoprime.p4gui.services.workflow.WorkflowRequestException;
import eu.prestoprime.p4gui.util.Tools;

@WebServlet("/admin/fprint/upload")
public class UploadLQToFPrintServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(UploadLQToFPrintServlet.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info("Called /admin/fprint/upload");

		User user = Tools.getSessionAttribute(request.getSession(), "user", User.class);

		RoleManager.checkRequestedRole(USER_ROLE.admin, user.getCurrentP4Service().getRole(), response);

		try {
			String id = request.getParameter("id");

			if (id != null) {
				Map<String, String> dParamsString = new HashMap<>();
				dParamsString.put("id", id);

				String wfID = WorkflowConnection.executeWorkflow(user.getCurrentP4Service(), P4Workflow.valueOf("fprint_upload"), dParamsString, null);

				response.getWriter().write(wfID);
			}
		} catch (WorkflowRequestException e) {
			logger.error(e.getMessage());
		}
	}
}
