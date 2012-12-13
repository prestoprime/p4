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
 * Java class for MediaInformationType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="MediaInformationType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}DSType">
 *       &lt;sequence>
 *         &lt;element name="MediaIdentification" type="{urn:mpeg:mpeg7:schema:2004}MediaIdentificationType" minOccurs="0"/>
 *         &lt;element name="MediaProfile" type="{urn:mpeg:mpeg7:schema:2004}MediaProfileType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MediaInformationType", propOrder = { "mediaIdentification", "mediaProfile" })
public class MediaInformationType extends DSType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "MediaIdentification")
	protected MediaIdentificationType mediaIdentification;
	@XmlElement(name = "MediaProfile", required = true)
	protected MediaProfileType mediaProfile;

	/**
	 * Gets the value of the mediaIdentification property.
	 * 
	 * @return possible object is {@link MediaIdentificationType }
	 * 
	 */
	public MediaIdentificationType getMediaIdentification() {
		return mediaIdentification;
	}

	/**
	 * Sets the value of the mediaIdentification property.
	 * 
	 * @param value
	 *            allowed object is {@link MediaIdentificationType }
	 * 
	 */
	public void setMediaIdentification(MediaIdentificationType value) {
		this.mediaIdentification = value;
	}

	/**
	 * Gets the value of the mediaProfile property.
	 * 
	 * @return possible object is {@link MediaProfileType }
	 * 
	 */
	public MediaProfileType getMediaProfile() {
		return mediaProfile;
	}

	/**
	 * Sets the value of the mediaProfile property.
	 * 
	 * @param value
	 *            allowed object is {@link MediaProfileType }
	 * 
	 */
	public void setMediaProfile(MediaProfileType value) {
		this.mediaProfile = value;
	}

}
