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
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for SummarySegmentGroupType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="SummarySegmentGroupType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}DSType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{urn:mpeg:mpeg7:schema:2004}TextualType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="SummarySegment" type="{urn:mpeg:mpeg7:schema:2004}SummarySegmentType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="SummarySegmentGroup" type="{urn:mpeg:mpeg7:schema:2004}SummarySegmentGroupType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="fidelity" type="{urn:mpeg:mpeg7:schema:2004}zeroToOneType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummarySegmentGroupType", propOrder = { "name", "summarySegment", "summarySegmentGroup" })
public class SummarySegmentGroupType extends DSType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "Name")
	protected List<TextualType> name;
	@XmlElement(name = "SummarySegment")
	protected List<SummarySegmentType> summarySegment;
	@XmlElement(name = "SummarySegmentGroup")
	protected List<SummarySegmentGroupType> summarySegmentGroup;
	@XmlAttribute(name = "fidelity")
	protected Float fidelity;

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
	 * Gets the value of the summarySegment property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the summarySegment property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getSummarySegment().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link SummarySegmentType }
	 * 
	 * 
	 */
	public List<SummarySegmentType> getSummarySegment() {
		if (summarySegment == null) {
			summarySegment = new ArrayList<SummarySegmentType>();
		}
		return this.summarySegment;
	}

	/**
	 * Gets the value of the summarySegmentGroup property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the summarySegmentGroup property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getSummarySegmentGroup().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link SummarySegmentGroupType }
	 * 
	 * 
	 */
	public List<SummarySegmentGroupType> getSummarySegmentGroup() {
		if (summarySegmentGroup == null) {
			summarySegmentGroup = new ArrayList<SummarySegmentGroupType>();
		}
		return this.summarySegmentGroup;
	}

	/**
	 * Gets the value of the fidelity property.
	 * 
	 * @return possible object is {@link Float }
	 * 
	 */
	public Float getFidelity() {
		return fidelity;
	}

	/**
	 * Sets the value of the fidelity property.
	 * 
	 * @param value
	 *            allowed object is {@link Float }
	 * 
	 */
	public void setFidelity(Float value) {
		this.fidelity = value;
	}

}
