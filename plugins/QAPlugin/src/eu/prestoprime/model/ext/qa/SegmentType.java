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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for SegmentType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="SegmentType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}DSType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="MediaInformation" type="{urn:mpeg:mpeg7:schema:2004}MediaInformationType"/>
 *           &lt;element name="MediaLocator" type="{urn:mpeg:mpeg7:schema:2004}MediaLocatorType"/>
 *         &lt;/choice>
 *         &lt;element name="StructuralUnit" type="{urn:mpeg:mpeg7:schema:2004}ControlledTermUseType"/>
 *         &lt;element name="CreationInformation" type="{urn:mpeg:mpeg7:schema:2004}CreationInformationType" minOccurs="0"/>
 *         &lt;element name="UsageInformation" type="{urn:mpeg:mpeg7:schema:2004}UsageInformationType" minOccurs="0"/>
 *         &lt;element name="TextAnnotation" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{urn:mpeg:mpeg7:schema:2004}TextAnnotationType">
 *                 &lt;attribute name="type">
 *                   &lt;simpleType>
 *                     &lt;union memberTypes=" {urn:mpeg:mpeg7:schema:2004}termAliasReferenceType {urn:mpeg:mpeg7:schema:2004}termURIReferenceType {http://www.w3.org/2001/XMLSchema}string">
 *                     &lt;/union>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Relation" type="{urn:mpeg:mpeg7:schema:2004}RelationType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SegmentType", propOrder = { "mediaInformation", "mediaLocator", "structuralUnit", "creationInformation", "usageInformation", "textAnnotation", "relation" })
@XmlSeeAlso({ AudioVisualSegmentType.class, StillRegionType.class, VideoSegmentType.class, MovingRegionType.class, AudioSegmentType.class })
public abstract class SegmentType extends DSType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "MediaInformation")
	protected MediaInformationType mediaInformation;
	@XmlElement(name = "MediaLocator")
	protected MediaLocatorType mediaLocator;
	@XmlElement(name = "StructuralUnit", required = true)
	protected ControlledTermUseType structuralUnit;
	@XmlElement(name = "CreationInformation")
	protected CreationInformationType creationInformation;
	@XmlElement(name = "UsageInformation")
	protected UsageInformationType usageInformation;
	@XmlElement(name = "TextAnnotation")
	protected List<SegmentType.TextAnnotation> textAnnotation;
	@XmlElement(name = "Relation")
	protected List<RelationType> relation;

	/**
	 * Gets the value of the mediaInformation property.
	 * 
	 * @return possible object is {@link MediaInformationType }
	 * 
	 */
	public MediaInformationType getMediaInformation() {
		return mediaInformation;
	}

	/**
	 * Sets the value of the mediaInformation property.
	 * 
	 * @param value
	 *            allowed object is {@link MediaInformationType }
	 * 
	 */
	public void setMediaInformation(MediaInformationType value) {
		this.mediaInformation = value;
	}

	/**
	 * Gets the value of the mediaLocator property.
	 * 
	 * @return possible object is {@link MediaLocatorType }
	 * 
	 */
	public MediaLocatorType getMediaLocator() {
		return mediaLocator;
	}

	/**
	 * Sets the value of the mediaLocator property.
	 * 
	 * @param value
	 *            allowed object is {@link MediaLocatorType }
	 * 
	 */
	public void setMediaLocator(MediaLocatorType value) {
		this.mediaLocator = value;
	}

	/**
	 * Gets the value of the structuralUnit property.
	 * 
	 * @return possible object is {@link ControlledTermUseType }
	 * 
	 */
	public ControlledTermUseType getStructuralUnit() {
		return structuralUnit;
	}

	/**
	 * Sets the value of the structuralUnit property.
	 * 
	 * @param value
	 *            allowed object is {@link ControlledTermUseType }
	 * 
	 */
	public void setStructuralUnit(ControlledTermUseType value) {
		this.structuralUnit = value;
	}

	/**
	 * Gets the value of the creationInformation property.
	 * 
	 * @return possible object is {@link CreationInformationType }
	 * 
	 */
	public CreationInformationType getCreationInformation() {
		return creationInformation;
	}

	/**
	 * Sets the value of the creationInformation property.
	 * 
	 * @param value
	 *            allowed object is {@link CreationInformationType }
	 * 
	 */
	public void setCreationInformation(CreationInformationType value) {
		this.creationInformation = value;
	}

	/**
	 * Gets the value of the usageInformation property.
	 * 
	 * @return possible object is {@link UsageInformationType }
	 * 
	 */
	public UsageInformationType getUsageInformation() {
		return usageInformation;
	}

	/**
	 * Sets the value of the usageInformation property.
	 * 
	 * @param value
	 *            allowed object is {@link UsageInformationType }
	 * 
	 */
	public void setUsageInformation(UsageInformationType value) {
		this.usageInformation = value;
	}

	/**
	 * Gets the value of the textAnnotation property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the textAnnotation property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getTextAnnotation().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link SegmentType.TextAnnotation }
	 * 
	 * 
	 */
	public List<SegmentType.TextAnnotation> getTextAnnotation() {
		if (textAnnotation == null) {
			textAnnotation = new ArrayList<SegmentType.TextAnnotation>();
		}
		return this.textAnnotation;
	}

	/**
	 * Gets the value of the relation property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the relation property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getRelation().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link RelationType }
	 * 
	 * 
	 */
	public List<RelationType> getRelation() {
		if (relation == null) {
			relation = new ArrayList<RelationType>();
		}
		return this.relation;
	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}TextAnnotationType">
	 *       &lt;attribute name="type">
	 *         &lt;simpleType>
	 *           &lt;union memberTypes=" {urn:mpeg:mpeg7:schema:2004}termAliasReferenceType {urn:mpeg:mpeg7:schema:2004}termURIReferenceType {http://www.w3.org/2001/XMLSchema}string">
	 *           &lt;/union>
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
	@XmlType(name = "")
	public static class TextAnnotation extends TextAnnotationType implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlAttribute(name = "type")
		protected String type;

		/**
		 * Gets the value of the type property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getType() {
			return type;
		}

		/**
		 * Sets the value of the type property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setType(String value) {
			this.type = value;
		}

	}

}
