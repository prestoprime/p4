/**
 * Tools.java
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
package eu.prestoprime.p4gui.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class Tools {

	public static void servletForward(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
		if (!page.startsWith("/"))
			page = "/" + page;
		servlet.getServletContext().getRequestDispatcher(page).forward(request, response);
	}

	public static void servletInclude(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
		if (!page.startsWith("/"))
			page = "/" + page;
		servlet.getServletContext().getRequestDispatcher(page).include(request, response);
	}

	public static <T> T getRequestAttribute(ServletRequest request, String attribute, Class<T> clazz) {
		Object object = request.getAttribute(attribute);
		if (object != null) {
			return clazz.cast(object);
		} else {
			try {
				T def = clazz.cast(clazz.newInstance());
				request.setAttribute(attribute, def);
				return def;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static <T> T getSessionAttribute(HttpSession session, String attribute, Class<T> clazz) {
		Object object = session.getAttribute(attribute);
		if (object != null) {
			return clazz.cast(object);
		} else {
			try {
				T def = clazz.cast(clazz.newInstance());
				session.setAttribute(attribute, def);
				return def;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static <T> T getApplicationAttribute(ServletContext context, String attribute, Class<T> clazz) {
		Object object = context.getAttribute(attribute);
		if (object != null)
			return clazz.cast(object);
		else {
			try {
				T def = clazz.cast(clazz.newInstance());
				context.setAttribute(attribute, def);
				return def;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Deprecated
	public static String stringify(Document dom) {
		String string = "";
		try {
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(dom);
			TransformerFactory.newInstance().newTransformer().transform(source, result);
			string = sw.toString().replaceAll("(\\r|\\n)", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return string;
	}

	@Deprecated
	public static String stringify2(Document dom) {
		String string = "";
		try {
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(dom);
			TransformerFactory.newInstance().newTransformer().transform(source, result);
			string = sw.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return string;
	}

	public static ArrayList<String> getFilesInFolder(String path, ArrayList<String> files) {
		File root = new File(path);
		File[] list = root.listFiles();
		for (File f : list) {
			if (f.isDirectory()) {
				Tools.getFilesInFolder(f.getAbsolutePath(), files);
			} else {
				files.add(f.getAbsolutePath());
			}
		}
		return files;
	}

	public static ArrayList<String> getFilesInFile(File list) {
		ArrayList<String> files = new ArrayList<String>();
		String line;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(list));
			while ((line = reader.readLine()) != null)
				files.add(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return files;
	}
}
