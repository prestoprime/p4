/**
 * RequestP4ServiceServlet.java
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
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.P4GUI.P4guiProperty;
import eu.prestoprime.p4gui.connection.CommonConnection;
import eu.prestoprime.p4gui.model.P4Service;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.util.Tools;

@WebServlet("/p4service/request")
public class RequestP4ServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);

		if (user.isLogged()) {
			// get parameters
			String p4service = request.getParameter("p4service");
			String role = request.getParameter("role");
			String agree = request.getParameter("agree");

			if (p4service != null && role != null && agree != null && agree.equals("yes")) {
				// get terms of use
				String terms = CommonConnection.getTermsOfUse(new P4Service(new URL(p4service), null), role);

				try {
					// send the activation email
					String host = P4GUI.getProperty(P4guiProperty.MAIL_SERVER);
					String from = P4GUI.getProperty(P4guiProperty.MAIL_ADDRESS);
					String to = from;
					Properties props = System.getProperties();
					props.put("mail.smtp.host", host);
					Session session = Session.getDefaultInstance(props, null);
					MimeMessage message = new MimeMessage(session);
					message.setFrom(new InternetAddress(from));
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
					Address[] replyTo = { new InternetAddress(user.getEmail()) };
					message.setReplyTo(replyTo);
					message.setSubject("[PrestoPRIME] P4Service request");
					message.setText("Hi p4admin,\n" + "A P4 user requested a p4ws userID.\n\n" + "User details:\n" + "username: " + user.getUsername() + "\n" + "email: " + user.getEmail() + "\n" + "p4ws: " + p4service + "\n" + "role: " + role + "\n" + "\n" + terms + "\n\n" + "HAL 9000");
					Transport.send(message);

					request.setAttribute("messageTitle", "New P4 Service Requested");
					request.setAttribute("messageBody", "Your request will be processed by P4 admin\nand a confirmation email will be sent to " + user.getEmail() + ".");

					Tools.servletInclude(this, request, response, "?id=message");
					return;
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}
		Tools.servletInclude(this, request, response, "?_b=");
	}
}
