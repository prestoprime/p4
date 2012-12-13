/**
 * WorkflowManager.java
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
package eu.prestoprime.workflow;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import eu.prestoprime.conf.ConfigurationException;
import eu.prestoprime.datamanagement.PersistenceDBException;
import eu.prestoprime.datamanagement.PersistenceManager;
import eu.prestoprime.datamanagement.PersistenceManager.P4Collection;
import eu.prestoprime.model.ModelUtils.P4Namespace;
import eu.prestoprime.model.workflow.StatusType;
import eu.prestoprime.model.workflow.WfDescriptor;
import eu.prestoprime.model.workflow.WfDescriptor.Services.Service;
import eu.prestoprime.model.workflow.WfDescriptor.Workflows.Workflow;
import eu.prestoprime.model.workflow.WfDescriptor.Workflows.Workflow.SParam;
import eu.prestoprime.model.workflow.WfDescriptor.Workflows.Workflow.Task;
import eu.prestoprime.model.workflow.WfStatus;
import eu.prestoprime.model.workflow.WfStatus.Params;
import eu.prestoprime.model.workflow.WfStatus.Params.DParamFile;
import eu.prestoprime.model.workflow.WfStatus.Params.DParamString;
import eu.prestoprime.model.workflow.WfStatus.Result;
import eu.prestoprime.model.workflow.WfStatus.TimeTable;
import eu.prestoprime.model.workflow.WfStatus.TimeTable.TaskReport;
import eu.prestoprime.workflow.exceptions.TaskExecutionFailedException;
import eu.prestoprime.workflow.exceptions.UndefinedServiceException;
import eu.prestoprime.workflow.exceptions.UndefinedWorkflowException;
import eu.prestoprime.workflow.exceptions.WorkflowExecutionFailedException;
import eu.prestoprime.workflow.plugin.WfServiceInterface;
import eu.prestoprime.workflow.plugin.WfServiceScanner;

public final class WorkflowManager {

	private static final Logger logger = LoggerFactory.getLogger(WorkflowManager.class);
	private static final String DESCRIPTOR_NAME = "workflows.xml";

	private static WorkflowManager instance;
	private JAXBContext context;

	private WfDescriptor descriptor;

	private WorkflowManager() throws JAXBException {
		try {
			context = JAXBContext.newInstance(P4Namespace.CONF.getValue());
			this.loadDescriptor();
		} catch (JAXBException e) {
			logger.error("Unable to instanziate the workflow JAXBContext...");
			throw e;
		}
	}

	private void loadDescriptor() {
		try {
			Node descriptorNode = PersistenceManager.getInstance().readXMLResource(P4Collection.ADMIN_COLLECTION, WorkflowManager.DESCRIPTOR_NAME);
			descriptor = (WfDescriptor) context.createUnmarshaller().unmarshal(descriptorNode);
		} catch (PersistenceDBException e) {
			logger.error("Unable to load from DB the workflow descriptor...");
			descriptor = new WfDescriptor();
		} catch (JAXBException e) {
			logger.error("Unable to parse the workflow descriptor...");
			descriptor = new WfDescriptor();
		}
	}

	public static WorkflowManager getInstance() {
		if (instance == null) {
			try {
				instance = new WorkflowManager();
			} catch (JAXBException e) {
				instance = null;
			}
		}
		return instance;
	}

	public WfDescriptor getWorkflowsDescriptor() {
		this.loadDescriptor();

		return descriptor;
	}

	public void setWorkflowsDescriptor(File descriptor) throws ConfigurationException {
		// get an empty node
		Node descriptorNode;
		try {
			descriptorNode = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			logger.error("Unable to create an empty Node...");
			throw new ConfigurationException("Unable to create an empty Node...");
		}

		// validate the new workflows descriptor
		try {
			WfDescriptor props = (WfDescriptor) context.createUnmarshaller().unmarshal(descriptor);
			context.createMarshaller().marshal(props, descriptorNode);
		} catch (JAXBException e) {
			logger.error("Unable to validate the new workflows descriptor");
			throw new ConfigurationException("Unable to validate the new workflows descriptor");
		}

		// store the new validated workflows descriptor
		try {
			PersistenceManager.getInstance().storeXMLResource(P4Collection.ADMIN_COLLECTION, WorkflowManager.DESCRIPTOR_NAME, descriptorNode);
		} catch (PersistenceDBException e) {
			logger.error("Unable to store the new validated workflows descriptor");
			throw new ConfigurationException("Unable to store the new validated workflows descriptor");
		}

		// load the new validated and stored workflows descriptor
		this.loadDescriptor();
	}

	public List<String> getWorkflows() {
		this.loadDescriptor();

		List<String> workflows = new ArrayList<>();
		for (Workflow workflow : descriptor.getWorkflows().getWorkflow())
			workflows.add(workflow.getId());
		return workflows;
	}

	public String executeWorkflow(String wfID, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws UndefinedWorkflowException {
		logger.debug("Searching workflow " + wfID);

		this.loadDescriptor();
		Workflow workflow = this.getWorkflow(wfID);

		logger.debug("Found workflow " + wfID);
		logger.debug("Loading static parameters for workflow " + wfID);

		Map<String, String> sParams = new HashMap<>();
		for (SParam param : workflow.getSParam())
			sParams.put(param.getKey(), param.getValue());

		logger.debug("Loaded static parameters for workflow " + wfID);
		logger.debug("Loading tasks for workflow " + wfID);

		List<List<Task>> tasks = this.getAllTasks(workflow);

		logger.debug("Loaded tasks for workflow " + wfID);

		String jobID = "job-" + UUID.randomUUID();

		Params params = new Params();
		for (Entry<String, String> entry : sParams.entrySet()) {
			eu.prestoprime.model.workflow.WfStatus.Params.SParam sParam = new eu.prestoprime.model.workflow.WfStatus.Params.SParam();
			sParam.setKey(entry.getKey());
			sParam.setValue(entry.getValue());
			params.getSParam().add(sParam);
		}
		for (Entry<String, String> entry : dParamsString.entrySet()) {
			DParamString dParamString = new DParamString();
			dParamString.setKey(entry.getKey());
			dParamString.setValue(entry.getValue());
			params.getDParamString().add(dParamString);
		}
		for (Entry<String, File> entry : dParamsFile.entrySet()) {
			DParamFile dParamFile = new DParamFile();
			dParamFile.setKey(entry.getKey());
			dParamFile.setValue(entry.getValue().getAbsolutePath());
			params.getDParamFile().add(dParamFile);
		}

		WfStatus wfStatus = new WfStatus();
		wfStatus.setId(jobID);
		wfStatus.setWorkflow(wfID);
		wfStatus.setTotalSteps(tasks.size());
		wfStatus.setLastCompletedStep(0);
		wfStatus.setStatus(StatusType.WAITING);
		wfStatus.setParams(params);
		wfStatus.setTimeTable(new TimeTable());

		this.setWfStatus(wfStatus);

		logger.debug("Starting new workflow executor");

		WorkflowExecutor wfExecutor = new WorkflowExecutor(tasks, wfStatus, sParams, dParamsString, dParamsFile);
		wfExecutor.setName(jobID);
		wfExecutor.start();

		logger.debug("Started workflow executor " + wfID);

		return jobID;
	}

	public WfStatus getWfStatus(String jobID) {
		try {
			Node node = PersistenceManager.getInstance().readXMLResource(P4Collection.WF_COLLECTION, jobID);
			return (WfStatus) context.createUnmarshaller().unmarshal(node);
		} catch (PersistenceDBException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deleteWfStatus(String jobID) throws PersistenceDBException {
		PersistenceManager.getInstance().deleteXMLResource(P4Collection.WF_COLLECTION, jobID);
	}

	private void setWfStatus(WfStatus wfStatus) {
		String jobID = wfStatus.getId();
		if (jobID == null)
			return;
		try {
			logger.debug("Creating wfStatus for workflow" + jobID);

			Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			context.createMarshaller().marshal(wfStatus, node);
			PersistenceManager.getInstance().storeXMLResource(P4Collection.WF_COLLECTION, jobID, node);

			logger.debug("Created wfStatus for workflow" + jobID);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (PersistenceDBException e) {
			e.printStackTrace();
		}
	}

	private Workflow getWorkflow(String id) throws UndefinedWorkflowException {
		for (Workflow workflow : descriptor.getWorkflows().getWorkflow())
			if (workflow.getId().equals(id))
				return workflow;
		throw new UndefinedWorkflowException("Unable to find a workflow with id " + id);
	}

	private List<List<Task>> getAllTasks(Workflow workflow) {
		List<List<Task>> tasks = new ArrayList<List<Task>>();
		List<Task> tmpTasks;
		int order = 1;
		while (!(tmpTasks = this.getTasksByOrder(workflow, order++)).isEmpty()) {
			tasks.add(tmpTasks);
		}
		return tasks;
	}

	private List<Task> getTasksByOrder(Workflow workflow, int order) {
		List<Task> tasks = new ArrayList<Task>();
		for (Task task : workflow.getTask())
			if (task.getStep() == order)
				tasks.add(task);
		return tasks;
	}

	/**
	 * @param The service ID to scan for
	 * @return The service object, representing the service class (ant method) to be executed
	 * @throws UndefinedServiceException Thrown if it's not possible to find a service that binds the request parameters
	 */
	@Deprecated
	private Service getService(String id) throws UndefinedServiceException {
		// search in wfDescriptor <services> *legacy* section
		for (Service service : descriptor.getServices().getService())
			if (service.getId().equals(id))
				return service;
		throw new UndefinedServiceException("Unable to find a service with id " + id);
	}

	class WorkflowExecutor extends Thread {

		private List<List<Task>> tasks;
		private WfStatus wfStatus;
		private Map<String, String> sParams;
		private Map<String, String> dParamString;
		private Map<String, File> dParamFile;

		public WorkflowExecutor(List<List<Task>> tasks, WfStatus wfStatus, Map<String, String> sParams, Map<String, String> dParamString, Map<String, File> dParamFile) {
			this.tasks = tasks;
			this.wfStatus = wfStatus;
			this.sParams = sParams;
			this.dParamString = dParamString;
			this.dParamFile = dParamFile;

			WorkflowManager.getInstance().setWfStatus(wfStatus);
		}

		@Override
		public void run() {
			logger.debug("Executing tasks for workflow " + this.getName());

			int step = 0;

			wfStatus.setStatus(StatusType.RUNNING);
			wfStatus.setDuration(0L);
			try {
				wfStatus.setStartup(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
			} catch (DatatypeConfigurationException e) {
				wfStatus.setStatus(StatusType.FAILED);
				Result result = new Result();
				result.setValue(e.getMessage());
				wfStatus.setResult(result);
				return;
			} finally {
				WorkflowManager.getInstance().setWfStatus(wfStatus);
			}

			while (!tasks.isEmpty()) {
				step++;

				List<Task> tmpTasks = tasks.remove(0);
				try {
					this.executeTasks(tmpTasks);

					wfStatus.setLastCompletedStep(step);
				} catch (WorkflowExecutionFailedException e) {
					e.printStackTrace();

					wfStatus.setStatus(StatusType.FAILED);
					wfStatus.setDuration(System.currentTimeMillis() - wfStatus.getStartup().toGregorianCalendar().getTimeInMillis());

					Result result = new Result();
					result.setValue(e.getMessage());
					wfStatus.setResult(result);

					logger.debug("Executed job " + this.getName() + " - FAILED");

					return;
				} finally {
					WorkflowManager.getInstance().setWfStatus(wfStatus);
				}
			}

			wfStatus.setStatus(StatusType.COMPLETED);
			wfStatus.setDuration(System.currentTimeMillis() - wfStatus.getStartup().toGregorianCalendar().getTimeInMillis());

			String resultValue = dParamString.get("result");
			if (resultValue != null) {
				Result result = new Result();
				result.setValue(resultValue);
				wfStatus.setResult(result);
			}
			WorkflowManager.getInstance().setWfStatus(wfStatus);

			logger.debug("Executed job " + this.getName() + " - SUCCESSFUL");
		}

		private void executeTasks(List<Task> tasks) throws WorkflowExecutionFailedException {
			List<TaskExecutor> executors = new ArrayList<>();
			for (Task task : tasks) {
				TaskExecutor executor = new TaskExecutor(task);
				executor.start();
				executors.add(executor);
			}
			for (TaskExecutor executor : executors) {
				try {
					executor.join();

					if (executor.isFailed() && executor.getTask().isCritical()) {
						throw new WorkflowExecutionFailedException("Critical task failed\n" + executor.getMessage());
					} else {
						TaskReport taskReport = new TaskReport();
						taskReport.setService(executor.task.getService());
						taskReport.setStep(executor.task.getStep());
						try {
							taskReport.setStartup(DatatypeFactory.newInstance().newXMLGregorianCalendar(executor.startup));
						} catch (DatatypeConfigurationException e) {
							throw new WorkflowExecutionFailedException("Task commit failed\n" + executor.getMessage());
						}
						taskReport.setDuration(executor.duration);

						wfStatus.getTimeTable().getTaskReport().add(taskReport);
						wfStatus.setDuration(System.currentTimeMillis() - wfStatus.getStartup().toGregorianCalendar().getTimeInMillis());
						WorkflowManager.getInstance().setWfStatus(wfStatus);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		class TaskExecutor extends Thread {

			private Task task;
			private GregorianCalendar startup;
			private long duration;
			private int attempts;
			private WfServiceInterface service;
			private boolean failed;
			private String message;

			public TaskExecutor(Task task) {
				this.task = task;
				this.startup = new GregorianCalendar();
				this.duration = 0;
				this.attempts = task.getAttempts();
				try {
					// new annotated services loader
					final Method method = WfServiceScanner.getInstance().getService(task.getService());
					final Object pluginClass = method.getDeclaringClass().newInstance();
					this.service = new WfServiceInterface() {
						
						@Override
						public void execute(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamsFile) throws TaskExecutionFailedException {
							try {
								method.invoke(pluginClass, sParams, dParamsString, dParamsFile);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								throw new TaskExecutionFailedException("Unable to invoke method on class loaded with the new annotated services loader...");
							}
						}
					};
					this.failed = false;
				} catch (UndefinedServiceException | IllegalAccessException | InstantiationException e1) {
					logger.warn(e1.getMessage());
					
					// not found/not instantiable with annotated services loader
					// try with legacy wfDescriptor services loader
					try {
						Class<?> taskClass = Class.forName(WorkflowManager.this.getService(task.getService()).getClazz());
						this.service = (WfServiceInterface) taskClass.newInstance();
						this.failed = false;
					} catch (UndefinedServiceException | ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e2) {
						e2.printStackTrace();
						this.failed = true;
						this.message = "(Constructor)" + e2.getMessage() + "\n";
					}
				}
			}

			public Task getTask() {
				return task;
			}

			public boolean isFailed() {
				return failed;
			}

			public String getMessage() {
				return message;
			}

			@Override
			public void run() {
				if (!failed) {
					if (attempts-- != 0) {
						try {
							service.execute(WorkflowExecutor.this.sParams, WorkflowExecutor.this.dParamString, WorkflowExecutor.this.dParamFile);
						} catch (TaskExecutionFailedException | RuntimeException e) {
							message += "(" + attempts + ") " + e.getMessage() + "\n";
							e.printStackTrace();
							this.run();
						}
					} else {
						failed = true;
					}
				}
				this.duration = (int) (System.currentTimeMillis() - startup.getTimeInMillis());
			}
		}
	}
}
