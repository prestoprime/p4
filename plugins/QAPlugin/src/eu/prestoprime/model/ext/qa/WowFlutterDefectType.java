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
 * Java class for WowFlutterDefectType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="WowFlutterDefectType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}ErrorEventType">
 *       &lt;sequence>
 *         &lt;element name="RelativeDetune">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="mean" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                 &lt;attribute name="maximum" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                 &lt;attribute name="variation" type="{http://www.w3.org/2001/XMLSchema}float" />
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
@XmlType(name = "WowFlutterDefectType", namespace = "http://www.prestospace.org/res/defect_quality", propOrder = { "relativeDetune" })
public class WowFlutterDefectType extends ErrorEventType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "RelativeDetune", required = true)
	protected WowFlutterDefectType.RelativeDetune relativeDetune;

	/**
	 * Gets the value of the relativeDetune property.
	 * 
	 * @return possible object is {@link WowFlutterDefectType.RelativeDetune }
	 * 
	 */
	public WowFlutterDefectType.RelativeDetune getRelativeDetune() {
		return relativeDetune;
	}

	/**
	 * Sets the value of the relativeDetune property.
	 * 
	 * @param value
	 *            allowed object is {@link WowFlutterDefectType.RelativeDetune }
	 * 
	 */
	public void setRelativeDetune(WowFlutterDefectType.RelativeDetune value) {
		this.relativeDetune = value;
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
	 *       &lt;attribute name="mean" type="{http://www.w3.org/2001/XMLSchema}float" />
	 *       &lt;attribute name="maximum" type="{http://www.w3.org/2001/XMLSchema}float" />
	 *       &lt;attribute name="variation" type="{http://www.w3.org/2001/XMLSchema}float" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class RelativeDetune implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlAttribute(name = "mean")
		protected Float mean;
		@XmlAttribute(name = "maximum")
		protected Float maximum;
		@XmlAttribute(name = "variation")
		protected Float variation;

		/**
		 * Gets the value of the mean property.
		 * 
		 * @return possible object is {@link Float }
		 * 
		 */
		public Float getMean() {
			return mean;
		}

		/**
		 * Sets the value of the mean property.
		 * 
		 * @param value
		 *            allowed object is {@link Float }
		 * 
		 */
		public void setMean(Float value) {
			this.mean = value;
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

		/**
		 * Gets the value of the variation property.
		 * 
		 * @return possible object is {@link Float }
		 * 
		 */
		public Float getVariation() {
			return variation;
		}

		/**
		 * Sets the value of the variation property.
		 * 
		 * @param value
		 *            allowed object is {@link Float }
		 * 
		 */
		public void setVariation(Float value) {
			this.variation = value;
		}

	}

}
