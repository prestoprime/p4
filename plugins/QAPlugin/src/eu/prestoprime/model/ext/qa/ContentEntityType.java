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
 * Java class for ContentEntityType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="ContentEntityType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}ContentDescriptionType">
 *       &lt;sequence>
 *         &lt;element name="MultimediaContent" type="{urn:mpeg:mpeg7:schema:2004}MultimediaContentType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentEntityType", propOrder = { "multimediaContent" })
public class ContentEntityType extends ContentDescriptionType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "MultimediaContent", required = true)
	protected MultimediaContentType multimediaContent;

	/**
	 * Gets the value of the multimediaContent property.
	 * 
	 * @return possible object is {@link MultimediaContentType }
	 * 
	 */
	public MultimediaContentType getMultimediaContent() {
		return multimediaContent;
	}

	/**
	 * Sets the value of the multimediaContent property.
	 * 
	 * @param value
	 *            allowed object is {@link MultimediaContentType }
	 * 
	 */
	public void setMultimediaContent(MultimediaContentType value) {
		this.multimediaContent = value;
	}

}
