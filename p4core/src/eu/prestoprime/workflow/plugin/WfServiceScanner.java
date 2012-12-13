/**
 * WfServiceScanner.java
 * Author: Francesco Rosso (rosso@eurix.it)
 * 
 * This file is part of PrestoPRIME Preservation Platform (P4).
 * 
 * Copyright (C) 2012 EURIX Srl, Torino, Italy
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
package eu.prestoprime.workflow.plugin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.prestoprime.conf.annotation.ClassScanner;
import eu.prestoprime.conf.annotation.ClassScannerRequest;
import eu.prestoprime.conf.annotation.ClassScannerRequestBuilder;
import eu.prestoprime.workflow.exceptions.UndefinedServiceException;
import eu.prestoprime.workflow.plugin.WfPlugin.WfService;

public class WfServiceScanner extends ClassScanner {

	private static WfServiceScanner instance;
	
	private WfServiceScanner(ClassScannerRequest request) {
		super(request);
	}
	
	public static WfServiceScanner getInstance() {
		if (instance == null) {
			ClassScannerRequest request = ClassScannerRequestBuilder.newInstance().classAnnotated(WfPlugin.class).methodAnnotated(WfService.class).build();
			instance = new WfServiceScanner(request);
		}
		return instance;
	}
	
	public Map<WfPlugin, Set<WfService>> getPluginBrief() {
		Map<WfPlugin, Set<WfService>> pluginBrief = new HashMap<>();
		
		for (Class<?> pluginClass : hits.keySet()) {
			Set<WfService> serviceBrief;
			if (pluginBrief.containsKey(pluginClass.getAnnotation(WfPlugin.class))) {
				serviceBrief = pluginBrief.get(pluginClass.getAnnotation(WfPlugin.class));
			} else {
				serviceBrief = new HashSet<>();
			}
			
			for (Method serviceMethod : hits.get(pluginClass)) {
				serviceBrief.add(serviceMethod.getAnnotation(WfService.class));
			}
			pluginBrief.put(pluginClass.getAnnotation(WfPlugin.class), serviceBrief);
		}
		
		return pluginBrief;
	}
	
	public Method getService(String service) throws UndefinedServiceException {
		for (Class<?> pluginClass : hits.keySet()) {
			for (Method serviceMethod : hits.get(pluginClass)) {
				if (serviceMethod.getAnnotation(WfService.class).name().equals(service)) {
					return serviceMethod;
				}
			}
		}
		throw new UndefinedServiceException("Unable to find a wfService named " + service + "...");
	}
}
