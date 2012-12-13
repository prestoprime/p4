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
 * Java class for NoiseGrainLevelType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="NoiseGrainLevelType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.prestospace.org/res/defect_quality}VisualImpairmentType">
 *       &lt;sequence>
 *         &lt;element name="MeanPSNR" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="MeanPSNRSamples" type="{http://www.prestospace.org/res/defect_quality}ImpairmentSampleListType" minOccurs="0"/>
 *         &lt;element name="DominantFrequency" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;urn:mpeg:mpeg7:schema:2004>zeroToOneType">
 *                 &lt;attribute name="maxFrequency" type="{urn:mpeg:mpeg7:schema:2004}unsigned32" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="BrightnessDependency" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded">
 *                   &lt;element name="GrainIntensity">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;urn:mpeg:mpeg7:schema:2004>zeroToOneType">
 *                           &lt;attribute name="brightness" use="required" type="{urn:mpeg:mpeg7:schema:2004}zeroToOneType" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Texel" type="{urn:mpeg:mpeg7:schema:2004}ImageLocatorType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NoiseGrainLevelType", namespace = "http://www.prestospace.org/res/defect_quality", propOrder = { "meanPSNR", "meanPSNRSamples", "dominantFrequency", "brightnessDependency", "texel" })
public class NoiseGrainLevelType extends VisualImpairmentType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "MeanPSNR")
	protected Float meanPSNR;
	@XmlElement(name = "MeanPSNRSamples")
	protected ImpairmentSampleListType meanPSNRSamples;
	@XmlElement(name = "DominantFrequency")
	protected NoiseGrainLevelType.DominantFrequency dominantFrequency;
	@XmlElement(name = "BrightnessDependency")
	protected NoiseGrainLevelType.BrightnessDependency brightnessDependency;
	@XmlElement(name = "Texel")
	protected List<ImageLocatorType> texel;

	/**
	 * Gets the value of the meanPSNR property.
	 * 
	 * @return possible object is {@link Float }
	 * 
	 */
	public Float getMeanPSNR() {
		return meanPSNR;
	}

	/**
	 * Sets the value of the meanPSNR property.
	 * 
	 * @param value
	 *            allowed object is {@link Float }
	 * 
	 */
	public void setMeanPSNR(Float value) {
		this.meanPSNR = value;
	}

	/**
	 * Gets the value of the meanPSNRSamples property.
	 * 
	 * @return possible object is {@link ImpairmentSampleListType }
	 * 
	 */
	public ImpairmentSampleListType getMeanPSNRSamples() {
		return meanPSNRSamples;
	}

	/**
	 * Sets the value of the meanPSNRSamples property.
	 * 
	 * @param value
	 *            allowed object is {@link ImpairmentSampleListType }
	 * 
	 */
	public void setMeanPSNRSamples(ImpairmentSampleListType value) {
		this.meanPSNRSamples = value;
	}

	/**
	 * Gets the value of the dominantFrequency property.
	 * 
	 * @return possible object is {@link NoiseGrainLevelType.DominantFrequency }
	 * 
	 */
	public NoiseGrainLevelType.DominantFrequency getDominantFrequency() {
		return dominantFrequency;
	}

	/**
	 * Sets the value of the dominantFrequency property.
	 * 
	 * @param value
	 *            allowed object is
	 *            {@link NoiseGrainLevelType.DominantFrequency }
	 * 
	 */
	public void setDominantFrequency(NoiseGrainLevelType.DominantFrequency value) {
		this.dominantFrequency = value;
	}

	/**
	 * Gets the value of the brightnessDependency property.
	 * 
	 * @return possible object is
	 *         {@link NoiseGrainLevelType.BrightnessDependency }
	 * 
	 */
	public NoiseGrainLevelType.BrightnessDependency getBrightnessDependency() {
		return brightnessDependency;
	}

	/**
	 * Sets the value of the brightnessDependency property.
	 * 
	 * @param value
	 *            allowed object is
	 *            {@link NoiseGrainLevelType.BrightnessDependency }
	 * 
	 */
	public void setBrightnessDependency(NoiseGrainLevelType.BrightnessDependency value) {
		this.brightnessDependency = value;
	}

	/**
	 * Gets the value of the texel property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the texel property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getTexel().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ImageLocatorType }
	 * 
	 * 
	 */
	public List<ImageLocatorType> getTexel() {
		if (texel == null) {
			texel = new ArrayList<ImageLocatorType>();
		}
		return this.texel;
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
	 *       &lt;sequence maxOccurs="unbounded">
	 *         &lt;element name="GrainIntensity">
	 *           &lt;complexType>
	 *             &lt;simpleContent>
	 *               &lt;extension base="&lt;urn:mpeg:mpeg7:schema:2004>zeroToOneType">
	 *                 &lt;attribute name="brightness" use="required" type="{urn:mpeg:mpeg7:schema:2004}zeroToOneType" />
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
	@XmlType(name = "", propOrder = { "grainIntensity" })
	public static class BrightnessDependency implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlElement(name = "GrainIntensity", namespace = "http://www.prestospace.org/res/defect_quality", required = true)
		protected List<NoiseGrainLevelType.BrightnessDependency.GrainIntensity> grainIntensity;

		/**
		 * Gets the value of the grainIntensity property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the grainIntensity property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getGrainIntensity().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link NoiseGrainLevelType.BrightnessDependency.GrainIntensity }
		 * 
		 * 
		 */
		public List<NoiseGrainLevelType.BrightnessDependency.GrainIntensity> getGrainIntensity() {
			if (grainIntensity == null) {
				grainIntensity = new ArrayList<NoiseGrainLevelType.BrightnessDependency.GrainIntensity>();
			}
			return this.grainIntensity;
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
		 *     &lt;extension base="&lt;urn:mpeg:mpeg7:schema:2004>zeroToOneType">
		 *       &lt;attribute name="brightness" use="required" type="{urn:mpeg:mpeg7:schema:2004}zeroToOneType" />
		 *     &lt;/extension>
		 *   &lt;/simpleContent>
		 * &lt;/complexType>
		 * </pre>
		 * 
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "value" })
		public static class GrainIntensity implements Serializable {

			private final static long serialVersionUID = 1L;
			@XmlValue
			protected float value;
			@XmlAttribute(name = "brightness", required = true)
			protected float brightness;

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
			 * Gets the value of the brightness property.
			 * 
			 */
			public float getBrightness() {
				return brightness;
			}

			/**
			 * Sets the value of the brightness property.
			 * 
			 */
			public void setBrightness(float value) {
				this.brightness = value;
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
	 *   &lt;simpleContent>
	 *     &lt;extension base="&lt;urn:mpeg:mpeg7:schema:2004>zeroToOneType">
	 *       &lt;attribute name="maxFrequency" type="{urn:mpeg:mpeg7:schema:2004}unsigned32" />
	 *     &lt;/extension>
	 *   &lt;/simpleContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "value" })
	public static class DominantFrequency implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlValue
		protected float value;
		@XmlAttribute(name = "maxFrequency")
		protected Long maxFrequency;

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
		 * Gets the value of the maxFrequency property.
		 * 
		 * @return possible object is {@link Long }
		 * 
		 */
		public Long getMaxFrequency() {
			return maxFrequency;
		}

		/**
		 * Sets the value of the maxFrequency property.
		 * 
		 * @param value
		 *            allowed object is {@link Long }
		 * 
		 */
		public void setMaxFrequency(Long value) {
			this.maxFrequency = value;
		}

	}

}