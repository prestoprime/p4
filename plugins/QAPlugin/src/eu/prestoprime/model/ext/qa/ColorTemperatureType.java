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
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ColorTemperatureType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="ColorTemperatureType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}VisualDType">
 *       &lt;sequence>
 *         &lt;element name="BrowsingCategory">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="hot"/>
 *               &lt;enumeration value="warm"/>
 *               &lt;enumeration value="moderate"/>
 *               &lt;enumeration value="cool"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="SubRangeIndex" type="{urn:mpeg:mpeg7:schema:2004}unsigned6"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorTemperatureType", propOrder = { "browsingCategory", "subRangeIndex" })
public class ColorTemperatureType extends VisualDType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "BrowsingCategory", required = true)
	protected String browsingCategory;
	@XmlElement(name = "SubRangeIndex")
	protected int subRangeIndex;

	/**
	 * Gets the value of the browsingCategory property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBrowsingCategory() {
		return browsingCategory;
	}

	/**
	 * Sets the value of the browsingCategory property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBrowsingCategory(String value) {
		this.browsingCategory = value;
	}

	/**
	 * Gets the value of the subRangeIndex property.
	 * 
	 */
	public int getSubRangeIndex() {
		return subRangeIndex;
	}

	/**
	 * Sets the value of the subRangeIndex property.
	 * 
	 */
	public void setSubRangeIndex(int value) {
		this.subRangeIndex = value;
	}

}
