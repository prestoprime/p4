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
import javax.xml.bind.annotation.XmlValue;

/**
 * <p>
 * Java class for TemporalInterpolationType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="TemporalInterpolationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="WholeInterval">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="MediaIncrDuration" type="{urn:mpeg:mpeg7:schema:2004}MediaIncrDurationType"/>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="KeyTimePoint">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="MediaRelIncrTimePoint" type="{urn:mpeg:mpeg7:schema:2004}MediaRelIncrTimePointType" maxOccurs="65535" minOccurs="2"/>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/choice>
 *         &lt;element name="InterpolationFunctions" maxOccurs="15">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="KeyValue" maxOccurs="65535" minOccurs="2">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>float">
 *                           &lt;attribute name="type" default="firstOrder">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="startPoint"/>
 *                                 &lt;enumeration value="firstOrder"/>
 *                                 &lt;enumeration value="secondOrder"/>
 *                                 &lt;enumeration value="notDetermined"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="param" type="{http://www.w3.org/2001/XMLSchema}float" default="0.0" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemporalInterpolationType", propOrder = { "wholeInterval", "keyTimePoint", "interpolationFunctions" })
public class TemporalInterpolationType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "WholeInterval")
	protected TemporalInterpolationType.WholeInterval wholeInterval;
	@XmlElement(name = "KeyTimePoint")
	protected TemporalInterpolationType.KeyTimePoint keyTimePoint;
	@XmlElement(name = "InterpolationFunctions", required = true)
	protected List<TemporalInterpolationType.InterpolationFunctions> interpolationFunctions;

	/**
	 * Gets the value of the wholeInterval property.
	 * 
	 * @return possible object is
	 *         {@link TemporalInterpolationType.WholeInterval }
	 * 
	 */
	public TemporalInterpolationType.WholeInterval getWholeInterval() {
		return wholeInterval;
	}

	/**
	 * Sets the value of the wholeInterval property.
	 * 
	 * @param value
	 *            allowed object is
	 *            {@link TemporalInterpolationType.WholeInterval }
	 * 
	 */
	public void setWholeInterval(TemporalInterpolationType.WholeInterval value) {
		this.wholeInterval = value;
	}

	/**
	 * Gets the value of the keyTimePoint property.
	 * 
	 * @return possible object is {@link TemporalInterpolationType.KeyTimePoint }
	 * 
	 */
	public TemporalInterpolationType.KeyTimePoint getKeyTimePoint() {
		return keyTimePoint;
	}

	/**
	 * Sets the value of the keyTimePoint property.
	 * 
	 * @param value
	 *            allowed object is
	 *            {@link TemporalInterpolationType.KeyTimePoint }
	 * 
	 */
	public void setKeyTimePoint(TemporalInterpolationType.KeyTimePoint value) {
		this.keyTimePoint = value;
	}

	/**
	 * Gets the value of the interpolationFunctions property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the interpolationFunctions property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getInterpolationFunctions().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link TemporalInterpolationType.InterpolationFunctions }
	 * 
	 * 
	 */
	public List<TemporalInterpolationType.InterpolationFunctions> getInterpolationFunctions() {
		if (interpolationFunctions == null) {
			interpolationFunctions = new ArrayList<TemporalInterpolationType.InterpolationFunctions>();
		}
		return this.interpolationFunctions;
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
	 *       &lt;sequence>
	 *         &lt;element name="KeyValue" maxOccurs="65535" minOccurs="2">
	 *           &lt;complexType>
	 *             &lt;simpleContent>
	 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>float">
	 *                 &lt;attribute name="type" default="firstOrder">
	 *                   &lt;simpleType>
	 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                       &lt;enumeration value="startPoint"/>
	 *                       &lt;enumeration value="firstOrder"/>
	 *                       &lt;enumeration value="secondOrder"/>
	 *                       &lt;enumeration value="notDetermined"/>
	 *                     &lt;/restriction>
	 *                   &lt;/simpleType>
	 *                 &lt;/attribute>
	 *                 &lt;attribute name="param" type="{http://www.w3.org/2001/XMLSchema}float" default="0.0" />
	 *               &lt;/extension>
	 *             &lt;/simpleContent>
	 *           &lt;/complexType>
	 *         &lt;/element>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "keyValue" })
	public static class InterpolationFunctions implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlElement(name = "KeyValue", required = true)
		protected List<TemporalInterpolationType.InterpolationFunctions.KeyValue> keyValue;

		/**
		 * Gets the value of the keyValue property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the keyValue property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getKeyValue().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link TemporalInterpolationType.InterpolationFunctions.KeyValue }
		 * 
		 * 
		 */
		public List<TemporalInterpolationType.InterpolationFunctions.KeyValue> getKeyValue() {
			if (keyValue == null) {
				keyValue = new ArrayList<TemporalInterpolationType.InterpolationFunctions.KeyValue>();
			}
			return this.keyValue;
		}

		/**
		 * <p>
		 * Java class for anonymous complex type.
		 * 
		 * <p>
		 * The following schema fragment specifies the expected content
		 * contained within this class.
		 * 
		 * <pre>
		 * &lt;complexType>
		 *   &lt;simpleContent>
		 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>float">
		 *       &lt;attribute name="type" default="firstOrder">
		 *         &lt;simpleType>
		 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *             &lt;enumeration value="startPoint"/>
		 *             &lt;enumeration value="firstOrder"/>
		 *             &lt;enumeration value="secondOrder"/>
		 *             &lt;enumeration value="notDetermined"/>
		 *           &lt;/restriction>
		 *         &lt;/simpleType>
		 *       &lt;/attribute>
		 *       &lt;attribute name="param" type="{http://www.w3.org/2001/XMLSchema}float" default="0.0" />
		 *     &lt;/extension>
		 *   &lt;/simpleContent>
		 * &lt;/complexType>
		 * </pre>
		 * 
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "value" })
		public static class KeyValue implements Serializable {

			private final static long serialVersionUID = 1L;
			@XmlValue
			protected float value;
			@XmlAttribute(name = "type")
			protected String type;
			@XmlAttribute(name = "param")
			protected Float param;

			/**
			 * Gets the value of the value property.
			 * 
			 */
			public float getValue() {
				return value;
			}

			/**
			 * Sets the value of the value property.
			 * 
			 */
			public void setValue(float value) {
				this.value = value;
			}

			/**
			 * Gets the value of the type property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getType() {
				if (type == null) {
					return "firstOrder";
				} else {
					return type;
				}
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

			/**
			 * Gets the value of the param property.
			 * 
			 * @return possible object is {@link Float }
			 * 
			 */
			public float getParam() {
				if (param == null) {
					return 0.0F;
				} else {
					return param;
				}
			}

			/**
			 * Sets the value of the param property.
			 * 
			 * @param value
			 *            allowed object is {@link Float }
			 * 
			 */
			public void setParam(Float value) {
				this.param = value;
			}

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
	 *       &lt;sequence>
	 *         &lt;element name="MediaRelIncrTimePoint" type="{urn:mpeg:mpeg7:schema:2004}MediaRelIncrTimePointType" maxOccurs="65535" minOccurs="2"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "mediaRelIncrTimePoint" })
	public static class KeyTimePoint implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlElement(name = "MediaRelIncrTimePoint", required = true)
		protected List<MediaRelIncrTimePointType> mediaRelIncrTimePoint;

		/**
		 * Gets the value of the mediaRelIncrTimePoint property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the mediaRelIncrTimePoint property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getMediaRelIncrTimePoint().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link MediaRelIncrTimePointType }
		 * 
		 * 
		 */
		public List<MediaRelIncrTimePointType> getMediaRelIncrTimePoint() {
			if (mediaRelIncrTimePoint == null) {
				mediaRelIncrTimePoint = new ArrayList<MediaRelIncrTimePointType>();
			}
			return this.mediaRelIncrTimePoint;
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
	 *       &lt;sequence>
	 *         &lt;element name="MediaIncrDuration" type="{urn:mpeg:mpeg7:schema:2004}MediaIncrDurationType"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "mediaIncrDuration" })
	public static class WholeInterval implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlElement(name = "MediaIncrDuration", required = true)
		protected MediaIncrDurationType mediaIncrDuration;

		/**
		 * Gets the value of the mediaIncrDuration property.
		 * 
		 * @return possible object is {@link MediaIncrDurationType }
		 * 
		 */
		public MediaIncrDurationType getMediaIncrDuration() {
			return mediaIncrDuration;
		}

		/**
		 * Sets the value of the mediaIncrDuration property.
		 * 
		 * @param value
		 *            allowed object is {@link MediaIncrDurationType }
		 * 
		 */
		public void setMediaIncrDuration(MediaIncrDurationType value) {
			this.mediaIncrDuration = value;
		}

	}

}