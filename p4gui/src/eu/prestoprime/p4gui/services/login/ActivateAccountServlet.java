/**
 * ActivateAccountServlet.java
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
import java.sql.SQLException;
import java.util.Properties;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.P4GUI.P4guiProperty;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.util.DataBaseManager;
import eu.prestoprime.p4gui.util.Tools;

@WebServlet("/activate")
public class ActivateAccountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(ActivateAccountServlet.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int hashCode = Integer.parseInt(request.getParameter("id"));
		String password = request.getParameter("password");
		String password2 = request.getParameter("password2");
		String name = request.getParameter("name");
		String institution = request.getParameter("institution");
		String phone = request.getParameter("phone");
		String address = request.getParameter("address");

		if (password != null && password2 != null && name != null && institution != null && phone != null && address != null) {
			if (!password.equals("") && !password2.equals("") && !name.equals("") && !institution.equals("") && !phone.equals("") && !address.equals("")) {
				if (!password.equals(password2)) {
					logger.warn("Password are not equal...");
					Tools.servletInclude(this, request, response, "");
					return;
				}

				try {
					// retrieve the account from hash code
					User user = DataBaseManager.getInstance().getUserByHashCode(hashCode);
					if (user != null) {

						// activate the account
						DataBaseManager.getInstance().activateUser(user.getUsername(), password, user.getName(), institution, phone, address);
						logger.info("Activated user " + user.getUsername());

						// send the activation email
						try {
							String host = P4GUI.getProperty(P4guiProperty.MAIL_SERVER);
							String from = P4GUI.getProperty(P4guiProperty.MAIL_ADDRESS);
							String to = user.getEmail();
							Properties props = System.getProperties();
							props.put("mail.smtp.host", host);
							Session session = Session.getDefaultInstance(props, null);
							MimeMessage message = new MimeMessage(session);
							message.setFrom(new InternetAddress(from));
							message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
							message.addRecipient(Message.RecipientType.BCC, new InternetAddress(from));
							message.setSubject("[PrestoPRIME] Your P4 account has been activated");
							message.setText("Hi " + user.getUsername() + ",\nYour account has been activated.\n\n" + "Enjoy!\n" + "P4 Team");
							Transport.send(message);
						} catch (MessagingException e) {
							e.printStackTrace();
							logger.error("Unable to send the account activation notification email...");
						}

						request.getSession().setAttribute(P4GUI.USER_BEAN_NAME, user);

						response.sendRedirect("?_b=");
						return;
					}
				} catch (SQLException e) {
					e.printStackTrace();
					logger.error("Error during account activation...");
				}

				Tools.servletInclude(this, request, response, "");
			}
		}
	}
}
