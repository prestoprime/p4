/**
 * PluginScanner.java
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
package eu.prestoprime.conf.annotation;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassScanner {

	private static final Logger logger = LoggerFactory.getLogger(ClassScanner.class);
	private static final ClassLoader[] classLoaders = { Thread.currentThread().getContextClassLoader(), ClassScanner.class.getClassLoader() };
	
	private final ClassScannerRequest request;
	protected Map<Class<?>, Set<Method>> hits;

	public static ClassScanner newInstance(ClassScannerRequest request) {
		return new ClassScanner(request);
	}

	protected ClassScanner(ClassScannerRequest request) {
		this.request = request;
		this.hits = new HashMap<>();
		
		logger.debug("Starting classpath scanner...");

		String classpath = null;
		for (URL url : ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs()) {
			classpath += File.pathSeparator + url.getFile();
		}
		if (classpath != null) {
			logger.debug("Scanning classpath: " + classpath);
			String[] classpaths = classpath.split(File.pathSeparator);
			for (String classpathRoot : classpaths) {
				this.scanPath(new File(classpathRoot), "");
			}
		}
	}

	private void scanPath(File classpathRoot, String relativePath) {
		File path = new File(classpathRoot, relativePath);

		try {
			if (path.isFile()) {
				if (path.getAbsolutePath().endsWith(".jar")) {
					JarFile jarFile = new JarFile(path);
					this.scanJar(jarFile);
				} else if (path.getAbsolutePath().endsWith(".class")) {
					this.checkClass(relativePath);
				}
			} else {
				String[] children = path.list();
				if (children != null) {
					for (String child : children) {
						this.scanPath(classpathRoot, relativePath + File.separator + child);
					}
				}
			}
		} catch (Throwable e) {
			logger.error("Error scanning into " + path.getAbsolutePath() + "...");
			e.printStackTrace();
		}
	}

	private void scanJar(JarFile jarFile) {
		Enumeration<JarEntry> allEntries = jarFile.entries();
		while (allEntries.hasMoreElements()) {
			JarEntry entry = (JarEntry) allEntries.nextElement();
			String classname = entry.getName();
			if (classname.endsWith(".class")) {
				this.checkClass(classname);
			}
		}
	}

	private void checkClass(String classname) {
		if (classname.startsWith("/")) {
			classname = classname.substring(1);
		}
		if (classname.endsWith(".class")) {
			classname = classname.substring(0, classname.length() - 6);
		}
		classname = classname.replaceAll("/", ".");

		logger.debug("Found class " + classname + "...");

		Class<?> clazz = null;
		for (ClassLoader classLoader : classLoaders) {
			try {
				clazz = Class.forName(classname, false, classLoader);
				break;
			} catch (ClassNotFoundException | NoClassDefFoundError e) {
				continue;
			}
		}

		if (clazz != null) {
			this.checkClass(clazz);
		} else {
			logger.error("Class " + classname + "not loadable with provided ClassLoader(s)...");
		}
	}

	private void checkClass(Class<?> clazz) {
		Set<Method> hitMethods = request.check(clazz);
		if (hitMethods.size() > 0)
			hits.put(clazz, hitMethods);
	}
}
