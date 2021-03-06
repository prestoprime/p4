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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ChannelMisalignmentType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="ChannelMisalignmentType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.prestospace.org/res/defect_quality}VisualImpairmentType">
 *       &lt;sequence>
 *         &lt;element name="HorizontalDisplacement" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="average" type="{urn:mpeg:mpeg7:schema:2004}zeroToOneType" />
 *                 &lt;attribute name="maximum" type="{urn:mpeg:mpeg7:schema:2004}zeroToOneType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="VerticalDisplacement" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="average" type="{urn:mpeg:mpeg7:schema:2004}zeroToOneType" />
 *                 &lt;attribute name="maximum" type="{urn:mpeg:mpeg7:schema:2004}zeroToOneType" />
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
@XmlType(name = "ChannelMisalignmentType", namespace = "http://www.prestospace.org/res/defect_quality", propOrder = { "horizontalDisplacement", "verticalDisplacement" })
public class ChannelMisalignmentType extends VisualImpairmentType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "HorizontalDisplacement")
	protected ChannelMisalignmentType.HorizontalDisplacement horizontalDisplacement;
	@XmlElement(name = "VerticalDisplacement")
	protected ChannelMisalignmentType.VerticalDisplacement verticalDisplacement;

	/**
	 * Gets the value of the horizontalDisplacement property.
	 * 
	 * @return possible object is
	 *         {@link ChannelMisalignmentType.HorizontalDisplacement }
	 * 
	 */
	public ChannelMisalignmentType.HorizontalDisplacement getHorizontalDisplacement() {
		return horizontalDisplacement;
	}

	/**
	 * Sets the value of the horizontalDisplacement property.
	 * 
	 * @param value
	 *            allowed object is
	 *            {@link ChannelMisalignmentType.HorizontalDisplacement }
	 * 
	 */
	public void setHorizontalDisplacement(ChannelMisalignmentType.HorizontalDisplacement value) {
		this.horizontalDisplacement = value;
	}

	/**
	 * Gets the value of the verticalDisplacement property.
	 * 
	 * @return possible object is
	 *         {@link ChannelMisalignmentType.VerticalDisplacement }
	 * 
	 */
	public ChannelMisalignmentType.VerticalDisplacement getVerticalDisplacement() {
		return verticalDisplacement;
	}

	/**
	 * Sets the value of the verticalDisplacement property.
	 * 
	 * @param value
	 *            allowed object is
	 *            {@link ChannelMisalignmentType.VerticalDisplacement }
	 * 
	 */
	public void setVerticalDisplacement(ChannelMisalignmentType.VerticalDisplacement value) {
		this.verticalDisplacement = value;
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
	 *       &lt;attribute name="maximum" type="{urn:mpeg:mpeg7:schema:2004}zeroToOneType" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class HorizontalDisplacement implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlAttribute(name = "average")
		protected Float average;
		@XmlAttribute(name = "maximum")
		protected Float maximum;

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

		/**
		 * Gets the value of the maximum property.
		 * 
		 * @return possible object is {@link Float }
		 * 
		 */
		public Float getMaximum() {
			return maximum;
		}

		/**
		 * Sets the value of the maximum property.
		 * 
		 * @param value
		 *            allowed object is {@link Float }
		 * 
		 */
		public void setMaximum(Float value) {
			this.maximum = value;
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
	 *       &lt;attribute name="average" type="{urn:mpeg:mpeg7:schema:2004}zeroToOneType" />
	 *       &lt;attribute name="maximum" type="{urn:mpeg:mpeg7:schema:2004}zeroToOneType" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class VerticalDisplacement implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlAttribute(name = "average")
		protected Float average;
		@XmlAttribute(name = "maximum")
		protected Float maximum;

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

		/**
		 * Gets the value of the maximum property.
		 * 
		 * @return possible object is {@link Float }
		 * 
		 */
		public Float getMaximum() {
			return maximum;
		}

		/**
		 * Sets the value of the maximum property.
		 * 
		 * @param value
		 *            allowed object is {@link Float }
		 * 
		 */
		public void setMaximum(Float value) {
			this.maximum = value;
		}

	}

}
