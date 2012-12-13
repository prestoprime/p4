/**
 * CreateUserIDServlet.java
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
package eu.prestoprime.p4gui.admin.users;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.prestoprime.model.workflow.StatusType;
import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.P4GUI.P4guiProperty;
import eu.prestoprime.p4gui.connection.WorkflowConnection;
import eu.prestoprime.p4gui.connection.WorkflowConnection.P4Workflow;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.services.auth.RoleManager;
import eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE;
import eu.prestoprime.p4gui.util.DataBaseManager;
import eu.prestoprime.p4gui.util.Tools;
import eu.prestoprime.workflow.exceptions.TaskExecutionFailedException;

@WebServlet("/admin/users/service/create")
public class CreateUserIDServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);
		RoleManager.checkRequestedRole(USER_ROLE.superadmin, user.getCurrentP4Service().getRole(), response);

		USER_ROLE role = USER_ROLE.valueOf(request.getParameter("role"));
		String username = request.getParameter("username");

		try {
			User customer = DataBaseManager.getInstance().getUserByUsername(username);
			if (user != null) {
				Map<String, String> dParamsString = new HashMap<>();
				dParamsString.put("user.role", role.toString());
				String jobID = WorkflowConnection.executeWorkflow(user.getCurrentP4Service(), P4Workflow.valueOf("create_user"), dParamsString, null);

				StatusType status = null;
				while (!(status = WorkflowConnection.getWorkflowStatus(user.getCurrentP4Service(), jobID).getStatus()).equals(StatusType.COMPLETED)) {
					if (status.equals(StatusType.FAILED)) {
						throw new TaskExecutionFailedException("Unable to create new userID...");
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				String userID = WorkflowConnection.getWorkflowStatus(user.getCurrentP4Service(), jobID).getResult().getValue();
				String email = customer.getEmail();

				String host = P4GUI.getProperty(P4guiProperty.MAIL_SERVER);
				String from = P4GUI.getProperty(P4guiProperty.MAIL_ADDRESS);
				String to = email;
				Properties props = System.getProperties();
				props.put("mail.smtp.host", host);
				Session session = Session.getDefaultInstance(props, null);
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(from));
				message.setSubject("[PrestoPRIME] Your userID for P4 Service");
				message.setText("Hi " + username + ",\n" + "welcome to P4 Service " + user.getCurrentP4Service().getURL() + "\n" + "\n" + "This is your userID associated with role '" + role + "':\n" + userID + "\n" + "\n" + "Enjoy!\n" + "P4 Team");
				Transport.send(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
