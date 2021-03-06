//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.24 at 11:42:28 PM CEST 
//

package eu.prestoprime.model.ext.qa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ColorStructureType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="ColorStructureType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}VisualDType">
 *       &lt;sequence>
 *         &lt;element name="Values">
 *           &lt;simpleType>
 *             &lt;restriction>
 *               &lt;simpleType>
 *                 &lt;list itemType="{urn:mpeg:mpeg7:schema:2004}unsigned8" />
 *               &lt;/simpleType>
 *               &lt;minLength value="1"/>
 *               &lt;maxLength value="256"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="colorQuant" use="required" type="{urn:mpeg:mpeg7:schema:2004}unsigned3" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorStructureType", propOrder = { "values" })
public class ColorStructureType extends VisualDType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlList
	@XmlElement(name = "Values", type = Integer.class)
	protected List<Integer> values;
	@XmlAttribute(name = "colorQuant", required = true)
	protected int colorQuant;

	/**
	 * Gets the value of the values property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the values property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getValues().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Integer }
	 * 
	 * 
	 */
	public List<Integer> getValues() {
		if (values == null) {
			values = new ArrayList<Integer>();
		}
		return this.values;
	}

	/**
	 * Gets the value of the colorQuant property.
	 * 
	 */
	public int getColorQuant() {
		return colorQuant;
	}

	/**
	 * Sets the value of the colorQuant property.
	 * 
	 */
	public void setColorQuant(int value) {
		this.colorQuant = value;
	}

}
