/**
 * ListPluginsServlet.java
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
package eu.prestoprime.p4gui.services.p4service;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.connection.CommonConnection;
import eu.prestoprime.p4gui.connection.ConnectionException;
import eu.prestoprime.p4gui.model.P4Service;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.util.Tools;
import eu.prestoprime.workflow.plugin.WfPlugin;
import eu.prestoprime.workflow.plugin.WfPlugin.WfService;

@WebServlet("/p4service/plugins")
public class ListPluginsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);

		if (user.isLogged()) {
			// get parameters
			String p4service = request.getParameter("p4service");

			// check URL
			if (!p4service.startsWith("http://") && !p4service.startsWith("https://"))
				p4service = "http://" + p4service;

			Map<WfPlugin, Set<WfService>> plugins;
			try {
				plugins = CommonConnection.getAvailablePlugins(new P4Service(new URL(p4service), null));
				request.setAttribute("plugins", plugins);
				Tools.servletInclude(this, request, response, "?_b=body/home/plugins.jsp");
			} catch (ConnectionException e) {
				Tools.servletInclude(this, request, response, "?_b=");
			}
		}
	}
}
