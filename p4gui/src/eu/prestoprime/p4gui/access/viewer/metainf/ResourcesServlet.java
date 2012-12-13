/**
 * ResourcesServlet.java
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
package eu.prestoprime.p4gui.access.viewer.metainf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.prestoprime.model.mets.AmdSecType;
import eu.prestoprime.model.mets.MdSecType;
import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.connection.AccessConnection;
import eu.prestoprime.p4gui.model.User;
import eu.prestoprime.p4gui.model.oais.DIP;
import eu.prestoprime.p4gui.services.auth.RoleManager;
import eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE;
import eu.prestoprime.p4gui.util.Tools;

@WebServlet("/access/viewer/metainf/6")
public class ResourcesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		User user = Tools.getSessionAttribute(request.getSession(), P4GUI.USER_BEAN_NAME, User.class);
		RoleManager.checkRequestedRole(USER_ROLE.consumer, user.getCurrentP4Service().getRole(), response);

		String id = request.getParameter("id");

		DIP dip = AccessConnection.getDIP(user.getCurrentP4Service(), id);

		Map<String, List<String>> resources = new HashMap<>();

		for (AmdSecType amdSec : dip.getMets().getAmdSec()) {

			// digiprovMD
			for (MdSecType mdSec : amdSec.getDigiprovMD()) {
				String idSec = mdSec.getID();
				if (mdSec.getMdRef() != null)
					this.addResource(resources, idSec, mdSec.getMdRef().getHref());
			}

			// rightsMD
			for (MdSecType mdSec : amdSec.getRightsMD()) {
				String idSec = mdSec.getID();
				if (mdSec.getMdRef() != null)
					this.addResource(resources, idSec, mdSec.getMdRef().getHref());
			}

			// sourceMD
			for (MdSecType mdSec : amdSec.getSourceMD()) {
				String idSec = mdSec.getID();
				if (mdSec.getMdRef() != null)
					this.addResource(resources, idSec, mdSec.getMdRef().getHref());
			}

			// techMD
			for (MdSecType mdSec : amdSec.getTechMD()) {
				String idSec = mdSec.getID();
				if (mdSec.getMdRef() != null)
					this.addResource(resources, idSec, mdSec.getMdRef().getHref());
			}
		}

		for (MdSecType mdSec : dip.getMets().getDmdSec()) {
			String idSec = mdSec.getID();
			if (mdSec.getMdRef() != null)
				this.addResource(resources, idSec, mdSec.getMdRef().getHref());
		}

		request.setAttribute("resources", resources);

		Tools.servletInclude(this, request, response, "/body/access/viewer/metainf/resources/viewResources.jsp");
	}

	private void addResource(Map<String, List<String>> resourcesMap, String id, String href) {
		List<String> resourcesList = resourcesMap.get(id);
		if (resourcesList == null) {
			resourcesList = new ArrayList<>();
			resourcesMap.put(id, resourcesList);
		}
		resourcesList.add(href);
	}
}
