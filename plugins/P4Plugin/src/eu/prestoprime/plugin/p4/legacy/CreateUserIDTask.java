/**
 * CreateUserIDTask.java
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
package eu.prestoprime.plugin.p4.legacy;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.conf.ConfigurationManager;
import eu.prestoprime.workflow.exceptions.TaskExecutionFailedException;
import eu.prestoprime.workflow.plugin.WfPlugin;
import eu.prestoprime.workflow.plugin.WfPlugin.WfService;
import eu.prestoprime.workflow.tasks.P4Task;

@WfPlugin(name = "P4Plugin")
public class CreateUserIDTask implements P4Task {

	private static Logger logger = LoggerFactory.getLogger(CreateUserIDTask.class);

	@Override
	@WfService(name = "create_user_id", version = "2.0.0")
	public void execute(Map<String, String> sParams, Map<String, String> dParamsString, Map<String, File> dParamFile) throws TaskExecutionFailedException {

		String role = dParamsString.get("user.role");
		String userID = ConfigurationManager.getUserInstance().addUser(role);

		logger.debug("userID: " + userID);

		dParamsString.put("userID", userID);
		dParamsString.put("result", userID);
	}
}
