/**
 * AdminConnection.java
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;

import eu.prestoprime.p4gui.admin.fixity.FixityCheckResponse;
import eu.prestoprime.p4gui.model.JobList;
import eu.prestoprime.p4gui.model.P4Service;
import eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE;
import eu.prestoprime.p4gui.util.parse.AdminActions;

public abstract class AdminConnection {

	public static String createUserID(P4Service service, USER_ROLE role) {

		String path = service.getURL() + "/conf/user/" + role;

		try {
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpPut(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String line;
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				if ((line = reader.readLine()) != null) {
					return line;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static JobList monitorJobs(P4Service service, String filter) {
		if (filter == null || filter.equals(""))
			filter = "all";

		JobList jobList = new JobList();
		try {
			String path = service.getURL() + "/admin/jobs/" + filter;
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

	public static List<String> checkDataType(P4Service service, String dataType, boolean available) {
		List<String> aipList = new ArrayList<>();

		String path = service.getURL() + "/access/dip/list/" + dataType + "?available=" + available;

		try {
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String line;
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				while ((line = reader.readLine()) != null) {
					aipList.add(line.trim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return aipList;
	}

	public static List<String> checkFormatRisk(P4Service service) {
		List<String> aipList = new ArrayList<>();

		String path = service.getURL() + "/admin/aip/risk/format";

		try {
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String line;
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				while ((line = reader.readLine()) != null) {
					aipList.add(line.trim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return aipList;
	}

	@Deprecated
	public static void getAdminActions(P4Service service, AdminActions actions) {
		try {
			String path = service.getURL() + "/access/dip/list/actions";
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpRequestBase request = new HttpGet(path);
			HttpResponse response = client.executeRequest(request);
			HttpEntity entity = response.getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line;
			while ((line = reader.readLine()) != null)
				actions.addAction(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> getFixityChecklist(P4Service service) {
		List<String> records = new ArrayList<String>();
		try {
			String path = service.getURL() + "/admin/aip/risk/fixity";
			HttpRequestBase request = new HttpGet(path);
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpEntity entity = client.executeRequest(request).getEntity();
			if (entity != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				String line;
				while ((line = reader.readLine()) != null) {
					records.add(line);
				}
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return records;
	}

	public static FixityCheckResponse getFixityCheck(P4Service service, String id) {
		try {
			P4HttpClient client = new P4HttpClient(service.getUserID());
			String path = service.getURL() + "/admin/fixitycheck/" + id;
			HttpRequestBase request = new HttpGet(path);
			HttpEntity entity = client.executeRequest(request).getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line;
			if ((line = reader.readLine()) != null) {
				return new FixityCheckResponse(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean restoreFromLTO(P4Service service, String from, String to) {
		try {
			String path = service.getURL() + "/admin/restore";
			HttpRequestBase request = new HttpPost(path);
			MultipartEntity part = new MultipartEntity();
			part.addPart("from", new StringBody(from));
			part.addPart("to", new StringBody(to));
			((HttpPost) request).setEntity(part);
			P4HttpClient client = new P4HttpClient(service.getUserID());
			HttpEntity entity = client.executeRequest(request).getEntity();
			if (entity != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				String line;
				if ((line = reader.readLine()) != null) {
					if (line.equals("Error")) {
						return false;
					} else {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
