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
 * Java class for Mpeg7Type complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="Mpeg7Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DescriptionProfile" type="{urn:mpeg:mpeg7:schema:2004}DescriptionProfileType"/>
 *         &lt;element name="DescriptionMetadata" type="{urn:mpeg:mpeg7:schema:2004}DescriptionMetadataType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Mpeg7Type", propOrder = { "descriptionProfile", "descriptionMetadata" })
@XmlSeeAlso({ Mpeg7.class })
public abstract class Mpeg7Type implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "DescriptionProfile", required = true)
	protected DescriptionProfileType descriptionProfile;
	@XmlElement(name = "DescriptionMetadata", required = true)
	protected DescriptionMetadataType descriptionMetadata;

	/**
	 * Gets the value of the descriptionProfile property.
	 * 
	 * @return possible object is {@link DescriptionProfileType }
	 * 
	 */
	public DescriptionProfileType getDescriptionProfile() {
		return descriptionProfile;
	}

	/**
	 * Sets the value of the descriptionProfile property.
	 * 
	 * @param value
	 *            allowed object is {@link DescriptionProfileType }
	 * 
	 */
	public void setDescriptionProfile(DescriptionProfileType value) {
		this.descriptionProfile = value;
	}

	/**
	 * Gets the value of the descriptionMetadata property.
	 * 
	 * @return possible object is {@link DescriptionMetadataType }
	 * 
	 */
	public DescriptionMetadataType getDescriptionMetadata() {
		return descriptionMetadata;
	}

	/**
	 * Sets the value of the descriptionMetadata property.
	 * 
	 * @param value
	 *            allowed object is {@link DescriptionMetadataType }
	 * 
	 */
	public void setDescriptionMetadata(DescriptionMetadataType value) {
		this.descriptionMetadata = value;
	}

}
