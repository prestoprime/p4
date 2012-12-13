/**
 * Workflow.java
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.multipart.BodyPartEntity;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import eu.prestoprime.conf.ConfigurationException;
import eu.prestoprime.datamanagement.DataException;
import eu.prestoprime.datamanagement.DataManager;
import eu.prestoprime.datamanagement.PersistenceDBException;
import eu.prestoprime.model.workflow.WfDescriptor;
import eu.prestoprime.model.workflow.WfStatus;
import eu.prestoprime.workflow.WorkflowManager;
import eu.prestoprime.workflow.exceptions.UndefinedWorkflowException;

@Path("/wf")
public class Workflow {

	private static final Logger logger = LoggerFactory.getLogger(Workflow.class);

	@Context
	HttpServletRequest request;

	@GET
	@Path("/descriptor")
	@Produces(MediaType.APPLICATION_XML)
	public Response getWorkflowDescriptor() {
		logger.info("Called /wf/descriptor");

		ResponseBuilder rb;

		// get the node
		WfDescriptor descriptor = WorkflowManager.getInstance().getWorkflowsDescriptor();

		rb = Response.status(Status.OK).entity(descriptor).type(MediaType.APPLICATION_XML_TYPE);

		return rb.build();
	}

	@POST
	@Path("/descriptor")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public Response setWorkflowDescriptor(FormDataMultiPart multiPart) {
		logger.info("Called /wf/descriptor/update");

		ResponseBuilder rb;

		// get the new workflow descriptor
		BodyPartEntity bpe = (BodyPartEntity) multiPart.getField("descriptor").getEntity();
		try {
			File descriptorFile = File.createTempFile("wf-descriptor-", ".tmp");
			descriptorFile.deleteOnExit();

			InputStream in = bpe.getInputStream();
			OutputStream out = new FileOutputStream(descriptorFile);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = in.read(bytes)) != -1)
				out.write(bytes, 0, read);
			in.close();
			out.flush();
			out.close();

			// update the workflow descriptor
			try {
				WorkflowManager.getInstance().setWorkflowsDescriptor(descriptorFile);

				rb = Response.status(Status.OK).entity("OK");
			} catch (ConfigurationException e) {
				e.printStackTrace();

				rb = Response.status(Status.INTERNAL_SERVER_ERROR);
			}
		} catch (IOException e) {
			e.printStackTrace();

			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

	@GET
	@Path("/list")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getAvailableWorkflows() {
		logger.info("Called /wf/list");

		ResponseBuilder rb;

		List<String> workflows = WorkflowManager.getInstance().getWorkflows();

		rb = Response.status(Status.OK);
		StringBuilder sb = new StringBuilder();
		for (String workflow : workflows)
			sb.append(workflow + "\n");
		rb.entity(sb.toString());

		return rb.build();
	}

	@POST
	@Path("/execute/{id}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public Response executeWorkflow(@PathParam("id") String wfID, FormDataMultiPart multiPart) {
		logger.info("Called /wf/execute/" + wfID);

		ResponseBuilder rb;

		WorkflowManager wfManager = WorkflowManager.getInstance();

		// get dynamic parameters
		Map<String, String> dParamsString = new HashMap<>();
		Map<String, File> dParamsFile = new HashMap<>();

		String userID = request.getHeader("userID");
		dParamsString.put("userID", userID);
		for (String dParamName : multiPart.getFields().keySet()) {
			FormDataBodyPart part = multiPart.getField(dParamName);

			if (part.getMediaType().isCompatible(MediaType.TEXT_PLAIN_TYPE)) {
				dParamsString.put(dParamName, part.getValue());
			} else {
				BodyPartEntity bpe = (BodyPartEntity) part.getEntity();
				try {
					File file = File.createTempFile("wf-", ".tmp");
					file.deleteOnExit();

					InputStream in = bpe.getInputStream();
					OutputStream out = new FileOutputStream(file);
					int read = 0;
					byte[] bytes = new byte[1024];
					while ((read = in.read(bytes)) != -1)
						out.write(bytes, 0, read);
					in.close();
					out.flush();
					out.close();

					dParamsFile.put(dParamName, file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// execute workflow
		try {
			logger.debug("Executing workflow " + wfID + " with following parameters:");
			logger.debug(dParamsString.toString());
			logger.debug(dParamsFile.toString());

			String jobID = wfManager.executeWorkflow(wfID, dParamsString, dParamsFile);

			rb = Response.status(Status.OK).entity(jobID);
		} catch (UndefinedWorkflowException e) {
			logger.error("Requested execution of workflow '" + wfID + "', but this workflow semms to be not defined in workflow descriptor...");
			e.printStackTrace();

			rb = Response.status(Status.BAD_REQUEST).entity("Workflow doesn't exist...");
		}

		return rb.build();
	}

	@GET
	@Path("/myjobs")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getMyJobs() {
		ResponseBuilder rb;

		String userID = request.getHeader("userID");
		logger.debug("------" + userID);
		StringBuffer sb = new StringBuffer();
		try {
			List<String> jobs = DataManager.getInstance().getWfStatus(userID, null);
			for (String job : jobs)
				sb.append(job + "\n");
		} catch (DataException e) {
			e.printStackTrace();
		}

		rb = Response.status(Status.OK).entity(sb.toString());

		return rb.build();
	}

	@GET
	@Path("/{id}/status")
	@Produces(MediaType.APPLICATION_XML)
	public Response getJobStatus(@PathParam("id") String jobID) {
		ResponseBuilder rb;

		WfStatus wfStatus = WorkflowManager.getInstance().getWfStatus(jobID);

		rb = Response.status(Status.OK).entity(wfStatus);

		return rb.build();
	}

	@DELETE
	@Path("/{id}/status")
	public Response deleteJobStatus(@PathParam("id") String jobID) {
		ResponseBuilder rb;

		try {
			WorkflowManager.getInstance().deleteWfStatus(jobID);
			rb = Response.status(Status.OK);
		} catch (PersistenceDBException e) {
			rb = Response.status(Status.BAD_REQUEST);
		}

		return rb.build();
	}

	@GET
	@Path("/{id}/result")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getJobResult(@PathParam("id") String jobID) {
		ResponseBuilder rb;

		WfStatus wfStatus = WorkflowManager.getInstance().getWfStatus(jobID);

		rb = Response.status(Status.OK).entity(wfStatus.getResult().getValue());

		return rb.build();
	}
}