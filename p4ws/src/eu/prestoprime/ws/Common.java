/**
 * Common.java
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
package eu.prestoprime.ws;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.core.HttpContext;

import eu.prestoprime.conf.ConfigurationManager;
import eu.prestoprime.conf.ConfigurationManager.P4Property;
import eu.prestoprime.conf.ConfigurationManager.P4Role;
import eu.prestoprime.datamanagement.DataException;
import eu.prestoprime.datamanagement.DataManager;
import eu.prestoprime.workflow.plugin.WfPlugin;
import eu.prestoprime.workflow.plugin.WfPlugin.WfService;
import eu.prestoprime.workflow.plugin.WfServiceScanner;

@Path("/")
public class Common {

	private static final Logger logger = LoggerFactory.getLogger(Common.class);

	@Context
	HttpContext context;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/checkrole")
	public Response getRole() {
		logger.debug("Called /common/checkrole");

		ResponseBuilder rb;

		String userID = context.getRequest().getHeaderValue("userID");
		if (userID != null) {
			P4Role role = ConfigurationManager.getUserInstance().getUserRole(userID);

			rb = Response.status(Status.OK).entity(role.toString());
		} else {
			rb = Response.status(Status.BAD_REQUEST);
		}

		return rb.build();
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/terms/{role}")
	public Response getTerms(@PathParam("role") String role) {
		logger.debug("Called /common/terms/" + role);

		ResponseBuilder rb;

		try {
			P4Role p4role = P4Role.valueOf(role);
			String terms = DataManager.getInstance().getTermsOfUse(p4role);

			rb = Response.status(Status.OK).entity(terms);
		} catch (DataException e) {
			rb = Response.status(Status.BAD_REQUEST);
		}

		return rb.build();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/contact")
	public Response getContact() {
		logger.debug("Called /common/contact");

		ResponseBuilder rb;

		rb = Response.status(Status.OK).entity(ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_WS_ADMIN));

		return rb.build();
	}

	private void showFiles(File[] directories, List<File> files) {
		for (File file : directories) {
			if (file.isDirectory()) {
				showFiles(file.listFiles(), files);
			} else {
				files.add(file);
			}
		}
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/filelist")
	public Response GetFileList() {
		logger.info("Called /common/filelist");

		ResponseBuilder rb;

		String p4Share = ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_SHARE);
		logger.debug("Loaded p4.shared.folder property: " + p4Share);

		List<File> fileList = new ArrayList<>();
		File[] directories = new File[1];
		directories[0] = new File(p4Share);
		this.showFiles(directories, fileList);

		StringBuilder sb = new StringBuilder();
		for (File file : fileList)
			if (file.isFile())
				sb.append(file.getAbsolutePath() + "\n");

		rb = Response.status(Status.OK).entity(sb.toString());

		return rb.build();
	}

	@GET
	@Path("/pluginlist")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPluginList() {
		logger.info("Called /pluginlist");

		ResponseBuilder rb;

		Map<WfPlugin, Set<WfService>> plugins = WfServiceScanner.getInstance().getPluginBrief();

		JSONObject json = new JSONObject();
		for (WfPlugin plugin : plugins.keySet()) {
			for (WfService task : plugins.get(plugin)) {
				try {
					JSONObject taskObject = new JSONObject();
					taskObject.put("service", task.name());
					taskObject.put("version", task.version());
					json.append(plugin.name(), taskObject);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		rb = Response.status(Status.OK).entity(json.toString());

		return rb.build();
	}
}
