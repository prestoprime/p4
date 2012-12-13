/**
 * RetrieveActionsServlet.java
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
package eu.prestoprime.p4gui.admin.actions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.connection.AdminConnection;
import eu.prestoprime.p4gui.connection.WorkflowConnection;
import eu.prestoprime.p4gui.connection.WorkflowConnection.P4Workflow;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.services.auth.RoleManager;
import eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE;
import eu.prestoprime.p4gui.services.workflow.WorkflowRequestException;
import eu.prestoprime.p4gui.util.Tools;

@WebServlet("/admin/actions")
public class RetrieveActionsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(RetrieveActionsServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.debug("Called /admin/actions");

		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);
		RoleManager.checkRequestedRole(USER_ROLE.admin, user.getCurrentP4Service().getRole(), response);

		String actionType = request.getParameter("type");
		String jobID;
		switch (actionType) {
		case "dataType":
			String dataType = request.getParameter("dataType");
			boolean available = Boolean.parseBoolean(request.getParameter("available"));
			request.setAttribute("result", AdminConnection.checkDataType(user.getCurrentP4Service(), dataType, available));
			Tools.servletInclude(this, request, response, "?_b=body/admin/actions/dataTypeActions.jsp");
			break;
		case "formatRisk":
			request.setAttribute("result", AdminConnection.checkFormatRisk(user.getCurrentP4Service()));
			Tools.servletInclude(this, request, response, "?_b=body/admin/actions/formatRiskActions.jsp");
			break;
		case "fixityRisk":
			request.setAttribute("result", AdminConnection.getFixityChecklist(user.getCurrentP4Service()));
			Tools.servletInclude(this, request, response, "?_b=body/admin/actions/fixityRiskActions.jsp");
			break;
		case "rebuildSolrIndex":
			try {
				jobID = WorkflowConnection.executeWorkflow(user.getCurrentP4Service(), P4Workflow.valueOf("rebuild_index"), null, null);
				response.sendRedirect(request.getContextPath() + "/admin/job?id=" + jobID);
			} catch (WorkflowRequestException e) {
				e.printStackTrace();
			}
			break;
		case "rebuildRightsIndex":
			try {
				jobID = WorkflowConnection.executeWorkflow(user.getCurrentP4Service(), P4Workflow.valueOf("rebuild_rights_index"), null, null);
				response.sendRedirect(request.getContextPath() + "/admin/job?id=" + jobID);
			} catch (WorkflowRequestException e) {
				e.printStackTrace();
			}
			break;
		}
	}
}
