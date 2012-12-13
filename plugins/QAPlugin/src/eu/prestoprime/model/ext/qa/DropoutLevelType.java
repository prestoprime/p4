//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.24 at 11:42:28 PM CEST 
//

package eu.prestoprime.model.ext.qa;

import java.io.Serializable;
import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for DropoutLevelType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="DropoutLevelType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.prestospace.org/res/defect_quality}VisualImpairmentType">
 *       &lt;sequence>
 *         &lt;element name="FractionOfFrames" type="{urn:mpeg:mpeg7:schema:2004}zeroToOneType" minOccurs="0"/>
 *         &lt;element name="Area" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="average" type="{urn:mpeg:mpeg7:schema:2004}zeroToOneType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SubsequentFrames" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="maximum" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DropoutLevelType", namespace = "http://www.prestospace.org/res/defect_quality", propOrder = { "fractionOfFrames", "area", "subsequentFrames" })
public class DropoutLevelType extends VisualImpairmentType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "FractionOfFrames")
	protected Float fractionOfFrames;
	@XmlElement(name = "Area")
	protected DropoutLevelType.Area area;
	@XmlElement(name = "SubsequentFrames")
	protected DropoutLevelType.SubsequentFrames subsequentFrames;

	/**
	 * Gets the value of the fractionOfFrames property.
	 * 
	 * @return possible object is {@link Float }
	 * 
	 */
	public Float getFractionOfFrames() {
		return fractionOfFrames;
	}

	/**
	 * Sets the value of the fractionOfFrames property.
	 * 
	 * @param value
	 *            allowed object is {@link Float }
	 * 
	 */
	public void setFractionOfFrames(Float value) {
		this.fractionOfFrames = value;
	}

	/**
	 * Gets the value of the area property.
	 * 
	 * @return possible object is {@link DropoutLevelType.Area }
	 * 
	 */
	public DropoutLevelType.Area getArea() {
		return area;
	}

	/**
	 * Sets the value of the area property.
	 * 
	 * @param value
	 *            allowed object is {@link DropoutLevelType.Area }
	 * 
	 */
	public void setArea(DropoutLevelType.Area value) {
		this.area = value;
	}

	/**
	 * Gets the value of the subsequentFrames property.
	 * 
	 * @return possible object is {@link DropoutLevelType.SubsequentFrames }
	 * 
	 */
	public DropoutLevelType.SubsequentFrames getSubsequentFrames() {
		return subsequentFrames;
	}

	/**
	 * Sets the value of the subsequentFrames property.
	 * 
	 * @param value
	 *            allowed object is {@link DropoutLevelType.SubsequentFrames }
	 * 
	 */
	public void setSubsequentFrames(DropoutLevelType.SubsequentFrames value) {
		this.subsequentFrames = value;
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
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;attribute name="average" type="{urn:mpeg:mpeg7:schema:2004}zeroToOneType" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class Area implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlAttribute(name = "average")
		protected Float average;

		/**
		 * Gets the value of the average property.
		 * 
		 * @return possible object is {@link Float }
		 * 
		 */
		public Float getAverage() {
			return average;
		}

		/**
		 * Sets the value of the average property.
		 * 
		 * @param value
		 *            allowed object is {@link Float }
		 * 
		 */
		public void setAverage(Float value) {
			this.average = value;
		}

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
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;attribute name="maximum" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class SubsequentFrames implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlAttribute(name = "maximum", required = true)
		@XmlSchemaType(name = "nonNegativeInteger")
		protected BigInteger maximum;

		/**
		 * Gets the value of the maximum property.
		 * 
		 * @return possible object is {@link BigInteger }
		 * 
		 */
		public BigInteger getMaximum() {
			return maximum;
		}

		/**
		 * Sets the value of the maximum property.
		 * 
		 * @param value
		 *            allowed object is {@link BigInteger }
		 * 
		 */
		public void setMaximum(BigInteger value) {
			this.maximum = value;
		}

	}

}