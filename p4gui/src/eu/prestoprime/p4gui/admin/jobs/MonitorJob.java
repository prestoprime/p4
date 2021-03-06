/**
 * MonitorJob.java
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
package eu.prestoprime.p4gui.admin.jobs;

import it.eurix.archtools.workflow.jaxb.WfStatus;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.connection.WorkflowConnection;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.util.Tools;

@WebServlet("/admin/job")
public class MonitorJob extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);

		String id = request.getParameter("id");
		WfStatus wfStatus = WorkflowConnection.getWorkflowStatus(user.getCurrentP4Service(), id);

		request.setAttribute("wfStatus", wfStatus);

		if (request.getParameter("div") != null)
			Tools.servletInclude(this, request, response, "body/admin/jobs/single/jobDiv.jsp");
		else
			Tools.servletInclude(this, request, response, "?_b=/body/admin/jobs/single/singleJob.jsp");
	}
}
