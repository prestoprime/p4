/**
 * InitializeIngestServlet.java
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
package eu.prestoprime.p4gui.ingest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.model.ModelUtils;
import eu.prestoprime.model.ModelUtils.P4JAXBPackage;
import eu.prestoprime.model.mets.Mets;
import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.model.oais.SIP;
import eu.prestoprime.p4gui.services.auth.RoleManager;
import eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE;
import eu.prestoprime.p4gui.util.Tools;

@WebServlet("/ingest/initialize")
@MultipartConfig
public class InitializeIngestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(InitializeIngestServlet.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);
		RoleManager.checkRequestedRole(USER_ROLE.producer, user.getCurrentP4Service().getRole(), response);

		String action = request.getParameter("action");
		logger.debug("action? " + action);

		switch (action) {
		case "new":
		default:
			request.getSession().setAttribute(P4GUI.SIP_BEAN_NAME, new SIP());
			logger.debug("Created new SIP...");
			break;

		case "edit":
		case "ingest":
			Part sipFile = request.getPart("sipFile");
			if (sipFile != null) {
				try {
					// validate sip
					logger.debug("Unmarshalling...");
					Unmarshaller unmarshaller = ModelUtils.getUnmarshaller(P4JAXBPackage.DATA_MODEL);
					Mets mets = (Mets) unmarshaller.unmarshal(sipFile.getInputStream());

					// set currentSIP bean
					request.getSession().setAttribute(P4GUI.SIP_BEAN_NAME, new SIP(mets));
					logger.debug("Loaded existing SIP...");
				} catch (JAXBException e) {
					e.printStackTrace();
				}
			} else {
				Tools.servletInclude(this, request, response, "?_b=body/ingest/initilize.jsp");
				return;
			}
		}

		Tools.servletInclude(this, request, response, "?_b=body/ingest/edit.jsp");
	}
}
