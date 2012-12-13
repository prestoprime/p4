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
 * Java class for FigureTrajectoryType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="FigureTrajectoryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MediaTime" type="{urn:mpeg:mpeg7:schema:2004}MediaTimeType"/>
 *         &lt;element name="Vertex" type="{urn:mpeg:mpeg7:schema:2004}TemporalInterpolationType" maxOccurs="unbounded" minOccurs="3"/>
 *         &lt;element name="Depth" type="{urn:mpeg:mpeg7:schema:2004}TemporalInterpolationType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="rectangle"/>
 *             &lt;enumeration value="ellipse"/>
 *             &lt;enumeration value="polygon"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FigureTrajectoryType", propOrder = { "mediaTime", "vertex", "depth" })
public class FigureTrajectoryType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "MediaTime", required = true)
	protected MediaTimeType mediaTime;
	@XmlElement(name = "Vertex", required = true)
	protected List<TemporalInterpolationType> vertex;
	@XmlElement(name = "Depth")
	protected TemporalInterpolationType depth;
	@XmlAttribute(name = "type", required = true)
	protected String type;

	/**
	 * Gets the value of the mediaTime property.
	 * 
	 * @return possible object is {@link MediaTimeType }
	 * 
	 */
	public MediaTimeType getMediaTime() {
		return mediaTime;
	}

	/**
	 * Sets the value of the mediaTime property.
	 * 
	 * @param value
	 *            allowed object is {@link MediaTimeType }
	 * 
	 */
	public void setMediaTime(MediaTimeType value) {
		this.mediaTime = value;
	}

	/**
	 * Gets the value of the vertex property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the vertex property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getVertex().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link TemporalInterpolationType }
	 * 
	 * 
	 */
	public List<TemporalInterpolationType> getVertex() {
		if (vertex == null) {
			vertex = new ArrayList<TemporalInterpolationType>();
		}
		return this.vertex;
	}

	/**
	 * Gets the value of the depth property.
	 * 
	 * @return possible object is {@link TemporalInterpolationType }
	 * 
	 */
	public TemporalInterpolationType getDepth() {
		return depth;
	}

	/**
	 * Sets the value of the depth property.
	 * 
	 * @param value
	 *            allowed object is {@link TemporalInterpolationType }
	 * 
	 */
	public void setDepth(TemporalInterpolationType value) {
		this.depth = value;
	}

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
