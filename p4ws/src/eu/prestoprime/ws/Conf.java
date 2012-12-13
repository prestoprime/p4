/**
 * Conf.java
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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.conf.ConfigurationManager;

@Path("/conf")
public class Conf {

	private static final Logger logger = LoggerFactory.getLogger(Conf.class);

	@PUT
	@Path("/user/{role}")
	public Response addUser(@PathParam("role") String role) {
		logger.debug("Called addUser...");

		ResponseBuilder rb;

		String userID = ConfigurationManager.getUserInstance().addUser(role);

		rb = Response.status(Status.OK).entity(userID);

		return rb.build();
	}

	@DELETE
	@Path("/user")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response deleteUser(@FormParam("userID") String userID) {
		logger.debug("Called deleteUser...");

		ResponseBuilder rb;

		ConfigurationManager.getUserInstance().deleteUser(userID);

		rb = Response.status(Status.OK);

		return rb.build();
	}

	@PUT
	@Path("/user/service/{serviceKey}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addService(@FormParam("userID") String userID, @PathParam("serviceKey") String serviceKey, @FormParam("serviceID") String serviceValue) {
		logger.debug("Called addService...");

		ResponseBuilder rb;

		ConfigurationManager.getUserInstance().addUserService(userID, serviceKey, serviceValue);

		rb = Response.status(Status.OK);

		return rb.build();
	}

	@DELETE
	@Path("/user/service/{serviceKey}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response deleteService(@FormParam("userID") String userID, @PathParam("serviceKey") String serviceKey) {
		logger.debug("Called deleteService...");

		ResponseBuilder rb;

		ConfigurationManager.getUserInstance().deleteUserService(userID, serviceKey);

		rb = Response.status(Status.OK);

		return rb.build();
	}
}
