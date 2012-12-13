/**
 * P4RWInformationPackage.java
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
package eu.prestoprime.datamanagement;

import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.w3c.dom.Node;

import eu.prestoprime.conf.ConfigurationManager;
import eu.prestoprime.conf.ConfigurationManager.P4Property;
import eu.prestoprime.model.dc.Record;
import eu.prestoprime.model.mets.FileType;
import eu.prestoprime.model.mets.FileType.FLocat;
import eu.prestoprime.model.mets.MdSecType;
import eu.prestoprime.model.mets.MdSecType.MdWrap;
import eu.prestoprime.model.mets.MdSecType.MdWrap.XmlData;
import eu.prestoprime.model.mets.Mets;
import eu.prestoprime.model.mets.MetsType.FileSec;
import eu.prestoprime.model.mets.MetsType.FileSec.FileGrp;
import eu.prestoprime.model.oais.DIP.DCField;
import eu.prestoprime.model.oais.RWInformationPackage;

public abstract class P4RWInformationPackage extends P4InformationPackage implements RWInformationPackage {

	public P4RWInformationPackage(String id, Node content) {
		super(id, content);
	}

	/**
	 * Use this method to update format or description.
	 */
	@Override
	public synchronized void setDCField(DCField field, List<String> values) throws P4IPException {
		Mets mets = this.getContentAsMets();

		for (MdSecType mdSec : mets.getDmdSec()) {
			MdWrap mdWrap = mdSec.getMdWrap();
			if (mdWrap.getMDTYPE() != null && mdWrap.getMDTYPE().equals("DC")) {
				if (mdWrap != null) {
					XmlData xmlData = mdWrap.getXmlData();
					if (xmlData != null) {
						for (Object obj : xmlData.getAny()) {
							Record record = (Record) obj;

							switch (field) {
							case description:
								record.getDescription().clear();
								record.getDescription().addAll(values);
								break;
							case format:
								record.getFormat().clear();
								record.getFormat().addAll(values);
								break;
							case date:
								record.getDate().clear();
								record.getDate().addAll(values);
								break;
							case creator:
								record.getCreator().clear();
								record.getCreator().addAll(values);
								break;
							case contributor:
							case coverage:
							case identifier: // never allow
							case language:
							case publisher:
							case relation:
							case source:
							case subject:
							case title:
							case type:
								throw new P4IPException("DCField update not allowed...");
							}

							break;
						}
					}
				}
			}
		}

		this.setContent(mets);
	}

	@Override
	public synchronized void addExternalFile(String mimeType, String href, String md5sum, long size) throws P4IPException {
		Mets mets = this.getContentAsMets();

		if (mets.getFileSec() == null) {
			mets.setFileSec(new FileSec());
		}

		if (mets.getFileSec().getFileGrp().size() == 0) {
			mets.getFileSec().getFileGrp().add(new FileGrp());
		}

		FileGrp fileGrp = mets.getFileSec().getFileGrp().get(0);
		List<FileType> fileList = fileGrp.getFile();

		FileType fileType = new FileType();
		fileType.setID("file-" + System.currentTimeMillis());
		fileType.setMIMETYPE(mimeType);
		fileType.setCHECKSUMTYPE("MD5");
		fileType.setCHECKSUM(md5sum);
		fileType.setSIZE(size);
		// update DATE
		DatatypeFactory df;
		try {
			df = DatatypeFactory.newInstance();
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(new Date());
			XMLGregorianCalendar xgc = df.newXMLGregorianCalendar(gc);
			fileType.setCREATED(xgc);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			throw new P4IPException("Unable to set DATE for METS file...");
		}
		fileList.add(fileType);

		List<FLocat> fLocatList = fileType.getFLocat();
		FLocat fLocat = new FLocat();
		fLocat.setLOCTYPE("OTHER");
		fLocat.setOTHERLOCTYPE("FILE");
		fLocat.setHref(href);
		fLocatList.add(fLocat);

		this.setContent(mets);
	}

	@Override
	public synchronized void addFile(String mimeType, String locType, String href, String md5sum, long size) throws P4IPException {
		Mets mets = this.getContentAsMets();

		if (mets.getFileSec() == null) {
			mets.setFileSec(new FileSec());
		}

		if (mets.getFileSec().getFileGrp().size() == 0) {
			mets.getFileSec().getFileGrp().add(new FileGrp());
		}

		FileGrp fileGrp = mets.getFileSec().getFileGrp().get(0);
		List<FileType> fileList = fileGrp.getFile();

		// get existing FileType or create a new one
		FileType fileType = null;
		for (FileType fileTypeTmp : fileList) {
			if (fileTypeTmp.getMIMETYPE().equals(mimeType)) {
				fileType = fileTypeTmp;
				break;
			}
		}
		if (fileType == null) {
			fileType = new FileType();
			fileType.setMIMETYPE(mimeType);
			fileList.add(fileType);
		}

		// set file ID
		fileType.setID("file-" + System.currentTimeMillis());

		// update DATE
		DatatypeFactory df;
		try {
			df = DatatypeFactory.newInstance();
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(new Date());
			XMLGregorianCalendar xgc = df.newXMLGregorianCalendar(gc);
			fileType.setCREATED(xgc);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			throw new P4IPException("Unable to set DATE for METS file...");
		}

		// update
		if (fileType.getCHECKSUM() == null && md5sum != null) {
			fileType.setCHECKSUM(md5sum);
			fileType.setCHECKSUMTYPE("MD5");
		}
		if (fileType.getSIZE() == null) {
			fileType.setSIZE(size);
		}

		// add or update FLocat
		List<FLocat> fLocatList = fileType.getFLocat();
		FLocat fLocat = null;

		for (FLocat fLocatTmp : fLocatList) {

			if (fLocatTmp.getLOCTYPE().equals(locType) || fLocatTmp.getOTHERLOCTYPE().equals(locType)) {
				fLocat = fLocatTmp;
				break;
			}

		}

		if (fLocat == null) {

			fLocat = new FLocat();
			fLocatList.add(fLocat);

			switch (locType) {
			case "URL":
				fLocat.setLOCTYPE(locType);
				fLocat.setOTHERLOCTYPE(null);
				break;

			case "URN":
				fLocat.setLOCTYPE(locType);
				fLocat.setOTHERLOCTYPE(null);
				break;

			default:
				fLocat.setLOCTYPE("OTHER");
				fLocat.setOTHERLOCTYPE(locType);
			}
		}

		if (locType.equals("FILE")) {
			href = ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_PLACEHOLDER) + File.separator + href.substring(ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_STORAGE_VOLUME).length() + 1);
		}

		fLocat.setID(null);
		fLocat.setHref(href);

		this.setContent(mets);
	}

	@Override
	public synchronized void replaceFileLocation(String mimeType, String oldLocType, String newLocType, String href, String md5sum, long size) throws P4IPException {
		// using this for replacing the p4share location in the SIP with the
		// link to the master file on tape storage that is already there.

		Mets mets = this.getContentAsMets();

		FileGrp fileGrp = mets.getFileSec().getFileGrp().get(0);
		List<FileType> fileList = fileGrp.getFile();

		// get existing FileType or create a new one
		FileType fileType = null;
		for (FileType fileTypeTmp : fileList) {
			if (fileTypeTmp.getMIMETYPE().equals(mimeType)) {
				fileType = fileTypeTmp;
				break;
			}
		}
		if (fileType == null) {
			fileType = new FileType();
			fileType.setMIMETYPE(mimeType);
			fileList.add(fileType);
		}

		// set file ID
		fileType.setID("file-" + System.currentTimeMillis());

		// update DATE
		DatatypeFactory df;
		try {
			df = DatatypeFactory.newInstance();
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(new Date());
			XMLGregorianCalendar xgc = df.newXMLGregorianCalendar(gc);
			fileType.setCREATED(xgc);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			throw new P4IPException("Unable to set DATE for METS file...");
		}

		// update
		if (fileType.getCHECKSUM() == null && md5sum != null) {
			fileType.setCHECKSUM(md5sum);
			fileType.setCHECKSUMTYPE("MD5");
		}
		if (fileType.getSIZE() == null) {
			fileType.setSIZE(size);
		}

		// add or update FLocat
		List<FLocat> fLocatList = fileType.getFLocat();
		FLocat fLocat = null;

		for (FLocat fLocatTmp : fLocatList) {

			if (fLocatTmp.getLOCTYPE().equals(oldLocType) || fLocatTmp.getOTHERLOCTYPE().equals(oldLocType)) {
				fLocat = fLocatTmp;
				break;
			}

		}

		if (fLocat == null) {

			fLocat = new FLocat();
			fLocatList.add(fLocat);
		}

		switch (newLocType) {
		case "URL":
			fLocat.setLOCTYPE(newLocType);
			fLocat.setOTHERLOCTYPE(null);
			break;

		case "URN":
			fLocat.setLOCTYPE(newLocType);
			fLocat.setOTHERLOCTYPE(null);
			break;

		default:
			fLocat.setLOCTYPE("OTHER");
			fLocat.setOTHERLOCTYPE(newLocType);
		}

		if (newLocType.equals("FILE")) {
			href = ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_PLACEHOLDER) + File.separator + href.substring(ConfigurationManager.getPropertyInstance().getProperty(P4Property.P4_STORAGE_VOLUME).length() + 1);
		}

		fLocat.setID(null);
		fLocat.setHref(href);

		this.setContent(mets);
	}

}
