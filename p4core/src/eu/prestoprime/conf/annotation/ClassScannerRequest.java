/**
 * ClassScannerRequest.java
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
package eu.prestoprime.conf.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassScannerRequest {

	private static final Logger logger = LoggerFactory.getLogger(ClassScannerRequest.class);
	
	private Class<? extends Annotation> classAnnotation;
	private Class<? extends Annotation> methodAnnotation;
	
	ClassScannerRequest(Class<? extends Annotation> classAnnotation, Class<? extends Annotation> methodAnnotation) {
		this.classAnnotation = classAnnotation;
		this.methodAnnotation = methodAnnotation;
	}

	public Set<Method> check(Class<?> clazz) {
		Set<Method> hitMethods = new HashSet<>();
		
		if (classAnnotation == null || clazz.isAnnotationPresent(classAnnotation)) {
			logger.debug("Found class match in class " + clazz.getName());

			for (Method method : clazz.getMethods()) {
				if (methodAnnotation == null || method.isAnnotationPresent(methodAnnotation)) {
					logger.debug("Found method match in class " + clazz.getName() + " - method " + method.getName());

					if (true) {
						hitMethods.add(method);
					}
				}
			}
		}
		return hitMethods;
	}
}
