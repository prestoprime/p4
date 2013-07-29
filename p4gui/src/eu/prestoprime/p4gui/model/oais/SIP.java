/**
 * SIP.java
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
package eu.prestoprime.p4gui.model.oais;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;

import eu.prestoprime.model.ModelUtils;
import eu.prestoprime.model.ModelUtils.P4JAXBPackage;
import eu.prestoprime.model.mets.Mets;
import eu.prestoprime.p4gui.util.parse.Location;
import eu.prestoprime.p4gui.util.parse.Resource;

public class SIP extends InformationPackage {

	public SIP() {
		super("", null);
		try {
			this.content = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SIP(Node content) {
		super("", content);
	}

	public SIP(Mets mets) {
		super("", null);

		try {
			// parse sip and get content as Node
			Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Marshaller marshaller = ModelUtils.getMarshaller(P4JAXBPackage.DATA_MODEL);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(mets, node);

			this.content = node;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Resource> getResources() {

		List<Resource> resources = super.getResources();

		if (resources.size() == 0) {
			ArrayList<Location> locations = new ArrayList<Location>();
			locations.add(new Location("", "", ""));
			resources.add(new Resource("", "", locations, "", ""));
		}

		return resources;
	}
}
