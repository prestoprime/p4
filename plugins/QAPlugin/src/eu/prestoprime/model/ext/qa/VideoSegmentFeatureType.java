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
 * Java class for VideoSegmentFeatureType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="VideoSegmentFeatureType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}VisualDSType">
 *       &lt;sequence>
 *         &lt;element name="TemporalTransition" type="{urn:mpeg:mpeg7:schema:2004}VisualTimeSeriesType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="RepresentativeFeature" type="{urn:mpeg:mpeg7:schema:2004}GofGopFeatureType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="MotionActivity" type="{urn:mpeg:mpeg7:schema:2004}MotionActivityType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VideoSegmentFeatureType", propOrder = { "temporalTransition", "representativeFeature", "motionActivity" })
@XmlSeeAlso({ MovingRegionFeatureType.class })
public class VideoSegmentFeatureType extends VisualDSType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "TemporalTransition")
	protected List<VisualTimeSeriesType> temporalTransition;
	@XmlElement(name = "RepresentativeFeature")
	protected List<GofGopFeatureType> representativeFeature;
	@XmlElement(name = "MotionActivity")
	protected MotionActivityType motionActivity;

	/**
	 * Gets the value of the temporalTransition property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the temporalTransition property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getTemporalTransition().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link VisualTimeSeriesType }
	 * 
	 * 
	 */
	public List<VisualTimeSeriesType> getTemporalTransition() {
		if (temporalTransition == null) {
			temporalTransition = new ArrayList<VisualTimeSeriesType>();
		}
		return this.temporalTransition;
	}

	/**
	 * Gets the value of the representativeFeature property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the representativeFeature property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getRepresentativeFeature().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link GofGopFeatureType }
	 * 
	 * 
	 */
	public List<GofGopFeatureType> getRepresentativeFeature() {
		if (representativeFeature == null) {
			representativeFeature = new ArrayList<GofGopFeatureType>();
		}
		return this.representativeFeature;
	}

	/**
	 * Gets the value of the motionActivity property.
	 * 
	 * @return possible object is {@link MotionActivityType }
	 * 
	 */
	public MotionActivityType getMotionActivity() {
		return motionActivity;
	}

	/**
	 * Sets the value of the motionActivity property.
	 * 
	 * @param value
	 *            allowed object is {@link MotionActivityType }
	 * 
	 */
	public void setMotionActivity(MotionActivityType value) {
		this.motionActivity = value;
	}

}