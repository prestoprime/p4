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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for SpokenContentLinkType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="SpokenContentLinkType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="probability" type="{urn:mpeg:mpeg7:schema:2004}zeroToOneType" default="1.0" />
 *       &lt;attribute name="acousticScore" type="{urn:mpeg:mpeg7:schema:2004}nonNegativeReal" />
 *       &lt;attribute name="nodeOffset" type="{urn:mpeg:mpeg7:schema:2004}unsigned16" default="1" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpokenContentLinkType")
@XmlSeeAlso({ eu.prestoprime.model.ext.qa.SpokenContentLatticeType.Block.Node.WordLink.class, eu.prestoprime.model.ext.qa.SpokenContentLatticeType.Block.Node.PhoneLink.class })
public class SpokenContentLinkType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlAttribute(name = "probability")
	protected Float probability;
	@XmlAttribute(name = "acousticScore")
	protected Double acousticScore;
	@XmlAttribute(name = "nodeOffset")
	protected Integer nodeOffset;

	/**
	 * Gets the value of the probability property.
	 * 
	 * @return possible object is {@link Float }
	 * 
	 */
	public float getProbability() {
		if (probability == null) {
			return 1.0F;
		} else {
			return probability;
		}
	}

	/**
	 * Sets the value of the probability property.
	 * 
	 * @param value
	 *            allowed object is {@link Float }
	 * 
	 */
	public void setProbability(Float value) {
		this.probability = value;
	}

	/**
	 * Gets the value of the acousticScore property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public Double getAcousticScore() {
		return acousticScore;
	}

	/**
	 * Sets the value of the acousticScore property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setAcousticScore(Double value) {
		this.acousticScore = value;
	}

	/**
	 * Gets the value of the nodeOffset property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public int getNodeOffset() {
		if (nodeOffset == null) {
			return 1;
		} else {
			return nodeOffset;
		}
	}

	/**
	 * Sets the value of the nodeOffset property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setNodeOffset(Integer value) {
		this.nodeOffset = value;
	}

}