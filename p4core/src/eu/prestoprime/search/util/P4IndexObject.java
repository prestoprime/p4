/**
 * P4IndexObject.java
 * Author: Philip Kahle (philip.kahle@uibk.ac.at)
 * 
 * This file is part of PrestoPRIME Preservation Platform (P4).
 * 
 * Copyright (C) 2009-2012 University of Innsbruck, Austria
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
package eu.prestoprime.search.util;

import java.util.ArrayList;
import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

public class P4IndexObject extends IndexObject {

	// fields according to the solr schema
	@Field("createDate")
	private Date createDate;

	@Field("waisda")
	private String[] userAnnot;

	@Field("dcContributor")
	private String[] dcContrib;

	@Field("dcCoverage")
	private String[] dcCoverage;

	@Field("dcCreator")
	private String[] dcCreator;

	@Field("dcDate")
	private Date[] dcDate;

	@Field("dcDescription")
	private String[] dcDescription;

	@Field("dcFormat")
	private String[] dcFormat;

	@Field("dcIdentifier")
	private String[] dcIdentifier;

	@Field("dcLanguage")
	private String[] dcLang;

	@Field("dcPublisher")
	private String[] dcPublisher;

	@Field("dcRelation")
	private String[] dcRelation;

	@Field("dcSource")
	private String[] dcSource;

	@Field("dcSubject")
	private String[] dcSubject;

	@Field("dcTitle")
	private String[] dcTitle;

	@Field("dcType")
	private String[] dcType;

	@Field("techAspectRatio")
	private String aspect;

	@Field("techWidth")
	private Integer width;

	@Field("techHeight")
	private Integer height;

	@Field("techResolution")
	private String resolution;

	@Field("techCodec")
	private String codec;

	@Field("techDuration")
	private Float duration;

	@Field("dateString")
	private String dateString; // for holding the free text field "date" from
								// uibk video db

	// @Field("year")
	// private Integer[] year;

	@Field("isScreenAdaptation")
	private Boolean isScreenAdapt; // true if "Literaturverfilmung"

	@Field("script")
	private String[] script;

	@Field("actors")
	private String[] actors;

	@Field("scene")
	private String[] scene;

	@Field("sound")
	private String[] sound;

	@Field("camera")
	private String[] camera;

	@Field("music")
	private String[] music;

	@Field("studio")
	private String[] studio;

	// Fields for Sorting are filled at construction time with the first Value
	// of the respective dublin core fields
	// TODO deal with the occurence of several dcRecords!!
	@Field("titleSort")
	private String titleSortField;
	@Field("dateSort")
	private Date dateSortField;
	@Field("creatorSort")
	private String creatorSortField;

	public P4IndexObject() {
		super();
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		// dateSortField is filled via copyField directive in solr now.
		// if(createDate != null){
		// this.setDateSortField(createDate);
		// }
		this.createDate = createDate;
	}

	public String[] getUserAnnot() {
		return userAnnot;
	}

	public void setUserAnnot(String[] userAnnot) {
		this.userAnnot = userAnnot;
	}

	public String[] getDcContrib() {
		return dcContrib;
	}

	public void setDcContrib(String[] dcContrib) {
		this.dcContrib = dcContrib;
	}

	public String[] getDcCoverage() {
		return dcCoverage;
	}

	public void setDcCoverage(String[] dcCoverage) {
		this.dcCoverage = dcCoverage;
	}

	public String[] getDcCreator() {
		return dcCreator;
	}

	public void setDcCreator(String[] dcCreator) {
		// copy first value to singleValued Sortfields
		if (dcCreator != null && dcCreator.length != 0)
			setCreatorSortField(dcCreator[0]);
		this.dcCreator = dcCreator;
	}

	public Date[] getDcDate() {
		return dcDate;
	}

	public ArrayList<String> getDcDateStrings() {
		ArrayList<String> ds;

		if (dcDate == null || dcDate.length == 0) {
			ds = new ArrayList<>(0);
		} else {
			ds = new ArrayList<>(dcDate.length);
			for (int i = 0; i < dcDate.length; i++) {
				ds.add(i, "" + dcDate[i]);
			}
		}
		return ds;
	}

	public void setDcDate(Date[] dcDate) {
		this.dcDate = dcDate;
	}

	public String[] getDcDescription() {
		return dcDescription;
	}

	public void setDcDescription(String[] dcDescription) {
		this.dcDescription = dcDescription;
	}

	public String[] getDcFormat() {
		return dcFormat;
	}

	public void setDcFormat(String[] dcFormat) {
		this.dcFormat = dcFormat;
	}

	public String[] getDcIdentifier() {
		return dcIdentifier;
	}

	public void setDcIdentifier(String[] dcIdentifier) {
		this.dcIdentifier = dcIdentifier;
	}

	public String[] getDcLang() {
		return dcLang;
	}

	public void setDcLang(String[] dcLang) {
		this.dcLang = dcLang;
	}

	public String[] getDcPublisher() {
		return dcPublisher;
	}

	public void setDcPublisher(String[] dcPublisher) {
		this.dcPublisher = dcPublisher;
	}

	public String[] getDcRelation() {
		return dcRelation;
	}

	public void setDcRelation(String[] dcRelation) {
		this.dcRelation = dcRelation;
	}

	public String[] getDcSource() {
		return dcSource;
	}

	public void setDcSource(String[] dcSource) {
		this.dcSource = dcSource;
	}

	public String[] getDcSubject() {
		return dcSubject;
	}

	public void setDcSubject(String[] dcSubject) {
		this.dcSubject = dcSubject;
	}

	public String[] getDcTitle() {
		return dcTitle;
	}

	public void setDcTitle(String[] dcTitle) {
		if (dcTitle != null && dcTitle.length > 0)
			setTitleSortField(dcTitle[0]);
		this.dcTitle = dcTitle;
	}

	public String[] getDcType() {
		return dcType;
	}

	public void setDcType(String[] dcType) {
		this.dcType = dcType;
	}

	public String getAspect() {
		return aspect;
	}

	public void setAspect(String aspect) {
		this.aspect = aspect;
	}

	public Integer getWidth() {
		return width;
	}

	public void setDimensions(Integer width, Integer height) {
		this.setWidth(width);
		this.setHeight(height);
		if (width != null && height != null) {
			this.setResolution(width.toString() + "x" + height.toString());
		}
	}

	/**
	 * use setDimensions() for specifying width, height and resolution at once
	 */
	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	/**
	 * use setDimensions() for specifying width, height and resolution at once
	 */
	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getResolution() {
		return resolution;
	}

	/**
	 * Do not use!!!! use setDimensions() for having proper resolution format
	 * Strings!
	 */
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getCodec() {
		return codec;
	}

	public void setCodec(String codec) {
		this.codec = codec;
	}

	public Float getDuration() {
		return duration;
	}

	public void setDuration(Float duration) {
		this.duration = duration;
	}

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	// public Integer[] getYear() {
	// return year;
	// }
	//
	// public void setYear(Integer[] year) {
	// this.year = year;
	// }

	public Boolean getIsScreenAdapt() {
		return isScreenAdapt;
	}

	public void setIsScreenAdapt(Boolean isScreenAdapt) {
		this.isScreenAdapt = isScreenAdapt;
	}

	public String[] getScript() {
		return script;
	}

	public void setScript(String[] script) {
		this.script = script;
	}

	public String[] getActors() {
		return actors;
	}

	public void setActors(String[] actors) {
		this.actors = actors;
	}

	public String[] getScene() {
		return scene;
	}

	public void setScene(String[] scene) {
		this.scene = scene;
	}

	public String[] getSound() {
		return sound;
	}

	public void setSound(String[] sound) {
		this.sound = sound;
	}

	public String[] getCamera() {
		return camera;
	}

	public void setCamera(String[] camera) {
		this.camera = camera;
	}

	public String[] getMusic() {
		return music;
	}

	public void setMusic(String[] music) {
		this.music = music;
	}

	public String[] getStudio() {
		return studio;
	}

	public void setStudio(String[] studio) {
		this.studio = studio;
	}

	public String getTitleSortField() {
		return titleSortField;
	}

	public void setTitleSortField(String titleSortField) {
		this.titleSortField = titleSortField;
	}

	public Date getDateSortField() {
		return dateSortField;
	}

	public void setDateSortField(Date dateSortField) {
		this.dateSortField = dateSortField;
	}

	public String getCreatorSortField() {
		return creatorSortField;
	}

	public void setCreatorSortField(String creatorSortField) {
		this.creatorSortField = creatorSortField;
	}

}
