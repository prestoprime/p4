/**
 * RebuildIndexServlet.java
 * Author: Philip Kahle (philip.kahle@uibk.ac.at)
 * 
 * This file is part of PrestoPRIME Preservation Platform (P4).
 * 
 * Copyright (C) 2009-2012 University of Innsbruck, Austria
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.connection.WorkflowConnection;
import eu.prestoprime.p4gui.connection.WorkflowConnection.P4Workflow;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.services.auth.RoleManager;
import eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE;
import eu.prestoprime.p4gui.services.workflow.WorkflowRequestException;
import eu.prestoprime.p4gui.util.Tools;

@WebServlet("/admin/actions/rebuildIndex")
public class RebuildIndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(RebuildIndexServlet.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);
		RoleManager.checkRequestedRole(USER_ROLE.producer, user.getCurrentP4Service().getRole(), response);

		logger.info("Called /admin/actions/rebuildIndex");

		// get wfID
		String wfID = "rebuild_index";

		// execute workflow
		Map<String, File> dParamsFile = new HashMap<>();

		// FIXME Setting no file here results in a BadRequest. MultipartEntity
		// (cp. executeWorkflow) without a part seems to be not allowed...
		File dummy = new File("/tmp/dummy.txt");
		if (!dummy.exists()) {
			OutputStream out = new FileOutputStream(dummy);
			out.close();
		}
		dummy.setLastModified(System.currentTimeMillis());
		dParamsFile.put("sip", dummy);
		// END WORKAROUND

		String jobID;
		try {
			jobID = WorkflowConnection.executeWorkflow(user.getCurrentP4Service(), P4Workflow.valueOf(wfID), null, dParamsFile);

			if (jobID.startsWith("<html>")) { // it's the tomcat 400 page
				response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			} else {
				response.sendRedirect(request.getContextPath() + "/admin/job?id=" + jobID);
			}
		} catch (WorkflowRequestException e) {
			e.printStackTrace();
		}
	}
}
