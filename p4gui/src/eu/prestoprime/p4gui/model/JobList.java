/**
 * JobList.java
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
package eu.prestoprime.p4gui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import eu.prestoprime.model.workflow.StatusType;

public class JobList {

	private List<Job> jobs;

	public JobList() {
		jobs = new ArrayList<>();
	}

	public List<Job> getJobs() {
		Collections.sort(jobs);
		return jobs;
	}

	public void addJob(Job job) {
		jobs.add(job);
	}

	public class Job implements Comparable<Job> {

		private String jobID;
		private StatusType status;
		private String wfID;
		private XMLGregorianCalendar startup;
		private long duration;
		private int totalSteps;
		private int lastCompletedStep;
		private String lastCompletedService;

		public Job(String jobID, StatusType status, String wfID, XMLGregorianCalendar startup, long duration, int totalSteps, int lastCompletedStep, String lastCompletedService) {
			this.jobID = jobID;
			this.status = status;
			this.wfID = wfID;
			this.startup = startup;
			this.duration = duration;
			this.totalSteps = totalSteps;
			this.lastCompletedStep = lastCompletedStep;
			this.lastCompletedService = lastCompletedService;
		}

		public String getJobID() {
			return jobID;
		}

		public StatusType getStatus() {
			return status;
		}

		public String getWfID() {
			return wfID;
		}

		public XMLGregorianCalendar getStartup() {
			return startup;
		}

		public long getDuration() {
			return duration;
		}

		public int getTotalSteps() {
			return totalSteps;
		}

		public int getLastCompletedStep() {
			return lastCompletedStep;
		}

		public String getLastCompletedService() {
			return lastCompletedService;
		}

		@Override
		public int compareTo(Job job) {
			if (job.getStartup() == null)
				return 1;
			if (this.getStartup() == null)
				return -1;
			return job.getStartup().toGregorianCalendar().compareTo(this.getStartup().toGregorianCalendar());
		}
	}
}
