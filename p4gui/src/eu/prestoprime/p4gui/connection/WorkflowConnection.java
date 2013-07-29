/**
 * WorkflowConnection.java
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
package eu.prestoprime.p4gui.connection;

import it.eurix.archtools.workflow.jaxb.StatusType;
import it.eurix.archtools.workflow.jaxb.WfDescriptor;
import it.eurix.archtools.workflow.jaxb.WfStatus;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.model.ModelUtils;
import eu.prestoprime.model.ModelUtils.P4JAXBPackage;
import eu.prestoprime.p4gui.model.JobList;
import eu.prestoprime.p4gui.model.P4Service;
import eu.prestoprime.p4gui.services.workflow.WorkflowRequestException;

public abstract class WorkflowConnection {

	private static final Logger logger = LoggerFactory.getLogger(WorkflowConnection.class);

	public static class P4Workflow {

		private String wfID;

		private P4Workflow(String wfID) {
			this.wfID = wfID;
		}

		public static P4Workflow valueOf(String wfID) {
			// TODO check workflow availability
			return new P4Workflow(wfID);
		}

		@Override
		public String toString() {
			return wfID;
		}
	}

	public static WfDescriptor getWfDescriptor(P4Service service) {

		String path = service.getURL() + "/wf/descriptor";

		try {
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				WfDescriptor descriptor = (WfDescriptor) ModelUtils.getUnmarshaller(P4JAXBPackage.CONF).unmarshal(entity.getContent());
				return descriptor;
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new WfDescriptor();
	}

	public static String executeWorkflow(P4Service service, P4Workflow workflow, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws WorkflowRequestException {

		if (dParamsString == null)
			dParamsString = new HashMap<>();
		if (dParamsFile == null)
			dParamsFile = new HashMap<>();

		dParamsString.put("wfID", workflow.toString());

		try {
			MultipartEntity part = new MultipartEntity();

			for (String key : dParamsString.keySet()) {
				String value = dParamsString.get(key);
				part.addPart(key, new StringBody(value));
			}

			for (String key : dParamsFile.keySet()) {
				File value = dParamsFile.get(key);
				part.addPart(key, new FileBody(value));
			}

			String path = service.getURL() + "/wf/execute/" + workflow;

			logger.debug("Calling " + path);

			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpPost(path);
			request.setHeader("content-data", "multipart/form-data");
			((HttpPost) request).setEntity(part);
			HttpResponse response = client.executeRequest(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					String line;
					if ((line = reader.readLine()) != null) {
						logger.debug("Requested new job: " + line);
						return line;
					}
				}
			} else {
				logger.debug(response.getStatusLine().toString());
				throw new WorkflowRequestException("Unable to request execution of workflow " + workflow + "...");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new WorkflowRequestException("Something wrong in WorkflowConnection requesting a workflow: no wfID returned...");
	}

	public static JobList getMyJobs(P4Service service) {
		JobList jobList = new JobList();
		try {
			String path = service.getURL() + "/wf/myjobs";
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpEntity entity = client.executeRequest(request).getEntity();
			if (entity != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] fields = line.split("\\t");
					jobList.addJob(jobList.new Job(fields[0], // jobID
							StatusType.valueOf(fields[1].toUpperCase()), // status
							fields[2], // wfID
							fields[3].equals("") ? null : DatatypeFactory.newInstance().newXMLGregorianCalendar(fields[3]), // startup
							fields[4].equals("") ? 0L : Long.parseLong(fields[4]), // duration
							Integer.parseInt(fields[5]), // totalSteps
							Integer.parseInt(fields[6]), // lastCompletedStep
							fields.length < 8 ? null : fields[7])); // lastCompletedService
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobList;
	}

	public static WfStatus getWorkflowStatus(P4Service service, String id) {
		String path = service.getURL() + "/wf/" + id + "/status";

		try {
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();

			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null)
				sb.append(line.trim());

			// parse wfStatus
			return (WfStatus) ModelUtils.getUnmarshaller(P4JAXBPackage.CONF).unmarshal(new ByteArrayInputStream(sb.toString().getBytes()));
		} catch (IOException | JAXBException e) {
			e.printStackTrace();
			return new WfStatus();
		}
	}

	public static void deleteWorkflowStatus(P4Service service, String id) throws WorkflowRequestException {
		String path = service.getURL() + "/wf/" + id + "/status";

		try {
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpDelete(path);
			HttpResponse response = client.executeRequest(request);

			if (response.getStatusLine().getStatusCode() == 200)
				return;
			else
				throw new WorkflowRequestException("Unable to delete this wfStatus...");
		} catch (IOException e) {
			e.printStackTrace();
			throw new WorkflowRequestException("Unable to execute request...");
		}
	}
}
