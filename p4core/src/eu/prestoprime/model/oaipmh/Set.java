//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.04 at 03:25:13 AM CEST 
//

package eu.prestoprime.model.oaipmh;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for set complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="set">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="setspec" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="setname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="setdescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "set", namespace = "http://www.prestoprime.eu/model/2012/oaipmhset", propOrder = { "setspec", "setname", "setdescription" })
public class Set implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(required = true)
	protected String setspec;
	@XmlElement(required = true)
	protected String setname;
	protected String setdescription;

	/**
	 * Gets the value of the setspec property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSetspec() {
		return setspec;
	}

	/**
	 * Sets the value of the setspec property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSetspec(String value) {
		this.setspec = value;
	}

	/**
	 * Gets the value of the setname property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSetname() {
		return setname;
	}

	/**
	 * Sets the value of the setname property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSetname(String value) {
		this.setname = value;
	}

	/**
	 * Gets the value of the setdescription property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSetdescription() {
		return setdescription;
	}

	/**
	 * Sets the value of the setdescription property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSetdescription(String value) {
		this.setdescription = value;
	}

}
