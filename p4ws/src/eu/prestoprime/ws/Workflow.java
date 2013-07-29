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

import it.eurix.archtools.workflow.rest.BasicWorkflowAPI;

import javax.ws.rs.Path;

import eu.prestoprime.datamanagement.P4DataManager;
import eu.prestoprime.workflow.P4WorkflowManager;

@Path("/wf")
public class Workflow extends BasicWorkflowAPI {

	public Workflow() {
		super(P4WorkflowManager.getInstance(), P4DataManager.getInstance());
	}

}