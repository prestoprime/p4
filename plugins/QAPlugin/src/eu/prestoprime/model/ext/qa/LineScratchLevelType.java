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
 * Java class for LineScratchLevelType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="LineScratchLevelType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.prestospace.org/res/defect_quality}VisualImpairmentType">
 *       &lt;sequence>
 *         &lt;element name="NumberOfScratches" type="{urn:mpeg:mpeg7:schema:2004}unsigned16"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LineScratchLevelType", namespace = "http://www.prestospace.org/res/defect_quality", propOrder = { "numberOfScratches" })
public class LineScratchLevelType extends VisualImpairmentType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "NumberOfScratches")
	protected int numberOfScratches;

	/**
	 * Gets the value of the numberOfScratches property.
	 * 
	 */
	public int getNumberOfScratches() {
		return numberOfScratches;
	}

	/**
	 * Sets the value of the numberOfScratches property.
	 * 
	 */
	public void setNumberOfScratches(int value) {
		this.numberOfScratches = value;
	}

}
