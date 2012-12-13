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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for GeographicPointType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="GeographicPointType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="longitude" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minInclusive value="-180.0"/>
 *             &lt;maxInclusive value="180.0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="latitude" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minInclusive value="-90.0"/>
 *             &lt;maxInclusive value="90.0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="altitude" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GeographicPointType")
public class GeographicPointType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlAttribute(name = "longitude", required = true)
	protected double longitude;
	@XmlAttribute(name = "latitude", required = true)
	protected double latitude;
	@XmlAttribute(name = "altitude")
	protected Double altitude;

	/**
	 * Gets the value of the longitude property.
	 * 
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Sets the value of the longitude property.
	 * 
	 */
	public void setLongitude(double value) {
		this.longitude = value;
	}

	/**
	 * Gets the value of the latitude property.
	 * 
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Sets the value of the latitude property.
	 * 
	 */
	public void setLatitude(double value) {
		this.latitude = value;
	}

	/**
	 * Gets the value of the altitude property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public Double getAltitude() {
		return altitude;
	}

	/**
	 * Sets the value of the altitude property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setAltitude(Double value) {
		this.altitude = value;
	}

}
