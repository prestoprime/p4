/**
 * PreviewIngestServlet.java
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
package eu.prestoprime.p4gui.ingest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import eu.prestoprime.model.dc.Record;
import eu.prestoprime.model.mets.AmdSecType;
import eu.prestoprime.model.mets.FileType;
import eu.prestoprime.model.mets.FileType.FLocat;
import eu.prestoprime.model.mets.MdSecType;
import eu.prestoprime.model.mets.MdSecType.MdWrap;
import eu.prestoprime.model.mets.MdSecType.MdWrap.XmlData;
import eu.prestoprime.model.mets.Mets;
import eu.prestoprime.model.mets.MetsType.FileSec;
import eu.prestoprime.model.mets.MetsType.FileSec.FileGrp;
import eu.prestoprime.model.mets.ObjectFactory;
import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.model.oais.SIP;

@WebServlet("/ingest/preview")
public class PreviewIngestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(PreviewIngestServlet.class);

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("Start creating METS...");

		Mets mets = new ObjectFactory().createMets();
		mets.setID(UUID.randomUUID().toString());

		// DmdSec
		Record record = new Record();
		record.getIdentifier().add(request.getParameter("dc_identifier"));
		record.getTitle().add(request.getParameter("dc_title"));
		record.getDescription().add(request.getParameter("dc_description"));
		record.getCreator().add(request.getParameter("dc_creator"));
		record.getSource().add(request.getParameter("dc_source"));
		record.getFormat().add(request.getParameter("dc_format"));
		record.getLanguage().add(request.getParameter("dc_language"));
		record.getPublisher().add(request.getParameter("dc_publisher"));

		XmlData xmlDataDC = new XmlData();
		xmlDataDC.getAny().add(record);

		MdWrap mdWrapDC = new MdWrap();
		mdWrapDC.setMDTYPE("DC");
		mdWrapDC.setXmlData(xmlDataDC);

		MdSecType mdSec = new MdSecType();
		mdSec.setID("dmd-001");
		mdSec.setMdWrap(mdWrapDC);

		mets.getDmdSec().add(mdSec);

		// amdSec
		String owl = request.getParameter("owl");
		if (owl != null && !owl.equals("")) {
			try {
				Node rightsNode = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(owl.getBytes())).getDocumentElement();

				XmlData xmlDataRights = new XmlData();
				xmlDataRights.getAny().add(rightsNode);

				MdWrap mdWrapRights = new MdWrap();
				mdWrapRights.setXmlData(xmlDataRights);
				mdWrapRights.setMDTYPE("OTHER");
				mdWrapRights.setOTHERMDTYPE("PPAVRO");
				mdWrapRights.setLABEL("PPRIGHTS");
				mdWrapRights.setMIMETYPE("application/owl+xml");

				MdSecType rightsMD = new MdSecType();
				rightsMD.setMdWrap(mdWrapRights);

				AmdSecType amdSec = new AmdSecType();
				amdSec.setID("amd-001");
				amdSec.getRightsMD().add(rightsMD);

				mets.getAmdSec().add(amdSec);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// FileGrp
		FileGrp fileGrp = new FileGrp();
		fileGrp.setID("rep-001");
		fileGrp.getADMID().add(mdSec);

		int i = 0;
		String mimetype;
		while ((mimetype = request.getParameter("resource_mimetype_" + i)) != null && !mimetype.equals("off")) {
			int j = 0;
			String href;
			if ((href = request.getParameter("resource_href_" + i + "_" + j)) != null && !href.equals("")) {
				FLocat tmpFLocat = new FLocat();
				tmpFLocat.setID("FLocat-00" + j);
				tmpFLocat.setHref(href);
				tmpFLocat.setLOCTYPE("OTHER");
				tmpFLocat.setOTHERLOCTYPE("FILE");

				FileType tmpFile = new FileType();
				tmpFile.setID("file");
				tmpFile.setMIMETYPE(mimetype);
				tmpFile.getFLocat().add(tmpFLocat);

				fileGrp.getFile().add(tmpFile);
			}
			i++;
		}

		if (fileGrp.getFile().size() > 0) {
			FileSec fileSec = new FileSec();
			fileSec.getFileGrp().add(fileGrp);
			mets.setFileSec(fileSec);
		}

		logger.info("METS created!");

		SIP sip = new SIP(mets);
		request.getSession().removeAttribute(P4GUI.SIP_BEAN_NAME);
		request.getSession().setAttribute(P4GUI.SIP_BEAN_NAME, sip);

		String sipString = sip.getContentAsString(true);

		response.setContentType("text/xml");
		response.setContentLength((int) sipString.length());
		response.setHeader("Content-Disposition", "attachment; filename=\"SIP.xml\"");
		response.getWriter().write(sipString);
	}
}
