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
 * Java class for NonMixtureCameraMotionSegmentType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="NonMixtureCameraMotionSegmentType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}CameraMotionSegmentType">
 *       &lt;sequence>
 *         &lt;element name="AmountOfMotion" type="{urn:mpeg:mpeg7:schema:2004}NonMixtureAmountOfMotionType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NonMixtureCameraMotionSegmentType", propOrder = { "amountOfMotion" })
public class NonMixtureCameraMotionSegmentType extends CameraMotionSegmentType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "AmountOfMotion", required = true)
	protected NonMixtureAmountOfMotionType amountOfMotion;

	/**
	 * Gets the value of the amountOfMotion property.
	 * 
	 * @return possible object is {@link NonMixtureAmountOfMotionType }
	 * 
	 */
	public NonMixtureAmountOfMotionType getAmountOfMotion() {
		return amountOfMotion;
	}

	/**
	 * Sets the value of the amountOfMotion property.
	 * 
	 * @param value
	 *            allowed object is {@link NonMixtureAmountOfMotionType }
	 * 
	 */
	public void setAmountOfMotion(NonMixtureAmountOfMotionType value) {
		this.amountOfMotion = value;
	}

}
