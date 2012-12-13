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
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for HierarchicalSummaryType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="HierarchicalSummaryType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}SummaryType">
 *       &lt;sequence>
 *         &lt;element name="SummarySegmentGroup" type="{urn:mpeg:mpeg7:schema:2004}SummarySegmentGroupType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="hierarchy" use="required" fixed="dependent">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="independent"/>
 *             &lt;enumeration value="dependent"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HierarchicalSummaryType", propOrder = { "summarySegmentGroup" })
public class HierarchicalSummaryType extends SummaryType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "SummarySegmentGroup", required = true)
	protected List<SummarySegmentGroupType> summarySegmentGroup;
	@XmlAttribute(name = "hierarchy", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String hierarchy;

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
	 * Gets the value of the hierarchy property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getHierarchy() {
		if (hierarchy == null) {
			return "dependent";
		} else {
			return hierarchy;
		}
	}

	/**
	 * Sets the value of the hierarchy property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setHierarchy(String value) {
		this.hierarchy = value;
	}

}
