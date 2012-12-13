/**
 * UploadMasterFileServlet.java
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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.services.auth.RoleManager;
import eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE;
import eu.prestoprime.p4gui.util.Tools;

@WebServlet("/ingest/masterfile")
@MultipartConfig()
public class UploadMasterFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(UploadMasterFileServlet.class);

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);
		RoleManager.checkRequestedRole(USER_ROLE.producer, user.getCurrentP4Service().getRole(), response);

		// prepare dynamic variables
		Part masterQualityPart = request.getPart("masterFile");
		String targetName = new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()) + ".mxf";

		// prepare static variables
		String host = "p4.eurixgroup.com";
		int port = 21;
		String username = "pprime";
		String password = "pprime09";

		FTPClient client = new FTPClient();
		try {
			client.connect(host, port);
			if (FTPReply.isPositiveCompletion(client.getReplyCode())) {
				if (client.login(username, password)) {
					client.setFileType(FTP.BINARY_FILE_TYPE);
					// TODO add behavior if file name is already present in
					// remote ftp folder
					// now OVERWRITES
					if (client.storeFile(targetName, masterQualityPart.getInputStream())) {
						logger.info("Stored file on remote FTP server " + host + ":" + port);

						request.setAttribute("masterfileName", targetName);
					} else {
						logger.error("Unable to store file on remote FTP server");
					}
				} else {
					logger.error("Unable to login on remote FTP server");
				}
			} else {
				logger.error("Unable to connect to remote FTP server");
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.fatal("General exception with FTPClient");
		}

		Tools.servletInclude(this, request, response, "/body/ingest/masterfile/listfiles.jsp");
	}
}
