//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.24 at 11:42:28 PM CEST 
//

package eu.prestoprime.model.ext.qa;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for TimeType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="TimeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;sequence>
 *           &lt;element name="TimePoint" type="{urn:mpeg:mpeg7:schema:2004}timePointType"/>
 *         &lt;/sequence>
 *         &lt;sequence minOccurs="0">
 *           &lt;element name="Duration" type="{urn:mpeg:mpeg7:schema:2004}durationType"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeType", propOrder = { "timePoint", "duration" })
@XmlSeeAlso({ eu.prestoprime.model.ext.qa.AvailabilityType.AvailabilityPeriod.class })
public class TimeType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "TimePoint", required = true)
	protected String timePoint;
	@XmlElement(name = "Duration")
	protected String duration;

	/**
	 * Gets the value of the timePoint property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTimePoint() {
		return timePoint;
	}

	/**
	 * Sets the value of the timePoint property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTimePoint(String value) {
		this.timePoint = value;
	}

	/**
	 * Gets the value of the duration property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDuration() {
		return duration;
	}

	/**
	 * Sets the value of the duration property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDuration(String value) {
		this.duration = value;
	}

}
