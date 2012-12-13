/**
 * MainUpdateServlet.java
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
package eu.prestoprime.p4gui.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.model.workflow.WfDescriptor;
import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.connection.WorkflowConnection;
import eu.prestoprime.p4gui.connection.WorkflowConnection.P4Workflow;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.services.auth.RoleManager;
import eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE;
import eu.prestoprime.p4gui.services.workflow.WorkflowRequestException;
import eu.prestoprime.p4gui.util.Tools;

@WebServlet("/update")
@MultipartConfig
public class MainUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(MainUpdateServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);
		RoleManager.checkRequestedRole(USER_ROLE.producer, user.getCurrentP4Service().getRole(), response);

		WfDescriptor descriptor = WorkflowConnection.getWfDescriptor(user.getCurrentP4Service());
		request.setAttribute("wfDescriptor", descriptor);

		Tools.servletInclude(this, request, response, "?_b=body/update/update.jsp");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info("Called /update");

		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);
		RoleManager.checkRequestedRole(USER_ROLE.producer, user.getCurrentP4Service().getRole(), response);

		File updateFile = File.createTempFile("update-", ".tmp");
		updateFile.deleteOnExit();

		InputStream in = request.getPart("updateFile").getInputStream();
		OutputStream out = new FileOutputStream(updateFile);
		int read = 0;
		byte[] bytes = new byte[1024];
		while ((read = in.read(bytes)) != -1)
			out.write(bytes, 0, read);
		in.close();
		out.flush();
		out.close();

		String wfID = request.getParameter("wfID");
		String aipID = request.getParameter("aipID");

		// prepare parameters
		Map<String, String> dParamsString = new HashMap<>();
		dParamsString.put("id", aipID);

		Map<String, File> dParamsFile = new HashMap<>();
		dParamsFile.put("resultFile", updateFile);

		try {
			// execute workflow
			String jobID = WorkflowConnection.executeWorkflow(user.getCurrentP4Service(), P4Workflow.valueOf(wfID), dParamsString, dParamsFile);

			response.sendRedirect(request.getContextPath() + "/admin/job?id=" + jobID);
		} catch (WorkflowRequestException e) {
			e.printStackTrace();
			logger.error("Unable to execute worklfow " + wfID + "...");
			Tools.servletInclude(this, request, response, "?_b=body/update/update.jsp");
		}
	}
}
