/**
 * ExecuteQorkflowServlet.java
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
package eu.prestoprime.p4gui.services.workflow;

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
import javax.servlet.http.Part;

import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.connection.WorkflowConnection;
import eu.prestoprime.p4gui.connection.WorkflowConnection.P4Workflow;
import eu.prestoprime.p4gui.model.JobList;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.services.auth.RoleManager;
import eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE;
import eu.prestoprime.p4gui.util.Tools;

@WebServlet("/wf/execute")
@MultipartConfig()
public class ExecuteWorkflowServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);
		RoleManager.checkRequestedRole(USER_ROLE.producer, user.getCurrentP4Service().getRole(), response);

		// get workflow name
		String wfID = request.getParameter("wfid");

		// get dynamic parameters
		Map<String, String> dParamsString = new HashMap<>();
		Map<String, File> dParamsFile = new HashMap<>();

		for (Part part : request.getParts()) {
			String key = part.getName();
			if (!key.equals("wfid")) {
				if (part.getContentType() == null) {// plain text
					String value = request.getParameter(key);

					dParamsString.put(key, value);
				} else {// file
					File value = File.createTempFile("wf-", ".tmp");

					dParamsFile.put(key, value);

					value.deleteOnExit();
					InputStream in = part.getInputStream();
					OutputStream out = new FileOutputStream(value);
					int read = 0;
					byte[] bytes = new byte[1024];
					while ((read = in.read(bytes)) != -1)
						out.write(bytes, 0, read);
					in.close();
					out.flush();
					out.close();
				}
			}
		}

		try {
			String jobID = WorkflowConnection.executeWorkflow(user.getCurrentP4Service(), P4Workflow.valueOf(wfID), dParamsString, dParamsFile);

			JobList jobs = Tools.getSessionAttribute(request.getSession(), P4GUI.JOBS_BEAN_NAME, JobList.class);
			// jobs.addJob(jobs.new Job(jobID, StatusType.WAITING));
		} catch (WorkflowRequestException e) {
			e.printStackTrace();
		}
	}
}
