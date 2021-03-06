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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for SummaryType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="SummaryType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}DSType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{urn:mpeg:mpeg7:schema:2004}TextualType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="SourceID" type="{urn:mpeg:mpeg7:schema:2004}UniqueIDType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummaryType", propOrder = { "name", "sourceID" })
@XmlSeeAlso({ SequentialSummaryType.class, HierarchicalSummaryType.class })
public abstract class SummaryType extends DSType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "Name")
	protected List<TextualType> name;
	@XmlElement(name = "SourceID")
	protected UniqueIDType sourceID;

	/**
	 * Gets the value of the name property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the name property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getName().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link TextualType }
	 * 
	 * 
	 */
	public List<TextualType> getName() {
		if (name == null) {
			name = new ArrayList<TextualType>();
		}
		return this.name;
	}

	/**
	 * Gets the value of the sourceID property.
	 * 
	 * @return possible object is {@link UniqueIDType }
	 * 
	 */
	public UniqueIDType getSourceID() {
		return sourceID;
	}

	/**
	 * Sets the value of the sourceID property.
	 * 
	 * @param value
	 *            allowed object is {@link UniqueIDType }
	 * 
	 */
	public void setSourceID(UniqueIDType value) {
		this.sourceID = value;
	}

}
