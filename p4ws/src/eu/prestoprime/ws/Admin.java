/**
 * Admin.java
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
package eu.prestoprime.ws;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse.Status;

import eu.prestoprime.datamanagement.DataException;
import eu.prestoprime.datamanagement.DataManager;
import eu.prestoprime.model.workflow.StatusType;

@Path("/admin")
public class Admin {

	private static final Logger logger = LoggerFactory.getLogger(Admin.class);

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/aip/risk/fixity")
	public Response queryFixityRisk() {
		logger.debug("Called /admin/aip/risk/fixity");

		ResponseBuilder rb;

		try {
			List<String> aipList = DataManager.getInstance().getAllAIP(null);// FIXME

			StringBuffer sb = new StringBuffer();
			for (String aip : aipList)
				sb.append(aip + "\n");

			rb = Response.status(Status.OK).entity(sb.toString());
		} catch (DataException e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/aip/risk/format")
	public Response queryFormatRisk() {
		logger.debug("Called /admin/aip/risk/format");

		ResponseBuilder rb;

		try {
			List<String> aipList = DataManager.getInstance().getAIPByFormatRisk();

			StringBuffer sb = new StringBuffer();
			for (String aip : aipList)
				sb.append(aip + "\n");

			rb = Response.status(Status.OK).entity(sb.toString());
		} catch (DataException e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/jobs/{status}")
	public Response queryJobs(@PathParam("status") String status) {
		logger.debug("Called /admin/jobs/" + status);

		ResponseBuilder rb;

		try {
			StatusType qstatus;
			if (status.equals("all"))
				qstatus = null;
			else
				qstatus = StatusType.valueOf(status.toUpperCase());

			List<String> res = DataManager.getInstance().getWfStatus(null, qstatus);

			StringBuilder sb = new StringBuilder();
			for (String s : res)
				sb.append(s + "\n");

			rb = Response.status(Status.OK).entity(sb.toString());
		} catch (DataException e) {
			e.printStackTrace();
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		} catch (RuntimeException e) {
			rb = Response.status(Status.BAD_REQUEST);
		}

		return rb.build();
	}
}
