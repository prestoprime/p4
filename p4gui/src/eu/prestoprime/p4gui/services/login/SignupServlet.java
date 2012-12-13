/**
 * SignupServlet.java
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
package eu.prestoprime.p4gui.services.login;

import java.io.IOException;
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

import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.P4GUI.P4guiProperty;
import eu.prestoprime.p4gui.util.DataBaseManager;
import eu.prestoprime.p4gui.util.Tools;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Tools.servletInclude(this, request, response, "?id=new");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String email2 = request.getParameter("email2");

		if (username != null && email != null && email2 != null) {
			if (!username.equals("") && !email.equals("")) {
				if (email.equals(email2)) {
					try {
						// check username uniqueness
						if (!DataBaseManager.getInstance().isUsernameAvailable(username)) {
							// username already present
							request.setAttribute("messageTitle", "Sorry...");
							request.setAttribute("messageBody", "The username is not available, please try another one.");

							Tools.servletInclude(this, request, response, "?id=message");
							return;
						} else {
							// save into DB
							DataBaseManager.getInstance().addUser(username, email);

							// send the notification email
							int hash = username.hashCode();
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
							message.setSubject("[PrestoPRIME] Activate your P4 account");
							message.setText("Hi " + username + ",\nin order to activate your P4 account you have to accept the terms of use.\n\n" + "Please, go to: http://" + request.getLocalName() + request.getContextPath() + "/?id=" + hash + "\n\n" + "P4 Team");
							Transport.send(message);

							request.setAttribute("messageTitle", "Congratulations!");
							request.setAttribute("messageBody", "Instructions to activate your P4 account\nhave been sent to " + email + ".");

							Tools.servletInclude(this, request, response, "?id=message");
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		Tools.servletInclude(this, request, response, "?id=new");
	}
}
